package com.alibaba.lvdebug.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Hashtable;

public class Word {
	public static final Color KeyWordColor = new Color(126, 8, 84);
	public static final Color StringWordColor = new Color(45, 36, 251);
	public static final Color NumberWordColor = new Color(183, 24, 29);
	public static final Color CommentWordColor = new Color(65, 126, 96);

	public final String text;
	public final Color color;
	public boolean isKeyWord;
	public boolean isNumber;
	public boolean isString;
	public boolean isComment;

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

	static final Hashtable<String, String> map = new Hashtable<String, String>();
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

	public int draw(Graphics2D g, int x, int y) {
		if (text != null) {
			g.setColor(this.color);
			g.drawString(text, x, y);
			return x + g.getFontMetrics().stringWidth(text);
		}
		return x;
	}
}
