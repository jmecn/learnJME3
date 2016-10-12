package net.jmecn.core;

import org.apache.log4j.Logger;

import net.jmecn.components.Decay;
import net.jmecn.components.Model;
import net.jmecn.components.Position;

import com.simsilica.es.ComponentFilter;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.es.Filters;

public class ExplosionService implements Service {
	static Logger log = Logger.getLogger(ExplosionService.class);

	private EntityData ed;
	private EntitySet bombs;

	@Override
	public void initialize(Game game) {
		this.ed = game.getEntityData();
		
		// Ö»¹ØÐÄÕ¨µ¯
		ComponentFilter<Model> filter = Filters.fieldEquals(
				Model.class, "name",
				Model.BOMB);

		bombs = ed.getEntities(filter, Model.class, Decay.class, Position.class);
	}

	@Override
	public void update(long time) {
		bombs.applyChanges();
			for (Entity e : bombs) {
				if (e.get(Decay.class).getPercent() >= 1) {
					log.info("Explosion~ " + e.getId());
					Position p = e.get(Position.class);
					EntityId explosion = ed.createEntity();
					ed.setComponents(explosion,
							new Model(Model.EXPLOSION),
							new Position(p.getLocation()));
				}
			}
	}

	@Override
	public void terminate(Game game) {
		bombs.release();
		bombs = null;
	}

}
