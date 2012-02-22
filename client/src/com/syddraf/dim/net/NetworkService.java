package com.syddraf.dim.net;

import java.util.concurrent.*;

import com.syddraf.dim.model.DIMMessage;

public class NetworkService {
	
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
	
	
	private SenderThread senderThread = null;
	public void giveMessage(DIMMessage msg) throws InterruptedException {
		this.senderThread.inputMessageQueue.put(msg);
	}
	
	
	
	private static NetworkService instance = null;
	private NetworkService() {
		this.senderThread = new SenderThread();
		this.senderThread.start();
	}
	
	public static NetworkService i() {
		if(instance == null)
			instance = new NetworkService();
		return instance;
	}
	
	public void initialize() {
		
	}
	
	public void uninitialize() {
		
	}
	
	public void reinitialize() {
		this.uninitialize();
		this.initialize();
	}
}
