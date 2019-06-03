package com.uestc.service.impl;

import com.uestc.entity.Statistics;
import com.uestc.service.IStaticsLogService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 统计各个节点的汇总的信息
 * @author 王俊
 */
@Slf4j
@Service
public class StaticsLogServiceImpl implements IStaticsLogService {
    @Override
    public void executeStatics(Statistics info) {
        log.info("--------------------------------------------------------------------------------");
        log.info(""+info.getName()+"爬虫任务");
        log.info("一共解析的列表页url个数:"+info.getUrl_list_num());
        log.info("一共解析的详细页url个数:"+info.getUrl_detail_num());
        log.info("成功解析的url个数:"+info.getSuccess_num());
        log.info("解析失败的url个数:"+info.getFail_num());
        Date startTime=info.getStartTime();
        if(startTime!=null)
            log.info("任务开始执行的时间:"+info.getStartTime().toLocaleString());
        else
            log.info("任务开始执行的时间:未记录");
        Date endTime=info.getEndTime();
        if(endTime!=null)
            log.info("任务结束执行的时间:"+info.getEndTime().toLocaleString());
        else
            log.info("任务结束执行的时间:未记录");
        log.info("--------------------------------------------------------------------------------");
        List<String> failList=info.getFailList();
        //将failList写到文件中记录起来
        try (FileWriter fileWriter = new FileWriter("./failList.txt")) {
            for (String url:failList)
                fileWriter.write(url+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    @Test
//    public void test(){
//        List<String> failList=new ArrayList<>();
//        failList.add("https://movie.douban.com/subject/3927736"+"\n");
//        failList.add("https://movie.douban.com/subject/3927736"+"\n");
//        failList.add("https://movie.douban.com/subject/3927736"+"\n");
//        failList.add("https://movie.douban.com/subject/3927736"+"\n");
//        failList.add("https://movie.douban.com/subject/3927736"+"\n");
//        failList.add("https://movie.douban.com/subject/3927736"+"\n");
//        try (FileWriter fileWriter = new FileWriter("./failList.txt")) {
//            for (String url:failList)
//                fileWriter.write(url);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
