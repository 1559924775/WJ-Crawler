package com.uestc.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.uestc.entity.Statistics;
import com.uestc.service.IDoubanLogService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.*;

/**
 * @王俊
 *  统计信息：
 *      1）共爬取列表url个数
 *      2）共爬取详细页url个数
 *      3）成功多少，失败多少
 *      4）失败的url记录
 *  动态信息：
 *      某节点正在解析url
 *      休息几分钟
 */

@Component
@Service(interfaceClass = IDoubanLogService.class)
@Slf4j
public class DoubanLogServiceImpl implements IDoubanLogService {

    @Autowired
    StaticsLogServiceImpl staticsLogService;
    List<String> failList;
    Statistics info;
    {
        info=new Statistics();
        info.setName("spider_douban");
        failList=new ArrayList<>();
        info.setFailList(failList);
    }

    @Override
    public synchronized void info(String msg) {
        log.info(msg);
    }

    @Override
    public synchronized void error(String msg) {
        log.error(msg);
    }

    @Override
    public synchronized void debug(String msg) {
        log.debug(msg);
    }

    @Override
    public synchronized void warn(String msg) {
        log.warn(msg);
    }

    @Override
    public synchronized void trace(String msg) {
        log.trace(msg);
    }

    @Override
    public synchronized void setLog(String msg) {
        info(msg);
    }

    @Override
    public synchronized void addUrlListNum(int num) {
        info.setUrl_list_num(info.getUrl_list_num()+num);
    }

    @Override
    public synchronized void addUrlDetailNum(int num) {
        info.setUrl_detail_num(info.getUrl_detail_num()+num);
    }

    @Override
    public synchronized void addSuccessNum(int num) {
        info.setSuccess_num(info.getSuccess_num()+num);
    }

    @Override
    public void addFailNum(int num) {
        info.setFail_num(info.getFail_num()+num);
    }

    @Override
    public synchronized void setDate(Date Date) {
        info.setDate(Date);
    }

    @Override
    public synchronized void setStartTime(Date Date) {
        info.setStartTime(Date);
    }

    @Override
    public synchronized void setEndTime(Date Date) {
        info.setEndTime(Date);
    }

    @Override
    public void addFailList(String url) {
        info.getFailList().add(url);
    }


    public void execute() throws JobExecutionException {
        //每次执行要将statics中的统计信息写入到日志中去。
        staticsLogService.executeStatics(info);
    }
}
