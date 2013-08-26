package com.mi.sta7.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.os.Bundle;
import android.util.Log;
public class Encoding {
	
	public static class Base64 {
		private static final char[] legalChars =
				"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
		/**
		 * data[]进行编码
		 * @param data
		 * @return
		 */
		public static String encode(byte[] data) {
			int start = 0;
			int len = data.length;
			StringBuffer buf = new StringBuffer(data.length * 3 / 2);
	
			int end = len - 3;
			int i = start;
			int n = 0;
	
			while (i <= end) {
				int d = ((((int) data[i]) & 0x0ff) << 16)
						| ((((int) data[i + 1]) & 0x0ff) << 8)
						| (((int) data[i + 2]) & 0x0ff);
	
				buf.append(legalChars[(d >> 18) & 63]);
				buf.append(legalChars[(d >> 12) & 63]);
				buf.append(legalChars[(d >> 6) & 63]);
				buf.append(legalChars[d & 63]);
	
				i += 3;
	
				if (n++ >= 14) {
					n = 0;
					buf.append(" ");
				}
			}
	
			if (i == start + len - 2) {
				int d = ((((int) data[i]) & 0x0ff) << 16)
						| ((((int) data[i + 1]) & 255) << 8);
	
				buf.append(legalChars[(d >> 18) & 63]);
				buf.append(legalChars[(d >> 12) & 63]);
				buf.append(legalChars[(d >> 6) & 63]);
				buf.append("=");
			} else if (i == start + len - 1) {
				int d = (((int) data[i]) & 0x0ff) << 16;
	
				buf.append(legalChars[(d >> 18) & 63]);
				buf.append(legalChars[(d >> 12) & 63]);
				buf.append("==");
			}
	
			return buf.toString();
		}
	
		private static int decode(char c) {
			if (c >= 'A' && c <= 'Z')
				return ((int) c) - 65;
			else if (c >= 'a' && c <= 'z')
				return ((int) c) - 97 + 26;
			else if (c >= '0' && c <= '9')
				return ((int) c) - 48 + 26 + 26;
			else
				switch (c) {
				case '+':
					return 62;
				case '/':
					return 63;
				case '=':
					return 0;
				default:
					throw new RuntimeException("unexpected code: " + c);
				}
		}
	
		/**
		 * Decodes the given Base64 encoded String to a new byte array. The byte
		 * array holding the decoded data is returned.
		 */
	
		public static byte[] decode(String s) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				decode(s, bos);
			} catch (IOException e) {
				throw new RuntimeException();
			}
			byte[] decodedBytes = bos.toByteArray();
			try {
				bos.close();
				bos = null;
			} catch (IOException ex) {
				System.err.println("Error while decoding BASE64: " + ex.toString());
			}
			return decodedBytes;
		}
	
		private static void decode(String s, OutputStream os) throws IOException {
			int i = 0;
			int len = s.length();
	
			while (true) {
				while (i < len && s.charAt(i) <= ' ')
					i++;
				if (i == len) break;
	
				int tri = (decode(s.charAt(i)) << 18)
						+ (decode(s.charAt(i + 1)) << 12)
						+ (decode(s.charAt(i + 2)) << 6)
						+ (decode(s.charAt(i + 3)));
	
				os.write((tri >> 16) & 255);
				if (s.charAt(i  + 2) == '=') break;
				os.write((tri >> 8) & 255);
				if (s.charAt(i + 3) == '=') break;
				os.write(tri & 255);
	
				i += 4;
			}
		}
	}
	public static class SimpleCrypto {
		private static final String LOG_TAG = "SIMPLECRYPTO";

		/**
		 * AES 128bit 加密
		 * 密码学中的高级加密标准（Advanced Encryption Standard，AES），又称高级加密标准
		 * Rijndael加密法，是美国联邦政府采用的一种区块加密标准。这个标准用来替代原先的DES
		 * @param seed 加密鍵
		 * @param clearText 要加密的內容明文
		 * @return
		 * @throws Exception
		 */
		public static String encrypt(String seed, String clearText) {
			byte[] result = null;
			try {
				byte[] rawKey = getRawKey(seed.getBytes());
				result = encrypt(rawKey, clearText.getBytes());
			} catch (Exception e) {
				return clearText;
			}
			return toHex(result);
		}

		/**
		 * AES 128bit 解密還原
		 * 
		 * @param seed 解密鍵
		 * @param encrypted 加密的內文
		 * @return
		 * @throws Exception
		 */
		public static String decrypt(String seed, String encrypted) {
			byte[] result = null;
			try {
				byte[] rawKey = getRawKey(seed.getBytes());
				byte[] enc = toByte(encrypted);
				result = decrypt(rawKey, enc);
			} catch (Exception e) {
				Log.e(LOG_TAG, "(decrypt) Exception=" + e.getLocalizedMessage());
				return encrypted;
			}
			return new String(result);
		}

		private static byte[] getRawKey(byte[] seed) throws Exception {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
			sr.setSeed(seed);
			kgen.init(128, sr); // 192 and 256 bits may not be available
			SecretKey skey = kgen.generateKey();
			byte[] raw = skey.getEncoded();
			return raw;
		}

		private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] encrypted = cipher.doFinal(clear);
			return encrypted;
		}

		private static byte[] decrypt(byte[] raw, byte[] encrypted)
				throws Exception {
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] decrypted = cipher.doFinal(encrypted);
			return decrypted;
		}

		public static String toHex(String txt) {
			return toHex(txt.getBytes());
		}

		public static String fromHex(String hex) {
			return new String(toByte(hex));
		}

		public static byte[] toByte(String hexString) {
			int len = hexString.length() / 2;
			byte[] result = new byte[len];
			for (int i = 0; i < len; i++)
				result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
						16).byteValue();
			return result;
		}

		public static String toHex(byte[] buf) {
			if (buf == null)
				return "";
			StringBuffer result = new StringBuffer(2 * buf.length);
			for (int i = 0; i < buf.length; i++) {
				appendHex(result, buf[i]);
			}
			return result.toString();
		}

		private final static String HEX = "0123456789ABCDEF";

		private static void appendHex(StringBuffer sb, byte b) {
			sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
		}

		private static byte[] iv = { 1, 2, 3, 4, 5, 6, 7, 8 }; // DES 加密初始化向量

		/**
		 * DES 加密
		 * 
		 * @param encryptString 要被加密的字串
		 * @param encryptKey 加密鍵
		 * @return: 加密後再經 Base64 編碼字串
		 * @throws Exception
		 */
		public static String encryptDES(String encryptKey, String encryptString) {
			// IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);
			IvParameterSpec zeroIv = new IvParameterSpec(iv);
			SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
			Cipher cipher;
			byte[] encryptedData = null;
			try {
				cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
				cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
				encryptedData = cipher.doFinal(encryptString.getBytes());
			} catch (NoSuchAlgorithmException e) {
				Log.e(LOG_TAG, "(encryptDES) getInstance NoSuchAlgorithmException="
						+ e.getLocalizedMessage());
			} catch (NoSuchPaddingException e) {
				Log.e(LOG_TAG, "(encryptDES) getInstance NoSuchPaddingException="
						+ e.getLocalizedMessage());
			} catch (InvalidKeyException e) {
				Log.e(LOG_TAG,
						"(encryptDES) init InvalidKeyException="
								+ e.getLocalizedMessage());
			} catch (InvalidAlgorithmParameterException e) {
				Log.e(LOG_TAG,
						"(encryptDES) init InvalidAlgorithmParams="
								+ e.getLocalizedMessage());
			} catch (IllegalBlockSizeException e) {
				Log.e(LOG_TAG,
						"(encryptDES) doFinal IllegalBlockException="
								+ e.getLocalizedMessage());
			} catch (BadPaddingException e) {
				Log.e(LOG_TAG,
						"(encryptDES) doFinal BadPaddingException="
								+ e.getLocalizedMessage());
			}
			return encryptedData == null ? encryptString : Base64
					.encode(encryptedData);
		}

		/**
		 * DES 解密
		 * 
		 * @param decryptString: 被加密且經 Base64 編碼的字串
		 * @param decryptKey 解密鍵
		 * @return: 原始字串
		 * @throws Exception
		 */
		public static String decryptDES(String decryptKey, String decryptString) {
			if (decryptString == null || decryptString.equals(""))
				return "";
			byte[] byteMi = Base64.decode(decryptString);
			IvParameterSpec zeroIv = new IvParameterSpec(iv);
			// IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);
			SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
			Cipher cipher;
			byte decryptedData[] = null;
			try {
				cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
				cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
				decryptedData = cipher.doFinal(byteMi);
			} catch (NoSuchAlgorithmException e) {
				Log.e(LOG_TAG, "(decryptDES) getInstance NoSuchAlgorithmException="
						+ e.getLocalizedMessage());
			} catch (NoSuchPaddingException e) {
				Log.e(LOG_TAG, "(decryptDES) getInstance NoSuchPaddingException="
						+ e.getLocalizedMessage());
			} catch (InvalidKeyException e) {
				Log.e(LOG_TAG,
						"(decryptDES) init InvalidKeyException="
								+ e.getLocalizedMessage());
			} catch (InvalidAlgorithmParameterException e) {
				Log.e(LOG_TAG,
						"(decryptDES) init InvalidAlgorithmParams="
								+ e.getLocalizedMessage());
			} catch (IllegalBlockSizeException e) {
				Log.e(LOG_TAG,
						"(decryptDES) doFinal IllegalBlockException="
								+ e.getLocalizedMessage());
			} catch (BadPaddingException e) {
				Log.e(LOG_TAG,
						"(decryptDES) doFinal BadPaddingException="
								+ e.getLocalizedMessage());
			}
			return decryptedData == null ? decryptString
					: new String(decryptedData);
		}
	}
	public static abstract class RSACoder {
	    public static final String KEY_ALGORITHM = "RSA";
	    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

	    public static final String PUBLIC_KEY = "RSAPublicKey";
	    public static final String PRIVATE_KEY = "RSAPrivateKey";

	    /** 用私钥对信息生成数字签名
	    * @param data 加密数据
	    * @param privateKey 私钥
	    * @return
	    * @throws Exception
	    */
	    public static String sign(byte[] data, String privateKey) throws Exception {   
	    	// 解密由base64编码的私钥
	        byte[] keyBytes = Encoding.Base64.decode(privateKey);
	        // 构造PKCS8EncodedKeySpec对象
	        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
	        // KEY_ALGORITHM 指定的加密算法
	        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
	        // 取私钥匙对象
	        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
	        // 用私钥对信息生成数字签名
	        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
	        signature.initSign(priKey);
	        signature.update(data);
	        return Encoding.Base64.encode(signature.sign());
	    }   

	    /** 校验数字签名
	    * @param data 加密数据
	    * @param publicKey 公钥
	    * @param sign 数字签名
	    * @return 校验成功返回true 失败返回false
	    * @throws Exception
	    */
	    public static boolean verify(byte[] data, String publicKey, String sign)
	            throws Exception {
	    	// 解密由base64编码的公钥
	        byte[] keyBytes = Encoding.Base64.decode(publicKey);
	        // 构造X509EncodedKeySpec对象
	        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
	        // KEY_ALGORITHM 指定的加密算法
	        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
	        // 取公钥匙对象
	        PublicKey pubKey = keyFactory.generatePublic(keySpec);
	        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
	        signature.initVerify(pubKey);
	        signature.update(data);
	        // 验证签名是否正常
	        return signature.verify(Encoding.Base64.decode(sign));
	    }   

		/** 解密
		 * 用私钥解密 http://www.5a520.cn http://www.feng123.com
		 * @param data
		 * @param key
		 * @return
		 * @throws Exception
		 */
	    public static byte[] decryptByPrivateKey(byte[] data, String key)
	            throws Exception {
	    	// 对密钥解密
			byte[] keyBytes = Encoding.Base64.decode(key);
			// 取得私钥
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
			// 对数据解密
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			return cipher.doFinal(data);
	    }

	    /** 解密 用公钥解密
	     * @param data
	     * @param key
	     * @return
	     * @throws Exception
	     */
	    public static byte[] decryptByPublicKey(byte[] data, String key)
	            throws Exception {
	    	// 对密钥解密
	        byte[] keyBytes = Encoding.Base64.decode(key);
	        // 取得公钥
	        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
	        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
	        Key publicKey = keyFactory.generatePublic(x509KeySpec);
	        // 对数据解密
	        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
	        cipher.init(Cipher.DECRYPT_MODE, publicKey);
	        return cipher.doFinal(data);
	    }

	    /** 加密 用公钥加密
	     * @param data
	     * @param key
	     * @return
	     * @throws Exception
	     */
	    public static byte[] encryptByPublicKey(byte[] data, String key)
	            throws Exception {
	        // 取得公钥
	        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key.getBytes());
	        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
	        Key publicKey = keyFactory.generatePublic(x509KeySpec);
	        // 对数据加密 
	        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
	        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
	        return cipher.doFinal(data);
	    	}

	    /** 加密 用私钥加密
	     * @param data
	     * @param key
	     * @return
	     * @throws Exception
	     */
	    public static byte[] encryptByPrivateKey(byte[] data, String key)
	            throws Exception {
	    	// 对密钥解密
	        byte[] keyBytes = Encoding.Base64.decode(key);
	        // 取得私钥 
	        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
	        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
	        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
	        // 对数据加密
	        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
	        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
	        return cipher.doFinal(data);
	    }

	    /** 取得私钥 
	     * @param keyMap
	     * @return
	     * @throws Exception
	     */
	    public static String getPrivateKey(Map<String, Object> keyMap)
	            throws Exception {
	        Key key = (Key) keyMap.get(PRIVATE_KEY);
	        return Encoding.Base64.encode(key.getEncoded());
	    }

	    /** 取得公钥
	     * @param keyMap
	     * @return
	     * @throws Exception
	     */
	    public static String getPublicKey(Map<String, Object> keyMap)
	            throws Exception {
	        Key key = (Key) keyMap.get(PUBLIC_KEY);
	        return Encoding.Base64.encode(key.getEncoded());
	    }

	    /** 初始化密钥
	     * @return
	     * @throws Exception
	     */
	    public static Map<String, Object> initKey() throws Exception {
	        KeyPairGenerator keyPairGen = KeyPairGenerator
	                .getInstance(KEY_ALGORITHM);
	        keyPairGen.initialize(1024);
	        KeyPair keyPair = keyPairGen.generateKeyPair();
	        // 公钥
	        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
	        // 私钥
	        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
	        Map<String, Object> keyMap = new HashMap<String, Object>(2);
	        keyMap.put(PUBLIC_KEY, publicKey);
	        keyMap.put(PRIVATE_KEY, privateKey);
	        return keyMap;
	    }
	}
	
    public static String encodeUrl(Bundle parameters) {
		if (parameters == null) return "";

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String key : parameters.keySet()) {
			Object parameter = parameters.get(key);
			if (!(parameter instanceof String)) continue;
			if (first) first = false; else sb.append("&");
			sb.append(URLEncoder.encode(key) + "=" + URLEncoder.encode(parameters.getString(key)));
		}
		return sb.toString();
	}
}