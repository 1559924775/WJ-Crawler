package com.uestc.start;


import com.uestc.service.IRepositoryService;
import com.uestc.util.EmailUtil;
import com.uestc.util.LoadPropertyUtil;
import com.uestc.util.RedisUtil;
import com.uestc.util.ZKUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 监视器
 * @author 王俊
 *
 */
@Component
@Slf4j
public class SpiderWatcher implements Watcher , ApplicationRunner {
	CuratorFramework client;
	List<String> oldChildrenList = new ArrayList<String>();
	@Autowired
	IRepositoryService redisQueueRepositoryService;

	RedisUtil redisUtil;
	//构造方法
	public SpiderWatcher() {
		redisUtil=new RedisUtil();
		//重试策略:重试3次，每次间隔时间指数增长(有具体增长公式)
		int baseSleepTimeMs= Integer.parseInt(LoadPropertyUtil.getZK("baseSleepTimeMs"));
		int maxRetries=Integer.parseInt(LoadPropertyUtil.getZK("maxRetries"));
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries);
		//zk地址
		String hosts = ZKUtil.ZOOKEEPER_HOSTS;
		client = CuratorFrameworkFactory.newClient(hosts,
				retryPolicy);
		//建立连接
		client.start();

		try {
			//获取子节点集合
			oldChildrenList = client.getChildren().usingWatcher(this)
					.forPath(ZKUtil.PATH);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//具体业务实现
	public void exceptionHandle() throws InterruptedException, KeeperException {
		try {
			//监控到变化，获取当前子节点，并再次注册watcher //watcher是一次性的
			List<String> currentChildrenList = client.getChildren()
					.usingWatcher(this).forPath(ZKUtil.PATH);
			for (String child : currentChildrenList) {
				if (!oldChildrenList.contains(child)) {
					log.info("新的爬虫节点上线：" + child);
				}
			}
			for (String child : oldChildrenList) {
				if (!currentChildrenList.contains(child)) {
					log.info("挂掉的爬虫节点为：" + child);
					String subject = "爬虫项目执行异常提醒";
					String message = "ip为"+child+"服务器上的爬虫项目执行异常，请及时处理！！！";


					//把队列中任务放入到容灾队列中去
					this.toRecovery(child,"spider_douban");
					this.toRecovery(child,"spider_zongheng");

					//正常的情况下应该先把信息记录到数据库再用一个监控数据库的项目去发送邮件。
					log.info(subject+":"+message);
					EmailUtil.sendEmail(subject, message);


				}
			}
			//子节点集合更新
			this.oldChildrenList = currentChildrenList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * 把队列中任务放入到容灾队列中去
	 * @param ip		爬虫进程ip
	 * @param str		爬虫对应的字符串
	 */
	public void toRecovery(String ip,final String str) throws Exception {
		List<String> queue= redisUtil.lrange(str+ip,0,-1);
		//将所有数据加入容灾队列   先放到recovery保险
		for(String url:queue){
			redisUtil.add(str+"_recovery",url);
		}
		//并将原来的节点队列清空，因为这个节点可能不会再上线了
		redisQueueRepositoryService.removeAll(str+ip);


		//如果还有在线的爬虫进程，将任务平均分配到这些爬虫进程的队列中去
		//如果爬虫项目结束，那么关闭监控项目
		//如果爬虫项目没有结束，启动一个线程等待上线，并将容灾对列中任务分配出去
		final int size=queue.size();
		Thread t=new Thread(new Runnable() {
			@Override
			public void run() {
				int idx=size;
				while(true){
//					等待爬虫上线，将容灾对列中任务分配出去
					List<String> currentTaskList = null;
					try {
						currentTaskList = client.getChildren().forPath(ZKUtil.PATH);
						if(!currentTaskList.isEmpty()){
							//把容灾队列中任务平均分发到进程队列中去
							while(idx>0){
								for(String iip:currentTaskList){
									String key=str+iip;
									String url=redisQueueRepositoryService.poll(str+"_recovery");
									idx--;
									if(url==null)return;
									redisQueueRepositoryService.add(key,url);
								}
							}
							return;
						}
						//爬虫还没有上线，等待五分钟
						TimeUnit.MILLISECONDS.sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		t.start();

	}
	//监控节点变化
	public void process(WatchedEvent event) {
		if (event.getType() == Event.EventType.NodeChildrenChanged) {
			try {
				//调用具体业务代码
				exceptionHandle();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeeperException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	@Override
	public void run(ApplicationArguments applicationArguments) throws Exception {
		System.in.read();//然后一直监控
	}
}
