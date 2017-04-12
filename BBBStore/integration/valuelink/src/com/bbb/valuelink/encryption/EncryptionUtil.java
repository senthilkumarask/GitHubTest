package com.bbb.valuelink.encryption;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.IvParameterSpec;

import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;

public class EncryptionUtil {

	private static final String  module = EncryptionUtil.class.getName();
	private static final ApplicationLogging logger = ClassLoggingFactory.getFactory().getLoggerForClassName(module);

	public static String  toHexString(byte[] bytes) {
		StringBuffer  buf = new StringBuffer (bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			buf.append(hexChar[(bytes[i] & 0xf0) >>> 4]);
			buf.append(hexChar[bytes[i] & 0x0f]);
		}
		return buf.toString();
     }

	public static int convertChar(char c) {	
		int i = 0;
		if ( '0' <= c && c <= '9' ) {
			i = c - '0' ;
		} else if ( 'a' <= c && c <= 'f' ) {
			i = c - 'a' + 0xa ;
		} else if ( 'A' <= c && c <= 'F' ) {
			i = c - 'A' + 0xa ;
		} else {
			throw new IllegalArgumentException ("Invalid hex character: [" + c + "]");
		}
		return i;
	}


	public static String  cleanHexString(String  str) {
		//logDebug("Enter cleanHexString [IN:" + str + "]");
		StringBuffer  buf = new StringBuffer ();
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) != (int) 32 && str.charAt(i) != ':') {
				buf.append(str.charAt(i));
			}
		}
		//logDebug("Exit cleanHexString [Out:" + buf + "]");
		return buf.toString();
	}


	public static byte[] fromHexString(String  str) {
		//logDebug("Enter fromHexString [IN:" + str + "]");
		str = cleanHexString(str);
		int stringLength = str.length();
		if ((stringLength & 0x1) != 0) {
			throw new IllegalArgumentException ("fromHexString requires an even number of hex characters");
		}
		byte[] b = new byte[stringLength / 2];
	
		for (int i = 0, j = 0; i < stringLength; i+= 2, j++) {
			int high = convertChar(str.charAt(i));
			int low = convertChar(str.charAt(i+1));
			b[j] = (byte) ((high << 4) | low);
		}
		if(logger.isLoggingDebug()) printByte("Exit fromHexString" , b);
		return b;
	}
	 
	/**
	 * @param length
	 * @return
	 */
	public static byte[] getRandomBytes(int length) {
		Random  rand = new Random ();
		byte[] randomBytes = new byte[length];
		rand.nextBytes(randomBytes);
		//Below is the test data
		//String s = "95e4d77c6d6c6c"; //This is for encrypt pin
		//String s = "e711eaffa0cac3ba"; //this is for encrypt MWK;
		//randomBytes = fromHexString(s);
		if(logger.isLoggingDebug()) printByte("Exit getRandomBytes" , randomBytes);

		return randomBytes;
	}

	/**
	 * @param key
	 * @param mode
	 * @return
	 */
	public static Cipher getCipher(SecretKey key, int mode) {
		byte[] zeros = { 0, 0, 0, 0, 0, 0, 0, 0 };
		IvParameterSpec iv = new IvParameterSpec(zeros);
	 
		// create the Cipher - DESede/CBC/NoPadding
		Cipher mwkCipher = null;
		try {
			mwkCipher = Cipher.getInstance("DESede/CBC/NoPadding");
		} catch (final NoSuchAlgorithmException  e) {
			logger.logError("Error in getCipher", e);
		} catch (final NoSuchPaddingException e) {
			logger.logError("Error in getCipher", e);
		}
		if (mwkCipher != null) {
			try {
				mwkCipher.init(mode, key, iv);
			} catch (final InvalidKeyException  e) {
				logger.logError("Error in getCipher", e);
			} catch (final InvalidAlgorithmParameterException  e) {
				logger.logError("Error in getCipher", e);
			}
		}
		return mwkCipher;
	} 
	
	/**
	 * @param pinBytes
	 * @return
	 */
	public static byte[] getPinCheckSum(byte[] pinBytes) {
		byte[] checkSum = new byte[1];
		checkSum[0] = 0;
		for (int i = 0; i < pinBytes.length; i++) {
			checkSum[0] += pinBytes[i];
		}
		if(logger.isLoggingDebug()) printByte("Exit getPinCheckSum" , checkSum);
		return checkSum;
	}
	
	public static void printByte(String method, byte[] b) {
		logger.logDebug(method + "[");
		for(byte b1 : b) {
			logger.logDebug((char)b1 + ":");
		}
		logger.logDebug("]");
	}

	
	/**
	 * Returns a new byte[] from the offset of the defined byte[] with a specific number of bytes
	 * @param bytes The byte[] to extract from
	 * @param offset The starting postition
	 * @param length The number of bytes to copy
	 * @return a new byte[]
	 */
	public static byte[] getByteRange(byte[] bytes, int offset, int length) {
		byte[] newBytes = new byte[length];
		for (int i = 0; i < length; i++) {
			newBytes[i] = bytes[offset + i];
		}
		if(logger.isLoggingDebug()) printByte("Exit getByteRange" , newBytes);
		return newBytes;
	}

	
    /**
     * Returns a date string formatted as directed by ValueLink
     * @return ValueLink formatted date String
     */
    public static String getDateString(final String format) {
        SimpleDateFormat  sdf = new SimpleDateFormat (format);
        String s =  sdf.format(new Date ());
        return s;
    }

    /**
     * Copies a byte[] into another byte[] starting at a specific position
     * @param source byte[] to copy from
     * @param target byte[] coping into
     * @param position the position on target where source will be copied to
     * @return a new byte[]
     */
    public static byte[] copyBytes(byte[] source, byte[] target, int position) {
    	//logDebug("Enter copyBytes [source:" + source + ", target:" + target + ", position:" + position + "]");
        byte[] newBytes = new byte[target.length + source.length];
        for (int i = 0, n = 0, x = 0; i < newBytes.length; i++) {
            if (i < position || i > (position + source.length - 2)) {
                newBytes[i] = target[n];
                n++;
            } else {
                for (; x < source.length; x++) {
                    newBytes[i] = source[x];
                    if (source.length - 1 > x) {
                        i++;
                    }
                }
            }
        }
        if(logger.isLoggingDebug()) printByte("Exit copyBytes" , newBytes);
        return newBytes;
    }
    
    // Added NOPMD since its could not be handled via custom exception
	/**
	 * @param rawKey
	 * @return
	 */
	public static SecretKey getDesEdeKey(byte[] rawKey) {
		if(logger.isLoggingDebug()) printByte("Enter getDesEdeKey" , rawKey);
		SecretKeyFactory skf = null;
		try {
			skf = SecretKeyFactory.getInstance("DESede");
		} catch (NoSuchAlgorithmException  e) {
			// should never happen since DESede is a standard algorithm
			logger.logError("Error in getDesEdeKey-", e);
			return null;
		}
		
        // load the raw key
		if (rawKey.length > 0) {
			DESedeKeySpec desedeSpec1 = null;
			try {
				desedeSpec1 = new DESedeKeySpec(rawKey);
			} catch (InvalidKeyException  e) {
				logger.logError("Not a valid DESede key", e);
				return null;
			}
			// create the SecretKey Object
			SecretKey key = null;
			try {
				key = skf.generateSecret(desedeSpec1);
			} catch (InvalidKeySpecException  e) {
				logger.logError("Error in getDesEdeKey-", e);
			}
			return key;
		} else {
			logger.logError("EncryptionUtil:getDesEdeKey :No valid DESede key available");
			throw new RuntimeException ("No valid DESede key available"); //NOPMD
		}
	}
	

    /**
     * @param primeHex
     * @param genString
     * @return
     */
    public static DHParameterSpec getDHParameterSpec(final String primeHex, final String genString) {
    	logDebug("Enter getDHParameterSpec [primeHex:" + primeHex + ", genString:" + genString + "]");
        byte[] primeByte = EncryptionUtil.fromHexString(primeHex);
        BigInteger  prime = new BigInteger (1, primeByte); // force positive (unsigned)
        BigInteger  generator = new BigInteger (genString);

        // initialize the parameter spec
        DHParameterSpec dhParamSpec = new DHParameterSpec(prime, generator, 1024);
        logDebug("Exit getDHParameterSpec [dhParamSpec:" + dhParamSpec + "]");
        return dhParamSpec;
    }

	private static void logDebug(final String string) {
		if(logger.isLoggingDebug()) {
			logger.logDebug(string);
		}
	}
	
	private final static int DES_KEY_SZ=8;

	public static final void des_set_odd_parity(byte[] key, int size)
	{
		for (int i=0; i < size; i++)
			key[i]=odd_parity[key[i]&0xff];
	}

	public static final boolean check_parity(byte[] key)
	{
		for (int i=0; i < DES_KEY_SZ; i++) {
            	if (key[i] != odd_parity[key[i]&0xff])
				return(false);
		}
		return(true);
	}

	public static final boolean des_is_weak_key(byte[] key) {
		boolean weak = true;
		for (int i=0; i < weak_keys.length; i++) {
			weak = true;
			for(int j=0;j < DES_KEY_SZ; j++) {
				if (weak_keys[i][j] != key[j]) {
					// not weak
					weak = false;
				}
			}
			if (weak) {
				break;
			} else {
			    continue;
			}
		}
		return(weak);
	}
	
	public static String rightJustified(final String s, final char filler, int size) {
		StringBuilder s1 = new StringBuilder();
		if (s != null && s.length() < size) {
			for (int i = 0; i < size - s.length(); i++) {
				s1.append(filler);
			}
			s1.append(s);
		}
		return s1.toString();
	}

	private static char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private final static byte[] odd_parity={
 		(byte)1,  (byte)1,  (byte)2,  (byte)2,  (byte)4,  (byte)4,  (byte)7,  (byte)7,  (byte)8,  (byte)8, (byte)11, (byte)11, (byte)13, (byte)13, (byte)14, (byte)14,
		(byte)16, (byte)16, (byte)19, (byte)19, (byte)21, (byte)21, (byte)22, (byte)22, (byte)25, (byte)25, (byte)26, (byte)26, (byte)28, (byte)28, (byte)31, (byte)31,
		(byte)32, (byte)32, (byte)35, (byte)35, (byte)37, (byte)37, (byte)38, (byte)38, (byte)41, (byte)41, (byte)42, (byte)42, (byte)44, (byte)44, (byte)47, (byte)47,
		(byte)49, (byte)49, (byte)50, (byte)50, (byte)52, (byte)52, (byte)55, (byte)55, (byte)56, (byte)56, (byte)59, (byte)59, (byte)61, (byte)61, (byte)62, (byte)62,
		(byte)64, (byte)64, (byte)67, (byte)67, (byte)69, (byte)69, (byte)70, (byte)70, (byte)73, (byte)73, (byte)74, (byte)74, (byte)76, (byte)76, (byte)79, (byte)79,
		(byte)81, (byte)81, (byte)82, (byte)82, (byte)84, (byte)84, (byte)87, (byte)87, (byte)88, (byte)88, (byte)91, (byte)91, (byte)93, (byte)93, (byte)94, (byte)94,
		(byte)97, (byte)97, (byte)98, (byte)98, (byte)100,(byte)100,(byte)103,(byte)103,(byte)104,(byte)104,(byte)107,(byte)107,(byte)109,(byte)109,(byte)110,(byte)110,
		(byte)112,(byte)112,(byte)115,(byte)115,(byte)117,(byte)117,(byte)118,(byte)118,(byte)121,(byte)121,(byte)122,(byte)122,(byte)124,(byte)124,(byte)127,(byte)127,
		(byte)128,(byte)128,(byte)131,(byte)131,(byte)133,(byte)133,(byte)134,(byte)134,(byte)137,(byte)137,(byte)138,(byte)138,(byte)140,(byte)140,(byte)143,(byte)143,
		(byte)145,(byte)145,(byte)146,(byte)146,(byte)148,(byte)148,(byte)151,(byte)151,(byte)152,(byte)152,(byte)155,(byte)155,(byte)157,(byte)157,(byte)158,(byte)158,
		(byte)161,(byte)161,(byte)162,(byte)162,(byte)164,(byte)164,(byte)167,(byte)167,(byte)168,(byte)168,(byte)171,(byte)171,(byte)173,(byte)173,(byte)174,(byte)174,
		(byte)176,(byte)176,(byte)179,(byte)179,(byte)181,(byte)181,(byte)182,(byte)182,(byte)185,(byte)185,(byte)186,(byte)186,(byte)188,(byte)188,(byte)191,(byte)191,
		(byte)193,(byte)193,(byte)194,(byte)194,(byte)196,(byte)196,(byte)199,(byte)199,(byte)200,(byte)200,(byte)203,(byte)203,(byte)205,(byte)205,(byte)206,(byte)206,
		(byte)208,(byte)208,(byte)211,(byte)211,(byte)213,(byte)213,(byte)214,(byte)214,(byte)217,(byte)217,(byte)218,(byte)218,(byte)220,(byte)220,(byte)223,(byte)223,
		(byte)224,(byte)224,(byte)227,(byte)227,(byte)229,(byte)229,(byte)230,(byte)230,(byte)233,(byte)233,(byte)234,(byte)234,(byte)236,(byte)236,(byte)239,(byte)239,
		(byte)241,(byte)241,(byte)242,(byte)242,(byte)244,(byte)244,(byte)247,(byte)247,(byte)248,(byte)248,(byte)251,(byte)251,(byte)253,(byte)253,(byte)254,(byte)254};

	private static final byte[][] weak_keys={
		/* weak keys */
		{(byte)0x01,(byte)0x01,(byte)0x01,(byte)0x01,(byte)0x01,(byte)0x01,(byte)0x01,(byte)0x01},
		{(byte)0xFE,(byte)0xFE,(byte)0xFE,(byte)0xFE,(byte)0xFE,(byte)0xFE,(byte)0xFE,(byte)0xFE},
		{(byte)0x1F,(byte)0x1F,(byte)0x1F,(byte)0x1F,(byte)0x1F,(byte)0x1F,(byte)0x1F,(byte)0x1F},
		{(byte)0xE0,(byte)0xE0,(byte)0xE0,(byte)0xE0,(byte)0xE0,(byte)0xE0,(byte)0xE0,(byte)0xE0},
		/* semi-weak keys */
		{(byte)0x01,(byte)0xFE,(byte)0x01,(byte)0xFE,(byte)0x01,(byte)0xFE,(byte)0x01,(byte)0xFE},
		{(byte)0xFE,(byte)0x01,(byte)0xFE,(byte)0x01,(byte)0xFE,(byte)0x01,(byte)0xFE,(byte)0x01},
		{(byte)0x1F,(byte)0xE0,(byte)0x1F,(byte)0xE0,(byte)0x0E,(byte)0xF1,(byte)0x0E,(byte)0xF1},
		{(byte)0xE0,(byte)0x1F,(byte)0xE0,(byte)0x1F,(byte)0xF1,(byte)0x0E,(byte)0xF1,(byte)0x0E},
		{(byte)0x01,(byte)0xE0,(byte)0x01,(byte)0xE0,(byte)0x01,(byte)0xF1,(byte)0x01,(byte)0xF1},
		{(byte)0xE0,(byte)0x01,(byte)0xE0,(byte)0x01,(byte)0xF1,(byte)0x01,(byte)0xF1,(byte)0x01},
		{(byte)0x1F,(byte)0xFE,(byte)0x1F,(byte)0xFE,(byte)0x0E,(byte)0xFE,(byte)0x0E,(byte)0xFE},
		{(byte)0xFE,(byte)0x1F,(byte)0xFE,(byte)0x1F,(byte)0xFE,(byte)0x0E,(byte)0xFE,(byte)0x0E},
		{(byte)0x01,(byte)0x1F,(byte)0x01,(byte)0x1F,(byte)0x01,(byte)0x0E,(byte)0x01,(byte)0x0E},
		{(byte)0x1F,(byte)0x01,(byte)0x1F,(byte)0x01,(byte)0x0E,(byte)0x01,(byte)0x0E,(byte)0x01},
		{(byte)0xE0,(byte)0xFE,(byte)0xE0,(byte)0xFE,(byte)0xF1,(byte)0xFE,(byte)0xF1,(byte)0xFE},
		{(byte)0xFE,(byte)0xE0,(byte)0xFE,(byte)0xE0,(byte)0xFE,(byte)0xF1,(byte)0xFE,(byte)0xF1}};


	public static String getTimeReversalPayload(final String originalPayload) {
		StringBuilder s1 = null;
		if (originalPayload != null) {
			s1 = new StringBuilder(originalPayload);
			String fs = originalPayload.substring(13, 14);
			String originalTransCode = originalPayload.substring(17, 21);
			s1.append(fs).append("F6").append(originalTransCode);
			s1.replace(17, 21, "0704");
		}
		return (s1 == null)?null:s1.toString();
	}
	
	public static void main(String[] args) {
		char FS = (char)0x1C;
		getDateString("MMDDyyyy HHmmss");
		logDebug(getTimeReversalPayload("SV.99910949997"+FS+"402400"+FS+"12100000"+FS+"1311212011"+FS+"707777007222699458"+FS+"42999109499970651"+FS+"EA31"));
	}
}
