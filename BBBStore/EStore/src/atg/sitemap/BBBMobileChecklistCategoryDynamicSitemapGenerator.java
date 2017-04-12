/**
 * 
 */
package atg.sitemap;

import java.util.Map;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.utils.BBBUtility;

import atg.repository.RepositoryItem;

/**
 * 		   @author dgup26 
 *         
 *         Included for BBBJ : 1218 | DSK Auto generated Urls (C2/C3) ||
 *         Auto SiteMap Generation For Mobile Checklist PLP site map generation the
 *
 * 
 */
public class BBBMobileChecklistCategoryDynamicSitemapGenerator extends BBBChecklistCategoryDynamicSitemapGenerator {
	private Map<String, String> urlPrefixFromDomainMap;

	@Override
	protected String getSitemapURL(RepositoryItem pSite, SitemapGeneratorService pSitemapGeneratorService,
			String checklistSEOUrl){
		if (isLoggingDebug()) {
			logDebug("Start getSitemapURL with checklistSEOUrl " + checklistSEOUrl);
		}
		String finalURL = null;
		 String finalURLPrefix=null;
	
		try {
			if (pSite != null) {
				 finalURLPrefix = (String)getUrlPrefixFromDomainMap().get((String)pSite.getPropertyValue(BBBCoreConstants.ID));
			}

			if (!BBBUtility.isEmpty(finalURLPrefix)  && !BBBUtility.isEmpty(checklistSEOUrl) && checklistSEOUrl.startsWith(BBBCoreConstants.SLASH)) {

					finalURL = finalURLPrefix.concat(checklistSEOUrl).concat(BBBCoreConstants.SLASH);
					finalURL=finalURL.toLowerCase();
			}
			
		} catch (Exception e) {
			logError(new StringBuilder().append("exception caught: ").append(e.getMessage()).toString());
			if(isLoggingDebug()){
				logError(" Exception occured while getSitemapURL ",e);
			}
		}
		if (isLoggingDebug()) {
			logDebug("End getSitemapURL with finalURL " + finalURL);
		}
		return finalURL;
	
	
		
	}

	public Map<String, String> getUrlPrefixFromDomainMap() {
		return urlPrefixFromDomainMap;
	}
	

	public void setUrlPrefixFromDomainMap(Map<String, String> urlPrefixFromDomainMap) {
		this.urlPrefixFromDomainMap = urlPrefixFromDomainMap;
	}
	

}
