package net.jmecn.components;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

public class Movement implements EntityComponent {

	private Vector3f direction;// 运动方向
	private float speed;// 速度

	public Movement(Vector3f direction, float speed) {
		this.direction = direction;
		this.speed = speed;
	}

	public Vector3f getDirection() {
		return direction;
	}

	public float getSpeed() {
		return speed;
	}

	@Override
	public String toString() {
		return "Movement [direction=" + direction + ", speed=" + speed + "]";
	}

}
