package net.jmecn.net.msg;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import com.simsilica.es.EntityId;


/**
 *  Dual-purpose message that is used to tell the server about
 *  the new player info as well as return the player info (inclduing
 *  EntityId) back to the client.
 *
 *  @author    Paul Speed
 */
@Serializable
public class PlayerInfoMessage extends AbstractMessage {

    private String name;
    private EntityId entityId;

    public PlayerInfoMessage() {
    }
    
    public PlayerInfoMessage( String name ) {
        this.name = name;
    }
    
    public PlayerInfoMessage( EntityId id ) {
        this.entityId = id;
    }
    
    public String getName() {
        return name;
    }
    
    public EntityId getEntityId() {
        return entityId;
    }
 
    @Override   
    public String toString() {
        return "PlayerInfoMessage[name=" + name + ", entityId=" + entityId + "]";
    }
}
