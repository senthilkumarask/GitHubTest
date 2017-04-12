package com.bbb.idm;

import java.util.Date;

public class AssociateVO {
	
	private String mAssociateId;
	private Date mLastLogin;
	
	public AssociateVO(){
		//default constructor
	}
	public AssociateVO(String pAssociateId,Date pLastLogin){
		this.mAssociateId = pAssociateId;
		this.mLastLogin = pLastLogin;
	}
	/**
	 * @return the mAssociateId
	 */
	public String getAssociateId() {
		return mAssociateId;
	}
	/**
	 * @param mAssociateId the mAssociateId to set
	 */
	public void setAssociateId(String pAssociateId) {
		this.mAssociateId = pAssociateId;
	}
	/**
	 * @return the mLastLoginTime
	 */
	public Date getLastLogin() {
		return mLastLogin;
	}
	/**
	 * @param mLastLoginTime the mLastLoginTime to set
	 */
	public void setLastLogin(Date pLastLogin) {
		this.mLastLogin = pLastLogin;
	}
	
	

}
