package com.bbb.search.endeca.boosting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.bbb.commerce.catalog.BBBConfigTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.endeca.EndecaClient;
import com.bbb.search.endeca.EndecaQueryGenerator;
import com.bbb.search.endeca.EndecaSearch;
import com.bbb.search.endeca.constants.BBBEndecaConstants;
import com.bbb.search.endeca.tools.EndecaSearchTools;
import com.bbb.search.endeca.vo.SearchBoostingAlgorithmVO;
import com.bbb.utils.BBBUtility;
import com.endeca.navigation.ENEQuery;
import com.endeca.navigation.ENEQueryException;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.FieldList;
import com.endeca.navigation.UrlENEQuery;
import com.endeca.navigation.UrlENEQueryParseException;
import com.endeca.navigation.UrlGen;

import atg.repository.MutableRepository;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;

/**
 * This class fetches the boosted products for L2/L3/Brand pages based on the 
 * categoryId from the repository
 * BBBI-1421
 */
public class L2L3BrandBoostingStrategy extends BBBGenericService implements SearchBoostingStrategy{
	private static final String PRODUCT_LIST = "productList";
	private static final String L2L3_BRAND_BOOSTING_STRATEGY = "L2L3BrandBoostingStrategy";
	private EndecaSearchTools endecaSearchTools;
	private MutableRepository omnitureReportRepository;
	private Map<String,String> omnitureSiteNameMap;
	private EndecaQueryGenerator queryGenerator;
	private BBBConfigTools catalogTools;
	private EndecaClient endecaClient;
	private EndecaSearch endecaSearch;
	

	
	public BBBConfigTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(BBBConfigTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	public EndecaQueryGenerator getQueryGenerator() {
		return queryGenerator;
	}

	public void setQueryGenerator(EndecaQueryGenerator queryGenerator) {
		this.queryGenerator = queryGenerator;
	}

	public EndecaClient getEndecaClient() {
		return endecaClient;
	}

	public void setEndecaClient(EndecaClient endecaClient) {
		this.endecaClient = endecaClient;
	}

	public EndecaSearch getEndecaSearch() {
		return endecaSearch;
	}

	public void setEndecaSearch(EndecaSearch endecaSearch) {
		this.endecaSearch = endecaSearch;
	}

	/**
	 * @return the omnitureSiteNameMap
	 */
	public Map<String, String> getOmnitureSiteNameMap() {
		return omnitureSiteNameMap;
	}

	/**
	 * @param omnitureSiteNameMap the omnitureSiteNameMap to set
	 */
	public void setOmnitureSiteNameMap(Map<String, String> omnitureSiteNameMap) {
		this.omnitureSiteNameMap = omnitureSiteNameMap;
	}

	/**
	 * @return omnitureReportRepository
	 */
	public MutableRepository getOmnitureReportRepository() {
		return omnitureReportRepository;
	}

	/**
	 * @param omnitureReportRepository
	 */
	public void setOmnitureReportRepository(
			MutableRepository omnitureReportRepository) {
		this.omnitureReportRepository = omnitureReportRepository;
	}
	
	/**
	 * @return the endecaSearchTools
	 */
	public EndecaSearchTools getEndecaSearchTools() {
		return endecaSearchTools;
	}

	/**
	 * @param endecaSearchTools the endecaSearchTools to set
	 */
	public void setEndecaSearchTools(EndecaSearchTools endecaSearchTools) {
		this.endecaSearchTools = endecaSearchTools;
	}

	/**
	 * This method is used to get the L2| L3 Brand boosted products for OPB boosting
	 * strategy
	 * 
	 * @param pSearchQuery
	 * @param serachBoostingAlgorithm
	 * @return List<String>
	 */
	@Override
	public List<String> getBoostedProducts(SearchQuery pSearchQuery,
			SearchBoostingAlgorithmVO searchBoostingAlgorithm, boolean l2l3BrandBoostingEnabled) {
		BBBPerformanceMonitor.start(L2L3_BRAND_BOOSTING_STRATEGY + "_getBoostedProducts");
		List<String> omnitureProductList = new ArrayList<String>();
		boolean removeOOSBoostedProduct;
		if (pSearchQuery == null) {
			BBBPerformanceMonitor.end(L2L3_BRAND_BOOSTING_STRATEGY + "_getBoostedProducts");
			this.logDebug(L2L3_BRAND_BOOSTING_STRATEGY + " Enter :: getBoostedProducts, Search query is null");
			return omnitureProductList;
		}
		this.logDebug(L2L3_BRAND_BOOSTING_STRATEGY + " Enter :: getBoostedProducts, Search query: " + pSearchQuery.getQueryURL() + " and searchBoostingAlgorithm "+ searchBoostingAlgorithm);
		pSearchQuery.appendBoostingLogs("L2L3BrandBoostingStrategy, Search query "+pSearchQuery.getQueryURL() + " and searchBoostingAlgorithm "+ searchBoostingAlgorithm);
		List<String> productList = new ArrayList<String>();
		RepositoryItemDescriptor reportStatusDesc;
		
		String categoryId;
		String pSiteId = this.getOmnitureSiteNameMap().get(pSearchQuery.getSiteId().toUpperCase());
		
		if(pSearchQuery.getCatalogRef().get(BBBEndecaConstants.CATA_ID) != null){
			if(pSearchQuery.isFromBrandPage()){
				categoryId = pSearchQuery.getKeyWord();
			} else {
				categoryId = pSearchQuery.getCatalogRef().get(BBBEndecaConstants.CATA_ID);
				if(categoryId.contains(BBBCoreConstants.PLUS) || categoryId.contains(BBBCoreConstants.SPACE)){
					if(pSearchQuery.getCatalogRef().get(BBBEndecaConstants.CATA_REF_ID) != null){
						categoryId = pSearchQuery.getCatalogRef().get(BBBEndecaConstants.CATA_REF_ID);
					} else {
						this.logDebug(L2L3_BRAND_BOOSTING_STRATEGY + " - categoryId with plus is :"+ categoryId+"and no catalogRefId found.");
						this.logDebug(L2L3_BRAND_BOOSTING_STRATEGY + " Exit :: getBoostedProducts, product list is empty. No boosting performed" );
					    pSearchQuery.appendBoostingLogs("L2L3BrandBoostingStrategy Exit [no catalogRefId found]:: getBoostedProducts, product list is empty. No boosting performed" );
						BBBPerformanceMonitor.end(L2L3_BRAND_BOOSTING_STRATEGY + "_getBoostedProducts");
					return omnitureProductList;
					}
				}
			}
			this.logDebug(L2L3_BRAND_BOOSTING_STRATEGY + "[getBoostedProducts] categoryId is : " + categoryId);
		try {	
			reportStatusDesc = this.getOmnitureReportRepository().getItemDescriptor(BBBEndecaConstants.REPORT_DATA_FOR_L2_L3_BRAND);
			RepositoryView view=reportStatusDesc.getRepositoryView();
			QueryBuilder userBuilder = view.getQueryBuilder();
			QueryExpression propertyName_identifier = userBuilder.createPropertyQueryExpression(BBBEndecaConstants.IDENTIFIER);
			QueryExpression identifier = userBuilder.createConstantQueryExpression(categoryId);
			Query fetchProducts_identifier = userBuilder.createComparisonQuery(propertyName_identifier, identifier, QueryBuilder.EQUALS);
			QueryExpression propertyName_concept = userBuilder.createPropertyQueryExpression(BBBCoreConstants.CONCEPT);
			QueryExpression concept = userBuilder.createConstantQueryExpression(pSiteId);
			Query fetchProducts_concept = userBuilder.createComparisonQuery(propertyName_concept, concept, QueryBuilder.EQUALS);
			Query[] fetchproductsQuery = {fetchProducts_identifier,fetchProducts_concept};
			Query fetchproducts = userBuilder.createAndQuery(fetchproductsQuery);
			RepositoryItem[] products = view.executeQuery(fetchproducts);
			String productString=null;
			if(products != null){
				for(RepositoryItem product :products){
				productString = (String)product.getPropertyValue(PRODUCT_LIST);
				}
			}
			if(!BBBUtility.isEmpty(productString) && productString.contains(BBBCoreConstants.COMMA)) {
				productList = Arrays.asList(productString.split(BBBCoreConstants.COMMA));
			} else if (!BBBUtility.isEmpty(productString) && !productString.contains(BBBCoreConstants.COMMA)) {
				productList.add(productString);
			}
				
		}
		catch (RepositoryException e){
			this.logError("RepositoryException in L2|L3| Brand getBoostedProducts method" +e);
		}
		}
		else {
			this.logError("No CategoryId found hence Repostitory call not executed and no boosting performed!");
		}
		
		omnitureProductList = productList;
		
		this.logDebug(L2L3_BRAND_BOOSTING_STRATEGY + " Exit :: getBoostedProducts, product list " + omnitureProductList);
		
		pSearchQuery.appendBoostingLogs("L2L3BrandBoostingStrategy :: getBoostedProducts, product list " + omnitureProductList );
		
		//filter OOS products from opbProductList based on config key values for category and brand
		if(pSearchQuery.isFromBrandPage()) {
			removeOOSBoostedProduct =  Boolean.valueOf(getCatalogTools().getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.REMOVE_OOS_BOOSTED_PRODUCT_BRAND, BBBCoreConstants.FALSE));
		} else {
			removeOOSBoostedProduct =  Boolean.valueOf(getCatalogTools().getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.REMOVE_OOS_BOOSTED_PRODUCT_CATEGORY, BBBCoreConstants.FALSE));
		}
		if(removeOOSBoostedProduct) {
			return filterInStockOpbProducts(pSearchQuery, omnitureProductList);
		}
		BBBPerformanceMonitor.end(L2L3_BRAND_BOOSTING_STRATEGY + "_getBoostedProducts");
		return omnitureProductList;
	}
	
	/**
	 * This method is used to get in stock boosted products for OPB boosting
	 * strategy
	 * 
	 * @param pSearchQuery
	 * @param opbProductList
	 * @return List<String>
	 */
	private List<String> filterInStockOpbProducts(SearchQuery pSearchQuery, List<String> opbProductList){

		this.logDebug("Enter :: L2L3BrandBoostingStrategy:filterInStockOpbProducts");
		if (opbProductList.isEmpty()) {
			this.logDebug("Exit :: L2L3BrandBoostingStrategy:filterInStockOpbProducts, product list is empty");
			return opbProductList;
		}
		List<String> inStockOmnitureProductList = new ArrayList<>();
		List<String> cacheOmnitureProductList = new ArrayList<>();
		try {
			String recordType = getEndecaSearch().getCatalogId(BBBEndecaConstants.RECORD_TYPE,BBBEndecaConstants.PROD_RECORD_TYPE);
			String navInventoryStatusId = getEndecaSearch().getCatalogId(BBBEndecaConstants.INVENTORY_STATUS,BBBEndecaConstants.POSITIVE);
			String navSiteId = getEndecaSearch().getCatalogId(BBBEndecaConstants.SITE_ID,getEndecaSearch().getSiteIdMap().get(pSearchQuery.getSiteId()));
			String navigationUrl=recordType +BBBCoreConstants.SPACE + navInventoryStatusId + BBBCoreConstants.SPACE + navSiteId;
			StringBuilder ntt= new StringBuilder();
			
			for (String opbProductId : opbProductList) {
				ntt.append(opbProductId+BBBCoreConstants.SPACE);
			}
			
			final UrlGen econdUrlGen = new UrlGen(null, getEndecaClient().getEncoding());
			econdUrlGen.addParam(BBBEndecaConstants.NAVIGATION, navigationUrl);
			econdUrlGen.addParam(BBBEndecaConstants.NAV_KEYWORD, ntt.substring(0, ntt.length()-1));
			econdUrlGen.addParam(BBBEndecaConstants.NAV_SEARCH_MODE, BBBEndecaConstants.SEARCH_MODE_MATCHANY);
			econdUrlGen.addParam(BBBEndecaConstants.NAV_PROPERTY_NAME, BBBEndecaConstants.ALL);
			String queryString = econdUrlGen.toString();
			this.logDebug("filter in stock OPB products search query : "+queryString);
			boolean isCacheEnabled = Boolean.parseBoolean(getCatalogTools().getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBEndecaConstants.OPB_BOOST_IN_STOCK_CACHE_ENABLED, BBBCoreConstants.FALSE));
			if (isCacheEnabled ) {
				cacheOmnitureProductList = getEndecaSearchTools().getBoostedProductListFromCache(queryString);
			}
			if (isCacheEnabled && cacheOmnitureProductList != null) {
				pSearchQuery.appendBoostingLogs("L2L3BrandBoostingStrategy :: filterInStockopbProducts, in stock OPB product list " + cacheOmnitureProductList );
				this.logDebug("Exit :: L2L3BrandBoostingStrategy:filterInStockOpbProducts, product list from cache: " + cacheOmnitureProductList);
				return cacheOmnitureProductList;
			} else {
			ENEQuery nbpQueryObject = new UrlENEQuery(queryString, getQueryGenerator().getEncoding());
			FieldList fList = new FieldList();
			fList.addField(BBBEndecaConstants.FEATURED_PRODUCT_ID);
			nbpQueryObject.setSelection(fList);
			ENEQueryResults opbResults = getEndecaClient().executeQuery(nbpQueryObject);
			inStockOmnitureProductList = getEndecaSearchTools().getBoostedProductList(opbResults, inStockOmnitureProductList,pSearchQuery);
			
			if (isCacheEnabled && inStockOmnitureProductList != null) {
				getEndecaSearchTools().insertBoostedProductsIntoCache(queryString, inStockOmnitureProductList);
				this.logDebug("OPB_BOOSTING_STRATEGY, InStockOPBQuery Result added in cache:"+ inStockOmnitureProductList);
			}
			this.logDebug("Exit :: L2L3BrandBoostingStrategy:filterInStockOpbProducts, product list from Endeca: " + inStockOmnitureProductList);
		}
		
		pSearchQuery.appendBoostingLogs("L2L3BrandBoostingStrategy :: filterInStockopbProducts, in stock OPB product list " + inStockOmnitureProductList );
			
		} catch (BBBBusinessException e) {
			logError("Business Exception while fetching dimension id ",e);
		} catch (BBBSystemException e) {
			logError("System Exception while fetching dimension id ",e);
		} catch (UrlENEQueryParseException e) {
			logError("UrlENEQueryParseException while fetching in stock omniture boosted products ",e);
		} catch (ENEQueryException e) {
			logError("ENEQueryException while fetching in stock omniture boosted products ",e);
		}
		return inStockOmnitureProductList;
	}
	
}
