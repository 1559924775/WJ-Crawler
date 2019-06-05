package com.uestc.service;

import com.uestc.domain.TbDouban1;
import com.uestc.domain.TbDouban2;
import org.mengyun.tcctransaction.api.Compensable;

public interface ITbDouban1Service {
    @Compensable
    void add(TbDouban1 tbDouban1);
}
