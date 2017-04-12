/**
 * 
 */
package com.bbb.search.endeca;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import atg.endeca.index.RecordStoreBulkLoaderImpl;
import atg.repository.MutableRepository;
import atg.repository.Query;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.ContextStatusEnum;
import atg.repository.search.indexing.DocumentSubmitterSession;
import atg.repository.search.indexing.IndexingException;
import atg.repository.search.indexing.IndexingOutputConfig;
import atg.repository.search.indexing.specifier.OutputItemSpecifier;
import atg.service.perfmonitor.PerformanceMonitor;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

/**
 * @author mmuneer
 *
 */
public class BBBRecordStoreBulkLoaderImpl extends RecordStoreBulkLoaderImpl {

	/**
	 * 
	 */
	public BBBRecordStoreBulkLoaderImpl() {
		//default constructor
	}

	private int skuFacetBatchSize;

	private String skuFacetRqlQuery;

	private String skuFacetValue;

	private List<String> exculdedL2Category;

	private BBBCatalogTools bbbCatalogTools;

	private MutableRepository catalogRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atg.repository.search.indexing.BulkLoaderImpl#loadPagedIteration(atg.
	 * repository.search.indexing.IndexingOutputConfig,
	 * atg.repository.search.indexing.Context, atg.repository.Query,
	 * atg.repository.search.indexing.DocumentSubmitterSession)
	 */
	@Override
	protected void loadPagedIteration(IndexingOutputConfig pOutputConfig, Context pContext, Query pQuery,
			DocumentSubmitterSession pDocSubSession) throws IndexingException, RepositoryException {

		if (pOutputConfig.getAbsoluteName().equalsIgnoreCase("/atg/commerce/search/SkuFeatureFacetOutputConfig")) {

			OutputItemSpecifier ois = pOutputConfig.getOutputItemSpecifier();
			if (isLoggingDebug()) {
				logDebug(new StringBuilder().append("OutputItemSpecifier: ").append(ois).toString());
				logDebug(new StringBuilder().append("itemDescriptor: ").append(ois.getItemDescriptor()).toString());
				logDebug(new StringBuilder().append("repositoryView: ")
						.append(ois.getItemDescriptor().getRepositoryView()).toString());
			}
			String rqlQuery = null;
			rqlQuery = getSkuFacetRqlQuery();

			Object[] params = new Object[1];
			params[0] = getSkuFacetValue();
			int totalCount = 0;
			int batchsize = getSkuFacetBatchSize();
			String rqlQueryRange = null;//rqlQuery + "RANGE " + totalCount + "+" + batchsize;
			RepositoryItem[] ritems = null;
			int count = batchsize;

			long startTimeNSecs = System.nanoTime();
			if (PerformanceMonitor.isEnabled()) {
				PerformanceMonitor.startOperation("Query Items to Bulk Index");
			}

			
				rqlQueryRange = rqlQuery;// + "RANGE " + totalCount + "+" + batchsize;

				if (ContextStatusEnum.CANCELING != pContext.getStatus()) {
					ritems = null;
					ritems = getSkuFacetItems(rqlQueryRange, ois.getItemDescriptor().getRepositoryView(), params);

					if (isLoggingInfo()) {
						long elapsedMSecs = (System.nanoTime() - startTimeNSecs) / 1000000L;
						double elapsedSecs = elapsedMSecs / 1000.0D;
						int processedCnt = ritems.length;
						double itemsPerSeconds = processedCnt / elapsedSecs;
						logInfo(new StringBuilder().append("Processed ").append(processedCnt).append(" items for ")
								.append(pOutputConfig.getAbsoluteName()).append(" in ").append(elapsedSecs)
								.append(" seconds.  Averaged ").append(itemsPerSeconds)
								.append(" items including sub-items per second").toString());

						startTimeNSecs = System.nanoTime();
					}
					loadIteration(pOutputConfig, pContext, pQuery, pDocSubSession, ritems);
					if (null != ritems) {
						count = ritems.length;
						totalCount = totalCount + count;
					}
				} else {

					logDebug("SkuFacet baselline get cancelled " + pContext.getStatus());
				}

			
			logDebug("Total count for skufacetFeatures   " + totalCount);
		} else {

			super.loadPagedIteration(pOutputConfig, pContext, pQuery, pDocSubSession);
		}

	}

	@Override
	public void processItem(Context pContext, RepositoryItem pItem, OutputItemSpecifier pSpecifier)
			throws IndexingException {

		if (pContext.getIndexingOutputConfig().getAbsoluteName()
				.equalsIgnoreCase("/atg/commerce/endeca/index/L2CategoryTypeAHeadOutputConfig")) {

			String categoryId = pItem.getRepositoryId();
			boolean isL2Categoryvalid = validateL2Category(categoryId);
			if (!isL2Categoryvalid) {
				return;
			}
			String siteIdFromRepoItem = getCurrentSiteId(pContext, pItem);
			if (null == siteIdFromRepoItem) {
				return;
			}
			Map<String, CategoryVO> parentCategoryMap = null;
			try {

				parentCategoryMap = getBbbCatalogTools().getParentCategory(categoryId, siteIdFromRepoItem.toString());
			} catch (BBBBusinessException e) {
				if (isLoggingError()) {
					logError("Error while fetching parent category - running baseline for L2Caegories " + e, e);
				}
			} catch (BBBSystemException e) {
				if (isLoggingError()) {
					logError("Error while fetching parent category - running baseline for L2Caegories " + e, e);
				}
			}
			if (parentCategoryMap.size() == 2) {
				// logDebug("its L2 category "+categoryId);
				super.processItem(pContext, pItem, pSpecifier);
			}
			/*
			 * else{ logDebug(
			 * "Ignoring L1 & L3 categories while running baseline index for L2Categories "
			 * +categoryId); }
			 */

		} else {
			super.processItem(pContext, pItem, pSpecifier);
		}

	}

	private String getCurrentSiteId(Context pContext, RepositoryItem pItem) {
		String[] siteIdsFromRepoItem = null;
		Collection<String> siteContextValues;
		try {
			siteContextValues = pItem.getContextMemberships();
			if ((siteContextValues != null) && (siteContextValues.size() > 0)) {
				Set<String> ids = new HashSet<String>(siteContextValues);
				ids.retainAll(pContext.getIndexInfo().getSiteIndexInfo().getSiteIDs());
				siteIdsFromRepoItem = (String[]) ids.toArray(new String[ids.size()]);
			}

		} catch (RepositoryException e) {
			if (isLoggingError()) {
				logError("Error while fetching siteId from context - running baseline for L2Caegories " + e, e);
			}
		}
		if (null == siteIdsFromRepoItem || siteIdsFromRepoItem.length == 0) {
			return null;
		}
		return siteIdsFromRepoItem[0];
	}

	private boolean validateL2Category(String categoryId) {

		if (getExculdedL2Category().contains(categoryId)) {
			return false;
		}

		try {
			RepositoryItem categoryRepositoryItem = getCatalogRepository().getItem(categoryId,
					BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR);
			if (categoryRepositoryItem.getPropertyValue(BBBCatalogConstants.PHANTOM_CATEGORY) != null) {
				Boolean isPhantomCategory = (Boolean) categoryRepositoryItem.getPropertyValue("phantomCategory");
				if (isPhantomCategory) {
					return false;
				}
			}
		} catch (RepositoryException e) {
			logError("Error while fetching phantom category - running baseline for L2Caegories " + e, e);
		}

		return true;
	}

	/**
	 * @param itemDescriptorName
	 * @param rqlQueryRange
	 * @param viewName
	 * @param params
	 * @return
	 */
	private RepositoryItem[] getSkuFacetItems(String rqlQueryRange, RepositoryView viewName, Object[] params) {
		RqlStatement statement = null;
		RepositoryItem[] queryResult = null;

		try {
			statement = RqlStatement.parseRqlStatement(rqlQueryRange);
			queryResult = statement.executeQuery(viewName, params);
			if (queryResult == null) {
				this.logDebug("No results returned for query [" + rqlQueryRange + "]");
			}

		} catch (final RepositoryException e) {
			if (this.isLoggingError()) {
				this.logError("catalog_1020 : Unable to retrieve data");
			}

		}
		return queryResult;
	}

	/**
	 * @return the skuFacetBatchSize
	 */
	public int getSkuFacetBatchSize() {
		return skuFacetBatchSize;
	}

	/**
	 * @param skuFacetBatchSize
	 *            the skuFacetBatchSize to set
	 */
	public void setSkuFacetBatchSize(int skuFacetBatchSize) {
		this.skuFacetBatchSize = skuFacetBatchSize;
	}

	/**
	 * @return the skuFacetRqlQuery
	 */
	public String getSkuFacetRqlQuery() {
		return skuFacetRqlQuery;
	}

	/**
	 * @param skuFacetRqlQuery
	 *            the skuFacetRqlQuery to set
	 */
	public void setSkuFacetRqlQuery(String skuFacetRqlQuery) {
		this.skuFacetRqlQuery = skuFacetRqlQuery;
	}

	/**
	 * @return the skuFacetValue
	 */
	public String getSkuFacetValue() {
		return skuFacetValue;
	}

	/**
	 * @param skuFacetValue
	 *            the skuFacetValue to set
	 */
	public void setSkuFacetValue(String skuFacetValue) {
		this.skuFacetValue = skuFacetValue;
	}

	/**
	 * @return the bbbCatalogTools
	 */
	public BBBCatalogTools getBbbCatalogTools() {
		return bbbCatalogTools;
	}

	/**
	 * @param bbbCatalogTools
	 *            the bbbCatalogTools to set
	 */
	public void setBbbCatalogTools(BBBCatalogTools bbbCatalogTools) {
		this.bbbCatalogTools = bbbCatalogTools;
	}

	/**
	 * @return the exculdedL2Category
	 */
	public List<String> getExculdedL2Category() {
		return exculdedL2Category;
	}

	/**
	 * @param exculdedL2Category
	 *            the exculdedL2Category to set
	 */
	public void setExculdedL2Category(List<String> exculdedL2Category) {
		this.exculdedL2Category = exculdedL2Category;
	}

	/**
	 * @return the catalogRepository
	 */
	public MutableRepository getCatalogRepository() {
		return catalogRepository;
	}

	/**
	 * @param catalogRepository
	 *            the catalogRepository to set
	 */
	public void setCatalogRepository(MutableRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}

}
