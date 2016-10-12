package net.jmecn.net;

import net.jmecn.components.Collision;
import net.jmecn.components.Model;
import net.jmecn.components.Player;
import net.jmecn.components.Position;
import net.jmecn.components.Shootable;
import net.jmecn.core.Game;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;

public class SingleGameClient implements GameClient {

	private Game game;
	private EntityData ed;
	private EntityId player;

	public SingleGameClient(Game game) {
		this.game = game;
	}

	// Single player specific
	public void start() {
		game.start();
		ed = game.getEntityData();
		
		EntityId world = ed.createEntity();
		ed.setComponents(world,
				new Position(new Vector3f(0, 0, 0)),
				new Model(Model.ICEWORLD),
				new Collision(),
				new Shootable());

		EntityId sky = ed.createEntity();
		ed.setComponents(sky,
				new Position(new Vector3f(0, 0, 0)),
				new Model(Model.SKY));
		
		player = ed.createEntity();
		ed.setComponents(player, new Player(),
        		new Position(new Vector3f(200, 20, 80)),
        		new Model(Model.OTO),
        		new Collision(50f));
	}
	
	@Override
	public EntityId getPlayer() {
		return player;
	}

	@Override
	public EntityData getEntityData() {
		return game.getEntityData();
	}

	@Override
	public void close() {
		game.stop();
	}

}
