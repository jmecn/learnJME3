package teris.game.states;

import teris.game.Game;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.FastMath;

public class InputStates extends AbstractAppState {

	Game game;
	InputManager inputManager;
	
	private final static String MOVE_NORTH = "north";
	private final static String MOVE_SOUTH = "south";
	private final static String MOVE_EAST = "east";
	private final static String MOVE_WEST = "west";
	
	private final static String ROTATE_CAMERA = "rotate_camera";
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		game = (Game) app;
		inputManager = game.getInputManager();
		inputManager.addMapping(MOVE_NORTH, new KeyTrigger(KeyInput.KEY_W));
		inputManager.addMapping(MOVE_SOUTH, new KeyTrigger(KeyInput.KEY_S));
		inputManager.addMapping(MOVE_EAST, new KeyTrigger(KeyInput.KEY_D));
		inputManager.addMapping(MOVE_WEST, new KeyTrigger(KeyInput.KEY_A));
		
		inputManager.addMapping(ROTATE_CAMERA, new KeyTrigger(KeyInput.KEY_TAB));
		
		inputManager.addListener(listener, ROTATE_CAMERA);
		
		inputManager.addRawInputListener(rawListener);;
	}
	
	RawInputListener rawListener = new RawInputListener() {
		@Override
		public void beginInput() {
		}

		@Override
		public void endInput() {
		}

		@Override
		public void onJoyAxisEvent(JoyAxisEvent evt) {
		}

		@Override
		public void onJoyButtonEvent(JoyButtonEvent evt) {
		}

		@Override
		public void onMouseMotionEvent(MouseMotionEvent evt) {
		}

		@Override
		public void onMouseButtonEvent(MouseButtonEvent evt) {
		}

		@Override
		public void onKeyEvent(KeyInputEvent evt) {
			if (evt.getKeyCode() == KeyInput.KEY_TAB) {
				rotateCamera();
			}
		}

		@Override
		public void onTouchEvent(TouchEvent evt) {
		}
	};
	ActionListener listener = new ActionListener() {
		@Override
		public void onAction(String name, boolean isPressed, float tpf) {
			if (isPressed) {
				switch (name) {
				case ROTATE_CAMERA: 
					break;
				}
			}
			
		}
	};
	
	private void rotateCamera() {
		game.getWellNode().rotate(0, FastMath.PI/30, 0);
	}
	@Override
	public void cleanup() {
		super.cleanup();
		inputManager.deleteMapping(MOVE_NORTH);
		inputManager.deleteMapping(MOVE_SOUTH);
		inputManager.deleteMapping(MOVE_EAST);
		inputManager.deleteMapping(MOVE_WEST);
		
		inputManager.deleteMapping(ROTATE_CAMERA);
	}



	@Override
	public void update(float tpf) {
		// TODO Auto-generated method stub

	}

}
