package game.components;

import java.awt.Color;

import com.simsilica.es.EntityComponent;

public class Model implements EntityComponent {
	private final String name;
	private final Color color;
	
	public final static String PLAYER = "player";
	public final static String BAD = "bad";
	public final static String TARGET = "target";
	
	public Model(String name, Color color) {
		this.name = name;
		this.color = color;
	}
	
	public String getName() {
		return name;
	}
	
	public Color getColor() {
		return color;
	}
}
