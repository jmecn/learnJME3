package teris.game.states;

import teris.game.Game;
import teris.game.scene.BoxGeometry;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

public class LogicStates extends AbstractAppState {

	private Game game;
	private AssetManager assetManager;

	// 矩阵的参数
	public final static int SIDE_X = 6;
	public final static int SIDE_Y = 16;
	public final static int SIDE_Z = 6;
	
	private int[][][] matrix = new int[SIDE_Y][SIDE_Z][SIDE_X];

	private boolean isChanged = false;

	// 7种方块的形状参数
	private String[] color = { "red", "blue", "orange", "cyan", "green", "purple", "yellow" };
	private static int[][] pattern = {
		{ 0x0f00, 0x4444, 0x0f00, 0x4444 }, // 长条型的四种状态
		{ 0x04e0, 0x0464, 0x00e4, 0x04c4 }, // 'T'型的四种状态
		{ 0x4620, 0x6c00, 0x4620, 0x6c00 }, // 'S'型的四种状态
		{ 0x2640, 0xc600, 0x2640, 0xc600 }, // 'Z'型的四种状态
		{ 0x6220, 0x1700, 0x2230, 0x0740 }, // 'L'型的四种状态
		{ 0x6440, 0x0e20, 0x44c0, 0x8e00 }, // 'J'型的四种状态
		{ 0x0660, 0x0660, 0x0660, 0x0660 }, // 'O'型的四种状态
	};
	
	// 当前方块参数
	private int blockType; // 方块类型 0-6
	private int turnState; // 方块状态 0-3
	private int posX; // 横坐标
	private int posY; // 纵坐标
	private int posZ; // 纵坐标
	private int blockState; // 下落状态

	// 下一个方块的参数
	private int nextBlockType; // 方块类型 0-6
	private int nextTurnState; // 方块状态 0-3
	
	// 游戏相关参数
	private int level; // 游戏级别 0-9
	private int score; // 游戏分数
	private float rate = 1f;// 方块下落速率 1秒
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		game = (Game) app;
		assetManager = game.getAssetManager();

		newGame();

		Node controlNode = game.getControlNode();
		BoxGeometry _0 = new BoxGeometry(assetManager, "yellow");
		BoxGeometry _1 = new BoxGeometry(assetManager, "yellow");
		BoxGeometry _2 = new BoxGeometry(assetManager, "yellow");
		BoxGeometry _3 = new BoxGeometry(assetManager, "yellow");
		controlNode.attachChild(_0);
		controlNode.attachChild(_1);
		controlNode.attachChild(_2);
		controlNode.attachChild(_3);
		_0.move(0, 0, 0);
		_1.move(1, 0, 0);
		_2.move(-1, 0, 0);
		_3.move(0, 0, 1);

		controlNode.move(0.5f, 13f, 0.5f);

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

			System.out.println("心跳");

			// 下面开始写逻辑

			if (!fallDown()) {// 方块下落
				deleteFullLine();// 尝试消除方块

				if (isGameEnd()) {
					setEnabled(false);
				} else {
					// 将当前控制节点与下一个方块交换
					getNextBlock();

					// 生成新的方块，并置于预览窗口中。
					createNewBlock();
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
		isChanged = true;
	}
	
	private void createNewBlock() {
		blockState = 1;
		posY = SIDE_Y - 1;
		posX = SIDE_X / 2 - 2;
		posZ = SIDE_Z / 2 - 2;
		
		blockType = nextBlockType;
		turnState = nextTurnState;
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
	 * 判断方块是否可以下落
	 * 
	 * @return
	 */
	private boolean fallDown() {
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
		return false;
	}
	
	/**
	 * 实现块落下的操作的方法
	 * 
	 * @return
	 */
	public boolean moveDown() {
		boolean result = false;
		if (blockState == 2)
			return result;

		dispBlock(0);
		if (assertValid(turnState, posX, posY - 1, posZ)) {
			posY--;
			result = true;
		} else {
			blockState = 2;
		}
		dispBlock(blockType + 1);
		return result;
	}

	/**
	 * 消除一行
	 * 
	 * @param line
	 */
	protected void newLine(int line) {
		// 清除一行
		matrix[posY][line] = new int[] { 0, 0, 0, 0, 0, 0};

		// 让屏幕上的方块下落
		for (int i = line; i > 0; i--) {
			matrix[posY][i] = matrix[posY][i - 1];
		}
		matrix[posY][0] = new int[] { 0, 0, 0, 0, 0, 0};
	}

	private void refresh() {
		if (isChanged) {
			isChanged = false;
			Node wellNode = game.getWellNode();
			for (int y = 0; y < SIDE_Y; y++) {
				for (int x = 0; x < SIDE_X; x++) {
					for (int z = 0; z < SIDE_Z; z++) {
						int index = matrix[y][z][x];
						if (index > 0) {
							Geometry box = new BoxGeometry(assetManager, color[index - 1]);
							box.setLocalTranslation(x - 2.5f, y, z - 2.5f);
							wellNode.attachChild(box);
						}
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
	 * 实现“块”翻转的方法
	 */
	public void leftTurn() {
		dispBlock(0);
		if (assertValid((turnState + 1) % 4, posX, posY, posZ)) {
			turnState = (turnState + 1) % 4;
		}
		dispBlock(blockType + 1);
	}

	/**
	 * 实现“块”的左移的方法
	 */
	public void leftMove() {
		dispBlock(0);
		if (assertValid(turnState, posX - 1, posY, posZ)) {
			posX--;
		}
		dispBlock(blockType + 1);
	}

	/**
	 * 实现块的右移
	 */
	public void rightMove() {
		System.out.println("right move");
		dispBlock(0);
		if (assertValid(turnState, posX + 1, posY, posZ)) {
			posX++;
		}
		dispBlock(blockType + 1);
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
					if (posZ + i < 0  || posZ + i >= SIDE_Z || posX + j < 0 || posX + j >= SIDE_X) {
						return false;
					}
					if (matrix[posY][posZ + i][posX + j] > 1)
						return false;
				}
				k = k >> 1;
			}
		}
		return result;
	}

	// 同步显示的方法
	public synchronized void dispBlock(int s) {
		int k = 0x8000;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (((int) pattern[blockType][turnState] & k) != 0) {
					matrix[posY][posZ + i][posX + j] = s;
				}
				k = k >> 1;
			}
		}
	}

}
