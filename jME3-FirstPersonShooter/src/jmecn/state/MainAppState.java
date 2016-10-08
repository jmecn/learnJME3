package jmecn.state;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

/**
 * 主界面
 * @author yanmaoyuan
 *
 */
public class MainAppState extends AbstractAppState implements ActionListener {

	private SimpleApplication simpleApp;
	
	public final static String START_GAME = "start_game";
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		
		simpleApp = (SimpleApplication)app;
		
		InputManager inputManager = simpleApp.getInputManager();
		inputManager.addMapping(START_GAME, new KeyTrigger(KeyInput.KEY_SPACE));
		inputManager.addListener(this, START_GAME);
	}

	@Override
	public void cleanup() {
		super.cleanup();
	}

	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		if (name.equals(START_GAME) && isPressed) {
			
			simpleApp.getStateManager().detach(this);
			simpleApp.getStateManager().attach(new InGameAppState());
		}
	}

	@Override
	public void stateDetached(AppStateManager stateManager) {
		super.stateDetached(stateManager);
	
		// 移除按键事件
		simpleApp.getInputManager().removeListener(this);
		simpleApp.getInputManager().deleteMapping(START_GAME);
	}

}
