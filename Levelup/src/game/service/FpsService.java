package game.service;

import game.core.Game;
import game.core.Service;
import game.core.Timer;

public class FpsService implements Service {

	Timer timer;
	@Override
	public void initialize(Game game) {
		timer = game.getTimer();

	}

	@Override
	public void update(long time) {
		System.out.println("FPS:" + timer.getFramePerSecond());

	}

	@Override
	public void terminate(Game game) {
		// TODO Auto-generated method stub

	}

}
