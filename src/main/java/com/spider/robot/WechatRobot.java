package com.spider.robot;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

import com.spider.entity.RobotResult;
import com.spider.entity.Star;
import com.spider.entity.Task;
import com.spider.manager.StarMng;
import com.spider.parse.PageParase;

/**
 * 
 * 
 * 描述:微信机器人
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月10日 上午9:41:01
 */
@Component
public class WechatRobot extends DefaultRobot {
	@Autowired
	private StarMng starMng;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(WechatRobot.class);

	public WechatRobot() {
		setName("微信");
	}

	public boolean validateDimensionZero(final Map<String, List<String>> options,
			Task task, Browser browser, Star star, RobotResult robotResult,
			Iterator<Star> starIterator, RobotListener robotListener) {
		return robotResult.getWechatNumber() > 0;
	}
	
	@Override
	public void grabData(final Map<String, List<String>> options, final Task task,
			final Browser browser, final Star star,
			final RobotResult robotResult, final Iterator<Star> starIterator,
			final RobotListener robotListener) {

		// 处理微博关键字
		final ProgressAdapter addWechatListener = new ProgressAdapter() {
			public void completed(ProgressEvent event) {
				browser.removeProgressListener(this);
				parse(options, task, browser, star, robotResult, starIterator,
						robotListener);
			}
		};

		browser.addProgressListener(addWechatListener);
		try {
			browser.setUrl("http://weixin.sogou.com/weixin?query="
					+ URLEncoder.encode(star.getName(), "utf-8")
					+ "&fr=sgsearch&sut=1138&type=2&ie=utf8&sst0=1441871133479&sourceid=inttime_week&interation=&interV=kKIOkrELjboJmLkElbYTkKIKmbELjbkRmLkElbk%3D_1893302304&tsn=2");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public void parse(final Map<String, List<String>> options, final Task task,
			final Browser browser, final Star star,
			final RobotResult robotResult, final Iterator<Star> starIterator,
			final RobotListener robotListener) {
		// 解析微信
		Display.getDefault().timerExec((int) 1000, new Runnable() {
			public void run() {
				String select = "totalItems (\\d{0,})";
				String text = browser.getText();
				
				if(text.indexOf("没有找到相关的微信公众号文章。")>0) {
					LOGGER.info("明星{}没有找到相关的微信公众号文章。", star.getName());
					robotResult.setWechatNumber(0);
					next(options, task, browser, star, robotResult,
							starIterator, robotListener);
					return;
				}
				if(text.indexOf("用户您好，您的访问过于频繁，为确认本次访问为正常用户行为，需要您协助验证。") > 0) {
					parse(options, task, browser, star, robotResult, starIterator, robotListener);
					
					return;
				}
				String number = PageParase.parseTextWithPatternHtml(text,
						"找到约<resnum id=\"scd_num\">(\\S{0,})</resnum>条结果")
						.replace(",", "");
				if (StringUtils.isBlank(number)) {
					number = PageParase.parseTextWithPatternHtml(text,
							"找到<resnum id=\"scd_num\">(\\S{0,})</resnum>条结果")
							.replace(",", "");

				}

				if (StringUtils.isBlank(number)) {
					number = PageParase.parseTextWithPatternHtml(text, select)
							.replace(",", "");
				}

				try {
					if (StringUtils.isBlank(number)) {
						number = browser.evaluate(
								"return $('div.wx-rb').length + ''").toString();
					}
				} catch (Exception e) {
					LOGGER.info("明星{}解析微信文章数出错", e, text);

				}

				if (StringUtils.isNotBlank(number)) {
					robotResult.setWechatNumber(Integer.valueOf(number));
					LOGGER.info("明星{}解析出来的微信文章数是{}", star.getName(),
							robotResult.getWechatNumber());

					next(options, task, browser, star, robotResult,
							starIterator, robotListener);
				} else {
					LOGGER.info("明星{}解析出来的微信文章数是{}，微信页面是：{}", new Object[] {
							star.getName(), "", text });
					if (text.indexOf("很抱歉，您的电脑或所在的局域网络有异常的访问，此刻我们无法响应您的请求。") >= 0) {
						LOGGER.info("很抱歉，您的电脑或所在的局域网络有异常的访问，此刻我们无法响应您的请求。，等待验证码处理");
						parse(options, task, browser, star, robotResult,
								starIterator, robotListener);
					} else {
						grabData(options, task, browser, star, robotResult,
								starIterator, robotListener);
						return;
					}
				}
			}
		});
	}
}
