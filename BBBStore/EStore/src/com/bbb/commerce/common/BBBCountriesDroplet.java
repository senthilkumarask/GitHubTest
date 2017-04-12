package com.bbb.commerce.common;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

/**
 * The class is the extension of the ATG DynamoServlet. The class is responsible
 * rendering the Countries for Credit card.
 * 
 * @author smalho
 * 
 */
public class BBBCountriesDroplet extends BBBDynamoServlet {

	private BBBCatalogToolsImpl catalogTools;
	public static final String OPARAM_OUTPUT = "output";
	public static final String OPARAM_COUNTRY = "country";
	public static final String OPARAM_ERROR = "error";
	public static final String OPARAM_EMPTY = "empty";
	public static final String OUTPUT_ERROR_MSG = "errorMsg";

	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		logDebug("Entry BBBCountriesDroplet.service");
		BBBPerformanceMonitor.start("BBBCountriesDroplet", "service");
		Map<String, String> countryMap = new HashMap<String, String>();

		try {
		    	String countryCode = pRequest.getParameter(BBBCatalogConstants.COUNTRY_CODE_PROPERTY);
			countryMap = getCatalogTools().getCountriesInfo(countryCode);
			countryMap=	BBBUtility.sortByComparator(countryMap);
			pRequest.setParameter("country", countryMap);
			pRequest.serviceParameter(OPARAM_OUTPUT, pRequest, pResponse);
		} catch (BBBSystemException e) {
			pRequest.setParameter(OUTPUT_ERROR_MSG, e.getMessage());
			logError(LogMessageFormatter.formatMessage(pRequest,
					"Business Exception from service of BBBCountriesDroplet ",
					BBBCoreErrorConstants.GET_COUNTRY_1387), e);
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
		} catch (BBBBusinessException e) {
			pRequest.setParameter(OUTPUT_ERROR_MSG, e.getMessage());
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
			logError(LogMessageFormatter.formatMessage(pRequest,
					"System Exception from service of BBBCountriesDroplet ",
					BBBCoreErrorConstants.GET_COUNTRY_1388), e);
		}
		logDebug(" BBBCountriesDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - ends");
		BBBPerformanceMonitor.end("BBBCountriesDroplet", "service");
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogToolsImpl getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogToolsImpl catalogTools) {
		this.catalogTools = catalogTools;
	}

}
