package com.spider.manager;

import java.util.List;

import com.spider.entity.Category;

/**
 * 
 * 
 * 描述:分类
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月8日 下午6:29:51
 */
public interface CategoryMng {
	/**
	 * 
	 * 描述:添加
	 * 
	 * @param category
	 * @author liyixing 2015年9月8日 下午6:30:12
	 */
	public void add(Category category);

	/**
	 * 
	 * 描述:获取所有
	 * 
	 * @return
	 * @author liyixing 2015年9月8日 下午6:30:54
	 */
	public List<Category> getAll();

	/**
	 * 
	 * 描述:分类名查询
	 * 
	 * @return
	 * @author liyixing 2015年9月9日 上午9:53:58
	 */
	public Category findByName(String name);
}
