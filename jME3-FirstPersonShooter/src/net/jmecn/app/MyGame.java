package net.jmecn.app;

import net.jmecn.state.MainAppState;
import strongdk.jme.appstate.console.ConsoleAppState;
import strongdk.jme.appstate.console.ConsoleDefaultCommandsAppState;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.system.AppSettings;

/**
 * 游戏主类
 * 
 * @author yanmaoyuan
 * 
 */
public class MyGame extends SimpleApplication {

	/**
	 * 构造方法，初始化游戏所使用的AppState，以及启动参数。
	 */
	public MyGame() {
		// 初始化AppState
		super(new DebugKeysAppState(), new FlyCamAppState(),
				new StatsAppState(), new ConsoleAppState(),
				new ConsoleDefaultCommandsAppState(), new MainAppState());

		// 初始化游戏设置
		AppSettings settings = new AppSettings(true);
		settings.setResolution(1024, 768);
		settings.setTitle("www.jmecn.net");

		setSettings(settings);
		setShowSettings(false);// 不显示设置窗口
		setPauseOnLostFocus(false);// 窗口失去焦点时，游戏不暂停
	}

	@Override
	public void simpleInitApp() {
		// 删除原有的按ESC退出游戏的功能
		inputManager.deleteMapping(INPUT_MAPPING_EXIT);
	}

	public static void main(String[] args) {
		MyGame game = new MyGame();
		game.start();
	}

}
