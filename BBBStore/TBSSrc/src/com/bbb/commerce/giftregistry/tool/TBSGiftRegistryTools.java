package com.bbb.commerce.giftregistry.tool;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.giftregistry.vo.RegistryItemVO;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;


/**
 * 
 * This class provides the low level functionality for gift registry
 * creation/manipulation.
 * 
 * 
 */
public class TBSGiftRegistryTools extends GiftRegistryTools {

	/**
	 * This method is a wrapper for getSKUDetails to include sku's parent
	 * product Id in the response
	 * 
	 * @param siteId
	 * @param skuId
	 * @return skuVO
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	
	@Override
	public SKUDetailVO getSKUDetailsWithProductId(final String siteId,
			final String skuId, RegistryItemVO registryItemVO)
			throws BBBSystemException, BBBBusinessException {
		boolean productWebOffered = false;
		boolean skuWebOffered = false;
		SKUDetailVO skuVO = this.getCatalogTools().getSKUDetails(
				SiteContextManager.getCurrentSiteId(), skuId.toString());
		RepositoryItem productRepoItem = getCatalogTools()
				.getParentProductItemForSku(skuId);
		// For Story BPSI-5384 - Update 3 flags (i.e. Disabled flag, Out of
		// stock flag, International shipping restriction flag.
		skuVO.setDisableFlag(((Boolean) skuVO.getSkuRepositoryItem()
				.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME)));
		if (!skuVO.isDisableFlag()) {
			try {
				int inStockStatus = getInventoryManager().getATGInventoryForTBS(siteId, skuId,
								BBBInventoryManager.PRODUCT_DISPLAY, 0);
				if (inStockStatus == BBBInventoryManager.AVAILABLE
						|| inStockStatus == BBBInventoryManager.LIMITED_STOCK) {
					skuVO.setSkuInStock(true);
				}
			} catch (BBBBusinessException e) {
				logDebug("Product not available", e);
			}
		}
		if (productRepoItem != null) {
			String productId = (String) productRepoItem.getPropertyValue(BBBCatalogConstants.ID);
			skuVO.setParentProdId(productId);
			// Set Seo url of parent product if sku is active and weboffered
			if (skuVO.getSkuRepositoryItem().getPropertyValue(
					BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) != null) {
				skuWebOffered = ((Boolean) skuVO.getSkuRepositoryItem()
						.getPropertyValue(
								BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME))
						.booleanValue();
			}
			if (productRepoItem
					.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) != null) {
				productWebOffered = ((Boolean) productRepoItem
						.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME))
						.booleanValue();
			}
			if (skuWebOffered && productWebOffered) {
				String seoUrl = (String) productRepoItem
						.getPropertyValue(BBBCatalogConstants.SEO_URL_PROD_RELATION_PROPERTY_NAME);
				if (null != seoUrl && registryItemVO != null) {
					registryItemVO.setProductURL(seoUrl
							+ BBBInternationalShippingConstants.SKU_ID_IN_URL
							+ skuId);
				}
			}
		}
		return skuVO;
	}

}
