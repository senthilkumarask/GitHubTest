package com.bbb.framework.webservices.test.vo;

/**
 * 
 * @author manohar
 *
 */
public class OrderHistoryVO {

	private String 	userToken;
	private String siteFlag;
	private String memberID;
	private String mbrNum;
	
	public String getUserToken() {
		return userToken;
	}
	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}
	public String getSiteFlag() {
		return siteFlag;
	}
	public void setSiteFlag(String siteFlag) {
		this.siteFlag = siteFlag;
	}
	public String getMemberID() {
		return memberID;
	}
	public void setMemberID(String memberID) {
		this.memberID = memberID;
	}
	public String getMbrNum() {
		return mbrNum;
	}
	public void setMbrNum(String mbrNum) {
		this.mbrNum = mbrNum;
	}
	
	
}
