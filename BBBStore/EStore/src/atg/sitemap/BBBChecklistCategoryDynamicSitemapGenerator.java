/**
 * 
 */
package atg.sitemap;

import java.util.List;
import java.util.Map;

import atg.commerce.sitemap.CatalogSitemapGenerator;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.multisite.SiteContext;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.multisite.SiteManager;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.SortDirectives;

import com.bbb.commerce.checklist.tools.CheckListTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBUtility;

/**
 * 		   @author dgup26 
 *         
 *         Included for BBBJ : 1218 | DSK Auto generated Urls (C2/C3) ||
 *         Auto SiteMap Generation For Checklist PLP site map generation the
 *         priority is given to Admin UI entry of category urls . In case the
 *         urls are not entered then urls are generated as
 * 
 *         http://bedbathandbeyond.com/store/checklist/wedding/dinning/glassware
 *         /123456
 * 
 *         where 123456 is the leaf category id . The sitemap would be generated
 *         for BedBathUS,Canada and baby store sites.
 * 
 *         Sitemap would be generated for all the valid category urls based on
 *         is_deleted,is_disabled and is_configure_complete flags.
 *
 * 
 */
public class BBBChecklistCategoryDynamicSitemapGenerator extends CatalogSitemapGenerator {
	private String mSitemapFilePrefixString;
	private CheckListTools checkListTools;
	private int sitemapCount;
	private List<String> sitesIncludedForGeneration;
	private Map<String, String> siteToBBBSiteMap;
	private String CLS_NAME = "BBBChecklistCategoryDynamicSitemapGenerator";

	/*
	 * (non-Javadoc)
	 * 
	 * @see atg.sitemap.DynamicSitemapGenerator#generateSitemap(atg.sitemap.
	 * SitemapGeneratorService) Overridden to include the sitemap only for Store
	 * Sites : BedBathUS,BedBathCanada and BuyBuyBaby
	 */
	@Override
	public void generateSitemap(SitemapGeneratorService pSitemapGeneratorService) {
		if (isLoggingDebug()) {
			logDebug("Start generating sitemap urls for dynamic pages...");
		}
		BBBPerformanceMonitor.start(CLS_NAME, "generateSitemap");
		this.sitemapCount = 0;

		if (!BBBUtility.isListEmpty(getSitesIncludedForGeneration())) {
			for (String site : getSitesIncludedForGeneration()) {
				SiteContextManager scm = pSitemapGeneratorService.getSiteContextManager();
				SiteContext context = null;
				try {
					context = scm.getSiteContext(site);
					scm.pushSiteContext(scm.getSiteContext(site));

					preGenerateSitemapUrls(pSitemapGeneratorService, site);

					generateSitemapUrls(getItemDescriptorName(), pSitemapGeneratorService, site);
				} catch (SiteContextException ex) {
						logError("SiteContext Exception in generateSitemapUrls for  BBBChecklistCategoryDynamicSitemapGenerator "+ex);
					if(isLoggingDebug()){
						logError(" Exception occured while generateSitemapUrls ",ex);
					}
				} finally {
					scm.popSiteContext(context);
				}
			}
		}
		BBBPerformanceMonitor.end(CLS_NAME, "generateSitemap");
		return;
	}

	/*
	 * overriden method to append site ID in generated site map file
	 * (non-Javadoc)
	 * 
	 * @see
	 * atg.sitemap.DynamicSitemapGenerator#preGenerateSitemapUrls(atg.sitemap
	 * .SitemapGeneratorService, java.lang.String)
	 */
	@Override
	protected void preGenerateSitemapUrls(SitemapGeneratorService pSitemapGeneratorService, String pSiteId) {
		if (isLoggingDebug()) {
			logDebug("Start preGenerateSitemapUrls with sites : " + getSitesIncludedForGeneration());
		}
		BBBPerformanceMonitor.start(CLS_NAME, "preGenerateSitemapUrls");
		if (!BBBUtility.isListEmpty(getSitesIncludedForGeneration())) {
			if (getSitemapFilePrefixString() != null && pSiteId != null) {
				this.setSitemapFilePrefix(getSitemapFilePrefixString() + "_" + pSiteId);
				if (isLoggingDebug()) {
					logDebug("New siteMapFileName:::" + getSitemapFilePrefix());
				}
			}
			callSuperpreGenerateSitemapUrls(pSitemapGeneratorService, pSiteId);
		}
		BBBPerformanceMonitor.end(CLS_NAME, "preGenerateSitemapUrls");
	}

	/**
	 * @param pSitemapGeneratorService
	 * @param pSiteId
	 */
	protected void callSuperpreGenerateSitemapUrls(SitemapGeneratorService pSitemapGeneratorService, String pSiteId) {
		super.preGenerateSitemapUrls(pSitemapGeneratorService, pSiteId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atg.sitemap.DynamicSitemapGenerator#getSitemapURL(atg.repository.
	 * RepositoryItem, atg.repository.RepositoryItem,
	 * atg.sitemap.SitemapGeneratorService) Overridden to generate the prefix as
	 * /checklist/${checklistName}
	 */
	@Override
	public String getSitemapURL(RepositoryItem pRepositoryItem, RepositoryItem pSite,
			SitemapGeneratorService pSitemapGeneratorService) throws RepositoryException {
		if (isLoggingDebug()) {
			logDebug("Inside Class " + CLS_NAME +" : Method : getSitemapURL : Site Id : "+pSite );
		}
		BBBPerformanceMonitor.start(CLS_NAME, "getSitemapURL");
		String finalURL = null;
		String checklistName = null;
		String prefixSEOUrl = BBBCoreConstants.SLASH + BBBCoreConstants.CHECKLIST_URL_CONSTANT;
		if (pRepositoryItem != null) {
			if (pRepositoryItem.getPropertyValue(BBBCoreConstants.DISPLAY_NAME) != null) {
				checklistName = (String) pRepositoryItem.getPropertyValue(BBBCoreConstants.DISPLAY_NAME);
			}
		}
		prefixSEOUrl = prefixSEOUrl + BBBCoreConstants.SLASH + getCheckListTools().formatUrlParam(checklistName);

		finalURL = getSitemapURL(pSite, pSitemapGeneratorService, prefixSEOUrl);

		if (isLoggingDebug()) {
			logDebug("Inside Class " + CLS_NAME +" : finalURL : " + finalURL);
		}
		BBBPerformanceMonitor.end(CLS_NAME, "getSitemapURL");
		return finalURL;
	}

	/**
	 * @param pSite
	 * @param pSitemapGeneratorService
	 * @param checklistSEOUrl
	 * @return
	 */
	protected String getSitemapURL(RepositoryItem pSite, SitemapGeneratorService pSitemapGeneratorService,
			String checklistSEOUrl) {
		if (isLoggingDebug()) {
			logDebug("Start getSitemapURL with checklistSEOUrl " + checklistSEOUrl + " Site : " + pSite.getRepositoryId());
		}
		BBBPerformanceMonitor.start(CLS_NAME, "getSitemapURL");
		String finalURL = null;
		try {
			if (pSite != null) {
				finalURL = pSitemapGeneratorService.getSiteURLManager()
						.getProductionSiteBaseURL(pSite.getRepositoryId(), checklistSEOUrl, null, null, false);
			} else {
				finalURL = checklistSEOUrl;
			}

			if (!(isNeedServletPathOnSitemapURL())) {
				finalURL = cutServletPathFromSitemapURL(finalURL);
			}

			if (finalURL.startsWith(BBBCoreConstants.SLASH)) {
				StringBuilder url = new StringBuilder(finalURL);
				if ((getWebApp() != null) && (pSite == null)) {
					String contextRoot = getWebApp().getContextRoot();
					if (!(StringUtils.isEmpty(contextRoot))) {
						SitemapTools.addPrefixToUrl(contextRoot, url);
					}

				}

				if (getUrlPrefix() != null) {
					SitemapTools.addPrefixToUrl(getUrlPrefix(), url);
				}

				finalURL = SitemapTools.escapeURL(url.toString());
			}
		} catch (Exception e) {
			this.logError(new StringBuilder().append("exception caught: ").append(e).toString());
			if(isLoggingDebug()){
				logError(" Exception occured while getSitemapURL ",e);
			}
		}
		if (isLoggingDebug()) {
			logDebug("End getSitemapURL with finalURL " + finalURL);
		}
		BBBPerformanceMonitor.end(CLS_NAME, "getSitemapURL");
		return finalURL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atg.sitemap.DynamicSitemapGenerator#generateSitemapUrls(java.lang.String,
	 * atg.sitemap.SitemapGeneratorService, java.lang.String) Overridden to
	 * generate SEO urls for C1,c2,c3 mapping. Preference given to admin UI
	 * entered urls else generated through checklist hirerachy
	 * is_disabled,is_deleted and is_config_complete check for categories
	 * checked.
	 */
	@Override
	public void generateSitemapUrls(String pItemDescriptorName, SitemapGeneratorService pSitemapGeneratorService,
			String pSiteId) {
		if (isLoggingDebug()) {
			logDebug("Inside Class "+CLS_NAME+" Method : Starting generateSitemapUrls ");
		}
		BBBPerformanceMonitor.start(CLS_NAME, "generateSitemapUrls");
		Repository repository = getSourceRepository();
		SitemapTools sitemapTools = pSitemapGeneratorService.getSitemapTools();
		try {
			RepositoryView view = repository.getView(pItemDescriptorName);
			TransactionDemarcation td = new TransactionDemarcation();

			RepositoryItem site = null;
			if (pSiteId != null) {
				SiteManager siteManager = pSitemapGeneratorService.getSiteURLManager().getSiteManager();
				site = siteManager.getSite(pSiteId);
			}

			Query query = getQuery(repository, pItemDescriptorName, pSiteId);

			if (isLoggingDebug())
				logDebug(new StringBuilder().append("generateSitemapUrls: query obtained: ").append(query.toString())
						.toString());
			SortDirectives sortDirectives = getSortDirectives();

			boolean finished = false;
			int currentIndex = 0;

			StringBuilder si = new StringBuilder();
			sitemapTools.appendSitemapHeader(si);
			int urlCount = 0;
			this.sitemapCount += 1;

			while (!(finished)) {
				td.begin(getTransactionManager(), 4);
				if (isLoggingDebug())
					logDebug("generateSitemapUrls: begin transaction");
				RepositoryItem[] items = view.executeQuery(query, currentIndex,
						currentIndex + getNumberOfItemsPerTransaction(), sortDirectives);

				if ((items != null) && (items.length > 0)) {
					if (isLoggingDebug()) {
						logDebug(new StringBuilder().append("generateSitemapUrls:count of items extracted = ")
								.append(items.length).toString());
					}
					for (int i = 0; i < items.length; ++i) {
						if (items[i] != null) {
							// Fetching the checklist/${checklistName} for url
							// along with the context path
							String checklistSEOUrl = getSitemapURL(items[i], site, pSitemapGeneratorService);
							String checklistId = items[i].getRepositoryId();
							// Fetch child categories for checklist categories
							List<RepositoryItem> categoryItems = getCheckListTools()
									.childCategoriesForChecklist(items[i]);
							if (categoryItems != null) {
								for (int counter = 0; counter < categoryItems.size(); ++counter) {
									// generate SEO url recursively.
									generateSEOUrlForChildCategory(checklistSEOUrl, site, categoryItems.get(counter),
											urlCount, pSitemapGeneratorService, si,checklistId);
								}
							}
						}
					}

				} else {
					finished = true;
				}
				currentIndex += getNumberOfItemsPerTransaction();

				if (isLoggingDebug()) {
					logDebug("generateSitemapUrls:end of transaction ");
				}
				td.end();
			}

			sitemapTools.appendSitemapFooter(si);

			sitemapTools.writeSitemap(si, getSitemapFilePrefix(), this.sitemapCount);
		} catch (RepositoryException ex) {
				logError("Repository Exception in generateSitemapUrls for  BBBChecklistCategoryDynamicSitemapGenerator "+ex);
				if(isLoggingDebug()){
					logError("Repository Exception in generateSitemapUrls for  BBBChecklistCategoryDynamicSitemapGenerator ",ex);
				}
		} catch (TransactionDemarcationException ex) {
				logError("TransactionDemarcationException in generateSitemapUrls for  BBBChecklistCategoryDynamicSitemapGenerator "+ex);
				if(isLoggingDebug()){
					logError("TransactionDemarcationException in generateSitemapUrls for  BBBChecklistCategoryDynamicSitemapGenerator ",ex);
				}
		}
		BBBPerformanceMonitor.end(CLS_NAME, "generateSitemapUrls");
	}

	private Query getQuery(Repository pRepository, String pItemDescriptorName, String pSiteId)
			throws RepositoryException {
		if (isLoggingDebug()) {
			logDebug("Starting Method getQuery with site id : " + pSiteId);
		}
		BBBPerformanceMonitor.start(CLS_NAME, "getQuery");
		final String siteIdConstant = getSiteToBBBSiteMap().get(pSiteId);

		RepositoryView view = pRepository.getView(pItemDescriptorName);
		QueryBuilder qb = view.getQueryBuilder();
		QueryExpression querySiteId;
		QueryExpression querySiteIdConstant;
		Query queryForChecklist;
		querySiteId = qb.createPropertyQueryExpression(BBBCoreConstants.SITE_FLAG);
		querySiteIdConstant = qb.createConstantQueryExpression(siteIdConstant);
		queryForChecklist = qb.createPatternMatchQuery(querySiteId, querySiteIdConstant, QueryBuilder.CONTAINS);
		if (isLoggingDebug()) {
			logDebug("Exiting getQuery queryForChecklist : " + queryForChecklist);
		}
		BBBPerformanceMonitor.end(CLS_NAME, "getQuery");
		return queryForChecklist;
	}

	/**
	 * Generate site map url xm ls.
	 *
	 * @param checklistSEOUrl
	 *            the checklist seo url
	 * @param site
	 *            the site
	 * @param sitemapTools
	 *            the sitemap tools
	 * @param item
	 *            the item
	 * @param urlCount
	 *            the url count
	 * @param pSitemapGeneratorService
	 *            the sitemap generator service
	 * @param si
	 *            the si
	 * @throws RepositoryException
	 *             the repository exception
	 */
	private void generateSiteMapUrlXMLs(String checklistSEOUrl, RepositoryItem site, RepositoryItem item, int urlCount,
			SitemapGeneratorService pSitemapGeneratorService, StringBuilder si,String checklistId) throws RepositoryException {
		String urlXml = "";
		if (isLoggingDebug()) {
			logDebug("Starting generateSiteMapUrlXMLs : checklistId : " +checklistId);
		}
		BBBPerformanceMonitor.start(CLS_NAME, "generateSiteMapUrlXMLs");
		SitemapTools sitemapTools = pSitemapGeneratorService.getSitemapTools();
		if (site != null) {
			String formatUrl = BBBCoreConstants.BLANK;
			if (BBBCoreConstants.SITE_BAB_US.equalsIgnoreCase(site.getRepositoryId())
					&& item.getPropertyValue(BBBCoreConstants.CATEGORY_URL) != null) {
				// the urls that are entered by Admin UI will have domain name along
				formatUrl = (String) item.getPropertyValue(BBBCoreConstants.CATEGORY_URL);
			} else if ( BBBCoreConstants.SITE_BBB.equalsIgnoreCase(site.getRepositoryId())
					&& item.getPropertyValue(BBBCoreConstants.BABY_CATEGORY_URL) != null) {
				formatUrl = (String) item.getPropertyValue(BBBCoreConstants.BABY_CATEGORY_URL);
			} else if ( BBBCoreConstants.SITE_BAB_CA.equalsIgnoreCase(site.getRepositoryId())
					&& item.getPropertyValue(BBBCoreConstants.CA_CATEGORY_URL) != null) {
				formatUrl = (String) item.getPropertyValue(BBBCoreConstants.CA_CATEGORY_URL);
			} else {
				if (item.getPropertyValue(BBBCoreConstants.DISPLAY_NAME) != null) {
					String checklistName = (String) item.getPropertyValue(BBBCoreConstants.DISPLAY_NAME);
					formatUrl = formatUrl + getCheckListTools().formatUrlParam(checklistName);
				}
				if (!(BBBUtility.isEmpty(checklistSEOUrl))) {
					formatUrl = checklistSEOUrl.concat(formatUrl) + BBBCoreConstants.SLASH;
				}
				formatUrl = formatUrl.concat(item.getRepositoryId()).concat(BBBCoreConstants.SLASH);
				// Appending checklistId in the URL
				if (!(BBBUtility.isEmpty(checklistId))) {
					formatUrl = formatUrl.concat(checklistId) + BBBCoreConstants.SLASH;
				}
				formatUrl = formatUrl.toLowerCase();
			}

			urlXml = sitemapTools.generateSitemapUrlXml(formatUrl, getChangeFrequency(), getPriority().toString(),
					pSitemapGeneratorService.isDebugMode());
		} else {
			String url = getSitemapURL(item);
			if (!(BBBUtility.isEmpty(checklistSEOUrl))) {
				url = checklistSEOUrl.concat(url);
			}
			urlXml = sitemapTools.generateSitemapUrlXml(url, getChangeFrequency(), getPriority().toString(),
					pSitemapGeneratorService.isDebugMode());
		}

		if ((urlCount >= pSitemapGeneratorService.getMaxUrlsPerSitemap())
				|| (!(sitemapTools.validateSitemapSize(urlXml, si, pSitemapGeneratorService.getMaxSitemapSize())))) {
			sitemapTools.appendSitemapFooter(si);

			sitemapTools.writeSitemap(si, getSitemapFilePrefix(), this.sitemapCount);

			si = new StringBuilder();
			sitemapTools.appendSitemapHeader(si);
			urlCount = 0;
			this.sitemapCount += 1;
		}
		si.append(urlXml);
		++urlCount;
		if(isLoggingDebug()) {
			logDebug("Exiting generateSiteMapUrlXMLs Method");
		}
		BBBPerformanceMonitor.end(CLS_NAME, "generateSiteMapUrlXMLs");

	}

	/**
	 * @param checklistSEOUrl
	 * @param site
	 * @param categoryItem
	 * @param urlCount
	 * @param pSitemapGeneratorService
	 * @param si
	 * @throws RepositoryException
	 */
	protected void generateSEOUrlForChildCategory(String checklistSEOUrl, RepositoryItem site,
			RepositoryItem categoryItem, int urlCount, SitemapGeneratorService pSitemapGeneratorService,
			StringBuilder si,String checklistId) throws RepositoryException {
		if (categoryItem != null) {
			if (isLoggingDebug()) {
				logDebug("Starting generateSEOUrlForChildCategory for parent category "
						+ categoryItem.getRepositoryId());
			}
			String seoURLPrefix = checklistSEOUrl;
			// for each c1 category populate the url as
			// checklist/${checklistName}/${c1_name}/${c1_id}
			generateSiteMapUrlXMLs(seoURLPrefix, site, categoryItem, urlCount, pSitemapGeneratorService, si,checklistId);
			// Fetch child category items.
			List<RepositoryItem> childCategoryItems = getCheckListTools()
					.childCategoriesForChecklistCategory(categoryItem);

			if (childCategoryItems != null) {
				// append the parent category name.
				String checklistName = BBBCoreConstants.BLANK;
				
					if (categoryItem.getPropertyValue(BBBCoreConstants.DISPLAY_NAME) != null) {
						checklistName = (String) categoryItem.getPropertyValue(BBBCoreConstants.DISPLAY_NAME);
					}
				
				seoURLPrefix = seoURLPrefix + getCheckListTools().formatUrlParam(checklistName)
						+ BBBCoreConstants.SLASH;
				for (int counter = 0; counter < childCategoryItems.size(); ++counter) {
					// generate SEO URl for each child category
					generateSEOUrlForChildCategory(seoURLPrefix, site, childCategoryItems.get(counter), urlCount,
							pSitemapGeneratorService, si,checklistId);
				}
			}
			if (isLoggingDebug()) {
				logDebug(
						"Exiting generateSEOUrlForChildCategory for parent category " + categoryItem.getRepositoryId());
			}
		}
	}

	public String getSitemapFilePrefixString() {
		return this.mSitemapFilePrefixString;
	}

	public void setSitemapFilePrefixString(String pSitemapFilePrefixString) {
		this.mSitemapFilePrefixString = pSitemapFilePrefixString;
	}

	public CheckListTools getCheckListTools() {
		return checkListTools;
	}

	public void setCheckListTools(CheckListTools checkListTools) {
		this.checkListTools = checkListTools;
	}

	public List<String> getSitesIncludedForGeneration() {
		return sitesIncludedForGeneration;
	}

	public void setSitesIncludedForGeneration(List<String> sitesIncludedForGeneration) {
		this.sitesIncludedForGeneration = sitesIncludedForGeneration;
	}

	public Map<String, String> getSiteToBBBSiteMap() {
		return siteToBBBSiteMap;
	}

	public void setSiteToBBBSiteMap(Map<String, String> siteToBBBSiteMap) {
		this.siteToBBBSiteMap = siteToBBBSiteMap;
	}

}
