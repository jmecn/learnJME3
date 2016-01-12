package com.ruanko.test;

import com.jme3.animation.AnimControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.debug.SkeletonDebugger;
import com.ruanko.asset.CreateAnimation;

public class TestAnimation extends TestBaseApp {

	@Override
	public void init() {
		CreateAnimation ca = new CreateAnimation(assetManager);
		AnimControl ac = ca.createAnimControl();
		
		Node node = ca.createModel();
		node.addControl(ac);
		
		rootNode.attachChild(node);
		
		SkeletonDebugger sd = new SkeletonDebugger("SkeletonDebuger", ac.getSkeleton());
		final Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Magenta);
		mat.getAdditionalRenderState().setDepthTest(false);
		sd.setMaterial(mat);
		node.attachChild(sd);
		
		ac.createChannel().setAnim("Anim");;
	}
	public static void main(String[] args) {
		TestBaseApp app = new TestAnimation();
		app.setShowSettings(false);
		app.start();
	}
}
