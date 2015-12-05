package com.spider.robot;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
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
 * 描述:微博URL获取机器人
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月10日 上午9:41:01
 */
@Component
public class WeiboUrlRobot extends DefaultRobot {
	@Autowired
	private StarMng starMng;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(TiebaRobot.class);
	@Autowired
	private RobotResultMng robotResultMng;

	@Override
	public void grabData(final Task task, final Browser browser,
			final Star star, final RobotResult robotResult,
			final Iterator<Star> starIterator, final RobotListener robotListener) {
		if (StringUtils.isNotBlank(star.getWeiboUrl())) {
			next(task, browser, star, robotResult, starIterator, robotListener);

			return;
		}

		// 处理微博关键字
		final ProgressAdapter addWeiboNameListener = new ProgressAdapter() {
			public void completed(ProgressEvent event) {
				browser.removeProgressListener(this);
				Display.getDefault().timerExec((int) 1000, new Runnable() {
					public void run() {
						// 解析微博名称
						// String select = "title";
						String text = browser.getText();
						String uid = PageParase
								.parseTextWithPatternHtml(text,
										",\"data\":\\{\"user\":\\[\\{\"u_id\":(\\d{0,}),");
						LOGGER.debug("明星{}微博url是{}", star.getName(), uid);

						if (StringUtils.isNotBlank(uid)) {
							star.setWeiboUrl("http://weibo.com/u/" + uid);
							starMng.save(star);
						} else {
							LOGGER.debug("明星{}微博url无效", star.getName());

							Display.getDefault().timerExec((int) 5000,
									new Runnable() {
										public void run() {
											grabData(task, browser, star,
													robotResult, starIterator,
													robotListener);
										}
									});

							return;
						}

						next(task, browser, star, robotResult, starIterator,
								robotListener);

						return;
					}
				});
			}
		};

		// 访问微博首页，完成自动登录
		browser.addProgressListener(new ProgressListener() {
			@Override
			public void completed(ProgressEvent event) {
				browser.removeProgressListener(this);

				Display.getDefault().timerExec((int) 1000, new Runnable() {
					public void run() {
						try {
							browser.addProgressListener(addWeiboNameListener);
							browser.setUrl("http://s.weibo.com/ajax/topsuggest.php?key="
									+ URLEncoder.encode(
											star.getName().replace("(", "")
													.replace(")", ""), "utf-8"));
						} catch (UnsupportedEncodingException e) {
							throw new RuntimeException(e);
						}

					}
				});
			}

			@Override
			public void changed(ProgressEvent event) {
				// TODO Auto-generated method stub

			}
		});

		browser.setUrl("http://weibo.com");
	}
}
