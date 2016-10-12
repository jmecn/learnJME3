package net.jmecn.net.msg;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityId;


/**
 *  Dual-purpose message that is used to tell the client
 *  about the latest game time but also can be used to 
 *  determine ping times.
 *
 *  @author    Paul Speed
 */
@Serializable
public class GameTimeMessage extends AbstractMessage {

    private long time;
    private long sent;

    public GameTimeMessage() {
    }
    
    public GameTimeMessage( long time ) {
        this.time = time;
        this.sent = time;
    }

    public GameTimeMessage( long time, long sent ) {
        this.time = time;
        this.sent = sent;
    }
 
    public GameTimeMessage updateGameTime( long time ) {
        return new GameTimeMessage(time, sent);
    }
    
    public long getTime() {
        return time;
    }
 
    public long getSentTime() {
        return sent;
    }
    
    @Override   
    public String toString() {
        return "GameTimeMessage[time=" + time + ", sent=" + sent + "]";
    }
}
