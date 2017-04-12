package com.bbb.account.vo.order;

import java.io.Serializable;

/**
 * Billing.java
 *
 * This file is part of OrderDetails webservice response
 * 
 */

public class Billing  implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Address address;

    private String addressNm;

    private String addressID;

    private long pymtID;

    private boolean addToAcct;

    private boolean promoEMailFlag;

    private String billToFlag;

    private String emailAddr;

    private boolean isPreferred;

    private String mobPhone;

    private boolean mobileOptinFlag;

    private boolean isSelected;

	/**
	 * @return the address
	 */
	public Address getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(Address address) {
		this.address = address;
	}

	/**
	 * @return the addressNm
	 */
	public String getAddressNm() {
		return addressNm;
	}

	/**
	 * @param addressNm the addressNm to set
	 */
	public void setAddressNm(String addressNm) {
		this.addressNm = addressNm;
	}

	/**
	 * @return the addressID
	 */
	public String getAddressID() {
		return addressID;
	}

	/**
	 * @param addressID the addressID to set
	 */
	public void setAddressID(String addressID) {
		this.addressID = addressID;
	}

	/**
	 * @return the pymtID
	 */
	public long getPymtID() {
		return pymtID;
	}

	/**
	 * @param pymtID the pymtID to set
	 */
	public void setPymtID(long pymtID) {
		this.pymtID = pymtID;
	}

	/**
	 * @return the addToAcct
	 */
	public boolean isAddToAcct() {
		return addToAcct;
	}

	/**
	 * @param addToAcct the addToAcct to set
	 */
	public void setAddToAcct(boolean addToAcct) {
		this.addToAcct = addToAcct;
	}

	/**
	 * @return the promoEMailFlag
	 */
	public boolean isPromoEMailFlag() {
		return promoEMailFlag;
	}

	/**
	 * @param promoEMailFlag the promoEMailFlag to set
	 */
	public void setPromoEMailFlag(boolean promoEMailFlag) {
		this.promoEMailFlag = promoEMailFlag;
	}

	/**
	 * @return the billToFlag
	 */
	public String getBillToFlag() {
		return billToFlag;
	}

	/**
	 * @param billToFlag the billToFlag to set
	 */
	public void setBillToFlag(String billToFlag) {
		this.billToFlag = billToFlag;
	}

	/**
	 * @return the emailAddr
	 */
	public String getEmailAddr() {
		return emailAddr;
	}

	/**
	 * @param emailAddr the emailAddr to set
	 */
	public void setEmailAddr(String emailAddr) {
		this.emailAddr = emailAddr;
	}

	/**
	 * @return the isPreferred
	 */
	public boolean isPreferred() {
		return isPreferred;
	}

	/**
	 * @param isPreferred the isPreferred to set
	 */
	public void setPreferred(boolean isPreferred) {
		this.isPreferred = isPreferred;
	}

	/**
	 * @return the mobPhone
	 */
	public String getMobPhone() {
		return mobPhone;
	}

	/**
	 * @param mobPhone the mobPhone to set
	 */
	public void setMobPhone(String mobPhone) {
		this.mobPhone = mobPhone;
	}

	/**
	 * @return the mobileOptinFlag
	 */
	public boolean isMobileOptinFlag() {
		return mobileOptinFlag;
	}

	/**
	 * @param mobileOptinFlag the mobileOptinFlag to set
	 */
	public void setMobileOptinFlag(boolean mobileOptinFlag) {
		this.mobileOptinFlag = mobileOptinFlag;
	}

	/**
	 * @return the isSelected
	 */
	public boolean isSelected() {
		return isSelected;
	}

	/**
	 * @param isSelected the isSelected to set
	 */
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

}
