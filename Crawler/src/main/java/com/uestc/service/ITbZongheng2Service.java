package com.uestc.service;


import com.uestc.domain.TbZongheng2;
import org.mengyun.tcctransaction.api.Compensable;

public interface ITbZongheng2Service {
    @Compensable
    void add(TbZongheng2 tbZongheng2);
}
