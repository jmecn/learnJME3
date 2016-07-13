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
	
	private EntitySet badEntities;// 坏人

	@Override
	public void initialize(Game game) {
		badEntities = game.getEntityData().getEntities(Position.class, Target.class);
	}

	@Override
	public void update(long time) {
		if (badEntities.applyChanges()) {
			for (Entity bad : badEntities) {
				Vector3f target = bad.get(Target.class).getLocation();
				Vector3f loc = bad.get(Position.class).getLocation();
				if (target.distanceSquared(loc) < 100) {
					bad.set(new Decay(1000));
				} else {
					Vector3f v = target.subtract(loc);
					v.normalizeLocal().multLocal(50);
					// 设置坏人的移动速度
					bad.set(new Velocity(v));
				}
			}
		}
	}

	@Override
	public void terminate(Game game) {
		badEntities.release();
		badEntities = null;

	}

}
