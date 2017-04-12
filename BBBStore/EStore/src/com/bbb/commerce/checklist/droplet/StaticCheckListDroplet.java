package com.bbb.commerce.checklist.droplet;

import java.io.IOException;

import javax.servlet.ServletException;




import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.checklist.manager.CheckListManager;
import com.bbb.commerce.checklist.tools.CheckListTools;
import com.bbb.commerce.checklist.vo.CheckListVO;
import com.bbb.commerce.giftregistry.tool.GiftRegistryTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.BBBLocalCacheContainer;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

/**
 * To populate addresses entered for shipping, profile addresses
 * 
 * @author ssi191
 * @description The droplet retrieves the flag whether checklist is enabled or not and populates the value of static /dynamic checklist based on the VO.
 * @version 1.0
 */

public class StaticCheckListDroplet extends BBBDynamoServlet {


	private CheckListTools checkListTools;
	private CheckListManager checkListManager;
	private BBBCatalogTools mCatalogTools;
	private BBBLocalCacheContainer cacheContainer;

	public BBBLocalCacheContainer getCacheContainer() {
		return cacheContainer;
	}


	public void setCacheContainer(BBBLocalCacheContainer cacheContainer) {
		this.cacheContainer = cacheContainer;
	}


	public GiftRegistryTools getGiftRegistryTools() {
		return giftRegistryTools;
	}


	public void setGiftRegistryTools(GiftRegistryTools giftRegistryTools) {
		this.giftRegistryTools = giftRegistryTools;
	}

	private GiftRegistryTools giftRegistryTools;

	/**
	 * The method retrieves the flag whether checklist is enabled or not.
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		CheckListVO checkListVO = null;
		String siteId = SiteContextManager.getCurrentSiteId();
		logDebug("Inside service() method of StaticCheckListDroplet:");
		BBBPerformanceMonitor.start(BBBPerformanceConstants.CHECKLIST_DROPLET, BBBCoreConstants.DROPLET_CHECKLIST);
		String checkListType = pRequest.getParameter(BBBCoreConstants.CHECKLIST_TYPE);
		String registryType = null;
		String guideType = null;
		String regTypeCode = null;
		String siteFlag = null;
		
		logDebug("Request parameters are: checkListType: " + checkListType);
		boolean isCheckListFlagDisabled = true;
		String type = null;
		try {
			type = this.getCheckListTools().checkRegistryTypeFromSubTypeCode(checkListType);
		} catch (BBBSystemException e) {
			logError("BBBSystem Exception StaticCheckListDroplet" + e.getMessage());
			logDebug("BBBSystem Exception StaticCheckListDroplet Stack Trace",e);
			pRequest.serviceLocalParameter(BBBCoreConstants.OPARAM_ERROR, pRequest, pResponse);
		} catch (BBBBusinessException e) {
			logError("BBBSystem Exception StaticCheckListDroplet" + e.getMessage());
			logDebug("BBBSystem Exception StaticCheckListDroplet Stack Trace",e);
			pRequest.serviceLocalParameter(BBBCoreConstants.OPARAM_ERROR, pRequest, pResponse);
		}
		if(!BBBUtility.isEmpty(type)){
			if(type.equalsIgnoreCase(BBBCoreConstants.REGISTRY)){
				registryType = checkListType;
			}
			else if(type.equalsIgnoreCase(BBBCoreConstants.NONREGISTRY)){
				guideType = checkListType;
			}
		}
		try{
			RepositoryItem[] checkListItems = null;
			String siteIdValue=getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,SiteContextManager.getCurrentSiteId()).get(0);
			if(this.getCheckListTools().showCheckList()){
				if (!BBBUtility.isEmpty(registryType) && BBBUtility.isEmpty(guideType)) {
					if(!(registryType.length()<=3)){
						 regTypeCode = this.getGiftRegistryTools()
								.getRegistryTypeCode(registryType,siteId);
						}
					else{
						regTypeCode = registryType;
					}
					if(null != this.getCacheContainer() && null != this.getCacheContainer().get(regTypeCode)){
						CheckListVO	checkVO = (CheckListVO)this.getCacheContainer().get(regTypeCode);
						//changes for ILD-293 | to set ischecklistDisabled flag on the basis of site
						if (null != checkVO.getSiteFlag() && !checkVO.getSiteFlag().contains(siteIdValue)) {
							isCheckListFlagDisabled =true;
						} else {
							isCheckListFlagDisabled = checkVO.isCheckListDisabled();
						}
					}
					else{
						checkListItems = getCheckListTools().fetchCheckListRepositoryItem(registryType);
						if(checkListItems != null){
						isCheckListFlagDisabled = (boolean) checkListItems[0]
								.getPropertyValue(BBBCoreConstants.IS_DISABLED);
						//changes for ILD-293 | to set ischecklistDisabled flag on the basis of site
						siteFlag = (String)checkListItems[0]
								.getPropertyValue(BBBCoreConstants.SITE_FLAG);
					}
					if (null != siteFlag && !siteFlag.contains(siteIdValue)) {
						isCheckListFlagDisabled =true;
					}
					}
				}
				else{
					if(null != this.getCacheContainer() && null != this.getCacheContainer().get(guideType)){
						CheckListVO	checkVO = (CheckListVO)this.getCacheContainer().get(guideType);
						//changes for ILD-293 | to set ischecklistDisabled flag on the basis of site
						if (null != checkVO.getSiteFlag() && !checkVO.getSiteFlag().contains(siteIdValue)) {
							isCheckListFlagDisabled =true;
						} else {
							isCheckListFlagDisabled = checkVO.isCheckListDisabled();
						}
					}
					else{
						checkListItems = getCheckListTools().fetchCheckListRepositoryItem(guideType);
						if(null != checkListItems){
						isCheckListFlagDisabled = (boolean) checkListItems[0]
								.getPropertyValue(BBBCoreConstants.IS_DISABLED);
						//changes for ILD-293 | to set ischecklistDisabled flag on the basis of site
						siteFlag = (String)checkListItems[0]
								.getPropertyValue(BBBCoreConstants.SITE_FLAG);
					}
					if (null != siteFlag && !siteFlag.contains(siteIdValue)) {
						isCheckListFlagDisabled =true;
					}
					}
				}
				
			}
			// For Checking if the flag is disabled.
			pRequest.setParameter(BBBCoreConstants.IS_DISABLED,isCheckListFlagDisabled);
			
			 if(!isCheckListFlagDisabled)
			{
				// For Checking if the flag is not disabled and show staic checklist.
				logDebug("Show Static Checklist StaticCheckListDroplet:");
				
				if (!BBBUtility.isEmpty(guideType)) {
					checkListVO = getCheckListManager().getRegistryCheckList(
							guideType, guideType, BBBCoreConstants.GUIDE);
				} else {
					checkListVO = getCheckListManager().getRegistryCheckList(
							null, registryType,BBBCoreConstants.STATIC);
				}
				pRequest.setParameter(BBBCoreConstants.STATIC_CHECKLIST_VO,checkListVO);
				if(!BBBUtility.isEmpty(registryType) && registryType.length()<=3){
					pRequest.setParameter(BBBCoreConstants.REGISTRY_TYPE,this.getCatalogTools().getRegistryTypeName(registryType,siteId));
				}
				else if(!BBBUtility.isEmpty(registryType) && registryType.length() >3){
					pRequest.setParameter(BBBCoreConstants.REGISTRY_TYPE,registryType);
				}
				else if(!BBBUtility.isEmpty(guideType)){
					pRequest.setParameter(BBBCoreConstants.REGISTRY_TYPE,"");
					pRequest.setParameter(BBBCoreConstants.GUIDE_TYPE,guideType);
				}
			}
			 pRequest.serviceLocalParameter(BBBCoreConstants.OPARAM, pRequest, pResponse);

		} catch (BBBSystemException e) {
			logError("BBBSystem Exception StaticCheckListDroplet" + e.getMessage());
			logDebug("BBBSystem Exception StaticCheckListDroplet Stack Trace",e);
			pRequest.serviceLocalParameter(BBBCoreConstants.OPARAM_ERROR, pRequest, pResponse);
		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "Business Exception from service of StaticCheckListDroplet ",BBBCoreErrorConstants.CHECKLIST_ERROR_10132) + e.getMessage());
			logDebug("usiness Exception from service of StaticCheckListDroplet Stack Trace",e);
			pRequest.serviceLocalParameter(BBBCoreConstants.OPARAM_ERROR, pRequest, pResponse);
		}
		BBBPerformanceMonitor.end(BBBPerformanceConstants.CHECKLIST_DROPLET, BBBCoreConstants.DROPLET_CHECKLIST);
	}


	public CheckListTools getCheckListTools() {
		return checkListTools;
	}

	public void setCheckListTools(CheckListTools checkListTools) {
		this.checkListTools = checkListTools;
	}

	public CheckListManager getCheckListManager() {
		return checkListManager;
	}

	public void setCheckListManager(CheckListManager checkListManager) {
		this.checkListManager = checkListManager;
	}


	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}


	public void setCatalogTools(BBBCatalogTools mCatalogTools) {
		this.mCatalogTools = mCatalogTools;
	}
	
}
