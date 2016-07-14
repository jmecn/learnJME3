package game.core.graphics;

import java.awt.Graphics;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

public class Box implements Shape {

	private float width;
	private float height;

	public Box() {
		this.width = 5 + FastMath.rand.nextInt(20);
		this.height = 5 + FastMath.rand.nextInt(10);
	}

	public Box(float width, float height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void draw(Graphics g, Vector3f loc) {
		int x = (int) (loc.x - width / 2);
		int y = (int) (loc.z - height / 2);
		g.fillRect(x, y, (int) width, (int) height);
	}

}
