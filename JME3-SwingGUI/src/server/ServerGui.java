package server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.jme3.app.StatsAppState;

/**
 * Simple Server GUI
 * @author yan
 *
 */
public class ServerGui extends JFrame {
	
	private String title = "Server GUI";
	
	private ServerMain server;
	private int width = 400;
	private int height = 300;
	
	private TextArea console;
	private JComboBox<String> combo;
	private JLabel timeLabel;
	
	private Timer timer;
	
	public ServerGui(final ServerMain server) {
		this.server = server;
		
		// init frame
		this.setTitle(title);
		this.setSize(width, height);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				server.stop();
				timer.cancel();
			}
		});
		
		
		// init list
		combo = new JComboBox<String>();
		combo.addItem("A");
		combo.addItem("B");
		combo.addItem("C");
		combo.addItem("D");
		
		combo.addActionListener(listener);
		
		JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
		header.add(new JLabel("List:"));
		header.add(combo);
		
		// init console
		console = new TextArea();
		console.setBackground(Color.WHITE);
		console.setEditable(false);
		JScrollPane body = new JScrollPane();
		body.setViewportView(console);
		
		// redirect system out
		PrintStream printStream = new PrintStream(new MyOutputStream());
		System.setOut(printStream);
		System.setErr(printStream);

		// init timer
		timeLabel = new JLabel("Time: 0");
		add(timeLabel);
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				float fps = server.getTimer().getFrameRate();
				float time = server.getTimer().getTimeInSeconds();
				
				String text = String.format(" FPS:%.2f ServerTime:%.1fs", fps, time);
				timeLabel.setText(text);
			}
		}, 0, 1000);
		
		// border layout
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(header, BorderLayout.NORTH);
		panel.add(body, BorderLayout.CENTER);
		panel.add(timeLabel, BorderLayout.SOUTH);
		this.setContentPane(panel);
		
		setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
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
	
	private ActionListener listener = new ActionListener() {
		public void actionPerformed(final ActionEvent e) {
			// know what you choose
			System.out.println("You select: " + combo.getSelectedItem());
			
			// do something to your server
			server.enqueue(new Callable<Void>() {
				public Void call() {
					
					// example, not really need this in headless server.
					StatsAppState state = server.getStateManager().getState(StatsAppState.class);
					String text = state.getFpsText().getText();
					System.out.println(text);
					
					return null;
				}
			});
		}
	};
	
	public static void main(String[] args) {
		ServerMain server = new ServerMain();
		server.start(com.jme3.system.JmeContext.Type.Headless);
		
		new ServerGui(server);
	}

}
