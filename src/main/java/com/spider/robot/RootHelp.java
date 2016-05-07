package com.spider.robot;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;

/**
 * 
 * 
 * 描述:机器人帮助类
 *
 * @author liyixing
 * @version 1.0
 * @since 2016年4月25日 上午9:58:34
 */
public class RootHelp {
	/**
	 * 
	 * 描述:重新开始一个任务
	 * 
	 * @author liyixing 2016年4月25日 上午10:08:36
	 */
	public static final void restart(Browser browser, ProgressAdapter oldAdp,
			ProgressAdapter newAdp) {
		browser.removeProgressListener(oldAdp);
		browser.addProgressListener(newAdp);
		browser.refresh();
	}
}
