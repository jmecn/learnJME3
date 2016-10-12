package net.jmecn.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.simsilica.es.base.DefaultEntityData;

/**
 * 游戏主类
 * @author yanmaoyuan
 *
 */
public class Game {
	
	static Logger log = Logger.getLogger(Game.class);
	
	private boolean started;
	private boolean enabled;
	
	private ScheduledExecutorService executor;
	private ServiceRunnable serviceRunner;
	private Timer timer;
	private List<Service> services = new ArrayList<Service>();
	private EntityFactory factory;
	
	public Game() {
		// 添加游戏服务
		services.add(new EntityDataService());
		services.add(new ExplosionService());// 爆炸要在Decay之前，免得丢失数据
		services.add(new DecayService());
		
		// 初始化定时器
		timer = new Timer();
		// 初始化游戏主线程
		serviceRunner = new ServiceRunnable();
	}

    public DefaultEntityData getEntityData() {
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
	 * 移除服务
	 * @param service
	 */
	public void removeService(Service service) {
		if (services.contains(service)) {
			service.terminate(this);
			services.remove(service);
		}
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
		
		// 固定刷新率每秒16帧，时间间隔为62.5毫秒。
		executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(serviceRunner, 0, 62, TimeUnit.MILLISECONDS);
		started = true;
		
		enabled = true;
		log.info("开始游戏");
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
