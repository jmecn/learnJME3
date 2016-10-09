package jmecn.app;

import jmecn.state.MainAppState;
import strongdk.jme.appstate.console.ConsoleAppState;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.system.AppSettings;

public class MyGame extends SimpleApplication {

	public MyGame() {
		// new AxisAppState()
		super(new DebugKeysAppState(), new FlyCamAppState(), new StatsAppState());
	}
	
	@Override
	public void simpleInitApp() {
		stateManager.attach(new ConsoleAppState());
		stateManager.attach(new MainAppState());
	}

	
	public void simpeUpdate(float tpf) {
	}

	public static void main(String[] args) {
		AppSettings settings = new AppSettings(true);
		settings.setSamples(0);
		settings.setResolution(1024, 768);
		settings.setTitle("www.jmecn.net");
		
		MyGame game = new MyGame();
		game.setSettings(settings);
		game.setShowSettings(false);// 不显示设置窗口
		game.setPauseOnLostFocus(false);
		game.start();
	}

}
