package com.bbb.internationalshipping.fulfillment.poservice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Iterator;
import java.util.Properties;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.PublicKeyDataDecryptorFactory;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyDataDecryptorFactory;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.framework.crypto.AbstractEncryptorComponent;
import com.bbb.utils.BBBConfigRepoUtils;

public class IntlPODecryptService extends BBBGenericService{


	private AbstractEncryptorComponent mEncryptorComponent;
	

	/**
	 * decrypt the passed in message stream
	 * 
	 * @param encrypted
	 *            The message to be decrypted.
	 * @param passPhrase
	 *            Pass phrase (key)
	 * 
	 * @return Clear text as a byte array. I18N considerations are not handled
	 *         by this routine
	 * @exception IOException
	 * @exception PGPException
	 * @exception NoSuchProviderException
	 */
	public  String decrypt(byte[] encrypted, InputStream keyIn, char[] password)
			throws IOException, PGPException, NoSuchProviderException {
		Security.addProvider(new BouncyCastleProvider());
		InputStream in = new ByteArrayInputStream(encrypted);

		InputStream decodedStream = PGPUtil.getDecoderStream(in);

		PGPObjectFactory pgpFactory = new PGPObjectFactory(decodedStream);
		PGPEncryptedDataList encyptedData = null;
		Object obj = pgpFactory.nextObject();

		//
		// the first object might be a PGP marker packet.
		//
		if (obj instanceof PGPEncryptedDataList) {
			encyptedData = (PGPEncryptedDataList) obj;
		} else {
			encyptedData = (PGPEncryptedDataList) pgpFactory.nextObject();
		}

		//
		// find the secret key
		//
		Iterator it = encyptedData.getEncryptedDataObjects();
		PGPPrivateKey privateKey = null;
		PGPPublicKeyEncryptedData pbe = null;
		PGPSecretKeyRingCollection pgpSecretKeyRingColl = new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(keyIn));

		while (privateKey == null && it.hasNext()) {
			pbe = (PGPPublicKeyEncryptedData) it.next();

			privateKey = findSecretKey(pgpSecretKeyRingColl, pbe.getKeyID(), password);

		}
		logDebug(" private key "+privateKey); 
		if (privateKey == null) {
			throw new IllegalArgumentException(
					"secret key for message not found.");
		}
		PublicKeyDataDecryptorFactory decryptorFactory=new BcPublicKeyDataDecryptorFactory(privateKey);


		InputStream clear = pbe.getDataStream(decryptorFactory);

		PGPObjectFactory pgpFact = new PGPObjectFactory(clear);


		PGPLiteralData pgpLiteralData = (PGPLiteralData) pgpFact.nextObject();

		InputStream unc = pgpLiteralData.getInputStream();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int ch;

		while ((ch = unc.read()) >= 0) {
			out.write(ch);

		}
		String decryptedPOFile=out.toString();
		out.close();
		return decryptedPOFile;
	}

	private static PGPPrivateKey findSecretKey(
			PGPSecretKeyRingCollection pgpSec, long keyID, char[] pass)
					throws PGPException, NoSuchProviderException {
		PGPSecretKey pgpSecKey = pgpSec.getSecretKey(keyID);

		if (pgpSecKey == null) {
			return null;
		}
		PBESecretKeyDecryptor decryptor = new BcPBESecretKeyDecryptorBuilder(new BcPGPDigestCalculatorProvider()).build(pass);
		return pgpSecKey.extractPrivateKey(decryptor);
	}



	public String  runtimePOFileDecryption(File purchaseOrderFile) throws BBBBusinessException  {
		byte[] encFromFile;
		String decryptFile=null;
		File file = null;
		InputStream inputStream = null;
		final Properties properties = new Properties();
		String key = null;
		String pgpPassPhrase=null;
		FileInputStream secKey = null;

		try {
			encFromFile = getBytesFromFile(purchaseOrderFile);
			String pgpKey = BBBConfigRepoUtils.getStringValue(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING, BBBInternationalShippingConstants.PGP_KEY_PATH);
			//String pgpPassPhrase=BBBConfigRepoUtils.getStringValue(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING, BBBInternationalShippingConstants.PGP_PASS_PHRASE);
			
	//		file = new File(getResourceFilePath());
			file = new File(BBBConfigRepoUtils.getStringValue(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING, BBBInternationalShippingConstants.PGP_FILE_PATH));
			inputStream = new FileInputStream( file );
			properties.load( inputStream );
			pgpPassPhrase = properties.getProperty(BBBInternationalShippingConstants.PO_DECRYPT_KEY);
			
			/*mEncryptorComponent = (AbstractEncryptorComponent) Nucleus
					.getGlobalNucleus().resolveName("/com/bbb/framework/crypto/AESEncryptorComponent");
			pgpPassPhrase=mEncryptorComponent.decryptString(key);
			logDebug(" private key "+pgpKey+" pgppass phrase "+pgpPassPhrase);  */
			secKey = new FileInputStream(pgpKey);
			decryptFile=  this.decrypt(encFromFile, secKey, pgpPassPhrase.toCharArray());
		} catch (IOException e) {
			throw new BBBBusinessException(BBBInternationalShippingConstants.DECRYPT_IO_ERROR_CODE,
					BBBInternationalShippingConstants.DECRYPT_IO_MESSAGE);
		} catch (NoSuchProviderException e) {
			throw new BBBBusinessException(BBBInternationalShippingConstants.NO_SUCH_PROVIDER_ERROR_CODE,
					BBBInternationalShippingConstants.NO_SUCH_PROVIDER_MESSAGE);
		} catch (PGPException e) {
			throw new BBBBusinessException(BBBInternationalShippingConstants.PGP_EXCEPTION_ERROR_CODE,
					BBBInternationalShippingConstants.PGP_EXCEPTION_MESSAGE);
		}
		finally{
			if(secKey != null )
				try {
					secKey.close();
				} catch (IOException e) {
					logError("IO exception while closing the stream", e);
				}
		}
		return decryptFile;
	}


	public static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int)length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			is.close();
			throw new IOException("Could not completely read file "+file.getName());
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;
	}


}
