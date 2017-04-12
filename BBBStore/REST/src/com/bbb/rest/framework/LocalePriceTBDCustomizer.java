package com.bbb.rest.framework;


import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.rest.filtering.RestPropertyCustomizer;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

public class LocalePriceTBDCustomizer extends BBBGenericService implements RestPropertyCustomizer{
    
	/** The catalog tools. */
	private BBBCatalogTools catalogTools;
	/**
	 * Gets the catalog tools.
	 *
	 * @return the catalog tools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.catalogTools;
	}

	/**
	 * Sets the catalog tools.
	 *
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
	
	public Object getPropertyValue(String pPropertyName, Object pResource) {
	
		logDebug("Entering LocalePriceCustomizer for Property value" + pPropertyName);
		Double itemPrice = 0.00;
		Object value = null;
		String priceValue = BBBCoreConstants.BLANK;
		try {
			String cutomizationCode = null;
			SKUDetailVO skuVO = (SKUDetailVO)DynamicBeans.getPropertyValue(pResource, "skuVO");
			if(skuVO!=null){
				cutomizationCode = skuVO.getCustomizableCodes();
			}
		    if(pPropertyName.contains("salePriceTBD")){
		    	value = DynamicBeans.getPropertyValue(pResource, "salePrice");
		    }else if(pPropertyName.contains("listPriceTBD")){
		    	value = DynamicBeans.getPropertyValue(pResource, "listPrice");
		    }
		    if(value != null && value instanceof Double){
				itemPrice = (Double) value;
				if(itemPrice != 0.0 && itemPrice <= 0.01 && !BBBUtility.isEmpty(getCustomizableCodes()) && !BBBUtility.isEmpty(cutomizationCode) && getCustomizableCodes().contains(cutomizationCode)) {
					priceValue = "lbl_price_is_tbd_customize";
				}else if(itemPrice != 0.0 && itemPrice <= 0.01 ){
					 priceValue = "lbl_price_is_tbd";
				 }else{
					 priceValue = "";
				 }
			}
		} catch (PropertyNotFoundException e) {
			logError("LocalePriceCustomizer::could not find the Property Name :" + pPropertyName + "::returning Empty Value with Exception Log trace :"+e);
		}

		return priceValue;

	}

	@Override
	public void setPropertyValue(String arg0, Object arg1, Object arg2) {
		//do nothing
	}
	
	public String getCustomizableCodes() {
		String customizableCodes = null;
		try {
			if (this.getCatalogTools() == null) {
				BBBCatalogTools catalogTools = (BBBCatalogTools) ServletUtil.getCurrentRequest().resolveName("com/bbb/commerce/catalog/BBBCatalogTools");
				this.setCatalogTools(catalogTools);
			}
			customizableCodes = getCatalogTools().getConfigValueByconfigType(BBBCoreConstants.EXIM_KEYS).get(BBBCoreConstants.CUSTOMIZATBLE_CTA_CODES);
		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(null, "BBBSystemException occurs in LocalePriceTBDCustomizer.getCustomizableCodes "
					+ ":: customizableCodes = " + customizableCodes), e);
		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(null, "BBBSystemException occurs in LocalePriceTBDCustomizer.getCustomizableCodes "
					+ ":: customizableCodes = " + customizableCodes), e);
		}
		return customizableCodes;
	}
	
}
