package game.components;
import com.simsilica.es.EntityComponent;


/**
 *  An entity with a decay component will be deleted
 *  at some point in the future.
 *
 *  @author    Paul Speed
 */
public class Decay implements EntityComponent {
    private long at;
 
    public Decay() {
    }
    
    public Decay( long at ) {
        this.at = at;
    }
    
    public long getTime() {
        return at;
    }
    
    @Override
    public String toString() {
        return "Decay[" + at + "]";
    }
}