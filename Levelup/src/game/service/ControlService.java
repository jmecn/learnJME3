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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class ControlService implements KeyListener, MouseListener, Service {

	private Logger log = LoggerFactory.getLogger(ControlService.class);
	
	private Game game;
	private EntityData ed;
	private EntitySet entities;

	@Override
	public void initialize(Game game) {
		this.game = game;
		ed = game.getEntityData();
		entities = ed.getEntities(Model.class, Position.class);

		ViewService view = game.getService(ViewService.class);
		if (view != null) {
			view.addKeyListener(this);
			view.addMouseListener(this);
		}
	}

	@Override
	public void update(long time) {
	}

	@Override
	public void terminate(Game game) {
		ViewService view = game.getService(ViewService.class);
		if (view != null) {
			view.removeKeyListener(this);
			view.removeMouseListener(this);
		}
	}

	// 按键监听
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE: {
			game.stop();
			break;
		}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	// 鼠标点击事件监听
	@Override
	public void mouseClicked(MouseEvent e) {
		switch (e.getButton()) {
		case MouseEvent.BUTTON1: {// 左键
			createPlayer(e.getX(), e.getY());
			break;
		}
		case MouseEvent.BUTTON2: {// 中键
			createTarget(e.getX(), e.getY());
			break;
		}
		case MouseEvent.BUTTON3: {// 右键
			createBad(e.getX(), e.getY());
			break;
		}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	private void createPlayer(int x, int z) {
		EntityId player = ed.createEntity();
		ed.setComponents(player,
				new Model(Model.PLAYER, Color.GREEN),
				new Position(new Vector3f(x, 0, z), null),
				new Velocity(new Vector3f(-1, 0, 0)));
		
		log.info("Create player at:" + x + ", " + z);
	}

	private void createBad(int x, int z) {
		EntityId player = ed.createEntity();
		ed.setComponents(player,
				new Model(Model.BAD, Color.RED),
				new Position(new Vector3f(x, 0, z), null),
				new Velocity(new Vector3f(1, 0, 0)));
		
		log.info("Create bad guy at:" + x + ", " + z);
	}
	
	private void createTarget(int x, int z) {
		entities.applyChanges();
		for(Entity e : entities) {
			ed.setComponent(e.getId(), new Target(new Vector3f(x, 0, z)));
		}
		
		EntityId target = ed.createEntity();
		ed.setComponents(target,
				new Model(Model.TARGET, Color.BLUE),
				new Position(new Vector3f(x, 0, z), null),
				new Decay(10000));
		
		log.info("Create target at:" + x + ", " + z);
	}
	
}
