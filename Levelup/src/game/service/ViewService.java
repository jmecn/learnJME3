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

import com.jme3.math.ColorRGBA;
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

	public final static int WIDTH = 1080;
	public final static int HEIGHT = 720;

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
		
		log.info("Canvas准备完毕");
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
			
			paintText();
			
			repaint();
			
		}
		
		// 更新data
		if (entities.applyChanges()) {
			// 清屏
			gBuffer.setColor(Color.white);
			gBuffer.fillRect(0, 0, WIDTH, HEIGHT);
			int size = entities.size();
			
			Entity[] ary = entities.toArray(new Entity[]{});
			for (Entity e : ary) {
				Position p = e.get(Position.class);
				Vector3f loc = p.getLocation();
				Model model = e.get(Model.class);

				ColorRGBA mc = model.getColor();
				Color c = new Color(mc.r, mc.g, mc.b, mc.a);
				gBuffer.setColor(c);
				
				model.getShape().draw(gBuffer, loc);
			}

			// 重绘
			paintText();
			
			repaint();
		}
	}

	@Override
	public void terminate(Game game) {
		entities.release();
		entities = null;
	}

	/**
	 * 绘制文字
	 */
	private void paintText() {
		gBuffer.setColor(Color.black);
		gBuffer.drawString("操作说明:", 0, 13);
		gBuffer.drawString("Z键:创建绿点", 0, 26);
		gBuffer.drawString("X键:创建红点", 0, 39);
		gBuffer.drawString("鼠标左键:在指定位置创建绿点", 0, 52);
		gBuffer.drawString("鼠标右键:在指定位置创建红点", 0, 65);
		gBuffer.drawString("鼠标中键:在指定点创建一个目标，当前屏幕上所有点都将追逐它", 0, 78);
		gBuffer.drawString("ESC:退出程序", 0, 91);
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
