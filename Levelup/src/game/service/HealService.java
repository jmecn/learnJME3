package game.service;

import game.components.Health;
import game.components.Mana;
import game.components.Potion;
import game.core.Game;
import game.core.Service;

import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

public class HealService implements Service {

	private EntityData ed;
	private EntitySet hps;
	
	@Override
	public void initialize(Game game) {
		ed = game.getEntityData();
		hps = ed.getEntities(Health.class, Potion.class);
	}

	@Override
	public void update(long time) {
		if (hps.applyChanges()) {
			for(Entity e : hps) {
				float hpDelta = e.get(Potion.class).getHp();
				Health hp = e.get(Health.class);
				float cur = hp.getCurrentHp();
				float max = hp.getMaxHp();
				
				if (hpDelta != 0f) {
					cur += hpDelta;
					if (cur < 0) cur = 0;
					if (cur > max) cur = max;
					e.set(new Health(cur, max));
				}
				
				ed.removeComponent(e.getId(), Potion.class);
			}
		}
		
	}

	@Override
	public void terminate(Game game) {
		// TODO Auto-generated method stub
		
	}
}
