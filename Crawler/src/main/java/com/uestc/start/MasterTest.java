package com.uestc.start;

import com.uestc.util.ZKUtil;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MasterTest {
    public static void main(String[] args) throws InterruptedException {
        final CountDownLatch countDownLatch=new CountDownLatch(1);
        Thread t1=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new MasterTest().test();
            }
        });
        t1.setName("t1");

        Thread t2=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new MasterTest().test();
            }
        });
        t2.setName("t2");
        t2.start();
        t1.start();
        countDownLatch.countDown();


    }
    public void test(){
//重试策略:重试3次，每次间隔时间指数增长(有具体增长公式)
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        //zk地址
        String hosts = ZKUtil.ZOOKEEPER_HOSTS;
        CuratorFramework client = CuratorFrameworkFactory.newClient(hosts, retryPolicy);
        //建立连接
        client.start();
        try {
            client.create().forPath("/master-election");
            takeLeaderShip(client);
            System.out.println(Thread.currentThread().getName()+"竞选成功！！！！");
        } catch (KeeperException.NodeExistsException e1){
            System.out.println(Thread.currentThread().getName()+"竞选失败");
            elecctionFail(client);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * 竞选成功后主节点要执行的操作
     */
    public void takeLeaderShip(CuratorFramework client){
        //从数据库拿到种子url加入到对应的主队列中去
        try {
            TimeUnit.MILLISECONDS.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //创建/master_election/success节点
        try {
            client.create().forPath("/master-election/success");
            System.out.println("master:"+Thread.currentThread().getName()+"开始爬虫任务");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 竞选失败后从节点要进行的操作
     */
    public void elecctionFail(CuratorFramework client){
        //监听master_election节点，如果创建了子节点，则开始自己的爬虫任务
        NodeCache cache=new NodeCache(client,"/master-election/success",false);
        try {
            cache.start(true);
            cache.getListenable().addListener(new NodeCacheListener() {
                @Override
                public void nodeChanged() throws Exception {
                    //可以开始爬虫任务了
                    System.out.println(Thread.currentThread().getName()+"开始爬虫任务");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            TimeUnit.MILLISECONDS.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
