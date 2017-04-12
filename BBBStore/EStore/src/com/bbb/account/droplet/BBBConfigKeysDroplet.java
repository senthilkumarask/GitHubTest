/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  BBBConfigKeysDroplet.java
 *
 *  DESCRIPTION: BBBConfigKeysDroplet load the config map from config repository for
 *  			 the given config key.
 *  			 
 *  HISTORY:
 *  02/16/2012 Initial version
 *	02/20/2012 Moved constants to BBBAccountConstants.java file
 *
 */
package com.bbb.account.droplet;

import java.io.IOException;

import java.util.Map;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.common.BBBDynamoServlet;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

import static com.bbb.constants.BBBAccountConstants.*;

public class BBBConfigKeysDroplet extends BBBDynamoServlet {
	
	private BBBCatalogTools mBBBCatalogTools;

	@Override
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		final String configKey= pRequest.getParameter(PARAM_CONFIG_KEY);
			logDebug("configKey is:"+ configKey);
		try {
			final Map<String, String> keyMap = getBBBCatalogTools().getConfigValueByconfigType(configKey);
				logDebug("configKey is:"+ configKey);
			pRequest.setParameter(PARAM_CONFIG_MAP,keyMap);
			pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
			return;
		} catch (BBBSystemException e) {
				logError("BBBSystemException:"+ e);
			pRequest.setParameter(PARAM_ERROR_MSG,e.toString());
		} catch (BBBBusinessException e) {
				logError("BBBBusinessException:"+ e);
			pRequest.setParameter(PARAM_ERROR_MSG,e.toString());
		}
		
		pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);

	}
	
	/**
	 * @return the mBBBCatalogTools
	 */
	public BBBCatalogTools getBBBCatalogTools() {
		return mBBBCatalogTools;
	}
	/**
	 * @param mBBBCatalogTools the mBBBCatalogTools to set
	 */
	public void setBBBCatalogTools(final BBBCatalogTools pBBBCatalogTools) {
		this.mBBBCatalogTools = pBBBCatalogTools;
	}


}
