package game.components;

import com.simsilica.es.EntityComponent;

public class Level implements EntityComponent {
	private int lv;
	
	public Level(final int lv) {
		this.lv = lv;
	}

	public int getLv() {
		return lv;
	}

	@Override
	public String toString() {
		return "Level [lv=" + lv + "]";
	}
	
}
