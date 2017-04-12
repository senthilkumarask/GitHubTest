package atg.sitemap;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.utils.BBBUtility;

public class BBBStaticSitemapGenerator extends StaticSitemapGenerator {
	private int bbbSitemapCount;
	private Map<String, String> siteSpecificPages;

	public Map<String, String> getSiteSpecificPages() {
		return siteSpecificPages;
	}

	public void setSiteSpecificPages(Map<String, String> siteSpecificPages) {
		this.siteSpecificPages = siteSpecificPages;
	}

	/*
	 * Overridden method to generate the site specific urls of the site for site
	 * map xml (non-Javadoc)
	 * 
	 * @see atg.sitemap.StaticSitemapGenerator#generateSitemap(atg.sitemap.
	 * SitemapGeneratorService)
	 */
	@Override
	public void generateSitemap(SitemapGeneratorService pSitemapGeneratorService) {
		if (isLoggingDebug())
			logDebug("Start generating sitemap urls for static pages...");
		List<String> sites = pSitemapGeneratorService.getSites();
		List<String> pages = getResolvedStaticPages();
		bbbSitemapCount = 0;
		if (sites != null) {
			Iterator<String> i$ = sites.iterator();
			do {
				if (!i$.hasNext())
					break;
				String site = i$.next();
				if (getSiteSpecificPages() != null && !getSiteSpecificPages().isEmpty()) {
					String siteSpecificPagesList = getSiteSpecificPages().get(site);
					String[] listOfPages = null;
					if(BBBUtility.isNotEmpty(siteSpecificPagesList)){
						listOfPages = siteSpecificPagesList.split(BBBCoreConstants.COMMA);
					}
					if (listOfPages != null && listOfPages.length > 0) {
						pages.clear();
						for (String page : listOfPages) {
							pages.add(page);
						}
					}else{
						break;
					}
				} else {
					break;
				}
				setStaticPages(pages);
				List<String> siteSpecificPages = getResolvedStaticPages();
				generateSitemapUrls(pSitemapGeneratorService, siteSpecificPages, site);
				if (isLoggingDebug())
					logDebug((new StringBuilder()).append("Finish generating static sitemap urls for site: ").append(site).toString());
			} while (true);
		} else {
			generateSitemapUrls(pSitemapGeneratorService, pages, null);
		}
		if (isLoggingDebug())
			logDebug("Finish generating sitemap urls for static pages");
	}

	/*
	 * Overridden super class method to generate the site map urls with respect
	 * to site specific
	 * 
	 * @see atg.sitemap.StaticSitemapGenerator#generateSitemapUrls(atg.sitemap.
	 * SitemapGeneratorService, java.util.List, java.lang.String)
	 */
	@Override
	protected void generateSitemapUrls(SitemapGeneratorService pSitemapGeneratorService, List<String> pages, String pSiteId) {
		if (pages != null) {
			SitemapTools sitemapTools = pSitemapGeneratorService.getSitemapTools();
			StringBuilder si = new StringBuilder();
			sitemapTools.appendSitemapHeader(si);
			int urlCount = 0;
			bbbSitemapCount++;
			for (Iterator<String> i$ = pages.iterator(); i$.hasNext();) {
				String page = i$.next();
				if (isLoggingDebug())
					logDebug((new StringBuilder()).append("Next page: ").append(page).toString());
				String relativeUrl = getPageURL(page);
				String pageChangeFrequency = getChangeFrequencyForPage(page);
				String pagePriority = getPriorityForPage(page).toString();
				String fullUrl = null;
				if (pSiteId == null)
					fullUrl = getLocationForPage(relativeUrl);
				else
					fullUrl = getLocationForPage(relativeUrl, pSiteId, pSitemapGeneratorService);
				String urlXml = sitemapTools.generateSitemapUrlXml(fullUrl, pageChangeFrequency, pagePriority, pSitemapGeneratorService.isDebugMode());
				boolean newSitemapStarted = addURLToSitemap(pSitemapGeneratorService, si, urlXml, urlCount, bbbSitemapCount);
				if (newSitemapStarted) {
					urlCount = 0;
					bbbSitemapCount++;
				}
				urlCount++;
			}

			sitemapTools.appendSitemapFooter(si);
			sitemapTools.writeSitemap(si, getSitemapFilePrefix()+"_"+pSiteId, bbbSitemapCount);
		}
	}
}
