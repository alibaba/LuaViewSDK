package com.alibaba.luaview.debugger.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;

import javax.swing.Icon;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class EclipseTabbedPaneUI extends BasicTabbedPaneUI {

	public static final Color LINE_COLOR = new Color(182, 188, 203);

	private static final int R = 13;

	@Override
	protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
		if (tabPlacement == BOTTOM) {
			g.setColor(LINE_COLOR);
			g.drawLine(x, y + h - 1, x + w, y + h - 1);
		}

		if (tabPlacement == TOP && isSelected) {
			g.setColor(LINE_COLOR);
			Shape shape = g.getClip();
			g.setClip(x, y, w + 2, h);
			g.drawRoundRect(x, y, w, h + R + R, R, R);
			g.setClip(shape);
		}

	}

	protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect) {
		super.paintTab(g, tabPlacement, rects, tabIndex, iconRect, textRect);
	}

	/**
	 * Give selected tab blue color with a gradient!!.
	 * 
	 * FIXME: with Plastic L&F the unselected background is too dark
	 */
	@Override
	protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
		// Color color = UIManager.getColor("control");
		if (isSelected) {
			if (tabPlacement == TOP) {
				g.setColor(Color.white);
				g.fillRoundRect(x, y, w, h + R + R, R, R);
			}
		} else {
		}
	}

	/**
	 * Do not paint a focus indicator.
	 */
	@Override
	protected void paintFocusIndicator(Graphics arg0, int arg1, Rectangle[] arg2, int arg3, Rectangle arg4, Rectangle arg5, boolean arg6) {
		// Leave it
	}

	/**
	 * We do not want the tab to "lift up" when it is selected.
	 */
	@Override
	protected void installDefaults() {
		super.installDefaults();
		tabAreaInsets = new Insets(0, 0, 0, 0);
		selectedTabPadInsets = new Insets(0, 0, 0, 0);
		contentBorderInsets = new Insets(1, 0, 0, 0);
	}

	/**
	 * Nor do we want the label to move.
	 */
	@Override
	protected int getTabLabelShiftY(int tabPlacement, int tabIndex, boolean isSelected) {
		return 0;
	}

	/**
	 * Increase the tab height a bit
	 */
	@Override
	protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
		return fontHeight + 4;
	}

	@Override
	protected void layoutLabel(int arg0, FontMetrics arg1, int arg2, String arg3, Icon arg4, Rectangle arg5, Rectangle arg6, Rectangle arg7, boolean arg8) {
		super.layoutLabel(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
	}

	/**
	 * Selected labels have a white color.
	 */
	@Override
	protected void paintText(Graphics tempG, int tabPlacement, Font font, FontMetrics metrics, int tabIndex, String title, Rectangle textRect,
			boolean isSelected) {

		Graphics2D g = (Graphics2D) tempG;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (isSelected && tabPlacement == TOP) {
			g.setColor(Color.BLACK);
		} else {
			g.setColor(Color.BLACK);
		}
		g.drawString(title, textRect.x, textRect.y + metrics.getAscent());
	}

	@Override
	protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
		int taille = 0;
		taille = super.calculateTabWidth(tabPlacement, tabIndex, metrics);
		return taille;
	}

	@Override
	protected void paintContentBorderTopEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
		if (selectedIndex != -1 && tabPlacement == TOP) {
			g.setColor(LINE_COLOR);
			g.drawLine(x, y, x + w, y);
		}
		if (tabPlacement == BOTTOM) {
			// g.setColor(Color.black);
			// g.drawLine(x, y + h - 1, x + w, y + h - 1);
		}
	}

	@Override
	protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
		g.setColor(LINE_COLOR);
		g.drawLine(x, y + h, x + w, y + h);
	}

	@Override
	protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
		// do nothingx, y, x, y + h);
	}

	@Override
	protected void paintContentBorderRightEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
		// do nothing
	}

	// @Override
	// protected MouseListener createMouseListener() {
	// return new CloseHandler();
	// }
	//
	// class CloseHandler extends MouseHandler {
	// public CloseHandler() {
	// super();
	// }
	//
	// public void mouseReleased(MouseEvent e) {
	// int x = e.getX();
	// int y = e.getY();
	// int tabIndex = -1;
	// int tabCount = tabPane.getTabCount();
	// for (int i = 0; i < tabCount; i++) {
	// if (rects[i].contains(x, y)) {
	// tabIndex = i;
	// break;
	// }
	// }
	//
	// if (tabIndex >= 0 && !e.isPopupTrigger()) {
	// Rectangle tabRect = rects[tabIndex];
	// y = y - tabRect.y;
	// if ((x >= tabRect.x + tabRect.width - 18) && (x <= tabRect.x +
	// tabRect.width - 8) && (y >= 5) && (y <= 15)) {
	// tabPane.remove(tabIndex);
	// }
	// }
	// }
	// }

}