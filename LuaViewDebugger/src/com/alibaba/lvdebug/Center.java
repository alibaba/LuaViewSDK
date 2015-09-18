package com.alibaba.lvdebug;

import com.alibaba.lvdebug.ui.DebuggerFrame;
import com.alibaba.lvdebug.ui.SrcCodeCenter;
import com.alibaba.lvdebug.ui.Updater;

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
