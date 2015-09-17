package com.alibaba.lvdebug;

import java.io.IOException;
import java.net.*;

public final class ServerMain {

	public static final String IP = "127.0.0.1";
	public static final int PORT = 9876;
	private static ServerSocket serverSocket;

	public static void main() {
		Runnable run = new Runnable() {
			public void run() {
				try {
					mainThread();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(run).start();
	}

	private static void mainThread() throws Exception {
		serverSocket = new ServerSocket(PORT);
		while (serverSocket.isBound()) {
			Socket connection = serverSocket.accept();
			connection.setSoTimeout(120000 * 1000);// 超时时间
			Server worker = new Server(connection);
			worker.run();
		}
	}

	public static void close() {
		try {
			if (serverSocket != null)
				serverSocket.close();
			serverSocket = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}