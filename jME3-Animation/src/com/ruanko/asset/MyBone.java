package com.ruanko.asset;

import com.jme3.animation.Bone;
import com.jme3.animation.Skeleton;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.debug.SkeletonDebugger;

public class MyBone extends Node{

	public MyBone(String name, AssetManager assetManager) {
		super(name);
		Bone bip01 = new Bone("Bip01");
		bip01.setBindTransforms(new Vector3f(0, 0, 1), new Quaternion(0, 0, 0, 1), new Vector3f(1, 1, 1));

		Bone bip02 = new Bone("Bip02");
		bip01.addChild(bip02);
		bip02.setBindTransforms(new Vector3f(0.74f, 0.74f, 0), new Quaternion(0, 0, 0, 1), new Vector3f(1, 1, 1));

		Bone bip03 = new Bone("Bip03");
		bip02.addChild(bip03);
		bip03.setBindTransforms(new Vector3f(0, 1, 0), new Quaternion(0, 0, 0, 1), new Vector3f(1, 1, 1));
		
		
		Bone[] bones = new Bone[] {bip01, bip02, bip03};
		Skeleton ske = new Skeleton(bones);
		
		SkeletonDebugger sd = new SkeletonDebugger("SkeletonDebuger", ske);
		final Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Magenta);
		mat.getAdditionalRenderState().setDepthTest(false);
		sd.setMaterial(mat);
		this.attachChild(sd);
	}
}
