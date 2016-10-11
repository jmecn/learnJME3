package net.jmecn.core;

import java.util.Set;

import net.jmecn.effects.DecayControl;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;

public class ShootState extends BaseAppState {
	// 实体系统
	private EntityData ed;
	private EntitySet entities;
	private Node shootable;
	
	@Override
	protected void initialize(Application app) {
		shootable = getStateManager().getState(VisualAppState.class).getShootable();
		ed = getStateManager().getState(EntityDataState.class).getEntityData();
		entities = ed.getEntities(Shoot.class);
	}
    
	public void update(float tpf) {
		if (entities.applyChanges()) {
			addShoot(entities.getAddedEntities());
		}
	}
	
	private void addShoot(Set<Entity> entities) {
		for(Entity e : entities) {
			Shoot shoot = e.get(Shoot.class);
			ed.removeComponent(e.getId(), Shoot.class);
			
			// 射线检测
			CollisionResults results = new CollisionResults();
			Ray ray = new Ray(shoot.getLocation(), shoot.getDirection());
			shootable.collideWith(ray, results);
			if (results.size() > 0) {
				CollisionResult closest = results.getClosestCollision();

				Quaternion rotation = new Quaternion();
				rotation.lookAt(shoot.getDirection(), Vector3f.UNIT_Y);
				
				// 弹痕标记
				EntityId bulletMark = ed.createEntity();
				ed.setComponents(bulletMark,
						new Position(closest.getContactPoint(), rotation),
						new Model(Model.BULLET));
				
				
				// 利用目标的名字来判断是否是炸弹
				Geometry target = closest.getGeometry();
				String name = target.getName();
				if (name.equals("BombGeom1")) {
					// 瞬间爆炸
					Node parent = target.getParent().getParent().getParent();
					System.out.println(parent);
					DecayControl control = parent.getControl(DecayControl.class);
					control.explosionNow();
				}
			}
		}
	}
	@Override
	protected void cleanup(Application app) {
	}

	@Override
	protected void onEnable() {
	}

	@Override
	protected void onDisable() {
	}

}
