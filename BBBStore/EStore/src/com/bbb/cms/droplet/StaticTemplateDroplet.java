package com.bbb.cms.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.cms.StaticPageBreadcrumbVO;
import com.bbb.cms.manager.StaticTemplateManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.profile.session.BBBSessionBean;

/**
 * This Class is used to populate static template related data.
 */

public class StaticTemplateDroplet extends BBBDynamoServlet {

	private StaticTemplateManager mStaticTemplateManager;
	private Repository mStaticTemplateRepository;
	
	public Repository getStaticTemplateRepository() {
		return mStaticTemplateRepository;
	}

	/**
	 * @param pStaticTemplateRepository
	 *            the mStaticTemplateRepository to set
	 */
	public void setStaticTemplateRepository(Repository pStaticTemplateRepository) {
		mStaticTemplateRepository = pStaticTemplateRepository;
	}
	/**
	 * @return the mStaticTemplateManager
	 */
	public StaticTemplateManager getStaticTemplateManager() {
		return mStaticTemplateManager;
	}

	/**
	 * @param pStaticTemplateManager
	 *            the mStaticTemplateManager to set
	 */
	public void setStaticTemplateManager(
			StaticTemplateManager pStaticTemplateManager) {
		mStaticTemplateManager = pStaticTemplateManager;
	}

	/**
	 * 
	 */
	@Override
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		
		logDebug("starting method StaticTemplateDroplet");


		RepositoryItem staticTemplateData = null;
		try {

			String siteId = null;
			String pageName = null;
			String babyCAMode = "false";
			int counter= 0;
			
			List<StaticPageBreadcrumbVO> parentPages = new ArrayList<StaticPageBreadcrumbVO>();
			RepositoryItem parentPage=null;
			

			if (pRequest.getLocalParameter(BBBCmsConstants.SITE_ID) != null) {
				siteId = (String) pRequest
						.getLocalParameter(BBBCmsConstants.SITE_ID);
			}

			if (pRequest.getLocalParameter(BBBCmsConstants.PAGENAME) != null) {
				pageName = (String) pRequest
						.getLocalParameter(BBBCmsConstants.PAGENAME);
			}
			BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
			if (sessionBean != null && sessionBean.getBabyCA() != null) {
				babyCAMode = (String)sessionBean.getBabyCA();
			}
			if(StringUtils.equalsIgnoreCase(babyCAMode,BBBCoreConstants.TRUE)){
				pageName = (pageName != null) ? pageName.concat(BBBCmsConstants.BABY_CA) : null;
			}
			
			staticTemplateData = getStaticTemplateManager()
					.getStaticTemplateData(siteId, pageName,babyCAMode);
			
			
			if (staticTemplateData != null) {
				parentPage=(RepositoryItem) staticTemplateData.getPropertyValue(BBBCmsConstants.PARENTPAGE);
				while(parentPage !=null &&(counter<5)){
						final StaticPageBreadcrumbVO breadcrumbVO = new StaticPageBreadcrumbVO();
						String bbbPageName =(String) parentPage.getPropertyValue(BBBCmsConstants.BBBPAGENAME);
						String pageTitle = (String) parentPage.getPropertyValue(BBBCmsConstants.PAGE_TITLE);
						breadcrumbVO.setBbbPageName(bbbPageName);
						breadcrumbVO.setPageTitle(pageTitle);
						parentPages.add(breadcrumbVO);
						RepositoryItem parentPageIdNew =(RepositoryItem) parentPage.getPropertyValue(BBBCmsConstants.PARENTPAGE);
						parentPage =parentPageIdNew;
						counter++;
					} 
				Collections.reverse(parentPages);
				pRequest.setParameter(BBBCmsConstants.STATIC_TEMPLATE_DATA,
						staticTemplateData);
				pRequest.setParameter(BBBCmsConstants.PAGE_BREADCRUMB,
						parentPages);
				pRequest.serviceParameter(BBBCmsConstants.OUTPUT, pRequest,
						pResponse);
			} else {
				
				pRequest.serviceParameter(BBBCmsConstants.EMPTY, pRequest,
						pResponse);
			}

		} catch (RepositoryException repException) {
			
			logError("catalog_1052: Repository Exception StaticTemplateDroplet", repException);
		
		}

		
		logDebug("Existing method StaticTemplateDroplet");

		
	}
}
