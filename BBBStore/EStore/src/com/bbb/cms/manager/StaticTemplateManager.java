package com.bbb.cms.manager;

import java.util.Map;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.bbb.cms.StaticContentVO;
import com.bbb.cms.tools.CmsTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

/**
 * This Class perform all the delegations for static template.
 */
public class StaticTemplateManager extends BBBGenericService {

	private CmsTools mCmsTools;

	/**
	 * This method returns static page data based on siteId and pageName.
	 * 
	 * @param pSiteId
	 * @param pPageName
	 * @return RepositoryItem
	 * @throws RepositoryException
	 */
	public RepositoryItem getStaticTemplateData(String pSiteId, String pPageName, String pbabyCAMode)
			throws RepositoryException {

		RepositoryItem staticTemplateData = null;

		
		logDebug("starting method StaticTemplateManager.getStaticTemplateData, Passed parameters: "
					+ "pSiteId=" + pSiteId + ", pPageName=" + pPageName);

		

		staticTemplateData = getCmsTools().getStaticTemplateData(pSiteId, pPageName,pbabyCAMode);
		
		
		logDebug("Existing method StaticTemplateManager.getStaticTemplateData");

	
		return staticTemplateData;

	}
	
	/**
	 * This method is used to get content for Static pages based on pageName
	 * @param pageName page name whose static content details needs to be fetched
	 * @return static content related details for a particular page
	 * @throws BBBBusinessException exception in case any business error occurred
	 * @throws BBBSystemException exception in case any error occurred while fetching static content
	 */
	@SuppressWarnings("unchecked")
	public StaticContentVO getStaticContent(String pageName) throws BBBBusinessException, BBBSystemException{
		logDebug("StaticTemplateManager.getStaticContent : START");
		final StaticContentVO staticContentVO = new StaticContentVO();
		try {

			final String siteId = SiteContextManager.getCurrentSiteId();
			logDebug("StaticTemplateManager.getStaticContent input param pageName "+pageName +" site Id from SiteContext "+siteId);
			if(StringUtils.isEmpty(pageName)){
				logError(" input parameter pageName is null or empty throwing BBBBusinessException");
				throw new BBBBusinessException(BBBCmsConstants.ERROR_NULL_INPUT_PARAM, "Page name can not be null");
			
			}else{
				
				final RepositoryItem staticTemplateData = this.getCmsTools().getStaticTemplateData(siteId, pageName,null);
				if(staticTemplateData!=null){

					staticContentVO.setTemplateId(staticTemplateData.getRepositoryId());
					staticContentVO.setPageName((String) staticTemplateData.getPropertyValue(BBBCmsConstants.PAGENAME));
					staticContentVO.setBbbPageName((String) staticTemplateData.getPropertyValue(BBBCmsConstants.BBBPAGENAME));
					staticContentVO.setPageCopy((String) staticTemplateData.getPropertyValue(BBBCmsConstants.PAGE_COPY));
					staticContentVO.setPageHeaderCopy((String) staticTemplateData.getPropertyValue(BBBCmsConstants.PAGE_HEASER_COPY));
					staticContentVO.setPageTitle((String) staticTemplateData.getPropertyValue(BBBCmsConstants.PAGE_TITLE));
					staticContentVO.setPageType(staticTemplateData.getPropertyValue(BBBCmsConstants.PAGETYPE)!=null?(Integer) staticTemplateData.getPropertyValue(BBBCmsConstants.PAGETYPE):null);
					staticContentVO.setSeoUrl((String) staticTemplateData.getPropertyValue(BBBCmsConstants.SEO_URL));
					staticContentVO.setSiteId(siteId);
					staticContentVO.setOmnitureData((Map) staticTemplateData.getPropertyValue(BBBCmsConstants.OMNITUREDATA));
				}else{
					logError(" no result found for pageName"+pageName +" and siteId "+siteId+" throwing BBBBusinessException");
					throw new BBBBusinessException(BBBCmsConstants.ERROR_VALUE_NOT_FOUND, "Value not found");

				}
			}
		} catch (RepositoryException e) {
			logError(" Repository Error in fetching data",e);
			throw new BBBSystemException(BBBCmsConstants.ERROR_FETCH_STATIC_CONTENT, "Error occurred while fetching static content");
			
		}
		
		logDebug("StaticTemplateManager.getStaticContent : END");
		return staticContentVO;

	}

	
	/**
	 * @return instance of CmsTools
	 */
	public CmsTools getCmsTools() {
		return mCmsTools;
	}

	/**
	 * @param pCmsTools
	 *            the pCmsTools to set
	 */
	public void setCmsTools(CmsTools pCmsTools) {
		mCmsTools = pCmsTools;
	}
	

}
