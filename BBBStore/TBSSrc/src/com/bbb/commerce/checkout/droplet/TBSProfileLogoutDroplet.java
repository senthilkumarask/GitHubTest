/**
 * 
 */
package com.bbb.commerce.checkout.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import com.bbb.account.BBBProfileTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.tbs.account.TBSProfileFormHandler;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;


/**
 * @author sm0191
 *
 */
public class TBSProfileLogoutDroplet extends BBBDynamoServlet {
	
	
	private BBBProfileTools profileTools;
	private TBSProfileFormHandler profileformHandler;
	
	/* (non-Javadoc)
	 * @see atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
	 */
	@Override
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		// TODO Auto-generated method stub
		 
		if (!getProfileformHandler().getProfile().isTransient()){
			
			getProfileformHandler().postLogoutUser(pRequest, pResponse);			 
	    }
		//ProfileForm.logo
		pRequest.serviceParameter("output", pRequest, pResponse);
	}

	/**
	 * @return the profileTools
	 */
	public BBBProfileTools getProfileTools() {
		return profileTools;
	}

	/**
	 * @param profileTools the profileTools to set
	 */
	public void setProfileTools(BBBProfileTools profileTools) {
		this.profileTools = profileTools;
	}

	/**
	 * @return the profileformHandler
	 */
	public TBSProfileFormHandler getProfileformHandler() {
		return profileformHandler;
	}

	/**
	 * @param profileformHandler the profileformHandler to set
	 */
	public void setProfileformHandler(TBSProfileFormHandler profileformHandler) {
		this.profileformHandler = profileformHandler;
	}

	  
}
