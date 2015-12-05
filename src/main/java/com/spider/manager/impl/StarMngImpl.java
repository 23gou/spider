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
	public List<Star> getAll() {
		return starDao.getAll();
	}

	@Override
	public List<Star> selectNotBaiduIndexRobot(RobotResult robotResult) {
		return starDao.selectNotBaiduIndexRobot(robotResult);
	}

	// @Override
	// public int importExcel(String fileName) {
	// initWorkbook(fileName);
	// readAll();
	// return 0;
	// }
	//
	// /**
	// * 描述:把excel所有的数据转化成对象
	// *
	// * @author liyixing 2015年9月9日 上午10:44:05
	// */
	// private void readAll() {
	// List<Star> stars = new ArrayList<Star>();
	//
	// while (UtilJxl.next()) {
	// //姓名
	// Star star = new Star();
	//
	// star.setName(UtilJxl.readCell("姓名"));
	// star.setWeiboUrl(UtilJxl.readCell("微博地址"));
	// star.setTiebaUrl(UtilJxl.readCell("贴吧地址"));
	// star.set(UtilJxl.readCell("分类"));
	// }
	// }
	//
	// /**
	// * 描述:初始化工作簿
	// *
	// * @param fileName
	// * @author liyixing 2015年9月9日 上午10:30:38
	// */
	// private void initWorkbook(String fileName) {
	// try {
	// InputStream inputStream = new FileInputStream(fileName);
	// UtilJxl.initWorkBook(inputStream);
	// } catch (Exception e) {
	// throw new RuntimeException(e);
	// }
	// }
}
