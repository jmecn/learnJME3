/*
 * $Id$
 *
 * Copyright (c) 2013 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package game.service.ai;

import game.components.MoveTo;
import game.components.Position;

import com.jme3.math.Vector3f;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *  Randomly wanders around without any real strategy.
 *
 *  @author    Paul Speed
 */
public class RandomWanderState implements State {

    static Logger log = LoggerFactory.getLogger(RandomWanderState.class);

    public void enter( StateMachine fsm, Mob mob ) {
    }
    
    public void execute( StateMachine fsm, long time, Mob mob ) {
 
        if( mob.isBusy(time) ) {
            // Not done with the last activity yet
            return;
        }
 
        Position pos = mob.getPosition();
        Vector3f loc = pos.getLocation();
        int x = (int)(loc.x / 2);
        int y = (int)(loc.z / 2);

        // See what the last direction was
        Vector3f lastLocation = mob.get("lastLocation");        
 
        mob.set("lastLocation", loc);
 
        // Set the desire to move to the specific location and 
        // let the movement system work out turning and stuff
    }
    
    public void leave( StateMachine fsm, Mob mob ) {
    }
}
