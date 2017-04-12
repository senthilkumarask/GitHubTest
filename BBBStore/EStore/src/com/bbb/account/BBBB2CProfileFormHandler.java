/*package com.bbb.account;

import java.util.HashMap;

import atg.commerce.profile.CommerceProfileFormHandler;

public class BBBB2CProfileFormHandler extends CommerceProfileFormHandler {
	 
	private String mNameOnCard;
	private String mSecureCode;
	private String mCreditCardNumber;
	private BBBProfileTools mProfileTools;
	private String mShallowCreditCardPropertyNames;
	HashMap mEditValue;
	
	public BBBB2CProfileFormHandler()
    {
        mEditValue = new HashMap();
        
    }
	*//**
	 * @return the mShallowCreditCardPropertyNames
	 *//*
	public String getShallowCreditCardPropertyNames() {
		return mShallowCreditCardPropertyNames;
	}

	*//**
	 * @param mShallowCreditCardPropertyNames the mShallowCreditCardPropertyNames to set
	 *//*
	public void setShallowCreditCardPropertyNames(
			String pShallowCreditCardPropertyNames) {
		this.mShallowCreditCardPropertyNames = pShallowCreditCardPropertyNames;
	}

	private BBBPropertyManager mPmgr;
	*//**
	 * @return the mCreditCardNumber
	 *//*
	public String getCreditCardNumber() {
		return mCreditCardNumber;
	}

	*//**
	 * @param mCreditCardNumber the mCreditCardNumber to set
	 *//*
	public void setCreditCardNumber(String pCreditCardNumber) {
		this.mCreditCardNumber = pCreditCardNumber;
	}

	public String getNameOnCard() {
		return mNameOnCard;
	}
	 
	public void setNameOnCard(String pNameOnCard) {
		this.mNameOnCard = pNameOnCard;
	}
	 
	public String getSecureCode() {
		return mSecureCode;
	}
	
	public void setSecureCode(String pSecureCode) {
		this.mSecureCode = pSecureCode;
	}
	
	public void preHandleCreateNewCreditCardAndAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
	        throws ServletException, IOException
	    {
	    	//System.out.println("validations for the secure code are here"+getSecureCode());
	    	//System.out.println("cc number"+getCreditCardNumber());
	    	addFormException(new DropletException("KEY_EMAIL_FIRST_PART_MAX_LENGTH_64"));
	    }
	
		public void handlePreferredBilling(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
	    throws ServletException, IOException
	{
		
		//System.out.println("hashMap values"+getEditValue());
		
		//return super.handleCreateNewCreditCardAndAddress(pRequest,pResponse);
	}
	public boolean handleCreateNewCreditCardAndAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
	        throws ServletException, IOException
	    {
			//System.out.println("handleCreateNewCreditCardAndAddress Starts");
			handlePreferredBilling(pRequest,pResponse);
			//System.out.println("handleCreateNewCreditCardAndAddress ends");
			return super.handleCreateNewCreditCardAndAddress(pRequest,pResponse);
	    }
	
	
	public void handleMakePreferredBilling(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
	        throws ServletException, IOException
	    {
			//getProfileTools().setDefaultBillingAddress();
			addFormException(new DropletException("err_txt_address"));
	    }
	
		*//**
		 * @return the mPmgr
		 *//*
		public BBBPropertyManager getPmgr() {
			return mPmgr;
		}

		*//**
		 * @param mPmgr the mPmgr to set
		 *//*
		public void setPmgr(BBBPropertyManager pPmgr) {
			this.mPmgr = pPmgr;
		}
		 public Map getEditValue()
		    {
		        return mEditValue;
		    }
		*//**
		 * @return the mProfileTools
		 *//*
		public BBBProfileTools getProfileTools() {
			return mProfileTools;
		}
		*//**
		 * @param mProfileTools the mProfileTools to set
		 *//*
		public void setProfileTools(BBBProfileTools pProfileTools) {
			this.mProfileTools = pProfileTools;
		}
	
}*/