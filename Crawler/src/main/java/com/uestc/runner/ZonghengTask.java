package com.uestc.runner;


import com.alibaba.dubbo.config.annotation.Reference;
import com.uestc.entity.ZonghengPage;
import com.uestc.service.*;
import com.uestc.util.ConditionController;
import com.uestc.util.LoadPropertyUtil;
import com.uestc.util.ZKUtil;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 以工作窃取的方式运行爬虫
 * @author 王俊
 */
@Component
public class ZonghengTask implements Runnable{

    @Autowired @Qualifier("httpClientDownLoadService")
    IDownLoadService httpClientDownLoadService;
    @Autowired @Qualifier("mysqlStoreService")
    IStoreService mysqlStoreService;
    @Autowired @Qualifier("zonghengProcessService")
    IProcessService zonghengProcessService;
    @Autowired
    ConditionController conditionController;
    @Reference
    IZonghengLogService zonghengLogService;
    Set<String> localDoneSet;
    String keyName=null;
    CuratorFramework client;

    @Autowired
    IRepositoryService redisQueueRepositoryService;
    public ZonghengTask(){
        localDoneSet=new HashSet<>();//只有一个爬虫周期有用
        //获取本地ip地址
        InetAddress localHost = null;
        try {
            localHost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String ip = localHost.getHostAddress();
        keyName="spider_zongheng"+ip;  //进程对应的队列
        //重试策略:重试3次，每次间隔时间指数增长(有具体增长公式)
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        //zk地址
        String hosts = ZKUtil.ZOOKEEPER_HOSTS;
        client = CuratorFrameworkFactory.newClient(hosts,
                retryPolicy);
        client.start();  //显然一直都要用

    }
    //这个是bx线程
    public void run() {
        //任务开始执行的时间
        zonghengLogService.setStartTime(new Date());
        //设置自己的主队列
        redisQueueRepositoryService.setQueue("spider_zongheng");
        String url="";
        while(true){
            conditionController.getLock().lock();
            try {
                //唤醒别人自己等待
                conditionController.getCda().signal();
                conditionController.getCdb().await(10000,TimeUnit.MILLISECONDS);

                System.out.println(Thread.currentThread().getName());
                url=redisQueueRepositoryService.poll(keyName); //刚开始只有主队列中有，需要窃取
                if(url==null){
                    //自己的任务完成了，从别人那里去窃取任务
                    url=steal();
                }
                int cnt=0; //不可能一直拿不到url
                while(url==null){
                    //取不到数据，可能是任务已经完成了，也可能加入url的事件还没有被单线程的redis处理，休息一会儿。
                    TimeUnit.MILLISECONDS.sleep(5000);
                    url=steal();
                    cnt++;//休息5s*12*10  10分钟还没有数据
                    //总是取不到任务，肯定是任务已经完成了
                    if(cnt==120){
                        zonghengLogService.setEndTime(new Date());
                        return;
                    }
                }
                //如果是详情页直接消费，如果是列表页解析列表页
                if(url.startsWith(LoadPropertyUtil.getZongheng("listUrlStartWith"))){
                    System.out.println("列表页:"+url);
                    //是列表页，将所有book详情页url加入到队列中，并将下一个列表页加入到队列中
                    String content=httpClientDownLoadService.download(url);
                    zonghengLogService.info("节点"+keyName+"正在解析列表页-url:"+url);
                    zonghengLogService.addUrlListNum(1);
                    this.resolveContent(content);
                    zonghengLogService.addSuccessNum(1);
                    //将下一页加入到队列中
                    /*
                    http://book.zongheng.com/store/c0/c0/b0/u0/p1/v9/s9/t0/u0/i1/ALL.html
                    http://book.zongheng.com/store/c0/c0/b0/u0/p2/v9/s9/t0/u0/i1/ALL.html
                    http://book.zongheng.com/store/c0/c0/b0/u0/p4/v9/s9/t0/u0/i1/ALL.html
                     */
                    int index=Integer.parseInt(url.substring(44,45));
                    if(index<999){
                        url=LoadPropertyUtil.getZongheng("listUrlStartWith")+index+++LoadPropertyUtil.getZongheng("listUrlendWit");
                        if(!this.existUrl(url)){
                            redisQueueRepositoryService.addUrltoAllZongheng("spider_zongheng",keyName,url,localDoneSet,zonghengLogService);
                        }
                    }
                }else if(url.startsWith(LoadPropertyUtil.getZongheng("detailUrlStartWith"))){
                    //直接消费
                    zonghengLogService.info("节点"+keyName+"正在解析详细页-url:"+url);
                    zonghengLogService.addUrlDetailNum(1);
                    String content=httpClientDownLoadService.download(url);
                    ZonghengPage page=new ZonghengPage();
                    page.setUrl(url);
                    page.setDate(new Date());
                    page.setContent(content);
                    zonghengProcessService.process(page);
                    mysqlStoreService.store(page);
                    zonghengLogService.addSuccessNum(1);
                }
                //  休息一个随机时间（3-5s）再抓取下一个页面，防止被封
                int time=(int)(Math.random()*(8000-1000+1))+1000;
                TimeUnit.MILLISECONDS.sleep(time);
            } catch (Exception e) {
                zonghengLogService.addFailNum(1);
                //解析失败的url加入失败队列
                if(url!=null&&url.length()!=0)
                    zonghengLogService.addFailList(url);
                e.printStackTrace();
            }finally {
                conditionController.getLock().unlock();
            }

        }
    }
    private void resolveContent(String content){
        List<String> queues=getAllQueue();
        //对应电影详情url
        String regex=LoadPropertyUtil.getZongheng("bookDetailUrl");
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher=pattern.matcher(content);
//        int index=0;
        int keyIdx=0;
        while(matcher.find()){
            String url=matcher.group();
            if(!this.existUrl(url)){
                //获取所有的进程队列，平均分配
                int idx=keyIdx%queues.size();
                String key=queues.get(idx);
                redisQueueRepositoryService.addUrltoAllZongheng(key,keyName,url,localDoneSet,zonghengLogService);
                keyIdx++;
            }
        }
    }

    /**
     * 验证url是否已经抓取过了
     * 验证逻辑：
     *  1）节点本地验证，若存在，那肯定爬过了，不存在->2)
     *  2）redis验证,先用布隆过滤器验证，若不存在，加入url，若返回存在->3)
     *  3)遍历redis中的doneSet验证
     * @param url
     * @return  任务队列中已经存在返回true
     */
    public boolean existUrl(String url){
        if(localDoneSet.contains(url))
            return true;
        else {
            //到redis去验证
            return redisQueueRepositoryService.existUrlInRedis(url);
        }
    }
    /**
     *  获取所有的爬虫进程队列
     * @return
     */
    private List<String> getAllQueue(){
        List<String> queues=new ArrayList<>();
        try {
            //ZKUtil.PATH节点下全是瞬时ip节点
            List<String> currentChildrenList = client.getChildren().forPath(ZKUtil.PATH);
            for(String ip:currentChildrenList){
                String key="spider_zongheng"+ip;
                queues.add(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return queues;
    }

    /**
     * 从别的进程队列窃取任务
     * @return
     */
    private String steal(){
        //轮询所有队列，窃取任务
        List<String> queues=getAllQueue();
        queues.add("spider_zongheng");
        for(int i=queues.size()-1;i>=0;i--){
            String key=queues.get(i);
            String url=redisQueueRepositoryService.pop(key);
            if(url!=null)return url;
        }
        return null;
    }
}
