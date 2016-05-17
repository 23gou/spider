package com.spider.entity;

import javax.persistence.Entity;

import com.spider.entity.base.BaseRobotResult;

/**
 * 
 * 
 * 描述:抓取结果
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月8日 下午6:19:57
 */
@Entity
public class RobotResult extends BaseRobotResult {

	/**
	 * 描述：
	 */

	private static final long serialVersionUID = 1L;

	public RobotResult() {
	}

	public enum ResultStatus {
		创建, 完成
	}

	public enum EditStatus {
		未编辑, 已编辑
	}
}
