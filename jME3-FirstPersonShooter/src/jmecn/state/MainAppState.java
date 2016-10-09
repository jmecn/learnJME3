package jmecn.state;

import strongdk.jme.appstate.console.CommandEvent;
import strongdk.jme.appstate.console.CommandListener;
import strongdk.jme.appstate.console.ConsoleAppState;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.event.MouseAppState;
import com.simsilica.lemur.style.BaseStyles;

/**
 * 主界面
 * @author yanmaoyuan
 *
 */
public class MainAppState extends AbstractAppState {

	private SimpleApplication simpleApp;
	private ConsoleAppState console;
	
	public final static String START_GAME = "start";
	public final static String QUIT_GAME = "quit";
	
	private Node guiNode = new Node("mainGui");
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		
		simpleApp = (SimpleApplication)app;
		
		simpleApp.getGuiNode().attachChild(guiNode);
		
		// 初始化控制台
		console = stateManager.getState(ConsoleAppState.class);
		if (console != null) {
			console.registerCommand(START_GAME, commandListener);
			console.registerCommand(QUIT_GAME, commandListener);
		}
		
		// 启用鼠标
		MouseAppState mouseAppState = stateManager.getState(MouseAppState.class);
    	if (mouseAppState != null) {
    		mouseAppState.setEnabled(true);
    	}
		
    	// 初始化摄像机位置
    	Camera cam = simpleApp.getCamera();
    	cam.setLocation(new Vector3f(0, 0, 0));
		cam.lookAtDirection(new Vector3f(0, 0, 1), Vector3f.UNIT_Y);
		
	    // 初始化Lemur GUI
		GuiGlobals.initialize(app);
		
		initGUI();
		
	}
	
	// 命令行指令监听器
	private CommandListener commandListener = new CommandListener() {
		@Override
		public void execute(CommandEvent evt) {
			String command = evt.getCommand();
			if (START_GAME.equalsIgnoreCase(command)) {
				startGame();
			} else if (QUIT_GAME.equalsIgnoreCase(command)) {
				quitGame();
			}
		}
	};
	
	/**
	 * 开始游戏
	 */
	private void startGame() {
		simpleApp.getStateManager().detach(MainAppState.this);
    	simpleApp.getStateManager().attach(new InGameAppState());
	}
	
	/**
	 * 退出游戏
	 */
	private void quitGame() {
		simpleApp.stop();
	}

	@SuppressWarnings("unchecked")
	private void initGUI() {
		// Load the 'glass' style
		BaseStyles.loadGlassStyle();
		
		// Set 'glass' as the default style when not specified
		GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");
		
		// 创建一个窗口
		Container myWindow = new Container();
		guiNode.attachChild(myWindow);

		// Put it somewhere that we will see it.
		// Note: Lemur GUI elements grow down from the upper left corner.
		myWindow.setLocalTranslation(450, 440, 0);

		// 标题
		myWindow.addChild(new Label("Main Menu"));
		
		// 开始游戏
		Button startBtn = myWindow.addChild(new Button(START_GAME));
		startBtn.addClickCommands(new Command<Button>() {
		        @Override
		        public void execute(Button source) {
		        	startGame();
		        }
		    });
		
		// 退出游戏
		Button quitBtn = myWindow.addChild(new Button(QUIT_GAME));
		quitBtn.addClickCommands(new Command<Button>() {
			@Override
			public void execute(Button source) {
				quitGame();
			}
		});
		
	}
	
	@Override
	public void stateDetached(AppStateManager stateManager) {
		// 禁用鼠标
		MouseAppState mouseAppState = stateManager.getState(MouseAppState.class);
    	if (mouseAppState != null) {
    		mouseAppState.setEnabled(false);
    	}
		
    	// 控制台
    	if (console != null) {
    		console.appendConsole("MainAppState detached");
    		console.unregisterCommands(commandListener);
    	}
    	
		guiNode.detachAllChildren();
		simpleApp.getGuiNode().detachChild(guiNode);
	}
	
}
