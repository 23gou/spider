package com.spider.manager.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.common.jdbc.page.Pagination;
import com.spider.dao.StarDao;
import com.spider.entity.RobotResult;
import com.spider.entity.Star;
import com.spider.manager.StarMng;

/**
 * 
 * 
 * 描述:明星
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月8日 下午6:43:43
 */
@Service
@Transactional
public class StarMngImpl implements StarMng {
	@Autowired
	private StarDao starDao;

	@Override
	public void add(Star star) {
		starDao.insert(star);
	}

	@Override
	public void delete(Long id) {
		starDao.delete(id);
	}

	@Override
	public void save(Star star) {
		starDao.update(star);
	}

	@Override
	public Pagination find(Star star, Integer pageNo) {
		return starDao.selectWithPage(star, pageNo, " id desc");
	}

	@Override
	public List<Star> getNotRobot(RobotResult robotResult) {
		return starDao.selectNotRobot(robotResult);
	}

	@Override
	public Star getById(Long id) {
		return starDao.selectById(id);
	}

	@Override
	public Star getByNameAndCategory(Star star) {
		return starDao.getByNameAndCategory(star);
	}

	@Override
	public List<Star> getList(List<String> categoryIds) {
		return starDao.getList(categoryIds);
	}

	@Override
	public List<Star> selectNotBaiduIndexRobot(RobotResult robotResult) {
		return starDao.selectNotBaiduIndexRobot(robotResult);
	}
}
