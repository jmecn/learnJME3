package game.core;

import game.service.*;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simsilica.es.EntityData;

/**
 * 游戏主类
 * @author yanmaoyuan
 *
 */
public class Game {
	
	private Logger log = LoggerFactory.getLogger(Game.class);
	
	private boolean started;
	private boolean enabled;
	
	private ScheduledExecutorService executor;
	private ServiceRunnable serviceRunner;
	private Timer timer;
	private JFrame frame;
	private List<Service> services = new ArrayList<Service>();
	private EntityFactory factory;
	
	public Game() {
		// 添加游戏服务
		services.add(new EntityDataService());
		services.add(new ControlService());
		services.add(new SinglePlayerService());
		services.add(new AiService());
		services.add(new MovementService());
		services.add(new CollisionService());
		services.add(new BoundaryService(20));
		services.add(new SpawnService());
		services.add(new DecayService());
		services.add(new ViewService());
		
		// 初始化定时器
		timer = new Timer();
		// 初始化游戏主线程
		serviceRunner = new ServiceRunnable();
	}

    public EntityData getEntityData() {
        return getService(EntityDataService.class).getEntityData(); 
    }
    
    /**
     * 添加服务
     * @param s
     * @return
     */
    public <T extends Service> T addService( T s ) {
        if( started ) {
            throw new IllegalStateException( "游戏已启动." );
        }
        services.add(s);
        return s;
    }
    
    /**
     * 查询服务
     * @param type
     * @return
     */
	@SuppressWarnings("unchecked")
	public <T extends Service> T getService(Class<T> type) {
		int len = services.size();
		for (int i = 0; i < len; i++) {
			Service s = services.get(i);
			if (type.isInstance(s)) {
				return (T) s;
			}
		}
		return null;
	}

	/**
	 * 开始游戏
	 */
	public void start() {
		if (started) {
			return;
		}
		
		// 顺序初始化所有服务
		for (Service s : services) {
			s.initialize(this);
		}
		
		factory = new EntityFactory(getEntityData());
		
		// 创建窗口
		createFrame();
		
		// 固定刷新率每秒16帧，时间间隔为62.5毫秒。
		executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(serviceRunner, 0, 62, TimeUnit.MILLISECONDS);
		started = true;
		
		enabled = true;
		log.info("开始游戏");
	}

	/**
	 * 创建游戏窗口
	 */
	private void createFrame() {
		// 创建窗口
		JFrame frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				stop();
			}

			@Override
			public void windowActivated(WindowEvent arg0) {
				enabled = true;
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
				enabled = false;
			}
			
		});
		frame.addKeyListener(getService(ControlService.class));
		
		// 配置窗口参数
		frame.setTitle("My Game");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		
		// 添加ViewService
		frame.add(getService(ViewService.class));
		frame.pack();
		
		// 窗口居中
		Dimension frameSize = frame.getSize();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int locX = (screenSize.width - frameSize.width)/2;
		int locY = (screenSize.height - frameSize.height)/2;
		frame.setLocation(locX, locY);
		
		// 显示窗口
		frame.setVisible(true);
	}
	
	/**
	 * 结束游戏
	 */
	public void stop() {
		if (!started) {
			return;
		}
		executor.shutdown();

		// 逆序清理所有的服务
		for (int i = services.size() - 1; i >= 0; i--) {
			Service s = services.get(i);
			s.terminate(this);
		}
		started = false;
		enabled = false;
		log.info("游戏结束");
		
		System.exit(0);
	}

	/**
	 * 运行所有服务
	 * @param gameTime
	 */
	protected void runServices(long gameTime) {
		int len = services.size();
		for (int i = 0; i < len; i++) {
			Service s = services.get(i);
			s.update(gameTime);
		}
	}

    public long getGameTime() {
        return timer.getTime(); 
    }
    
    public Timer getTimer() {
    	return timer;
    }
    
    public JFrame getFrame() {
    	return frame;
    }
    
    public EntityFactory getFactory() {
    	return factory;
    }
    
	private class ServiceRunnable implements Runnable {
		public void run() {
			
			try {
				timer.update();
				if (!enabled)
					return;
				runServices(timer.getTimePerFrame());
			} catch (RuntimeException e) {
				log.error("服务运行发生异常", e);
			}
		}
	}

	public static void main(String[] args) {
		Game game = new Game();
		game.start();
	}
}
