package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBUtility;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

public class CanadaURLGeneratorDroplet extends BBBPresentationDroplet {
	
	/**
	 * to hold catalog tool variable
	 */	
	private BBBCatalogTools catalogTools;
	
	/**
	 * baby CA is True
	 */
	private static final String BABY_CA_PARAM = "?babyCA=true";
		
	/**
	 *  This method will be used to get catalog tools.
	 *  
	 * @return catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}
	
	/**
	 * This method will be used to set catalog tools.
	 * 
	 * @param catalogTools - Catalog Tools
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
	
	/**
	 * This class will be used to generator canada url.
	 * 
	 * @param request
	 *            - HTTP Request
	 * @param response
	 *            - HTTP Response
	 * @throws IOException
	 *             if an error occurs
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		BBBPerformanceMonitor.start("CanadaURLGeneratorDroplet", "service");
	
		logDebug(" CanadaURLGeneratorDroplet service(DynamoHttpServletRequest," +
				" DynamoHttpServletResponse) - start");
	
		String finalCanadaURL = null;
		List<String> buyBuyBabyCAURL;
		try {
			buyBuyBabyCAURL = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.REFERRAl_CONTROLS,
					BBBCoreConstants.BABY_CANADA_SOURCE_URL);
			if(!BBBUtility.isListEmpty(buyBuyBabyCAURL)){
				finalCanadaURL=buyBuyBabyCAURL.get(0);
			}
			///finalCanadaURL = finalCanadaURL ;
			pRequest.setParameter("canadaURL", finalCanadaURL);
			pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
		} catch (BBBSystemException e) {
			logError("Error while generating URL for Canada Site", e);
		} catch (BBBBusinessException e) {
			logError("Error while generating URL for Canada Site", e);
		}
		BBBPerformanceMonitor.end("CanadaURLGeneratorDroplet", "service");	
		logDebug(" CanadaURLGeneratorDroplet service(DynamoHttpServletRequest," +
				" DynamoHttpServletResponse) - End");
		
	}

}
