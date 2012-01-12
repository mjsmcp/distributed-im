package com.syddraf.dim.net;

import java.util.HashMap;

public class ConnectionManager {

	private static ConnectionManager instance = null;
	public static ConnectionManager getInstance() {
		if(instance == null)
			instance = new ConnectionManager();
		return instance;
	}
	
	
	PeerServer server = null;
	HashMap<String, PeerConnection> map = new HashMap<String, PeerConnection>();
	private ConnectionManager() {
		this.server = new PeerServer();
	}
	
	public PeerServer serverThread() {
		return this.server;
	}
	
	public PeerConnection getPeerConnection(String userName) {
		return this.map.get(userName);
	}
	
	public void addPeerConnection(String userName, PeerConnection connection) {
		this.map.put(userName, connection);
	}
}
