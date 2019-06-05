package com.uestc.service.impl;


import com.uestc.entity.DoubanPage;
import com.uestc.entity.Page;
import com.uestc.entity.ZonghengPage;
import com.uestc.service.IStoreService;

/**
 * 数据存储实现类Console
 * @author 王俊
 * @Test 用于测试
 */
//把解析的数据输出到控制台
//@Scope("prototype")
//@Service
public class ConsoleStoreService implements IStoreService {

	public void store(Page page1) {
		if(page1 instanceof DoubanPage){
			DoubanPage page=(DoubanPage)page1;
			// TODO Auto-generated method stub
			System.out.println("-------------------------MOVE-------------------------------------");
			System.out.println("电影名:+++++++++++++++++++"+page.getMoveName());
			System.out.println("评分:"+page.getScore());
			System.out.println("评论人数:"+page.getCommentnumber());
			System.out.println("类型:"+page.getType());
		}else if(page1 instanceof ZonghengPage){
			ZonghengPage page=(ZonghengPage)page1;
			System.out.println("-------------------------BOOK-------------------------------------");
			System.out.println("书名:-----------------------"+page.getName());
			System.out.println("作者:"+page.getAuthor());
			System.out.println("字数:"+page.getNumber());
			System.out.println("总推荐:"+page.getRecommend());
			System.out.println("总点击:"+page.getClick());
			System.out.println("周推荐:"+page.getRecommend_week());
		}
	}

}
