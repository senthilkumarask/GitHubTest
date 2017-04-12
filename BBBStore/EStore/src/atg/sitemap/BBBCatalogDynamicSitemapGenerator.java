/**
 * 
 */
package atg.sitemap;

import atg.commerce.sitemap.CatalogSitemapGenerator;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryView;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import com.bbb.commerce.catalog.BBBCatalogConstants;

/**
 * @author BBB
 * 
 */
public class BBBCatalogDynamicSitemapGenerator extends CatalogSitemapGenerator {
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
	
	@Override
	protected Query getQuery(Repository pRepository, String pItemDescriptorName) throws RepositoryException
	{
		RepositoryView view = pRepository.getView(pItemDescriptorName);
		QueryBuilder qb = view.getQueryBuilder();
		if (BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR.equalsIgnoreCase(pItemDescriptorName)){
			 final QueryExpression pProperty = qb.createPropertyQueryExpression(BBBCatalogConstants.DISABLE_CATEGORY_PROPERTY_NAME);
			 final QueryExpression pValue = qb.createConstantQueryExpression(Boolean.FALSE);
			 final Query query = qb.createComparisonQuery(pProperty, pValue, QueryBuilder.EQUALS);
			 return query;
		}
		return qb.createUnconstrainedQuery();
	}
	
}
