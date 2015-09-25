package com.alibaba.lvdebug.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import com.alibaba.lvdebug.Center;
import com.alibaba.lvdebug.ClientCmd;
import com.alibaba.lvdebug.Config;

public class DebuggerFrame extends JFrame {

	private static final long serialVersionUID = -4760175577170083855L;

	private JPanel contentPane;
	private JTextField textFieldCmdInput;
	private final Center center;

	/**
	 * Create the frame.
	 */
	public DebuggerFrame(final Center c) {
		this.center = c;
		setTitle("LuaView 调试工具");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 640, 750);
		contentPane = new JPanel();
		// contentPane.setBorder(new EmptyBorder(1, 1, 1, 1));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));

		JPanel panelHead = new HeadPanel();
		panelHead.setBackground(Color.WHITE);
		panelHead.setBorder(null);
		FlowLayout flowLayout = (FlowLayout) panelHead.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel.add(panelHead, BorderLayout.NORTH);

		textFieldCmdInput = new JTextField();
		textFieldCmdInput.setToolTipText("调试命令");
		textFieldCmdInput.setPreferredSize(new Dimension(100, Config.BTN_H + 1));
		textFieldCmdInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				center.cmdBuffer.pushCmd(new ClientCmd(textFieldCmdInput.getText().trim()));
			}
		});
		panelHead.add(textFieldCmdInput);
		textFieldCmdInput.setColumns(15);

		buttonNextLine = new JButton("下一行");
		buttonNextLine.setPreferredSize(new Dimension(Config.BTN_W, Config.BTN_H));
		buttonNextLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				center.srcCodeCenter.clearGotoLine();
				setIsDebugging(false);
				center.cmdBuffer.pushCmd(new ClientCmd("n"));
			}
		});
		try {
			Image img = ImageIO.read(getClass().getResource("play.png"));
			Image img2 = ImageIO.read(getClass().getResource("play2.png"));
			buttonNextBreakPoint = new ImageButton(new ImageIcon(img), new ImageIcon(img2));
			buttonNextBreakPoint.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					center.srcCodeCenter.clearGotoLine();
					setIsDebugging(false);
					center.cmdBuffer.pushCmd(new ClientCmd("c"));
				}
			});
		} catch (Exception ex) {
		}
		panelHead.add(buttonNextBreakPoint);
		panelHead.add(buttonNextLine);

		buttonNextCodeLine = new JButton("下一步");
		buttonNextCodeLine.setPreferredSize(new Dimension(Config.BTN_W, Config.BTN_H));
		buttonNextCodeLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				center.srcCodeCenter.clearGotoLine();
				setIsDebugging(false);
				center.cmdBuffer.pushCmd(new ClientCmd("s"));
			}
		});
		panelHead.add(buttonNextCodeLine);

		buttonCallStack = new JButton("显示调用栈");
		buttonCallStack.setPreferredSize(new Dimension(Config.BTN_W2, Config.BTN_H));
		buttonCallStack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				center.cmdBuffer.pushCmd(new ClientCmd("bt"));
			}
		});
		panelHead.add(buttonCallStack);

		JPanel panelBody = new JPanel();
		panelBody.setBorder(null);
		panel.add(panelBody, BorderLayout.CENTER);
		panelBody.setLayout(new BorderLayout(0, 0));

		JSplitPane splitPane = new JSplitPane();

		// splitPane.setBorderPainted(false);// 不打印边框
		splitPane.setBorder(null);// 除去边框
		splitPane.setResizeWeight(0.8);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		panelBody.add(splitPane, BorderLayout.CENTER);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(null);
		scrollPane.setViewportBorder(null);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		splitPane.setRightComponent(scrollPane);

		outputArea = new JTextArea();
		outputArea.setTabSize(4);
		outputArea.setLineWrap(true);
		outputArea.setBorder(null);
		scrollPane.setViewportView(outputArea);

		tabbedPane = new MyTabbedPanel(JTabbedPane.TOP);
		tabbedPane.setBorder(null);
		tabbedPane.setUI(new EclipseTabbedPaneUI());
		splitPane.setLeftComponent(tabbedPane);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				center.worker.close();
			}
		});
	}

	private JTabbedPane tabbedPane;
	private JTextArea outputArea;
	private JButton buttonNextLine;
	private JButton buttonNextCodeLine;
	private JButton buttonNextBreakPoint;
	private JButton buttonCallStack;

	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	public JTextArea getOutputArea() {
		return outputArea;
	}

	public JTextField getTextFieldCmdInput() {
		return textFieldCmdInput;
	}

	public JButton getButtonNextLine() {
		return buttonNextLine;
	}

	public JButton getButtonNextCodeLine() {
		return buttonNextCodeLine;
	}

	public JButton getButtonNextBreakPoint() {
		return buttonNextBreakPoint;
	}

	public void setIsDebugging(boolean yes) {
		this.getButtonNextLine().setEnabled(yes);
		this.getButtonNextCodeLine().setEnabled(yes);
		this.getButtonNextBreakPoint().setEnabled(yes);
		this.getTextFieldCmdInput().setEnabled(yes);
		this.buttonCallStack.setEnabled(yes);
	}
}
