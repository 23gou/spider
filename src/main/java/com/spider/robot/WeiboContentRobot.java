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
 * 描述:微博转评赞机器人
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月10日 上午9:41:01
 */
@Component
public class WeiboContentRobot extends DefaultRobot {
	@Autowired
	private StarMng starMng;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(WeiboContentRobot.class);
	@Autowired
	private RobotResultMng robotResultMng;

	public WeiboContentRobot() {
		setName("微博转评赞");
	}

	public boolean validateDimensionZero(
			final Map<String, List<String>> options, Task task,
			Browser browser, Star star, RobotResult robotResult,
			Iterator<Star> starIterator, RobotListener robotListener) {
		return robotResult.getWeiboComment() > 0
				&& robotResult.getWeiboForward() > 0
				&& robotResult.getWeiboLinkStatus() > 0;
	}

	@Override
	public void grabData(final Map<String, List<String>> options,
			final Task task, final Browser browser, final Star star,
			final RobotResult robotResult, final Iterator<Star> starIterator,
			final RobotListener robotListener) {

		// 处理微博关键字
		final ProgressAdapter addWeiboNameListener = new ProgressAdapter() {
			public void completed(ProgressEvent event) {
				browser.removeProgressListener(this);
				LOGGER.info("addWeiboNameListener");
				// 登录模块
				parse(options, task, browser, star, robotResult, starIterator,
						robotListener);
			}

			/**
			 * 描述:解析
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

			private void parse(final Map<String, List<String>> options,
					final Task task, final Browser browser, final Star star,
					final RobotResult robotResult,
					final Iterator<Star> starIterator,
					final RobotListener robotListener) {

				Display.getDefault().timerExec((int) 1000, new Runnable() {
					@SuppressWarnings("unchecked")
					public void run() {
						String text = browser.getText();
						LOGGER.info("parse:" + text);
						if (!validateLoginAndTimes(options, task, browser,
								star, robotResult, starIterator, robotListener,
								text)) {
							return;
						}

						String weiboName = parseWeiboName(text);

						star.setWeiboName(weiboName);
						LOGGER.info("明星{}解析出来的微博名称是{}", star.getName(),
								weiboName);

						if (StringUtils.isBlank(weiboName)) {
							LOGGER.info("明星{}解析出来的微博名称为空{}，等待重新解析",
									star.getName(), weiboName);
							parse(options, task, browser, star, robotResult,
									starIterator, robotListener);
							return;
						}

						starMng.save(star);

						if (text.indexOf("他还没有发过微博") > 0
								|| text.indexOf("她还没有发过微博") > 0) {
							LOGGER.info("明星{}没有发过微博，需要重新抓取", star.getName());

							robotResult.setWeiboComment(0);
							robotResult.setWeiboForward(0);
							robotResult.setWeiboLinkStatus(0);
							grabData(options, task, browser, star, robotResult,
									starIterator, robotListener);
							return;
						}

						List<Map<String, Object>> rvs = null;
						try {
							String js = 
									"var $_=function (p,e,a,av){var es = [];var esi = 0;var divs = p.getElementsByTagName(e); for(var i = 0; i < divs.length; i++){var div = divs[i]; if(div.getAttribute(a)==av){es[esi++]=div;}}return es};"
									// 清理转发的微博内容
									+ "var eitems = $_(document,'div','class','WB_feed_expand');"
									+ "for(var i = 0;i<eitems.length;i++){"
									+ "eitems[i].innerHTML='';"
									+ "}"
									// 获取自己发的微博内容
									+ "var items = $_(document,'div','action-type','feed_list_item');"
									+ "var r = '{\"data\":[';"
									+ "var x = 0;"
									+ "for(var i = 0; i<items.length;i++) {"
									//清楚点赞过的内容
									+ "var maintitle = $_(items[i],'span','class','main_title');if(maintitle.length){continue;}"
									+ "x++;"
									+ "var date = $_(items[i],'a','node-type','feed_list_item_date');"
									+ "var feed_list_options = $_(items[i],'div','node-type','feed_list_options')[0];"
									+ "var forward = $_(feed_list_options,'span','node-type','forward_btn_text');"
									+ "var comment = $_(feed_list_options,'span','node-type','comment_btn_text');"
									+ "var like = $_(feed_list_options,'span','node-type','like_status')[0].getElementsByTagName('em');"
									+ "if(i!==0) {"
									+ "	r=r+',';"
									+ "}"
									+ "r=r+'{\"date\":\"'+date[0].innerHTML+'\",\"forward\":\"'+forward[0].innerHTML+'\",\"comment\":\"'+comment[0].innerHTML+'\",\"like\":\"'+like[1].innerHTML+'\"'+'}';"
									+ "if(x>=7){break;}"
									+ "}"
									+ "r=r+']}';"
									+ "return r;";
							String vs = browser
									.evaluate(js).toString();
							vs = vs.replace("class=\"W_ficon ficon_forward",
									"class='W_ficon ficon_forward").replace(
									"class=\"W_ficon ficon_repeat",
									"class='W_ficon ficon_repeat");
							vs = vs.replace("S_ficon\"", "S_ficon'");
							ObjectMapper objectMapper = new ObjectMapper();

							Map<String, Object> r = objectMapper.readValue(vs,
									Map.class);
							rvs = (List<Map<String, Object>>) r.get("data");

							if (rvs.size() == 0 && text.indexOf("他还没有发过微博") < 0) {
								parse(options, task, browser, star,
										robotResult, starIterator,
										robotListener);
								LOGGER.info("无法解析出明星微博数据，重新解析");
								return;
							}

							if (rvs.size() == 0 && text.indexOf("她还没有发过微博") < 0) {
								parse(options, task, browser, star,
										robotResult, starIterator,
										robotListener);
								LOGGER.info("无法解析出明星微博数据，重新解析");
								return;
							}

							LOGGER.info("明星{}js解析微博信息出来的微博数是：{}", new Object[] {
									star.getName(), rvs.size() });
						} catch (Exception e) {
							// 解析微博数据出错
							LOGGER.info("解析微博数据出错，重新开始解析", e);
							parse(options, task, browser, star, robotResult,
									starIterator, robotListener);
							return;
						}

						parseArticle(star, robotResult, text, rvs, browser);
						next(options, task, browser, star, robotResult,
								starIterator, robotListener);
					}

					/**
					 * 
					 * 描述:验证是否登录，或者是否访问受限
					 * 
					 * @param options
					 * @param task
					 * @param browser
					 * @param star
					 * @param robotResult
					 * @param starIterator
					 * @param robotListener
					 * @param text
					 * @return
					 * @author liyixing 2016年5月7日 上午9:39:25
					 */
					private boolean validateLoginAndTimes(
							final Map<String, List<String>> options,
							final Task task, final Browser browser,
							final Star star, final RobotResult robotResult,
							final Iterator<Star> starIterator,
							final RobotListener robotListener, String text) {
						if (text.indexOf("<!-- 登陆模块 -->") >= 0) {
							parse(options, task, browser, star, robotResult,
									starIterator, robotListener);

							LOGGER.info("<!-- 登陆模块 -->");
							return false;
						}

						if (text.indexOf("你所在的帐号、IP或应用由于违反了新浪微博的安全检测规则，暂时无法完成此操作，正确输入验证码答案即可正常访问。") >= 0) {
							parse(options, task, browser, star, robotResult,
									starIterator, robotListener);
							LOGGER.info("你所在的帐号、IP或应用由于违反了新浪微博的安全检测规则，暂时无法完成此操作，正确输入验证码答案即可正常访问。");
							return false;
						}

						return true;
					}

					private String parseWeiboName(String text) {
						String weiboName = PageParase.parseTextWithPatternHtml(
								text, "<TITLE>(\\S{0,})的微博_微博<");

						if (StringUtils.isBlank(weiboName)) {
							weiboName = PageParase.parseTextWithPatternHtml(
									text, "<title>(\\S{0,})的微博_微博<");
						}
						return weiboName;
					}
				});
			}
		};

		if (org.apache.commons.lang3.StringUtils.isBlank(star.getWeiboUrl())) {
			LOGGER.info("明星{}微博地址无效", star.getName());

			robotResult.setWeiboComment(0);
			robotResult.setWeiboForward(0);
			robotResult.setWeiboLinkStatus(0);
			next(options, task, browser, star, robotResult, starIterator,
					robotListener);
			return;
		}

		if ("#".equals(star.getWeiboUrl().trim())) {
			LOGGER.info("明星{}微博地址无效，是#", star.getName());
			robotResult.setWeiboComment(0);
			robotResult.setWeiboForward(0);
			robotResult.setWeiboLinkStatus(0);
			next(options, task, browser, star, robotResult, starIterator,
					robotListener);
			return;
		}

		browser.addProgressListener(addWeiboNameListener);
		browser.setUrl(star.getWeiboUrl());
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
		sort(rvs);

		int n = 0;
		// 只取前面7条记录
		for (int i = 0; i < 7 && i < rvs.size(); i++) {
			n++;
			String forward = rvs.get(i).get("forward").toString()
					.replace("转发", "").trim();
			String comment = rvs.get(i).get("comment").toString()
					.replace("评论", "").trim();
			String like = rvs.get(i).get("like").toString().trim();

			forward = PageParase.parseTextWithPatternHtml(forward, "(\\d+)");

			// 转发数
			if (StringUtils.isBlank(forward)) {
				forward = "0";
			}

			robotResult.setWeiboForward(robotResult.getWeiboForward()
					+ Integer.valueOf(forward));

			comment = PageParase.parseTextWithPatternHtml(comment, "(\\d+)");

			// 评论数
			if (StringUtils.isBlank(comment)) {
				comment = "0";
			}
			robotResult.setWeiboComment(robotResult.getWeiboComment()
					+ Integer.valueOf(comment));

			like = PageParase.parseTextWithPatternHtml(like, "(\\d+)");
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

		LOGGER.info("明星{}解析出来的微博转发{}，评论{}，点赞{}", new Object[] { star.getName(),
				robotResult.getWeiboForward(), robotResult.getWeiboComment(),
				robotResult.getWeiboLinkStatus() });
	}

	/**
	 * 
	 * 描述:将微博数据，按照时间排序
	 * 
	 * @param rvs
	 * @author liyixing 2016年5月7日 上午10:01:20
	 */
	private void sort(List<Map<String, Object>> rvs) {
		Collections.sort(rvs, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				Date date1 = null;
				Date date2 = null;

				try {
					date1 = UtilDateTime.parse(
							o1.get("date").toString().trim(), "ss秒前");
					Calendar datec1 = UtilDateTime.getCalendar(date1);
					datec1.set(Calendar.YEAR,
							Calendar.getInstance().get(Calendar.YEAR));
					datec1.set(Calendar.MONTH,
							Calendar.getInstance().get(Calendar.MONTH));
					datec1.set(Calendar.DAY_OF_MONTH, Calendar.getInstance()
							.get(Calendar.DAY_OF_MONTH));
					datec1.set(Calendar.HOUR_OF_DAY, Calendar.getInstance()
							.get(Calendar.HOUR_OF_DAY));
					datec1.set(Calendar.MINUTE,
							Calendar.getInstance().get(Calendar.MINUTE));
					// N秒前
					datec1.set(Calendar.SECOND,
							Calendar.getInstance().get(Calendar.SECOND)
									- datec1.get(Calendar.SECOND));
					date1 = datec1.getTime();
				} catch (ParseException e) {
					LOGGER.info("格式ss秒钟前失败，{}", o1.get("date").toString()
							.trim());
				}

				try {
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
							o2.get("date").toString().trim(), "ss秒前");
					Calendar datec2 = UtilDateTime.getCalendar(date2);
					datec2.set(Calendar.YEAR,
							Calendar.getInstance().get(Calendar.YEAR));
					datec2.set(Calendar.MONTH,
							Calendar.getInstance().get(Calendar.MONTH));
					datec2.set(Calendar.DAY_OF_MONTH, Calendar.getInstance()
							.get(Calendar.DAY_OF_MONTH));
					datec2.set(Calendar.HOUR_OF_DAY, Calendar.getInstance()
							.get(Calendar.HOUR_OF_DAY));
					datec2.set(Calendar.MINUTE,
							Calendar.getInstance().get(Calendar.MINUTE));
					// N秒前
					datec2.set(Calendar.SECOND,
							Calendar.getInstance().get(Calendar.SECOND)
									- datec2.get(Calendar.SECOND));
					date2 = datec2.getTime();
				} catch (ParseException e) {
					LOGGER.info("格式ss秒钟前失败，{}", o2.get("date").toString()
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
	}
}
