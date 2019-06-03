package com.uestc.start;




import com.uestc.service.IDownLoadService;
import com.uestc.service.impl.HttpClientDownLoadService;
import com.uestc.util.RedisUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 爬取免费代理ip到redis中作为代理ip
 * @author 王俊
 */
public class IPSpiderToRedis {
    public static void main(String[] args) throws Exception{
        String url="https://www.kuaidaili.com/free/inha/18/";
        int idx=19;
        while(true){
            url="https://www.kuaidaili.com/free/inha/"+idx+"/";
            idx++;
            start(url);
            TimeUnit.MILLISECONDS.sleep(2000);
        }

    }

    private static void start(String url){

        IDownLoadService iDownLoadService=new HttpClientDownLoadService();
        String content=iDownLoadService.download(url);

        String regex="<td data-title=\"IP\">[0-9]+[.]{1}[0-9]+[.]{1}[0-9]+[.]{1}[0-9]+";
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher=pattern.matcher(content);
        List<String> list=new ArrayList<String>();

        while(matcher.find()){
            String s=matcher.group().replace("<td data-title=\"IP\">","");
            list.add(s);
        }
        regex ="<td data-title=\"PORT\">[0-9]+";
        pattern=Pattern.compile(regex);
        matcher=pattern.matcher(content);
        int index=0;
        while(matcher.find()){
            String s=matcher.group().replace("<td data-title=\"PORT\">","");
            s=list.get(index)+":"+s;
            list.set(index++,s);
        }
        RedisUtil redisUtil = new RedisUtil();
        for(String s:list){
            System.out.println(s);
            redisUtil.addSet("proxy",s);
        }
    }
}