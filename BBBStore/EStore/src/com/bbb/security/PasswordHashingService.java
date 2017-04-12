package com.bbb.security;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.xml.bind.DatatypeConverter;


import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;

import com.bbb.common.BBBGenericService;
import com.bbb.framework.performance.BBBPerformanceMonitor;

/**
 * @author rsirangu
 * 
 */
public class PasswordHashingService extends BBBGenericService {
	
	/**
	 * to hold property value
	 */
	public static final String PROPERTY_NAME_VALUE = "value";
	
	/**
	 * to hold property iterations
	 */
	public static final String PROPERTY_ITERATIONS_VALUE = "iterations";	
	
	/**
	 * to hold property value
	 */
	private static final String SECID = "secId";
	
	/**
	 * to hold item descriptor name value
	 */
	private static final String ITEMDESCRIPTORNAME = "passwordSalt";
	
	
	/**
	 * to hold Algorithm Name value
	 */
	private static final String SHA256 = "SHA-256";
	
	/**
	 * to hold UTF-8 String
	 */
	private static final String  UTF8 = "UTF-8";
	
	/**
	 * Password Hasher Items Descriptor
	 */
	private String mPhashDesc;
	
	/**
	 * to Get hasher description
	 * @return mPhashDesc - return hasher description
	 */
	public String getPhashDesc() {
		return mPhashDesc;
	}

	/**
	 * @param pPhashDesc 
	 */
	public void setPhashDesc(String pPhashDesc) {
		this.mPhashDesc = pPhashDesc;
	}
	/**
	 * to hold pwdSaltRepo repository
	 */
	private Repository pwdSaltRepo;

	 /**
	  * To return password salt repository
	 * @return pwdSaltRepo - return password salt repository
	 */
	public Repository getPwdSaltRepo() {
				return pwdSaltRepo;
			}
			
	 /**
	  * To set password repository
	 * @param pPwdSaltRepo - set password repository
	 */
	public void setPwdSaltRepo(Repository pPwdSaltRepo) {
				this.pwdSaltRepo = pPwdSaltRepo;
			}

	/**
	 * This method is used to create a password salt value.
	 * @param pkey - key for salt
	 * @param kValue - salt
	 * @param pIterations
	 * @return true / false --> true only if salt is get created in database
	 */
	public boolean createPasswordSalt(String pkey , String kValue , int pIterations){
		 BBBPerformanceMonitor.start( PasswordHashingService.class.getName() + " : " + "createPasswordSalt(String pkey , String kValue)");		  
		  
		  logDebug("createPasswordSalt : START");
		  
		MutableRepository  hasherRepo = (MutableRepository ) getPwdSaltRepo();
		  try {
			  MutableRepositoryItem mutableRepoItem = hasherRepo.createItem(pkey, getPhashDesc());
			  mutableRepoItem.setPropertyValue(PROPERTY_NAME_VALUE, kValue);
			  mutableRepoItem.setPropertyValue(PROPERTY_ITERATIONS_VALUE, pIterations);			  
			  hasherRepo.addItem(mutableRepoItem);	
			 
			  logDebug("createPasswordSalt : END");
			 
			  BBBPerformanceMonitor.end( PasswordHashingService.class.getName() + " : " + "createPasswordSalt(String pkey , String kValue)");
			  return true;
		} catch (RepositoryException e) {
			  logDebug(e.getMessage());
			  BBBPerformanceMonitor.end( PasswordHashingService.class.getName() + " : " + "createPasswordSalt(String pkey , String kValue)");
			 return false;
		}
	}
	
	/**
	 * This method is used to update a password salt value.
	 * @param pkey
	 * @param kValue
	 * @param pIterations
	 * @return true / false --> true only if salt is get updated in database
	 */
	public boolean updatePasswordSalt(String pOldKey ,String pNewkey , String pSalt , int pIterations){	
		 BBBPerformanceMonitor.start( PasswordHashingService.class.getName() + " : " + "updatePasswordSalt(String pkey , String kValue)");		  
		  
		  logDebug("updatePasswordSalt : START");
		  
		  MutableRepository  hasherRepo = (MutableRepository ) getPwdSaltRepo();
		   MutableRepositoryItem mutableUser = null;
		try {
			   mutableUser = hasherRepo.getItemForUpdate(pOldKey,getPhashDesc());
			   hasherRepo.removeItem(mutableUser.getRepositoryId(),getPhashDesc());
			   
			   MutableRepositoryItem mutableRepoItem = hasherRepo.createItem(pNewkey, getPhashDesc());
				  mutableRepoItem.setPropertyValue(PROPERTY_NAME_VALUE, pSalt);
				  mutableRepoItem.setPropertyValue(PROPERTY_ITERATIONS_VALUE,pIterations );			  
				  hasherRepo.addItem(mutableRepoItem);	
				  
				  
			  
				  logDebug("updatePasswordSalt : END");
				
				  BBBPerformanceMonitor.end( PasswordHashingService.class.getName() + " : " + "updatePasswordSalt(String pkey , String kValue)");
			   return true;
		} catch (RepositoryException e) {
			logError(e.getMessage(),e);
			 BBBPerformanceMonitor.end( PasswordHashingService.class.getName() + " : " + "updatePasswordSalt(String pkey , String kValue)");
			return false;
		}
		   
	}
	
	/**
	 * This method will convert any input string into hashed value by using a SHA-256 Algorithm 
	 * 
	 * @param base - input string for hashing
	 * @return string - this method will return an hashed value in String format
	 */
	public  String sha256(String base) {
		 BBBPerformanceMonitor.start( PasswordHashingService.class.getName() + " : " + "sha256(String base)");		  
		  
		  logDebug("sha256 : START");
		  
		MessageDigest digest;
		  String hashKey = null;
		try {
			digest = MessageDigest.getInstance(SHA256);
	        byte[] hash;
			try {
				hash = digest.digest(base.getBytes(UTF8));
		        hashKey = DatatypeConverter.printBase64Binary(hash);
			} catch (UnsupportedEncodingException e) {
				logError(e.getMessage(),e);
			}
		} catch (NoSuchAlgorithmException e) {
			logError(e.getMessage(),e);
		}
		
		 logDebug("sha256 : END");
		
		 BBBPerformanceMonitor.end( PasswordHashingService.class.getName() + " : " + "sha256(String base)");
		return hashKey;
		
	}
	/**
	 * This method will be used to get a salt from Java Secure Random Generation.
	 * @return - generate slat value
	 * @throws NoSuchAlgorithmException
	 */
	public  byte[] generateSalt() throws NoSuchAlgorithmException {
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		// Generate a 8 byte (64 bit) salt as recommended by RSA PKCS5
		byte[] salt = new byte[8];
		random.nextBytes(salt);
		return salt;
	}
	/**
	 * This method is used to get password salt value
	 * @param pkey - key
	 * @return password salt value
	 */
	public RepositoryItem getPasswordSalt(String pkey){	
		
		logDebug("sha256 : start");
		
		 BBBPerformanceMonitor.start( PasswordHashingService.class.getName() + " : " + "getPasswordSalt(String pkey)");
		 RepositoryItemDescriptor userDesc;
		 RepositoryItem fRepoResult = null;
		try {
			userDesc = getPwdSaltRepo().getItemDescriptor(ITEMDESCRIPTORNAME);
			RepositoryView repoView = userDesc.getRepositoryView();
			QueryBuilder queryBuilder = repoView.getQueryBuilder();
			QueryExpression key = queryBuilder.createPropertyQueryExpression(SECID);
            QueryExpression cnsKeyValue = queryBuilder.createConstantQueryExpression(pkey);
            Query resultQuery  =
            	queryBuilder.createPatternMatchQuery(key, cnsKeyValue, QueryBuilder.EQUALS);
            // execute the query and get the results
            RepositoryItem[]  repoResult = repoView.executeQuery(resultQuery);
            if(repoResult!=null && repoResult.length >=1){
            	fRepoResult = repoResult[0];            	
            }
		} catch (RepositoryException e) {
			logError(e.getMessage(),e);
		}
		
		 logDebug("sha256 : end");
		
		 BBBPerformanceMonitor.end( PasswordHashingService.class.getName() + " : " + "getPasswordSalt(String pkey)");
		return fRepoResult;
	}
}
