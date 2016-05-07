package com.spider.dao.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.common.jdbc.JdbcTemplateBaseDao;
import com.common.jdbc.SqlBuilder;
import com.common.jdbc.page.Pagination;
import com.spider.dao.StarDao;
import com.spider.entity.RobotResult;
import com.spider.entity.Star;

/**
 * 
 * 
 * 描述:明星
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月8日 下午6:24:43
 */
@Repository
public class StarDaoImpl extends JdbcTemplateBaseDao implements StarDao {
	@Override
	protected Class<?> getEntityClass() {
		return Star.class;
	}

	@Override
	public void insert(Star star) {
		star.setId(add(star));

		return;
	}

	@Override
	public void delete(Long id) {
		super.delete(id);

		return;
	}

	@Override
	public void update(Star star) {
		SqlBuilder sqlBuilder = new SqlBuilder(
				"update Star set gmtModify=current_timestamp()");
		if (StringUtils.isNotBlank(star.getName())) {
			sqlBuilder.set("name", star.getName());
		}
		if (star.getWeiboUrl() != null) {
			sqlBuilder.set("weiboUrl", star.getWeiboUrl());
		}
		if (star.getWeiboUrl() != null) {
			sqlBuilder.set("tiebaUrl", star.getTiebaUrl());
		}
		if (StringUtils.isNotBlank(star.getTiebaName())) {
			sqlBuilder.set("tiebaName", star.getTiebaName());
		}
		if (StringUtils.isNotBlank(star.getWeiboName())) {
			sqlBuilder.set("weiboName", star.getWeiboName());
		}
		super.update(star.getId(), sqlBuilder);
	}

	@Override
	public Pagination selectWithPage(Star star, Integer pageNo, String orderBy) {
		SqlBuilder sqlBuilder = new SqlBuilder("select * from  Star where 1=1");

		if (StringUtils.isNotBlank(star.getName())) {
			sqlBuilder.andLike("name", "%" + star.getName() + "%");
		}

		if (star.getCategoryId() != null) {
			sqlBuilder.andEqualTo("categoryId", star.getCategoryId());
		}

		if (sqlBuilder.ifNotNull(orderBy)) {
			sqlBuilder.append(" order by " + orderBy);
		} else {
			sqlBuilder.append(" order by  id desc");
		}
		return getPage(sqlBuilder, pageNo == null ? 1 : pageNo, 14);
	}

	@Override
	public List<Star> selectNotRobot(RobotResult robotResult) {
		SqlBuilder sqlBuilder = new SqlBuilder("SELECT star.* FROM star "
				+ "LEFT JOIN robotresult "
				+ "ON robotresult.starId = star.id AND robotresult.taskId = "
				+ robotResult.getTaskId() + " "
				+ "WHERE robotresult.id IS NULL");

		sqlBuilder.append(" order by  star.id desc");

		return query(sqlBuilder);
	}

	@Override
	public Star selectById(Long id) {
		return super.queryForObject(id);
	}

	@Override
	public Star getByNameAndCategory(Star star) {
		SqlBuilder sqlBuilder = new SqlBuilder("select * from  Star where 1=1");

		if (StringUtils.isNotBlank(star.getName())) {
			sqlBuilder.andEqualTo("name", star.getName());
		}

		if (star.getCategoryId() != null) {
			sqlBuilder.andEqualTo("categoryId", star.getCategoryId());
		}

		sqlBuilder.append(" order by  id asc");
		return queryForObject(sqlBuilder);
	}

	@Override
	public List<Star> getList(List<String> categoryIds) {
		SqlBuilder sqlBuilder = new SqlBuilder("select * from  Star where 1=1");

		if(CollectionUtils.isNotEmpty(categoryIds)) {
			sqlBuilder.append(" and categoryId in (" + categoryIds.toString().replace("[", "").replace("]", "") + ")");
		}
		
		sqlBuilder.append(" order by id desc ");
		return super.query(sqlBuilder);
	}

	@Override
	public List<Star> selectNotBaiduIndexRobot(RobotResult robotResult) {
		SqlBuilder sqlBuilder = new SqlBuilder("SELECT star.* FROM star "
				+ "INNER JOIN robotresult "
				+ "ON robotresult.starId = star.id AND robotresult.taskId = "
				+ robotResult.getTaskId() + " "
				+ "WHERE robotresult.baiduIndexImg IS NULL");

		sqlBuilder.append(" order by  star.id desc");

		return query(sqlBuilder);
	}
}
