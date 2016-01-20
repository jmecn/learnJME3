package teris.game.states;

import teris.game.Game;
import teris.game.control.RotateControl;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

/**
 * 输入处理状态类
 * 
 * @author yanmaoyuan
 * 
 */
public class InputStates extends AbstractAppState implements ActionListener {

	private final static String MOVE_NORTH = "north";// 方块向下移动
	private final static String MOVE_SOUTH = "south";// 方块向南移动
	private final static String MOVE_EAST = "east";// 方块向东移动
	private final static String MOVE_WEST = "west";// 方块向西移动
	private final static String MOVE_DOWN = "down";// 方块(快速)向下移动
	private final static String ROTATE_CONTROL_RIGHT = "rotate_control_right";// 右旋
	private final static String ROTATE_CONTROL_LEFT = "rotate_control_left";// 左旋
	private final static String ROTATE_WELL_RIGHT = "rotate_well_right";// 井右旋
	private final static String ROTATE_WELL_LEFT = "rotate_well_left";// 井左旋
	private final static String PAUSE = "pause";// 游戏暂停

	String[] keys = { MOVE_NORTH, MOVE_SOUTH, MOVE_EAST, MOVE_WEST, MOVE_DOWN,
			ROTATE_CONTROL_RIGHT, ROTATE_CONTROL_LEFT, ROTATE_WELL_RIGHT, ROTATE_WELL_LEFT, PAUSE };

	private Game game;
	private InputManager inputManager;

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		game = (Game) app;

		// 初始化按键
		inputManager = game.getInputManager();
		inputManager.addMapping(MOVE_NORTH, new KeyTrigger(KeyInput.KEY_W));
		inputManager.addMapping(MOVE_SOUTH, new KeyTrigger(KeyInput.KEY_S));
		inputManager.addMapping(MOVE_EAST, new KeyTrigger(KeyInput.KEY_D));
		inputManager.addMapping(MOVE_WEST, new KeyTrigger(KeyInput.KEY_A));
		inputManager.addMapping(MOVE_DOWN, new KeyTrigger(KeyInput.KEY_X));
		inputManager.addMapping(ROTATE_CONTROL_RIGHT, new KeyTrigger(KeyInput.KEY_E));
		inputManager.addMapping(ROTATE_CONTROL_LEFT, new KeyTrigger(KeyInput.KEY_Q));
		inputManager.addMapping(ROTATE_WELL_RIGHT, new KeyTrigger(KeyInput.KEY_Z));
		inputManager.addMapping(ROTATE_WELL_LEFT, new KeyTrigger(KeyInput.KEY_C));
		inputManager.addMapping(PAUSE, new KeyTrigger(KeyInput.KEY_P));

		inputManager.addListener(this, keys);
	}

	@Override
	public void cleanup() {
		super.cleanup();
		inputManager.removeListener(this);
		for (String key : keys) {
			inputManager.deleteMapping(key);
		}
	}

	// ActionListener()
	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		if (isPressed) {
			switch (name) {
			case MOVE_NORTH:
				break;
			case MOVE_SOUTH:
				break;
			case MOVE_EAST:
				break;
			case MOVE_WEST:
				break;
			case MOVE_DOWN:
				break;
			case ROTATE_CONTROL_RIGHT:
				game.getControlNode().getControl(RotateControl.class).rotate(true);
				break;
			case ROTATE_CONTROL_LEFT:
				game.getControlNode().getControl(RotateControl.class).rotate(false);
				break;
			case ROTATE_WELL_RIGHT:
				game.getWellNode().getControl(RotateControl.class).rotate(true);
				break;
			case ROTATE_WELL_LEFT:
				game.getWellNode().getControl(RotateControl.class).rotate(false);
				break;
			case PAUSE:
				break;
			default:
				break;
			}
		}
	}

}
