package game.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.components.Decay;
import game.components.Position;
import game.components.Target;
import game.components.Velocity;
import game.core.Game;
import game.core.Service;

import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;

public class AiService implements Service {

	private Logger log = LoggerFactory.getLogger(AiService.class);
	
	private EntitySet entities;// 坏人

	@Override
	public void initialize(Game game) {
		entities = game.getEntityData().getEntities(Position.class, Target.class);
	}

	@Override
	public void update(long time) {
		if (entities.applyChanges()) {
			for (Entity e : entities) {
				Vector3f target = e.get(Target.class).getLocation();
				Vector3f loc = e.get(Position.class).getLocation();
				if (target.distanceSquared(loc) < 100) {
					e.set(new Decay(0));
					log.info("抓到目标啦!" + e);
				} else {
					Vector3f v = target.subtract(loc);
					v.normalizeLocal().multLocal(50);
					// 设置移动速度
					e.set(new Velocity(v));
				}
			}
		}
	}

	@Override
	public void terminate(Game game) {
		entities.release();
		entities = null;

	}

}
