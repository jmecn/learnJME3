package game.service;

import game.components.Decay;
import game.components.Model;
import game.components.Position;
import game.components.Target;
import game.components.Velocity;
import game.core.Game;
import game.core.Service;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;

/**
 * 用户控制服务
 * 
 * @author yanmaoyuan
 * 
 */
public class ControlService implements KeyListener, MouseMotionListener, MouseListener, Service {

	private Logger log = LoggerFactory.getLogger(ControlService.class);
	
	private Game game;
	private EntityData ed;
	private EntitySet entities;
	
	private boolean xPressed = false;
	private boolean zPressed = false;

	private boolean lPressed = false;// 鼠标左键
	private boolean rPressed = false;// 鼠标右键
	
	@Override
	public void initialize(Game game) {
		this.game = game;
		ed = game.getEntityData();
		entities = ed.getEntities(Model.class, Position.class);

		ViewService view = game.getService(ViewService.class);
		if (view != null) {
			view.addKeyListener(this);
			view.addMouseListener(this);
			view.addMouseMotionListener(this);
		}
	}

	@Override
	public void update(long time) {
		if (zPressed) {
			createPlayer(360, 360);
		}
		if (xPressed) {
			createBad(720, 360);
		}
	}

	@Override
	public void terminate(Game game) {
		ViewService view = game.getService(ViewService.class);
		if (view != null) {
			view.removeKeyListener(this);
			view.removeMouseListener(this);
			view.removeMouseMotionListener(this);
		}
	}

	// 按键监听
	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_X:
			xPressed = true;break;
		case KeyEvent.VK_Z:
			zPressed = true;break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE: 
			game.stop();
			break;
		case KeyEvent.VK_X:
			xPressed = false;
			break;
		case KeyEvent.VK_Z:
			zPressed = false;
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	// 鼠标点击事件监听
	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		switch (e.getButton()) {
		case MouseEvent.BUTTON1: {// 左键
			createPlayer(e.getX(), e.getY());
			lPressed = true;
			break;
		}
		case MouseEvent.BUTTON2: {// 中键
			createTarget(e.getX(), e.getY());
			break;
		}
		case MouseEvent.BUTTON3: {// 右键
			createBad(e.getX(), e.getY());
			rPressed = true;
			break;
		}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		switch (e.getButton()) {
		case MouseEvent.BUTTON1: {// 左键
			lPressed = false;
			break;
		}
		case MouseEvent.BUTTON2: {// 中键
			break;
		}
		case MouseEvent.BUTTON3: {// 右键
			rPressed = false;
			break;
		}
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (lPressed) {
			createPlayer(e.getX(), e.getY());
		}
		if (rPressed) {
			createBad(e.getX(), e.getY());
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {}

	private void createPlayer(int x, int z) {
		EntityId player = ed.createEntity();
		ed.setComponents(player,
				new Model(Model.PLAYER, Color.GREEN),
				new Position(new Vector3f(x, 0, z), null),
				new Velocity(randomDirection().mult(60)));
		
		log.info("创建玩家实体:" + x + ", " + z);
	}

	private void createBad(int x, int z) {
		EntityId player = ed.createEntity();
		ed.setComponents(player,
				new Model(Model.BAD, Color.RED),
				new Position(new Vector3f(x, 0, z), null),
				new Velocity(randomDirection().mult(60)));
		
		log.info("创建坏人实体:" + x + ", " + z);
	}
	
	private void createTarget(int x, int z) {
		entities.applyChanges();
		for(Entity e : entities) {
			String name = e.get(Model.class).getName();
			if (!Model.TARGET.equals(name)) {
				ed.setComponent(e.getId(),
						new Target(new Vector3f(x, 0, z)));
			}
		}
		
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
