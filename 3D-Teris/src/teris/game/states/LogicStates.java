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
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class LogicStates extends AbstractAppState {

	private Game game;
	private AssetManager assetManager;
	
	// 用于控制方块移动、变形的控制器
	private MoveControl mc;
	private RotateControl rc;
	
	// 矩阵的参数
	public final static int SIDE_X = 6;
	public final static int SIDE_Y = 16;
	public final static int SIDE_Z = 6;

	// 矩阵节点(井)的参数
	private int[][][] matrix = new int[SIDE_Y][SIDE_Z][SIDE_X];
	private BoxGeometry[][][] wells = new BoxGeometry[SIDE_Y][SIDE_Z][SIDE_X];
	private boolean matrixChanged = false;

	// 受控节点的参数
	private int[][] shape = new int[4][4];
	private BoxGeometry[][] controls = new BoxGeometry[4][4];
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
	private boolean reachButton; // 下落状态

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
		game = (Game) app;
		assetManager = game.getAssetManager();

		mc = game.getControlNode().getControl(MoveControl.class);
		rc = game.getControlNode().getControl(RotateControl.class);
		
		for(int y=0; y<SIDE_Y; y++) {
			for(int x=0; x<SIDE_X; x++) {
				for(int z=0; z<SIDE_Z; z++) {
					wells[y][z][x] = new BoxGeometry(assetManager, 0);
					wells[y][z][x].setLocalTranslation(x - 2.5f, y, z - 2.5f);
				}
			}
		}
		
		for(int x=0; x<4; x++) {
			for(int y=0; y<4; y++) {
				controls[y][x] = new BoxGeometry(assetManager, 0);
				controls[y][x].setLocalTranslation(x - 1.5f, 0, y - 1.5f);
			}
		}
		
		newGame();
	}



	private float timeInSecond = 0f;// 当前已经过的时间
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
				
				// 
				game.getControlNode().setLocalTranslation(new Vector3f(0, SIDE_Y - 1, 0));
				game.getControlNode().setLocalRotation(new Quaternion());
				
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

		// 清空矩阵
		clear();
		
		game.getControlNode().setLocalTranslation(0, SIDE_Y-1, 0);

		// 生成方块
		getNextBlock();

		createNewBlock();
		
		getNextBlock();

		// 启动游戏
		setEnabled(true);
	}

	public void clear() {
		// 清空矩阵
		for(int y=0; y<SIDE_Y; y++) {
			for(int x=0; x<SIDE_X; x++) {
				for(int z=0; z<SIDE_Z; z++) {
					matrix[y][z][x] = 0;
				}
			}
		}
		matrixChanged = true;
		
		for(int x = 0; x<4; x++) {
			for(int y=0; y<4; y++) {
				shape[y][x] = 0;
			}
		}
		controlChanged = true;
	}
	
	private void createNewBlock() {
		reachButton = false;
		posY = SIDE_Y - 1;
		posX = SIDE_X / 2 - 2;
		posZ = SIDE_Z / 2 - 2;
		
		blockType = nextBlockType;
		turnState = nextTurnState;
		
		updateControlNode(blockType + 1);
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
			Node wellNode = game.getWellNode();
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
			Node controlNode = game.getControlNode();
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
		if (score / 1000 > level && level < 9) {
			level++;
		}
	}
	
	/**
	 * 方块顺时针旋转
	 */
	public void rotateRight() {
		if (assertValid((turnState + 1) % 4, posX, posY, posZ)) {
			if (!rc.isRotating()) {
				turnState = (turnState + 1) % 4;
				rc.rotate(true);
			}
		}
	}
	/**
	 * 方块逆时针旋转
	 */
	public void rotateLeft() {
		if (assertValid((turnState + 7) % 4, posX, posY, posZ)) {
			if (!rc.isRotating()) {
				turnState = (turnState + 7) % 4;
				game.getControlNode().getControl(RotateControl.class).rotate(false);
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
		if (reachButton)
			return result;

		if (assertValid(turnState, posX, posY - 1, posZ)) {
			posY--;
			result = true;
			mc.move(DIRECTION.DOWN);
		} else {
			reachButton = true;
		}
		return result;
	}
	
	/**
	 * 方块向北移动
	 */
	public void moveNorth() {
		if (assertValid(turnState, posX, posY, posZ-1)) {
			if (!mc.isMoving()) {
				posZ--;
				mc.move(DIRECTION.NORTH);
			}
		}
	}
	/**
	 * 方块向南移动
	 */
	public void moveSouth() {
		if (assertValid(turnState, posX, posY, posZ+1)) {
			if (!mc.isMoving()) {
				posZ++;
				mc.move(DIRECTION.SOUTH);
			}
		}
	}
	/**
	 * 方块向西移动
	 */
	public void moveWest() {
		if (assertValid(turnState, posX - 1, posY, posZ)) {
			if (!mc.isMoving()) {
				posX--;
				mc.move(DIRECTION.WEST);
			}
		}
	}

	/**
	 * 实现块的右移
	 */
	public void moveEast() {
		if (assertValid(turnState, posX + 1, posY, posZ)) {
			if (!mc.isMoving()) {
				posX++;
				mc.move(DIRECTION.EAST);
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
	 * @param s
	 */
	private void updateControlNode(int s) {
		assert s > 0;
		
		int k = 0x8000;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (((int) pattern[blockType][turnState] & k) != 0) {
					shape[i][j] = s;
				} else {
					shape[i][j] = 0;
				}
				k = k >> 1;
			}
		}
		
		controlChanged = true;
	}

}
