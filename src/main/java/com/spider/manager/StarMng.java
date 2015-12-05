package com.spider.manager;

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
 * @since 2015年9月8日 下午6:29:51
 */
public interface StarMng {
	public Star getById(Long id);
	/**
	 * 
	 * 描述:导入
	 * 
	 * @param fileName
	 * @return
	 * @author liyixing 2015年9月9日 上午10:19:36
	 */
	// public int importExcel(String fileName);
	/**
	 * 
	 * 描述:添加
	 * 
	 * @param star
	 * @author liyixing 2015年9月9日 下午3:23:40
	 */
	public void add(Star star);

	/**
	 * 
	 * 描述:删除
	 * 
	 * @param id
	 * @author liyixing 2015年9月9日 下午4:13:44
	 */
	public void delete(Long id);

	/**
	 * 
	 * 描述:修改
	 * 
	 * @param star
	 * @author liyixing 2015年9月9日 下午4:14:13
	 */
	public void save(Star star);

	/**
	 * 
	 * 描述:分页查询
	 * 
	 * @param star
	 * @return
	 * @author liyixing 2015年9月9日 下午5:57:11
	 */
	public Pagination find(Star star, Integer pageNo);
	
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
	 * 描述:姓名查询（分类）
	 * 
	 * @param star
	 * @return
	 * @author liyixing 2015年9月9日 下午5:57:11
	 */
	public Star getByNameAndCategory(Star star);
	
	/**
	 * 
	 * 描述:查询某期任务中，还没有结果的
	 * 
	 * @param robotResult
	 * @return
	 * @author liyixing 2015年9月12日 上午9:30:09
	 */
	public List<Star> getNotRobot(RobotResult robotResult);
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
