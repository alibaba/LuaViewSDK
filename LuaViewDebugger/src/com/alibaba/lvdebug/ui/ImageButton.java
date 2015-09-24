package com.alibaba.lvdebug.ui;

import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class ImageButton extends JButton {

	private static final long serialVersionUID = -8171450113272087720L;

	public ImageButton(ImageIcon icon, ImageIcon icon2) {
		setSize(icon.getImage().getWidth(null), icon.getImage().getHeight(null));
		setIcon(icon);
		this.setDisabledIcon(icon2);
		setMargin(new Insets(0, 0, 0, 0));// 将边框外的上下左右空间设置为0
		setIconTextGap(0);// 将标签中显示的文本和图标之间的间隔量设置为0
		setBorderPainted(false);// 不打印边框
		setBorder(null);// 除去边框
		setText(null);// 除去按钮的默认名称
		setFocusPainted(false);// 除去焦点的框
		setContentAreaFilled(false);// 除去默认的背景填充
	}
}