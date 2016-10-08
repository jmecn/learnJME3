package jmecn.state;

import jmecn.effects.DecayControl;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.ui.Picture;

/**
 * 游戏内的场景
 * @author yanmaoyuan
 *
 */
public class InGameAppState extends AbstractAppState {

	private final static String LEFT = "left";
	private final static String RIGHT = "right";
	private final static String FORWARD = "forward";
	private final static String BACKWARD = "backward";
	private final static String JUMP = "jump";
	private final static String TRIGGER = "trigger";// 左键扣动扳机
	private final static String BOMB = "bomb";// 右键扔手雷

	private SimpleApplication simpleApp;
	private Camera cam;

	// 游戏内场景的结点
	private Node rootNode = new Node("InGameScene");
	private Node guiNode = new Node("InGameGUI");
	private Node shootable = new Node("Shootable");

	// 音效
	private AudioNode audio_gun;
	private AudioNode audio_bang;
	private AudioNode audio_beep;

	// 物理
	private BulletAppState bulletAppState;
	private CharacterControl player;
	private Node terrainModel;
	private Geometry mark;

	// 手雷
	private RigidBodyControl ball_phy;

	// 运动逻辑
	private boolean left = false, right = false, forward = false,
			backward = false, trigger = false, bomb = false;
	private Vector3f camDir = new Vector3f();
	private Vector3f camLeft = new Vector3f();
	private Vector3f walkDirection = new Vector3f();
	private float moveSpeed = 2f;

	// 特效

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);

		this.simpleApp = (SimpleApplication) app;
		this.cam = simpleApp.getCamera();

		// 初始化场景
		simpleApp.getViewPort().setBackgroundColor(
				new ColorRGBA(0.75f, 0.875f, 1f, 1f));
		simpleApp.getRootNode().attachChild(rootNode);
		simpleApp.getGuiNode().attachChild(guiNode);
		rootNode.attachChild(shootable);

		// 加载地形
		terrainModel = (Node) app.getAssetManager().loadModel(
				"Models/Terrain/iceworld.blend");
		terrainModel.scale(10);
		shootable.attachChild(terrainModel);

		terrainModel.breadthFirstTraversal(new SceneGraphVisitor() {
			@Override
			public void visit(Spatial spatial) {
				// BlenderLoader导入的模型反光度都太高了，降低一点。
				if (spatial instanceof Geometry) {
					Geometry geom = (Geometry) spatial;
					Material mat = geom.getMaterial();
					mat.setFloat("Shininess", 0);
				}
			}
		});

		initAudio();
		initSunLight();
		initCrossHairs();
		initBullet();
		initInput();
		initMark();
	}

	/* gun shot sound is to be triggered by a mouse click. */
	private void initAudio() {
		audio_gun = new AudioNode(simpleApp.getAssetManager(),
				"Sound/Effects/Gun.wav", DataType.Buffer);
		audio_gun.setPositional(true);
		audio_gun.setLooping(false);
		audio_gun.setVolume(2);
		rootNode.attachChild(audio_gun);

		audio_bang = new AudioNode(simpleApp.getAssetManager(),
				"Sound/Effects/Bang.wav", DataType.Buffer);
		audio_bang.setPositional(true);
		audio_bang.setLooping(false);
		audio_bang.setVolume(3);
		rootNode.attachChild(audio_bang);

		audio_beep = new AudioNode(simpleApp.getAssetManager(),
				"Sound/Effects/Beep.ogg", DataType.Buffer);
		audio_beep.setPositional(true);
		audio_beep.setLooping(false);
		audio_beep.setVolume(3);
		rootNode.attachChild(audio_beep);

	}

	/**
	 * 初始化阳光
	 */
	protected void initSunLight() {

		DirectionalLight dl = new DirectionalLight();
		dl.setDirection(new Vector3f(-3, -5, -3).normalize());
		dl.setColor(new ColorRGBA(.6f, .6f, .6f, 0.8f));
		rootNode.addLight(dl);

		dl = new DirectionalLight();
		dl.setDirection(new Vector3f(3, -5, 3).normalize());
		dl.setColor(new ColorRGBA(.4f, .4f, .4f, 0.2f));
		rootNode.addLight(dl);
	}

	/**
	 * 准星
	 */
	protected void initCrossHairs() {
		Picture pic = new Picture("cross");
		pic.setImage(simpleApp.getAssetManager(), "Texture/cross.png", true);
		pic.setWidth(1024);
		pic.setHeight(768);
		guiNode.attachChild(pic);
	}

	protected void initBullet() {
		bulletAppState = new BulletAppState();
		simpleApp.getStateManager().attach(bulletAppState);

		// 地形
		RigidBodyControl rigidBodyControl = new RigidBodyControl(0);
		terrainModel.addControl(rigidBodyControl);

		// 玩家
		CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(10f,
				40f, 1);
		player = new CharacterControl(capsuleShape, 50f);
		player.setJumpSpeed(60);
		player.setFallSpeed(60);
		player.setGravity(98f);
		player.setPhysicsLocation(new Vector3f(0, 20, 0));

		bulletAppState.getPhysicsSpace().add(terrainModel);
		bulletAppState.getPhysicsSpace().add(player);
	}

	/** A red ball that marks the last spot that was "hit" by the "shot". */
	protected void initMark() {
		Sphere sphere = new Sphere(6, 6, 0.2f);
		mark = new Geometry("BOOM!", sphere);
		Material mark_mat = new Material(simpleApp.getAssetManager(),
				"Common/MatDefs/Misc/Unshaded.j3md");
		mark_mat.setColor("Color", ColorRGBA.Black);
		mark.setMaterial(mark_mat);
	}

	protected void initInput() {
		InputManager inputManager = simpleApp.getInputManager();

		inputManager.addMapping(LEFT, new KeyTrigger(KeyInput.KEY_A));
		inputManager.addMapping(RIGHT, new KeyTrigger(KeyInput.KEY_D));
		inputManager.addMapping(FORWARD, new KeyTrigger(KeyInput.KEY_W));
		inputManager.addMapping(BACKWARD, new KeyTrigger(KeyInput.KEY_S));
		inputManager.addMapping(JUMP, new KeyTrigger(KeyInput.KEY_SPACE));
		inputManager.addMapping(TRIGGER, new MouseButtonTrigger(
				MouseInput.BUTTON_LEFT));
		inputManager.addMapping(BOMB, new MouseButtonTrigger(
				MouseInput.BUTTON_RIGHT));

		MyListener listener = new MyListener();
		inputManager.addListener(listener, LEFT, RIGHT, FORWARD, BACKWARD,
				JUMP, TRIGGER, BOMB);
	}

	class MyListener implements ActionListener {
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
			} else if (name.equals(BOMB)) {
				bomb = isPressed;
			}
		}
	}

	private float gunTime = 0f;
	public final static float GUN_COOLDOWN_TIME = 0.1f;// 枪管冷却时间
	private float bombTime = 0f;
	public final static float BOMB_COOLDOWN_TIME = 1f;// 炸弹冷却时间

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
		walkDirection.normalizeLocal().multLocal(moveSpeed);
		player.setWalkDirection(walkDirection);
		cam.setLocation(player.getPhysicsLocation());

		// 连续开枪
		gunTime += tpf;
		if (trigger) {
			if (gunTime >= GUN_COOLDOWN_TIME) {
				gunTime = 0f;
				shoot();
			}
		}

		bombTime += tpf;
		if (bomb) {
			if (bombTime >= BOMB_COOLDOWN_TIME) {
				bombTime = 0;
				bomb();
			}
		}
	}

	/**
	 * 开枪
	 */
	private void shoot() {
		audio_bang.playInstance();

		// 射线检测
		CollisionResults results = new CollisionResults();
		Ray ray = new Ray(cam.getLocation(), cam.getDirection());
		shootable.collideWith(ray, results);
		if (results.size() > 0) {
			CollisionResult closest = results.getClosestCollision();

			// 弹痕标记
			Geometry newMark = mark.clone();
			newMark.setLocalTranslation(closest.getContactPoint());
			rootNode.attachChild(newMark);
			
			// 利用目标的名字来判断是否是炸弹
			Geometry target = closest.getGeometry();
			String name = target.getName();
			if (name.equals("BombGeom1")) {
				
				// 瞬间爆炸
				Node parent = target.getParent().getParent().getParent();
				DecayControl control = parent.getControl(DecayControl.class);
				control.explosionNow();
			}
		}
	}

	/**
	 * 扔雷
	 */
	private void bomb() {
		Vector3f loc = cam.getLocation();
		loc.addLocal(cam.getDirection().mult(3));

		// 导入炸弹模型
		Node bomb = (Node)simpleApp.getAssetManager().loadModel("Models/Bomb/bomb.blend");
		
		// 让手雷在5秒后消失，然后爆炸BOOM!!
		DecayControl decayContorl = new DecayControl(audio_gun, simpleApp);
		bomb.addControl(decayContorl);
		
		shootable.attachChild(bomb);
		bomb.setLocalTranslation(loc);
		ball_phy = new RigidBodyControl(0.5f);
		bomb.addControl(ball_phy);
		bulletAppState.getPhysicsSpace().add(ball_phy);
		ball_phy.setLinearVelocity(cam.getDirection().mult(50));
		ball_phy.setGravity(new Vector3f(0, -98f, 0));
		
	}


	@Override
	public void cleanup() {
		super.cleanup();
	}

}
