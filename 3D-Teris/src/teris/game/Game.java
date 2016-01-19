package teris.game;

import teris.game.states.InputStates;
import teris.game.states.LogicStates;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class Game extends SimpleApplication {

	// Lights
	private AmbientLight ambient;
	private DirectionalLight sun;
	
	private Node wellNode = new Node("well");
	private Node controlNode = new Node("controll");
	
	@Override
	public void simpleInitApp() {
		rootNode.attachChild(wellNode);
		rootNode.attachChild(controlNode);
		
		stateManager.attach(new LogicStates());
		stateManager.attach(new InputStates());
		
		initCamera();
		initLight();
	}
	
	private void initCamera() {
		cam.setLocation(new Vector3f(12, 25, 12));
		cam.lookAt(new Vector3f(3, 13, 3), cam.getUp());
		this.flyCam.setEnabled(false);
	}
	
	/**
	 * Initialize the light
	 */
	private void initLight() {
		// Ambient light
		ambient = new AmbientLight();
		ambient.setColor(ColorRGBA.White);
		rootNode.addLight(ambient);

		// Sun
		sun = new DirectionalLight();
		sun.setColor(ColorRGBA.White);
		sun.setDirection(new Vector3f(-.5f, -.5f, -.5f).normalizeLocal());
		rootNode.addLight(sun);
	}
	
	public Node getWellNode() {
		return wellNode;
	}
	
	public Node getControlNode() {
		return controlNode;
	}

	public static void main(String[] args) {
		Game app = new Game();
		app.start();
	}

}
