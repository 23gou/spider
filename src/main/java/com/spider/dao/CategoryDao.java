package com.spider.dao;

import java.util.List;

import com.spider.entity.Category;

/**
 * 
 * 
 * 描述:分类
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月8日 下午6:21:20
 */
public interface CategoryDao {
	/**
	 * 
	 * 描述:添加分类
	 * 
	 * @param category
	 * @author liyixing 2015年9月8日 下午6:21:40
	 * @return
	 */
	public long insert(Category category);

	/**
	 * 
	 * 描述:获取所有
	 * 
	 * @author liyixing 2015年9月8日 下午6:22:28
	 * @return
	 */
	public List<Category> selectAll();

	/**
	 * 
	 * 描述:根据分类名查询
	 * 
	 * @return
	 * @author liyixing 2015年9月9日 上午9:40:16
	 */
	public Category selectByName(String name);
}
