package yan.mazegame.states;

import java.util.ArrayList;
import java.util.List;

import yan.mazegame.Game;

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
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

public class TestPlayerState extends AbstractAppState implements ActionListener, AnimEventListener{

	private Game game;
	private Node rootNode;
	private Node guiNode;
	
	private AppStateManager stateManager;
	private InputManager inputManager;
	private AssetManager assetManager;
	private Camera cam;
	

	private BulletAppState bulletAppState;
	private RigidBodyControl landscape;
	private CharacterControl player;
	private Vector3f walkDirection = new Vector3f();
	private Vector3f lookDirection = new Vector3f();
	private boolean left = false, right = false, up = false, down = false;
	private TerrainQuad terrain;
	private Material mat_terrain;

	private Spatial sinbad;
	
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
		
		/** Set up Physics */
		bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);
		//bulletAppState.getPhysicsSpace().enableDebug(assetManager);

		setUpKeys();
		/** 0. Create player node */
		AmbientLight light = new AmbientLight();
		rootNode.addLight(light);

		sinbad = assetManager.loadModel("Models/Sinbad/Sinbad.mesh.xml");
		sinbad.scale(1f);
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
		cc.setMaxDistance(200f);

		/** 1. Create terrain material and load four textures into it. */
		mat_terrain = new Material(assetManager,
				"Common/MatDefs/Terrain/Terrain.j3md");

		/** 1.1) Add ALPHA map (for red-blue-green coded splat textures) */
		mat_terrain
				.setTexture("Alpha", assetManager
						.loadTexture("Textures/Terrain/splat/alphamap.png"));

		/** 1.2) Add GRASS texture into the red layer (Tex1). */
		Texture grass = assetManager
				.loadTexture("Textures/Terrain/splat/grass.jpg");
		grass.setWrap(WrapMode.Repeat);
		mat_terrain.setTexture("Tex1", grass);
		mat_terrain.setFloat("Tex1Scale", 64f);

		/** 1.3) Add DIRT texture into the green layer (Tex2) */
		Texture dirt = assetManager
				.loadTexture("Textures/Terrain/splat/dirt.jpg");
		dirt.setWrap(WrapMode.Repeat);
		mat_terrain.setTexture("Tex2", dirt);
		mat_terrain.setFloat("Tex2Scale", 32f);

		/** 1.4) Add ROAD texture into the blue layer (Tex3) */
		Texture rock = assetManager
				.loadTexture("Textures/Terrain/splat/road.jpg");
		rock.setWrap(WrapMode.Repeat);
		mat_terrain.setTexture("Tex3", rock);
		mat_terrain.setFloat("Tex3Scale", 128f);

		/** 2. Create the height map */
		AbstractHeightMap heightmap = null;
		Texture heightMapImage = assetManager
				.loadTexture("Textures/Terrain/splat/mountains512.png");
		heightmap = new ImageBasedHeightMap(heightMapImage.getImage());
		heightmap.load();

		/**
		 * 3. We have prepared material and heightmap. Now we create the actual
		 * terrain: 3.1) Create a TerrainQuad and name it "my terrain". 3.2) A
		 * good value for terrain tiles is 64x64 -- so we supply 64+1=65. 3.3)
		 * We prepared a heightmap of size 512x512 -- so we supply 512+1=513.
		 * 3.4) As LOD step scale we supply Vector3f(1,1,1). 3.5) We supply the
		 * prepared heightmap itself.
		 */
		terrain = new TerrainQuad("my terrain", 65, 513,
				heightmap.getHeightMap());

		/**
		 * 4. We give the terrain its material, position & scale it, and attach
		 * it.
		 */
		terrain.setMaterial(mat_terrain);
		terrain.setLocalTranslation(0, -100, 0);
		terrain.setLocalScale(2f, 1f, 2f);
		rootNode.attachChild(terrain);

		/** 5. The LOD (level of detail) depends on were the camera is: */
		List<Camera> cameras = new ArrayList<Camera>();
		cameras.add(cam);
		TerrainLodControl control = new TerrainLodControl(terrain, cameras);
		terrain.addControl(control);

		/** 6. Add physics: */
		/*
		 * We set up collision detection for the scene by creating a static
		 * RigidBodyControl with mass zero.
		 */
		terrain.addControl(new RigidBodyControl(0));

		// We set up collision detection for the player by creating
		// a capsule collision shape and a CharacterControl.
		// The CharacterControl offers extra settings for
		// size, stepheight, jumping, falling, and gravity.
		// We also put the player in its starting position.
		CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 7f, 1);
		player = new CharacterControl(capsuleShape, 0.05f);
		player.setJumpSpeed(20);
		player.setFallSpeed(30);
		player.setGravity(30);
		player.setPhysicsLocation(new Vector3f(-10, 10, 10));

		// We attach the scene and the player to the rootnode and the physics
		// space,
		// to make them appear in the game world.
		bulletAppState.getPhysicsSpace().add(terrain);
		bulletAppState.getPhysicsSpace().add(player);
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
		if (walkDirection.length() != 0 && !lookDirection.equals(walkDirection)) {
			lookDirection.set(walkDirection);
			lookDirection.y= 0;
			sinbad.lookAt(player.getPhysicsLocation().add(lookDirection), Vector3f.UNIT_Y);
		}
		player.setWalkDirection(walkDirection);
		sinbad.setLocalTranslation(player.getPhysicsLocation());
		
		
		if (player.onGround()) {
			String animName = channel.getAnimationName();
			if (animName.startsWith("Jump")) {
				walk(walking);
			}
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
		} else if (animName.equals("Dance")) {
			if (walking) {
				walk(true);
			} else {
				idle();
			}
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
		// animChannel1.setAnim("DrawSwords");
		// animChannel1.setAnim("StandUpBack");
		// animChannel1.setAnim("StandUpFront");

		// animChannel1.setAnim("IdleTop");
		// animChannel2.setAnim("IdleBase");
		// animChannel1.setLoopMode(LoopMode.Loop);

		// animChannel1.setAnim("Dance");
		// animChannel1.setLoopMode(LoopMode.DontLoop);
		// animChannel1.setAnim("SliceVertical");
		// animChannel1.setAnim("SliceHorizontal");

		// animChannel1.setAnim("JumpStart");
		// animChannel1.setAnim("JumpLoop");
		// animChannel1.setAnim("JumpEnd");

		// animChannel1.setAnim("HandsClosed");
		// animChannel1.setAnim("HandsRelaxed");
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
				channel.setLoopMode(LoopMode.DontLoop);
			}
		}
	}

}
