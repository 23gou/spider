package com.spider.robot;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.browser.Browser;
import org.springframework.stereotype.Component;

import com.spider.entity.RobotResult;
import com.spider.entity.Star;
import com.spider.entity.Task;

/**
 * 
 * 
 * 描述:空机器人,没有任何业务,只是用来作为第一个机器人使用
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月10日 上午9:41:01
 */
@Component
public class NoneRobot extends DefaultRobot {
	public NoneRobot() {
		this.setName("空机器人");
	}

	@Override
	public void grabData(final Map<String, List<String>> options, final Task task,
			final Browser browser, final Star star,
			final RobotResult robotResult, final Iterator<Star> starIterator,
			final RobotListener robotListener) {
		next(options, task, browser, star, robotResult, starIterator,
				robotListener);
	}
}
