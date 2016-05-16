package com.spider.robot;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.spider.common.UtilImage;
import com.spider.common.UtilImage.Position;
import com.spider.entity.RobotResult;
import com.spider.entity.Star;
import com.spider.entity.Task;
import com.spider.manager.RobotResultMng;
import com.spider.manager.StarMng;

/**
 * 
 * 
 * 描述:百度指数机器人
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月10日 上午9:41:01
 */
@Component
public class BaiduIndexRobot extends DefaultRobot {
	@Autowired
	private StarMng starMng;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(BaiduIndexRobot.class);
	@Autowired
	private RobotResultMng robotResultMng;

	private BaiduIndexRobot baiduIndexRobot;

	public BaiduIndexRobot() {
		baiduIndexRobot = this;
		setName("百度指数");
	}
	
	public boolean validateDimensionZero(Map<String, List<String>> options,
			Task task, Browser browser, Star star, RobotResult robotResult,
			Iterator<Star> starIterator, RobotListener robotListener) {
		return StringUtils.isNotBlank(robotResult.getBaiduIndexImg());
	}

	@Override
	public void grabData(final Map<String, List<String>> options, final Task task,
			final Browser browser, final Star star,
			final RobotResult robotResult, final Iterator<Star> starIterator,
			final RobotListener robotListener) {
		// 处理百度指数关键字
		final ProgressAdapter baiduIndexListener = new ProgressAdapter() {
			public void completed(ProgressEvent event) {
				browser.removeProgressListener(this);
				LOGGER.info("baiduIndexListener");
				// 解析百度指数
				parse(options, task, browser, star, robotResult,
						starIterator, this, robotListener);
			}

			/**
			 * 描述:查找指数数字加载完成后的数据
			 * 
			 * @author liyixing 2015年9月12日 下午5:26:28
			 */

			private void parse(Map<String, List<String>> options,
					final Task task, final Browser browser, final Star star,
					final RobotResult robotResult,
					final Iterator<Star> starIterator,
					final ProgressAdapter baiduIndexListener,
					final RobotListener robotListener) {
				Display.getDefault().timerExec((int) 3000, new Runnable() {
					public void run() {
						LOGGER.info("parse");
						Browser browser1 = browser;

						// 检查是否存在页面错误
						if (!validatePage(options, task, browser, star,
								robotResult, starIterator, robotListener,
								browser1)) {
							return;
						}

						Object a;

						a = getData(options, task, browser, star, robotResult,
								starIterator, baiduIndexListener,
								robotListener, browser1);

						if (a != null && a instanceof Boolean) {
							return;
						}

						if (a != null) {
							try {
								Position position = getPos(browser,
										"T('div.lrRadius span.ftlwhf.enc2imgVal')[0]");

								LOGGER.info(
										"明星{}截图坐标，html元素坐标（x{},y{}）",
										new Object[] { star.getName(),
												String.valueOf(position.x),
												String.valueOf(position.y) });

								if (position.x < 10) {
									LOGGER.info(
											"明星{}百度指数截图失败，无法识别出数据，再次等待,x={}",
											star.getName(), position.x);
									// 重新截图
									parse(options, task, browser, star,
											robotResult, starIterator,
											baiduIndexListener, robotListener);
									return;
								}

								if (position.h < 1) {
									LOGGER.info(
											"明星{}百度指数截图失败，无法识别出数据，刷新界面重新读取，高度={}",
											star.getName(), position.h);
									// 重新截图
									baiduIndexRobot.grabData(options, task,
											browser, star, robotResult,
											starIterator, robotListener);
									return;
								}

								position.w += 10;
								BufferedImage bufferedImage = resize(UtilImage
										.getInstance().createScreenCapture(
												position));
								// 生成64位
								String base64 = toBase64(bufferedImage);
								robotResult.setBaiduIndexImg(base64);
								// 解析数字
								LOGGER.info("明星{}百度指数截图完成", star.getName());
								robotResult.setBaiduIndexRank(0);
								robotResult.setBaiduIndex(0);
								next(options, task, browser, star, robotResult,
										starIterator, robotListener);
								return;
							} catch (Exception e) {
								LOGGER.info("抓取图片出错", e);
								LOGGER.info("明星{}百度指数截图失败，无法识别出数据，刷新界面重新读取",
										star.getName());
								// 重新截图
								baiduIndexRobot.grabData(options, task,
										browser, star, robotResult,
										starIterator, robotListener);
								return;
							}
						} else {
							//
							LOGGER.info("return document.getElementById('auto_gsid_5').getElementsByTagName('table')[0].innerHTML读取到的数据是空的");
							// 对于百度指数，页面数据存在异步加载，有可能是因为数据还未加载完成
							parse(options, task, browser, star,
									robotResult, starIterator,
									baiduIndexListener, robotListener);
						}
					}

					/**
					 * 
					 * 描述:数据获取
					 * 
					 * @param options
					 * @param task
					 * @param browser
					 * @param star
					 * @param robotResult
					 * @param starIterator
					 * @param baiduIndexListener
					 * @param robotListener
					 * @param browser1
					 * @param a
					 * @return
					 * @author liyixing 2016年5月6日 下午2:25:25
					 */
					private Object getData(Map<String, List<String>> options,
							final Task task, final Browser browser,
							final Star star, final RobotResult robotResult,
							final Iterator<Star> starIterator,
							final ProgressAdapter baiduIndexListener,
							final RobotListener robotListener, Browser browser1) {
						Object a = null;
						
						try {
							a = browser1
									.evaluate("return document.getElementById('auto_gsid_5').getElementsByTagName('table')[0].innerHTML");
						} catch (Exception e) {
							LOGGER.info(
									"return document.getElementById('auto_gsid_5').getElementsByTagName('table')[0].innerHTML",
									e);

							if (browser1.getText().indexOf("很抱歉，您要访问的页面不存在！") >= 0) {
								LOGGER.info("很抱歉，您要访问的页面不存在！", e);
								// 重新截图
								baiduIndexRobot.grabData(options, task,
										browser, star, robotResult,
										starIterator, robotListener);
								return false;
							}

							// 对于百度指数，页面数据存在异步加载，有可能是因为数据还未加载完成
							parse(options, task, browser, star,
									robotResult, starIterator,
									baiduIndexListener, robotListener);
							return false;
						}

						return a;
					}

					/**
					 * 
					 * 描述:页面检查
					 * 
					 * @param options
					 * @param task
					 * @param browser
					 * @param star
					 * @param robotResult
					 * @param starIterator
					 * @param robotListener
					 * @param browser1
					 * @return
					 * @author liyixing 2016年5月6日 下午2:25:07
					 */
					private boolean validatePage(
							Map<String, List<String>> options, final Task task,
							final Browser browser, final Star star,
							final RobotResult robotResult,
							final Iterator<Star> starIterator,
							final RobotListener robotListener, Browser browser1) {
						if (browser1.getText().indexOf("由于您访问过于频繁，请稍后再试") >= 0) {
							browser.setUrl("http://index.baidu.com");
							LOGGER.info("由于您访问过于频繁，请稍后再试");
							grabData(options, task, browser, star, robotResult,
									starIterator, robotListener);
							return false;
						}

						if (browser1.getText().indexOf(
								"未被收录，如要查看相关数据，您需要购买创建新词的权限。") >= 0) {
							LOGGER.info("明星{}未被收录，如要查看相关数据，您需要购买创建新词的权限。",
									star.getName());
							robotResult.setBaiduIndex(0);
							robotResult.setBaiduIndexRank(0);
							next(options, task, browser, star, robotResult,
									starIterator, robotListener);
							return false;
						}

						if (browser1.getText().indexOf("暂无相关数据") >= 0) {
							LOGGER.info("明星{}提示暂无相关数据，需要重新加载页面。",
									star.getName());
							grabData(options, task, browser, star, robotResult,
									starIterator, robotListener);
							return false;
						}

						return true;
					}

					/**
					 * 描述:取元素坐标
					 * 
					 * @param browser
					 * @param browser1
					 * @return
					 * @author liyixing 2015年9月15日 下午6:06:10
					 */
					private Position getPos(final Browser browser, String select) {
						Position position = UtilImage.getPosition(browser,
								select);
						// 读取坐标
						// 主坐标
						position.x = position.x + browser.getLocation().x;
						position.y = browser.getLocation().y + position.y;

						org.eclipse.swt.widgets.Composite parent = browser
								.getParent();
						while (parent != null) {
							if (parent instanceof TabFolder) {
								TabFolder tabFolder = (TabFolder) parent;

								position.y = position.y
										+ tabFolder.getItems()[0].getBounds().y
										+ tabFolder.getItems()[0].getBounds().height
										+ parent.getLocation().y;
							} else {
								position.y = parent.getLocation().y
										+ position.y;
							}
							position.x = position.x + parent.getLocation().x;

							parent = parent.getParent();
						}
						return position;
					}
				});
			}
		};

		browser.addProgressListener(baiduIndexListener);

		try {
			browser.setUrl("http://index.baidu.com/?tpl=trend&word="
					+ URLEncoder.encode(star.getName(), "gbk"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * 描述:放大截获的图片
	 * 
	 * @param bufferedImage
	 * @return
	 * @author liyixing 2015年9月14日 下午1:47:14
	 */
	public BufferedImage resize(BufferedImage bufferedImage) {
		// 1.1放大
		BufferedImage result = UtilImage.resize(bufferedImage,
				(int) (bufferedImage.getWidth() * 1.1),
				(int) (bufferedImage.getHeight() * 1.1));

		return result;
	}

	/**
	 * 
	 * 描述:图片转化成64位
	 * 
	 * @param bufferedImage
	 * @return
	 * @author liyixing 2015年9月14日 下午1:49:57
	 */
	public String toBase64(BufferedImage bufferedImage) {
		// 生成64位
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try {

			ImageIO.write(bufferedImage, "JPEG", bos);

			// 对字节数组Base64编码
			byte[] imageBytes = bos.toByteArray();
			String imageString = Base64.encodeBase64String(imageBytes);
			bos.close();

			return imageString;
		} catch (IOException e) {
			LOGGER.error("转化成64位出错", e);

			throw new RuntimeException(e);
		}
	}
}
