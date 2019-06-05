package com.uestc.service;


import com.uestc.entity.DoubanPage;
import com.uestc.entity.Page;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.api.TransactionContext;

/**
 * 数据存储接口
 * @author 王俊
 *
 */

public interface IStoreService {
	@Compensable
	public void store(Page page);
//	@Compensable
//	public void test(DoubanPage page);
}
