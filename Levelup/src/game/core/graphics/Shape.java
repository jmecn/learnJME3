package game.core.graphics;

import java.awt.Graphics;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

public interface Shape {
	public void draw(Graphics g, Vector3f loc);
}
