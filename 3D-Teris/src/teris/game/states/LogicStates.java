package teris.game.states;

import java.util.HashMap;
import java.util.Map.Entry;

import teris.game.DIRECTION;
import teris.game.Main;
import teris.game.control.MoveControl;
import teris.game.control.RotateControl;
import teris.game.scene.BoxGeometry;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Grid;
import com.jme3.shadow.DirectionalLightShadowRenderer;

public class LogicStates extends AbstractAppState {

	private Main game;
	
	private Node rootNode = new Node("logicRoot");
	private Node guiNode = new Node("logicGui");
	
	private Node wellNode;// 游戏中的“井”节点
	private Node controlNode;// 受控节点
	private Node previewNode;// 预览节点
	
	private Node axisNode;// Axis
	
	private BitmapText scoreTxt;
	
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
	
	// 预览节点的参数
	private int[][] previewShape = new int[4][4];
	private BoxGeometry[][] previews = null;
	private boolean previewChanged = false;

	// 7种方块的形状参数
	private static int[][] pattern = {
		{ 0x0f00, 0x2222, 0x00f0, 0x4444 }, // 'I'型的四种状态
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
	private int posZ; // 纵坐标

	// 下一个方块的参数
	private int nextBlockType; // 方块类型 0-6
	private int nextTurnState; // 方块状态 0-3
	
	// 游戏相关参数
	private int level; // 游戏级别 0-9
	private int score; // 游戏分数
	private float rate;// 方块下落速率
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		AssetManager assetManager = app.getAssetManager();
		
		// 初始化游戏场景
		game = (Main) app;
		
		game.getRootNode().attachChild(rootNode);
		game.getGuiNode().attachChild(guiNode);
		
		game.getViewPort().setBackgroundColor(new ColorRGBA(0.3f, 0.4f, 0.5f, 1));
		
		initGui();
		initCamera();
		initLight();
		
		initScene();
		
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
		
		if (previews == null) {
			previews = new BoxGeometry[4][4];
			
			// 方块坐标的偏移量
			Vector3f postion = new Vector3f();
			Vector3f offset = new Vector3f(-2+0.5f, 0.5f, -2+0.5f);
						
			for(int x=0; x<4; x++) {
				for(int y=0; y<4; y++) {
					// 计算实际坐标
					postion.set(offset.add(x, 0, y));
					
					previews[y][x] = new BoxGeometry(assetManager, 0);
					previews[y][x].setLocalTranslation(postion);
				}
			}
		}
		
		newGame();
	}
	
	private void initGui() {
		BitmapFont fnt = game.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
		String txtA = "Score: 0\nLevel: 0";
		scoreTxt = new BitmapText(fnt, false);
		scoreTxt.setText(txtA);
		scoreTxt.setLocalTranslation(0, 640, 0);
		guiNode.attachChild(scoreTxt);

	}

	private void initCamera() {
		game.getCamera().setLocation(new Vector3f(0, 25, 10));
		game.getCamera().lookAt(new Vector3f(0, 8, 0), game.getCamera().getUp());
	}
	
	/**
	 * 初始化光照
	 */
	private void initLight() {
		
		/**
		 * 创建一个垂直向下的方向光源，这道光将会产生阴影，这样就能预知方块下落的位置。
		 */
		DirectionalLight light = new DirectionalLight();
		ColorRGBA color = new ColorRGBA(0.5f, 0.5f, 0.5f, 1f);
		light.setColor(color);
		light.setDirection(new Vector3f(0, -1f, 0).normalizeLocal());
		rootNode.addLight(light);
		
		// 产生阴影
		DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(game.getAssetManager(), 1024, 4);
		dlsr.setLight(light);
		game.getViewPort().addProcessor(dlsr);
		rootNode.setShadowMode(ShadowMode.CastAndReceive);
		
		/**
		 * 再添加一个方向光源，让朝向摄像机的方位亮一点。
		 */
		light = new DirectionalLight();
		color = new ColorRGBA(0.5f, 0.5f, 0.5f, 1);
		light.setColor(color);
		light.setDirection(new Vector3f(0, -1, -1).normalize());
		rootNode.addLight(light);
		
		/**
		 * 再添加一个环境光，让游戏场景稍微亮一些。
		 */
		AmbientLight ambient = new AmbientLight();
		rootNode.addLight(ambient);
	}
	
	/**
	 * 初始化场景
	 */
	private void initScene() {
		rootNode.attachChild(getWellNode());
		
		rootNode.attachChild(getPreviewNode());
		
	}
	
	private Node getWellNode() {
		if (wellNode == null) {
			wellNode = new Node("well");
			
			// 添加旋转控制器
			wellNode.addControl(new RotateControl());
			
			// 将受控节点添加到"井"节点中，这样旋转"井"的时候受控节点也会一起旋转。
			wellNode.attachChild(getControlNode());
			
			wellNode.setShadowMode(ShadowMode.Receive);
			
			// 添加参考坐标系
			axisNode = getAxisNode();
			wellNode.attachChild(getAxisNode());
		}
		
		return wellNode;
	}
	private Node getControlNode() {
		if (controlNode == null) {
			
			controlNode = new Node("control");
			
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
	private Node getPreviewNode() {
		if (previewNode == null) {
			previewNode = new Node("preview");
			previewNode.scale(0.5f);
			previewNode.rotate(FastMath.QUARTER_PI/3, 0, 0);
			previewNode.move(0, 0, 5);
			previewNode.setShadowMode(ShadowMode.Off);
		}
		
		return previewNode;
	}

	private Node getAxisNode() {
		if (axisNode == null) {
			axisNode = new Node("AxisNode");
			Geometry grid = new Geometry("Axis", new Grid(7, 7, 1f));
			
			AssetManager assetManager = game.getAssetManager();
			Material gm = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
			gm.setColor("Color", ColorRGBA.White);
			gm.getAdditionalRenderState().setWireframe(true);
			grid.setMaterial(gm);
			
			grid.center().move(0, 0, 0);

			axisNode.attachChild(grid);
		}

		return axisNode;
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
					scoreTxt.setText("Your Final Score: " + score);
				} else {
					// 将当前控制节点与下一个方块交换
					createNewBlock();

					// 生成新的方块，并置于预览窗口中。
					getNextBlock();
				}
			} else {
				moveControl.move(DIRECTION.DOWN);
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
		createNewBlock();

		// 生成预览方块
		getNextBlock();
		
		// 启动游戏
		setEnabled(true);
	}

	private void createNewBlock() {
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
		
		updatePreview();
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

	HashMap<Integer, Vector3f> recordMap = new HashMap<Integer, Vector3f>();
	private void deleteFullLine() {
		recordMap.clear();
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
				for(int j=0; j<SIDE_X; j++) {
					recordIt(j, posY, i);
				}
				full_line_num++;
			}
		}
		
		for (int j = 0; j < SIDE_X; j++) {
			boolean isfull = true;

			for (int i = 0; i < SIDE_Z; i++) {
				if (matrix[posY][i][j] == 0) {
					isfull = false;
					continue;
				}
			}
			if (isfull) {
				// record it
				for(int i=0; i<SIDE_Z; i++) {
					recordIt(j, posY, i);
				}
				full_line_num++;
			}
		}
		
		// 消除并让方块下落
		if (full_line_num > 0) {
			for(Entry<Integer, Vector3f> entry : recordMap.entrySet()) {
				Vector3f pos = entry.getValue();
				int x= (int) pos.x;
				int y= (int) pos.y;
				int z= (int) pos.z;
				
				// 让该方块上方的所有方块下落
				for(int i=y; i<SIDE_Y-1; i++) {
					matrix[i][z][x] = matrix[i+1][z][x];
				}
				matrix[SIDE_Y-1][z][x] = 0;
			}
			
			matrixChanged = true;
		}
		addScore(full_line_num);
	}

	/**
	 * 记录那些点被消除了。
	 * @param x
	 * @param y
	 * @param z
	 */
	private void recordIt(int x, int y, int z) {
		int hashCode = x*1000 + y*10 + z;
		if (!recordMap.containsKey(hashCode)) {
			recordMap.put(hashCode, new Vector3f(x, y, z));
		}
	}
	/**
	 * 消除一行
	 * 
	 * @param line
	 */
	protected void newLine(int line) {
		// 清除一行
		//matrix[posY][line] = new int[] { 0, 0, 0, 0, 0, 0};

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
		
		// 预览节点发生了改变
		if (previewChanged) {
			previewChanged = false;
			
			for(int y=0; y<4; y++) {
				for(int x=0; x<4; x++) {
					int index = previewShape[y][x];
					if (index > 0) {
						previews[y][x].setColor(index - 1);
						previewNode.attachChild(previews[y][x]);
					} else {
						previewNode.detachChild(previews[y][x]);
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
		score = score + lineNum * lineNum;
		// 每1000分升一级，最高9级。
		if (score / 100 > level && level < 9) {
			level++;
			rate -= 0.1f;
		}
		
		scoreTxt.setText("Score: " + score + "\nLevel: " + level);
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
		
		// 也许可以加入旋转踢墙功能wallkick
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
	 * 快速下落
	 * @return
	 */
	public void quickDown() {
		while(moveDown());
	}
	/**
	 * 方块下落
	 * 
	 * @return
	 */
	public boolean moveDown() {
		boolean result = false;

		if (assertValid(turnState, posX, posY - 1, posZ)) {
			posY--;
			result = true;
		}
		return result;
	}
	
	/**
	 * 根据井旋转的角度，计算正确的东西南北方向。
	 * @param dir
	 */
	public void move(DIRECTION dir) {
		int offset = wellNode.getControl(RotateControl.class).getOffset();
		offset += dir.getValue();
		
		while (offset < 0) {
			offset += 4;
		}
		if (offset > 3) offset %= 4;
		
		switch (offset) {
		case 0: moveNorth();break;
		case 1: moveWest();break;
		case 2: moveSouth();break;
		case 3: moveEast();break;
		}
		
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
	 * 刷新预览节点
	 */
	private void updatePreview() {
		int k = 0x8000;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (((int) pattern[nextBlockType][nextTurnState] & k) != 0) {
					previewShape[i][j] = nextBlockType + 1;
				} else {
					previewShape[i][j] = 0;
				}
				k = k >> 1;
			}
		}
		
		previewChanged = true;
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
