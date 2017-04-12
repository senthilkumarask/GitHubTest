package com.bbb.certona;

import java.util.HashMap;
//import java.util.List;
import java.util.Map;

import atg.nucleus.ServiceException;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

/**
 * This class contains Certona Specific Configurations
 * 
 * @author ikhan2
 *
 */
public class CertonaConfig extends BBBGenericService {

	private Map<String,String> siteIdAppIdMap;
	private static final String CERTONA_KEYS = "CertonaKeys";
	private BBBCatalogTools mCatalogTools;
	
	public Map<String, String> getSiteIdAppIdMap() {
		
		if(siteIdAppIdMap == null){
			try {
				siteIdAppIdMap = getCatalogTools().getConfigValueByconfigType(CERTONA_KEYS);
				
				logDebug("CLS=[CertonaConfig] MSG=" +
						"[Fetched siteIDAppId Map for Certona from catalog ="+siteIdAppIdMap);
				
			} catch (BBBSystemException e) {
				logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1057+" BBBSystemException [Could not find CERTONA_KEYS in Catalog ] of getSiteIdAppIdMap from CertonaConfig",e);
				return new HashMap<String,String>();
				
			} catch (BBBBusinessException e) {
				logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1058+" BBBBusinessException [Could not find CERTONA_KEYS in Catalog ] of getSiteIdAppIdMap from CertonaConfig",e);
				return new HashMap<String,String>();
			}
		}
		
		return siteIdAppIdMap;
	}

	public void setSiteIdAppIdMap(final Map<String, String> siteIdAppIdMap) {
		this.siteIdAppIdMap = siteIdAppIdMap;
	}
	
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}
	
	public void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.mCatalogTools = catalogTools;
	}
	
	/**
	 * This method starts the service as a separate thread.
	 * 
	 * @throws ServiceException
	 *             an exception of type ServiceException
	 */
	public void doStartService() throws ServiceException {
		//Re-initialize the instance objects
		getSiteIdAppIdMap();
	}

	/**
	 * Uninitialize the object.
	 * 
	 * @throws ServiceException
	 *             an exception of type ServiceException
	 */
	public void doStopService() throws ServiceException {
		if(siteIdAppIdMap != null){
			siteIdAppIdMap = null;
		}
	}
	
}
