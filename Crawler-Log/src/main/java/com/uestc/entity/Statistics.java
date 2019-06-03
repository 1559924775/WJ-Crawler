package com.uestc.entity;


import java.util.Date;
import java.util.List;

/**
 * @author 王俊
 * 统计爬虫的信息，每爬一次统计一次
 */
@lombok.Data
public class Statistics {
    String name; //爬虫任务的名字
    int url_list_num;//处理列表页的个数
    int url_detail_num;//处理详细页的个数
    int success_num;//成功个数
    int fail_num;//失败个数
    Date date;//任务执行日期
    Date startTime;//开始时间
    Date endTime;//结束时间
    List<String> failList;//解析失败的url队列
}
