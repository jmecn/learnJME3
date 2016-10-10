package net.jmecn.app;

import net.jmecn.core.CollisionAppState;
import net.jmecn.core.PlayerInputAppState;
import net.jmecn.core.EntityDataState;
import net.jmecn.core.GameAppState;
import net.jmecn.core.VisualAppState;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;

public class TestES extends SimpleApplication {

	private TestES () {
		super(new FlyCamAppState(),
				new VisualAppState(),
				new CollisionAppState(),
				new PlayerInputAppState(),
				new EntityDataState(),
				new GameAppState());
	}
	
	@Override
	public void simpleInitApp() {
	}

	public static void main(String[] args) {
		TestES app = new TestES();
        app.start();
	}

}
