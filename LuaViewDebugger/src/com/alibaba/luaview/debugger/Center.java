package com.alibaba.luaview.debugger;

import com.alibaba.luaview.debugger.ui.DebuggerFrame;
import com.alibaba.luaview.debugger.ui.SrcCodeCenter;
import com.alibaba.luaview.debugger.ui.Updater;

public class Center {
	public DebuggerFrame frame;
	public final SrcCodeCenter srcCodeCenter;
	public final Updater updater;
	public final Log log;
	public final ClientCmdBuffer cmdBuffer;
	public final Main main;
	public Server worker;

	public Center(Main serverMain) {
		this.main = serverMain;
		this.cmdBuffer = new ClientCmdBuffer(this);
		this.srcCodeCenter = new SrcCodeCenter(this);
		this.updater = new Updater(this);
		this.log = new Log(this);
	}

}
