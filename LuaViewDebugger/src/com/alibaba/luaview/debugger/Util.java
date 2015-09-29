package com.alibaba.luaview.debugger;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Util {

	public static void writeString(String s, DataOutputStream writer) throws Exception {
		if (s != null) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			byte[] data = s.getBytes("UTF-8");
			dos.writeInt(data.length);
			dos.write(data);
			dos.flush();
			byte[] bs = bos.toByteArray();
			writer.write(bs);
			writer.flush();
		}
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
}
