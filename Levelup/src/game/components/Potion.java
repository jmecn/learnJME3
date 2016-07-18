package game.components;

import com.simsilica.es.EntityComponent;

/**
 * Ò©¼Á
 * @author yanmaoyuan
 *
 */
public class Potion implements EntityComponent {

	float hp;
	
	public Potion(float hp) {
		this.hp = hp;
	}
	public float getHp() {
		return hp;
	}
	@Override
	public String toString() {
		return "Potion [hp=" + hp + "]";
	}
}
