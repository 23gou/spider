package com.spider.view;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.internal.ole.win32.COM;
import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class A {
	static final String PROPERTY_DOCUMENT = "Document"; //$NON-NLS-1$
	protected Shell shell;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Device.DEBUG = true;

		System.setProperty("org.eclipse.swt.browser.XULRunnerPath",
				System.getProperty("user.dir") + "/xulrunner");
		// System.setProperty("org.eclipse.swt.browser.DefaultType", "mozilla");
		try {
			A window = new A();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
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
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		final Browser browser = new Browser(shell, SWT.NONE );
		browser.setBounds(28, 160, 64, 64);

		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browser.setUrl("http://www.baidu.com");
			}
		});
		btnNewButton.setBounds(12, 10, 80, 27);
		btnNewButton.setText("New Button");

		Button btnNewButton_1 = new Button(shell, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Browser browser1 = browser;
				browser1.evaluate("return document.title");
				browser1.evaluate("return document.documentElement.outerHTML");

				String s = browser.getText();
				System.out.println(s);
				s = browser.getText();
				OleAutomation auto = null;

				try {
					Object webbrower = FieldUtils.getDeclaredField(
							Browser.class, "webBrowser", true).get(browser1);
					auto = (OleAutomation) FieldUtils.getDeclaredField(
							webbrower.getClass(), "auto", true).get(webbrower);
				} catch (IllegalArgumentException e1) {
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				}
				/* get the document object */
				int[] rgdispid = auto
						.getIDsOfNames(new String[] { PROPERTY_DOCUMENT });
				Variant pVarResult = auto.getProperty(rgdispid[0]);
				s = pVarResult.getString();
				if (pVarResult == null || pVarResult.getType() == COM.VT_EMPTY) {
					if (pVarResult != null)
						pVarResult.dispose();
					return; //$NON-NLS-1$
				}
				OleAutomation document = pVarResult.getAutomation();
				s = pVarResult.getString();
				pVarResult.dispose();

				/* get the html object */
				rgdispid = document
						.getIDsOfNames(new String[] { "documentElement" }); //$NON-NLS-1$
				s = pVarResult.getString();
				if (rgdispid == null) {
					/* implies that the browser is displaying non-HTML content */
					document.dispose();
					return; //$NON-NLS-1$
				}
				pVarResult = document.getProperty(rgdispid[0]);
				s = pVarResult.getString();
				document.dispose();
				if (pVarResult == null || pVarResult.getType() == COM.VT_EMPTY) {
					if (pVarResult != null)
						pVarResult.dispose();
					return; //$NON-NLS-1$
				}
				OleAutomation element = pVarResult.getAutomation();
				pVarResult.dispose();

				/* get its outerHTML property */
				rgdispid = element.getIDsOfNames(new String[] { "outerHTML" }); //$NON-NLS-1$
				pVarResult = element.getProperty(rgdispid[0]);
				s = pVarResult.getString();
				element.dispose();
				if (pVarResult == null || pVarResult.getType() == COM.VT_EMPTY) {
					if (pVarResult != null)
						pVarResult.dispose();
					return; //$NON-NLS-1$
				}
				String result = pVarResult.getString();
				pVarResult.dispose();

				return;

			}
		});
		btnNewButton_1.setBounds(111, 10, 80, 27);
		btnNewButton_1.setText("New Button");

	}
}
