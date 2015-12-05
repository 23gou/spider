package com.spider.entity.base;

import java.util.Date;

import com.common.jdbc.BaseEntity;

/**
 * 
 * 
 * 描述:任务
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月8日 下午6:17:13
 */
@javax.persistence.MappedSuperclass
public class BaseTask extends BaseEntity {
	private static final long serialVersionUID = -7948930413157445187L;

	public BaseTask() {
	}

	/**
	 * 任务名称
	 */
	private String name;
	/**
	 * 开始时间
	 */
	private Date startDateTime;

	/**
	 * 用来做对比的任务
	 */
	private Long contrastTaskId;

	/**
	 * 任务状态
	 */
	private String taskStatus;
	/**
	 * 类型
	 */
	private String type;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(Date startDateTime) {
		this.startDateTime = startDateTime;
	}

	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}

	public Long getContrastTaskId() {
		return contrastTaskId;
	}

	public void setContrastTaskId(Long contrastTaskId) {
		this.contrastTaskId = contrastTaskId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
