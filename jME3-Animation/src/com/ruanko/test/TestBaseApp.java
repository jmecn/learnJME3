package com.ruanko.test;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.debug.Arrow;
import com.jme3.system.AppSettings;
import com.ruanko.asset.MyBone;

public abstract class TestBaseApp extends SimpleApplication {

	@Override
	public void start() {
		settings = new AppSettings(true);
		settings.setFrameRate(60);
		settings.setBitsPerPixel(24);
		settings.setResolution(800, 600);
		settings.setTitle("Test");
		setSettings(settings);
		setPauseOnLostFocus(false);
		super.start();
	}

	@Override
	public void simpleInitApp() {
		AmbientLight light = new AmbientLight();
		rootNode.addLight(light);

		initAmbient();
		initCam();
		showNodeAxes(10);
		
		init();
	}

	public abstract void init();

	private void initCam() {
		cam.setLocation(new Vector3f(3, 4, 5));
		cam.lookAt(Vector3f.ZERO, cam.getUp());
		this.flyCam.setMoveSpeed(20f);
	}
	
	private void initAmbient() {
		AmbientLight light = new AmbientLight();
		light.setColor(ColorRGBA.White);
		rootNode.addLight(light);
	}

	private void showNodeAxes(float axisLen) {
		// X red
		Vector3f v = new Vector3f(axisLen, 0, 0);
		Arrow a = new Arrow(v);
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Red);
		Geometry geom = new Geometry(rootNode.getName() + "XAxis", a);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);

		// Y green
		v = new Vector3f(0, axisLen, 0);
		a = new Arrow(v);
		mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Green);
		geom = new Geometry(rootNode.getName() + "YAxis", a);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);

		// Z blue
		v = new Vector3f(0, 0, axisLen);
		a = new Arrow(v);
		mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom = new Geometry(rootNode.getName() + "ZAxis", a);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);
	}

}
