package com.bbb.account.signup;

import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.bbb.common.BBBGenericService;

/**
 * @author rmahmo
 *
 */
public class BBBEmailSignUpManager extends BBBGenericService {
	
	//constants
	private static final String ITEM_SIGN_UP = "signUp";
	//private static final String PROP_EMAIL = "email";
	private static final String PROP_ZIP_CODE = "zipcode";
	//private static final String PROP_SOURCE_CODE = "sourceCode";
	
	private MutableRepository signUpRepository;
	
	/**
	 * default constructor
	 */
	public BBBEmailSignUpManager(){
		super();
	}

	/**
	 * @return the signUpRepository
	 */
	public MutableRepository getSignUpRepository() {
		return signUpRepository;
	}

	/**
	 * @param signUpRepository the signUpRepository to set
	 */
	public void setSignUpRepository(MutableRepository signUpRepository) {
		this.signUpRepository = signUpRepository;
	}
	
	/**
	 * 
	 * @param pEmail
	 * @param pZipcode
	 * @throws RepositoryException
	 */
	public void addSignUpInformation(String pId, String pZipcode) throws RepositoryException{
		logDebug("addSignUpInformation::");
		logDebug("pId::"+pId);
		logDebug("pZipcode::"+pZipcode);
		MutableRepositoryItem item = getSignUpRepository().createItem(pId, ITEM_SIGN_UP);
		item.setPropertyValue(PROP_ZIP_CODE, pZipcode);
		this.getSignUpRepository().addItem(item);
	}
	
	/**
	 * 
	 * @param pId
	 * @return
	 * @throws RepositoryException 
	 */
	public boolean isRecordExist(String pId) throws RepositoryException{
		logDebug("isRecordExist::");
		logDebug("record Id::"+pId);
		RepositoryItem item = getSignUpRepository().getItem(pId, ITEM_SIGN_UP);
		if(item != null){
			logDebug("record already exists::");
			return true;
		}
		logDebug("record doesn't exist::");
		return false;
	}
}