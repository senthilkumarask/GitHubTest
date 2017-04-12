package com.bbb.common.droplet;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.browse.webservice.manager.BBBEximManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBEximConstants;

// TODO: Auto-generated Javadoc
/**
 * The Class EximCustomizationDroplet.
 */
public class EximCustomizationDroplet extends BBBDynamoServlet {
	
   /** The exim manager. */
   private BBBEximManager eximManager;
	
	
	/**
	 * Gets the exim manager.
	 *
	 * @return the exim manager
	 */
	public BBBEximManager getEximManager() {
	return eximManager;
    }


   /**
    * Sets the exim manager.
    *
    * @param eximManager the new exim manager
    */
   public void setEximManager(BBBEximManager eximManager) {
	this.eximManager = eximManager;
   }


	/* (non-Javadoc)
	 * @see atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
	 */
	@Override
	public void service(DynamoHttpServletRequest req,
			DynamoHttpServletResponse res) throws ServletException, IOException {
		
		logDebug("EximCustomizationCodesDroplet.service method begins");
		
		HashMap<String,String> eximCustomizationCodesMap = getEximManager().getEximValueMap();
		
		if(null != eximCustomizationCodesMap && !eximCustomizationCodesMap.isEmpty()){
			req.setParameter(BBBEximConstants.EXIM_CUST_CODES_MAP, eximCustomizationCodesMap);
		}
		
		String personalizationOptions = req.getParameter("personalizationOptions");
		if(personalizationOptions != null ){			
			String personalizationOptionsDisplay = getEximManager().getPersonalizedOptionsDisplayCode(personalizationOptions);
			req.setParameter("personalizationOptionsDisplay", personalizationOptionsDisplay);
		}			
		req.serviceLocalParameter(BBBCoreConstants.OPARAM, req, res);
		
		
	}
}



