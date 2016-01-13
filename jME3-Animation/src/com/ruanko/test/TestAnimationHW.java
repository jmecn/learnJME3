package com.ruanko.test;

import com.jme3.animation.AnimControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.debug.SkeletonDebugger;
import com.ruanko.asset.CreateAnimationHW;

public class TestAnimationHW extends TestBaseApp {

	@Override
	public void init() {
		CreateAnimationHW ca = new CreateAnimationHW(assetManager);
		Node node = ca.createModel();
		rootNode.attachChild(node);
		
		AnimControl ac = node.getControl(AnimControl.class);
		if (ac != null) {
			ac.createChannel().setAnim("Anim");
			
			SkeletonDebugger sd = new SkeletonDebugger("SkeletonDebuger", ac.getSkeleton());
			final Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
			mat.setColor("Color", ColorRGBA.Magenta);
			mat.getAdditionalRenderState().setDepthTest(false);
			sd.setMaterial(mat);
			rootNode.attachChild(sd);
		}
	}
	public static void main(String[] args) {
		TestBaseApp app = new TestAnimationHW();
		app.setShowSettings(false);
		app.start();
	}
}
