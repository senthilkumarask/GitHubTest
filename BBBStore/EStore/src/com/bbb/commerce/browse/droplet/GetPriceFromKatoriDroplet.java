//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Reproduction or use of this file without explicit 
//written consent is prohibited.
//
//Created by: Naveen Kumar
//
//Created on: 29-November-2011
//--------------------------------------------------------------------------------

package com.bbb.commerce.browse.droplet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.browse.webservice.manager.BBBEximManager;
import com.bbb.commerce.browse.manager.ProductManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.BBBConfigToolsImpl;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.rest.catalog.vo.KatoriPriceRestVO;
import com.bbb.rest.output.BBBCustomTagComponent;
import com.bbb.utils.BBBUtility;
/**
 * This class is to populate the all info of product which is the part of
 * product VO.
 * 
 */
public class GetPriceFromKatoriDroplet extends BBBDynamoServlet {

	private static final String IN_CART_FLAG = "inCartFlag";
	private static final String FREE_SHIPPING_MSG = "freeShippingMsg";
	private static final String SHIP_MSG_FLAG = "shipMsgFlag";
	private final static String REF_NUM = "refNum"; 
	private final static String OPARAM_OUTPUT="output";
	private final static String OPARAM_ERROR="error";
	private final static String EXIM_ITEM_PRICE = "eximItemPrice";
	private final static String WAS_PRICE ="wasPrice";
	private final static String PRICE_LABEL_CODE = "priceLabelCode";
	private final static String DYNAMIC_PRICE_SKU = "dynamicPriceSku";
	private final static String EXIM_PERSONALISED_PRICE = "eximPersonalizedPrice";
	private BBBEximManager eximPricingManager;
	/** The bbbCustomTagComponent component instance */
	private BBBCustomTagComponent customTagComponent = null;
	private BBBCatalogTools mCatalogTools;
	private String highThreshold;
	private ProductManager productManager;
	/**
	 * @return the mCatalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}
	
	/**
	 * @param mCatalogTools
	 *            the CatalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools mCatalogTools) {
		this.mCatalogTools = mCatalogTools;
	}

	/**
	 * This method get the reference id from the jsp and pass these
	 * value to manager class and get the price from Exim Pricing Service
	 * 
	 * @param DynamoHttpServletRequest
	 *            , DynamoHttpServletResponse
	 * @return void 
	 * @throws ServletException
	 *             , IOException
	 */
	@Override
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		
		logDebug("GetPriceFromKatoriDroplet.service starts");
		
		String refNumParam = pRequest.getParameter(REF_NUM);
		String skuId = pRequest.getParameter(BBBCoreConstants.SKUID);
		String siteId =SiteContextManager.getCurrentSiteId();
		SKUDetailVO  pSKUDetailVO = null;
		try {
			logDebug("Fectching sku details for skuId:"+skuId);
			if(skuId != null && !StringUtils.isEmpty(skuId)){
				pSKUDetailVO = getProductManager().getSKUDetails(siteId, skuId, false);
			}
		} catch (BBBSystemException | BBBBusinessException e) {
			logError("Error while getting SKUDetailVO in GetPriceFromKatoriDroplet", e);
		}
		//BBBH-2889 - For TBS : introduced to handle personalization response for multisku products on tbs when incart flag is true for the sku
		boolean inCartFlag = false;
		if(!BBBUtility.isEmpty(pRequest.getParameter(IN_CART_FLAG))){
			inCartFlag = Boolean.valueOf(pRequest.getParameter(IN_CART_FLAG));
		}
		logDebug("InCartFlag for SKU :"+inCartFlag);
		//end
		String productId = pRequest.getParameter(BBBCoreConstants.PRODUCTID);
		logDebug("ref num :: " + refNumParam + " skuId:"+skuId + " siteId:"+siteId);
		pRequest.setParameter(SHIP_MSG_FLAG, false);
		KatoriPriceRestVO katoriPriceVO = getEximPricingManager().getPriceByRef(refNumParam, skuId, siteId, inCartFlag, productId);
		String personlizedPrice = katoriPriceVO.getKatoriPersonlizedPrice();
		String itemPrice = katoriPriceVO.getKatoriItemPrice();
		if(!katoriPriceVO.isErrorExist() && pSKUDetailVO != null) {
			// BBBH-220 - ship message display changes
			setShipMessageFlag(pRequest, skuId, siteId, itemPrice);
			pRequest.setParameter(EXIM_ITEM_PRICE, itemPrice);

			pRequest.setParameter(EXIM_PERSONALISED_PRICE, personlizedPrice);
			pRequest.setParameter(IN_CART_FLAG, Boolean.valueOf(pSKUDetailVO.isInCartFlag()));
			logDebug("InCartFlag for SKU :"+pSKUDetailVO.isInCartFlag());
			pRequest.setParameter(DYNAMIC_PRICE_SKU, pSKUDetailVO.isDynamicPriceSKU());
			logDebug("Is Dynamic SKU :"+pSKUDetailVO.isDynamicPriceSKU());
			pRequest.setParameter(BBBInternationalShippingConstants.SHOPPERCURRENCY, katoriPriceVO.getCurrencySymbol());
		
				try {
					double salePrice = getCatalogTools().getSalePrice(null,
							skuId).doubleValue();
					if (salePrice > 0) {
						double listPrice = getCatalogTools().getListPrice(null,
								skuId).doubleValue();
						if (!pSKUDetailVO.getPersonalizationType().equalsIgnoreCase(
								BBBCoreConstants.PERSONALIZATION_CODE_PY)) {
							pRequest.setParameter(WAS_PRICE, listPrice);
							pRequest.setParameter(PRICE_LABEL_CODE, pSKUDetailVO.getPricingLabelCode());	
						}
						else {
							if((pSKUDetailVO.getPricingLabelCode()!=null && pSKUDetailVO.getPricingLabelCode().equalsIgnoreCase(BBBCoreConstants.ORIG))){
								pRequest.setParameter(PRICE_LABEL_CODE, "");
							}
						}
						
						logDebug("SalePrice: " + salePrice + " ListPrice"
								+ listPrice);
					}
				} catch (BBBSystemException e) {
					logError(
							"Error while getting price in GetPriceFromKatoriDroplet",
							e);
				}
						
			pRequest.serviceParameter(OPARAM_OUTPUT, pRequest,pResponse);
		} else {
			pRequest.setParameter("errorMsg", katoriPriceVO.getErrorMsg());
			pRequest.serviceParameter(OPARAM_ERROR, pRequest,pResponse);
		}
		
		logDebug("GetPriceFromKatoriDroplet.service ends");
	}

	/**
	 * Sets the ship message flag.
	 *
	 * @param pRequest the request
	 * @param skuId the sku id
	 * @param siteId the site id
	 * @param personlizedPrice the personlized price
	 */
	private void setShipMessageFlag(final DynamoHttpServletRequest pRequest,
			String skuId, String siteId, String itemPrice) {
		String shipMsgDisplayFlag = BBBCoreConstants.FALSE;
		String higherShippingThreshhold =null;
		try {
			shipMsgDisplayFlag = getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS , BBBCoreConstants.SHIP_MSG_DISPLAY_FLAG).get(0);
			if(Boolean.parseBoolean(shipMsgDisplayFlag) && !getCatalogTools().isSkuLtl(siteId, skuId) && 
					!((BBBSessionBean) ServletUtil.getCurrentRequest().resolveName(BBBGiftRegistryConstants.SESSION_BEAN)).isInternationalShippingContext()){
				double higherShipThreshhold = 0.00;
				if(BBBUtility.isEmpty(getHighThreshold())) {
					higherShippingThreshhold =  ((BBBConfigToolsImpl) getCatalogTools()).getLblTxtTemplateManager().getPageLabel(BBBCoreConstants.LBL_HIGHER_FREE_SHIPPING_THRESHOLD, 
						null, null,SiteContextManager.getCurrentSiteId());
					setHighThreshold(higherShippingThreshhold);
				} else {
					higherShippingThreshhold=getHighThreshold()+BBBCoreConstants.BLANK;
				}
				
				if(!StringUtils.isBlank(higherShippingThreshhold)){
					String trimedHigherShippingThreshold = higherShippingThreshhold.replaceAll("[^0-9^.]", BBBCoreConstants.BLANK).trim();
					if(!trimedHigherShippingThreshold.equalsIgnoreCase(BBBCoreConstants.BLANK)){		
						higherShipThreshhold = Double.parseDouble(higherShippingThreshhold);
					}
				} else{
		    		return;
		    	}
				Map<String, String> placeholderMap = new HashMap<String, String>();
				if(!StringUtils.isBlank(itemPrice) && Double.valueOf(itemPrice) > higherShipThreshhold){
					placeholderMap.put(BBBCoreConstants.CURRENCY, BBBCoreConstants.DOLLAR);
					placeholderMap.put(BBBCoreConstants.HIGHER_SHIP_THRESHHOLD, higherShippingThreshhold);
					pRequest.setParameter(SHIP_MSG_FLAG, true);
					pRequest.setParameter(FREE_SHIPPING_MSG, ((BBBConfigToolsImpl) getCatalogTools()).getLblTxtTemplateManager()
							.getPageTextArea(BBBCoreConstants.TXT_FREESHIPPING_PRODUCT, placeholderMap));
				}
			}
		} catch (BBBSystemException | BBBBusinessException e) {
			logError("Error while getting config key ShipMsgDisplayFlag value", e);
		}
	}
	
	/**
	 * @return the eximPricingManager
	 */
	public BBBEximManager getEximPricingManager() {
		return this.eximPricingManager;
	}
	/**
	 * @param eximPricingManager the eximPricingManager to set
	 */
	public void setEximPricingManager(BBBEximManager eximPricingManager) {
		this.eximPricingManager = eximPricingManager;
	}
	/**
	 * @return the bbbCustomTagComponent
	 */
	public BBBCustomTagComponent getCustomTagComponent() {
		return customTagComponent;
	}
	
	
	/**
	 * @param bbbCustomTagComponent the bbbCustomTagComponent to set
	 */
	public void setCustomTagComponent(BBBCustomTagComponent customTagComponent) {
		this.customTagComponent = customTagComponent;
	}
	/**
	 * @return the highThreshold
	 */
	public String getHighThreshold() {
		return highThreshold;
	}

	/**
	 * @param highThreshold the highThreshold to set
	 */
	public void setHighThreshold(String highThreshold) {
		this.highThreshold = highThreshold;
	}

	public ProductManager getProductManager() {
		return productManager;
	}

	public void setProductManager(ProductManager productManager) {
		this.productManager = productManager;
	}

}