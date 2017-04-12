package com.bbb.search.droplet;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import org.apache.commons.lang.StringEscapeUtils;


import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.comparison.BBBProductComparisonList;
import com.bbb.commerce.catalog.comparison.vo.CompareProductEntryVO;
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBSearchConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.bean.query.SortCriteria;
import com.bbb.search.bean.result.BBBProduct;
import com.bbb.search.bean.result.CategoryParentVO;
import com.bbb.search.bean.result.FacetParentVO;
import com.bbb.search.bean.result.SearchResults;
import com.bbb.utils.BBBUtility;

public class TBSSearchDroplet extends SearchDroplet {
	
	private String mUpcSearchMode;
	
	private BBBCatalogTools mCatalogTools;

	/**
	 * @return the upcSearchMode
	 */
	public String getUpcSearchMode() {
		return mUpcSearchMode;
	}

	/**
	 * @param pUpcSearchMode the upcSearchMode to set
	 */
	public void setUpcSearchMode(String pUpcSearchMode) {
		mUpcSearchMode = pUpcSearchMode;
	}
	
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.mCatalogTools = catalogTools;
	}

	/**
	 * This droplet is overwritten for invoking the service method based on the search type.
	 * For the product search it should invoke the service() method of the Store layer, 
	 * for the UPC search only it should invoke the service method of the TBS layer. 
	 */
	@Override
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		String type = pRequest.getParameter(BBBCoreConstants.TYPE);
		if(StringUtils.isBlank(type)){
			type = TBSConstants.PRODUCT;
		}
		if(!StringUtils.isBlank(type) && type.equalsIgnoreCase(TBSConstants.PRODUCT)){
			super.service(pRequest, pResponse);
		} else if(!StringUtils.isBlank(type) && type.equalsIgnoreCase(TBSConstants.UPC)){
			logDebug("Entering Service Method of Search Droplet.");
			setValidSearchCriteria(true);
			setHeaderSearch(false);
			
			//Creating SearchQuery Object with pRequest parameters.
			SearchQuery pSearchQuery = new SearchQuery();
			final Map<String, String> catalogRef = new HashMap<String, String>();
			final SortCriteria sortCriteria = new SortCriteria();
			String origKeyword = pRequest.getParameter(TBSConstants.KEYWORD);
			
			pSearchQuery.setSearchMode(getUpcSearchMode());
			
			if(null!=pRequest.getParameter(BBBCoreConstants.PARTIAL_FLAG)){
				String partialFlag=pRequest.getParameter(BBBCoreConstants.PARTIAL_FLAG);
				pSearchQuery.setPartialFlag(partialFlag);
			}
			// Search term to display to user
			String enteredSearchTerm = pRequest.getParameter(BBBCoreConstants.ORIG_SEARCH_TERM);
			if(BBBUtility.isNotEmpty(enteredSearchTerm)){
				enteredSearchTerm = URLDecoder.decode(enteredSearchTerm);
			}
			else if(BBBUtility.isNotEmpty(origKeyword)){
				//removing prefix zeros from the skuid/upc 
				if(origKeyword.startsWith(BBBCoreConstants.STRING_ZERO)){
					origKeyword = origKeyword.substring(1);
				}
				origKeyword = origKeyword.replaceAll(BBBCoreConstants.SPACE, BBBCoreConstants.BLANK);
				origKeyword = origKeyword.replaceAll(BBBCoreConstants.COMMA_CONCATE_ZERO, BBBCoreConstants.COMMA);

				enteredSearchTerm = origKeyword.replaceAll(BBBCoreConstants.COMMA, BBBCoreConstants.PLUS).trim();
			}
			
			if(BBBUtility.isNotEmpty(enteredSearchTerm)){
				enteredSearchTerm = StringEscapeUtils.unescapeHtml(enteredSearchTerm);
			}
			String[] splitSkus = origKeyword.split(BBBCoreConstants.COMMA);
			RepositoryItem skuItem = null;
			Set<RepositoryItem> skuAttrRelation = null;
			RepositoryItem skuAttribute = null;
			String skuAttrId = null;
			for (String skuId : splitSkus) {
				try {
					skuItem = getCatalogTools().getSkuRepositoryItem(skuId);
					skuAttrRelation = (Set<RepositoryItem>) skuItem.getPropertyValue(TBSConstants.SKU_ATTRIBUTE_RELATION);
					if(skuAttrRelation != null && skuAttrRelation.size() > TBSConstants.ZERO ){
						vlogDebug("skuAttrRelation :: "+skuAttrRelation );
						for (RepositoryItem skuAttrReln : skuAttrRelation) {
							skuAttribute = (RepositoryItem) skuAttrReln.getPropertyValue(TBSConstants.SKU_ATTRIBUTE);
							if(skuAttribute != null){
								skuAttrId = skuAttribute.getRepositoryId();
							}
							if(BBBUtility.isNotEmpty(enteredSearchTerm) && !StringUtils.isBlank(skuAttrId) && (skuAttrId.equals(TBSConstants.KIRSCH_SKU_ATTRIBUTE) || skuAttrId.equals(TBSConstants.CMO_SKU_ATTRIBUTE))){
								enteredSearchTerm = enteredSearchTerm.replace(skuId, BBBCoreConstants.BLANK);
							}
						}
					}
				} catch (BBBBusinessException e) {
					vlogError("BBBBusinessException occurred while getting skuItem "+e);
				} catch (BBBSystemException e) {
					vlogError("BBBSystemException occurred while getting skuItem "+e);
				}
			}
			pRequest.setParameter(BBBCoreConstants.ENTERED_SEARCH_TERM, enteredSearchTerm);
			pSearchQuery.setKeyWord(enteredSearchTerm);
		
			pSearchQuery.setHostname(pRequest.getServerName());
			if (null != origKeyword){
				origKeyword = StringEscapeUtils.unescapeHtml(origKeyword);
			}
			
			String lastSearchUrl = pRequest.getParameter(TBSConstants.SAVEDURL);
			if(!BBBUtility.isEmpty(lastSearchUrl)){
				String domain=pRequest.getServerName();
				String path=TBSConstants.PATH;
				logDebug("path to set :" + path);
				logDebug("domain to set:" + domain);
		
				Cookie cookie = new Cookie(TBSConstants.SAVEDURL, lastSearchUrl);
				cookie.setDomain(domain);
				cookie.setPath(path);
				BBBUtility.addCookie(pResponse, cookie, true);
			}
			
			setCurrentViewUsingCookies(pRequest, pResponse);
			
			if ((null != pRequest.getParameter(TBSConstants.IS_REDIRECT)) && (Boolean.valueOf(pRequest.getParameter(TBSConstants.IS_REDIRECT)))) {
				pSearchQuery.setRedirected(true);
			}
			if (null != pRequest.getParameter(TBSConstants.IS_HEADER) && (BBBCoreConstants.YES_CHAR).equalsIgnoreCase(pRequest.getParameter(TBSConstants.IS_HEADER))) {
				setHeaderSearch(true);
			}else if ((null != enteredSearchTerm && enteredSearchTerm.length() < TBSConstants.MIN_KEYWORD_LENGTH) && (!(BBBCoreConstants.TRUE).equalsIgnoreCase(pRequest.getParameter(TBSConstants.FROM_BRAND_PAGE)))) {
				setHeaderSearch(true);
				setValidSearchCriteria(false);
			}

			String pSiteId = pRequest.getParameter(BBBCoreConstants.SITE_ID);
			if (pSiteId == null) {
				pSiteId = SiteContextManager.getCurrentSiteId();
			}
			if (pSiteId.startsWith(BBBCoreConstants.TBS)) {
				pSiteId = pSiteId.substring(4);
			}
			
			catalogRef.put(BBBCoreConstants.SEARCHTYPE, BBBCoreConstants.UPC_SEARCH);

			pSearchQuery.setSiteId(pSiteId);
			String searchMode = pRequest.getParameter(TBSConstants.SEARCH_MODE);
			logDebug("SearchDroplet searchMode from Request"+searchMode);
			if(BBBUtility.isNotEmpty(searchMode)){
				pSearchQuery.setSearchMode(searchMode);
				logDebug("searchMode is partial match"+pSearchQuery.getSearchMode());
			}
			
			// Setting the Facets List to Query Object to obtain the results for.
			List<String> pFacets = null;
			
			if (isHeaderSearch()) {
				catalogRef.put(BBBSearchConstants.CATALOG_ID, pRequest.getParameter(BBBSearchConstants.CATALOGID));
				catalogRef.put(BBBCoreConstants.CATALOG_REF_ID, pRequest.getParameter(BBBSearchConstants.CATALOG_REF_ID));

				pFacets = Arrays.asList(getHeaderFacets().split(BBBCoreConstants.COMMA));
				// Set flag to indicate that Search is for Header section and bypass useless type casting response.
				pSearchQuery.setHeaderSearch(true);
			}		
			else{
				pSearchQuery.setHeaderSearch(false);
				if(null != pRequest.getParameter(BBBCoreConstants.PAGE_NUM)){
					pSearchQuery.setPageNum(pRequest.getParameter(BBBCoreConstants.PAGE_NUM));
					logDebug("pagenum value for all pages where null != pRequest.getParameter(pagNum) "+pSearchQuery.getPageNum());
				}
				else{
					pSearchQuery.setPageNum(BBBCoreConstants.STRING_ONE);
				}
				pSearchQuery.setKeyWord(enteredSearchTerm);
				// Flag to mark If request is coming for Brand.
				if((BBBCoreConstants.TRUE).equalsIgnoreCase(pRequest.getParameter(TBSConstants.FROM_BRAND_PAGE))){
					catalogRef.put(TBSConstants.FROM_BRAND_PAGE, BBBCoreConstants.TRUE);

					pSearchQuery.setFromBrandPage(true);
				}
				if((BBBCoreConstants.TRUE).equalsIgnoreCase(pRequest.getParameter(BBBCoreConstants.FROM_COLLEGE))){
					catalogRef.put(BBBCoreConstants.FROM_COLLEGE, BBBCoreConstants.TRUE);

					pSearchQuery.setFromCollege(true);
				}
				
				//Replacing - with + to pass catalog id to Endeca Query generator 
				if(null==pRequest.getParameter(BBBSearchConstants.CATALOGID)){
					if(pRequest.getParameter(BBBSearchConstants.CATALOGID) != null){
						catalogRef.put(BBBSearchConstants.CATALOG_ID, pRequest.getParameter(BBBSearchConstants.CATALOGID).replace('-', '+'));
					}
					catalogRef.put(BBBCoreConstants.CATALOG_REF_ID, pRequest.getParameter(BBBSearchConstants.CATALOG_REF_ID));

				}
				else{
					catalogRef.put(BBBSearchConstants.CATALOG_ID, pRequest.getParameter(BBBSearchConstants.CATALOGID).replace('-', '+'));
					catalogRef.put(BBBCoreConstants.CATALOG_REF_ID, pRequest.getParameter(BBBSearchConstants.CATALOG_REF_ID));

				}


				if(null!=pRequest.getParameter(BBBCoreConstants.PAGE_SORT_OPT)){
					final String sortString = pRequest.getParameter(BBBCoreConstants.PAGE_SORT_OPT);
					if(sortString.contains(BBBCoreConstants.HYPHEN)){
						final StringBuffer sortBuffer = new StringBuffer(pRequest.getParameter(BBBCoreConstants.PAGE_SORT_OPT));
						final int sortOrderIndex=sortBuffer.indexOf(BBBCoreConstants.HYPHEN);
						
						// Setting the Sort order for Ascending or not.
						if((sortBuffer.substring(sortOrderIndex+1)).equals(BBBCoreConstants.STRING_ONE)){
							sortCriteria.setSortAscending(false);
						}
						else {
							sortCriteria.setSortAscending(true);
						}
						
						// Setting the Sort field name to be sorted on.
						sortCriteria.setSortFieldName(sortBuffer.substring(0,sortOrderIndex));
					}
				}
				else if(null != pRequest.getParameter(BBBCoreConstants.BCC_SORT_CODE)){
					final String sortString = pRequest.getParameter(BBBCoreConstants.BCC_SORT_CODE);
					final String sortOrderString = pRequest.getParameter(BBBCoreConstants.BCC_SORT_ORDER);
					
					if(!BBBUtility.isEmpty(sortOrderString) && sortOrderString.equals(BBBCoreConstants.STRING_ONE)){
						sortCriteria.setSortAscending(false);
					}
					else {
						sortCriteria.setSortAscending(true);
					}
					
					sortCriteria.setSortFieldName(sortString);
					
				}
				pFacets = Arrays.asList(getHeaderFacets().split(BBBCoreConstants.COMMA));
			}
			
			pSearchQuery.setQueryFacets(pFacets);
			pSearchQuery.setCatalogRef(catalogRef);
			pSearchQuery.setSortCriteria(sortCriteria);
			try{
				if(null!=pSearchQuery.getKeyWord() && (pSearchQuery.getCatalogRef().isEmpty()|| BBBUtility.isEmpty(pSearchQuery.getCatalogRef().get(BBBSearchConstants.CATALOG_ID)))){
					catalogRef.put(BBBSearchConstants.CATALOG_ID, getSearchManager().getCatalogId(BBBCoreConstants.RECORD_TYPE, BBBSearchConstants.PRODUCT));

					pSearchQuery.setCatalogRef(catalogRef);
				}
				
				//Setting the Page size in Query Object, firstly by checking in parameter, if not available then check cookie
				String pageSizeFilter = null;
				pageSizeFilter = pRequest.getParameter(TBSConstants.PAG_FILTER_OPT);
				List<String> dropdownList = getSearchManager().fetchPerPageDropdownList();
				String highest = dropdownList.get(dropdownList.size() - 1);
				if(!StringUtils.isEmpty(pageSizeFilter)) {
					if(!dropdownList.contains(pageSizeFilter)){
						// If View All is selected then set Page Size to highest
						pSearchQuery.setPageSize(highest);
					}
					else{
						pSearchQuery.setPageSize(pageSizeFilter);
					}
				}
				else if(BBBUtility.isEmpty(pageSizeFilter)){
					pageSizeFilter = pRequest.getCookieParameter(TBSConstants.PAGE_SIZE_COOKIE_NAME);
					if(pageSizeFilter != null && !dropdownList.contains(pageSizeFilter)){
						// If View All is selected then set Page Size to highest
						pSearchQuery.setPageSize(highest);
					}
					else{
						pSearchQuery.setPageSize(pageSizeFilter);
					}
				}
				
				final SearchResults browseSearchVO = getSearchManager().performSearch(pSearchQuery);
				if(browseSearchVO == null){
					pRequest.setParameter(BBBCoreConstants.ENTERED_SEARCH_TERM, enteredSearchTerm);
				    pRequest.serviceParameter(BBBCoreConstants.EMPTY_OPARAM, pRequest, pResponse);
				    pResponse.setStatus(404);
				}
				else{
					pRequest.setParameter(BBBCoreConstants.BROWSE_SEARCH_VO,browseSearchVO);
					getDropdownList(); 
					vlogDebug("Final facets returned "+browseSearchVO.getFacets());
					if(null != browseSearchVO.getCategoryHeader() && null == browseSearchVO.getCategoryHeader().getQuery()){
						CategoryParentVO pCVo = new CategoryParentVO();
						Map<String,CategoryVO> pMap= getCatalogTools().getParentCategory(pSearchQuery.getCatalogRef().get(BBBCoreConstants.CATALOG_REF_ID), pSearchQuery.getSiteId());
						if(null != pMap && pMap.size() >= 1){
							if(null != pMap.get(BBBCoreConstants.STRING_ONE) && !pMap.get(BBBCoreConstants.STRING_ONE).getPhantomCategory().booleanValue()){
								pCVo.setQuery(pMap.get(BBBCoreConstants.STRING_ONE).getCategoryId());
							}
							else{
								pCVo.setQuery(pMap.get(BBBCoreConstants.STRING_ZERO).getCategoryId());
							}
							pCVo.setName(pMap.get(BBBCoreConstants.STRING_ZERO).getCategoryName());
							pCVo.setPhantomCategory(pMap.get(BBBCoreConstants.STRING_ZERO).getPhantomCategory());
						}
						browseSearchVO.setCategoryHeader(pCVo);
					}
									
					pRequest.setParameter(BBBCoreConstants.SEARCH_TERM, origKeyword);
					if (isValidSearchCriteria()) {
						
						if(browseSearchVO.getBbbProducts() != null && browseSearchVO.getBbbProducts().getBBBProductCount() > 0 ){
							LinkedHashMap<String,CompareProductEntryVO> compareProductsMap = new LinkedHashMap<String, CompareProductEntryVO>();
							List productsInDrawer = (List) pRequest.getObjectParameter(BBBCoreConstants.PRODUCT_COMPARISON_LIST_ITEMS);
							if (!BBBUtility.isListEmpty(productsInDrawer)) {
								for (Iterator iter = productsInDrawer.iterator(); iter.hasNext();) {
									CompareProductEntryVO drawerproductVO = (CompareProductEntryVO) iter.next();
									compareProductsMap.put(drawerproductVO.getProductId(), drawerproductVO);
								}
							}
							List<BBBProduct> list = browseSearchVO.getBbbProducts().getBBBProducts();
							if(compareProductsMap.size() > 0){
								for(BBBProduct product : list){
									if(compareProductsMap.containsKey(product.getProductID())){
										product.setInCompareDrawer(true);
									}
								}
							} else{
								for(BBBProduct product : list){
										product.setInCompareDrawer(false);
								}
							}
						}
						
						int pos = 0;
						FacetParentVO tempvo = null;
						if(browseSearchVO.getFacets() != null ){
							List<FacetParentVO> list = browseSearchVO.getFacets();
							Iterator<FacetParentVO> itr = list.iterator();
							while(itr.hasNext()){
								FacetParentVO vo = itr.next();
								if(vo.getName().equalsIgnoreCase(TBSConstants.DEPARTMENT)){
									tempvo = vo;
									break;
								}
								pos++;
							}
							if(pos != 0 && tempvo != null){
								list.remove(pos);
								list.add(0,tempvo);
							}
						}
						
						if ((browseSearchVO.getBbbProducts() == null || browseSearchVO.getBbbProducts().getBBBProductCount() == 0)
								&& (browseSearchVO.getAssetMap() == null || browseSearchVO.getAssetMap().isEmpty())
								&& (browseSearchVO.getPartialSearchResults()==null || browseSearchVO.getPartialSearchResults().isEmpty())) {
							
							vlogDebug("CLS = SearchDroplet /MSG=[EMPTY- BBBProducts- AssetMap is null/empty]");
							
							if (null != browseSearchVO.getRedirUrl() && ((null == pRequest.getParameter(TBSConstants.IS_REDIRECT)) || (!Boolean.valueOf(pRequest.getParameter(TBSConstants.IS_REDIRECT))))) {
								String redirectUrl = browseSearchVO.getRedirUrl();
								if (!BBBUtility.isEmpty(redirectUrl)) {
									if (redirectUrl.contains(BBBCoreConstants.QUESTION_MARK)) {
										redirectUrl = redirectUrl + BBBCoreConstants.AMPERSAND + TBSConstants.REDIRECT_URL_IDENTIFIER;
									} else {
										redirectUrl = redirectUrl + BBBCoreConstants.QUESTION_MARK + TBSConstants.REDIRECT_URL_IDENTIFIER;
									}
								}
								pRequest.setParameter(TBSConstants.REDIRECT_URL, redirectUrl);
								pRequest.serviceParameter(TBSConstants.OUTPUT_REDIRECT, pRequest,
										pResponse);
							} else if (null != browseSearchVO.getFacets()
									&& browseSearchVO.getFacets().size() != 0) {
								pRequest.serviceParameter(BBBCoreConstants.OPARAM, pRequest, pResponse);
							} else {
								pRequest.setParameter(BBBCoreConstants.ENTERED_SEARCH_TERM, enteredSearchTerm);
								pRequest.serviceParameter(BBBCoreConstants.EMPTY_OPARAM, pRequest, pResponse);
								pResponse.setStatus(404);
							}
						} else {
							logDebug("CLS = [SearchDroplet] /MSG=[OUTPUT PARAM =" + browseSearchVO.getAssetMap() + "]");
							if (null != browseSearchVO.getRedirUrl() && ((null == pRequest.getParameter(TBSConstants.IS_REDIRECT)) || (!Boolean.valueOf(pRequest.getParameter(TBSConstants.IS_REDIRECT))))) {
								String redirectUrl = browseSearchVO.getRedirUrl();
								if (!BBBUtility.isEmpty(redirectUrl)) {
									if (redirectUrl.contains(BBBCoreConstants.QUESTION_MARK)) {
										redirectUrl = redirectUrl + BBBCoreConstants.AMPERSAND + TBSConstants.REDIRECT_URL_IDENTIFIER;
									} else {
										redirectUrl = redirectUrl + BBBCoreConstants.QUESTION_MARK + TBSConstants.REDIRECT_URL_IDENTIFIER;
									}
								}
								pRequest.setParameter(TBSConstants.REDIRECT_URL, redirectUrl);
								pRequest.serviceParameter(TBSConstants.OUTPUT_REDIRECT, pRequest, pResponse);
							} else if (browseSearchVO.getBbbProducts() != null || browseSearchVO.getPartialSearchResults() != null) {
									if(browseSearchVO.getBbbProducts() != null){
										final String linkString = createLinkString(browseSearchVO.getBbbProducts().getBBBProducts());
										pRequest.setParameter(TBSConstants.LINK_STRING, linkString);
									}
								pRequest.serviceParameter(BBBCoreConstants.OPARAM, pRequest, pResponse);
							}
						}
					}else{
						pRequest.serviceLocalParameter(TBSConstants.UN_MATCH_SEARCH_CRITERIA, pRequest, pResponse);
					}
				}
			}
			catch (BBBBusinessException e) {
				vlogError(LogMessageFormatter.formatMessage(pRequest, "BBBBusinessException from service of SearchDroplet", BBBCoreErrorConstants.BROWSE_ERROR_1010),e);
				pRequest.setParameter("enteredSearchTerm", enteredSearchTerm);
				pRequest.setParameter(OUTPUT_ERROR_MSG, SEARCH_BIZ_EXCEPTION);
				pRequest.serviceLocalParameter(BBBCoreConstants.ERROR_OPARAM, pRequest,pResponse);
			}
			catch (BBBSystemException e) {
				vlogError(LogMessageFormatter.formatMessage(pRequest, "BBBSystemException from service of SearchDroplet", BBBCoreErrorConstants.BROWSE_ERROR_1011),e);
				pRequest.setParameter("enteredSearchTerm", enteredSearchTerm);
				pRequest.setParameter(OUTPUT_ERROR_MSG, SEARCH_SYS_EXCEPTION);
				pRequest.serviceLocalParameter(BBBCoreConstants.ERROR_OPARAM, pRequest,pResponse);
			}
		}
	}
	
	private void setCurrentViewUsingCookies(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
		vlogDebug("setCurrentViewUsingCookies [start]");
		String plpView = (String) pRequest.getParameter(TBSConstants.VIEW);
		String currentViewParam = null;
		if (!StringUtils.isBlank(plpView)) {
			currentViewParam = plpView;
		} else {
			final Cookie[] allCookies = pRequest.getCookies();
			if (allCookies != null) {
				for (int cookiesCount = 0; cookiesCount < allCookies.length; cookiesCount++) {
					if (TBSConstants.CURRENT_VIEW.equals(allCookies[cookiesCount].getName())) {
						currentViewParam = allCookies[cookiesCount].getValue();
						break;
					}
				}
				if (StringUtils.isBlank(currentViewParam)) {
					// Set default value as grid
					currentViewParam = getDefaultView();
				}
			}
		}
        if(!StringUtils.isBlank(currentViewParam)) {
        	currentViewParam=currentViewParam.toLowerCase();
        }
        pRequest.setParameter(TBSConstants.VIEW, currentViewParam);
		List<String> configValues=null;
		try {
			configValues = getCatalogTools().getAllValuesForKey(getSearchDimConfig(), getMaxAgeCurrentViewCookie());
		} catch (BBBBusinessException bbbbEx) {
			vlogError(BBBCoreErrorConstants.BROWSE_ERROR_1045+" Business Exception occurred while fetching configValue for configKey maxAgeForCurrentViewCookie from method setCurrentViewUsingCookies in SearchDroplet",bbbbEx);
		} catch (BBBSystemException bbbsEx) {
			vlogError(BBBCoreErrorConstants.BROWSE_ERROR_1046+" System Exception occurred while fetching configValue for configKey maxAgeForCurrentViewCookie from method setCurrentViewUsingCookies in SearchDroplet",bbbsEx);
		}
		String maxAgeForCookie=null;
		if (configValues != null && !configValues.isEmpty()) {
			maxAgeForCookie = configValues.get(0);
		}

		// Set this view to cookies
		Cookie cookie = new Cookie(TBSConstants.CURRENT_VIEW, currentViewParam);
		String domain = pRequest.getServerName();
		String path = TBSConstants.PATH;
		cookie.setDomain(domain);
		cookie.setPath(path);
		if(!StringUtils.isBlank(maxAgeForCookie)) {
			cookie.setMaxAge(Integer.parseInt(maxAgeForCookie));
		}
		BBBUtility.addCookie(pResponse, cookie, true);
		vlogDebug("setCurrentViewUsingCookies [END] :cookie  " +cookie);
		
	}
}
