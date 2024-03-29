package com.syddraf.dim.net;

import com.google.gson.Gson;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
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
import java.math.BigInteger;
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
    Logger msgRecLogger = Logger.getLogger("Message Received");
    Logger msgSndLogger = Logger.getLogger("Message Sent");
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
                                        NetworkService.this.msgRecLogger.log(Level.INFO, "[ReceiverThread] Received from" +msg.headerFrom());
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
		public BlockingQueue<DIMMessage> inputMessageQueue = 
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
						System.out.println(destination);
                                                if(dht.getData()!= null) {
						// Extract the PeerAddress
						String destinationEntryJson = new String(dht.getData().getData());
						NetworkEntry destinationEntry = new Gson().fromJson(destinationEntryJson, NetworkEntry.class);
                                                System.out.println(destinationEntry.expo);
                                                System.out.println(destinationEntry.modulus);
						try {
							PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(
									new RSAPublicKeySpec(
											new BigInteger(destinationEntry.modulus),
											new BigInteger(destinationEntry.expo)
										)
									);
							KeyManager.insertIntoCache(destinationEntry.user_id, publicKey);
						} catch (InvalidKeySpecException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NoSuchAlgorithmException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						try {
							peerAddr = new PeerAddress(new Number160(destinationEntry.peer_address), 
									InetAddress.getByName(destinationEntry.ip_addr), destinationEntry.tcp_port, destinationEntry.udp_port);
							
							PeerAddressCache.i().insert(destination, peerAddr);
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                                                }
					}
					try {
	                    if(peerAddr != null) {
                                
                                msg = KeyManager.encrypt(msg);
                                
	                    	peer.send(peerAddr, msg.serialize());
	                    }
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
	
        public PublicKey getKey(String name) {
            FutureDHT dht = this.peer.get(Number160.createHash(name));
            dht.awaitUninterruptibly();
            if(dht.getData() != null) {
                String destinationEntryJson = new String(dht.getData().getData());
                NetworkEntry destinationEntry = new Gson().fromJson(destinationEntryJson, NetworkEntry.class);
                try {
                        PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(
                                        new RSAPublicKeySpec(
                                                        new BigInteger(destinationEntry.modulus),
                                                        new BigInteger(destinationEntry.expo)
                                                )
                                        );
                        return publicKey;
                } catch (InvalidKeySpecException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                } catch (NoSuchAlgorithmException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                }
            }
            
            return null;
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
                InetSocketAddress inetSock = new InetSocketAddress(InetAddress.getByName("129.59.61.103"),4000);
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
                                  
					DIMMessage msg = new Gson().fromJson((String)arg1, DIMMessage.class);
                                        msg = KeyManager.decrypt(msg);
					NetworkService.this.msgRecLogger.log(Level.INFO, "[Callback] Received from" +msg.headerFrom());
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
            System.out.println(entry.expo);
            entry.modulus = pub.getModulus().toString();
            System.out.println(entry.modulus);
            entry.peer_address = peer.getPeerAddress().getID().toString();
            entry.tcp_port = peer.getPeerAddress().portTCP();
            entry.udp_port = peer.getPeerAddress().portUDP();
            entry.user_id = PreferenceManager.getInstance().get("myName", "-");
            

            Number160 nr = Number160.createHash(entry.user_id);
            Data d = new Data(new Gson().toJson(entry).getBytes());
            FutureDHT futureDHT = peer.put(nr, d);
            futureDHT.awaitUninterruptibly();
            System.out.println("Network Initialized");
            
            /*
            // Load my information into the network
            
            NetworkEntry entry2 = new NetworkEntry();
            KeyFactory fact2 = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec pub2 = fact.getKeySpec(KeyManager.getKeyPair().getPublic(), RSAPublicKeySpec.class);
            entry.ip_addr = peer.getPeerAddress().getInetAddress().getHostAddress();
            entry.expo = pub.getPublicExponent().toString();
            entry.modulus = pub.getModulus().toString();
            entry.peer_address = peer.getPeerAddress().getID().toString();
            entry.tcp_port = peer.getPeerAddress().portTCP();
            entry.udp_port = peer.getPeerAddress().portUDP();
            entry.user_id = "matthew";//PreferenceManager.getInstance().get("myName", "-");
            

            Number160 nr2 = Number160.createHash(entry.user_id);
            Data d2 = new Data(new Gson().toJson(entry2).getBytes());
            FutureDHT futureDHT2 = peer.put(nr2, d2);
            futureDHT2.awaitUninterruptibly();
            */

           
            
            
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
            if(msg == null) System.out.println("error, null message");
            if(this.senderThread == null) System.out.println("Thread null??");
            if(this.senderThread.inputMessageQueue == null) System.out.println("error null queue");
            this.senderThread.inputMessageQueue.offer(msg);
		
	}
}
