package com.bbb.search.endeca.assembler.cartridge.handler;

import java.util.List;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.endeca.constants.BBBEndecaConstants;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.cartridge.NavigationCartridgeHandler;
import com.endeca.infront.navigation.model.CollectionFilter;
import com.endeca.infront.navigation.model.Filter;
import com.endeca.infront.navigation.util.EQLUtil;

/**
 * This handler would convert boostStrata and buryStrata into NOT EQL that can be used 
 * for filtering records from query 
 * @author sc0054
 *
 */
public class NegativeMatchingHandler extends NavigationCartridgeHandler<ContentItem, ContentItem> {
	
	//used for maintaining EQL negative filter in request scope
	private String negativeFilter;
	
	//boolean indicating whether EQL filter from query string is applied
	private boolean queryEqlApplied = false;
	
	//boostStrata and buryStrata are retrieved from property file 
	//as these might change depending on content configuration used
	private String boostStrataPropertyName;
	private String buryStrataPropertyName;
	
	//filter conditions used for adding in EQL
	private static final String FILTER_CONDITION_START = "( ";
	private static final String FILTER_CONDITION_END = " )";
	private static final String FILTER_CONDITION_NOT = " not ";
	private static final String FILTER_CONDITION_AND = " and ";
	
	//final condition defined for EQL
	private static final String NAV_REC_STRUCT_CONDITION_START = "collection()/record[";
	private static final String NAV_REC_STRUCT_CONDITION_END = "]";


	/**
	 * Overriding abstract method and returning same content item back
	 */
	@Override
	protected ContentItem wrapConfig(ContentItem pContentItem) {
		return pContentItem;
	}
	
	/**
	 * Updated negativeFilter based on boostStrata and buryStrata in ContentItem
	 * These will be in required format as content item used is of results list type 
	 */
	@Override
	public void preprocess(ContentItem pContentItem) {
		List<CollectionFilter> boostStrata = null;
		List<CollectionFilter> buryStrata = null;
		
		//boostStrata & buryStrata would be in List<CollectionFilter> format already
		if(null != pContentItem.get(getBoostStrataPropertyName()) && pContentItem.get(getBoostStrataPropertyName()) instanceof List<?>) {
			boostStrata = (List<CollectionFilter>) pContentItem.get(getBoostStrataPropertyName());			
		}
		if(null != pContentItem.get(getBuryStrataPropertyName()) && pContentItem.get(getBuryStrataPropertyName()) instanceof List<?>) {
			buryStrata = (List<CollectionFilter>) pContentItem.get(getBuryStrataPropertyName());
		}
		
		//check if existing query has EQL filter and apply only for the first time
		if(!queryEqlApplied 
				&& getNavigationState() != null 
				&& getNavigationState().getFilterState() != null 
				&& getNavigationState().getFilterState().getEqlFilter() != null) {
			String currentEqlFilter = getNavigationState().getFilterState().getEqlFilter().getExpression();
			if(currentEqlFilter != null && !currentEqlFilter.equals("")) {
				negativeFilter = currentEqlFilter;
				queryEqlApplied = true;
			}
		}

		if(null != boostStrata || null != buryStrata) {
			String eqlFilter = getNotEqlFilterString(boostStrata,buryStrata);
			
			//parse eql filter only when it contains at least one filter
			if(null != eqlFilter) {

				//if negativeFilter already exists append current eql to it and set that to current request
				if(null != negativeFilter && !negativeFilter.equals("")) {
					negativeFilter = removeCollectionRecordFilter(negativeFilter) + FILTER_CONDITION_AND + FILTER_CONDITION_START 
													+ eqlFilter + FILTER_CONDITION_END;
				} else {
					negativeFilter = eqlFilter;
				}
				
				negativeFilter = NAV_REC_STRUCT_CONDITION_START + negativeFilter + NAV_REC_STRUCT_CONDITION_END;
			}
		}
		
		//ILD-239
		DynamoHttpServletRequest dynamoRequest = ServletUtil.getCurrentRequest();
		SearchQuery searchQuery = (SearchQuery)dynamoRequest.getAttribute(BBBEndecaConstants.RQST_PARAM_NAME_SEARCH_QUERY_VO);
		if(null != negativeFilter && !negativeFilter.equals("")) {
			
			if(null == searchQuery || (null != searchQuery && !searchQuery.isHeaderSearch()) ) {
				/***
				 * FilterState is used to get the Negative filters in
				 * ResultListsHandler which are injected in
				 * MdexRequestBroker.
				 */
				dynamoRequest.setAttribute(BBBEndecaConstants.NEGATIVE_FILTER, negativeFilter);
			}
		}
	}
	
	
	/**
	 * getter method for returning negativeFilter
	 * @return
	 */
	public String getNegativeFilter() {
		if(null != this.negativeFilter && !this.negativeFilter.equals("")) {
			return this.negativeFilter;
		}
		return null;
	}
	
	
	/**
	 * Reads booststrata and burystrata and converts to not eql queries based on either of the conditions
	 * booststrata is condition 1 and burystrata becomes condition 2 
	 * @param boostStrata
	 * @param buryStrata
	 * @return
	 */
	public static String getNotEqlFilterString(List<CollectionFilter> boostStrata, List<CollectionFilter> buryStrata) {
		if (((boostStrata == null) || (boostStrata.size() == 0)) && ((buryStrata == null) || (buryStrata.size() == 0)))
	    {
	      return null;
	    }
	    StringBuilder buf = new StringBuilder();
	    //buf.append("stratify(");

	    if (boostStrata != null && boostStrata.size() > 0)
	    {
		    String prefix = "";
	    	buf.append(FILTER_CONDITION_NOT).append(FILTER_CONDITION_START);
		    for (Filter boostFilter : boostStrata)
		    {
		    	buf.append(prefix);
		    	buf.append(removeCollectionRecordFilter(EQLUtil.toEQLString(boostFilter)));
		    	prefix = FILTER_CONDITION_AND;
		    }
		    buf.append(FILTER_CONDITION_END);
		    
	    }

	    if (buryStrata != null && buryStrata.size() > 0)
	    {
	    	//check if there are any filters applied in booststrata 
	    	if(!buf.equals("")) {
	    		buf.append(FILTER_CONDITION_AND);
	    	}
		    String prefix = "";
	    	buf.append(FILTER_CONDITION_NOT).append(FILTER_CONDITION_START);
	    	for (Filter buryFilter : buryStrata)
	    	{
		    	buf.append(prefix);
		    	buf.append(removeCollectionRecordFilter(EQLUtil.toEQLString(buryFilter)));
		    	prefix = FILTER_CONDITION_AND;
	    	}
	    	buf.append(FILTER_CONDITION_END);
	    	
	    }

	    return buf.toString();
	}
	
	/**
	 * remove unwanted "collection()/record[" and "]" strings tagged to output of EQLUtil.toEQLString() 
	 * @param inputEqlString
	 * @return
	 */
	private static String removeCollectionRecordFilter(String inputEqlString) {
		String outputEqlString = "";
		
		//process string only when length is > 21 indicating it contains string to be parsed out  
		if(null != inputEqlString && !inputEqlString.equals("") && inputEqlString.length() > 21) {
			outputEqlString = inputEqlString.substring(NAV_REC_STRUCT_CONDITION_START.length(),inputEqlString.length()-1);
		}
		
		return outputEqlString;
	}

	/**
	 * @return the boostStrataPropertyName
	 */
	public String getBoostStrataPropertyName() {
		return boostStrataPropertyName;
	}

	/**
	 * @param boostStrataPropertyName the boostStrataPropertyName to set
	 */
	public void setBoostStrataPropertyName(String boostStrataPropertyName) {
		this.boostStrataPropertyName = boostStrataPropertyName;
	}

	/**
	 * @return the buryStrataPropertyName
	 */
	public String getBuryStrataPropertyName() {
		return buryStrataPropertyName;
	}

	/**
	 * @param buryStrataPropertyName the buryStrataPropertyName to set
	 */
	public void setBuryStrataPropertyName(String buryStrataPropertyName) {
		this.buryStrataPropertyName = buryStrataPropertyName;
	}	
	
}
