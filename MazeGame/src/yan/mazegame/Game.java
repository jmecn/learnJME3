package yan.mazegame;

import yan.mazegame.states.GameState;

import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.system.AppSettings;

/**
 * 程序入口
 * 
 * @author yanmaoyuan
 * 
 */
public class Game extends SimpleApplication {

	// 启动入口
	public static void main(String[] args) {
		SimpleApplication app = new Game();

		// 设置
		AppSettings settings = new AppSettings(true);
		settings.setTitle("Maze Game");
		settings.setResolution(1024, 768);

		app.setSettings(settings);
		app.setShowSettings(false);
		app.setPauseOnLostFocus(false);

		app.start();
	}

	// 构造方法
	public Game() {
		super(new StatsAppState(), new GameState(),
				new ScreenshotAppState(""));
	}

	@Override
	public void simpleInitApp() {}
}
