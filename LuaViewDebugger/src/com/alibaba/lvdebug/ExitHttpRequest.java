package com.alibaba.lvdebug;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public final class ExitHttpRequest {

	public final String EXIT_APP = "Exit-App".toLowerCase();

	public String sendExitCmd() {
		String result = null;
		try {
			Socket socket = new Socket(ServerMain.IP, ServerMain.PORT);

			DataInputStream dis = new DataInputStream(socket.getInputStream());
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			Util.writeString("exit", dos);
			result = Util.readString(dis);
			socket.close();
			Thread.sleep(1000);
		} catch (Exception e) {
		} finally {
		}
		return result;
	}
}
