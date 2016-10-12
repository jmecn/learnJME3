package net.jmecn.app;

import net.jmecn.state.CollisionAppState;
import net.jmecn.state.EntityDataState;
import net.jmecn.state.GameAppState;
import net.jmecn.state.HudState;
import net.jmecn.state.PlayerInputAppState;
import net.jmecn.state.ShootState;
import net.jmecn.state.VisualAppState;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;

/**
 * ≤‚ ‘µ£»Œ”Œœ∑
 * @author yanmaoyuan
 *
 */
public class TestSinglePlayer extends SimpleApplication {

	private TestSinglePlayer() {
		super(new FlyCamAppState(),
				new StatsAppState(),
				new EntityDataState(),
				new GameAppState(),
				new HudState(),
				new ShootState(),
				new VisualAppState(),
				new CollisionAppState(),
				new PlayerInputAppState());
	}

	@Override
	public void simpleInitApp() {
	}

	public static void main(String[] args) {
		TestSinglePlayer app = new TestSinglePlayer();
		app.start();
	}

}
