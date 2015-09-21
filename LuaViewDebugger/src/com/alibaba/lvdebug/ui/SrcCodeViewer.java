package com.alibaba.lvdebug.ui;

/**
 * AWT Sample application
 *
 * @author 
 * @version 1.00 05/09/22
 */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;
import com.alibaba.lvdebug.Center;
import com.alibaba.lvdebug.ClientCmd;

// 类的作用：树结构的浏览器
/**
 * 
 * @author dongxicheng
 * 
 */
public final class SrcCodeViewer extends MyScrollPanel {

	private static final long serialVersionUID = -196018687886621L;

	public boolean canBreakPoint = true;

	public final String fileName;

	private final Vector<Line> lines = new Vector<Line>();

	private FontMetrics fontMetrics;

	private Center center;

	SrcCodeViewer(String fileName, String content, Center center) {
		super();
		this.center = center;
		this.setFont(new Font("黑体", Font.PLAIN, 16));
		fontMetrics = getFontMetrics(getFont());

		addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_W) {
				} else if (arg0.getKeyCode() == KeyEvent.VK_S) {
				} else if (arg0.getKeyCode() == KeyEvent.VK_A) {
				} else if (arg0.getKeyCode() == KeyEvent.VK_D) {
				}
				updateUI();
			}

			public void keyReleased(KeyEvent arg0) {
			}

			public void keyTyped(KeyEvent arg0) {
			}
		});
		this.fileName = fileName;
		this.setFileSting(content);
	}

	void setFileSting(String s) {
		this.lines.removeAllElements();
		String[] arr = s.split("\n");
		for (int i = 0; i < arr.length; i++) {
			Line line = new Line(arr[i]);
			line.index = i + 1;// 设置行号
			this.lines.add(line);
		}
	}

	public final int X0 = LINE_H * 3;
	public final int Y0 = LINE_H * 2;

	public void myPaint(Graphics2D g) {
		g.setColor(new Color(0xf0f0f0));
		g.fillRect(0, 0, LINE_H * 2, (this.lines.size() + 2) * LINE_H);
		resetMaxWH();
		try {
			setNodeX(X0);
			setNodeY(Y0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 0; i < this.lines.size(); i++) {
			Line line = this.lines.elementAt(i);
			line.tag = "" + (i + 1);
			drawOneLine(true, line, g, line.x, line.y);
		}
		this.clearPoint();
	}

	BasicStroke stroke = new BasicStroke(1);

	/**
	 * 显示root
	 * 
	 * @param line
	 * @param g
	 * @param topX
	 * @param topY
	 */
	private void drawOneLine(boolean yes, Line line, Graphics2D g, int topX, int topY) {
		if (line == null)
			return;
		int x = line.x;
		int y = line.y;

		if (isYOnView(y - LINE_H) || isYOnView(y + LINE_H)) {
			if (line.isCurrentLine) {
				g.setColor(new Color(221, 234, 207));
				g.fillRect(0, y - LINE_H + 2, this.getWidth(), LINE_H);
			}

			g.setColor(Color.BLACK);
			// g.drawString(text, x + 2, y - 2);
			line.draw(g, x + 2, y - 2);

			if (line.isBreakPoint) {// 断点
				int dx = 2;
				g.setColor(Color.red);
				g.fillArc(x - LINE_H * 2 - LINE_H + dx, y - LINE_H + (int) (dx * 2), LINE_H - dx * 2, LINE_H - dx * 2, 0, 360);
			}

			g.setColor(Color.GRAY);
			int w = g.getFontMetrics().stringWidth(line.tag);
			g.drawString(line.tag, x - w - LINE_H, y - 2);
		}
		if (this.pressedPointX() < X0 && this.canBreakPoint && isPressTheLine(x, y, LINE_H / 2)) {
			line.isBreakPoint = !line.isBreakPoint;
			updateUI();
			if (line.isBreakPoint) {
				String s = "b " + this.fileName + ":" + line.index;
				center.cmdBuffer.pushCmd(new ClientCmd(s));
			} else {
				String s = "rb " + this.fileName + ":" + line.index;
				center.cmdBuffer.pushCmd(new ClientCmd(s));
			}
		}

		if (X0 < this.pressedPointX() && isPressTheLine(x, y, LINE_H / 2)) {
			String s = line.getPressedString(this.pressedPointX(), this.pressedPointY());
			if (s != null && s.length() > 0) {
				center.cmdBuffer.pushCmd(new ClientCmd("run print(\"" + s + " =\" , " + s + ")"));
			}
		}
	}

	/**
	 * 设置x坐标
	 * 
	 * @param node
	 * @param x0
	 * @param PER_WIDTH
	 */
	private void setNodeX(int x0) {
		for (int i = 0; i < this.lines.size(); i++) {
			Line node = this.lines.elementAt(i);
			if (node == null) {
				return;
			}
			node.x = x0;

			node.width = fontMetrics.stringWidth(node.text) + 16;

			int tempX = node.x + node.width + 100;
			this.setMaxW(tempX);
		}
	}

	/**
	 * 设置y坐标
	 * 
	 * @param node
	 * @return
	 */
	private void setNodeY(int y0) {
		for (int i = 0; i < this.lines.size(); i++) {
			Line obj = this.lines.elementAt(i);
			obj.y = y0 + i * LINE_H;
			setMaxH(obj.y + 50);
		}
	}

	public void gotoLine(int lineNumber) {
		lineNumber -= 1;
		for (int i = 0; i < this.lines.size(); i++) {
			Line line = this.lines.elementAt(i);
			if (lineNumber == i) {
				line.isCurrentLine = true;
			} else {
				line.isCurrentLine = false;
			}
		}
		this.setNextYOnView(lineNumber * LINE_H + Y0);
		this.updateUI();
	}

}
