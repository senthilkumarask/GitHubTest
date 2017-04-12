package com.bbb.rest.giftregistry;

import java.io.IOException;
import java.util.List;


import javax.servlet.ServletException;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.giftregistry.droplet.BridalToolkitLinkDroplet;
import com.bbb.commerce.giftregistry.vo.BridalRegistryVO;
import com.bbb.common.BBBGenericService;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

public class BridalToolkitRegistriesAPI extends BBBGenericService {
	
	private static final String SITE_ID = "siteId";
	private BridalToolkitLinkDroplet bridalToolkitLinkDroplet;

	public BridalToolkitLinkDroplet getBridalToolkitLinkDroplet() {
		return bridalToolkitLinkDroplet;
	}

	public void setBridalToolkitLinkDroplet(
			BridalToolkitLinkDroplet bridalToolkitLinkDroplet) {
		this.bridalToolkitLinkDroplet = bridalToolkitLinkDroplet;
	}
	


	public List<BridalRegistryVO> getBridalToolkitRegistries()
			throws BBBBusinessException, BBBSystemException {
		

		String siteId = SiteContextManager.getCurrentSiteId();
		List<BridalRegistryVO> bridalRegistryVOList = null;
	
		logDebug(" BridalToolkitRegistriesAPI getBridalToolkitRegistries() Started");
		
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		DynamoHttpServletResponse pResponse = ServletUtil.getCurrentResponse();

		pRequest.setParameter(SITE_ID, siteId);	

		try{
			
			getBridalToolkitLinkDroplet().service(pRequest, pResponse); 
			bridalRegistryVOList = (List<BridalRegistryVO>) pRequest.getObjectParameter("bridalRegistryVOList");
			
		} catch (IOException ioException) {
			logError(ioException);
			throw new BBBSystemException(BBBCatalogErrorCodes.BRIDAL_TOOLKIT_REGISTRY_IO_EXCEPTION, "IO Exception raised while getting Bridaltoolkit registries.");
		} catch (ServletException servletException) {
			logError(servletException);
		    throw new BBBSystemException(BBBCatalogErrorCodes.BRIDAL_TOOLKIT_REGISTRY_SYSTEM_EXCEPTION, "Servlet Exception raised while getting Bridaltoolkit registries.");
		}
		
		logDebug(" BridalToolkitRegistriesAPI getBridalToolkitRegistries() - ends");
	
		return bridalRegistryVOList; 
	}

	
}
