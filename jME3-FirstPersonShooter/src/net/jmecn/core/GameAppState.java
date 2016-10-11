package net.jmecn.core;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;

/**
 * ÓÎÏ·Ö÷Àà
 * @author yanmaoyuan
 *
 */
public class GameAppState extends BaseAppState {

	private EntityData ed;
	private SimpleApplication app;
	
	private EntityId player = null;
	
	@Override
	public void initialize(Application app) {

		this.app = (SimpleApplication) app;
		this.app.setPauseOnLostFocus(true);
		this.app.setDisplayStatView(true);

		this.ed = this.app.getStateManager().getState(EntityDataState.class)
				.getEntityData();

		EntityId world = ed.createEntity();
		this.ed.setComponents(world,
				new Position(new Vector3f(0, 0, 0)),
				new Model(Model.ICEWORLD),
				new Collision());

		EntityId sky = ed.createEntity();
		this.ed.setComponents(sky,
				new Position(new Vector3f(0, 0, 0)),
				new Model(Model.SKY));
		
        player = ed.createEntity();
        ed.setComponents(player, new Player(),
        		new Position(new Vector3f(200, 20, 80)),
        		new Model(Model.OTO),
        		new Collision(50f));
	}

	@Override
	protected void cleanup(Application app) {
	}


	@Override
	protected void onEnable() {
	}


	@Override
	protected void onDisable() {
	}

	public EntityId getPlayer() {
		return player;
	}
}