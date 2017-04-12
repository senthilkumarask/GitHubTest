package com.bbb.search.endeca;

import static com.bbb.constants.BBBCoreConstants.AMPERSAND;
import static com.bbb.constants.BBBCoreConstants.EQUAL;
import static com.bbb.constants.BBBCoreConstants.SITE_ID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.endeca.constants.BBBEndecaConstants;
import com.bbb.search.endeca.vo.EndecaQueryVO;
import com.bbb.utils.BBBUtility;
import com.endeca.navigation.ENEQuery;
import com.endeca.navigation.ENEQueryException;
import com.endeca.navigation.ERecSortKey;
import com.endeca.navigation.ERecSortKeyList;
import com.endeca.navigation.UrlENEQuery;
import com.endeca.navigation.UrlGen;
import com.endeca.soleng.urlformatter.UrlFormatException;

import atg.core.util.StringUtils;
import atg.repository.MutableRepository;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

/** This class implements the functionality to convert Search Query Object into ENEQuery Object as expected from Endeca.
 * @author agoe21
 *
 */
public class EndecaQueryGenerator extends BBBGenericService{

	private String sortField;
	private String didYouMean;
	private String queryParamNtpc;
	private String queryParamNtpr;
	private String queryParamNrc;
	private String searchField;
	private int	pageSize;
	private String encoding;
	private String queryURL;
	private String sortString;
	private EndecaSearch endecaSearch;
	private String searchMode;
	private String searchModeAll;
	private EndecaClient endecaClient;
	/*private ENEContentManager contentManager;*/
	private MutableRepository siteRepository;
	private String priceFilterField;
	private String boostBurrySort;

	private BBBCatalogTools mCatalogTools;
	private String dimNonDisplayMapConfig;
	private String defaultKey="boost_bury_sort_order";
	private Map<String, String> priorityFeaturesMap;
	private int	omnitureRecordCount;
	
	private EndecaSearchUtil endecaSearchUtil;
	private String ntxParamMatchAllExact;
	/**
	 * to hold search parameter
	 */
	private String mSearchPopularItemsParameter;

	/**
	 * This method is used to get search parameter
	 * @return mSearchPopularItemsParameter
	 */
	public String getSearchPopularItemsParameter() {
		return mSearchPopularItemsParameter;
	}


	/**
	 * This method is used to set search parameter
	 * @param pSearchPopularItemsParameter
	 */
	public void setSearchPopularItemsParameter(String pSearchPopularItemsParameter) {
		this.mSearchPopularItemsParameter = pSearchPopularItemsParameter;
	}


	/**
	 * This method takes Search Query object and transform it to the Search Engine specific Query request.
	 * @param pSearchQuery
	 * @return ENEQuery
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */



	public Map<String, String> getPriorityFeaturesMap() {
		return this.priorityFeaturesMap;
	}


	public void setPriorityFeaturesMap(Map<String, String> priorityFeaturesMap) {
		this.priorityFeaturesMap = priorityFeaturesMap;
	}


	/**
	 * @param pSearchQuery
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public EndecaQueryVO generateEndecaQuery(final SearchQuery pSearchQuery) throws BBBBusinessException, BBBSystemException{

		logDebug("Entering EndecaQueryGenerator.generateEndecaQuery method.");


		String channelDimId = null;
		String channelThemeDimId=null;
		String queryString = null;
		String brandName = "";
		/*ENEContentQuery contentQuery = null;*/
		EndecaQueryVO endecaQueryVO = new EndecaQueryVO();
		StringBuffer cacheableQueryKey = new StringBuffer();
		String strNrEndecaQuery = null;
		 //Start:BPS-799:Implement search within search functionality
		String ntkall ="";
		String ntxmode ="";
		int count=1;
		String andKeyword = "";
		
		String storeId = pSearchQuery.getStoreId();
		boolean onlineTab = pSearchQuery.isOnlineTab();
		
		if (!StringUtils.isEmpty(pSearchQuery.getNarrowDown())) {
			logDebug("Entering generateEndecaQuery.pSearchQuery.getNarrowDown() ");
			List<String> userInput = new ArrayList<String>(
					Arrays.asList(pSearchQuery.getNarrowDown().trim()
							.toLowerCase().split(BBBCoreConstants.NARROWDOWN_DELIMITER_FL+BBBCoreConstants.UNDERSCORE)));
			for (String narrowTerm : userInput) {
				if (!StringUtils.isEmpty(narrowTerm)) {
					if (!StringUtils.isEmpty(pSearchQuery.getNarrowDown())) {
						ntkall = ntkall + BBBCoreConstants.PIPE_SYMBOL + getSearchField();
						ntxmode = ntxmode + BBBCoreConstants.PIPE_SYMBOL  + getSearchModeAll();
						if(!StringUtils.isEmpty(pSearchQuery.getKeyWord())){
						andKeyword = andKeyword + BBBCoreConstants.PIPE_SYMBOL  + narrowTerm.trim();
						}
						else{
							if(count==1){
						    andKeyword = narrowTerm.trim();
							}else{
								andKeyword += BBBCoreConstants.PIPE_SYMBOL +narrowTerm.trim();
							}
						}
					} 
					count++;
				}
				
			}
			
		}
		

		logDebug("andKeyword : "+andKeyword);
		//End:BPS-799:Implement search within search functionality

		// Get the Dimension Value Id for current Site ID.
		String siteDimId = getEndecaSearch().getCatalogId(BBBEndecaConstants.SITE_ID2,getEndecaSearch().getSiteIdMap().get(pSearchQuery.getSiteId()));
		//BBBJ-1220 
		if(pSearchQuery.isFromChecklistCategory()){
			String dimensionID = this.getEndecaSearchUtil().getCheckListSeoDimId(pSearchQuery,ServletUtil.getCurrentRequest());
			if(BBBUtility.isEmpty(dimensionID)){
				logError("Error while getting dimension id for checklist category");
				throw new BBBSystemException(BBBCoreErrorConstants.ERROR_CHECKLIST_CATEGORY_DIMENSION, "System Exception occurred while getting dimension id from Endeca"); 
			}
			pSearchQuery.setChecklistCategoryDimensionId(dimensionID);
		}	
		if(BBBUtility.isNotEmpty(pSearchQuery.getChannelId())){
			// Get the Dimension Value Id for current channel	 
			if(getEndecaSearch().getCatalogId(BBBEndecaConstants.CHANNEL,pSearchQuery.getChannelId()) != null){
				channelDimId = getEndecaSearch().getCatalogId(BBBEndecaConstants.CHANNEL,pSearchQuery.getChannelId());	
			}else{
				logError("Error while getting dimension id for channel");
				throw new BBBSystemException(BBBCoreErrorConstants.ERROR_CHANNEL_DIMENSION, "System Exception occurred while getting dimension id from Endeca");
			}			 
			// Get the Dimension Value Id for current channelTheme
			if(BBBUtility.isNotEmpty(pSearchQuery.getChannelThemeId())){
				channelThemeDimId = getEndecaSearch().getCatalogId(BBBEndecaConstants.THEME_ID, pSearchQuery.getChannelThemeId());
				queryString = BBBEndecaConstants.NAVIGATION+"="+ siteDimId + BBBCoreConstants.PLUS + channelDimId + BBBCoreConstants.PLUS + channelThemeDimId    +"&";
			} else{
			queryString = BBBEndecaConstants.NAVIGATION+"="+ siteDimId + BBBCoreConstants.PLUS + channelDimId    +"&";
			}
		}else{
			queryString = BBBEndecaConstants.NAVIGATION+"="+ siteDimId +"&";	
		}

		//queryString = NAVIGATION+"="+ siteDimId;
		/*START - Added as part of R2.2 Story - 116-D1 & 116-D2*/
		//Fetch default perPage value from Config Key
		List<String> default_per_page = null;
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		//if(request !=null && BrowserTyper.isBrowserType(getIpadDeviceChecker(), request.getHeader(USER_AGENT))){
		if(request !=null && BBBUtility.isStringPatternValid(BBBEndecaConstants.IPAD_PATTERN, request.getHeader(BBBEndecaConstants.USER_AGENT))){
			logDebug("header for ipad request is " + request.getHeader(BBBEndecaConstants.USER_AGENT));
			default_per_page = getCatalogTools().getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS, BBBCatalogConstants.PER_PAGE_DEFAULT_ON_IPAD);
		}else{
			default_per_page = getCatalogTools().getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS, BBBCatalogConstants.PER_PAGE_DEFAULT); 
		}
		
		if(default_per_page != null && !default_per_page.isEmpty() && BBBUtility.isEmpty(pSearchQuery.getPageSize())){
			int pageSize = Integer.parseInt(default_per_page.get(0));
			this.setPageSize(pageSize);
			pSearchQuery.setPageSize(default_per_page.get(0));
		}
		/*END - Added as part of R2.2 Story - 116-D1 & 116-D2*/
		if(!StringUtils.isEmpty(pSearchQuery.getCatalogRef().get("catalogId")) && !pSearchQuery.isFromChecklistCategory()){
			// set navigation parameter.
			String recordType = getEndecaSearch().getCatalogId(BBBEndecaConstants.RECORD_TYPE,
					BBBEndecaConstants.PROD_RECORD_TYPE);
			queryString = BBBEndecaConstants.NAVIGATION+"="+pSearchQuery.getCatalogRef().get("catalogId");
			if(!pSearchQuery.getCatalogRef().get("catalogId").contains(siteDimId))
			{
				queryString = BBBEndecaConstants.NAVIGATION+"="+pSearchQuery.getCatalogRef().get("catalogId")+ BBBCoreConstants.PLUS + siteDimId;
				/*//Start:BPS-799:Implement search within search functionality
				if(BBBUtility.isNotEmpty(pSearchQuery.getCatalogRef().get("catalogRefId"))&& 
						BBBUtility.isNotEmpty(pSearchQuery.getNarrowDown())&& !pSearchQuery.isFromBrandPage()){
					queryString = queryString+ " "+getEndecaSearch().getCatalogId(RECORD_TYPE,
							PROD_RECORD_TYPE);
				}
				//End:BPS-799:Implement search within search functionality
				*/if(BBBUtility.isNotEmpty(channelDimId) && !pSearchQuery.getCatalogRef().get("catalogId").contains(channelDimId)){
					queryString += BBBCoreConstants.PLUS + channelDimId;
				}
			}//Start:BPS-799:Implement search within search functionality
			if(BBBUtility.isNotEmpty(pSearchQuery.getCatalogRef().get("catalogRefId"))&& 
					BBBUtility.isNotEmpty(pSearchQuery.getNarrowDown())&& !pSearchQuery.isFromBrandPage()&&
					!pSearchQuery.getCatalogRef().get("catalogId").contains(recordType)){
				queryString = queryString+ BBBCoreConstants.PLUS+recordType;
			}
			if(BBBUtility.isNotEmpty(pSearchQuery.getCatalogRef().get("catalogRefId"))&& 
					BBBUtility.isEmpty(pSearchQuery.getNarrowDown())&& !pSearchQuery.isFromBrandPage()&&
					pSearchQuery.getCatalogRef().get("catalogId").contains(recordType)){
				queryString = queryString.replace(BBBCoreConstants.PLUS+recordType, "");
			}//End:BPS-799:Implement search within search functionality
		} else if(pSearchQuery.isFromChecklistCategory()) {
			// set navigation parameter
			if(BBBUtility.isEmpty(pSearchQuery.getCatalogRef().get("catalogId"))){
				pSearchQuery.getCatalogRef().put("catalogId", pSearchQuery.getChecklistCategoryDimensionId());
				//pSearchQuery.getCatalogRef().put("catalogRefId", pSearchQuery.getChecklistCategoryDimensionId());
				queryString = BBBEndecaConstants.NAVIGATION+"="+pSearchQuery.getChecklistCategoryDimensionId();
			} else {
				queryString =  BBBEndecaConstants.NAVIGATION+"="+pSearchQuery.getCatalogRef().get("catalogId").replaceAll("-", BBBCoreConstants.PLUS);
			}
			if(BBBUtility.isEmpty(pSearchQuery.getCatalogRef().get("catalogRefId"))){
				pSearchQuery.getCatalogRef().put("catalogRefId", pSearchQuery.getChecklistCategoryDimensionId());
			}
			
			
			if(!queryString.contains(siteDimId))
			{
				queryString = queryString + BBBCoreConstants.PLUS + siteDimId;
			}
		}
		if(!StringUtils.isEmpty(pSearchQuery.getKeyWord()) &&
                      ("true").equalsIgnoreCase(pSearchQuery.getCatalogRef().get("frmBrandPage"))){

               // Get the Dimension Value Id for current Brand name.
              logDebug("Apppending Brand Id to query string which is passed as keyword in case of brand" + pSearchQuery.getKeyWord());
              //RM Defect 23496-Start. Not able to see the changes made in Endeca in IST forSEO copy inbrand page
              brandName = pSearchQuery.getKeyWord();
              if (BBBUtility.isValidNumber(brandName)) {
            	  brandName= getCatalogTools().getBrandName(pSearchQuery.getKeyWord());
              } 
              String brandDimId = getEndecaSearch().getCatalogId(BBBEndecaConstants.BRAND, brandName);
              
              //RM Defect 23496-END
               if(null != brandDimId && BBBUtility.isNotEmpty(pSearchQuery.getCatalogRef().get("catalogId")) && !pSearchQuery.getCatalogRef().get("catalogId").contains(brandDimId)){
                     queryString += BBBCoreConstants.PLUS + brandDimId;
               
               }
			   //BBBI-3656 | Facet search not working with l2/l3/brand
               if(BBBUtility.isNotEmpty(brandDimId)){
            	   request.getSession().setAttribute(BBBEndecaConstants.BRAND_DIM_ID, brandDimId);
               }
        }
        queryString += "&";

		// START -- R2.1 -- Price Range Slider Scope Item #66

		/*if(null != pSearchQuery.getPriceRangeInfo() && BBBUtility.isNotEmpty(pSearchQuery.getPriceRangeInfo().get("priceMin"))
				&& BBBUtility.isNotEmpty(pSearchQuery.getPriceRangeInfo().get("priceMax"))){
			String pPriceRangeString =getPriceFilterField()+ "|BTWN+" + pSearchQuery.getPriceRangeInfo().get("priceMin") + "+" + pSearchQuery.getPriceRangeInfo().get("priceMax");
			//urlGen.addParam(NAV_FILTER,pPriceRangeString);
			queryString += "&" + NAV_FILTER + "=" + pPriceRangeString;
		}*/

		// END   -- R2.1 -- Price Range Slider Scope Item #66

		final UrlGen urlGen = new UrlGen(queryString, getEncoding());

		 
		if(pSearchQuery.getGsEndecaQuery() != null ){			 
			
			strNrEndecaQuery = parseQueryForGS(pSearchQuery);
			if(!StringUtils.isEmpty(strNrEndecaQuery)){
				// set navigation refinement parameter.
				urlGen.addParam(BBBEndecaConstants.NAV_NR, strNrEndecaQuery);
			}
		}
		 
		if(!StringUtils.isEmpty(pSearchQuery.getCatalogRef().get("catalogRefId"))){
			// set navigation refinement parameter.
			//BBBJ-1220
			if(!pSearchQuery.isFromChecklistCategory()){ 
				urlGen.addParam(BBBEndecaConstants.NAV_REFINEMENT, pSearchQuery.getCatalogRef().get("catalogRefId"));
			}
			//Start:BPS-799:Implement search within search functionality
			if(BBBUtility.isNotEmpty(pSearchQuery.getNarrowDown())){
				urlGen.addParam(BBBEndecaConstants.NAV_SEARCH_MODE, ntxmode.substring(1));
				urlGen.addParam(BBBEndecaConstants.NAV_PROPERTY_NAME, ntkall.substring(1));
				urlGen.addParam(BBBEndecaConstants.NAV_KEYWORD, andKeyword);
				urlGen.addParam(BBBEndecaConstants.NAV_SEARCH_PHRASES_NTPC, getQueryParamNtpc());
				urlGen.addParam(BBBEndecaConstants.NAV_SEARCH_PHRASES_NTPR, getQueryParamNtpr());
			}
			//End:BPS-799:Implement search within search functionality
		}
		if(!StringUtils.isEmpty(pSearchQuery.getKeyWord()) && !BBBUtility.isEmpty(pSearchQuery.getKeyWord().trim())){
			// START JIRA Defect # BBBSL-1013
            String keyword= pSearchQuery.getKeyWord().trim();
            //urlGen.addParam(NAV_KEYWORD, pSearchQuery.getKeyWord());
            // END   JIRA Defect # BBBSL-1013
			/* RM Defect 15480 - Remove brand facet when frmBrandPage is true    */
			/*if(("true").equalsIgnoreCase(pSearchQuery.getCatalogRef().get("frmBrandPage"))){
				urlGen.addParam(NAV_PROPERTY_NAME, getEndecaSearch().getPropertyMap().get("PRODUCT_BRAND"));
				urlGen.addParam(NAV_SEARCH_MODE, getSearchModeAll());
			}
			else */if(("true").equalsIgnoreCase(pSearchQuery.getCatalogRef().get("fromCollege"))){
				urlGen.addParam(BBBEndecaConstants.NAV_PROPERTY_NAME, getEndecaSearch().getConfigUtil().getPropertyMap().get("PRODUCT_COLLEGE_NAME"));
				urlGen.addParam(BBBEndecaConstants.NAV_SEARCH_MODE, getSearchModeAll());
				logDebug("EndecaQueryGenerator.generateEndecaQuery Ntx/searchmode when catalogref(fromCollege) is true"+getSearchModeAll());
			}// RM 21910 : START : added for type ahead search based on product name for STOFU  
			else if(BBBUtility.isNotEmpty(pSearchQuery.getCatalogRef().get("searchProperty"))){
				urlGen.addParam(BBBEndecaConstants.NAV_PROPERTY_NAME, getEndecaSearch().getConfigUtil().getPropertyMap().get(pSearchQuery.getCatalogRef().get("searchProperty")));
				urlGen.addParam(BBBEndecaConstants.NAV_SEARCH_MODE, getSearchModeAll());
				logDebug("EndecaQueryGenerator.generateEndecaQuery Ntx/searchmode when catalogref(searchProperty) is not empty "+getSearchModeAll());
			}// RM 21910 : END : added for type ahead search based on product name for STOFU
			else{
                          // START JIRA Defect # BBBSL-1013
                           if(!("true").equalsIgnoreCase(pSearchQuery.getCatalogRef().get("frmBrandPage"))){
                        	   	 keyword = keyword.replaceAll("'", "");
								 keyword = ((keyword.replaceAll("[^0-9A-Za-z\"\']"," ")).replaceAll("[\']", "")).replaceAll(" +", " ");
                                 keyword = keyword.replaceAll("-", " ");
                                // keyword = keyword.replaceAll(" +", " ");
//         			Fix for Defect 2257071
                                 if(BBBUtility.isEmpty(keyword.trim())){
             				return null;
                                 }
                          }
                           else if(("true").equalsIgnoreCase(pSearchQuery.getCatalogRef().get("frmBrandPage"))){
                        	   // empty keyword as we have brand id in keyword in case of brand page 
                        	   keyword = brandName;
                           }
                          // END   JIRA Defect # BBBSL-1013
                          if(null!=pSearchQuery.getNarrowDown()){
                    		   keyword=keyword+andKeyword;  
                    	   }
                          //Adding check to populate the ntk parameter for search configurable story
				if(!pSearchQuery.isFromBrandPage() && !pSearchQuery.isHeaderSearch()) {
					if(BBBUtility.isNotEmpty(pSearchQuery.getGroupId())){
					   urlGen.addParam(BBBEndecaConstants.NAV_PROPERTY_NAME,pSearchQuery.getGroupId());
						}
					else{
						urlGen.addParam(BBBEndecaConstants.NAV_PROPERTY_NAME, getSearchField());
					}
				}
				else{
					urlGen.addParam(BBBEndecaConstants.NAV_PROPERTY_NAME, getSearchField());
				}
				
				//Start:BPS-799:Implement search within search functionality
				if(BBBUtility.isNotEmpty(pSearchQuery.getNarrowDown())){
						urlGen.addParam(BBBEndecaConstants.NAV_PROPERTY_NAME,getSearchField()+ntkall);  
         	   }
				//End:BPS-799:Implement search within search functionality
			}
			
			
			if(BBBUtility.isNotEmpty(pSearchQuery.getSearchMode())){
				urlGen.addParam(BBBEndecaConstants.NAV_SEARCH_MODE, pSearchQuery.getSearchMode());
				//Start:BPS-799:Implement search within search functionality
				if(null!=pSearchQuery.getNarrowDown()){
					urlGen.addParam(BBBEndecaConstants.NAV_SEARCH_MODE, pSearchQuery.getSearchMode()+ntxmode);
				}
				//Start:BPS-799:Implement search within search functionality
				logDebug("EndecaQueryGenerator.generateEndecaQuery Ntx/searchmode is set from URL or partial Logic(92F) "+pSearchQuery.getSearchMode());
			}
			else if(("true").equalsIgnoreCase(pSearchQuery.getCatalogRef().get("fromCollege")) || BBBUtility.isNotEmpty(pSearchQuery.getCatalogRef().get("searchProperty"))){
				urlGen.addParam(BBBEndecaConstants.NAV_SEARCH_MODE, getSearchModeAll());
				logDebug("EndecaQueryGenerator.generateEndecaQuery Ntx/searchmode when catalogref(fromCollege) is true (searchProperty) is not empty"+getSearchModeAll());
			}
			else{
				urlGen.addParam(BBBEndecaConstants.NAV_SEARCH_MODE, getSearchMode());
				//Start:BPS-799:Implement search within search functionality
				if(null!=pSearchQuery.getNarrowDown()){
					urlGen.addParam(BBBEndecaConstants.NAV_SEARCH_MODE, getSearchMode()+ntxmode);
				}
				//Start:BPS-799:Implement search within search functionality
				logDebug("EndecaQueryGenerator.generateEndecaQuery default search Mode"+getSearchMode());
			}
			
            // START JIRA Defect # BBBSL-1013
            urlGen.addParam(BBBEndecaConstants.NAV_KEYWORD, keyword.trim().toLowerCase());
            logDebug("Keyword searched for : "+ keyword.trim());
            // END   JIRA Defect # BBBSL-1013
			urlGen.addParam(BBBEndecaConstants.NAV_DID_YOU_MEAN, getDidYouMean());
			urlGen.addParam(BBBEndecaConstants.NAV_SEARCH_PHRASES_NTPC, getQueryParamNtpc());
			urlGen.addParam(BBBEndecaConstants.NAV_SEARCH_PHRASES_NTPR, getQueryParamNtpr());
		}
		else if(!pSearchQuery.isHeaderSearch()){
			//R2.1.1 Scope #519 Sorting of Brands only on PLP  - Start
			urlGen.addParam(BBBEndecaConstants.NAV_SEARCH_PHRASES_NRC, getQueryParamNrc());
			//R2.1.1 Scope #519 Sorting of Brands only on PLP  - End
		}
		if(null!=pSearchQuery.getPageSize()){
			pageSize = Integer.parseInt(pSearchQuery.getPageSize());
		}

		if(!StringUtils.isEmpty(pSearchQuery.getPageNum())){
			final long pageNum = Long.parseLong(pSearchQuery.getPageNum());
			final Long recordOffset = (pageSize * (pageNum - 1));
			urlGen.addParam(BBBEndecaConstants.NAV_REC_OFFSET, recordOffset.toString());
		}
		
		
		if (!StringUtils.isEmpty(storeId) && !onlineTab ) {
			String storeQueryAsEqlFilter = createEQLFilterForStoreSearch(urlGen.toString(),storeId);
			if(storeQueryAsEqlFilter != null) {
				urlGen.addParam(BBBEndecaConstants.NEGATIVE_RECORD_FILTER, storeQueryAsEqlFilter);
			}
		}

		queryString = urlGen.toString();
		cacheableQueryKey.append(queryString);
		cacheableQueryKey.append(AMPERSAND + BBBEndecaConstants.PARAM_PAGESIZE + EQUAL + pageSize);
		
		//setting page size so that results list handler uses this 
		pSearchQuery.setPageSize(""+pageSize);

		pSearchQuery.setQueryURL(queryString);
		logDebug("Query String: " +queryString);
		ENEQuery queryObject = null;
		try{
		queryObject = new UrlENEQuery(queryString, getEncoding());
		//queryObject.setDimSearchCompound(true);
		/*//start of 92F. This code is used to reduce the facet results and search results
		if(pSearchQuery.getStopWrdRemovedString()!=null && pSearchQuery.getStopWrdRemovedString().size()>2 && 
				pSearchQuery.getStopWrdRemovedString().size()<8 && ("mode+matchpartial").equalsIgnoreCase(pSearchQuery.getSearchMode())){
			//should be less than 5 or number configured BY BUSINESS
			pageSize=1;
			queryObject.setNavNumERecs(pageSize);
			queryObject.setNavAllRefinements(false);
		}//end of 92 F
		else{*/
		queryObject.setNavNumERecs(pageSize);
		queryObject.setNavAllRefinements(true);

		ERecSortKeyList sortKeyList = null;
		String key = null;
		ERecSortKey sortKey = null;
		
		if(!BBBUtility.isEmpty(pSearchQuery.getSortCriteria().getSortFieldName()) &&
				null != getEndecaSearch().getSortFieldMap() &&
				null != getEndecaSearch().getSortFieldMap().get(pSearchQuery.getSortCriteria().getSortFieldName().toLowerCase())){
			logDebug("SortCriteria fieldName"+pSearchQuery.getSortCriteria().getSortFieldName());
			logDebug("SortAscending: "+pSearchQuery.getSortCriteria().isSortAscending());
			String endecaSortKey = getEndecaSearch().getSortFieldMap().get(pSearchQuery.getSortCriteria().getSortFieldName().toLowerCase());
			logDebug("endecaSortKey=getEndecaSearch().getSortFieldMap().get(SortCriteria)"+endecaSortKey);
			// Check to see if the default sort option for the current request is intended to
			//bring the boosted / buried records through Page builder.
			// If yes, then ensure that there is no sort option explicitly passed in our query otherwise it will overrrule PB rule.
			
			if(!StringUtils.isEmpty(getBoostBurrySort())){
				if(!(getBoostBurrySort()).equalsIgnoreCase(pSearchQuery.getSortCriteria().getSortFieldName())){
					key = endecaSortKey;
					sortKeyList = new ERecSortKeyList();
					if(key !=null){
						if(BBBUtility.isEmpty(pSearchQuery.getKeyWord()) && !(pSearchQuery.isFromBrandPage())){
							if(key.equalsIgnoreCase("P_Date")){
								String rankKey = "P_Product_Rank";
								sortKey = new ERecSortKey(rankKey, true);
								sortKeyList.add(sortKey);
							}
						}
						sortKey = new ERecSortKey(key,pSearchQuery.getSortCriteria().isSortAscending());
						if(!pSearchQuery.getSortCriteria().isSortAscending()){
							pSearchQuery.setSortString(pSearchQuery.getSortCriteria().getSortFieldName()+"-1");
							logDebug("sort String in if boost bury: "+pSearchQuery.getSortString());
						}
						else{
							pSearchQuery.setSortString(pSearchQuery.getSortCriteria().getSortFieldName()+"-0");
							logDebug("sort String in else boost bury: "+pSearchQuery.getSortString());
						}
						sortKeyList.add(sortKey);
					}
				}else{
					pSearchQuery.setSortString(getBoostBurrySort()+"-0");
					logDebug("if fieldname is not BoostBurrySort"+pSearchQuery.getSortString());
				}
			}
		}
		else{
			if(StringUtils.isEmpty(pSearchQuery.getKeyWord())){
				sortKeyList = new ERecSortKeyList();
				// Request is not from Search Results page for the very first time.
				key = getSortField();
				logDebug("key from getSortField "+key);
				if(key !=null){
					if(key.equalsIgnoreCase("P_Date")){
						String rankKey = "P_Product_Rank";
						sortKey = new ERecSortKey(rankKey,true);
						sortKeyList.add(sortKey);
					}
					sortKey = new ERecSortKey(key,false);
				}
				pSearchQuery.setSortString("Date-1");
				sortKeyList.add(sortKey);
			}
		else{
				pSearchQuery.setSortString("");
			}
		}

		//Setting the Sort Key parameters.
		if(null != sortKeyList && sortKeyList.size() >0){
			queryObject.setNavActiveSortKeys(sortKeyList);
		}

		cacheableQueryKey.append(AMPERSAND + BBBEndecaConstants.PARAM_KEY + EQUAL + key);
		cacheableQueryKey.append(AMPERSAND + BBBEndecaConstants.PARAM_SORT_STRING + EQUAL + pSearchQuery.getSortString());

		// Setting the Record Filter for site retrieving site specific content.
		//queryObject.setNavRecordFilter(getEndecaSearch().getSiteConfig().get(pSearchQuery.getSiteId()));
		cacheableQueryKey.append(AMPERSAND + SITE_ID + EQUAL + pSearchQuery.getSiteId());

		/*if(null == contentManager){
			contentManager = new ENEContentManager();
		}
		//ENEContentManager contentManager = new ENEContentManager();
		//contentManager = new ENEContentManager();
		contentQuery = (ENEContentQuery) contentManager.createQuery();
		contentQuery.setRuleZone(NAV_PAGE_ZONE);
		contentQuery.setENEQuery(queryObject);

		endecaQueryVO.setEndicaContentQuery(contentQuery);*/
		endecaQueryVO.setQueryObject(queryObject);
		endecaQueryVO.setQueryString(cacheableQueryKey.toString());
				
		}
		
		
		
		catch(ENEQueryException queryException){
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR_ENDECA_1001,"ENEQuery Exception ", queryException);
		} /*catch (InitializationException e) {
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR_ENDECA_1002,"Endeca Initialization Exception ", e);
		} catch (ContentException contentException) {
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR_ENDECA_1003,"Endeca Content Exception ", contentException);
		}*/
		
		
		
		return endecaQueryVO;
	}

	/**
	 * Creates EQL filter based on P_Stores and storeId from request 
	 * @param pCurrentQueryString
	 * @param storeId
	 * @return eql filter
	 */
	private String createEQLFilterForStoreSearch(String pCurrentQueryString, String pStoreId) {
		//used for Store search
		String EQL_STORE_SEARCH = "endeca:matches(.,\"%s\",\"%s\",\"all\")";
		String eqlSearchQuery = String.format(EQL_STORE_SEARCH,BBBEndecaConstants.P_STORES,pStoreId);
		try {
			String nrsParameterValue = EndecaSearchUtil.getParameter(pCurrentQueryString, BBBEndecaConstants.NEGATIVE_RECORD_FILTER);
			if (!StringUtils.isEmpty(nrsParameterValue)) {				
				//if eqlFilter already exists (highly unlikely), append current eql to it and set that to current request
				//process string only when length is > 21 indicating it contains string to be parsed out  
				if(nrsParameterValue.length() > BBBEndecaConstants.NAV_REC_STRUCT_CONDITION_START.length()+1) {
					nrsParameterValue = nrsParameterValue.substring(BBBEndecaConstants.NAV_REC_STRUCT_CONDITION_START.length(),nrsParameterValue.length()-1);
					eqlSearchQuery = nrsParameterValue + BBBEndecaConstants.FILTER_CONDITION_AND 
										+ BBBEndecaConstants.FILTER_CONDITION_START 
										+ eqlSearchQuery 
										+ BBBEndecaConstants.FILTER_CONDITION_END;
				}
			} 
			return BBBEndecaConstants.NAV_REC_STRUCT_CONDITION_START + eqlSearchQuery + BBBEndecaConstants.NAV_REC_STRUCT_CONDITION_END;
		} catch (UrlFormatException ufe) {
			logDebug("Exception while fetching Nrs value from current query string - "+ufe.getMessage());
		}
		
		return null;
	}
	
	/**
	 * @param pSearchQuery
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public EndecaQueryVO generatePartialEndecaQuery(final SearchQuery pSearchQuery) throws BBBBusinessException, BBBSystemException{

		logDebug("Entering EndecaQueryGenerator.generateEndecaQuery method.");
		
		String queryString = null;
		EndecaQueryVO endecaQueryVO = new EndecaQueryVO();
		StringBuffer cacheableQueryKey = new StringBuffer();
		
		// Get the Dimension Value Id for current Site ID.
		String siteDimId = getEndecaSearch().getCatalogId(BBBEndecaConstants.SITE_ID2,getEndecaSearch().getSiteIdMap().get(pSearchQuery.getSiteId()));
	
		queryString = BBBEndecaConstants.NAVIGATION+"="+pSearchQuery.getCatalogRef().get("catalogId")+ " " + siteDimId;		
        queryString += "&";

        final UrlGen urlGen = new UrlGen(queryString, getEncoding());

            String keyword= pSearchQuery.getKeyWord().trim();
            keyword = keyword.replaceAll("'", "");
            keyword = ((keyword.replaceAll("[^0-9A-Za-z\"\']"," ")).replaceAll("[\']", "")).replaceAll(" +", " ");
            keyword = keyword.replaceAll("-", " ");
            // keyword = keyword.replaceAll(" +", " ");
            //Fix for Defect 2257071
            if(BBBUtility.isEmpty(keyword.trim()))
 			return null;
                      
			urlGen.addParam(BBBEndecaConstants.NAV_PROPERTY_NAME, getSearchField());
			urlGen.addParam(BBBEndecaConstants.NAV_SEARCH_MODE, pSearchQuery.getSearchMode());
            urlGen.addParam(BBBEndecaConstants.NAV_KEYWORD, keyword.trim());
            logDebug("Keyword searched for : "+ keyword.trim());
            queryString = urlGen.toString();
            cacheableQueryKey.append(queryString);
            

            pSearchQuery.setQueryURL(queryString);
            logDebug("Query String: " +queryString);
            ENEQuery queryObject = null;
            try{
            	queryObject = new UrlENEQuery(queryString, getEncoding());
            	final List<String> maxPartialMatchProduct = getCatalogTools().getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, BBBCmsConstants.PARTIAL_SEARCH_CAROUSEL_MAX_PRODUCTS_KEY );
            	final int maxPartialMatchProductCount = Integer.parseInt(maxPartialMatchProduct.get(0));     	
            	queryObject.setNavNumERecs(maxPartialMatchProductCount);
            	queryObject.setNavAllRefinements(false);		
		
            	cacheableQueryKey.append(AMPERSAND + BBBEndecaConstants.PARAM_PAGESIZE + EQUAL + pageSize);
            	cacheableQueryKey.append(AMPERSAND + SITE_ID + EQUAL + pSearchQuery.getSiteId());

            	endecaQueryVO.setQueryString(cacheableQueryKey.toString());
            	endecaQueryVO.setQueryObject(queryObject);

            }catch(ENEQueryException queryException){
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR_ENDECA_1001,"ENEQuery Exception ", queryException);
		}
		
		return endecaQueryVO;
	}


	
	
	/**
	 * This method will take endeca query and append Guided selling related endeca query
	 * @param pSearchQuery
	 * @param strNrEndecaQuery
	 * @return String
	 */
	private String parseQueryForGS(final SearchQuery pSearchQuery) {
		int i =0;
		String strNrEndecaQuery = null;
		Map<String, String> gsQueryMap;
		String keyEndecaQuery;		
		gsQueryMap =pSearchQuery.getGsEndecaQuery();
		Set<String> gsEndecaQuery = gsQueryMap.keySet();
		Iterator<String> itGs = gsEndecaQuery.iterator();  
		while(itGs.hasNext()){		  
			keyEndecaQuery = (String) itGs.next();
			if(i==0){
				strNrEndecaQuery = gsQueryMap.get(keyEndecaQuery) ;  
			}else{
				//strNrEndecaQuery = BBBEndecaConstants.AND + strNrEndecaQuery +BBBEndecaConstants.STRING_DEL+ gsQueryMap.get(keyEndecaQuery) + BBBEndecaConstants.STRING_END;  
				StringBuffer EndecaQuery = new StringBuffer();
				EndecaQuery.append(BBBEndecaConstants.AND);
				EndecaQuery.append(strNrEndecaQuery);
				EndecaQuery.append(BBBEndecaConstants.STRING_DEL);
				EndecaQuery.append(gsQueryMap.get(keyEndecaQuery));
				EndecaQuery.append(BBBEndecaConstants.STRING_END);
				strNrEndecaQuery = EndecaQuery.toString();
			}		  
			i++;
		} 	    
		return strNrEndecaQuery;
	}
	
	/**
	 * @return
	 */
	public String refineQuery(final String pQueryURL) {
		// Updated the code for R 2.2 SEO friendly URL Story : Start
		StringBuffer queryBuffer = new StringBuffer();
		String prefixString = null;
		boolean firstIndex = true;
		
		String[] queryParams = pQueryURL.split("&");
		if(queryParams != null & queryParams.length >0){
			
			for(String param : queryParams){
				if(!BBBUtility.isEmpty(param) && param.contains(BBBEndecaConstants.NEGATIVE_RECORD_FILTER+"=")){
					continue;
				}
				if(!BBBUtility.isEmpty(param) && param.contains(BBBEndecaConstants.NAVIGATION+"=")){
					prefixString = param.substring(param.indexOf(BBBEndecaConstants.NAVIGATION+"=") + 2);
					prefixString = prefixString.replaceAll(" ", "-");
				}else {
					if(firstIndex){
						queryBuffer.append(param);
						firstIndex = false;
					}else{
						queryBuffer.append("&" + param);
					}
					
				}
			}
			
		}
		
		if(!BBBUtility.isEmpty(prefixString)){
			queryBuffer = queryBuffer.insert(0, prefixString + "?");
		}
		// Updated the code for R 2.2 SEO friendly URL Story : End
		// Commented for R2.2 SEO friendly Story : Start
		/*// set Navigation refinement parameter.
		if(pQueryURL.contains(NAV_REFINEMENT+"=")){
			queryBuffer.replace(queryBuffer.indexOf(NAV_REFINEMENT+"="), queryBuffer.indexOf(NAV_REFINEMENT+"=")+3, "CatalogRefId=");
		}*/
		// Commented for R2.2 SEO friendly Story : End
		//set actual keyword.
		if(pQueryURL.contains(BBBEndecaConstants.NAV_KEYWORD+"=")){
			queryBuffer.replace(queryBuffer.indexOf(BBBEndecaConstants.NAV_KEYWORD+"="), queryBuffer.indexOf(BBBEndecaConstants.NAV_KEYWORD+"=")+4, "Keyword=");
		}
		// set search field / property to search on.
		if(pQueryURL.contains(BBBEndecaConstants.NAV_PROPERTY_NAME+"=")){
			queryBuffer.replace(queryBuffer.indexOf(BBBEndecaConstants.NAV_PROPERTY_NAME+"="), queryBuffer.indexOf(BBBEndecaConstants.NAV_PROPERTY_NAME+"=")+4, "SearchField=");
		}
		
		return subRefineQuery(queryBuffer.toString());
	}

	/**
	 * @param queryString
	 * @return
	 */
	private String subRefineQuery(final String queryString){

		final StringBuffer queryBuffer = new StringBuffer(queryString);
		// Set sort field
		// Commented for R2.2 SEO friendly Story : Start
		/*if(queryString.contains(NAV_SORT_FIELD+"=")){
			queryBuffer.replace(queryBuffer.indexOf(NAV_SORT_FIELD+"="), queryBuffer.indexOf(NAV_SORT_FIELD+"=")+3, "pagSortOpt=");
		}*/
		// Commented for R2.2 SEO friendly Story : End
		// set Search mode
		if(queryString.contains(BBBEndecaConstants.NAV_SEARCH_MODE+"=")){
			queryBuffer.replace(queryBuffer.indexOf(BBBEndecaConstants.NAV_SEARCH_MODE+"="), queryBuffer.indexOf(BBBEndecaConstants.NAV_SEARCH_MODE+"=")+4, "SearchMode=");
		}
		// set did you mean flag
		if(queryString.contains(BBBEndecaConstants.NAV_DID_YOU_MEAN+"=")){
			queryBuffer.replace(queryBuffer.indexOf(BBBEndecaConstants.NAV_DID_YOU_MEAN+"="), queryBuffer.indexOf(BBBEndecaConstants.NAV_DID_YOU_MEAN+"=")+4, "DidYouMean=");
		}
		// set page Num parameter
		// Commented for R2.2 SEO friendly Story : Start
		/*if(queryString.contains(NAV_REC_OFFSET+"=")){
			queryBuffer.replace(queryBuffer.indexOf(NAV_REC_OFFSET+"="), queryBuffer.indexOf(NAV_REC_OFFSET+"=")+3, "pagNum=");
		}*/
		// Commented for R2.2 SEO friendly Story : End
		// set sort order
		if(queryString.contains(BBBEndecaConstants.NAV_SORT_ORDER)){
			queryBuffer.delete(queryBuffer.indexOf(BBBEndecaConstants.NAV_SORT_ORDER+"="), queryBuffer.indexOf(BBBEndecaConstants.NAV_SORT_ORDER+"=")+5);
		}
		return queryBuffer.toString();
	}


	/** THis method takes out the content of a given Catridge and set it into a list to be returned.
	 * @param contentItem
	 * @param cartridgeName
	 * @return
	 */
/*	protected List<PromoVO> getContentList(final ContentItem pContentItem,final String cartridgeName){

	return null;
	
        final List<PromoVO> promoList = new ArrayList<PromoVO>();
        final List<ContentItem> pList = (List<ContentItem>) pContentItem.getProperty(cartridgeName).getValue();
        final Iterator<?> iterator = pList.iterator();
        PromoVO pPromoVO = null;
        
        while (iterator.hasNext()) {
	        	String promoImageLink = null;
	            String promoImage = null;
	            String promoImageWidth = null;
	            String promoImageHeight = null;
	            String seoTxt= null;
	            String promoImageAlt = null;
	            String blurbTxt = null;
	            String relatedSearchString = null;
	            String mobilePromoImageLink = null;
	            String mobilePromoImageSrc = null;
	            String mobilePromoImageAlt = null;
              final ContentItem contentItem = (ContentItem) iterator.next();

              pPromoVO = new PromoVO();

              final Property imageProperty = contentItem.getProperty(getEndecaSearch().getCatridgePropertyMap().get(IMAGE_PATH));
              final Property captionProperty = contentItem.getProperty(getEndecaSearch().getCatridgePropertyMap().get(CAPTION));
              final Property linkProperty = contentItem.getProperty(getEndecaSearch().getCatridgePropertyMap().get(IMAGE_TARGET_URL));
              final Property imageHref = contentItem.getProperty(getEndecaSearch().getCatridgePropertyMap().get(IMAGE_HREF));
              final Property imageSrc = contentItem.getProperty(getEndecaSearch().getCatridgePropertyMap().get(IMAGE_SRC));
              final Property seoText = contentItem.getProperty(getEndecaSearch().getCatridgePropertyMap().get(SEO_TEXT));
              final Property imageAlt = contentItem.getProperty(getEndecaSearch().getCatridgePropertyMap().get(IMAGE_ALT));
              final Property blurbText = contentItem.getProperty(getEndecaSearch().getCatridgePropertyMap().get(FOOTER_TEXT));
              final Property relatedSearch = contentItem.getProperty(getEndecaSearch().getCatridgePropertyMap().get(RELATED_SEARCHES));
              final Property mobImageHref = contentItem.getProperty(getEndecaSearch().getCatridgePropertyMap().get(MOBILE_IMAGE_HREF));
              final Property mobImageSrc = contentItem.getProperty(getEndecaSearch().getCatridgePropertyMap().get(MOBILE_IMAGE_SRC));
              final Property mobImageAlt = contentItem.getProperty(getEndecaSearch().getCatridgePropertyMap().get(MOBILE_IMAGE_ALT));
              
          	 //Start Added for R 2.2 568 Featured Product
              final Property featuredProductTop = contentItem.getProperty(getEndecaSearch().getCatridgePropertyMap().get(TOP_RATED_FEATURED_PRODUCT));
              final Property featuredProductNew = contentItem.getProperty(getEndecaSearch().getCatridgePropertyMap().get(NEWEST_FEATURED_PRODUCT));
              final Property featuredProductPopular = contentItem.getProperty(getEndecaSearch().getCatridgePropertyMap().get(POPULAR_FEATURED_PRODUCT));
              final Property featuredProductTrending = contentItem.getProperty(getEndecaSearch().getCatridgePropertyMap().get(TRENDING_FEATURED_PRODUCT));
              final Property featuredProductSponsored = contentItem.getProperty(getEndecaSearch().getCatridgePropertyMap().get(SPONSORED_FEATURE_PRODUCT));
              
              Map<String,String> featuredProductMap = new TreeMap<String, String>();
              
              if((featuredProductTop != null) && (featuredProductTop.getValue() != null) && (featuredProductTop.getValue() instanceof ERecList)) {
            	  featuredProductMap = getFeaturedProducts(featuredProductMap, featuredProductTop,  TOP_RATED_FEATURED_PRODUCT);
              }
              
              if((featuredProductNew != null) && (featuredProductNew.getValue() != null) && (featuredProductNew.getValue() instanceof ERecList)) {
            	  featuredProductMap = getFeaturedProducts(featuredProductMap, featuredProductNew,  NEWEST_FEATURED_PRODUCT);
              }
              
              if((featuredProductPopular != null) &&  (featuredProductPopular.getValue() != null) && (featuredProductPopular.getValue() instanceof ERecList)) {
            	  featuredProductMap = getFeaturedProducts(featuredProductMap, featuredProductPopular,  POPULAR_FEATURED_PRODUCT);
              }
              
              if((featuredProductTrending != null) &&  (featuredProductTrending.getValue() != null)  && (featuredProductTrending.getValue() instanceof ERecList)) {
            	  featuredProductMap = getFeaturedProducts(featuredProductMap, featuredProductTrending,  TRENDING_FEATURED_PRODUCT);
              }
              
              if((featuredProductSponsored != null) && (featuredProductSponsored.getValue() != null)  && (featuredProductSponsored.getValue() instanceof ERecList)) {
            	  featuredProductMap = getFeaturedProducts(featuredProductMap, featuredProductSponsored,  SPONSORED_FEATURE_PRODUCT);
              }
          	//End Added for R 2.2 568 Featured Product
              
              Image image = null;
              String caption = null;
              Link link = null;
              String target = null;

              if ((imageProperty != null) && (imageProperty.getValue() != null) ) {
                  image = (Image) imageProperty.getValue();
                  promoImage = image.getUri();
                  promoImageHeight = String.valueOf(image.getPixelHeight());
                  promoImageWidth = String.valueOf(image.getPixelWidth());
              }

              if ((captionProperty != null) && (captionProperty.getValue() != null)) {
                  caption = (String) captionProperty.getValue();
              }

              if ((linkProperty != null) && (linkProperty.getValue() != null)) {
                  link = (Link) linkProperty.getValue();
                  if ((link != null)  && (link.hasUrl())) {
                	  //link.getUrl(URL_FORMATTER,CONTROLLER);
                	  if (link.hasTarget()) {
                		  target=link.getTarget();
                	  }
                	  promoImageLink = link.getStaticUrl();
                  }
              }

              if(null != imageHref){
            	  promoImageLink = (String) imageHref.getValue();
              }
              if(null != imageSrc){
            	  promoImage = (String) imageSrc.getValue();
              }
              if(null != seoText){
            	  seoTxt = (String) seoText.getValue();
              }
              if(null != blurbText){
            	  blurbTxt = (String) blurbText.getValue();
              }
              if(null != imageAlt){
            	  promoImageAlt = (String) imageAlt.getValue();
              }
              if (null != relatedSearch) {
  				relatedSearchString = (String) relatedSearch.getValue();
  				String relatedString[] = relatedSearchString.split(BBBCatalogConstants.DELIMITERCOMMA);
  				Map<String, String> keywordMap = new HashMap<String, String>();
  				for (String commaSeperated : relatedString) {
  					// BBBSL-3455 - Start - storing sanitized search term as key and display text as value.
                    keywordMap.put(commaSeperated.trim().toLowerCase().replaceAll("[\'\"]", "").replaceAll("[^a-z0-9]", " ").replaceAll("[ ]+", "-"), commaSeperated);
                    // BBBSL-3455 - End
                    //resultString = inputString.replace(/[^a-z0-9\'\"]/gi,' ').replace(/[\'\"]/g,'').replace(/[ ]+/g,' ').toLowerCase().trim().replace(/[ ]/g,'-');

  				}
  				pPromoVO.setRelatedSeperated(keywordMap);
  			}
              if(null != mobImageHref){
            	  mobilePromoImageLink = (String) mobImageHref.getValue();
              }
              if(null != mobImageSrc){
            	  mobilePromoImageSrc = (String) mobImageSrc.getValue();
              }
              if(null != mobImageAlt){
            	  mobilePromoImageAlt = (String) mobImageAlt.getValue();
              }
              // Populating Promo VO Object.
              pPromoVO.setImageHref(promoImageLink);
	    	  pPromoVO.setLinkTarget(target);
              pPromoVO.setImageSrc(promoImage);
	    	  pPromoVO.setImageHeight(promoImageHeight);
	    	  pPromoVO.setImageWidth(promoImageWidth);
	    	  pPromoVO.setImageAlt(promoImageAlt);
	    	  pPromoVO.setSeoText(seoTxt);
	    	  pPromoVO.setCaption(caption);
	    	  pPromoVO.setBlurbPlp(blurbTxt);
	    	  pPromoVO.setRelatedSearchString(relatedSearchString);
	    	  pPromoVO.setMobileImageHref(mobilePromoImageLink);
	    	  pPromoVO.setMobileImageSrc(mobilePromoImageSrc);
	    	  pPromoVO.setMobileImageAlt(mobilePromoImageAlt);
	    	  pPromoVO.setFeaturedProducts(featuredProductMap);

	    	  promoList.add(pPromoVO);
        }
        return promoList;
	}*/

	//Start Added for R 2.2 568 featured Product 

	/**
	 * This method id used to Iterate for Featured Products.
	 * Returns Map of Featured Product with ProductId as key and featured product as value. 
	 * @param featureProductMap Feature Product Property Map
	 * @param featureProperty ERec received from Endeca
	 * @param featureType Name of Featured Product Property set in Map
	 * @return
	 */
	/*public Map<String, String> getFeaturedProducts(Map<String, String> featureProductMap, Property featureProperty , String featureType){
		
		logDebug("Start Fetching details for Featured Property " + featureType);
		
		ERecList recordsList = (ERecList)featureProperty.getValue();
		if(recordsList != null){
			final ListIterator iterRecords = recordsList.listIterator();
			
				ERec record = null;
				PropertyMap properties = null;
				String match = null;
			
				while (iterRecords.hasNext() && featureProductMap.size() <= 5) {
					record = (ERec) iterRecords.next();
					properties = record.getProperties();
					match = (String) properties.get(FEATURED_PRODUCT_ID);
					for(int j=0; j <= featureProductMap.size(); j++){
						if(featureProductMap.containsKey(match)){
							
							logDebug("Product Id : " + match + ", already exists in Featured Product Map.");
							
							String existingPriority = getPriorityFeaturesMap().get(featureProductMap.get(match));
							String newPriority = getPriorityFeaturesMap().get(featureType);
							if(Integer.parseInt(existingPriority) > Integer.parseInt(newPriority)){
								
								logDebug("Updating Featured Product Map, Removing Existing Feature : " + featureProductMap.get(match));
								logDebug("Adding New Feature : " + featureType);
								
								featureProductMap.remove(match);
								featureProductMap.put(match, featureType);
							}
						}else{
							featureProductMap.put(match, featureType);
							
							logDebug("Adding New Featured Product Property for Product ID : " + match + " and feature property : " + featureType);
							
						}
					}
					
				}
		}else{
			
			logDebug("No result found for Featured Property : " + featureType);
			
		}
		return featureProductMap;
	}*/
	//End Added for R 2.2 568 featured Product
	/**
	 * This method is used to prepare search query which was entered in
	 * the BCC popular items section
	 * @param pBccQuery - Query entered in Search Box
	 * @param pSiteId - Site Id
	 * @return ENEContentQuery - returns ENE query.
	 */
	/*public ENEContentQuery   getPopularItemsQuery(String pBccQuery , String pSiteId , int pRecordNo) {

		BBBPerformanceMonitor.start( EndecaQueryGenerator.class.getName() + " : " + "getPopularItemsQuery(pBccQuery , pSiteId , pRecordNo)");
		
		
		logDebug("Enter.EndecaQueryGenerator.getPopularItemsQuery(String pBccQuery , String pSiteId)");
	
		String searchQuery = pBccQuery;
				//getSearchPopularItemsParameter()++"&N="+siteDimId;
		ENEContentQuery contentQuery = null;
		try {
			ENEQuery queryObject = new UrlENEQuery(searchQuery, getEncoding());
			//number of products to return
			queryObject.setNavNumERecs(pRecordNo);
			//avoid returning refinements for the search results
			queryObject.setNavAllRefinements(false);	
			
			try {
				ENEContentManager contentManageryrs = new ENEContentManager();
				contentManageryrs = new ENEContentManager();
				contentQuery = (ENEContentQuery) contentManageryrs.createQuery();
			} catch (ContentException contentExce) {
				logError(contentExce.getMessage());
				
			}catch (InitializationException InitialExe) {
				
				logError(InitialExe.getMessage());					
				
			}
			contentQuery.setRuleZone(NAV_PAGE_ZONE);
			contentQuery.setENEQuery(queryObject);
			
		} catch (UrlENEQueryParseException urlEncodingExe) {
			logError(urlEncodingExe.getMessage());					
			
		}catch (Exception genericEx) {
			
			logError("Generice Exception........"+genericEx.getMessage());					
			
		}
		
		logDebug("Exit.EndecaQueryGenerator.getPopularItemsQuery(String pBccQuery , String pSiteId)");
		
		
		BBBPerformanceMonitor.end( EndecaQueryGenerator.class.getName() + " : " + "getPopularItemsQuery(pBccQuery , pSiteId , pRecordNo)");
		
	 return contentQuery;
	}*/

	public String getSortField() {
		return sortField;
	}

	public void setSortField(final String pSortField) {
		this.sortField = pSortField;
	}

	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @param pPageSize the pageSize to set
	 */
	public void setPageSize(final int pPageSize) {
		pageSize = pPageSize;
	}

	/**
	 * @return the searchField
	 */
	public String getSearchField() {
		return searchField;
	}

	/**
	 * @param pSearchField the searchField to set
	 */
	public void setSearchField(final String pSearchField) {
		searchField = pSearchField;
	}

	/**
	 * @return the didYouMean
	 */
	public String getDidYouMean() {
		return didYouMean;
	}

	/**
	 * @param pDidYouMean the didYouMean to set
	 */
	public void setDidYouMean(final String pDidYouMean) {
		didYouMean = pDidYouMean;
	}

	public String getQueryParamNtpc() {
		return queryParamNtpc;
	}

	public void setQueryParamNtpc(String pQueryParamNtpc) {
		this.queryParamNtpc = pQueryParamNtpc;
	}

	public String getQueryParamNtpr() {
		return queryParamNtpr;
	}

	public void setQueryParamNtpr(String pQueryParamNtpr) {
		this.queryParamNtpr = pQueryParamNtpr;
	}

	/**
	 * @return the encoding
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * @param pEncoding the encoding to set
	 */
	public void setEncoding(final String pEncoding) {
		encoding = pEncoding;
	}

	/**
	 * @return the queryURL
	 *//*
	public String getQueryURL() {
		return queryURL;
	}

	*//**
	 * @param pQueryURL the queryURL to set
	 *//*
	public void setQueryURL(final String pQueryURL) {
		queryURL = pQueryURL;
	}

	*//**
	 * @return the sortString
	 *//*
	public String getSortString() {
		return sortString;
	}

	*//**
	 * @param pSortString the sortString to set
	 *//*
	public void setSortString(final String pSortString) {
		sortString = pSortString;
	}
*/
	public EndecaSearch getEndecaSearch() {
		return endecaSearch;
	}

	public void setEndecaSearch(final EndecaSearch endecaSearch) {
		this.endecaSearch = endecaSearch;
	}

	public String getSearchMode() {
		return searchMode;
	}

	public void setSearchMode(final String searchMode) {
		this.searchMode = searchMode;
	}

	public String getSearchModeAll() {
		return searchModeAll;
	}

	public void setSearchModeAll(final String searchModeAll) {
		this.searchModeAll = searchModeAll;
	}

	/**
	 * @return the endecaClient
	 */
	public EndecaClient getEndecaClient() {
		return endecaClient;
	}

	/**
	 * @param endecaClient the endecaClient to set
	 */
	public void setEndecaClient(final EndecaClient endecaClient) {
		this.endecaClient = endecaClient;
	}

	public MutableRepository getSiteRepository() {
		return siteRepository;
	}

	public void setSiteRepository(MutableRepository siteRepository) {
		this.siteRepository = siteRepository;
	}

	public String getPriceFilterField() {
		return priceFilterField;
	}

	public void setPriceFilterField(String priceFilterField) {
		this.priceFilterField = priceFilterField;
	}

	/**
	 * @return the dimNonDisplayMapConfig
	 */
	public String getDimNonDisplayMapConfig() {
		return dimNonDisplayMapConfig;
	}

	/**
	 * @param dimNonDisplayMapConfig the dimNonDisplayMapConfig to set
	 */
	public void setDimNonDisplayMapConfig(String dimNonDisplayMapConfig) {
		this.dimNonDisplayMapConfig = dimNonDisplayMapConfig;
	}

	/**
	 * @return the boostBurrySort
	 */
	public String getBoostBurrySort() {
		try {
			boostBurrySort=getCatalogTools().getAllValuesForKey(getDimNonDisplayMapConfig(),defaultKey).get(0);
		} catch (BBBSystemException e) {
			logError("System Exception occurred while fetching boostburry key from dimnondisplayconfigtype", e);

			return null;
		} catch (BBBBusinessException e) {
			logError("Business Exception occurred while fetching boostburry key from dimnondisplayconfigtype", e);
					return null;
		}
		return boostBurrySort;
	}

	/**
	 * @param boostBurrySort the boostBurrySort to set
	 */
	public void setBoostBurrySort(String boostBurrySort) {
		this.boostBurrySort = boostBurrySort;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param pCatalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}

	/**
	 * @return the queryParamNrc
	 */
	public String getQueryParamNrc() {
		return queryParamNrc;
	}

	/**
	 * @param queryParamNrc the queryParamNrc to set
	 */
	public void setQueryParamNrc(String pqueryParamNrc) {
		this.queryParamNrc = pqueryParamNrc;
	}
	/**
	 * Method to replace the special characters for the given keyword 
	 * 
	 * @return String with no special characters
	 */
	public String replaceSpecialCharacters(String pKeyword) {
		String keyword=null;
		if(BBBUtility.isNotEmpty(pKeyword)){
		keyword = pKeyword.replaceAll("'", "");
		keyword = ((keyword.replaceAll("[^0-9A-Za-z\"\']"," ")).replaceAll("[\']", "")).replaceAll(" +", " ");
		keyword = keyword.replaceAll("-", " ");
		}
		return keyword;
	}

	/**
	 * @return number of omniture records for search term
	 */
	public int getOmnitureRecordCount() {
		return omnitureRecordCount;
	}


	public void setOmnitureRecordCount(int omnitureRecordCount) {
		this.omnitureRecordCount = omnitureRecordCount;
	}


	/**
	 * @return Ntx param with match all exact value
	 */
	public String getNtxParamMatchAllExact() {
		return ntxParamMatchAllExact;
	}


	public void setNtxParamMatchAllExact(String ntxParamMatchAllExact) {
		this.ntxParamMatchAllExact = ntxParamMatchAllExact;
	}


	/**
	 * @return the endecaSearchUtil
	 */
	public EndecaSearchUtil getEndecaSearchUtil() {
		return endecaSearchUtil;
	}


	/**
	 * @param endecaSearchUtil the endecaSearchUtil to set
	 */
	public void setEndecaSearchUtil(EndecaSearchUtil endecaSearchUtil) {
		this.endecaSearchUtil = endecaSearchUtil;
	}
}
