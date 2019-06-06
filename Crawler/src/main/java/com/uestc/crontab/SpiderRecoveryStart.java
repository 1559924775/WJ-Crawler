package com.uestc.crontab;

import com.uestc.start.RunnableStart;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 用于节点发生故障后直接启动 ，只会运行一次，到下一次定时，任务就会正常启动
 * @author 王俊
 */
//@Component
public class SpiderRecoveryStart implements ApplicationRunner, ApplicationContextAware {
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        RunnableStart runnableStart=applicationContext.getBean(RunnableStart.class);
        runnableStart.setRecovery(true);
        runnableStart.start();
    }

    ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}
