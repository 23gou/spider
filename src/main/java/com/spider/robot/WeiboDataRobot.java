package com.spider.robot;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spider.entity.RobotResult;
import com.spider.entity.Star;
import com.spider.entity.Task;
import com.spider.manager.RobotResultMng;
import com.spider.manager.StarMng;
import com.spider.parse.PageParase;

/**
 * 
 * 
 * 描述:微博指数机器人
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月10日 上午9:41:01
 */
@Component
public class WeiboDataRobot extends DefaultRobot {
	@Autowired
	private StarMng starMng;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(WeiboDataRobot.class);
	@Autowired
	private RobotResultMng robotResultMng;

	public WeiboDataRobot() {
		setName("微指数");
	}

	public boolean validateDimensionZero(final Map<String, List<String>> options,
			Task task, Browser browser, Star star, RobotResult robotResult,
			Iterator<Star> starIterator, RobotListener robotListener) {
		return robotResult.getWeiboData() > 0;
	}

	@SuppressWarnings("unchecked")
	private void parseValue(final Map<String, List<String>> options, final Task task,
			final Browser browser, final Star star,
			final RobotResult robotResult, final Iterator<Star> starIterator,
			final RobotListener robotListener, final ProgressAdapter my,
			String text) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String vs = text;
			Map<String, Object> result = objectMapper.readValue(vs, Map.class);
			int allData = 0;
			try {
				List<Map<String, Object>> results = (List<Map<String, Object>>) result
						.get("zt");

				for (Map<String, Object> oneDay : results) {
					// 计算7天总数
					Object v = oneDay.get("value");

					if (v != null) {
						allData += Integer.valueOf(v.toString());
					}
				}
			} catch (Exception e) {
				LOGGER.info("明星{}的微博指数出错，重新开始", star.getName());
				browser.addProgressListener(my);
				browser.refresh();
			}

			LOGGER.info("明星{}的关键字总数是{}", star.getName(), allData);
			robotResult.setWeiboData(allData);
			next(options, task, browser, star, robotResult, starIterator,
					robotListener);
		} catch (Exception e) {
			LOGGER.info("明星{}的微博指数出错，重新开始", star.getName());
			grabData(options, task, browser, star, robotResult, starIterator, robotListener);
			return;
		}
	}

	@Override
	public void grabData(final Map<String, List<String>> options, final Task task,
			final Browser browser, final Star star,
			final RobotResult robotResult, final Iterator<Star> starIterator,
			final RobotListener robotListener) {
		robotResult.setWeiboData(0);
		// 上周一
		final Date preWeek = UtilDateTime.getPreMondy(task.getStartDateTime());

		// 获取关键字ID
		final ProgressAdapter weiboDataListener = new ProgressAdapter() {
			public void completed(ProgressEvent event) {
				final ProgressAdapter my = this;
				browser.removeProgressListener(this);
				Display.getDefault().timerExec((int) 1000, new Runnable() {
					public void run() {
						String text = browser.getText();
						parseValue(options, task, browser, star, robotResult,
								starIterator, robotListener, my, text);
					}
				});
			}
		};

		// 获取关键字ID
		final ProgressAdapter weiboDataIdListener = new ProgressAdapter() {
			public void completed(final ProgressEvent event) {
				browser.removeProgressListener(this);
				Display.getDefault().timerExec((int) 1000, new Runnable() {
					public void run() {
						// String url = browser.getUrl();
						String text = browser.getText();
						if (text.indexOf("<!-- 登录浮层 -->") > 0) {
							return;
						}
						LOGGER.info(text);
						final String id = PageParase.parseTextWithPatternHtml(
								text, "\\{\"id\":\"(\\d{0,})\"");
						LOGGER.info("明星{}的关键字ID是{}}", star.getName(), id);

						if (StringUtils.isBlank(id)) {
							next(options, task, browser, star, robotResult,
									starIterator, robotListener);
							return;
						}

						// 进入指数首页，然后跳转到数据页
						final ProgressAdapter indexData = new ProgressAdapter() {
							public void completed(ProgressEvent event) {
								browser.removeProgressListener(this);
								Display.getDefault().timerExec((int) 1000,
										new Runnable() {
											public void run() {
												// 计算时间，一周范围，上周一到这周
												Date now = new Date();

												Header header = new BasicHeader(
														"referer",
														"http://data.weibo.com/index");
												String url = "http://data.weibo.com/index/ajax/getchartdata?wid="
														+ id
														+ "&sdate="
														+ DateUtils
																.formatDate(
																		preWeek,
																		DateUtils.YYYY_MM_DD)
														+ "&edate="
														+ DateUtils.formatDate(
																UtilDateTime
																		.getPreSundy(task
																				.getStartDateTime()),
																DateUtils.YYYY_MM_DD)
														+ "&__rnd="
														+ now.getTime();
												String u = PageParase.toUrl(
														url, header);
												parseValue(options, task,
														browser, star,
														robotResult,
														starIterator,
														robotListener,
														weiboDataListener, u);
											}
										});

								return;
							}
						};

						browser.addProgressListener(indexData);
						browser.setUrl("http://data.weibo.com/index");
					}
				});
			}
		};

		// 进入指数首页，然后跳转到ID获取页
		final ProgressAdapter indexId = new ProgressAdapter() {
			public void completed(ProgressEvent event) {
				browser.removeProgressListener(this);
				Display.getDefault().timerExec((int) 1000, new Runnable() {
					public void run() {
						// 计算时间，一周范围，上周一到这周
						Date now = new Date();
						browser.addProgressListener(weiboDataIdListener);
						PageParase.toUrl(
								browser,
								"http://data.weibo.com/index/ajax/contrast?key2="
										+ star.getName()
										+ "&key3=&key4=&key5="
										+ DateUtils.formatDate(preWeek,
												DateUtils.YYYY_MM_DD)
										+ "&key6="
										+ DateUtils.formatDate(UtilDateTime
												.getPreSundy(task
														.getStartDateTime()),
												DateUtils.YYYY_MM_DD)
										+ "&_t=0&__rnd=" + now.getTime());
					}
				});
			}
		};

		browser.addProgressListener(indexId);
		browser.setUrl("http://data.weibo.com/index");
	}
}
