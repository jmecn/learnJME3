package yan.mazegame.states;

import yan.mazegame.Game;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

public class GameAppStates extends AbstractAppState {

	private Game game = null;
	private Node rootNode = new Node("gRootNode");
	private Node guiNode = new Node("gGuiNode");
	
	private Node sky;// 天空
	private Node terrain;// 地形
	private Node npc;// NPC
	private Node player;// 玩家
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		// TODO Auto-generated method stub
		super.initialize(stateManager, app);
		
		// 首次加载
		if (game == null) {
			game = (Game) app;
			game.getRootNode().attachChild(rootNode);
			game.getGuiNode().attachChild(guiNode);
		}
		

	}

	@Override
	public void update(float tpf) {
		// TODO Auto-generated method stub
		super.update(tpf);
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		super.cleanup();
	}
	
	

}
