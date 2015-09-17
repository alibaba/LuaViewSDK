package com.alibaba.lvdebug;

import com.alibaba.lvdebug.ui.DebuggerFrame;
import com.alibaba.lvdebug.ui.SrcCodeCenter;
import com.alibaba.lvdebug.ui.Updater;

public class Center {
	public DebuggerFrame frame;
	public SrcCodeCenter srcCodeCenter;
	public Updater updater;
	public Log log;
	public ClientCmdBuffer cmdBuffer;

	public Center() {
		cmdBuffer = new ClientCmdBuffer(this);
		srcCodeCenter = new SrcCodeCenter(this);
		this.updater = new Updater(this);
		this.log = new Log(this);
	}

}
