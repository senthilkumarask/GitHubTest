package com.bbb.commerce.giftregistry.droplet;


import java.util.ArrayList;
import java.util.List;

import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.giftregistry.tool.GiftRegistryTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.kickstarters.KickStarterVO;
import com.bbb.kickstarters.droplet.KickStarterDetailsDroplet;
import com.bbb.simplifyRegistry.RegistryInputVO;
import com.bbb.simplifyRegistry.RegistryInputsByTypeVO;
import com.bbb.simplifyRegistry.manager.SimplifyRegistryManager;
import atg.multisite.SiteContext;
import atg.multisite.SiteContextManager;

/**
 * This class retrives registryInputs by registry type
 * 
 * @author dwaghmare
 * 
 */
public class RegistryStatusDroplet extends BBBDynamoServlet {
	
	public GiftRegistryTools giftRegistryTools;
	private SiteContext siteContext;
	private BBBCatalogTools catalogTools;
	
	



	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}


	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}


	public SiteContext getSiteContext() {
		return siteContext;
	}


	public void setSiteContext(SiteContext siteContext) {
		this.siteContext = siteContext;
	}


	public GiftRegistryTools getGiftRegistryTools() {
		return giftRegistryTools;
	}


	public void setGiftRegistryTools(GiftRegistryTools giftRegistryTools) {
		this.giftRegistryTools = giftRegistryTools;
	}


	/**
	 * to hold error variable
	 */
	public static final String OPARAM_ERROR = "error";
	

	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	throws javax.servlet.ServletException, java.io.IOException {
		BBBPerformanceMonitor.start(RegistryStatusDroplet.class.getName() + " : " + "service");
		
		String registryId = ((String) pRequest.getLocalParameter("registryId"));
		String siteId = getSiteContext().getSite().getId();
		
		String registryStatus=null;
		boolean regPublic= true;
		try{
		registryStatus = getGiftRegistryTools().getRegistryStatus(registryId,this.getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId).get(0));
		} catch (final BBBBusinessException e) {
			this.logError("registry status error",
					e);
		}catch (final BBBSystemException e) {
			this.logError("registry status error",
					e);
		}
			if(registryStatus==null || registryStatus.equalsIgnoreCase("Y")){
				regPublic=true;
			}else{
				regPublic=false;
			}
				pRequest.setParameter("regPublic",regPublic);
				pRequest.serviceParameter(BBBCmsConstants.OUTPUT, pRequest,
						pResponse);

		BBBPerformanceMonitor.end(RegistryStatusDroplet.class.getName() + " : " + "service");
	}
}
