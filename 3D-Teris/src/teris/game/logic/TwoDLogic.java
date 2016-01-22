package teris.game.logic;

import java.util.ArrayList;
import java.util.Random;

/**
 * ��ͳ��2D����˹�����㷨�߼�
 * @author yanmaoyuan
 *
 */
public class TwoDLogic {
	// ����Ĳ���
	public final static int SIDE_X = 10;
	public final static int SIDE_Y = 20;
	private Random rand = new Random();

	// ����ڵ�(��)�Ĳ���
	private int[][] matrix = new int[SIDE_Y][SIDE_X];
	private boolean matrixChanged = false;

	// �ܿؽڵ�Ĳ���
	private int[][] control = new int[4][4];
	private boolean controlChanged = false;

	// Ԥ���ڵ�Ĳ���
	private int[][] preview = new int[4][4];
	private boolean previewChanged = false;

	// 7�ַ������״����
	private static int[][] pattern = { { 0x0f00, 0x2222, 0x00f0, 0x4444 }, // 'I'�͵�����״̬
			{ 0x0644, 0x0e20, 0x2260, 0x0470 }, // 'J'�͵�����״̬
			{ 0x0622, 0x02e0, 0x4460, 0x0740 }, // 'L'�͵�����״̬
			{ 0x0660, 0x0660, 0x0660, 0x0660 }, // 'O'�͵�����״̬
			{ 0x4620, 0x0360, 0x0462, 0x06c0 }, // 'S'�͵�����״̬
			{ 0x0270, 0x0464, 0x0e40, 0x2620 }, // 'T'�͵�����״̬
			{ 0x2640, 0x0630, 0x0264, 0x0c60 }, // 'Z'�͵�����״̬
	};

	// ��ǰ�������
	private int blockType; // �������� 0-6
	private int turnState; // ����״̬ 0-3
	private int posX; // ������
	private int posY; // ������

	// ��һ������Ĳ���
	private int nextBlockType; // �������� 0-6
	private int nextTurnState; // ����״̬ 0-3

	// ��Ϸ��ز���
	private int level; // ��Ϸ���� 0-9
	private int score; // ��Ϸ����
	private float rate;// ������������
	
	/**
	 * ��ʼ����Ϸ
	 */
	public void newGame() {

		// ��վ�������
		for (int y = 0; y < SIDE_Y; y++) {
			for (int x = 0; x < SIDE_X; x++) {
				matrix[y][x] = 0;
			}
		}
		matrixChanged = true;

		// ����ܿؽڵ���״����
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 4; y++) {
				control[y][x] = 0;
			}
		}
		controlChanged = true;

		// ��ʼ����Ϸ�ȼ������֡���������
		level = 0;
		score = 0;
		rate = 1f;

		// ��ʼ���ܿط����λ��
		createNewBlock();

		// ����Ԥ������
		getNextBlock();
	}

	/**
	 * ���ɵ�ǰ����
	 */
	public void createNewBlock() {
		posY = SIDE_Y - 1;
		posX = SIDE_X / 2 - 2;

		blockType = nextBlockType;
		turnState = nextTurnState;
		
		// ˢ�¿��ƽڵ�
		int k = 0x8000;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (((int) pattern[blockType][turnState] & k) != 0) {
					control[i][j] = blockType + 1;
				} else {
					control[i][j] = 0;
				}
				k = k >> 1;
			}
		}

		controlChanged = true;
	}

	/**
	 * ������һ������
	 */
	public void getNextBlock() {
		nextBlockType = rand.nextInt(7);
		nextTurnState = rand.nextInt(4);
		
		// ˢ��Ԥ���ڵ�
		int k = 0x8000;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (((int) pattern[nextBlockType][nextTurnState] & k) != 0) {
					preview[i][j] = nextBlockType + 1;
				} else {
					preview[i][j] = 0;
				}
				k = k >> 1;
			}
		}

		previewChanged = true;
	}

	/**
	 * �ж���Ϸ�Ƿ����
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
	 * ɾ����Ա����
	 */
	private  ArrayList<Integer> intList = new ArrayList<Integer>();
	public void deleteFullLine() {
		intList.clear();// ��ջ���
		int full_line_num = 0;
		
		// �ֱ���Y��ɨ�裬�����Ƿ��нڵ㱻����
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


		// �������÷�������
		if (full_line_num > 0) {
			for (Integer value : intList) {
				int x = value / 100;
				int y = value - x*100;

				// �ø÷����Ϸ������з�������
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
	 * ��¼��Щ�㱻�����ˡ�
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
	 * ���ӷ���
	 * 
	 * @param lineNum
	 *            ������������
	 */
	public void addScore(int lineNum) {
		score = score + lineNum * lineNum;
		// ÿ1000����һ�������9����
		if (score / 100 > level && level < 9) {
			level++;
			rate -= 0.1f;
		}
	}

	/**
	 * ����˳ʱ����ת
	 */
	public boolean rotateRight() {
		if (assertValid((turnState + 1) % 4, posX, posY)) {
			turnState = (turnState + 1) % 4;
			return true;
		} else {
			return false;
		}

		// Ҳ�����Լ�����ת��ǽ����wallkick
	}

	/**
	 * ������ʱ����ת
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
	 * ��������
	 * 
	 * @return
	 */
	public void quickDown() {
		while (moveDown());
	}

	/**
	 * ��������
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
	 * ���������ƶ�
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
	 * ʵ�ֿ������
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
	 * ��֤����λ����Ч��
	 * 
	 * @param turnState
	 * @param posX
	 * @param posY
	 * @return
	 */
	public boolean assertValid(int turnState, int posX, int posY) {
		boolean result = true;
		int k = 0x8000;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if ((int) (pattern[blockType][turnState] & k) != 0) {
					if (posY + i < 0 || posY + i >= SIDE_Y || posX + j < 0 || posX + j >= SIDE_X) {
						return false;
					}
					if (matrix[posY + i][posX + j] > 0)
						return false;
				}
				k = k >> 1;
			}
		}
		return result;
	}

	/**
	 * ˢ�¾���
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