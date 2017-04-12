/**
 * 
 */
package com.bbb.account.signup;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.transaction.TransactionManager;

import atg.droplet.DropletException;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.common.BBBGenericFormHandler;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.utils.BBBUtility;

/**
 * @author rmahmo
 *
 */
public class BBBEmailSignUpFormHandler extends BBBGenericFormHandler {
	
	
	private static final String INTERNAL_ERROR = "There was an internal system error, please try again";
	private static final String EMAIL_EXIST = "You have already signed up, Thanks.";
	private static final String INVALID_EMAIL = "The email address you entered is not valid";
	private static final String EMAIL_NOT_MATCH = "The email must match the confirm email";
	private static final String INVALID_ZIP = "The zip code you entered is not valid";
	
	private String email;
	private String emailConfirm;
	private String zipCode;
	private String sourceCode;
	private boolean success;
	private BBBEmailSignUpManager signUpManager;
	private TransactionManager transactionManager;
	
	/**
	 * default constructor
	 */
	public BBBEmailSignUpFormHandler(){
		super();
	}
	
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the emailConfirm
	 */
	public String getEmailConfirm() {
		return emailConfirm;
	}

	/**
	 * @param emailConfirm the emailConfirm to set
	 */
	public void setEmailConfirm(String emailConfirm) {
		this.emailConfirm = emailConfirm;
	}

	/**
	 * @return the zipCode
	 */
	public String getZipCode() {
		return zipCode;
	}
	/**
	 * @param zipCode the zipCode to set
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	/**
	 * @return the signUpManager
	 */
	public BBBEmailSignUpManager getSignUpManager() {
		return signUpManager;
	}
	/**
	 * @param signUpManager the signUpManager to set
	 */
	public void setSignUpManager(BBBEmailSignUpManager signUpManager) {
		this.signUpManager = signUpManager;
	}
	/**
	 * @return the transactionManager
	 */
	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	/**
	 * @param transactionManager the transactionManager to set
	 */
	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * @param success the success to set
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * @return the sourceCode
	 */
	public String getSourceCode() {
		return sourceCode;
	}

	/**
	 * @param sourceCode the sourceCode to set
	 */
	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	/**
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean handleSignUp(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		logDebug("handleSignUp:::");
		logDebug("email::" +this.getEmail());
		logDebug("emailConfirm::" +this.getEmailConfirm());
		logDebug("zipCode::" +this.getEmailConfirm());
		logDebug("sourceCode::"+this.getSourceCode());
		
		TransactionDemarcation td = new TransactionDemarcation();
        boolean rollback = true;
        setSuccess(false);
		try{
			td.begin(getTransactionManager());
			boolean valid = validateInput();
			if(valid){
				String siteID = SiteContextManager.getCurrentSiteId();
				String sourceCode = this.getSourceCode();
				//if it's BABY, append siteID with source code
				if(BBBCoreConstants.SITE_BBB.equalsIgnoreCase(siteID)){
					sourceCode = sourceCode + BBBCoreConstants.SITE_BBB;
				}
				String id = this.getEmail() + ":" + sourceCode;
				logDebug("siteID::"+siteID);
				logDebug("sourceCode::"+sourceCode);
				logDebug("id::"+id);
				boolean recordExist = getSignUpManager().isRecordExist(id);
				if(recordExist){
					this.addFormException(new DropletException(EMAIL_EXIST));
				}else{
					this.getSignUpManager().addSignUpInformation(id, this.getZipCode());
					this.setSuccess(true);
				}
			}
			rollback = false;
		}catch(RepositoryException re){
			logError("RepositoryExceptoin" + re);
			this.addFormException(new DropletException(INTERNAL_ERROR));
		}
		catch (TransactionDemarcationException tde) {
	        logError("Error ending transaction::" + tde);
	        this.addFormException(new DropletException(INTERNAL_ERROR));
	    }finally{
			try {
                td.end(rollback);
            } catch (TransactionDemarcationException tde) {
                logError("Error ending transaction::" + tde);
                this.addFormException(new DropletException(INTERNAL_ERROR));
            }
		}
		return true;
	}
	
	/**
	 * check all inputs and return true if all are valid
	 * @return
	 */
	private boolean validateInput(){
		if(BBBUtility.isValidEmail(this.getEmail())){
			if(this.getEmailConfirm() != null && !this.getEmailConfirm().equals("")){
				if(!BBBUtility.isSameEmail(this.getEmail(), this.getEmailConfirm())){
					this.addFormException(new DropletException(EMAIL_NOT_MATCH));
					return false;
				}
			}
			if(this.getZipCode() != null && !this.getZipCode().equals("")){
				if(!BBBUtility.isValidZip(this.getZipCode())){
					this.addFormException(new DropletException(INVALID_ZIP));
					return false;	
				}
			}
		}else{
			this.addFormException(new DropletException(INVALID_EMAIL));
			return false;
		}
		return true;
	}
}