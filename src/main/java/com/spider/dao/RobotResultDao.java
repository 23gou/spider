package com.spider.dao;

import com.common.jdbc.page.Pagination;
import com.spider.entity.RobotResult;
import com.spider.entity.Star;

/**
 * 
 * 
 * 描述:抓取结果
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月8日 下午6:21:20
 */
public interface RobotResultDao {
	/**
	 * 
	 * 描述:更新指定的字段的排名
	 * 
	 * @param robotResult
	 * @param fieldName
	 *            需要排名的字段
	 * @param rankFieldName
	 *            排名后的排名值存放的字段
	 * @param asc
	 *            排序
	 * @param zero
	 *            是否包含数值为0的情况
	 * @author liyixing 2015年9月10日 下午2:51:27
	 */
	public void updateRank(RobotResult robotResult, String fieldName,
			String rankFieldName, boolean asc, boolean zero);

	/**
	 * 
	 * 描述:更新综合分
	 * 
	 * @param robotResult
	 * @param fieldNames
	 *            需要算入综合分的字段
	 * @author liyixing 2015年9月10日 下午2:51:27
	 */
	public void updateScore(RobotResult robotResult, String... fieldNames);

	/**
	 * 
	 * 描述:插入
	 * 
	 * @param robotResult
	 * @author liyixing 2015年9月10日 下午3:00:27
	 */
	public void insert(RobotResult robotResult);

	/**
	 * 
	 * 描述:修改
	 * 
	 * @param robotResult
	 * @author liyixing 2015年9月11日 下午2:40:08
	 */
	public void udpate(RobotResult robotResult);

	/**
	 * 
	 * 描述:根据明星和任务，查询
	 * 
	 * @param robotResult
	 * @author liyixing 2015年9月10日 下午3:02:07
	 * @return
	 */
	public RobotResult selectByTaskAndStar(RobotResult robotResult);

	/**
	 * 
	 * 描述:分页查询
	 * 
	 * @param robotResult
	 * @param pageNo
	 * @param pageSize
	 * @param orderBy
	 * @return
	 * @author liyixing 2015年9月12日 下午6:23:00
	 */
	public Pagination selectWithPage(RobotResult robotResult, Star star,
			int pageNo, int pageSize, String orderBy);

	/**
	 * 
	 * 描述:分页查询
	 * 
	 * @param robotResult
	 * @param pageNo
	 * @param pageSize
	 * @param orderBy
	 * @return
	 * @author liyixing 2015年9月12日 下午6:23:00
	 */
	public RobotResult selectById(Long id);

	/**
	 * 
	 * 描述:删除未完成的
	 * 
	 * @author liyixing 2015年9月10日 下午3:16:15
	 */
	// public void deleteNotSuccess();
	/**
	 * 
	 * 描述:删除
	 * 
	 * @param id
	 * @author liyixing 2015年10月16日 上午10:59:09
	 */
	public void delete(Long id);
}
