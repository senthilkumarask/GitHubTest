package com.bbb.search.endeca;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import atg.core.util.StringUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.endeca.constants.BBBEndecaConstants;
import com.bbb.search.endeca.vo.EndecaQueryVO;
import com.bbb.utils.BBBUtility;
/*import com.endeca.content.ContentException;
import com.endeca.content.InitializationException;
import com.endeca.content.ene.ENEContentManager;
import com.endeca.content.ene.ENEContentQuery;*/
import com.endeca.navigation.ENEQuery;
import com.endeca.navigation.ENEQueryException;
import com.endeca.navigation.ERecSortKey;
import com.endeca.navigation.ERecSortKeyList;
import com.endeca.navigation.UrlENEQuery;
import com.endeca.navigation.UrlGen;

public class TBSEndecaQueryGenerator extends EndecaQueryGenerator {

	//private ENEContentManager contentManager;

	private String mUpcSearchMode;

	private String mUpcSearchField;

	/**
	 * @return the upcSearchMode
	 */
	public String getUpcSearchMode() {
		return mUpcSearchMode;
	}

	/**
	 * @return the upcSearchField
	 */
	public String getUpcSearchField() {
		return mUpcSearchField;
	}

	/**
	 * @param pUpcSearchMode
	 *            the upcSearchMode to set
	 */
	public void setUpcSearchMode(String pUpcSearchMode) {
		mUpcSearchMode = pUpcSearchMode;
	}

	/**
	 * @param pUpcSearchField
	 *            the upcSearchField to set
	 */
	public void setUpcSearchField(String pUpcSearchField) {
		mUpcSearchField = pUpcSearchField;
	}

	@SuppressWarnings("unchecked")
	public EndecaQueryVO generateEndecaQuery(final SearchQuery pSearchQuery)
			throws BBBBusinessException, BBBSystemException {

		logDebug("TBSEndecaQueryGenerator :: generateEndecaQuery() method :: START");

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
		
		
		String channelDimId = null;
		String queryString = null;
		String brandName = "";
		//ENEContentQuery contentQuery = null;
		EndecaQueryVO endecaQueryVO = new EndecaQueryVO();
		StringBuffer cacheableQueryKey = new StringBuffer();

		String strNrEndecaQuery = null;

		// Get the Dimension Value Id for current Site ID.
		String siteDimId = getEndecaSearch().getCatalogId(TBSConstants.SITE_ID2,getEndecaSearch().getSiteIdMap().get(pSearchQuery.getSiteId()));
		 
		String searchTypeCheck = pSearchQuery.getCatalogRef().get("searchType");
		if(null!= searchTypeCheck && searchTypeCheck.equals("UPC_Search")){
			queryString = TBSConstants.NAVIGATION+"=0&";
		}else{
		if(BBBUtility.isNotEmpty(pSearchQuery.getChannelId())){
			// Get the Dimension Value Id for current channel	 
			if(getEndecaSearch().getCatalogId(TBSConstants.CHANNEL,pSearchQuery.getChannelId()) != null){
				channelDimId = getEndecaSearch().getCatalogId(TBSConstants.CHANNEL,pSearchQuery.getChannelId());	
			}else{
				logError("Error while getting dimension id for channel");
				throw new BBBSystemException(BBBCoreErrorConstants.ERROR_CHANNEL_DIMENSION, "System Exception occurred while getting dimension id from Endeca");
			}			 
			queryString = TBSConstants.NAVIGATION+"="+ siteDimId + "+" + channelDimId    +"&";
		}else{
			queryString = TBSConstants.NAVIGATION+"="+ siteDimId +"&";	
		}
		}
		List<String> default_per_page = null;
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		
		//JanPS Merge - if condition
		if (StringUtils.isEmpty(pSearchQuery.getPageSize())){
			logDebug("TBSEndecaQueryGenerator: generateEndecaQuery(): pSearchQuery pageSize is not existing");
			if(request !=null && BBBUtility.isStringPatternValid(TBSConstants.IPAD_PATTERN, request.getHeader(TBSConstants.USER_AGENT))){
				logDebug("header for ipad request is " + request.getHeader(TBSConstants.USER_AGENT));
				default_per_page = getCatalogTools().getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS, BBBCatalogConstants.PER_PAGE_DEFAULT_ON_IPAD);
			}else{
				default_per_page = getCatalogTools().getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS, BBBCatalogConstants.PER_PAGE_DEFAULT); 
			}
			
			if(default_per_page != null && !default_per_page.isEmpty()){
				int pageSize = Integer.parseInt(default_per_page.get(0));
				this.setPageSize(pageSize);
				pSearchQuery.setPageSize(default_per_page.get(0));
			}
		}//JanPS Merge - if condition
		
		/*END - Added as part of R2.2 Story - 116-D1 & 116-D2*/
		//ATG/Endeca Upgrade fix - updated if condition
		if(!StringUtils.isEmpty(pSearchQuery.getCatalogRef().get("catalogId")) && (null== searchTypeCheck || (null!= searchTypeCheck && !searchTypeCheck.equals("UPC_Search")))){
			// set navigation parameter.
			queryString = TBSConstants.NAVIGATION+"="+pSearchQuery.getCatalogRef().get("catalogId");
			if(!pSearchQuery.getCatalogRef().get("catalogId").contains(siteDimId))
			{
				queryString = TBSConstants.NAVIGATION+"="+pSearchQuery.getCatalogRef().get("catalogId")+ " " + siteDimId;
				if(BBBUtility.isNotEmpty(channelDimId) && !pSearchQuery.getCatalogRef().get("catalogId").contains(channelDimId)){
					queryString += " " + channelDimId;
				}
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
              String brandDimId = getEndecaSearch().getCatalogId(TBSConstants.BRAND, brandName);
              
              //RM Defect 23496-END
               if(null != brandDimId && BBBUtility.isNotEmpty(pSearchQuery.getCatalogRef().get("catalogId")) && !pSearchQuery.getCatalogRef().get("catalogId").contains(brandDimId)){
                     queryString += " " + brandDimId;
               }
               //BBBI-3656 | Facet search not working with l2/l3/brand
               if(BBBUtility.isNotEmpty(brandDimId)){
            	   request.getSession().setAttribute(BBBEndecaConstants.BRAND_DIM_ID, brandDimId);
               }
        }
        queryString += "&";

		final UrlGen urlGen = new UrlGen(queryString, getEncoding());
		 
		if(pSearchQuery.getGsEndecaQuery() != null ){			 
			
			strNrEndecaQuery = parseQueryForGS(pSearchQuery);
			if(!StringUtils.isEmpty(strNrEndecaQuery)){
				// set navigation refinement parameter.
				urlGen.addParam(TBSConstants.NAV_NR, strNrEndecaQuery);
			}
		}
		 
		if(!StringUtils.isEmpty(pSearchQuery.getCatalogRef().get("catalogRefId"))){
			// set navigation refinement parameter.
			urlGen.addParam(TBSConstants.NAV_REFINEMENT, pSearchQuery.getCatalogRef().get("catalogRefId"));
		}
		if(!StringUtils.isEmpty(pSearchQuery.getKeyWord()) && !BBBUtility.isEmpty(pSearchQuery.getKeyWord().trim())){
			// START JIRA Defect # BBBSL-1013
            String keyword= pSearchQuery.getKeyWord().trim();
            if(null!= searchTypeCheck){
            	urlGen.addParam(TBSConstants.NAV_PROPERTY_NAME, getUpcSearchField());
				urlGen.addParam(TBSConstants.NAV_SEARCH_MODE, getSearchMode());
				logDebug("EndecaQueryGenerator.generateEndecaQuery Ntx/searchmode for UPC search of type MIE"+getSearchMode());
            }            
            else if(("true").equalsIgnoreCase(pSearchQuery.getCatalogRef().get("fromCollege"))){
				urlGen.addParam(TBSConstants.NAV_PROPERTY_NAME, getEndecaSearch().getConfigUtil().getPropertyMap().get("PRODUCT_COLLEGE_NAME"));
				urlGen.addParam(TBSConstants.NAV_SEARCH_MODE, getSearchModeAll());
				logDebug("EndecaQueryGenerator.generateEndecaQuery Ntx/searchmode when catalogref(fromCollege) is true"+getSearchModeAll());
			}// RM 21910 : START : added for type ahead search based on product name for STOFU  
			else if(BBBUtility.isNotEmpty(pSearchQuery.getCatalogRef().get("searchProperty"))){
				urlGen.addParam(TBSConstants.NAV_PROPERTY_NAME, getEndecaSearch().getConfigUtil().getPropertyMap().get(pSearchQuery.getCatalogRef().get("searchProperty")));
				urlGen.addParam(TBSConstants.NAV_SEARCH_MODE, getSearchModeAll());
				logDebug("EndecaQueryGenerator.generateEndecaQuery Ntx/searchmode when catalogref(searchProperty) is not empty "+getSearchModeAll());
			}// RM 21910 : END : added for type ahead search based on product name for STOFU
			else{
              // START JIRA Defect # BBBSL-1013
               if(!("true").equalsIgnoreCase(pSearchQuery.getCatalogRef().get("frmBrandPage"))){
            	   	 keyword = keyword.replaceAll("'", "");
					 keyword = ((keyword.replaceAll("[^0-9A-Za-z\"\']"," ")).replaceAll("[\']", "")).replaceAll(" +", " ");
                     keyword = keyword.replaceAll("-", " ");
                     if(BBBUtility.isEmpty(keyword.trim())){
                    	 return null;
                     }
              }
               else if(("true").equalsIgnoreCase(pSearchQuery.getCatalogRef().get("frmBrandPage"))){
            	   // empty keyword as we have brand id in keyword in case of brand page 
            	   keyword = brandName;
               }
              // END   JIRA Defect # BBBSL-1013
				urlGen.addParam(TBSConstants.NAV_PROPERTY_NAME, getSearchField());
			}
			
			if(BBBUtility.isNotEmpty(pSearchQuery.getSearchMode())){
				urlGen.addParam(TBSConstants.NAV_SEARCH_MODE, pSearchQuery.getSearchMode());
				logDebug("EndecaQueryGenerator.generateEndecaQuery Ntx/searchmode is set from URL or partial Logic(92F) "+pSearchQuery.getSearchMode());
			}
			else if(("true").equalsIgnoreCase(pSearchQuery.getCatalogRef().get("fromCollege")) || BBBUtility.isNotEmpty(pSearchQuery.getCatalogRef().get("searchProperty"))){
				urlGen.addParam(TBSConstants.NAV_SEARCH_MODE, getSearchModeAll());
				logDebug("EndecaQueryGenerator.generateEndecaQuery Ntx/searchmode when catalogref(fromCollege) is true (searchProperty) is not empty"+getSearchModeAll());
			}
			else{
				urlGen.addParam(TBSConstants.NAV_SEARCH_MODE, getSearchMode());
				logDebug("EndecaQueryGenerator.generateEndecaQuery default search Mode"+getSearchMode());
			}
			
            // START JIRA Defect # BBBSL-1013
            urlGen.addParam(TBSConstants.NAV_KEYWORD, keyword.trim().toLowerCase());
            logDebug("Keyword searched for : "+ keyword.trim().toLowerCase());
            // END   JIRA Defect # BBBSL-1013
			urlGen.addParam(TBSConstants.NAV_DID_YOU_MEAN, getDidYouMean());
			urlGen.addParam(TBSConstants.NAV_SEARCH_PHRASES_NTPC, getQueryParamNtpc());
			urlGen.addParam(TBSConstants.NAV_SEARCH_PHRASES_NTPR, getQueryParamNtpr());
		}
		else if(!pSearchQuery.isHeaderSearch()){
			//R2.1.1 Scope #519 Sorting of Brands only on PLP  - Start
			urlGen.addParam(TBSConstants.NAV_SEARCH_PHRASES_NRC, getQueryParamNrc());
			//R2.1.1 Scope #519 Sorting of Brands only on PLP  - End
		}
		if(null!=pSearchQuery.getPageSize()){
			setPageSize(Integer.parseInt(pSearchQuery.getPageSize()));
		}

		if(!StringUtils.isEmpty(pSearchQuery.getPageNum())){
			final long pageNum = Long.parseLong(pSearchQuery.getPageNum());
			final Long recordOffset = (getPageSize() * (pageNum - 1));
			urlGen.addParam(TBSConstants.NAV_REC_OFFSET, recordOffset.toString());
		}

		queryString = urlGen.toString();
		cacheableQueryKey.append(queryString);
		cacheableQueryKey.append(BBBCoreConstants.AMPERSAND + TBSConstants.PARAM_PAGESIZE + BBBCoreConstants.EQUAL + getPageSize());

		//ATG/Endeca Upgrade fix
		//setting page size so that results list handler uses this 
		pSearchQuery.setPageSize(""+getPageSize());

		pSearchQuery.setQueryURL(queryString);
		logDebug("Query String: " +queryString);
		ENEQuery queryObject = null;
		try{
		queryObject = new UrlENEQuery(queryString, getEncoding());
		queryObject.setNavNumERecs(getPageSize());
		queryObject.setNavAllRefinements(true);
		if(null!= searchTypeCheck && searchTypeCheck.equals("UPC_Search")){
			queryObject.setNavAllRefinements(false);
		}

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

		cacheableQueryKey.append(BBBCoreConstants.AMPERSAND + TBSConstants.PARAM_KEY + BBBCoreConstants.EQUAL + key);
		cacheableQueryKey.append(BBBCoreConstants.AMPERSAND + TBSConstants.PARAM_SORT_STRING + BBBCoreConstants.EQUAL + pSearchQuery.getSortString());

		// Setting the Record Filter for site retrieving site specific content.
		cacheableQueryKey.append(BBBCoreConstants.AMPERSAND + BBBCoreConstants.SITE_ID + BBBCoreConstants.EQUAL + pSearchQuery.getSiteId());

		/*if(null == contentManager){
			contentManager = new ENEContentManager();
		}
		contentQuery = (ENEContentQuery) contentManager.createQuery();
		contentQuery.setRuleZone(TBSConstants.NAV_PAGE_ZONE);
		contentQuery.setENEQuery(queryObject);*/

		//endecaQueryVO.setEndicaContentQuery(contentQuery);
		endecaQueryVO.setQueryString(cacheableQueryKey.toString());
		
		
		}catch(ENEQueryException queryException){
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR_ENDECA_1001,"ENEQuery Exception ", queryException);
		} /*catch (InitializationException e) {
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR_ENDECA_1002,"Endeca Initialization Exception ", e);
		} catch (ContentException contentException) {
			throw new BBBSystemException(BBBCoreErrorConstants.ERROR_ENDECA_1003,"Endeca Content Exception ", contentException);
		}*/
		
		
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
//				strNrEndecaQuery = TBSConstants.AND + strNrEndecaQuery +TBSConstants.STRING_DEL+ gsQueryMap.get(keyEndecaQuery) + TBSConstants.STRING_END;  
				StringBuffer EndecaQuery = new StringBuffer();
				EndecaQuery.append(TBSConstants.AND);
				EndecaQuery.append(strNrEndecaQuery);
				EndecaQuery.append(TBSConstants.STRING_DEL);
				EndecaQuery.append(gsQueryMap.get(keyEndecaQuery));
				EndecaQuery.append(TBSConstants.STRING_END);
				strNrEndecaQuery = EndecaQuery.toString();
			}		  
			i++;
		} 	    
		return strNrEndecaQuery;
	}

}
