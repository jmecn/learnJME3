package game.components;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * 出生点
 * @author yanmaoyuan
 *
 */
public class BornPoint implements EntityComponent {

	Vector3f location;// 出生点
	float maxRadius;// 离开出生点的最远距离
	
	public BornPoint(final Vector3f location, final float maxRadius) {
		this.location = location;
		this.maxRadius = maxRadius;
	}

	public Vector3f getLocation() {
		return location;
	}

	public float getMaxRadius() {
		return maxRadius;
	}
	
	@Override
	public String toString() {
		return "BornPoint[Location=" + location + ", radius=" + maxRadius + "]";
	}
}
