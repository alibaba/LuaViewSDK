package com.alibaba.luaview.debugger.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Hashtable;

import com.alibaba.luaview.debugger.Config;

public final class Word {
	private static final Color KeyWordColor = new Color(184, 51, 161);
	private static final Color ClassNameColor = new Color(111, 65, 166);
	private static final Color MemberColor = new Color(80, 129, 135);
	private static final Color StringWordColor = new Color(45, 36, 251);
	private static final Color NumberWordColor = new Color(183, 24, 29);
	private static final Color CommentWordColor = new Color(65, 126, 96);

	private static final Hashtable<String, String> map = new Hashtable<String, String>();
	{
		String[] keys = { "for", "if", "else", "elseif", "then", "do", "end",//
				"print", "return", "function", "local",//
				"self", "this", "contine", "break", "null", "nil",//
				"System", //
				"UITableView", "UIImageView", "UIButton", "UILabel", "UIView",//
				"UIAlertView", "UIPageControl", "UIScrollView",//
				"UITextField", "Timer",//
				"true", "false", "^", "~", "!", "table", ":", "repeat", "until", "debug", };
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			map.put(key, key);
		}
	}

	public static boolean isKeyWord(String key) {
		return map.get(key) != null;
	}

	public static boolean isClassName(String key) {
		if (key != null && key.length() > 0) {
			char c = key.charAt(0);
			if ('A' <= c && c <= 'Z') {
				return true;
			}
		}
		return false;
	}

	public final String text;
	public final Color color;
	public boolean isWord;
	public boolean isKeyWord;
	public boolean isNumber;
	public boolean isString;
	public boolean isComment;
	public boolean isClassName;
	public boolean isMember;

	public Word prev;
	public Word next;

	// 宽高信息
	int x;
	int w;
	int y;
	int h;

	public String getPressedString(int x, int y) {
		// && this.y <= y && y <= this.y + this.h
		if (this.x <= x && x <= this.x + this.w) {
			String s = this.text.trim();
			Word w = this;
			for (; w.prev != null && w.prev.prev != null && w.prev.text.equals(".") && w.prev.prev.isWord;) {
				s = w.prev.prev.text + w.prev.text + w.text;
				w = w.prev.prev;
			}
			return s;
		}
		return null;
	}

	public Word(String s, Word prev) {
		this.text = s;
		if (isClassName(s)) {
			this.isClassName = true;
			this.color = ClassNameColor;
		} else if (isKeyWord(s)) {
			this.isKeyWord = true;
			this.color = KeyWordColor;
		} else if (s.startsWith("--")) {
			this.isComment = true;
			this.color = CommentWordColor;
		} else if (s.startsWith("\"") || s.startsWith("\'")) {
			this.isString = true;
			this.color = StringWordColor;
		} else if (s.length() > 0 && Character.isDigit(s.charAt(0))) {
			this.isNumber = true;
			this.color = NumberWordColor;
		} else {
			if (s.equals("window")) {
				this.color = MemberColor;
			} else if (prev != null && ".".equals(prev.text)) {
				this.color = MemberColor;
			} else {
				this.color = Color.black;
			}
		}
		if (s != null && s.length() > 0 && CharsToWords.wordStart(s.charAt(0))) {
			this.isWord = true;
		}
	}

	public int draw(Graphics2D g, int x, int y) {
		if (text != null) {
			g.setColor(this.color);
			g.drawString(text, x, y);
			int x2 = x + g.getFontMetrics().stringWidth(text);
			this.x = x;
			this.y = y;
			this.w = x2 - x;
			this.h = Config.LINE_H;
			return x2;
		}
		return x;
	}
}
