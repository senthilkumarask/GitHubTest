/*
 *
 * File  : AESEncryptorComponent.java
 * Project:     BBB
 */
package com.bbb.framework.crypto;

import java.io.Serializable;

import atg.core.util.Base64;
import atg.nucleus.ServiceException;

/**
 * This is a Dynamo component which provides AES encryption and decryption functionality.
 * 
 *
 */
public class AESEncryptorComponent extends AbstractEncryptorComponent implements Serializable {

    /**
     * generated serial version uid.
     */
    private static final long serialVersionUID = 5899614270931897833L;

    /**
     * This method sets up the AES Encryptor.
     * 
     * @see com.bbb.framework.crypto.AbstractEncryptorComponent#setupEncryptor()
     * @throws EncryptorException
     *                 if the encryptor could not be setup.
     * @throws ServiceException 
     */
    public void setupEncryptor() throws EncryptorException, ServiceException {
		final byte[] keyBytes = Base64.decodeToByteArray(getKey());
		setEncryptor(new AESEncryptor(keyBytes));		
    }
    
    public AESEncryptorComponent(){
    	//default constructor
    }

	@Override
	public void setupEncryptor(String pKey) throws EncryptorException {
		final byte[] keyBytes = Base64.decodeToByteArray(pKey);
		setEncryptor(new AESEncryptor(keyBytes));
	}
}
