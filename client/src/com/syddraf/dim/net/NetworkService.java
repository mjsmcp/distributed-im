package com.syddraf.dim.net;

import com.google.gson.Gson;

import java.security.KeyFactory;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.*;

import com.syddraf.dim.PreferenceManager;
import com.syddraf.dim.crypto.KeyManager;
import com.syddraf.dim.gui.ContactManager;
import com.syddraf.dim.model.DIMMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.tomp2p.connection.Bindings;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDHT;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerMaker;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.rpc.ObjectDataReply;
import net.tomp2p.storage.Data;

public class NetworkService {
	private static final int DIM_PORT = 4000;
	Socket socket = null;
    NetworkMessageListener l = null;
    private SenderThread senderThread = null;
    private ReceiverThread receiverThread = null;
    Random r = new Random();
    Bindings b = new Bindings();
    Peer peer = null;
    
    public class ReceiverThread extends Thread {
    	private BlockingQueue<DIMMessage> messageQueue = 
    			new LinkedBlockingQueue<DIMMessage>();
    	
    	public void run() {
    		while(true) {
	    		try {
					DIMMessage msg = messageQueue.take();
					ContactManager.getInstance().postMessage(msg.headerFrom(), msg);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
    	
    	public void messageReceived(DIMMessage msg) {
    		this.messageQueue.add(msg);
    	}
    }
	public class SenderThread extends Thread {
		private BlockingQueue<DIMMessage> inputMessageQueue = 
				new LinkedBlockingQueue<DIMMessage>();	
		
		public void run() {
			while(true) {
				try {
					// Take message from the queue
					DIMMessage msg = this.inputMessageQueue.take();
					String destination = msg.headerTo();
					
					// Look up the peer in the cache
					PeerAddress peerAddr = PeerAddressCache.i().query(msg.headerTo());
					
					// If the peer is not in the cache
					if(peerAddr == null) {
						
						// Query the 
						FutureDHT dht = peer.get(Number160.createHash(destination));
						dht.awaitUninterruptibly();
						
						// Extract the PeerAddress
						String destinationEntryJson = new String(dht.getData().getData());
						NetworkEntry destinationEntry = new Gson().fromJson(destinationEntryJson, NetworkEntry.class);
						try {
							peerAddr = new PeerAddress(new Number160(destinationEntry.peer_address), 
									InetAddress.getByName(destinationEntry.ip_addr), destinationEntry.tcp_port, destinationEntry.udp_port);
							
							PeerAddressCache.i().insert(destination, peerAddr);
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					try {
						peer.send(peerAddr, msg.serialize());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (InterruptedException e) {}
			}
		}
	}
	

	private static NetworkService instance = null;
	private NetworkService() {
		
	}
	
	public static NetworkService i() {
		if(instance == null)
			instance = new NetworkService();
		return instance;
	}
	
	public void initialize(NetworkMessageListener l) {
        System.out.println("Initializing NetworkService");
		this.l = l;
		
        try {
            //Connect to the swarm
        	r = new Random();
			b = new Bindings();
			peer = new PeerMaker(new Number160(r)).setPorts(4000).setBindings(b).buildAndListen();
			InetSocketAddress inetSock = new InetSocketAddress(InetAddress.getByName("192.168.0.3"),4000);
			FutureBootstrap fb = peer.bootstrap(inetSock);
			fb.awaitUninterruptibly();
			
        	// Start up the senderThread
            this.senderThread = new SenderThread();
            this.senderThread.start();
            
            // Start receiver stuff
            this.receiverThread = new ReceiverThread();
            this.receiverThread.start();
            peer.setObjectDataReply(new ObjectDataReply() {

				@Override
				public Object reply(PeerAddress arg0, Object arg1)
						throws Exception {
					DIMMessage msg = new Gson().fromJson((String) arg1, DIMMessage.class);
					NetworkService.this.receiverThread.messageReceived(msg);
					return null;
				}
            	
            });
            
            // Load my information into the network
            NetworkEntry entry = new NetworkEntry();
            KeyFactory fact = KeyFactory.getInstance("RSA");
			RSAPublicKeySpec pub = fact.getKeySpec(KeyManager.getKeyPair().getPublic(), RSAPublicKeySpec.class);
            entry.ip_addr = peer.getPeerAddress().getInetAddress().getHostAddress();
            entry.expo = pub.getPublicExponent().toString();
            entry.modulus = pub.getModulus().toString();
            entry.peer_address = peer.getPeerAddress().getID().toString();
            entry.tcp_port = peer.getPeerAddress().portTCP();
            entry.udp_port = peer.getPeerAddress().portUDP();
            entry.user_id = PreferenceManager.getInstance().get("myName", "-");
            Data d = new Data(new Gson().toJson(entry));
            peer.add(Number160.createHash(entry.user_id), d);
            
            
        } catch (Exception ex) {
            Logger.getLogger(NetworkService.class.getName()).log(Level.SEVERE, null, ex);
        }
	}
	
	public void uninitialize() {
		peer.shutdown();
	}
	
	public void reinitialize(NetworkMessageListener l) {
		this.uninitialize();
		this.initialize(l);
	}
	
	public void giveMessage(DIMMessage msg) {
		try {
			this.senderThread.inputMessageQueue.put(msg);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
}
