package com.alibaba.lvdebug.ui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JScrollPane;

public class TopLineScrollPane extends JScrollPane {

	private static final long serialVersionUID = -3785351070903066566L;

	public void paint(Graphics g) {
		super.paint(g);
		int w = this.getWidth();
		int h = this.getHeight();
		g.setClip(0, 0, w, h);
		g.setColor(new Color(171, 171, 171));
		g.drawLine(0, 0, w, 0);
	}
}
