package net.jmecn.app;

import net.jmecn.state.CollisionState;
import net.jmecn.state.EntityDataState;
import net.jmecn.state.HudState;
import net.jmecn.state.InputState;
import net.jmecn.state.ShootState;
import net.jmecn.state.ModelState;
import net.jmecn.state.SingleGameState;

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
				new SingleGameState());
	}

	@Override
	public void simpleInitApp() {
	}

	public static void main(String[] args) {
		TestSinglePlayer app = new TestSinglePlayer();
		app.start();
	}

}
