package game.service;

import game.core.Game;
import game.core.Service;

import com.simsilica.es.EntityData;
import com.simsilica.es.base.DefaultEntityData;

public class EntityDataService implements Service {

	private EntityData ed;

	public EntityDataService() {
	}

	public EntityData getEntityData() {
		return ed;
	}

	protected EntityData createEntityData() {
		return new DefaultEntityData();
	}

	public void initialize(Game systems) {
		this.ed = createEntityData();
	}

	public void update(long gameTime) {
	}

	public void terminate(Game systems) {
		ed.close();
	}

}
