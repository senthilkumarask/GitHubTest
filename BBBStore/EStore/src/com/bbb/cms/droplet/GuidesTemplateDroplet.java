package com.bbb.cms.droplet;

import java.util.List;

import atg.core.util.StringUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.common.BBBDynamoServlet;

import com.bbb.cms.GuidesTemplateVO;
import com.bbb.cms.manager.GuidesTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;

public class GuidesTemplateDroplet extends BBBDynamoServlet {
	private static final String OUTPUT = "output";
	public static final String OPARAM_ERROR = "error";
	private static final String LST_DROP_DOWN = "lstDropDown";

	private static final String LST_GUIDES_TEMPLATE = "lstGuidesTemplate";

	private static final String EMPTY = "empty";

	private static final String GUIDES_CATEGORY = "guidesCategory";

	private static final String CONTENT_TYPE = "contentType";
	
	private static final String SITE_ID = "siteId";

	private GuidesTemplateManager mGuidesTemplateManager = null;

	private List<Integer> mLstDropDown;
	private String mDefaultContentType;
	private String mDefaultGuideCategory;
	private BBBCatalogTools catalogTools;
	private String configKey;

	private List<String> validContentTypes;

	public List<String> getValidContentTypes() {
		return validContentTypes;
	}

	public void setValidContentTypes(List<String> validContentTypes) {
		this.validContentTypes = validContentTypes;
	}

	/**
	 * @return the lstDropDown
	 */
	public List<Integer> getLstDropDown() {
		return mLstDropDown;
	}

	/**
	 * @param pLstDropDown
	 *          the lstDropDown to set
	 */
	public void setLstDropDown(List<Integer> pLstDropDown) {
		mLstDropDown = pLstDropDown;
	}

	/**
	 * @return the guidesTemplateManager
	 */
	public GuidesTemplateManager getGuidesTemplateManager() {
		return mGuidesTemplateManager;
	}

	/**
	 * @param pGuidesTemplateManager
	 *          the guidesTemplateManager to set
	 */
	public void setGuidesTemplateManager(GuidesTemplateManager pGuidesTemplateManager) {
		mGuidesTemplateManager = pGuidesTemplateManager;
	}

	public String getDefaultContentType() {
		return mDefaultContentType;
	}

	public void setDefaultContentType(String defaultContentType) {
		this.mDefaultContentType = defaultContentType;
	}

	public String getDefaultGuideCategory() {
		return mDefaultGuideCategory;
	}

	public void setDefaultGuideCategory(String defaultGuideCategory) {
		this.mDefaultGuideCategory = defaultGuideCategory;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * @return the configKey
	 */
	public String getConfigKey() {
		return configKey;
	}

	/**
	 * @param configKey the configKey to set
	 */
	public void setConfigKey(String configKey) {
		this.configKey = configKey;
	}

	/**
	 * This method gets the value of contentType and guidesCategory from the page
	 * and interact with manager class.
	 */

	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
			throws javax.servlet.ServletException, java.io.IOException {

		String contentType = (String) pRequest.getLocalParameter(CONTENT_TYPE);
		String siteId = (String) pRequest.getLocalParameter(SITE_ID);
		if(StringUtils.isEmpty(contentType)){
			
				logDebug("contentType is null from frontEnd. So using default contentType : "+getDefaultContentType());
			
			contentType = getDefaultContentType();
		}
		String guidesCategory = (String) pRequest.getLocalParameter(GUIDES_CATEGORY);
		if(StringUtils.isEmpty(guidesCategory)){
			
				logDebug("contentType is null from frontEnd. So using default guidesCategory : "+getDefaultGuideCategory());
			
			guidesCategory = getDefaultGuideCategory();
		}
		List<GuidesTemplateVO> lstGuidesTemplate = null;
		if (StringUtils.isEmpty(contentType) || StringUtils.isEmpty(guidesCategory)) {
			pRequest.serviceParameter(EMPTY, pRequest, pResponse);
			return;
		} else {
			if(!getValidContentTypes().contains(contentType)){
				pResponse.setStatus(404);
				pRequest.serviceParameter(OPARAM_ERROR, pRequest, pResponse);
				return;
			}
			lstGuidesTemplate = getGuidesTemplateManager().getGuidesTemplateData(contentType, guidesCategory, siteId);
			if (lstGuidesTemplate == null || lstGuidesTemplate.isEmpty()) {
				pRequest.setParameter("emptyResults", true);
				pRequest.serviceParameter(OUTPUT, pRequest, pResponse);
				return;
			}
		}
		try{
			List<String> lstDropDown = (List<String>) this.getCatalogTools().getContentCatalogConfigration(this.getConfigKey());
			String firstDropDown=lstDropDown.get(0);
			pRequest.setParameter(LST_GUIDES_TEMPLATE, lstGuidesTemplate);
			pRequest.setParameter(LST_DROP_DOWN, lstDropDown);
			pRequest.setParameter("FirstElement", firstDropDown);
			
			pRequest.serviceParameter(OUTPUT, pRequest, pResponse);
		}catch (BBBBusinessException e) {
			 
		    logError(LogMessageFormatter.formatMessage(pRequest, "GuidesTemplateDroplet|service()|BBBBusinessException","catalog_1034"),e);
		          
		        	
			pRequest.setParameter("errorMsg","err_guides_config_exception");
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest,
					pResponse);
		} catch (BBBSystemException e) {
			
		    logError(LogMessageFormatter.formatMessage(pRequest, "GuidesTemplateDroplet|service()|BBBSystemException","catalog_1035"),e);
		      
			pRequest.setParameter("errorMsg","err_guides_config_exception");
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest,
					pResponse);
		} 
	}
}
