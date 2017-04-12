package com.bbb.commerce.giftregistry.droplet;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.RegistryTypeVO;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * This class validates registry type code with defined registry type codes and if registry type is invalid it will return default registry type code .
 * 
 * 
 * @author sjai81
 */
public class GetValidRegistryTypeDroplet extends BBBPresentationDroplet {

	public static final String SITE_ID = "siteId";
	public static final String REG_TYPE = "regType";
	public static final String VALID_REG_TYPE = "validRegType";
	private GiftRegistryManager mGiftRegistryManager;
	private BBBCatalogTools catalogTools;

	/**
	 * Return Valid Registry type
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
		
		logDebug(" GetValidRegistryTypeDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");

		String registryType = (String) pRequest.getParameter(REG_TYPE);
		String siteId = pRequest.getParameter(SITE_ID);

		boolean validRegistryType = false;
		List<RegistryTypeVO> registryTypeList;
		try {
			registryTypeList = getGiftRegistryManager().fetchRegistryTypes(siteId);
			if(!BBBUtility.isListEmpty(registryTypeList)){
				for(RegistryTypeVO type : registryTypeList){
					if(type.getRegistryCode().equalsIgnoreCase(registryType)){
						validRegistryType = true;
						break;
					}
				}
			}
			if (validRegistryType) {
				pRequest.setParameter(VALID_REG_TYPE, registryType);
				pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
			} else {
				logDebug("Invalid registry type parameter. Setting default registry type from config key");
				String defaultRegistryType = null;
				List<String> keyValueList = getCatalogTools().getAllValuesForKey("GiftRegistryConfig","DefaultRegistryTypeForThirdParty");

				if (!BBBUtility.isListEmpty(keyValueList)) {
					defaultRegistryType = keyValueList.get(0);
				}
				pRequest.setParameter(VALID_REG_TYPE, defaultRegistryType);
				pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
			}

		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "BBBBusinessException from service of GetValidRegistryTypeDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004),e);
			pRequest.setParameter(OUTPUT_ERROR_MSG,
					"err_regsearch_biz_exception");
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "BBBSystemException from service of GetValidRegistryTypeDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1005),e);
			pRequest.setParameter(OUTPUT_ERROR_MSG,
					"err_regsearch_sys_exception");
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
		} catch (RepositoryException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "Repository Exception from service of GetValidRegistryTypeDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1001), e);
			pRequest.setParameter(OUTPUT_ERROR_MSG,
					"err_regsearch_repo_exception");
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
		}

		logDebug(" GetValidRegistryTypeDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - end");

	}
	
	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * Gets the gift registry manager.
	 * 
	 * @return the giftRegistryManager
	 */
	public GiftRegistryManager getGiftRegistryManager() {
		return mGiftRegistryManager;
	}

	/**
	 * Sets the gift registry manager.
	 * 
	 * @param pGiftRegistryManager
	 *            the giftRegistryManager to set
	 */
	public void setGiftRegistryManager(GiftRegistryManager pGiftRegistryManager) {
		mGiftRegistryManager = pGiftRegistryManager;
	}
	
}
