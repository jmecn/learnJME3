package yan.maze;

/**
 * 迷宫中的房间。
 * 使用4个boolean变量标识各个方向的门是否打开。
 * @author yan
 *
 */
public class Cell {
	public boolean[] door = new boolean[] { false, false, false, false };
	// 0: east; 1: south; 2: west; 3 north; have door: true
}