package com.uestc.service;

import java.util.Set;

/**
 * 操作工作队列
 * @author 王俊
 */
public interface IRepositoryService {
    public String take()throws InterruptedException ;

    public void add(String url);

    public void add(String key, String url);

    public String poll();

    public String pop();

    public String poll(String key);

    public String pop(String key);

    public void setQueue(String name);

    public void removeAll(String key);

    public boolean existUrlInRedis(String url);

    public void addUrltoAllZongheng(String key, String keyName, String url, Set<String> localDoneSet, IZonghengLogService zonghengLogService);

    public void addUrltoAllDouban(String key, String keyName, String url, Set<String> localDoneSet, IDoubanLogService doubanLogService);

    public void addUrltoAll(String key, String url);
}
