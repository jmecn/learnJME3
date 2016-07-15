package game.components;

import com.simsilica.es.EntityComponent;

/**
 * 刷怪点
 * 
 * @author yanmaoyuan
 * 
 */
public class SpawnPoint implements EntityComponent {

	/**
	 * 此刷怪点能刷出的最大怪物数量.
	 */
	private int maximum;

	/**
	 * 当前的怪物数量
	 */
	private int current;

	/**
	 * 刷怪冷却时间
	 */
	private long start;
	private long delta;

	public SpawnPoint(final int maximum, final int current, long start, long delta) {
		this.maximum = maximum;
		this.current = current;
		
		this.start = start;
		this.delta = delta;
	}
	
	public SpawnPoint(final int maximum, final int current, long deltaMillis) {
		this.maximum = maximum;
		this.current = current;

		this.start = System.nanoTime();
		this.delta = deltaMillis * 1000000;
	}
	
	public SpawnPoint(final int maximum, final int current) {
		this.maximum = maximum;
		this.current = current;
		this.start = System.nanoTime();
		this.delta = 5000000000l;// 5秒
	}
	
	public SpawnPoint(final int maximum) {
		this.maximum = maximum;
		this.current = 0;
		this.start = System.nanoTime();
		this.delta = 5000000000l;// 5秒
	}
	
	public SpawnPoint() {
		this.maximum = 10;
		this.current = 0;
		this.start = System.nanoTime();
		this.delta = 5000000000l;// 5秒
	}

	public int getMaximumCount() {
		return maximum;
	}
	
	public int getCurrentCount() {
		return current;
	}
	
	public long getStartTime() {
		return start;
	}
	
	public long getDeltaTime() {
		return delta;
	}
	
	public boolean isFull() {
		return current >= maximum;
	}

	public double getPercent() {
		long time = System.nanoTime();
		return (double) (time - start) / delta;
	}
	
	@Override
	public String toString() {
		return "SpawnPoint[cur=" + current + " max=" + maximum + " coolDown=" + (getPercent()>1.0) + "]";
	}

}
