package com.spider.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.common.jdbc.page.Pagination;
import com.spider.dao.TaskDao;
import com.spider.entity.Task;
import com.spider.manager.TaskMng;

/**
 * 
 * 
 * 描述:明星
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月8日 下午6:43:43
 */
@Service
@Transactional
public class TaskMngImpl implements TaskMng {
	@Autowired
	private TaskDao taskDao;

	@Override
	public void add(Task task) {
		taskDao.insert(task);
	}

	@Override
	public void delete(Long id) {

	}

	@Override
	public void save(Task task) {
		taskDao.update(task);
	}

	@Override
	public Pagination find(Task task, Integer pageNo, Integer pageSize) {
		return taskDao.selectWithPage(task, pageNo, pageSize, " id desc");
	}
}
