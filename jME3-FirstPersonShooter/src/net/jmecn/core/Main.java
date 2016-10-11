package net.jmecn.core;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;

/**
 * 程序启动入口
 * 
 * @author yanmaoyuan
 * 
 */
public class Main extends SimpleApplication {

	private Main() {
		super(new StatsAppState(), new FlyCamAppState(), new ShootState(), new VisualAppState(),
				new CollisionAppState(), new PlayerInputAppState(),
				new EntityDataState(), new GameAppState());
	}

	@Override
	public void simpleInitApp() {
	}

	public static void main(String[] args) {
		Main app = new Main();
		app.start();
	}

}
