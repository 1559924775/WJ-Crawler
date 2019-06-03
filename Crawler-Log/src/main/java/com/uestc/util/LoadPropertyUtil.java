package com.uestc.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 读取配置文件属性工具类
 * @author 王俊
 */
public class LoadPropertyUtil {
	
	//读取优酷配置文件
	public static String getYOUKU(String key){
		String value = "";
		Locale locale = Locale.getDefault();
		try {
			ResourceBundle localResource = ResourceBundle.getBundle("youku",
					locale);
			value = localResource.getString(key);
		} catch (MissingResourceException mre) {
			value = "";
		}
		return value;
	}
	//读取猫眼配置文件
	public static String getMAOYAN(String key){
		String value = "";
		Locale locale = Locale.getDefault();
		try {
			ResourceBundle localResource = ResourceBundle.getBundle("maoyan",
					locale);
			value = localResource.getString(key);
		} catch (MissingResourceException mre) {
			value = "";
		}
		return value;
	}
	//读取豆瓣配置文件
	public static String getDouban(String key){
		String value = "";
		Locale locale = Locale.getDefault();
		try {
			ResourceBundle localResource = ResourceBundle.getBundle("douban",
					locale);
			value = localResource.getString(key);
		} catch (MissingResourceException mre) {
			value = "";
		}
		return value;
	}
	//读取豆瓣配置文件
	public static String getZongheng(String key){
		String value = "";
		Locale locale = Locale.getDefault();
		try {
			ResourceBundle localResource = ResourceBundle.getBundle("zongheng",
					locale);
			value = localResource.getString(key);
		} catch (MissingResourceException mre) {
			value = "";
		}
		return value;
	}
	//读取定时任务配置文件
	public static String getCrontab(String key){
		String value = "";
		Locale locale = Locale.getDefault();
		try {
			ResourceBundle localResource = ResourceBundle.getBundle("crontab",
					locale);
			value = localResource.getString(key);
		} catch (MissingResourceException mre) {
			value = "";
		}
		return value;
	}
	//读取解码配置文件
	public static String getDecode(String key){
		String value = "";
		Locale locale = Locale.getDefault();
		try {
			ResourceBundle localResource = ResourceBundle.getBundle("decode",
					locale);
			value = localResource.getString(key);
		} catch (MissingResourceException mre) {
			value = "";
		}
		return value;
	}
	//读取reidsUitl配置文件
	public static String getRedis(String key){
		String value = "";
		Locale locale = Locale.getDefault();
		try {
			ResourceBundle localResource = ResourceBundle.getBundle("redis",
					locale);
			value = localResource.getString(key);
		} catch (MissingResourceException mre) {
			value = "";
		}
		return value;
	}
	//读取ZKUtil配置文件
	public static String getZK(String key){
		String value = "";
		Locale locale = Locale.getDefault();
		try {
			ResourceBundle localResource = ResourceBundle.getBundle("zookeeper",
					locale);
			value = localResource.getString(key);
		} catch (MissingResourceException mre) {
			value = "";
		}
		return value;
	}
}
