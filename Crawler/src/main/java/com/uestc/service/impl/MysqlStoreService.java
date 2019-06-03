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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * 数据存储实现类，存入数据库
 * @author 王俊
 *
 */
//把解析的数据输出到控制台
@Scope("prototype")
@Service
public class MysqlStoreService implements IStoreService {
	@Autowired
	TbDouban1Mapper tbDouban1Mapper;
	@Autowired
	TbDouban2Mapper tbDouban2Mapper;
	@Autowired
	TbZongheng1Mapper tbZongheng1Mapper;
	@Autowired
	TbZongheng2Mapper tbZongheng2Mapper;
	IdWorker idWorker;
	{
		int workerId=Integer.parseInt(LoadPropertyUtil.getWorker("workerId"));
		int datacenterId=Integer.parseInt(LoadPropertyUtil.getWorker("datacenterId"));
		idWorker=new IdWorker(workerId,datacenterId);
	}
	public void store(Page page1) {
		String id=idWorker.nextId()+"";//由雪花算法得到
		if(page1 instanceof DoubanPage){
			DoubanPage page=(DoubanPage)page1;
			// TODO Auto-generated method stub
			//存入数据库  需要对应的dao和xml
			System.out.println("存入数据库douban");
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

			TbDouban2 tbDouban2=new TbDouban2();
			tbDouban2.setCommentnumber(page.getCommentnumber());
			//Date不能为空
			tbDouban2.setDate(page.getDate().toLocaleString());
			tbDouban2.setId(id);
			tbDouban2.setScore(page.getScore());
			//引入分布式事务
			tbDouban1Mapper.insert(tbDouban1);
			tbDouban2Mapper.insert(tbDouban2);

		}else if(page1 instanceof ZonghengPage){
			ZonghengPage page=(ZonghengPage)page1;
			System.out.println("存入数据库zongheng");
			TbZongheng1 tbZongheng1=new TbZongheng1();
			tbZongheng1.setId(id);
			tbZongheng1.setName(page.getName());
			tbZongheng1.setNumber(page.getNumber());
			tbZongheng1.setType(page.getType());
			tbZongheng1.setUrl(page.getUrl());

			TbZongheng2 tbZongheng2=new TbZongheng2();
			tbZongheng2.setClick(page.getClick());
			tbZongheng2.setDate(page.getDate().toLocaleString());
			tbZongheng2.setRecommend(page.getRecommend());
			tbZongheng2.setId(id);

			tbZongheng1Mapper.insert(tbZongheng1);
			tbZongheng2Mapper.insert(tbZongheng2);
		}
	}

}
