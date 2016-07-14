package game.components;

import com.simsilica.es.EntityComponent;

/**
 *  Represents an activity that a MOB or player is actively
 *  engaged in over a period of time.  During this time, no
 *  other activities can be performed.
 *
 *  @author    Paul Speed
 */
public class Activity implements EntityComponent {

    public static final byte SPAWNING = 0;
    public static final byte WALKING = 1;
    public static final byte TURNING = 2;
    public static final byte WAITING = 3;
    public static final byte FIGHTING = 4;

    private byte type;  
    private long startTime;
    private long endTime;
 
    public Activity() {
    }
    
    public Activity( byte type, long startTime, long endTime ) {
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    public byte getType() {
        return type;
    }
    
    public long getStartTime() {
        return startTime;
    }
    
    public long getEndTime() {
        return endTime;
    }
    
    @Override
    public String toString() {
        return "Activity[" + type + ", from:" + startTime + ", to:" + endTime + "]";
    }
}