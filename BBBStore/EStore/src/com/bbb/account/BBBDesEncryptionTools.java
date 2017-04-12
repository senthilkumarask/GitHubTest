package com.bbb.account;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import atg.core.util.Base64;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

public class BBBDesEncryptionTools extends BBBGenericService {
	
	private final static String KIRSCH_KEYS="KirschKeys";
	private final static String ENC_PASS_PHRASE="encPassPhrase";
	private final static String ENC_ITR_COUNT="encIterationCount";
	private final static String ENC_SALT="encSalt";
	
	private final static int HEX_RADIX=16;
	private final static String ENC_PBE_WITH_MD5_AND_DES="PBEWithMD5AndDES";
	
	
	private transient Cipher mEcipher;
	private transient Cipher mDcipher;
	private transient boolean initialized;
	private transient String mPassPhrase;
	
	private transient byte[] mSalt;

	// Iteration count
	private transient int mIterationCount;
	
	/**
	 * Instance of BBBCatalogTools.
	 */
	private transient BBBCatalogTools mBBBCatalogTools;
	
	/**
	 * @return the mBBBCatalogTools
	 */
	public BBBCatalogTools getBBBCatalogTools() {
		return mBBBCatalogTools;
	}
	/**
	 * @param mBBBCatalogTools the mBBBCatalogTools to set
	 */
	public void setBBBCatalogTools(final BBBCatalogTools pBBBCatalogTools) {
		this.mBBBCatalogTools = pBBBCatalogTools;
	}



	private void initDependencies() throws BBBSystemException, BBBBusinessException{
		try{
			final Map<String, String> levelorKeys = getBBBCatalogTools().getConfigValueByconfigType(KIRSCH_KEYS);
				logDebug("Levelor map is:" + levelorKeys);
			mPassPhrase =  levelorKeys.get(ENC_PASS_PHRASE);
			mIterationCount =  Integer.parseInt(levelorKeys.get(ENC_ITR_COUNT));
			final String strSalt= levelorKeys.get(ENC_SALT);
			final String[] arrSalt = strSalt.split(",");
			mSalt= new byte[arrSalt.length];
			for(int i=0;i<arrSalt.length;i++){
				mSalt[i]=(byte)Integer.parseInt(arrSalt[i],HEX_RADIX);
			}
		}catch(NumberFormatException nfe){
			logError(LogMessageFormatter.formatMessage(null, "NumberFormatException - Error in parsing Integer during Encryption", BBBCoreErrorConstants.ACCOUNT_ERROR_1003 ), nfe);
		}
	}
	private synchronized void init(){
		if(!initialized){
			try {
				initDependencies();
				final KeySpec keySpec = new PBEKeySpec(mPassPhrase.toCharArray(),mSalt,mIterationCount);
				final SecretKey key = SecretKeyFactory.getInstance(ENC_PBE_WITH_MD5_AND_DES)
						.generateSecret(keySpec);
				final AlgorithmParameterSpec paramSpec = new PBEParameterSpec(mSalt, mIterationCount);
				mEcipher = Cipher.getInstance(key.getAlgorithm());
				mDcipher = Cipher.getInstance(key.getAlgorithm());
				mEcipher.init(Cipher.ENCRYPT_MODE, key,paramSpec);
				mDcipher.init(Cipher.DECRYPT_MODE, key,paramSpec);
				initialized=true;
			} catch (javax.crypto.NoSuchPaddingException e) {
				logError(LogMessageFormatter.formatMessage(null, "NoSuchPaddingException -during Encryption Decryption initilaziation", BBBCoreErrorConstants.ACCOUNT_ERROR_1010 ), e);
			} catch (java.security.NoSuchAlgorithmException e) {
				logError(LogMessageFormatter.formatMessage(null, "NoSuchAlgorithmException -during Encryption Decryption initilaziation", BBBCoreErrorConstants.ACCOUNT_ERROR_1021 ), e);
			} catch (java.security.InvalidKeyException e) {
				logError(LogMessageFormatter.formatMessage(null, "InvalidKeyException -during Encryption Decryption initilaziation", BBBCoreErrorConstants.ACCOUNT_ERROR_1022 ), e);
			} catch (InvalidKeySpecException e) {
				logError(LogMessageFormatter.formatMessage(null, "InvalidKeySpecException -during Encryption Decryption initilaziation", BBBCoreErrorConstants.ACCOUNT_ERROR_1055 ), e);
			} catch (InvalidAlgorithmParameterException e) {
				logError(LogMessageFormatter.formatMessage(null, "InvalidAlgorithmParameterException -during Encryption Decryption initilaziation", BBBCoreErrorConstants.ACCOUNT_ERROR_1057 ), e);
			} catch (BBBSystemException e) {
				logError(LogMessageFormatter.formatMessage(null, "BBBSystemException -during Encryption Decryption initilaziation", BBBCoreErrorConstants.ACCOUNT_ERROR_1058 ), e);
			} catch (BBBBusinessException e) {
				logError(LogMessageFormatter.formatMessage(null, "BBBBusinessException -during Encryption Decryption initilaziation", BBBCoreErrorConstants.ACCOUNT_ERROR_1059 ), e);
			}
		}
	}
	
	public String encrypt(final String str) {
		try {
			if(!initialized){
				init();
			}
			if(BBBUtility.isEmpty(str)){
				return null;
			}
			// Encode the string into bytes using utf-8
			final byte[] utf8 = str.getBytes("UTF8");

			// Encrypt
			final byte[] enc = mEcipher.doFinal(utf8);

			// Encode bytes to base64 to get a string
			return Base64.encodeToString(enc);
		} catch (javax.crypto.BadPaddingException e) {
			logError(LogMessageFormatter.formatMessage(null, "BadPaddingException -during Encryption", BBBCoreErrorConstants.ACCOUNT_ERROR_1074 ), e);
		} catch (IllegalBlockSizeException e) {
			logError(LogMessageFormatter.formatMessage(null, "IllegalBlockSizeException -during Encryption", BBBCoreErrorConstants.ACCOUNT_ERROR_1075 ), e);
		} catch (UnsupportedEncodingException e) {
			logError(LogMessageFormatter.formatMessage(null, "UnsupportedEncodingException -during Encryption", BBBCoreErrorConstants.ACCOUNT_ERROR_1076 ), e);
		}
		return null;
	}

	public String decrypt(final String str) {
		try {
			if(!initialized){
				init();
			}
			if(BBBUtility.isEmpty(str)){
				return null;
			}
			// Decode base64 to get bytes
			final byte[] dec = Base64.decodeToByteArray(str);

			// Decrypt
			final byte[] utf8 = mDcipher.doFinal(dec);

			// Decode using utf-8
			return new String(utf8, "UTF8");
		} catch (javax.crypto.BadPaddingException e) {
			logError(LogMessageFormatter.formatMessage(null, "BadPaddingException -during Decryption", BBBCoreErrorConstants.ACCOUNT_ERROR_1077 ), e);
		} catch (IllegalBlockSizeException e) {
			logError(LogMessageFormatter.formatMessage(null, "IllegalBlockSizeException -during Decryption", BBBCoreErrorConstants.ACCOUNT_ERROR_1078 ), e);
		} catch (UnsupportedEncodingException e) {
			logError(LogMessageFormatter.formatMessage(null, "UnsupportedEncodingException -during Decryption", BBBCoreErrorConstants.ACCOUNT_ERROR_1085 ), e);
		}
		return null;
	}
}
