package net.jmecn.state;

import com.jme3.app.Application;
import com.jme3.app.state.AppState;
import com.jme3.app.state.BaseAppState;

public class SingleGameState extends BaseAppState {
	
	AppState[] states = new AppState[]{
			new EntityDataState(),
			new VisualAppState(),
			new HudState(),
			new ShootState(),
			new CollisionAppState(),
			new PlayerInputAppState(),
			new GameAppState(),
	};

	@Override
	protected void initialize(Application app) {
		getStateManager().attachAll(states);
	}

	@Override
	protected void cleanup(Application app) {
		for(int i= states.length; i >0; i--) {
			getStateManager().detach(states[i-1]);
		}
	}

	@Override
	protected void onEnable() {
		for(AppState state : states) {
			state.setEnabled(true);
		}
	}

	@Override
	protected void onDisable() {
		for(AppState state : states) {
			state.setEnabled(false);
		}
	}
	
	public void quitGame() {
		this.setEnabled(false);
		getStateManager().getState(MainAppState.class).setEnabled(true);
	}

}
