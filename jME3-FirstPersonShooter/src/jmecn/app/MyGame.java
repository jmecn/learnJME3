package jmecn.app;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapText;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;

public class MyGame extends SimpleApplication {

	Spatial terrain;

	CharacterControl player;
	
	@Override
	public void simpleInitApp() {
		// 加载地形
		terrain = assetManager.loadModel("Models/Terrain/iceworld.blend");
		rootNode.attachChild(terrain);
		
		// BlenderLoader导入的模型反光度都太高了，降低一点。
		Node node = ((Node)terrain);
		node.breadthFirstTraversal(new SceneGraphVisitor() {
			@Override
			public void visit(Spatial spatial) {
				if (spatial instanceof Geometry) {
					Geometry geom = (Geometry)spatial;
					Material mat = geom.getMaterial();
					mat.setFloat("Shininess", 0);
				}
			}
		});
		
		DirectionalLight sun = new DirectionalLight();
		sun.setColor(new ColorRGBA(0.5f, 0.5f, 0.5f, 1));
		sun.setDirection(new Vector3f(-3, -5, -3));
		rootNode.addLight(sun);
		
		sun = new DirectionalLight();
		sun.setColor(new ColorRGBA(0.5f, 0.5f, 0.5f, 1));
		sun.setDirection(new Vector3f(3, -5, 3));
		rootNode.addLight(sun);

		// 摄像机移动速度
		flyCam.setMoveSpeed(10);

		// 十字准心
		initCrossHairs();

		initBullet();

	}

	protected void initCrossHairs() {
		// guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
		BitmapText ch = new BitmapText(guiFont, false);
		ch.setSize(guiFont.getCharSet().getRenderedSize());
		ch.setText("+"); // crosshairs
		ch.setLocalTranslation(
				// center
				settings.getWidth() / 2
						- guiFont.getCharSet().getRenderedSize() / 3 * 2,
				settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
		guiNode.attachChild(ch);
	}

	// 物理引擎
	private BulletAppState bulletAppState;

	protected void initBullet() {
		// 激活Bullet物理引擎
		bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);
		
		// 物理空间
		PhysicsSpace space = bulletAppState.getPhysicsSpace();

		// 添加地图
		terrain.addControl(new RigidBodyControl(0));
		space.add(terrain);

		// 玩家
		CapsuleCollisionShape collisionShape = new CapsuleCollisionShape(0.5f, 2f);
		player = new CharacterControl(collisionShape, 0.5f);
		player.setPhysicsLocation(new Vector3f(0, 2, 0));
		space.add(player);
	}
	
	public void simpeUpdate(float tpf) {
		cam.setLocation(player.getPhysicsLocation());
	}

	public static void main(String[] args) {
		MyGame game = new MyGame();
		game.start();
	}

}
