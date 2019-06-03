package com.uestc.crontab;


import com.uestc.service.impl.RedisQueueRepositoryService;
import com.uestc.start.RunnableStart;
import com.uestc.util.RedisUtil;
import org.junit.Test;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * master向redis 添加分类url
 * @author 王俊
 *
 */
public class SpiderJob implements Job {
	RedisQueueRepositoryService redisQueueRepositoryService;
	RunnableStart runnableStart;
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		//每天定时开启去爬取数据，来分析趋势

		JobDataMap jobDataMap=arg0.getMergedJobDataMap();
		redisQueueRepositoryService= (RedisQueueRepositoryService) jobDataMap.get("redisQueueRepositoryService");
		runnableStart = (RunnableStart) jobDataMap.get("runnableStart");

		//清除掉上一个周期的doneSet-------应该交给清理系统去做
		RedisUtil redisUtil=new RedisUtil();
		redisUtil.deleteKey("doneSet");
		//各个节点竞选出一个master，由master去数据库取出起始url加入的主队列中
		/**
		 * 竞选master:
		 * 	所有节点去创建master-election节点，创建成功者为主节点
		 * 		失败者注册该节点的watch，监听到节点掉线，重新选举
		 * 		成功者拉取数据后创建/master-election/success节点
		 * 		失败者监听到success，开始工作
		 * 	掉线重连的节点发现已经有success节点了，直接开始工作
		 * 	每个周期结束之后，要清理节点。
		 */

		//掉线重连的检查工作不应该写在定时任务中，应该写个直接开始的ApplicatinoRunner
		//逻辑在RunnableStart中

//
//		Map<String, List<String>> map=new HashMap<>();// 网站—起始url
//		//从txt文件中读取
//		readStartUrlFromFile(map,"douban");
//		readStartUrlFromFile(map,"zongheng");
//		for(String str : map.keySet()){
//			//放入到对应的队列中
//			redisQueueRepositoryService.setQueue("spider_"+str);
//			List<String> urls=map.get(str);
//			for(String url:urls){
//				redisQueueRepositoryService.add(url);
//			}
//
//		}
		runnableStart.start();
	}
	public void readStartUrlFromFile(Map<String, List<String>> map,String fileName){
		//加入fileName爬虫的起始url
		List<String> list=new ArrayList<>();
		try {
			File file=new File(fileName+".txt");
			FileReader reader=new FileReader(file.getAbsolutePath());
			BufferedReader bufferedReader=new BufferedReader(reader);
			String url=bufferedReader.readLine();
			while(url!=null&&url.length()!=0) {
				list.add(url);
				url = bufferedReader.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		map.put(fileName,list);
	}
}
