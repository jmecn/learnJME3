package teris.game;

import teris.game.states.InputStates;
import teris.game.states.LogicStates;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.system.AppSettings;

public class Game extends SimpleApplication {

	@Override
	public void simpleInitApp() {
		
		// 这个俄罗斯方块游戏不需要FlyCamera，因此我移除了它。
		FlyCamAppState fcs = stateManager.getState(FlyCamAppState.class);
		if (fcs != null) stateManager.detach(fcs);
		
		// 我也不需要显示调试信息
		StatsAppState sas = stateManager.getState(StatsAppState.class);
		if (sas != null) stateManager.detach(sas);
		
		// Debug也移除掉
		DebugKeysAppState dkas = stateManager.getState(DebugKeysAppState.class);
		if (dkas != null) stateManager.detach(dkas);
		
		// TODO 加入主界面
		// 还没开发
		
		// 加入游戏核心逻辑
		LogicStates logic = new LogicStates();
		// 先不启动，等初始化结束后再启动。
		logic.setEnabled(false);
		stateManager.attach(logic);
		
		// 加入输入控制
		InputStates input = new InputStates();
		stateManager.attach(input);
	}
	
	@Override
	public void simpleUpdate(float tpf) {

	}
	
	public static void main(String[] args) {
		Game app = new Game();
		AppSettings settings = new AppSettings(true);
		settings.setTitle("3D Teris");
		settings.setResolution(480, 640);
		settings.setFrequency(75);
		app.setSettings(settings);
		app.setShowSettings(false);
		app.setPauseOnLostFocus(false);
		
		app.start();
	}

}
