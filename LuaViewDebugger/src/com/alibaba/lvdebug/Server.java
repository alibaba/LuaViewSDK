package com.alibaba.lvdebug;

import java.io.*;
import java.net.*;

import com.alibaba.lvdebug.ui.Updater;

public final class Server {
	private Socket connection;
	private DataOutputStream writer = null;
	private DataInputStream reader = null;
	private Updater uiUpdater;

	public Server(Socket connection) {
		super();
		this.connection = connection;
		try {
			reader = new DataInputStream(connection.getInputStream());
			writer = new DataOutputStream(connection.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		uiUpdater = new Updater();
	}

	private void writeString(String s) throws Exception {
		writeString(s, writer);
	}

	public static void writeString(String s, DataOutputStream writer) throws Exception {
		if (s != null) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			dos.writeInt(s.length());
			dos.write(s.getBytes("UTF-8"));
			dos.flush();
			byte[] bs = bos.toByteArray();
			writer.write(bs);
			writer.flush();
		}
	}

	private String readString() throws Exception {
		return readString(reader);
	}

	public static String readString(DataInputStream reader) throws Exception {
		int len = reader.readInt();
		if (len > 0) {
			byte[] bs = new byte[len];
			reader.readFully(bs);
			String s = new String(bs, "UTF-8");
			return s;
		}
		return null;
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
							ServerMain.close();
							System.exit(0);
							return;
						} else {
							uiUpdater.run(string);
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
						ClientCmd cmd = ClientCmd.popCmd();
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