package game.core.graphics;

import java.awt.Graphics;

import com.jme3.math.Vector3f;

/**
 * Ê®×Ö²æ
 * @author yanmaoyuan
 *
 */
public class Cross implements Shape {
	
	@Override
	public void draw(Graphics g, Vector3f loc) {
		int x = (int)loc.x;
		int y = (int)loc.z;

		g.drawLine(x-5, y, x+5, y);
		g.drawLine(x, y-5, x, y+5);
	}

}
