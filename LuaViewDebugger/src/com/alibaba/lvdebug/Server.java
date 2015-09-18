package com.alibaba.lvdebug;

import java.io.*;
import java.net.*;
import com.alibaba.lvdebug.ClientCmd;

public final class Server {
	private Socket connection;
	private DataOutputStream writer = null;
	private DataInputStream reader = null;
	private Center center;

	public Server(Socket connection, Center center) {
		super();
		this.connection = connection;
		try {
			reader = new DataInputStream(connection.getInputStream());
			writer = new DataOutputStream(connection.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.center = center;
	}

	private void writeString(String s) throws Exception {
		Util.writeString(s, writer);
	}

	private String readString() throws Exception {
		return Util.readString(reader);
	}

	private boolean continueRun = true;

	public void run() {
		Runnable runReader = new Runnable() {
			@Override
			public void run() {
				try {
					for (; continueRun;) {
						Thread.sleep(10);
						String string = readString();
						if (string == null || string.length() <= 0) {
							continue;
						}
						System.out.println("Received Cmd: " + string);
						if ("exit".equalsIgnoreCase(string)) {
							center.serverMain.close();
							System.exit(0);
							return;
						} else {
							center.updater.run(string);
						}
					}
				} catch (Exception e) {
					// e.printStackTrace();
				} finally {
					close();
				}
				System.out.println("----------调试结束1-----------");
			}

		};
		Runnable runWriter = new Runnable() {
			@Override
			public void run() {
				try {
					for (; continueRun;) {
						ClientCmd cmd = center.cmdBuffer.popCmd();
						if (cmd != null) {
							System.out.println("Send Client Cmd: " + cmd);
							writeString(cmd.cmdString);
						}
						Thread.sleep(10);
					}
				} catch (Exception e) {
					// e.printStackTrace();
				} finally {
					close();
				}
				System.out.println("----------调试结束2-----------");
			}

		};
		new Thread(runReader).start();
		new Thread(runWriter).start();
	}

	public void close() {
		try {
			this.continueRun = false;
			connection.close();
			reader.close();
			writer.close();
		} catch (Exception e) {
		}
	}
}