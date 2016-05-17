package com.spider.entity.base;

import com.common.jdbc.BaseEntity;

/**
 * 
 * 
 * 描述:明星
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月8日 下午6:17:13
 */
@javax.persistence.MappedSuperclass
public class BaseStar extends BaseEntity {
	private static final long serialVersionUID = -7948930413157445187L;

	public BaseStar() {
	}

	/**
	 * 明星姓名
	 */
	private String name;
	/**
	 * 贴吧名称
	 */
	private String tiebaName;
	/**
	 * 贴吧url
	 */
	private String tiebaUrl;
	/**
	 * 微博名
	 */
	private String weiboName;
	/**
	 * 微博地址
	 */
	private String weiboUrl;
	/**
	 * 分类ID
	 */
	private Long categoryId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTiebaName() {
		return tiebaName;
	}

	public void setTiebaName(String tiebaName) {
		this.tiebaName = tiebaName;
	}

	public String getTiebaUrl() {
		if(tiebaUrl == null) {
			tiebaUrl = "";
		}
		return tiebaUrl;
	}

	public void setTiebaUrl(String tiebaUrl) {
		this.tiebaUrl = tiebaUrl;
	}

	public String getWeiboName() {
		return weiboName;
	}

	public void setWeiboName(String weiboName) {
		this.weiboName = weiboName;
	}

	public String getWeiboUrl() {
		if(weiboUrl == null) {
			weiboUrl = "";
		}
		
		return weiboUrl;
	}

	public void setWeiboUrl(String weiboUrl) {
		this.weiboUrl = weiboUrl;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
}
