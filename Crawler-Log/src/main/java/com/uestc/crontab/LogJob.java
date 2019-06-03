package com.uestc.crontab;

import com.uestc.service.impl.DoubanLogServiceImpl;
import com.uestc.service.impl.ZonghengLogServiceImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 统一的日志服务
 * @author 王俊
 */
@Data
@Slf4j
public class LogJob implements Job {

    //通过jobExecutionContext获得
    DoubanLogServiceImpl doubanLogService;
    ZonghengLogServiceImpl zonghengLogService;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap= jobExecutionContext.getMergedJobDataMap();
        doubanLogService = (DoubanLogServiceImpl) jobDataMap.get("doubanLogService");
        zonghengLogService = (ZonghengLogServiceImpl) jobDataMap.get("zonghengLogService");
        log.info("=================================爬虫周期=======================================----------------------");
        doubanLogService.execute();
        zonghengLogService.execute();
        log.info("=================================爬虫周期=============================================================");
    }
}
