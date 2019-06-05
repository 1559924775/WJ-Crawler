package com.uestc.service;

import com.uestc.domain.TbZongheng1;
import com.uestc.domain.TbZongheng2;
import org.mengyun.tcctransaction.api.Compensable;

public interface ITbZongheng1Service {
    @Compensable
    void add(TbZongheng1 tbZongheng1);
}
