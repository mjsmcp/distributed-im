package com.syddraf.dim.crypto;

import com.sun.crypto.provider.RSACipher;
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
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import com.syddraf.dim.PreferenceManager;
import com.syddraf.dim.model.DIMMessage;
import com.syddraf.dim.net.NetworkService;
import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KeyManager {
	
	private PublicKey publicKey = null;
	private PrivateKey privateKey = null;
	private SecretKeySpec aesKeySpec = null;
	private Map<String, PublicKey> keyCache = new HashMap<String, PublicKey>();
	private static KeyManager instance = null;
	public static void init() { KeyManager.getInstance(); }
	private static KeyManager getInstance() {
		if(instance == null)
			instance = new KeyManager();
		return instance;
	}
	
	public static void insertIntoCache(String person, PublicKey key) { KeyManager.getInstance().insert(person, key); }
	private void insert(String person, PublicKey key) {
		this.keyCache.put(person, key);
	}
        
        public static void insert(String name) { KeyManager.getInstance().iinsert(name); }
        
        public void iinsert(String name) {
            this.insert(name, NetworkService.i().getKey(name));
        }
	private KeyManager() {
		try {
			KeyGenerator gen = KeyGenerator.getInstance("AES");
			gen.init(128);
			this.aesKeySpec = new SecretKeySpec(gen.generateKey().getEncoded(),"AES");
		// Load keys from the preference file
		String pubkeyModulus = PreferenceManager.getInstance().get("PRIVATE_KEY_MODULUS", "");
		String pubkeyExponent = PreferenceManager.getInstance().get("PRIVATE_KEY_EXPONENT", "");
		String privkeyModulus = PreferenceManager.getInstance().get("PUBLIC_KEY_MODULUS", "");
		String privkeyExponent = PreferenceManager.getInstance().get("PUBLIC_KEY_EXPONENT", "");
		
		// If any of the key components is null, we need to generate a new pair
		if(pubkeyModulus.equals("") || pubkeyExponent.equals("") || privkeyModulus.equals("") || privkeyExponent.equals("")) {
			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(512);
			KeyPair pair = generator.generateKeyPair();
			KeyFactory fact = KeyFactory.getInstance("RSA");
			RSAPublicKeySpec pub = fact.getKeySpec(pair.getPublic(), RSAPublicKeySpec.class);
			RSAPrivateKeySpec priv = fact.getKeySpec(pair.getPrivate(), RSAPrivateKeySpec.class);
			
			// Set the new pair
			PreferenceManager.getInstance().put("PRIVATE_KEY_MODULUS", priv.getModulus().toString());
			PreferenceManager.getInstance().put("PRIVATE_KEY_EXPONENT", priv.getPrivateExponent().toString());
			PreferenceManager.getInstance().put("PUBLIC_KEY_MODULUS", pub.getModulus().toString());
			PreferenceManager.getInstance().put("PUBLIC_KEY_EXPONENT", pub.getPublicExponent().toString());
		}

		// Generate the key objects from the components
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
	public static DIMMessage encrypt(DIMMessage msg) { return KeyManager.getInstance().iEncrypt(msg); }
	private DIMMessage iEncrypt(DIMMessage msg) {
		
		try {
			
			// Encrypt Body
			Cipher aesCipher = Cipher.getInstance("AES");
			aesCipher.init(Cipher.ENCRYPT_MODE, this.aesKeySpec);
			msg.bodyMessage(Base64.encodeBytes(aesCipher.doFinal(msg.bodyMessage().getBytes())));
			
			
			
			
			
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, this.keyCache.get(msg.headerTo()));
			msg.setAESKey(Base64.encodeBytes(cipher.doFinal(this.aesKeySpec.getEncoded())));
                   
             
			return msg;
                      
                        
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
		return msg;
	}
	public static DIMMessage decrypt(DIMMessage message) { return KeyManager.getInstance().iDecrypt(message); }
	private DIMMessage iDecrypt(DIMMessage msg) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, this.privateKey);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        baos.write(Base64.decode(msg.getAESKey()));
                    
			SecretKeySpec s = new SecretKeySpec(cipher.doFinal(baos.toByteArray()), "AES");
			
                        
			Cipher aesCipher = Cipher.getInstance("AES");
			aesCipher.init(Cipher.DECRYPT_MODE, s);
			msg.bodyMessage(new String(aesCipher.doFinal(Base64.decode(msg.bodyMessage()))));
			
			return msg;
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
		return msg;
	}
	public static KeyPair getKeyPair() {
		return new KeyPair(getInstance().publicKey, getInstance().privateKey);
	}
	
	
	
	

	
	
}
