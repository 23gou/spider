package com.spider.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.gou23.cgodo.jxl.Jxl;
import cn.gou23.cgodo.util.UtilBean;
import cn.gou23.cgodo.util.UtilLog;

import com.common.jdbc.page.Pagination;
import com.common.util.DateUtils;
import com.spider.common.UtilApplicationContext;
import com.spider.common.UtilImage;
import com.spider.entity.Category;
import com.spider.entity.RobotResult;
import com.spider.entity.RobotResult.EditStatus;
import com.spider.entity.RobotResult.ResultStatus;
import com.spider.entity.Star;
import com.spider.entity.Task;
import com.spider.entity.Task.TaskStatus;
import com.spider.entity.Task.TaskType;
import com.spider.entity.TaskOption;
import com.spider.entity.TaskOption.TaskOptionStatus;
import com.spider.entity.TaskOption.TaskOptionType;
import com.spider.manager.CategoryMng;
import com.spider.manager.RobotResultMng;
import com.spider.manager.StarMng;
import com.spider.manager.TaskMng;
import com.spider.manager.TaskOptionMng;
import com.spider.robot.Robot;
import com.spider.robot.Robot.RobotListener;

/**
 * 
 * 
 * 描述:试图
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月8日 下午1:17:03
 */
@Component
public class SpiderView {
	private Logger logger = LoggerFactory.getLogger(SpiderView.class);
	protected Shell shell;
	private Table table;
	private Text text;
	private Table table_1;
	private Text text_1;
	private Text text_2;
	private Combo combo_1;
	private List<Category> categories;
	private Map<Long, Category> categoriesById;
	private Map<Long, Category> categoriesByName;
	private Label lblNewLabel_2;
	private Combo combo_3;
	private Text text_3;
	private Combo combo_4;
	private Text text_4;
	private Text text_5;
	private List<Task> tasks;
	private Map<Long, Task> tasksById;
	private Map<Long, Task> tasksByName;
	private Combo combo_2;
	private Combo combo;
	private Combo combo_5;
	private Button button;
	private Button button_7;
	private Label lblNewLabel_5;
	private Label lblNewLabel_3;
	private Group group_5;
	private Group starEdit;
	private Group robotResultEdit;
	private Combo combo_6;
	private Button button_11;
	private MyBrowser mybrowser;
	private Text text_6;
	private boolean baiduLogin = false;
	private boolean weiboLogin = false;
	private static RobotResultMng robotResultMng;
	private static StarMng starMng;
	private static Robot robot;
	private static TaskMng taskMng;
	private static SpiderView window;
	private static CategoryMng categoryMng;
	private static TaskOptionMng taskOptionMng;
	/**
	 * 明星表格分页
	 */
	private Pagination starPagination;
	/**
	 * 结果表格分页
	 */
	private Pagination robotResultPagination;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.setProperty("org.eclipse.swt.browser.XULRunnerPath",
				System.getProperty("user.dir") + "/xulrunner");
		try {
			// 读取spring
			com.spider.common.UtilApplicationContext.load();
			categoryMng = UtilApplicationContext.get(CategoryMng.class);
			starMng = UtilApplicationContext.get(StarMng.class);
			robot = UtilApplicationContext.get("noneRobot");
			taskMng = UtilApplicationContext.get(TaskMng.class);
			robotResultMng = UtilApplicationContext.get(RobotResultMng.class);
			taskOptionMng = UtilApplicationContext.get(TaskOptionMng.class);
			window = new SpiderView();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
			UtilLog.error("系统出错", e);
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		Display display = Display.getDefault();
		shell = new Shell(display, SWT.CLOSE);
		shell.setSize(1020, 628);
		shell.setText("明星抓取");
		shell.setBounds(1, 1, 1020, 750);

		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				System.exit(0);
			}
		});

		TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setBounds(0, 50, 1014, 672);

		TabItem tabItem_2 = new TabItem(tabFolder, SWT.NONE);
		tabItem_2.setText("\u6293\u53D6");

		Group group_2 = new Group(tabFolder, SWT.NONE);
		tabItem_2.setControl(group_2);
		group_2.setText("\u6293\u53D6");

		Label label = new Label(group_2, SWT.NONE);
		label.setText("\u5BF9\u6BD4\u65F6\u95F4");
		label.setBounds(5, 16, 50, 17);

		combo = new Combo(group_2, SWT.NONE);
		combo.setToolTipText("");
		combo.setBounds(60, 13, 224, 25);
		combo.setText("\u6293\u53D6\u65F6\u95F4");

		button = new Button(group_2, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 坐标0是全部任务，所以坐标要大于1
				if (combo.getItems().length > 1
						&& combo.getSelectionIndex() < 1) {
					errorMessage("请选择对比时间");
					alertMsg(shell, "请选择对比时间");
					return;
				}

				TaskOptionDialog taskOptionDialog = new TaskOptionDialog(shell,
						SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
				@SuppressWarnings("unchecked")
				Map<String, List<String>> result = (Map<String, List<String>>) taskOptionDialog
						.open();

				if (MapUtils.isNotEmpty(result)
						&& CollectionUtils.isNotEmpty(result
								.get(TaskOptionType.分类.toString()))
						&& CollectionUtils.isNotEmpty(result
								.get(TaskOptionType.维度.toString()))) {
					// 对比时间
					Task ctask = getSelectTask(combo);
					Task task = new Task();

					if (ctask != null) {
						task.setContrastTaskId(ctask.getId());
					}
					task.setStartDateTime(new Date());
					task.setTaskStatus(TaskStatus.未完成.toString());
					task.setType(TaskType.抓取.toString());
					taskMng.add(task);

					// 保存每个选项
					List<String> categories = result.get(TaskOptionType.分类
							.toString());
					List<String> dimensions = result.get(TaskOptionType.维度
							.toString());

					for (String category : categories) {
						taskOptionMng.add(task.getId(),
								TaskOptionType.分类.toString(), category);
					}

					for (String dimension : dimensions) {
						taskOptionMng.add(task.getId(),
								TaskOptionType.维度.toString(), dimension);
					}

					taskOptionMng.add(task.getId(),
							TaskOptionType.当前明星.toString(), Integer.MAX_VALUE
									+ "");

					List<Star> stars = starMng.getList(categories);

					showTask();
					grab(result, task, stars);
				} else {
					errorMessage("请选择要抓取的维度和分类");
					alertMsg(shell, "请选择要抓取的维度和分类");
				}
			}
		});
		button.setText("\u5F00\u59CB");
		button.setBounds(290, 11, 50, 27);

		initBrowser(group_2);
		// browser.setUrl("about:config");
		final Button button_6 = new Button(group_2, SWT.NONE);
		Button button_4 = new Button(group_2, SWT.NONE);
		button_4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				weiboLogin = true;
				mybrowser.setUrl("http://weibo.com/");
				if (baiduLogin) {
					showTaskButton();
				}
			}
		});
		button_4.setText("\u767B\u5F55\u5FAE\u535A");
		button_4.setBounds(824, 11, 80, 27);

		button_6.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				baiduLogin = true;
				mybrowser.setUrl("http://index.baidu.com");
				if (weiboLogin) {
					showTaskButton();
				}
			}
		});
		button_6.setText("\u767B\u5F55\u767E\u5EA6");
		button_6.setBounds(738, 11, 80, 27);

		button_7 = new Button(group_2, SWT.NONE);
		button_7.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// browser.setUrl("http://data.weibo.com/index");
				// 继续，查询还没有抓取结果的人员
				Task task = getSelectTask(combo_6);

				if (task == null) {
					errorMessage("请选择需要继续的任务");
					alertMsg(shell, "请选择需要继续的任务");
					return;
				}

				if (TaskStatus.完成.toString().equals(task.getTaskStatus())) {
					errorMessage("该任务已经完成，无需继续");
					alertMsg(shell, "该任务已经完成，无需继续");
					return;
				}

				// 开始执行
				RobotResult robotResult = new RobotResult();

				robotResult.setTaskId(task.getId());
				List<Star> stars = starMng.getNotRobot(robotResult);
				showTask();
				grab(taskOptionMng.getMapByTaskAndName(robotResult.getTaskId(),
						null, TaskOptionStatus.未完成.toString()), task, stars);
			}
		});
		button_7.setText("\u7EE7\u7EED");
		button_7.setBounds(629, 11, 50, 27);

		combo_6 = new Combo(group_2, SWT.NONE);
		combo_6.setToolTipText("");
		combo_6.setBounds(399, 13, 224, 25);
		combo_6.setText("\u6293\u53D6\u65F6\u95F4");
		combo_6.select(0);

		Label label_3 = new Label(group_2, SWT.NONE);
		label_3.setBounds(346, 16, 53, 17);
		label_3.setText("\u7EE7\u7EED\u4EFB\u52A1");

		button_11 = new Button(group_2, SWT.NONE);
		button_11.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 继续，查询还没有抓取百度指数的人员
				Task task = getSelectTask(combo_6);

				if (task == null) {
					errorMessage("请选择要追加的任务");
					alertMsg(shell, "请选择要追加的任务");
					return;
				}

				if (task.getTaskStatus().equals(TaskStatus.未完成.toString())) {
					errorMessage("该任务还未完成，无法追加");
					alertMsg(shell, "该任务还未完成，无法追加");
					return;
				}

				TaskOptionDialog taskOptionDialog = new TaskOptionDialog(shell,
						SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
				@SuppressWarnings("unchecked")
				Map<String, List<String>> result = (Map<String, List<String>>) taskOptionDialog
						.open();

				if (MapUtils.isNotEmpty(result)
						&& CollectionUtils.isNotEmpty(result
								.get(TaskOptionType.分类.toString()))
						&& CollectionUtils.isNotEmpty(result
								.get(TaskOptionType.维度.toString()))) {
					// 保存每个选项
					List<String> categories = result.get(TaskOptionType.分类
							.toString());
					List<String> dimensions = result.get(TaskOptionType.维度
							.toString());

					for (String category : categories) {
						taskOptionMng.add(task.getId(),
								TaskOptionType.分类.toString(), category);
					}

					for (String dimension : dimensions) {
						taskOptionMng.add(task.getId(),
								TaskOptionType.维度.toString(), dimension);
					}

					List<TaskOption> currentStarOption = taskOptionMng
							.getByTaskAndName(task.getId(),
									TaskOptionType.当前明星.toString(), null);

					if (CollectionUtils.isEmpty(currentStarOption)) {
						taskOptionMng.add(task.getId(),
								TaskOptionType.当前明星.toString(),
								Integer.MAX_VALUE + "");
					} else {
						taskOptionMng.update(currentStarOption.get(0).getId(),
								null, Integer.MAX_VALUE + "");
					}

					// 开始执行
					// 任务状态改成未完成
					task.setTaskStatus(TaskStatus.未完成.toString());
					taskMng.save(task);
					RobotResult robotResult = new RobotResult();

					robotResult.setTaskId(task.getId());
					List<Star> stars = starMng.getList(categories);
					showTask();
					grab(result, task, stars);
				}
			}
		});
		button_11.setBounds(682, 11, 50, 27);
		button_11.setText("\u8FFD\u52A0");

		TabItem tabItem_1 = new TabItem(tabFolder, SWT.NONE);
		tabItem_1.setText("\u6293\u53D6\u67E5\u770B");
		tabItem_1.getBounds();

		Group group_1 = new Group(tabFolder, SWT.NONE);
		tabItem_1.setControl(group_1);

		table_1 = new Table(group_1, SWT.BORDER | SWT.FULL_SELECTION);
		table_1.setBounds(3, 190, 976, 409);
		table_1.setHeaderVisible(true);
		table_1.setLinesVisible(true);

		TableColumn tblclmnId_1 = new TableColumn(table_1, SWT.NONE);
		tblclmnId_1.setWidth(30);
		tblclmnId_1.setText("ID");

		TableColumn tableColumn_3 = new TableColumn(table_1, SWT.NONE);
		tableColumn_3.setWidth(58);
		tableColumn_3.setText("\u59D3\u540D");

		TableColumn tableColumn_25 = new TableColumn(table_1, SWT.NONE);
		tableColumn_25.setWidth(100);
		tableColumn_25.setText("\u5206\u7C7B");

		TableColumn tableColumn_26 = new TableColumn(table_1, SWT.NONE);
		tableColumn_26.setWidth(100);
		tableColumn_26.setText("\u6293\u53D6\u65E5");

		TableColumn tblclmnNewColumn_3 = new TableColumn(table_1, SWT.NONE);
		tblclmnNewColumn_3.setWidth(100);
		tblclmnNewColumn_3.setText("\u7EFC\u5408\u5206\u6570");

		TableColumn tableColumn_27 = new TableColumn(table_1, SWT.NONE);
		tableColumn_27.setWidth(100);
		tableColumn_27.setText("\u7EFC\u5408\u6392\u540D");

		TableColumn tableColumn_4 = new TableColumn(table_1, SWT.NONE);
		tableColumn_4.setWidth(128);
		tableColumn_4.setText("\u767E\u5EA6\u6307\u6570\u622A\u56FE");

		TableColumn tableColumn_5 = new TableColumn(table_1, SWT.NONE);
		tableColumn_5.setWidth(61);
		tableColumn_5
				.setText("\u767E\u5EA6\u6307\u6570\u4E00\u5468\u5E73\u5747\u6570");

		TableColumn tblclmnNewColumn = new TableColumn(table_1, SWT.NONE);
		tblclmnNewColumn.setWidth(38);
		tblclmnNewColumn
				.setText("\u767E\u5EA6\u6307\u6570\u4E00\u5468\u5E73\u5747\u6570\u6392\u540D");

		TableColumn tableColumn_6 = new TableColumn(table_1, SWT.NONE);
		tableColumn_6.setWidth(72);
		tableColumn_6
				.setText("\u767E\u5EA6\u65B0\u95FB\u4E00\u5468\u6536\u5F55\u91CF(\u4EC5\u9650\u6807\u9898)");

		TableColumn tableColumn_7 = new TableColumn(table_1, SWT.NONE);
		tableColumn_7.setWidth(39);
		tableColumn_7
				.setText("\u767E\u5EA6\u65B0\u95FB\u4E00\u5468\u6536\u5F55\u91CF(\u4EC5\u9650\u6807\u9898)\u6392\u540D");

		TableColumn tableColumn_8 = new TableColumn(table_1, SWT.NONE);
		tableColumn_8.setWidth(103);
		tableColumn_8
				.setText("\u767E\u5EA6\u8D34\u5427\u4F1A\u5458\u4E00\u5468\u589E\u957F\u6570");

		TableColumn tableColumn_9 = new TableColumn(table_1, SWT.NONE);
		tableColumn_9.setWidth(36);
		tableColumn_9
				.setText("\u767E\u5EA6\u8D34\u5427\u4F1A\u5458\u4E00\u5468\u589E\u957F\u6570\u6392\u540D");

		TableColumn tableColumn_10 = new TableColumn(table_1, SWT.NONE);
		tableColumn_10.setWidth(74);
		tableColumn_10
				.setText("\u767e\u5ea6\u8d34\u5427\u5e16\u5b50\u4e00\u5468\u589e\u957f\u6570");

		TableColumn tableColumn_11 = new TableColumn(table_1, SWT.NONE);
		tableColumn_11.setWidth(45);
		tableColumn_11
				.setText("\u767e\u5ea6\u8d34\u5427\u5e16\u5b50\u4e00\u5468\u589e\u957f\u6570\u6392\u540D");

		TableColumn tableColumn_12 = new TableColumn(table_1, SWT.NONE);
		tableColumn_12.setWidth(68);
		tableColumn_12
				.setText("\u767E\u5EA6\u8D34\u5427\u4E00\u5468\u7B7E\u5230\u5E73\u5747\u6570");

		TableColumn tableColumn_13 = new TableColumn(table_1, SWT.NONE);
		tableColumn_13.setWidth(43);
		tableColumn_13
				.setText("\u767E\u5EA6\u8D34\u5427\u4E00\u5468\u7B7E\u5230\u5E73\u5747\u6570\u6392\u540D");

		TableColumn tblclmnNewColumn_1 = new TableColumn(table_1, SWT.NONE);
		tblclmnNewColumn_1.setWidth(65);
		tblclmnNewColumn_1
				.setText("\u65b0\u6d6a\u5fae\u535a\u70ed\u8bae\u4e00\u5468\u603b\u6570");

		TableColumn tableColumn_14 = new TableColumn(table_1, SWT.NONE);
		tableColumn_14.setWidth(49);
		tableColumn_14
				.setText("\u65b0\u6d6a\u5fae\u535a\u70ed\u8bae\u4e00\u5468\u603b\u6570\u6392\u540D");

		TableColumn tableColumn_15 = new TableColumn(table_1, SWT.NONE);
		tableColumn_15.setWidth(75);
		tableColumn_15
				.setText("\u65b0\u6d6a\u5fae\u535a\u7c89\u4e1d\u4e00\u5468\u589e\u957f\u6570");

		TableColumn tableColumn_16 = new TableColumn(table_1, SWT.NONE);
		tableColumn_16.setWidth(40);
		tableColumn_16
				.setText("\u65b0\u6d6a\u5fae\u535a\u7c89\u4e1d\u4e00\u5468\u589e\u957f\u6570\u6392\u540D");

		TableColumn tblclmnNewColumn_2 = new TableColumn(table_1, SWT.NONE);
		tblclmnNewColumn_2.setWidth(41);
		tblclmnNewColumn_2
				.setText("\u65b0\u6d6a\u5fae\u535a\u8fd1\u4e03\u6761\u5e73\u5747\u8f6c\u53d1\u6570");

		TableColumn tableColumn_19 = new TableColumn(table_1, SWT.NONE);
		tableColumn_19.setWidth(47);
		tableColumn_19
				.setText("\u65b0\u6d6a\u5fae\u535a\u8fd1\u4e03\u6761\u5e73\u5747\u8f6c\u53d1\u6570\u6392\u540D");

		TableColumn tableColumn_17 = new TableColumn(table_1, SWT.NONE);
		tableColumn_17.setWidth(52);
		tableColumn_17
				.setText("\u65b0\u6d6a\u5fae\u535a\u8fd1\u4e03\u6761\u5e73\u5747\u8bc4\u8bba\u6570");

		TableColumn tableColumn_20 = new TableColumn(table_1, SWT.NONE);
		tableColumn_20.setWidth(40);
		tableColumn_20
				.setText("\u65b0\u6d6a\u5fae\u535a\u8fd1\u4e03\u6761\u5e73\u5747\u8bc4\u8bba\u6570\u6392\u540D");

		TableColumn tableColumn_18 = new TableColumn(table_1, SWT.NONE);
		tableColumn_18.setWidth(60);
		tableColumn_18
				.setText("\u65b0\u6d6a\u5fae\u535a\u8fd1\u4e03\u6761\u5e73\u5747\u70b9\u8d5e\u6570");

		TableColumn tableColumn_21 = new TableColumn(table_1, SWT.NONE);
		tableColumn_21.setWidth(56);
		tableColumn_21
				.setText("\u65b0\u6d6a\u5fae\u535a\u8fd1\u4e03\u6761\u5e73\u5747\u70b9\u8d5e\u6570\u6392\u540D");

		TableColumn tableColumn_22 = new TableColumn(table_1, SWT.NONE);
		tableColumn_22.setWidth(71);
		tableColumn_22
				.setText("\u5fae\u4fe1\u6587\u7ae0\u63d0\u53ca\u91cf\u4e00\u5468\u603b\u6570");

		TableColumn tableColumn_23 = new TableColumn(table_1, SWT.NONE);
		tableColumn_23.setWidth(100);
		tableColumn_23
				.setText("\u5fae\u4fe1\u6587\u7ae0\u63d0\u53ca\u91cf\u4e00\u5468\u603b\u6570\u6392\u540D");

		Button button_8 = new Button(group_1, SWT.NONE);
		button_8.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				robotResultPagination.setPageNo(robotResultPagination
						.getPrePage());
				showRobotResult();
			}
		});
		button_8.setText("\u4E0A\u4E00\u9875");
		button_8.setBounds(92, 605, 60, 27);

		Button button_9 = new Button(group_1, SWT.NONE);
		button_9.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				robotResultPagination.setPageNo(robotResultPagination
						.getNextPage());
				showRobotResult();
			}
		});
		button_9.setText("\u4E0B\u4E00\u9875");
		button_9.setBounds(158, 605, 60, 27);

		lblNewLabel_3 = new Label(group_1, SWT.NONE);
		lblNewLabel_3.setBounds(288, 610, 400, 17);

		Group group_6 = new Group(group_1, SWT.NONE);
		robotResultEdit = group_6;
		group_6.setText("\u7F16\u8F91\u767E\u5EA6\u6307\u6570");
		group_6.setBounds(3, 100, 624, 84);

		Button btnNewButton_9 = new Button(group_6, SWT.NONE);
		btnNewButton_9.setBounds(185, 47, 80, 27);

		btnNewButton_9.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveBaiduIndex();
			}
		});
		btnNewButton_9.setText("\u4FDD\u5B58\u6307\u6570");

		text_6 = new Text(group_6, SWT.BORDER);
		text_6.setBounds(93, 47, 73, 23);

		text_6.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				saveBaiduIndex();
			}

			@Override
			public void focusGained(FocusEvent e) {

			}
		});

		Label label_2 = new Label(group_6, SWT.NONE);
		label_2.setBounds(10, 53, 61, 17);
		label_2.setText("\u767E\u5EA6\u6307\u6570");

		Button button_10 = new Button(group_6, SWT.NONE);
		button_10.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 删除
				if (table_1.getSelectionIndex() >= 0) {
					TableItem item = table_1.getItem(table_1
							.getSelectionIndex());
					Long id = Long.valueOf(item.getText(0));
					robotResultMng.delete(id);
					showRobotResult();
				} else {
					errorMessage("请选择要删除的数据");
				}
			}
		});
		button_10.setBounds(283, 47, 80, 27);
		button_10.setText("\u5220\u9664");

		Group group_7 = new Group(group_1, SWT.NONE);
		group_7.setText("\u67E5\u8BE2\u6761\u4EF6");
		group_7.setBounds(3, 10, 976, 84);

		text_1 = new Text(group_7, SWT.BORDER);
		text_1.setBounds(47, 27, 80, 25);

		Label lblNewLabel_4 = new Label(group_7, SWT.NONE);
		lblNewLabel_4.setBounds(10, 35, 31, 17);
		lblNewLabel_4.setText("\u59D3\u540D");

		combo_2 = new Combo(group_7, SWT.NONE);
		combo_2.setBounds(132, 27, 266, 25);
		combo_2.setToolTipText("");
		combo_2.setText("\u6293\u53D6\u65F6\u95F4");

		combo_3 = new Combo(group_7, SWT.NONE);
		combo_3.setBounds(415, 27, 167, 25);
		combo_3.setText("\u660E\u661F\u5206\u7C7B");

		combo_5 = new Combo(group_7, SWT.NONE);
		combo_5.setBounds(588, 27, 120, 25);
		combo_5.setItems(new String[] {
				"\u767E\u5EA6\u6307\u6570\u662F\u5426\u7F16\u8F91",
				"\u5DF2\u7F16\u8F91", "\u672A\u7F16\u8F91" });
		combo_5.setText("\u767E\u5EA6\u6307\u6570\u662F\u5426\u7F16\u8F91");

		Button button_5 = new Button(group_7, SWT.NONE);
		button_5.setBounds(714, 25, 80, 27);
		button_5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				robotResultPagination.setPageNo(1);
				showRobotResult();
			}
		});
		button_5.setText("\u67E5\u8BE2");

		Button button_2 = new Button(group_7, SWT.NONE);
		button_2.setBounds(800, 25, 80, 27);
		button_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				importExcel();
			}
		});
		button_2.setText("\u5BFC\u5165");

		Button button_3 = new Button(group_7, SWT.NONE);
		button_3.setBounds(886, 25, 80, 27);
		button_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 导出
				export();
			}
		});
		button_3.setText("\u5BFC\u51FA");

		Button button_12 = new Button(group_1, SWT.NONE);
		button_12.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				robotResultPagination.setPageNo(robotResultPagination
						.getTotalPage());
				showRobotResult();
			}
		});
		button_12.setText("\u5C3E\u9875");
		button_12.setBounds(222, 605, 60, 27);

		Button button_13 = new Button(group_1, SWT.NONE);
		button_13.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				robotResultPagination.setPageNo(1);
				showRobotResult();
			}
		});
		button_13.setText("\u9996\u9875");
		button_13.setBounds(26, 605, 60, 27);

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("\u660E\u661F\u8BBE\u7F6E");

		Group group = new Group(tabFolder, SWT.NONE);
		tabItem.setControl(group);

		table = new Table(group, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(10, 268, 936, 331);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tblclmnId = new TableColumn(table, SWT.NONE);
		tblclmnId.setWidth(45);
		tblclmnId.setText("ID");

		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(100);
		tableColumn.setText("\u59D3\u540D");

		TableColumn tableColumn_1 = new TableColumn(table, SWT.NONE);
		tableColumn_1.setWidth(340);
		tableColumn_1.setText("\u5FAE\u535A\u5730\u5740");

		TableColumn tableColumn_2 = new TableColumn(table, SWT.NONE);
		tableColumn_2.setWidth(340);
		tableColumn_2.setText("\u8D34\u5427\u5730\u5740");

		TableColumn tableColumn_24 = new TableColumn(table, SWT.NONE);
		tableColumn_24.setWidth(100);
		tableColumn_24.setText("\u5206\u7C7B");

		Button button_1 = new Button(group, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (table.getSelectionIndex() < 0) {
					errorMessage("请选择要删除的明星");
					return;
				} else {
					starMng.delete((Long) starEdit.getData());
					table.remove(table.getSelectionIndex());
				}
			}
		});
		button_1.setText("\u5220\u9664");
		button_1.setBounds(1054, 72, 80, 27);

		Button btnNewButton_5 = new Button(group, SWT.NONE);
		btnNewButton_5.setBounds(96, 605, 80, 27);
		btnNewButton_5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				starPagination.setPageNo(starPagination.getNextPage());
				showStars();
			}
		});
		btnNewButton_5.setText("\u4E0B\u4E00\u9875");

		Button btnNewButton_4 = new Button(group, SWT.NONE);
		btnNewButton_4.setBounds(10, 605, 80, 27);
		btnNewButton_4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				starPagination.setPageNo(starPagination.getPrePage());
				showStars();
			}
		});
		btnNewButton_4.setText("\u4E0A\u4E00\u9875");

		Group group_3 = new Group(group, SWT.NONE);
		group_3.setText("\u5206\u7C7B\u7BA1\u7406");
		group_3.setBounds(10, 10, 370, 55);

		Button btnNewButton_1 = new Button(group_3, SWT.NONE);
		btnNewButton_1.setBounds(163, 18, 80, 27);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (StringUtils.isBlank(text_2.getText())) {
					errorMessage("请输入分类名");
					text_2.forceFocus();
				} else if (categoryMng.findByName(text_2.getText()) != null) {
					errorMessage("分类：" + text_2.getText() + "已经存在");
					text_2.forceFocus();
				} else {
					Category category = new Category();

					category.setName(text_2.getText());
					categoryMng.add(category);
					errorMessage(null);
					showCategory();
				}
			}
		});
		btnNewButton_1.setText("\u6DFB\u52A0\u5206\u7C7B");

		text_2 = new Text(group_3, SWT.BORDER);
		text_2.setBounds(10, 20, 147, 23);

		lblNewLabel_5 = new Label(group, SWT.NONE);
		lblNewLabel_5.setBounds(192, 615, 400, 17);

		Group group_4 = new Group(group, SWT.NONE);
		group_4.setText("\u67E5\u8BE2\u6761\u4EF6");
		group_4.setBounds(10, 201, 858, 61);

		Label lblNewLabel = new Label(group_4, SWT.NONE);
		lblNewLabel.setBounds(10, 33, 61, 17);
		lblNewLabel.setText("\u59D3\u540D\uFF1A");

		text = new Text(group_4, SWT.BORDER);
		text.setBounds(77, 27, 73, 23);

		combo_1 = new Combo(group_4, SWT.NONE);
		combo_1.setBounds(242, 22, 88, 25);

		Label lblNewLabel_1 = new Label(group_4, SWT.NONE);
		lblNewLabel_1.setBounds(168, 30, 61, 17);
		lblNewLabel_1.setText("\u5206\u7C7B\uFF1A");

		Button btnNewButton = new Button(group_4, SWT.NONE);
		btnNewButton.setBounds(359, 23, 80, 27);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				starPagination.setPageNo(1);
				showStars();
			}
		});
		btnNewButton.setText("\u67E5\u8BE2");

		group_5 = new Group(group, SWT.NONE);
		starEdit = group_5;
		group_5.setText("\u7F16\u8F91");
		group_5.setBounds(10, 86, 951, 94);

		Label lblNewLabel_6 = new Label(group_5, SWT.NONE);
		lblNewLabel_6.setBounds(10, 32, 41, 17);
		lblNewLabel_6.setText("\u59D3\u540D\uFF1A");

		text_3 = new Text(group_5, SWT.BORDER);
		text_3.setBounds(60, 27, 73, 23);

		combo_4 = new Combo(group_5, SWT.NONE);
		combo_4.setBounds(246, 27, 88, 25);

		text_4 = new Text(group_5, SWT.BORDER);
		text_4.setBounds(60, 57, 320, 23);

		Label lblNewLabel_8 = new Label(group_5, SWT.NONE);
		lblNewLabel_8.setBounds(10, 62, 41, 17);
		lblNewLabel_8.setText("\u5FAE\u535A\uFF1A");

		text_5 = new Text(group_5, SWT.BORDER);
		text_5.setBounds(450, 59, 320, 23);

		Button btnNewButton_2 = new Button(group_5, SWT.NONE);
		btnNewButton_2.setBounds(776, 57, 80, 27);
		btnNewButton_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearStarEdit();
			}
		});
		btnNewButton_2.setText("\u6E05\u7A7A");

		Button btnNewButton_3 = new Button(group_5, SWT.NONE);
		btnNewButton_3.setBounds(862, 57, 80, 27);
		btnNewButton_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Star star = new Star();
				Category category = getSelectCategory(combo_4);
				// 名称
				star.setName(text_3.getText());
				// 微博主页
				star.setWeiboUrl(text_4.getText());
				star.setTiebaUrl(text_5.getText());

				// 分类
				if (category != null) {
					star.setCategoryId(category.getId());
				} else {
					errorMessage("请选择分类");
					return;
				}

				if (StringUtils.isBlank(star.getName())) {
					errorMessage("请输入明星姓名");
					return;
				}

				errorMessage(null);

				// 保存
				if (starEdit.getData() != null) {
					star.setId((Long) starEdit.getData());
					starMng.save(star);
					showNewStarTableItem(star);
					clearStarEdit();
				} else {
					// 添加
					starMng.add(star);
					showStarTableItem(star);
					starEdit.setData(star.getId());
					clearStarEdit();
				}
			}

		});
		btnNewButton_3.setText("\u4FDD\u5B58");

		Label lblNewLabel_7 = new Label(group_5, SWT.NONE);
		lblNewLabel_7.setBounds(185, 32, 46, 17);
		lblNewLabel_7.setText("\u5206\u7C7B\uFF1A");

		Label label_1 = new Label(group_5, SWT.NONE);
		label_1.setBounds(386, 60, 61, 17);
		label_1.setText("\u8D34\u5427\u4E3B\u9875\uFF1A");

		lblNewLabel_2 = new Label(shell, SWT.NONE);
		lblNewLabel_2.setBounds(10, 8, 692, 36);
		lblNewLabel_2.setText("\u6D88\u606F");

		lblNewLabel_2.setForeground(lblNewLabel.getDisplay().getSystemColor(
				SWT.COLOR_RED));

		showCategory();
		showTask();
		initStarTable();
		initRobotResultTable();
		hideTaskButton();
	}

	/**
	 * 
	 * 描述:初始化浏览器
	 * 
	 * @param group_2
	 * @author liyixing 2016年5月6日 上午9:44:13
	 */
	private void initBrowser(Group group_2) {
		if ("IE".equals(System.getProperty("browser"))) {
			logger.info("IE");
			mybrowser = new MyBrowser(group_2, SWT.NONE);
		} else {
			mybrowser = new MyBrowser(group_2, SWT.NONE | SWT.MOZILLA);
			logger.info("MOZILLA");
		}

		mybrowser.setUrl("http://baidu.com");
		mybrowser.setBounds(10, 49, 986, 583);
		mybrowser.setJavascriptEnabled(true);
	}

	/**
	 * 描述:获取选中的类目
	 * 
	 * @author liyixing 2015年9月9日 下午3:18:36
	 * @return
	 */

	private Category getSelectCategory(Combo combo) {
		if (combo.getSelectionIndex() >= 0) {
			return categoriesByName
					.get(combo.getItem(combo.getSelectionIndex()));
		} else {
			return null;
		}
	}

	/**
	 * 描述:获取选中的任务
	 * 
	 * @author liyixing 2015年9月9日 下午3:18:36
	 * @return
	 */

	private Task getSelectTask(Combo combo) {
		if (combo.getSelectionIndex() >= 0) {
			return tasksByName.get(combo.getItem(combo.getSelectionIndex()));
		} else {
			return null;
		}
	}

	/**
	 * 
	 * 描述:把最新的分类显示到下拉框中
	 * 
	 * @author liyixing 2015年9月8日 下午6:46:43
	 */
	public void showCategory() {
		categories = categoryMng.getAll();

		try {
			categoriesById = UtilBean.beansGroupByPkField(categories, "id");
			categoriesByName = UtilBean.beansGroupByPkField(categories, "name");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		combo_1.removeAll();
		combo_3.removeAll();
		combo_4.removeAll();
		combo_1.add("全部分类");
		combo_3.add("全部分类");
		combo_4.add("全部分类");
		for (Category category : categories) {
			combo_1.add(category.getName());
			combo_3.add(category.getName());
			combo_4.add(category.getName());
		}

		combo_1.select(0);
		combo_3.select(0);
		combo_4.select(0);
	}

	/**
	 * 
	 * 描述:把最新的任务显示到下拉框中
	 * 
	 * @author liyixing 2015年9月8日 下午6:46:43
	 */
	@SuppressWarnings("unchecked")
	public void showTask() {
		Task task = new Task();
		tasks = (List<Task>) taskMng.find(task, 1, Integer.MAX_VALUE).getList();

		try {
			tasksById = UtilBean.beansGroupByPkField(tasks, "id");
			tasksByName = UtilBean.beansGroupByPkField(tasks, "name");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		combo_2.removeAll();
		combo.removeAll();
		combo_6.removeAll();
		combo_2.add("全部任务");
		combo.add("全部任务");
		combo_6.add("全部任务");

		for (Task task2 : tasks) {
			combo.add(task2.getName());
			combo_2.add(task2.getName());
			combo_6.add(task2.getName());
		}

		combo_2.select(0);
		combo.select(0);
		combo_6.select(0);
	}

	/**
	 * 
	 * 描述:消息
	 * 
	 * @param text
	 * @author liyixing 2015年8月18日 下午11:21:11
	 */
	private void errorMessage(String text) {
		if (text == null) {
			lblNewLabel_2.setText("消息");
			return;
		}
		// if (errorMessages.size() == 0) {
		// lblNewLabel_2.setText("消息：【" + text + "】");
		// errorMessages.add(text);
		// } else if(errorMessages .size() == 1) {
		// lblNewLabel_2.setText("消息：【" + errorMessages.get(0) + "】\r\n");
		// lblNewLabel_2.setText("消息：【" + text + "】");
		// errorMessages.add(text);
		// }

		lblNewLabel_2.setText("消息：【" + text + "】");
		return;
	}

	/**
	 * 
	 * 描述:初始化Star表格
	 * 
	 * @param group
	 * @author liyixing 2015年8月18日 下午8:01:08
	 */
	private void initStarTable() {
		starPagination = new Pagination();
		starPagination.setPageSize(15);

		/*
		 * NOTE: EraseItem is called repeatedly. Therefore it is critical for
		 * performance that this method be as efficient as possible.
		 */
		// SWT.EraseItem，选择某个行
		// table.addListener(SWT.EraseItem, new Listener() {
		table.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				e.detail &= ~SWT.HOT;
				// if ((e.detail & SWT.SELECTED) == 0)
				// return; /* item not selected */

				if (table.getSelectionIndex() >= 0) {
					TableItem item = table.getItem(table.getSelectionIndex());

					starEdit.setData(Long.valueOf(item.getText(0)));
					text_3.setText(item.getText(1));
					//
					text_4.setText(item.getText(2));
					text_5.setText(item.getText(3));
					Category category = categoriesByName.get(item.getText(4));

					if (category != null) {
						selectCombo(combo_4, category.getName());
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		showStars();
	}

	/**
	 * 
	 * 描述:初始化抓取结果表格
	 * 
	 * @param group
	 * @author liyixing 2015年8月18日 下午8:01:08
	 */
	private void initRobotResultTable() {
		robotResultPagination = new Pagination();
		robotResultPagination.setPageSize(15);

		/*
		 * NOTE: EraseItem is called repeatedly. Therefore it is critical for
		 * performance that this method be as efficient as possible.
		 */
		// SWT.EraseItem，选择某个行
		// table_1.addListener(SWT.EraseItem, new Listener() {
		table_1.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				e.detail &= ~SWT.HOT;
				// if ((e.detail & SWT.SELECTED) == 0)
				// return; /* item not selected */

				if (table_1.getSelectionIndex() >= 0) {
					TableItem item = table_1.getItem(table_1
							.getSelectionIndex());
					robotResultEdit.setData(Long.valueOf(item.getText(0)));
					text_6.setText(item.getText(7));
					text_6.forceFocus();
					text_6.selectAll();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		showRobotResult();
	}

	/**
	 * 
	 * 描述:选定指定的值
	 * 
	 * @param text
	 * @author liyixing 2015年9月9日 下午3:52:05
	 */
	private void selectCombo(Combo combo, String text) {
		for (int i = 0; i < combo.getItems().length; i++) {
			if (text.equals(combo.getItems()[i])) {
				combo.select(i);
				return;
			}
		}
	}

	/**
	 * 
	 * 描述:给明星表格添加显示一条数据
	 * 
	 * @param star
	 * @author liyixing 2015年9月9日 下午3:58:25
	 */
	private void showStarTableItem(Star star) {
		TableItem tableItem = new TableItem(table, SWT.NONE);
		showStarTableItem(star, tableItem);
	}

	/**
	 * 
	 * 描述:给明星表格添加显示一条数据
	 * 
	 * @param star
	 * @author liyixing 2015年9月9日 下午3:58:25
	 */
	private void showStarTableItem(Star star, TableItem tableItem) {
		tableItem.setText(0, star.getId().toString());
		tableItem.setText(1, star.getName());
		tableItem.setText(2, star.getWeiboUrl());
		tableItem.setText(3, star.getTiebaUrl());

		Category category = categoriesById.get(star.getCategoryId());

		if (category != null) {
			tableItem.setText(4, category.getName());
		}

	}

	private void showRobotResultTableItem(RobotResult robotResult,
			TableItem tableItem) {
		tableItem.setText(0, robotResult.getId().toString());
		Star star = starMng.getById(robotResult.getStarId());
		tableItem.setText(1, star.getName());
		Category category = categoriesById.get(robotResult.getCategoryId());

		if (category != null) {
			tableItem.setText(2, category.getName());
		}

		tableItem.setText(3, DateUtils.formatDate(
				robotResult.getStartDateTime(), DateUtils.YYYY_MM_DD_HH_MM_SS));
		tableItem.setText(4, robotResult.getScore().toString());
		tableItem.setText(5, robotResult.getScoreRank().toString());

		// 百度截图
		try {
			if (StringUtils.isNotBlank(robotResult.getBaiduIndexImg())) {
				tableItem.setImage(6, new Image(null, UtilImage
						.base64StringToImg(robotResult.getBaiduIndexImg())
						.getImageData().scaledTo(125, 30)));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		tableItem.setText(7, robotResult.getBaiduIndex().toString());
		tableItem.setText(8, robotResult.getBaiduIndexRank().toString());
		tableItem.setText(9, robotResult.getBaiduNews().toString());
		tableItem.setText(10, robotResult.getBaiduNewsRank().toString());
		tableItem.setText(11, robotResult.getTiebaMemberNumInc().toString());
		tableItem
				.setText(12, robotResult.getTiebaMemberNumIncRank().toString());
		tableItem.setText(13, robotResult.getTiebaPostNumInc().toString());
		tableItem.setText(14, robotResult.getTiebaPostNumIncRank().toString());
		tableItem.setText(15, robotResult.getTiebaSign().toString());
		tableItem.setText(16, robotResult.getTiebaSignRank().toString());
		tableItem.setText(17, robotResult.getWeiboData().toString());
		tableItem.setText(18, robotResult.getWeiboDataRank().toString());
		tableItem.setText(19, robotResult.getWeiboFanInc().toString());
		tableItem.setText(20, robotResult.getWeiboFanIncRank().toString());
		tableItem.setText(21, robotResult.getWeiboForward().toString());
		tableItem.setText(22, robotResult.getWeiboForwardRank().toString());
		tableItem.setText(23, robotResult.getWeiboComment().toString());
		tableItem.setText(24, robotResult.getWeiboCommentRank().toString());
		tableItem.setText(25, robotResult.getWeiboLinkStatus().toString());
		tableItem.setText(26, robotResult.getWeiboLinkStatusRank().toString());
		tableItem.setText(27, robotResult.getWechatNumber().toString());
		tableItem.setText(28, robotResult.getWechatRank().toString());
	}

	/**
	 * 
	 * 描述:给明星表格添加显示一条数据
	 * 
	 * @param star
	 * @author liyixing 2015年9月9日 下午3:58:25
	 */
	private void showRobotResultTableItem(RobotResult robotResult) {
		TableItem tableItem = new TableItem(table_1, SWT.NONE);
		showRobotResultTableItem(robotResult, tableItem);
	}

	/**
	 * 
	 * 描述:刷新结果表格
	 * 
	 * @param star
	 * @author liyixing 2015年9月9日 下午3:58:25
	 */
	private void refreshRobotResultTableItem() {
		showRobotResult();
	}

	/**
	 * 
	 * 描述:明星表格数据覆盖
	 * 
	 * @param star
	 * @author liyixing 2015年9月9日 下午3:58:25
	 */
	private void showNewStarTableItem(Star star) {
		TableItem tableItem = null;

		// id匹配，当先点击查询，或者下一页，上一页之类的按钮，造成数据重新加载，通过getSelectionIndex无法获取
		for (TableItem item : table.getItems()) {
			if (item.getText(0).equals(star.getId().toString())) {
				tableItem = item;

				break;
			}
		}

		if (tableItem == null) {
			return;
		}
		showStarTableItem(star, tableItem);
	}

	/**
	 * 
	 * 描述:显示明星
	 * 
	 * @author liyixing 2015年9月9日 下午5:19:33
	 */
	@SuppressWarnings("unchecked")
	private void showStars() {
		table.removeAll();
		Star star = new Star();

		star.setName(text.getText());

		Category category = getSelectCategory(combo_1);

		if (category != null) {
			star.setCategoryId(category.getId());
		}

		starPagination = starMng.find(star, starPagination.getPageNo());

		for (Star starTemp : (List<Star>) starPagination.getList()) {
			showStarTableItem(starTemp);
		}

		lblNewLabel_5.setText("共" + starPagination.getTotalPage() + "页（"
				+ starPagination.getTotalCount() + "记录），当前第"
				+ starPagination.getPageNo() + "页");
	}

	/**
	 * 
	 * 描述:显示结果
	 * 
	 * @author liyixing 2015年9月9日 下午5:19:33
	 */
	@SuppressWarnings("unchecked")
	private void showRobotResult() {
		table_1.removeAll();
		RobotResult robotResult = new RobotResult();
		Star star = new Star();
		Task task = getSelectTask(combo_2);
		Category category = getSelectCategory(combo_3);

		if (category != null) {
			robotResult.setCategoryId(category.getId());
		}

		if (task != null) {
			robotResult.setTaskId(task.getId());
		}

		star.setName(text_1.getText());

		if (combo_5.getSelectionIndex() >= 0) {
			if (combo_5.getItem(combo_5.getSelectionIndex()).equals("已编辑")) {
				robotResult.setEditStatus(EditStatus.已编辑.toString());
			} else if (combo_5.getItem(combo_5.getSelectionIndex()).equals(
					"未编辑")) {
				robotResult.setEditStatus(EditStatus.未编辑.toString());
			}
		}

		robotResultPagination = robotResultMng.find(robotResult, star,
				robotResultPagination.getPageNo(), 17, "RobotResult.ID DESC");

		lblNewLabel_3.setText("共" + robotResultPagination.getTotalPage() + "页（"
				+ robotResultPagination.getTotalCount() + "记录），当前第"
				+ robotResultPagination.getPageNo() + "页");

		for (RobotResult robotResultTemp : (List<RobotResult>) robotResultPagination
				.getList()) {
			showRobotResultTableItem(robotResultTemp);
		}
	}

	/**
	 * 描述:清理明星编辑数据
	 * 
	 * @author liyixing 2015年9月10日 上午10:26:48
	 */

	private void clearStarEdit() {
		starEdit.setData(null);
		text_3.setText("");
		text_4.setText("");
		text_5.setText("");
	}

	/**
	 * 描述:清理抓取结果编辑数据
	 * 
	 * @author liyixing 2015年9月10日 上午10:26:48
	 */

	public void clearResultEdit() {

		// 清理
		robotResultEdit.setData(null);
		text_6.setText("");
	}

	/**
	 * 描述:
	 * 
	 * @param task
	 * @param stars
	 * @param star
	 * @param thisRobotResult
	 * @author liyixing 2015年9月14日 上午11:09:53
	 */

	private void grab(Map<String, List<String>> options, Task task,
			List<Star> stars) {
		hideTaskButton();

		// 当前按钮和对比按钮
		if (task.getContrastTaskId() != null) {
			Task t = tasksById.get(task.getContrastTaskId());

			if (t != null) {
				selectCombo(combo, t.getName());
			}
		}

		selectCombo(combo_6, task.getName());
		Iterator<Star> starIterator = (Iterator<Star>) stars.iterator();

		if (starIterator.hasNext()) {
			mybrowser.startCheck();
			Star currentStar = starIterator.next();
			RobotResult currentRobotResult = new RobotResult();

			currentRobotResult.setStarId(currentStar.getId());
			currentRobotResult.setTaskId(task.getId());
			RobotResult robotResult = robotResultMng
					.getByTaskAndStar(currentRobotResult);

			if (robotResult != null) {
				currentRobotResult = robotResult;
			} else {
				currentRobotResult.setEditStatus(EditStatus.未编辑.toString());
			}

			currentRobotResult.setResultStatus(ResultStatus.创建.toString());
			currentRobotResult.setCategoryId(currentStar.getCategoryId());
			robot.grabData(options, task, mybrowser, currentStar,
					currentRobotResult, starIterator, new RobotListener() {
						public void completed(Task task, Browser browser,
								Star star, RobotResult robotResult,
								Iterator<Star> starIterator) {
							mybrowser.closeCheck();
							showTask();
							showRobotResult();
							showTaskButton();
							errorMessage("抓取任务完成");
							alertMsg(shell, "抓取任务完成");
						}
					});
		} else {
			errorMessage("没有要抓取的数据");
			alertMsg(shell, "没有要抓取的数据");
			showTaskButton();
		}
	}

	/**
	 * 
	 * 描述:因此任务按钮
	 * 
	 * @author liyixing 2015年9月17日 上午10:19:53
	 */
	public void hideTaskButton() {
		button.setEnabled(false);
		button_7.setEnabled(false);
		button_11.setEnabled(false);
	}

	/**
	 * 
	 * 描述:因此任务按钮
	 * 
	 * @author liyixing 2015年9月17日 上午10:19:53
	 */
	public void showTaskButton() {
		button.setEnabled(true);
		button_7.setEnabled(true);
		button_11.setEnabled(true);
	}

	/**
	 * 导入
	 */
	public void importExcel() {
		FileDialog fileselect = new FileDialog(shell, SWT.SINGLE);
		fileselect.setFilterNames(new String[] { "请选择excel文件" });
		fileselect.setFilterExtensions(new String[] { "*.xls" });
		String url = "";
		url = fileselect.open();

		if (StringUtils.isBlank(url)) {
			return;
		}

		// 初始化表格
		File file = new File(url);

		if (!file.exists()) {
			errorMessage("无效的文件，请重新选择");
			return;
		}

		// 读取文件内容
		Jxl jxl = new Jxl();
		String jpg = null;
		// File jpgFile = null;

		try {
			jxl.initWorkBook(new FileInputStream(file));
			Workbook workbook = jxl.getWorkbook();
			// 生成任务
			Task task = new Task();

			task.setType(TaskType.导入.toString());
			task.setTaskStatus(TaskStatus.完成.toString());

			for (Sheet sheet : workbook.getSheets()) {
				jxl.setSheet(sheet);
				// 处理每张表格
				// 表名=分类
				String categoryName = sheet.getName();
				Category category = categoriesByName.get(categoryName);

				if (category == null) {
					// 保存分类
					category = new Category();
					category.setName(categoryName);
					categoryMng.add(category);
					// 重新读取
					showCategory();
				}

				// 处理结果
				while (jxl.next()) {
					RobotResult robotResult = new RobotResult();
					Star star = new Star();
					String starName = jxl.readCell("姓名");

					star.setName(starName);
					star.setCategoryId(category.getId());
					// 明星是否存在，不存在添加
					Star starR = starMng.getByNameAndCategory(star);

					if (starR == null) {
						logger.info("明星:{}，分类{}，不存在，需要", star.getName(),
								categoryName);
						starMng.add(star);
						starR = star;
					}

					robotResult.setStarId(starR.getId());
					robotResult.setCategoryId(category.getId());
					robotResult.setStartDateTime(DateUtils.parseDate(jxl
							.readCell("抓取日")));

					if (task.getStartDateTime() == null) {
						task.setStartDateTime(new Date());
						taskMng.add(task);
						showTask();

						jpg = file.getParent()
								+ "/"
								+ DateUtils.formatDate(
										robotResult.getStartDateTime(),
										"yyyy_MM_dd_HH_mm_ss");
						// jpgFile = new File(jpg);
					}

					if (jpg != null) {
						try {
							File f = new File(jpg + "/" + starR.getName()
									+ ".jpg");

							if (f.exists()) {
								robotResult.setBaiduIndexImg(UtilImage
										.toBase64(f));
							}
						} catch (Exception e) {

						}
					}

					robotResult.setTaskId(task.getId());
					robotResult.setScore(Integer.valueOf(jxl.readCell("综合分数")));
					robotResult.setScoreRank(Integer.valueOf(jxl
							.readCell("综合排名")));
					robotResult.setBaiduIndex(Integer.valueOf(jxl
							.readCell("百度指数一周平均数")));
					robotResult.setBaiduIndexRank(Integer.valueOf(jxl
							.readCell("百度指数一周平均数排名")));

					robotResult.setBaiduNews(Integer.valueOf(jxl
							.readCell("百度新闻一周收录量(仅限标题)")));
					robotResult.setBaiduNewsRank(Integer.valueOf(jxl
							.readCell("百度新闻一周收录量(仅限标题)排名")));

					robotResult.setTiebaMemberNumInc(Integer.valueOf(jxl
							.readCell("百度贴吧会员一周增长数")));
					robotResult.setTiebaMemberNumIncRank(Integer.valueOf(jxl
							.readCell("百度贴吧会员一周增长数排名")));

					robotResult.setTiebaPostNumInc(Integer.valueOf(jxl
							.readCell("百度贴吧帖子一周增长数")));
					robotResult.setTiebaPostNumIncRank(Integer.valueOf(jxl
							.readCell("百度贴吧帖子一周增长数排名")));

					robotResult.setTiebaSign(Integer.valueOf(jxl
							.readCell("百度贴吧一周签到平均数")));
					robotResult.setTiebaSignRank(Integer.valueOf(jxl
							.readCell("百度贴吧一周签到平均数排名")));

					robotResult.setWeiboData(Integer.valueOf(jxl
							.readCell("新浪微博热议一周总数")));
					robotResult.setWeiboDataRank(Integer.valueOf(jxl
							.readCell("新浪微博热议一周总数排名")));

					robotResult.setWeiboFanInc(Integer.valueOf(jxl
							.readCell("新浪微博粉丝一周增长数")));
					robotResult.setBaiduIndexRank(Integer.valueOf(jxl
							.readCell("新浪微博粉丝一周增长数排名")));

					robotResult.setWeiboForward(Integer.valueOf(jxl
							.readCell("新浪微博近七条平均转发数")));
					robotResult.setWeiboForwardRank(Integer.valueOf(jxl
							.readCell("新浪微博近七条平均转发数排名")));

					robotResult.setWeiboComment(Integer.valueOf(jxl
							.readCell("新浪微博近七条平均评论数")));
					robotResult.setWeiboCommentRank(Integer.valueOf(jxl
							.readCell("新浪微博近七条平均评论数排名")));

					robotResult.setWeiboLinkStatus(Integer.valueOf(jxl
							.readCell("新浪微博近七条平均点赞数")));
					robotResult.setWeiboLinkStatusRank(Integer.valueOf(jxl
							.readCell("新浪微博近七条平均点赞数排名")));

					robotResult.setWechatNumber(Integer.valueOf(jxl
							.readCell("微信文章提及量一周总数")));
					robotResult.setWechatRank(Integer.valueOf(jxl
							.readCell("微信文章提及量一周总数排名")));

					robotResult.setTiebaMemberNum(Integer.valueOf(jxl
							.readCell("百度贴吧会员数")));
					robotResult.setTiebaPostNum(Integer.valueOf(jxl
							.readCell("百度贴吧帖子数")));
					robotResult.setWeiboFan(Integer.valueOf(jxl
							.readCell("新浪微博粉丝数")));
					robotResult.setResultStatus(ResultStatus.完成.toString());
					robotResult.setEditStatus(EditStatus.未编辑.toString());

					robotResultMng.add(robotResult);
				}
			}
			showRobotResult();
			jxl.close();
			errorMessage("导入完成");
		} catch (Exception e) {
			errorMessage("保存文件失败，请选择其他位置");
			logger.error("导入出错", e);
			return;
		}
	}

	/**
	 * 
	 * 描述:导出
	 * 
	 * @author liyixing 2015年9月16日 下午4:10:52
	 */
	@SuppressWarnings("unchecked")
	public void export() {
		// List<Star> stars = starMng.getAll();
		try {
			// Map<String, Star> starsById = UtilBean.beansGroupByPkField(stars,
			// "id");
			RobotResult robotResult = new RobotResult();
			Star star = new Star();
			Task task = getSelectTask(combo_2);

			if (task != null) {
				robotResult.setTaskId(task.getId());
			} else {
				errorMessage("请选择要导出的任务");
				return;
			}

			FileDialog fileselect = new FileDialog(shell, SWT.SINGLE);
			fileselect.setFilterNames(new String[] { "请选择excel文件" });
			fileselect.setFilterExtensions(new String[] { "*.xls" });
			String url = "";
			url = fileselect.open();

			if (StringUtils.isBlank(url)) {
				return;
			}

			if (!url.endsWith(".xls")) {
				url = url + ".xls";
			}

			Pagination pagination = new Pagination();
			// 初始化表格
			File file = new File(url);
			File parent = new File(file.getParent());
			String jpg = file.getParent()
					+ "/"
					+ DateUtils.formatDate(task.getStartDateTime(),
							"yyyy_MM_dd_HH_mm_ss");
			File jpgparent = new File(jpg);

			if (!parent.exists()) {
				parent.mkdirs();
			}

			if (!parent.exists()) {
				parent.mkdirs();
			}

			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					errorMessage("保存文件失败，请选择其他位置");
					return;
				}
			}

			if (!jpgparent.exists()) {
				jpgparent.mkdirs();
			}

			FileOutputStream outputStream = new FileOutputStream(file);
			Jxl jxl = new Jxl();
			jxl.initWorkBook(outputStream, null);
			// 最后一个处理的结果的分类名
			Long lastCategoryId = null;

			do {
				// 先按照分类排序，来保证同一分类连续处理不会出现中断
				pagination = robotResultMng.find(robotResult, star,
						pagination.getPageNo(), 100,
						"RobotResult.categoryId ASC, RobotResult.ID DESC");

				for (RobotResult robotResultTemp : (List<RobotResult>) pagination
						.getList()) {// 需要初始化表格
					Star star2 = starMng.getById(robotResultTemp.getStarId());
					// 保存图片
					try {

						if (StringUtils.isNotBlank(robotResultTemp
								.getBaiduIndexImg())) {
							Image image = new Image(null, UtilImage
									.base64StringToImg(
											robotResultTemp.getBaiduIndexImg())
									.getImageData().scaledTo(125, 30));
							File jpgFile = new File(jpg + "/" + star2.getName()
									+ ".jpg");
							FileOutputStream fileOutputStream = new FileOutputStream(
									jpgFile);

							if (!jpgFile.exists()) {
								jpgFile.createNewFile();
							}
							ImageLoader loader = new ImageLoader();
							loader.data = new ImageData[] { image
									.getImageData() };
							loader.save(fileOutputStream, SWT.IMAGE_JPEG);
							fileOutputStream.close();
						}
					} catch (Exception e) {

					}
					Category category = categoriesById.get(robotResultTemp
							.getCategoryId());

					if (!robotResultTemp.getCategoryId().equals(lastCategoryId)) {
						lastCategoryId = robotResultTemp.getCategoryId();

						if (category == null) {
							jxl.addSheet("未分类");
						} else {
							jxl.addSheet(category.getName());
						}

						for (TableColumn tableColumn : table_1.getColumns()) {
							if (tableColumn.getText().equals("百度指数截图")) {
								continue;
							}
							jxl.addCell(tableColumn.getText());
						}

						jxl.addCell("百度贴吧会员数");
						jxl.addCell("百度贴吧帖子数");
						jxl.addCell("新浪微博粉丝数");
					}

					jxl.nextWrite();

					jxl.addCell(robotResultTemp.getId().toString());
					jxl.addCell(star2.getName());
					jxl.addCell(category == null ? "未分类" : category.getName());
					jxl.addCell(DateUtils.formatDate(
							robotResultTemp.getStartDateTime(),
							DateUtils.YYYY_MM_DD_HH_MM_SS));
					jxl.addCell(robotResultTemp.getScore().toString());
					jxl.addCell(robotResultTemp.getScoreRank().toString());
					jxl.addCell(robotResultTemp.getBaiduIndex().toString());
					jxl.addCell(robotResultTemp.getBaiduIndexRank().toString());
					jxl.addCell(robotResultTemp.getBaiduNews().toString());
					jxl.addCell(robotResultTemp.getBaiduNewsRank().toString());
					jxl.addCell(robotResultTemp.getTiebaMemberNumInc()
							.toString());
					jxl.addCell(robotResultTemp.getTiebaMemberNumIncRank()
							.toString());
					jxl.addCell(robotResultTemp.getTiebaPostNumInc().toString());
					jxl.addCell(robotResultTemp.getTiebaPostNumIncRank()
							.toString());
					jxl.addCell(robotResultTemp.getTiebaSign().toString());
					jxl.addCell(robotResultTemp.getTiebaSignRank().toString());
					jxl.addCell(robotResultTemp.getWeiboData().toString());
					jxl.addCell(robotResultTemp.getWeiboDataRank().toString());
					jxl.addCell(robotResultTemp.getWeiboFanInc().toString());
					jxl.addCell(robotResultTemp.getWeiboFanIncRank().toString());
					jxl.addCell(robotResultTemp.getWeiboForward().toString());
					jxl.addCell(robotResultTemp.getWeiboForwardRank()
							.toString());
					jxl.addCell(robotResultTemp.getWeiboComment().toString());
					jxl.addCell(robotResultTemp.getWeiboCommentRank()
							.toString());
					jxl.addCell(robotResultTemp.getWeiboLinkStatus().toString());
					jxl.addCell(robotResultTemp.getWeiboLinkStatusRank()
							.toString());
					jxl.addCell(robotResultTemp.getWechatNumber().toString());
					jxl.addCell(robotResultTemp.getWechatRank().toString());
					jxl.addCell(robotResultTemp.getTiebaMemberNum().toString());
					jxl.addCell(robotResultTemp.getTiebaPostNum().toString());
					jxl.addCell(robotResultTemp.getWeiboFan().toString());
				}

				if (pagination.isLastPage()) {
					break;
				}
				pagination.setPageNo(pagination.getNextPage());
			} while (true);

			errorMessage("处理完成");
			jxl.close();
			outputStream.close();
		} catch (Exception e) {
			errorMessage("保存文件失败，请选择其他位置");
			return;
		}
	}

	private void saveBaiduIndex() {
		if (StringUtils.isBlank(text_6.getText())) {
			errorMessage("请输入百度指数");
			return;
		}

		if (!NumberUtils.isNumber(text_6.getText())) {
			errorMessage("百度指数必须是数字");
			return;
		}

		if (robotResultEdit.getData() == null) {
			errorMessage("请选择要编辑的数据");
			return;
		}

		if (text_6.getText().equals("0")) {
			return;
		}

		// 保存
		RobotResult robotResult = robotResultMng.getById((Long) robotResultEdit
				.getData());
		Map<String, List<String>> options = taskOptionMng.getMapByTaskAndName(
				robotResult.getTaskId(), TaskOptionType.维度.toString(), null);
		robotResult.setBaiduIndex(Integer.valueOf(text_6.getText()));
		robotResult.setEditStatus(EditStatus.已编辑.toString());
		robotResultMng.save(robotResult);
		robotResultMng.countRank(options, robotResult);
		robotResultMng.countScore(robotResult);
		// showNewRobotResultTableItem(robotResult);
		refreshRobotResultTableItem();
		clearResultEdit();
	}

	public static void alertMsg(Shell shell, String msg) {
		MessageBox alertBox = new MessageBox(shell);
		alertBox.setMessage(msg);
		alertBox.setText("信息");
		alertBox.open();
	}
}
