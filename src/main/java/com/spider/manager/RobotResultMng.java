package com.spider.manager;

import java.util.List;
import java.util.Map;

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
 * @since 2015年9月8日 下午6:29:51
 */
public interface RobotResultMng {
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
	 *            是否升序
	 * @author liyixing 2015年9月10日 下午2:51:27
	 */
	public void countRank(RobotResult robotResult, String fieldName,
			String rankFieldName, boolean asc, boolean zero);

	/**
	 * 
	 * 描述:更新综合分数
	 * 
	 * @author liyixing 2015年9月10日 下午2:51:27
	 */
	public void countScore(RobotResult robotResult);

	/**
	 * 
	 * 描述:计算积分和
	 * 
	 * @param robotResult
	 * @author liyixing 2015年9月14日 下午3:11:16
	 */
	public void countRank(Map<String,List<String>> options,RobotResult robotResult);

	/**
	 * 
	 * 描述:插入
	 * 
	 * @param robotResult
	 * @author liyixing 2015年9月10日 下午3:00:27
	 */
	public void add(RobotResult robotResult);

	/**
	 * 
	 * 描述:保存
	 * 
	 * @param robotResult
	 * @author liyixing 2015年9月10日 下午3:00:27
	 */
	public void save(RobotResult robotResult);

	/**
	 * 
	 * 描述:根据明星和任务，查询
	 * 
	 * @param robotResult
	 * @author liyixing 2015年9月10日 下午3:02:07
	 * @return
	 */
	public RobotResult getByTaskAndStar(RobotResult robotResult);

	/**
	 * 
	 * 描述:查询
	 * 
	 * @param robotResult
	 * @param star
	 * @param pageNo
	 * @param orderBy
	 * @return
	 * @author liyixing 2015年9月12日 下午6:32:21
	 */
	public Pagination find(RobotResult robotResult, Star star, int pageNo,
			int pageSize, String orderBy);

	/**
	 * 
	 * 描述:主键
	 * 
	 * @param id
	 * @return
	 * @author liyixing 2015年9月14日 下午2:41:24
	 */
	public RobotResult getById(Long id);

	/**
	 * 
	 * 描述:删除
	 * 
	 * @param id
	 * @author liyixing 2015年10月16日 上午10:58:23
	 */
	public void delete(Long id);
}
