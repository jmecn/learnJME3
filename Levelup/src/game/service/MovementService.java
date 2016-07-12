package game.service;

import game.components.Position;
import game.components.Target;
import game.components.Vector;
import game.core.Game;
import game.core.Service;

import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;

public class MovementService implements Service {

	EntitySet movable;
	@Override
	public void initialize(Game game) {
		movable = game.getEntityData().getEntities(Position.class, Target.class);

	}

	@Override
	public void update(long time) {
		if (movable.applyChanges()) {
		}
	}

	@Override
	public void terminate(Game game) {
		// TODO Auto-generated method stub

	}

}
