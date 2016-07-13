package game.core;

import game.components.Decay;
import game.components.Model;
import game.components.Position;
import game.components.Velocity;

import java.awt.Color;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;

/**
 * 实体工厂，用来创建各种类型的实体
 * @author yanmaoyuan
 *
 */
public class EntityFactory {
	private Logger log = LoggerFactory.getLogger(EntityFactory.class);
	
	private EntityData ed;
	
	public EntityFactory(EntityData ed) {
		this.ed = ed;
	}
	
	public void createPlayer(int x, int z) {
		EntityId player = ed.createEntity();
		ed.setComponents(player,
				new Model(Model.PLAYER, Color.GREEN),
				new Position(new Vector3f(x, 0, z), null),
				new Velocity(randomDirection().mult(60)));
		
		log.info("创建玩家实体:" + x + ", " + z);
	}

	public void createBad(int x, int z) {
		EntityId player = ed.createEntity();
		ed.setComponents(player,
				new Model(Model.BAD, Color.RED),
				new Position(new Vector3f(x, 0, z), null),
				new Velocity(randomDirection().mult(60)));
		
		log.info("创建坏人实体:" + x + ", " + z);
	}
	
	public void createTarget(int x, int z) {
		
		EntityId target = ed.createEntity();
		ed.setComponents(target,
				new Model(Model.TARGET, Color.BLUE),
				new Position(new Vector3f(x, 0, z), null),
				new Decay(12000));// 目标点在屏幕上出现12秒，然后消失。
		
		log.info("创建一个目标实体:" + x + ", " + z);
	}
	
	/**
	 * 获得一个随机方向的初速度
	 * @return
	 */
	private Vector3f randomDirection() {
		float theta = FastMath.rand.nextFloat() * FastMath.TWO_PI;
		float x = FastMath.sin(theta);
		float z = FastMath.cos(theta);
		Vector3f dir = new Vector3f(x, 0, z);
		dir.normalizeLocal();
		return dir;
	}

}
