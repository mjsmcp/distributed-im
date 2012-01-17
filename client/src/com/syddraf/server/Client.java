package com.syddraf.server;

import java.io.IOException;
import java.net.Socket;

public class Client {

	Socket socket = null;
	public Client(Socket socket) {
		this.socket = socket;
		int i = -1;
		String s = "";
		try {
			while((i = this.socket.getInputStream().read()) != 0) 
				s += (char)i;
			
			//TODO extract the welcome message
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void close() throws IOException {
		this.socket.close();
		
	}
}
