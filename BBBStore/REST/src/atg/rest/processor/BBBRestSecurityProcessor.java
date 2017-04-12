/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  BBBRestSecurityProcessor.java
 *
 *  DESCRIPTION: A over-ride pipeline component handles the Rest security related service
 *  HISTORY:
 *  31/11/12 Initial version
 *
 */
package atg.rest.processor;

import java.io.IOException;

import atg.rest.RestException;
import atg.rest.security.ComponentSecurityConfiguration;
import atg.rest.util.RepositoryURI;
import atg.security.AccessControlEntry;
import atg.security.AccessControlList;
import atg.security.BBBRestClientPersona;
import atg.security.Persona;
import atg.security.SecurityException;
import atg.security.ThreadSecurityManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.constants.RestConstants;

/**
 * DESCRIPTION: Overrided to handle custom check for method checkPersona
 * 
 * @author akhaju
 */
public class BBBRestSecurityProcessor extends RestSecurityProcessor {


	private boolean mRestCustomRepoSecurityEnable;
	private boolean mHttpsEnabled;
	/**
	 * @return the restCustomRepoSecurityEnable
	 */
	public boolean isRestCustomRepoSecurityEnable() {
		return mRestCustomRepoSecurityEnable;
	}

	/**
	 * @param pRestCustomRepoSecurityEnable the restCustomRepoSecurityEnable to set
	 */
	public void setRestCustomRepoSecurityEnable(boolean pRestCustomRepoSecurityEnable) {
		mRestCustomRepoSecurityEnable = pRestCustomRepoSecurityEnable;
	}

	

	/**
	 * @return the httpsEnabled
	 */
	public boolean isHttpsEnabled() {
		return mHttpsEnabled;
	}

	/**
	 * @param pHttpsEnabled
	 *            the httpsEnabled to set
	 */
	public void setHttpsEnabled(boolean pHttpsEnabled) {
		mHttpsEnabled = pHttpsEnabled;
	}

	/**
	 * This method checks whether the user is allowed to access the
	 * component/Repository or not. It matches the persona rule from the user
	 * profile to validate it
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @param pParsedURI
	 * @param pRepositoryURI
	 * @throws RestException
	 */
	private void checkPersona(DynamoHttpServletResponse pResponse, RepositoryURI pRepositoryURI) throws RestException {
		if (isLoggingDebug()) {
			logDebug("BBBRestSecurityProcessor:checkPersona starts pRepositoryURI =" + pRepositoryURI);
		}

		String container = null;
		ComponentSecurityConfiguration csc = null;
		if (pRepositoryURI == null) {
			logError("Repository " + pRepositoryURI + " is null");
			return;
		}
		container = pRepositoryURI.getResourceContainer();
		csc = getRestSecurityManager().getComponentSecurityConfiguration(pRepositoryURI.getResourceContainer());

		if (csc != null) {
			try {

				// Below Code Iterates the acl rules defined in the restSecurity
				// xml corresponding to the component/repository
				AccessControlList accList = csc.getSecurityConfiguration().getSecurityPolicy().getEffectiveAccessControlList(csc);
				if (accList != null) {

					AccessControlEntry[] acl = accList.getAccessControlEntries();
					if (!(acl == null || acl.length == 0)) {
						boolean check = false;
						for (AccessControlEntry accessControlEntry : acl) {
							Persona per = accessControlEntry.getPersona();
							Persona[] perlist = ThreadSecurityManager.currentUser().getPersonae();
							String personaName = per.getName();
							for (Persona personaRole : perlist) {
								if (isLoggingDebug()) {
									logDebug("persona.getName() = " + personaRole.getName() + " per.getName() =" + personaName);
								}
								
								// If isHttpsEnabled return true that means request should be secure.
								// Below code will check whether component access is secure or not i.e. Only logged in
								// user can access that.
								if (!(personaRole instanceof BBBRestClientPersona)) {
									continue;
								}
								if (isHttpsEnabled() && !ServletUtil.getCurrentRequest().isSecure()) {
									logError("BBBRestSecurityProcessor.service()- Error Code = " + 401 + " ERROR MESSAGE = " + RestConstants.HTTPS_ERROR + " for URI =" + pRepositoryURI);
									throw new RestException(RestConstants.HTTPS_ERROR + " " + container, pResponse, 401);
								}

								// Below code iterates the roles assigned to
								// User and matches with the component rule
								if (personaRole.getName().equalsIgnoreCase(personaName)) {
									check = true;
									break;
								}
							}
						}
						if (!check) {
							throw new RestException(RestConstants.NOT_ALLOWED + "  " + container, pResponse, 401);
						}
					}
				}
			} catch (SecurityException e) {
				logError("BBBRestSecurityProcessor:checkPersona has SecurityException" + e.getMessage());
				throw new RestException(RestConstants.NOT_ALLOWED + " " + container, pResponse, 401);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBRestSecurityProcessor:checkPersona - No security configured for " + pRepositoryURI);
			logDebug("BBBRestSecurityProcessor:checkPersona ends");
		}
	}

	/**
	 * Over-ride method of handleRepositoryRequest
	 * 
	 * @param pParsedURI
	 * @param pRequest
	 * @param pResponse
	 * @throws RestException
	 * @throws IOException
	 */
	protected void handleRepositoryRequest(RepositoryURI pRepositoryURI, DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws RestException {
		if (isRestCustomRepoSecurityEnable()) {
			checkPersona(pResponse, pRepositoryURI);
		}
		super.handleRepositoryRequest(pRepositoryURI, pRequest, pResponse);
	}

}
