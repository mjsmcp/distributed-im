package com.syddraf.dim.net;

import java.util.HashMap;

import net.tomp2p.peers.PeerAddress;

public class PeerAddressCache {

	private HashMap<String, PeerAddress> cache = new HashMap<String, PeerAddress>();
	
	private static PeerAddressCache instance = null;
	public static PeerAddressCache i() {
		if(instance == null)
			instance = new PeerAddressCache();
		return instance;
	}
	
	public PeerAddress query(String userName) {
		if(!cache.containsKey(userName)) {
			return null;
		}
		return cache.get(userName);
	}
	
	public void insert(String userName, PeerAddress addr) {
		cache.put(userName, addr);
	}
}
