package com.uestc.service.impl;


import com.uestc.dao.TbDouban1Mapper;
import com.uestc.dao.TbDouban2Mapper;
import com.uestc.dao.TbZongheng1Mapper;
import com.uestc.dao.TbZongheng2Mapper;
import com.uestc.domain.TbDouban1;
import com.uestc.domain.TbDouban2;
import com.uestc.domain.TbZongheng1;
import com.uestc.domain.TbZongheng2;
import com.uestc.entity.DoubanPage;
import com.uestc.entity.Page;
import com.uestc.entity.ZonghengPage;
import com.uestc.service.IStoreService;
import com.uestc.util.IdWorker;
import com.uestc.util.LoadPropertyUtil;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.api.Propagation;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.apache.solr.client.solrj.impl.XMLResponseParser.log;

/**
 * 数据存储实现类，存入数据库
 * @author 王俊
 *
 */
//把解析的数据输出到控制台
//@Scope("prototype")
@Service
public class MysqlStoreService implements IStoreService {
	@Autowired
	TbDouban1Service tbDouban1Service;
	@Autowired
	TbDouban2Service tbDouban2Service;
	@Autowired
	TbZongheng1Service tbZongheng1Service;
	@Autowired
	TbZongheng2Service tbZongheng2Service;
	IdWorker idWorker;
	{
		int workerId=Integer.parseInt(LoadPropertyUtil.getWorker("workerId"));
		int datacenterId=Integer.parseInt(LoadPropertyUtil.getWorker("datacenterId"));
		idWorker=new IdWorker(workerId,datacenterId);
	}
	public void confirmStore(Page page1) {
		log.info("comfirmStore");
	}
	public void cancelStore(Page page1) {
		log.info("cancelStore");
	}
	@Compensable(propagation = Propagation.REQUIRES_NEW,confirmMethod = "confirmStore", cancelMethod = "cancelStore", asyncConfirm = true)
	public void store( Page page1) {

		if(page1 instanceof DoubanPage){
			DoubanPage page=(DoubanPage)page1;
			// TODO Auto-generated method stub
			//存入数据库  需要对应的dao和xml
			String id=idWorker.nextId()+"";//由雪花算法得到
			TbDouban1 tbDouban1=new TbDouban1();
			tbDouban1.setArea(page.getArea());
			tbDouban1.setDirector(page.getDirector());
			tbDouban1.setDuration(page.getDuration());
			tbDouban1.setId(id);
			tbDouban1.setName(page.getMoveName());
			tbDouban1.setLanguage(page.getLanguage());
			tbDouban1.setTime(page.getTime());
			tbDouban1.setType(page.getType());
			tbDouban1.setUrl(page.getUrl());

			id=idWorker.nextId()+"";//两个id是不同的  TbDouban2的fid和TbDouban1的id相同
			TbDouban2 tbDouban2=new TbDouban2();
			tbDouban2.setCommentnumber(page.getCommentnumber());
			//Date不能为空
			if(page.getDate()!=null)
				tbDouban2.setDate(page.getDate().toLocaleString());
			tbDouban2.setId(id);
			tbDouban2.setScore(page.getScore());

			//add成功了会调用confirmAdd ,两个add都成功了，store就成功了，就会调用confirmStore方法。
			TbDouban1 one=tbDouban1Service.getOne(tbDouban1);
			if(one==null){
				//分布式事务
				tbDouban2.setFid(tbDouban1.getId());
				tbDouban1Service.add(tbDouban1);
				tbDouban2Service.add(tbDouban2);
			}else{
				tbDouban2.setFid(one.getId());
				tbDouban2Service.add(tbDouban2);
			}
		}else if(page1 instanceof ZonghengPage){
			String id=idWorker.nextId()+"";//由雪花算法得到
			ZonghengPage page=(ZonghengPage)page1;

			TbZongheng1 tbZongheng1=new TbZongheng1();
			tbZongheng1.setId(id);
			tbZongheng1.setName(page.getName());
			tbZongheng1.setNumber(page.getNumber());
			tbZongheng1.setType(page.getType());
			tbZongheng1.setUrl(page.getUrl());

			id=idWorker.nextId()+"";//两个id是不同的
			TbZongheng2 tbZongheng2=new TbZongheng2();
			tbZongheng2.setClick(page.getClick());
			if(page.getDate()!=null)
				tbZongheng2.setDate(page.getDate().toLocaleString());
			tbZongheng2.setRecommend(page.getRecommend());
			tbZongheng2.setId(id);

			//若果tbZongheng1已经有了，就不插入主要信息表-TbZongheng1了
			TbZongheng1 one=tbZongheng1Service.getOne(tbZongheng1);
			if(one==null){
				//分布式事务
				tbZongheng2.setFid(tbZongheng1.getId());
				tbZongheng1Service.add(tbZongheng1);
				tbZongheng2Service.add(tbZongheng2);
			}else{
				tbZongheng2.setFid(one.getId());
				tbZongheng2Service.add(tbZongheng2);
			}
		}
	}

}
