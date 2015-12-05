package com.spider.robot;

import java.util.Iterator;

import org.eclipse.swt.browser.Browser;

import com.spider.entity.RobotResult;
import com.spider.entity.Star;
import com.spider.entity.Task;

public interface Robot {
	/**
	 * 
	 * 描述:抓取任务
	 * 
	 * @param content
	 * @author liyixing 2015年9月10日 上午9:32:18
	 */
	// public void grab(Task task, Browser browser, List<Star> stars);

	/**
	 * 
	 * 描述:抓取数据
	 * 
	 * @param content
	 * @author liyixing 2015年9月10日 上午9:32:18
	 */
	public void grabData(Task task, Browser browser, Star star,
			RobotResult robotResult, Iterator<Star> starIterator,
			RobotListener robotListener);

	/**
	 * 
	 * 描述:下一个爬虫
	 * 
	 * @param content
	 * @author liyixing 2015年9月10日 上午9:32:18
	 */
	public void setNext(Robot next);

	/**
	 * 
	 * 描述:下一个爬虫开始
	 * 
	 * @author liyixing 2015年9月10日 上午11:06:34
	 */
	// public boolean next(Task task, Browser browser, Star stars);
	/**
	 * 
	 * 
	 * 描述:抓取监听器
	 *
	 * @author liyixing
	 * @version 1.0
	 * @since 2015年9月14日 上午11:03:42
	 */
	public interface RobotListener {
		/**
		 * 
		 * 描述:抓取完成事件
		 * 
		 * @author liyixing 2015年9月14日 上午11:04:19
		 */
		public void completed(Task task, Browser browser, Star star,
				RobotResult robotResult, Iterator<Star> starIterator);
	}
}
