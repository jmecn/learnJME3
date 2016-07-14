package game.components;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;


/**
 *  Signals the intent of an entity to move to a 
 *  particular location... direction change is implied.
 *  A time property is included just in case priority
 *  is ever needed... though most of these will all
 *  be at the same time for any given frame anyway.
 *
 *  @author    Paul Speed
 */
public class MoveTo implements EntityComponent {
    private long time;
    private Vector3f pos;
    
    public MoveTo() {
    }
    
    public MoveTo( Vector3f pos, long time ) {
        this.time = time;
        this.pos = pos;
    }
 
    public MoveTo newTime( long time ) {
        return new MoveTo(pos, time);
    }
    
    public long getTime() {
        return time;
    }
    
    public Vector3f getLocation() {
        return pos;
    }
    
    @Override
    public String toString() {
        return "MoveTo[" + pos + " at:" + time + "]";
    }
}
