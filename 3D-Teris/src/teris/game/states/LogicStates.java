package teris.game.states;

import java.util.Random;

import teris.game.Game;
import teris.game.shape.BoxGeometry;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

public class LogicStates extends AbstractAppState {

	private Game game;
	private AssetManager assetManager;
	
	private int[][][] matrix = new int[16][6][6];
	
	private boolean matrixChanged = false;
	
	String[] color = {"red", "blue", "orange", "cyan", "green", "purple", "yellow"};
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		game = (Game) app;
		assetManager = game.getAssetManager();
		
		Random rand = new Random();
		for(int level=0; level<16; level++) {
			for(int x=0; x<6; x++) {
				for(int y=0; y<6; y++) {
					matrix[level][y][x] = rand.nextInt(7);
				}
			}
		}
		matrixChanged = true;
	}

	@Override
	public void update(float tpf) {
		if (matrixChanged) {
			matrixChanged = false;
			Node wellNode = game.getWellNode();
			for(int level=0; level<16; level++) {
				for(int x=0; x<6; x++) {
					for(int y=0; y<6; y++) {
						int index = matrix[level][y][x];
						if (index > 0) {
							Geometry box = new BoxGeometry(assetManager, color[index]);
							box.setLocalTranslation(x-2.5f, level, y-2.5f);
							wellNode.attachChild(box);
						}
					}
				}
			}
		}
	}

}
