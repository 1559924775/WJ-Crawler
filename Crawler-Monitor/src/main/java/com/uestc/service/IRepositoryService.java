package com.uestc.service;
/**
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
}
