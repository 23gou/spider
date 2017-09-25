package com.spider.parse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.swt.browser.Browser;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.lsiding.cgodo.util.UtilLog;

/**
 * 
 * 
 * 描述:页面解析
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月9日 下午6:38:01
 */
public class PageParase {
	/**
	 * 
	 * 描述:解析html的数据
	 * 
	 * @param content
	 * @param select
	 * @return
	 * @author liyixing 2015年9月9日 下午6:39:01
	 */
	public static final Elements parse(Document document, String select) {
		return document.select(select);
	}

	/**
	 * 
	 * 描述:解析html的数据
	 * 
	 * @param content
	 * @param select
	 * @return
	 * @author liyixing 2015年9月9日 下午6:39:01
	 */
	public static final String parseHtml(Document document, String select) {
		Elements elements = parse(document, select);

		return elements.html();
	}

	/**
	 * 
	 * 描述:解析html的数据
	 * 
	 * @param content
	 * @param select
	 * @return
	 * @author liyixing 2015年9月9日 下午6:39:01
	 */
	public static final String parseVal(Document document, String select) {
		Elements elements = parse(document, select);

		return elements.val();
	}

	/**
	 * 
	 * 描述:获取指定元素数据，已正则方式
	 * 
	 * @param text
	 * @author liyixing 2015年9月11日 上午10:02:13
	 * @return
	 */
	public static final List<String> parseTextWithPattern(String text,
			String rep) {
		Pattern pattern = Pattern.compile(rep);
		Matcher matcher = pattern.matcher(text);
		List<String> results = new ArrayList<String>();

		while (matcher.find()) {
			String txt = matcher.group(1);
			results.add(txt);
		}

		return results;
	}

	/**
	 * 
	 * 描述:获取指定元素数据，已正则方式
	 * 
	 * @param text
	 * @author liyixing 2015年9月11日 上午10:02:13
	 * @return
	 */
	public static final String parseTextWithPatternHtml(String text, String rep) {
		Pattern pattern = Pattern.compile(rep);
		Matcher matcher = pattern.matcher(text);
		String result = "";

		while (matcher.find() && matcher.groupCount() > 0) {
			String txt = matcher.group(1);
			result = result + txt;
		}

		return result;
	}

	/**
	 * 
	 * 描述:添加链接跳转的元素，并跳转
	 * 
	 * @return
	 * @author liyixing 2015年9月11日 下午3:56:28
	 */
	public static final String toUrl(Browser browser, String href) {
		// browser.evaluate("return document.getElementsByTagName('body')[0].innerHTML");
		String id = "myA" + (new Date()).getTime();
		// String script =
		// "function invokeClick(element) { if(element.click){element.click();alert('click');}    else if(element.fireEvent){element.fireEvent('onclick');alert('fireEvent');}    else if(document.createEvent){alert('createEvent');    var evt = document.createEvent(\"MouseEvents\");    evt.initEvent(\"click\", true, true);    element.dispatchEvent(evt);    }}"
		// + "var a = document.getElementsByTagName('body')[0];"
		// + "var b = document.createElement('a');b.href='"
		// + href
		// + "';  b.id='"
		// + id
		// +
		// "';a.appendChild(b);var c = document.createElement('input');c.type='button';b.appendChild(c);invokeClick(c);";

		String script = "location.href='" + href + "'";

		if(!browser.execute(script)){
			UtilLog.error("js执行失败");
		}
		return id;

	}
	
	/**
	 * 
	 * 描述:添加链接跳转的元素，并跳转
	 * 
	 * @return
	 * @author liyixing 2015年9月11日 下午3:56:28
	 */
	public static final String toUrl(String href,Header... headers) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(href);

		// httpGet.setHeader(
		// "user-agent",
		// "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.125 Safari/537.36");
		// httpGet.setHeader("Accept",
		// "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		// httpGet.setHeader("Upgrade-Insecure-Requests", "1");

		if (ArrayUtils.isNotEmpty(headers)) {
			httpGet.setHeaders(headers);
		}

		try {
			// httpPost.set
			CloseableHttpResponse response = httpclient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			return EntityUtils.toString(entity);
		} catch (Exception e) {
			return null;
		} finally {
			// response.close();
		}

	}
}
