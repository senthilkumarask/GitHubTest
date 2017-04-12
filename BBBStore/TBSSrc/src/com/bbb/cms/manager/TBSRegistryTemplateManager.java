package com.bbb.cms.manager;

import com.bbb.cms.RegistryTemplateVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.TBSConstants;

public class TBSRegistryTemplateManager extends RegistryTemplateManager {
	
	/**
	 * When the site is TBS_ , we are passing general site Ids to fetch the content that are
	 * configured based on siteId
	 */
	@Override
	public RegistryTemplateVO getRegistryTemplateData(String pPageName,String pSiteId){
		
		vlogDebug("TBSRegistryTemplateManager.getRegistryTemplateData() Method Start");
		RegistryTemplateVO registryTemplateVO = super.getRegistryTemplateData(pPageName, pSiteId);
		
		if (registryTemplateVO == null) {
			if(pSiteId.equals(TBSConstants.SITE_TBS_BAB_US)){
			 	
			 	 pSiteId = BBBCoreConstants.SITE_BAB_US;
				
			}else if(pSiteId.equals(TBSConstants.SITE_TBS_BAB_CA)){
				
				 pSiteId = BBBCoreConstants.SITE_BAB_CA;
				 
			}else if(pSiteId.equals(TBSConstants.SITE_TBS_BBB)){
				
				 pSiteId = BBBCoreConstants.SITE_BBB;
			}
			registryTemplateVO = super.getRegistryTemplateData(pPageName, pSiteId);
		}
		 
		 vlogDebug("the siteid after modification" +pSiteId);
		 
		 vlogDebug("TBSRegistryTemplateManager.getRegistryTemplateData() Method End");
		return registryTemplateVO;
	}

}
