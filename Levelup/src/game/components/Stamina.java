package game.components;

import com.simsilica.es.EntityComponent;

/**
 * 耐力值
 * @author yanmaoyuan
 *
 */
public class Stamina implements EntityComponent {

	float cur;// 当前值
	float max;// 最大值
	
	public Stamina(float cur, final float max) {
		if (cur > max) { cur = max;}
		
		this.cur = cur;
		this.max = max;
		
	}
	
	public float getCurrentHp() {
		return cur;
	}
	
	public float getMaxHp() {
		return max;
	}
	
	public float getPercent() {
		return cur / max;
	}
	
	public String toString() {
		return "SP[" + cur + "/" + max + "]";
	}
}
