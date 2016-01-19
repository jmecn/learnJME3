package teris.game;

import com.jme3.app.SimpleApplication;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

public class Game extends SimpleApplication {

	private Node wellNode = new Node("well");
	private Node controlNode = new Node("controll");
	
	@Override
	public void simpleInitApp() {
		rootNode.attachChild(wellNode);
		rootNode.attachChild(controlNode);
	}

	public static void main(String[] args) {
		Game app = new Game();
		AppSettings setting = new AppSettings(true);
		setting.setWidth(800);
		setting.setHeight(600);
		setting.setTitle("3D Teris");
		app.setSettings(setting);
		app.showSettings = false;
		app.start();
	}

}
