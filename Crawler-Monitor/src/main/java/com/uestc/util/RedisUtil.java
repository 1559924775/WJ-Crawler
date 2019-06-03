package com.uestc.util;



import io.rebloom.client.Client;
import lombok.Data;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.util.Pool;

import java.util.List;

/**
 * 爬到一个url，在加入到任务队列之前，先使用布隆过滤器判断，
 * 	1）若返回不存在，方心加入
 * 	2）若返回存在，未必真的存在，要进一步查找（redis维护一个set）
 */

/**
 * 操作redis数据库的工具类
 * @author 王俊
 *
 */
@Data
public class RedisUtil {

	Jedis resource = null;
	Pool<Jedis> jedisPool;
	Client client;//布隆过滤器使用
	public RedisUtil() {


		String ip=LoadPropertyUtil.getRedis("redis_ip");
		int port=Integer.parseInt(LoadPropertyUtil.getRedis("redis_port"));
		jedisPool=(Pool<Jedis>)new JedisPool(ip,port);
		resource=  jedisPool.getResource();
//		resource.auth(LoadPropertyUtil.getRedis("redis_pwd"));
		client= new Client(jedisPool);
		System.out.println(resource.ping());
	}

	/**
	 * 查询
	 *
	 * @param key
	 * @param start
	 * @param end 			为-1 时代表获取所有
	 * @return
	 */
	public List<String> lrange(String key, int start, int end) {

		List<String> list = resource.lrange(key, start, end);
		return list;

	}

	/**
	 * 添加队列  l进 r出
	 *
	 * @param Key
	 * @param url
	 */
	public void add(String Key, String url) {
		resource.lpush(Key, url);
	}


	/**
	 * 获取队列 FIFO    l进 r出
	 *
	 * @param key
	 * @return
	 */
	public String poll(String key) {
		String result = resource.rpop(key);
		return result;
	}

	/**
	 * 获取栈  LIFO    l进 l出
	 * @param key
	 * @return
	 */
	public String pop(String key) {
		String result = resource.lpop(key);
		return result;
	}

	/**
	 * 清空列表
	 * @param key
	 */
	public void removeAll(String key){
		resource.ltrim(key,1,0);
	}

	/**
	 * 添加set
	 *
	 * @param Key
	 * @param value
	 */
	public void addSet(String Key, String value) {
		resource.sadd(Key, value);
	}

	/**
	 * 随机获取Set 值
	 *
	 * @param key
	 */
	public String getSet(String key) {
		String value = resource.srandmember(key);
		return value;
	}

	/**
	 * 删除Set 随机值
	 *
	 * @param key
	 * @param value
	 */
	public void deleteSet(String key, String value) {
		resource.srem(key, value);
	}

	public static void main(String[] args) {
		RedisUtil redisUtil = new RedisUtil();
		String s=redisUtil.poll("spider_zongheng192.168.1.1");
		System.out.println(s);
	}

	/**
	 * 删除一个键   每一个爬虫周期都有一个urllist来存爬过的url.一个周期完删掉
	 * @param key
	 */
	public void deleteKey(String key){
		resource.del(key);
	}

	/**
	 * 布隆过滤器判断是否存在
	 * @param val
	 */
	public boolean existsFilter(String val){
		return client.exists("doneSetFilter",val);
	}

	/**
	 * 加入布隆过滤器
	 * @param val
	 */
	public void addDoneSetFilter(String val){
		client.add("doneSetFilter",val);
	}

	/**
	 * 所有爬过的url加入set中避免重复
	 * @param val
	 */
	public void addDoneSet(String val){
		addSet("doneSet",val);
	}

	/**
	 * 布隆过滤器不能验证，到doneSet中去验证
	 * @param val
	 * @return
	 */
	public boolean existsUrl(String val){
		return resource.sismember("doneSet",val);
	}
	public Transaction newTransaction(){
		Transaction transaction = resource.multi();//是new出来的this.transaction = new Transaction(this.client);
		return  transaction;
	}

//	@Test
//	public void test3() {
//		Client client = new Client(jedisPool);
//		resource.del("book");
//		for (int i = 0; i < 1000; i++) {
//			String book = "book" + i;
//			client.add("book", book);
//			boolean ret = client.exists("book", book); // 判断自己见过的，没有出现误判
//			System.out.println(ret);
//			if (!ret) {
//				System.out.println(i);
//			}
//		}
//		jedisPool.close();
//	}

	@Test
	public void test(){
		try {
			Transaction transaction = resource.multi();
			transaction.lpush("key", "11");
			transaction.lpush("key", "22");
			transaction.lpush("key", "33");
			List<Object> list = transaction.exec();
		} catch (JedisDataException e) {

		}
		List<String> list=resource.lrange("key",0,-1);
		System.out.println(list);
		System.out.println(this.poll("key"));
	}
}