package net.jmecn.core;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

public class Shoot implements EntityComponent {

	private Vector3f location;
	private Vector3f direction;

	public Shoot(Vector3f location, Vector3f direction) {
		this.location = new Vector3f(location);
		this.direction = new Vector3f(direction);
	}

	public Vector3f getLocation() {
		return location;
	}

	public Vector3f getDirection() {
		return direction;
	}

	@Override
	public String toString() {
		return "Shoot [location=" + location + ", direction=" + direction + "]";
	}

}
