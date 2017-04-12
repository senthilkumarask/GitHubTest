package com.bbb.commerce.browse.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import com.bbb.commerce.checkout.manager.BBBSameDayDeliveryManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;


/**
 * The Class BBBCreateSDDCookieDroplet.
 */
public class BBBCreateSDDCookieDroplet extends BBBDynamoServlet{
	
	/** The sdd cookie name. */
	private String sddCookieName;
	
	/** The sdd manager. */
	private BBBSameDayDeliveryManager sddManager;
	private String homePageURL;
	
	/* (non-Javadoc)
	 * @see atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
	 */
	@Override
	public void service(DynamoHttpServletRequest request, DynamoHttpServletResponse response)throws ServletException, IOException {
		logDebug("BBBCheckSDDEligibilityServlet.start" + request.getRequestURI());
		
		BBBSessionBean sessionBean = (BBBSessionBean) request.getObjectParameter(BBBCoreConstants.SESSION_BEAN_NAME);
		logDebug("is request from homepage: "+sessionBean.getReqFromHomepage());
		if(checkIfHomepageRequest(sessionBean,request)){
		String sddCookieValue = BBBUtility.getCookie(request, this.getSddCookieName());
		String domain = request.getParameter(BBBCoreConstants.DOMAIN_REQ_PARAM);
		if(BBBUtility.isEmpty(domain)){
			if(request.getServerName()!=null) {
				domain = request.getServerName();
			} else {
				domain = BBBCoreConstants.BLANK;
			}
			logDebug("Cookie Domain value: "+domain);
		}
		if (BBBUtility.isEmpty(sddCookieValue)) {
			getSddManager().createSDDModalCookie(domain,response,sessionBean,getSddCookieName());
			request.serviceLocalParameter(BBBCoreConstants.OUTPUT, request, response);
		}else{
			sessionBean.setShowSDD(false);
			logDebug("Sdd modal cookie exists");
		}
		}
		
		logDebug("BBBCheckSDDEligibilityServlet.end" + request.getRequestURI());
	}
	
	public boolean checkIfHomepageRequest(BBBSessionBean sessionBean,DynamoHttpServletRequest request){
    
    if((BBBUtility.isEmpty(sessionBean.getReqFromHomepage()) 
				|| sessionBean.getReqFromHomepage().equalsIgnoreCase(BBBCoreConstants.TRUE)) 
				&& null != sessionBean.getLandingZipcodeVO() && sessionBean.getLandingZipcodeVO().isSddEligibility()){
    	String isAkamaiEnabled=BBBConfigRepoUtils.getAllValues(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.HOME_PAGE_CACHING_KEY).get(0);
    	if(Boolean.parseBoolean(isAkamaiEnabled) && request.getRequestURI().contains(getHomePageURL())){
    		return false;
    	}

    	return true;
    }else{
    	return false;
    }
	}

	/**
	 * Gets the sdd cookie name.
	 *
	 * @return the sdd cookie name
	 */
	public String getSddCookieName() {
		return sddCookieName;
	}
	

	/**
	 * Sets the sdd cookie name.
	 *
	 * @param sddCookieName the new sdd cookie name
	 */
	public void setSddCookieName(String sddCookieName) {
		this.sddCookieName = sddCookieName;
	}

	/**
	 * Gets the sdd manager.
	 *
	 * @return the sdd manager
	 */
	public BBBSameDayDeliveryManager getSddManager() {
		return sddManager;
	}
	

	/**
	 * Sets the sdd manager.
	 *
	 * @param sddManager the new sdd manager
	 */
	public void setSddManager(BBBSameDayDeliveryManager sddManager) {
		this.sddManager = sddManager;
	}

	public String getHomePageURL() {
		return homePageURL;
	}
	

	public void setHomePageURL(String homePageURL) {
		this.homePageURL = homePageURL;
	}
	
	
	

}
