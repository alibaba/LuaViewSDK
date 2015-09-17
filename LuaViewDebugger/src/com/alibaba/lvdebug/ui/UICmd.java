package com.alibaba.lvdebug.ui;

public class UICmd {

	public final String cmd;// log/load

	public final String fileName;

	public final String content;

	public UICmd(String cmd, String fileName, String info) {
		this.cmd = trim(cmd);
		this.fileName = trim(fileName);
		this.content = info;
	}

	private String trim(String s) {
		if (s != null) {
			s = s.trim();
			if (s.startsWith(":")) {
				s = s.substring(1);
			}
			return s.trim();
		}
		return s;
	}

}
