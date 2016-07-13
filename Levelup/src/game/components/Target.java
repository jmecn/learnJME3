package game.components;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

public class Target implements EntityComponent {

	private Vector3f location;
	
	public Target(Vector3f loc) {
		this.location = loc;
	}
	
	public Vector3f getLocation() {
		return location;
	}
}
