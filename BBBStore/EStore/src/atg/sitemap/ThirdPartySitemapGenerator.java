package atg.sitemap;

import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.seo.UrlParameter;
import atg.repository.seo.UrlParameterLookup;
import atg.repository.seo.UrlTemplate;

import com.bbb.config.manager.ConfigTemplateManager;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.utils.BBBUtility;

public class ThirdPartySitemapGenerator extends DynamicSitemapGenerator {
	
	public static final String SCENE7_URL = "scene7_url";
	public boolean scene7;
	private ConfigTemplateManager configTemplateManager = null;
	private String mSitemapFilePrefixString;
	/**
	 * @return the sitemapFilePrefixString
	 */
	public String getSitemapFilePrefixString() {
		return this.mSitemapFilePrefixString;
	}
	
	/**
	 * @param pSitemapFilePrefixString
	 *            the sitemapFilePrefixString to set
	 */
	public void setSitemapFilePrefixString(String pSitemapFilePrefixString) {
		this.mSitemapFilePrefixString = pSitemapFilePrefixString;
	}
	
	public boolean isScene7() {
		return this.scene7;
	}

	public void setScene7(boolean scene7) {
		this.scene7 = scene7;
	}

	public ConfigTemplateManager getConfigTemplateManager() {
		return this.configTemplateManager;
	}

	public void setConfigTemplateManager(ConfigTemplateManager configTemplateManager) {
		this.configTemplateManager = configTemplateManager;
		getConfigTemplateManager().setSiteMapType(BBBCoreConstants.SITE_MAP_TYPE_PRODUCT_IMAGE);
	}

	@Override
	public String getSitemapURL(RepositoryItem pRepositoryItem, RepositoryItem pSite, SitemapGeneratorService pSitemapGeneratorService) throws RepositoryException {
		UrlTemplate template = getTemplate();
		UrlParameter[] params = null;
		UrlParameterLookup[] lookups = null;

		UrlParameterLookup itemlookup = getUrlParameterLookup("item", pRepositoryItem);

		if (pSite != null) {
			UrlParameterLookup siteLookup = getUrlParameterLookup("site", pSite);
			lookups = new UrlParameterLookup[] { itemlookup, siteLookup };
		} else {
			lookups = new UrlParameterLookup[] { itemlookup };
		}
		params = template.cloneUrlParameters();
		populateUrlParameters(params, lookups);
		
		String formattedURL = null;
		try {
			formattedURL = template.formatUrl(params, getWebApp());
		} catch (Exception e) {
			logError("ThirdPartySitemapGenerator exception caught: " + e);
		}
		StringBuilder finalURL = new StringBuilder();
		
		// Start : R 2.2 Product Image SiteMap Generation 504-b : Add Additional parameters in URL
		if(isScene7()){
			String scene7_url = null;
			scene7_url = getConfigTemplateManager().getThirdPartyURL(SCENE7_URL, BBBCoreConstants.THIRD_PARTY_URL);
			if(BBBUtility.isNotEmpty(scene7_url) && BBBUtility.isNotEmpty(formattedURL)){
				if(isLoggingDebug()){
					logDebug("scene7_url : " + scene7_url);
				}
				if(formattedURL != null && formattedURL.indexOf("http") <=-1){
					//Added below changes as part of SSL implementation for Sitemap Generation
					scene7_url = BBBCoreConstants.HTTPS + BBBCoreConstants.COLON + scene7_url;
					finalURL.append(scene7_url);
					finalURL.append(formattedURL);
					finalURL.append("?");
				}else if(formattedURL != null){
					finalURL.append(formattedURL);
				}
			}
		}
		if(isLoggingDebug()){
			logDebug("finalURL : " + finalURL.toString());
		}
		if(BBBCoreConstants.SITE_MAP_TYPE_PRODUCT_IMAGE.equals(getSitemapFilePrefixString())){
			
			String repositoryItemId = pRepositoryItem.getRepositoryId();
			String repositoryItemDescriptor = pRepositoryItem.getItemDescriptor().getItemDescriptorName();
		
			return (finalURL + ";" + repositoryItemDescriptor + ";" + repositoryItemId).toString() + ";" + getSitemapFilePrefixString();
		}
		return finalURL.toString();
		
		// End : R 2.2 Product Image SiteMap Generation 504-b 
	}
	@Override
	protected void preGenerateSitemapUrls(
			SitemapGeneratorService pSitemapGeneratorService, String pSiteId) {
		if (getSitemapFilePrefixString() != null && pSiteId != null) {
			this.setSitemapFilePrefix(getSitemapFilePrefixString() + "_" + pSiteId);
			if (isLoggingDebug()) {
				logDebug("New siteMapFileName:::" + getSitemapFilePrefix());
			}
		}
		super.preGenerateSitemapUrls(pSitemapGeneratorService, pSiteId);
	}
}
