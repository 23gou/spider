package com.spider.dao;

import com.common.jdbc.page.Pagination;
import com.spider.entity.Task;

/**
 * 
 * 
 * 描述:任务
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月8日 下午6:21:20
 */
public interface TaskDao {
	/**
	 * 
	 * 描述:插入新的
	 * 
	 * @param star
	 * @author liyixing 2015年9月9日 上午10:16:13
	 */
	public void insert(Task task);

	/**
	 * 
	 * 描述:修改
	 * 
	 * @param star
	 * @author liyixing 2015年9月9日 下午4:15:35
	 */
	public void update(Task task);

	/**
	 * 
	 * 描述:分页查询
	 * 
	 * @param star
	 * @param orderBy
	 * @return
	 * @author liyixing 2015年9月9日 下午5:20:36
	 */
	public Pagination selectWithPage(Task task, Integer pageNo, Integer pageSize, String orderBy);
}
