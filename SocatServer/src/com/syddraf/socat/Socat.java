package com.syddraf.socat;

import java.io.IOException;
import java.net.Socket;

public class Socat {

	Socket s1 = null;
	Socket s2 = null;
	
	public Socat(Socket s1, Socket s2) {
		this.s1 = s1;
		this.s2 = s2;
		new Sock1Reader().start();
		new Sock2Reader().start();
	}
	private class Sock1Reader extends Thread {
		
		public void run() {
			int i = 0;
			try {
				while((i = s1.getInputStream().read()) != -1)
					Socat.this.s2.getOutputStream().write(i);
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}
	
	private class Sock2Reader extends Thread {
		
		public void run() {
			int i = 0;
			try {
				while((i = s2.getInputStream().read()) != -1)
					Socat.this.s1.getOutputStream().write(i);
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}
}
