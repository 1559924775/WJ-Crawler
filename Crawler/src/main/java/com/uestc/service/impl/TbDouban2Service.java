package com.uestc.service.impl;

import com.uestc.dao.TbDouban2Mapper;
import com.uestc.domain.TbDouban2;
import com.uestc.domain.TbDouban2Example;
import com.uestc.service.ITbDouban2Service;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.api.Propagation;
import org.mengyun.tcctransaction.context.MethodTransactionContextEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TbDouban2Service implements ITbDouban2Service {
    @Autowired
    TbDouban2Mapper tbDouban2Mapper;
    @Override
    @Compensable(propagation = Propagation.REQUIRES_NEW,confirmMethod = "confirmAdd", cancelMethod = "cancelAdd", transactionContextEditor = MethodTransactionContextEditor.class)
    public void add(TbDouban2 tbDouban2) {
        tbDouban2.setStatus("inserting");
        tbDouban2Mapper.insert(tbDouban2);

    }
    public void confirmAdd(TbDouban2 tbDouban2){
        //更新draft状态为空，表示正真的插入。
        if(tbDouban2!=null&&tbDouban2.getStatus().equals("inserting")){
            TbDouban2Example example=new TbDouban2Example();
            tbDouban2.setStatus("inserted");
            TbDouban2Example.Criteria criteria=example.createCriteria();
            criteria.andIdEqualTo(tbDouban2.getId());
            tbDouban2Mapper.updateByExampleSelective(tbDouban2,example);

        }
    }
    public void cancelAdd(TbDouban2 tbDouban2){
        //删除掉draft的那行
        if(tbDouban2!=null&&tbDouban2.getStatus().equals("inserting")){
            TbDouban2Example example=new TbDouban2Example();
            TbDouban2Example.Criteria criteria=example.createCriteria();
            criteria.andIdEqualTo(tbDouban2.getId());
            tbDouban2Mapper.deleteByExample(example);
        }

    }


}
