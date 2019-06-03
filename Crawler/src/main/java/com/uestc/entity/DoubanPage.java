package com.uestc.entity;

import lombok.Data;
import org.apache.solr.client.solrj.beans.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 存储页面信息实体类      一条豆瓣电影数据记录
 * 表 1-记录不变的字段
 * 表 2-记录每天变化的字段
 *
 */
@Data   //
public class DoubanPage implements  Page{


	//html页面内容
	String content;

	//存入表1
	//导演
	private String director;
	//语言
	private String language;
	//时长
	private  String duration;
	//影片类型
	private String type;
	//上映时间
	private String time;
	//制片地区
	private String area;
	//电影名称
	private String moveName;
	//本页面url
	private String url;

	//存入表2
	//评分人数   （针对刚上映的电影）
	private String commentnumber;
	//用户评分
	private String score;
	private Date date; //爬取的日期时间,有长期的数据才能观察趋势

	public String getContent(){
		return this.content;
	}
	public String getUrl(){
		return  this.url;
	}
}

