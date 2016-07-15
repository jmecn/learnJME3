package game.components;

import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;

/**
 * 伤害值
 * @author yanmaoyuan
 *
 */
public class Damage implements EntityComponent {

	EntityId dealer;// 伤害制造者
	int delta;// 伤害值
	
	public Damage(int delta, EntityId dealer) {
		this.dealer = dealer;
		this.delta = delta;
	}
	
	public String toString() {
		return "Damage[value=" +delta+ ", dealer=" + dealer.getId() + "]";
	}
}
