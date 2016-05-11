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
import com.spider.entity.TaskOption.TaskOptionStatus;
import com.spider.entity.TaskOption.TaskOptionType;
import com.spider.entity.Star;
import com.spider.entity.Task;
import com.spider.entity.TaskOption;
import com.spider.manager.RobotResultMng;
import com.spider.manager.TaskMng;
import com.spider.manager.TaskOptionMng;

public class DefaultRobot implements Robot {
	@Autowired
	private RobotResultMng robotResultMng;
	@Autowired
	private TaskMng taskMng;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultRobot.class);
	private Date startDate = null;
	// 抓取次数
	private int times = 1;
	protected String name = "默认";
	// 是否必须的
	protected boolean must = false;
	protected Robot next;
	@Autowired
	private TaskOptionMng taskOptionMng;

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
		List<String> dimensions = options.get("维度");

		if (isMust() || dimensions.contains(getName())) {
			LOGGER.info("{}需要抓取，验证是否为0", getName());
			if (!validateZero(options, task, browser, star, robotResult,
					starIterator, robotListener)) {
				LOGGER.info("{}为0再次抓取一次", getName());
				times++;
				// 需要重新抓取一次
				grabData(options, task, browser, star, robotResult,
						starIterator, robotListener);

				return;
			}
		}

		if (next != null) {

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

		TaskOption taskOptionCurrent = taskOptionMng.getByTaskAndName(
				task.getId(), TaskOptionType.当前明星.toString(), null).get(0);

		taskOptionMng.update(taskOptionCurrent.getId(), null,
				robotResult.getStarId() + "");
		robotResultMng.countRank(options, robotResult);
		robotResultMng.countScore(robotResult);

		Date endDate = new Date();
		LOGGER.info("明星{}耗时{}", star.getName(),endDate.getTime()
				- (startDate == null ? task.getStartDateTime().getTime()
						: startDate.getTime()));

		// 进入下一个明星
		if (starIterator.hasNext()) {
			// 过5秒之后再执行
			Display.getDefault().timerExec((int) 6000, new Runnable() {
				public void run() {
					RobotResult nextRobotResult = new RobotResult();
					Star nextStar = starIterator.next();

					nextRobotResult.setTaskId(task.getId());
					nextRobotResult.setStarId(nextStar.getId());
					
					RobotResult robotResult = robotResultMng.getByTaskAndStar(nextRobotResult);
					
					if(robotResult != null) {
						nextRobotResult = robotResult;
					} else {
						nextRobotResult.setEditStatus(EditStatus.未编辑.toString());
					}
					
					nextRobotResult.setCategoryId(nextStar.getCategoryId());
					nextRobotResult.setResultStatus(ResultStatus.创建.toString());
					startDate = new Date();
					next(options, task, browser, nextStar, nextRobotResult,
							starIterator, robotListener);
				}
			});
		} else {
			// 保存状态
			task.setTaskStatus(Task.TaskStatus.完成.toString());
			taskMng.save(task);
			// 保存选项状态
			List<TaskOption> taskOptions = taskOptionMng.getByTaskAndName(
					task.getId(), null, TaskOptionStatus.未完成.toString());

			for (TaskOption taskOption : taskOptions) {
				taskOption.setStatus(TaskOptionStatus.已完成.toString());
				taskOptionMng.update(taskOption.getId(),
						taskOption.getStatus(), null);
			}

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

	@Override
	public boolean validateZero(Map<String, List<String>> options, Task task,
			Browser browser, Star star, RobotResult robotResult,
			Iterator<Star> starIterator, RobotListener robotListener) {
		if (times == 2) {
			LOGGER.info("{}已经重复抓取过一次", getName());
			times = 1;

			return true;
		}

		// 验证当前数据是不是为0
		return validateDimensionZero(options, task, browser, star, robotResult,
				starIterator, robotListener);
	}

	/**
	 * 
	 * 描述:验证自己的维度是不是为空
	 * 
	 * @author liyixing 2016年5月9日 上午10:27:58
	 */
	public boolean validateDimensionZero(Map<String, List<String>> options,
			Task task, Browser browser, Star star, RobotResult robotResult,
			Iterator<Star> starIterator, RobotListener robotListener) {
		return true;
	}
}
