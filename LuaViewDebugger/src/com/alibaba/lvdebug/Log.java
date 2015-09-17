package com.alibaba.lvdebug;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTextArea;

public final class Log {
	private Center center;

	public Log(Center center) {
		this.center = center;
	}

	public synchronized void print(String s) {
		if (s == null) {
			s = "";
		}
		if (center.frame != null) {
			JTextArea textArea = center.frame.getOutputArea();
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
	private SimpleDateFormat dateFormat = new SimpleDateFormat("[MM-dd HH:mm:ss.SSS]  ");

	public String date() {
		return dateFormat.format(new Date());
	}
}
