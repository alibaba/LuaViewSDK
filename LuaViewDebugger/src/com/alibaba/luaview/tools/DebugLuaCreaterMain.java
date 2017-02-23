package com.alibaba.luaview.tools;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Vector;

public class DebugLuaCreaterMain {

	public static void main(String[] args) {
		new DebugLuaCreaterMain().run();
	}

	private void run() {
		byte[] bs = file_to_bytes("/Users/dongxicheng/Desktop/LuaView-SDK/LuaViewSDK/IOS/SDK/LuaViewSDK/Classes/lvdebug/debug.lua");
		PrintStream ps = getPrintStream("/Users/dongxicheng/Desktop/LuaView-SDK/LuaViewSDK/IOS/SDK/LuaViewSDK/Classes/lvdebug/LVDebugLua.m");
		ps.println();
		ps.println("// debug.lua");
		ps.println("char g_debug_lua[" + (bs.length + 1) + "] = {");
		ps.print("    ");
		int index0 = 0;
		for (int i = 0; i < bs.length; i++) {
			int c = bs[i] & 0xff;
			if (' ' <= c && c < 127 && c != '\'' && c != '\\') {
				ps.print("'" + (char) c + "',");
			} else {
				ps.print("0x" + Integer.toHexString(c) + ",");
			}
			if (c == '\n') {
				ps.println();
				String s = new String(bs, index0, i - index0, Charset.forName("UTF-8")).trim();
				if (s.length() > 0)
					ps.println("    // " + s);
				index0 = i + 1;
				ps.print("    ");
			}
		}
		ps.println("0");
		ps.println("};");
		ps.println();
		ps.close();
	}

	private byte[] file_to_bytes(String fileName) {
		try {
			DataInputStream fis = new DataInputStream(new FileInputStream(fileName));
			int length = fis.available();
			byte[] bs = new byte[length];
			fis.readFully(bs);
			fis.close();
			return bs;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	void strings_to_file(Vector<String> strings, String fileName) {
		try {
			PrintStream dos = getPrintStream(fileName);
			for (int i = 0; i < strings.size(); i++) {
				dos.println(strings.elementAt(i));
			}
			dos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private PrintStream getPrintStream(String fileName) {
		try {
			PrintStream f = new PrintStream(new File(fileName), "UTF-8");
			return f;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
