package net.jmecn.state;

import java.util.ArrayList;
import java.util.List;

import net.jmecn.components.Player;
import net.jmecn.core.Game;
import net.jmecn.net.SingleGameClient;

import org.apache.log4j.Logger;

import com.jme3.app.Application;
import com.jme3.app.state.AppState;
import com.jme3.app.state.BaseAppState;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

public class SingleGameState extends BaseAppState {
	
	static Logger log = Logger.getLogger(SingleGameState.class);
	
	private List<AppState> gameStates = new ArrayList<AppState>();
	
	private SingleGameClient client;

	private EntitySet players;
	@Override
	protected void initialize(Application app) {
		Game game = new Game();
		client = new SingleGameClient(game);
		client.start();
		
		log.info("Client Started");
		
		EntityData ed = client.getEntityData();
		players = ed.getEntities(Player.class);
		
		gameStates.add(new EntityDataState(ed));
		gameStates.add(new ModelState());
		gameStates.add(new HudState());
		gameStates.add(new ShootState());
		gameStates.add(new CollisionState());
		gameStates.add(new InputState());
		
		getStateManager().attachAll(gameStates);
		
		getStateManager().getState(InputState.class).setPlayer(players.getEntity(client.getPlayer()));
		getStateManager().getState(HudState.class).setPlayer(players.getEntity(client.getPlayer()));
	}

	@Override
	protected void cleanup(Application app) {
		for( int i = gameStates.size() -1; i >= 0; i-- ) {
            AppState state = gameStates.get(i);
            getStateManager().detach(state);   
        }
        gameStates.clear();
		client.close();
	}

	@Override
	protected void onEnable() {
		for(AppState state : gameStates) {
			state.setEnabled(true);
		}
	}

	@Override
	protected void onDisable() {
		for(AppState state : gameStates) {
			state.setEnabled(false);
		}
	}
	
	public void update(float tpf) {
		if (players.applyChanges()) {
			getStateManager().getState(HudState.class).updatePlayer();
		}
	}
	
	public void quitGame() {
		this.setEnabled(false);
		getStateManager().getState(MainAppState.class).setEnabled(true);
	}

}
