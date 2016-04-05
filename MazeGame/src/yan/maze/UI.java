package yan.maze;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import yan.mazegame.logic.BlockCreator;
import yan.mazegame.logic.MazeCreator;

/**
 * 程序主界面
 * 
 * @author yan
 * 
 */
public class UI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6014389155626784508L;
	/* 窗口控件 */
	private JLabel statsText;// 状态栏
	// 状态栏提示信息
	private String format = "高:%d, 宽:%d, 需要方块:%d(%d组).";

	/* 迷宫参数 */
	
	// 迷宫生成器
	private int col; // 列
	private int row; // 行
	private long seed;// 种子
	private boolean isRand;// 是否随机
	private MazeCreator mc;

	// 方块生成器
	private int roadSize;// 道路宽度
	private BlockCreator bc;
	
	// 绘图板
	private int pixel;// 每个方块的宽度
	private Canvas canvas;

	/**
	 * 构造函数，初始化主窗口
	 */
	public UI() {
		// 创建迷宫生成器
		col = 12;
		row = 9;
		seed = md5("yan");
		isRand = false;
		mc = new MazeCreator(col, row, seed, isRand);
		
		// 创建方块生成器
		roadSize = 2;
		bc = new BlockCreator(roadSize);
		
		// 创建画板
		pixel = 12;
		canvas = new Canvas(pixel);

		this.setTitle("YAN的迷宫生成器");
		this.setSize(1024, 768);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setJMenuBar(getJMenuBar());// 设置菜单
		this.setContentPane(getContentPanel());// 主界面布局
		
		// 生成迷宫
		this.updateMaze();

		// 显示窗口
		this.setVisible(true);

	}

	/**
	 * 主界面布局
	 * 
	 * @return
	 */
	private JPanel getContentPanel() {
		JPanel panel = new JPanel(new BorderLayout());

		// 画板
		JScrollPane pane = new JScrollPane();
		pane.setViewportView(canvas);
		panel.add(pane, BorderLayout.CENTER);

		// 工具条
		panel.add(getJToolBar(), BorderLayout.EAST);

		// 状态栏
		JPanel status = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(status, BorderLayout.SOUTH);
		statsText = new JLabel();
		status.add(statsText);

		return panel;
	}

	/**
	 * 刷新迷宫
	 */
	private void updateMaze() {
		// 生成迷宫
		mc.config(row, col, seed, isRand);
		mc.create();

		// 生成方块
		updateBlocks();

		// 生成图形
		updateCanvas();
	}

	void updateBlocks() {
		bc.setRoadSize(roadSize);
		bc.create(mc);
		
		int bRow = bc.getBlockRow();
		int bCol = bc.getBlockCol();
		int bCnt = bc.getBlockCount();
		int bStack = bCnt / 64;
		if (bCnt % 64 != 0)
			bStack++;
		String str = String.format(format, bRow, bCol, bCnt, bStack);
		statsText.setText(str);
	}
	
	void updateCanvas() {
		canvas.setPixel(pixel);
		canvas.setMap(bc);
		
		// 刷新
		canvas.updateUI();
	}
	
	/**
	 * 菜单
	 */
	public JMenuBar getJMenuBar() {
		JMenuBar bar = new JMenuBar();

		JMenu fMenu = new JMenu("文件(F)");
		bar.add(fMenu);

		JMenuItem exItem = new JMenuItem("导出png图片(E)");
		exItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					ImageIO.write(canvas.getImage(), "png", new File("map.png"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		fMenu.add(exItem);

		return bar;
	}

	/**
	 * 工具条
	 * 
	 * @return
	 */
	public JToolBar getJToolBar() {
		JToolBar toolBar = new JToolBar("工具条");
		toolBar.setOrientation(JToolBar.VERTICAL);
		toolBar.setAlignmentY(5);

		final JLabel l1 = new JLabel("行数: " + row);
		addTool(toolBar, l1);

		final JSlider rowSlider = new JSlider(JSlider.HORIZONTAL, 5, 40, row);
		rowSlider.setMajorTickSpacing(10);
		rowSlider.setPaintLabels(true);
		rowSlider.setPaintTicks(true);
		rowSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				row = rowSlider.getValue();
				l1.setText("行数: " + row);
				updateMaze();
			}
		});
		addTool(toolBar, rowSlider);

		final JLabel l2 = new JLabel("列数: " + col);
		addTool(toolBar, l2);

		final JSlider colSlider = new JSlider(JSlider.HORIZONTAL, 5, 60, col);
		colSlider.setMajorTickSpacing(10);
		colSlider.setPaintLabels(true);
		colSlider.setPaintTicks(true);
		colSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				col = colSlider.getValue();
				l2.setText("列数:" + col);
				updateMaze();
			}
		});
		addTool(toolBar, colSlider);

		final JLabel l4 = new JLabel("道路宽度:" + roadSize);
		addTool(toolBar, l4);

		final JSlider roadSlider = new JSlider(JSlider.HORIZONTAL, 1, 3,
				roadSize);
		roadSlider.setMajorTickSpacing(1);
		roadSlider.setPaintLabels(true);
		roadSlider.setPaintTicks(true);
		roadSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				roadSize = roadSlider.getValue();
				l4.setText("道路宽度:" + roadSize);

				// 生成方块
				updateBlocks();

				// 生成图形
				updateCanvas();
			}

		});
		addTool(toolBar, roadSlider);

		final JLabel l5 = new JLabel("方块像素:" + pixel);
		addTool(toolBar, l5);

		final JSlider pixelSlider = new JSlider(JSlider.HORIZONTAL, 8, 32,
				pixel);
		pixelSlider.setPaintLabels(true);
		pixelSlider.setMajorTickSpacing(8);
		pixelSlider.setPaintTicks(true);
		pixelSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				pixel = pixelSlider.getValue();
				l5.setText("方块像素:" + pixel);
				
				updateCanvas();
			}
		});
		addTool(toolBar, pixelSlider);

		final JTextField seedText = new JTextField(10);
		final JCheckBox isRandCheck = new JCheckBox("随机生成迷宫");
		isRandCheck.setSelected(isRand);
		isRandCheck.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				isRand = isRandCheck.isSelected();
				seedText.setEnabled(!isRand);
			}
		});
		addTool(toolBar, isRandCheck);

		JLabel l3 = new JLabel("使用迷宫种子:");
		addTool(toolBar, l3);

		seedText.setText("yan");
		addTool(toolBar, seedText);

		JButton refreshBtn = new JButton("刷新地图");
		refreshBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isRand) {
					String seeds = seedText.getText();
					seed = md5(seeds);
				}
				updateMaze();
			}
		});
		addTool(toolBar, refreshBtn);
		
		JButton astarBtn = new JButton("A星寻路");
		astarBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				new Thread() {
					public void run() {
						bc.pathfinding();
						updateCanvas();
					}
				}.start();
			}
		});
		addTool(toolBar, astarBtn);

		return toolBar;
	}

	/**
	 * 往工具栏中添加控件
	 * @param toolBar
	 * @param comp
	 */
	private void addTool(JToolBar toolBar, Component comp) {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(comp);
		toolBar.add(panel);
	}
	
	private long md5(String seeds) {
		long value = seed;
		try {
			// 使用MD5算法，生成随机种子。
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(seeds.getBytes("UTF-8"));
	
			byte byteData[] = md.digest();
	
			// convert the byte to hex format method 2
			StringBuffer hexString = new StringBuffer();
			hexString.append("0x");
			for (int i = 0; i < 7; i++) {
				String hex = Integer.toHexString(0xff & byteData[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			value = Long.decode(hexString.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return value;
	}

	public static void main(String[] args) {
		new UI();
	}
}
