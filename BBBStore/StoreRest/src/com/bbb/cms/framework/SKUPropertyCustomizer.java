package com.bbb.cms.framework;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.repository.RepositoryItem;
import atg.rest.filtering.RestPropertyCustomizer;
import atg.servlet.ServletUtil;

import com.bbb.common.BBBGenericService;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.store.catalog.BBBGSCatalogToolsImpl;
import com.bbb.store.catalog.vo.FilteredStyleSkuDetailVO;

/**
 *
 * 
 */
public class SKUPropertyCustomizer extends BBBGenericService implements
		RestPropertyCustomizer {

	public Object getPropertyValue(String pPropertyName, Object pResource) {

		logDebug("Entering SKUPropertyCustomizer.getPropertyValue");
		
		try {
			RepositoryItem skuRepositoryItem = (RepositoryItem) DynamicBeans.getSubPropertyValue(pResource, pPropertyName);
			BBBGSCatalogToolsImpl bbbgsCatalogToolsImpl = (BBBGSCatalogToolsImpl)ServletUtil.getCurrentRequest().resolveName("/com/bbb/store/catalog/BBBGSCatalogToolsImpl");
			if(skuRepositoryItem != null && bbbgsCatalogToolsImpl != null){
				String pSkuId = skuRepositoryItem.getRepositoryId();
				logDebug("In SkuPropertyCustomizer. Sku Id:"+pSkuId);

				FilteredStyleSkuDetailVO filteredSkuDetails = new FilteredStyleSkuDetailVO();
				//filteredSkuDetails =(FilteredStyleSkuDetailVO) BBBGSFilteredResponse.getFilteredStyleSkuDetails(bbbgsCatalogToolsImpl.getGSSkuDetails(pSkuId));
				filteredSkuDetails = bbbgsCatalogToolsImpl.getGSMinimalSKUDetails(pSkuId);
				return filteredSkuDetails;
			}		
		} catch (PropertyNotFoundException e) {
			logError("Property not found",e);
		} catch (BBBSystemException e) {
			logError(e);
		} catch (BBBBusinessException e) {
			logError(e);
		}
		return null;
		
	}

	@Override
	public void setPropertyValue(String arg0, Object arg1, Object arg2) {
		throw new UnsupportedOperationException("Not implemented");
	}
}
