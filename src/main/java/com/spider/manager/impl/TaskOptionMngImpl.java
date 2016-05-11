package com.spider.manager.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spider.dao.TaskOptionDao;
import com.spider.entity.TaskOption;
import com.spider.entity.TaskOption.TaskOptionStatus;
import com.spider.manager.TaskOptionMng;

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
public class TaskOptionMngImpl implements TaskOptionMng {
	@Autowired
	private TaskOptionDao dao;

	@Override
	public TaskOption add(Long taskId, String name, String value) {
		TaskOption taskOption = new TaskOption();

		taskOption.setName(name);
		taskOption.setTaskId(taskId);
		taskOption.setValue(value);
		taskOption.setStatus(TaskOptionStatus.未完成.toString());
		taskOption.setId(dao.insert(taskOption));

		return taskOption;
	}

	@Override
	public void clean(Long taskId) {
		dao.clean(taskId);
	}

	@Override
	public List<TaskOption> getByTaskAndName(Long taskId, String name,
			String status) {
		return dao.getByTaskAndName(taskId, name, status);
	}

	@Override
	public Map<String, List<String>> getMapByTaskAndName(Long taskId,
			String name, String status) {
		List<TaskOption> options = getByTaskAndName(taskId, name, status);
		Map<String, List<String>> result = new HashMap<String, List<String>>();

		for (TaskOption taskOption : options) {
			List<String> os = result.get(taskOption.getName());

			if (os == null) {
				os = new ArrayList<String>();
				result.put(taskOption.getName(), os);
			}

			os.add(taskOption.getValue());
		}

		return result;
	}

	@Override
	public void update(Long id, String status, String value) {
		dao.update(id, status, value);
	}
}
