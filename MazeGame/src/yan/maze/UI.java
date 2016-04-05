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
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
	JLabel statsText;// 状态栏

	/* 迷宫参数 */
	private int col = 12; // 列
	private int row = 9; // 行
	private long seed = 47;// 种子
	private boolean isRand = true;// 是否随机
	private int roadSize = 1;// 道路宽度
	private int pixel = 24;// 每个方块的宽度

	// 迷宫生成器
	private MazeCreator mc = new MazeCreator(col, row, seed, isRand);
	// 方块生成器
	private BlockCreator bc = new BlockCreator(roadSize);
	// 绘图板
	private Canvas canvas = new Canvas();

	/**
	 * 构造函数，初始化主窗口
	 */
	public UI() {
		this.setTitle("YAN的迷宫生成器");
		this.setSize(1024, 768);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// 设置菜单
		this.setJMenuBar(getJMenuBar());

		// 主界面布局
		this.setContentPane(getContentPanel());

		// 刷新迷宫
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

	private void updateMaze() {
		// 配置参数
		mc.config(row, col, seed, isRand);
		bc.setRoadSize(roadSize);
		canvas.setPixel(pixel);

		// 生成迷宫
		mc.create();

		// 生成地图
		bc.create(mc);

		// 绘制图形
		canvas.setMap(bc);

		// 刷新
		canvas.updateUI();

		int bRow = bc.getBlockRow();
		int bCol = bc.getBlockCol();
		int bCnt = bc.getBlockCount();
		int bStack = bCnt / 64;
		if (bCnt % 64 != 0)
			bStack++;
		String str = String.format("高:%d, 宽:%d, 需要方块:%d(%d组).", bRow, bCol,
				bCnt, bStack);
		statsText.setText(str);
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
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);// 禁止浮动
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

				bc.setRoadSize(roadSize);
				bc.create(mc);

				// 绘制图形
				canvas.setMap(bc);

				// 刷新
				canvas.updateUI();

				int bRow = bc.getBlockRow();
				int bCol = bc.getBlockCol();
				int bCnt = bc.getBlockCount();
				int bStack = bCnt / 64;
				if (bCnt % 64 != 0)
					bStack++;
				String str = String.format("高:%d, 宽:%d, 需要方块:%d(%d组).", bRow,
						bCol, bCnt, bStack);
				statsText.setText(str);
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
				canvas.setPixel(pixel);
				canvas.setMap(bc);
				canvas.updateUI();
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
				if (isRand) {
					seedText.setEnabled(false);
				} else {
					seedText.setEnabled(true);
				}
			}
		});
		addTool(toolBar, isRandCheck);

		JLabel l3 = new JLabel("使用迷宫种子:");
		addTool(toolBar, l3);

		seedText.setText("" + this.seed);
		addTool(toolBar, seedText);

		JButton button = new JButton("刷新地图");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				if (!isRand)
					try {
						// 使用MD5算法，生成随机种子。
						
						String seeds = seedText.getText();
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
						seed = Long.decode(hexString.toString());
					} catch (Exception ex) {
						ex.printStackTrace();
					}

				updateMaze();
			}
		});
		addTool(toolBar, button);

		return toolBar;
	}

	private void addTool(JToolBar toolBar, Component comp) {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(comp);
		toolBar.add(panel);
	}

	public static void main(String[] args) {
		new UI();
	}
}
