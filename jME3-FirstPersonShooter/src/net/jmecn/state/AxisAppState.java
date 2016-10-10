package net.jmecn.state;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.debug.Grid;

/**
 * 参考坐标系
 * 
 * @author yanmaoyuan
 * 
 */
public class AxisAppState extends AbstractAppState {
	public final static String TOGGLE_AXIS = "toggle_axis";
	private SimpleApplication game;
	private Node rootNode;

	private InputManager inputManager;
	private AssetManager assetManager;

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);

		if (game == null) {
			game = (SimpleApplication) app;
			rootNode = new Node("AxisRootNode");
			game.getRootNode().attachChild(rootNode);
		}
		inputManager = app.getInputManager();
		assetManager = app.getAssetManager();

		initKeys();
		showNodeAxes(200);
		
		toggleAxis();// 默认关闭坐标系
	}

	@Override
	public void cleanup() {
		super.cleanup();

		game.getRootNode().detachChild(rootNode);
		inputManager.removeListener(actionListener);
		inputManager.deleteMapping(TOGGLE_AXIS);
	}

	private void initKeys() {
		inputManager.addMapping(TOGGLE_AXIS, new KeyTrigger(KeyInput.KEY_F4));
		inputManager.addListener(actionListener, TOGGLE_AXIS);
	}

	private ActionListener actionListener = new ActionListener() {

		public void onAction(String name, boolean keyPressed, float tpf) {
			if (name.equals(TOGGLE_AXIS) && keyPressed) {
				toggleAxis();
			}
		}
	};

	public boolean toggleAxis() {
		if (game.getRootNode().hasChild(rootNode)) {
			game.getRootNode().detachChild(rootNode);
			return false;
		} else {
			game.getRootNode().attachChild(rootNode);
			return true;
		}
	}

	private void showNodeAxes(float axisLen) {
		int l = (int) (axisLen * 2) + 1;
		Mesh mesh = new Grid(l, l, 10);
		Geometry grid = new Geometry("Axis", mesh);
		Material gm = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		gm.setColor("Color", ColorRGBA.White);
		gm.getAdditionalRenderState().setWireframe(true);
		grid.setMaterial(gm);
		grid.center().move(0, 0, 0);

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
	}
}