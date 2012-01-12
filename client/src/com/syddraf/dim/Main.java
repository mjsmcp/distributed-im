package com.syddraf.dim;

import java.security.NoSuchAlgorithmException;

import com.syddraf.dim.crypto.KeyManager;

public class Main {

	/**
	 * @param args
	 * @throws NoSuchAlgorithmException 
	 */
	public static void main(String[] args) throws NoSuchAlgorithmException {
		String toBeEncrypted = "Hello, world!";
		String encrypted = KeyManager.encrypt(toBeEncrypted);
		System.out.println(encrypted);
		String decrypted = KeyManager.decrypt(encrypted);
		System.out.println(decrypted);

	}

}
