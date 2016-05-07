package com.spider.robot;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.spider.entity.RobotResult;
import com.spider.entity.RobotResult.EditStatus;
import com.spider.entity.RobotResult.ResultStatus;
import com.spider.entity.Star;
import com.spider.entity.Task;
import com.spider.manager.RobotResultMng;
import com.spider.manager.TaskMng;

public class DefaultRobot implements Robot {
	@Autowired
	private RobotResultMng robotResultMng;
	@Autowired
	private TaskMng taskMng;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultRobot.class);
	private Date startDate = null;
	protected String name = "默认";
	// 是否必须的
	protected boolean must = false;
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
	protected void next(Map<String, List<String>> options, Task task,
			Browser browser, Star star, RobotResult robotResult,
			Iterator<Star> starIterator, RobotListener robotListener) {
		if (next != null) {
			List<String> dimensions = options.get("维度");
			// 是否在需要抓取的维度内
			DefaultRobot next = (DefaultRobot) this.next;
			LOGGER.info("下一个机器人是：" + next.getName() + "," + next.isMust());

			if (next.isMust() || dimensions.contains(next.getName())) {
				LOGGER.info("需要执行下一个机器人");
				next.grabData(options, task, browser, star, robotResult,
						starIterator, robotListener);
			} else {
				LOGGER.info("不需要执行下一个机器人");
				next.next(options, task, browser, star, robotResult,
						starIterator, robotListener);
			}
		}
	}

	@Override
	public void grabData(Map<String, List<String>> options, final Task task,
			final Browser browser, Star star, RobotResult robotResult,
			final Iterator<Star> starIterator, final RobotListener robotListener) {
		robotResult.setResultStatus(ResultStatus.完成.toString());
		robotResult.setStartDateTime(task.getStartDateTime());

		if (robotResult.getId() == null) {
			LOGGER.info("明星{}保存", star.getName());
			robotResultMng.add(robotResult);
		} else {
			LOGGER.info("明星{}修改", star.getName());
			robotResultMng.save(robotResult);
		}
		
		robotResultMng.countRank(options, robotResult);
		robotResultMng.countScore(robotResult);

		Date endDate = new Date();
		LOGGER.info("明星{}耗时", endDate.getTime()
				- (startDate == null ? task.getStartDateTime().getTime()
						: startDate.getTime()));

		// 进入下一个明星
		if (starIterator.hasNext()) {
			// 过3秒之后再执行
			Display.getDefault().timerExec((int) 3000, new Runnable() {
				public void run() {
					RobotResult nextRobotResult = new RobotResult();
					Star nextStar = starIterator.next();

					nextRobotResult.setTaskId(task.getId());
					nextRobotResult.setStarId(nextStar.getId());
					nextRobotResult.setCategoryId(nextStar.getCategoryId());
					nextRobotResult.setResultStatus(ResultStatus.创建.toString());
					nextRobotResult.setEditStatus(EditStatus.未编辑.toString());
					startDate = new Date();
					next(options, task, browser, nextStar, nextRobotResult,
							starIterator, robotListener);
				}
			});
		} else {
			// 保存状态
			task.setTaskStatus(Task.TaskStatus.完成.toString());
			taskMng.save(task);
			robotListener.completed(task, browser, star, robotResult,
					starIterator);
		}
	}

	public RobotResultMng getRobotResultMng() {
		return robotResultMng;
	}

	public void setRobotResultMng(RobotResultMng robotResultMng) {
		this.robotResultMng = robotResultMng;
	}

	public TaskMng getTaskMng() {
		return taskMng;
	}

	public void setTaskMng(TaskMng taskMng) {
		this.taskMng = taskMng;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Robot getNext() {
		return next;
	}

	public boolean isMust() {
		return must;
	}

	public void setMust(boolean must) {
		this.must = must;
	}
}
