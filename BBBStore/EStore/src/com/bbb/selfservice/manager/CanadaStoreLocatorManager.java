/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: syadav
 *
 * Created on: 19-December-2011
 * --------------------------------------------------------------------------------
 */
package com.bbb.selfservice.manager;

import java.util.List;
import com.bbb.common.BBBGenericService;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.StoreVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

public class CanadaStoreLocatorManager extends BBBGenericService {

	private BBBCatalogTools mCatalogTools;
	
	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param pCatalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}
	
	public List<StoreVO> getCanadaStoreLocator()throws BBBBusinessException,BBBSystemException {

			logDebug("CanadaStoreLocatorManager.getCanadaStoreLocator() method started");
		
		return mCatalogTools.getCanadaStoreLocatorInfo(); 
		
	}
	
}
