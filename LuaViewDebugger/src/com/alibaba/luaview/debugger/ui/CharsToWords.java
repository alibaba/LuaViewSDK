package com.alibaba.luaview.debugger.ui;

import java.util.Vector;

public class CharsToWords {

	int index;

	public Vector<Word> split(String s) {
		Vector<Word> words = new Vector<Word>();
		index = 0;
		Word prev = null;
		for (int i = 0; i < s.length(); i++) {
			Word word = getOneWord(s, prev);
			if (word != null) {
				if (prev != null) {
					word.prev = prev;
					prev.next = word;
				}
				words.add(word);
				prev = word;
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

	public Word getOneWord(String s, Word preWord) {
		StringBuffer buffer = new StringBuffer();
		for (; index < s.length();) {
			char c = s.charAt(index);
			// ".." 链接运算符
			if (buffer.length() <= 0 && charAt(index, s) == '.' && charAt(index + 1, s) == '.') {
				buffer.append("..");
				index += 2;
				break;
			}
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
			return new Word(w, preWord);
		}
		return null;
	}

	public boolean is09(char c) {
		if ('0' <= c && c <= '9') {
			return true;
		}
		return false;
	}

	public static boolean isAF(char c) {
		if ('A' <= c && c <= 'F') {
			return true;
		}
		if ('a' <= c && c <= 'f') {
			return true;
		}
		return false;
	}

	public static boolean wordStart(char c) {
		if (isAZ(c)) {
			return true;
		}
		if ('_' == c) {
			return true;
		}
		return false;
	}

	public static boolean isAZ(char c) {
		if ('A' <= c && c <= 'Z') {
			return true;
		}
		if ('a' <= c && c <= 'z') {
			return true;
		}
		return false;
	}
}
