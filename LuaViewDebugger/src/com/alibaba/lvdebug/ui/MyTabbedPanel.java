package com.alibaba.lvdebug.ui;

import java.awt.Graphics;

import javax.swing.JTabbedPane;

public class MyTabbedPanel extends JTabbedPane {
	private static final long serialVersionUID = -4995869134364341154L;

	public MyTabbedPanel(int tabPlacement) {
		super(tabPlacement);
	}

	public void paint(Graphics g) {
		super.paint(g);
		// int w = this.getWidth();
		// int h = this.getHeight();
		// g.setColor(new Color(171, 171, 171));
		// g.drawLine(0, h, w, h);
	}
}
