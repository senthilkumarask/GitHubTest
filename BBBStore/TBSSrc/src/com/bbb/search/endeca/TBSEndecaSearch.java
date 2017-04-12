package com.bbb.search.endeca;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import com.bbb.utils.BBBConfigRepoUtils;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.seo.ItemLinkException;
import atg.repository.seo.UrlParameter;
import atg.service.webappregistry.WebApp;

import com.bbb.commerce.catalog.vo.BrandVO;
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.bean.result.FacetParentVO;
import com.bbb.search.bean.result.FacetQuery;
import com.bbb.search.bean.result.FacetQueryResult;
import com.bbb.search.bean.result.FacetQueryResults;
import com.bbb.search.bean.result.SearchResults;
import com.bbb.search.endeca.constants.BBBEndecaConstants;
import com.bbb.search.endeca.vo.EndecaQueryVO;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;
/*import com.endeca.content.ContentException;
import com.endeca.content.ContentItem;
import com.endeca.content.ContentItemList;
import com.endeca.content.InitializationException;
import com.endeca.content.InvalidQueryException;
import com.endeca.content.ene.ENEContentManager;
import com.endeca.content.ene.ENEContentQuery;
import com.endeca.content.ene.ENEContentResults;
import com.endeca.content.ene.NavigationRecords;
import com.endeca.content.support.ContentItemListImpl;*/
import com.endeca.navigation.DimLocation;
import com.endeca.navigation.DimLocationList;
import com.endeca.navigation.DimVal;
import com.endeca.navigation.DimValList;
import com.endeca.navigation.DimensionSearchResult;
import com.endeca.navigation.DimensionSearchResultGroup;
import com.endeca.navigation.DimensionSearchResultGroupList;
import com.endeca.navigation.ENEQuery;
import com.endeca.navigation.ENEQueryException;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.ERec;
import com.endeca.navigation.ERecList;
import com.endeca.navigation.Navigation;
import com.endeca.navigation.PropertyMap;
import com.endeca.navigation.UrlENEQuery;
import com.endeca.navigation.UrlGen;

import atg.multisite.SiteContextManager;
import atg.repository.Repository;
import atg.repository.seo.ItemLinkException;
import atg.repository.seo.UrlParameter;
import atg.service.webappregistry.WebApp;

public class TBSEndecaSearch extends EndecaSearch {
	
	/**
	 * mCatalogRepository to hold CatalogRepository.
	 */
	private Repository mCatalogRepository;
	private int cacheTimeout; 
	
	public int getCacheTimeout() {
		return cacheTimeout;
	}

	public void setCacheTimeout(int cacheTimeout) {
		this.cacheTimeout = cacheTimeout;
	}

	private String mMieSearchInterface;
	
	/**
	 * @return the catalogRepository
	 */
	public Repository getCatalogRepository() {
		return mCatalogRepository;
	}

	/**
	 * @param pCatalogRepository the catalogRepository to set
	 */
	public void setCatalogRepository(Repository pCatalogRepository) {
		mCatalogRepository = pCatalogRepository;
	}

	/**
	 * @return the mieSearchInterface
	 */
	public String getMieSearchInterface() {
		return mMieSearchInterface;
	}

	/**
	 * @param pMieSearchInterface the mieSearchInterface to set
	 */
	public void setMieSearchInterface(String pMieSearchInterface) {
		mMieSearchInterface = pMieSearchInterface;
	}

	@Override
	public SearchResults performSearch(final SearchQuery pSearchQuery) throws BBBBusinessException,BBBSystemException {
		// Adjust siteID for Endeca since it will not recognize TBS_ sites
		if( pSearchQuery.getSiteId().equals("TBS_BedBathUS") ) {
			pSearchQuery.setSiteId("BedBathUS");
		}
		else if( pSearchQuery.getSiteId().equals("TBS_BuyBuyBaby") ) {
			pSearchQuery.setSiteId("BuyBuyBaby");			
		}
		else if( pSearchQuery.getSiteId().equals("TBS_BedBathCanada") ) {
			pSearchQuery.setSiteId("BedBathCanada");			
		}
		
		//ATG/Endeca upgrade fix - for fetching brands in topnav
		String searchTypeCheck = pSearchQuery.getCatalogRef().get("searchType");
		if(null!= searchTypeCheck && searchTypeCheck.equals("TopNav_Brand_Search")) {
			SearchResults searchResults = null;
			
			// Generate Endeca Query.
			final EndecaQueryVO endecaQueryVO = generateEndecaQuery(pSearchQuery);
			//	Fix for Defect 2257071  Added null check
			if (null != endecaQueryVO) {
	    			
					String cacheName = null;
					int cacheTimeout = 0;

					// feching search result from cache
	    			logDebug("cache key:" + endecaQueryVO.getQueryString());
					cacheName = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY , BBBCoreConstants.KEYWORD_SEARCH_CACHE_NAME);
					try {
						cacheTimeout = this.getCacheTimeout();
					} catch (NumberFormatException e) {
						logError("EndecaSearch.performSearch || NumberFormatException occured in " +
								GETTING_CACHE_TIMEOUT_FOR + BBBCoreConstants.KEYWORD_SEARCH_CACHE_TIMEOUT, e);
						cacheTimeout = getKeywordCacheTimeout();
					} catch (NullPointerException e) {
						logError("EndecaSearch.performSearch || NullPointerException occured in " +
								GETTING_CACHE_TIMEOUT_FOR + BBBCoreConstants.KEYWORD_SEARCH_CACHE_TIMEOUT, e);
						cacheTimeout = getKeywordCacheTimeout();
					}
					logDebug("cacheName:" + cacheName);
	    		//BBBSL-2389 Cache retrieval is by passed for staging server.	
	    		logDebug("Is Staging Server:: "+ isStagingServer() + " Is Header Search Request :: " + pSearchQuery.isHeaderSearch());
		    	if(isStagingServer() && pSearchQuery.isHeaderSearch()){
		    			logDebug("Request is for Staging Server thus skippping cache");
		    	}else{
	    			logDebug("Request is for Production Server");
	    			/*RM Defect 15480 Redirection Loop Fix*/ 
	    			if(pSearchQuery.isRedirected()){
	    				searchResults = (SearchResults) getObjectCache().get(endecaQueryVO.getQueryString()+BBBEndecaConstants.ISREDIRECTED, cacheName);
	    			}else{
	    				logDebug("query string"+endecaQueryVO.getQueryString());
	    				searchResults = (SearchResults) getObjectCache().get(endecaQueryVO.getQueryString(), cacheName);
	    			}
	    			/*RM Defect 15480 Redirection Loop Fix */
	    		}	
	    			
	    			if(searchResults == null){	// Actual Query Call to Endeca to obtain ENEQueryResults Object in case result not found in cache
	    				//fetch just facets and return SearchResults VO
	    				try {
		    				ENEQueryResults endecaQueryResults = getEndecaClient().executeQuery(new UrlENEQuery(endecaQueryVO.getQueryString(), getQueryGenerator().getEncoding()));
		    				List<FacetParentVO> facetList = getFacets(endecaQueryResults.getNavigation(), endecaQueryVO.getQueryString(), pSearchQuery,null,null);
		    				vlogDebug("FacetList from search result "+facetList);
		    				
		    				searchResults = new SearchResults();
		    				searchResults.setFacets(facetList);
		    				
		    				/*RM Defect 15480  Redirection Loop Fix */
							if(pSearchQuery.isRedirected()){
								if( null !=searchResults &&
										((null !=searchResults.getBbbProducts() &&
										searchResults.getBbbProducts().getBBBProductCount() > 0L)  || !(BBBUtility.isListEmpty(searchResults.getFacets())))){
								getObjectCache().put(endecaQueryVO.getQueryString()+BBBEndecaConstants.ISREDIRECTED, searchResults, cacheName, cacheTimeout);
								}
							}else{
								if( null !=searchResults &&
										((null !=searchResults.getBbbProducts() &&
										searchResults.getBbbProducts().getBBBProductCount() > 0L)  || !(BBBUtility.isListEmpty(searchResults.getFacets())))){
								getObjectCache().put(endecaQueryVO.getQueryString(), searchResults, cacheName, cacheTimeout);
								logDebug("search is Cached");
								}
							}
							/*RM Defect 15480  Redirection Loop Fix */
	    				} catch(ENEQueryException endecaException) {
	    					BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_ENDECA_1001, "ENEQueryException in calling TBSEndeca performSearch");
	    					BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, "performSearch");
	    					throw new BBBSystemException(BBBCoreErrorConstants.ERROR_ENDECA_1001,"ENEQuery   Exception ", endecaException);
	    				}
	    			} else {
	    				logDebug("Search result fetched from cache");
	    			}
			}
			//if searchResults still null return empty VO back
			if(searchResults == null){
				searchResults = new SearchResults();
			}
			return searchResults;
		}
		//ATG/Endeca upgrade fix

		return super.performSearch(pSearchQuery);
	}

	
	/**
	 * This method is overriden to override the SearchMode with UpcSearchMode for UPC
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public FacetQueryResults performFacetSearch(final FacetQuery pFacetQuery)  throws BBBBusinessException,BBBSystemException{

		vlogDebug("TBSEndecaSearch :: Entering EndecaSearch.performFacetSearch method.");

		final String methodName = BBBCoreConstants.ENDECA_FACET_SEARCH;
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);

		ENEQueryResults results = null;
		ENEQuery queryObject = null;
		//ENEContentQuery contentQuery = null;
		//ENEContentResults contentResults = null;
		final List<FacetQueryResult> pResults = new ArrayList<FacetQueryResult>();
		final FacetQueryResult pFacetQueryResult =new FacetQueryResult();
		final FacetQueryResult pFacetQueryResult1 =new FacetQueryResult();
		FacetQueryResults pFacetQueryResults = null;

		String brandFacetDisplayName = BBBConfigRepoUtils.getStringValue("DimDisplayConfig", "Brand");
		final UrlGen urlGen = new UrlGen(null, getEndecaClient().getEncoding());

		// Generate Dimension query
		// Request is for getAllBrands
		if(pFacetQuery.getKeyword() == null){
			pFacetQuery.setKeyword("");
		}

		urlGen.addParam(TBSConstants.DIM_QUERY_KEYWORD,pFacetQuery.getKeyword()+"*");
		urlGen.addParam(TBSConstants.DIM_QUERY_MODE, ((TBSEndecaQueryGenerator)getQueryGenerator()).getUpcSearchMode());
		urlGen.addParam(TBSConstants.DIM_REC_FILTER, getConfigUtil().getSiteConfig().get(pFacetQuery.getSiteId()));

		/* START R2.1 TypeAhead for Most Popular Keywords */

		if(pFacetQuery.isShowPopularTerms()){
			// Get the Dimension Value Id for current Site ID.
			final String typeAheadDimId = getCatalogId("Record Type", getConfigUtil().getRecordTypeNames().get("TypeAhead"));
			//	Fix for defect Id 19586
			if(BBBUtility.isNotEmpty(typeAheadDimId)){
			    urlGen.addParam(TBSConstants.NAVIGATION, typeAheadDimId + " " + getCatalogId("Site_ID",getSiteIdMap().get(pFacetQuery.getSiteId())));
			}
			else{
			    urlGen.addParam(TBSConstants.NAVIGATION, getCatalogId("Site_ID",getSiteIdMap().get(pFacetQuery.getSiteId())));
			}
			urlGen.addParam(TBSConstants.NAV_KEYWORD,pFacetQuery.getKeyword()+"*");
			urlGen.addParam(TBSConstants.NAV_PROPERTY_NAME, getMieSearchInterface());
			urlGen.addParam(TBSConstants.NAV_SEARCH_MODE,"rel+static(P_SearchHit,descending)");
		}
		/* END   R2.1 TypeAhead for Most Popular Keywords */

		// fetching the search result from cache
		vlogDebug("cache key:" + urlGen.toString());
		final String cacheName = getCatalogTools().getAllValuesForKey(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY , BBBCoreConstants.KEYWORD_SEARCH_CACHE_NAME).get(0);
		int cacheTimeout = 0;
		try {
			cacheTimeout = this.getCacheTimeout();
		} catch (NumberFormatException exception) {
			vlogError("EndecaSearch.performFacetSearch || NumberFormatException occured in " +
					TBSConstants.GETTING_CACHE_TIMEOUT_FOR + BBBCoreConstants.KEYWORD_SEARCH_CACHE_TIMEOUT, exception);
			cacheTimeout = getKeywordCacheTimeout();
		} catch (NullPointerException exception) {
			vlogError("EndecaSearch.performFacetSearch || NullPointerException occured in " +
					TBSConstants.GETTING_CACHE_TIMEOUT_FOR + BBBCoreConstants.KEYWORD_SEARCH_CACHE_TIMEOUT, exception);
			cacheTimeout = getKeywordCacheTimeout();
		}
		pFacetQueryResults = (FacetQueryResults) getObjectCache().get(urlGen.toString(), cacheName);
		vlogDebug("cache Name performFacetSearch:" + cacheName);

		if(pFacetQueryResults == null || pFacetQueryResults.getResults() == null || pFacetQueryResults.getResults().isEmpty()){

			try{
				pFacetQueryResults = new FacetQueryResults();
				// call endeca for fetching search result in case not there in cache
				vlogDebug("search results not available in cache hence fetching from Endeca");
				results = getEndecaClient().executeQuery(new UrlENEQuery(urlGen.toString(),getQueryGenerator().getEncoding()));

				/*ENEContentManager contentManager = new ENEContentManager();
				contentManager = new ENEContentManager();
				contentQuery = (ENEContentQuery) contentManager.createQuery();
				contentQuery.setRuleZone(TBSConstants.NAV_PAGE_ZONE);
				contentQuery.setENEQuery(queryObject);

				// Execute Endeca Query
				contentResults = getEndecaClient().executeQuery(contentQuery);
				results = contentResults.getENEQueryResults();*/

				vlogDebug("Endeca Query String : " + urlGen.toString());

			}
			catch(ENEQueryException e){
				BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_ENDECA_1001, "ENEQueryException in calling Endeca performFacetSearch");
				BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
				throw new BBBSystemException(BBBCoreErrorConstants.ERROR_ENDECA_1001,"Endeca Exception",e);
			}
			/*catch (InitializationException e) {
				BBBUtility.passErrToPage( BBBCoreErrorConstants.ERROR_ENDECA_1002, "InitializationException in calling Endeca performFacetSearch");
				BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
				throw new BBBSystemException(BBBCoreErrorConstants.ERROR_ENDECA_1002,"Endeca Exception",e);
			}
			catch (InvalidQueryException e) {
				BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_ENDECA_1003, "InvalidQueryException in calling Endeca performFacetSearch");
				BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
				throw new BBBSystemException(BBBCoreErrorConstants.ERROR_ENDECA_1003,"Endeca Exception",e);
			}
			catch (ContentException e) {
				BBBUtility.passErrToPage(BBBCoreErrorConstants.ERROR_ENDECA_1004, "ContentException in calling Endeca performFacetSearch");
				BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
				throw new BBBSystemException(BBBCoreErrorConstants.ERROR_ENDECA_1004,"Endeca Exception",e);
			}*/


			final DimensionSearchResult dimSearchResult = results.getDimensionSearch();
			DimensionSearchResultGroupList groups = null;
			groups = dimSearchResult.getResults();
			final ListIterator iterGroups = groups.listIterator();
			// Iterator for getting Results map.
			final Map<String,String> pMatches = new TreeMap<String, String>();
			final Map<String,String> pMatches1 = new TreeMap<String, String>();
			Map<String, String> dimensionMap = getConfigUtil().getDimensionMap();

			while (iterGroups.hasNext()) {
				final DimensionSearchResultGroup group = (DimensionSearchResultGroup) iterGroups.next();
				final DimValList roots = group.getRoots();
				final ListIterator iterRoots = roots.listIterator();
				while (iterRoots.hasNext()) {
					final DimVal root = (DimVal) iterRoots.next();
					final String site = root.getName();
					if(("DEPARTMENT".equals(dimensionMap.get(site))) || (brandFacetDisplayName.equals(dimensionMap.get(site)))){
						final ListIterator iterDimLocationLists = group.listIterator();
						while (iterDimLocationLists.hasNext()) {
							final DimLocationList dimLocationList = (DimLocationList) iterDimLocationLists.next();
							final ListIterator iterDimLocations = dimLocationList.listIterator();
							boolean flag = false;
							while (iterDimLocations.hasNext()) {
								final DimLocation dimLocation = (DimLocation) iterDimLocations.next();
								final DimValList ancestors = dimLocation.getAncestors();
								final ListIterator iterAncestors = ancestors.listIterator();
								String name;
								StringBuffer breadcrumb = null;
							
								while (iterAncestors.hasNext()) {
									final DimVal ancestor = (DimVal) iterAncestors.next();
									name = ancestor.getName();
									if(breadcrumb != null && breadcrumb.length() !=0 )
									{
										breadcrumb.append(" > " + name + " > ");
										flag = true;
									}
									else{
										breadcrumb = new StringBuffer(name);
									}
								}
								final DimVal dimVal = dimLocation.getDimValue();
								final long ID = dimVal.getId();
								name = dimVal.getName();

								if(breadcrumb != null && breadcrumb.length() !=0 ){
									if (flag){
										breadcrumb.append(name) ;
									}
									else{
										breadcrumb.append(" > ") ;
										breadcrumb.append(name) ;
									}
								}
								else{
									breadcrumb = new StringBuffer(name);
								}
								if("DEPARTMENT".equals(dimensionMap.get(site))){
									//PS-61408 | Adding phantom category check to hide phantom categories from Department suggestions
									boolean phantom=false;
									try {
										phantom = getCatalogTools().isPhantomCategory(String.valueOf(ID));
									} catch (BBBBusinessException e){
										logError("Exception in method EndecaSearch.performFacetSearch while checking phantom "+ID   );
									} catch (BBBSystemException e) {
										logError("Exception in method EndecaSearch.performFacetSearch ::" , e);
									}
									pFacetQueryResult.setFacetName("department");
									if(pMatches.size() < getMaxMatches()){
										if(!phantom){
											pMatches.put( String.valueOf(ID), breadcrumb.toString());
										}
									}
									pFacetQueryResult.setMatches(pMatches);
								}
								else if(brandFacetDisplayName.equals(dimensionMap.get(site))){
									pFacetQueryResult1.setFacetName("brand");
									if(pMatches1.size() < getMaxMatches()){
										pMatches1.put( String.valueOf(ID), breadcrumb.toString());
									}
									pFacetQueryResult1.setMatches(pMatches1);
								}
							}
						}
					}
				}
			}
			
			pFacetQueryResults.setFacetQuery(pFacetQuery);

			pResults.add(pFacetQueryResult);
			pResults.add(pFacetQueryResult1);

			/* START R2.1 TypeAhead for Most Popular Keywords */

			if(pFacetQuery.isShowPopularTerms() && results.containsNavigation()){
					// Call process Response method to retrieve Search Results object.
					final Navigation nav= results.getNavigation();
					final ERecList records = nav.getERecs();
					ERec record = null;
					PropertyMap properties = null;
					String match = null;
					final ListIterator iterRecords = records.listIterator();
					final FacetQueryResult pFacetQueryResult2 =new FacetQueryResult();
					final Map<String,String> matchTree = new TreeMap<String, String>();
					for (int i = 0; iterRecords.hasNext() && matchTree.size() < getMaxMatches(); i++) {
						pFacetQueryResult2.setFacetName("popular");
						record = (ERec) iterRecords.next();
						properties = record.getProperties();
						match = (String) properties.get(getConfigUtil().getPropertyMap().get("POPULAR_SEARCH_TERM"));
						matchTree.put(String.valueOf(i), match);
					}
					pFacetQueryResult2.setMatches(matchTree);
					pResults.add(pFacetQueryResult2);
			}

			/* END   R2.1 TypeAhead for Most Popular Keywords */

			pFacetQueryResults.setResults(pResults);

			if(pFacetQueryResults.getResults() != null && !pFacetQueryResults.getResults().isEmpty()){
				getObjectCache().put(urlGen.toString(), pFacetQueryResults, cacheName, cacheTimeout);
			}


		}else{
			logDebug("Search result fetched from cache");
		}

		BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);

		vlogDebug("TBSEndecaSearch :: Exiting EndecaSearch.performFacetSearch method.");
		
		return pFacetQueryResults;
	}
	
	
	/**
	 * This is to obtain the Result List from the Object.
	 * We are not getting product information from Endeca for UPC search,, so to populate the product information manually overriding this method.
	 * @param argNav
	 * @return BBBProductList
	 */
 	@SuppressWarnings({ "rawtypes", "unchecked" })
	/*public BBBProductList getProductList(Navigation argNav, String pColor, ENEContentResults pCoResults){

		vlogDebug("TBSEndecaSearch :: getProductList() START");

		ERecList records = null;
		ERec record = null;
		ListIterator iterRecords = null;
		PropertyMap properties = null;
		String productID = null;
		BBBProductList bbbProducts = new BBBProductList();
		BBBProduct bbbOthResult = null;
		BBBProduct bbbProduct = null;
		long productCount = 0L;
		Map<String, List<BBBProduct>> otherResults = new HashMap<String, List<BBBProduct>>();

		// Obtain total result count
		productCount = argNav.getTotalNumERecs();
		bbbProducts.setBBBProductCount(productCount);

		// pCoResults is gauranteed to be not null
		if(null != pCoResults){
		ContentItem contentItem = pCoResults.getContent();

			if(	null != contentItem
				&& null != contentItem.getProperty(getConfigUtil().getCatridgeNameMap().get("CENTER"))
				&& null != contentItem.getProperty(getConfigUtil().getCatridgeNameMap().get("CENTER")).getValue()){

				ContentItemList centerColumnItems = (ContentItemListImpl)contentItem.getProperty(getConfigUtil().getCatridgeNameMap().get("CENTER")).getValue();
				ContentItem cartridge = null;
				Iterator iter = centerColumnItems.iterator();
				boolean flag = true;
				boolean boostRecordFlag = false;

				while(iter.hasNext()){
					
					if(boostRecordFlag){
						break;
					}
					flag = false;
		     		cartridge = (ContentItem) iter.next();

		     		if(null == cartridge || null == cartridge.getTemplateId()){
		     			vlogDebug("Results List from Page Builder configuration not retrieved. -3 ");
						records = argNav.getERecs();
		     		} else{

		     			if (cartridge.getTemplateId().equalsIgnoreCase(getCatridgePropertyMap().get("RESULT_LIST"))
			     				|| cartridge.getTemplateId().equalsIgnoreCase(getCatridgePropertyMap().get("RESULT_LIST_ORIGINAL"))) {

			     			NavigationRecords navRecs = (NavigationRecords) cartridge.getProperty(getCatridgePropertyMap().get("NAV_RECORDS")).getValue();
			     			if(null == navRecs){
				 				vlogDebug("Results List from Page Builder configuration not retrieved. -1 ");
			     				records = argNav.getERecs();
				 			}
			     			else{
			     				vlogDebug("Results List from Page Builder configuration retrieved.");
				 				records = navRecs.getERecs();
				 				boostRecordFlag = true;
			     			}
			     		}
			     		else{
			     			vlogDebug("Results List from Page Builder configuration not retrieved. -2 ");
							records = argNav.getERecs();
						}

		     		}
				}
				if(flag){
					vlogDebug("Results List from Page Builder configuration not retrieved. -4 ");
					records = argNav.getERecs();
				}
 			}
			else{
				vlogDebug("Results List from Page Builder configuration not retrieved. -5 ");
				records = argNav.getERecs();
			}
		}else{
			vlogDebug("Results List from Page Builder configuration not retrieved. -5 ");
			records = argNav.getERecs();
		}
		// Put an iterator on the Obtained Result List.

			iterRecords = records.listIterator();
			SortedMap<Integer,String> attr = null;
			Map<String,SkuVO> pSkuSet = null;
			List<SkuVO> skuVOList= null;
			String skuId = null;
			RepositoryItem skuItem = null;
			Set<RepositoryItem> parentProducts = null;
			RepositoryItem productItem = null;
			
			while (iterRecords.hasNext()) {
				record = (ERec) iterRecords.next();
				properties = record.getProperties();
				productID = (String) properties.get(getPropertyMap().get("PRODUCT_ID"));
				
				bbbProduct = new BBBProduct();
				bbbProduct.setProductID(productID);
				bbbProduct.setProductName((String) properties.get(getPropertyMap().get("PRODUCT_TITLE")));
				bbbProduct.setSwatchFlag((String)properties.get(getPropertyMap().get("PRODUCT_SWATCH_FLAG")));

				bbbProduct.setHighPrice((String) properties.get(getPropertyMap().get("PRODUCT_HIGH_PRICE")));
				bbbProduct.setLowPrice((String) properties.get(getPropertyMap().get("PRODUCT_LOW_PRICE")));

				bbbProduct.setImageURL((String) properties.get(getPropertyMap().get("PRODUCT_IMAGE")));
				// Set the vertical Image URL for the product
				bbbProduct.setVerticalImageURL((String) properties.get(getPropertyMap().get("PRODUCT_VERTICAL_IMAGE")));

				bbbProduct.setPriceRange((String) properties.get(getPropertyMap().get("PRODUCT_PRICE_RANGE")));
				bbbProduct.setReviews((String)properties.get(getPropertyMap().get("PRODUCT_REVIEWS")));
				bbbProduct.setRatings((String)properties.get(getPropertyMap().get("PRODUCT_RATINGS")));
				bbbProduct.setDescription((String) properties.get(getPropertyMap().get("PRODUCT_DESC")));
				bbbProduct.setVideoId((String) properties.get(getPropertyMap().get("VIDEO_ID")));
				bbbProduct.setGuideId((String) properties.get(getPropertyMap().get("GUIDE_ID")));
				bbbProduct.setGuideTitle((String) properties.get(getPropertyMap().get("GUIDE_TITLE")));
				bbbProduct.setGuideImage((String) properties.get(getPropertyMap().get("GUIDE_IMAGE")));
				bbbProduct.setGuideAltText((String) properties.get(getPropertyMap().get("GUIDE_ALT_TEXT")));
				bbbProduct.setGuideShortDesc((String) properties.get(getPropertyMap().get("GUIDE_SHORT_DESC")));
				bbbProduct.setSeoUrl((String) properties.get(getPropertyMap().get("PRODUCT_SEO_URL")));

				if(BBBUtility.isNotEmpty((String) properties.get(getPropertyMap().get("PRODUCT_ON_SALE")))){
					bbbProduct.setOnSale((String) properties.get(getPropertyMap().get("PRODUCT_ON_SALE")));
				}


				logDebug("SEO Url for "+bbbProduct.getProductID()+" is "+bbbProduct.getSeoUrl());

				bbbProduct.setCollectionFlag(((String)properties.get(getPropertyMap().get("COLLECTION_PRODUCT"))));

				// added for R2-item 141
				if(("04").equalsIgnoreCase(((String)properties.get(getPropertyMap().get("PRODUCT_ROLLUP_TYPE_CODE"))))
	            	|| ("05").equalsIgnoreCase(((String)properties.get(getPropertyMap().get("PRODUCT_ROLLUP_TYPE_CODE"))))){
	            	bbbProduct.setRollupFlag(true);
	            }

				String otherCat = (String) properties.get(getPropertyMap().get("OTHER_RESULT_CATEGORY"));
				String otherTitle = (String) properties.get(getPropertyMap().get("OTHER_RESULT_TITLE"));
				String otherLink = (String) properties.get(getPropertyMap().get("OTHER_RESULT_LINK"));
				String otherPageType = (String) properties.get(getPropertyMap().get("OTHER_RESULT_PAGE_TYPE"));

				if(BBBUtility.isNotEmpty(otherCat)){
					if(otherResults.containsKey(otherCat)){
						bbbOthResult = new BBBProduct();
						bbbOthResult.setProductID(productID);
						bbbOthResult.setOthResLink(otherLink);
						bbbOthResult.setOthResTitle(otherTitle);
						bbbOthResult.setOtherPageType(otherPageType);
						otherResults.get(otherCat).add(bbbOthResult);
					}
					else if(!otherResults.containsKey(otherCat)){
						bbbOthResult = new BBBProduct();
						List<BBBProduct> othResList = new ArrayList<BBBProduct>();
						bbbOthResult.setProductID(productID);
						bbbOthResult.setOthResLink(otherLink);
						bbbOthResult.setOthResTitle(otherTitle);
						bbbOthResult.setOtherPageType(otherPageType);
						othResList.add(bbbOthResult);
						otherResults.put(otherCat, othResList);
					}
				}
				// Iterate through Multi valued property for single Result Item.
				final Iterator props = properties.entrySet().iterator();
				attr =new TreeMap<Integer,String>();
				pSkuSet = new HashMap<String,SkuVO>();
				SkuVO pSkuVO = null;
				skuVOList = new ArrayList<SkuVO>();
				// BBBSL-2187 : To Display Swatch in alphabetical order
				Map<String,SkuVO> colorSkuSet = new TreeMap<String, SkuVO>();

				while (props.hasNext()) {
		        	final Property prop = (Property)props.next();

		            // Product Attributes Property.
		        	if(getPropertyMap().get("PRODUCT_ATTR").equalsIgnoreCase(prop.getKey().toString())){
		        		JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(prop.getValue().toString());
		            	final DynaBean jSONResultbean = (DynaBean) JSONSerializer.toJava(jsonObject);
		        		DynaClass dynaClass = jSONResultbean.getDynaClass();
		        		DynaProperty dynaProp[] = dynaClass.getDynaProperties();
		        		List<String> propertyNames = new ArrayList<String>();
		        		for (int i = 0; i < dynaProp.length; i++) {
		        			String name = dynaProp[i].getName();
		        			propertyNames.add(name);
		        		}
		        		if(propertyNames.contains(getAttributePropmap().get("PRIORITY")) && propertyNames.contains(getAttributePropmap().get("PLACEHOLDER")) && propertyNames.contains(getAttributePropmap().get("DISPDESC")) ){
		        			String placeholder = (String) jSONResultbean.get(getAttributePropmap().get("PLACEHOLDER"));
		        			if(!("".equals(jSONResultbean.get(getAttributePropmap().get("PRIORITY")))) && placeholder.indexOf(getPlaceHolder())>=0 ){
		        			int priority = Integer.parseInt((String)jSONResultbean.get(getAttributePropmap().get("PRIORITY")));
		        				attr.put(priority, (String)jSONResultbean.get(getAttributePropmap().get("DISPDESC")));
		        			}
		        		}
		        	}
		            bbbProduct.setAttribute(attr);
		            // Product Swatch Image Info property.
		            if(getPropertyMap().get("PRODUCT_CHILD_PRODUCT").equalsIgnoreCase(prop.getKey().toString())){
		            	JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(prop.getValue().toString());
		            	boolean caPriceflag = false;
		            	if(prop.getValue().toString().contains("CA_IS_PRICE")){
		            		caPriceflag = true;
		            	}
		        		final DynaBean jSONResultbean = (DynaBean) JSONSerializer.toJava(jsonObject);
		        		DynaClass dynaClass = jSONResultbean.getDynaClass();
		        		DynaProperty dynaProp[] = dynaClass.getDynaProperties();
		        		List<String> propertyNames = new ArrayList<String>();
		        		for (int i = 0; i < dynaProp.length; i++) {
		        			String name = dynaProp[i].getName();
		        			propertyNames.add(name);
		        		}
		        		if(propertyNames.contains(getSwatchInfoMap().get("SKU_ID"))){
		        			pSkuVO = new SkuVO();
		        			pSkuVO.setSkuID((String) jSONResultbean.get(getSwatchInfoMap().get("SKU_ID")));
		        			if(propertyNames.contains(getSwatchInfoMap().get("SWATCH_IMAGE"))){
		        				pSkuVO.setSkuSwatchImageURL((String) jSONResultbean.get(getSwatchInfoMap().get("SWATCH_IMAGE")));
		        			}
		        			if(propertyNames.contains(getSwatchInfoMap().get("PRODUCT_IMAGE"))){
		        				pSkuVO.setSkuMedImageURL((String) jSONResultbean.get(getSwatchInfoMap().get("PRODUCT_IMAGE")));
		        			}
		        			if(propertyNames.contains(getSwatchInfoMap().get("COLOR"))){
		        				pSkuVO.setColor((String) jSONResultbean.get(getSwatchInfoMap().get("COLOR")));
		        			}
		        			if(propertyNames.contains(getSwatchInfoMap().get("PRODUCT_VERTICAL_IMAGE"))){
		        				pSkuVO.setSkuVerticalImageURL((String) jSONResultbean.get(getSwatchInfoMap().get("PRODUCT_VERTICAL_IMAGE")));
		        			}

		        			skuVOList.add(pSkuVO);
		        			pSkuSet.put(pSkuVO.getSkuID(), pSkuVO);
		        		}
		        		if(null != pColor){
		        			if(propertyNames.contains(getSwatchInfoMap().get("GROUP")) && propertyNames.contains(getSwatchInfoMap().get("PRODUCT_IMAGE"))){
				            	if(pColor.equalsIgnoreCase((String) jSONResultbean.get(getSwatchInfoMap().get("GROUP")))){
				            		// Setting image for a single color group selection.
				            		bbbProduct.setImageURL((String) jSONResultbean.get(getSwatchInfoMap().get("PRODUCT_IMAGE")));
				        		}
		        			}
		        			if(propertyNames.contains(getSwatchInfoMap().get("GROUP")) && propertyNames.contains(getSwatchInfoMap().get("PRODUCT_VERTICAL_IMAGE"))){
				            	if(pColor.equalsIgnoreCase((String) jSONResultbean.get(getSwatchInfoMap().get("GROUP")))){
				            		// Setting vertical image for a single color group selection.
				            		bbbProduct.setVerticalImageURL((String) jSONResultbean.get(getSwatchInfoMap().get("PRODUCT_VERTICAL_IMAGE")));
				        		}
		        			}
		        		}
		        		String siteID = SiteContextManager.getCurrentSiteId();
		        		if(caPriceflag && jSONResultbean.get("CA_IS_PRICE") != null && siteID.equals(TBSConstants.SITE_TBS_BAB_CA)){
		        			bbbProduct.setPriceRange((String)jSONResultbean.get("CA_IS_PRICE"));
		        		} else if(StringUtils.isBlank(productID)){
		        			bbbProduct.setPriceRange((String)jSONResultbean.get("SKU_PRICE"));
		        		}
	        		}
		            bbbProduct.setSkuSet(pSkuSet);
		          //if productId is not available then populating manually.
					if(StringUtils.isBlank(bbbProduct.getProductID()) && pSkuVO != null ){
						skuId = pSkuVO.getSkuID();
						
						// Map the Endeca properties to corresponding properties of Results VO.
						if(StringUtils.isBlank(productID) && !StringUtils.isBlank(skuId)){
							try {
								skuItem = getCatalogRepository().getItem(skuId, "sku");
							} catch (RepositoryException e) {
								vlogError("RepositoryException occurred "+e);
							}
						}
						if(skuItem != null){
							parentProducts = (Set<RepositoryItem>) skuItem.getPropertyValue("parentProducts");
							List<RepositoryItem> products = new ArrayList<RepositoryItem>(parentProducts);
							if(products != null && products.size() > 0){
								productItem = products.get(0);
							}
						}
						if(productItem != null){
							bbbProduct.setProductID((String) productItem.getPropertyValue("id"));
							bbbProduct.setProductName((String) productItem.getPropertyValue("displayName"));
							bbbProduct.setImageURL((String) productItem.getPropertyValue("thumbnailImage"));
						}
					}
					
		            for(SkuVO skuVO : skuVOList){
		            	if(null != skuVO.getColor() && !colorSkuSet.containsKey(skuVO.getColor())){
		            			colorSkuSet.put(skuVO.getColor(), skuVO);
		            	}
		            }
		            bbbProduct.setColorSet(colorSkuSet);
		        }
		        // Adding the Populated Product Item into Product List VO.
		        bbbProducts.getBBBProducts().add(bbbProduct);
		    }
			bbbProducts.setOtherResults(otherResults);

			logDebug("TBSEndecaSearch :: getProductList() :: END");

			return bbbProducts;
	}*/
 	 	 	
	
	/**
	 * @param listAllBrands
	 * @return List of all Valid Featured brands for which display flag is true
	 * @throws BBBSystemException 
	 * @throws BBBBusinessException 
	 */
	public List<BrandVO> getActiveBrandsById(List<BrandVO> listAllBrands) {
		String methodName = "BrandsDroplet:getActiveBrands";
		logDebug(methodName + "[getActiveBrands method Start]");
		final List<BrandVO> listAllActiveBrands = new ArrayList<BrandVO>();
		BrandVO brandVONew = null;
		if (listAllBrands != null && listAllBrands.size() != 0) {
			for (BrandVO brandVO : listAllBrands) {

				try {
					String siteID = SiteContextManager.getCurrentSiteId();
					if( siteID.startsWith("TBS_")) {
						siteID = siteID.substring(4);
					}
					
					brandVONew = null;
					
					brandVONew = this.getCatalogTools().getBrandDetails(
							brandVO.getFeaturedBrandURL(),
							siteID);

					boolean display_flag = this.getCatalogTools()
							.getBrandDisplayFlag(brandVONew.getBrandName());

					if (display_flag) {
						WebApp pDefaultWebApp = null;
						UrlParameter[] pUrlParams = getBrandTemplate()
								.cloneUrlParameters();
						pUrlParams[0].setValue(brandVONew.getBrandName());
						try {
							brandVONew.setSeoURL(getBrandTemplate().formatUrl(
									pUrlParams, pDefaultWebApp));
						} catch (ItemLinkException e) {
							logError(
									"Exception occourred while creating SEO URL for the category : "
											+ brandVO.getBrandName(), e);
						}
						brandVONew.setBrandImage(brandVO.getBrandImage());
						brandVONew.setBrandImageAltText(brandVO.getBrandImageAltText());
						brandVONew.setBrandDesc(brandVO.getBrandDesc());
						listAllActiveBrands.add(brandVONew);
					}

				} catch (BBBBusinessException e) {
					logError("Error while Retrieving Brand from Repository for Brand ID: "+brandVO.getFeaturedBrandURL(),e);
				} catch (BBBSystemException e) {
					logError("Error while Retrieving Brand from Repository for Brand ID: "+brandVO.getFeaturedBrandURL(),e);
				}
			}
		} else {
			logDebug(methodName
					+ "[getActiveBrands: listAllBrands is null or empty]");
		}
		logDebug(methodName + "[getActiveBrands method end]");
		return listAllActiveBrands;
	}
}