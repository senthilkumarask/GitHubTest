package com.bbb.pipeline;

import java.util.List;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

public class TBSCheckSesionExpireServlet extends BBBCheckSesionExpireServlet {

	private BBBCatalogTools catalogTools;
	
	/**
	 * This method is overridden for TBS to fetch host path.
	 * 
	 * @return the host url
	 * 
	 */
	@Override
	public String getHost()  {

		if (isLoggingDebug()) {
			logDebug("[Start]: getHost()");
		}
		String hostpath = BBBCoreConstants.BLANK;
		List<String> configValue = null;
		try {
			configValue = this.getCatalogTools().getAllValuesForKey(
					BBBCoreConstants.MOBILEWEB_CONFIG_TYPE, BBBCoreConstants.REQUESTDOMAIN_CONFIGURE);
		} catch (BBBSystemException e) {
			logError("TBSCheckSesionExpireServlet.getHost :: System Exception occured while fetching config value for config key " 
						+ BBBCoreConstants.REQUESTDOMAIN_CONFIGURE + "config type " + BBBCoreConstants.MOBILEWEB_CONFIG_TYPE + e);
		} catch (BBBBusinessException e) {
			logError("TBSCheckSesionExpireServlet.getHost :: Business Exception occured while fetching config value for config key " 
						+ BBBCoreConstants.REQUESTDOMAIN_CONFIGURE + "config type " + BBBCoreConstants.MOBILEWEB_CONFIG_TYPE + e);
		}
		
		if(configValue != null && configValue.size() > 0){
			hostpath = configValue.get(0);
		}
		if (isLoggingDebug()) {
			logDebug("hostpath: " + hostpath);
			logDebug("[End]: getHost()");
		}

		return hostpath;
	}

	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
}
