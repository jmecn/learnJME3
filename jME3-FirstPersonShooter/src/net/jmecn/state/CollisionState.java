package net.jmecn.state;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.jmecn.components.Collision;
import net.jmecn.components.Model;
import net.jmecn.components.Movement;
import net.jmecn.components.Player;
import net.jmecn.components.Position;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;

/**
 * 基于BulletAppState的碰撞检测
 * @author yanmaoyuan
 */
public class CollisionState extends BaseAppState {

	private SimpleApplication simpleApp;
	private Camera cam;
	
	private final BulletAppState bulletAppState;
	private RigidBodyControl terrain;
	private CharacterControl player;
	
	private EntityData ed;
	private EntitySet entities;
	private EntitySet movingPlayer;

	private Map<EntityId, RigidBodyControl> objects;
	
	public CollisionState() {
		bulletAppState = new BulletAppState();
		
		objects = new HashMap<EntityId, RigidBodyControl>();
	}
	
	@Override
	public void initialize(Application app) {
		
		this.simpleApp = (SimpleApplication) app;

		ed = this.simpleApp.getStateManager().getState(EntityDataState.class).getEntityData();
        entities = ed.getEntities(Collision.class, Model.class, Position.class);
        
        movingPlayer = ed.getEntities(Player.class, Movement.class);
        
        cam = app.getCamera();
	}

	@Override
	public void update(float tpf) {
		if (entities.applyChanges()) {
            removeCollision(entities.getRemovedEntities());
            addCollision(entities.getAddedEntities());
        }
		
		// 更新物体的位置
		for(Entry<EntityId, RigidBodyControl> entry : objects.entrySet()) {
			EntityId id = entry.getKey();
			RigidBodyControl obj = entry.getValue();
			
			if (obj.isActive()) {
				ed.setComponent(id, new Position(obj.getPhysicsLocation()));
			}
		}
		
		if (player != null) {
			if (movingPlayer.applyChanges()) {
				Entity e = movingPlayer.iterator().next();
				Movement move = e.get(Movement.class);
				player.setWalkDirection(move.getDirection().mult(move.getSpeed()));
			}
			
			cam.setLocation(player.getPhysicsLocation());
		}
	}

	private void removeCollision(Set<Entity> entities) {
        for (Entity e : entities) {
        	RigidBodyControl object = objects.remove(e.getId());
        	bulletAppState.getPhysicsSpace().removeCollisionObject(object);
        }
		
	}
	
	private void addCollision(Set<Entity> entities) {
		for (Entity e : entities) {
			Collision collision = e.get(Collision.class);
			Position position = e.get(Position.class);
			Model model = e.get(Model.class);
			
			String name = model.getName();
			if (name.equals(Model.BOMB)) {
				
				// 定义一个刚体
				RigidBodyControl rigidBody = new RigidBodyControl(collision.getMass());
				objects.put(e.getId(), rigidBody);

				// 添加模型
				Spatial bombModel = simpleApp.getStateManager().getState(ModelState.class).getModel(e.getId());
				bombModel.addControl(rigidBody);

				//  加入到物理空间
				bulletAppState.getPhysicsSpace().add(bombModel);

				// 设置物理参数
				rigidBody.setLinearVelocity(collision.getLinearVelocity());
				rigidBody.setAngularVelocity(collision.getAnglurVelocity());
				rigidBody.setGravity(collision.getGravity());
				rigidBody.setPhysicsLocation(position.getLocation());
			}
			
			if (name.equals(Model.ICEWORLD)) {
				// 地形
				terrain = new RigidBodyControl(0);
				Spatial terrainModel = simpleApp.getStateManager().getState(ModelState.class).getModel(e.getId());
				terrainModel.addControl(terrain);
				bulletAppState.getPhysicsSpace().add(terrainModel);
			}
			
			if (name.equals(Model.OTO)) {
				// 玩家
				CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(10f, 40f, 1);
				player = new CharacterControl(capsuleShape, collision.getMass());
				player.setJumpSpeed(60);
				player.setFallSpeed(60);
				player.setGravity(98);
				player.setPhysicsLocation(position.getLocation());

				bulletAppState.getPhysicsSpace().add(player);
			}
        }
	}
	
	@Override
	public void cleanup(Application app) {
		entities.release();
        entities = null;
        
        movingPlayer.release();
        movingPlayer = null;
	}

	@Override
	public void stateDetached(AppStateManager stateManager) {
		super.stateDetached(stateManager);
		
		stateManager.detach(bulletAppState);
	}

	@Override
	protected void onEnable() {
		getStateManager().attach(bulletAppState);
		entities.applyChanges();
		addCollision(entities);
	}

	@Override
	protected void onDisable() {
		getStateManager().detach(bulletAppState);
		removeCollision(entities);
	}

}