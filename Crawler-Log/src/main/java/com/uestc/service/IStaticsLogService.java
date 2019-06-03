package com.uestc.service;


import com.uestc.entity.Statistics;

import javax.xml.crypto.Data;

/**
 * 统计接口
 * @author 王俊
 */
public interface IStaticsLogService {
    //将统计信息写入日志
    void executeStatics(Statistics info);
}
