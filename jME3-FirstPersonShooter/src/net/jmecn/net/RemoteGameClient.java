package net.jmecn.net;

import net.jmecn.net.msg.PlayerInfoMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.network.Client;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.client.RemoteEntityData;
import com.simsilica.es.net.ObjectMessageDelegator;

public class RemoteGameClient implements GameClient {
	static Logger log = LoggerFactory.getLogger(RemoteGameClient.class);

	private Client client;
	private String playerName;
	private EntityId player;
	private RemoteEntityData ed;

	private ObjectMessageDelegator delegator;

	public RemoteGameClient(String playerName, Client client, int entityChannel) {
		this.client = client;
		this.playerName = playerName;
		this.ed = new RemoteEntityData(client, entityChannel);

		this.delegator = new ObjectMessageDelegator(this, true);
		client.addMessageListener(delegator, delegator.getMessageTypes());

		// Send the player info to the server
		client.send(new PlayerInfoMessage(playerName).setReliable(true));
	}

	@Override
	public EntityId getPlayer() {
		return player;
	}

	@Override
	public EntityData getEntityData() {
		return ed;
	}

	@Override
	public void close() {
		// Close the remote entity data
		log.info("Closing entity connection");
		ed.close();

		// Close the network connection
		log.info("Closing client connection");
		if (client.isConnected()) {
			client.close();
		}
	}

}
