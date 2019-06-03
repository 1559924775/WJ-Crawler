package com.uestc.start;


import com.uestc.dao.TbStartUrlMapper;
import com.uestc.domain.TbStartUrl;
import com.uestc.runner.DoubanTask;
import com.uestc.runner.ZonghengTask;
import com.uestc.service.impl.RedisQueueRepositoryService;
import com.uestc.util.ConditionController;
import com.uestc.util.ZKUtil;
import lombok.Data;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

/**
 * @author 王俊
     * 分布式爬虫工作密取思路
     *     1，一个主Deque,一个容灾队列，每个爬虫进程一个Deque  （爬虫队列中只能是详情页url，主队列中既可以是列表url也可以是详情页url）；
     *     2，从自己的队列中消费（poll从尾)，如果自己队列以空，轮询队列中窃取任务（pop从头）（优先从主队列中）；
     *          1）除了主队列外，要获取其他队列要通过zookeeper来查看运行的爬虫；
     *     3，如果窃取到的任务会产生新的任务，平均分配到各个爬虫队列中；
     *          2）要通过zookeeper来查看运行的爬虫；
     *     4，如果监控到某个爬虫挂掉，要将该爬虫队列中的任务先分到一个容灾队列中，若爬虫没有全部挂掉，再均分到其他在线的爬虫对列中；
//     *     5, 如果监控到新加入的爬虫，要创建自己的工作队列（空，从其他队列窃取，优先从主队列）；  （新进程会自己曾经队列）
     *     6，如果全部挂掉，所有任务都会放到容灾队列中去。
     *          1）监控服务启动一个线程等待爬虫上线，将容灾队列中的任务分配到爬虫队列中去。
     *     7，加锁分析：
     *          1）poll()从自己的队列中取url，不会产生竞争；
     *          2）窃取用pop()会产生竞争，但是redis本身是单线程模型的，一次只会处理一个事件，所以不需要加锁；
     *          3）注：虽然redis是单线程的，取数据和加入数据时会阻塞，但处理url的过程是并发的。
 */

/**
 * 爬虫任务开始入口
 * @author 王俊
 */
@Data
@Component
public class RunnableStart {
    @Autowired
    DoubanTask doubanTask;
    @Autowired
    ZonghengTask zonghengTask;

    @Autowired
    TbStartUrlMapper tbStartUrlMapper;
    @Autowired
    RedisQueueRepositoryService redisQueueRepositoryService;

    CuratorFramework client;//与zookeeper的连接

    CountDownLatch countDownLatch;

    boolean isRecovery;
    /**
     * @param args
     */
    public static void main(String[] args) {
        //怎么解码：https://www.cnblogs.com/zmj97/p/10180770.html
        // TODO Auto-generated method stub
    }

    public RunnableStart(){
        countDownLatch=new CountDownLatch(1);
        //重试策略:重试3次，每次间隔时间指数增长(有具体增长公式)
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        //zk地址
        String hosts = ZKUtil.ZOOKEEPER_HOSTS;
        client = CuratorFrameworkFactory.newClient(hosts, retryPolicy);
        //建立连接
        client.start();
    }
    public void start(){
        //对于重启恢复的不必选举
        if(isRecovery)
            election();
        //这个任务进程到zookeeper上去注册，方便监控应用情况
        registerOnZookeeper();
        //加入起始页 (应该通过定时任务在夜里添加进去)  //spider_zongheng是主队列
        ExecutorService executorService= Executors.newCachedThreadPool();
        ConditionController conditionController=new ConditionController();
        //开启消费者线程去消费
        Thread consumer=new Thread(doubanTask);
        consumer.setName("豆瓣爬虫");
        executorService.submit(consumer);

        //开启消费者线程去消费
        Thread consumer1=new Thread(zonghengTask);
        consumer1.setName("纵横爬虫");
        executorService.submit(consumer1);
        executorService.shutdown();
    }
    public void registerOnZookeeper(){
        //开始爬虫任务，注册节点自己方便监控
        String ip="";
        try {
            //获取本地ip地址
            InetAddress localHost = InetAddress.getLocalHost();
            ip = localHost.getHostAddress();
            //每启动一个爬虫应用，创建一个临时节点，子节点名称为当前ip
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
                    .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE).forPath(ZKUtil.PATH+"/"+ip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void election(){
        //先竞选主节点，拉取种子url，再开始注册自己的节点，开始任务。
        try {
            client.create().forPath("/master-election");
            //处理竞选成的逻辑  -搞一个类，留一个钩子方法
            doTakeLeaderShip();
        } catch (KeeperException.NodeExistsException e1){
            //处理竞选失败的逻辑
            doElecctionFail();
        }catch (Exception e) {
            e.printStackTrace();
        }
        //主节点完成任务后大家才能往下走
        try {
            //等待/success节点的创建
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /**
     * 竞选成功后主节点要执行的操作
     */
    public void doTakeLeaderShip(){
        //从数据库拿到种子url加入到对应的主队列中去
        List<TbStartUrl> startUrl=tbStartUrlMapper.selectByExample(null);
        Map<String, List<String>> map=new HashMap<>();// 网站—起始url
        for(TbStartUrl tbStartUrl:startUrl){
            String name=tbStartUrl.getName();
            String url=tbStartUrl.getUrl();
            List<String> list=map.get(name);
            if(list==null){
                list=new ArrayList<>();
                list.add(url);
                map.put(name,list);
            }else{
                list.add(url);
            }
        }
        for(String str : map.keySet()){
            //放入到对应的队列中
            redisQueueRepositoryService.setQueue("spider_"+str);
            List<String> urls=map.get(str);
            for(String url:urls){
                redisQueueRepositoryService.addUrltoAll("spider_"+str,url);
            }
        }
        //创建/master_election/success节点
        try {
            client.create().forPath("/master-election/success");
            countDownLatch.countDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   
    /**
     * 竞选失败后从节点要进行的操作
     */
    public void doElecctionFail(){
        //监听master_election节点，如果创建了子节点，则开始自己的爬虫任务
        NodeCache cache=new NodeCache(client,"/master-election/success",false);
        try {
            cache.start(true);
            cache.getListenable().addListener(new NodeCacheListener() {
                @Override
                public void nodeChanged() throws Exception {
                    //可以开始爬虫任务了
                    countDownLatch.countDown();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
