package com.alibaba.lvdebug;

import com.alibaba.lvdebug.ui.DebuggerFrame;

//"h             help info"
//"c             continue"
//"s             trace"
//"n             next"
//"p var         print variable"
//"b src:line    add breakpoint"
//"d num         del breakpoint"
//"bl            list breakpoint"
//"be num        enable breakpoint"
//"bd num        disable breakpoint"
//"bt            print traceback"

public final class ClientCmd {

	public final String cmdString;

	public String toString() {
		return "Cmd: " + cmdString;
	}

	public ClientCmd(String cmd) {
		if (cmd != null) {
			this.cmdString = cmd;
		} else {
			this.cmdString = "none";
		}
	}

	private static ClientCmd currentCmd;

	public static void setIsDebugging(boolean yes) {
		DebuggerFrame.frame.getButtonNextLine().setEnabled(yes);
		DebuggerFrame.frame.getButtonNextCodeLine().setEnabled(yes);
		DebuggerFrame.frame.getButtonNextBreakPoint().setEnabled(yes);
		DebuggerFrame.frame.getTextFieldCmdInput().setEnabled(yes);
	}

	public static synchronized void pushCmd(ClientCmd cmd) {
		if (currentCmd != null) {
			currentCmd = null;
			Log.print("!!!操作被忽略 (调试已经结束/断开/或者操作太快)\n");
		}
		currentCmd = cmd;
	}

	public static synchronized ClientCmd popCmd() {
		ClientCmd cmd = currentCmd;
		currentCmd = null;
		return cmd;
	}

}
