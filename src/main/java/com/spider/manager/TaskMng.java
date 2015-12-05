package com.spider.manager;

import com.common.jdbc.page.Pagination;
import com.spider.entity.Task;

/**
 * 
 * 
 * 描述:任务
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月8日 下午6:29:51
 */
public interface TaskMng {
	/**
	 * 
	 * 描述:导入
	 * 
	 * @param fileName
	 * @return
	 * @author liyixing 2015年9月9日 上午10:19:36
	 */
	// public int importExcel(String fileName);
	/**
	 * 
	 * 描述:添加
	 * 
	 * @param star
	 * @author liyixing 2015年9月9日 下午3:23:40
	 */
	public void add(Task task);

	/**
	 * 
	 * 描述:删除
	 * 
	 * @param id
	 * @author liyixing 2015年9月9日 下午4:13:44
	 */
	public void delete(Long id);

	/**
	 * 
	 * 描述:修改
	 * 
	 * @param star
	 * @author liyixing 2015年9月9日 下午4:14:13
	 */
	public void save(Task task);

	/**
	 * 
	 * 描述:分页查询
	 * 
	 * @param star
	 * @return
	 * @author liyixing 2015年9月9日 下午5:57:11
	 */
	public Pagination find(Task task, Integer pageNo, Integer pageSize);
}
