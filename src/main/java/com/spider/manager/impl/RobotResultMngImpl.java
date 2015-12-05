package com.spider.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.common.jdbc.page.Pagination;
import com.spider.dao.RobotResultDao;
import com.spider.entity.RobotResult;
import com.spider.entity.Star;
import com.spider.manager.RobotResultMng;

/**
 * 
 * 
 * 描述:抓取结果
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月8日 下午6:43:43
 */
@Service
@Transactional
public class RobotResultMngImpl implements RobotResultMng {
	@Autowired
	private RobotResultDao robotResultDao;

	@Override
	public void countRank(RobotResult robotResult, String fieldName,
			String rankFieldName, boolean asc, boolean zero) {
		robotResultDao.updateRank(robotResult, fieldName, rankFieldName, asc, zero);
	}

	@Override
	public void add(RobotResult robotResult) {
		robotResultDao.insert(robotResult);
	}

	@Override
	public RobotResult getByTaskAndStar(RobotResult robotResult) {
		return robotResultDao.selectByTaskAndStar(robotResult);
	}

	@Override
	public void save(RobotResult robotResult) {
		robotResultDao.udpate(robotResult);
	}

	@Override
	public Pagination find(RobotResult robotResult, Star star, int pageNo,
			int pageSize, String orderBy) {
		return robotResultDao.selectWithPage(robotResult, star, pageNo,
				pageSize, orderBy);
	}

	@Override
	public RobotResult getById(Long id) {
		return robotResultDao.selectById(id);
	}

	@Override
	public void countRank(RobotResult robotResult) {
		// 排名
		countRank(robotResult, "wechatNumber", "wechatRank", false,true);
		countRank(robotResult, "weiboForward", "weiboForwardRank", false,true);
		countRank(robotResult, "weiboComment", "weiboCommentRank", false,true);
		countRank(robotResult, "weiboLinkStatus", "weiboLinkStatusRank", false,true);
		countRank(robotResult, "weiboFanInc", "weiboFanIncRank", false,true);
		countRank(robotResult, "weiboData", "weiboDataRank", false,true);
		countRank(robotResult, "tiebaSign", "tiebaSignRank", false,true);
		countRank(robotResult, "tiebaPostNumInc", "tiebaPostNumIncRank", false,true);
		countRank(robotResult, "tiebaMemberNumInc", "tiebaMemberNumIncRank",
				false,true);
		countRank(robotResult, "baiduNews", "baiduNewsRank", false,true);
		countRank(robotResult, "baiduIndex", "baiduIndexRank", false,false);
	}

	@Override
	public void countScore(RobotResult robotResult) {
		robotResultDao.updateScore(robotResult, "wechatRank",
				"weiboForwardRank", "weiboCommentRank", "weiboLinkStatusRank",
				"weiboFanIncRank", "weiboDataRank", "tiebaSignRank",
				"tiebaPostNumIncRank", "tiebaMemberNumIncRank",
				"baiduNewsRank", "baiduIndexRank");
		// 计算综合分数
		countRank(robotResult, "score", "scoreRank", true,true);
	}

	@Override
	public void delete(Long id) {
		robotResultDao.delete(id);
	}
}
