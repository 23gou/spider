package com.spider.entity.base;

import com.common.jdbc.BaseEntity;

/**
 * 
 * 
 * 描述:分类
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月8日 下午6:17:13
 */
@javax.persistence.MappedSuperclass
public class BaseCategory extends BaseEntity {
	private static final long serialVersionUID = -7948930413157445187L;

	public BaseCategory() {
	}

	/**
	 * 分类名
	 */
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
