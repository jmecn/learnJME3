package net.jmecn.app;

import net.jmecn.core.Model;
import net.jmecn.effects.Explosion;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.SkyFactory;
import com.jme3.util.SkyFactory.EnvMapType;

/**
 * 模型工厂
 * 
 * @author yanmaoyuan
 * 
 */
public class ModelFactory {

	private AssetManager assetManager;

	public ModelFactory(AssetManager assetManager) {
		this.assetManager = assetManager;
	}

	/**
	 * 加载模型资源
	 * 
	 * @param name
	 * @return
	 */
	public Spatial create(String name) {
		if (name.equals(Model.ICEWORLD))
			return getIceWorld();
		else if (name.equals(Model.SKY))
			return getSky();
		else
			return assetManager.loadModel(name);
	}

	/**
	 * 加载iceworld地图
	 * 
	 * @return
	 */
	public Node getIceWorld() {
		Node iceworld = (Node) assetManager.loadModel(Model.ICEWORLD);
		iceworld.scale(10);

		iceworld.breadthFirstTraversal(new SceneGraphVisitor() {
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

		return iceworld;
	}

	/**
	 * 加载天空
	 * 
	 * @return
	 */
	public Spatial getSky() {
		return SkyFactory.createSky(assetManager, "Textures/Sky/sky.jpg",
				EnvMapType.SphereMap);
	}

	/**
	 * 爆炸特效
	 * 
	 * @return
	 */
	public Node getExplosion() {
		return new Explosion(assetManager);
	}

	/**
	 * 创建一个正方体
	 * 
	 * @param color
	 * @return
	 */
	public Geometry createBox(ColorRGBA color) {
		Geometry spatial = new Geometry("Box", new Box(1, 1, 1));
		Material mat = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", color);
		spatial.setMaterial(mat);
		return spatial;
	}

	/**
	 * 创建一个球体
	 * 
	 * @param color
	 * @return
	 */
	public Geometry createSphere(ColorRGBA color) {
		Geometry spatial = new Geometry("Sphere", new Sphere(6, 6, 1));
		Material mat = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", color);
		spatial.setMaterial(mat);
		return spatial;
	}

	/**
	 * 创建一个圆柱体
	 * 
	 * @param color
	 * @return
	 */
	public Geometry createCylinder(ColorRGBA color) {
		Geometry spatial = new Geometry("Cylinder", new Cylinder(2, 6, 1, 4,
				true));
		Material mat = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", color);
		spatial.setMaterial(mat);

		return spatial;
	}

}
