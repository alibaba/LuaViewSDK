package com.alibaba.lvdebug;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.alibaba.lvdebug.ui.DebuggerFrame;

public class Main {

	public static void main(String[] args) {
		new Main().main();
	}

	private ServerSocket serverSocket;

	public void main() {
		// 关闭老的
		String ret = new ExitHttpRequest().sendExitCmd();
		if (ret != null && "ok".equals(ret.trim())) {
			System.out.println("关闭...");
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("启动...");

		{
			DebuggerFrame frame = new DebuggerFrame(null);
			frame.dispose();
		}

		// 启动主控制窗口
		MainFrame.main();

		// 启动socket调试端口监听服务
		try {
			openSocketListner();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void openSocketListner() throws Exception {
		serverSocket = new ServerSocket(Config.PORT);
		while (serverSocket.isBound()) {
			Socket connection = serverSocket.accept();
			connection.setSoTimeout(60 * 60 * 24 * 1000);// 超时时间
			Center center = new Center(this);
			center.frame = debuggerFrameMain(center);
			Server worker = new Server(connection, center);
			worker.run();
			// 创建一个tab
			center.srcCodeCenter.showHelpTab();
		}
	}

	private DebuggerFrame debuggerFrameMain(Center center) {
		// 获取屏幕大小
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int w = screenSize.width;
		int h = screenSize.height;

		// 创建界面
		DebuggerFrame frame = new DebuggerFrame(center);

		// 设置窗口大小位置
		frame.setLocation(w / 2, 0);
		frame.setSize(w / 2, h);
		frame.setVisible(true);

		frame.setIsDebugging(true);
		frame.setVisible(true);
		// center.log.print("等待...手机链接到调试器\n");
		return frame;
	}

	public void close() {
		try {
			if (serverSocket != null)
				serverSocket.close();
			serverSocket = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
