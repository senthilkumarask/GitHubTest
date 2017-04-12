package atg.commerce.util;

import java.io.IOException;

import javax.servlet.ServletException;

import com.bbb.cms.GuidesTemplateVO;
import com.bbb.cms.manager.GuidesTemplateManager;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.redirectURLs.CategoryRedirectURLLoader;
import com.bbb.utils.BBBUtility;

import atg.repository.seo.CanonicalItemLink;
import atg.repository.seo.ItemLinkException;
import atg.repository.seo.UrlParameter;
import atg.repository.seo.UrlParameterLookup;
import atg.repository.seo.UrlTemplate;
import atg.repository.seo.UrlTemplateMapper;
import atg.repository.seo.UserResourceLookup;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;


/**
 * 
 * extending OOB class CanonicalItemLink , for getting the guide title for canonical urls.
 *
 */
public class BBBCanonicalItemLink extends CanonicalItemLink {
	private GuidesTemplateManager guidesTemplateManager;
	private CategoryRedirectURLLoader categoryRedirectURL;
	/**
	 * 
	 * @return guidesTemplateManager
	 */
	public GuidesTemplateManager getGuidesTemplateManager() {
	return guidesTemplateManager;
    }
	/**
	 * 
	 * @param guidesTemplateManager
	 */
	public void setGuidesTemplateManager(GuidesTemplateManager guidesTemplateManager) {
		this.guidesTemplateManager = guidesTemplateManager;
	}
	
	/**
	 * @return the categoryRedirectURL
	 */
	public CategoryRedirectURLLoader getCategoryRedirectURL() {
		return categoryRedirectURL;
	}
	
	/**
	 * @param categoryRedirectURL the categoryRedirectURL to set
	 */
	public void setCategoryRedirectURL(CategoryRedirectURLLoader categoryRedirectURL) {
		this.categoryRedirectURL = categoryRedirectURL;
	}
	
	/**
	 * Copying content of OOB class and adding a check for guides (to get item link)
	 */
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		UrlParameterLookup itemLookup = null;
		UrlParameterLookup siteLookup = null;
		String itemDescriptorName = null;
		UrlTemplate template = null;
		UrlParameter[] params = null;
		String url = null;
		UrlTemplateMapper templateMapper = null;
		try {
			if (isLoggingDebug()) {
				logDebug("Entered service method of ItemLink");
				logDebug("Request URI with query string is <"
						+ pRequest.getRequestURIWithQueryString() + ">");
				logDebug("User-Agent is <" + pRequest.getHeader("USER-AGENT") + ">");
			}
			
            if(!BBBUtility.isEmpty((String)pRequest.getParameter("doItemLookUp")) 
            		&& ((String) pRequest.getParameter("doItemLookUp")).equals(BBBCoreConstants.FALSE)
            		&& getCategoryRedirectURL().getCategoryRedirectURLMap() != null && getCategoryRedirectURL().getCategoryRedirectURLMap().containsKey(pRequest.getParameter(S_ID))) {
            	// populating redirect urls
                url = getCategoryRedirectURL().getCategoryRedirectURLMap().get(pRequest.getParameter(S_ID));
            } else {
            	itemLookup = getItemLookup(pRequest);
    			siteLookup = getSiteLookup(pRequest);

    			if (isLoggingDebug()) {
    				logDebug("Item lookup=" + itemLookup);
    				logDebug("Site lookup=" + siteLookup);
    			}

    			itemDescriptorName = getItemDescriptorName(pRequest, itemLookup);

    			if (itemDescriptorName == null) {
    				throw new ItemLinkException(
    						UserResourceLookup
    								.getResource("ERROR_NO_ITEM_DESCRIPTOR"));
    			}

    			if (isLoggingDebug()) {
    				logDebug("itemDescriptorName=" + itemDescriptorName);
    			}

    			templateMapper = getTemplateMapper(pRequest, itemDescriptorName,
    					itemLookup);

    			if (templateMapper == null) {
    				throw new ItemLinkException(UserResourceLookup.getResource(
    						"ERROR_NO_TEMPLATE_MAPPER",
    						new String[] { itemDescriptorName }));
    			}

    			if (isLoggingDebug()) {
    				logDebug("templateMapper=" + templateMapper);
    			}

    			template = getTemplate(pRequest, templateMapper);

    			if (template == null) {
    				pRequest.serviceLocalParameter(S_EMPTY, pRequest, pResponse);
    			} else {
    				if (isLoggingDebug()) {
    					logDebug("template path is <" + template.getAbsoluteName()
    							+ ">");
    					logDebug("template=" + template.getUrlTemplateFormat());
    				}

    				params = template.cloneUrlParameters();
    				
    				populateParams(pRequest, params, new UrlParameterLookup[] {
    						itemLookup, siteLookup });
    				//Adding code to check if we have got title for a guide or not .If not found we get it by making call to repository
    				if(itemDescriptorName.equalsIgnoreCase(BBBCoreConstants.GUIDES) && params[0].getPropertyName().equalsIgnoreCase(BBBCoreConstants.TITLE) && BBBUtility.isEmpty(params[0].getValue())){
    					logDebug("Guide Link not set for " + params[1].getValue());
    					GuidesTemplateVO guideVO=this.getGuidesTemplateManager().getGuidesLongDescription(params[1].getValue());
    					if(guideVO != null){
    						params[0].setValue(guideVO.getTitle());
    					}
    				}
    				//code end
    				url = template.formatUrl(params, getDefaultWebApp());
                }
			}
            
            if (isLoggingDebug()) {
				logDebug("url=" + url);
			}

			pRequest.setParameter("url", url);
			pRequest.serviceLocalParameter(S_OUTPUT, pRequest, pResponse);
			
		} catch (ItemLinkException ure) {
			if (isLoggingError()) {
				logError(ure);
			}
			pRequest.setParameter("errorMessage", ure.getMessage());
			pRequest.serviceLocalParameter(S_ERROR, pRequest, pResponse);
		} catch (Throwable t) {
			if (isLoggingError()) {
				logError(t);
			}
			pRequest.setParameter("errorMessage",
					UserResourceLookup.getResource("UNEXPECTED_ERROR"));
			pRequest.serviceLocalParameter(S_ERROR, pRequest, pResponse);
		}

		if (!(isLoggingDebug()))
			return;
		logDebug("Leaving service method of ItemLink");
	}
}
