package com.bbb.common.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.browse.webservice.manager.BBBEximManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBEximConstants;

/**
 * This class is used to get the exim customization codes 
 * from the BBB_EXIM_CUSTOMIZATION_CODES table, 
 * which are used to display the single code on the front end.
 * @author asi162
 *
 */

public class GetEximCustomizationCodesDroplet  extends BBBDynamoServlet{

	
	private BBBEximManager customizationManager;
	
	/**
	 * 
	 * @return customizationManager
	 */
	public BBBEximManager getCustomizationManager() {
		return customizationManager;
	}

	/**
	 * 
	 * @param customizationManager
	 */
	public void setCustomizationManager(
			BBBEximManager customizationManager) {
		this.customizationManager = customizationManager;
	}




	@Override
	public void service(DynamoHttpServletRequest req,
			DynamoHttpServletResponse res) throws ServletException, IOException {
		
		logDebug("EximCustomizationCodesDroplet.service method begins");
		
		String eximCodesJsonString = getCustomizationManager().getCustomizationCodes();
		
		if(!eximCodesJsonString.equalsIgnoreCase("{}")){
			req.setParameter(BBBEximConstants.EXIM_CODES_JSON_STRING, eximCodesJsonString);
		}else{
			req.setParameter(BBBEximConstants.EXIM_CODES_JSON_STRING, BBBCoreConstants.BLANK);
		}
		req.serviceLocalParameter(BBBCoreConstants.OPARAM, req, res);
	}
}
