package com.uestc.service.impl;


import com.uestc.dao.TbZongheng2Mapper;
import com.uestc.domain.TbZongheng2;
import com.uestc.domain.TbZongheng2Example;
import com.uestc.service.ITbZongheng2Service;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.api.Propagation;
import org.mengyun.tcctransaction.context.MethodTransactionContextEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TbZongheng2Service implements ITbZongheng2Service {
    @Autowired
    TbZongheng2Mapper tbZongheng2Mapper;
    @Override
    @Compensable(propagation = Propagation.REQUIRES_NEW,confirmMethod = "confirmAdd", cancelMethod = "cancelAdd", transactionContextEditor = MethodTransactionContextEditor.class)
    public void add(TbZongheng2 tbZongheng2) {
        tbZongheng2.setStatus("inserting");
        tbZongheng2Mapper.insert(tbZongheng2);
    }
    public void confirmAdd(TbZongheng2 tbZongheng2){
        //更新draft状态为空，表示正真的插入。
        if(tbZongheng2!=null&&tbZongheng2.getStatus().equals("inserting")){
            TbZongheng2Example example=new TbZongheng2Example();
            tbZongheng2.setStatus("inserted");
            TbZongheng2Example.Criteria criteria=example.createCriteria();
            criteria.andIdEqualTo(tbZongheng2.getId());
            tbZongheng2Mapper.updateByExampleSelective(tbZongheng2,example);
        }
    }
    public void cancelAdd(TbZongheng2 TbZongheng2){
        //删除掉draft的那行
        if(TbZongheng2!=null&&TbZongheng2.getStatus().equals("inserting")){
            TbZongheng2Example example=new TbZongheng2Example();
            TbZongheng2Example.Criteria criteria=example.createCriteria();
            criteria.andIdEqualTo(TbZongheng2.getId());
            tbZongheng2Mapper.deleteByExample(example);
        }
    }


}
