package com.alibaba.lvdebug.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;

import java.awt.FlowLayout;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JTabbedPane;

import com.alibaba.lvdebug.ClientCmd;
import javax.swing.JTextArea;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class DebuggerFrame extends JFrame {
	public static DebuggerFrame frame;
	/**
	 * 
	 */
	private static final long serialVersionUID = -4760175577170083855L;
	private JPanel contentPane;
	private JTextField textFieldCmdInput;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DebuggerFrame frame = new DebuggerFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public DebuggerFrame() {
		setTitle("Lua调试器 V1.0.0");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 640, 750);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));

		JPanel panelHead = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelHead.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel.add(panelHead, BorderLayout.NORTH);

		textFieldCmdInput = new JTextField();
		textFieldCmdInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ClientCmd.pushCmd(new ClientCmd(textFieldCmdInput.getText().trim()));
			}
		});
		panelHead.add(textFieldCmdInput);
		textFieldCmdInput.setColumns(20);

		buttonNextLine = new JButton("下一行");
		buttonNextLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ClientCmd.pushCmd(new ClientCmd("n"));
			}
		});
		panelHead.add(buttonNextLine);

		buttonNextCodeLine = new JButton("下一步");
		buttonNextCodeLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ClientCmd.pushCmd(new ClientCmd("s"));
			}
		});
		panelHead.add(buttonNextCodeLine);

		buttonNextBreakPoint = new JButton("下一断点");
		buttonNextBreakPoint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ClientCmd.pushCmd(new ClientCmd("c"));
			}
		});
		panelHead.add(buttonNextBreakPoint);

		JPanel panelBody = new JPanel();
		panel.add(panelBody, BorderLayout.CENTER);
		panelBody.setLayout(new BorderLayout(0, 0));

		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.7);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		panelBody.add(splitPane, BorderLayout.CENTER);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		splitPane.setRightComponent(scrollPane);

		outputArea = new JTextArea();
		outputArea.setLineWrap(true);
		scrollPane.setViewportView(outputArea);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		splitPane.setLeftComponent(tabbedPane);

		frame = this;

	}

	private JTabbedPane tabbedPane;
	private JTextArea outputArea;
	private JButton buttonNextLine;
	private JButton buttonNextCodeLine;
	private JButton buttonNextBreakPoint;

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
}
