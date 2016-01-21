package teris.game.states;

import teris.game.Game;
import teris.game.control.MoveControl;
import teris.game.control.MoveControl.DIRECTION;
import teris.game.control.RotateControl;
import teris.game.scene.BoxGeometry;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.debug.Grid;

public class LogicStates extends AbstractAppState {

	private Game game;
	
	private Node scene;// 游戏场景
	private Node wellNode;// 游戏中的“井”节点
	private Node controlNode;// 受控节点
	private Node axisNode;// Axis
	
	private MoveControl moveControl;// 用于控制方块移动的控制器
	private RotateControl rotateControl;// 用于控制方块旋转的控制器
	
	// 矩阵的参数
	public final static int SIDE_X = 6;
	public final static int SIDE_Y = 16;
	public final static int SIDE_Z = 6;

	// 矩阵节点(井)的参数
	private int[][][] matrix = new int[SIDE_Y][SIDE_Z][SIDE_X];
	private BoxGeometry[][][] wells = null;
	private boolean matrixChanged = false;

	// 受控节点的参数
	private int[][] shape = new int[4][4];
	private BoxGeometry[][] controls = null;
	private boolean controlChanged = false;

	// 7种方块的形状参数
	private static int[][] pattern = {
		{ 0x0f00, 0x2222, 0x00f0, 0x4444 }, // 长条型的四种状态
		{ 0x0270, 0x0464, 0x0e40, 0x2620 }, // 'T'型的四种状态
		{ 0x4620, 0x0360, 0x0462, 0x06c0 }, // 'S'型的四种状态
		{ 0x2640, 0x0630, 0x0264, 0x0c60 }, // 'Z'型的四种状态
		{ 0x0622, 0x02e0, 0x4460, 0x0740 }, // 'L'型的四种状态
		{ 0x0644, 0x0e20, 0x2260, 0x0470 }, // 'J'型的四种状态
		{ 0x0660, 0x0660, 0x0660, 0x0660 }, // 'O'型的四种状态
	};
	
	// 当前方块参数
	private int blockType; // 方块类型 0-6
	private int turnState; // 方块状态 0-3
	private int posX; // 横坐标
	private int posY; // 纵坐标
	private int posZ; // 纵坐标
	private boolean reachBottom; // 下落状态

	// 下一个方块的参数
	private int nextBlockType; // 方块类型 0-6
	private int nextTurnState; // 方块状态 0-3
	
	// 游戏相关参数
	private int level; // 游戏级别 0-9
	private int score; // 游戏分数
	private float rate = 0.5f;// 方块下落速率 1秒
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		AssetManager assetManager = app.getAssetManager();
		
		// 初始化游戏场景
		game = (Game) app;
		game.getRootNode().attachChild(getScene());
	
		// 初始化数据结构
		if (wells == null) {
			wells = new BoxGeometry[SIDE_Y][SIDE_Z][SIDE_X];
			
			// 方块坐标的偏移量
			Vector3f postion = new Vector3f();
			Vector3f offset = new Vector3f(-SIDE_X/2+0.5f, 0.5f, -SIDE_Z/2+0.5f);
			
			for(int y=0; y<SIDE_Y; y++) {
				for(int x=0; x<SIDE_X; x++) {
					for(int z=0; z<SIDE_Z; z++) {
						// 计算实际坐标
						postion.set(offset.add(x, y, z));
						
						wells[y][z][x] = new BoxGeometry(assetManager, 0);
						wells[y][z][x].setLocalTranslation(postion);
					}
				}
			}
		}
		
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
		
		newGame();
	}
	
	private Node getScene() {
		if (scene == null) {
			scene = new Node("scene");
			scene.attachChild(getWellNode());
			
			// 添加一个方向光源
			DirectionalLight light = new DirectionalLight();
			ColorRGBA color = new ColorRGBA(1, 1, 1, 1);
			color.mult(0.3f);
			light.setColor(color);
			light.setDirection(new Vector3f(-1, -1, -1).normalize());
			scene.addLight(light);
		}
		return scene;
	}
	
	private Node getWellNode() {
		if (wellNode == null) {
			wellNode = new Node("well");
			
			// 添加旋转控制器
			wellNode.addControl(new RotateControl(FastMath.QUARTER_PI));
			
			// 将受控节点添加到"井"节点中，这样旋转"井"的时候受控节点也会一起旋转。
			wellNode.attachChild(getControlNode());
			
			wellNode.setShadowMode(ShadowMode.Receive);
			
			// 添加参考坐标系
			axisNode = showNodeAxies(50f);
			wellNode.attachChild(axisNode);
		}
		
		return wellNode;
	}
	private Node getControlNode() {
		if (controlNode == null) {
			
			controlNode = new Node("controll");
			
			// 添加旋转控制器
			rotateControl = new RotateControl();
			controlNode.addControl(rotateControl);
			
			// 添加移动控制器
			moveControl = new MoveControl();
			controlNode.addControl(moveControl);
			
			// 受控节点只产生阴影，不接收阴影
			controlNode.setShadowMode(ShadowMode.Cast);
		}
		return controlNode;
	}

	private Node showNodeAxies(float axisLen) {
		AssetManager assetManager = game.getAssetManager();
		
		Node rootNode = new Node("AxisNode");
		Geometry grid = new Geometry("Axis_b", new Grid(7, 7, 1f));
		Material gm = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		gm.setColor("Color", ColorRGBA.White);
		gm.getAdditionalRenderState().setWireframe(true);
		grid.setMaterial(gm);
		grid.center().move(0, 0, 0);

		rootNode.attachChild(grid);
		
		//
		Vector3f v = new Vector3f(axisLen, 0, 0);
		Arrow a = new Arrow(v);
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Red);
		Geometry geom = new Geometry(rootNode.getName() + "XAxis", a);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);

		//
		v = new Vector3f(0, axisLen, 0);
		a = new Arrow(v);
		mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Green);
		geom = new Geometry(rootNode.getName() + "YAxis", a);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);

		//
		v = new Vector3f(0, 0, axisLen);
		a = new Arrow(v);
		mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom = new Geometry(rootNode.getName() + "ZAxis", a);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);

		return rootNode;
	}
	
	/**
	 * 当前已经过的时间
	 */
	private float timeInSecond = 0f;
	@Override
	public void update(float tpf) {
		if (!isEnabled()) {
			return;
		}

		timeInSecond += tpf;
		if (timeInSecond >= rate) {
			timeInSecond -= rate;

			// 下面开始写逻辑
			
			if (!moveDown()) {// 方块下落
				
				// 把受控节点添加到井中
				addToWell();
				
				// 将受控节点复位
				resetControlNode();
				
				deleteFullLine();// 尝试消除方块

				if (isGameEnd()) {
					setEnabled(false);
				} else {
					// 将当前控制节点与下一个方块交换
					createNewBlock();

					// 生成新的方块，并置于预览窗口中。
					getNextBlock();
				}
			}

			// 刷新界面
			refresh();
		}
	}

	/**
	 * 开始新游戏
	 */
	private void newGame() {

		// 清空矩阵数据
		for(int y=0; y<SIDE_Y; y++) {
			for(int x=0; x<SIDE_X; x++) {
				for(int z=0; z<SIDE_Z; z++) {
					matrix[y][z][x] = 0;
				}
			}
		}
		matrixChanged = true;
		
		// 清空受控节点形状数据
		for(int x = 0; x<4; x++) {
			for(int y=0; y<4; y++) {
				shape[y][x] = 0;
			}
		}
		controlChanged = true;
		
		
		// 初始化游戏等级、积分、下落速率
		level = 0;
		score = 0;
		rate = 1f;
		
		// 初始化受控节点的位置
		resetControlNode();

		// 初始化受控方块的位置
		reachBottom = false;
		posY = SIDE_Y - 1;
		posX = SIDE_X / 2 - 2;
		posZ = SIDE_Z / 2 - 2;
		
		// 生成当前受控方块
		blockType = FastMath.rand.nextInt(7);
		turnState = FastMath.rand.nextInt(4);
		updateControl();// 刷新受控方块

		// 生成预览方块
		nextBlockType = FastMath.rand.nextInt(7);
		nextTurnState = FastMath.rand.nextInt(4);
		
		// 启动游戏
		setEnabled(true);
	}

	private void createNewBlock() {
		reachBottom = false;
		posY = SIDE_Y - 1;
		posX = SIDE_X / 2 - 2;
		posZ = SIDE_Z / 2 - 2;
		
		blockType = nextBlockType;
		turnState = nextTurnState;
		
		updateControl();
	}

	private void getNextBlock() {
		nextBlockType = FastMath.rand.nextInt(7);
		nextTurnState = FastMath.rand.nextInt(4);
	}

	private boolean isGameEnd() {
		boolean result = false;

		for (int col = 0; col < SIDE_X; col++) {
			for(int row = 0; row<SIDE_Z; row++) {
				if (matrix[SIDE_Y-1][row][col] != 0) {
					result = true;
				}
			}
		}

		return result;
	}

	private void deleteFullLine() {
		int full_line_num = 0;
		for (int i = 0; i < SIDE_Z; i++) {
			boolean isfull = true;

			for (int j = 0; j < SIDE_X; j++) {
				if (matrix[posY][i][j] == 0) {
					isfull = false;
					continue;
				}
			}
			if (isfull) {
				newLine(i);// 消除一行
				full_line_num++;
			}
		}
		addScore(full_line_num);
	}

	/**
	 * 消除一行
	 * 
	 * @param line
	 */
	protected void newLine(int line) {
		// 清除一行
		matrix[posY][line] = new int[] { 0, 0, 0, 0, 0, 0};

//		// 让屏幕上的方块下落
//		for (int i = line; i > 0; i--) {
//			matrix[posY][i] = matrix[posY][i - 1];
//		}
//		matrix[posY][0] = new int[] { 0, 0, 0, 0, 0, 0};
		
		matrixChanged = true;
	}

	private void refresh() {
		// 矩阵发生了改变
		if (matrixChanged) {
			matrixChanged = false;
			
			for (int y = 0; y < SIDE_Y; y++) {
				for (int x = 0; x < SIDE_X; x++) {
					for (int z = 0; z < SIDE_Z; z++) {
						int index = matrix[y][z][x];
						if (index > 0) {
							wells[y][z][x].setColor(index - 1);
							wellNode.attachChild(wells[y][z][x]);
						} else {
							wellNode.detachChild(wells[y][z][x]);
						}
					}
				}
			}
		}
		
		// 受控节点发生了改变
		if (controlChanged) {
			controlChanged = false;
			
			for(int y=0; y<4; y++) {
				for(int x=0; x<4; x++) {
					int index = shape[y][x];
					if (index > 0) {
						controls[y][x].setColor(index - 1);
						controlNode.attachChild(controls[y][x]);
					} else {
						controlNode.detachChild(controls[y][x]);
					}
				}
			}
			
		}
	}

	/**
	 * 增加分数
	 * 
	 * @param lineNum
	 *            被消除的行数
	 */
	public void addScore(int lineNum) {
		score = score + (level + 1) * lineNum;
		// 每1000分升一级，最高9级。
		if (score / 100 > level && level < 9) {
			level++;
		}
	}
	
	public void rotateWellRight() {
		wellNode.getControl(RotateControl.class).rotate(true);
	}
	
	public void rotateWellLeft() {
		wellNode.getControl(RotateControl.class).rotate(false);
	}
	/**
	 * 方块顺时针旋转
	 */
	public void rotateRight() {
		if (assertValid((turnState + 1) % 4, posX, posY, posZ)) {
			if (!rotateControl.isRotating()) {
				turnState = (turnState + 1) % 4;
				rotateControl.rotate(true);
			}
		}
	}
	/**
	 * 方块逆时针旋转
	 */
	public void rotateLeft() {
		if (assertValid((turnState + 7) % 4, posX, posY, posZ)) {
			if (!rotateControl.isRotating()) {
				turnState = (turnState + 7) % 4;
				rotateControl.rotate(false);
			}
		}
	}

	/**
	 * 方块下落
	 * 
	 * @return
	 */
	public boolean moveDown() {
		boolean result = false;
		if (reachBottom)
			return result;

		if (assertValid(turnState, posX, posY - 1, posZ)) {
			posY--;
			result = true;
			moveControl.move(DIRECTION.DOWN);
		} else {
			reachBottom = true;
		}
		return result;
	}
	
	/**
	 * 方块向北移动
	 */
	public void moveNorth() {
		if (assertValid(turnState, posX, posY, posZ-1)) {
			if (!moveControl.isMoving()) {
				posZ--;
				moveControl.move(DIRECTION.NORTH);
			}
		}
	}
	/**
	 * 方块向南移动
	 */
	public void moveSouth() {
		if (assertValid(turnState, posX, posY, posZ+1)) {
			if (!moveControl.isMoving()) {
				posZ++;
				moveControl.move(DIRECTION.SOUTH);
			}
		}
	}
	/**
	 * 方块向西移动
	 */
	public void moveWest() {
		if (assertValid(turnState, posX - 1, posY, posZ)) {
			if (!moveControl.isMoving()) {
				posX--;
				moveControl.move(DIRECTION.WEST);
			}
		}
	}

	/**
	 * 实现块的右移
	 */
	public void moveEast() {
		if (assertValid(turnState, posX + 1, posY, posZ)) {
			if (!moveControl.isMoving()) {
				posX++;
				moveControl.move(DIRECTION.EAST);
			}
		}
	}

	/**
	 * 验证方块位置有效性
	 * 
	 * @param turnState
	 * @param posX
	 * @param posY
	 * @return
	 */
	protected boolean assertValid(int turnState, int posX, int posY, int posZ) {
		boolean result = true;
		int k = 0x8000;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if ((int) (pattern[blockType][turnState] & k) != 0) {
					if (posY < 0 || posY >= SIDE_Y || posZ + i < 0  || posZ + i >= SIDE_Z || posX + j < 0 || posX + j >= SIDE_X) {
						return false;
					}
					if (matrix[posY][posZ + i][posX + j] > 0)
						return false;
				}
				k = k >> 1;
			}
		}
		return result;
	}

	/**
	 * 刷新矩阵。
	 * @param s
	 */
	private void addToWell() {
		int k = 0x8000;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (((int) pattern[blockType][turnState] & k) != 0) {
					matrix[posY][posZ + i][posX + j] = blockType + 1;
				}
				k = k >> 1;
			}
		}
		
		matrixChanged = true;
	}
	
	/**
	 * 刷新控制节点
	 */
	private void updateControl() {
		int k = 0x8000;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (((int) pattern[blockType][turnState] & k) != 0) {
					shape[i][j] = blockType + 1;
				} else {
					shape[i][j] = 0;
				}
				k = k >> 1;
			}
		}
		
		controlChanged = true;
	}
	
	/**
	 * 复位受控节点的位置
	 */
	private void resetControlNode() {
		controlNode.setLocalTranslation(new Vector3f(0, SIDE_Y - 1, 0));
		controlNode.setLocalRotation(new Quaternion());
	}

	/**
	 * 显示参考坐标系
	 */
	public void showAxis() {
		if (wellNode.hasChild(axisNode)) {
			wellNode.detachChild(axisNode);
		} else {
			wellNode.attachChild(axisNode);
		}
	}
	/**
	 * 切换游戏的暂停/运行状态
	 */
	public void pause() {
		if (isEnabled()) {
			setEnabled(false);
		} else {
			setEnabled(true);
		}
	}

}
