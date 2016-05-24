package testpts;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.pts.PTSLoader;
import com.jme3.scene.Spatial;

public class TestPtsLoader extends SimpleApplication {
	
	public static void main(String[] args) {
		TestPtsLoader app = new TestPtsLoader();
		app.start();
	}

	@Override
	public void simpleInitApp() {
		assetManager.registerLoader(PTSLoader.class, "pts");
		
		Spatial model = assetManager.loadModel("Models/TQ02.pts");
		model.center().move(0, 0, 0);
		
		rootNode.attachChild(model);
	}

}
