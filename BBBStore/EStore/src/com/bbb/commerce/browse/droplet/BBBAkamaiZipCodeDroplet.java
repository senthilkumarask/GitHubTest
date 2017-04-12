/**
 * 
 */
package com.bbb.commerce.browse.droplet;

import java.io.IOException;
import javax.servlet.ServletException;

import com.bbb.commerce.porch.service.PorchServiceManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.profile.session.BBBSessionBean;

import atg.core.util.StringUtils;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

/**
 * @author sm0191
 *
 */
public class BBBAkamaiZipCodeDroplet extends BBBDynamoServlet {

	/**
	 * 
	 */
	public BBBAkamaiZipCodeDroplet() {
		// TODO Auto-generated constructor stub
	}
	
	private BBBSessionBean sessionBean;
	
	private PorchServiceManager porchServiceManager;

	/* (non-Javadoc)
	 * @see atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
	 */
	@Override
	public void service(DynamoHttpServletRequest req, DynamoHttpServletResponse res)
			throws ServletException, IOException {

		BBBSessionBean bbbsessionBean = (BBBSessionBean) req.resolveName("/com/bbb/profile/session/SessionBean");
		String porchZipCode = bbbsessionBean.getPorchZipCode();
		if(!StringUtils.isBlank(porchZipCode)){
			req.setParameter("zipCode", porchZipCode);
			req.serviceParameter(BBBCoreConstants.OPARAM, req,
					res);
		} else{
		Profile userProfile = (Profile) req.getObjectParameter("profile");
		String zipCode=null;	 
		if(userProfile.isTransient()){
			zipCode = getPorchServiceManager().zipCodeFromAkamaiHeader(req);
		}
		else {
			 RepositoryItem shippingAddress = (RepositoryItem) userProfile.getPropertyValue("shippingAddress");
			 if(null!=shippingAddress && null!=shippingAddress.getPropertyValue("postalCode")){
				 zipCode = (String) shippingAddress.getPropertyValue("postalCode");
			 }
			 else{
				 zipCode = getPorchServiceManager().zipCodeFromAkamaiHeader(req);
			 }
			}
		 
		
		req.setParameter("zipCode", zipCode);
		req.serviceParameter(BBBCoreConstants.OPARAM, req,
				res);
		}
	}

	

	/**
	 * @return the sessionBean
	 */
	public BBBSessionBean getSessionBean() {
		return sessionBean;
	}

	/**
	 * @param sessionBean the sessionBean to set
	 */
	public void setSessionBean(BBBSessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}



	/**
	 * @return the porchServiceManager
	 */
	public PorchServiceManager getPorchServiceManager() {
		return porchServiceManager;
	}



	/**
	 * @param porchServiceManager the porchServiceManager to set
	 */
	public void setPorchServiceManager(PorchServiceManager porchServiceManager) {
		this.porchServiceManager = porchServiceManager;
	}

	
}
