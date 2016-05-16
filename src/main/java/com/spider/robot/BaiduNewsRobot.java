package com.spider.robot;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.gou23.cgodo.util.UtilDateTime;

import com.common.util.DateUtils;
import com.spider.entity.RobotResult;
import com.spider.entity.Star;
import com.spider.entity.Task;
import com.spider.manager.RobotResultMng;
import com.spider.manager.StarMng;
import com.spider.parse.PageParase;

/**
 * 
 * 
 * 描述:百度新闻机器人
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月10日 上午9:41:01
 */
@Component
public class BaiduNewsRobot extends DefaultRobot {
	@Autowired
	private StarMng starMng;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(BaiduNewsRobot.class);
	@Autowired
	private RobotResultMng robotResultMng;

	public BaiduNewsRobot() {
		this.setName("百度新闻");
	}

	public boolean validateDimensionZero(final Map<String, List<String>> options,
			Task task, Browser browser, Star star, RobotResult robotResult,
			Iterator<Star> starIterator, RobotListener robotListener) {
		return robotResult.getBaiduNews() > 0;
	}
	
	@Override
	public void grabData(final Map<String, List<String>> options, final Task task,
			final Browser browser, final Star star,
			final RobotResult robotResult, final Iterator<Star> starIterator,
			final RobotListener robotListener) {
		// 上周一
		final Date preWeek = UtilDateTime.getPreMondy(task.getStartDateTime());
		// 上周日
		final Date preWeekS = UtilDateTime.getPreSundy(task.getStartDateTime());

		// 进入指数首页，然后跳转到ID获取页
		final ProgressAdapter indexId = new ProgressAdapter() {
			public void completed(ProgressEvent event) {
				browser.removeProgressListener(this);
				Display.getDefault().timerExec((int) 1000, new Runnable() {
					public void run() {
						// 计算时间，一周范围，上周一到这周
						String text = browser.getText();
						LOGGER.info(text);
						if (text.indexOf("抱歉，没有找到与“<em>" + star.getName()
								+ "</em>” 相关的新闻内容。") >= 0) {
							robotResult.setBaiduNews(0);
							next(options, task, browser, star, robotResult,
									starIterator, robotListener);
							return;
						}
						
						if (text.indexOf("抱歉，没有找到与“<EM>" + star.getName()
								+ "</EM>” 相关的新闻内容。") >= 0) {
							robotResult.setBaiduNews(0);
							next(options, task, browser, star, robotResult,
									starIterator, robotListener);
							return;
						}

						String num = PageParase.parseTextWithPatternHtml(text,
								"找到相关新闻(\\S{0,})篇").replace(",", "");

						if (StringUtils.isBlank(num)) {
							num = PageParase.parseTextWithPatternHtml(text,
									"找到相关新闻约(\\S{0,})篇").replace(",", "");
						}

						if (num != null) {
							num = num.replace("约", "");
						}

						if (StringUtils.isBlank(num) || "0".equals(num)) {
							LOGGER.info("");
						}

						if (StringUtils.isNotBlank(num)) {
							LOGGER.info("明星{}的百度新闻数是{}", star.getName(), num);
							robotResult.setBaiduNews(Integer.valueOf(num));
							next(options, task, browser, star, robotResult,
									starIterator, robotListener);

							return;
						} else {
							LOGGER.info("明星{}的百度新闻数检索失败，重新开始检索，页面数据是{}",
									star.getName(), text);
							grabData(options, task, browser, star, robotResult,
									starIterator, robotListener);
							return;
						}
					}
				});
			}
		};

		browser.addProgressListener(indexId);
		try {

			browser.setUrl("http://news.baidu.com/ns?from=news&cl=2&bt="
					+ (UtilDateTime.setDateToFirstTime(preWeek).getTime() / 1000)
					+ "&y0="
					+ DateUtils.formatDate(preWeek, "yyyy")
					+ "&m0="
					+ DateUtils.formatDate(preWeek, "M")
					+ "&d0="
					+ DateUtils.formatDate(preWeek, "d")
					+ "&y1="
					+ DateUtils.formatDate(preWeekS, "yyyy")
					+ "&m1="
					+ DateUtils.formatDate(preWeekS, "M")
					+ "&d1="
					+ DateUtils.formatDate(preWeekS, "d")
					+ "&et="
					+ (UtilDateTime.setDateToLastTime(preWeekS).getTime() / 1000)
					+ "&q1=" + URLEncoder.encode(star.getName(), "gbk")
					+ "&submit=%B0%D9%B6%C8%D2%BB%CF%C2&q3=&q4=&mt=0&lm=&s=2"
					+ "&begin_date="
					+ DateUtils.formatDate(preWeek, DateUtils.YYYY_MM_DD)
					+ "&end_date="
					+ DateUtils.formatDate(preWeekS, DateUtils.YYYY_MM_DD)
					+ "&tn=newstitledy&ct=0&rn=20&q6=");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
}
