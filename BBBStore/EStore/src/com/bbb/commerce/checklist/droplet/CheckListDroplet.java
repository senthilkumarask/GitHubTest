package com.bbb.commerce.checklist.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.checklist.manager.CheckListManager;
import com.bbb.commerce.checklist.tools.CheckListTools;
import com.bbb.commerce.checklist.vo.CheckListProgressVO;
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
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * To populate addresses entered for shipping, profile addresses
 * 
 * @author ssi191
 * @description The droplet retrieves the flag whether checklist is enabled or not and populates the value of static /dynamic checklist based on the VO.
 * @version 1.0
 */

public class CheckListDroplet extends BBBDynamoServlet {


	private CheckListTools checkListTools;
	private CheckListManager checkListManager;
	private BBBSessionBean sessionBean;
	private BBBLocalCacheContainer cacheContainer;
	private BBBCatalogTools catalogTools;
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
		String regTypeCode = null;
		logDebug("Inside service() method of CheckListDroplet:");
		BBBPerformanceMonitor.start(BBBPerformanceConstants.CHECKLIST_DROPLET, BBBCoreConstants.DROPLET_CHECKLIST);
		String registryType = pRequest.getParameter(BBBCoreConstants.REGISTRY_TYPE);
		String guideType = pRequest.getParameter(BBBCoreConstants.GUIDE_TYPE);
		String manualC3Check = pRequest.getParameter(BBBCoreConstants.MANUAL_C3_CHECK);
		String staticChecklist= pRequest.getParameter(BBBCoreConstants.STATIC_CHECKLIST);
		final String registryId = pRequest.getParameter(BBBCoreConstants.REGISTRY_ID);
		String cat1Id=pRequest.getParameter(BBBCoreConstants.CAT1_ID);
		String cat2Id=pRequest.getParameter(BBBCoreConstants.CAT2_ID);
		String cat3Id=pRequest.getParameter(BBBCoreConstants.CAT3_ID);
		logDebug("Request parameters are: cat1Id: "+ cat1Id + " cat2Id: " + cat2Id + " cat3Id: " + cat3Id + " registryType: " + registryType + " guideType: " + guideType + " staticChecklist: " + staticChecklist);
		String fromRegistryActivity=pRequest.getParameter(BBBCoreConstants.FROM_REGISTRY_ACTIVITY);
		boolean isCheckListFlagDisabled = true;
		String siteFlag = null;
		
		try{
			RepositoryItem[] checkListItems = null;
			String siteIdValue=getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,SiteContextManager.getCurrentSiteId()).get(0);
			String siteId = SiteContextManager.getCurrentSiteId(); 	
			if(this.getCheckListTools().showCheckList()){
				if (!BBBUtility.isEmpty(registryType) && BBBUtility.isEmpty(guideType)) {
					if(!(registryType.length()<=3)){
						 regTypeCode = this.getGiftRegistryTools()
								.getRegistryTypeCode(registryType,siteId);
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
				else if(!BBBUtility.isEmpty(guideType)){
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
			if(BBBCoreConstants.TRUE.equals(pRequest.getParameter(BBBCoreConstants.GET_CHECKLIST_FLAG)))
			{
				pRequest.serviceLocalParameter(BBBCoreConstants.OPARAM, pRequest, pResponse);
				return;
			}
			
			 if(!isCheckListFlagDisabled && BBBCoreConstants.TRUE.equalsIgnoreCase(staticChecklist))
			{
				// For Checking if the flag is not disabled and show staic checklist.
				logDebug("Show Static Checklist CheckListDroplet:");
				
				if (!BBBUtility.isEmpty(guideType)) {
					checkListVO = getCheckListManager().getRegistryCheckList(
							guideType, guideType, BBBCoreConstants.GUIDE);
				} else {
					checkListVO = getCheckListManager().getRegistryCheckList(
							registryId, registryType,BBBCoreConstants.STATIC);
				}
				pRequest.setParameter(BBBCoreConstants.STATIC_CHECKLIST_VO,checkListVO);
			}else if( !isCheckListFlagDisabled && getSessionBean().getChecklistVO() !=null && !BBBUtility.isEmpty(cat2Id) && !BBBUtility.isEmpty(cat1Id)){
				// For manual selection of c3 clicks
				logDebug("Show Manual check of category c3 CheckListDroplet:");
				String totalC3QuantityAdded=pRequest.getParameter(BBBCoreConstants.TOTAL_C3_QUANTITY_ADDED);
				String totalC3QuanitySuggested=pRequest.getParameter(BBBCoreConstants.TOTAL_C3_SUGGESTED);
				String noOfC1=pRequest.getParameter(BBBCoreConstants.NO_OF_C1);
				String averagePercentage=pRequest.getParameter(BBBCoreConstants.AVERAGE_PERCENTAGE);
				String isChecklistSelected=pRequest.getParameter(BBBCoreConstants.IS_CHECKLIST_SELECTED);
				String c3AddedQuantity=pRequest.getParameter(BBBCoreConstants.C3_ADDED_QUANTITY);
				CheckListProgressVO checkListProgressVO=null;
				if(null != isChecklistSelected && null != registryId && null !=registryType && null !=totalC3QuantityAdded
						&& null !=totalC3QuanitySuggested && null !=averagePercentage && null !=noOfC1 && null !=c3AddedQuantity){
			    if(BBBUtility.isEmpty(cat3Id)){
			    	cat3Id=null;
			    }
				checkListProgressVO=getCheckListManager().getUpdatedProgressOnManualCheck(Boolean.parseBoolean(isChecklistSelected), cat1Id,
						cat2Id, cat3Id, registryId, registryType, Integer.parseInt(totalC3QuantityAdded),
						Integer.parseInt(totalC3QuanitySuggested),Double.parseDouble(averagePercentage),Integer.parseInt(noOfC1),0);
				}
				pRequest.setParameter(BBBCoreConstants.CHECKLIST_PROGRESS_VO,checkListProgressVO);
				checkListVO=getCheckListTools().populateCheckListVOAfterManualCheck(checkListProgressVO,getSessionBean().getChecklistVO(),Boolean.parseBoolean(isChecklistSelected));
				getSessionBean().setChecklistVO(checkListVO);
			}

			else if(BBBCoreConstants.FALSE.equalsIgnoreCase(staticChecklist)  && ((getSessionBean().getChecklistVO() == null ||( null !=getSessionBean().getChecklistVO() && getSessionBean().getChecklistVO().getRegistryId() !=registryId)) || BBBCoreConstants.TRUE.equalsIgnoreCase(fromRegistryActivity))){
				// for updating dynamic checklist VO in session.
				logDebug("Show Dynamic Checklist CheckListDroplet:");
				checkListVO = getCheckListManager().getRegistryCheckList(
						registryId, registryType,BBBCoreConstants.DYNAMIC);
				getSessionBean().setChecklistVO(checkListVO);				
			}
			else if(getSessionBean().getChecklistVO() == null && null != manualC3Check && manualC3Check.equalsIgnoreCase(BBBCoreConstants.TRUE) && !isCheckListFlagDisabled && !BBBUtility.isEmpty(cat2Id) && !BBBUtility.isEmpty(cat1Id)){
				checkListVO = getCheckListManager().getRegistryCheckList(
						registryId, registryType,BBBCoreConstants.DYNAMIC);
				getSessionBean().setChecklistVO(checkListVO);
				String totalC3QuantityAdded=pRequest.getParameter(BBBCoreConstants.TOTAL_C3_QUANTITY_ADDED);
				String totalC3QuanitySuggested=pRequest.getParameter(BBBCoreConstants.TOTAL_C3_SUGGESTED);
				String noOfC1=pRequest.getParameter(BBBCoreConstants.NO_OF_C1);
				String averagePercentage=pRequest.getParameter(BBBCoreConstants.AVERAGE_PERCENTAGE);
				String isChecklistSelected=pRequest.getParameter(BBBCoreConstants.IS_CHECKLIST_SELECTED);
				String c3AddedQuantity=pRequest.getParameter(BBBCoreConstants.C3_ADDED_QUANTITY);
				CheckListProgressVO checkListProgressVO=null;
				if(null != isChecklistSelected && null != registryId && null !=registryType && null !=totalC3QuantityAdded
						&& null !=totalC3QuanitySuggested && null !=averagePercentage && null !=noOfC1 && null !=c3AddedQuantity){
			    if(BBBUtility.isEmpty(cat3Id)){
			    	cat3Id=null;
			    }
				checkListProgressVO=getCheckListManager().getUpdatedProgressOnManualCheck(Boolean.parseBoolean(isChecklistSelected), cat1Id,
						cat2Id, cat3Id, registryId, registryType, Integer.parseInt(totalC3QuantityAdded),
						Integer.parseInt(totalC3QuanitySuggested),Double.parseDouble(averagePercentage),Integer.parseInt(noOfC1),0);
				}
				pRequest.setParameter(BBBCoreConstants.CHECKLIST_PROGRESS_VO,checkListProgressVO);
				checkListVO=getCheckListTools().populateCheckListVOAfterManualCheck(checkListProgressVO,getSessionBean().getChecklistVO(),Boolean.parseBoolean(isChecklistSelected));
				getSessionBean().setChecklistVO(checkListVO);
			}
			 pRequest.serviceLocalParameter(BBBCoreConstants.OPARAM, pRequest, pResponse);

		} catch (BBBSystemException e) {
			logError("BBBSystem Exception ChecklistDroplet" + e.getMessage());
			logDebug("BBBSystem Exception ChecklistDroplet Stack Trace",e);
			pRequest.serviceLocalParameter(BBBCoreConstants.OPARAM_ERROR, pRequest, pResponse);
		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "Business Exception from service of CheckListDroplet ",BBBCoreErrorConstants.CHECKLIST_ERROR_10132) + e.getMessage());
			logDebug("usiness Exception from service of CheckListDroplet Stack Trace",e);
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
	public BBBSessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(BBBSessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}


	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}


	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
	
}
