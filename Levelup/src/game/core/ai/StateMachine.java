package game.core.ai;

import game.core.Game;


/**
 *  A collection of states.
 *
 *  @author    Paul Speed
 */
public class StateMachine {
    private Game game;
    private Mob mob;
    private StateMachineConfig config;    
    private State current;
    
    public StateMachine( Game game, Mob mob, StateMachineConfig config ) {
        this.game = game;
        this.mob = mob;
        this.config = config;
    } 
 
    public Mob getMob() {
        return mob;
    }
 
    public Game getSystems() {
        return game;
    }
 
    public void start() {
        if( current == null ) {
            current = config.getDefaultState();
        }
        current.enter(this, mob);
    }
    
    public void stop() {
        current.leave(this, mob);
    }
 
    public void setState( String id ) {
        setState(config.getState(id));
    }    
    
    protected void setState( State state ) {
        if( state == null ) {
            state = config.getDefaultState();
        }
        
        if( current == state ) {
            return;
        }
 
        if( current != null ) {       
            current.leave(this, mob);
        }
        
        this.current = state;
        current.enter(this, mob);
    }
 
    public void update( long time ) {
        if( current == null ) {
            setState(config.getDefaultState());
        }
        current.execute(this, time, mob);
    }  
}
