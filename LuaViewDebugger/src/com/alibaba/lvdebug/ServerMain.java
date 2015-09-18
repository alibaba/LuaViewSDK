package com.alibaba.lvdebug;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.*;
import com.alibaba.lvdebug.ui.DebuggerFrame;

public final class ServerMain {
	public static final String IP = "127.0.0.1";

	public static final int PORT = 9876;

	private ServerSocket serverSocket;

	public void main() {
		Runnable run = new Runnable() {
			public void run() {
				try {
					new ServerMain().mainThread();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(run).start();
	}

	private void mainThread() throws Exception {
		serverSocket = new ServerSocket(PORT);
		while (serverSocket.isBound()) {
			Socket connection = serverSocket.accept();
			connection.setSoTimeout(120000 * 1000);// 超时时间
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