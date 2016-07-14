package game.components;

import game.core.graphics.Box;
import game.core.graphics.Shape;

import com.jme3.math.ColorRGBA;
import com.simsilica.es.EntityComponent;

public class Model implements EntityComponent {
	private final String name;
	private final ColorRGBA color;
	private final Shape shape;
	
	public final static String PLAYER = "player";// 玩家
	public final static String BAD = "bad";// 坏人
	public final static String TARGET = "target";// 追逐目标
	public final static String RESPAWN_POINT = "respawn_point";// 刷怪点
	
	public Model(String name, ColorRGBA color) {
		this.name = name;
		this.color = color;
		this.shape = new Box();
	}
	
	public Model(String name, ColorRGBA color, Shape shape) {
		this.name = name;
		this.color = color;
		this.shape = shape;
	}
	
	public String getName() {
		return name;
	}
	
	public ColorRGBA getColor() {
		return color;
	}
	
	public Shape getShape() {
		return shape;
	}
	
}
