/**
 * 
 */
package com.bbb.social.facebook;

import atg.core.util.StringUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryPropertyDescriptor;

/**
 * @author alakra
 * 
 */
public class PropertyManager extends ApplicationLoggingImpl {

	private String mIdPropertyName = "id";
	private String mUserNamePropertyName = "userName";
	private String mNamePropertyName = "name";
	private String mFirstNamePropertyName = "firstName";
	private String mMiddleNamePropertyName = "middleName";
	private String mLastNamePropertyName = "lastName";
	private String mGenderPropertyName = "gender";
	private String mVerifiedPropertyName = "verified";
	private String mOfflineAccessTokenPropertyName = "offlineAccessToken";
	private String mTokenValidPropertyName = "tokenValid";
	private String mSchoolsPropertyName = "schools";
	private String mSchoolIDPropertyName = "schoolID";
	private String mFriendsPropertyName = "friends";
	private String mBBBProfilePropertyName = "bbbProfile";
	private String mFacebookProfilePropertyName = "facebookProfile";
	private String mUserPropertyName = "user";
	private String mFacebookUserPropertyName = "fbUser";
	private String mFacebookSchoolPropertyName = "fbSchool";

	/**
	 * 
	 */
	public PropertyManager() {
		// Deafult Constructor
	}

	public String getLoggingIdentifier() {
		return "FacebookPropertyManager";
	}

	public boolean hasProperty(String pPropertyName, RepositoryItem pItem) {
		if (pItem == null) {
			return false;
		}
		try {
			return hasProperty(pPropertyName, pItem.getItemDescriptor());
		} catch (RepositoryException exc) {
			if (isLoggingError()) {
				if (exc.getSourceException() != null) {
					logError(exc.toString(), exc.getSourceException());
				} else {
					logError(exc);
				}
			}
		}
		return false;
	}

	public boolean hasProperty(String pPropertyName, RepositoryItemDescriptor pItemDescriptor) {
		if (pItemDescriptor == null) {
			return false;
		}
		if (pPropertyName == null) {
			return false;
		}
		String propertyNames[] = StringUtils.splitStringAtCharacter(pPropertyName, '.');
		RepositoryItemDescriptor rid = pItemDescriptor;
		for (int i = 0; i < propertyNames.length; i++) {
			if (rid == null || !rid.hasProperty(propertyNames[i])) {
				return false;
			}
			RepositoryPropertyDescriptor pd = (RepositoryPropertyDescriptor) rid
					.getPropertyDescriptor(propertyNames[i]);
			rid = pd.getPropertyItemDescriptor();
		}

		return true;
	}

	/**
	 * @return the idPropertyName
	 */
	public final String getIdPropertyName() {
		return mIdPropertyName;
	}

	/**
	 * @return the userNamePropertyName
	 */
	public final String getUserNamePropertyName() {
		return mUserNamePropertyName;
	}

	/**
	 * @return the namePropertyName
	 */
	public final String getNamePropertyName() {
		return mNamePropertyName;
	}

	/**
	 * @return the firstNamePropertyName
	 */
	public final String getFirstNamePropertyName() {
		return mFirstNamePropertyName;
	}

	/**
	 * @return the middleNamePropertyName
	 */
	public final String getMiddleNamePropertyName() {
		return mMiddleNamePropertyName;
	}

	/**
	 * @return the lastNamePropertyName
	 */
	public final String getLastNamePropertyName() {
		return mLastNamePropertyName;
	}

	/**
	 * @return the genderPropertyName
	 */
	public final String getGenderPropertyName() {
		return mGenderPropertyName;
	}

	/**
	 * @return the verifiedPropertyName
	 */
	public final String getVerifiedPropertyName() {
		return mVerifiedPropertyName;
	}

	/**
	 * @return the offlineAccessTokenPropertyName
	 */
	public final String getOfflineAccessTokenPropertyName() {
		return mOfflineAccessTokenPropertyName;
	}

	/**
	 * @return the tokenValidPropertyName
	 */
	public final String getTokenValidPropertyName() {
		return mTokenValidPropertyName;
	}

	/**
	 * @return the schoolsPropertyName
	 */
	public final String getSchoolsPropertyName() {
		return mSchoolsPropertyName;
	}

	/**
	 * @return the schoolIDPropertyName
	 */
	public final String getSchoolIDPropertyName() {
		return mSchoolIDPropertyName;
	}

	/**
	 * @return the friendsPropertyName
	 */
	public final String getFriendsPropertyName() {
		return mFriendsPropertyName;
	}

	/**
	 * @return the bBBProfilePropertyName
	 */
	public final String getBBBProfilePropertyName() {
		return mBBBProfilePropertyName;
	}

	/**
	 * @return the facebookProfilePropertyName
	 */
	public final String getFacebookProfilePropertyName() {
		return mFacebookProfilePropertyName;
	}

	/**
	 * @return the userPropertyName
	 */
	public final String getUserPropertyName() {
		return mUserPropertyName;
	}

	/**
	 * @return the facebookUserPropertyName
	 */
	public final String getFacebookUserPropertyName() {
		return mFacebookUserPropertyName;
	}

	/**
	 * @return the facebookSchoolPropertyName
	 */
	public final String getFacebookSchoolPropertyName() {
		return mFacebookSchoolPropertyName;
	}
}
