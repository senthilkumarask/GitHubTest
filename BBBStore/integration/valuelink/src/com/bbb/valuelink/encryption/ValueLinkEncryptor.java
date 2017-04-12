package com.bbb.valuelink.encryption;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;
import com.bbb.valuelink.constants.BBBValueLinkConstants;

public class ValueLinkEncryptor extends BBBGenericService {

	public static final String  module = ValueLinkEncryptor.class.getName();
	private SecretKey mwk = null;
	private SecretKey kek = null;
	private String mExchangeKey;
	private String workingKey;
	private BBBCatalogTools catalogTools;
	
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}


    /**
	* Encrypt the defined pin using the configured keys
	* @param pin Plain text String of the pin
	* @return Hex String of the encrypted pin (EAN) for transmission to ValueLink
	*/
	public String  encryptPin(String  pin) {
		// get the Cipher
		Cipher mwkCipher = EncryptionUtil.getCipher(this.getMwkKey(), Cipher.ENCRYPT_MODE);
		
		// pin to bytes
		byte[] pinBytes = pin.getBytes();
		 
		// 7 bytes of random data
		byte[] random = EncryptionUtil.getRandomBytes(7);
		
		// pin checksum
		byte[] checkSum = EncryptionUtil.getPinCheckSum(pinBytes);

		// put all together
		byte[] eanBlock = new byte[16];
		int i;
		for (i = 0; i < random.length; i++) {
			eanBlock[i] = random[i];
		}
		eanBlock[7] = checkSum[0];
	    for (i = 0; i < pinBytes.length; i++) {
	    	eanBlock[i + 8] = pinBytes[i];
	    }
	    
//	    System.out.println("EAN BLOCK-" + EncryptionUtil.toHexString(eanBlock));
	
	    // encrypy the ean
	    String  encryptedEanHex = null;
	    try {
	    	byte[] encryptedEan = mwkCipher.doFinal(eanBlock);
	    	encryptedEanHex = EncryptionUtil.toHexString(encryptedEan);
	    } catch (IllegalStateException  e) {
	    	logError(e, module);
	    } catch (IllegalBlockSizeException e) {
	    	logError(e, module);
	    } catch (BadPaddingException e) {
	    	logError(e, module);
	    }

	    /*if (isLoggingDebug()) {
	    	logDebug("encryptPin : " + pin + " / " + encryptedEanHex, module);
	    }*/
	 
	    return encryptedEanHex;
	} 

	private void logDebug(String string, String module2) {
		logDebug("DEBUG " + module + ":" + string);
		
	}

	public boolean isLoggingDebug() {
		return true;
	}

	public void logError(Exception e, String module2) {
		logError(module2, e);
	}

	protected SecretKey getMwkKey() {
		if (mwk == null) {
			mwk = EncryptionUtil.getDesEdeKey(EncryptionUtil.getByteRange(getMwk(), 0, 24));
		}
		 
		
		logDebug("MWK : " + EncryptionUtil.toHexString(mwk.getEncoded()), module);
		
		 
		return mwk;
	}
	
	/**
	 * Decrypt an encrypted pin using the configured keys
	 * @param pin Hex String of the encrypted pin (EAN)
	 * @return Plain text String of the pin
	 */
	public String  decryptPin(String  pin) {
		// get the Cipher
		Cipher mwkCipher = EncryptionUtil.getCipher(this.getMwkKey(), Cipher.DECRYPT_MODE);
	 
		// decrypt pin
		String  decryptedPinString = null;
		try {
			byte[] decryptedEan = mwkCipher.doFinal(EncryptionUtil.fromHexString(pin));
			byte[] decryptedPin = EncryptionUtil.getByteRange(decryptedEan, 8, 8);
			decryptedPinString = new String (decryptedPin);
		} catch (IllegalStateException  e) {
			logError(e, module);
		} catch (IllegalBlockSizeException e) {
			logError(e, module);
		} catch (BadPaddingException e) {
			logError(e, module);
		}
	 
		
		logDebug("decryptPin : " + pin + " / " + decryptedPinString, module);
		
	
		return decryptedPinString;
	}
	
    protected byte[] getMwk() {
    	byte[] b =  EncryptionUtil.fromHexString( BBBConfigRepoUtils.getStringValue(BBBValueLinkConstants.VALUE_LINK_KEYS,BBBValueLinkConstants.MWK_LINK_KEY));
    	logDebug(new StringBuilder("MWK String :").append(b).append(":MWK length-").append(b.length).toString());
    	return b;
    }
     

	
	//DECRYPTED MWK (STEP B.9)
	public void setWorkingKey(String s) {
		workingKey = s;
	}

    public byte[] cryptoViaKek(byte[] content, int mode) {
        // open a cipher using the kek for transport
    	Cipher cipher = EncryptionUtil.getCipher(getKekKey(), mode);
        byte[] dec = new byte[0];
        try {
            dec = cipher.doFinal(content);
        } catch (IllegalStateException  e) {
            logError(e, module);
        } catch (IllegalBlockSizeException e) {
            logError(e, module);
        } catch (BadPaddingException e) {
            logError(e, module);
        }
        return dec;
    }
    
    
    public SecretKey getKekKey() {
        if (kek == null) {
            kek = EncryptionUtil.getDesEdeKey(getKek());
        }

        
         logDebug("Raw KEK : " + EncryptionUtil.toHexString(getKek()), module);
         logDebug("KEK : " + EncryptionUtil.toHexString(kek.getEncoded()), module);
       

        return kek;
    }
    
    /**
     * Use the KEK to encrypt a value usually the MWK
     * @param content byte array to encrypt
     * @return encrypted byte array
     */
    public byte[] encryptViaKek(byte[] content) {
        return cryptoViaKek(content, Cipher.ENCRYPT_MODE);
    }

    /**
     * Ue the KEK to decrypt a value
     * @param content byte array to decrypt
     * @return decrypted byte array
     */
    public byte[] decryptViaKek(byte[] content) {
        return cryptoViaKek(content, Cipher.DECRYPT_MODE);
    }
    
    public byte[] getKek() {
    	return getExchangeKey().getBytes();
    }
    	
    public String getExchangeKey() {
    	mExchangeKey = "215e10aad0bdcf7b34d317a236b81139";
		return mExchangeKey;
	}
    
    public void setExchangeKey(final String pExKey) {
    	mExchangeKey = pExKey;
	}

	public static void main(String[] args) {
		/*String pin = "01788105";
		ValueLinkEncryptor e = new ValueLinkEncryptor();
		e.setWorkingKey("e65d3e628975f79ed0626de52a6d891fe65d3e628975f79e");
		e.encryptPin(pin);*/
	}
}
