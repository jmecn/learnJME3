package server;

import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.system.JmeContext.Type;

public class ServerMain extends SimpleApplication {
	
	public static void main(String[] args) {
		ServerMain server = new ServerMain();
		server.start(Type.Headless);
	}
	
	public ServerMain() {
		// setup your appStates. dont really need StatsAppState in a headless server.
		super(new StatsAppState());
	}

	@Override
	public void simpleInitApp() {
	}
}
