package com.bbb.search.endeca.assembler.cartridge.manager;

import static atg.core.util.StringUtils.isNotEmpty;
import static com.bbb.search.endeca.EndecaSearchUtil.getParameter;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.MATCH_MODE_ALL;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.MATCH_MODE_ALL_ANY;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.MATCH_MODE_ALL_PARTIAL;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.MATCH_MODE_ANY;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.MATCH_MODE_BOOLEAN;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.MATCH_MODE_PARTIAL;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.MATCH_MODE_PARTIAL_MAX;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.NAV_SEARCH_MODE;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.TRUE;

import java.util.List;

import com.bbb.common.BBBGenericService;
import com.bbb.search.endeca.constants.BBBEndecaConstants;
import com.endeca.infront.navigation.NavigationState;
import com.endeca.infront.navigation.model.MatchMode;
import com.endeca.infront.navigation.model.SearchFilter;
import com.endeca.soleng.urlformatter.UrlFormatException;

import atg.endeca.assembler.AssemblerTools;
import atg.endeca.assembler.navigation.NavigationStateProcessor;
import atg.nucleus.logging.VariableArgumentApplicationLogging;
import atg.servlet.ServletUtil;

/**
 * 	Apply match mode filter to NavigationState
 *
 */
public class MatchModeNavStateProcessor extends BBBGenericService implements NavigationStateProcessor  {

	VariableArgumentApplicationLogging mLogger = AssemblerTools.getApplicationLogging();
	
	@Override
	public NavigationState process(NavigationState pNavigationState) {
		mLogger.vlogDebug("MatchModeNavStateProcessor: In NavState - "+ pNavigationState);

		//update matchmode so that refinements and other queries also use this matchmode
		String ntxParameter;
		try {
			ntxParameter = getParameter(ServletUtil.getCurrentRequest().getQueryString(),NAV_SEARCH_MODE);
		} catch(UrlFormatException urlFormatExe) {
			ntxParameter = "";
		}
		String partialFlag = ServletUtil.getCurrentRequest().getParameter(BBBEndecaConstants.PARTIAL_FLAG);
		
		NavigationState navState = pNavigationState;
		if(TRUE.equalsIgnoreCase(partialFlag)){
			navState = setMatchModeToNavState(MATCH_MODE_PARTIAL,pNavigationState); 
		} else if(isNotEmpty(ntxParameter)) {
			navState = setMatchModeToNavState(ntxParameter,pNavigationState);
		}

		 mLogger.vlogDebug("MatchModeNavStateProcessor: Out NavState - " + navState);
		 return navState;
	}
	
	/**
	 * 	Apply matchmode to nav state
	 *
	 */
	private NavigationState setMatchModeToNavState(String matchmodeString, NavigationState pNavigationState) {
		MatchMode matchmode = MatchMode.ALL;
		if(matchmodeString.contains(MATCH_MODE_ALL_PARTIAL)){
			matchmode = MatchMode.ALLPARTIAL;
		} else if(matchmodeString.contains(MATCH_MODE_PARTIAL)){
			matchmode = MatchMode.PARTIAL;
		} else if(matchmodeString.contains(MATCH_MODE_ALL)){
			matchmode = MatchMode.ALL;
		} else if(matchmodeString.contains(MATCH_MODE_ANY)){
			matchmode = MatchMode.ANY;
		} else if(matchmodeString.contains(MATCH_MODE_ALL_ANY)){
			matchmode = MatchMode.ALLANY;
		} else if(matchmodeString.contains(MATCH_MODE_PARTIAL_MAX)){
			matchmode = MatchMode.PARTIALMAX;
		} else if(matchmodeString.contains(MATCH_MODE_BOOLEAN)){
			matchmode = MatchMode.BOOLEAN;
		} 
		
		if(null != pNavigationState.getFilterState() 
				&& null != pNavigationState.getFilterState().getSearchFilters()
				&& pNavigationState.getFilterState().getSearchFilters().size() > 0 ) {
			
			List<SearchFilter> searchFilters = pNavigationState.getFilterState().getSearchFilters();
			searchFilters.get(0).setMatchMode(matchmode);
			
			return pNavigationState.updateSearchFilters(searchFilters);
		}
		return pNavigationState;
	}

}
