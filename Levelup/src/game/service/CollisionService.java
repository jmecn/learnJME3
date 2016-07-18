package game.service;

import game.components.CollisionShape;
import game.components.Damage;
import game.components.Level;
import game.components.Model;
import game.components.Position;
import game.core.Game;
import game.core.Service;

import java.util.Set;

import com.jme3.util.SafeArrayList;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;

/**
 * 碰撞检测
 * 
 * @author yanmaoyuan
 * 
 */
public class CollisionService implements Service {

	private Game game;
	private EntityData ed;
	private EntitySet entities;
	private SafeArrayList<Entity> colliders = new SafeArrayList<Entity>(Entity.class);

	@Override
	public void initialize(Game game) {
		this.game = game;
		ed = game.getEntityData();
		entities = ed.getEntities(Model.class, Position.class, CollisionShape.class);

	}

	@Override
	public void update(long time) {
		if (entities.applyChanges()) {
			removeColliders(entities.getRemovedEntities());
			addColliders(entities.getAddedEntities());
		}

		Entity[] array = colliders.getArray();
		for (int i = 0; i < array.length; i++) {
			Entity e1 = array[i];
			for (int j = i + 1; j < array.length; j++) {
				Entity e2 = array[j];
				generateContacts(e1, e2);
			}
		}
	}

	@Override
	public void terminate(Game game) {
		entities.release();
		entities = null;
	}

	protected void addColliders(Set<Entity> set) {
		colliders.addAll(set);
	}

	protected void removeColliders(Set<Entity> set) {
		colliders.removeAll(set);
	}

	protected void generateContacts(Entity e1, Entity e2) {
		// 同一实体不检测
		if (e1 == e2)
			return;
		
		Model m1 = e1.get(Model.class);
		Model m2 = e2.get(Model.class);
		
		// e1是坏人 e2是子弹
		if (Model.BAD.equals(m1.getName()) && Model.BULLET.equals(m2.getName())) {
			Position p1 = e1.get(Position.class);
			Position p2 = e2.get(Position.class);
	
			CollisionShape s1 = e1.get(CollisionShape.class);
			float r1 = s1.getRadius();
			CollisionShape s2 = e2.get(CollisionShape.class);
			float r2 = s2.getRadius();
	
			float threshold = r1 + r2;
			threshold *= threshold;
	
			float distSq = p1.getLocation().distanceSquared(p2.getLocation());
			if (distSq > threshold) {
				return; // 没有发生碰撞
			}

			// TODO 根据玩家的攻击力给予伤害
			EntityId player = game.getService(SinglePlayerService.class).getPlayer();
			Level lv = ed.getComponent(player, Level.class);
			int lvl = lv.getLv();
			e1.set(new Damage(lvl*2, player));
			
			// 移除这个子弹
			ed.removeEntity(e2.getId());
		}
	}

	protected void generateContacts() {

		Entity[] array = colliders.getArray();
		for (int i = 0; i < array.length; i++) {
			Entity e1 = array[i];
			for (int j = i + 1; j < array.length; j++) {
				Entity e2 = array[j];
				generateContacts(e1, e2);
			}
		}
	}
}
