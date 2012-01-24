package com.syddraf.jdht;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.tomp2p.connection.Bindings;
import net.tomp2p.connection.ChannelCreator;
import net.tomp2p.futures.FutureDHT;
import net.tomp2p.futures.FutureDiscover;
import net.tomp2p.futures.FutureResponse;
import net.tomp2p.futures.FutureChannelCreator;
import net.tomp2p.p2p.Peer;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.storage.Data;

public class JDHT{

	Peer peer = null;
	/**
	 * Starts a DHT Node that acts as a server
	 * @param port
	 * @throws Exception 
	 */
	public JDHT(int port) throws Exception {
		try {
			Random r = new Random(42L);
			peer = new Peer(new Number160(r));
			Bindings b = new Bindings();
			b.addInterface("eth0");
			peer.listen(port, port, b);
			for (;;) 
			{
				for (PeerAddress pa : peer.getPeerBean().getPeerMap().getAll()) 
				{
					FutureChannelCreator fcc= new FutureChannelCreator(peer.getConnectionBean().getReservation().reserve(1));
					fcc.awaitUninterruptibly();
					ChannelCreator cc = fcc.getChannelCreator();
					FutureResponse fr1 = peer.getHandshakeRPC().pingTCP(pa, cc);
					fr1.awaitUninterruptibly();
					if (fr1.isSuccess())
						System.out.println("peer online T:" + pa);
					else
						System.out.println("offline " + pa);
					FutureResponse fr2 = peer.getHandshakeRPC().pingUDP(pa, cc);
					fr2.awaitUninterruptibly();
					peer.getConnectionBean().getReservation().release(cc);
					if (fr2.isSuccess())
						System.out.println("peer online U:" + pa);
					else
						System.out.println("offline " + pa);
				}
				Thread.sleep(1500);
			}
		} finally 
		{
			peer.shutdown();
		}
	}
	
	public JDHT(InetAddress addr, int port) throws Exception {
		Random r = new Random(43L);
		this.peer = new Peer(new Number160(r));
		peer.getP2PConfiguration().setBehindFirewall(true);
		peer.listen(port, port);
		PeerAddress pa = new PeerAddress(Number160.ZERO, addr, port, port);
		FutureDiscover fd = peer.discover(pa);
		fd.awaitUninterruptibly();
		if(fd.isSuccess()) {
			System.out.println("found that my outside address is " + fd.getPeerAddress());
		} else {
			System.out.println("failed " + fd.getFailedReason());
		}
		
	}
	
	@Override
	public void finalize() {
		peer.shutdown();
	}
	
	
	public String get(String key) {
		FutureDHT dht = peer.get(Number160.createHash(key));
		dht.awaitUninterruptibly();
		return new String(dht.getData().get(key).getData());	
	}
	
	public void put(String key, String data) throws IOException {
		FutureDHT dht = peer.put(Number160.createHash(key), new Data(data));
		dht.awaitUninterruptibly();
	}


}
