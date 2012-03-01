package com.syddraf.dim.net;

import com.google.gson.Gson;

import java.util.Random;
import java.util.concurrent.*;

import com.syddraf.dim.model.DIMMessage;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.tomp2p.connection.Bindings;
import net.tomp2p.p2p.Peer;
import net.tomp2p.peers.Number160;

public class NetworkService {
	private static final int DIM_PORT = 4000;
	Socket socket = null;
    NetworkMessageListener l = null;
    private SenderThread senderThread = null;
    private ReceiverThread receiverThread = null;
	public class SenderThread extends Thread{
		private BlockingQueue<DIMMessage> inputMessageQueue = 
				new LinkedBlockingQueue<DIMMessage>();	
		
		public void run() {
			while(true) {
				try {
					DIMMessage msg = this.inputMessageQueue.take();
					//TODO Serialize and Send
				} catch (InterruptedException e) {}
			}
		}
	}
	
	public class ReceiverThread extends Thread {
            
            public void run() {
                try {
                    InputStream is = NetworkService.this.socket.getInputStream();
                    
                    while(true) {
                        String s = "";
                        int i = 0;
                        while((i = is.read()) != '\n') {
                            s += (char)i;
                        }
                        DIMMessage msg = new Gson().fromJson(s, DIMMessage.class);
                        l.receiveMessage(msg.headerFrom(), msg);
                        
                    }
                } catch(Exception e) {
                    e.printStackTrace();
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
            Random r = new Random();
            Peer peer = new Peer(new Number160(r));
            Bindings b = new Bindings();
            peer.listen(NetworkService.DIM_PORT, NetworkService.DIM_PORT, b);
        	// Start up the senderThread
            this.senderThread = new SenderThread();
            this.senderThread.start();
            
            // Start up the receiverThread
            this.receiverThread = new ReceiverThread();
            this.receiverThread.start();
        } catch (Exception ex) {
            Logger.getLogger(NetworkService.class.getName()).log(Level.SEVERE, null, ex);
        }
	}
	
	public void uninitialize() {
		
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
