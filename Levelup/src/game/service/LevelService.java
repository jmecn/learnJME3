package game.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.components.Exp;
import game.components.Level;
import game.core.Game;
import game.core.Service;

import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

public class LevelService implements Service {

	private Logger log = LoggerFactory.getLogger(LevelService.class);
	
	public final static int expTable[] = {
		100,
		110,
		120,
		130,
		140,
		150,
		160,
		170,
		180,
		190,
		200,
		210,
		220,
		230,
		240,
		250,
	};
	EntityData ed;
	EntitySet entities;
	@Override
	public void initialize(Game game) {
		ed = game.getEntityData();
		entities = ed.getEntities(Exp.class, Level.class);
	}

	@Override
	public void update(long time) {
		if (entities.applyChanges()) {
			for(Entity e : entities) {
				Exp exp = e.get(Exp.class);
				Level lv = e.get(Level.class);
				
				// 已经满级, 不再获得经验值。
				if (lv.getLv() == expTable.length - 1) {
					e.set(new Exp(0, exp.getMax()));
					continue;
				}
				// 升级
				if (exp.getPercent() >= 1f) {
					levelUp(e);
				}
			}
		}
	}
	
	/**
	 * 升级
	 */
	private void levelUp(Entity e) {
		Exp exp = e.get(Exp.class);
		Level lv = e.get(Level.class);
		
		float cur = exp.getCurrent();
		float max = exp.getMax();
		int lvl = lv.getLv();
		
		while(cur >= max) {
			cur -= max;
			// 下一级经验值
			max = expTable[lvl];
			lvl++;
			
			log.info("玩家升到了" + lvl + "级!");
			
			// 满级了
			if (lvl == expTable.length - 1) {
				lvl --;
				cur = 0;
			}
		}
		
		e.set(new Exp(cur, max));
		e.set(new Level(lvl));
		
	}
	

	@Override
	public void terminate(Game game) {
		entities.release();
		entities = null;
	}

}
