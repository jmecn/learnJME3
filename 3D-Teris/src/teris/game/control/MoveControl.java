package teris.game.control;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 * 移动
 * @author yanmaoyuan
 *
 */
public class MoveControl extends AbstractControl {

	/**
	 * <pre>
	 * 位移的方向。
	 * 正X轴向东，负X轴向西；
	 * 正Y轴向上，负Y轴向下；
	 * 正Z轴向南，负Z轴向北。
	 * </pre>
	 * 
	 * @author yanmaoyuan
	 *
	 */
	public enum DIRECTION {
		NORTH, SOUTH, EAST, WEST, UP, DOWN
	}
	
	private boolean isMoving;
	private DIRECTION dir;
	
	/**
	 * 完成1次移动的周期，默认为0.1秒。
	 */
	private float time;
	private float scale;
	
	/**
	 * 总的移动距离，默认1。
	 */
	private float unit;
	/**
	 * 已经移动的距离
	 */
	private float distAlreadyMoved;
	
	public MoveControl() {
		time = 0.1f;
		scale = 1 / time;
		unit = 1f;
		distAlreadyMoved = 0f;
		isMoving = false;
		dir = DIRECTION.DOWN;
	}
	
	@Override
	protected void controlUpdate(float tpf) {
		if (isMoving) {
			float dist = unit * scale * tpf;
			distAlreadyMoved += dist;
	
			// 判断是否已经完成位移
			if (distAlreadyMoved >= unit) {
				// 防止移动过远
				dist -= distAlreadyMoved - unit;
	
				// 已经完成移动，将关键参数复位。
				distAlreadyMoved = 0f;
				isMoving = false;
			}
	
			// 移动
			switch (dir) {
			case NORTH:
				spatial.move(0, 0, -dist);
				break;
			case SOUTH:
				spatial.move(0, 0, dist);
				break;
			case EAST:
				spatial.move(dist, 0, 0);
				break;
			case WEST:
				spatial.move(-dist, 0, 0);
				break;
			case UP:
				spatial.move(0, dist, 0);
				break;
			case DOWN:
				spatial.move(0, -dist, 0);
				break;
			}
		}
	}
	
	public void move(DIRECTION dir) {
		if (!isMoving) {
			isMoving = true;
			this.dir = dir;
		}
		
	}

	public boolean isMoving() {
		return isMoving;
	}
	
	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {}

}
