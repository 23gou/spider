package com.spider.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

import com.common.jdbc.JdbcTemplateBaseDao;
import com.common.jdbc.SqlBuilder;
import com.common.jdbc.page.Pagination;
import com.spider.dao.RobotResultDao;
import com.spider.entity.RobotResult;
import com.spider.entity.Star;

/**
 * 
 * 
 * 描述:抓取结果
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月8日 下午6:24:43
 */
@Repository
public class RobotResultDaoImpl extends JdbcTemplateBaseDao implements
		RobotResultDao {
	@Override
	protected Class<?> getEntityClass() {
		return RobotResult.class;
	}

	@Override
	public void updateScore(RobotResult robotResult, String... fieldNames) {
		SqlBuilder sqlBuilder = new SqlBuilder(
				"UPDATE robotresult SET score =  ");
		boolean first = true;

		for (String fieldName : fieldNames) {
			if (!first) {
				sqlBuilder.append(" + ");
			}

			sqlBuilder.append(fieldName);

			first = false;
		}
		super.update(sqlBuilder);
		RobotResult result = super.queryForObject(robotResult.getId());
		try {
			BeanUtils.copyProperties(result, robotResult);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void updateRank(RobotResult robotResult, String fieldName,
			String rankFieldName, boolean asc, boolean zero) {
		SqlBuilder sqlBuilder = new SqlBuilder("UPDATE robotresult "
				+ "INNER JOIN  (" + "SELECT robotresult.id,robotresult."
				+ fieldName + ",@rank:=@rank+1 as rank FROM   "
				+ "( SELECT id," + fieldName
				+ " FROM robotresult WHERE robotresult.taskId = "
				+ robotResult.getTaskId() + " AND robotresult.categoryId = "
				+ robotResult.getCategoryId()
				+ (zero ? "" : " AND robotresult." + fieldName + ">0 ")
				+ " ORDER BY " + fieldName + "  " + (asc ? "ASC" : "DESC")
				+ ") robotresult ,(SELECT @rank:=0) rk " + ") rank "
				+ "ON rank.id = robotresult.id " + "SET robotresult."
				+ rankFieldName + "=rank.rank ");
		super.update(sqlBuilder);
		RobotResult result = super.queryForObject(robotResult.getId());
		try {
			BeanUtils.copyProperties(result, robotResult);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void insert(RobotResult robotResult) {
		robotResult.setId(super.add(robotResult));
	}

	@Override
	public RobotResult selectByTaskAndStar(RobotResult robotResult) {
		SqlBuilder sqlBuilder = new SqlBuilder(
				"select * from  RobotResult where 1=1");

		sqlBuilder.andEqualTo("taskId", robotResult.getTaskId());
		sqlBuilder.andEqualTo("starId", robotResult.getStarId());

		return queryForObject(sqlBuilder);
	}

	@Override
	public void udpate(RobotResult robotResult) {
		SqlBuilder sqlBuilder = new SqlBuilder(
				"update RobotResult set gmtModify=current_timestamp()");
		if (StringUtils.isNotBlank(robotResult.getResultStatus())) {
			sqlBuilder.set("resultStatus", robotResult.getResultStatus());
		}

		if (StringUtils.isNotBlank(robotResult.getEditStatus())) {
			sqlBuilder.set("editStatus", robotResult.getEditStatus());
		}

		if (robotResult.getBaiduIndex() != null) {
			sqlBuilder.set("baiduIndex", robotResult.getBaiduIndex());
		}
		if (robotResult.getBaiduIndexImg() != null) {
			sqlBuilder.set("baiduIndexImg", robotResult.getBaiduIndexImg());
		}
		super.update(robotResult.getId(), sqlBuilder);
	}

	@Override
	public Pagination selectWithPage(RobotResult robotResult, Star star,
			int pageNo, int pageSize, String orderBy) {
		SqlBuilder sqlBuilder = new SqlBuilder(
				"select RobotResult.* from  RobotResult "
						+ "left join Star on Star.id = RobotResult.starId "
						+ "where 1=1");

		if (robotResult.getTaskId() != null) {
			sqlBuilder
					.andEqualTo("RobotResult.taskId", robotResult.getTaskId());
		}

		if (robotResult.getCategoryId() != null) {
			sqlBuilder.andEqualTo("RobotResult.categoryId",
					robotResult.getCategoryId());
		}

		if (robotResult.getEditStatus() != null) {
			sqlBuilder.andEqualTo("RobotResult.editStatus",
					robotResult.getEditStatus());
		}

		if (star != null) {
			if (StringUtils.isNotBlank(star.getName())) {
				sqlBuilder.andLike("Star.name", "%" + star.getName() + "%");
			}
		}

		if (StringUtils.isNotBlank(orderBy)) {
			sqlBuilder.append(" ORDER BY " + orderBy);
		}

		return super.getPage(sqlBuilder, pageNo, pageSize);
	}

	@Override
	public RobotResult selectById(Long id) {
		return super.queryForObject(id);
	}

	@Override
	public void delete(Long id) {
		super.delete(id);
	}

	// @Override
	// public void deleteNotSuccess() {
	// SqlBuilder sqlBuilder = new SqlBuilder(
	// "delete from  RobotResult where 1=1");
	//
	// sqlBuilder.andNotEqualTo("ResultStatus", ResultStatus.完成.toString());
	//
	// super.delete(sqlBuilder);
	// }
}
