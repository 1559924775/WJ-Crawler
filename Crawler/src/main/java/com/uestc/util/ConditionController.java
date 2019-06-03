package com.uestc.util;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author :王俊
 *控制多个爬虫协调进行，以免爬取频繁被封ip
 */
@Data
@Component
public class ConditionController {
    private ReentrantLock lock=new ReentrantLock();
    private Condition cda=lock.newCondition();
    private Condition cdb=lock.newCondition();
}
