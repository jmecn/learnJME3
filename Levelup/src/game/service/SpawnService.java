package game.service;

import game.components.AoI;
import game.components.Model;
import game.components.Position;
import game.components.SpawnPoint;
import game.components.Velocity;
import game.components.BornPoint;
import game.core.Game;
import game.core.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.es.Filters;

/**
 * 刷怪服务，负责刷新所有的怪物。
 * @author yanmaoyuan
 *
 */
public class SpawnService implements Service {

	private Logger log = LoggerFactory.getLogger(SpawnService.class);

	private Game game;
	private EntityData ed;
	private EntitySet spawnPoints;
	private EntitySet players;
	
	private EntitySet mobs;// 所有的怪物
	private EntitySet childMobs;// 有妈的孩子
	private EntitySet movingMobs;// 运动着的生物

	// 刷怪关系
	private HashMap<EntityId, EntityId> mothers = new HashMap<EntityId, EntityId>();
		
	// 最大刷怪数量
	public final static int MAX_MOBS = 50;

	@Override
	public void initialize(Game game) {
		this.game = game;
		ed = game.getEntityData();
		
		// 刷怪点
		spawnPoints = ed.getEntities(Position.class, SpawnPoint.class, AoI.class);
		
		players = ed.getEntities(
				Filters.fieldEquals(Model.class, "name", Model.PLAYER),
				Position.class, Model.class);

		mobs = ed.getEntities(
				Filters.fieldEquals(Model.class, "name", Model.BAD),
				Model.class);
		
		childMobs = ed.getEntities(BornPoint.class);
		movingMobs = ed.getEntities(BornPoint.class, Position.class, Velocity.class);
	}

	private int time = 0;

	@Override
	public void update(long timePerFrame) {

		if (childMobs.applyChanges()) {
			notifyDead(childMobs.getRemovedEntities());
		}
		
		if (movingMobs.applyChanges()) {
			goHome(movingMobs);
		}
		
		// 系统刷怪有1秒时间间隔
		time += timePerFrame;
		if (time >= 1000000000l) {
			time -= 1000000000;
			respawn();
		}

	}

	/**
	 * 怪物死亡后要通知刷新点
	 * @param entities
	 */
	private void notifyDead(Set<Entity> entities) {
		for (Entity e : entities) {
			EntityId id = mothers.get(e.getId());
			
			if (id != null) {
				mothers.remove(e.getId());
				// 复制一份数据，只更新当前数量。
				SpawnPoint point = ed.getComponent(id, SpawnPoint.class);
				int max = point.getMaximumCount();
				int count = point.getCurrentCount() - 1;
				long start = point.getStartTime();
				long delta = point.getDeltaTime();
				
				ed.setComponent(id, new SpawnPoint(max, count, start, delta));
				log.info("死亡，更新刷新点：" + id);
			}
		}
	}
	
	/**
	 * 刷怪
	 * 
	 */
	private void respawn() {

		mobs.applyChanges();// 更新怪物
		int mobCount = mobs.size();
		if (mobCount >= MAX_MOBS)
			return;

		spawnPoints.applyChanges();// 更新刷怪点
		int spawnCount = spawnPoints.size();
		if (spawnCount <= 0)
			return;
		
		players.applyChanges();// 更新玩家
		int playerCount = players.size();
		if (playerCount <= 0)
			return;

		/**
		 * 下面这个刷怪策略，适用于ARPG游戏，当玩家靠近刷怪点的时候才会刷怪。
		 */
		List<Entity> activeSpawn = getActiveSpawnPoint();
		spawnCount = activeSpawn.size();
		if (spawnCount > 0) {
			// 随机挑一个刷怪点
			int index = FastMath.rand.nextInt(spawnCount);
			Entity e = activeSpawn.get(index);
			spawn(e);
		}
	}
	
	/**
	 * 挑选激活的刷怪点
	 * @return
	 */
	protected List<Entity> getActiveSpawnPoint() {
		/**
		 * 这里将要对2个实体集合进行遍历，算法复杂度O(N*N)。
		 * 使用数组而不使用增强for循环，因为增强for循环遍历时会创建大量的Iterator对象，导致内存暴涨。
		 */
		Entity[] sAry = spawnPoints.toArray(new Entity[] {});
		Entity[] pAry = players.toArray(new Entity[] {});

		// 判断玩家附近是否有刷怪点
		List<Entity> activeSpawn = new ArrayList<Entity>();
		for (int j = 0; j < pAry.length; j++) {
			Entity player = pAry[j];
			
			// 玩家的位置
			Vector3f pLoc = player.get(Position.class).getLocation();

			for (int i = 0; i < sAry.length; i++) {
				Entity spawnPoint = sAry[i];

				// 这个刷新点已经被激活，不用再计算。
				if (activeSpawn.contains(spawnPoint))
					continue;

				// 刷新点的位置
				Vector3f sLoc = spawnPoint.get(Position.class).getLocation();

				// 判断玩家距离刷怪点的距离是否足够近
				float distance = spawnPoint.get(AoI.class).getRadiusSquare();
				if (sLoc.distanceSquared(pLoc) <= distance) {
					// 激活这个刷怪点
					activeSpawn.add(spawnPoint);
				}
			}
		}
		
		return activeSpawn;
	}
	
	/**
	 * 刷新一个怪物
	 * @param e
	 */
	private void spawn(Entity e) {
		SpawnPoint point = e.get(SpawnPoint.class);
		
		// 怪物数量不足
		if (!point.isFull() && point.getPercent() > 1.0) {

			// 怪物的出生点
			Vector3f loc = e.get(Position.class).getLocation();
			float area = e.get(AoI.class).getRadius();// 怪物的游荡半径
			
			// 出生位置在附近
			float r = FastMath.rand.nextFloat() * area;
			float t = FastMath.rand.nextFloat() * FastMath.TWO_PI;
			float x = FastMath.sin(t) * r;
			float y = FastMath.cos(t) * r;
			
			EntityId id = game.getFactory().createBad(loc.x+x, loc.z+y);
			
			// 记录这个怪物的刷新点，当怪物消失后要通知这个刷怪点。
			ed.setComponent(id, new BornPoint(loc, area));
			
			mothers.put(id, e.getId());

			// 重置刷怪点
			int current = point.getCurrentCount() + 1;
			int max = point.getMaximumCount();
			ed.setComponent(e.getId(), new SpawnPoint(max, current));
		}
	}
	
	/**
	 * 怪物离家太远，就会想要回家。
	 * @param entities
	 */
	private void goHome(Set<Entity> entities) {
		for(Entity e : movingMobs) {
			BornPoint mother = e.get(BornPoint.class);
			float radius = mother.getMaxRadius();
			
			Vector3f loc1 = mother.getLocation();
			Vector3f loc2 = e.get(Position.class).getLocation();
			
			if (loc1.distanceSquared(loc2) >= radius * radius) {
				Velocity v = e.get(Velocity.class);
				if (v != null) {
					Vector3f linear = loc1.subtract(loc2);
					linear.normalizeLocal().multLocal(15);

					ed.setComponent(e.getId(), new Velocity(linear));
				}
			}
		}
	}

	@Override
	public void terminate(Game game) {
		spawnPoints.release();
		spawnPoints = null;
		
		players.release();
		players = null;
		
		mobs.release();
		mobs = null;
		
		childMobs.release();
		childMobs = null;
		
		movingMobs.release();
		movingMobs = null;
	}

}
