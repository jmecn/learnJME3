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

	private final static int SIDE_X = 6;
	private final static int SIDE_Y = 16;
	private final static int SIDE_Z = 6;
	
	private Game game;
	private AssetManager assetManager;
	
	private int[][][] matrix = new int[SIDE_Y][SIDE_Z][SIDE_X];
	
	private boolean matrixChanged = false;
	
	String[] color = {"red", "blue", "orange", "cyan", "green", "purple", "yellow"};
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		game = (Game) app;
		assetManager = game.getAssetManager();
		
		Random rand = new Random();
		for(int y=0; y<SIDE_Y; y++) {
			for(int x=0; x<SIDE_X; x++) {
				for(int z=0; z<SIDE_Z; z++) {
					if (rand.nextInt(30) == 1)
						matrix[y][z][x] = rand.nextInt(4)-1;
				}
			}
		}
		matrixChanged = true;
		
		Node controlNode = game.getControlNode();
		BoxGeometry _0 = new BoxGeometry(assetManager, "yellow");
		BoxGeometry _1 = new BoxGeometry(assetManager, "yellow");
		BoxGeometry _2 = new BoxGeometry(assetManager, "yellow");
		BoxGeometry _3 = new BoxGeometry(assetManager, "yellow");
		controlNode.attachChild(_0);
		controlNode.attachChild(_1);
		controlNode.attachChild(_2);
		controlNode.attachChild(_3);
		_0.move(0, 0, 0);
		_1.move(1, 0, 0);
		_2.move(-1, 0, 0);
		_3.move(0, 0, 1);
		
		controlNode.move(0.5f, 13f, 0.5f);
		
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
							Geometry box = new BoxGeometry(assetManager, color[index-1]);
							box.setLocalTranslation(x-2.5f, level, y-2.5f);
							wellNode.attachChild(box);
						}
					}
				}
			}
		}
	}

}
