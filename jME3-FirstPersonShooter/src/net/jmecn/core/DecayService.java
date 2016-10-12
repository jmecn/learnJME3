package net.jmecn.core;

import net.jmecn.components.Decay;

import org.apache.log4j.Logger;

import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

/**
 * Removes any entities whose Decay time has come.
 * 
 * @author Paul Speed
 */
public class DecayService implements Service {

	static Logger log = Logger.getLogger(DecayService.class);

	private EntityData ed;
	private EntitySet decaying;

	public void initialize(Game game) {
		this.ed = game.getEntityData();
		decaying = ed.getEntities(Decay.class);
		
	}

	public void update(long gameTime) {
		decaying.applyChanges();
		if (!decaying.isEmpty()) {
			for (Entity e : decaying) {
				Decay decay = e.get(Decay.class);
				if( decay.getPercent() >= 1.0 ) {
					log.info("Remove entity " + e);
	                ed.removeEntity(e.getId());
	            }
			}
		}
	}

	public void terminate(Game game) {
		decaying.release();
		decaying = null;
	}

}