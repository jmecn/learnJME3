package yan.mazegame;

import yan.mazegame.states.AxisState;
import yan.mazegame.states.TestMazeState;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.system.AppSettings;

/**
 * 程序入口
 * 
 * @author yanmaoyuan
 * 
 */
public class TestMazeCreator extends SimpleApplication {

	public static void main(String[] args) {
		SimpleApplication app = new TestMazeCreator();

		AppSettings settings = new AppSettings(true);
		settings.setTitle("迷宫生成测试");
		settings.setResolution(1024, 768);

		app.setSettings(settings);
		app.setShowSettings(false);
		app.setPauseOnLostFocus(false);

		app.start();
	}

	public TestMazeCreator() {
		super(new TestMazeState(),
				new AxisState(),
				new DebugKeysAppState(),
				new FlyCamAppState(),
				new StatsAppState());
	}

	@Override
	public void simpleInitApp() {
		flyCam.setMoveSpeed(50);
	}
}
