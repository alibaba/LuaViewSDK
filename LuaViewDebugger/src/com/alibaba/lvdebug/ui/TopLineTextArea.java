package com.alibaba.lvdebug.ui;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JTextArea;

public class TopLineTextArea extends JTextArea {

	private static final long serialVersionUID = 5017533477769014810L;

	public void paint(Graphics g) {
		super.paint(g);
		int w = this.getWidth();
		int h = this.getHeight();
		g.setClip(0, 0, w, h);
		g.setColor(new Color(171, 171, 171));
		g.drawLine(0, 0, w, 0);
	}
}
