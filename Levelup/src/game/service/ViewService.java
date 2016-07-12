package game.service;

import game.core.Game;
import game.core.Service;

import java.awt.Canvas;

import com.simsilica.es.EntityData;

public class ViewService implements Service {

	EntityData ed;
	Canvas canvas;

	public ViewService(Canvas canvas) {
		this.canvas = canvas;
	}

	@Override
	public void initialize(Game game) {

	}

	@Override
	public void update(long time) {
		// ¸üÐÂdata
		
		// ÖØ»æ
		canvas.repaint();
	}

	@Override
	public void terminate(Game game) {
		// TODO Auto-generated method stub

	}

}
