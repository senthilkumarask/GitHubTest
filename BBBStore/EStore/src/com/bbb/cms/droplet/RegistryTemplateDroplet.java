package com.bbb.cms.droplet;

import java.util.List;

import com.bbb.cms.RegistryTemplateVO;
import com.bbb.cms.manager.RegistryTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;


public class RegistryTemplateDroplet extends BBBDynamoServlet{

	private RegistryTemplateManager mRegistryTemplateManager = null;
	private BBBCatalogTools mCatalogTools;
	private String regChklstPdfKey;
	private String collgChklstPdfKey;
	
	final String REG_PAGENAME = "pageName";
	final String EMPTY = "empty";
	final String REGISTRY_OUTPUT = "output";
	final String REG_TEMP_VO = "RegistryTemplateVO";
	final String PAGE_NAME_OTHERS = "Others";
	final String PAGE_TYPE_COLLEGE = "College";
	final String PAGE_WRAPPER_COLLEGE = "college collegeChecklist useFB";
	final String PAGE_WRAPPER_REGISTRY = "registry registryFeatures useFB";
	final String PAGE_VARIATION_BC = "bc";
	final String PAGE_VARIATION_BR = "br";
	final String PAGE_VARIATION_BB = "bb";
	

	public RegistryTemplateManager getRegistryTemplateManager() {

		return mRegistryTemplateManager;
	}

	public void setRegistryTemplateManager(
			RegistryTemplateManager pRegistryTemplateManager) {
		this.mRegistryTemplateManager = pRegistryTemplateManager;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param pCatalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}

	/**
	 * 
	 * 
	 */




	public void service(final DynamoHttpServletRequest request, final DynamoHttpServletResponse response)
			throws javax.servlet.ServletException, java.io.IOException {

		logDebug("starting method RegistryTemplateDroplet");
		

		String pageName =null; 
		String siteId = getCurrentSiteId();
		
		if(siteId==null)
		{
			siteId=request.getParameter("siteId");
		}

		if(request.getParameter(REG_PAGENAME) !=null){
			pageName = (String)request.getParameter(REG_PAGENAME);
		}

		if(StringUtils.isEmpty(pageName)) {
			request.serviceParameter(EMPTY, request, response);
		} else{
			
				logDebug("Calling RegistryTemplateManager : ");
			
			RegistryTemplateVO registryTemplateVO=getRegistryTemplateManager().getRegistryTemplateData(pageName, siteId);
			if(registryTemplateVO != null){
				
				logDebug("Received RegistryTemplateVO : "+registryTemplateVO);
			
				String registryPdfURL = null;
				try {
					String regKey=this.getRegChklstPdfKey();
					if(regKey!=null){
						registryPdfURL = getCatalogTools().getContentCatalogConfigration(getRegChklstPdfKey()).get(0);			}		
				} catch (BBBSystemException e) {	
					
					logError(LogMessageFormatter.formatMessage(null, "RegistryTemplateDroplet.service() | BBBSystemException ","catalog_1048"), e);		
					
				} catch (BBBBusinessException e) {	
					
					logError(LogMessageFormatter.formatMessage(null, "RegistryTemplateDroplet.service() | BBBBusinessException ","catalog_1049"), e);	
					
				}
				if (registryPdfURL != null) {
					request.setParameter("registryPdfURL", registryPdfURL);						
				}

				String collegePdfURL = null;
				try {
					String colgKey=this.getCollgChklstPdfKey();
					if(colgKey!=null){
						collegePdfURL = getCatalogTools().getContentCatalogConfigration(getCollgChklstPdfKey()).get(0);			}		
				} catch (BBBSystemException e) {
					
					logError(LogMessageFormatter.formatMessage(request, "RegistryTemplateDroplet.service() | BBBSystemException ","catalog_1050"), e);
					
				} catch (BBBBusinessException e) {	
					
					logError(LogMessageFormatter.formatMessage(request, "RegistryTemplateDroplet.service() | BBBBusinessException ","catalog_1051"), e);	
					
				}
				if (collegePdfURL != null) {
					request.setParameter("collegePdfURL", collegePdfURL);						
				}

				setPageTheme(request, registryTemplateVO,siteId);
				request.setParameter(REG_TEMP_VO, registryTemplateVO);
				request.serviceParameter(REGISTRY_OUTPUT, request, response);
			}
			else{
				
					logDebug("Received RegistryTemplateVO as Null: ");
				
				request.serviceParameter(EMPTY, request, response);
			}
		}
		
			logDebug("Existing method RegistryTemplateDroplet");
		
	}

	protected String getCurrentSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}

	public void setPageTheme(final DynamoHttpServletRequest request, RegistryTemplateVO registryTemplateVO, String siteId){
			String pageWrapper = null;
			String pageVariation = null;
			String bbbPageName = null;
			String pageName = registryTemplateVO.getPageName();
			String pageType = registryTemplateVO.getPageType();
			List<String> pageAttributes = null;
			
			if(pageName != null && pageName.equalsIgnoreCase(PAGE_NAME_OTHERS)){
				bbbPageName = registryTemplateVO.getBbbPageName();
				try {
					pageAttributes = this.getCatalogTools().getAllValuesForKey(BBBCmsConstants.PAGE_THEME_KEYS, bbbPageName);
				} catch (BBBBusinessException bbbBusinessException) {
					logDebug("No Value found for PagethemeKeys " + bbbPageName);
				} catch (BBBSystemException bbbSystemException) {
					logError(LogMessageFormatter.formatMessage(null, "RegistryTemplateDroplet.setPageTheme() | BBBSystemException ","catalog_1049"), bbbSystemException);	
				}
			}else{
				try {
					pageAttributes = this.getCatalogTools().getAllValuesForKey(BBBCmsConstants.PAGE_THEME_KEYS, pageName);
				} catch (BBBBusinessException bbbBusinessException) {
					logDebug("No Value found for PagethemeKeys " + pageName);
				} catch (BBBSystemException bbbSystemException) {
					logError(LogMessageFormatter.formatMessage(null, "RegistryTemplateDroplet.setPageTheme() | BBBSystemException ","catalog_1049"), bbbSystemException);	
				}
			}
			
			if (pageAttributes != null && !pageAttributes.isEmpty()) {
				String attributes[] = pageAttributes.get(0).split(","); 
				pageWrapper = attributes[0];
				if(attributes.length>1){
					pageVariation = attributes[1];
				}
				logDebug("pageWrapper from config keys  : " + pageWrapper);
				logDebug("pageVariation from config keys : " + pageVariation);
			}
			
			if(pageType != null){
				if (pageWrapper == null) {
					if(pageName.equalsIgnoreCase(PAGE_NAME_OTHERS)) {
						if (pageType.equalsIgnoreCase(PAGE_TYPE_COLLEGE)){
							pageWrapper = PAGE_WRAPPER_COLLEGE;
						} else {
							pageWrapper = PAGE_WRAPPER_REGISTRY;
						}
					}
				}
				if (pageType.equalsIgnoreCase(PAGE_TYPE_COLLEGE)){
					pageVariation = PAGE_VARIATION_BC;
				}
			}

			if(pageVariation == null && (siteId.equals(BBBCoreConstants.SITE_BAB_US) || siteId.equals(BBBCoreConstants.SITE_BAB_CA))){
				pageVariation = PAGE_VARIATION_BR;
			}
			if(siteId.equals(BBBCoreConstants.SITE_BBB)){
				pageVariation = PAGE_VARIATION_BB;
			}
			
			registryTemplateVO.setPageWrapper(pageWrapper);
			registryTemplateVO.setPageVariation(pageVariation);
	}
	public String getRegChklstPdfKey() {
		return regChklstPdfKey;
	}

	public void setRegChklstPdfKey(String regChklstPdfKey) {
		this.regChklstPdfKey = regChklstPdfKey;
	}

	public String getCollgChklstPdfKey() {
		return collgChklstPdfKey;
	}

	public void setCollgChklstPdfKey(String collgChklstPdfKey) {
		this.collgChklstPdfKey = collgChklstPdfKey;
	}

}
