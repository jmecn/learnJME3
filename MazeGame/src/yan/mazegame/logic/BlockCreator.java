package yan.mazegame.logic;

import java.awt.Point;
import java.util.List;

import yan.mazegame.logic.pathfinding.Node;
import yan.mazegame.logic.pathfinding.PathFinding;

/**
 * <pre>
 * 方块生成器
 * 由于Minecraft是由方块组成的，想建造迷宫的话，必须使用方块来建造墙壁和道路！
 * 
 * 这个类将{@link MazeCreator MazeCreator}类中生成的迷宫转换成了方块形式。
 * </pre>
 * @author yan
 *
 */
public class BlockCreator {

	// 道路宽度
	// 考虑迷宫的道路使用方块隔开，因此实际宽度要+1。
	private int ROAD_SIZE;

	private int[][] blocks = null;// 迷宫地图

	private int blockCol;// 方块列数
	private int blockRow;// 方块行数
	private int blockCount;// 方块总量

	/**
	 * 默认构造函数
	 */
	public BlockCreator() {
		ROAD_SIZE = 3;// 默认道路宽度2
	}

	/**
	 * 设置初始道路宽度
	 * 
	 * @param roadSize
	 */
	public BlockCreator(final int roadSize) {
		ROAD_SIZE = roadSize + 1;
	}

	/**
	 * 设置道路宽度
	 * 
	 * @param size
	 */
	public void setRoadSize(final int size) {
		ROAD_SIZE = size + 1;
	}

	/**
	 * 生成方块迷宫
	 * 
	 * @param mc
	 */
	public void create(MazeCreator mc) {
		create(mc.getMaze(), mc.getColCount(), mc.getRowCount());
	}

	/**
	 * 生成方块迷宫
	 * 
	 * @param maze
	 * @param cols
	 * @param rows
	 */
	public void create(final Cell[][] maze, final int cols, final int rows) {
		// 计算迷宫的占地面积：行列数
		this.blockRow = rows * ROAD_SIZE + 1;
		this.blockCol = cols * ROAD_SIZE + 1;
		blocks = new int[blockRow][blockCol];

		// 根据迷宫数据，将整个区域建造墙壁
		for (int col = 0; col < cols; col++) {
			for (int row = 0; row < rows; row++) {
				Cell cell = maze[row][col];
				int x = col * ROAD_SIZE;
				int y = row * ROAD_SIZE;
				// 0: east; 1: south; 2: west; 3 north; have door: true
				if (!cell.door[3]) {
					makeWall(x, y, x + ROAD_SIZE, y);
				}
				if (!cell.door[0]) {
					makeWall(x + ROAD_SIZE, y, x + ROAD_SIZE, y + ROAD_SIZE);
				}
				if (!cell.door[1]) {
					makeWall(x, y + ROAD_SIZE, x + ROAD_SIZE, y + ROAD_SIZE);
				}
				if (!cell.door[2]) {
					makeWall(x, y, x, y + ROAD_SIZE);
				}
			}
		}

		// 计算构造整个迷宫使用的方块数量
		blockCount = 0;
		for (int y = 0; y < blockRow; y++) {
			for (int x = 0; x < blockCol; x++) {
				if (blocks[y][x] == 1) {
					blockCount++;
				}
			}
		}
	}

	/**
	 * A*寻路
	 */
	public void pathfinding() {
		int[] hit = { 1 }; // the area of which movement is
		Point begin = new Point(0, 1);// the beginning points of
		Point dest = new Point(blockCol - 1, blockRow - 2);// the destination points
		PathFinding astar = new PathFinding(blocks, hit);
		
		List path = astar.searchPath(begin, dest);
		
		if (path != null) {
			// visit the position，describe the path
			for (int i = 0; i < path.size(); i++) {
				Node node = (Node) path.get(i);
				Point pos = node._Pos;
				blocks[pos.y][pos.x] = 2;
			}
		}
	}

	/**
	 * 建造一堵墙
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	private void makeWall(int x1, int y1, int x2, int y2) {
		for (int y = y1; y <= y2; y++) {
			for (int x = x1; x <= x2; x++) {
				blocks[y][x] = 1;
			}
		}
	}

	public int[][] getMap() {
		return blocks;
	}

	public int getBlockCol() {
		return blockCol;
	}

	public int getBlockRow() {
		return blockRow;
	}

	public int getBlockCount() {
		return blockCount;
	}

}
