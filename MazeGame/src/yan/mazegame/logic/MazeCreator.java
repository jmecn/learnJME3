package yan.mazegame.logic;

import java.util.Random;

/**
 * 迷宫生成器
 * @author yan
 *
 */
public class MazeCreator {
	private final static int ROWS = 20;// 默认行数
	private final static int COLS = 30;// 默认列数
	
	private final static int DIRECTION_NUM = 4;
	private final static int DIRECTION_EAST = 0;
	private final static int DIRECTION_SOUTH = 1;
	private final static int DIRECTION_WEST = 2;
	private final static int DIRECTION_NORTH = 3;

	private int[] cells;// 标识每个格子是否连通
	private Cell[][] maze;// 迷宫

	private int rows;
	private int cols;
	private int cellCnt;
	private Random rand;

	/**
	 * 使用默认参数构造对象
	 */
	public MazeCreator() {
		this(ROWS, COLS, 0, false);
	}

	/**
	 * 构造一个迷宫生成器
	 * @param rows 行数
	 * @param cols 列数
	 * @param seed 随机数种子
	 * @param rand 是否使用种子来生成迷宫，false为完全随机，true则根据种子来生成。
	 */
	public MazeCreator(int rows, int cols, long seed, boolean rand) {
		if (rand) {
			this.rand = new Random();
		} else {
			this.rand = new Random(1000);
		}
		
		this.rows = rows;
		this.cols = cols;

		this.maze = new Cell[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				maze[i][j] = new Cell();
			}
		}
		maze[0][0].door[DIRECTION_WEST] = true;// 起点
		maze[rows - 1][cols - 1].door[DIRECTION_EAST] = true;// 终点

		this.cellCnt = cols * rows;
		this.cells = new int[cellCnt];
		for (int i = 0; i < cellCnt; i++) {
			cells[i] = -1;
		}
	}
	
	public void config(int rows, int cols, long seed, boolean rand) {
		if (rand) {
			this.rand = new Random();
		} else {
			this.rand = new Random(seed);
		}
		
		this.rows = rows;
		this.cols = cols;

		this.maze = new Cell[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				maze[i][j] = new Cell();
			}
		}
		maze[0][0].door[DIRECTION_WEST] = true;// 起点
		maze[rows - 1][cols - 1].door[DIRECTION_EAST] = true;// 终点

		this.cellCnt = cols * rows;
		this.cells = new int[cellCnt];
		for (int i = 0; i < cellCnt; i++) {
			cells[i] = -1;
		}
	}
	
	public void setRandomSeed(long seed) {
		this.rand.setSeed(seed);
	}

	public int getRowCount() {
		return rows;
	}
	
	public int getColCount() {
		return cols;
	}

	public Cell[][] getMaze() {
		return maze;
	}
	/**
	 * 创建迷宫
	 */
	public void create() {
		int direction = 0;
		int c1 = 0;
		int c2 = 0;
		// 随机选择一面墙
		while (true) {
			c1 = rand.nextInt(cellCnt);
			direction = rand.nextInt(DIRECTION_NUM);
			switch (direction) {
			case DIRECTION_EAST:
				if (c1 % cols == cols - 1)
					c2 = -1;
				else
					c2 = c1 + 1;
				break;
			case DIRECTION_SOUTH:
				if ((rows - 1) == (c1 - c1 % cols) / cols)
					c2 = -1;
				else
					c2 = c1 + cols;
				break;
			case DIRECTION_WEST:
				if (c1 % cols == 0)
					c2 = -1;
				else
					c2 = c1 - 1;
				break;
			case DIRECTION_NORTH:
				if (0 == (c1 - c1 % cols) / cols)
					c2 = -1;
				else
					c2 = c1 - cols;
				break;
			default:
				System.exit(0);
				break;
			}
			if (c2 < 0)
				continue;
			// 判断随机挑选的两个相邻房间是否连通。
			if (is_Connect(c1, c2))
				continue;
			else {
				// 移除房间之间的墙壁
				union_Cells(c1, c2);
				// 更新迷宫
				maze[(c1 - c1 % cols) / cols][c1 % cols].door[direction] = true;
				maze[(c2 - c2 % cols) / cols][c2 % cols].door[(direction + 2) % DIRECTION_NUM] = true;
			}
			// 如果起点和终点连通了，就说明迷宫生成成功
			if (is_Connect(0, cellCnt - 1) && all_Connect())
				break;
			
		}
	}

	/**
	 * 判断两个房间是否连通
	 * @param c1
	 * @param c2
	 * @return
	 */
	private boolean is_Connect(int c1, int c2) {
		while (cells[c1] >= 0)
			c1 = cells[c1];
		while (cells[c2] >= 0)
			c2 = cells[c2];
		if (c1 == c2)
			return true;
		else
			return false;
	}

	/**
	 * 判断是否所有的房间都连通了
	 * @return
	 */
	private boolean all_Connect() {
		int i, count_root = 0;
		for (i = 0; i < rows * cols; i++) {
			if (cells[i] < 0)
				count_root++;
		}
		if (1 == count_root)
			return true;
		else
			return false;
	}

	/**
	 * if the two adjacent rooms are not connect, remove the wall between them(or fix a door)
	 * @param c1
	 * @param c2
	 */
	private void union_Cells(int c1, int c2) {
		while (cells[c1] >= 0)
			c1 = cells[c1];
		while (cells[c2] >= 0)
			c2 = cells[c2];

		// the depth of the tree with c2 is deepper than Tc1, Tc1 attach to Tc2
		if (cells[c1] > cells[c2]) {
			cells[c1] = c2;
		} else {
			if (cells[c1] == cells[c2])
				cells[c1]--;
			cells[c2] = c1;
		}
	}
}