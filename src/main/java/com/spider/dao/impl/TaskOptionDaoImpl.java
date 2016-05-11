package com.spider.dao.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.common.jdbc.JdbcTemplateBaseDao;
import com.common.jdbc.SqlBuilder;
import com.spider.dao.TaskOptionDao;
import com.spider.entity.TaskOption;

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
public class TaskOptionDaoImpl extends JdbcTemplateBaseDao implements
		TaskOptionDao {
	@Override
	protected Class<?> getEntityClass() {
		return TaskOption.class;
	}

	@Override
	public long insert(TaskOption taskOption) {
		return super.add(taskOption);
	}

	@Override
	public void clean(Long taskId) {
		super.delete(new SqlBuilder("delete from TaskOption where taskId = "
				+ taskId));
	}

	@Override
	public List<TaskOption> getByTaskAndName(Long taskId, String name,
			String status) {
		SqlBuilder sqlBuilder = new SqlBuilder(
				" select * from TaskOption where 1=1");

		if (taskId != null) {
			sqlBuilder.andEqualTo("taskId", taskId);
		}

		if (StringUtils.isNotBlank(name)) {
			sqlBuilder.andEqualTo("name", name);
		}

		if (StringUtils.isNotBlank(status)) {
			sqlBuilder.andEqualTo("status", status);
		}

		return super.query(sqlBuilder);
	}

	@Override
	public void update(Long id, String status,String value) {
		SqlBuilder sqlBuilder = new SqlBuilder(
				"update TaskOption set gmtModify=current_timestamp()");
		if (StringUtils.isNotBlank(status)) {
			sqlBuilder.set("status", status);
		}
		
		if (StringUtils.isNotBlank(value)) {
			sqlBuilder.set("value", value);
		}
		
		super.update(id, sqlBuilder);
	}
}
