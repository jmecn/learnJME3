package game.service;

import game.components.AoI;
import game.components.Model;
import game.components.Position;
import game.components.Velocity;
import game.core.Game;
import game.core.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;
import com.simsilica.es.Filters;

public class AiService implements Service {

	private Logger log = LoggerFactory.getLogger(AiService.class);
	
	private EntitySet entities;// 坏人
	private EntitySet players;// 玩家
	private EntityData ed;

	@Override
	public void initialize(Game game) {
		ed = game.getEntityData();
		
		entities = ed.getEntities(Filters.fieldEquals(Model.class, "name",
				Model.BAD), Model.class, Position.class, AoI.class);
		
		players = ed.getEntities(Filters.fieldEquals(Model.class, "name",
				Model.PLAYER), Model.class, Position.class);
	}

	@Override
	public void update(long time) {
		entities.applyChanges();
		players.applyChanges();
		
		for (Entity e : entities) {
			Vector3f loc = e.get(Position.class).getLocation();
			float aoiDist = e.get(AoI.class).getRadiusSquare();

			// 寻找离自己距离最近的玩家
			float minDist = Float.MAX_VALUE;
			Entity target = null;
			for(Entity p : players) {
				Vector3f pLoc = p.get(Position.class).getLocation();
				float dist = loc.distanceSquared(pLoc);
				if (dist > aoiDist) continue;

				// 记录距离最近的玩家
				if (dist < minDist) {
					minDist = dist;
					target = p;
				}
			}
			
			// 没有找到距离比较近的玩家，保持原状。
			if (target == null) {
				continue;
			} else {
				// 设置移动速度
				Vector3f v = target.get(Position.class).getLocation().subtract(loc);
				v.normalizeLocal().multLocal(20);
				e.set(new Velocity(v));
			}
		}
	}

	@Override
	public void terminate(Game game) {
		entities.release();
		entities = null;

	}

}
