package com.uestc.service.impl;

import com.uestc.dao.TbDouban1Mapper;
import com.uestc.domain.TbDouban1;
import com.uestc.domain.TbDouban1Example;
import com.uestc.service.ITbDouban1Service;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.api.Propagation;
import org.mengyun.tcctransaction.context.MethodTransactionContextEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TbDouban1Service implements ITbDouban1Service {
    @Autowired
    TbDouban1Mapper tbDouban1Mapper;
    @Override
    @Compensable(propagation = Propagation.REQUIRES_NEW,confirmMethod = "confirmAdd", cancelMethod = "cancelAdd", transactionContextEditor =  MethodTransactionContextEditor.class)
    public void add(TbDouban1 tbDouban1) {
        //加一个draft字段。confirm去掉该字段，cancel是删除该条数据。
        tbDouban1.setStatus("inserting");
        tbDouban1Mapper.insert(tbDouban1);
    }
    public void confirmAdd(TbDouban1 tbDouban1){
        //更新draft状态为空，表示正真的插入。
        if(tbDouban1!=null&&tbDouban1.getStatus().equals("inserting")){
            TbDouban1Example example=new TbDouban1Example();
            tbDouban1.setStatus("inserted");
            TbDouban1Example.Criteria criteria=example.createCriteria();
            criteria.andIdEqualTo(tbDouban1.getId());
            tbDouban1Mapper.updateByExampleSelective(tbDouban1,example);

        }

    }
    public void cancelAdd(TbDouban1 tbDouban1){
        //删除掉draft的那行
        if(tbDouban1!=null&&tbDouban1.getStatus().equals("inserting")){
            TbDouban1Example example=new TbDouban1Example();
            TbDouban1Example.Criteria criteria=example.createCriteria();
            criteria.andIdEqualTo(tbDouban1.getId());
            tbDouban1Mapper.deleteByExample(example);
        }

    }
    public TbDouban1 getOne(TbDouban1 tbDouban1){
        TbDouban1Example example=new TbDouban1Example();
        TbDouban1Example.Criteria criteria=example.createCriteria();
        criteria.andNameEqualTo(tbDouban1.getName());
        criteria.andUrlEqualTo(tbDouban1.getUrl());
        List<TbDouban1> list=tbDouban1Mapper.selectByExample(example);
        if(list==null||list.size()==0)return null;
        else return list.get(0);
    }

}
