package com.bbb.search.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.search.bean.result.FacetQuery;
import com.bbb.search.bean.result.FacetQueryResults;
import com.bbb.search.integration.SearchManager;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBDynamoServlet;


/**
 * This droplet is to render the flyout with type ahead suggestions.
 * 
 * @author agoe21
 */

/**
 * @author agoe21
 *
 */
public class TypeAheadDroplet extends BBBDynamoServlet 
{
	private SearchManager mSearchManager;
	private BBBCatalogTools mCatalogTools;
	private String mSearchDimConfig;
	private String mTypeAheadDimListKey;
	private String mShowPopularKeywords;
	
	// Property to hold Comma separated Dimension names to be queried on for Type Ahead Droplet
	private String mFacets;
	private static final String PAGE_SIZE_COOKIE_NAME = "pageSizeFilter";
	
	/* ===================================================== *
				GETTERS and SETTERS
	 * ===================================================== */
	
	public String getFacets() {
		List<String> facetList = new ArrayList<String>();
		try {
			facetList = getCatalogTools().getAllValuesForKey(getSearchDimConfig(), getTypeAheadDimListKey());
		} catch (BBBBusinessException bbbbEx) {
			logError(BBBCoreErrorConstants.BROWSE_ERROR_1016+" Business Exception occurred while fetching dimensions display list from  getFacets from TypeAheadDroplet",bbbbEx);
		} catch (BBBSystemException bbbsEx) {
			logError(BBBCoreErrorConstants.BROWSE_ERROR_1017+" System Exception occurred while fetching dimensions display list from  getFacets from TypeAheadDroplet",bbbsEx);
		}
		if(!facetList.isEmpty()){
			mFacets = facetList.get(0);
		}
		//System.out.println("Type Ahead Facet list ::" +mFacets);
		return mFacets;
	}

	public void setFacets(final String pFacets) {
		this.mFacets = pFacets;
	}

	public SearchManager getSearchManager() {
		return mSearchManager;
	}

	public void setSearchManager(final SearchManager pSearchManager) {
		this.mSearchManager = pSearchManager;
	}
	
	public String getSearchDimConfig() {
		return mSearchDimConfig;
	}

	public void setSearchDimConfig(String searchDimConfig) {
		this.mSearchDimConfig = searchDimConfig;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}
	
	public String getTypeAheadDimListKey() {
		return mTypeAheadDimListKey;
	}

	public void setTypeAheadDimListKey(String typeAheadDimListKey) {
		this.mTypeAheadDimListKey = typeAheadDimListKey;
	}
	
	public String getShowPopularKeywords() {
		return mShowPopularKeywords;
	}

	public void setShowPopularKeywords(String showPopularKeywords) {
		this.mShowPopularKeywords = showPopularKeywords;
	}

	/* (non-Javadoc)
	 * @see atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
	 */
	public void service(final DynamoHttpServletRequest pRequest,final DynamoHttpServletResponse pResponse) throws ServletException,IOException 
	{
		logDebug("Entering service Method of Typeahead droplet.");
		if (pRequest.getParameter(BBBSearchBrowseConstants.TYPE_AHEAD_KEYWORD) != null) 
		{
			
			final FacetQuery pFacetQuery = new FacetQuery();
			pFacetQuery.setKeyword(pRequest.getParameter(BBBSearchBrowseConstants.TYPE_AHEAD_KEYWORD));
			
			/**
			 * siteId from the SiteContextManager
			 */
			String pSiteId = pRequest.getParameter(BBBCoreConstants.SITE_ID);
			if (pSiteId == null) {
				pSiteId = SiteContextManager.getCurrentSiteId();
			}
			
			/* START R2.1 TypeAhead for Most Popular Keywords */
			if(("true").equalsIgnoreCase(getShowPopularKeywords())){
				pFacetQuery.setShowPopularTerms(true);
			}
			/* END   R2.1 TypeAhead for Most Popular Keywords */
			
			pFacetQuery.setSiteId(pSiteId);
			List<String> pFacets = Arrays.asList(getFacets().split(BBBSearchBrowseConstants.TYPE_AHEAD_FACET_LIST_DELIMITER));
			pFacetQuery.setFacets(pFacets);
			pFacetQuery.setCatalogId(pRequest.getParameter("categoryId"));
			try {
				
				//For type ahead search in department, set page filter size
				String pageSizeFilter = pRequest.getCookieParameter(PAGE_SIZE_COOKIE_NAME);
				if(pageSizeFilter != null){
					pFacetQuery.setPageFilterSize(pageSizeFilter);
				}
						
				final FacetQueryResults facetQueryResults = getSearchManager().performTypeAheadSearch(pFacetQuery);
				pRequest.setParameter(BBBSearchBrowseConstants.TYPE_AHEAD_RESULTS_VO,facetQueryResults);
				pRequest.serviceParameter(BBBCoreConstants.OPARAM, pRequest, pResponse);	
			} 
			catch (BBBBusinessException e) {
				logError(LogMessageFormatter.formatMessage(pRequest, "Business Exception from service of TypeAheadDroplet ",BBBCoreErrorConstants.BROWSE_ERROR_1018),e);
				pRequest.setParameter(BBBCoreErrorConstants.ERROR_MESSAGE, e.getMessage());
				pRequest.serviceLocalParameter(BBBCoreConstants.ERROR_OPARAM, pRequest,pResponse);
			} 
			catch (BBBSystemException e) {
				logError(LogMessageFormatter.formatMessage(pRequest, "System Exception from service of TypeAheadDroplet ",BBBCoreErrorConstants.BROWSE_ERROR_1019),e);
				pRequest.setParameter(BBBCoreErrorConstants.ERROR_MESSAGE, e.getMessage());
				pRequest.serviceLocalParameter(BBBCoreConstants.ERROR_OPARAM, pRequest,pResponse);
			}
		}
	}
}