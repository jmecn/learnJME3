package com.ruanko.model;

import com.ruanko.service.Logic;

public class Block {

	private int blockType; // 方块类型 0-6
	private int turnState; // 方块状态 0-3
	private int posX; // 横坐标
	private int posY; // 纵坐标
	private int blockState; // 下落状态

	private int[][] screen;
	private static int[][] pattern = { { 0x0f00, 0x4444, 0x0f00, 0x4444 }, // 长条型的四种状态
			{ 0x04e0, 0x0464, 0x00e4, 0x04c4 }, // 'T'型的四种状态
			{ 0x4620, 0x6c00, 0x4620, 0x6c00 }, // 反'Z'型的四种状态
			{ 0x2640, 0xc600, 0x2640, 0xc600 }, // 'Z'型的四种状态
			{ 0x6220, 0x1700, 0x2230, 0x0740 }, // '7'型的四种状态
			{ 0x6440, 0x0e20, 0x44c0, 0x8e00 }, // 反'7'型的四种状态
			{ 0x0660, 0x0660, 0x0660, 0x0660 }, // 方块的四种状态
	};

	public Block(Logic logic) {
		this.screen = logic.getScreen();
		blockState = 1;
		blockType = (int) (Math.random() * 9999) % 7;
		turnState = (int) (Math.random() * 9999) % 4;
		posY = 0;
		posX = screen[0].length / 2 - 2;
	}

	public int getPattern() {
		return pattern[blockType][turnState];
	}

	public int getState() {
		return blockType;
	}
	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

	/**
	 * 实现“块”翻转的方法
	 */
	public void leftTurn() {
		dispBlock(0);
		if (assertValid((turnState + 1) % 4, posY, posX)) {
			turnState = (turnState + 1) % 4;
		}
		dispBlock(blockType + 1);
	}

	/**
	 * 实现“块”的左移的方法
	 */
	public void leftMove() {
		dispBlock(0);
		if (assertValid(turnState, posY, posX - 1)) {
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
		if (assertValid(turnState, posY, posX + 1)) {
			posX++;
		}
		dispBlock(blockType + 1);
	}

	/**
	 * 实现块落下的操作的方法
	 * 
	 * @return
	 */
	public boolean fallDown() {
		boolean result = false;
		if (blockState == 2)
			return result;

		dispBlock(0);
		if (assertValid(turnState, posY + 1, posX)) {
			posY++;
			result = true;
		} else {
			blockState = 2;
		}
		dispBlock(blockType + 1);
		return result;
	}

	/**
	 * 验证方块位置有效性
	 * 
	 * @param turnState
	 * @param posX
	 * @param posY
	 * @return
	 */
	protected boolean assertValid(int turnState, int posY, int posX) {
		boolean result = true;
		int k = 0x8000;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if ((int) (pattern[blockType][turnState] & k) != 0) {
					if (posY + i < 0  || posY + i >= screen.length || posX + j < 0 || posX + j >= screen[0].length) {
						return false;
					}
					if (screen[posY + i][posX + j] > 1)
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
					screen[posY + i][posX + j] = s;
				}
				k = k >> 1;
			}
		}
	}
}
