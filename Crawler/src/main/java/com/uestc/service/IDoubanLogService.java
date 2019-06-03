package com.uestc.service;


import java.util.Date;

/**
 * 日志服务，RPC调用
 * @author 王俊
 */
public interface IDoubanLogService {
    void info(String msg);

    void error(String msg);

    void debug(String msg);

    void warn(String msg);

    void  trace(String msg);

    void setLog(String msg);

    void addUrlListNum(int num);

    void addUrlDetailNum(int num);

    void addSuccessNum(int num);

    void addFailNum(int num);

    void setDate(Date Date);

    void setStartTime(Date Date);

    void setEndTime(Date Date);

    void addFailList(String url);
}
