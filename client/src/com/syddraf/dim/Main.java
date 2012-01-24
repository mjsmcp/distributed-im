package com.syddraf.dim;



import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import java.security.NoSuchAlgorithmException;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDHT;
import net.tomp2p.p2p.Peer;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;

import com.syddraf.dim.crypto.KeyManager;
import com.syddraf.jdht.JDHT;
import com.syddraf.server.Server;

public class Main {

	public static void terminate() {
		System.out.println("Usage: ");
		System.out.println("dim MODE");
		System.out.println("dim MODE PREF");
		System.out.println("MODE = {seed, peer, server}");
		System.out.println("PREF = Filepath to preferences file");
		System.exit(0);
	}
	
	public static void runAsSeed(String preferencePath) {
		PreferenceManager.init(preferencePath);
		KeyManager.init();
		
		try {
			JDHT jdht = new JDHT(4000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void runAsPeer(String preferencePath) {
		PreferenceManager.init(preferencePath);
		KeyManager.init();
		try {
			JDHT jdht = new JDHT(InetAddress.getByName("192.168.0.64"), 4000);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * @param args
	 * @throws NoSuchAlgorithmException 
	 */
	public static void main(String[] args) throws NoSuchAlgorithmException {
		if(args.length != 1 && args.length != 2) terminate();
		String path = System.getProperty("user.home") + System.getProperty("file.separator") + "dim.conf";
		if(args.length == 2) {
			path = new File(args[1]).getAbsolutePath();
		}
		
		if(args[0].equals("seed"))
			runAsSeed(path);
		else if(args[0].equals("peer"))
			runAsPeer(path);
		else 
			Server.main(path);
	}



}
