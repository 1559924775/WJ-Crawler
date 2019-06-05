package com.uestc.crontab;

import com.uestc.service.impl.RedisQueueRepositoryService;
import com.uestc.start.RunnableStart;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import com.uestc.util.*;

/**
 * 分类url定时抓取job
 * @author 王俊
 */
@Component
public class UrlScheduler implements ApplicationRunner  ,ApplicationContextAware{


	@Override
	public void run(ApplicationArguments applicationArguments) throws Exception {
		try {
			//获取默认调度器
			Scheduler defaultScheduler = StdSchedulerFactory.getDefaultScheduler();
			//开启调度器
			defaultScheduler.start();

			/**
			 * (1)在进行任务调度时JobDataMap存储在JobExecutionContext中，非常方便获取
			 * (2)JobDataMap可以用来装载任何可以序列化的数据对象，当job实例对象被执行时这些参数对象会传递给它
			 * (3)JobDataMap实现了JDK的Map接口，并且添加了一些非常方便的方法用来存取数据基本数据类型。
			 */
            RedisQueueRepositoryService redisQueueRepositoryService = applicationContext.getBean(RedisQueueRepositoryService.class);
            RunnableStart runnableStart =applicationContext.getBean(RunnableStart.class);
            JobDataMap jobDataMap=new JobDataMap();
            jobDataMap.put("redisQueueRepositoryService",redisQueueRepositoryService);
            jobDataMap.put("runnableStart",runnableStart);

			//被调度的任务
			JobDetailImpl jobDetail = new JobDetailImpl(LoadPropertyUtil.getCrontab("jobName"), Scheduler.DEFAULT_GROUP, SpiderJob.class);
            jobDetail.setJobDataMap(jobDataMap);
			//定时执行任务
			CronTrigger trigger = new CronTriggerImpl("spider-job", Scheduler.DEFAULT_GROUP, LoadPropertyUtil.getCrontab("cronExpression"));

			//添加调度任务
			defaultScheduler.scheduleJob(jobDetail , trigger);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	ApplicationContext applicationContext;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext=applicationContext;
	}
}
