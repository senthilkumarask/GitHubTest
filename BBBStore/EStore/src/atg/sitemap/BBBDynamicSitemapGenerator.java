/**
 * 
 */
package atg.sitemap;

import java.util.Set;

import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

/**
 * @author vchan5
 * 
 */
public class BBBDynamicSitemapGenerator extends DynamicSitemapGenerator {
	private String mSitemapFilePrefixString;
	private String mSiteIdInRepository;

	/*
	 * Overridden method to filter the duplicates in the siteMapgeneration xml
	 * file (non-Javadoc)
	 * 
	 * @see atg.sitemap.DynamicSitemapGenerator#getSitemapURL(atg.repository.
	 * RepositoryItem, atg.repository.RepositoryItem,
	 * atg.sitemap.SitemapGeneratorService)
	 */
	@Override
	public String getSitemapURL(RepositoryItem pRepositoryItem,
			RepositoryItem pSite,
			SitemapGeneratorService pSitemapGeneratorService)
			throws RepositoryException {
		if (getSiteIdInRepository() != null && pSite!=null)
			if (pRepositoryItem.getPropertyValue(getSiteIdInRepository()) != null) {
				@SuppressWarnings("unchecked")
				Set<RepositoryItem> pageItem = (Set<RepositoryItem>) pRepositoryItem
						.getPropertyValue(getSiteIdInRepository());
				if (pageItem.contains(pSite)) {
					return super.getSitemapURL(pRepositoryItem, pSite,
							pSitemapGeneratorService);
				}

			}
		return null;
	}

	/**
	 * @return the sitemapFilePrefixString
	 */
	public String getSitemapFilePrefixString() {
		return mSitemapFilePrefixString;
	}

	/**
	 * @param pSitemapFilePrefixString
	 *            the sitemapFilePrefixString to set
	 */
	public void setSitemapFilePrefixString(String pSitemapFilePrefixString) {
		this.mSitemapFilePrefixString = pSitemapFilePrefixString;
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
	protected void preGenerateSitemapUrls(
			SitemapGeneratorService pSitemapGeneratorService, String pSiteId) {
		if (getSitemapFilePrefixString() != null && pSiteId != null) {
			this.setSitemapFilePrefix(getSitemapFilePrefixString() + "_"
					+ pSiteId);
			if (isLoggingDebug()) {
				logDebug("New siteMapFileName:::" + getSitemapFilePrefix());
			}
		}
		super.preGenerateSitemapUrls(pSitemapGeneratorService, pSiteId);
	}

	/**
	 * @return the siteIdInRepository
	 */
	public String getSiteIdInRepository() {
		return mSiteIdInRepository;
	}

	/**
	 * @param pSiteIdInRepository
	 *            the siteIdInRepository to set
	 */
	public void setSiteIdInRepository(String pSiteIdInRepository) {
		this.mSiteIdInRepository = pSiteIdInRepository;
	}
}
