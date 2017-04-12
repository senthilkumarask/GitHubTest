package com.bbb.search.endeca.assembler.cartridge.manager;

import static com.bbb.search.endeca.constants.BBBEndecaConstants.RQST_PARAM_NAME_SEARCH_QUERY_VO;

import com.bbb.common.BBBGenericService;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.endeca.EndecaSearchUtil;
import com.endeca.infront.navigation.NavigationState;
import com.endeca.infront.navigation.model.FilterState;

import atg.endeca.assembler.AssemblerTools;
import atg.endeca.assembler.navigation.NavigationStateProcessor;
import atg.nucleus.logging.VariableArgumentApplicationLogging;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

/**
 * 	Apply site id record filter to NavigationState
 *
 */
public class SiteFilterNavStateProcessor extends BBBGenericService implements NavigationStateProcessor  {

	private EndecaSearchUtil mEndecaSearchUtil;
	
	VariableArgumentApplicationLogging mLogger = AssemblerTools.getApplicationLogging();
	
	@Override
	public NavigationState process(NavigationState pNavigationState) {
		
		mLogger.vlogDebug("SiteFilterNavStateProcessor: In NavState - "+ pNavigationState);
		
		DynamoHttpServletRequest dynamoRequest = ServletUtil.getCurrentRequest();
		SearchQuery searchQuery = (SearchQuery)dynamoRequest.getAttribute(RQST_PARAM_NAME_SEARCH_QUERY_VO);

		FilterState filters = pNavigationState.getFilterState();
		filters = getEndecaSearchUtil().addSiteIdFilter(searchQuery, filters);
		
		NavigationState navState = pNavigationState.updateFilterState(filters);
		
		mLogger.vlogDebug("SiteFilterNavStateProcessor: Out NavState - " + navState);
		return navState;
	}

	public EndecaSearchUtil getEndecaSearchUtil() {
		return mEndecaSearchUtil;
	}

	public void setEndecaSearchUtil(EndecaSearchUtil pEndecaSearchUtil) {
		this.mEndecaSearchUtil = pEndecaSearchUtil;
	}
	
}
