package net.jmecn.net;

import net.jmecn.components.Collision;
import net.jmecn.components.Decay;
import net.jmecn.components.Position;
import net.jmecn.net.msg.GameTimeMessage;
import net.jmecn.net.msg.PlayerInfoMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.network.serializing.Serializer;
import com.jme3.network.serializing.serializers.FieldSerializer;

/**
 *
 *  @author    Paul Speed
 */
public class FPSSerializers {

    static Logger log = LoggerFactory.getLogger(FPSSerializers.class);
    
    private static final Class[] classes = {
        GameTimeMessage.class,
        PlayerInfoMessage.class
    };
    
    private static final Class[] forced = {
        Collision.class,
        Decay.class,
        Position.class,
    };

    public static void initialize() {
 
        Serializer.registerClasses(classes);
    
        // Register these manually since Spider Monkey currently
        // requires them all to have @Serializable but we already know
        // which serializer we want to use.  Eventually I will fix SM
        // but for now I'll do this here.
        Serializer fieldSerializer = new FieldSerializer();
        boolean error = false;        
        for( Class c : forced) {
            try {
                Serializer.registerClass(c, fieldSerializer);
            } catch( Exception e ) {
                log.error("Error registering class:" + c, e);
                error = true;
            }
        }
        if( error ) {
            throw new RuntimeException("Some classes failed to register");
        }
    }
}