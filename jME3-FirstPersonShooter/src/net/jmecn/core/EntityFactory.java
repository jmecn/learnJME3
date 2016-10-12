package net.jmecn.core;

import org.apache.log4j.Logger;

import com.simsilica.es.EntityData;

/**
 * 实体工厂，用来创建各种类型的实体
 * @author yanmaoyuan
 *
 */
public class EntityFactory {
	static Logger log = Logger.getLogger(EntityFactory.class);
	
	private EntityData ed;
	
	public EntityFactory(EntityData ed) {
		this.ed = ed;
	}
	
	public void createPlayer() {
		
	}
	
}
