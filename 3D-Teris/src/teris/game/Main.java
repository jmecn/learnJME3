package teris.game;

import teris.game.states.ThreeDLogicStates;
import teris.game.states.TwoDLogicStates;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.system.AppSettings;

/**
 * 应用程序的主函数入口
 * @author yanmaoyuan
 *
 */
public class Main extends SimpleApplication {

	public Main() {
		super(new ThreeDLogicStates(),// 3d game
			//new TwoDLogicStates(),// 2d game [developing]
			new ScreenshotAppState("")
		);
	}
	
	@Override
	public void simpleInitApp() {}
	
	public static void main(String[] args) {
		Main app = new Main();
		
		AppSettings settings = new AppSettings(true);
		settings.setTitle("3D Teris");
		settings.setResolution(480, 640);
		
		app.setSettings(settings);
		app.setShowSettings(false);
		app.setPauseOnLostFocus(false);
		
		app.start();
	}


}
