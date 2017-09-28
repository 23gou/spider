package com.spider.view;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lsiding.cgodo.util.UtilMisc;

/**
 * 
 * 
 * 描述:浏览器seturl之后，可能会造成页面没有加载。通过geturl获取到的地址是seturl之前的地址<br>
 * 该浏览器会在每次seturl之后，保留当时set的url地址
 *
 * @author liyixing
 * @version 1.0
 * @since 2016年5月7日 上午11:02:51
 */
public class MyBrowser extends Browser {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(MyBrowser.class);

	/**
	 * 最后一次页面加载事件
	 */
	private Date last = new Date();
	/**
	 * 检查机制是否开启
	 */
	private boolean checkStart = false;
	private MyBrowser myself = this;
	/**
	 * 保留URL
	 */
	private String myUrl = "";

	public MyBrowser(Composite parent, int style) {
		super(parent, style);

		addProgressListener(new ProgressListener() {
			@Override
			public void completed(ProgressEvent event) {
				LOGGER.info("页面加载完毕，当前url{}", getUrl());
				// 如果触发了加载事件，则更新最后一次页面加载事件
				last = new Date();
			}

			@Override
			public void changed(ProgressEvent event) {
			}
		});
	}

	@Override
	protected void checkSubclass() {

	}

	public boolean setUrl(String url, String postData, String[] headers) {
		List<String> headsList = UtilMisc
				.toList(new String[] {
						"User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36 QIHU 360SE",
						"X-Requested-With:XMLHttpRequest",
						"Accept-Encoding: gzip, deflate, sdch",
						"Accept-Language: zh-CN,zh;q=0.8" });

		if (ArrayUtils.isNotEmpty(headers)) {
			headsList.addAll(UtilMisc.toList(headers));
		}

		String[] hs = headsList.toArray(new String[headsList.size()]);
		return super.setUrl(url, postData, hs);
	}

	public boolean setUrl(String url) {
		myUrl = url;
		boolean r = true;
		LOGGER.info("url:" + url);
		// 刷新页面，如果browser当前地址和setUrl相同，不会刷新，所以不能调用setUrl，只能调用刷新
		if (StringUtils.equals(getUrl(), url)) {
			LOGGER.info("refresh");
			super.refresh();
		} else {
			LOGGER.info("setUrl");
			r = super.setUrl(url);
			LOGGER.info("set url : {}", r);
		}

		return r;
	}

	public String getMyUrl() {
		return myUrl;
	}

	/**
	 * 
	 * 描述:开启监控机制
	 * 
	 * @author liyixing 2016年5月9日 下午4:03:53
	 */
	public void startCheck() {
		checkStart = true;
		last = new Date();
		check();
	}

	/**
	 * 
	 * 描述:关闭监控机制
	 * 
	 * @author liyixing 2016年5月9日 下午4:03:53
	 */
	public void closeCheck() {
		checkStart = false;
	}

	/**
	 * 
	 * 描述: 检查浏览器是否正常运行下去，很多情况，比如百度贴吧，页面实际上加载完成了，但是加载事件不执行，需要系统监控,让页面刷新一次来让系统继续下去
	 * 
	 * @author liyixing 2016年5月9日 下午3:57:36
	 */
	public void check() {
		Display.getDefault().timerExec((int) 1000, new Runnable() {
			public void run() {
				LOGGER.info("start {},time {}", checkStart,
						new Date().getTime() - last.getTime());
				String url = myself.getUrl();
				LOGGER.info("url:" + url);
				String myurl = myself.getMyUrl();
				LOGGER.info("myurl:" + myurl);
				String text = myself.getText();

				if (checkStart) {
					if (new Date().getTime() - last.getTime() >= 25000) {
						LOGGER.info("是否百度贴吧：" + (text.indexOf("百度贴吧") > 0));
						// 超过一定时间，浏览器没有反应，怎需要刷新页面
						LOGGER.info("超过{}，浏览器没有反应，需要刷新页面", 25000);

						myself.setUrl(myurl);
						last = new Date();
					}

					check();
				}
			}
		});
	}

	public Date getLast() {
		return last;
	}

	public void setLast(Date last) {
		this.last = last;
	}

	public boolean isCheckStart() {
		return checkStart;
	}

	public void setCheckStart(boolean checkStart) {
		this.checkStart = checkStart;
	}

	public MyBrowser getMyself() {
		return myself;
	}

	public void setMyself(MyBrowser myself) {
		this.myself = myself;
	}
}
