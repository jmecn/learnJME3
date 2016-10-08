package jmecn.app;

import jmecn.state.AxisAppState;
import jmecn.state.InGameAppState;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;

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
		MyGame game = new MyGame();
		game.start();
	}

}
