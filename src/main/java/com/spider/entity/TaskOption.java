package com.spider.entity;

import javax.persistence.Entity;

import com.spider.entity.base.BaseTaskOption;

/**
 * 
 * 
 * 描述:任务选项
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月8日 下午6:19:57
 */
@Entity
public class TaskOption extends BaseTaskOption {

	/**
	 * 描述：
	 */

	private static final long serialVersionUID = 1L;

	public TaskOption() {
	}

	public enum TaskOptionType {
		维度, 分类, 当前明星
	}

	public enum TaskOptionStatus {
		未完成, 已完成
	}
}
