package com.bbb.valuelink.encryption;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPrivateKeySpec;
import javax.crypto.spec.DHPublicKeySpec;

import com.bbb.common.BBBGenericService;

public class ValueLinkKeyGenerator extends BBBGenericService {

	public static final String  module = ValueLinkKeyGenerator.class.getName();
	protected SecretKey mwk = null;
	protected SecretKey kek = null;
	protected Long  mwkIndex = null;
	private String mPrime;
	private String mGen;
	private String exchangekey;

	private void logDebug(String string, String module2) {
		logDebug(string);
	}

	public boolean isLoggingDebug() {
		return true;
	}

	private void logError(Exception e, String module2) {
		logError(module2, e);
	}

	//Get Kek
	protected byte[] getKek() {
    	return EncryptionUtil.fromHexString(getExchangeKey());
    }
    	
    private String getExchangeKey() {
    	if (exchangekey == null) {
    		exchangekey = "205e10abd0bcce7a34d316a237b91038205e10abd0bcce7a";
    	}
    	return exchangekey; 
	}

	//Mechant Private key
    String mMerchantPrivateKey;
    
	private byte[] getMerchantPrivateKeyBytes() {
    	return EncryptionUtil.fromHexString(mMerchantPrivateKey);
    }

	
    private String getMerchantPrivateKeyString() {
    	return mMerchantPrivateKey;
	}
    
    public void setMerchantPrivateKeyString(String pMerchantPrivateKey) {
    	mMerchantPrivateKey = pMerchantPrivateKey;
    }
    
    // Added NOPMD - (this is test class )since its could not be handled via custom exception
	/**
     * Output the creation of public/private keys + KEK to the console for manual database update
     */
    public StringBuffer  outputKeyCreation(boolean kekOnly, String  kekTest) {
    	if (getPrime() == null || getGenerator() == null || getVLPublicKey() == null) {
    		logError(null, "ValueLinkKeyGenerator:outputKeyGeneration :Keys not initialized properly. Make sure to set P, G and VL public key.");
    		throw new RuntimeException("Keys not initialized properly. Make sure to set P, G and VL public key."); //NOPMD - this is test class
		}
        return this.outputKeyCreation(0, kekOnly, kekTest);
    }

    private StringBuffer  outputKeyCreation(int loop, boolean kekOnly, String  kekTest) {
        StringBuffer  buf = new StringBuffer ();
        loop++;

        if (loop > 100) {
            // only loop 100 times; then throw an exception
        	throw new IllegalStateException ("Unable to create 128 byte keys in 100 tries");
        }

        // place holder for the keys
        DHPrivateKey privateKey = null;
        DHPublicKey publicKey = null;

        if (!kekOnly) {
            KeyPair  keyPair = null;
            try {
                keyPair = this.createKeys();
            } catch (NoSuchAlgorithmException  e) {
                logError(e, module);
            } catch (InvalidAlgorithmParameterException  e) {
                logError(e, module);
            } catch (InvalidKeySpecException  e) {
                logError(e, module);
            }

            if (keyPair != null) {
                publicKey = (DHPublicKey) keyPair.getPublic();
                privateKey = (DHPrivateKey) keyPair.getPrivate();

                if (publicKey == null || publicKey.getY().toByteArray().length != 128) {
                    // run again until we get a 128 byte public key for VL
                	return this.outputKeyCreation(loop, kekOnly, kekTest);
                }
            } else {
                logDebug("Returned a null KeyPair", module);
                return this.outputKeyCreation(loop, kekOnly, kekTest);
            }
        } else {
            // use our existing private key to generate a KEK
        	try {
                privateKey = (DHPrivateKey) getMerchantPrivateKey();
            } catch (Exception  e) {
                logError(e, module);
            }
        }

        // the KEK
        byte[] kekBytes = null;
        try {
            kekBytes = generateKek(privateKey);
        } catch (NoSuchAlgorithmException  e) {
            logError(e, module);
        } catch (InvalidKeySpecException  e) {
            logError(e, module);
        } catch (InvalidKeyException  e) {
            logError(e, module);
        }

        // the 3DES KEK value
        SecretKey loadedKek = EncryptionUtil.getDesEdeKey(kekBytes);
        byte[] loadKekBytes = loadedKek.getEncoded();

        // test the KEK
        Cipher cipher = EncryptionUtil.getCipher(getKekKey(), Cipher.ENCRYPT_MODE);
        byte[] kekTestB = { 0, 0, 0, 0, 0, 0, 0, 0 };
        byte[] kekTestC = new byte[0];
        if (kekTest != null) {
            kekTestB = EncryptionUtil.fromHexString(kekTest);
        }

        // encrypt the test bytes
        try {
            kekTestC = cipher.doFinal(kekTestB);
        } catch (Exception  e) {
            logError(e, module);
        }

        if (!kekOnly) {
            // public key (just Y)
        	BigInteger  y = publicKey.getY();
            byte[] yBytes = y.toByteArray();
            String  yHex = EncryptionUtil.toHexString(yBytes);
            buf.append("RESULT>>>>> Begin Merchant Public Key (Y @ " + yBytes.length + " / " + yHex.length() + ") ========\n");
            buf.append(yHex + "\n");
            buf.append("======== End Public Key ========\n\n");

            // private key (just X)
            BigInteger  x = privateKey.getX();
            byte[] xBytes = x.toByteArray();
            String  xHex = EncryptionUtil.toHexString(xBytes);
            setMerchantPrivateKeyString(xHex);
            buf.append("RESULT>>>>> Begin Merchant Private Key (X @ " + xBytes.length + " / " + xHex.length() + ") ========\n");
            buf.append(xHex + "\n");
            buf.append("======== End Private Key ========\n\n");

            // private key (full)
            byte[] privateBytes = privateKey.getEncoded();
            String  privateHex = EncryptionUtil.toHexString(privateBytes);
            buf.append("======== Begin Private Key (Full @ " + privateBytes.length + " / " + privateHex.length() + ") ========\n");
            buf.append(privateHex + "\n");
            buf.append("======== End Private Key ========\n\n");
        }

        if (kekBytes != null) {
            buf.append("======== Begin KEK (" + kekBytes.length + ") ========\n");
            buf.append(EncryptionUtil.toHexString(kekBytes) + "\n");
            buf.append("======== End KEK ========\n\n");

            buf.append("======== Begin KEK (DES) (" + loadKekBytes.length + ") ========\n");
            buf.append(EncryptionUtil.toHexString(loadKekBytes) + "\n");
            buf.append("======== End KEK (DES) ========\n\n");

            buf.append("======== Begin KEK Test (" + kekTestC.length + ") ========\n");
            buf.append(EncryptionUtil.toHexString(kekTestC) + "\n");
            buf.append("======== End KEK Test ========\n\n");
        } else {
            logError(null, "KEK came back empty");
        }

        return buf;
    }

    /**
     * Create a set of public/private keys using ValueLinks defined parameters
     * @return KeyPair object containing both public and private keys
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     */
    public KeyPair  createKeys() throws NoSuchAlgorithmException , InvalidAlgorithmParameterException , InvalidKeySpecException  {
    	DHParameterSpec dhParamSpec = EncryptionUtil.getDHParameterSpec(getPrime(), getGenerator());
        logDebug(dhParamSpec.getP().toString() + " / " + dhParamSpec.getG().toString(), module);

        // create the public/private key pair using parameters defined by valuelink
        KeyPairGenerator  keyGen = KeyPairGenerator.getInstance("DH");
        keyGen.initialize(dhParamSpec);
        KeyPair  keyPair = keyGen.generateKeyPair();

        return keyPair;
    }

    /**
     * Generate a key exchange key for use in encrypting the mwk
     * @param privateKey The private key for the merchant
     * @return byte array containing the kek
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     */
    public byte[] generateKek(PrivateKey  privateKey) throws NoSuchAlgorithmException , InvalidKeySpecException , InvalidKeyException  {
        // get the ValueLink public key
    	PublicKey  vlPublic = getValueLinkPublicKey();

        // generate shared secret key
    	KeyAgreement ka = KeyAgreement.getInstance("DH");
        ka.init(privateKey);
        ka.doPhase(vlPublic, true);
        byte[] secretKey = ka.generateSecret();
        
        EncryptionUtil.printByte("Secret Key", secretKey);

        
        logDebug("B1 - Secret Key : " + EncryptionUtil.toHexString(secretKey) + " / " + secretKey.length, module);
        

        // generate 3DES from secret key using VL algorithm (KEK)
        MessageDigest  md = MessageDigest.getInstance("SHA1");
        byte[] digest = md.digest(secretKey);
        byte[] des2 = EncryptionUtil.getByteRange(digest, 0, 16);
        byte[] first8 = EncryptionUtil.getByteRange(des2, 0, 8);
        byte[] kek = EncryptionUtil.copyBytes(des2, first8, 0);

       
        logDebug("B3 - Generated KEK : " + EncryptionUtil.toHexString(kek) + " / " + kek.length, module);
        
        
        //odd parity check
        EncryptionUtil.des_set_odd_parity(kek, kek.length);
        
        logDebug("B4 - After applying odd parity : " + EncryptionUtil.toHexString(kek) + " / " + kek.length, module);
        
        // Set ket to exchangeKey
        this.exchangekey = EncryptionUtil.toHexString(kek);
        return kek;
    }

    /**
     * Get a public key object for the ValueLink supplied public key
     * @return PublicKey object of ValueLinks's public key
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public PublicKey  getValueLinkPublicKey() throws NoSuchAlgorithmException , InvalidKeySpecException  {
        // read the valuelink public key
    	String  publicValue = getVLPublicKey();
        byte[] publicKeyBytes = EncryptionUtil.fromHexString(publicValue);

        // initialize the parameter spec
        DHParameterSpec dhParamSpec = EncryptionUtil.getDHParameterSpec(getPrime(), getGenerator());

        // load the valuelink public key
        KeyFactory  keyFactory = KeyFactory.getInstance("DH");
        BigInteger  publicKeyInt = new BigInteger (publicKeyBytes);
        DHPublicKeySpec dhPublicSpec = new DHPublicKeySpec(publicKeyInt, dhParamSpec.getP(), dhParamSpec.getG());
        PublicKey  vlPublic = keyFactory.generatePublic(dhPublicSpec);

        return vlPublic;
    }
    
    /**
     * @return
     */
    public String getGenerator() {
		return mGen;
	}
    
    /**
     * @param pGen
     */
    public void setGenerator(final String pGen) {
    	mGen = pGen;
    }
    

    /**
     * @return
     */
    public String getPrime() {
		return mPrime;
	}
    
    /**
     * @param pPrime
     */
    public void setPrime(final String pPrime) {
    	mPrime = pPrime;
    }


    private String mVLPubKey;
    /**
     * @return
     */
    private String getVLPublicKey() {
    	//CLGC Public Key
		return mVLPubKey;
	}
    
    public void setVLPublicKey(String pVLPubKey) {
    	mVLPubKey = pVLPubKey;
    }

	/**
     * Get merchant Private Key
     * @return PrivateKey object for the merchant
     */
    public PrivateKey  getMerchantPrivateKey() throws InvalidKeySpecException , NoSuchAlgorithmException  {
        byte[] privateKeyBytes = getMerchantPrivateKeyBytes();

        // initialize the parameter spec
        DHParameterSpec dhParamSpec = EncryptionUtil.getDHParameterSpec(getPrime(), getGenerator());

        // load the private key
        KeyFactory  keyFactory = KeyFactory.getInstance("DH");
        BigInteger  privateKeyInt = new BigInteger (privateKeyBytes);
        DHPrivateKeySpec dhPrivateSpec = new DHPrivateKeySpec(privateKeyInt, dhParamSpec.getP(), dhParamSpec.getG());
        PrivateKey  privateKey = keyFactory.generatePrivate(dhPrivateSpec);

        return privateKey;
    }

    /**
     * Generate a new MWK
     * @return Hex String of the new encrypted MWK ready for transmission to ValueLink
     */
    public byte[] generateMwk() {
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("DES");
        } catch (NoSuchAlgorithmException  e) {
            logError(e, module);
        }

        // generate the DES key 1
        SecretKey des1 = keyGen.generateKey();
        SecretKey des2 = keyGen.generateKey();

        if (des1 != null && des2 != null) {
            byte[] desByte1 = des1.getEncoded();
            byte[] desByte2 = des2.getEncoded();
            byte[] desByte3 = des1.getEncoded();

            // check for weak keys
            try {
                if (DESKeySpec.isWeak(des1.getEncoded(), 0) || DESKeySpec.isWeak(des2.getEncoded(), 0)) {
                    return generateMwk();
                }
            } catch (Exception  e) {
                logError(e, module);
            }

            byte[] des3 = EncryptionUtil.copyBytes(desByte1, EncryptionUtil.copyBytes(desByte2, desByte3, 0), 0); 
            return generateMwk(des3);
        } else {
            logDebug("Null DES keys returned", module);
        }

        return null;
    }

    /**
     * Generate a new MWK
     * @param desBytes byte array of the DES key (24 bytes)
     * @return Hex String of the new encrypted MWK ready for transmission to ValueLink
     */
    public byte[] generateMwk(byte[] desBytes) {
       
        logDebug("DES Key : " + EncryptionUtil.toHexString(desBytes) + " / " + desBytes.length, module);
        
        SecretKeyFactory skf1 = null;
        SecretKey mwk = null;
        try {
            skf1 = SecretKeyFactory.getInstance("DESede");
        } catch (NoSuchAlgorithmException  e) {
            logError(e, module);
        }
        DESedeKeySpec desedeSpec2 = null;
        try {
            desedeSpec2 = new DESedeKeySpec(desBytes);
        } catch (InvalidKeyException  e) {
            logError(e, module);
        }
        if (skf1 != null && desedeSpec2 != null) {
            try {
                mwk = skf1.generateSecret(desedeSpec2);
            } catch (InvalidKeySpecException  e) {
                logError(e, module);
            }
        }
        if (mwk != null) {
            return generateMwk(mwk);
        } else {
            return null;
        }
    }

    /**
     * Generate a new MWK
     * @param mwkdes3 pre-generated DES3 SecretKey
     * @return Hex String of the new encrypted MWK ready for transmission to ValueLink
     */
    public byte[] generateMwk(SecretKey mwkdes3) {
        // zeros for checksum
    	byte[] zeros = { 0, 0, 0, 0, 0, 0, 0, 0 };

        // 8 bytes random data
    	byte[] random = new byte[8];
        Random  ran = new Random ();
        ran.nextBytes(random);

        //String randomHexString = "e711eaffa0cac3ba";
        //random = EncryptionUtil.fromHexString(randomHexString);

        // open a cipher using the new mwk
        Cipher cipher = EncryptionUtil.getCipher(mwkdes3, Cipher.ENCRYPT_MODE);

        // make the checksum - encrypted 8 bytes of 0's
        byte[] encryptedZeros = new byte[0];
        try {
            encryptedZeros = cipher.doFinal(zeros);
        } catch (IllegalStateException  e) {
            logError(e, module);
        } catch (IllegalBlockSizeException e) {
            logError(e, module);
        } catch (BadPaddingException e) {
            logError(e, module);
        }

        // make the 40 byte MWK - random 8 bytes + key + checksum
        byte[] newMwk = EncryptionUtil.copyBytes(mwkdes3.getEncoded(), encryptedZeros, 0);
        newMwk = EncryptionUtil.copyBytes(random, newMwk, 0);

        
         logDebug("C1 - Random 8 byte : " + EncryptionUtil.toHexString(random), module);
         logDebug("C2 - Encrypted 0's : " + EncryptionUtil.toHexString(encryptedZeros), module);
         logDebug("Decrypted MWK : " + EncryptionUtil.toHexString(mwkdes3.getEncoded()) + " / " + mwkdes3.getEncoded().length, module);
         logDebug("Encrypted MWK : " + EncryptionUtil.toHexString(newMwk) + " / " + newMwk.length, module);
        

        return newMwk;
    }

    public SecretKey getKekKey() {
        if (kek == null) {
            kek = EncryptionUtil.getDesEdeKey(getKek());
        }

       
        logDebug("Raw KEK : " + EncryptionUtil.toHexString(getKek()), module);
        logDebug("KEK : " + EncryptionUtil.toHexString(kek.getEncoded()), module);
        

        return kek;
    }
    
	public static void main(String[] args) throws NoSuchAlgorithmException {
		
		Operation o = Operation.GENERATE_WORKING_KEY;
		if (args != null && args.length > 0) {
			String op = args[0];
			if (op != null && op.equalsIgnoreCase("generateKey")) {
				o = Operation.GENERATE_PUBLIC_PRIVATE_KEY;
			}
		}
		ValueLinkKeyGenerator vlg = new ValueLinkKeyGenerator();
		
		vlg.initialize(o);
		
		vlg.performOperation(o);
	}
	
	private void performOperation(Operation o) throws NoSuchAlgorithmException {
		
		switch (o) { // NOPMD - this is test class: 
		case GENERATE_WORKING_KEY:
			//Run this to generate KEK first
			StringBuffer buf = outputKeyCreation(true, null);
			outputWorkingKeyCreation();
			break;
		case GENERATE_PUBLIC_PRIVATE_KEY:
			StringBuffer buf1 = outputKeyCreation(false, null);
			outputWorkingKeyCreation();
			break;
		default: //do nothing
			break;
		}
	}
	
	private void outputWorkingKeyCreation( ) throws NoSuchAlgorithmException {
		if (getPrime() == null || getGenerator() == null || getVLPublicKey() == null || getMerchantPrivateKeyString() == null) {
			throw new RuntimeException("Keys not initialized properly. Make sure to set P, G, VL public key and merchant private key");// NOPMD - this is test class
		}
	
		byte[] keyBlockC3 = generateMwk(EncryptionUtil.fromHexString(getDecryptedMWK()));
		
		logDebug("C3 - MWK" + EncryptionUtil.toHexString(keyBlockC3));
		
		Cipher mwkCipher = EncryptionUtil.getCipher(getKekKey(), Cipher.ENCRYPT_MODE);
		String  encryptedEanHex = null;
	    try {
	    	byte[] encryptedEan = mwkCipher.doFinal(keyBlockC3);
	    	encryptedEanHex = EncryptionUtil.toHexString(encryptedEan);
	    } catch (IllegalStateException  e) {
	    	logError(e, module);
	    } catch (IllegalBlockSizeException e) {
	    	logError(e, module);
	    } catch (BadPaddingException e) {
	    	logError(e, module);
	    }
	    logDebug("RESULT>>>>> C5 - Use this for AssignWorkingKey :" + encryptedEanHex + ":");// NOPMD - this is test class
	}
	
	public String getDecryptedMWK() throws NoSuchAlgorithmException {
		String decryptedMWk = "";
		byte[] randBytes = null;
		KeyGenerator mwkKeyGen = KeyGenerator.getInstance("DES");
		mwkKeyGen.init(56);
		byte[] randBytes1 = mwkKeyGen.generateKey().getEncoded();
		byte[] randBytes2 = mwkKeyGen.generateKey().getEncoded();
		randBytes = EncryptionUtil.copyBytes(randBytes1, randBytes2, 0);
		byte[] des2 = EncryptionUtil.getByteRange(randBytes, 0, 16);
        byte[] first8 = EncryptionUtil.getByteRange(des2, 0, 8);
        byte[] dMWK = EncryptionUtil.copyBytes(des2, first8, 0);
        decryptedMWk = EncryptionUtil.toHexString(dMWK);
        logDebug("RESULT>>>>> B9 - decryptedMWk, configure this to ValueLinkEncryptor.properties :" + decryptedMWk + ":");// NOPMD - this is test class
		return decryptedMWk;
	}
	
	public void initialize(Operation o) {
		switch (o) { // NOPMD - this is test class
			case GENERATE_WORKING_KEY:
				setMerchantPrivateKeyString("1891cbe42db76bdd16d8647e8545c794d7162e0817b92878f28b1e2617816df2eac7a464545f527830207d84d4f2f4009641dc09261691231139531c4b9f7e05a11b41b4cdee9bd0e6d850dba782ee0bc8c4db0a26717c4d5ec6cdac1477000163e45e2a808e20c2ce25a519d78c6d4e8c1872b86ab87191a22ad24b5d30c027");
				
			case GENERATE_PUBLIC_PRIVATE_KEY:
				setPrime("e516e43e5457b2f66f6ca367b335ead8319939fa4df6c1b7f86e73e922a6d19393255e419096668174e35c818a66117f799e8666c8050ee436f9801351606c55d45faba03f39e2923ba926a9cd75d4bdbca9de78b62a9b847a781c692c063eaacb43a396f01d121d042755d0b7c0b2dfa8b498a57e4d90c30ca049a7ac2b7f73");
				setGenerator("5");
				setVLPublicKey("6B0276780D3E07911D744F545833005E8C2F755E0FE59A8660527F7B7E070A45EEB853DA70C6EFE2B8BF278F0B4A334A49DF0985635745A3DAD2E85A9C0EEFAE657CC382A0B3EAE9C3F85B0A2305282612CFD2857801131EC9FE313DB9DADFB914A30EE077E8A97E5574CE5BD56661B021C39116913710947FAA38FFCB4FC045");
				break;
			default: //do nothing
				break;
		}
	}
	
	private enum Operation {
		GENERATE_PUBLIC_PRIVATE_KEY,
		GENERATE_WORKING_KEY
	}
}
