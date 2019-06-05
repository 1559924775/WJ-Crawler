package com.uestc.service;

import com.uestc.domain.TbDouban2;
import com.uestc.domain.TbZongheng2;
import org.mengyun.tcctransaction.api.Compensable;

public interface ITbDouban2Service {
    @Compensable
    void add(TbDouban2 tbDouban2);
}
