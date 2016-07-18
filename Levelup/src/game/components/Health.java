package game.components;

import com.simsilica.es.EntityComponent;

/**
 * 生命值
 * @author yanmaoyuan
 *
 */
public class Health implements EntityComponent {

	float cur;// 当前生命值
	float max;// 最大生命值
	
	public Health(float cur, final float max) {
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
		return "HP[" + cur + "/" + max + "]";
	}
}
