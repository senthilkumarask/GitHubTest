package com.bbb.common.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.browse.webservice.manager.BBBEximManager;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

/**
 * This Droplet fetches the vendor JS and CSS from the 
 * BBB_CUSTOMIZATION_VENDORS table based on the 3 parameters
 * 1. Cart
 * 2. Product
 * 3. wishlist
 * 
 * @author Amandeep Singh Dhammu
 *
 */

public class GetVendorConfigurationDroplet extends BBBDynamoServlet{
	
	private BBBCatalogToolsImpl catalogTools;
	
	public BBBCatalogToolsImpl getCatalogTools() {
		return catalogTools;
	}
	
	public void setCatalogTools(BBBCatalogToolsImpl catalogTools) {
		this.catalogTools = catalogTools;
	}

	@Override
	public void service(DynamoHttpServletRequest req,
			DynamoHttpServletResponse res) throws ServletException, IOException {

		String pageWrapper = req.getParameter(BBBCoreConstants.PAGE_WRAPPER);
		String productId = req.getParameter(BBBCoreConstants.PRODUCTID);
		String section = req.getParameter(BBBCoreConstants.SECTION);
		
		Set<String> vendorJs = new HashSet<String>();		
					
		try {
			vendorJs = getCatalogTools().getVendorConfigurationJS(productId, section,pageWrapper,BBBCoreConstants.CHANNEL_DESKTOP);
		} catch (BBBSystemException e) {
			logError("System exvception during fetching vendor configuration",e);
		} catch (BBBBusinessException e) {
			logError("Business exception during fetching vendor configuration",e);
		}
		if(!vendorJs.isEmpty()){
			req.setParameter("vendorJs", vendorJs);
		}
		
		req.serviceLocalParameter("output", req, res);
	}

}
