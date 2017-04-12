package com.bbb.commerce.giftregistry.droplet;
import java.io.IOException;

import javax.servlet.ServletException;

import org.jsoup.helper.StringUtil;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

/**
 * This class get the registry type name for a given registry type code.
 * 
 * 
 * @author ssha53
 */
public class GetRegistryTypeNameDroplet extends BBBPresentationDroplet {

	/**
	 * The output parameter that contains the registryVO get from web service
	 * call.
	 */
	public static final String OUTPUT_REGISTRY_TYPE_NAME = "registryTypeName";

	public static final String SITE_ID = "siteId";
	
	/** The Catalog tools. */
	private BBBCatalogTools mCatalogTools;

	/**
	 * Return Registry type name for given registry code.
	 * 
	 * @param pRequest
	 *            - http request
	 * @param pResponse
	 *            - http response
	 * @throws IOException
	 *             if an error occurs
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
		logDebug(" GetRegistryTypeNameDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");

		final String registryTypeCode = pRequest
				.getParameter("registryTypeCode");
		String siteId = pRequest.getParameter(SITE_ID);
 
		
		if(BBBUtility.isBlank(siteId))
		{
			siteId = SiteContextManager.getCurrentSiteId();
		}
		
		
		String registryTypeName = null;
		try {

			if (null != registryTypeCode) {
				registryTypeName = getCatalogTools().getRegistryTypeName(
						registryTypeCode,siteId);
			}

			pRequest.setParameter(OUTPUT_REGISTRY_TYPE_NAME, registryTypeName);
			pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
		} catch (BBBBusinessException e) {
			pRequest.setParameter(OUTPUT_ERROR_MSG, e.getMessage());
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
			logError(LogMessageFormatter.formatMessage(pRequest, "Get Registry name by registy code BBBBusinessException from service of GetRegistryTypeNameDroplet ",BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1070),e);
		} catch (BBBSystemException e) {
			pRequest.setParameter(OUTPUT_ERROR_MSG, e.getMessage());
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
			logError(LogMessageFormatter.formatMessage(pRequest, "Get Registry name by registy code BBBSystemException from service of GetRegistryTypeNameDroplet ",BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1071),e);
		}
		logDebug(" GetRegistryTypeNameDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - end");

	}

	/**
	 * Gets the catalog tools.
	 * 
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * Sets the catalog tools.
	 * 
	 * @param catalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		mCatalogTools = catalogTools;
	}

}
