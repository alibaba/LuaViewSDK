package com.alibaba.lvdebug;

import java.awt.Dimension;
import java.awt.Toolkit;

import com.alibaba.lvdebug.ui.DebuggerFrame;
import com.alibaba.lvdebug.ui.SrcCodeCenter;

public class Main {

	public static void main(String[] args) {
		// 关闭老的
		String ret = ExitHttpRequest.sendExitCmd();
		if (ret != null && "ok".equals(ret.trim())) {
			System.out.println("关闭...");
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("启动...");

		// 打开调试器窗口
		debuggerFrameMain();

		// 启动socket调试端口监听服务
		ServerMain.main();

		// 创建一个tab
		SrcCodeCenter.showHelpTab();
	}

	private static void debuggerFrameMain() {
		// 获取屏幕大小
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int w = screenSize.width;
		int h = screenSize.height;

		// 创建界面
		DebuggerFrame frame = new DebuggerFrame();

		// 设置窗口大小位置
		frame.setLocation(w / 2, 0);
		frame.setSize(w / 2, h);
		frame.setVisible(true);

		ClientCmd.setIsDebugging(true);
		Log.print("等待...手机链接到调试器\n");
	}
}
