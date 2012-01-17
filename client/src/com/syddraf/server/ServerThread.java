package com.syddraf.server;

import java.io.IOException;
import java.net.ServerSocket;

import com.syddraf.dim.PreferenceManager;

public class ServerThread extends Thread {

	public void run() {
		try {
			ServerSocket ss = new ServerSocket(PreferenceManager.getInstance().get("SERVER_PORT", 8181));
			while(true) {
			new Client(ss.accept());	
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
