package com.alibaba.lvdebug.ui;

import java.awt.Graphics2D;
import java.util.Vector;

public class Line {
	int index;
	String tag;
	int x;
	int y;
	int width;
	int higth;
	final String text;
	boolean isBreakPoint;
	boolean isCurrentLine;

	final Vector<Word> words;

	public Line(String s) {
		this.text = s;
		words = new CharsToWords().split(s);
	}

	public int draw(Graphics2D g, int x, int y) {
		for (int i = 0; i < words.size(); i++) {
			Word word = words.elementAt(i);
			x = word.draw(g, x, y);
		}
		return x;
	}

	public String getPressedString(int x, int y) {
		for (int i = 0; i < words.size(); i++) {
			Word word = words.elementAt(i);
			String s = word.getPressedString(x, y);
			if (s != null) {
				return s;
			}
		}
		return null;
	}
}
