package com.spider.robot;

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
public class WeiboFansRobot extends DefaultRobot {
	@Autowired
	private StarMng starMng;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(WeiboFansRobot.class);
	@Autowired
	private RobotResultMng robotResultMng;

	public WeiboFansRobot() {
		setName("微博粉丝增长");
	}

	@Override
	public void grabData(Map<String, List<String>> options, final Task task,
			final Browser browser, final Star star,
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

			private void parse(Map<String, List<String>> options,
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
							LOGGER.info("明星{}没有发过微博", star.getName());

							robotResult.setWeiboComment(0);
							robotResult.setWeiboFan(0);
							robotResult.setWeiboFanInc(0);
							robotResult.setWeiboForward(0);
							robotResult.setWeiboLinkStatus(0);
							next(options, task, browser, star, robotResult,
									starIterator, robotListener);
							return;
						}

						List<Map<String, Object>> rvs = null;
						try {
							String vs = browser
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

						if (StringUtils.isNotBlank(weiboName)) {
							parseWeiboFan(task, star, robotResult, text);
							next(options, task, browser, star, robotResult,
									starIterator, robotListener);
						} else {
							LOGGER.info("明星{}解析出来的微博名称为空{}", star.getName(),
									weiboName);
							next(options, task, browser, star, robotResult,
									starIterator, robotListener);
						}
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
							Map<String, List<String>> options, final Task task,
							final Browser browser, final Star star,
							final RobotResult robotResult,
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
			robotResult.setWeiboFan(0);
			robotResult.setWeiboFanInc(0);
			robotResult.setWeiboForward(0);
			robotResult.setWeiboLinkStatus(0);
			next(options, task, browser, star, robotResult, starIterator,
					robotListener);
			return;
		}

		if ("#".equals(star.getWeiboUrl().trim())) {
			LOGGER.info("明星{}微博地址无效，是#", star.getName());
			robotResult.setWeiboComment(0);
			robotResult.setWeiboFan(0);
			robotResult.setWeiboFanInc(0);
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
}
