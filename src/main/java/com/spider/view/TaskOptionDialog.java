package com.spider.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import com.spider.common.UtilApplicationContext;
import com.spider.entity.Category;
import com.spider.manager.CategoryMng;

/**
 * 
 * 
 * 描述:开始选项
 *
 * @author liyixing
 * @version 1.0
 * @since 2016年5月5日 上午9:56:51
 */
public class TaskOptionDialog extends Dialog {

	protected Map<String, List<String>> result = new HashMap<String, List<String>>();
	protected Shell shell;
	private static CategoryMng categoryMng;
	private Group groupCategory;
	private Group groupDimension;
	private List<Button> checkCategories;
	private List<Button> checkDimensions;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public TaskOptionDialog(Shell parent, int style) {
		super(parent, style);
		categoryMng = UtilApplicationContext.get(CategoryMng.class);
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		checkDimensions = new ArrayList<Button>();
		shell = new Shell(getParent(), SWT.CLOSE | SWT.APPLICATION_MODAL);
		shell.setSize(437, 405);
		shell.setText("\u6293\u53D6\u9009\u9879");
		groupCategory = new Group(shell, SWT.NONE);
		groupCategory.setText("\u6293\u53D6\u5206\u7C7B");
		groupCategory.setBounds(0, 0, 431, 193);

		groupDimension = new Group(shell, SWT.NONE);
		groupDimension.setText("\u6293\u53D6\u7EF4\u5EA6");
		groupDimension.setBounds(0, 199, 431, 133);

		Button baiduIndex = new Button(groupDimension, SWT.CHECK);
		baiduIndex.setSelection(true);
		baiduIndex.setBounds(10, 23, 80, 17);
		baiduIndex.setText("\u767E\u5EA6\u6307\u6570");
		checkDimensions.add(baiduIndex);

		Button baiduNews = new Button(groupDimension, SWT.CHECK);
		baiduNews.setSelection(true);
		baiduNews.setText("\u767E\u5EA6\u65B0\u95FB");
		baiduNews.setBounds(116, 23, 80, 17);
		checkDimensions.add(baiduNews);

		Button tieba = new Button(groupDimension, SWT.CHECK);
		tieba.setSelection(true);
		tieba.setText("\u8D34\u5427\u4F1A\u5458\u4E0E\u5E16\u5B50\u589E\u957F");
		tieba.setBounds(222, 54, 130, 17);
		checkDimensions.add(tieba);

		Button tiebaSign = new Button(groupDimension, SWT.CHECK);
		tiebaSign.setSelection(true);
		tiebaSign.setText("\u8D34\u5427\u7B7E\u5230");
		tiebaSign.setBounds(222, 23, 80, 17);
		checkDimensions.add(tiebaSign);

		Button wechat = new Button(groupDimension, SWT.CHECK);
		wechat.setSelection(true);
		wechat.setText("\u5FAE\u4FE1");
		wechat.setBounds(10, 54, 80, 17);
		checkDimensions.add(wechat);

		Button weibo = new Button(groupDimension, SWT.CHECK);
		weibo.setSelection(true);
		weibo.setText("\u5FAE\u535A\u8F6C\u8BC4\u8D5E");
		weibo.setBounds(116, 54, 80, 17);
		checkDimensions.add(weibo);

		Button weiboData = new Button(groupDimension, SWT.CHECK | SWT.CENTER);
		weiboData.setSelection(true);
		weiboData.setAlignment(SWT.LEFT);
		weiboData.setText("\u5FAE\u6307\u6570");
		weiboData.setBounds(308, 23, 80, 17);
		checkDimensions.add(weiboData);
		
		Button weiboFans = new Button(groupDimension, SWT.CHECK);
		weiboFans.setSelection(true);
		weiboFans.setText("\u5FAE\u535A\u7C89\u4E1D\u589E\u957F");
		weiboFans.setBounds(10, 85, 130, 17);
		checkDimensions.add(weiboFans);

		Button button_1 = new Button(shell, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getOptions();
				shell.close();
				shell.dispose();
			}
		});
		button_1.setBounds(10, 338, 80, 27);
		button_1.setText("\u786E\u5B9A\u5F00\u59CB");

		showCategory();
	}

	/**
	 * 
	 * 描述:显示分类
	 * 
	 * @author liyixing 2016年5月5日 下午1:10:17
	 */
	private void showCategory() {
		// 初始化分类
		List<Category> categories = categoryMng.getAll();
		checkCategories = new ArrayList<Button>();
		// 一行4列
		int col = 1;
		int row = 1;
		for (int index = 0; index < categories.size(); index++) {
			Category category = categories.get(index);
			Button btnCheckButton = new Button(groupCategory, SWT.CHECK);
			checkCategories.add(btnCheckButton);
			btnCheckButton.setBounds(10 + (col - 1) * 106, 23 + (row - 1) * 31,
					80, 17);
			btnCheckButton.setText(category.getName());
			btnCheckButton.setData(category.getId().toString());
			btnCheckButton.setSelection(true);

			col++;

			if (col > 4) {
				row++;
				col = 1;
			}
		}
	}

	/**
	 * 
	 * 描述:结构化选项结果
	 * 
	 * @author liyixing 2016年5月5日 下午4:33:45
	 */
	private void getOptions() {
		// 结构化选项
		List<String> dimensions = new ArrayList<String>();

		for (Button button : checkDimensions) {
			if (button.getSelection()) {
				dimensions.add(button.getText());
			}
		}
		
		List<String> categories = new ArrayList<String>();

		for (Button button : checkCategories) {
			if (button.getSelection()) {
				categories.add((String) button.getData());
			}
		}
		
		result.put("分类", categories);
		result.put("维度", dimensions);
	}
}
