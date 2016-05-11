package com.spider.dao;

import java.util.List;

import com.spider.entity.TaskOption;

/**
 * 
 * 
 * 描述:任务选项
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月8日 下午6:21:20
 */
public interface TaskOptionDao {
	/**
	 * 
	 * 描述:插入新的
	 * 
	 * @param star
	 * @author liyixing 2015年9月9日 上午10:16:13
	 */
	public long insert(TaskOption taskOption);

	/**
	 * 
	 * 描述:清理任务选项
	 * 
	 * @param star
	 * @author liyixing 2015年9月9日 下午4:15:35
	 */
	public void clean(Long taskId);

	/**
	 * 
	 * 描述:任务，名称搜索
	 * 
	 * @param taskId
	 * @param name
	 * @return
	 * @author liyixing 2016年5月5日 下午4:25:45
	 */
	public List<TaskOption> getByTaskAndName(Long taskId, String name,String status);
	
	public void update(Long id, String status,String value);
}
