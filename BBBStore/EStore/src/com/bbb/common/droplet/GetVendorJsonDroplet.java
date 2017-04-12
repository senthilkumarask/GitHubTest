package com.bbb.common.droplet;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletException;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.browse.webservice.manager.BBBEximManager;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBEximConstants;
import com.bbb.repositorywrapper.IRepositoryWrapper;
import com.bbb.repositorywrapper.RepositoryWrapperImpl;

/**
 * This droplet class gives the json modal for the vendors
 * @author asi162
 *
 */

public class GetVendorJsonDroplet extends BBBDynamoServlet{

	
	private MutableRepository vendorRepository; 
	
	private BBBEximManager eximManager;
	
	public MutableRepository getVendorRepository() {
		return vendorRepository;
	}

	public void setVendorRepository(MutableRepository vendorRepository) {
		this.vendorRepository = vendorRepository;
	}

	public BBBEximManager getEximManager() {
		return eximManager;
	}

	public void setEximManager(BBBEximManager eximManager) {
		this.eximManager = eximManager;
	}

	@Override
	public void service(DynamoHttpServletRequest req,
			DynamoHttpServletResponse res) throws ServletException, IOException {
	
		
		
		String vendorJsonString = getEximManager().getVendorJSON();
		
		
		if(!vendorJsonString.equalsIgnoreCase("{}")){
			req.setParameter(BBBEximConstants.VENDORS_JSON, vendorJsonString);
		}else{
			req.setParameter(BBBEximConstants.VENDORS_JSON, BBBCoreConstants.BLANK);
		}
		
		//req.setParameter("jsonString", jsonString);
		req.serviceLocalParameter(BBBCoreConstants.OPARAM, req, res);
		
		
	}

	
	
	
}
