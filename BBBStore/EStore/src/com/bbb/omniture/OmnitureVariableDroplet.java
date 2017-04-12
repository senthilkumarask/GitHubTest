package com.bbb.omniture;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.search.endeca.constants.BBBEndecaConstants;
import com.bbb.utils.BBBUtility;

public class OmnitureVariableDroplet extends BBBDynamoServlet{
	
	private BBBCatalogTools bbbCatalogTools;
	
	
	/**
	 * @return the bbbCatalogTools
	 */
	public BBBCatalogTools getBbbCatalogTools() {
		return bbbCatalogTools;
	}

	/**
	 * @param bbbCatalogTools the bbbCatalogTools to set
	 */
	public void setBbbCatalogTools(BBBCatalogTools bbbCatalogTools) {
		this.bbbCatalogTools = bbbCatalogTools;
	}


	@Override
	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) 
			throws ServletException, IOException{
		String methodName = "GetOmnitureVariable.service";
		
		BBBPerformanceMonitor.start(BBBPerformanceConstants.OMNITURE_VARIABLE_DROPLET, methodName);
		
		final String lOmnitureVariable = this.getBbbCatalogTools().getOmnitureVariable(pRequest);
			pRequest.setParameter(BBBCoreConstants.OMNITURE_VARIABLE, lOmnitureVariable);
		pRequest.serviceParameter(BBBCoreConstants.OPARAM, pRequest, pResponse);
		BBBPerformanceMonitor.end(BBBPerformanceConstants.OMNITURE_VARIABLE_DROPLET, methodName);
	}
}
