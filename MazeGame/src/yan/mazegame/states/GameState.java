package yan.mazegame.states;

import yan.mazegame.Game;
import yan.mazegame.logic.BlockCreator;
import yan.mazegame.logic.MazeCreator;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.SkyFactory;
import com.jme3.util.TangentBinormalGenerator;

public class GameState extends AbstractAppState implements ActionListener, AnimEventListener{

	private Game game;
	private Node rootNode;
	private Node guiNode;
	
	private AppStateManager stateManager;
	private InputManager inputManager;
	private AssetManager assetManager;
	private Camera cam;
	private Camera cam2;
	
	/**
	 * 碰撞检测相关
	 */
	private BulletAppState bulletAppState;
	private RigidBodyControl landscape;
	private CharacterControl player;
	private Vector3f walkDirection = new Vector3f();
	private boolean left = false, right = false, up = false, down = false;

	/* 迷宫参数 */
	private Node maze;
	private int col = 14; // 列
	private int row = 15; // 行
	private long seed = 47;// 种子
	private boolean isRand = true;// 是否随机
	private int roadSize = 4;// 道路宽度
	// 迷宫生成器
	private MazeCreator mc = new MazeCreator(col, row, seed, isRand);
	// 方块生成器
	private BlockCreator bc = new BlockCreator(roadSize);
	// 迷宫的起点和终点
	private Vector3f startPoint = new Vector3f();
	private Vector3f stopPoint = new Vector3f();
	
	// 角色模型
	private Vector3f location = new Vector3f();
	private Vector3f lookDirection = new Vector3f();
	private Spatial sinbad;
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		
		if (game == null) {
			game = (Game) app;
			rootNode = new Node("GameRootNode");
			guiNode = new Node("GameGuiNode");
			
			this.stateManager = stateManager;
			this.inputManager = app.getInputManager();
			this.assetManager = app.getAssetManager();
			this.cam = app.getCamera();
		}
		game.getRootNode().attachChild(rootNode);
		game.getGuiNode().attachChild(guiNode);
		
		
		/** Set up Physics */
		this.bulletAppState = new BulletAppState();
		this.stateManager.attach(bulletAppState);
		//bulletAppState.getPhysicsSpace().enableDebug(assetManager);

		setUpKeys();
		/** 0. Create player node */
		sinbad = assetManager.loadModel("Models/Sinbad/Sinbad.mesh.xml");
		sinbad.scale(0.2f);
		rootNode.attachChild(sinbad);

		animControl = sinbad.getControl(AnimControl.class);
		channel = animControl.createChannel();
		animChannel2 = animControl.createChannel();
		idle();
		animControl.addListener(this);
		

		ChaseCamera cc = new ChaseCamera(cam, sinbad, inputManager);
		cc.setEnabled(true);
		cc.setRotationSpeed(1);
		cc.setDragToRotate(true);
		cc.setInvertVerticalAxis(true);
		cc.setMinDistance(3f);
		cc.setDefaultDistance(3f);
		cc.setMaxDistance(10f);

		/** 1. Create terrain material and load four textures into it. */
		maze = new Node("maze");
		initMaze();
		rootNode.attachChild(maze);

		initLight();
		
		/** 2. load sky*/
        rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false));
        
        /** 3. 初始化第2个摄像头 */
        initViewPort2();
        
		/** 6. Add physics: */
		/*
		 * We set up collision detection for the scene by creating a static
		 * RigidBodyControl with mass zero.
		 */
		landscape = new RigidBodyControl(CollisionShapeFactory.createMeshShape(maze), 0);
		maze.addControl(landscape);
		
		// We set up collision detection for the player by creating
		// a capsule collision shape and a CharacterControl.
		// The CharacterControl offers extra settings for
		// size, stepheight, jumping, falling, and gravity.
		// We also put the player in its starting position.
		CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(0.3f, 1.5f, 1);
		player = new CharacterControl(capsuleShape, 0.05f);
		player.setJumpSpeed(10);
		player.setFallSpeed(30);
		player.setGravity(30);
		
		player.setPhysicsLocation(startPoint);

		// We attach the scene and the player to the rootnode and the physics
		// space,
		// to make them appear in the game world.
		bulletAppState.getPhysicsSpace().add(landscape);
		bulletAppState.getPhysicsSpace().add(player);
	}
	
	@Override
	public void cleanup() {
		game.getRootNode().detachChild(rootNode);
		game.getGuiNode().detachChild(guiNode);
		super.cleanup();
	}

	private void initViewPort2() {
		cam2 = cam.clone();
        cam2.setName("cam2");
        cam2.setViewPort(0.7f, 1f, 0f, 0.3f);
        cam2.setLocation(new Vector3f(0, 1, 0));
        cam2.lookAt(new Vector3f(0, 0, 0), new Vector3f(1, 0, 0));

        final ViewPort view3 = game.getRenderManager().createMainView("Top center", cam2);
        view3.setClearFlags(true, true, true);
        view3.attachScene(rootNode);
	}
	/**
	 * 初始化光照
	 */
	boolean lightInitialzed = false;

	private void initLight() {
		if (!lightInitialzed) {
			/**
			 * 创建一个垂直向下的方向光源，这道光将会产生阴影，这样就能预知方块下落的位置。
			 */
			DirectionalLight light = new DirectionalLight();
			ColorRGBA color = new ColorRGBA(1, 1, 1, 1f);
			light.setColor(color);
			light.setDirection(new Vector3f(3, -10, 4).normalizeLocal());
			rootNode.addLight(light);

			/**
			 * 再添加一个环境光，让游戏场景稍微亮一些。
			 */
			AmbientLight ambient = new AmbientLight();
			rootNode.addLight(ambient);

			lightInitialzed = true;
		}
	}
	private void initMaze() {
		// 配置参数
		mc.config(row, col, seed, isRand);
		bc.setRoadSize(roadSize);

		// 生成迷宫
		mc.create();

		// 生成地图
		bc.create(mc);

		
		int[][] map = bc.getMap();
		int row = bc.getBlockRow();
		int col = bc.getBlockCol();

		startPoint.set(roadSize/2, 1, roadSize/2+1);
		stopPoint.set(col-roadSize/2, 1, col-roadSize/2+1);
		location.set(startPoint);
		
		// 方块坐标的偏移量
		Vector3f postion = new Vector3f();
		Vector3f offset = new Vector3f(0f, 0.5f, 0);

		maze.detachAllChildren();

		Node wall = new Node("wall");
		maze.attachChild(wall);

		Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		Texture diff = assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall.jpg");
		diff.setWrap(Texture.WrapMode.Repeat);
		Texture norm = assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall_normal.jpg");
		norm.setWrap(Texture.WrapMode.Repeat);
		mat.setTexture("DiffuseMap", diff);
		mat.setTexture("NormalMap", norm);
		mat.setFloat("Shininess", 2.0f);
		
		Box mesh = new Box(0.5f, 0.5f, 0.5f);
		Geometry geom = new Geometry("box", mesh);
		geom.setMaterial(mat);

		for (int y = 0; y < row; y++) {
			for (int x = 0; x < col; x++) {

				if (map[y][x] > 0) {
					// 计算实际坐标
					postion.set(offset.add(x, 0, y));
					Geometry brick = geom.clone();
					brick.setLocalTranslation(postion);
					wall.attachChild(brick);
				}
			}
		}

		maze.attachChild(wall.clone().move(0, 1, 0));
		maze.attachChild(wall.clone().move(0, 2, 0));

	   // 创建地板
		Material mat2 = assetManager.loadMaterial("Textures/Terrain/BrickWall/BrickWall.j3m");
		mat2.getTextureParam("DiffuseMap").getTextureValue().setWrap(WrapMode.Repeat);
		mat2.getTextureParam("NormalMap").getTextureValue().setWrap(WrapMode.Repeat);
		mat2.getTextureParam("ParallaxMap").getTextureValue().setWrap(WrapMode.Repeat);
		Box floor = new Box(col/2+1, 0.1f, row/2+1);
		TangentBinormalGenerator.generate(floor);
		floor.scaleTextureCoordinates(new Vector2f(col/2, row/2));
		Geometry floorGeom = new Geometry("Floor", floor);
		floorGeom.setMaterial(mat2);
		floorGeom.setShadowMode(ShadowMode.Receive);
		floorGeom.move(col/2, -0.1f, row/2);
		maze.attachChild(floorGeom);
	}

	/**
	 * We over-write some navigational key mappings here, so we can add
	 * physics-controlled walking and jumping:
	 */
	private void setUpKeys() {
		inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
		inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
		inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
		inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
		inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
		inputManager.addListener(this, "Left");
		inputManager.addListener(this, "Right");
		inputManager.addListener(this, "Up");
		inputManager.addListener(this, "Down");
		inputManager.addListener(this, "Jump");
	}

	/**
	 * These are our custom actions triggered by key presses. We do not walk
	 * yet, we just keep track of the direction the user pressed.
	 */
	public void onAction(String binding, boolean value, float tpf) {
		if (binding.equals("Left")) {
			if (value) {
				left = true;
			} else {
				left = false;
			}
			walk(left || right || up || down);
		} else if (binding.equals("Right")) {
			if (value) {
				right = true;
			} else {
				right = false;
			}
			walk(left || right || up || down);
		} else if (binding.equals("Up")) {
			if (value) {
				up = true;
			} else {
				up = false;
			}
			walk(left || right || up || down);
		} else if (binding.equals("Down")) {
			if (value) {
				down = true;
			} else {
				down = false;
			}
			walk(left || right || up || down);
		} else if (binding.equals("Jump")) {
			player.jump();
			jump(value);
		}
	}

	boolean gameEnd = false;
	/**
	 * This is the main event loop--walking happens here. We check in which
	 * direction the player is walking by interpreting the camera direction
	 * forward (camDir) and to the side (camLeft). The setWalkDirection()
	 * command is what lets a physics-controlled player walk. We also make sure
	 * here that the camera moves with player.
	 */
	@Override
	public void update(float tpf) {
		Vector3f camDir = cam.getDirection().clone().multLocal(0.6f);
		Vector3f camLeft = cam.getLeft().clone().multLocal(0.4f);
		walkDirection.set(0, 0, 0);
		
		if (left) {
			walkDirection.addLocal(camLeft);
		}
		if (right) {
			walkDirection.addLocal(camLeft.negate());
		}
		if (up) {
			walkDirection.addLocal(camDir);
		}
		if (down) {
			walkDirection.addLocal(camDir.negate());
		}
		
		// 计算移动方向
		if (walkDirection.length() > 0) {
			
			walkDirection.y = 0;
			walkDirection.normalizeLocal();
			
			// 计算面向
			if (!lookDirection.equals(walkDirection)) {
				lookDirection.set(walkDirection);
				sinbad.lookAt(location.add(lookDirection), Vector3f.UNIT_Y);
			}
			
			// 不要出迷宫边界!
			Vector3f tmp = location.add(walkDirection);
			if (tmp.x<0 || tmp.x>bc.getBlockCol()) {
				walkDirection.x = 0;
			}
			if (tmp.z<0 || tmp.z>bc.getBlockRow()) {
				walkDirection.z = 0;
			}
		}
		player.setWalkDirection(walkDirection.mult(0.1f));
		location.set(player.getPhysicsLocation());
		sinbad.setLocalTranslation(location);
		
		// 头顶的摄像头
		cam2.setLocation(new Vector3f(location).setY(30));
		
		if (player.onGround()) {
			String animName = channel.getAnimationName();
			if (animName.startsWith("Jump")) {
				walk(walking);
			}
		}
		
		if (sinbad.getLocalTranslation().distance(stopPoint) < roadSize && !gameEnd) {
			System.out.println("已经到出口了");
			dance(true);
			gameEnd = true;
		}
	}

	// 动画相关的参数
	private AnimControl animControl = null;
	private AnimChannel channel = null;
	private AnimChannel animChannel2 = null;

	boolean walking = false;
	@Override
	public void onAnimCycleDone(AnimControl control, AnimChannel channel,
			String animName) {
		if (animName.equals("RunTop") && !walking) {
			idle();
		} else if (animName.equals("JumpStart")) {
			channel.setAnim("JumpLoop");
		} else if (animName.equals("JumpLoop")) {
			if (player.onGround()) {
				channel.setAnim("JumpEnd");
				channel.setLoopMode(LoopMode.DontLoop);
			}
		} else if (animName.equals("JumpEnd")) {
			idle();
		}

	}

	@Override
	public void onAnimChange(AnimControl control, AnimChannel channel,
			String animName) {
		// TODO Auto-generated method stub
	}

	void idle() {
		channel.setAnim("IdleTop");
		animChannel2.setAnim("IdleBase");
	}
	void walk(boolean value) {
		walking = value;
		if (value) {
			if (!channel.getAnimationName().equals("RunTop")) {
				channel.setAnim("RunTop");
				animChannel2.setAnim("RunBase");
				channel.setLoopMode(LoopMode.Loop);
			}
		} else {
			idle();
		}
	}

	void jump(boolean value) {
		if (value) {
			channel.setAnim("JumpStart");
			channel.setLoopMode(LoopMode.DontLoop);
		}
	}
	void dance(boolean value) {
		if (value) {
			System.out.println(channel);
			if (!channel.getAnimationName().equals("Dance")) {
				channel.setAnim("Dance");
				channel.setLoopMode(LoopMode.Loop);
			}
		}
	}

}
