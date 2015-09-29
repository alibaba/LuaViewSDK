package com.alibaba.luaview.debugger;

public class ClientCmd {

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

}
