package com.bbb.search.droplet;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.search.bean.result.BBBProduct;
import com.bbb.search.bean.result.BBBProductList;
import com.bbb.search.bean.result.SearchResults;
import com.bbb.search.integration.SearchManager;

/**
 * This droplet is to get the retrive and render Search Result VO from the output parameter of Search Droplet
 * to render in List section of Results/ Listing Page.  
 * @author agoe21
 *
 */


public class ResultListDroplet extends BBBDynamoServlet{

	private SearchManager mSearchManager;
	
	/* ===================================================== *
				GETTERS and SETTERS
	 * ===================================================== */
	
	/**
	 * @return mSearchManager
	 */
	public SearchManager getSearchManager() {
		return this.mSearchManager;
	}

	/**
	 * @param pSearchManager
	 */
	public void setSearchManager(final SearchManager pSearchManager) {
		 this.mSearchManager = pSearchManager;
	}

	@Override
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		logDebug("Entering Service Method of Result List Droplet.");
		SearchResults searchResults  =((SearchResults)pRequest.getObjectParameter(BBBSearchBrowseConstants.BROWSE_SEARCH_VO));
		StringBuilder productTmpList=new StringBuilder("");
		if(searchResults == null){
			logError("ResultListDroplet.service Recieved Null value from fort end \n Session ID - " + pRequest.getSession().getId() + " Request Data  - " + pRequest.toString());
			pRequest.serviceParameter(BBBCoreConstants.EMPTY, pRequest, pResponse);
		} else {
			final BBBProductList elist = searchResults.getBbbProducts();
			pRequest.setParameter(BBBSearchBrowseConstants.SEARCH_RESULTS_VO, elist);

			if (null != elist &&  null != elist.getBBBProducts() && !elist.getBBBProducts().isEmpty()) {
				final Iterator<BBBProduct> it = elist.getBBBProducts().iterator();
				while (it.hasNext()) {
					final BBBProduct productVO = it.next();
					productTmpList = productTmpList.append(productVO.getProductID() + BBBCoreConstants.SEMICOLON);
				}
			}
			pRequest.setParameter(BBBSearchBrowseConstants.PRODUCTS_LIST, productTmpList);
			pRequest.serviceParameter(BBBCoreConstants.OPARAM, pRequest, pResponse);
		}
	}
}
