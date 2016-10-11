package net.jmecn.app;

import net.jmecn.state.MainAppState;
import net.jmecn.state.MusicState;
import strongdk.jme.appstate.console.ConsoleAppState;
import strongdk.jme.appstate.console.ConsoleDefaultCommandsAppState;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.font.BitmapFont;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.input.InputMapper;
import com.simsilica.lemur.style.BaseStyles;

/**
 * 游戏主类
 * 
 * @author yanmaoyuan
 * 
 */
public class MyGame extends SimpleApplication {

	/**
	 * 构造方法，初始化游戏所使用的AppState，以及启动参数。
	 */
	public MyGame() {
		// 初始化AppState
		super(new DebugKeysAppState(),
				new FlyCamAppState(),
				new StatsAppState(),
				new ScreenshotAppState("",
						System.currentTimeMillis()),
				new ConsoleAppState(),
				new ConsoleDefaultCommandsAppState(),
				new MusicState(),
				new MainAppState());

		// 初始化游戏设置
		AppSettings settings = new AppSettings(true);
		settings.setResolution(1024, 768);
		settings.setTitle("反恐精英 - www.jmecn.net");

		setSettings(settings);
		setShowSettings(false);// 不显示设置窗口
		setPauseOnLostFocus(false);// 窗口失去焦点时，游戏不暂停
	}

	@Override
	public void simpleInitApp() {
		// 删除原有的按ESC退出游戏的功能
		inputManager.deleteMapping(INPUT_MAPPING_EXIT);

		// Initialize the Lemur helper instance
		GuiGlobals.initialize(this);

		// Load the 'glass' style
		BaseStyles.loadStyleResources("Interface/Style/style.groovy");

		// Set 'glass' as the default style when not specified
		GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");

		BitmapFont font = assetManager.loadFont("Interface/Font/font.fnt");
		GuiGlobals.getInstance().getStyles().setDefault(font);

		// Setup default key mappings
        PlayerFunctions.initializeDefaultMappings(GuiGlobals.getInstance().getInputMapper());
        
		InputMapper inputMapper = GuiGlobals.getInstance().getInputMapper();
		inputMapper.addDelegate(PlayerFunctions.F_SCREENSHOT, this, "takeScreenshot");
	}

	/**
	 * 屏幕截图
	 */
	public void takeScreenshot() {
		stateManager.getState(ScreenshotAppState.class).takeScreenshot();
	}

	public static void main(String[] args) {
		MyGame game = new MyGame();
		game.start();
	}

}
