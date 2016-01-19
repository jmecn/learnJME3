package com.ruanko.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ruanko.service.Logic;

/**
 * 主窗口
 * @author yanmaoyuan
 *
 */
public class MainFrame extends JFrame {
	private GamePanel gp;
	private PreviewPanel pp;
	private JTextField scoreTxt;
	private JTextField levelTxt;
	private JButton playBtn;
	private JButton stopBtn;
	private Logic l;
	/**
	 * 构造方法
	 */
	public MainFrame() {
		this.setTitle("Tetris");
		this.setSize(400, 536);
		this.setResizable(false);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		JPanel contentPane = new JPanel();
		this.setContentPane(contentPane);

		gp = new GamePanel();
		JPanel leftPane = new JPanel(new FlowLayout(FlowLayout.LEADING));
		leftPane.setPreferredSize(new Dimension(120, 400));
		contentPane.add(gp);
		contentPane.add(leftPane);
		leftPane.add(new JLabel("SCORE"));
		scoreTxt = new JTextField(9);
		scoreTxt.setText("0");
		scoreTxt.setEditable(false);
		leftPane.add(scoreTxt);
		leftPane.add(new JLabel("LEVEL"));
		levelTxt = new JTextField(9);
		levelTxt.setText("0");
		levelTxt.setEditable(false);
		leftPane.add(levelTxt);
		leftPane.add(new JLabel("NEXT BLOCK"));
		pp = new PreviewPanel();
		leftPane.add(pp);
		playBtn = new JButton("PLAY");
		leftPane.add(playBtn);
		playBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				l.start();
				gp.requestFocus();
			}
		});
		
		stopBtn = new JButton("STOP");
		leftPane.add(stopBtn);
		stopBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				l.stop();
				gp.requestFocus();
			}
		});
		l = new Logic(gp, pp, this);
	}

	public void setScore(int score) {
		scoreTxt.setText("" + score);
	}
	public void setLevel(int level) {
		levelTxt.setText("" + level);
	}
}
