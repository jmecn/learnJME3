package net.jmecn.game;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;

/**
 * Test Bullet Physics
 * @author yanmaoyuan
 *
 */
public class GameAppState extends BaseAppState {

	public static void main(String[] args) {
		SimpleApplication app = new SimpleApplication() {
			@Override
			public void simpleInitApp() {
				stateManager.attachAll(
						new ScreenshotAppState("", System.currentTimeMillis()),
						new GameAppState());
			}
		};
		app.start();
	}

	
	private final static String LEFT = "left";// A
	private final static String RIGHT = "right";// D
	private final static String FORWARD = "forward";// W
	private final static String BACKWARD = "backward";// S
	private final static String JUMP = "jump";// Space
	private final static String TRIGGER = "trigger";// Mouse_left
	private final static String BULLET_DEBUG = "bullet_debug";// F4

	private SimpleApplication simpleApp;
	private Camera cam;

	// Scene
	private Node rootNode;

	// Physics
	private boolean debugEnabled = true;
	private BulletAppState bulletAppState;
	private RigidBodyControl terrain;
	private CharacterControl player;

	// Walking parameters
	private boolean left = false, right = false, forward = false, backward = false, trigger = false;
	
	private Vector3f camLoc;
	private Vector3f camDir;
	private Vector3f camLeft;
	private Vector3f walkDirection;
	private float moveSpeed;
	
	public GameAppState() {
		rootNode = new Node("GameRoot");
		
		bulletAppState = new BulletAppState();
		bulletAppState.setDebugEnabled(debugEnabled);
		
		camLoc = new Vector3f();
		camDir = new Vector3f();
		camLeft = new Vector3f();
		walkDirection = new Vector3f();
		moveSpeed = 2f;
	}
	
	@Override
	protected void initialize(Application app) {
		
		this.simpleApp = (SimpleApplication) app;
		this.cam = app.getCamera();
		
		/**
		 * Load models
		 */
		Spatial model = app.getAssetManager().loadModel("Models/Terrain/iceworld.j3o");
		rootNode.attachChild(model);

		/**
		 * Initialize Bullet Physics
		 */
		// Terrain
		this.terrain = new RigidBodyControl(0);
		model.addControl(terrain);

		// Player
		CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(10f, 40f, 1);
		player = new CharacterControl(capsuleShape, 50f);
		player.setJumpSpeed(60);
		player.setFallSpeed(60);
		player.setGravity(98f);
		
		
		/**
		 * Initialize input
		 */
		InputManager inputManager = app.getInputManager();
		inputManager.addMapping(LEFT, new KeyTrigger(KeyInput.KEY_A));
		inputManager.addMapping(RIGHT, new KeyTrigger(KeyInput.KEY_D));
		inputManager.addMapping(FORWARD, new KeyTrigger(KeyInput.KEY_W));
		inputManager.addMapping(BACKWARD, new KeyTrigger(KeyInput.KEY_S));
		inputManager.addMapping(JUMP, new KeyTrigger(KeyInput.KEY_SPACE));
		inputManager.addMapping(BULLET_DEBUG, new KeyTrigger(KeyInput.KEY_F4));
		inputManager.addMapping(TRIGGER, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
	}

	/**
	 * Create a cylinder
	 * @param color
	 * @return
	 */
	public Geometry createCylinder(ColorRGBA color) {
		Geometry spatial = new Geometry("Cylinder", new Cylinder(2, 6, 1, 4, true));
		
		Material mat = new Material(simpleApp.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", color);
		
		spatial.setMaterial(mat);

		return spatial;
	}

	private ActionListener myListener = new ActionListener() {
		@Override
		public void onAction(String name, boolean isPressed, float tpf) {
			if (name.equals(LEFT)) {
				left = isPressed;
			} else if (name.equals(RIGHT)) {
				right = isPressed;
			} else if (name.equals(FORWARD)) {
				forward = isPressed;
			} else if (name.equals(BACKWARD)) {
				backward = isPressed;
			} else if (name.equals(JUMP)) {
				player.jump();
			} else if (name.equals(TRIGGER)) {
				trigger = isPressed;
			} else if (name.equals(BULLET_DEBUG) && isPressed) {
				debugEnabled = !debugEnabled;
				bulletAppState.setDebugEnabled(debugEnabled);
			}
		}
	};

	public final static float COOLDOWN_TIME = 0.2f;
	private float time = 0f;

	@Override
	public void update(float tpf) {
		/**
		 * Walking
		 */
		camDir.set(cam.getDirection()).multLocal(0.6f);
		camLeft.set(cam.getLeft()).multLocal(0.4f);
		walkDirection.set(0, 0, 0);
		if (left) {
			walkDirection.addLocal(camLeft);
		}
		if (right) {
			walkDirection.addLocal(camLeft.negate());
		}
		if (forward) {
			walkDirection.addLocal(camDir);
		}
		if (backward) {
			walkDirection.addLocal(camDir.negate());
		}
		walkDirection.y = 0;
		walkDirection.normalizeLocal().multLocal(moveSpeed);
		player.setWalkDirection(walkDirection);
		cam.setLocation(player.getPhysicsLocation());

		/**
		 * Left click
		 */
		time += tpf;
		if (trigger) {
			if (time >= COOLDOWN_TIME) {
				time = 0f;
				
				camLoc.set(cam.getLocation());
				camLoc.addLocal(cam.getDirection().mult(20));

				Spatial bomb = createCylinder(ColorRGBA.Red);
				rootNode.attachChild(bomb);
				bomb.setLocalTranslation(camLoc);
				
				RigidBodyControl control = new RigidBodyControl(0.5f);
				bomb.addControl(control);
				bulletAppState.getPhysicsSpace().add(control);
				
				control.setLinearVelocity(cam.getDirection().mult(100).add(walkDirection));
				control.setGravity(new Vector3f(0, -98f, 0));
			}
		}
	}

	@Override
	protected void cleanup(Application app) {
	}

	@Override
	protected void onEnable() {
		simpleApp.getRootNode().attachChild(rootNode);
		
		getStateManager().attach(bulletAppState);
		bulletAppState.getPhysicsSpace().add(terrain);
		bulletAppState.getPhysicsSpace().add(player);
		
		simpleApp.getInputManager().addListener(myListener,
				LEFT, RIGHT, FORWARD, BACKWARD,
				JUMP, TRIGGER, BULLET_DEBUG);
	}

	@Override
	protected void onDisable() {
		rootNode.removeFromParent();

		bulletAppState.getPhysicsSpace().remove(terrain);
		bulletAppState.getPhysicsSpace().remove(player);
		getStateManager().detach(bulletAppState);
		
		simpleApp.getInputManager().removeListener(myListener);
	}

}
