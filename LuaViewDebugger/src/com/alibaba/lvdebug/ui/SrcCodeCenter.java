package com.alibaba.lvdebug.ui;

import java.util.Hashtable;

import javax.swing.JTabbedPane;

public class SrcCodeCenter {

	static final Hashtable<String, SrcCodeViewer> table = new Hashtable<String, SrcCodeViewer>();

	public static void showHelpTab() {
		String s = "h             help info" + "\n" + //
				"c             continue run" + "\n" + //
				"s             trace" + "\n" + //
				"n             next" + "\n" + //
				"p var         print variable" + "\n" + //
				"b src:line    add breakpoint" + "\n" + //
				"rb src:line   remove breakpoint" + "\n" + //
				"bl            list breakpoint" + "\n" + //
				"bt            print traceback" + "\n";
		loadfile("帮助信息", s).canBreakPoint = false;
	}

	public static SrcCodeViewer loadfile(String fileName, String content) {
		content = content.replace("\t", "    ");
		JTabbedPane tabbedPane = DebuggerFrame.frame.getTabbedPane();
		if (fileName != null) {
			SrcCodeViewer temp = table.get(fileName);
			tabbedPane.remove(temp);
			table.remove(fileName);
		}
		SrcCodeViewer viewer = new SrcCodeViewer(fileName, content);
		tabbedPane.addTab(fileName, viewer);
		table.put(fileName, viewer);
		return viewer;
	}

	public static void running(String fileName, String lineNumber) {
		try {
			if (fileName != null) {
				SrcCodeViewer viewer = table.get(fileName);
				if (viewer != null) {
					viewer.gotoLine(Integer.parseInt(lineNumber.trim()));

					JTabbedPane tabbedPane = DebuggerFrame.frame.getTabbedPane();
					tabbedPane.setSelectedComponent(viewer);
					if (DebuggerFrame.frame.isAlwaysOnTop()) {
						DebuggerFrame.frame.setAlwaysOnTop(true);
					}
					if (DebuggerFrame.frame.isVisible()) {
						DebuggerFrame.frame.setVisible(true);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println(fileName + lineNumber);
	}
}
