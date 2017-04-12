package com.bbb.search.endeca.assembler.cartridge.manager;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.endeca.assembler.cartridge.handler.NegativeMatchingHandler;
import com.bbb.search.endeca.constants.BBBEndecaConstants;
import com.endeca.infront.navigation.MdexRequestBroker;
import com.endeca.infront.navigation.NavigationException;
import com.endeca.infront.navigation.model.EqlFilter;
import com.endeca.infront.navigation.model.FilterState;
import com.endeca.infront.navigation.model.MdexResource;
import com.endeca.infront.navigation.request.MdexQuery;
import com.endeca.infront.navigation.request.MdexRequest;

/**
 * This class is used for overwriting createMdexRequest that gets called by all handlers
 * In this method negativeMatchingHandler's negativeFilter is used to update EQLFilter of filter state 
 * 
 * @author sc0054
 *
 */
public class ExtendedMdexRequestBroker extends MdexRequestBroker {
	
	/**
	 * @return the negativeMatchingContentInitializer
	 */
	public NegativeMatchingHandler getNegativeMatchingHandler() {
		return negativeMatchingHandler;
	}

	/**
	 * @param negativeMatchingHandler the negativeMatchingHandler to set
	 */
	public void setNegativeMatchingHandler(
			NegativeMatchingHandler negativeMatchingHandler) {
		this.negativeMatchingHandler = negativeMatchingHandler;
	}

	NegativeMatchingHandler negativeMatchingHandler;

	public ExtendedMdexRequestBroker(MdexResource mdexResource,
			boolean debugEnabled) {
		super(mdexResource, debugEnabled);
	}
	
	/**
	 * Updating filter state to use negativefilter from servletrequest set inside negativematching handler
	 */
	@Override
	public MdexRequest createMdexRequest(FilterState filterState, MdexQuery mdexQuery) throws NavigationException {
		
 	if(null != negativeMatchingHandler && null != negativeMatchingHandler.getNegativeFilter()) {
			String negativeFilter = negativeMatchingHandler.getNegativeFilter();
			
			//process using negative filter fetched from request scope
			if(null != negativeFilter && !negativeFilter.equals("") && null != filterState) {
				
				DynamoHttpServletRequest dynamoRequest = ServletUtil.getCurrentRequest();
				SearchQuery searchQuery = (SearchQuery)dynamoRequest.getAttribute(BBBEndecaConstants.RQST_PARAM_NAME_SEARCH_QUERY_VO);
				
				//apply negative matching filter only when current query is not header search
				if(null == searchQuery || (null != searchQuery && !searchQuery.isHeaderSearch()) ) {
					filterState.setEqlFilter(new EqlFilter(negativeFilter));
				}
			}
	}
		
		return super.createMdexRequest(filterState, mdexQuery);
	}

}
