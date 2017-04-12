/*
 *
 * File  : AbstractEncryptor.java
 * Project:     BBB
 */
package com.bbb.framework.crypto;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * 
 * 
 * @version 1.0
 */
@SuppressWarnings("serial")
public abstract class AbstractEncryptor implements Serializable {

	/**
     * This defines the character set which is used explicitly during byte[]<-->String conversions.
     */
    public static final String CHAR_SET = "UTF8";

    static {
	// dynamically register the SunJCE provider
	Security.addProvider(new com.sun.crypto.provider.SunJCE());
    }

    /**
     * the Cipher used to do ALGORITHM decryption using the key above.
     */
    private transient javax.crypto.Cipher mDecryptCipher;

    /**
     * the Cipher used to do ALGORITHM encryption using the key above.
     */
    private transient javax.crypto.Cipher mEncryptCipher;

    /**
     * the Key used by this instance to encrypt and decrypt.
     */
    private transient byte[] mKeyBytes;

    /**
     * This constructor takes in a cipher key in the form of a byte array. This should be 128, 192, or 256 bits long,
     * with 256 bits (32 bytes) being strong recommended.
     * 
     * @param pKey
     *                byte array of the cipher key. 32 bytes/256 bits recommended.
     * @throws EncryptorException
     *                 if the key cannot be used to setup the encryption and decryption Cipher instances.
     */
    public AbstractEncryptor(final byte[] pKey) throws EncryptorException {
	super();
	setKey(pKey);
    }

    /**
     * This constructor takes in a cipher key in the form of a String. This should be 128, 192, or 256 bits long, with
     * 256 bits (32 characters) being strong recommended.
     * 
     * @param pKey
     *                String of the cipher key. 32 characters/256 bits recommended.
     * @throws EncryptorException
     *                 if the key cannot be used to setup the encryption and decryption Cipher instances.
     */
    public AbstractEncryptor(final String pKey) throws EncryptorException {
	super();
	try {
	    setKey(Base64.decodeBase64(pKey.getBytes(CHAR_SET)));
	} catch (UnsupportedEncodingException e) {
	    throw new EncryptorException(e);
	}
    }

    public AbstractEncryptor() throws EncryptorException{
    	super();
    	initialize();
    }
    
    /**
     * This method computes the MAC message signature for the clear text. This is basically a checksum so you know you
     * decrypted the data successfully and that it wasn't tampered with.
     * 
     * @param pClearText
     *                the plain text you wish to compute the MAC for. This must match the plain text you encrypt.
     * @return a String, which is a Base64 encoded version of the checksum signature of the cleartext, based on the
     *         current algorithm and key.
     * @throws EncryptorException
     *                 if the key is invalid, if the algorithm is improperly defined, or if the encoding fails.
     */
    public String computeMAC(final String pClearText) throws EncryptorException {
	byte[] digest = null;
	String hmacString = null;
	final SecretKeySpec skeySpec = new SecretKeySpec(this.mKeyBytes, getAlgorithmName());
	// use HmacSHA1 or HmacMD5 (HmacMD5 is faster, but weaker)
	try {
	    final Mac mac = Mac.getInstance("HmacSHA1");
	    mac.init(skeySpec);
	    try {
		mac.update(pClearText.getBytes(CHAR_SET));
	    } catch (UnsupportedEncodingException e) {
		throw new EncryptorException(e);
	    }
	    digest = mac.doFinal();

	    hmacString = new String(new Base64().encode(digest), CHAR_SET);
	} catch (InvalidKeyException e) {
	    throw new EncryptorException("InvalidKeyException", e);
	} catch (NoSuchAlgorithmException e) {
	    throw new EncryptorException("NoSuchAlgorithmException", e);
	} catch (UnsupportedEncodingException e) {
	    throw new EncryptorException("UnsupportedEncodingException", e);
	}
	return hmacString;
    }

    /**
     * This method wraps the byte array based encrypt and allows you to pass in a String, which it decodes to a byte
     * array. The encrypted resulting byte array is then encoded into a String and returned.
     * 
     * @param pStringToDecrypt
     *                The String you wish to decrypt with this Encryptor
     * @return The decryped version of the input String, after it was decoded into a byte array using Base64 decoding.
     *         The decrypted byte array was then encoded back into a String and returns here.
     * @throws EncryptorException
     *                 if the String to decrypt could not be decoded using Base64.
     */
    public final String decrypt(final String pStringToDecrypt) throws EncryptorException {
	String decryptedString;
	try {
	    final byte[] encryptedBytes = Base64.decodeBase64(pStringToDecrypt.getBytes(CHAR_SET));
	    final byte[] decryptedBytes = this.decrypt(encryptedBytes);
	    decryptedString = new String(decryptedBytes, CHAR_SET);
	} catch (UnsupportedEncodingException e) {
	    throw new EncryptorException(e);
	}

	return decryptedString;
    }

    /**
     * This method wraps the byte array based encrypt and allows you to pass in a String, which it decodes to a byte
     * array. The encrypted resulting byte array is then encoded into a String and returned.
     * 
     * @param pStringToEncrypt
     *                The String you wish to encrypt with this AESEncryptor
     * @return The encryped version of the input String, encoded into a String using Base64 encoding.
     * @throws EncryptorException
     *                 if the encrypted bytes could not be encoded using Base64.
     */
    public final String encrypt(final String pStringToEncrypt) throws EncryptorException {
	byte[] decryptedBytes;
	String encryptedString = null;
	try {
	    decryptedBytes = pStringToEncrypt.getBytes(CHAR_SET);
	    final byte[] encryptedBytes = this.encrypt(decryptedBytes);
	    encryptedString = new String(new Base64().encode(encryptedBytes), CHAR_SET);
	} catch (UnsupportedEncodingException e) {
	    throw new EncryptorException(e);
	}

	return encryptedString;
    }

    /**
     * This method takes in a cipher key byte array, sets it into this class's member variable KeyBytes, and then calls
     * initialize() to initialize the encryption and decryption Cipher instances with this new key.
     * 
     * @param pKey
     *                byte array of the cipher key. 32 bytes/256 bits recommended.
     * @throws EncryptorException
     *                 if initialization fails, typically due to a bad key.
     */
    public final void setKey(final byte[] pKey) throws EncryptorException {
	this.mKeyBytes = (byte[]) pKey.clone();
	initialize();
    }

    /**
     * This method initializes the encryption and decryption Cipher instances using the key stored in the member
     * variable KeyBytes.
     * 
     * @throws EncryptorException
     *                 if the Ciphers could not be setup properly, usually due to a bad key.
     */
    private void initialize() throws EncryptorException {
	try {
	    // Create the SecretKeySpec using the key in mKeyBytes
	    final SecretKeySpec skeySpec = new SecretKeySpec(this.mKeyBytes, getAlgorithmName());
	    // Create a new Cipher instance to do decryption using ALGORITHM
	    final javax.crypto.Cipher decryptCipher = javax.crypto.Cipher.getInstance(getAlgorithmName());
	    // Initialize this Cipher as a decryption cipher and pass in the key
	    // spec to use
	    decryptCipher.init(javax.crypto.Cipher.DECRYPT_MODE, skeySpec);
	    // If the previous two steps succeeded, set this new decryption
	    // Cipher to be this class's member decryption Cipher
	    this.mDecryptCipher = decryptCipher;
	    // Create a new Cipher instance to do encryption using ALGORITHM
	    final javax.crypto.Cipher encryptCipher = javax.crypto.Cipher.getInstance(getAlgorithmName());
	    // Initialize this Cipher as an encryption cipher and pass in the
	    // key spec to us
	    encryptCipher.init(javax.crypto.Cipher.ENCRYPT_MODE, skeySpec);
	    // If the previous two steps succeeded, set this new encryption
	    // Cipher to be this class's member encryption Cipher
	    this.mEncryptCipher = encryptCipher;
	} catch (InvalidKeyException e) {
	    throw new EncryptorException("Failed to initialize encryptor: Invalid Key: ", e);
	} catch (NoSuchAlgorithmException e) {
	    throw new EncryptorException("Failed to initialize encryptor: No such algorithm: ", e);
	} catch (NoSuchPaddingException e) {
	    throw new EncryptorException("Failed to initialize encryptor: No such padding: ", e);
	}
    }

    /**
     * This method takes in a byte array and decrypts it using the AES algorithm and the key used to setup this class.
     * 
     * @param pBytesToDecrypt
     *                the byte array to be decrypted using AES and the key in mKeyBytes
     * @return a byte array of decrypted data
     * @throws EncryptorException
     *                 if decryption fails.
     */
    protected final byte[] decrypt(final byte[] pBytesToDecrypt) throws EncryptorException {
	try {
	    synchronized (this) {
		return this.mDecryptCipher.doFinal(pBytesToDecrypt);
	    }
	} catch (IllegalBlockSizeException ibse) {
	    throw new EncryptorException("Failed to decrypt: ", ibse);
	} catch (BadPaddingException bpe) {
	    throw new EncryptorException("Failed to decrypt: ", bpe);
	}
    }

    /**
     * This method takes in a byte array and encrypts it using the AES algorithm and the key used to setup this class.
     * 
     * @param pBytesToEncrypt
     *                the byte array to be encrypted using AES and the key in mKeyBytes
     * @return a byte array of encrypted data
     * @throws EncryptorException
     *                 if encryption fails.
     */
    protected final byte[] encrypt(final byte[] pBytesToEncrypt) throws EncryptorException {
	try {
	    synchronized (this) {
		return this.mEncryptCipher.doFinal(pBytesToEncrypt);
	    }
	} catch (IllegalBlockSizeException ibse) {
	    throw new EncryptorException("Failed to encrypt: ", ibse);
	} catch (BadPaddingException bpe) {
	    throw new EncryptorException("Failed to encrypt: ", bpe);
	}
    }

    /**
     * The Algorithm name for the Encryptor.
     * 
     * @return the Algorithm name for this instance of the encryptor.
     */
    protected abstract String getAlgorithmName();
}
