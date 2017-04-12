package com.bbb.simplifyRegistry.droplet;

import atg.core.util.StringUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.account.BBBProfileTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.profile.session.BBBSessionBean;

public class SimpleRegVerifyPrimaryUserDroplet extends BBBDynamoServlet {
	
	private BBBProfileTools profileTools;
	public static final String PROFILE_STATUS = "profileStatus";

	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
			throws javax.servlet.ServletException, java.io.IOException {
		String email = ((String) pRequest.getLocalParameter(BBBCoreConstants.EMAIL));
		logDebug(new StringBuilder("Inside service method of SimpleRegVerifyPrimaryUserDroplet with Email : ").append(email).toString());
	    String profileStatus = null;
	    BBBSessionBean sessionBean = ((BBBSessionBean) (pRequest.resolveName(BBBCoreConstants.SESSION_BEAN)));
		try {
				profileStatus = getProfileTools().checkForRegistration(email);
			} catch (BBBBusinessException e) {
				if(isLoggingError()){
					logError("Error while validating the profile is valid or not by mail id: "+e,e);
				}
				pRequest.serviceParameter(BBBCoreConstants.ERROR_OPARAM, pRequest,	pResponse);
				return;
			}
			 if(!StringUtils.isBlank(profileStatus)){
				 if(profileStatus.equalsIgnoreCase("profile_already_exist")){
					 sessionBean.setRegistryProfileStatus("regUserAlreadyExists");
					 sessionBean.setRegistredUser(true);
				 }
				 else if(profileStatus.equalsIgnoreCase("profile_available_for_extenstion")){
					 sessionBean.setRegistredUser(true);
					 sessionBean.setRegistryProfileStatus("refProfileExtenssion");
				 }
				 
				 else if(profileStatus.equalsIgnoreCase("Profile not found")){
					 sessionBean.setRegistryProfileStatus("regNewUser");
					 sessionBean.setRegistredUser(false);
				 }
			 }
			
	    sessionBean.setUserEmailId(email);
		pRequest.setParameter(PROFILE_STATUS,sessionBean.getRegistryProfileStatus());
		pRequest.serviceParameter(BBBCmsConstants.OUTPUT, pRequest,	pResponse);
	
	}
		
		public BBBProfileTools getProfileTools() {
			return profileTools;
		}


		public void setProfileTools(BBBProfileTools profileTools) {
			this.profileTools = profileTools;
}

}