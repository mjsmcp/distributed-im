package com.syddraf.dim.net;

import com.google.gson.Gson;
import java.util.concurrent.*;

import com.syddraf.dim.model.DIMMessage;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkService {
	Socket socket = null;
        NetworkMessageListener l = null;
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
	private SenderThread senderThread = null;
	public void giveMessage(DIMMessage msg) {
            try {
		this.senderThread.inputMessageQueue.put(msg);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
	}
	
	
	
	private static NetworkService instance = null;
	private NetworkService() {
        try {
            
            //InetAddress addr = InetAddress.getByName("192.168.0.65");
            //System.out.println("Connecting to socket 8198 + " + addr.getHostName());
            //this.socket = new Socket(addr, 8198);
            //this.senderThread = new SenderThread();
            //this.senderThread.start();
        } catch (Exception ex) {
            Logger.getLogger(NetworkService.class.getName()).log(Level.SEVERE, null, ex);
        }
		
	}
	
	public static NetworkService i() {
		if(instance == null)
			instance = new NetworkService();
		return instance;
	}
	
	public void initialize(NetworkMessageListener l) {
                System.out.println("Initializing NetworkService");
		this.l = l;
	}
	
	public void uninitialize() {
		
	}
	
	public void reinitialize() {
		//this.uninitialize();
		//this.initialize();
	}
}
