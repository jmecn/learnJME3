package teris.game.logic;

import java.util.ArrayList;
import java.util.Random;

/**
 * 传统的2D俄罗斯方块算法逻辑
 * @author yanmaoyuan
 *
 */
public class TwoDLogic {
	// 矩阵的参数
	public final static int SIDE_X = 10;
	public final static int SIDE_Y = 20;
	private Random rand = new Random();

	// 矩阵节点(井)的参数
	private int[][] matrix = new int[SIDE_Y][SIDE_X];
	private boolean matrixChanged = false;

	// 受控节点的参数
	private int[][] control = new int[4][4];
	private boolean controlChanged = false;

	// 预览节点的参数
	private int[][] preview = new int[4][4];
	private boolean previewChanged = false;

	// 7种方块的形状参数
	private static int[][] pattern = { { 0x0f00, 0x2222, 0x00f0, 0x4444 }, // 'I'型的四种状态
			{ 0x0644, 0x0e20, 0x2260, 0x0470 }, // 'J'型的四种状态
			{ 0x0622, 0x02e0, 0x4460, 0x0740 }, // 'L'型的四种状态
			{ 0x0660, 0x0660, 0x0660, 0x0660 }, // 'O'型的四种状态
			{ 0x4620, 0x0360, 0x0462, 0x06c0 }, // 'S'型的四种状态
			{ 0x0270, 0x0464, 0x0e40, 0x2620 }, // 'T'型的四种状态
			{ 0x2640, 0x0630, 0x0264, 0x0c60 }, // 'Z'型的四种状态
	};

	// 当前方块参数
	private int blockType; // 方块类型 0-6
	private int turnState; // 方块状态 0-3
	private int posX; // 横坐标
	private int posY; // 纵坐标

	// 下一个方块的参数
	private int nextBlockType; // 方块类型 0-6
	private int nextTurnState; // 方块状态 0-3

	// 游戏相关参数
	private int level; // 游戏级别 0-9
	private int score; // 游戏分数
	private float rate;// 方块下落速率
	
	/**
	 * 开始新游戏
	 */
	public void newGame() {

		// 清空矩阵数据
		for (int y = 0; y < SIDE_Y; y++) {
			for (int x = 0; x < SIDE_X; x++) {
				matrix[y][x] = 0;
			}
		}
		matrixChanged = true;

		// 清空受控节点形状数据
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 4; y++) {
				control[y][x] = 0;
			}
		}
		controlChanged = true;

		// 初始化游戏等级、积分、下落速率
		level = 0;
		score = 0;
		rate = 1f;

		// 初始化受控方块的位置
		createNewBlock();

		// 生成预览方块
		getNextBlock();
	}

	/**
	 * 生成当前方块
	 */
	public void createNewBlock() {
		posY = SIDE_Y - 1;
		posX = SIDE_X / 2 - 2;

		blockType = nextBlockType;
		turnState = nextTurnState;
		
		// 刷新控制节点
		int k = 0x8000;
		for (int y = 0; y < 4; y++) {
			for (int z = 0; z < 4; z++) {
				if (((int) pattern[blockType][turnState] & k) != 0) {
					control[y][z] = blockType + 1;
				} else {
					control[y][z] = 0;
				}
				k = k >> 1;
			}
		}

		controlChanged = true;
	}

	/**
	 * 生成下一个方块
	 */
	public void getNextBlock() {
		nextBlockType = rand.nextInt(7);
		nextTurnState = rand.nextInt(4);
		
		// 刷新预览节点
		int k = 0x8000;
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				if (((int) pattern[nextBlockType][nextTurnState] & k) != 0) {
					preview[y][x] = nextBlockType + 1;
				} else {
					preview[y][x] = 0;
				}
				k = k >> 1;
			}
		}

		previewChanged = true;
	}

	/**
	 * 判断游戏是否结束
	 * @return
	 */
	public boolean isGameEnd() {
		boolean result = false;
		for (int col = 0; col < SIDE_X; col++) {
			if (matrix[SIDE_Y - 1][col] != 0) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * 删除满员的行
	 */
	private  ArrayList<Integer> intList = new ArrayList<Integer>();
	public void deleteFullLine() {
		intList.clear();// 清空缓存
		int full_line_num = 0;
		
		System.out.println(posY);
		// 分别延Y轴扫描，看看是否有节点被消除
		for (int y = posY; y < SIDE_Y; y++) {
			boolean isfull = true;
			for (int x = 0; x < SIDE_X; x++) {
				if (matrix[y][x] == 0) {
					isfull = false;
					continue;
				}
			}
			if (isfull) {
				for (int x = 0; x < SIDE_X; x++) {
					recordIt(x, posY, y);
				}
				full_line_num++;
			}
		}


		// 消除并让方块下落
		if (full_line_num > 0) {
			for (Integer value : intList) {
				int x = value / 100;
				int y = value - x*100;

				// 让该方块上方的所有方块下落
				for (int i = y; i < SIDE_Y - 1; i++) {
					matrix[i][x] = matrix[i + 1][x];
				}
				matrix[SIDE_Y - 1][x] = 0;
			}

			matrixChanged = true;
		}
		addScore(full_line_num);
	}

	/**
	 * 记录那些点被消除了。
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	private void recordIt(int x, int y, int z) {
		int hashCode = x * 1000 + y * 10 + z;
		if (!intList.contains(hashCode)) {
			intList.add(hashCode);
		}
	}

	/**
	 * 增加分数
	 * 
	 * @param lineNum
	 *            被消除的行数
	 */
	public void addScore(int lineNum) {
		score = score + lineNum * lineNum;
		// 每1000分升一级，最高9级。
		if (score / 100 > level && level < 9) {
			level++;
			rate -= 0.1f;
		}
	}

	/**
	 * 方块顺时针旋转
	 */
	public boolean rotateRight() {
		if (assertValid((turnState + 1) % 4, posX, posY)) {
			turnState = (turnState + 1) % 4;
			return true;
		} else {
			return false;
		}

		// 也许可以加入旋转踢墙功能wallkick
	}

	/**
	 * 方块逆时针旋转
	 */
	public boolean rotateLeft() {
		if (assertValid((turnState + 7) % 4, posX, posY)) {
			turnState = (turnState + 7) % 4;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 快速下落
	 * 
	 * @return
	 */
	public void quickDown() {
		while (moveDown());
	}

	/**
	 * 方块下落
	 * 
	 * @return
	 */
	public boolean moveDown() {
		boolean result = false;

		if (assertValid(turnState, posX, posY - 1)) {
			posY--;
			result = true;
		}
		return result;
	}

	/**
	 * 方块向西移动
	 */
	public boolean moveLeft() {
		if (assertValid(turnState, posX - 1, posY)) {
			posX--;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 实现块的右移
	 */
	public boolean moveRight() {
		if (assertValid(turnState, posX + 1, posY)) {
			posX++;
			return true;
		} else {
			return false;
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
	public boolean assertValid(int turnState, int posX, int posY) {
		System.out.println("AssetValid: x=" + posX + " , y=" + posY);
		boolean result = true;
		int k = 0x8000;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if ((int) (pattern[blockType][turnState] & k) != 0) {
					if (posY - i < 0 || posY - i >= SIDE_Y || posX + j < 0 || posX + j >= SIDE_X) {
						return false;
					}
					if (matrix[posY - i][posX + j] > 0)
						return false;
				}
				k = k >> 1;
			}
		}
		return result;
	}

	/**
	 * 刷新矩阵。
	 * 
	 * @param s
	 */
	public void addToWell() {
		int k = 0x8000;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (((int) pattern[blockType][turnState] & k) != 0) {
					matrix[posY + i][posX + j] = blockType + 1;
				}
				k = k >> 1;
			}
		}

		matrixChanged = true;
	}

	/**
	 * Getters & Setters
	 */
	
	public boolean isMatrixChanged() {
		return matrixChanged;
	}

	public boolean isControlChanged() {
		return controlChanged;
	}

	public boolean isPreviewChanged() {
		return previewChanged;
	}

	public void setMatrixChanged(boolean matrixChanged) {
		this.matrixChanged = matrixChanged;
	}

	public void setControlChanged(boolean controlChanged) {
		this.controlChanged = controlChanged;
	}

	public void setPreviewChanged(boolean previewChanged) {
		this.previewChanged = previewChanged;
	}

	public int getLevel() {
		return level;
	}

	public int getScore() {
		return score;
	}

	public float getRate() {
		return rate;
	}
	
	public int getMatrix(int x, int y) {
		return matrix[y][x];
	}
	
	public int getPreview(int x, int y) {
		return preview[y][x];
	}
	
	public int getPreviewType() {
		return nextBlockType;
	}
	
	public int getPreviewPattern() {
		return pattern[nextBlockType][nextTurnState];
	}
	
	public int getControl(int x, int y) {
		return control[y][x];
	}
	
	public int getControlType() {
		return blockType;
	}
	
	public int getControlPattern() {
		return pattern[blockType][turnState];
	}
}
