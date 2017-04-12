package com.bbb.seo;


import atg.repository.Repository;
import atg.repository.seo.IndirectUrlTemplate;
import atg.repository.seo.ItemLinkException;
import atg.repository.seo.UrlParameter;
import atg.service.webappregistry.WebApp;
import com.bbb.common.BBBGenericService;

public class CategorySeoLinkGenerator extends BBBGenericService {

	IndirectUrlTemplate categoryTemplate;
	Repository defaultRepository;
	String defaultItemDescriptorName;

	public IndirectUrlTemplate getCategoryTemplate() {
		return categoryTemplate;
	}

	public void setCategoryTemplate(IndirectUrlTemplate categoryTemplate) {
		this.categoryTemplate = categoryTemplate;
	}

	public Repository getDefaultRepository() {
		return defaultRepository;
	}

	public void setDefaultRepository(Repository defaultRepository) {
		this.defaultRepository = defaultRepository;
	}

	public String getDefaultItemDescriptorName() {
		return defaultItemDescriptorName;
	}

	public void setDefaultItemDescriptorName(String defaultItemDescriptorName) {
		this.defaultItemDescriptorName = defaultItemDescriptorName;
	}

	public String formatUrl(String categoryId, String displayName) {
		String formattedURL = null;
		
		logDebug("formatUrl() starts : input parameters :  category "
					+ categoryId + ", displayName " + displayName);
		
		try {
			WebApp pDefaultWebApp = null;
			UrlParameter[] pUrlParams = getCategoryTemplate()
					.cloneUrlParameters();
			pUrlParams[0].setValue(displayName);
			pUrlParams[1].setValue(categoryId);
			formattedURL = getCategoryTemplate().formatUrl(pUrlParams,
					pDefaultWebApp);
			
			logDebug("formattedURL generated from IndirectUrlTemplate : "
						+ formattedURL);
			
		} catch (ItemLinkException e) {
			
			logError(
						"Exception occourred while creating SEO URL for the category : "
								+ displayName, e);
			
		} catch (Exception e) {
			
			logError(
						"Exception occourred while creating SEO URL for the category : "
								+ displayName, e);
			
		}
		return formattedURL;
	}

}
