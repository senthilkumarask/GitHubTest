package com.bbb.account.droplet;

import static com.bbb.constants.BBBAccountConstants.OPARAM_ERROR;
import static com.bbb.constants.BBBAccountConstants.OPARAM_OUTPUT;
import static com.bbb.constants.BBBAccountConstants.PARAM_CONFIG_KEY;
import static com.bbb.constants.BBBAccountConstants.PARAM_ERROR_MSG;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.utils.BBBUtility;

public class BBBICrossTagDroplet extends BBBDynamoServlet {

	private BBBCatalogTools mBBBCatalogTools;
	private static final String IS_CATEGORY_ID_PRESENT = "isCategoryIdPresent";
	private static final String CATEGORY_ID = "categoryId";

	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		logDebug("BBBICrossTagDroplet : Start");
		String configKey = pRequest.getParameter(PARAM_CONFIG_KEY);
		String categoryId = pRequest.getParameter(CATEGORY_ID);
		boolean isCategoryIdPresent = false;
		logDebug("configKey is:" + configKey);
		logDebug("categoryId" + categoryId);
		try {
			final Map<String, String> keyMap = getBBBCatalogTools().getConfigValueByconfigType(configKey);
			Set<String> configKeySet = new HashSet<String>();
			configKeySet = keyMap.keySet();
			
			if (!BBBUtility.isBlank(categoryId) && !configKeySet.isEmpty()) {
				if (configKeySet.contains(categoryId)) {
					isCategoryIdPresent = true;
				}
			}
			logDebug("Category Presnt in config Keys  : " + isCategoryIdPresent);
			pRequest.setParameter(IS_CATEGORY_ID_PRESENT, isCategoryIdPresent);
			pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
		} catch (BBBSystemException e) {
			logError("BBBSystemException:" + e);
			pRequest.setParameter(PARAM_ERROR_MSG, "Error");
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
		} catch (BBBBusinessException e) {
			logError("BBBBusinessException:" + e);
			pRequest.setParameter(PARAM_ERROR_MSG,"Error");
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
		}

		logDebug("BBBICrossTagDroplet : End");

	}

	/**
	 * @return the mBBBCatalogTools
	 */
	public BBBCatalogTools getBBBCatalogTools() {
		return mBBBCatalogTools;
	}

	/**
	 * @param mBBBCatalogTools
	 *            the mBBBCatalogTools to set
	 */
	public void setBBBCatalogTools(final BBBCatalogTools pBBBCatalogTools) {
		this.mBBBCatalogTools = pBBBCatalogTools;
	}

}
