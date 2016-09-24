package client;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.jme3.math.Vector3f;
import com.jme3.system.JmeCanvasContext;

/**
 * 主窗口类
 * 
 * @author yanmaoyuan
 * 
 */
public class Main extends JFrame {

	private MyGame app;
	protected Canvas canvas;

	public Main(MyGame game) {
		this.app = game;

		this.setTitle("我的窗口");
		this.setSize(800, 600);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.getContentPane().setLayout(new BorderLayout());

		// 添加JME画布 Add JME3 canvas to the GUI
		JmeCanvasContext context = (JmeCanvasContext) app.getContext();
		Canvas canvas = context.getCanvas();
		this.getContentPane().add(canvas, BorderLayout.CENTER);
		
		this.getContentPane().add(getStatusPanel(), BorderLayout.SOUTH);

		redirectConsole();
		
		createMenu();
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				app.stop();
				timer.cancel();
			}
		});
	}
	
	private JLabel timeLabel;
	private Timer timer;
	private JPanel getStatusPanel() {
		JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		timeLabel = new JLabel("Time: 0");
		statusPanel.add(timeLabel);
		
		// 创建一个定时器，监控JME应用状态 Create a timer to update JME3 app's attributes.
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				float fps = app.getTimer().getFrameRate();
				float time = app.getTimer().getTimeInSeconds();
				
				String text = String.format(" FPS:%.2f ServerTime:%.1fs", fps, time);
				timeLabel.setText(text);
			}
		}, 0, 1000);
		
		return statusPanel;
	}

	
	private TextArea console;
	
	public class MyOutputStream extends OutputStream {
		public void write(int arg0) throws IOException {
			// ignore
		}

		public void write(byte data[]) throws IOException {
			console.append(new String(data));
		}

		public void write(byte data[], int off, int len) throws IOException {
			console.append(new String(data, off, len));
			console.setCaretPosition(console.getText().length());
		}
	}
	
	/**
	 * 重定向控制台
	 * redirect System.out to my console
	 */
	private void redirectConsole() {
		console = new TextArea();
		console.setBackground(Color.WHITE);
		console.setEditable(false);
		
		JScrollPane scroll = new JScrollPane();// 滚动条
		scroll.setViewportView(console);
		
		this.getContentPane().add(scroll, BorderLayout.SOUTH);
		
		// redirect system out
		PrintStream printStream = new PrintStream(new MyOutputStream());
		System.setOut(printStream);
		System.setErr(printStream);
	}
	
	/**
	 * 创建菜单
	 * Create a menu
	 */
	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu menuFile = new JMenu("文件(F)");
		menuFile.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menuFile);

		final JMenuItem itemBox = new JMenuItem("创建Box(B)");
		itemBox.setMnemonic(KeyEvent.VK_B);
		itemBox.setAccelerator(KeyStroke.getKeyStroke("B"));
		menuFile.add(itemBox);
		itemBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				app.enqueue(new Callable<Void>() {
					public Void call() {
						
						app.createBox(new Vector3f(2, 0, 0));
						return null;
					}
				});
			}
		});
		
		final JMenuItem itemLoad = new JMenuItem("导入模型文件(L)");
		itemLoad.setMnemonic(KeyEvent.VK_L);
		itemLoad.setAccelerator(KeyStroke.getKeyStroke("L"));
		menuFile.add(itemLoad);
		itemLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadModel();
			}
		});

		menuFile.add(new JSeparator());

		JMenuItem itemExit = new JMenuItem("退出(X)");
		itemExit.setMnemonic(KeyEvent.VK_X);
		itemExit.setAccelerator(KeyStroke.getKeyStroke("ctrl W"));
		menuFile.add(itemExit);
		itemExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				dispose();
				app.stop();
			}
		});
	}
	
	private JFileChooser chooser;
	private JFileChooser getFileChooser() {
		if (chooser == null) {
			chooser = new JFileChooser();
			
			chooser.setDialogType(JFileChooser.OPEN_DIALOG);
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setDialogTitle("打开文件");
			
			// add filters
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("*.j3o", "j3o"));
		}
		
		return chooser;
	}
	/**
	 * 导入模型
	 * Load model
	 */
	private void loadModel() {
		JFileChooser chooser = getFileChooser();
		
		// 选择文件路径 Choose a model file
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			if (file != null) {
				final String path = file.getAbsolutePath();
				app.enqueue(new Callable<Void>() {
					public Void call() {
						app.loadModel(path);
						
						return null;
					}
				});
	
			}
		}
	}
	
	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				MyGame game = new MyGame();
				game.setShowSettings(false);
				game.setPauseOnLostFocus(false);
				game.createCanvas();

				// 启动游戏  Start game canvas
				game.startCanvas(true);

				// 创建窗口 Start GUI
				Main main = new Main(game);
				main.setVisible(true);
			}
		});
	}

}
