package game.service;

import game.components.Decay;
import game.components.Position;
import game.core.Game;
import game.core.Service;

import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

/**
 * 检测所有实体是否处于屏幕边界。
 * 
 * @author yanmaoyuan
 * 
 */
public class BoundaryService implements Service {

	private EntityData ed;
	private EntitySet entities;
	private float margin;// 外边距
	private Vector3f min;
	private Vector3f max;

	public BoundaryService(float margin) {
		this.margin = margin;
	}

	@Override
	public void initialize(Game game) {
		ed = game.getEntityData();
		entities = ed.getEntities(Position.class);

		float width = ViewService.WIDTH;
		float height = ViewService.HEIGHT;

		// 计算边界的最大值和最小值，用于检测实体的是否超出了边界。
		Vector3f worldMin = new Vector3f(0, 0, 0);
		Vector3f worldMax = new Vector3f(width, 0, height);
		min = worldMin.addLocal(-margin, -margin, -margin);
		max = worldMax.addLocal(margin, margin, margin);
	}

	@Override
	public void update(long time) {
		entities.applyChanges();
		for (Entity e : entities) {
			// 如果实体触碰了边界，就让它们消失。
			Position pos = e.get(Position.class);
			Vector3f loc = pos.getLocation();

			boolean changed = false;
			if (loc.x < min.x) {
				changed = true;
			} else if (loc.x > max.x) {
				changed = true;
			}
			
			if (loc.y < min.y) {
				changed = true;
			} else if (loc.y > max.y) {
				changed = true;
			}

			if (loc.z < min.z) {
				changed = true;
			} else if (loc.z > max.z) {
				changed = true;
			}

			// 超出边界就死啦
			if (changed) {
				e.set(new Decay(0));
			}
		}
	}

	@Override
	public void terminate(Game game) {
		// 释放实体
		entities.release();
		entities = null;
	}

}
