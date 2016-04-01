package yan.mazegame.states;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.util.SkyFactory;

public class AssetAppStates extends AbstractAppState {

	private AssetManager assetManager;
	
	private Node sky;// 天空
	private Node terrain;// 地形
	private Node npc;// NPC
	private Node player;// 玩家
	
	
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		this.assetManager = app.getAssetManager();
		
		super.initialize(stateManager, app);
	}

	public Spatial getTerrain() {
		Box plain = new Box(100, 100, 2);
		Geometry geom = new Geometry("地面", plain);
		
		return geom;
	}
	
	public Spatial getSky() {
		Spatial sky = null;
		SkyFactory sf = new SkyFactory();
		return sky;
	}
}
