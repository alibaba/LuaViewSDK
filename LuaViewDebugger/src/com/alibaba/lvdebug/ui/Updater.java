package com.alibaba.lvdebug.ui;

import com.alibaba.lvdebug.Center;
import com.alibaba.lvdebug.ui.UICmd;

public final class Updater {
	public final String CMD_NAME = "Cmd-Name:".toLowerCase();
	public final String FILE_NAME = "File-Name:".toLowerCase();

	private final Center center;

	public Updater(Center center) {
		this.center = center;
	}

	private UICmd readCmd(String string) throws Exception {
		String[] heads = null;
		String info = null;
		{// parse head
			int index = string.indexOf("\n\n");
			if (index >= 0) {
				String headString = string.substring(0, index);
				info = string.substring(index + 2);
				heads = headString.split("\n");
			}
		}
		String cmdName = null;
		String fileName = null;
		for (int i = 0; i < heads.length; i++) {
			String s = heads[i];
			if (s != null && s.length() > 0) {
				String str = s.toLowerCase();
				if (str.startsWith(CMD_NAME)) {
					cmdName = s.substring(CMD_NAME.length());
				} else if (str.startsWith(FILE_NAME)) {
					fileName = s.substring(FILE_NAME.length());
				}
			}
		}
		UICmd cmd = new UICmd(cmdName, fileName, info);
		return cmd;
	}

	public void run(String string) {
		try {
			UICmd cmd = this.readCmd(string);
			if (cmd.cmd != null) {
				if ("log".equals(cmd.cmd)) {
					center.log.print(cmd.content);
				} else if ("loadfile".equals(cmd.cmd)) {
					this.center.srcCodeCenter.loadfile(cmd.fileName, cmd.content);
					center.frame.setTitle(cmd.fileName);
				} else if ("running".equals(cmd.cmd)) {
					this.center.srcCodeCenter.running(cmd.fileName, cmd.content);
				} else {
					System.err.println("unkonw cmd: " + cmd.cmd);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}