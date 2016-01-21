package teris.game;

import teris.game.control.MoveControl;
import teris.game.control.RotateControl;
import teris.game.states.InputStates;
import teris.game.states.LogicStates;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.debug.Grid;

public class Game extends SimpleApplication {

	// Lights
	private AmbientLight ambient;
	private DirectionalLight sun;
	// Axis
	private Spatial axisNode;
	
	private Node wellNode = new Node("well");
	private Node controlNode = new Node("controll");
	
	@Override
	public void simpleInitApp() {
		wellNode = new Node("well");
		wellNode.addControl(new RotateControl(FastMath.QUARTER_PI));
		
		controlNode = new Node("controll");
		controlNode.addControl(new RotateControl());
		controlNode.addControl(new MoveControl());
		
		rootNode.attachChild(wellNode);
		wellNode.attachChild(controlNode);
		
		stateManager.attach(new LogicStates());
		stateManager.attach(new InputStates());
		
		initGui();
		initCamera();
		initLight();
		initKeys();
		initViewPort();
		axisNode = showNodeAxies(50f);
	}
	
	@Override
	public void simpleUpdate(float tpf) {

	}
	
	private void initGui() {
		String txtB = "KeyPress:\n[J] [K]: Load model.\n[U] [I]: Rotate model.\n[O] [P]: Scale model.\n[F1]: turn on/off wireframe.\n[F2]: turn on/off axis.";
		BitmapText txt;
		BitmapFont fnt = assetManager.loadFont("Interface/Fonts/Default.fnt");
		txt = new BitmapText(fnt, false);
		txt.setBox(new Rectangle(0, 0, settings.getWidth(), settings.getHeight()));
		txt.setText(txtB);
		txt.setLocalTranslation(0, txt.getHeight(), 0);
		guiNode.attachChild(txt);

	}

	private void initCamera() {
		cam.setLocation(new Vector3f(5, 25, 5));
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
		wellNode.addLight(sun);
	}
	
	private void initViewPort() {
		viewPort.setBackgroundColor(new ColorRGBA(0.3f, 0.4f, 0.5f, 1));
	}

	public Spatial showNodeAxies(float axisLen) {
		Node rootNode = new Node("AxisNode");
		Geometry grid = new Geometry("Axis_b", new Grid(7, 7, 1f));
		Material gm = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		gm.setColor("Color", ColorRGBA.White);
		gm.getAdditionalRenderState().setWireframe(true);
		grid.setMaterial(gm);
		grid.center().move(0, -0.5f, 0);

		rootNode.attachChild(grid);
		
		//
		Vector3f v = new Vector3f(axisLen, 0, 0);
		Arrow a = new Arrow(v);
		Material mat = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Red);
		Geometry geom = new Geometry(rootNode.getName() + "XAxis", a);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);

		//
		v = new Vector3f(0, axisLen, 0);
		a = new Arrow(v);
		mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Green);
		geom = new Geometry(rootNode.getName() + "YAxis", a);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);

		//
		v = new Vector3f(0, 0, axisLen);
		a = new Arrow(v);
		mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom = new Geometry(rootNode.getName() + "ZAxis", a);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);

		return rootNode;
	}
	
	private void initKeys() {
		inputManager.addMapping("showAxis", new KeyTrigger(KeyInput.KEY_F2));
		inputManager.addListener(new ActionListener() {
			@Override
			public void onAction(String name, boolean isPressed, float tpf) {
				if (isPressed) {
					switch (name) {
					case "showAxis":
						if (wellNode.hasChild(axisNode)) {
							wellNode.detachChild(axisNode);
						} else {
							wellNode.attachChild(axisNode);
						}
						break;
					}
				}

			}
		}, "showAxis");
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
