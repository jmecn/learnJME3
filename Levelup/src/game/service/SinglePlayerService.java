package game.service;

import game.components.Model;
import game.core.Game;
import game.core.Service;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.math.FastMath;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;
import com.simsilica.es.Filters;

/**
 * 玩家的操作逻辑
 * @author yanmaoyuan
 *
 */
public class SinglePlayerService implements Service {

	protected enum GameState { LoadLevel, Starting, Joining, Playing, Death, EndLevel, GameOver };
	
	private Logger log = LoggerFactory.getLogger(SinglePlayerService.class);
	
	private Random rand;
	
	private Game game;
	private EntityData ed;
	private EntitySet players;
	
	protected GameState state = GameState.GameOver;
	
	public SinglePlayerService() {
		rand = FastMath.rand;
	}

	
	@Override
	public void initialize(Game game) {
		this.game = game;
		ed = game.getEntityData();
		
		players = ed.getEntities(Filters.fieldEquals(Model.class, "name", Model.PLAYER), Model.class);
		
	}

	@Override
	public void update(long time) {
		switch (state) {
		case LoadLevel :
			initGame();
			state = GameState.Starting;
			break;
		case Starting :
			state = GameState.Joining;
			break;
		case Joining :
			state = GameState.Playing;
			break;
		case Playing:
			if (players.applyChanges()) {
				if (players.getRemovedEntities().size() > 0) {
					// 玩家被吃了。。
					state = GameState.Death;
				}
			}
			break;
		case Death :
			break;
		case EndLevel:
			break;
		case GameOver :
			state = GameState.LoadLevel;
			break;
		}
	}

	@Override
	public void terminate(Game game) {
		players.release();
		players = null;
		
	}
	
	private void initGame() {
		// 创建玩家
		game.getFactory().createPlayer(540, 360);
		
		// 创建刷怪点
		for(int i=0; i<13; i++) {
			int x = rand.nextInt(ViewService.WIDTH);
			int y = rand.nextInt(ViewService.HEIGHT);
			
			game.getFactory().createSpawnPoint(x, y);
		}
	}

}
