package game.core.graphics;

import java.awt.Graphics;

import com.jme3.math.Vector3f;

public class Circle implements Shape{
	float radius;
	
	public Circle(float radius) {
		this.radius = radius;
	}
	
	@Override
	public void draw(Graphics g, Vector3f loc) {
		g.fillOval(
				(int)(loc.x-radius), 
				(int)(loc.z-radius), 
				(int)radius * 2, 
				(int)radius * 2);
	}
}
