//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Reproduction or use of this file without explicit 
//written consent is prohibited.
//
//Created by: ssha53
//
//Created on: 01-November-2011
//--------------------------------------------------------------------------------

package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;
import javax.servlet.ServletException;
import com.bbb.account.BBBProfileManager;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.ProfileTools;

// TODO: Auto-generated Javadoc
/**
 * This class check the profile exist for the given email or not and return true
 * or false in output.
 * 
 * @author ssha53
 */
public class ProfileExistCheckDroplet extends BBBPresentationDroplet {

	/*
	 * ===================================================== * Constants
	 * =====================================================
	 */

	/**
	 * The output parameter that contains the true or false (based on the
	 * profile exist or not).
	 */
	public static final String OUTPUT_COREGISTRANT_STATUS = "doesCoRegistrantExist";
	
	/**
	 * String constant for nonSister status
	 */
	public static final String COREGISTRANT_ON_NON_SISTER_SITE = "nonSister";

	/*
	 * ===================================================== * MEMBER VARIABLES
	 * =====================================================
	 */

	/** The Tools. */
	private ProfileTools mTools;

	private static final String SITE_ID = "siteId";	
	/*
	 * ===================================================== * STANDARD METHODS
	 * =====================================================
	 */

	/**
	 * Generates Boolean object for profile exist for email or not..
	 * 
	 * @param pRequest
	 *            - http request
	 * @param pResponse
	 *            - http response
	 * @throws IOException
	 *             if an error occurs
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
		logDebug(" ProfileExistCheckDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");

		final String emailId = pRequest.getParameter("emailId");
		// Get profileId for coregistrantEmailId if exist
		// String coRegProfileId = null;
		boolean doesCoRegistrantExist = false;
		final RepositoryItem profile = getTools().getItemFromEmail(emailId.toLowerCase());
		if (profile != null) {
			final String coRegProfileId = profile.getRepositoryId();
			if (coRegProfileId != null) {
				doesCoRegistrantExist = true;
			}
		}
		
		String coRegistrantStatus =String.valueOf(doesCoRegistrantExist);
		
		//if user exists then check if this is present on non sister site
		if(doesCoRegistrantExist){

			boolean isUserPresentToOtherGroup = getManager().isUserPresentToOtherGroup(
					profile, pRequest.getParameter(SITE_ID));
			
			if(isUserPresentToOtherGroup){
				coRegistrantStatus = COREGISTRANT_ON_NON_SISTER_SITE;
			}
			
		}
		
		pRequest.setParameter(OUTPUT_COREGISTRANT_STATUS, coRegistrantStatus);
		
		pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);

		logDebug(" ProfileExistCheckDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - end");

	}

	/*
	 * ===================================================== * GETTERS and
	 * SETTERS =====================================================
	 */

	/**
	 * Gets the tools.
	 * 
	 * @return the tools
	 */
	public ProfileTools getTools() {
		return mTools;
	}

	/**
	 * Sets the tools.
	 * 
	 * @param pTools
	 *            the tools to set
	 */
	public void setTools(final ProfileTools pTools) {
		mTools = pTools;
	}
	
	private BBBProfileManager mManager;
	
	/**
	 * @return mManager
	 */
	public BBBProfileManager getManager() {
		return mManager;
	}

	/**
	 * @param pManager
	 */
	public void setManager(BBBProfileManager pManager) {
		mManager = pManager;
	}

}
