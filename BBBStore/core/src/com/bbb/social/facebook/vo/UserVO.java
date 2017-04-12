package com.bbb.social.facebook.vo;


import java.io.Serializable;
import java.util.Set;

/**
 * 
 * @author manohar
 * @version 1.0
 */
public class UserVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private String mID;
	
	/**
	 * 
	 */
	private String mBBBUserID;
	
	/**
	 * 
	 */
	private String mUserName;
	
	/**
	 * 
	 */
	private String mName;
	
	/**
	 * 
	 */
	private String mFirstName;
	
	/**
	 * 
	 */
	private String mMiddleName;
	
	/**
	 * 
	 */
	private String mLastName;
	
	/**
	 * 
	 */
	private String mGender;
	
	/**
	 * 
	 */
	private boolean mVerified;
	
	/**
	 * 
	 */
	private String mOfflineAccessToken;
	
	/**
	 * 
	 */
	private boolean mTokenValid;
	
	/**
	 * 
	 */
	private Set<SchoolVO> mSchools;

	/**
	 * 
	 */
	private Set<FriendVO> mFriends;
	
	/**
	 * 
	 */
	private String mEmail;
	
	/**
	 * @return the mEmail
	 */
	public String getEmail() {
		return mEmail;
	}

	/**
	 * @param pEmail the pEmail to set
	 */
	public void setEmail(String pEmail) {
		this.mEmail = pEmail;
	}

	/**
	 * @return the ID
	 */
	public String getID() {
		return mID;
	}

	/**
	 * @param pID the iD to set
	 */
	public void setID(String pID) {
		mID = pID;
	}

	/**
	 * @return the bBBUserID
	 */
	public String getBBBUserID() {
		return mBBBUserID;
	}
	
	/**
	 * @return the userName
	 */
	public final String getUserName() {
		return mUserName;
	}

	/**
	 * @param pUserName the userName to set
	 */
	public final void setUserName(String pUserName) {
		mUserName = pUserName;
	}
	
	/**
	 * @param pBBBUserID the bBBUserID to set
	 */
	public void setBBBUserID(String pBBBUserID) {
		mBBBUserID = pBBBUserID;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return mName;
	}

	/**
	 * @param pName the name to set
	 */
	public void setName(String pName) {
		mName = pName;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return mFirstName;
	}

	/**
	 * @param pFirstName the firstName to set
	 */
	public void setFirstName(String pFirstName) {
		mFirstName = pFirstName;
	}

	/**
	 * @return the middleName
	 */
	public String getMiddleName() {
		return mMiddleName;
	}

	/**
	 * @param pMiddleName the middleName to set
	 */
	public void setMiddleName(String pMiddleName) {
		mMiddleName = pMiddleName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return mLastName;
	}

	/**
	 * @param pLastName the lastName to set
	 */
	public void setLastName(String pLastName) {
		mLastName = pLastName;
	}

	/**
	 * @return the gender
	 */
	public String getGender() {
		return mGender;
	}

	/**
	 * @param pGender the gender to set
	 */
	public void setGender(String pGender) {
		mGender = pGender;
	}

	/**
	 * @return the verified
	 */
	public boolean isVerified() {
		return mVerified;
	}

	/**
	 * @param pVerified the verified to set
	 */
	public void setVerified(boolean pVerified) {
		mVerified = pVerified;
	}

	/**
	 * @return the offlineAccessToken
	 */
	public String getOfflineAccessToken() {
		return mOfflineAccessToken;
	}

	/**
	 * @param pOfflineAccessToken the offlineAccessToken to set
	 */
	public void setOfflineAccessToken(String pOfflineAccessToken) {
		mOfflineAccessToken = pOfflineAccessToken;
	}
	
	/**
	 * @return the tokenValid
	 */
	public boolean isTokenValid() {
		return mTokenValid;
	}

	/**
	 * @param pTokenValid the tokenValid to set
	 */
	public void setTokenValid(boolean pTokenValid) {
		mTokenValid = pTokenValid;
	}
	
	/**
	 * @return the schools
	 */
	public Set<SchoolVO> getSchools() {
		return mSchools;
	}

	/**
	 * @param pSchools the schools to set
	 */
	public void setSchools(Set<SchoolVO> pSchools) {
		mSchools = pSchools;
	}

	/**
	 * @return the friends
	 */
	public Set<FriendVO> getFriends() {
		return mFriends;
	}

	/**
	 * @param pFriends the friends to set
	 */
	public void setFriends(Set<FriendVO> pFriends) {
		mFriends = pFriends;
	}
}