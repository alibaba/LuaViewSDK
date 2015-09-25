package com.alibaba.lvdebug.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;

import com.alibaba.lvdebug.Config;

abstract public class MyScrollPanel extends JPanel {

	private static final long serialVersionUID = -8495786914354054078L;

	private Font font = new java.awt.Font("宋体", 0, 14);

	public final int LINE_H = Config.LINE_H;

	private int bar_w = 15;

	private int max_w, max_h;

	private Point point, pointPressed, draggedPoint, m_repressedPoint;
	private int m_pressedButtonID = -1;

	private final Point zeroPoint = new Point(0, 0);

	public boolean updateUI = true;

	public MyScrollPanel() {
		this.setBorder(null);
		this.addMouseWheelListener(new MouseWheelListener() {

			public void mouseWheelMoved(MouseWheelEvent arg0) {
				zeroPoint.y += arg0.getWheelRotation() * 50;
				if (updateUI)
					updateUI();
			}

		});
		this.addMouseMotionListener(new MouseMotionListener() {

			public void mouseDragged(MouseEvent arg0) {
				if (pointPressed == null) {
					return;
				}
				if (draggedPoint == null) {
					draggedPoint = arg0.getPoint();
					return;
				}
				if (isOnBarArea(pointPressed.x)) {
					Point oldPoint = draggedPoint;
					Point newPoint = arg0.getPoint();
					draggedPoint = newPoint;
					scrollBar(newPoint.y - oldPoint.y);
				} else {
					Point oldPoint = draggedPoint;
					Point newPoint = arg0.getPoint();
					draggedPoint = newPoint;
					zeroPoint.x -= newPoint.x - oldPoint.x;
					zeroPoint.y -= newPoint.y - oldPoint.y;
				}
				if (updateUI)
					updateUI();
			}

			public void mouseMoved(MouseEvent arg0) {
			}

		});
		/**
		 * 鼠标点击
		 */
		addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				m_pressedButtonID = e.getButton();
				if (e.getClickCount() >= 2) {
					m_repressedPoint = e.getPoint();
				}
			}

			public void mouseEntered(MouseEvent e) {
				int t = e.getButton();
				if (0 <= t && t <= 2) {
				} else {
					draggedPoint = point = pointPressed = null;
				}
			}

			public void mouseExited(MouseEvent e) {
				int t = e.getButton();
				if (0 <= t && t <= 2) {
				} else {
					draggedPoint = point = pointPressed = null;
				}
			}

			public void mousePressed(MouseEvent e) {
				draggedPoint = point = pointPressed = null;
				pointPressed = e.getPoint();
				requestFocus();
				updateUI();
			}

			public void mouseReleased(MouseEvent e) {
				if (pointPressed != null) {
					Point t = e.getPoint();
					if (pointPressed.x == t.x && pointPressed.y == t.y) {
						if (!isOnBarArea(t.x)) {
							point = t;
							updateUI();
							return;
						}
					}
				}
				draggedPoint = point = pointPressed = null;
				updateUI();
			}
		});
	}

	private boolean isOnBarArea(int x) {
		if (screenW - bar_w < x && x < screenW)
			return true;
		return false;
	}

	private void scrollBar(int dy) {
		zeroPoint.y += dy * max_h / screenH;
	}

	public void update(Graphics g) {
		paint(g);
	}

	private int screenW = 100, screenH = 100;

	public void paint(Graphics tempG) {
		if (tempG instanceof Graphics2D == false) {
			return;
		}
		Graphics2D g = (Graphics2D) tempG;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setFont(font);
		if (nextY >= 0) {
			zeroPoint.y = nextY;
			nextY = -1;
		}
		screenW = getWidth();
		screenH = getHeight();
		max_w = max_w < screenW ? screenW : max_w;
		max_h = max_h < screenH ? screenH : max_h;
		if (zeroPoint.x < 0) {
			zeroPoint.x = 0;
		} else if (zeroPoint.x > max_w - screenW) {
			zeroPoint.x = max_w - screenW;
		}
		if (zeroPoint.y < 0) {
			zeroPoint.y = 0;
		} else if (zeroPoint.y > max_h - screenH) {
			zeroPoint.y = max_h - screenH;
		}
		g.setColor(Color.white);
		g.fillRect(0, 0, screenW, screenH);
		// 显示位置
		g.setClip(0, 0, screenW - bar_w, screenH);
		g.translate(-zeroPoint.x, -zeroPoint.y);
		myPaint(g);// //////////////////////
		g.translate(zeroPoint.x, zeroPoint.y);
		// bar
		int bar_X = screenW - bar_w;
		int bar_Y = zeroPoint.y * screenH / max_h;
		int bar_W = bar_w;
		int bar_H = screenH * screenH / max_h;
		if (bar_H > screenH - 1) {
			bar_H = screenH - 1;
		}
		g.setClip(0, 0, screenW, screenH);
		g.setColor(new Color(250, 250, 250));
		g.fillRect(screenW - bar_w, -1, bar_w + 1, screenH + 2);
		g.setColor(new Color(232, 232, 232));
		g.drawRect(screenW - bar_w, -1, bar_w + 8, screenH + 2);
		fillBar(g, bar_X, bar_Y, bar_W, bar_H);

		int w = this.getWidth();
		int h = this.getHeight();
		g.setColor(new Color(171, 171, 171));
		g.drawLine(0, h, w, h);
		if (m_repaint) {
			m_repaint = false;
			if (updateUI)
				updateUI();
		}
	}

	private void fillBar(Graphics g, int x, int y, int w, int h) {
		y += 3;
		h -= 6;
		h = h < 2 ? 2 : h;
		x += 4;
		w -= 8;
		g.setColor(new Color(194, 194, 194));
		g.fillRoundRect(x, y, w, h, w, w);
		// for (int i = 0; i < w / 2; i++) {
		// int t = 255 * (i + T) / ((w / 2) + T);
		// Color c = new Color((t << 16) + (t << 8) + t);
		// g.setColor(c);
		// g.drawLine(x + i, y, x + i, y + h);
		// }
		// for (int i = w / 2; i < w; i++) {
		// int t = 255 * ((w - i) + T) / ((w / 2) + T);
		// Color c = new Color((t << 16) + (t << 8) + t);
		// g.setColor(c);
		// g.drawLine(x + i, y, x + i, y + h);
		// }
	}

	abstract public void myPaint(Graphics2D g);

	public boolean isRectOnView(int topX, int topY, int endX, int endY) {
		int viewX0 = zeroPoint.x;
		int viewY0 = zeroPoint.y;
		int viewX2 = zeroPoint.x + screenW;
		int viewY2 = zeroPoint.y + screenH;

		int tempx0 = topX > viewX0 ? topX : viewX0;
		int tempy0 = topY > viewY0 ? topY : viewY0;
		int tempx2 = endX > viewX2 ? endX : viewX2;
		int tempy2 = endY > viewY2 ? endY : viewY2;
		if (tempx0 < tempx2 && tempy0 < tempy2) {
			return true;
		}
		return false;
	}

	public boolean isYOnView(int y) {
		int viewY0 = zeroPoint.y;
		int viewY2 = zeroPoint.y + screenH;
		if (y >= viewY0 && y < viewY2) {
			return true;
		}
		return false;
	}

	private int nextY = -1;

	public void setNextYOnView(int y) {
		// System.out.println("y:" + y);
		if (isYOnView(y) == false) {
			int viewY0 = zeroPoint.y;
			int viewY2 = zeroPoint.y + screenH;
			if (Math.abs(viewY0 - y) < Math.abs(viewY2 - y)) {
				nextY = y - LINE_H;
			} else {
				nextY = y - screenH + LINE_H;
			}
		} else {
			nextY = -1;
		}
	}

	public void resetMaxWH() {
		max_w = 10;
		max_h = 10;
	}

	public void setMaxW(int w) {
		max_w = w < max_w ? max_w : w;
	}

	public int getMaxW() {
		return max_w;
	}

	public void setMaxH(int h) {
		max_h = h < max_h ? max_h : h;
	}

	public boolean isPressThePoint(int x, int y, int r) {
		if (point != null) {
			int tmp = Math.abs(point.x + zeroPoint.x - x) + Math.abs(point.y + zeroPoint.y - y);
			if (tmp < r) {
				point = null;
				return true;
			}
		}
		return false;
	}

	public boolean isPressTheLine(int x, int y, int h) {
		if (point != null) {
			int pY = point.y + zeroPoint.y;
			int y0 = y - h;
			int y1 = y;
			if (y0 < pY && pY < y1) {
				return true;
			}
		}
		return false;
	}

	public int pressedPointX() {
		if (point != null) {
			return point.x + zeroPoint.x;
		}
		return 0;
	}

	public int pressedPointY() {
		if (point != null) {
			return point.y + zeroPoint.y;
		}
		return 0;
	}

	public int pressedButtonID() {
		return m_pressedButtonID;
	}

	public void clearPoint() {
		point = null;
	}

	private boolean m_repaint;

	public void shouldRepaint() {
		m_repaint = true;
	}

	public Point getPressedPoint() {
		Point t = point;
		if (t != null) {
			t.x += zeroPoint.x;
			t.y += zeroPoint.y;
		}
		point = null;
		return t;
	}

	public Point getRepressedPoint() {
		Point t = m_repressedPoint;
		if (t != null) {
			t.x += zeroPoint.x;
			t.y += zeroPoint.y;
		}
		m_repressedPoint = null;
		return t;
	}

	public void setBarW(int w) {
		bar_w = w;
	}

}
