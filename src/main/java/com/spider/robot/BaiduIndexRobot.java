package com.spider.robot;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.spider.common.Tesseract;
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
 * 描述:微博机器人
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
	}

	@Override
	public void grabData(final Task task, final Browser browser,
			final Star star, final RobotResult robotResult,
			final Iterator<Star> starIterator, final RobotListener robotListener) {
		// 处理微博关键字
		final ProgressAdapter baiduIndexListener = new ProgressAdapter() {
			public void completed(ProgressEvent event) {
				browser.removeProgressListener(this);
				LOGGER.info("baiduIndexListener");
				// 解析微博名称
				findIndex(task, browser, star, robotResult, starIterator, this,
						robotListener);
			}

			// browser.removeProgressListener(this);
			// next(task, browser, star, robotResult, starIterator);

			/**
			 * 描述:查找指数数字加载完成后的数据
			 * 
			 * @param task
			 * @param browser
			 * @param star
			 * @param robotResult
			 * @param starIterator
			 * @author liyixing 2015年9月12日 下午5:26:28
			 */

			private void findIndex(final Task task, final Browser browser,
					final Star star, final RobotResult robotResult,
					final Iterator<Star> starIterator,
					final ProgressAdapter baiduIndexListener,
					final RobotListener robotListener) {
				Display.getDefault().timerExec((int) 4000, new Runnable() {
					public void run() {
						LOGGER.info("findindex");
						Browser browser1 = browser;

						if (browser1.getText().indexOf("由于您访问过于频繁，请稍后再试") >= 0) {
							browser.setUrl("http://index.baidu.com");
							LOGGER.info("由于您访问过于频繁，请稍后再试");
							grabData(task, browser, star, robotResult,
									starIterator, robotListener);
							return;
						}
						if (browser1.getText().indexOf(
								"未被收录，如要查看相关数据，您需要购买创建新词的权限。") >= 0) {
							LOGGER.info("明星{}未被收录，如要查看相关数据，您需要购买创建新词的权限。",
									star.getName());
							robotResult.setBaiduIndex(0);
							robotResult.setBaiduIndexRank(0);
							next(task, browser, star, robotResult,
									starIterator, robotListener);
							return;
						}

						if (browser1.getText().indexOf("暂无相关数据") >= 0) {
							LOGGER.info("明星{}提示暂无相关数据，需要重新加载页面。",
									star.getName());
							grabData(task, browser, star, robotResult,
									starIterator, robotListener);
							return;
						}
						Object a;

						// try {
						// a = browser1
						// .evaluate("return T('.tiptext')[0].innerHTML");
						//
						// if (StringUtils.isNotBlank(a.toString())) {
						// // 验证码
						// Position position = getPos(browser, browser1,
						// "T('.verifyImg')[0]");
						// // 截图
						// BufferedImage bufferedImage = UtilImage
						// .getInstance().createScreenCapture(
						// position);
						// String v = getV(bufferedImage);
						//
						// LOGGER.debug(v);
						// }
						// } catch (Exception e) {
						// LOGGER.error("错误", e);
						// }

						try {
							a = browser1
									.evaluate("return document.getElementById('auto_gsid_5').getElementsByTagName('table')[0].innerHTML");
						} catch (Exception e) {
							LOGGER.info(
									"return document.getElementById('auto_gsid_5').getElementsByTagName('table')[0].innerHTML",
									e);

							if (browser1.getText().indexOf("很抱歉，您要访问的页面不存在！") >= 0) {
								LOGGER.info("很抱歉，您要访问的页面不存在！", e);
								Display.getDefault().timerExec((int) 1000,
										new Runnable() {
											public void run() {
												// 重新截图
												baiduIndexRobot.grabData(task,
														browser, star,
														robotResult,
														starIterator,
														robotListener);
											}
										});
								return;
							}
							findIndex(task, browser, star, robotResult,
									starIterator, baiduIndexListener,
									robotListener);
							return;
						}

						if (a != null) {
							try {
								Position position = getPos(browser, browser1,
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
									findIndex(task, browser, star, robotResult,
											starIterator, baiduIndexListener,
											robotListener);
									return;
								}

								if (position.h < 1) {
									LOGGER.info(
											"明星{}百度指数截图失败，无法识别出数据，刷新界面重新读取，高度={}",
											star.getName(), position.h);
									Display.getDefault().timerExec((int) 1000,
											new Runnable() {
												public void run() {
													// 重新截图
													baiduIndexRobot.grabData(
															task, browser,
															star, robotResult,
															starIterator,
															robotListener);
												}
											});
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
								String v = getV(bufferedImage);

								if (StringUtils.isBlank(v)) {
									LOGGER.info("明星{}百度指数截图失败，无法识别出数据，再次等待",
											star.getName());
									// 重新截图
									findIndex(task, browser, star, robotResult,
											starIterator, baiduIndexListener,
											robotListener);
									return;
								} else if (!NumberUtils.isNumber(v)) {
									LOGGER.info(
											"明星{}百度指数截图完成，但是数据中有无法识别为数字的符号{}",
											star.getName(), v);
									robotResult.setBaiduIndex(0);
									robotResult.setBaiduIndexRank(0);
									next(task, browser, star, robotResult,
											starIterator, robotListener);
									return;
								} else {
									LOGGER.info("明星{}百度指数截图完成", star.getName());
									// robotResult.setBaiduIndex(Integer.valueOf(v));
									robotResult.setBaiduIndexRank(0);
									robotResult.setBaiduIndex(0);
									next(task, browser, star, robotResult,
											starIterator, robotListener);
									return;
								}
							} catch (Exception e) {
								LOGGER.info("抓取图片出错", e);
								LOGGER.info("明星{}百度指数截图失败，无法识别出数据，刷新界面重新读取",
										star.getName());
								Display.getDefault().timerExec((int) 1000,
										new Runnable() {
											public void run() {
												// 重新截图
												baiduIndexRobot.grabData(task,
														browser, star,
														robotResult,
														starIterator,
														robotListener);
											}
										});
								return;
							}
						} else {
							//
							LOGGER.info("return document.getElementById('auto_gsid_5').getElementsByTagName('table')[0].innerHTML读取到的数据是空的");
							findIndex(task, browser, star, robotResult,
									starIterator, baiduIndexListener,
									robotListener);
						}
					}

					/**
					 * 描述:取元素坐标
					 * 
					 * @param browser
					 * @param browser1
					 * @return
					 * @author liyixing 2015年9月15日 下午6:06:10
					 */
					private Position getPos(final Browser browser,
							Browser browser1, String select) {
						Position position = UtilImage.getPosition(browser,
								select);
						// 读取坐标
						// 主坐标
						position.x = position.x + browser1.getLocation().x;
						position.y = browser1.getLocation().y + position.y;

						org.eclipse.swt.widgets.Composite parent = browser1
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
	 * 描述:解析出图片的数据
	 * 
	 * @param bufferedImage
	 * @return
	 * @author liyixing 2015年9月14日 下午1:50:09
	 */
	public String getV(BufferedImage bufferedImage) {
		try {
			File file = new File(System.getProperty("user.home") + "/temp.jpeg");

			if (!file.exists()) {
				file.createNewFile();
			}
			ImageIO.write(bufferedImage, "JPEG", file);
			String v = Tesseract.recognizeText(file);// v = "123.123";
			if (StringUtils.isNotBlank(v)) {
				v = v.replace(",", "").replace("E", "6").replace("B", "8")
						.replace(".", "").replace("S", "9").trim();
			}

			return v;
		} catch (Exception e) {
			// throw new RuntimeException(e);

			return "0";
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
