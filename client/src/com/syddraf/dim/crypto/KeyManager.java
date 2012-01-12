package com.syddraf.dim.crypto;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.syddraf.dim.PreferenceManager;

public class KeyManager {
	
	private PublicKey publicKey = null;
	private PrivateKey privateKey = null;
	
	private static KeyManager instance = null;
	private static KeyManager getInstance() {
		if(instance == null)
			instance = new KeyManager();
		return instance;
	}
	private KeyManager() {
		try {
		String pubkeyModulus = PreferenceManager.getInstance().get("PRIVATE_KEY_MODULUS", "");
		String pubkeyExponent = PreferenceManager.getInstance().get("PRIVATE_KEY_EXPONENT", "");
		String privkeyModulus = PreferenceManager.getInstance().get("PUBLIC_KEY_MODULUS", "");
		String privkeyExponent = PreferenceManager.getInstance().get("PUBLIC_KEY_EXPONENT", "");
		
		if(pubkeyModulus.equals("") || pubkeyExponent.equals("") || privkeyModulus.equals("") || privkeyExponent.equals("")) {
			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(4096);
			KeyPair pair = generator.generateKeyPair();
			KeyFactory fact = KeyFactory.getInstance("RSA");
			RSAPublicKeySpec pub = fact.getKeySpec(pair.getPublic(), RSAPublicKeySpec.class);
			RSAPrivateKeySpec priv = fact.getKeySpec(pair.getPrivate(), RSAPrivateKeySpec.class);
			PreferenceManager.getInstance().put("PRIVATE_KEY_MODULUS", priv.getModulus().toString());
			PreferenceManager.getInstance().put("PRIVATE_KEY_EXPONENT", priv.getPrivateExponent().toString());
			PreferenceManager.getInstance().put("PUBLIC_KEY_MODULUS", pub.getModulus().toString());
			PreferenceManager.getInstance().put("PUBLIC_KEY_EXPONENT", pub.getPublicExponent().toString());
		}

		this.publicKey = KeyFactory.getInstance("RSA").generatePublic(
				new RSAPublicKeySpec(
						new BigInteger(PreferenceManager.getInstance().get("PUBLIC_KEY_MODULUS", "0")),
						new BigInteger(PreferenceManager.getInstance().get("PUBLIC_KEY_EXPONENT", "0"))
					)
				);
		
		this.privateKey = KeyFactory.getInstance("RSA").generatePrivate(
				new RSAPrivateKeySpec(
						new BigInteger(PreferenceManager.getInstance().get("PRIVATE_KEY_MODULUS", "0")),
						new BigInteger(PreferenceManager.getInstance().get("PRIVATE_KEY_EXPONENT", "0"))
					)
				);
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Takes in a string and encrypts it, returning the value in base64.
	 * @param message
	 * @return
	 */
	public static String encrypt(String message) { return KeyManager.getInstance().iEncrypt(message); }
	private String iEncrypt(String message) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, this.publicKey);
			return Base64.encodeBytes(cipher.doFinal(message.getBytes()));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	public static String decrypt(String message) { return KeyManager.getInstance().iDecrypt(message); }
	private String iDecrypt(String message) {
		try {
			byte[] data = Base64.decode(message.getBytes());
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, this.privateKey);
			return new String(cipher.doFinal(data));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}
	
	
	
	

	
	
}
