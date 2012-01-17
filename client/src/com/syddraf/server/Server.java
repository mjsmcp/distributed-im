package com.syddraf.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.syddraf.dim.PreferenceManager;
import com.syddraf.dim.crypto.KeyManager;

public class Server {

	private static Server instance = null;
	public static Server getInstance() {
		if(instance == null)
			instance = new Server();
		return instance;
	}
	Map<String, Client> clients = new HashMap<String, Client>();
	public static void main(String path) {
		PreferenceManager.init(path);
		KeyManager.init();
	}
	
	
	public boolean addClient(String userName, Client client) throws IOException {
		if(this.clients.containsKey(userName)) {
			this.clients.get(userName).close();
		}
		
		this.clients.put(userName, client);
		return true;
	}
}
