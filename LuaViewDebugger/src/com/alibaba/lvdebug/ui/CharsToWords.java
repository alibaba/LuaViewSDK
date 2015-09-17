package com.alibaba.lvdebug.ui;

import java.util.Vector;

public class CharsToWords {

	int index;

	public Vector<Word> split(String s) {
		Vector<Word> words = new Vector<Word>();
		index = 0;
		for (int i = 0; i < s.length(); i++) {
			Word word = getOneWord(s);
			if (word != null) {
				words.add(word);
			} else {
				break;
			}
		}
		return words;
	}

	private char charAt(int index, String s) {
		if (index < s.length()) {
			return s.charAt(index);
		}
		return 0;
	}

	public Word getOneWord(String s) {
		StringBuffer buffer = new StringBuffer();
		for (; index < s.length();) {
			char c = s.charAt(index);
			// 字符串
			if (buffer.length() <= 0 && (c == '"' || c == '\'')) {
				char c0 = c;
				char preC = 0;
				buffer.append(c);
				index++;
				for (; index < s.length();) {
					c = s.charAt(index);
					buffer.append(c);
					index++;
					if (preC != '\\' && c == c0) {
						break;
					}
				}
				break;
			}
			// 注释信息
			if (buffer.length() <= 0 && charAt(index, s) == '-' && charAt(index + 1, s) == '-') {
				for (; index < s.length();) {
					c = s.charAt(index);
					buffer.append(c);
					index++;
					if (c == '\n') {
						break;
					}
				}
				break;
			}
			// 数字
			if (buffer.length() <= 0 && is09(c)) {
				for (; index < s.length();) {
					c = s.charAt(index);
					if (is09(c) || isAF(c) || c == '.' || c == 'X' || c == 'x') {
						buffer.append(c);
						index++;
					} else {
						break;
					}
				}
				break;
			}
			// 表达式
			if (isAZ(c) || is09(c) || c == '_') {
				buffer.append(c);
				index++;
			} else {
				// 普通标点符号括号等.
				if (buffer.length() <= 0) {
					buffer.append(c);
					index++;
				}
				break;
			}
		}
		if (buffer.length() > 0) {
			String w = buffer.toString();
			return new Word(w);
		}
		return null;
	}

	public boolean is09(char c) {
		if ('0' <= c && c <= '9') {
			return true;
		}
		return false;
	}

	public boolean isAF(char c) {
		if ('A' <= c && c <= 'F') {
			return true;
		}
		if ('a' <= c && c <= 'f') {
			return true;
		}
		return false;
	}

	public boolean isAZ(char c) {
		if ('A' <= c && c <= 'Z') {
			return true;
		}
		if ('a' <= c && c <= 'z') {
			return true;
		}
		return false;
	}
}
