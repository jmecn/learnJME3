package game.components;

import com.simsilica.es.EntityComponent;


/**
 *  The object is dead if it has this component.
 *
 *  @author    Paul Speed
 */
public class Dead implements EntityComponent {
    private long time;
    
    public Dead() {
    }
    
    public Dead( long time ) {
        this.time = time;
    }
    
    public long getTime() {
        return time;
    }
    
    @Override
    public String toString() {
        return "Dead[at:" + time + "]";
    }
}