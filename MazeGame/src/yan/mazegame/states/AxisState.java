package yan.mazegame.states;

import yan.mazegame.Game;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.debug.Grid;

public class AxisState extends AbstractAppState {
	private Game game;
	private Node rootNode;
	private Node guiNode;

	private AppStateManager stateManager;
	private InputManager inputManager;
	private AssetManager assetManager;
	private Camera cam;
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		
		if (game == null) {
			game = (Game) app;
			rootNode = game.getRootNode();
			guiNode = game.getGuiNode();
		}
		this.stateManager = stateManager;
		inputManager = app.getInputManager();
		assetManager = app.getAssetManager();
		cam = app.getCamera();
		
		initKeys();
		showNodeAxes(50);
	}

	@Override
	public void update(float tpf) {
		super.update(tpf);
	}

	/** Declaring the "Shoot" action and mapping to its triggers. */
	private void initKeys() {
		inputManager.addMapping("Shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); // trigger 2:
		inputManager.addListener(actionListener, "Shoot");
	}

	/** Defining the "Shoot" action: Determine what was hit and how to respond. */
	private ActionListener actionListener = new ActionListener() {

		public void onAction(String name, boolean keyPressed, float tpf) {
			if (name.equals("Shoot") && !keyPressed) {
				// 1. Reset results list.
				CollisionResults results = new CollisionResults();
				// 2. Aim the ray from cam loc to cam direction.
				Ray ray = new Ray(cam.getLocation(), cam.getDirection());
				// 3. Collect intersections between Ray and Shootables in
				// results list.
				rootNode.collideWith(ray, results);
				// 4. Print the results
				if (results.size() > 0) {
					Spatial geom = results.getCollision(0).getGeometry()
							.getParent();
					String hit = geom.getName();
					Vector3f tran = geom.getLocalTranslation();
					Quaternion rot = geom.getLocalRotation();
					System.out.println(hit + " translation:" + tran
							+ ", rotation:" + rot);
				}
			}
		}
	};

	public void showNodeAxes(float axisLen) {
		Mesh mesh = new Grid(31, 31, 1);
		Geometry grid = new Geometry("Axis", mesh);
		Material gm = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		gm.setColor("Color", ColorRGBA.White);
		gm.getAdditionalRenderState().setWireframe(true);
		grid.setMaterial(gm);
		grid.center().move(0, 0, 0);

		rootNode.attachChild(grid);

		//
		Vector3f v = new Vector3f(axisLen, 0, 0);
		Arrow a = new Arrow(v);
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
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
	}
}
