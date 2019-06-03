package com.uestc.crontab;

import com.uestc.service.impl.DoubanLogServiceImpl;
import com.uestc.service.impl.ZonghengLogServiceImpl;
import com.uestc.util.LoadPropertyUtil;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import sun.rmi.runtime.Log;

/**
 * 分类url定时抓取job
 * Created by 王俊
 *
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
			DoubanLogServiceImpl doubanLogService = applicationContext.getBean(DoubanLogServiceImpl.class);
            ZonghengLogServiceImpl zonghengLogService =applicationContext.getBean(ZonghengLogServiceImpl.class);
            JobDataMap jobDataMap=new JobDataMap();
            jobDataMap.put("doubanLogService",doubanLogService);
            jobDataMap.put("zonghengLogService",zonghengLogService);

			//被调度的任务
			JobDetail jobDetail = new JobDetail(LoadPropertyUtil.getCrontab("jobName"), Scheduler.DEFAULT_GROUP, LogJob.class);
            jobDetail.setJobDataMap(jobDataMap);
			//定时执行任务
			CronTrigger trigger = new CronTrigger("spider-job", Scheduler.DEFAULT_GROUP, LoadPropertyUtil.getCrontab("cronExpression"));

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
