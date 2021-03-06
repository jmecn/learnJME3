package net.jmecn.state;


import net.jmecn.app.PlayerFunctions;
import net.jmecn.components.Collision;
import net.jmecn.components.Decay;
import net.jmecn.components.Jump;
import net.jmecn.components.Model;
import net.jmecn.components.Movement;
import net.jmecn.components.Position;
import net.jmecn.components.Shoot;
import net.jmecn.components.Shootable;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.input.CameraInput;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.input.InputMapper;

public class InputState extends BaseAppState {

	private final static String LEFT = "left";
	private final static String RIGHT = "right";
	private final static String FORWARD = "forward";
	private final static String BACKWARD = "backward";
	private final static String JUMP = "jump";
	private final static String TRIGGER = "trigger";// 左键扣动扳机
	private final static String BOMB = "bomb";// 右键扔手雷
	private final static String ESC = "esc";// 退出游戏

	private SimpleApplication simpleApp;
	private AudioNode ak47;
	private Camera cam;
	private EntityData ed;
	private EntityId player;
	
	// 运动逻辑
	private boolean left = false, right = false, forward = false,
			backward = false, trigger = false, bomb = false;
	private Vector3f camLoc = new Vector3f();
	private Vector3f camDir = new Vector3f();
	private Vector3f camLeft = new Vector3f();
	private Vector3f walkDirection = new Vector3f();
	private float moveSpeed = 2f;

	private float gunTime = 0f;
	public final static float GUN_COOLDOWN_TIME = 0.18f;// 枪管冷却时间
	private float bombTime = 0f;
	public final static float BOMB_COOLDOWN_TIME = 1f;// 炸弹冷却时间
	
	@Override
	protected void initialize(Application app) {

		this.simpleApp = (SimpleApplication) app;
		this.cam = app.getCamera();

		ed = getStateManager().getState(EntityDataState.class).getEntityData();

		ak47 = new AudioNode(app.getAssetManager(), "Sound/weapons/ak47-1.wav", DataType.Buffer);
		ak47.setPositional(false);
		ak47.setLooping(false);
		ak47.setVolume(2);
		
		
		// TODO use lemur input insteadof jme input
		InputMapper inputMapper = GuiGlobals.getInstance().getInputMapper();
		inputMapper.addDelegate(PlayerFunctions.F_EXIT, this, "quitGame");

		InputManager inputManager = app.getInputManager();

		inputManager.deleteMapping(CameraInput.FLYCAM_FORWARD);
		inputManager.deleteMapping(CameraInput.FLYCAM_BACKWARD);
		inputManager.deleteMapping(CameraInput.FLYCAM_STRAFELEFT);
		inputManager.deleteMapping(CameraInput.FLYCAM_STRAFERIGHT);
		inputManager.deleteMapping(CameraInput.FLYCAM_RISE);
		inputManager.addMapping(LEFT, new KeyTrigger(KeyInput.KEY_A));
		inputManager.addMapping(RIGHT, new KeyTrigger(KeyInput.KEY_D));
		inputManager.addMapping(FORWARD, new KeyTrigger(KeyInput.KEY_W));
		inputManager.addMapping(BACKWARD, new KeyTrigger(KeyInput.KEY_S));
		inputManager.addMapping(JUMP, new KeyTrigger(KeyInput.KEY_SPACE));
		inputManager.addMapping(TRIGGER, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		inputManager.addMapping(BOMB, new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
		inputManager.addMapping(ESC, new KeyTrigger(KeyInput.KEY_ESCAPE));

		inputManager.addListener(myListener, LEFT, RIGHT, FORWARD, BACKWARD, JUMP, TRIGGER, BOMB, ESC);
	}
	
	/**
	 * 按键事件监听器
	 */
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
			} else if (name.equals(JUMP) && isPressed) {
				ed.setComponent(player, new Jump());
			} else if (name.equals(TRIGGER)) {
				trigger = isPressed;
			} else if (name.equals(BOMB)) {
				bomb = isPressed;
			} else if (name.equals(ESC) && isPressed) {
				// 退出游戏
				quitGame();
			}
		}
	};

	@Override
	public void update(float tpf) {
		// 人物行走
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
		walkDirection.normalizeLocal();
		
		Movement movement = new Movement(walkDirection, moveSpeed);
		ed.setComponent(player, movement);
		
		// 连续开枪
		gunTime += tpf;
		if (trigger) {
			if (gunTime >= GUN_COOLDOWN_TIME) {
				gunTime = 0f;
				
				ak47.playInstance();
				ed.setComponent(player, new Shoot(cam.getLocation(), cam.getDirection()));
			}
		}

		bombTime += tpf;
		if (bomb) {
			if (bombTime >= BOMB_COOLDOWN_TIME) {
				bombTime = 0f;
				
				camLoc.set(cam.getLocation());
				camLoc.addLocal(cam.getDirection().mult(10));
				Vector3f linearVelocity = cam.getDirection().mult(100);
				Vector3f gravity = new Vector3f(0, -98f, 0);

				EntityId bomb = ed.createEntity();
				ed.setComponents(bomb, 
					new Model(Model.BOMB),
					new Collision(0.5f, linearVelocity, new Vector3f(0, 0, 0), gravity),
					new Position(camLoc),
					new Shootable(),
					new Decay(5000));
			}
		}
		
	}
	
	@Override
	protected void cleanup(Application app) {
	}
	
	public void setPlayer(Entity player) {
		this.player = player.getId();
	}
	/**
	 * 退出游戏
	 */
	public void quitGame() {
		getStateManager().detach(getStateManager().getState(SingleGameState.class));
		MainAppState main = getStateManager().getState(MainAppState.class);
		if (main != null) main.setEnabled(true);
	}

	@Override
	protected void onEnable() {
		simpleApp.getRootNode().attachChild(ak47);
	}

	@Override
	protected void onDisable() {
		ak47.removeFromParent();
	}
}