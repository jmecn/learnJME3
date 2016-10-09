package jmecn.effects;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;

/**
 * 一段时间后会自动消失
 * 
 * @author yanmaoyuan
 * 
 */
public class DecayControl extends AbstractControl {

	private float totalTime = 5f;
	private float time = 0;

	private AudioNode boom;
	private Explosion explosion;
	private boolean used = false;
	
	private SimpleApplication simpleApp;
	
	public DecayControl(AudioNode boom, SimpleApplication simpleApp) {
		this.simpleApp = simpleApp;
		
		totalTime = 5f;
		time = 0f;
		this.boom = boom;
		
		explosion = new Explosion(simpleApp.getAssetManager(),
				simpleApp.getRenderManager());
	}

	@Override
	protected void controlUpdate(float tpf) {
		time += tpf;

		if (time >= totalTime && !used) {
			time = 0;
			// 从父节点中移除此物体
			explosionNow();
		}

	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {}

	public void explosionNow() {
		used = true;
		
		// 从父节点中移除此物体
		Node node = spatial.getParent();
		node.detachChild(spatial);
	
		// 注意！！这个物体在移除场景的时候，碰撞形状并未从BulletAppState中移除！
		BulletAppState bulletApppState = simpleApp.getStateManager().getState(BulletAppState.class);
		if (bulletApppState != null) {
			bulletApppState.getPhysicsSpace().removeAll(spatial);
		}
		
		// 设置爆炸特效
		Vector3f location = spatial.getLocalTranslation();
		spatial.removeControl(this);
	
		boom.playInstance();
		
		explosion.setLocalTranslation(location);
		node.attachChild(explosion);
	}

}
