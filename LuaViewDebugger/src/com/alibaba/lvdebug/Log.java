package com.alibaba.lvdebug;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTextArea;

import com.alibaba.lvdebug.ui.DebuggerFrame;

public final class Log {
	public static synchronized void print(String s) {
		if (s == null) {
			s = "";
		}
		if (DebuggerFrame.frame != null) {
			JTextArea textArea = DebuggerFrame.frame.getOutputArea();
			{
				String text = textArea.getText();
				if (text.length() <= 0 || text.endsWith("\n")) {
					s = date() + s;
				}
			}
			textArea.append(s);
			textArea.setCaretPosition(textArea.getText().length());
		}
	}

	// 设置日期格式
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("[MM-dd HH:mm:ss.SSS]  ");

	public static String date() {
		return dateFormat.format(new Date());
	}
}
