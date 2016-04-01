package yan.mazegame;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;

public class Main {

	public static void main(String[] args) {
		SimpleApplication app = new Game();
		
		AppSettings settings = new AppSettings(true);
		settings.setTitle("Maze Game");
		settings.setResolution(640, 480);
		
		app.setSettings(settings);
		app.setShowSettings(false);
		app.setPauseOnLostFocus(false);
		
		app.start();
	}

}
