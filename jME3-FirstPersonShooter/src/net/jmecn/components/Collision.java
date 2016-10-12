package net.jmecn.components;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

public class Collision implements EntityComponent {

	private float mass;

	private Vector3f linearVelocity;// 线速度
	private Vector3f anglurVelocity;// 角速度
	private Vector3f gravity;// 重力加速度

	public Collision() {
		mass = 0;
		linearVelocity = new Vector3f();
		anglurVelocity = new Vector3f();
		gravity = new Vector3f(0, -98, 0);
	}
	
	public Collision(float mass) {
		this.mass = mass;
		linearVelocity = new Vector3f();
		anglurVelocity = new Vector3f();
		gravity = new Vector3f(0, -98, 0);
	}
	
	public Collision(float mass, Vector3f linearVelocity,
			Vector3f anglurVelocity, Vector3f gravity) {
		this.mass = mass;
		this.linearVelocity = linearVelocity;
		this.anglurVelocity = anglurVelocity;
		this.gravity = gravity;
	}

	public float getMass() {
		return mass;
	}

	public Vector3f getLinearVelocity() {
		return linearVelocity;
	}

	public Vector3f getAnglurVelocity() {
		return anglurVelocity;
	}

	public Vector3f getGravity() {
		return gravity;
	}

	@Override
	public String toString() {
		return "Collision [mass=" + mass + ", linearVelocity=" + linearVelocity
				+ ", anglurVelocity=" + anglurVelocity + ", gravity=" + gravity
				+ "]";
	}

}
