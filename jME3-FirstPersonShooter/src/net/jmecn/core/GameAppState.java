package net.jmecn.core;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;

public class GameAppState extends AbstractAppState {

	private EntityData ed;
	private SimpleApplication app;
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {

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
		
        EntityId me = ed.createEntity();
        ed.setComponents(me, new Player(),
        		new Position(new Vector3f(200, 20, 80)),
        		new Model(Model.OTO),
        		new Collision(50f));
	}

	@Override
	public void cleanup() {
	}

	@Override
	public void update(float tpf) {
	}

}