package com.spider.robot;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.spider.entity.RobotResult;
import com.spider.entity.Star;
import com.spider.entity.Task;
import com.spider.manager.RobotResultMng;
import com.spider.manager.StarMng;
import com.spider.parse.PageParase;

/**
 * 
 * 
 * 描述:贴吧签到机器人
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月10日 上午9:41:01
 */
@Component
public class TiebaSignRobot extends DefaultRobot {
	@Autowired
	private StarMng starMng;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(TiebaSignRobot.class);
	@Autowired
	private RobotResultMng robotResultMng;

	public TiebaSignRobot() {
		setName("贴吧签到");
	}

	public boolean validateDimensionZero(Map<String, List<String>> options,
			Task task, Browser browser, Star star, RobotResult robotResult,
			Iterator<Star> starIterator, RobotListener robotListener) {
		return robotResult.getTiebaSign() > 0;
	}
	
	@Override
	public void grabData(Map<String, List<String>> options, final Task task,
			final Browser browser, final Star star,
			final RobotResult robotResult, final Iterator<Star> starIterator,
			final RobotListener robotListener) {
		robotResult.setTiebaSign(0);

		// 进入签到首页，然后跳转到ID获取页
		final ProgressAdapter tiebaSign = new ProgressAdapter() {
			public void completed(ProgressEvent event) {
				browser.removeProgressListener(this);
				Display.getDefault().timerExec((int) 1000, new Runnable() {
					public void run() {
						parse(options, task, browser, star, robotResult,
								starIterator, robotListener);
					}
				});
			}
		};

		browser.addProgressListener(tiebaSign);

		try {
			browser.setUrl("http://tieba.baidu.com/sign/index?kw="
					+ URLEncoder.encode(star.getName().replace("(", "")
							.replace(")", ""), "gbk") + "&type=2&pn=1");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 描述:解析签名数
	 * 
	 * @param star
	 * @param robotResult
	 * @param text
	 * @author liyixing 2015年9月11日 下午6:12:36
	 */
	private void parse(Map<String, List<String>> options, final Task task,
			final Browser browser, final Star star,
			final RobotResult robotResult, final Iterator<Star> starIterator,
			final RobotListener robotListener) {
		String text = browser.getText();
		// LOGGER.info(text);
		String rep = "\"weekly_rank_info\":\\s{0,}\\{\"sign_count\":(\\d{0,}),";
		try {
			robotResult.setTiebaSign(Integer.valueOf(PageParase
					.parseTextWithPatternHtml(text, rep)));
		} catch (Exception e) {
			LOGGER.info(
					"明星{}的贴吧{}，签到数是解析失败，重新加载页面,url{}，text{}。",
					new Object[] { star.getName(), star.getTiebaName(),
							browser.getUrl(), browser.getText() });
			grabData(options, task, browser, star, robotResult, starIterator,
					robotListener);
			return;
		}
		LOGGER.info(
				"明星{}的贴吧{}，签到数是{}",
				new Object[] { star.getName(), star.getTiebaName(),
						robotResult.getTiebaSign() });
		next(options, task, browser, star, robotResult, starIterator,
				robotListener);
	}
}