package net.jmecn.components;
import com.simsilica.es.EntityComponent;

public class Model implements EntityComponent {
    private final String name;
    
    public final static String ICEWORLD = "Models/Terrain/iceworld.j3o";
    public final static String BOMB = "Models/Bomb/bomb.blend";
    public static final String OTO = "Models/Oto/Oto.mesh.xml";
    public final static String BULLET = "Bullet";
	public static final String SKY = "Sky";
	public static final String EXPLOSION = "Explosion";

    public Model(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Model[" + name + "]";
    }
}