package game.service;

import game.components.Decay;
import game.components.Position;
import game.components.Velocity;
import game.core.Game;
import game.core.Service;

import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;

/**
 * 实体运动计算子系统
 * @author yanmaoyuan
 *
 */
public class MovementService implements Service {

	EntitySet entities;
	@Override
	public void initialize(Game game) {
		entities = game.getEntityData().getEntities(Position.class, Velocity.class);
	}

	@Override
	public void update(long time) {
		float tpf = time / 1000000000f;
        entities.applyChanges();
        for( Entity e : entities ) {
            Position pos = e.get(Position.class);
            Velocity vel = e.get(Velocity.class);

            Vector3f loc = pos.getLocation();
            Vector3f linear = vel.getLinear();
            loc = loc.add( (float)(linear.x * tpf),
                           (float)(linear.y * tpf),
                           (float)(linear.z * tpf) );

            if (loc.x > ViewService.WIDTH || loc.z > ViewService.HEIGHT || loc.x < 0 || loc.z < 0) {
            	e.set(new Decay(0));
            }
            e.set(new Position(loc, null));
        }
	}

	@Override
	public void terminate(Game game) {
		entities.release();
		entities = null;

	}

}
