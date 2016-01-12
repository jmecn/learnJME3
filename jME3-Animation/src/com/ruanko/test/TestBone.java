package com.ruanko.test;

import com.ruanko.asset.MyBone;

public class TestBone extends TestBaseApp {

	@Override
	public void init() {
		MyBone bone = new MyBone("Bone", assetManager);
		rootNode.attachChild(bone);
	}
	public static void main(String[] args) {
		TestBaseApp app = new TestBone();
		app.setShowSettings(false);
		app.start();
	}
}
