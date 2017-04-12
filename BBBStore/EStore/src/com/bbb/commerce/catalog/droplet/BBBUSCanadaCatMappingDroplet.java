package com.bbb.commerce.catalog.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.catalog.vo.CategoryMappingVo;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

/**
 * @author ngup50
 * Part of Release 2.2 79-C story implementation.
 * This droplet is called from canonicalTag.jsp.
 * it takes categoryId as input param. and returns corresponding canada or Us url as output param.
 * This droplet calls getUSCanadaCategoryMapping api of BBBCatalogToolsImpl.
 */

public class BBBUSCanadaCatMappingDroplet extends BBBDynamoServlet {
	
	private BBBCatalogToolsImpl catalogTools;
	
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		logDebug("[Start : droplet BBBUSCanadaCatMappingDroplet]");
		CategoryMappingVo categoryMappingVO;
		String siteId = this.extractCurrentSiteId();
		logDebug("Current Site id in BBBUSCanadaCatMappingDroplet : " + siteId);
		if (BBBUtility.isNotEmpty((String)pRequest.getObjectParameter("categoryId"))){
			String categoryId=(String)pRequest.getObjectParameter("categoryId");
			String postURL=(String)pRequest.getObjectParameter("postURL");
			logDebug("PostUrl from canonical tags jsp in BBBUSCanadaCatMappingDroplet : "+ postURL);
			logDebug("Category id from canonical tags jsp in BBBUSCanadaCatMappingDroplet : "+ categoryId);
			try {
				categoryMappingVO=getCatalogTools().getUSCanadaCategoryMapping(categoryId);
				if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_US)){
					if(BBBUtility.isEmpty(categoryMappingVO.getCanadaUrl())){
						if(BBBUtility.isNotEmpty(categoryMappingVO.getCanadaCategoryId())){
							String returnPostURL=postURL.replace(categoryId, categoryMappingVO.getCanadaCategoryId());
							logDebug("returnPostURL in BBBUSCanadaCatMappingDroplet : " + returnPostURL);
							pRequest.setParameter("returnPostURL", returnPostURL);
						}else{
							pRequest.setParameter("returnPostURL", "");
						}
						
					}
					else{
						logDebug("returnPostURL in BBBUSCanadaCatMappingDroplet : " + categoryMappingVO.getCanadaUrl());
						pRequest.setParameter("returnPostURL", categoryMappingVO.getCanadaUrl());
					}
				}else{
					if(BBBUtility.isEmpty(categoryMappingVO.getUsUrl())){
						if(BBBUtility.isNotEmpty(categoryMappingVO.getUsCategoryId())){
							String returnPostURL=postURL.replace(categoryId, categoryMappingVO.getUsCategoryId());
							logDebug("returnPostURL in BBBUSCanadaCatMappingDroplet : " + returnPostURL);
							pRequest.setParameter("returnPostURL", returnPostURL);
						}else{
							pRequest.setParameter("returnPostURL", "");
						}
						
					}
					else{
						logDebug("returnPostURL in BBBUSCanadaCatMappingDroplet : " + categoryMappingVO.getUsUrl());
						pRequest.setParameter("returnPostURL", categoryMappingVO.getUsUrl());
					}
				}
			} catch (BBBBusinessException e) {
				logError(LogMessageFormatter.formatMessage(pRequest, "Business Exception from service of BBBUSCanadaCatMappingDroplet ",BBBCoreErrorConstants.BROWSE_ERROR_1020),e);
				pRequest.setParameter("error", "err_us_canada_category_mapping_system_error");
				pRequest.serviceLocalParameter("error", pRequest, pResponse);
			} catch (BBBSystemException e) {
				logError(LogMessageFormatter.formatMessage(pRequest, "System Exception from service of BBBUSCanadaCatMappingDroplet ",BBBCoreErrorConstants.BROWSE_ERROR_1021),e);
				pRequest.setParameter("error", "err_us_canada_category_mapping_system_error");
				pRequest.serviceLocalParameter("error", pRequest, pResponse);
			}
		}
		pRequest.serviceParameter("output", pRequest, pResponse);
	}

	protected String extractCurrentSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogToolsImpl getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogToolsImpl catalogTools) {
		this.catalogTools = catalogTools;
	}


}
