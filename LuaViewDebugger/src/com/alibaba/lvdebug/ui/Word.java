package com.alibaba.lvdebug.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Hashtable;

import com.alibaba.lvdebug.Config;

public final class Word {
	public static final Color KeyWordColor = new Color(126, 8, 84);
	public static final Color StringWordColor = new Color(45, 36, 251);
	public static final Color NumberWordColor = new Color(183, 24, 29);
	public static final Color CommentWordColor = new Color(65, 126, 96);

	private static final Hashtable<String, String> map = new Hashtable<String, String>();
	{
		String[] keys = { "if", "else", "elseif", "then", "do", "end",//
				"print", "return", "delegate", "function", "local",//
				"self", "this", "contine", "break",//
				"window", "System", //
				"UITableView", "UIImageView", "UIButton", "UILabel", "UIView",//
				"UIAlertView", "UIPageControl", "UIScrollView",//
				"UITextField", "Timer",//
				"true", "false", "^", "~", "!", "table", ":" };
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			map.put(key, key);
		}
	}

	public static boolean isKeyWord(String key) {
		if (key != null && key.length() > 0) {
			char c = key.charAt(0);
			if ('A' <= c && c <= 'Z') {
				return true;
			}
		}
		return map.get(key) != null;
	}

	public final String text;
	public final Color color;
	public boolean isKeyWord;
	public boolean isNumber;
	public boolean isString;
	public boolean isComment;
	private int x, y, w, h;

	public String getPressedString(int x, int y) {
		// && this.y <= y && y <= this.y + this.h
		if (this.x <= x && x <= this.x + this.w) {
			return this.text.trim();
		}
		return null;
	}

	public Word(String s) {
		this.text = s;
		if (isKeyWord(s)) {
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
			this.color = Color.black;
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
