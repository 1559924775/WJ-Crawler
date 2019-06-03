package com.uestc.service.impl;


import com.uestc.service.IDoubanLogService;
import com.uestc.service.IDownLoadService;
import com.uestc.service.IRepositoryService;
import com.uestc.service.IZonghengLogService;
import com.uestc.util.RedisUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Transaction;

import java.util.Set;

/**
 * 操作工作队列的服务
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

    public boolean existUrlInRedis(String url){
        //先用布隆过滤器验证，返回不存在那肯定不存在
        if(!redisUtil.existsFilter(url)){
            return false;
        }else{
            //用doneSet验证
            return redisUtil.existsUrl(url);
        }
    }

    /**
     * 将url加入任务队列，本地以爬取任务set，reids中已爬取任务set
     * @param key   是主队列或者节点队列
     * @param keyName
     * @param url
     * @param localDoneSet
     * @param doubanLogService
     */
    @Override
    public void addUrltoAllDouban(String key, String keyName, String url, Set<String> localDoneSet, IDoubanLogService doubanLogService){
        //redis事务 （这里不需要，只用保证1在2之前就行了）
        Transaction transaction=redisUtil.newTransaction();
        try{
            //顺序不能乱，先加入任务节点，在加入记录set，所以要用事务
            //如果先加入了set，
            transaction.lpush(key,url);//this是prototype的            1)
            if(localDoneSet!=null)
                localDoneSet.add(url);//加入到以爬取的url的list中
            doubanLogService.info("节点"+keyName+"加入本地localDoneSet: "+url);
            transaction.sadd("doneSet",url);                  //  2)
            doubanLogService.info("节点"+keyName+"加入reids的doneSet: "+url);
            transaction.exec();
            redisUtil.addDoneSetFilter(url);
            doubanLogService.info("节点"+keyName+"加入reids的布隆过滤器: "+url);
            transaction.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 将url加入任务队列，本地以爬取任务set，reids中已爬取任务set
     * @param key   是主队列或者节点队列
     * @param keyName
     * @param url
     * @param localDoneSet
     * @param zonghengLogService
     */
    public void addUrltoAllZongheng(String key, String keyName, String url, Set<String> localDoneSet, IZonghengLogService zonghengLogService){
        Transaction transaction=redisUtil.newTransaction();
        try{
            //顺序不能乱，先加入任务节点，在加入记录set，所以要用事务
            transaction.lpush(key,url);//this是prototype的
            if(localDoneSet!=null)
                localDoneSet.add(url);//加入到以爬取的url的list中
            zonghengLogService.info("节点"+keyName+"加入本地localDoneSet: "+url);
            transaction.sadd("doneSet",url);
            zonghengLogService.info("节点"+keyName+"加入reids的doneSet: "+url);
            transaction.exec();
            redisUtil.addDoneSetFilter(url);
            zonghengLogService.info("节点"+keyName+"加入reids的布隆过滤器: "+url);
            transaction.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //起始url加入
    public void addUrltoAll(String key, String url){
        //redis事务 （这里不需要，只用保证1在2之前就行了）
        Transaction transaction=redisUtil.newTransaction();
        try{
            //顺序不能乱，先加入任务节点，在加入记录set，所以要用事务
            //如果先加入了set，
            transaction.lpush(key,url);//this是prototype的            1)
            transaction.sadd("doneSet",url);                  //  2)
            transaction.exec();
            redisUtil.addDoneSetFilter(url);
            transaction.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
