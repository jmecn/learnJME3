package net.jmecn.state;

import strongdk.jme.appstate.console.CommandEvent;
import strongdk.jme.appstate.console.CommandListener;
import strongdk.jme.appstate.console.ConsoleAppState;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.Action;
import com.simsilica.lemur.ActionButton;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.TextField;
import com.simsilica.lemur.event.MouseAppState;

/**
 * 主界面
 * 
 * @author yanmaoyuan
 * 
 */
public class MainAppState extends BaseAppState {

	private SimpleApplication simpleApp;
	private ConsoleAppState console;

	public final static String START_GAME = "start";
	public final static String QUIT_GAME = "quit";

	// 3D场景
	private Node rootNode = new Node("mainScene");
	private Spatial model;

	// GUI
	private Node guiNode = new Node("mainGui");

	private Container menu;

	private TextField userField;
	private TextField hostField;
	private TextField portField;

	@Override
	public void initialize(Application app) {

		simpleApp = (SimpleApplication) app;
		console = simpleApp.getStateManager().getState(ConsoleAppState.class);

		init3DScene();

		initGUI();
	}

	@Override
	public void update(float tpf) {
		// 让场景缓慢旋转
		model.rotate(0, tpf * FastMath.DEG_TO_RAD, 0);
	}

	@Override
	protected void cleanup(Application app) {
	}

	@Override
	protected void onEnable() {
		simpleApp.getGuiNode().attachChild(guiNode);
		simpleApp.getRootNode().attachChild(rootNode);

		// 启用鼠标
		MouseAppState mouseAppState = getStateManager().getState(
				MouseAppState.class);
		if (mouseAppState != null) {
			mouseAppState.setEnabled(true);
		}

		// 启用控制台
		if (console != null) {
			console.registerCommand(START_GAME, commandListener);
			console.registerCommand(QUIT_GAME, commandListener);
		}

		// 禁用FlyCamera
		simpleApp.getFlyByCamera().setEnabled(false);
		
		Camera cam = simpleApp.getCamera();
		cam.setLocation(new Vector3f(200, 400, 200));
		cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
		
		simpleApp.getViewPort().setBackgroundColor(ColorRGBA.Black);
	}

	@Override
	protected void onDisable() {
		guiNode.removeFromParent();
		rootNode.removeFromParent();

		// 禁用鼠标
		MouseAppState mouseAppState = getStateManager().getState(
				MouseAppState.class);
		if (mouseAppState != null) {
			mouseAppState.setEnabled(false);
		}

		// 禁用控制台
		if (console != null) {
			console.appendConsole("MainAppState detached");
			console.unregisterCommands(commandListener);
		}

		simpleApp.getFlyByCamera().setEnabled(true);
	}

	/**
	 * 初始化3D场景
	 */
	private void init3DScene() {
		// 给主界面的背景添加一个3D场景
		model = simpleApp.getAssetManager().loadModel(
				"Models/Terrain/iceworld.j3o");
		rootNode.attachChild(model);
	}

	/**
	 * 初始化GUI
	 */
	private void initGUI() {
		// 创建一个窗口
		menu = new Container();
		guiNode.attachChild(menu);

		// Put it somewhere that we will see it.
		// Note: Lemur GUI elements grow down from the upper left corner.
		menu.setLocalTranslation(450, 440, 0);

		// 标题
		Label title = new Label("菜单");

		// 开始游戏
		ActionButton single = new ActionButton(new Action("单人游戏") {
			@Override
			public void execute(Button source) {
				startGame();
			}
		});

		ActionButton multi = new ActionButton(new Action("局域网") {
			@Override
			public void execute(Button source) {
				// 打开局域网设置
			}
		});

		// 退出游戏
		ActionButton quit = new ActionButton(new Action("退出游戏") {
			@Override
			public void execute(Button source) {
				quitGame();
			}
		});
		
		menu.addChild(title);
		menu.addChild(single);
		menu.addChild(multi);
		menu.addChild(quit);

		// 根据摄像机的位置，将菜单居中。
		Camera cam = simpleApp.getCamera();
		float menuScale = cam.getHeight()/600f;

        Vector3f pref = menu.getPreferredSize();
        float bias = (cam.getHeight() - (pref.y*menuScale)) * 0.1f;
        menu.setLocalTranslation(cam.getWidth() * 0.5f - pref.x * 0.5f * menuScale,
                                 cam.getHeight() * 0.5f + pref.y * 0.5f * menuScale + bias,
                                 10);
        menu.setLocalScale(menuScale);
        
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
		setEnabled(false);
		
		simpleApp.getStateManager().attach(new SingleGameState());
	}

	/**
	 * 退出游戏
	 */
	private void quitGame() {
		simpleApp.stop();
	}
}
