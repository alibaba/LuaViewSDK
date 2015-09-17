package com.alibaba.lvdebug;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public final class ExitHttpRequest {

	public static final String EXIT_APP = "Exit-App".toLowerCase();

	public static String sendExitCmd() {
		String result = null;
		try {
			Socket socket = new Socket("127.0.0.1", ServerMain.PORT);

			DataInputStream dis = new DataInputStream(socket.getInputStream());
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			Server.writeString("exit", dos);
			result = Server.readString(dis);
			socket.close();
			Thread.sleep(1000);
		} catch (Exception e) {
		} finally {
		}
		return result;
	}
}
