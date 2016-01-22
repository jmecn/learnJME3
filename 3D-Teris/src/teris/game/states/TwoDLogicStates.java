package teris.game.states;

import teris.game.DIRECTION;
import teris.game.Main;
import teris.game.control.MoveControl;
import teris.game.control.RotateControl;
import teris.game.logic.TwoDLogic;
import teris.game.scene.BoxGeometry;
import static teris.game.logic.TwoDLogic.SIDE_X;
import static teris.game.logic.TwoDLogic.SIDE_Y;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Grid;
import com.jme3.shadow.DirectionalLightShadowRenderer;

/**
 * 2D俄罗斯方块
 * @author yanmaoyuan
 *
 */
public class TwoDLogicStates extends AbstractAppState {

	// 按键
	private final static String MOVE_R = "move_right";// 方块向东移动
	private final static String MOVE_L = "move_left";// 方块向西移动
	private final static String MOVE_DOWN = "move_down";// 方块(快速)向下移动
	private final static String ROTATE_R = "rotate_right";// 受控节点右旋
	private final static String ROTATE_L = "rotate_left";// 受控节点左旋
	private final static String PAUSE = "pause";// 游戏暂停

	private String[] keys = { MOVE_R, MOVE_L, MOVE_DOWN, ROTATE_R, ROTATE_L, PAUSE };
	
	private Main game;
	private AssetManager assetManager;
	private InputManager inputManager;
	
	private Node rootNode = new Node("logicRoot");
	private Node guiNode = new Node("logicGui");
	
	private Node wellNode;// 游戏中的“井”节点
	private Node controlNode;// 受控节点
	private Node previewNode;// 预览节点
	
	private Node axisNode;// Axis
	
	private BitmapText uiText;
	
	private MoveControl moveControl;// 用于控制方块移动的控制器
	private RotateControl rotateControl;// 用于控制方块旋转的控制器
	
	private TwoDLogic logic;

	// 矩阵节点(井)的参数
	private BoxGeometry[][] wells = null;

	// 受控节点的参数
	private BoxGeometry[][] controls = null;
	
	// 预览节点的参数
	private BoxGeometry[][] previews = null;

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		
		// 3D俄罗斯方块的核心逻辑类
		logic = new TwoDLogic();
		
		game = (Main) app;
		assetManager = game.getAssetManager();
		inputManager = game.getInputManager();
		
		// 初始化场景图
		game.getRootNode().attachChild(rootNode);
		rootNode.attachChild(getWellNode());
		rootNode.attachChild(getPreviewNode());
		
		game.getViewPort().setBackgroundColor(new ColorRGBA(0.3f, 0.4f, 0.5f, 1));
		
		// 初始化GUI
		game.getGuiNode().attachChild(guiNode);
		initGui();
		
		// 初始化摄像机
		Camera cam = game.getCamera();
		cam.setLocation(new Vector3f(0, 25, 10));
		cam.lookAt(new Vector3f(0, 8, 0), cam.getUp());
		
		// 初始化灯光
		initLight();
		
		// 初始化输入控制
		initKeys();
		
		// 开始游戏
		newGame();
	}
	
	/**
	 * 当前已经过的时间
	 */
	private float timeInSecond = 0f;
	
	/**
	 * 游戏主循环
	 */
	@Override
	public void update(float tpf) {
		if (!isEnabled()) {
			return;
		}

		timeInSecond += tpf;
		if (timeInSecond >= logic.getRate()) {
			timeInSecond -= logic.getRate();

			// 下面开始写逻辑
			
			if (!logic.moveDown()) {// 方块下落
				
				// 把受控节点添加到井中
				logic.addToWell();
				
				// 将受控节点复位
				resetControlNode();
				
				logic.deleteFullLine();// 尝试消除方块

				if (logic.isGameEnd()) {
					uiText.setText("Your Final Score: " + logic.getScore());
					setEnabled(false);
				} else {
					logic.createNewBlock();// 改变受控方块
					logic.getNextBlock();// 生成新的预览方块
					
					// 更新分数
					uiText.setText("Score: " + logic.getScore() + "\nLevel: " + logic.getLevel());
				}
			} else {
				// 方块正常下降
				moveControl.move(DIRECTION.DOWN);
			}

			// 刷新界面
			refresh();
		}
	}
	
	@Override
	public void cleanup() {
		// 移除场景图
		game.getRootNode().detachChild(rootNode);
		game.getGuiNode().detachChild(guiNode);
		game.getViewPort().setBackgroundColor(new ColorRGBA(0, 0, 0, 1));
		
		// 清空按键映射
		inputManager.removeListener(listener);
		for (String key : keys) {
			inputManager.deleteMapping(key);
		}
		
		super.cleanup();
	}
	
	/**
	 * 初始化GUI
	 */
	private void initGui() {
		BitmapFont fnt = game.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
		String txtA = "Score: 0\nLevel: 0";
		uiText = new BitmapText(fnt, false);
		uiText.setText(txtA);
		uiText.setLocalTranslation(0, 640, 0);
		
		guiNode.attachChild(uiText);
	}

	/**
	 * 防止多次初始化光照。
	 */
	private boolean lightInitialzed = false;
	/**
	 * 初始化光照
	 */
	private void initLight() {
		if (!lightInitialzed) {
			/**
			 * 创建一个垂直向下的方向光源，这道光将会产生阴影，这样就能预知方块下落的位置。
			 */
			DirectionalLight light = new DirectionalLight();
			ColorRGBA color = new ColorRGBA(0.5f, 0.5f, 0.5f, 1f);
			light.setColor(color);
			light.setDirection(new Vector3f(0, -1f, 0).normalizeLocal());
			rootNode.addLight(light);
			
			// 产生阴影
			DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(game.getAssetManager(), 1024, 4);
			dlsr.setLight(light);
			game.getViewPort().addProcessor(dlsr);
			rootNode.setShadowMode(ShadowMode.CastAndReceive);
			
			/**
			 * 再添加一个方向光源，让朝向摄像机的方位亮一点。
			 */
			light = new DirectionalLight();
			color = new ColorRGBA(0.5f, 0.5f, 0.5f, 1);
			light.setColor(color);
			light.setDirection(new Vector3f(0, -1, -1).normalize());
			rootNode.addLight(light);
			
			/**
			 * 再添加一个环境光，让游戏场景稍微亮一些。
			 */
			AmbientLight ambient = new AmbientLight();
			rootNode.addLight(ambient);
			
			lightInitialzed = true;
		}
	}
	
	/**
	 * 初始化输入控制
	 */
	private void initKeys() {
		// 初始化按键
		inputManager = game.getInputManager();
		inputManager.addMapping(MOVE_R, new KeyTrigger(KeyInput.KEY_D), new KeyTrigger(KeyInput.KEY_RIGHT));
		inputManager.addMapping(MOVE_L, new KeyTrigger(KeyInput.KEY_A), new KeyTrigger(KeyInput.KEY_LEFT));
		inputManager.addMapping(MOVE_DOWN, new KeyTrigger(KeyInput.KEY_X));
		inputManager.addMapping(ROTATE_R, new KeyTrigger(KeyInput.KEY_E));
		inputManager.addMapping(ROTATE_L, new KeyTrigger(KeyInput.KEY_C));
		inputManager.addMapping(PAUSE, new KeyTrigger(KeyInput.KEY_P));

		inputManager.addListener(listener, keys);
	}
	
	private ActionListener listener = new ActionListener() {

		@Override
		public void onAction(String name, boolean isPressed, float tpf) {
			if (isPressed) {
				switch (name) {
				case MOVE_R:
					move(DIRECTION.EAST);
					break;
				case MOVE_L:
					move(DIRECTION.WEST);
					break;
				case MOVE_DOWN:
					quickDown();
					break;
				case ROTATE_R:
					rotateRight();
					break;
				case ROTATE_L:
					rotateLeft();
					break;
				case PAUSE:
					/**
					 * 切换游戏的暂停/运行状态
					 */
					if (isEnabled()) {
						setEnabled(false);
					} else {
						setEnabled(true);
					}
					break;
				default:
					break;
				}
			}
			
		}
		
	};
	
	/**
	 * 获得“井”节点。若节点不存在则创建它。
	 * @return
	 */
	private Node getWellNode() {
		if (wellNode == null) {
			wellNode = new Node("well");
			
			// 添加旋转控制器
			wellNode.addControl(new RotateControl());
			
			// 将受控节点添加到"井"节点中，这样旋转"井"的时候受控节点也会一起旋转。
			wellNode.attachChild(getControlNode());
			
			wellNode.setShadowMode(ShadowMode.Receive);
			
			// 添加参考坐标系
			axisNode = getAxisNode();
			wellNode.attachChild(getAxisNode());
			
			// 初始化"井"节点中的方块数据结构
			if (wells == null) {
				wells = new BoxGeometry[SIDE_Y][SIDE_X];
				
				// 方块坐标的偏移量
				Vector3f postion = new Vector3f();
				Vector3f offset = new Vector3f(-SIDE_X/2+0.5f, 0.5f, -SIDE_Y/2+0.5f);
				
				for(int y=0; y<SIDE_Y; y++) {
					for(int x=0; x<SIDE_X; x++) {
							// 计算实际坐标
							postion.set(offset.add(x, y, 0));
							
							wells[y][x] = new BoxGeometry(assetManager, 0);
							wells[y][x].setLocalTranslation(postion);
					}
				}
			}
		}
		
		return wellNode;
	}
	
	private Node getControlNode() {
		if (controlNode == null) {
			
			controlNode = new Node("control");
			
			// 添加旋转控制器
			rotateControl = new RotateControl();
			controlNode.addControl(rotateControl);
			
			// 添加移动控制器
			moveControl = new MoveControl();
			controlNode.addControl(moveControl);
			
			// 受控节点只产生阴影，不接收阴影
			controlNode.setShadowMode(ShadowMode.Cast);
			
			// 初始化受控节点中的方块数据结构
			if (controls == null) {
				controls = new BoxGeometry[4][4];
				
				// 方块坐标的偏移量
				Vector3f postion = new Vector3f();
				Vector3f offset = new Vector3f(-2+0.5f, 0.5f, -2+0.5f);
							
				for(int x=0; x<4; x++) {
					for(int y=0; y<4; y++) {
						// 计算实际坐标
						postion.set(offset.add(x, 0, y));
						
						controls[y][x] = new BoxGeometry(assetManager, 0);
						controls[y][x].setLocalTranslation(postion);
					}
				}
			}
		}
		return controlNode;
	}
	private Node getPreviewNode() {
		if (previewNode == null) {
			previewNode = new Node("preview");
			previewNode.scale(0.5f);
			previewNode.rotate(FastMath.QUARTER_PI/3, 0, 0);
			previewNode.move(0, 0, 5);
			previewNode.setShadowMode(ShadowMode.Off);
			
			// 初始化预览节点中的方块数据结构
			if (previews == null) {
				previews = new BoxGeometry[4][4];
				
				// 方块坐标的偏移量
				Vector3f postion = new Vector3f();
				Vector3f offset = new Vector3f(-2+0.5f, 0.5f, -2+0.5f);
							
				for(int x=0; x<4; x++) {
					for(int y=0; y<4; y++) {
						// 计算实际坐标
						postion.set(offset.add(x, 0, y));
						
						previews[y][x] = new BoxGeometry(assetManager, 0);
						previews[y][x].setLocalTranslation(postion);
					}
				}
			}
		}
		
		return previewNode;
	}

	private Node getAxisNode() {
		if (axisNode == null) {
			axisNode = new Node("AxisNode");
			Geometry grid = new Geometry("Axis", new Grid(7, 7, 1f));
			
			AssetManager assetManager = game.getAssetManager();
			Material gm = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
			gm.setColor("Color", ColorRGBA.White);
			gm.getAdditionalRenderState().setWireframe(true);
			grid.setMaterial(gm);
			
			grid.center().move(0, 0, 0);

			axisNode.attachChild(grid);
		}

		return axisNode;
	}
	
	/**
	 * 复位受控节点的位置
	 */
	private void resetControlNode() {
		controlNode.setLocalTranslation(new Vector3f(0, SIDE_Y - 1, 0));
		controlNode.setLocalRotation(new Quaternion());
	}

	private void refresh() {
		// 矩阵发生了改变
		if (logic.isMatrixChanged()) {
			logic.setMatrixChanged(false);
			
			for (int y = 0; y < SIDE_Y; y++) {
				for (int x = 0; x < SIDE_X; x++) {
					int index = logic.getMatrix(x, y);
					if (index > 0) {
						wells[y][x].setColor(index - 1);
						wellNode.attachChild(wells[y][x]);
					} else {
						wellNode.detachChild(wells[y][x]);
					}
				}
			}
		}
		
		// 受控节点发生了改变
		if (logic.isControlChanged()) {
			logic.setControlChanged(false);
			
			for(int y=0; y<4; y++) {
				for(int x=0; x<4; x++) {
					int index = logic.getControl(x, y);
					if (index > 0) {
						controls[y][x].setColor(index - 1);
						controlNode.attachChild(controls[y][x]);
					} else {
						controlNode.detachChild(controls[y][x]);
					}
				}
			}
			
		}
		
		// 预览节点发生了改变
		if (logic.isPreviewChanged()) {
			logic.setPreviewChanged(false);
			
			for(int y=0; y<4; y++) {
				for(int x=0; x<4; x++) {
					int index = logic.getPreview(x, y);
					if (index > 0) {
						previews[y][x].setColor(index - 1);
						previewNode.attachChild(previews[y][x]);
					} else {
						previewNode.detachChild(previews[y][x]);
					}
				}
			}
			
		}
	}

	/**
	 * 开始新游戏
	 */
	public void newGame() {

		logic.newGame();
		
		
		// 初始化受控节点的位置
		resetControlNode();

		// 初始化受控方块的位置
		logic.createNewBlock();
		
		// 生成预览方块
		logic.getNextBlock();
		
		// 启动游戏
		setEnabled(true);
	}

	/**
	 * 方块顺时针旋转
	 */
	private void rotateRight() {
		if (!rotateControl.isRotating() && logic.rotateRight()) {
			rotateControl.rotate(true);
		}
		// 也许可以加入旋转踢墙功能wallkick
	}
	/**
	 * 方块逆时针旋转
	 */
	private void rotateLeft() {
		if (!rotateControl.isRotating() && logic.rotateLeft()) {
			rotateControl.rotate(false);
		}
	}

	/**
	 * 快速下落
	 * @return
	 */
	private void quickDown() {
		while(logic.moveDown());
	}
	
	/**
	 * 平移方块
	 * 
	 * @param dir
	 */
	private void move(DIRECTION dir) {
		// 方块正在移动中，驳回移动请求。
		if (moveControl.isMoving()) {
			return;
		}

		int direction = dir.getValue();
		
		// 如果开启此功能，就会根据井旋转的角度，计算正确的东西南北方向。
		{
			direction += wellNode.getControl(RotateControl.class).getOffset();
	
			while (direction < 0) {
				direction += 4;
			}
			if (direction > 3) {
				direction %= 4;
			}
		}

		switch (direction) {
		case 0:
			break;
		case 1:
			if (logic.moveLeft()) {
				// 方块向西移动
				moveControl.move(DIRECTION.WEST);
			}
			break;
		case 2:
			break;
		case 3:
			if (logic.moveRight()) {
				// 方块向东移动
				moveControl.move(DIRECTION.EAST);
			}
			break;
		}
	}
}
