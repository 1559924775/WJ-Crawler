package com.uestc.service.impl;


import com.uestc.service.IRepositoryService;
import com.uestc.util.RedisUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * @author 王俊
 */
@Scope("prototype")
@Service
@Data
public class RedisQueueRepositoryService implements IRepositoryService {

    private RedisUtil redisUtil=null;
    private String name=null;  //队列的名字
    public RedisQueueRepositoryService()
    {
        redisUtil=new RedisUtil();
    }
    @Override
    public String poll()  {
        String url=redisUtil.poll(name);
        return url;
    }

    @Override
    public String pop() {
        String url=redisUtil.pop(name);
        return url;
    }
    @Override
    public String poll(String key)  {
        String url=redisUtil.poll(key);
        return url;
    }

    @Override
    public String pop(String key) {
        String url=redisUtil.pop(key);
        return url;
    }

    @Override
    public void setQueue(String name) {
        this.name=name;
    }

    @Override
    public void removeAll(String key) {
        redisUtil.removeAll(key);
    }

    @Override
    public String take() throws InterruptedException {
        return null;
    }

    public void add(String url) {
        redisUtil.add(name,url);
    }

    public void add(String key,String url) {
        redisUtil.add(key,url);
    }
}
