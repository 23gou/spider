package com.spider.robot;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
 * 描述:微博机器人
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月10日 上午9:41:01
 */
@Component
public class WeiboRobot extends DefaultRobot {
	@Autowired
	private StarMng starMng;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(WeiboRobot.class);
	@Autowired
	private RobotResultMng robotResultMng;
	/**
	 * 最多取7条微博
	 */
	private static final int MAX_WEIBO = 7;

	@Override
	public void grabData(final Task task, final Browser browser,
			final Star star, final RobotResult robotResult,
			final Iterator<Star> starIterator, final RobotListener robotListener) {

		// 处理微博关键字
		final ProgressAdapter addWeiboNameListener = new ProgressAdapter() {
			public void completed(ProgressEvent event) {
				browser.removeProgressListener(this);
				LOGGER.info("addWeiboNameListener");
				// 登录模块
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
			 * @param text
			 * @author liyixing 2015年9月18日 下午1:29:53
			 */

			private void parse(final Task task, final Browser browser,
					final Star star, final RobotResult robotResult,
					final Iterator<Star> starIterator,
					final RobotListener robotListener) {

				Display.getDefault().timerExec((int) 1000, new Runnable() {
					public void run() {
						LOGGER.info("parse");
						Browser browser1 = browser;
						browser1.getUrl();
						String text = browser.getText();
						if (text.indexOf("<!-- 登陆模块 -->") >= 0) {
							parse(task, browser, star, robotResult,
									starIterator, robotListener);

							LOGGER.info("<!-- 登陆模块 -->");
							return;
						}

						if (text.indexOf("你所在的帐号、IP或应用由于违反了新浪微博的安全检测规则，暂时无法完成此操作，正确输入验证码答案即可正常访问。") >= 0) {
							parse(task, browser, star, robotResult,
									starIterator, robotListener);
							LOGGER.info("你所在的帐号、IP或应用由于违反了新浪微博的安全检测规则，暂时无法完成此操作，正确输入验证码答案即可正常访问。");
							return;
						}

						// 解析微博名称
						// text.substring(text.indexOf("forward_btn_text") -
						// 30);

						// String select = "title";
						String weiboName = PageParase.parseTextWithPatternHtml(
								text, "<TITLE>(\\S{0,})的微博_微博<");

						if (StringUtils.isBlank(weiboName)) {
							weiboName = PageParase.parseTextWithPatternHtml(
									text, "<title>(\\S{0,})的微博_微博<");
						}

						star.setWeiboName(weiboName);
						LOGGER.info("明星{}解析出来的微博名称是{}", star.getName(),
								weiboName);
						starMng.save(star);

						if (text.indexOf("他还没有发过微博") > 0) {
							LOGGER.info("明星{}没有发过微博", star.getName());

							robotResult.setWeiboComment(0);
							robotResult.setWeiboFan(0);
							robotResult.setWeiboFanInc(0);
							robotResult.setWeiboForward(0);
							robotResult.setWeiboLinkStatus(0);
							next(task, browser, star, robotResult,
									starIterator, robotListener);
							return;
						}

						if (text.indexOf("她还没有发过微博") > 0) {
							LOGGER.info("明星{}没有发过微博", star.getName());

							robotResult.setWeiboComment(0);
							robotResult.setWeiboFan(0);
							robotResult.setWeiboFanInc(0);
							robotResult.setWeiboForward(0);
							robotResult.setWeiboLinkStatus(0);
							next(task, browser, star, robotResult,
									starIterator, robotListener);
							return;
						}

						// LOGGER.info(text);

						// List<String> forwards = PageParase
						// .parseTextWithPattern(
						// text,
						// "<span class=\\\\\"line S_line1\\\\\" node-type=\\\\\"forward_btn_text\\\\\">转发\\s{0,}(\\d{0,})<\\\\");
						//
						// if (forwards.size() == 0) {
						// forwards = PageParase
						// .parseTextWithPattern(
						// text,
						// "<span node-type=\"forward_btn_text\" class=\"line S_line1\">转发\\s{0,}(\\d{0,})</span>");
						// }
						// if (forwards.size() == 0) {
						// forwards = PageParase
						// .parseTextWithPattern(
						// text,
						// "<span node-type=\"forward_btn_text\" class=\"line S_line1\">转发\\s{0,}(\\d{0,})</span>");
						// }
						//
						// if (forwards.size() == 0) {
						// forwards = PageParase.parseTextWithPattern(text,
						// "转发\\s{0,}(\\d{0,})\\s{0,}</span>");
						// }
						//
						// if (forwards.size() == 0) {
						// forwards = PageParase.parseTextWithPattern(text,
						// "转发\\s{0,}(\\d{0,})\\s{0,}&lt;/span");
						// }
						List<Map<String, Object>> rvs = null;
						try {
							String vs = browser1
									.evaluate(
											"var $_=function (p,e,a,av){var es = [];var esi = 0;var divs = p.getElementsByTagName(e); for(var i = 0; i < divs.length; i++){var div = divs[i]; if(div.getAttribute(a)==av){es[esi++]=div;}}return es};"
													+ "var eitems = $_(document,'div','class','WB_feed_expand');"
													+ "for(var i = 0;i<eitems.length;i++){"
													+ "eitems[i].innerHTML='';"
													+ "}"
													+ "var items = $_(document,'div','action-type','feed_list_item');"
													+ "var r = '{\"data\":[';"
													+ "for(var i = 0; i<items.length&&i<8;i++) {"
													+ "var date = $_(items[i],'a','node-type','feed_list_item_date');"
													+ "var feed_list_options = $_(items[i],'div','node-type','feed_list_options')[0];"
													+ "var forward = $_(feed_list_options,'span','node-type','forward_btn_text');"
													+ "var comment = $_(feed_list_options,'span','node-type','comment_btn_text');"
													+ "var like = $_(feed_list_options,'span','node-type','like_status')[0].getElementsByTagName('em');"
													+ "if(i!==0) {"
													+ "	r=r+',';"
													+ "}"
													+ "r=r+'{\"date\":\"'+date[0].innerHTML+'\",\"forward\":\"'+forward[0].innerHTML+'\",\"comment\":\"'+comment[0].innerHTML+'\",\"like\":\"'+like[0].innerHTML+'\"'+'}'"
													+ "}"
													+ "r=r+']}';"
													+ "return r;").toString();
							ObjectMapper objectMapper = new ObjectMapper();

							Map<String, Object> r = objectMapper.readValue(vs,
									Map.class);
							rvs = (List<Map<String, Object>>) r.get("data");

							if (rvs.size() == 0 && text.indexOf("他还没有发过微博") < 0) {
								parse(task, browser, star, robotResult,
										starIterator, robotListener);
								LOGGER.info("无法解析出明星微博数据，重新解析");
								return;
							}

							if (rvs.size() == 0 && text.indexOf("她还没有发过微博") < 0) {
								parse(task, browser, star, robotResult,
										starIterator, robotListener);
								LOGGER.info("无法解析出明星微博数据，重新解析");
								return;
							}

							LOGGER.info("明星{}js解析微博信息出来的微博数是：{}", new Object[] {
									star.getName(), rvs.size() });
						} catch (Exception e) {
							// 解析微博数据出错
							LOGGER.info("解析微博数据出错，重新开始解析", e);
							parse(task, browser, star, robotResult,
									starIterator, robotListener);
							return;
						}

						// if (forwards.size() == 0
						// && text.indexOf("他还没有发过微博") < 0) {
						// parse(task, browser, star, robotResult,
						// starIterator, robotListener);
						// LOGGER.info("无法解析出明星微博数据，重新解析");
						// return;
						// }
						//
						// if (forwards.size() == 0
						// && text.indexOf("她还没有发过微博") < 0) {
						// parse(task, browser, star, robotResult,
						// starIterator, robotListener);
						// LOGGER.info("无法解析出明星微博数据，重新解析");
						// return;
						// }

						// if(forwards.size()==0) {
						// LOGGER.info("无法解析出明星{}微博数据，因为不存在转发数据",star.getName());
						//
						// return;
						// }

						// Document document = Jsoup.parse(browser.getText());
						// star.setWeiboName(PageParase.parseHtml(document,
						// select).trim()
						// .replace("的微博_微博", ""));
						// Document scriptDocument = Jsoup.parse(document
						// .getElementsByTag("script").get(37).html());

						// Elements elementsForward = document
						// .select("span[node-type=\"forward_btn_text\"]");

						if (StringUtils.isNotBlank(weiboName)) {
							star.setWeiboName(weiboName);
							LOGGER.info("明星{}解析出来的微博名称是{}", star.getName(),
									weiboName);
							starMng.save(star);

							parseArticle(star, robotResult, text, rvs, browser);
							parseWeiboFan(task, star, robotResult, text);

							next(task, browser, star, robotResult,
									starIterator, robotListener);
						} else {
							LOGGER.info("明星{}解析出来的微博名称为空{}", star.getName(),
									weiboName);
							next(task, browser, star, robotResult,
									starIterator, robotListener);
						}
					}
				});
			}
		};

		if (org.apache.commons.lang3.StringUtils.isBlank(star.getWeiboUrl())) {
			LOGGER.info("明星{}微博地址无效", star.getName());

			robotResult.setWeiboComment(0);
			robotResult.setWeiboFan(0);
			robotResult.setWeiboFanInc(0);
			robotResult.setWeiboForward(0);
			robotResult.setWeiboLinkStatus(0);
			next(task, browser, star, robotResult, starIterator, robotListener);
			return;
		}

		if ("#".equals(star.getWeiboUrl().trim())) {
			LOGGER.info("明星{}微博地址无效，是#", star.getName());
			robotResult.setWeiboComment(0);
			robotResult.setWeiboFan(0);
			robotResult.setWeiboFanInc(0);
			robotResult.setWeiboForward(0);
			robotResult.setWeiboLinkStatus(0);
			next(task, browser, star, robotResult, starIterator, robotListener);
			return;
		}

		browser.addProgressListener(addWeiboNameListener);
		browser.setUrl(star.getWeiboUrl());
	}

	/**
	 * 描述:微博粉丝数
	 * 
	 * @param robotResult
	 * @param text
	 * @author liyixing 2015年9月11日 上午11:24:03
	 */
	private void parseWeiboFan(Task task, Star star,
			final RobotResult robotResult, String text) {
		// 微博粉丝数
		String weiboFan = PageParase
				.parseTextWithPatternHtml(text,
						">(\\d{0,})<\\\\/strong><span class=\\\\\"S_txt2\\\\\">粉丝<\\\\/span>");

		if (StringUtils.isBlank(weiboFan)) {
			weiboFan = PageParase.parseTextWithPatternHtml(text,
					">(\\d{0,})</strong><span class=\"S_txt2\">粉丝</span>");
		}

		if (StringUtils.isBlank(weiboFan)) {
			weiboFan = PageParase
					.parseTextWithPatternHtml(
							text,
							"&gt;(\\d{0,})&lt;\\\\/strong&gt;&lt;span class=\\\\\"S_txt2\\\\\"&gt;粉丝&lt;\\\\/span&gt;");
		}
		if (StringUtils.isBlank(weiboFan)) {
			weiboFan = PageParase
					.parseTextWithPatternHtml(text,
							"&gt;(\\d{0,})&lt;/strong&gt;&lt;span class=\"S_txt2\"&gt;粉丝&lt;/span&gt;");
		}

		if (StringUtils.isNotBlank(weiboFan)) {
			robotResult.setWeiboFan(Integer.valueOf(weiboFan.trim()));
			LOGGER.info("明星{}解析出来的微博粉丝数是{}", star.getName(),
					robotResult.getWeiboFan());
			robotResult.setWeiboFanInc(robotResult.getWeiboFan());

			// 处理增长
			if (task.getContrastTaskId() != null) {
				RobotResult robotResultTemp = new RobotResult();

				robotResultTemp.setTaskId(task.getContrastTaskId());
				robotResultTemp.setStarId(star.getId());

				RobotResult contrastRobotResult = robotResultMng
						.getByTaskAndStar(robotResultTemp);

				if (contrastRobotResult != null) {
					robotResult.setWeiboFanInc(robotResult.getWeiboFan()
							- contrastRobotResult.getWeiboFan());
				}
			}
		}
	}

	/**
	 * 描述:解析微博文章
	 * 
	 * @param robotResult
	 * @param text
	 * @param forwards
	 * @author liyixing 2015年9月11日 上午11:19:57
	 */
	private void parseArticle(Star star, final RobotResult robotResult,
			String text, List<Map<String, Object>> rvs, Browser browser1) {

		// 最近7条微博转发,评论,点赞数平均数
		robotResult.setWeiboComment(0);
		robotResult.setWeiboForward(0);
		robotResult.setWeiboLinkStatus(0);

		// 时间排序
		Collections.sort(rvs, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				Date date1 = null;
				Date date2 = null;

				try {
					// date1 = UtilDateTime.parse("今天 07:58", "今天 HH:mm");

					date1 = UtilDateTime.parse(
							o1.get("date").toString().trim(), "mm分钟前");
					Calendar datec1 = UtilDateTime.getCalendar(date1);
					datec1.set(Calendar.YEAR,
							Calendar.getInstance().get(Calendar.YEAR));
					datec1.set(Calendar.MONTH,
							Calendar.getInstance().get(Calendar.MONTH));
					datec1.set(Calendar.DAY_OF_MONTH, Calendar.getInstance()
							.get(Calendar.DAY_OF_MONTH));
					datec1.set(Calendar.HOUR_OF_DAY, Calendar.getInstance()
							.get(Calendar.HOUR_OF_DAY));
					// N分钟前
					datec1.set(Calendar.MINUTE,
							Calendar.getInstance().get(Calendar.MINUTE)
									- datec1.get(Calendar.MINUTE));
					date1 = datec1.getTime();
				} catch (ParseException e) {
					LOGGER.info("格式mm分钟前失败，{}", o1.get("date").toString()
							.trim());
				}

				try {
					date2 = UtilDateTime.parse(
							o2.get("date").toString().trim(), "mm分钟前");
					Calendar datec2 = UtilDateTime.getCalendar(date2);
					datec2.set(Calendar.YEAR,
							Calendar.getInstance().get(Calendar.YEAR));
					datec2.set(Calendar.MONTH,
							Calendar.getInstance().get(Calendar.MONTH));
					datec2.set(Calendar.DAY_OF_MONTH, Calendar.getInstance()
							.get(Calendar.DAY_OF_MONTH));
					datec2.set(Calendar.HOUR_OF_DAY, Calendar.getInstance()
							.get(Calendar.HOUR_OF_DAY));
					// N分钟前
					datec2.set(Calendar.MINUTE,
							Calendar.getInstance().get(Calendar.MINUTE)
									- datec2.get(Calendar.MINUTE));
					date2 = datec2.getTime();
				} catch (ParseException e) {
					LOGGER.info("格式mm分钟前失败，{}", o2.get("date").toString()
							.trim());
				}

				if (date1 == null) {
					try {
						// date = UtilDateTime.parse("今天 07:58", "今天 HH:mm");

						date1 = UtilDateTime.parse(o1.get("date").toString()
								.trim(), "今天 HH:mm");
						Calendar datec1 = UtilDateTime.getCalendar(date1);
						datec1.set(Calendar.YEAR,
								Calendar.getInstance().get(Calendar.YEAR));
						datec1.set(Calendar.MONTH,
								Calendar.getInstance().get(Calendar.MONTH));
						datec1.set(Calendar.DAY_OF_MONTH, Calendar
								.getInstance().get(Calendar.DAY_OF_MONTH));
						date1 = datec1.getTime();
					} catch (ParseException e) {
						LOGGER.info("格式今天 HH:mm失败，{}", o1.get("date")
								.toString().trim());
					}
				}

				if (date2 == null) {
					try {
						date2 = UtilDateTime.parse(o2.get("date").toString()
								.trim(), "今天 HH:mm");
						Calendar datec2 = UtilDateTime.getCalendar(date2);
						datec2.set(Calendar.YEAR,
								Calendar.getInstance().get(Calendar.YEAR));
						datec2.set(Calendar.MONTH,
								Calendar.getInstance().get(Calendar.MONTH));
						datec2.set(Calendar.DAY_OF_MONTH, Calendar
								.getInstance().get(Calendar.DAY_OF_MONTH));
						date2 = datec2.getTime();
					} catch (ParseException e) {
						LOGGER.info("格式今天 HH:mm失败，{}", o2.get("date")
								.toString().trim());
					}
				}

				if (date1 == null) {
					// 月日格式
					try {
						// date1 = UtilDateTime.parse("8月4日 09:06",
						// "MM月dd日 HH:mm");

						date1 = UtilDateTime.parse(o1.get("date").toString()
								.trim(), "MM月dd日 HH:mm");
						Calendar datec1 = UtilDateTime.getCalendar(date1);
						datec1.set(Calendar.YEAR,
								Calendar.getInstance().get(Calendar.YEAR));
						date1 = datec1.getTime();
					} catch (ParseException e) {
						LOGGER.info("格式MM月dd日失败，{}", o1.get("date").toString()
								.trim());
					}
				}
				if (date2 == null) {
					try {
						date2 = UtilDateTime.parse(o2.get("date").toString()
								.trim(), "MM月dd日 HH:mm");
						Calendar datec2 = UtilDateTime.getCalendar(date2);
						datec2.set(Calendar.YEAR,
								Calendar.getInstance().get(Calendar.YEAR));
						date2 = datec2.getTime();
					} catch (ParseException e) {
						LOGGER.info("格式MM月dd日失败，{}", o2.get("date").toString()
								.trim());
					}
				}
				// 年月日
				if (date1 == null) {
					try {
						date1 = UtilDateTime.parse(o1.get("date").toString()
								.trim(), "yyyy-MM-dd HH:mm");
					} catch (ParseException e) {
						LOGGER.info("格式yyyy-MM-dd失败，{}", o1.get("date")
								.toString().trim());
					}
				}

				if (date2 == null) {
					try {
						date2 = UtilDateTime.parse(o2.get("date").toString()
								.trim(), "yyyy-MM-dd HH:mm");
					} catch (ParseException e) {
						LOGGER.info("格式yyyy-MM-dd失败，{}", o2.get("date")
								.toString().trim());
					}
				}
				long l = date2.getTime() - date1.getTime();

				if (l > 0) {
					return 1;
				}

				if (l < 0) {
					return -1;
				}

				return 0;
			}
		});

		int n = 0;
		// 只取前面7条记录
		for (int i = 0; i < 7 && i < rvs.size(); i++) {
			n++;
			String forward = rvs.get(i).get("forward").toString()
					.replace("转发", "").trim();
			String comment = rvs.get(i).get("comment").toString()
					.replace("评论", "").trim();
			String like = rvs.get(i).get("like").toString().trim();

			// 转发数
			if (StringUtils.isBlank(forward)) {
				forward = "0";
			}

			robotResult.setWeiboForward(robotResult.getWeiboForward()
					+ Integer.valueOf(forward));
			// 评论数
			if (StringUtils.isBlank(comment)) {
				comment = "0";
			}
			robotResult.setWeiboComment(robotResult.getWeiboComment()
					+ Integer.valueOf(comment));

			// 点赞数
			if (StringUtils.isBlank(like)) {
				like = "0";
			}
			robotResult.setWeiboLinkStatus(robotResult.getWeiboLinkStatus()
					+ Integer.valueOf(like));
		}

		if (n != 0) {
			robotResult.setWeiboComment(BigDecimal
					.valueOf(robotResult.getWeiboComment())
					.divide(BigDecimal.valueOf(n), 0, BigDecimal.ROUND_UP)
					.intValue());
			robotResult.setWeiboForward(BigDecimal
					.valueOf(robotResult.getWeiboForward())
					.divide(BigDecimal.valueOf(n), 0, BigDecimal.ROUND_UP)
					.intValue());
			robotResult.setWeiboLinkStatus(BigDecimal
					.valueOf(robotResult.getWeiboLinkStatus())
					.divide(BigDecimal.valueOf(n), 0, BigDecimal.ROUND_UP)
					.intValue());
		}

		// try {
		// String vs = browser1
		// .evaluate(
		// "var $_=function (p,e,a,av){var es = [];var esi = 0;var divs = p.getElementsByTagName(e); for(var i = 0; i < divs.length; i++){var div = divs[i]; if(div.getAttribute(a)==av){es[esi++]=div;}}return es};"
		// + "var items = $_(document,'div','action-type','feed_list_item');"
		// + "var r = \"{'data':[\";"
		// + "for(var i = 0; i<items.length&&i<8;i++) {"
		// + "var date = $_(items[i],'a','node-type','feed_list_item_date');"
		// +
		// "var feed_list_options = $_(items[i],'div','node-type','feed_list_options')[0];"
		// +
		// "var forward = $_(feed_list_options,'span','node-type','forward_btn_text');"
		// +
		// "var comment = $_(feed_list_options,'span','node-type','comment_btn_text');"
		// +
		// "var like = $_(feed_list_options,'span','node-type','like_status')[0].getElementsByTagName('em');"
		// + "if(i!==0) {"
		// + "	r=r+',';"
		// + "}"
		// +
		// "r=r+\"{'date':'\"+date[0].innerHTML+\"','forward':'\"+forward[0].innerHTML+\"','comment':'\"+comment[0].innerHTML+\"','like':'\"+like[0].innerHTML+\"'\"+\"}\""
		// + "}" + "r=r+\"]}\";" + "return r;")
		// .toString();
		// ObjectMapper objectMapper = new ObjectMapper();
		//
		// Map<String, Object> r = objectMapper.readValue(vs, Map.class);
		// List<Map<String, Object>> rvs = (List<Map<String, Object>>) r
		// .get("data");
		// LOGGER.info("明星{}js解析微博信息出来的微博数是：{}", new Object[] {
		// star.getName(), rvs.size() });
		// } catch (Exception e) {
		// LOGGER.info("明星{}js解析微博信息出错，使用正则模式",
		// new Object[] { star.getName() }, e);
		// int n = MAX_WEIBO;
		// List<String> comments = PageParase
		// .parseTextWithPattern(
		// text,
		// "<span class=\\\\\"pos\\\\\"><span class=\\\\\"line S_line1\\\\\" node-type=\\\\\"comment_btn_text\\\\\">评论\\s{0,}(\\d{0,})\\s{0,}<\\\\");
		//
		// if (comments.size() == 0) {
		// comments = PageParase
		// .parseTextWithPattern(
		// text,
		// "<span node-type=\"comment_btn_text\" class=\"line S_line1\">评论\\s{0,}(\\d{0,})\\s{0,}</span>");
		//
		// }
		//
		// if (comments.size() == 0) {
		// comments = PageParase.parseTextWithPattern(text,
		// "评论\\s{0,}(\\d{0,})");
		//
		// }
		// List<String> linkStatus = PageParase
		// .parseTextWithPattern(
		// text,
		// "<span class=\\\\\"line S_line1\\\\\"><span node-type=\\\\\"like_status\\\\\"><i class=\\\\\"W_icon icon_praised_b\\\\\"><\\\\/i>\\s{0,}<em>\\s{0,}(\\d{0,})\\s{0,}<\\\\/em>");
		//
		// if (linkStatus.size() == 0) {
		// linkStatus = PageParase
		// .parseTextWithPattern(
		// text,
		// "<span class=\"line S_line1\"><span node-type=\"like_status\"><i class=\"W_icon icon_praised_b\"></i>\\s{0,}<em>\\s{0,}(\\d{0,})\\s{0,}</em>");
		//
		// }
		// if (linkStatus.size() == 0) {
		// linkStatus = PageParase.parseTextWithPattern(text,
		// "&lt;em&gt;(\\d{0,})&lt;\\\\/em&gt;&lt;\\\\/span");
		//
		// }
		//
		// if (n > forwards.size()) {
		// n = forwards.size();
		// }
		//
		// for (int i = 0; i < n; i++) {
		// // 转发数
		// if (StringUtils.isBlank(forwards.get(i))) {
		// forwards.set(i, "0");
		// }
		//
		// robotResult.setWeiboForward(robotResult.getWeiboForward()
		// + Integer.valueOf(forwards.get(i).trim()));
		// if (comments.size() <= i) {
		// comments.add("0");
		// }
		// // 评论数
		// if (StringUtils.isBlank(comments.get(i))) {
		// comments.set(i, "0");
		// }
		// robotResult.setWeiboComment(robotResult.getWeiboComment()
		// + Integer.valueOf(comments.get(i).trim()));
		//
		// if (linkStatus.size() <= i) {
		// linkStatus.add("0");
		// }
		// // 点赞数
		// if (StringUtils.isBlank(linkStatus.get(i))) {
		// linkStatus.set(i, "0");
		// }
		// robotResult.setWeiboLinkStatus(robotResult.getWeiboLinkStatus()
		// + Integer.valueOf(linkStatus.get(i).trim()));
		// }
		//
		// if (n != 0) {
		// robotResult.setWeiboComment(BigDecimal
		// .valueOf(robotResult.getWeiboComment())
		// .divide(BigDecimal.valueOf(n), 0, BigDecimal.ROUND_UP)
		// .intValue());
		// robotResult.setWeiboForward(BigDecimal
		// .valueOf(robotResult.getWeiboForward())
		// .divide(BigDecimal.valueOf(n), 0, BigDecimal.ROUND_UP)
		// .intValue());
		// robotResult.setWeiboLinkStatus(BigDecimal
		// .valueOf(robotResult.getWeiboLinkStatus())
		// .divide(BigDecimal.valueOf(n), 0, BigDecimal.ROUND_UP)
		// .intValue());
		// }
		// }

		LOGGER.info("明星{}解析出来的微博转发{}，评论{}，点赞{}", new Object[] { star.getName(),
				robotResult.getWeiboForward(), robotResult.getWeiboComment(),
				robotResult.getWeiboLinkStatus() });
	}
}
