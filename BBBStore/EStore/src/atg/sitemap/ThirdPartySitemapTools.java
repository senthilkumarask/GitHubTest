package atg.sitemap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import atg.adapter.gsa.GSARepository;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.multisite.SiteManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.config.manager.ConfigTemplateManager;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

/**
 * @author ugoel
 *
 */
public class ThirdPartySitemapTools extends SitemapTools {
	
	private Map<String, String> siteMapNodesXMLMap;
	private Map<String, String> siteMapRepositoryMap;
	private Map<String, String> specialCharactersMap;

	private BBBCatalogTools catalogTools;
	private SiteManager siteManager = null;
	
	public static final String SCENE7_URL = "scene7_url";
	public boolean scene7;
	private ConfigTemplateManager configTemplateManager = null;
	
	/**
	 * Special Character Map To be Replaced
	 * @return
	 */
	public Map<String, String> getSpecialCharactersMap() {
		return specialCharactersMap;
	}

	/**
	 * @param specialCharactersMap
	 */
	public void setSpecialCharactersMap(Map<String, String> specialCharactersMap) {
		this.specialCharactersMap = specialCharactersMap;
	}
	
	public SiteManager getSiteManager() {
		return this.siteManager;
	}

	public void setSiteManager(final SiteManager siteManager) {
		this.siteManager = siteManager;
	}
	public boolean isScene7() {
		return this.scene7;
	}

	public void setScene7(final boolean scene7) {
		this.scene7 = scene7;
	}

	public ConfigTemplateManager getConfigTemplateManager() {
		return this.configTemplateManager;
	}

	public void setConfigTemplateManager(ConfigTemplateManager configTemplateManager) {
		this.configTemplateManager = configTemplateManager;
	}
	
	public Map<String, String> getSiteMapRepositoryMap() {
		return this.siteMapRepositoryMap;
	}
	public void setSiteMapRepositoryMap(Map<String, String> siteMapRepositoryMap) {
		this.siteMapRepositoryMap = siteMapRepositoryMap;
	}
	
	public Map<String, String> getSiteMapNodesXMLMap() {
		return this.siteMapNodesXMLMap;
	}
	public void setSiteMapNodesXMLMap(Map<String, String> siteMapNodesXMLMap) {
		this.siteMapNodesXMLMap = siteMapNodesXMLMap;
	}

	public BBBCatalogTools getCatalogTools() {
		return this.catalogTools;
	}
	public void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}


	/* (non-Javadoc)
	 * @see atg.sitemap.SitemapTools#appendSitemapHeader(java.lang.StringBuilder)
	 */
	@Override
	public void appendSitemapHeader(StringBuilder pSitemapXML) {
		String siteMapType = null;
		siteMapType = getConfigTemplateManager().getSiteMapType();
		if(siteMapType != null && siteMapType.equals((BBBCoreConstants.SITE_MAP_TYPE_PRODUCT_IMAGE).toString())){
			pSitemapXML.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" xmlns:image=\"http://www.google.com/schemas/sitemap-image/1.1\">");
		}else{
			pSitemapXML.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");
		}
		
	}

	@SuppressWarnings("boxing")
	@Override
	public String generateSitemapUrlXml(final String pUrl, final String pFrequency, final String pPriority, final boolean pDebugMode) {
		
		if (BBBUtility.isEmpty(pUrl) || pUrl.equalsIgnoreCase(BBBCoreConstants.NULL_VALUE)) {
			return "";
		}
		
		// Start : R 2.2 Product Image SiteMap Generation 504-b 
		String itemURL = "";
		String productURL = "";
		String productImageLOC = "";
		String productCaption = "";
		String productTitle = "";
		String repositoryItemDescriptior = "";
		String repositoryItemId = "";
		String siteMapType = "";
		String siteContextURL = "";	
		
		RepositoryItem repositoryItem = null;
		
		String siteIdFromContext = SiteContextManager.getCurrentSiteId();
		final String [] urlParts = StringUtils.splitStringAtString(pUrl, BBBCoreConstants.SEMICOLON);
		
		
		if(urlParts.length > 1){
			try {//Added below changes as part of SSL implementation for Sitemap Generation
				siteContextURL = BBBCoreConstants.HTTPS + BBBCoreConstants.CONSTANT_SLASH + (String) (getSiteManager().getSite(siteIdFromContext).getPropertyValue(BBBCoreConstants.PRODUCTION_URL));
			} catch (RepositoryException e) {
				logError(LogMessageFormatter.formatMessage(null , "RepositoryException - Error in fetching Site Context", BBBCoreErrorConstants.SITEMAP_GET_CONTEXT_EXCEPTION ), e);
			}
			productImageLOC = urlParts[0];
			itemURL = urlParts[0];
			repositoryItemDescriptior = urlParts[1];
			repositoryItemId = urlParts[2];
			siteMapType = urlParts[3];
		}else{
			itemURL = urlParts[0];
		}
		
		if (BBBCoreConstants.SITE_MAP_TYPE_PRODUCT_IMAGE.equals(siteMapType) && BBBUtility.isNotEmpty(siteContextURL)){
			
			final GSARepository mSourceRepository = (GSARepository) resolveName(getSiteMapRepositoryMap().get(siteMapType));
			
			try {
				if(BBBUtility.isNotEmpty(repositoryItemId) && BBBUtility.isNotEmpty(repositoryItemDescriptior)){
					repositoryItem = mSourceRepository.getItem(repositoryItemId, repositoryItemDescriptior);
				}
			} catch (RepositoryException e) {
				logError(LogMessageFormatter.formatMessage(null , "RepositoryException - Error in fetching Reposotory Item for Product : " + repositoryItemId, BBBCoreErrorConstants.SITEMAP_GET_PRODUCT_ITEM_EXCEPTION ), e);
			}
			
			
			ProductVO productVO = null;
			StringBuilder urlXml = new StringBuilder();
			String siteMapXMLFormat = getSiteMapNodesXMLMap().get(siteMapType);
			
			if(repositoryItem != null){
				productVO = getCatalogTools().getProductDetails(repositoryItem, siteIdFromContext);
			}
			
			// Check for Different type of products and populate Values
			
			if(repositoryItem != null && productVO != null){
				 if(productVO.getChildSKUs() != null && productVO.getChildSKUs().size() > 1l){
						
						if(BBBUtility.isNotEmpty(productVO.getSeoUrl())){
							productURL =  siteContextURL + productVO.getSeoUrl();
						}
						if(BBBUtility.isNotEmpty(productVO.getShortDescription())){
							productCaption = productVO.getShortDescription();
						}
						if(BBBUtility.isNotEmpty(productVO.getName())){
							productTitle = productVO.getName();
						}
						if(BBBUtility.isNotEmpty(productURL) && BBBUtility.isNotEmpty(productImageLOC)){
							
							siteMapXMLFormat = siteMapXMLFormat.replace("$imageLoc", productImageLOC);
							siteMapXMLFormat = siteMapXMLFormat.replace("$imageCaption", StringEscapeUtils.escapeXml(productCaption));
							siteMapXMLFormat = siteMapXMLFormat.replace("$imageTitle", StringEscapeUtils.escapeXml(productTitle));
							
							
							
							final List<String> childSKUList = new ArrayList<String>(productVO.getChildSKUs());
							StringBuilder urlXmlSKU = new StringBuilder();
							String scene7_url = null;
							scene7_url = getConfigTemplateManager().getThirdPartyURL(SCENE7_URL, BBBCoreConstants.THIRD_PARTY_URL);
							
							for(int i =0 ; i < productVO.getChildSKUs().size(); i++){
							try {
								final SKUDetailVO pSKUDetailVO = getCatalogTools().getSKUDetails(siteIdFromContext, false, childSKUList.get(i).toString());
								if(null != pSKUDetailVO){
									
									StringBuilder  skuImageLoc = new StringBuilder();
									String skuImageCaption = "";
									String skuImageTitle = "";
									String siteMapXMLFormatSKU = getSiteMapNodesXMLMap().get(siteMapType);
									if(BBBUtility.isNotEmpty(pSKUDetailVO.getSkuImages().getBasicImage())){
										if(isScene7()){
											if(isLoggingDebug()){
												logDebug("scene7_url : " + scene7_url);
											}
											if(pSKUDetailVO.getSkuImages().getBasicImage().indexOf("http") <=-1){
												//Added below changes as part of SSL implementation for Sitemap Generation
												skuImageLoc.append(BBBCoreConstants.HTTPS);
												skuImageLoc.append(BBBCoreConstants.COLON);
												skuImageLoc.append(scene7_url);
												skuImageLoc.append("/");
												skuImageLoc.append(pSKUDetailVO.getSkuImages().getBasicImage());
											}else{
												skuImageLoc.append(pSKUDetailVO.getSkuImages().getBasicImage());
											}
										}
										if(isScene7()){
											skuImageLoc.append("?");
										}
									}
									if(BBBUtility.isNotEmpty(pSKUDetailVO.getDescription())){
										skuImageCaption = pSKUDetailVO.getDescription();
									}
									if(BBBUtility.isNotEmpty(pSKUDetailVO.getDisplayName())){
										skuImageTitle = pSKUDetailVO.getDisplayName();
									}
									if(BBBUtility.isNotEmpty(skuImageLoc.toString())){
										siteMapXMLFormatSKU = siteMapXMLFormatSKU.replace("$imageLoc", skuImageLoc);
										siteMapXMLFormatSKU = siteMapXMLFormatSKU.replace("$imageCaption", StringEscapeUtils.escapeXml(skuImageCaption));
										siteMapXMLFormatSKU = siteMapXMLFormatSKU.replace("$imageTitle", StringEscapeUtils.escapeXml(skuImageTitle));
										urlXmlSKU.append(siteMapXMLFormatSKU);
									}
								}
							} catch (BBBSystemException e) {
								logError(LogMessageFormatter.formatMessage(null , "BBBSystemException - Error in fetching Reposotory Item for SKU : " + childSKUList.get(i).toString() , BBBCoreErrorConstants.SITEMAP_GET_SKU_ITEM_EXCEPTION ), e);
							} catch (BBBBusinessException e) {
								logError(LogMessageFormatter.formatMessage(null , "BBBSystemException - Error in fetching Reposotory Item for SKU: " + childSKUList.get(i).toString() , BBBCoreErrorConstants.SITEMAP_GET_SKU_ITEM_EXCEPTION ), e);
							}
							}
							urlXml.append("<url>");
							urlXml.append("<loc>");
							urlXml.append(productURL);
							urlXml.append("</loc>");
							urlXml.append(siteMapXMLFormat);
							urlXml.append(urlXmlSKU);
							urlXml.append("</url>");
						
					}
				}else {
					
					if(BBBUtility.isNotEmpty(productVO.getSeoUrl())){
						productURL =  siteContextURL +  productVO.getSeoUrl();
					}
					if(BBBUtility.isNotEmpty(productVO.getShortDescription())){
						productCaption = productVO.getShortDescription();
					}
					if(BBBUtility.isNotEmpty(productVO.getName())){
						productTitle = productVO.getName();
					}
					if(BBBUtility.isNotEmpty(productURL) && BBBUtility.isNotEmpty(productImageLOC)){
						
						siteMapXMLFormat = siteMapXMLFormat.replace("$imageLoc", productImageLOC);
						siteMapXMLFormat = siteMapXMLFormat.replace("$imageCaption", StringEscapeUtils.escapeXml(productCaption));
						siteMapXMLFormat = siteMapXMLFormat.replace("$imageTitle", StringEscapeUtils.escapeXml(productTitle));
						
						urlXml.append("<url>");
						urlXml.append("<loc>");
						urlXml.append(productURL);
						urlXml.append("</loc>");
						urlXml.append(siteMapXMLFormat);
						urlXml.append("</url>");
					}
					
				}
					
			}
			
			return urlXml.toString();
			
		}
		// End : R 2.2 Product Image SiteMap Generation 504-b 
		
		StringBuilder urlXml = new StringBuilder();
		String indent1 = "";
		String indent2 = "";
		if (pDebugMode) {
			indent1 = "\n\t";
			indent2 = "\n\t\t";
		}

		urlXml.append(indent1);
		urlXml.append("<url>");
		urlXml.append(indent2);
		urlXml.append("<loc>");
		urlXml.append(itemURL);
		urlXml.append(indent2);
		urlXml.append("</loc>");
		urlXml.append(indent2);
		urlXml.append("<changefreq>");
		urlXml.append(pFrequency);
		urlXml.append(indent2);
		urlXml.append("</changefreq>");
		urlXml.append(indent2);
		urlXml.append("<priority>");
		urlXml.append(pPriority);
		urlXml.append(indent2);
		urlXml.append("</priority>");
		urlXml.append(indent1);
		urlXml.append("</url>");

		return urlXml.toString();
		
	}
	
}
