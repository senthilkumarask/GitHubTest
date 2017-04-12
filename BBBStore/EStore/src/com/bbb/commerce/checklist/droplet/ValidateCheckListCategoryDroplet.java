package com.bbb.commerce.checklist.droplet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import com.bbb.commerce.checklist.vo.CheckListSeoUrlHierarchy;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.endeca.EndecaSearchUtil;
import com.bbb.utils.BBBUtility;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

public class ValidateCheckListCategoryDroplet extends BBBDynamoServlet {
	private EndecaSearchUtil searchUtil;
	
	static final String CATEGORY_ENABLED="categoryEnable";
	static final String CHECKLIST_ENABLED="checkListEnable";
	static final String CHECKLIST_DISPLAY_NAME="checkListDisplayName";
	static final String CATEGORY_DISPLAY_NAME="categoryDisplayName";
	static final String IS_VALID_URL="isValidUrl";

	/**
	 * This Droplet used check the availability of URL in Coherence cahce and
	 * checks checklist and category disable status
	 */
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		String isValidUrl=BBBCoreConstants.FALSE;
		String checkListType=null;
		String checkListEnabled=null;
		String categoryEnabled=null;
		
		String categoryDisplayName=null;
		String checkListDisplayName=null;
		
		SearchQuery pSearchQuery = new SearchQuery();
		logDebug("ValidateCheckListCategoryDroplet Start"); 
		
		getSearchUtil().getCheckListSeoDimId(pSearchQuery, pRequest);
		CheckListSeoUrlHierarchy checkListSeoUrlHierarchy=createCheckListSeoUrl(pSearchQuery);
		if (checkListSeoUrlHierarchy != null) {
			if(checkListSeoUrlHierarchy.isRegTypeCheckList()){
				checkListType=BBBCoreConstants.REGISTRY_TYPE;
			}else{
				checkListType=BBBCoreConstants.GUIDE_TYPE;
			}
			if(checkListSeoUrlHierarchy.isCategoryEnabled())
			{
				categoryEnabled = BBBCoreConstants.TRUE;
			}
			if(! checkListSeoUrlHierarchy.isCheckListDisabled())
			{
				checkListEnabled = BBBCoreConstants.TRUE;
		 	}
			checkListDisplayName=checkListSeoUrlHierarchy.getCheckListDisplayName();
			categoryDisplayName=checkListSeoUrlHierarchy.getCheckListCategoryName();
			pRequest.setParameter(IS_VALID_URL, BBBCoreConstants.TRUE);
		}else{
			pRequest.setParameter(IS_VALID_URL, BBBCoreConstants.FALSE);
			}
		    
		logDebug("ValidateCheckListCategoryDroplet isValidUrl:" + isValidUrl + ",checkListType:" + checkListType
				+ ",categoryDisplayName:" + categoryDisplayName + ",categoryDisplayName:" + categoryDisplayName
				+ ",checkListSeoUrlHierarchy:" + checkListSeoUrlHierarchy);

		pRequest.setParameter(CHECKLIST_DISPLAY_NAME, checkListDisplayName);
		pRequest.setParameter(CATEGORY_DISPLAY_NAME, categoryDisplayName);
		pRequest.setParameter(CATEGORY_ENABLED, categoryEnabled);
		pRequest.setParameter(CHECKLIST_ENABLED, checkListEnabled);
		pRequest.setParameter(BBBCoreConstants.CHECK_LIST_TYPE, checkListType);
		pRequest.serviceLocalParameter(BBBCoreConstants.OPARAM, pRequest, pResponse);
	}




	/**
	 * @param pSearchQuery
	 * @return
	 */
	protected CheckListSeoUrlHierarchy createCheckListSeoUrl(SearchQuery pSearchQuery) {
		return pSearchQuery.getCheckListSeoUrlHierarchyVo();
	}
	
	
	

	/**
	 * @return the searchUtil
	 */
	public EndecaSearchUtil getSearchUtil() {
		return searchUtil;
	}
	

	/**
	 * @param searchUtil the searchUtil to set
	 */
	public void setSearchUtil(EndecaSearchUtil searchUtil) {
		this.searchUtil = searchUtil;
	}
	
}
