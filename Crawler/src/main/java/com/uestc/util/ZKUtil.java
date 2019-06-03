package com.uestc.util;


/**
 * zookeeper 工具类
 * @author 王俊
 *
 */
public class ZKUtil {
	//Zookeeper 永久节点
    public static final String PATH = LoadPropertyUtil.getZK("path");
    //Zookeeper连接地址
    public static final String ZOOKEEPER_HOSTS = LoadPropertyUtil.getZK("zookeeper_host");
}