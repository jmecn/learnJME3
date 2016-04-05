package yan.mazegame;

import yan.mazegame.states.TestPlayerState;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.system.AppSettings;

/**
 * 程序入口
 * 
 * @author yan
 * 
 */
public class TestPlayerControl extends SimpleApplication {

	public static void main(String[] args) {
		SimpleApplication app = new TestPlayerControl();

		AppSettings settings = new AppSettings(true);
		settings.setTitle("玩家控制测试");
		settings.setResolution(1024, 768);

		app.setSettings(settings);
		app.setShowSettings(false);
		app.setPauseOnLostFocus(false);

		app.start();
	}

	public TestPlayerControl() {
		super(new TestPlayerState(),
				new DebugKeysAppState(),
				new FlyCamAppState(),
				new StatsAppState());
	}

	@Override
	public void simpleInitApp() {}
}
