package game.service;

import game.components.Decay;
import game.core.Game;
import game.core.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

/**
 * Removes any entities whose Decay time has come.
 * 
 * @author Paul Speed
 */
public class DecayService implements Service {

	static Logger log = LoggerFactory.getLogger(DecayService.class);

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
					log.info("าฦณýสตฬๅ:" + e);
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