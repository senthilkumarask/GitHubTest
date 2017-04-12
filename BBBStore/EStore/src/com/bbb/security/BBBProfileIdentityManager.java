/**
 * 
 */
package com.bbb.security;

import atg.security.LoginUserAuthority;
import atg.security.ProfileIdentityManager;
import atg.security.SecurityException;
import atg.security.SecurityUtils;

/**
 * @author mmuneer
 *
 */
public class BBBProfileIdentityManager extends ProfileIdentityManager {

	/* (non-Javadoc)
	 * @see atg.security.BasicIdentityManager#checkAuthenticationByPassword(java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public boolean checkAuthenticationByPassword(String pLogin,
			String pPassword, Object pHashKey) throws SecurityException {
		
		if (!(getUserAuthority() instanceof LoginUserAuthority)) {
			if (isLoggingError())
				logError(LOG_PASSWORD_NOT_SUPPORTED);
			throw new SecurityException(LOG_PASSWORD_NOT_SUPPORTED);
		}

		LoginUserAuthority authority = (LoginUserAuthority) getUserAuthority();

		String[] userPasswordHasherCompPaths = getUserPasswordHasherComponents(pLogin);
		boolean authenticated;
	 
		if ((userPasswordHasherCompPaths.length == 1)
				&& (SecurityUtils.checkUserPwdHasherMatchesPMGRPwdHasher(
						authority, userPasswordHasherCompPaths[0]))) {
			authenticated = SecurityUtils.checkAuthenticationByPassword(
					authority, pLogin, pPassword, pHashKey,	null);
		} else {
			authenticated = SecurityUtils
					.checkAuthenticationByPasswordUsingUserPwdHasher(authority,
							pLogin, pPassword, pHashKey,
							null,userPasswordHasherCompPaths);
		}

		if (!(authenticated)) {
			if (isLoggingDebug()) {
				 logDebug("Password authentication failed for " + pLogin);
			}
			return false;
		}

		 
		return true;
	}
	 
}
