package game.core;

/**
 * 游戏服务接口
 * @author yanmaoyuan
 *
 */
public interface Service {

	public void initialize(Game game);
	
	public void update(long time);
	
	public void terminate(Game game);
}
