package jmecn.app;

import jmecn.state.AxisAppState;
import jmecn.state.InGameAppState;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.system.AppSettings;

public class MyGame extends SimpleApplication {

	public MyGame() {
		super(new DebugKeysAppState(), new FlyCamAppState(),  new StatsAppState(), new AxisAppState(), new InGameAppState());
	}
	
	@Override
	public void simpleInitApp() {
	}

	
	public void simpeUpdate(float tpf) {
	}

	public static void main(String[] args) {
		AppSettings settings = new AppSettings(true);
		settings.setResolution(1024, 768);
		settings.setFullscreen(true);
		settings.setSamples(40);
		settings.setTitle("www.jmecn.net");
		
		MyGame game = new MyGame();
		game.setSettings(settings);
		game.setShowSettings(false);// 不显示设置窗口
		game.setPauseOnLostFocus(false);
		game.start();
	}

}
