package com.syddraf.dim;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.google.gson.Gson;
import com.syddraf.dim.model.DIMMessage;

public class PeerServer extends Thread {

	ServerSocket serverSocket = null;
	public PeerServer() {
		try {
			this.serverSocket = new ServerSocket(PreferenceManager.getInstance().get("SERVER_PORT", 8021));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		while(true) {
			try {
				Socket socket = this.serverSocket.accept();
				String s = "";
				int i = 0;
				while((i = socket.getInputStream().read()) != 0) {
					s += (char)i;
				}
				DIMMessage msg = new Gson().fromJson(s, DIMMessage.class);
				ConnectionManager.getInstance().addPeerConnection(msg.headerFrom(), new PeerConnection(socket));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
