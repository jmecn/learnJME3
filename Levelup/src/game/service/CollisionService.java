package game.service;

import game.components.CollisionShape;
import game.components.Decay;
import game.components.Position;
import game.core.Game;
import game.core.Service;

import java.util.Set;

import com.jme3.util.SafeArrayList;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

/**
 * 碰撞检测
 * 
 * @author yanmaoyuan
 * 
 */
public class CollisionService implements Service {

	private EntityData ed;
	private EntitySet entities;
	private SafeArrayList<Entity> colliders = new SafeArrayList<Entity>(
			Entity.class);

	@Override
	public void initialize(Game game) {
		ed = game.getEntityData();
		entities = ed.getEntities(Position.class, CollisionShape.class);

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
		if (e1 == e2)
			return;

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

		// 看看谁被吃掉了！胜者将吸收败者1/100的半径
		if (s1.getRadius() >= s2.getRadius()) {
			e1.set(new CollisionShape(r1 + r2 / 100));
			e2.set(new Decay(0));
		} else {
			e2.set(new CollisionShape(r2 + r1 / 100));
			e1.set(new Decay(0));
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
