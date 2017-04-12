package atg.sitemap;

import java.util.Map;

import atg.repository.RepositoryItem;
import atg.repository.seo.ItemLinkException;
import atg.repository.seo.UrlParameter;
import atg.repository.seo.UrlParameterLookup;
import atg.repository.seo.UrlTemplate;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.utils.BBBUtility;

/**
 * Extends BBBCatalogDynamicSitemapGenerator and will be used for generating
 * Mobile site maps for Product, Category and Brand URLs. OOB ATG
 * CatalogDynamicSitemapGenerator class uses the domain configured in ATG site
 * repository to create site maps. Since Mobile channel uses the same ATG site
 * used by desktop channel, we need a different source for reading domain names
 * for Mobile site maps. This class overrides the OOB method which generates the
 * site map url, to read the domain prefix from a component property instead of
 * site repository.
 * 
 * @author ssha53
 * @version %I%, %G%
 */
public class BBBMobileCatalogDynamicSitemapGenerator extends BBBCatalogDynamicSitemapGenerator {
	
	/** The url prefix from domain map. */
	private Map<String, String> mUrlPrefixFromDomainMap;
	

	/**
	 * This method is used to create sitemap URL for input catalog repository
	 * item. Its generates URL using direct/indirect URL template specified in
	 * components template property. It prefixes the domain name read from local
	 * property
	 * 
	 * @param pRepositoryItem
	 *            Catalog repository item
	 * @param pSitemapGeneratorService
	 *            Mobile Sitemap Generator service that holds global settings
	 * @param pSite
	 *            Site repository item
	 * @return Sitemap URL for a given repository item in <code>String</code>
	 *         format
	 * @throws None
	 */
	public String getSitemapURL(final RepositoryItem pRepositoryItem, final RepositoryItem pSite, 
			final SitemapGeneratorService pSitemapGeneratorService){
		if(this.isLoggingDebug()){
			this.logDebug("BBBMobileCatalogDynamicSitemapGenerator.getSitemapURL() - start");
		}
		
		// Read indirect URL remplate from component property. There will be
		// three templates, for Product, Category and Brand
		final UrlTemplate template = getTemplate();
		UrlParameter[] params = null;
		
		// parameter in a UrlTemplate
		UrlParameterLookup[] lookups = null;

		//get lookup for URL parameters with item name specified repository item. 
		final UrlParameterLookup itemlookup = getUrlParameterLookup(BBBCoreConstants.ITEM, pRepositoryItem);

		if (pSite == null) {
			lookups = new UrlParameterLookup[] { itemlookup };
		} else {
			final UrlParameterLookup siteLookup = getUrlParameterLookup(BBBCoreConstants.SITE, pSite);
			lookups = new UrlParameterLookup[] { itemlookup, siteLookup };
		}

		params = template.cloneUrlParameters();
		populateUrlParameters(params, lookups);
		
		// Read Mobile domain for given site id
		String finalURLPrefix = null; 
		if(null!=pSite){
			finalURLPrefix =  getUrlPrefixFromDomainMap().get((String)pSite.getPropertyValue(BBBCoreConstants.ID));
		}
		String  finalURL= "";
		try {
			// If final url is not NULL use template to format URL and append
			// final URL as prefix.
				final String formatedURL = template.formatUrl(params, null);
				
				// Concatenate domain name to the URL generated from URL
				// template
				if(null!=finalURLPrefix && !BBBUtility.isEmpty(formatedURL)){
					finalURL = finalURLPrefix.concat(formatedURL);
				}
		} catch (ItemLinkException e) {
			// In case of exceptions, log error and skip to the next item. Item
			// type and item id should be logged for debugging
			logError("BBBMobileCatalogDynamicSitemapGenerator.getSitemapURL() :: ", e);
		}
		
		if(this.isLoggingDebug()){
			this.logDebug("BBBMobileCatalogDynamicSitemapGenerator.getSitemapURL() - end");
		}
		return finalURL;
	}

	
	/**
	 * This method returns <code>Map</code> contains site Id as key and mobile
	 * site domain as value
	 * 
	 * @return the urlPrefixFromDomainMap in <code>Map<String, String></code>
	 *         format
	 */
	public Map<String, String> getUrlPrefixFromDomainMap() {
		return mUrlPrefixFromDomainMap;
	}

	
	/**
	 * This method sets site Id to mobile site domain mapping from component
	 * properties file
	 * 
	 * @param pUrlPrefixFromDomainMap
	 *            the urlPrefixFromDomainMap to set
	 */
	public void setUrlPrefixFromDomainMap(final Map<String, String> pUrlPrefixFromDomainMap) {
		mUrlPrefixFromDomainMap = pUrlPrefixFromDomainMap;
	}

}
