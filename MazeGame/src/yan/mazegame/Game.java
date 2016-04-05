package yan.mazegame;

import yan.mazegame.states.GameState;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.system.AppSettings;

/**
 * ³ÌÐòÈë¿Ú
 * 
 * @author yanmaoyuan
 * 
 */
public class Game extends SimpleApplication {

	public static void main(String[] args) {
		SimpleApplication app = new Game();

		AppSettings settings = new AppSettings(true);
		settings.setTitle("Maze Game");
		settings.setResolution(1024, 768);

		app.setSettings(settings);
		app.setShowSettings(false);
		app.setPauseOnLostFocus(false);

		app.start();
	}

	public Game() {
		super(new GameState(),
				new ScreenshotAppState(""));
	}

	@Override
	public void simpleInitApp() {}
}
