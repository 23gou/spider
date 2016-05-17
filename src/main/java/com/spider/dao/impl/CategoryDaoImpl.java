package com.spider.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.common.jdbc.JdbcTemplateBaseDao;
import com.common.jdbc.SqlBuilder;
import com.spider.dao.CategoryDao;
import com.spider.entity.Category;

/**
 * 
 * 
 * 描述:分类
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月8日 下午6:24:43
 */
@Repository
public class CategoryDaoImpl extends JdbcTemplateBaseDao implements CategoryDao {

	@Override
	public long insert(Category category) {
		category.setId(add(category));
		
		return category.getId();
	}

	@Override
	public List<Category> selectAll() {
		SqlBuilder sqlBuilder = new SqlBuilder("select * from Category where 1=1");
		sqlBuilder.append(" order by id ");
		
		return query(sqlBuilder);
	}

	@Override
	protected Class<?> getEntityClass() {
		return Category.class;
	}

	@Override
	public Category selectByName(String name) {
		SqlBuilder sqlBuilder = new SqlBuilder("select * from Category where name = ?");
		sqlBuilder.setParams(name);
		sqlBuilder.append(" order by id ");
		
		return queryForObject(sqlBuilder);
	}
}
