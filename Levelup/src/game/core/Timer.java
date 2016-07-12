package game.core;

/**
 * 计时器
 * @author yanmaoyuan
 *
 */
public class Timer {

	private long startTime;
	private long previousTime;
	private long timePerFrame;
	
	public Timer() {
		startTime = System.nanoTime();
	}
	
	public long getTime() {
		return System.nanoTime() - startTime;
	}
	
	/**
	 * 更新计时器
	 */
	public void update() {
		timePerFrame = getTime() - previousTime;
		previousTime = getTime();
	}
	
	/**
	 * 获取没帧时间间隔
	 * @return
	 */
	public long getTimePerFrame() {
		return timePerFrame;
	}
	
	/**
	 * 获得每秒帧速
	 * @return
	 */
	public float getFramePerSecond() {
		return 1000000000f / timePerFrame;
	}
	
	/**
	 * 重置计时器
	 */
	public void reset() {
		startTime = System.nanoTime();
		previousTime = getTime();
	}
}
