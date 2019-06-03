package com.uestc.crontab;

import com.uestc.start.RunnableStart;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 快速启动无需定时，仅仅用于测试
 * @author 王俊
 */
@Component
public class SpiderQuickStart implements ApplicationRunner, ApplicationContextAware {

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        RunnableStart runnableStart=applicationContext.getBean(RunnableStart.class);
        System.out.println("开始了————————————————————————————————————");
        runnableStart.start();
    }

    ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}
