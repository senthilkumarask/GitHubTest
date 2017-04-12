package com.bbb.account;

import atg.core.util.Address;

public class BBBAddressObject extends Address{
	/**
	 * vnalini
	 */
	private static final long serialVersionUID = 1L;
	private String mCompanyName;
	private boolean poBoxAddress;
	private boolean qasValidated;
	private String email;
	private String phoneNumber;
	
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public boolean isQasValidated() {
		return qasValidated;
	}

	public void setQasValidated(boolean qasValidated) {
		this.qasValidated = qasValidated;
	}

	public boolean isPoBoxAddress() {
		return poBoxAddress;
	}

	public void setPoBoxAddress(boolean poBoxAddress) {
		this.poBoxAddress = poBoxAddress;
	}
	 public String getCompanyName()
	    {
	        return mCompanyName;
	    }

	    public void setCompanyName(String pCompanyName)
	    {
	        mCompanyName = pCompanyName;
	        addChangedProperty("companyName");
	    }
}