package net.jmecn.core;

import com.simsilica.es.base.DefaultEntityData;

public class EntityDataService implements Service {

	private DefaultEntityData ed;

	public EntityDataService() {
	}

	public DefaultEntityData getEntityData() {
		return ed;
	}

	protected DefaultEntityData createEntityData() {
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
