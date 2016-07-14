package game.service;

import game.components.Model;
import game.components.Position;
import game.components.Target;
import game.components.Velocity;
import game.core.Game;
import game.core.Service;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;
import com.simsilica.es.Filters;

/**
 * ÓÃ»§¿ØÖÆ·þÎñ
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

	private boolean lPressed = false;// Êó±ê×ó¼ü
	private boolean rPressed = false;// Êó±êÓÒ¼ü
	
	@Override
	public void initialize(Game game) {
		this.game = game;
		ed = game.getEntityData();
		entities = ed.getEntities(Filters.fieldEquals(Model.class, "name", Model.PLAYER),
				Model.class, Position.class);
		
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
			game.getFactory().createBad(720, 360);
		}
		if (xPressed) {
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

	// °´¼ü¼àÌý
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

	// Êó±êµã»÷ÊÂ¼þ¼àÌý
	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		switch (e.getButton()) {
		case MouseEvent.BUTTON1: {// ×ó¼ü
			game.getFactory().createSpawnPoint(e.getX(), e.getY());
			lPressed = true;
			break;
		}
		case MouseEvent.BUTTON2: {// ÖÐ¼ü
			break;
		}
		case MouseEvent.BUTTON3: {// ÓÒ¼ü
			setTarget(e.getX(), e.getY());
			game.getFactory().createTarget(e.getX(), e.getY());
			rPressed = true;
			break;
		}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		switch (e.getButton()) {
		case MouseEvent.BUTTON1: {// ×ó¼ü
			lPressed = false;
			break;
		}
		case MouseEvent.BUTTON2: {// ÖÐ¼ü
			break;
		}
		case MouseEvent.BUTTON3: {// ÓÒ¼ü
			rPressed = false;
			entities.applyChanges();
			for(Entity player : entities) {
				ed.removeComponent(player.getId(), Velocity.class);
			}
			break;
		}
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (lPressed) {
			game.getFactory().createSpawnPoint(e.getX(), e.getY());
		}
		if (rPressed) {
			setTarget(e.getX(), e.getY());
			game.getFactory().createTarget(e.getX(), e.getY());
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	public void setTarget(int x, int z) {
		entities.applyChanges();
		for(Entity e : entities) {
			Vector3f target = new Vector3f(x, 0, z);
			Vector3f loc = e.get(Position.class).getLocation();
			
			Vector3f v = target.subtract(loc);
			v.normalizeLocal().multLocal(100);
			ed.setComponents(e.getId(), new Velocity(v));
		}
	}
}
