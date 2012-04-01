package com.syddraf.dim;



import java.security.NoSuchAlgorithmException;
import com.syddraf.dim.crypto.KeyManager;
import com.syddraf.dim.gui.*;
import com.syddraf.dim.net.NetworkService;
public class Main {

	public static void terminate() {
		System.out.println("Usage: ");
		System.out.println("dim MODE");
		System.out.println("dim MODE PREF");
		System.out.println("MODE = {seed, peer}");
		System.out.println("PREF = Filepath to preferences file");
		System.exit(0);
	}
	
	public static void runAsSeed(String preferencePath) {
		PreferenceManager.init(preferencePath);
		KeyManager.init();

		
	}
	
	public static void runAsPeer(String preferencePath) {
		PreferenceManager.init(preferencePath);
		KeyManager.init();
		
		
	}
	/**
	 * @param args
	 * @throws NoSuchAlgorithmException 
	 */
	public static void main(String[] args) throws NoSuchAlgorithmException {
            /*
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
                     
                        */
            
            PreferenceManager.init("/home/syddraf/prefs");
            PreferenceManager.getInstance().put("myName", args[0]);
            KeyManager.init();
            new MainWindow().setVisible(true);
	}



}
