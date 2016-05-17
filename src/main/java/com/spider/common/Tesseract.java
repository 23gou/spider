package com.spider.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * 描述:图像识别
 *
 * @author liyixing
 * @version 1.0
 * @since 2015年9月14日 上午10:26:41
 */
public class Tesseract {
	public static final String tesseract = Tesseract.class
			.getResource("/tesseract-ocr/tesseract.exe").getFile().substring(1);
	private static final String EOL = System.getProperty("line.separator");

	/**
	 * @param imageFile
	 *            传入的图像文件
	 * @param imageFormat
	 *            传入的图像格式
	 * @return 识别后的字符串
	 */
	public static final String recognizeText(File imageFile) throws Exception {
		/**
		 * 设置输出文件的保存的文件目录
		 */
		File outputFile = new File(imageFile.getParentFile(), "output.txt");

		StringBuffer strB = new StringBuffer();
		List<String> cmd = new ArrayList<String>();
		cmd.add(tesseract);
		cmd.add(imageFile.getName());
		cmd.add(outputFile.getName());
		// cmd.add("chi_sim");
		cmd.add("-l eng");
		cmd.add("-psm 2");

		ProcessBuilder pb = new ProcessBuilder();

		/**
		 * Sets this process builder's working directory.
		 */
		pb.directory(imageFile.getParentFile());
		pb.command(cmd);
		pb.redirectErrorStream(true);
		Process process = pb.start();
		// tesseract.exe 1.jpg 1 -l chi_sim
		// Runtime.getRuntime().exec("tesseract.exe 1.jpg 1 -l chi_sim");
		/**
		 * the exit value of the process. By convention, 0 indicates normal
		 * termination.
		 */
		// System.out.println(cmd.toString());
		int w = process.waitFor();

		if (w == 0)// 0代表正常退出
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(outputFile.getAbsolutePath() + ".txt"),
					"UTF-8"));
			String str;

			while ((str = in.readLine()) != null) {
				strB.append(str).append(EOL);
			}
			in.close();
		} else {
			String msg;
			switch (w) {
			case 1:
				msg = "Errors accessing files. There may be spaces in your image's filename.";
				break;
			case 29:
				msg = "Cannot recognize the image or its selected region.";
				break;
			case 31:
				msg = "Unsupported image format.";
				break;
			default:
				msg = "Errors occurred.";
			}
			throw new RuntimeException(msg);
		}
//		new File(outputFile.getAbsolutePath() + ".txt").delete();
		return strB.toString().replaceAll("\\s*", "");
	}
}