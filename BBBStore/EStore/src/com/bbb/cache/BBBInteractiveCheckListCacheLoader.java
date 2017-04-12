package com.bbb.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import atg.nucleus.ServiceException;
import atg.repository.Repository;
import atg.repository.RepositoryItem;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.checklist.tools.CheckListTools;
import com.bbb.commerce.checklist.vo.CheckListVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.BBBLocalCacheContainer;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBUtility;

/**
 * 
 * @author ssi191 BBBInteractiveCheckListCacheLoader loads the checklist data for 
 *        each registry type in local cache container.
 * 
 */

public class BBBInteractiveCheckListCacheLoader extends BBBGenericService {

	private Repository registryCheckListRepository;
	private BBBLocalCacheContainer cacheContainer;
	private String queryCheckListForRegistryType;
	private CheckListTools checkListTools;
	private BBBCatalogTools catalogTools;

	@Override
	public void doStartService() throws ServiceException {
		super.doStartService();
		List<String> regTypeList = new ArrayList<String>();
		String registryTypeConfigKeys = null;
		registryTypeConfigKeys = this.getCatalogTools().getConfigKeyValue(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.CHECKLIST_REGISTRY_TYPES, BBBCoreConstants.BLANK);
		if (BBBUtility.isNotEmpty(registryTypeConfigKeys)) {
			regTypeList = Arrays.asList(registryTypeConfigKeys.split(BBBCoreConstants.COMMA));
		try {
			for(String regType:regTypeList){
				this.populateCheckListVO(regType);
			}
		}
		 catch (BBBSystemException e) {
			logError("BBBInteractiveCheckListCacheLoader | doStartService | BBBSystemException", e);
		} catch (BBBBusinessException e) {
			logError("BBBBusinessException | doStartService | BBBBusinessException", e);
		}
	}
	}

	/**
	 * @return the registryCheckListRepository
	 */
	public Repository getRegistryCheckListRepository() {
		return registryCheckListRepository;
	}

	/**
	 * @param registryCheckListRepository the registryCheckListRepository to set
	 */
	public void setRegistryCheckListRepository(
			Repository registryCheckListRepository) {
		this.registryCheckListRepository = registryCheckListRepository;
	}


	/**
	 * @return the queryCheckListForRegistryType
	 */
	public String getQueryCheckListForRegistryType() {
		return queryCheckListForRegistryType;
	}

	/**
	 * @param queryCheckListForRegistryType the queryCheckListForRegistryType to set
	 */
	public void setQueryCheckListForRegistryType(
			String queryCheckListForRegistryType) {
		this.queryCheckListForRegistryType = queryCheckListForRegistryType;
	}

	/**
	 * The method populates the CheckListVO for a given registryType and stores it in local cache.
	 * @param registryType
	 * @return
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException
	 */
	public void populateCheckListVO(final String registryType) throws BBBSystemException, BBBBusinessException{
		BBBPerformanceMonitor.start(
				"BBBInteractiveCheckListCacheLoader", "populateCheckListVO");
		this.logDebug("BBBInteractiveCheckListCacheLoader.populateCheckListVO() method start");
		this.logDebug("Caching for registryType: " + registryType);
		long startTime = System.currentTimeMillis();
		RepositoryItem[] checkListItems = this.getCheckListTools().fetchCheckListRepositoryItem(registryType);
		CheckListVO checkListVO = new CheckListVO();
		if(null != checkListItems && checkListItems.length != 0){
			checkListVO = this.getCheckListTools().populateStaticCheckListVO(registryType, false);
					
		}
		else{
			checkListVO.setCheckListDisabled(true);
			this.logDebug("BBBInteractiveCheckListCacheLoader:There is no repository item to cache for registryType: " + registryType);
		}
		this.logDebug("BBBInteractiveCheckListCacheLoader.populateCheckListVO() method end");
		long endTime = System.currentTimeMillis();
		this.logDebug("Total time taken in populating CheckListVO() : " + (endTime-startTime));			
		this.getCacheContainer().put(registryType, checkListVO);

		BBBPerformanceMonitor.end(
				"BBBInteractiveCheckListCacheLoader", "populateCheckListVO");
	}
		

	/**
	 * @return the checkListTools
	 */
	public CheckListTools getCheckListTools() {
		return checkListTools;
	}

	/**
	 * @param checkListTools the checkListTools to set
	 */
	public void setCheckListTools(CheckListTools checkListTools) {
		this.checkListTools = checkListTools;
	}

	public BBBLocalCacheContainer getCacheContainer() {
		return cacheContainer;
	}

	public void setCacheContainer(BBBLocalCacheContainer cacheContainer) {
		this.cacheContainer = cacheContainer;
	}

	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}


}
