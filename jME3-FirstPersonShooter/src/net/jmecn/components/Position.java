package net.jmecn.components;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

public class Position implements EntityComponent {

	private final Vector3f location;
	private final Quaternion rotation;

	public Position(Vector3f location) {
		this.location = location;
		this.rotation = new Quaternion();
	}
	
	public Position(Vector3f location, Quaternion rotation) {
		this.location = location;
		this.rotation = rotation;
	}

	public Vector3f getLocation() {
		return location;
	}
	
	public Quaternion getRotation() {
		return rotation;
	}

	@Override
	public String toString() {
		return "Position [location=" + location + ", rotation=" + rotation
				+ "]";
	}
}