package com.bbb.search.endeca;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.bean.result.Asset;
import com.bbb.search.bean.result.AutoSuggestVO;
import com.bbb.search.bean.result.BBBProductList;
import com.bbb.search.bean.result.CategoryParentVO;
import com.bbb.search.bean.result.CurrentDescriptorVO;
import com.bbb.search.bean.result.FacetParentVO;
import com.bbb.search.bean.result.PaginationVO;
import com.bbb.search.bean.result.SearchResults;
import com.bbb.search.endeca.assembler.cartridge.config.ThreeColumnPageContentItem;
import com.bbb.search.endeca.constants.BBBEndecaConstants;
import com.bbb.utils.BBBUtility;
import com.endeca.infront.assembler.AssemblerException;
import com.endeca.infront.assembler.ContentItem;
/*import com.endeca.content.ContentException;
import com.endeca.content.InvalidQueryException;
import com.endeca.content.ene.ENEContentQuery;
import com.endeca.content.ene.ENEContentResults;*/
import com.endeca.navigation.ENEConnection;
import com.endeca.navigation.ENEQuery;
import com.endeca.navigation.ENEQueryException;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.HttpENEConnection;
import com.endeca.navigation.Navigation;
import com.endeca.navigation.UrlGen;

import atg.endeca.assembler.AssemblerTools;
import atg.nucleus.Nucleus;

/**
 * This holds the Search Engine Client configuration.
 * @author agoe21
 *
 */
public class EndecaClient extends BBBGenericService
{
	private String host;
	private int port;
	private String encoding;
	private EndecaQueryGenerator queryGenerator;
	private String thirdPartyURL;
	private BBBCatalogTools catalogTools;
	private String pEndecaUrl;
	private String pEndecaPort;
	
	public static final String SEARCH_MODE = "SearchMode";
	
	// Constants to hold config keys for Endeca Specific details.
	//private static final String ENDECA_HOST_CONFIG_KEY = "endeca_url";
	//private static final String ENDECA_PORT_CONFIG_KEY = "endeca_port";
	
	
	//V11 upgrade
	private AssemblerTools assemblerTools;
	
	
	
	
	/**
	 * This method obtain Connection from Search Engine and queries with Search Engine specific parameters.
	 * @param argURL
	 * @param pageSize
	 * @return
	 * @throws ENEQueryException
	 */
	/*public  ENEContentResults executeQuery(final ENEContentQuery contentQuery) throws ENEQueryException, InvalidQueryException, ContentException 
	{
		logDebug("Entering EndecaClient.executeQuery method.");
		
		contentQuery.setENEConnection(getEneConnection());
		
		//Performance Monitoring Code.
		String methodName = "executeQuery";
        BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
		
		final ENEContentResults contentResults = (ENEContentResults) contentQuery.execute();
		
		BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
		
		logDebug("Exit EndecaClient.executeQuery method.");
		
		return contentResults;
	}*/
	
	/**
	 * This method creates a BBBContentItem so that BBBDefaultHandler is called and will return ContentItem 
	 * @return contentItem
	 * @throws AssemblerException 
	 */
	public ContentItem executeContentQuery(String contentCollection) throws AssemblerException {
		
		logDebug("Entering EndecaClient.executeContentQuery method.");
		
		//Performance Monitoring Code.
		String methodName = "executeContentQuery";
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);

		ThreeColumnPageContentItem requestContentItem = new ThreeColumnPageContentItem(BBBEndecaConstants.CONTENT_ITEM_TYPE);
		
		List<String> contentPaths = new ArrayList<String>();
		contentPaths.add(contentCollection);
		requestContentItem.setContentPaths(contentPaths);
		
		ThreeColumnPageContentItem responseContentItem = (ThreeColumnPageContentItem) getAssemblerTools().invokeAssembler(requestContentItem);
		
		BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
		
		logDebug("Exit EndecaClient.executeContentQuery method.");
		
		return responseContentItem;
	}
	
	
	
	public  ENEQueryResults executeQuery(final ENEQuery pEneQuery) throws ENEQueryException 
	{
		logDebug("Entering EndecaClient.executeQuery method.");
		
		//Performance Monitoring Code.
		String methodName = "executeQuery";
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
		
		final ENEQueryResults results = getEneConnection().query(pEneQuery);

		BBBPerformanceMonitor.end(BBBPerformanceConstants.SEARCH_INTEGRATION, methodName);
		
		logDebug("Exit EndecaClient.executeQuery method.");
		
		return results;
	}

	
	/**
	 * RM Defect 23496.This method removes NTPR,NTPC,NRC in SEO URL
	 * @param urlGen
	 */
	private void removeEndecaQueryParam(UrlGen urlGen){
		urlGen.removeParam(BBBEndecaConstants.NAV_SEARCH_PHRASES_NTPC);
		urlGen.removeParam(BBBEndecaConstants.NAV_SEARCH_PHRASES_NTPR);
		urlGen.removeParam(BBBEndecaConstants.NAV_SEARCH_PHRASES_NRC);
	}
	
	/**
	 * To create pagination hyper links
	 * @param argOffset
	 * @param argQueryString
	 * @return
	 */
	public  String createPagingHyperlink(final long argOffset, final String argQueryString, final long pageSize,final SearchQuery pSearchQuery)
	{
		
		//logDebug("Entering EndecaClient.createPagingHyperlink method.");
		
		UrlGen urlGen = null;
		String hyperlink = null;
		urlGen = new UrlGen(argQueryString, getEncoding());
		/*urlGen.addParam(PAGE_NUMBER, String.valueOf(argOffset));
		urlGen.addParam(PAGE_SIZE,  String.valueOf(pageSize));*/
		urlGen.addParam(BBBEndecaConstants.SORT_FIELD,pSearchQuery.getSortString());
		urlGen.removeParam(SEARCH_MODE);
		urlGen.removeParam(BBBEndecaConstants.SEARCH_FIELD);
		urlGen.removeParam(BBBEndecaConstants.KEYWORD);
		urlGen.removeParam(BBBEndecaConstants.NAV_FILTER);
		urlGen.removeParam(BBBEndecaConstants.DID_YOU_MEAN);
		// Added for R2.2 SEO friendly Story : Start
		urlGen.removeParam(BBBEndecaConstants.NAV_REFINEMENT);
		urlGen.removeParam(BBBEndecaConstants.NAV_REC_OFFSET);
		// Added for R2.2 SEO friendly Story : End
		urlGen.removeParam(BBBEndecaConstants.NAV_NR);
		/* RM Defect 15480 changes*/
		if(pSearchQuery.isRedirected()){
			urlGen.addParam(BBBEndecaConstants.IS_REDIRECT,"true");
		}
		/* RM Defect 15480 changes*/
		
		if(pSearchQuery.isFromCollege()){
			urlGen.addParam(BBBEndecaConstants.FRM_COLLEGE_PAGE,"true");
		}
/*		if(BBBUtility.isNotEmpty(pSearchQuery.getSearchMode())){
			urlGen.addParam(NAV_SEARCH_MODE, pSearchQuery.getSearchMode());
		}*/
		if(BBBUtility.isNotEmpty(pSearchQuery.getPartialFlag())){
			urlGen.addParam(BBBEndecaConstants.PARTIAL_FLAG, pSearchQuery.getPartialFlag());
		}
		// START -- R2.1 -- Price Range Slider Scope Item #66
		/*if(null != pSearchQuery.getPriceRangeInfo() && BBBUtility.isNotEmpty(pSearchQuery.getPriceRangeInfo().get(MIN_PRICE)) 
				&& BBBUtility.isNotEmpty(pSearchQuery.getPriceRangeInfo().get(MAX_PRICE))){
			urlGen.addParam(MIN_PRICE,pSearchQuery.getPriceRangeInfo().get(MIN_PRICE));
			urlGen.addParam(MAX_PRICE,pSearchQuery.getPriceRangeInfo().get(MAX_PRICE));
			
			if(BBBUtility.isNotEmpty(pSearchQuery.getPriceRangeInfo().get(DEFAULT_MIN_PRICE_RANGE)) && BBBUtility.isNotEmpty(pSearchQuery.getPriceRangeInfo().get(DEFAULT_MAX_PRICE_RANGE))){
				urlGen.addParam(DEFAULT_MIN_PRICE_RANGE, pSearchQuery.getPriceRangeInfo().get(DEFAULT_MIN_PRICE_RANGE));
				urlGen.addParam(DEFAULT_MAX_PRICE_RANGE, pSearchQuery.getPriceRangeInfo().get(DEFAULT_MAX_PRICE_RANGE));
			}
		}*/
		// END   -- R2.1 -- Price Range Slider Scope Item #66
		
		//urlGen.removeParam("CatalogId");
		removeEndecaQueryParam(urlGen);
		hyperlink =  urlGen.toString();
		
		//logDebug("Exit EndecaClient.createPagingHyperlink method.");
		
		return hyperlink;
	}

	/**
	 * Create child category hyperlink
	 * @param argID
	 * @param argQueryString
	 * @return
	 */
	public  String createExposureHyperlink(final long argID, final String argQueryString,final SearchQuery pSearchQuery)
	{
		
		//logDebug("Entering EndecaClient.createExposureHyperlink method.");
		
		UrlGen urlGen = null;
		String hyperlink = null;
		urlGen = new UrlGen(argQueryString, getEncoding());
		urlGen.addParam(BBBEndecaConstants.CAT_REF_ID, String.valueOf(argID));
		urlGen.addParam(BBBEndecaConstants.SORT_FIELD,pSearchQuery.getSortString());
		urlGen.removeParam(BBBEndecaConstants.PAGE_NUMBER);
		urlGen.removeParam(SEARCH_MODE);
		urlGen.removeParam(BBBEndecaConstants.SEARCH_FIELD);
		urlGen.removeParam(BBBEndecaConstants.KEYWORD);
		urlGen.removeParam(BBBEndecaConstants.NAV_FILTER);
		urlGen.removeParam(BBBEndecaConstants.DID_YOU_MEAN);
		// Added for R2.2 SEO friendly Story : Start
		urlGen.removeParam(BBBEndecaConstants.NAV_REFINEMENT);
		urlGen.removeParam(BBBEndecaConstants.NAV_REC_OFFSET);
		// Added for R2.2 SEO friendly Story : End
		if(pSearchQuery.isRedirected()){
			urlGen.addParam(BBBEndecaConstants.IS_REDIRECT,"true");
		}
		
		if(pSearchQuery.isFromCollege()){
			urlGen.addParam(BBBEndecaConstants.FRM_COLLEGE_PAGE,"true");
		}
/*		if(BBBUtility.isNotEmpty(pSearchQuery.getSearchMode())){
			urlGen.addParam(NAV_SEARCH_MODE, pSearchQuery.getSearchMode());
		}*/
		
		// START -- R2.1 -- Price Range Slider Scope Item #66
		/*if(null != pSearchQuery.getPriceRangeInfo() && BBBUtility.isNotEmpty(pSearchQuery.getPriceRangeInfo().get(MIN_PRICE)) 
				&& BBBUtility.isNotEmpty(pSearchQuery.getPriceRangeInfo().get(MAX_PRICE))){
			urlGen.addParam(MIN_PRICE,pSearchQuery.getPriceRangeInfo().get(MIN_PRICE));
			urlGen.addParam(MAX_PRICE,pSearchQuery.getPriceRangeInfo().get(MAX_PRICE));
			
			if(BBBUtility.isNotEmpty(pSearchQuery.getPriceRangeInfo().get(DEFAULT_MIN_PRICE_RANGE)) && BBBUtility.isNotEmpty(pSearchQuery.getPriceRangeInfo().get(DEFAULT_MAX_PRICE_RANGE))){
				urlGen.addParam(DEFAULT_MIN_PRICE_RANGE, pSearchQuery.getPriceRangeInfo().get(DEFAULT_MIN_PRICE_RANGE));
				urlGen.addParam(DEFAULT_MAX_PRICE_RANGE, pSearchQuery.getPriceRangeInfo().get(DEFAULT_MAX_PRICE_RANGE));
			}
		}*/
		// END   -- R2.1 -- Price Range Slider Scope Item #66
		removeEndecaQueryParam(urlGen);
		hyperlink = urlGen.toString();
		
		//logDebug("Exit EndecaClient.createExposureHyperlink method.");
		
		return hyperlink;
	}

	/**
	 * Create All tab hyperlink
	 * @param argID
	 * @param argQueryString
	 * @return
	 */
	public  String createCategoryAllHyperlink(final long argID, final String argQueryString,final SearchQuery pSearchQuery)
	{
		
		//logDebug("Entering EndecaClient.createCategoryAllHyperlink method.");
		
		UrlGen urlGen = null;
		String hyperlink = null;
		urlGen = new UrlGen(argQueryString, getEncoding());
		urlGen.addParam(BBBEndecaConstants.CAT_ID, String.valueOf(argID));
		urlGen.addParam(BBBEndecaConstants.CAT_REF_ID, String.valueOf(argID));
		urlGen.addParam(BBBEndecaConstants.SORT_FIELD,pSearchQuery.getSortString());
		urlGen.removeParam(BBBEndecaConstants.PAGE_NUMBER);
		urlGen.removeParam(SEARCH_MODE);
		urlGen.removeParam(BBBEndecaConstants.KEYWORD);
		urlGen.removeParam(BBBEndecaConstants.NAV_FILTER);
		urlGen.removeParam(BBBEndecaConstants.SEARCH_FIELD);
		urlGen.removeParam(BBBEndecaConstants.DID_YOU_MEAN);
		// Added for R2.2 SEO friendly Story : Start
		urlGen.removeParam(BBBEndecaConstants.NAV_REFINEMENT);
		urlGen.removeParam(BBBEndecaConstants.NAV_REC_OFFSET);
		// Added for R2.2 SEO friendly Story : End
		if(pSearchQuery.isRedirected()){
			urlGen.addParam(BBBEndecaConstants.IS_REDIRECT,"true");
		}
		
		if(pSearchQuery.isFromCollege()){
			urlGen.addParam(BBBEndecaConstants.FRM_COLLEGE_PAGE,"true");
		}
/*		if(BBBUtility.isNotEmpty(pSearchQuery.getSearchMode())){
			urlGen.addParam(NAV_SEARCH_MODE, pSearchQuery.getSearchMode());
		}*/
		
		// START -- R2.1 -- Price Range Slider Scope Item #66
		/*if(null != pSearchQuery.getPriceRangeInfo() && BBBUtility.isNotEmpty(pSearchQuery.getPriceRangeInfo().get(MIN_PRICE)) 
				&& BBBUtility.isNotEmpty(pSearchQuery.getPriceRangeInfo().get(MAX_PRICE))){
			urlGen.addParam(MIN_PRICE,pSearchQuery.getPriceRangeInfo().get(MIN_PRICE));
			urlGen.addParam(MAX_PRICE,pSearchQuery.getPriceRangeInfo().get(MAX_PRICE));
			
			if(BBBUtility.isNotEmpty(pSearchQuery.getPriceRangeInfo().get(DEFAULT_MIN_PRICE_RANGE)) && BBBUtility.isNotEmpty(pSearchQuery.getPriceRangeInfo().get(DEFAULT_MAX_PRICE_RANGE))){
				urlGen.addParam(DEFAULT_MIN_PRICE_RANGE, pSearchQuery.getPriceRangeInfo().get(DEFAULT_MIN_PRICE_RANGE));
				urlGen.addParam(DEFAULT_MAX_PRICE_RANGE, pSearchQuery.getPriceRangeInfo().get(DEFAULT_MAX_PRICE_RANGE));
			}
		}*/
		// END   -- R2.1 -- Price Range Slider Scope Item #66
		removeEndecaQueryParam(urlGen);
		hyperlink = urlGen.toString();
		
		//logDebug("Exit EndecaClient.createCategoryAllHyperlink method.");
		
		return hyperlink;
	}

	/**
	 * To create the category refinement hyperlink
	 * @param argNValue
	 * @param argQueryString
	 * @return
	 */
	public  String createRefinementHyperlink(final String argNValue, final String argQueryString,final SearchQuery pSearchQuery)
	{
		
		//logDebug("Entering EndecaClient.createRefinementHyperlink method.");
		
		UrlGen urlGen = null;
		String hyperlink = null;
		urlGen = new UrlGen(argQueryString, getEncoding());
		// Commented for R2.2 SEO friendly Story : Start
		//urlGen.addParam(CAT_ID, argNValue);
		// Commented for R2.2 SEO friendly Story : End
		urlGen.addParam(BBBEndecaConstants.SORT_FIELD,pSearchQuery.getSortString());
		urlGen.removeParam(BBBEndecaConstants.PAGE_NUMBER);
		urlGen.removeParam(SEARCH_MODE);
		urlGen.removeParam(BBBEndecaConstants.SEARCH_FIELD);
		urlGen.removeParam(BBBEndecaConstants.NAV_FILTER);
		urlGen.removeParam(BBBEndecaConstants.DID_YOU_MEAN);
		urlGen.removeParam(BBBEndecaConstants.KEYWORD);
		// Added for R2.2 SEO friendly Story : Start
		urlGen.removeParam(BBBEndecaConstants.NAV_REFINEMENT);
		urlGen.removeParam(BBBEndecaConstants.NAV_REC_OFFSET);
		// Added for R2.2 SEO friendly Story : End
		
		if(pSearchQuery.isRedirected()){
			urlGen.addParam(BBBEndecaConstants.IS_REDIRECT,"true");
		}
		
		if(pSearchQuery.isFromCollege()){
			urlGen.addParam(BBBEndecaConstants.FRM_COLLEGE_PAGE,"true");
		}
/*		if(BBBUtility.isNotEmpty(pSearchQuery.getSearchMode())){
			urlGen.addParam(NAV_SEARCH_MODE, pSearchQuery.getSearchMode());
		}*/
		if(BBBUtility.isNotEmpty(pSearchQuery.getPartialFlag())){
			urlGen.addParam(BBBEndecaConstants.PARTIAL_FLAG, pSearchQuery.getPartialFlag());
		}
		// START -- R2.1 -- Price Range Slider Scope Item #66
		/*if(null != pSearchQuery.getPriceRangeInfo() && BBBUtility.isNotEmpty(pSearchQuery.getPriceRangeInfo().get(MIN_PRICE)) 
				&& BBBUtility.isNotEmpty(pSearchQuery.getPriceRangeInfo().get(MAX_PRICE))){
			urlGen.addParam(MIN_PRICE,pSearchQuery.getPriceRangeInfo().get(MIN_PRICE));
			urlGen.addParam(MAX_PRICE,pSearchQuery.getPriceRangeInfo().get(MAX_PRICE));
			
			if(BBBUtility.isNotEmpty(pSearchQuery.getPriceRangeInfo().get(DEFAULT_MIN_PRICE_RANGE)) && BBBUtility.isNotEmpty(pSearchQuery.getPriceRangeInfo().get(DEFAULT_MAX_PRICE_RANGE))){
				urlGen.addParam(DEFAULT_MIN_PRICE_RANGE, pSearchQuery.getPriceRangeInfo().get(DEFAULT_MIN_PRICE_RANGE));
				urlGen.addParam(DEFAULT_MAX_PRICE_RANGE, pSearchQuery.getPriceRangeInfo().get(DEFAULT_MAX_PRICE_RANGE));
			}
		}*/
		// END   -- R2.1 -- Price Range Slider Scope Item #66
		removeEndecaQueryParam(urlGen);
		hyperlink = urlGen.toString();
		
		//logDebug("Exit EndecaClient.createRefinementHyperlink method.");
		
		return hyperlink;
	}
	
	/**
	 * To create the Asset Tab hyperlink
	 * @param argNValue
	 * @param argQueryString
	 * @return
	 */
	public  String createAssetTabHyperlink(final String argNValue, final String argQueryString,final SearchQuery pSearchQuery)
	{
		
		//logDebug("Entering EndecaClient.createAssetTabHyperlink method.");
		logDebug("argNValue"+argNValue);
		logDebug("argQueryString"+argQueryString);
		UrlGen urlGen = null;
		String hyperlink = null;
		urlGen = new UrlGen(argQueryString, getEncoding());
		// Commented for R2.2 SEO friendly Story : Start
		//urlGen.addParam(CAT_ID, argNValue);
		// Commented for R2.2 SEO friendly Story : Start
		urlGen.removeParam(BBBEndecaConstants.SORT_FIELD);
		urlGen.removeParam(BBBEndecaConstants.PAGE_NUMBER);
		urlGen.removeParam(BBBEndecaConstants.KEYWORD);
		urlGen.removeParam(SEARCH_MODE);
		urlGen.removeParam(BBBEndecaConstants.SEARCH_FIELD);
		urlGen.removeParam(BBBEndecaConstants.NAV_FILTER);
		urlGen.removeParam(BBBEndecaConstants.DID_YOU_MEAN);
		// Added for R2.2 SEO friendly Story : Start
		urlGen.removeParam(BBBEndecaConstants.NAV_REFINEMENT);
		urlGen.removeParam(BBBEndecaConstants.NAV_REC_OFFSET);
		// Added for R2.2 SEO friendly Story : End
		
		if(pSearchQuery.isRedirected()){
			urlGen.addParam(BBBEndecaConstants.IS_REDIRECT,"true");
		}
		
		if(pSearchQuery.isFromCollege()){
			urlGen.addParam(BBBEndecaConstants.FRM_COLLEGE_PAGE,"true");
		}
/*		if(BBBUtility.isNotEmpty(pSearchQuery.getSearchMode())){
			urlGen.addParam(NAV_SEARCH_MODE, pSearchQuery.getSearchMode());
		}*/
		
		// START -- R2.1 -- Price Range Slider Scope Item #66
		/*if(null != pSearchQuery.getPriceRangeInfo() && BBBUtility.isNotEmpty(pSearchQuery.getPriceRangeInfo().get(MIN_PRICE)) 
				&& BBBUtility.isNotEmpty(pSearchQuery.getPriceRangeInfo().get(MAX_PRICE))){
			urlGen.addParam(MIN_PRICE,pSearchQuery.getPriceRangeInfo().get(MIN_PRICE));
			urlGen.addParam(MAX_PRICE,pSearchQuery.getPriceRangeInfo().get(MAX_PRICE));
			
			if(BBBUtility.isNotEmpty(pSearchQuery.getPriceRangeInfo().get(DEFAULT_MIN_PRICE_RANGE)) && BBBUtility.isNotEmpty(pSearchQuery.getPriceRangeInfo().get(DEFAULT_MAX_PRICE_RANGE))){
				urlGen.addParam(DEFAULT_MIN_PRICE_RANGE, pSearchQuery.getPriceRangeInfo().get(DEFAULT_MIN_PRICE_RANGE));
				urlGen.addParam(DEFAULT_MAX_PRICE_RANGE, pSearchQuery.getPriceRangeInfo().get(DEFAULT_MAX_PRICE_RANGE));
			}
		}*/
		// END   -- R2.1 -- Price Range Slider Scope Item #66
		removeEndecaQueryParam(urlGen);
		hyperlink = urlGen.toString();
		logDebug("hyperlink from createAssetTabHyperlink"+hyperlink);
		//logDebug("Exit EndecaClient.createAssetTabHyperlink method.");
		
		return hyperlink;
	}

	/**
	 * Genrates removal text search descriptor
	 * @param argQueryString
	 * @return
	 */
	public  String createRemoveTextSearchLink(final String argQueryString,final SearchQuery pSearchQuery)
	{
		
		//logDebug("Entering EndecaClient.createRemoveTextSearchLink method.");
		
		UrlGen urlGen = null;
		String hyperlink = null;
		urlGen = new UrlGen(argQueryString, getEncoding());
		urlGen.addParam(BBBEndecaConstants.SORT_FIELD,pSearchQuery.getSortString());
		urlGen.removeParam(BBBEndecaConstants.KEYWORD);
		urlGen.removeParam(BBBEndecaConstants.SEARCH_FIELD);
		urlGen.removeParam(SEARCH_MODE);
		urlGen.removeParam(BBBEndecaConstants.PAGE_NUMBER);
		urlGen.removeParam(BBBEndecaConstants.DID_YOU_MEAN);
		urlGen.removeParam(BBBEndecaConstants.NAV_FILTER);
		// Added for R2.2 SEO friendly Story : Start
		urlGen.removeParam(BBBEndecaConstants.NAV_REFINEMENT);
		urlGen.removeParam(BBBEndecaConstants.NAV_REC_OFFSET);
		// Added for R2.2 SEO friendly Story : End
		
		if(pSearchQuery.isRedirected()){
			urlGen.addParam(BBBEndecaConstants.IS_REDIRECT,"true");
		}
		
		if(pSearchQuery.isFromCollege()){
			urlGen.addParam(BBBEndecaConstants.FRM_COLLEGE_PAGE,"true");
		}
/*		if(BBBUtility.isNotEmpty(pSearchQuery.getSearchMode())){
			urlGen.addParam(NAV_SEARCH_MODE, pSearchQuery.getSearchMode());
		}*/
		if(BBBUtility.isNotEmpty(pSearchQuery.getPartialFlag())){
			urlGen.addParam(BBBEndecaConstants.PARTIAL_FLAG, pSearchQuery.getPartialFlag());
		}
		// START -- R2.1 -- Price Range Slider Scope Item #66
		/*if(null != pSearchQuery.getPriceRangeInfo() && BBBUtility.isNotEmpty(pSearchQuery.getPriceRangeInfo().get(MIN_PRICE)) 
				&& BBBUtility.isNotEmpty(pSearchQuery.getPriceRangeInfo().get(MAX_PRICE))){
			urlGen.addParam(MIN_PRICE,pSearchQuery.getPriceRangeInfo().get(MIN_PRICE));
			urlGen.addParam(MAX_PRICE,pSearchQuery.getPriceRangeInfo().get(MAX_PRICE));
			
			if(BBBUtility.isNotEmpty(pSearchQuery.getPriceRangeInfo().get(DEFAULT_MIN_PRICE_RANGE)) && BBBUtility.isNotEmpty(pSearchQuery.getPriceRangeInfo().get(DEFAULT_MAX_PRICE_RANGE))){
				urlGen.addParam(DEFAULT_MIN_PRICE_RANGE, pSearchQuery.getPriceRangeInfo().get(DEFAULT_MIN_PRICE_RANGE));
				urlGen.addParam(DEFAULT_MAX_PRICE_RANGE, pSearchQuery.getPriceRangeInfo().get(DEFAULT_MAX_PRICE_RANGE));
			}
		}*/
		// END   -- R2.1 -- Price Range Slider Scope Item #66
		removeEndecaQueryParam(urlGen);
		hyperlink =  urlGen.toString();
		
		//logDebug("Exit EndecaClient.createRemoveTextSearchLink method.");
		
		return hyperlink;
	}

	/**
	 * Generates removal descriptor query
	 * @param nParam
	 * @param argQueryString
	 * @return
	 */
	public String createRemoveDescriptorQuery(final String nParam, final String argQueryString,final SearchQuery pSearchQuery)
	{
		
		//logDebug("Entering EndecaClient.createRemoveDescriptorQuery method.");
		
		final UrlGen urlGen = new UrlGen(argQueryString, getEncoding());
		// Commented for R2.2 SEO friendly Story : Start
		//urlGen.addParam(CAT_ID, nParam);
		// Commented for R2.2 SEO friendly Story : Start
		urlGen.addParam(BBBEndecaConstants.SORT_FIELD,pSearchQuery.getSortString());
		urlGen.removeParam(BBBEndecaConstants.PAGE_NUMBER);
		urlGen.removeParam(BBBEndecaConstants.KEYWORD);
		urlGen.removeParam(SEARCH_MODE);
		urlGen.removeParam(BBBEndecaConstants.SEARCH_FIELD);
		urlGen.removeParam(BBBEndecaConstants.DID_YOU_MEAN);
		urlGen.removeParam(BBBEndecaConstants.NAV_FILTER);
		// Added for R2.2 SEO friendly Story : Start
		urlGen.removeParam(BBBEndecaConstants.NAV_REFINEMENT);
		urlGen.removeParam(BBBEndecaConstants.NAV_REC_OFFSET);
		// Added for R2.2 SEO friendly Story : End
		
		if(pSearchQuery.isRedirected()){
			urlGen.addParam(BBBEndecaConstants.IS_REDIRECT,"true");
		}
/*		if(BBBUtility.isNotEmpty(pSearchQuery.getSearchMode())){
			urlGen.addParam(NAV_SEARCH_MODE, pSearchQuery.getSearchMode());
		}*/
		if(BBBUtility.isNotEmpty(pSearchQuery.getPartialFlag())){
			urlGen.addParam(BBBEndecaConstants.PARTIAL_FLAG, pSearchQuery.getPartialFlag());
		}
		/*if(isFromBrandPage){
			urlGen.addParam(FRM_BRAND_PAGE,"true");
		}
		if(isFromCollege){
			urlGen.addParam(FRM_COLLEGE_PAGE,"true");
		}*/
		removeEndecaQueryParam(urlGen);
		final String query = urlGen.toString();
		
		//logDebug("Exit EndecaClient.createRemoveDescriptorQuery method.");
		
		return query;
	}

	/**
	 * This method iterates the result object to get all the constituents.
	 * @param bbbProducts
	 * @param pagingLinks
	 * @param catList
	 * @param facetList
	 * @param descriptorList
	 * @param assetMap 
	 * @return
	 */
	public SearchResults getResultObject(final BBBProductList bbbProducts,
			final PaginationVO pagingLinks, final CategoryParentVO catList,
			final List<FacetParentVO> facetList,
			final List<CurrentDescriptorVO> descriptorList, final Map<String, Asset> assetMap,final List<AutoSuggestVO> pList, String redirUrl) {
		
		logDebug("Entering EndecaClient.getResultObject method.");
		
		final SearchResults bsVO = new SearchResults();
		if (bbbProducts != null) {
			bsVO.setBbbProducts(bbbProducts);
		}
		if (pagingLinks != null) {
			bsVO.setPagingLinks(pagingLinks);
		}

		if(catList!=null){
			bsVO.setCategoryHeader(catList);
		}
		if(facetList!=null){
			bsVO.setFacets(facetList);
		}
		if(descriptorList!=null){
			bsVO.setDescriptors(descriptorList);
		}
		if(assetMap!=null){
			bsVO.setAssetMap(assetMap);
		}
		if(pList!=null){
			bsVO.setAutoSuggest(pList);
		}
		if(redirUrl!=null){
			bsVO.setRedirUrl(redirUrl);
		}
		
		logDebug("Exit EndecaClient.getResultObject method.");
		
		return bsVO;
		
	}
	
	
	
	/**
	 * This method returns the current query after removing the default fields.
	 * @param pRefinedString
	 * @return
	 */
	public String createCurrentQuery(final String pRefinedString,final String pageSize,final Navigation argNav,final SearchQuery pSearchQuery) {
       // final long argPageSize = Integer.parseInt(pageSize);
     //  long currentOffset = 0L;
       // long currentPage = 0L;            
      //  currentOffset = argNav.getERecsOffset();
       // currentPage = (currentOffset / argPageSize) + 1L;
        final UrlGen urlGen = new UrlGen(pRefinedString, getEncoding());
        urlGen.addParam(BBBEndecaConstants.SORT_FIELD,pSearchQuery.getSortString());
        //urlGen.removeParam(PAGE_NUMBER);
        // Commented for R2.2 SEO friendly Story : Start
        //urlGen.addParam(PAGE_NUMBER, String.valueOf(currentPage));
        //urlGen.addParam(PAGE_SIZE,  String.valueOf(pageSize));
        // Commented for R2.2 SEO friendly Story : Start
        urlGen.removeParam(BBBEndecaConstants.KEYWORD);
        urlGen.removeParam(SEARCH_MODE);
        urlGen.removeParam(BBBEndecaConstants.SEARCH_FIELD);
        urlGen.removeParam(BBBEndecaConstants.DID_YOU_MEAN);
        urlGen.removeParam(BBBEndecaConstants.NAV_FILTER);
        // Added for R2.2 SEO friendly Story : Start	
        urlGen.removeParam(BBBEndecaConstants.NAV_REFINEMENT);
		urlGen.removeParam(BBBEndecaConstants.NAV_REC_OFFSET);
		// EPH based filtering
		urlGen.removeParam(BBBEndecaConstants.NAV_NR);
		
		//remove EQL filter from query
		urlGen.removeParam(BBBEndecaConstants.NEGATIVE_RECORD_FILTER);
		
		// Added for R2.2 SEO friendly Story : End
         if(pSearchQuery.isRedirected()){
			urlGen.addParam(BBBEndecaConstants.IS_REDIRECT,"true");
		}
		
		if(pSearchQuery.isFromCollege()){
			urlGen.addParam(BBBEndecaConstants.FRM_COLLEGE_PAGE,"true");
		}
		/*if(BBBUtility.isNotEmpty(pSearchQuery.getSearchMode())){
			urlGen.addParam(NAV_SEARCH_MODE, pSearchQuery.getSearchMode());
		}*/
		if(BBBUtility.isNotEmpty(pSearchQuery.getPartialFlag())){
			urlGen.addParam(BBBEndecaConstants.PARTIAL_FLAG, pSearchQuery.getPartialFlag());
		}
		// START -- R2.1 -- Price Range Slider Scope Item #66
		/*if(null != pSearchQuery.getPriceRangeInfo() && BBBUtility.isNotEmpty(pSearchQuery.getPriceRangeInfo().get(MIN_PRICE)) 
				&& BBBUtility.isNotEmpty(pSearchQuery.getPriceRangeInfo().get(MAX_PRICE))){
			urlGen.addParam(MIN_PRICE,pSearchQuery.getPriceRangeInfo().get(MIN_PRICE));
			urlGen.addParam(MAX_PRICE,pSearchQuery.getPriceRangeInfo().get(MAX_PRICE));
			
			if(BBBUtility.isNotEmpty(pSearchQuery.getPriceRangeInfo().get(DEFAULT_MIN_PRICE_RANGE)) && BBBUtility.isNotEmpty(pSearchQuery.getPriceRangeInfo().get(DEFAULT_MAX_PRICE_RANGE))){
				urlGen.addParam(DEFAULT_MIN_PRICE_RANGE, pSearchQuery.getPriceRangeInfo().get(DEFAULT_MIN_PRICE_RANGE));
				urlGen.addParam(DEFAULT_MAX_PRICE_RANGE, pSearchQuery.getPriceRangeInfo().get(DEFAULT_MAX_PRICE_RANGE));
			}
		}*/
		// END   -- R2.1 -- Price Range Slider Scope Item #66
		removeEndecaQueryParam(urlGen);
        return urlGen.toString();
	}



	
	/**
	 * @return the host
	 */
	public String getHost() {
		
		List<String> urlList = new ArrayList<String>();
		try {
			urlList = getCatalogTools().getAllValuesForKey(getThirdPartyURL(), getEndecaUrl());
		} catch (BBBBusinessException bbbbEx) {
			logError(BBBCoreErrorConstants.BROWSE_ERROR_1038+" BusinessException in endeca host url list from getHost from EndecaClient",bbbbEx);
		} catch (BBBSystemException bbbsEx) {
			logError(BBBCoreErrorConstants.BROWSE_ERROR_1039+" SystemException in endeca host url list from getHost from EndecaClient",bbbsEx);
		}
		if(!urlList.isEmpty()){
			host = urlList.get(0);
		}
		
		return host;
		
		
	}

	/**
	 * @param pHost the host to set
	 */
	public void setHost(final String pHost) {
		host = pHost;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		//if(port == 0){
			List<String> portList = new ArrayList<String>();
			try {
				portList = getCatalogTools().getAllValuesForKey(getThirdPartyURL(), getEndecaPort());
			} catch (BBBBusinessException bbbbEx) {
				logError(BBBCoreErrorConstants.BROWSE_ERROR_1038+" BusinessException in endeca Port list from getPort from EndecaClient",bbbbEx);
			} catch (BBBSystemException bbbsEx) {
				logError(BBBCoreErrorConstants.BROWSE_ERROR_1039+" SystemException in endeca Port list from getPort from EndecaClient",bbbsEx);
			}
			if(!portList.isEmpty()){
				port = Integer.parseInt(portList.get(0));
			}
		//}
		return port;
	}

	/**
	 * @param pPort the port to set
	 */
	public void setPort(final int pPort) {
		port = pPort;
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
	 * @return the eneConnection
	 */
	public ENEConnection getEneConnection() {
		return new HttpENEConnection(this.getHost(), this.getPort());
	}

	/**
	 * @return the queryGenerator
	 */
	public EndecaQueryGenerator getQueryGenerator() {
		return queryGenerator;
	}

	/**
	 * @param pQueryGenerator the queryGenerator to set
	 */
	public void setQueryGenerator(final EndecaQueryGenerator pQueryGenerator) {
		queryGenerator = pQueryGenerator;
	}

	/**
	 * @return the thirdPartyURL
	 */
	public String getThirdPartyURL() {
		return thirdPartyURL;
	}

	/**
	 * @param thirdPartyURL the thirdPartyURL to set
	 */
	public void setThirdPartyURL(final String pThirdPartyURL) {
		this.thirdPartyURL = pThirdPartyURL;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		
		return this.catalogTools;
	}

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools pCatalogTools) {
		this.catalogTools = pCatalogTools;
	}

	/**
	 * @return the pEndecaUrl
	 */
	public String getEndecaUrl() {
		return pEndecaUrl;
	}

	/**
	 * @param pEndecaUrl the pEndecaUrl to set
	 */
	public void setEndecaUrl(String pEndecaUrl) {
		this.pEndecaUrl = pEndecaUrl;
	}

	/**
	 * @return the pEndecaPort
	 */
	public String getEndecaPort() {
		return pEndecaPort;
	}

	/**
	 * @param pEndecaPort the pEndecaPort to set
	 */
	public void setEndecaPort(String pEndecaPort) {
		this.pEndecaPort = pEndecaPort;
	}
	
	/**
	 * 
	 * @return
	 */
	public AssemblerTools getAssemblerTools() {
		return assemblerTools;
	}


	/**
	 * 
	 * @param assemblerTools
	 */
	public void setAssemblerTools(AssemblerTools assemblerTools) {
		this.assemblerTools = assemblerTools;
	}
	
	// Inclusion of configuration_path and endeca_application
	
	private String mConfigurationPathKey;
	private String mConfigurationPath;
	private String mEndecaAapplicationNameKey;
	private String mEndecaAapplicationName;
	
	private File mConfigurationPathFile;
	
	/*<ul>
	 * <li> Getter for configurationPathKey</li>
	 * <li> Setter for configurationPathKey</li>
	 *</ul>
	 */
	public String getConfigurationPathKey() {
		return mConfigurationPathKey;
	}

	public void setConfigurationPathKey(String configurationPathKey) {
		this.mConfigurationPathKey = configurationPathKey;
	}
	
	/*
	 * <p>Setter for configurationPath</p>
	 */
	
	public void setConfigurationPath(String pConfigurationPath) {
		this.mConfigurationPath = pConfigurationPath;
	}
	
	/*
	 * <p>getConfigurationPath() invokes getCatalogTools().getAllValuesForKey(...) & retrieves keyValue from the configureKey Repository!
	 * The implementation for getAllValuesForKey(...) can be found on BBBConfigToolsImpl!
	 * </p>
	 */
	
	public String getConfigurationPath() {
	List<String> list = new ArrayList<String>();
		try {
			list = getCatalogTools().getAllValuesForKey(getThirdPartyURL(), getConfigurationPathKey());
		} catch (BBBBusinessException bbbbEx) {
			logError(BBBCoreErrorConstants.BROWSE_ERROR_1038+" BusinessException in endeca configuration path from getConfigurationPath from EndecaClient",bbbbEx);
		} catch (BBBSystemException bbbsEx) {
			logError(BBBCoreErrorConstants.BROWSE_ERROR_1039+" SystemException in endeca configuration path from getConfigurationPath from EndecaClientt",bbbsEx);
		}
		if(!list.isEmpty()){
			mConfigurationPath =(String)(list.get(0));
		}
		return mConfigurationPath;
	}	

	/*<ul>
	 * <li> Getter for endecaApplicationNameKey</li>
	 * <li> Setter for endecaApplicationNameKey</li>
	 *</ul>
	 */
	
	public String getEndecaAapplicationNameKey() {
		return mEndecaAapplicationNameKey;
	}

	public void setEndecaAapplicationNameKey(String endecaAapplicationNameKey) {
		this.mEndecaAapplicationNameKey = endecaAapplicationNameKey;
	}

	/*
	 * <p>getEndecaAapplicationName() invokes getCatalogTools().getAllValuesForKey(...) & retrieves keyValue from the configureKey Repository!
	 * The implementation for getAllValuesForKey(...) can be found on BBBConfigToolsImpl!
	 * </p>
	 */
	public String getEndecaAapplicationName() {
		List<String> list = new ArrayList<String>();
		try {
			list = getCatalogTools().getAllValuesForKey(getThirdPartyURL(), getEndecaAapplicationNameKey());
		} catch (BBBBusinessException bbbbEx) {
			logError(BBBCoreErrorConstants.BROWSE_ERROR_1038+" BusinessException in endeca application name from mEndecaAapplicationNameKey from EndecaClient",bbbbEx);
		} catch (BBBSystemException bbbsEx) {
			logError(BBBCoreErrorConstants.BROWSE_ERROR_1039+" SystemException in endeca application name from mEndecaAapplicationNameKey from EndecaClient",bbbsEx);
		}
		if(!list.isEmpty()){
			mEndecaAapplicationName =(String)(list.get(0));
		}
		return mEndecaAapplicationName;
	}
	
	/*
	 * <p>Setter for endecaApplicationName</p>
	 */
	
	public void setEndecaAapplicationName(String endecaAapplicationName) {
		this.mEndecaAapplicationName = endecaAapplicationName;
	}



	public File getConfigurationPathFile() {
		if(null == mConfigurationPathFile) {
			return new File(getConfigurationPath());
		}
		return mConfigurationPathFile;
	}



	public void setConfigurationPathFile(File mConfigurationPathFile) {
		this.mConfigurationPathFile = mConfigurationPathFile;
	}
}