package com.spider.manager.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spider.dao.CategoryDao;
import com.spider.entity.Category;
import com.spider.manager.CategoryMng;

/**
 * 
 * 
 * 描述:分类管理
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月8日 下午6:43:43
 */
@Service
@Transactional
public class CategoryMngImpl implements CategoryMng {
	@Autowired
	private CategoryDao categoryDao;

	@Override
	public void add(Category category) {
		categoryDao.insert(category);
	}

	@Override
	public List<Category> getAll() {
		return categoryDao.selectAll();
	}

	@Override
	public Category findByName(String name) {
		return categoryDao.selectByName(name);
	}

}
