package com.alibaba.luaview.debugger.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class HeadPanel extends JPanel {

	private static final long serialVersionUID = -4421177374953807366L;

	public void paint(Graphics tempG) {
		if (tempG instanceof Graphics2D == false) {
			return;
		}
		Graphics2D g = (Graphics2D) tempG;
		// g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		// RenderingHints.VALUE_ANTIALIAS_ON);
		int w = this.getWidth();
		int h = this.getHeight();

		for (int i = 0; i < h; i++) {
			int t = (207 * (h - i) + 171 * i) / h;
			Color c = new Color(t, t, t);
			g.setColor(c);
			g.drawLine(0, i, w, i);
		}
		g.setColor(new Color(81, 81, 81));
		g.drawLine(0, h - 1, w, h - 1);
		this.paintChildren(g);
	}
}
