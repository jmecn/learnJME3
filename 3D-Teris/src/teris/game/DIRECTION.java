package teris.game;
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
	NORTH(0), WEST(1), SOUTH(2), EAST(3), UP(4), DOWN(5);
	
	private int value;
	private DIRECTION(int value) {
		this.value = value;
	}
	public int getValue() {return this.value;};
}