package net.jmecn.net;

import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;

public interface GameClient {
	
	public EntityId getPlayer();

	public EntityData getEntityData();

	public void close();
	
}