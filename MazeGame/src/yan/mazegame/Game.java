package yan.mazegame;

import yan.mazegame.states.AssetAppStates;
import yan.mazegame.states.GameAppStates;
import yan.mazegame.states.TimerAppStates;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.ScreenshotAppState;

/**
 * ³ÌÐòÈë¿Ú
 * @author yanmaoyuan
 *
 */
public class Game extends SimpleApplication {

	public Game() {
		super(new GameAppStates(),
			new AssetAppStates(),
			new TimerAppStates(),
			new ScreenshotAppState(""));
	}

	@Override
	public void simpleInitApp() {
		// TODO Auto-generated method stub
	}

}
