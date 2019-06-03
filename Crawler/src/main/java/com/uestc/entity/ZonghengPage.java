package com.uestc.entity;



import lombok.Data;

import java.util.Date;

/**
 * 表1-记录不变的字段
 * 表2-记录每天变化的字段
 */
@Data
public class ZonghengPage implements  Page{

    //存入表1
    private long id;
    private String name;
    private String author;
    private String number;//字数
    private String  url;
    private String type;

    //存入表2
    private String recommend;//总推荐数
    private String click;//总点击量
    private Date date; //爬取的日期时间,有长期的数据才能观察趋势

    //html
    private String content; //内容
    private String recommend_week;//周推荐数

    public String getContent(){
        return this.content;
    }
    public String getUrl(){
        return  this.url;
    }
}
