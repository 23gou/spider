package com.spider.entity.base;

import com.common.jdbc.BaseEntity;

/**
 * 
 * 
 * 描述:任务选项
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月8日 下午6:17:13
 */
@javax.persistence.MappedSuperclass
public class BaseTaskOption extends BaseEntity {
	private static final long serialVersionUID = -7948930413157445187L;

	public BaseTaskOption() {
	}

	/**
	 * 任务选型名称
	 */
	private String name;
	/**
	 * 任务选型值
	 */
	private String value;
	/**
	 * 任务
	 */
	private Long taskId;
	
	/**
	 * 状态
	 */
	private String status;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
