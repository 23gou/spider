package com.spider.dao;

import java.util.List;

import com.common.jdbc.page.Pagination;
import com.spider.entity.RobotResult;
import com.spider.entity.Star;

/**
 * 
 * 
 * 描述:明星
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月8日 下午6:21:20
 */
public interface StarDao {
	/**
	 * 
	 * 描述:插入新的
	 * 
	 * @param star
	 * @author liyixing 2015年9月9日 上午10:16:13
	 */
	public void insert(Star star);

	/**
	 * 
	 * 描述:删除
	 * 
	 * @param id
	 * @author liyixing 2015年9月9日 下午4:15:29
	 */
	public void delete(Long id);

	/**
	 * 
	 * 描述:删除
	 * 
	 * @param id
	 * @author liyixing 2015年9月9日 下午4:15:29
	 * @return
	 */
	public Star selectById(Long id);

	/**
	 * 
	 * 描述:修改
	 * 
	 * @param star
	 * @author liyixing 2015年9月9日 下午4:15:35
	 */
	public void update(Star star);

	/**
	 * 
	 * 描述:姓名查询
	 * 
	 * @param star
	 * @return
	 * @author liyixing 2015年9月9日 下午5:57:11
	 */
	public Star getByNameAndCategory(Star star);

	/**
	 * 
	 * 描述:分页查询
	 * 
	 * @param star
	 * @param orderBy
	 * @return
	 * @author liyixing 2015年9月9日 下午5:20:36
	 */
	public Pagination selectWithPage(Star star, Integer pageNo, String orderBy);

	/**
	 * 
	 * 描述:查询所有
	 * 
	 * @param star
	 * @return
	 * @author liyixing 2015年9月9日 下午5:57:11
	 */
	public List<Star> getAll();

	/**
	 * 
	 * 描述:查询某期任务中，还没有结果的
	 * 
	 * @param robotResult
	 * @return
	 * @author liyixing 2015年9月12日 上午9:30:09
	 */
	public List<Star> selectNotRobot(RobotResult robotResult);

	/**
	 * 
	 * 描述:查询某期红，百度指数还没有更新过的
	 * 
	 * @param robotResult
	 * @return
	 * @author liyixing 2015年9月12日 上午9:30:09
	 */
	public List<Star> selectNotBaiduIndexRobot(RobotResult robotResult);
}
