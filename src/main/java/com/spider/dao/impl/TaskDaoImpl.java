package com.spider.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.common.jdbc.JdbcTemplateBaseDao;
import com.common.jdbc.SqlBuilder;
import com.common.jdbc.page.Pagination;
import com.spider.dao.TaskDao;
import com.spider.entity.Task;

/**
 * 
 * 
 * 描述:任务
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月8日 下午6:24:43
 */
@Repository
public class TaskDaoImpl extends JdbcTemplateBaseDao implements TaskDao {
	@Override
	protected Class<?> getEntityClass() {
		return Task.class;
	}

	@Override
	public void insert(Task task) {
		task.setId(add(task));
	}

	@Override
	public void update(Task task) {
		SqlBuilder sqlBuilder = new SqlBuilder(
				"update Task set gmtModify=current_timestamp()");
		if (StringUtils.isNotBlank(task.getTaskStatus())) {
			sqlBuilder.set("taskStatus", task.getTaskStatus());
		}
		if (StringUtils.isNotBlank(task.getName())) {
			sqlBuilder.set("name", task.getName());
		}
		super.update(task.getId(), sqlBuilder);
	}

	@Override
	public Pagination selectWithPage(Task task, Integer pageNo, Integer pageSize, String orderBy) {
		SqlBuilder sqlBuilder = new SqlBuilder("select * from  Task where 1=1");

		// if (StringUtils.isNotBlank(star.getName())) {
		// sqlBuilder.andLike("name", "%" + star.getName() + "%");
		// }
		//
		// if (star.getCategoryId() != null) {
		// sqlBuilder.andEqualTo("categoryId", star.getCategoryId());
		// }

		if (sqlBuilder.ifNotNull(orderBy)) {
			sqlBuilder.append(" order by " + orderBy);
		} else {
			sqlBuilder.append(" order by  id desc");
		}
		return getPage(sqlBuilder, pageNo == null ? 1 : pageNo, pageSize);
	}
}
