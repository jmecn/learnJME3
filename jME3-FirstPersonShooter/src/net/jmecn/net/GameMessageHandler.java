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

package net.jmecn.net;

import net.jmecn.components.Dead;
import net.jmecn.components.Model;
import net.jmecn.components.Position;
import net.jmecn.core.Game;
import net.jmecn.net.msg.GameTimeMessage;
import net.jmecn.net.msg.PlayerInfoMessage;

import com.jme3.math.Vector3f;
import com.jme3.network.HostedConnection;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.Name;


/**
 *
 *  @version   $Revision$
 *  @author    Paul Speed
 */
public class GameMessageHandler {

    public static final String ATTRIBUTE = "GameHandler";

    private HostedConnection conn;
    private Game systems;
    private EntityData ed;
    private String name;
    private EntityId player;

    private long nextMove;

    public GameMessageHandler( Game systems, HostedConnection conn ) {
        this.systems = systems;
        this.ed = systems.getEntityData();
        this.conn = conn;
    }
 
    public void close() {
        // Here we can remove the player's entity, etc.
        if( player != null ) {
            // Kill the player so their loot drops.
            ed.setComponent(player, new Dead(systems.getGameTime()));
        }
    }
 
    protected void ping( GameTimeMessage msg ) {
        // Send the latest game time back
        long time = systems.getGameTime();
        conn.send(msg.updateGameTime(time).setReliable(true));
    }
    
    protected void playerInfo( PlayerInfoMessage msg ) {
System.out.println( "Got player info:" + msg );    
        if( player == null ) {
            this.name = msg.getName();
            
            // Find a position for the player
//            Vector3f loc = systems.getService(MazeService.class).getPlayerSpawnLocation();
//            Maze maze = systems.getService(MazeService.class).getMaze(); 
        
            // Create a player
            long time = systems.getGameTime();
            player = ed.createEntity();
            ed.setComponents(player,
            		new Name(name),
            		new Position(new Vector3f(200, 10, 80)),
            		new Model(Model.OTO));
 
            // Send a message back to the player with their entity ID
            conn.send(new PlayerInfoMessage(player).setReliable(true));
            
            // Send the current game time
            conn.send(new GameTimeMessage(time).setReliable(true));
            
            // Go ahead and send them the maze, also
            //conn.send(new MazeDataMessage(maze).setReliable(true));           
        }
    }
    
    protected void move( /*MoveMessage msg */) {
        
    
    }
}
