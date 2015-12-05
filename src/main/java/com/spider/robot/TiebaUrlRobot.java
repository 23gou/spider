package com.spider.robot;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.browser.Browser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.spider.entity.RobotResult;
import com.spider.entity.Star;
import com.spider.entity.Task;
import com.spider.manager.RobotResultMng;
import com.spider.manager.StarMng;

/**
 * 
 * 
 * 描述:微博URL获取机器人
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月10日 上午9:41:01
 */
@Component
public class TiebaUrlRobot extends DefaultRobot {
	@Autowired
	private StarMng starMng;
	// private static final Logger LOGGER = LoggerFactory
	// .getLogger(TiebaRobot.class);
	@Autowired
	private RobotResultMng robotResultMng;

	@Override
	public void grabData(final Task task, final Browser browser,
			final Star star, final RobotResult robotResult,
			final Iterator<Star> starIterator, final RobotListener robotListener) {
		if (StringUtils.isNotBlank(star.getTiebaUrl())) {
			next(task, browser, star, robotResult, starIterator, robotListener);

			return;
		} else {
			star.setTiebaUrl("http://tieba.baidu.com/f?ie=utf-8&kw="
					+ star.getName().replace("(", "").replace(")", "")
					+ "&fr=search");
			starMng.save(star);
			next(task, browser, star, robotResult, starIterator, robotListener);

			return;
		}
	}
}
