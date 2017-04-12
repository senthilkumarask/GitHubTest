package com.bbb.cms.droplet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import atg.multisite.SiteContextManager;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.cms.manager.ContentTemplateManager;
import com.bbb.cms.vo.CLPResponseVO;
import com.bbb.cms.vo.CMSResponseVO;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;


/**
 * This class extends BBBDynamoServlet and contain methods used to  to access 
 * all data related to Custom landing page for Desktop and mobile site.
 * 
 * @author ssha53
 * @version %I%, %G%
 */
public class CustomLandingTemplateDroplet extends BBBDynamoServlet {
	
	private final static String CLPNAME = "clpName";
	
	private final static String CLP_TITLE = "clpTitle";
	
	private static final String ALT_URL_REQUIRED = "alternateURLRequired";
	
	private static final String ALTERNATE_URL = "alternateURL";
	
	private static final String CANADA_ALTERNATE_URL = "canadaAlternateURL";
	
	private static final String US_ALTERNATE_URL = "usAlternateURL";
	
	private static final String ARRAY_SEPARETOR = "}";
	
	private static final String US_CATEGORY_ID = "usCategoryId";
	
	private static final String CANADA_CATEGORY_ID = "canadaCategoryId";
	
	private static final String OMNITURE_FLAG = "omnitureFlag";
	// ContentTemplateManager instance
	private ContentTemplateManager mContentTemplateMgr;

	private BBBCatalogTools mCatalogTools;
	
	

	/**
	 * This method is used to fetch custom landing page information for input
	 * category Id and channel.this method also provide alternate site 
	 * custom landing page URL.
	 * 
	 * @param pRequest
	 *            Catalog repository item
	 * @param pSitemapGeneratorService
	 *            Mobile Sitemap Generator service that holds global settings
	 * @param pResponse
	 *            Site repository item
	 * @throws ServletException
	 * @Throws IOException
	 */
	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
					throws  ServletException, IOException
	{

		logDebug("CustomLandingTemplateDroplet.service() - start");
		
		BBBPerformanceMonitor.start(CustomLandingTemplateDroplet.class.getName() 
				+ ":" + BBBCoreConstants.CONSTANT_SERVICE);
		
		final String categoryId = pRequest.getParameter(BBBCoreConstants.PARAM_CATEGORY_ID);
		final String templateName = pRequest.getParameter(BBBCoreConstants.TEMPLATE_NAME);
 		final String name = pRequest.getParameter(BBBCoreConstants.PARAM_NAME);
 		final String altURLRequired = pRequest.getParameter(ALT_URL_REQUIRED);
 		final String omnitureFlag = pRequest.getParameter(OMNITURE_FLAG);
 		//final String clpTitle = pRequest.getParameter(CLP_TITLE);
 		
 		logDebug("CustomLandingTemplateDroplet.service() - input categoryId = " + categoryId );
 		logDebug("CustomLandingTemplateDroplet.service() - input templateName = " + templateName);
 		logDebug("CustomLandingTemplateDroplet.service() - input name = " + name);
 		logDebug("CustomLandingTemplateDroplet.service() - input altURLRequired = " + altURLRequired);
 		logDebug("CustomLandingTemplateDroplet.service() - input omnitureFlag = " + omnitureFlag);
 		
 		final String siteId = getCurrentSiteId();
		logDebug("CustomLandingTemplateDroplet.service() categoryId :: " + categoryId);
		logDebug("CustomLandingTemplate.service() siteId :: " + siteId );
 		try {
			
			// Get data from Custom landing template repository for a given category id
			if(!BBBUtility.isEmpty(categoryId) || !BBBUtility.isEmpty(name)){
			
				String jsonString = "{associatedCategory:"+ categoryId + ARRAY_SEPARETOR;
				if(BBBUtility.isEmpty(categoryId)){
					jsonString = "{clpName:"+ name + ARRAY_SEPARETOR;
				}
				
				// set channel to request parameter
				pRequest.setParameter(BBBCoreConstants.CHANNEL, BBBUtility.getChannel());
				
				if(null != omnitureFlag 
						&& omnitureFlag.equals(BBBCoreConstants.TRUE)){
				getCategoryDetails(pRequest, categoryId, siteId);	
				}
				
				final CMSResponseVO responseVO = getContentTemplateMgr().getContent(templateName, jsonString);
				if(null != responseVO && null != responseVO.getResponseItems() ){
					
					// Alternate URL is required or not check
					if(null != altURLRequired 
							&& altURLRequired.equals(BBBCoreConstants.TRUE)){
						// alternate URL repository
						String canadaCategoryId = null;
						String usCategoryId = null;
						if(BBBCoreConstants.DEFAULT_CHANNEL_VALUE.equalsIgnoreCase(BBBUtility.getChannel())){
							canadaCategoryId = (String) responseVO.getResponseItems()[0].getPropertyValue(CANADA_CATEGORY_ID);
							usCategoryId = (String) responseVO.getResponseItems()[0].getPropertyValue(US_CATEGORY_ID);
						}
						
						//Checking for CanadaCatID whether a CLP template exists for it or not
						if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_US)&& null!=canadaCategoryId){
						final boolean templateExistCanada=getContentTemplateMgr().checkTemplateForId(templateName,canadaCategoryId);
								if(templateExistCanada){
									//Creating Alternate URl once template exist for catID
									final String canadaAlternateURL = getContentTemplateMgr().createCanonicalURL(canadaCategoryId, templateName);
									logDebug("CustomLandingTemplateDroplet.service() - canadaAlternateURL :: " + canadaAlternateURL);
									if(!BBBUtility.isEmpty(canadaAlternateURL)){
										pRequest.setParameter (CANADA_ALTERNATE_URL, canadaAlternateURL);
									}
								}
						}
						//Checking for USCatID whether a CLP template exists for it or not
						if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)&& null!=usCategoryId){
						final boolean templateExistUS=getContentTemplateMgr().checkTemplateForId(templateName,usCategoryId);
								if(templateExistUS){
									//Creating Alternate URl once template exist for catID
									final String usAlternateURL = getContentTemplateMgr().createCanonicalURL(usCategoryId, templateName);
									logDebug("CustomLandingTemplateDroplet.service() - usAlternateURL :: " + usAlternateURL);
									if(!BBBUtility.isEmpty(usAlternateURL)){
										pRequest.setParameter (US_ALTERNATE_URL, usAlternateURL);
									}
								}
						}
						logDebug("CustomLandingTemplateDroplet.service() - alternateURLRequired :: " + altURLRequired);
							
							// check if template exist for alternate URL so create alternate url
							final String alternateURL = getContentTemplateMgr().createAlternateURL(categoryId, templateName, BBBUtility.getAlternateChannel());
						
							logDebug("CustomLandingTemplateDroplet.service() - alternateURL :: " + alternateURL);
							
							if(!BBBUtility.isEmpty(alternateURL)){
								pRequest.setParameter (ALTERNATE_URL, alternateURL);
							}

							
					}
					
					pRequest.setParameter (CLPNAME, responseVO.getResponseItems()[0].getPropertyValue(CLPNAME));
					pRequest.setParameter (CLP_TITLE, responseVO.getResponseItems()[0].getPropertyValue(CLP_TITLE));
					pRequest.setParameter (BBBCoreConstants.PARAM_RESPONSE_VO, responseVO);
					pRequest.serviceParameter (BBBCoreConstants.OPARAM, pRequest, pResponse);
	
				}else{
					// error out id template not exist
					logDebug("CustomLandingTemplateDroplet.service() :: Empty Response");
					pRequest.serviceParameter (BBBCoreConstants.EMPTY, pRequest, pResponse);
				}
				
			}else{
				
				logDebug("CustomLandingTemplateDroplet.service() :: Error in Response");
				// error output if template not exist
				pRequest.serviceParameter (BBBCoreConstants.ERROR, pRequest, pResponse);
			}
				

				
		} catch (BBBSystemException e) {

			logError(LogMessageFormatter.formatMessage(pRequest, "System Exception from " +
					"service of CustomLandingTemplateDroplet ",BBBCoreErrorConstants.BROWSE_ERROR_1024),e);
			pRequest.setParameter(BBBCoreConstants.ERROR, BBBCoreErrorConstants.ERROR_CUSTOM_LANDING);
			pRequest.serviceLocalParameter(BBBCoreConstants.ERROR, pRequest, pResponse);
			pResponse.setStatus(404);
		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "Business Exception from " +
					"service of CustomLandingTemplateDroplet ", BBBCoreErrorConstants.BROWSE_ERROR_1025),e);
			pRequest.setParameter(BBBCoreConstants.ERROR, BBBCoreErrorConstants.ERROR_CUSTOM_LANDING);
			pRequest.serviceLocalParameter(BBBCoreConstants.ERROR, pRequest, pResponse);
			pResponse.setStatus(404);
		} catch (RepositoryException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "System Exception from " +
					"service of CustomLandingTemplateDroplet ", BBBCoreErrorConstants.BROWSE_ERROR_1024),e);
			pRequest.setParameter(BBBCoreConstants.ERROR, BBBCoreErrorConstants.ERROR_CUSTOM_LANDING);
			pRequest.serviceLocalParameter(BBBCoreConstants.ERROR, pRequest, pResponse);
			pResponse.setStatus(404);
		}
		BBBPerformanceMonitor.end(CustomLandingTemplateDroplet.class.getName()+ ":" + BBBCoreConstants.CONSTANT_SERVICE);

		logDebug("CustomLandingTemplateDroplet.service() - end");
	}


	protected String getCurrentSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}


	private void getCategoryDetails(final DynamoHttpServletRequest pRequest,
			final String categoryId, final String siteId)
			throws BBBBusinessException, BBBSystemException {
		String categoryL1 = "";
		String categoryL2 = "";
		String categoryL3 = "";
		
	
		final Map<String, CategoryVO> parentCategoryMap = this.getCatalogTools().getParentCategory(categoryId, siteId);
		if (parentCategoryMap != null) {
			for (int count = 0; count < parentCategoryMap.size(); count++) {
				final CategoryVO category = parentCategoryMap.get(String.valueOf(count)); 
				if (category != null) {
					if (count == 0) {
						categoryL2 = category.getCategoryName();  
					}
					if (count == 1) {
						categoryL1 = category.getCategoryName();  
					}
				}
			}
		}
		
		final CategoryVO categoryVO = this.getCatalogTools().getCategoryDetail(siteId, categoryId,false);
		//included as part of Release 2.1 implementation
		this.getCatalogTools().getBccManagedCategory(categoryVO);
		
		if (categoryVO != null) {
			categoryL3 = categoryVO.getCategoryName();
			if(categoryL1 != null && categoryL1.equalsIgnoreCase(categoryL3)) {
				categoryL3 = "";
			}
		}
		
		pRequest.setParameter (BBBCoreConstants.CATEGORYL1, categoryL1);
		pRequest.setParameter (BBBCoreConstants.CATEGORYL2, categoryL2);
		pRequest.setParameter (BBBCoreConstants.CATEGORYL3, categoryL3);
	}	

	
	/**
	 * This method is used to invoke Custom landing template data using Rest Call
	 * 
	 * @param categoryId in <code>String</code> format
	 * @param templateName in <code>String</code> format
	 * @param altURLRequired in <code>String</code> format
	 * @param clpName in <code>String</code> format
	 * @return CMSResponseVO
	 */
	public CLPResponseVO getCustomLandingTemplate(final String categoryId, final String templateName, 
			final String altURLRequired, final String clpName)  {
		logDebug("CustomLandingTemplateDroplet.getCustomLandingTemplate() - start");

		BBBPerformanceMonitor.start("CustomLandingTemplateDroplet"
				+ " getCustomLandingTemplate");
		
		logDebug("CustomLandingTemplateDroplet.getCustomLandingTemplate() - categoryId :: " + categoryId);
		logDebug("CustomLandingTemplateDroplet.getCustomLandingTemplate() - templateName :: " + templateName);
		logDebug("CustomLandingTemplateDroplet.getCustomLandingTemplate() - altURLRequired :: " + altURLRequired);
		logDebug("CustomLandingTemplateDroplet.getCustomLandingTemplate() - clpName :: " + clpName);
		
		final CLPResponseVO responseVO = new CLPResponseVO();
		try{
			final DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
			final DynamoHttpServletResponse pResponse = ServletUtil.getCurrentResponse();
			pRequest.setParameter(BBBCoreConstants.PARAM_CATEGORY_ID, categoryId);
			pRequest.setParameter(BBBCoreConstants.TEMPLATE_NAME, templateName);
			pRequest.setParameter(ALT_URL_REQUIRED, altURLRequired);
			pRequest.setParameter(BBBCoreConstants.PARAM_NAME, clpName);
			try{
				//calls the "service" method to fetch the details of the Custom Landing page
				service(pRequest, pResponse);
			}catch (ServletException e) {
				responseVO.setErrorExist(true);
				responseVO.setErrorCode(BBBCatalogErrorCodes.CLP_NOT_AVAILABLE_IN_REPO);
				logError("CustomLandingTemplateDroplet.getCustomLandingTemplate() " +
						"Error Code = " + responseVO.getErrorCode());
			}catch (IOException e) {
				responseVO.setErrorExist(true);
				responseVO.setErrorCode(BBBCatalogErrorCodes.CLP_NOT_AVAILABLE_IN_REPO);
				logError("CustomLandingTemplateDroplet.getCustomLandingTemplate() " +
						"Error Code = " + responseVO.getErrorCode());
			}
			if("error".equalsIgnoreCase((String)pRequest
					.getObjectParameter("error"))){
				responseVO.setErrorExist(true);
				responseVO.setErrorCode(BBBCatalogErrorCodes.CLP_NOT_AVAILABLE_IN_REPO);
				logError("CustomLandingTemplateDroplet.getCustomLandingTemplate() " +
						"Error Code = " + responseVO.getErrorCode());
			}
			
			responseVO.setCmsResponseVO((CMSResponseVO) pRequest.getObjectParameter(BBBCoreConstants.PARAM_RESPONSE_VO));
			
			if(null != pRequest.getObjectParameter(ALTERNATE_URL)){
				responseVO.setAlternateURL((String)pRequest.getObjectParameter(ALTERNATE_URL));
			}

		}finally{
			BBBPerformanceMonitor.end("CustomLandingTemplateDroplet"
					+ " getCustomLandingTemplate");
		}
		
		logDebug("CustomLandingTemplateDroplet.getCustomLandingTemplate() - end");
		return responseVO;
	}


	/**
	 * @return the contentTemplateMgr
	 */
	public ContentTemplateManager getContentTemplateMgr() {
		return mContentTemplateMgr;
	}


	/**
	 * @param pContentTemplateMgr the contentTemplateMgr to set
	 */
	public void setContentTemplateMgr(
			final ContentTemplateManager pContentTemplateMgr) {
		mContentTemplateMgr = pContentTemplateMgr;
	}
	

    /**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}

	

}