package game.service;

import game.components.Health;
import game.components.Level;
import game.components.Model;
import game.components.SpawnPoint;
import game.core.Game;
import game.core.Service;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.math.FastMath;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
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
	private EntityId player;
	
	protected GameState state = GameState.GameOver;
	
	public SinglePlayerService() {
		rand = FastMath.rand;
	}

	
	@Override
	public void initialize(Game game) {
		this.game = game;
		ed = game.getEntityData();
		
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
			Health hp = ed.getComponent(player, Health.class);
			Level lv = ed.getComponent(player, Level.class);
			if (hp == null || lv == null) 
				break;
			
			// 没血就死了
			if (hp.getPercent() <= 0) {
				state = GameState.Death;
			}
			
			// 升到15级就赢了 (?)
			if (lv.getLv() >= LevelService.expTable.length) {
				state = GameState.EndLevel;
			}
			
			break;
		case Death :
			clearGame();
			state = GameState.GameOver;
			break;
		case EndLevel:
			clearGame();
			state = GameState.GameOver;
			break;
		case GameOver :
			state = GameState.LoadLevel;
			break;
		}
	}

	@Override
	public void terminate(Game game) {
	}
	
	private void clearGame() {
		// 移除所有生物
		EntitySet mobs = ed.getEntities(
				Filters.fieldEquals(Model.class, "name", Model.BAD),
				Model.class);
		mobs.applyChanges();
		for(Entity e : mobs) {
			ed.removeEntity(e.getId());
		}
		
		// 移除所有刷怪点
		EntitySet spawnPoints = ed.getEntities(SpawnPoint.class);
		spawnPoints.applyChanges();
		for(Entity e : spawnPoints) {
			ed.removeEntity(e.getId());
		}
	}
	private void initGame() {
		// 创建玩家
		if (player == null) {
			player = game.getFactory().createPlayer();
		} else {
			game.getFactory().resetPlayer(player);
		}
		game.getService(ViewService.class).setPlayer(player);
		
		// 创建刷怪点
		for(int i=0; i<13; i++) {
			int x = rand.nextInt(ViewService.WIDTH);
			int y = rand.nextInt(ViewService.HEIGHT);
			
			game.getFactory().createSpawnPoint(x, y);
		}
	}
	
	public EntityId getPlayer() {
		return player;
	}

}
