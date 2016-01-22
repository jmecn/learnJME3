package teris.game.states;

import teris.game.Main;

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
	private final static String C_ROTATE_R = "rotate_control_right";// 受控节点右旋
	private final static String C_ROTATE_L = "rotate_control_left";// 受控节点左旋
	private final static String W_ROTATE_R = "rotate_well_right";// 井右旋
	private final static String W_ROTATE_L = "rotate_well_left";// 井左旋
	private final static String DEBUG = "debug";// 调试模式
	private final static String PAUSE = "pause";// 游戏暂停

	String[] keys = { MOVE_NORTH, MOVE_SOUTH, MOVE_EAST, MOVE_WEST, MOVE_DOWN,
			C_ROTATE_R, C_ROTATE_L, W_ROTATE_R, W_ROTATE_L, DEBUG, PAUSE };

	private Main game;
	private LogicStates logicState;
	private InputManager inputManager;

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		game = (Main) app;
		logicState = game.getStateManager().getState(LogicStates.class);
		
		// 初始化按键
		inputManager = game.getInputManager();
		inputManager.addMapping(MOVE_NORTH, new KeyTrigger(KeyInput.KEY_W), new KeyTrigger(KeyInput.KEY_UP));
		inputManager.addMapping(MOVE_SOUTH, new KeyTrigger(KeyInput.KEY_S), new KeyTrigger(KeyInput.KEY_DOWN));
		inputManager.addMapping(MOVE_EAST, new KeyTrigger(KeyInput.KEY_D), new KeyTrigger(KeyInput.KEY_RIGHT));
		inputManager.addMapping(MOVE_WEST, new KeyTrigger(KeyInput.KEY_A), new KeyTrigger(KeyInput.KEY_LEFT));
		inputManager.addMapping(MOVE_DOWN, new KeyTrigger(KeyInput.KEY_X));
		inputManager.addMapping(C_ROTATE_R, new KeyTrigger(KeyInput.KEY_E));
		inputManager.addMapping(C_ROTATE_L, new KeyTrigger(KeyInput.KEY_C));
		inputManager.addMapping(W_ROTATE_R, new KeyTrigger(KeyInput.KEY_Q));
		inputManager.addMapping(W_ROTATE_L, new KeyTrigger(KeyInput.KEY_Z));
		inputManager.addMapping(DEBUG, new KeyTrigger(KeyInput.KEY_F2));
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
				logicState.moveNorth();
				break;
			case MOVE_SOUTH:
				logicState.moveSouth();
				break;
			case MOVE_EAST:
				logicState.moveEast();
				break;
			case MOVE_WEST:
				logicState.moveWest();
				break;
			case MOVE_DOWN:
				logicState.quickDown();
				break;
			case C_ROTATE_R:
				logicState.rotateRight();
				break;
			case C_ROTATE_L:
				logicState.rotateLeft();
				break;
			case W_ROTATE_R:
				logicState.rotateWellRight();
				break;
			case W_ROTATE_L:
				logicState.rotateWellLeft();
				break;
			case DEBUG:
				logicState.showAxis();
				break;
			case PAUSE:
				logicState.pause();
				break;
			default:
				break;
			}
		}
	}

}
