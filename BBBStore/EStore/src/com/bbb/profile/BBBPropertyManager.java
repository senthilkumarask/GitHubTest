package com.bbb.profile;

import atg.commerce.profile.CommercePropertyManager;
import atg.nucleus.Nucleus;
import atg.security.PasswordHasher;
import atg.security.PasswordHasherConfigurer;

/**
 * BBB-specific extensions with names of extended Store properties from crs.
 *
 */
public class BBBPropertyManager extends CommercePropertyManager {
	private String mConfirmPasswordPropertyName;
	private String mPhoneNumberPropertyName;
	private String mMobileNumberPropertyName;
	private String mSiteIdPropertyName;
	private String mFavouriteStoreIdPropertyName;
	private String mMemberIdPropertyName;
	private String mMemberTokenPropertyName;
	private String mCreditCardNumberPropertyName;
	private String mSecureCodePropertyName;
	private String mNameOnCardPropertyName;
	private String mBillingAddress;
	private String mOldPasswordPropertyName;
	private String mSiteId = "siteId";
	private String mUserSiteItemsPropertyName = "userSiteItems";
	private String mSiteEmailAddressPropertyName = "siteEmailAddress";
	private String mNewRegistrationPropertyName;
	private String mSchoolIdsPropertyName;
	private String mSchoolPromotionsPropertyName;
	private String mFbProfileExtended = "fbProfileExtended";
	private String mLastActivityTime = "lastActivity";
	private String mLoggedIn = "loggedIn";
	private String mMigratedAccount = "migratedAccount";
	private String mProfileItemDiscriptorName = "user";
	private String mEmailOptInPropertyName = "emailoptin";
	private String mEmailOptInBabyPropertyName = "emailOptIn_BabyCA";
	private String mSource = "source";

	private String mStatus = "status";
	private String mWalletId = "walletId";
	
	private String mAccountLockItemDescriptorName = "bbb_profile_lock";
	private String mAccountLockProfileId = "profileId";
	private String mInvalidLoginAttemptCount = "invalidLoginAttemptCount";
	private String mLastInvalidLoginAttemptTime = "lastInvalidLoginAttemptTime";
	private String mSaveCreditCardInfoToProfile ="saveCreditCardInfoToProfile";
	private String mResetPasswordPropertyName;
	private String mDescriptionPropertyName;
	private String mAutoLoginItemDescriptorName ="user";
	private String resetEmailPropertyName;
	
	//dkhadka: added for shadow profile	
	private String mSourcePropertyName="source";	
	private String mStatusPropertyName="status";
	private String mWalletIdPropertyName = "walletId";
	private String isPoBoxAddressPropertyName;
	private String qasValidatedPropertyName;
	private String resetPasswordToken;
	
	String mMailingAddressPropertyName = "mailingAddress";
	
	public String getMailingAddressPropertyName() {
		return mMailingAddressPropertyName;
	}

	public void setMailingAddressPropertyName(String pMailingAddressPropertyName) {
		this.mMailingAddressPropertyName = pMailingAddressPropertyName;
	}

	public String getResetPasswordToken() {
		return resetPasswordToken;
	}

	public void setResetPasswordToken(String resetPasswordToken) {
		this.resetPasswordToken = resetPasswordToken;
	}

	public String getIsPoBoxAddressPropertyName() {
		return isPoBoxAddressPropertyName;
	}

	public void setIsPoBoxAddressPropertyName(String isPoBoxAddressPropertyName) {
		this.isPoBoxAddressPropertyName = isPoBoxAddressPropertyName;
	}

	public String getqasValidatedPropertyName() {
		return qasValidatedPropertyName;
	}

	public void setqasValidatedPropertyName(String qasValidatedPropertyName) {
		this.qasValidatedPropertyName = qasValidatedPropertyName;
	}
	
	public String getWalletIdPropertyName() {
		return mWalletIdPropertyName ;
	}

	public void setWalletIdPropertyName(String walletIdPropertyName) {
		mWalletIdPropertyName  = walletIdPropertyName;
	}

		public String getStatusPropertyName() {
			return mStatusPropertyName;
	}

	public void setStatusPropertyName(String statusPropertyName) {
			this.mStatusPropertyName = statusPropertyName;
	}	
			
	public String getmSourcePropertyName() {
			return mSourcePropertyName;
	}

	public void setmSourcePropertyName(String mSourcePropertyName) {
		this.mSourcePropertyName = mSourcePropertyName;
	}		
	//dkhadka: End	
		

	public String getSource() {
		return mSource;
	}

	public void setSource(String mSource) {
		this.mSource = mSource;
	}

	public String getStatus() {
		return mStatus;
	}

	public void setStatus(String mStatus) {
		this.mStatus = mStatus;
	}

	public String getWalletId() {
		return mWalletId;
	}

	public void setWalletId(String mWalletId) {
		this.mWalletId = mWalletId;
	}
	
	public String getAutoLoginItemDescriptorName() {
		return mAutoLoginItemDescriptorName;
	}

	public void setAutoLoginItemDescriptorName(String mAutoLoginItemDescriptorName) {
		this.mAutoLoginItemDescriptorName = mAutoLoginItemDescriptorName;
	}

	/**
	 * @return the mDescriptionPropertyName
	 */
	public String getDescriptionPropertyName() {
		return mDescriptionPropertyName;
	}
	
	/**
	 * @param mDescriptionPropertyName the mDescriptionPropertyName to set
	 */
	public void setDescriptionPropertyName(	String pDescriptionPropertyName) {
		this.mDescriptionPropertyName = pDescriptionPropertyName;
	}
	
	/**
	 * @return the mResetPasswordPropertyName
	 */
	public String getResetPasswordPropertyName() {
		return mResetPasswordPropertyName;
	}
	/**
	 * @param mResetPasswordPropertyName the mResetPasswordPropertyName to set
	 */
	public void setResetPasswordPropertyName(
			String pResetPasswordPropertyName) {
		this.mResetPasswordPropertyName = pResetPasswordPropertyName;
	}

	/**
	 * @return the mAccountLockItemDescriptorName
	 */
	public String getAccountLockItemDescriptorName() {
		return mAccountLockItemDescriptorName;
	}

	/**
	 * @param mAccountLockItemDescriptorName the mAccountLockItemDescriptorName to set
	 */
	public void setAccountLockItemDescriptorName(
			String pAccountLockItemDescriptorName) {
		this.mAccountLockItemDescriptorName = pAccountLockItemDescriptorName;
	}

	/**
	 * @return the mAccountLockProfileId
	 */
	public String getAccountLockProfileId() {
		return mAccountLockProfileId;
	}

	/**
	 * @param mAccountLockProfileId the mAccountLockProfileId to set
	 */
	public void setAccountLockProfileId(String pAccountLockProfileId) {
		this.mAccountLockProfileId = pAccountLockProfileId;
	}

	/**
	 * @return the mInvalidLoginAttemptCount
	 */
	public String getInvalidLoginAttemptCount() {
		return mInvalidLoginAttemptCount;
	}

	/**
	 * @param mInvalidLoginAttemptCount the mInvalidLoginAttemptCount to set
	 */
	public void setInvalidLoginAttemptCount(String InvalidLoginAttemptCount) {
		this.mInvalidLoginAttemptCount = InvalidLoginAttemptCount;
	}

	/**
	 * @return the mLastInvalidLoginAttemptTime
	 */
	public String getLastInvalidLoginAttemptTime() {
		return mLastInvalidLoginAttemptTime;
	}

	/**
	 * @param mLastInvalidLoginAttemptTime the mLastInvalidLoginAttemptTime to set
	 */
	public void setLastInvalidLoginAttemptTime(String pLastInvalidLoginAttemptTime) {
		this.mLastInvalidLoginAttemptTime = pLastInvalidLoginAttemptTime;
	}

	/**
	 * @return the mEmailOptInPropertyName
	 */
	public String getEmailOptInPropertyName() {
		return mEmailOptInPropertyName;
	}

	/**
	 * @param pEmailOptInPropertyName the pEmailOptInPropertyName to set
	 */
	public void setEmailOptInPropertyName(String pEmailOptInPropertyName) {
		this.mEmailOptInPropertyName = pEmailOptInPropertyName;
	}
	
	/**
	 * @return the mEmailOptInPropertyName
	 */
	public String getEmailOptInBabyPropertyName() {
		return mEmailOptInBabyPropertyName;
	}

	/**
	 * @param pEmailOptInPropertyName the pEmailOptInPropertyName to set
	 */
	public void setEmailOptInBabyPropertyName(String mEmailOptInBabyPropertyName) {
		this.mEmailOptInBabyPropertyName = mEmailOptInBabyPropertyName;
	}

	/**
	 * @return the mProfileItemDiscriptorName
	 */
	public String getProfileItemDiscriptorName() {
		return mProfileItemDiscriptorName;
	}

	/**
	 * @param mProfileItemDiscriptorName the mProfileItemDiscriptorName to set
	 */
	public void setProfileItemDiscriptorName(String pProfileItemDiscriptorName) {
		this.mProfileItemDiscriptorName = pProfileItemDiscriptorName;
	}

	/**
	 * @return the mLoggedIn
	 */
	public String getLoggedIn() {
		return mLoggedIn;
	}

	/**
	 * @param pLoggedIn the pLoggedIn to set
	 */
	public void setLoggedIn(String pLoggedIn) {
		this.mLoggedIn = pLoggedIn;
	}

	/**
	 * @return the mMigratedAccount
	 */
	public String getMigratedAccount() {
		return mMigratedAccount;
	}

	/**
	 * @param pMigratedAccount the pMigratedAccount to set
	 */
	public void setMigratedAccount(String pMigratedAccount) {
		this.mMigratedAccount = pMigratedAccount;
	}

	/**
	 * @return the mLastActivityTime
	 */
	public String getLastActivityTime() {
		return mLastActivityTime;
	}

	/**
	 * @param pLastActivityTime the pLastActivityTime to set
	 */
	public void setLastActivityTime(String pLastActivityTime) {
		this.mLastActivityTime = pLastActivityTime;
	}

	/**
	 * @return the mFbProfileExtended
	 */
	public String getFbProfileExtended() {
		return mFbProfileExtended;
	}

	/**
	 * @param mFbProfileExtended the mFbProfileExtended to set
	 */
	public void setFbProfileExtended(String pFbProfileExtended) {
		this.mFbProfileExtended = pFbProfileExtended;
	}

	/**
	 * @return the siteEmailAddressPropertyName
	 */
	public String getSiteEmailAddressPropertyName() {
		return mSiteEmailAddressPropertyName;
	}

	/**
	 * @param pSiteEmailAddressPropertyName the siteEmailAddressPropertyName to set
	 */
	public void setSiteEmailAddressPropertyName(String pSiteEmailAddressPropertyName) {
		mSiteEmailAddressPropertyName = pSiteEmailAddressPropertyName;
	}

	/**
	 * @return the userSiteItemsPropertyName
	 */
	public String getUserSiteItemsPropertyName() {
		return mUserSiteItemsPropertyName;
	}

	/**
	 * @param pUserSiteItemsPropertyName the userSiteItemsPropertyName to set
	 */
	public void setUserSiteItemsPropertyName(String pUserSiteItemsPropertyName) {
		mUserSiteItemsPropertyName = pUserSiteItemsPropertyName;
	}

	
	
	/**
	 * @return the siteId
	 */
	public String getSiteId() {
		return mSiteId;
	}

	/**
	 * @param pSiteId the siteId to set
	 */
	public void setSiteId(String pSiteId) {
		mSiteId = pSiteId;
	}

	
	protected String mNewCreditCard = "newCreditCard";
	 
	  public String getNewCreditCard() {
	    return mNewCreditCard;
	  }

	  public void setNewCreditCard(String pNewCreditCard) {
	    mNewCreditCard = pNewCreditCard;
	  }
	
	/**
	 * @return the mOldPasswordPropertyName
	 */
	public String getOldPasswordPropertyName() {
		return mOldPasswordPropertyName;
	}
	public void setOldPasswordPropertyName(String sOldPasswordPropertyName) {
		this.mOldPasswordPropertyName = sOldPasswordPropertyName;
	}
	/**
	 * @return the mConfirmPasswordPropertyName
	 */
	public String getConfirmPasswordPropertyName() {
		return mConfirmPasswordPropertyName;
	}
	/**
	 * @param mConfirmPasswordPropertyName the mConfirmPasswordPropertyName to set
	 */
	public void setConfirmPasswordPropertyName(
			String mConfirmPasswordPropertyName) {
		this.mConfirmPasswordPropertyName = mConfirmPasswordPropertyName;
	}
	/**
	 * @return the mPhoneNumberPropertyName
	 */
	public String getPhoneNumberPropertyName() {
		return mPhoneNumberPropertyName;
	}
	/**
	 * @param mPhoneNumberPropertyName the mPhoneNumberPropertyName to set
	 */
	public void setPhoneNumberPropertyName(String mPhoneNumberPropertyName) {
		this.mPhoneNumberPropertyName = mPhoneNumberPropertyName;
	}
	/**
	 * @return the mMobileNumberPropertyName
	 */
	public String getMobileNumberPropertyName() {
		return mMobileNumberPropertyName;
	}
	/**
	 * @param mMobileNumberPropertyName the mMobileNumberPropertyName to set
	 */
	public void setMobileNumberPropertyName(String mMobileNumberPropertyName) {
		this.mMobileNumberPropertyName = mMobileNumberPropertyName;
	}
	/**
	 * @return the mSiteIdPropertyName
	 */
	public String getSiteIdPropertyName() {
		return mSiteIdPropertyName;
	}
	/**
	 * @param mSiteIdPropertyName the mSiteIdPropertyName to set
	 */
	public void setSiteIdPropertyName(String pSiteIdPropertyName) {
		this.mSiteIdPropertyName = pSiteIdPropertyName;
	}
	/**
	 * @return the mFavouriteStoreIdPropertyName
	 */
	public String getFavouriteStoreIdPropertyName() {
		return mFavouriteStoreIdPropertyName;
	}
	/**
	 * @param pFavouriteStoreIdPropertyName the mFavouriteStoreIdPropertyName to set
	 */
	public void setFavouriteStoreIdPropertyName(
			String pFavouriteStoreIdPropertyName) {
		this.mFavouriteStoreIdPropertyName = pFavouriteStoreIdPropertyName;
	}
	
	/**
	 * @return the mMemberIdPropertyName
	 */
	public String getMemberIdPropertyName() {
		return mMemberIdPropertyName;
	}

	/**
	 * @param mMemberIdPropertyName the mMemberIdPropertyName to set
	 */
	public void setMemberIdPropertyName(String pMemberIdPropertyName) {
		this.mMemberIdPropertyName = pMemberIdPropertyName;
	}
	
	/**
	 * @return the mMemberTokenPropertyName
	 */
	public String getMemberTokenPropertyName() {
		return mMemberTokenPropertyName;
	}

	/**
	 * @param mMemberTokenPropertyName the mMemberTokenPropertyName to set
	 */
	public void setMemberTokenPropertyName(String pMemberTokenPropertyName) {
		this.mMemberTokenPropertyName = pMemberTokenPropertyName;
	}
	
	/**
	 * @return the mSecureCodePropertyName
	 */
	public String getSecureCodePropertyName() {
		return mSecureCodePropertyName;
	}
	/**
	 * @param mSecureCodePropertyName the mSecureCodePropertyName to set
	 */
	public void setSecureCodePropertyName(String pSecureCodePropertyName) {
		this.mSecureCodePropertyName = pSecureCodePropertyName;
	}
	/**
	 * @return the mNameOnCardPropertyName
	 */
	public String getNameOnCardPropertyName() {
		return mNameOnCardPropertyName;
	}
	/**
	 * @param mNameOnCardPropertyName the mNameOnCardPropertyName to set
	 */
	public void setNameOnCardPropertyName(String pNameOnCardPropertyName) {
		this.mNameOnCardPropertyName = pNameOnCardPropertyName;
	}
	/**
	 * @return the mCreditCardNumberPropertyName
	 */
	public String getCreditCardNumberPropertyName() {
		return mCreditCardNumberPropertyName;
	}
	/**
	 * @param mCreditCardNumberPropertyName the mCreditCardNumberPropertyName to set
	 */
	public void setCreditCardNumberPropertyName(
			String pCreditCardNumberPropertyName) {
		this.mCreditCardNumberPropertyName = pCreditCardNumberPropertyName;		
	}
	/**
	 * @return the mBillingAddress
	 */
	public String getBillingAddress() {
		return mBillingAddress;
	}
	/**
	 * @param mBillingAddress the mBillingAddress to set
	 */
	public void setBillingAddress(String pBillingAddress) {
		this.mBillingAddress = pBillingAddress;
	}

	public String getNewRegistrationPropertyName() {
		return mNewRegistrationPropertyName;
	}

	public void setNewRegistrationPropertyName(String pNewRegistrationPropertyName) {
		this.mNewRegistrationPropertyName = pNewRegistrationPropertyName;
	}

	/**
	 * @return the mSchoolIdsPropertyName
	 */
	public String getSchoolIdsPropertyName() {
		return mSchoolIdsPropertyName;
	}

	/**
	 * @param pSchoolIdsPropertyName the mSchoolIdsPropertyName to set
	 */
	public void setSchoolIdsPropertyName(String pSchoolIdsPropertyName) {
		mSchoolIdsPropertyName = pSchoolIdsPropertyName;
	}

	/**
	 * @return the mSchoolPromotionsPropertyName
	 */
	public String getSchoolPromotionsPropertyName() {
		return mSchoolPromotionsPropertyName;
	}

	/**
	 * @param pSchoolPromotionsPropertyName the mSchoolPromotionsPropertyName to set
	 */
	public void setSchoolPromotionsPropertyName(
			String pSchoolPromotionsPropertyName) {
		mSchoolPromotionsPropertyName = pSchoolPromotionsPropertyName;
	}
	public String getSaveCreditCardInfoToProfile() {
		return mSaveCreditCardInfoToProfile;
	}

	public void setSaveCreditCardInfoToProfile(
			String mSaveCreditCardInfoToProfile) {
		this.mSaveCreditCardInfoToProfile = mSaveCreditCardInfoToProfile;
	}
	
	
	@Override
	public PasswordHasherConfigurer getPasswordHasherConfigurer() {
	  
     if(null==super.getPasswordHasherConfigurer())
    	 return (PasswordHasherConfigurer)Nucleus.getGlobalNucleus().resolveName("/atg/dynamo/security/PasswordHasherConfigurer");
     else
    	 return super.getPasswordHasherConfigurer();
		 
	}

	 
	  

	/* (non-Javadoc)
	 * @see atg.userprofiling.PropertyManager#setPasswordHasher(atg.security.PasswordHasher)
	 */
	@Override
	public void setPasswordHasher(PasswordHasher pHasher) {
		
		getPasswordHasherConfigurer().setPasswordHasher(pHasher);
	}

	/* (non-Javadoc)
	 * @see atg.userprofiling.PropertyManager#setAlternateUserPasswordHasher(atg.security.PasswordHasher)
	 */
	@Override
	public void setAlternateUserPasswordHasher(
			PasswordHasher pAlternateUserPasswordHasher) {
		// TODO Auto-generated method stub
		getPasswordHasherConfigurer().setAlternateUserPasswordHasher(pAlternateUserPasswordHasher);
	}

	public String getResetEmailPropertyName() {
		return resetEmailPropertyName;
	}

	public void setResetEmailPropertyName(String resetEmailPropertyName) {
		this.resetEmailPropertyName = resetEmailPropertyName;
	}

	 

}
