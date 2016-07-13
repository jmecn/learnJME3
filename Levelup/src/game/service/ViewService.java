package game.service;

import game.components.Model;
import game.components.Position;
import game.core.Game;
import game.core.Service;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

/**
 * 显示服务。 继承Canvas类，用于绘制UI。
 * 
 * @author yanmaoyuan
 * 
 */
public class ViewService extends Canvas implements Service {

	private static final long serialVersionUID = 1L;
	
	private Logger log = LoggerFactory.getLogger(ViewService.class);

	public final static int WIDTH = 800;
	public final static int HEIGHT = 600;

	private Image mBuffer;// 缓冲区
	private Graphics gBuffer;

	private EntityData ed;
	private EntitySet entities;

	public ViewService() {
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
	}

	@Override
	public void initialize(Game game) {
		ed = game.getEntityData();
		entities = ed.getEntities(Model.class, Position.class);
	}

	@Override
	public void update(long time) {
		// 初始化缓冲区
		if (mBuffer == null) {
			mBuffer = createImage(WIDTH, HEIGHT);
			gBuffer = mBuffer.getGraphics();
			// 清屏
			gBuffer.setColor(Color.white);
			gBuffer.fillRect(0, 0, WIDTH, HEIGHT);
		}
		
		// 更新data
		if (entities.applyChanges()) {
			// 清屏
			gBuffer.setColor(Color.white);
			gBuffer.fillRect(0, 0, WIDTH, HEIGHT);
			
			for (Entity e : entities) {
				Position p = e.get(Position.class);
				Vector3f loc = p.getLocation();
				Model model = e.get(Model.class);

				gBuffer.setColor(model.getColor());
				if (Model.PLAYER.equals(model.getName())) {
					gBuffer.fillOval((int) loc.x, (int) loc.z, 10, 10);
				} else if (Model.BAD.equals(model.getName())) {
					gBuffer.fillRect((int) loc.x, (int) loc.z, 10, 10);
				} else if (Model.TARGET.equals(model.getName())) {
					gBuffer.draw3DRect((int) loc.x, (int) loc.z, 10, 10, true);
				}
			}

			// 重绘
			repaint();
		}
	}

	@Override
	public void terminate(Game game) {
		entities.release();
		entities = null;
	}

	/**
	 * 重载Canvas中的paint方法，将缓冲区的图像绘制到屏幕上。
	 */
	@Override
	public void paint(Graphics g) {
		g.drawImage(mBuffer, 0, 0, this);

	}

	/**
	 * 重载Canvas中的update方法，直接调用paint(g)，避免屏幕闪烁。
	 */
	@Override
	public void update(Graphics g) {
		paint(g);
	}

}
