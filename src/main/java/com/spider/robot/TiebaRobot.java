package com.spider.robot;

import java.util.Iterator;

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
import com.spider.manager.RobotResultMng;
import com.spider.manager.StarMng;
import com.spider.parse.PageParase;

/**
 * 
 * 
 * 描述:贴吧机器人
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月10日 上午9:41:01
 */
@Component
public class TiebaRobot extends DefaultRobot {
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
		LOGGER.info("贴吧");
		// 处理贴吧关键字
		final ProgressAdapter addTiebaNameListener = new ProgressAdapter() {
			public void completed(ProgressEvent event) {
				browser.removeProgressListener(this);
				LOGGER.info("addTiebaNameListener");
				parse(task, browser, star, robotResult, starIterator,
						robotListener);

			}

			/**
			 * 描述:
			 * 
			 * @param task
			 * @param browser
			 * @param star
			 * @param robotResult
			 * @param starIterator
			 * @param robotListener
			 * @author liyixing 2015年9月25日 下午3:08:36
			 */

			private void parse(final Task task, final Browser browser,
					final Star star, final RobotResult robotResult,
					final Iterator<Star> starIterator,
					final RobotListener robotListener) {
				Display.getDefault().timerExec((int) 1000, new Runnable() {
					public void run() {
						try {
							LOGGER.info("parse");
							Browser browser1 = browser;
							// 解析贴吧关键字
							String text = browser.getText();

							//
							// if (StringUtils.isBlank(text)) {
							// text = browser1.evaluate(
							// "return document.title").toString();
							// text = text
							// + browser1
							// .evaluate(
							// "return document.getElementsByTagName('body')[0].innerHTML")
							// .toString();
							// }
							if (StringUtils.isBlank(text)) {
								LOGGER.info("明星{}解析出来的贴吧数据是空：{}",
										star.getName(), text);
								parse(task, browser1, star, robotResult,
										starIterator, robotListener);
								return;
							}
							String tiebaName = PageParase
									.parseTextWithPatternHtml(text,
											"<TITLE>[\\s]{0,}([\\S]{0,})_百度贴吧[\\s]{0,}<");

							if (StringUtils.isBlank(tiebaName)) {
								tiebaName = PageParase
										.parseTextWithPatternHtml(text,
												"<title>[\\s]{0,}([\\S]{0,})_百度贴吧[\\s]{0,}<");
							}
							LOGGER.info("明星{}解析出来的贴吧名称是{}", star.getName(),
									tiebaName);
							// LOGGER.info("明星{}解析出来的贴吧数据是：{}", star.getName(),
							// text);

							if (StringUtils.isNotBlank(tiebaName)) {
								star.setTiebaName(tiebaName);
								// LOGGER.info(text);
								starMng.save(star);
								// 解析出帖子数和会员数

								// IE
								String memberNumber = PageParase
										.parseTextWithPatternHtml(text,
												"<SPAN class=j_visit_num>(\\S{0,})</SPAN>")
										.replace(",", "");

								if (StringUtils.isBlank(memberNumber)) {
									memberNumber = PageParase
											.parseTextWithPatternHtml(text,
													"<SPAN class=card_menNum>(\\S{0,})</SPAN>")
											.replace(",", "");
								}

								// 火狐
								if (StringUtils.isBlank(memberNumber)) {
									memberNumber = PageParase
											.parseTextWithPatternHtml(text,
													"<span class=\"card_menNum\">(\\S{0,})</span>")
											.replace(",", "");
								}

								if (StringUtils.isBlank(memberNumber)) {
									memberNumber = PageParase
											.parseTextWithPatternHtml(text,
													"<span class=\"j_visit_num\">(\\S{0,})</span>")
											.replace(",", "");
								}

								Integer memberNum = Integer
										.valueOf(memberNumber);
								// IE
								String postNumber = PageParase
										.parseTextWithPatternHtml(text,
												"<SPAN class=\"j_post_num post_num\">(\\S{0,})</SPAN>")
										.replace(",", "");

								// text.substring(text.indexOf("card_infoNum") -
								// 20);
								if (StringUtils.isBlank(postNumber)) {
									postNumber = PageParase
											.parseTextWithPatternHtml(text,
													"<SPAN class=card_infoNum>(\\S{0,})</SPAN>")
											.replace(",", "")
											.replace("</SPAN>", "");
								}

								if (StringUtils.isBlank(postNumber)) {
									postNumber = PageParase
											.parseTextWithPatternHtml(text,
													"<span class=\"card_infoNum\">(\\S{0,})</span>")
											.replace(",", "")
											.replace("</span>", "");
								}

								if (StringUtils.isBlank(postNumber)) {
									postNumber = PageParase
											.parseTextWithPatternHtml(text,
													"<span class=\"j_post_num post_num\">(\\S{0,})</span>")
											.replace(",", "");
								}
								Integer postNum = Integer.valueOf(postNumber);
								robotResult.setTiebaMemberNum(memberNum);
								robotResult.setTiebaMemberNumInc(memberNum);
								robotResult.setTiebaPostNum(postNum);
								robotResult.setTiebaPostNumInc(postNum);

								// 计算增量
								RobotResult caroRobotResult = new RobotResult();

								if (task.getContrastTaskId() != null) {
									caroRobotResult.setTaskId(task
											.getContrastTaskId());
									caroRobotResult.setStarId(star.getId());
									RobotResult contrastRobotResult = robotResultMng
											.getByTaskAndStar(caroRobotResult);

									if (contrastRobotResult != null) {
										// 计算增量
										robotResult.setTiebaMemberNumInc(robotResult
												.getTiebaMemberNum()
												- contrastRobotResult
														.getTiebaMemberNum());
										robotResult.setTiebaPostNumInc(robotResult
												.getTiebaPostNum()
												- contrastRobotResult
														.getTiebaPostNum());
									}
								}

								next(task, browser, star, robotResult,
										starIterator, robotListener);
							} else {
								LOGGER.info("明星{}解析出来的贴吧名称为空是{}",
										star.getName(), tiebaName);
								grabData(task, browser, star, robotResult,
										starIterator, robotListener);
								return;
							}
						} catch (Exception e) {
							LOGGER.error("解析贴吧失败，重新解析", e);
							parse(task, browser, star, robotResult, starIterator, robotListener);
							return;
						}
					}
				});
			}
		};

		browser.addProgressListener(addTiebaNameListener);
		browser.setUrl(star.getTiebaUrl());
	}
}
