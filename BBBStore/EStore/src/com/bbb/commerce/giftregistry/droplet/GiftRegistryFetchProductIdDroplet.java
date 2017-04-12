package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;
import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

// TODO: Auto-generated Javadoc
/**
 * The Class GiftRegistryFetchProductIdDroplet.
 */
public class GiftRegistryFetchProductIdDroplet extends BBBPresentationDroplet {

	/** The Catalog tools. */
	private BBBCatalogTools mCatalogTools;

	/**
	 * Gets the catalog tools.
	 * 
	 * @return the catalogTools
	 */
	public final BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * Sets the catalog tools.
	 * 
	 * @param pCatalogTools
	 *            the catalogTools to set
	 */
	public final void setCatalogTools(BBBCatalogTools pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}

	/**
	 * The method retrieves the list of Product VOs from Catalog.
	 * 
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		logDebug("Entering Service Method of GiftRegistryFetchProductIdDroplet.");
		final String skuId = pRequest
				.getParameter(BBBGiftRegistryConstants.SKU_ID);
		logDebug("skuId: " + skuId);
		String productId;
		boolean inStore=false;
		try {

			productId = getCatalogTools().getParentProductForSku(skuId);
			if(BBBUtility.isEmpty(productId)){
				productId = getCatalogTools().getParentProductForSku(skuId, true);
				inStore = true;
			}
			pRequest.setParameter("inStore", inStore);
			logDebug("productId: " + productId);
			pRequest.setParameter(BBBGiftRegistryConstants.PRODUCTID, productId);
			pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);

		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "BBBBusinessException from SERVICE of GiftRegistryFetchProductIdDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004),e);
			pRequest.setParameter(OUTPUT_ERROR_MSG,
					BBBGiftRegistryConstants.BUSINESS_EXCEPTION);
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "BBBSystemException from SERVICE of GiftRegistryFetchProductIdDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1005),e);
			pRequest.setParameter(OUTPUT_ERROR_MSG,
					BBBGiftRegistryConstants.SYSTEM_EXCEPTION);
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
		} 
		logDebug("Exit Service Method of GiftRegistryFetchProductIdDroplet.");
	}
}
