package game.core.ai;
/**
 *  Represents a single state in an FSM.
 *
 *  @author    Paul Speed
 */
public interface State {
    public void enter( StateMachine fsm, Mob mob );
    public void execute( StateMachine fsm, long time, Mob mob );
    public void leave( StateMachine fsm, Mob mob );
}
