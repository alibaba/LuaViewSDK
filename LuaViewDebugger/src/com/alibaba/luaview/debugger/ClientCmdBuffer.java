package com.alibaba.luaview.debugger;

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

public final class ClientCmdBuffer {

	private final Center center;
 

	public ClientCmdBuffer(Center center) {
		this.center = center;
	}

	private ClientCmd currentCmd;



	public synchronized void pushCmd(ClientCmd cmd) {
		if (currentCmd != null) {
			currentCmd = null;
			center.log.print("!!!操作被忽略 (调试已经结束/断开/或者操作太快)\n");
		}
		currentCmd = cmd;
	}

	public synchronized ClientCmd popCmd() {
		ClientCmd cmd = currentCmd;
		currentCmd = null;
		return cmd;
	}

}
