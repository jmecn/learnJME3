package yan.maze;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * 画板
 * @author yan
 *
 */
public class Canvas extends JPanel {
	private BufferedImage image;// 图像缓冲区

	private int SIZE = 12;
	
	public Canvas() {
		SIZE = 12;// 默认12像素
	}
	/**
	 * 设置像素宽度
	 * 至少7 px
	 * @param px
	 */
	public void setPixel(int px) {
		if (px < 7) px = 7;
		SIZE = px;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -3767183299577116646L;

	int[][] map;
	int col = 0;
	int row = 0;

	int width = 0;
	int height = 0;

	/**
	 * 设置迷宫地图
	 * 
	 * @param map
	 */
	public void setMap(BlockCreator bc) {
		this.map = bc.getMap();
		this.row = bc.getBlockRow();
		this.col = bc.getBlockCol();

		width = col * SIZE + 9;
		height = row * SIZE + 9;

		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		drawImage();
		this.setPreferredSize(new Dimension(width, height));
	}

	/**
	 * 将迷宫地图画到缓冲区中。
	 */
	private void drawImage() {
		// 清屏
		image.flush();

		// 循环绘制界面
		Graphics g = image.getGraphics();
		for (int y = 0; y < row; y++) {
			g.setColor(Color.BLACK);
			for (int x = 0; x < col; x++) {
				// 绘制界面方块
				if (map[y][x] != 0)
					drawUnit(g, x, y);
			}
		}
	}

	/**
	 * 绘制单个方块
	 * 
	 * @param g
	 *            画笔
	 * @param posX
	 *            横坐标
	 * @param posY
	 *            纵坐标
	 * @param state
	 */
	private void drawUnit(Graphics g, int posX, int posY) {
		// 设置方块坐标
		int x = 5 + posX * SIZE;
		int y = 5 + posY * SIZE;

		// 设置线段颜色
		g.setColor(Color.BLACK);
		// 绘制线段
		g.drawLine(x + 1, y, x + SIZE - 2, y);
		g.drawLine(x + 1, y + SIZE - 1, x + SIZE - 2, y + SIZE - 1);
		g.drawLine(x, y + 1, x, y + SIZE - 2);
		g.drawLine(x + SIZE - 1, y + 1, x + SIZE - 1, y + SIZE - 2);
		// 绘制立体的线段
		g.drawLine(x + 2, y + SIZE - 3, x + SIZE - 3, y + SIZE - 3);
		g.drawLine(x + SIZE - 3, y + 2, x + SIZE - 3, y + SIZE - 3);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.drawImage(image, 0, 0, null);
	}
	
	@Override
	public void update(Graphics g) {
		paint(g);
	}
	public BufferedImage getImage() {
		return image;
	}
}
