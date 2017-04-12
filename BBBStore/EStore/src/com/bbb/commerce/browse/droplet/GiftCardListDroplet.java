package com.bbb.commerce.browse.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.giftregistry.droplet.BBBPresentationDroplet;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;

/**
 * To populate addresses entered for shipping, profile addresses
 * 
 * @author Vipul Agarwal
 * @story UC_GiftCardList
 * @description The droplet retrieves the list of ProductVOs and passes to the gift_card_grid.jsp.
 * @version 1.0
 */

public class GiftCardListDroplet extends BBBPresentationDroplet {

	private SiteContextManager mSiteContextManager;
	private BBBCatalogTools mCatalogTools;

	/**
	 * @return the siteContextManager
	 */
	public final SiteContextManager getSiteContextManager() {
		return mSiteContextManager;
	}

	/**
	 * @param pSiteContextManager
	 *            the siteContextManager to set
	 */
	public final void setSiteContextManager(
			SiteContextManager pSiteContextManager) {
		mSiteContextManager = pSiteContextManager;
	}

	/**
	 * @return the catalogTools
	 */
	public final BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param pCatalogTools
	 *            the catalogTools to set
	 */
	public final void setCatalogTools(BBBCatalogTools pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}

	/**
	 * The method retrieves the list of Product VOs from Catalog.
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		
		String siteId = extractCurrentSiteId();
		if(siteId == null){
			siteId = pRequest.getParameter("siteId");
		}
		logDebug("siteId: " + siteId);
		try {
			List<ProductVO> productVOList = getCatalogTools().getGiftProducts(
					siteId);
			logDebug("productVOList: " + productVOList);
			
			pRequest.setParameter(BBBSearchBrowseConstants.PRODUCT_VO_LIST,
					productVOList);
			pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);

		} catch (BBBSystemException e) {
			pRequest.setParameter(OUTPUT_ERROR_MSG, e.getMessage());
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "Business Exception from service of GiftCardListDroplet ",BBBCoreErrorConstants.BROWSE_ERROR_1029),e);
			pRequest.setParameter(OUTPUT_ERROR_MSG, e.getMessage());
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
		}

	}

	protected String extractCurrentSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}
}
