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
import com.bbb.store.catalog.vo.MinimalProductDetailVO;


/**
 * 
 *
 */
public class ProductPropertyCustomizer  extends BBBGenericService  implements RestPropertyCustomizer{
	
	public Object getPropertyValue(String pPropertyName, Object pResource) {
		
		logDebug("Entering ProductPropertyCustomizer.getPropertyValue");
		
		try {
			RepositoryItem productRepositoryItem = (RepositoryItem) DynamicBeans.getSubPropertyValue(pResource, pPropertyName);
			BBBGSCatalogToolsImpl bbbgsCatalogToolsImpl = (BBBGSCatalogToolsImpl)ServletUtil.getCurrentRequest().resolveName("/com/bbb/store/catalog/BBBGSCatalogToolsImpl");
			if(productRepositoryItem != null && bbbgsCatalogToolsImpl != null){
				String pProductId = productRepositoryItem.getRepositoryId();
				logDebug("In ProductPropertyCustomizer. Product Id:"+pProductId);

				MinimalProductDetailVO minimalProductDetails = new MinimalProductDetailVO();
				minimalProductDetails = bbbgsCatalogToolsImpl.getGSMinimalProductDetails(pProductId);
				return minimalProductDetails;
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
