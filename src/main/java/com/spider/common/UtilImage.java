package com.spider.common;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.springframework.util.StreamUtils;

/**
 * 图片处理
 * 
 **/

public class UtilImage {
	public static void main(String[] args) throws Exception {
		UtilImage capture = UtilImage.getInstance();
		capture.select();
	}

	private UtilImage() {

		try {
			robot = new Robot();
		} catch (AWTException e) {
			System.err.println("Internal Error: " + e);
			e.printStackTrace();
		}
		JPanel cp = (JPanel) dialog.getContentPane();
		cp.setLayout(new BorderLayout());
		// 鼠标松开
		labFullScreenImage.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent evn) {
				isFirstPoint = true;
				dialog.setVisible(false);
			}
		});

		// 鼠标拖动
		labFullScreenImage.addMouseMotionListener(new MouseMotionAdapter() {
			// 鼠标拖拽事件,在鼠标拖动的时候触发
			public void mouseDragged(MouseEvent evn) {
				if (isFirstPoint) {
					// 鼠标第一次按下
					x1 = evn.getX();
					y1 = evn.getY();
					isFirstPoint = false;
				} else {
					// 非第一次按下
					x2 = evn.getX();
					y2 = evn.getY();
					// 鼠标可能往左移动或者往右移动
					int maxX = Math.max(x1, x2);
					int maxY = Math.max(y1, y2);
					int minX = Math.min(x1, x2);
					int minY = Math.min(y1, y2);
					recX = minX;
					recY = minY;
					recW = maxX - minX;
					recH = maxY - minY;
					labFullScreenImage.drawRectangle(recX, recY, recW, recH);
					isSelect = true;
				}

				System.out.println(evn.getX() + "," + evn.getY() + ",");
			}

			/**
			 * 鼠标移动，重新画线
			 * 
			 * @see java.awt.event.MouseMotionAdapter#mouseMoved(java.awt.event.MouseEvent)
			 */
			public void mouseMoved(MouseEvent e) {
				labFullScreenImage.drawCross(e.getX(), e.getY());
			}
		});

		cp.add(BorderLayout.CENTER, labFullScreenImage);
		dialog.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		dialog.setAlwaysOnTop(true);
		dialog.setMaximumSize(Toolkit.getDefaultToolkit().getScreenSize());
		dialog.setUndecorated(true);
		dialog.setSize(dialog.getMaximumSize());
		dialog.setModal(true);
	}

	// Singleton Pattern
	public static UtilImage getInstance() {
		return defaultCapturer;
	}

	/** 捕捉全屏慕 */
	public Icon captureFullScreen() {
		fullScreenImage = robot.createScreenCapture(new Rectangle(Toolkit
				.getDefaultToolkit().getScreenSize()));
		ImageIcon icon = new ImageIcon(fullScreenImage);
		return icon;
	}

	/** 捕捉屏幕的一个矫形区域 */
	public void select() {
		/**
		 * 由于AWT的窗口工具是白地屏幕，会把底层的屏幕挡住，因此先截图底层屏幕，再显示窗口，然后把截图出来的数据显示在窗口中，
		 * 来完成对底层屏幕的区域选择
		 */
		fullScreenImage = robot.createScreenCapture(new Rectangle(Toolkit
				.getDefaultToolkit().getScreenSize()));
		ImageIcon icon = new ImageIcon(fullScreenImage);
		labFullScreenImage.setIcon(icon);
		dialog.setVisible(true);
	}

	/**
	 * 
	 * 描述:给浏览器添加坐标获取函数
	 * 
	 * @param browser
	 * @author liyixing 2015年9月15日 下午4:48:11
	 * @return
	 */
	public static final Position getPosition(Browser browser, String object) {
		String xScript = "function getLeft(e){var offset=e.offsetLeft;if(e.offsetParent!=null) offset+=getLeft(e.offsetParent);return offset;} return "
				+ "getLeft(" + object + ")";
		String yScript = "function getTop(e){var offset=e.offsetTop;if(e.offsetParent!=null) offset+=getTop(e.offsetParent);return offset;} return "
				+ "getTop(" + object + ")";
		String wScript = "return " + object + ".offsetWidth";
		String hScript = "return " + object + ".offsetHeight";
		Position position = new Position();

		position.x = Integer.valueOf(new BigDecimal(browser.evaluate(xScript)
				.toString()).intValue());
		position.y = Integer.valueOf(new BigDecimal(browser.evaluate(yScript)
				.toString()).intValue());
		position.w = Integer.valueOf(new BigDecimal(browser.evaluate(wScript)
				.toString()).intValue());
		position.h = Integer.valueOf(new BigDecimal(browser.evaluate(hScript)
				.toString()).intValue());

		return position;
	}

	/**
	 * 改变图片的大小
	 * 
	 * @param source
	 *            源文件
	 * @param targetW
	 *            目标长
	 * @param targetH
	 *            目标宽
	 * @return
	 */
	public static final BufferedImage resize(BufferedImage source, int targetW,
			int targetH) {
		int type = source.getType();
		BufferedImage target = null;
		double sx = (double) targetW / source.getWidth();
		double sy = (double) targetH / source.getHeight();
		// 这里想实现在targetW，targetH范围内实现等比缩放。如果不需要等比缩放
		// 则将下面的if else语句注释即可
		if (sx > sy) {
			sx = sy;
			targetW = (int) (sx * source.getWidth());
		} else {
			sy = sx;
			targetH = (int) (sy * source.getHeight());
		}
		if (type == BufferedImage.TYPE_CUSTOM) { // handmade
			ColorModel cm = source.getColorModel();
			WritableRaster raster = cm.createCompatibleWritableRaster(targetW,
					targetH);
			boolean alphaPremultiplied = cm.isAlphaPremultiplied();
			target = new BufferedImage(cm, raster, alphaPremultiplied, null);
		} else {
			target = new BufferedImage(targetW, targetH, type);
		}
		Graphics2D g = target.createGraphics();
		// smoother than exlax:
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
		g.dispose();
		return target;
	}

	/**
	 * 
	 * 描述:截取
	 * 
	 * @author liyixing 2015年9月12日 下午4:17:41
	 */
	public BufferedImage createScreenCapture() {
		return robot.createScreenCapture(new Rectangle(recX, recY, recW, recH));

	}

	/**
	 * 
	 * 描述:截取
	 * 
	 * @author liyixing 2015年9月12日 下午4:17:41
	 */
	public BufferedImage createScreenCapture(Position position) {
		return robot.createScreenCapture(new Rectangle(position.x, position.y,
				position.w, position.h));

	}

	/**
	 * base 64 string to image
	 * 
	 * @param base64String
	 * @return
	 * @throws IOException
	 */
	public static final Image base64StringToImg(final String base64String)
			throws IOException {
		InputStream stream = new ByteArrayInputStream(
				Base64.decodeBase64(base64String));
		try {
			Display display = Display.getCurrent();
			ImageData data = new ImageData(stream);
			if (data.transparentPixel > 0) {
				return new Image(display, data, data.getTransparencyMask());
			}
			return new Image(display, data);
		} finally {
			stream.close();
		}
	}

	/**
	 * 
	 * 描述:图片转化成64位
	 * 
	 * @param bufferedImage
	 * @return
	 * @author liyixing 2015年9月14日 下午1:49:57
	 * @throws IOException
	 */
	public static final String toBase64(File f) throws IOException {
		FileInputStream in = new FileInputStream(f);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		StreamUtils.copy(in, outputStream);
		// 生成64位
		in.close();
		String imageString = Base64
				.encodeBase64String(outputStream.toByteArray());
		outputStream.close();

		return imageString;
	}

	// singleton design pattern
	private static UtilImage defaultCapturer = new UtilImage();
	private int x1, y1, x2, y2;
	public int recX, recY, recH, recW; // 截取的图像
	private boolean isFirstPoint = true;
	private BackgroundImage labFullScreenImage = new BackgroundImage();
	private Robot robot;
	// 是否已经选择
	public boolean isSelect = false;
	/**
	 * 用于显示全屏屏幕截图的图片
	 */
	private BufferedImage fullScreenImage;
	private JDialog dialog = new JDialog();

	/**
	 * 
	 * 
	 * 描述:坐标
	 *
	 * @author liyixing
	 * @version 1.0
	 * @since 2015年9月15日 下午5:02:28
	 */
	public static class Position {
		public int x;
		public int y;
		public int w;
		public int h;
	}
}

/** 显示图片的Label */
class BackgroundImage extends JLabel {

	/**
	 * 描述：
	 */
	private static final long serialVersionUID = 1L;

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawRect(x, y, w, h);
		String area = Integer.toString(w) + " * " + Integer.toString(h);
		g.drawString(area, x + (int) w / 2 - 15, y + (int) h / 2);
		g.drawLine(lineX, 0, lineX, getHeight());

		g.drawLine(0, lineY, getWidth(), lineY);
	}

	public void drawRectangle(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		h = height;
		w = width;
		repaint();
	}

	public void drawCross(int x, int y) {
		lineX = x;
		lineY = y;
		repaint();
	}

	int lineX, lineY;
	int x, y, h, w;
}