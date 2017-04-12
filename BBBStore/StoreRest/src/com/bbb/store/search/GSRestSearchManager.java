
/**
 * This class is stofu specific and used for perform search.
 */
package com.bbb.store.search;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;

import atg.servlet.ServletUtil;

import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.commerce.catalog.vo.AllCategoriesVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.rest.search.RestSearchManager;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.bean.result.BBBProduct;
import com.bbb.search.bean.result.BBBProductList;
import com.bbb.search.bean.result.CategoryParentVO;
import com.bbb.search.bean.result.FacetParentVO;
import com.bbb.search.bean.result.FacetQueryResult;
import com.bbb.search.bean.result.FacetQueryResults;
import com.bbb.search.bean.result.FacetRefinementVO;
import com.bbb.search.bean.result.RootCategoryRefinementVO;
import com.bbb.search.bean.result.SearchResults;
import com.bbb.search.vo.CategoryNavigationVO;
import com.bbb.utils.BBBUtility;

public class GSRestSearchManager extends RestSearchManager {
	private static final String EMPTY_STRING = "";
	private static final String PERFORM_GS_SEARCH = "performGSSearch";

	private static final String CATEGORY_DEL = ">";

	private static final String DEPARTMENT = "DEPARTMENT";

	private static final String STRING_APPEND = "\",\"searchProperty\":\"PRODUCT_TITLE\"}}";

	private static final String PERFORM_SEARCH_KEYWORD = "{\"performSearch\":{\"keyword\":\"";

	private static final String TYPE_AHEAD_PRODUCT_LIMIT = "typeAheadProductLimit";

	private static final String CONTENT_CATALOG_KEYS = "ContentCatalogKeys";

	private static final String PERFORM_GS_TYPE_AHEAD_SEARCH = "performGSTypeAheadSearch";
	
	private static final String GET_ALL_CATEGORIES = "getAllCategories";

	private static final String PERFORM_SEARCH = "performSearch";

	private static final String CHANNEL_ID = "channelID";
	private static final String CHANNEL_THEME_ID = "channelThemeID";

	/** String for channelID */
	private String channelIdProperty;	
	public String getChannelIdProperty() {
		return channelIdProperty;	}	

	public void setChannelIdProperty(String channelIdProperty) {
		this.channelIdProperty = channelIdProperty;
	}
	
	/**String for channelThemeID*/
	private String channelThemeIdProperty;	
	public String getChannelThemeIdProperty() {
		return channelThemeIdProperty;
	}
	public void setChannelThemeIdProperty(String channelThemeIdProperty) {
		this.channelThemeIdProperty = channelThemeIdProperty;
	}

	/**
	 * This method is wrapper over super class performSearch method.
	 * This method will get channel id from request and set it into input map and call super class method.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SearchResults performSearch(String jsonSearchString) throws BBBSystemException,BBBBusinessException{
		SearchResults searchResult = new SearchResults();		 
		Map searchQueryMap = new HashMap();
		JSONObject jsonObject = null;
		List<String> lstGsQuery = new ArrayList<String>();
		BBBPerformanceMonitor.start(PERFORM_GS_SEARCH);		
		logDebug("GSRestSearchManager.performSearch starts, search string : "+jsonSearchString);
		
		if(jsonSearchString != null){			 
			try {
				jsonObject = (JSONObject) JSONSerializer.toJSON(jsonSearchString);			
				DynaBean JSONResultbean = (DynaBean) JSONSerializer.toJava(jsonObject); 
				if(JSONResultbean !=null){
					DynaBean JSONSearchbean=  (DynaBean)JSONResultbean.get(PERFORM_SEARCH); 
					if(JSONSearchbean != null){
						DynaProperty properties[] = JSONSearchbean.getDynaClass().getDynaProperties();
						for (int i = 0; i < properties.length; i++) {
							String name = properties[i].getName();
							if(JSONSearchbean.get(name) instanceof String){
								String value =(String)JSONSearchbean.get(name);
								
								logDebug("Search String : Key" + i+ " ::"+ name);
								logDebug("Search String : value" + i+ " ::"+ value);
								
								searchQueryMap.put(name, value);
							}else if(JSONSearchbean.get(name) instanceof List){
								List<DynaBean> lstsearchBean = (List<DynaBean>) JSONSearchbean.get(name);
								Iterator it = lstsearchBean.iterator();
								while(it.hasNext()){
									//DynaBean dynBean = (DynaBean) it.next();								
									lstGsQuery.add((String)it.next());									
								}
								if(!lstGsQuery.isEmpty()){
									searchQueryMap.put("GSQuerykey", lstGsQuery);	
								}			
								
								logDebug("Search String : Key" + i+ " ::"+ name);
								
							}
						}	
					}else{
						logError("Error while parsing json string");
						BBBPerformanceMonitor.end(PERFORM_GS_SEARCH);
						throw new BBBSystemException(BBBCoreErrorConstants.ERROR_PARSE_SEARCH_JSON, "System Exception occurred while parsing passed search json string");
					}					
				}
			} catch (Exception e) {
				logError("Error while parsing json string :" + e.getMessage());
				BBBPerformanceMonitor.end(PERFORM_GS_SEARCH);
				throw new BBBSystemException(BBBCoreErrorConstants.ERROR_PARSE_SEARCH_JSON, "System Exception occurred while parsing passed search json string");
			}
			if(!BBBUtility.isMapNullOrEmpty(searchQueryMap)){

				searchResult  = performGSSearch(searchQueryMap);
			}	
		}	
		
		logDebug("GSRestSearchManager.performSearch ends");
		
		BBBPerformanceMonitor.end(PERFORM_GS_SEARCH);	
		return searchResult;
	}	 






	@SuppressWarnings("unchecked")
	public SearchResults performGSSearch(Map<String,?> queryParams) throws BBBSystemException,BBBBusinessException{
		String Key= null;
		SearchResults searchResult = new SearchResults();		
		Map<String,String> mapGSQuery = new HashMap<String, String>();
		List<String> lstGsQuery = new ArrayList<String>();
		SearchQuery pQuery = new SearchQuery();
		String channelId = null;
		String channelThemeId=null;
		if(queryParams !=null && !queryParams.isEmpty()){	

			pQuery = super.createEndecaQuery(queryParams);

			if(null != ServletUtil.getCurrentRequest()){
				channelThemeId = ServletUtil.getCurrentRequest().getHeader(getChannelThemeIdProperty());
				channelId = ServletUtil.getCurrentRequest().getHeader(getChannelIdProperty());
				if(BBBUtility.isNotEmpty(channelThemeId)){
					if (isLoggingDebug()) {
						logDebug("ChannelTheme Id "+channelThemeId);
					}
					pQuery.setChannelThemeId(channelThemeId);
				}
				else{
					if (isLoggingDebug()) {
						logDebug("ChannelTheme Id is not present");
					}
				}
				if(BBBUtility.isNotEmpty(channelId)){
					
					logDebug("Channel Id "+channelId);
					
					pQuery.setChannelId(channelId);	 
				}else{
					logError("Channel Id is not present in the header");
					//throw new BBBBusinessException(BBBCoreErrorConstants.ERROR_HEADER_CHANNEL, "Channel Id should be present at header");
				} 
			}				 


			if(queryParams.get("GSQuerykey") !=null){
				lstGsQuery =  (List<String>) queryParams.get("GSQuerykey");
				if(!lstGsQuery.isEmpty()){
					for(int i=0 ; i<lstGsQuery.size(); i++){
						Key = "GSQuery" + i;
						mapGSQuery.put(Key,lstGsQuery.get(i));
					}
				}
			} 
			pQuery.setGsEndecaQuery(mapGSQuery);   


			// Check If the request is to fetch all results associated with a Brand.
			if(("true").equalsIgnoreCase((String)queryParams.get("frmGSTypeAhead"))){
				pQuery.setgSTypeAhead(true);
				//				catalogRef = pQuery.getCatalogRef();
				//				catalogRef.put("frmGSTypeAhead", "true");
				//				pQuery.setCatalogRef(catalogRef);
			}
			else{
				pQuery.setgSTypeAhead(false);
			}


			// Querying Search Engine with the populated VO to return Results Object. 

			searchResult =  getSearchManager().performSearch(pQuery);		 


		}
		return searchResult;
	}




	/**
	 * STOFU related
	 * Returns Tree structure for header
	 * 
	 * The tree strucutre contains all level of categories in tree format 
	 * including top level (root), 2nd level and 3rd level categories
	 * 
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public AllCategoriesVO getAllCategories() throws BBBSystemException,
			BBBBusinessException {
		Map<String, String> queryParams = new HashMap<String, String>();
		String channelID = EMPTY_STRING;
		String channelThemeID = EMPTY_STRING;
		
		logDebug("Inside class: GSRestSearchManager,  method :getAllCategories");
		
		BBBPerformanceMonitor.start(GET_ALL_CATEGORIES);

		if (null != ServletUtil.getCurrentRequest()) {
			channelID = ServletUtil.getCurrentRequest().getHeader(
					getChannelIdProperty());
			channelThemeID=ServletUtil.getCurrentRequest().getHeader(getChannelThemeIdProperty());			
		}

		queryParams.put(CHANNEL_ID, channelID);
		if(channelThemeID != null){
			queryParams.put(CHANNEL_THEME_ID, channelThemeID);
		}
		else{
			queryParams.put(CHANNEL_THEME_ID, EMPTY_STRING);
		}
		return super.getAllCategories(queryParams);	
	}

	/**
	 * Purpose of this method is to perform type ahead search for Guided Selling.This method will do type ahead search based on Brand,
	 * Department and product title. 	
	 * @param searchKeyword
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public GSTypeAheadVO performGSTypeAheadSearch(final String searchKeyword) throws BBBSystemException,BBBBusinessException{

		
		logDebug("GSRestSearchManager.performGSTypeAheadSearch starts with Keyword :"+searchKeyword);
		
		BBBPerformanceMonitor.start(PERFORM_GS_TYPE_AHEAD_SEARCH);
		String strNoOfProduct = null;
		int countProduct=1;
		GSTypeAheadVO obTypeAheadVO =new GSTypeAheadVO(); 
		BBBProduct obBBBProduct;
		List<BBBProduct> lstBbbProducts = new ArrayList<BBBProduct>();
		List<String> lstCatalogConfigKeys = new ArrayList<String>();
		List<GSProductSearchVO> lstGSProductSearchVO = new ArrayList<GSProductSearchVO>();

		getFacedTypeAhead(searchKeyword, obTypeAheadVO);


		//To do: Use json api
		String strKeyWordSearch = PERFORM_SEARCH_KEYWORD + searchKeyword + STRING_APPEND;
		SearchResults searchResult =performSearch(strKeyWordSearch);
		lstCatalogConfigKeys = getCatalogTools().getAllValuesForKey(CONTENT_CATALOG_KEYS,TYPE_AHEAD_PRODUCT_LIMIT);
		if(!lstCatalogConfigKeys.isEmpty()){
			strNoOfProduct  = lstCatalogConfigKeys.get(0);
		}

		if(searchResult.getBbbProducts() !=null){
			BBBProductList obBBBProductList = searchResult.getBbbProducts();
			if(obBBBProductList.getBBBProducts() !=null){
				lstBbbProducts = obBBBProductList.getBBBProducts();
				Iterator<BBBProduct> itBBBIterator = (Iterator<BBBProduct>) lstBbbProducts.iterator();						
				while(itBBBIterator.hasNext()){
					if(countProduct>Integer.parseInt(strNoOfProduct)){
						break;
					}
					GSProductSearchVO obGSProductSearchVO = new GSProductSearchVO();
					obBBBProduct = itBBBIterator.next();
					obGSProductSearchVO.setProductId(obBBBProduct.getProductID());
					obGSProductSearchVO.setProductName(obBBBProduct.getProductName());
					lstGSProductSearchVO.add(obGSProductSearchVO);
					countProduct++;				
				}			
				if(!lstGSProductSearchVO.isEmpty()){					 
					obTypeAheadVO.setProductTitleResult(lstGSProductSearchVO);
				}	
			}			
		}

		
		logDebug("GSRestSearchManager.performGSTypeAheadSearch ends");
		
		BBBPerformanceMonitor.end(PERFORM_GS_TYPE_AHEAD_SEARCH);		
		return obTypeAheadVO;	
	}

	/**
	 * This method will get Department and Brand type ahead search value.
	 * @param searchKeyword
	 * @param obTypeAheadVO
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private void getFacedTypeAhead(final String searchKeyword,
			GSTypeAheadVO obTypeAheadVO) throws BBBSystemException,
			BBBBusinessException {
		FacetQueryResults facetQueryResult;
		List<FacetQueryResult> lstFacetQueryResult;
		String[] strCategoryArray;
		String strCategory;
		
		logDebug("GSRestSearchManager.getFacedTypeAhead enters");
		

		facetQueryResult =super.performTypeAheadSearch(searchKeyword,false);
		if(facetQueryResult != null){
			lstFacetQueryResult= facetQueryResult.getResults();
			Iterator<FacetQueryResult> itFacet = lstFacetQueryResult.iterator();
			while(itFacet.hasNext()){
				FacetQueryResult objFacetQueryResult = itFacet.next();
				if(BBBUtility.isNotEmpty(objFacetQueryResult.getFacetName()) && objFacetQueryResult.getFacetName().equalsIgnoreCase(DEPARTMENT)){
					Map<String,String> mapFacetQueryResult =  objFacetQueryResult.getMatches();
					Set<String> setDeparmentId = mapFacetQueryResult.keySet();
					Iterator<String> itDeparmentId = setDeparmentId.iterator();
					while(itDeparmentId.hasNext()){
						String strDepartmentId = itDeparmentId.next();
						String strCategoryBreadCrum = mapFacetQueryResult.get(strDepartmentId);
						if(strCategoryBreadCrum != null){
							strCategoryArray= strCategoryBreadCrum.split(CATEGORY_DEL);
							strCategory =strCategoryArray[strCategoryArray.length-1];
							mapFacetQueryResult.put(strDepartmentId, strCategory);
						}

					}
					break;
				}				
			}
			obTypeAheadVO.setFacetQueryResults(facetQueryResult);
		}
		
		logDebug("GSRestSearchManager.getFacedTypeAhead ends");
		
	} 

}




