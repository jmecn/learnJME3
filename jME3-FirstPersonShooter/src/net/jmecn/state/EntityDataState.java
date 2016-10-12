package net.jmecn.state;

import com.jme3.app.state.AbstractAppState;
import com.simsilica.es.EntityData;
import com.simsilica.es.base.DefaultEntityData;

public class EntityDataState extends AbstractAppState {
	private EntityData entityData;

	public EntityDataState() {
		this(new DefaultEntityData());
	}

	public EntityDataState(EntityData ed) {
		this.entityData = ed;
	}

	public EntityData getEntityData() {
		return entityData;
	}

	@Override
	public void cleanup() {
		entityData.close();
		entityData = null; // cannot be reused
	}
}