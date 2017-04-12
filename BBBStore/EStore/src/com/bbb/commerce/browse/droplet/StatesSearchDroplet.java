package com.bbb.commerce.browse.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.search.integration.SearchManager;

/**
 * @author vchan5
 *DynamoServelet to fetch result states from Endeca for all the colleges available
 */
public class StatesSearchDroplet extends BBBDynamoServlet {
	private static final String LIST_OF_STATES = "listOfStates";
	private static final String OUTPUT = "output";

	private SearchManager mSearchManager;

	/* Generic Service method to server dynamo request and will return list of all states from Endeca 
	 * (non-Javadoc)
	 * @see atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
	 */
	@Override
	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		List<StateVO> listAllStates = null;
		try {
			listAllStates = getSearchManager().getStates();
		} catch (BBBBusinessException bbbbEx) {
			logError(" BusinessException While fetching State List From Endeca", bbbbEx);
		} catch (BBBSystemException bbbsEx) {
			logError("SYstem Exception While fetching State List From Endeca", bbbsEx);
		}
		pRequest.setParameter(LIST_OF_STATES, listAllStates);
		pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
	}
	/**
	 * @return the searchManager
	 */
	public SearchManager getSearchManager() {
		return mSearchManager;
	}

	/**
	 * @param searchManager
	 *            the searchManager to set
	 */
	public void setSearchManager(SearchManager pSearchManager) {
		this.mSearchManager = pSearchManager;
	}


}
