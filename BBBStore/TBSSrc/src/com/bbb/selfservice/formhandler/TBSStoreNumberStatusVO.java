package com.bbb.selfservice.formhandler;

public class TBSStoreNumberStatusVO {
	
	private String mStatus;
	private String mMessage;
	private String mStoreNumber;
	private String mRedirectUrl;
	
	public String getRedirectUrl() {
		return mRedirectUrl;
	}

	public void setRedirectUrl(String mRedirectUrl) {
		this.mRedirectUrl = mRedirectUrl;
	}

	public TBSStoreNumberStatusVO(String pStatus, String pMessage, String pStoreNumber, String pRedirectUrl){
		this.mStatus = pStatus;
		this.mMessage = pMessage;
		this.mStoreNumber = pStoreNumber;
		this.mRedirectUrl = pRedirectUrl;
	}
	
	public String getStatus() {
		return mStatus;
	}
	
	public void setStatus(String pStatus) {
		this.mStatus = pStatus;
	}
	
	public String getMessage() {
		return mMessage;
	}
	public void setMessage(String pMessage) {
		this.mMessage = pMessage;
	}
	
	public String getStoreNumber() {
		return mStoreNumber;
	}
	
	public void setStoreNumber(String pStoreNumber) {
		this.mStoreNumber = pStoreNumber;
	}	

}
