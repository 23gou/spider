package com.spider.entity;

import javax.persistence.Entity;

import com.common.util.DateUtils;
import com.spider.entity.base.BaseTask;

/**
 * 
 * 
 * 描述:任务
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月8日 下午6:19:57
 */
@Entity
public class Task extends BaseTask {

	/**
	 * 描述：
	 */

	private static final long serialVersionUID = 1L;

	public Task() {

	}

	public String getName() {
		// 默认任务名称
		setName(DateUtils.formatDate(getStartDateTime(),
				DateUtils.YYYY_MM_DD_HH_MM_SS)
				+ "["
				+ getTaskStatus()
				+ "]"
				+ "[" + getType() + "]" + "[" + getId() + "]");

		return super.getName();
	}

	public enum TaskStatus {
		未完成, 完成
	}

	public enum TaskType {
		抓取, 导入
	}
}
