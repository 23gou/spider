package com.spider.robot;

import java.util.Date;
import java.util.Iterator;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.spider.entity.RobotResult;
import com.spider.entity.Star;
import com.spider.entity.Task;
import com.spider.manager.RobotResultMng;
import com.spider.manager.TaskMng;

/**
 * 修改
 * 
 * 描述:
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年10月26日 下午3:10:13
 */
public class UpateRobot implements Robot {
	@Autowired
	private RobotResultMng robotResultMng;
	@Autowired
	private TaskMng taskMng;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(UpateRobot.class);
	private Date startDate = null;

	protected Robot next;

	public void setNext(Robot next) {
		this.next = next;
	}

	/**
	 * 
	 * 描述:调用下一个爬虫
	 * 
	 * @author liyixing 2015年9月10日 下午3:40:46
	 */
	protected void next(Task task, Browser browser, Star star,
			RobotResult robotResult, Iterator<Star> starIterator,
			RobotListener robotListener) {
		// browser.setText("");
		if (next != null) {
			next.grabData(task, browser, star, robotResult, starIterator,
					robotListener);
		}
	}

	@Override
	public void grabData(final Task task, final Browser browser, Star star,
			RobotResult robotResult, final Iterator<Star> starIterator,
			final RobotListener robotListener) {
		LOGGER.info("明星{}保存", star.getName());
		robotResultMng.save(robotResult);
		// robotResultMng.countRank(robotResult);
		// robotResultMng.countScore(robotResult);

		Date endDate = new Date();

		if (startDate != null) {
			LOGGER.info("明星{}耗时", endDate.getTime() - startDate.getTime());
		}

		// 直接进入下一个明星
		if (starIterator.hasNext()) {
			// 过10秒之后再执行
			Display.getDefault().timerExec((int) 3000, new Runnable() {
				public void run() {
					RobotResult nextRobotResult = new RobotResult();
					Star nextStar = starIterator.next();

					nextRobotResult.setTaskId(task.getId());
					nextRobotResult.setStarId(nextStar.getId());
					nextRobotResult = robotResultMng
							.getByTaskAndStar(nextRobotResult);
					startDate = new Date();
					next(task, browser, nextStar, nextRobotResult,
							starIterator, robotListener);
				}
			});
		} else {
			// 保存状态
			// task.setTaskStatus(Task.TaskStatus.完成.toString());
			// taskMng.save(task);
			robotListener.completed(task, browser, star, robotResult,
					starIterator);
		}
	}
}
