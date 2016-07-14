package game.core.graphics;

import java.awt.Graphics;

import com.jme3.math.Vector3f;

public interface Shape {
	public void draw(Graphics g, Vector3f loc);
}
