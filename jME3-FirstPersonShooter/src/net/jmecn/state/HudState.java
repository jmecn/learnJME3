package net.jmecn.state;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;

public class HudState extends BaseAppState {

	private Node guiNode;
	
	public HudState() {
		guiNode = new Node("Hud");
	}
	
	@Override
	protected void initialize(Application app) {
		Picture pic = new Picture("cross");
		pic.setImage(app.getAssetManager(), "Interface/Images/cross.png", true);
		pic.setWidth(1024);
		pic.setHeight(768);
		
		Camera cam = app.getCamera();
		float scaleY = cam.getHeight() / 768f;
		float scaleX = cam.getWidth() / 1024f;
		pic.scale(scaleX, scaleY, 1);
		
		guiNode.attachChild(pic);
	}

	@Override
	protected void cleanup(Application app) {
	}

	@Override
	protected void onEnable() {
		((SimpleApplication)getApplication()).getGuiNode().attachChild(guiNode);
	}

	@Override
	protected void onDisable() {
		guiNode.removeFromParent();
	}

}
