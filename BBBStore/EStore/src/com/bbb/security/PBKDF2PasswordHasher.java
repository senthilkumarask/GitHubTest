package com.bbb.security;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.codec.binary.Hex;
import atg.nucleus.Nucleus;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;
import atg.repository.RepositoryItem;
import atg.security.DigestPasswordHasher;
import atg.security.PasswordHasher;
import atg.security.PasswordHasher2Adapter;
 
 
/**
 * This class will be used for advanced PBKDF2 password hashing mechanism for new registrations 
 *  and legacy customer will be handled as per old Mechanism.
 * 
 * @author rsirangu
 *
 */
public class PBKDF2PasswordHasher extends PasswordHasher2Adapter {

	/**
	 * to hold serial number.
	 */
	private static final long serialVersionUID = -9109787715803318366L;
	
	private static final ApplicationLogging MLOGGING =
		    ClassLoggingFactory.getFactory().getLoggerForClass(PBKDF2PasswordHasher.class);

	/**
	 * to hold DigestPasswordHasher Algorithm.
	 */
	public static final DigestPasswordHasher mDgestPwdHasher = (DigestPasswordHasher)Nucleus.getGlobalNucleus().resolveName("/atg/dynamo/security/DigestPasswordHasher");

	/**
	 * to hold PasswordHashingService Algorithm.
	 */
	public static final PasswordHashingService mPwdHashingService = (PasswordHashingService)Nucleus.getGlobalNucleus().resolveName("/com/bbb/security/PasswordHashingService");
	 

	private String algorithm;

	private String encoding;

		/**
	 * to hold number of iterations.
	 */
	private int mNumIterations;

	public int getNumIterations() {
		return mNumIterations;
	}

	public void setNumIterations(int pNumIterations) {
		this.mNumIterations = pNumIterations;
	}
	/* (non-Javadoc)
	 * @see atg.security.PasswordHasher2#checkPassword(java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public boolean checkPassword(String pLogin, String pLoginPassword, String pEncryptedPassword, Object pHashKey) {
		if(pLoginPassword!= null && pLoginPassword.equals(pEncryptedPassword) ){
			return true;
		}else{
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see atg.security.PasswordHasher2#encryptPassword(java.lang.String, java.lang.String)
	 */
	@Override
	public String encryptPassword(String pLogin, String pPassword) {
		String key = mPwdHashingService.sha256(pLogin);
		RepositoryItem repoItem = mPwdHashingService.getPasswordSalt(key);
		String saltValue = null;		 
		int iterations = getNumIterations();
		if(repoItem!=null){
			saltValue = (String)repoItem.getPropertyValue(PasswordHashingService.PROPERTY_NAME_VALUE);
			iterations = (Integer)repoItem.getPropertyValue(PasswordHashingService.PROPERTY_ITERATIONS_VALUE);
		}		
		byte[] saltByte = null;	
		/*try {
			/*if(saltValue==null){
				saltByte = generateSalt();
				saltValue = DatatypeConverter.printBase64Binary( saltByte);
				mPwdHashingService.createPasswordSalt(key, saltValue , iterations);						   
			}else{
				saltByte = DatatypeConverter.parseBase64Binary(saltValue);
			}*/
		if(saltValue!=null){
			saltByte = DatatypeConverter.parseBase64Binary(saltValue);
			try {
				byte[] encryptedByte =  getEncryptedPassword(pPassword, saltByte,iterations);
				return DatatypeConverter.printBase64Binary(encryptedByte);
			} catch (InvalidKeySpecException e) {
				   MLOGGING.logError("Error encrypting password "+ e.getMessage());
			}	         
		    catch (NoSuchAlgorithmException e) {
		    	 MLOGGING.logError("Error encrypting password "+ e.getMessage());		     }        
		}else{
			return mDgestPwdHasher.encryptPassword(pPassword);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see atg.security.PasswordHasher2#getLoginPasswordHasher()
	 */
	@Override
	public PasswordHasher getLoginPasswordHasher() {
	 
			PBKDF2PasswordHasher hasher = new PBKDF2PasswordHasher();
			hasher.setNumIterations(getNumIterations());
			hasher.setEncoding(getEncoding());
			hasher.setAlgorithm(getAlgorithm());
			hasher.setPwdHasherComponentPath(getPwdHasherComponentPath());			 
			return hasher;
			 
		 
	}

	/* (non-Javadoc)
	 * @see atg.security.PasswordHasher2#getPasswordHashKey()
	 */
	@Override
	public Object getPasswordHashKey() {
		return new Long(new SecureRandom().nextLong());
	}

	/* (non-Javadoc)
	 * @see atg.security.PasswordHasher2#hashPasswordForLogin(java.lang.String, java.lang.String)
	 */
	@Override
	public String hashPasswordForLogin(String pLogin, String pPassword) {
		String key = mPwdHashingService.sha256(pLogin);
		RepositoryItem repoItem = mPwdHashingService.getPasswordSalt(key);
		String saltValue = null;
		int iterations = getNumIterations();
		if(repoItem!=null){
			saltValue = (String)repoItem.getPropertyValue(PasswordHashingService.PROPERTY_NAME_VALUE);
			if(repoItem.getPropertyValue(PasswordHashingService.PROPERTY_ITERATIONS_VALUE)!=null){
				iterations = (Integer)repoItem.getPropertyValue(PasswordHashingService.PROPERTY_ITERATIONS_VALUE);
			}
		}
		byte[] salt = null;
		if(saltValue!=null){
			salt = DatatypeConverter.parseBase64Binary(saltValue);			
			byte[] encryptedByte = null;		
			try {
				encryptedByte = getEncryptedPassword(pPassword, salt , iterations);
			} catch (NoSuchAlgorithmException nse) {
				 MLOGGING.logError("Error hashing password "+ nse.getMessage());
			} catch (InvalidKeySpecException e) {
				 MLOGGING.logError("Error hashing password "+ e.getMessage());
			}				
			String inputPasswordString = DatatypeConverter.printBase64Binary(encryptedByte); 
			return inputPasswordString;
		}else{
			return mDgestPwdHasher.hashPasswordForLogin(pPassword);
		}
	}	

	/**
	 * This method will be used to get a salt from Java Secure Random Generation.
	 * @return - generate slat value
	 * @throws NoSuchAlgorithmException
	 */
	public String generateSalt() {
		// Generate a 8 byte (64 bit) salt as recommended by RSA PKCS5
		byte[] salt = new byte[8];
		try
		{
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			random.nextBytes(salt);
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return new String(Hex.encodeHex(salt));
	}

	/** This method will be used to generate encryption value of PBKDF2 algorithm. 
	 * @param password - password value
	 * @param salt - salt value
	 * @return  byte  - byte
	 * @throws NoSuchAlgorithmException - No Such Algorithm
	 * @throws InvalidKeySpecException - In Valid Key Spec
	 */
	public  byte[] getEncryptedPassword(String password, byte[] salt , int pIterations)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		String algorithm = "PBKDF2WithHmacSHA1";
		// SHA-1 generates 80 bit hashes, so that's what makes sense here
		int derivedKeyLength = 80;
		int iterations = pIterations;
		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength);
		SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);
		return f.generateSecret(spec).getEncoded();
	}

	 

	/**
	 * @return the algorithm
	 */
	public String getAlgorithm() {
		return algorithm;
	}

	/**
	 * @param algorithm the algorithm to set
	 */
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * @return the encoding
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * @param encoding the encoding to set
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	 

		
}