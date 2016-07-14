package game.components;

import com.simsilica.es.EntityComponent;

/**
 * Area of interest
 * 实体的兴趣范围
 * @author yanmaoyuan
 *
 */
public class AoI implements EntityComponent {

	private float radius;// 半径
	
	public AoI(float radius) {
		this.radius = radius;
	}
	
	public float getRadius() {
		return radius;
	}
	
	public float getRadiusSquare() {
		return radius * radius;
	}
	
	public String toString() {
		return "AoI["+ radius+ "]";
	}
}
