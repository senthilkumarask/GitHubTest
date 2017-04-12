package com.bbb.cms.droplet;

import java.util.List;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

public class PageTabsOrderingDroplet extends BBBDynamoServlet {

	private BBBCatalogTools mCatalogTools = null;
	public static final String CLEARANCEDEALS ="Clearance Deals";
	//Defining the Constants
	
	private String mAlsoCheckOut;
	private String mLastViewedItems;
	private String mTopCollegeItems;
	private String mClearanceDeals;
	private String mJustForYou;
	
	private String mCustomerAlsoViewed;
	private String mFrequentlyBoughtWith;
	private String mTopUniversityItems;
	
	/**
	 * @return the alsoCheckOut
	 */
	public String getAlsoCheckOut() {
		return this.mAlsoCheckOut;
	}

	/**
	 * @param pAlsoCheckOut the alsoCheckOut to set
	 */
	public void setAlsoCheckOut(String pAlsoCheckOut) {
		this.mAlsoCheckOut = pAlsoCheckOut;
	}

	/**
	 * @return the lastViewedItems
	 */
	public String getLastViewedItems() {
		return this.mLastViewedItems;
	}

	/**
	 * @param pLastViewedItems the lastViewedItems to set
	 */
	public void setLastViewedItems(String pLastViewedItems) {
		this.mLastViewedItems = pLastViewedItems;
	}

	/**
	 * @return the topCollegeItems
	 */
	public String getTopCollegeItems() {
		return this.mTopCollegeItems;
	}

	/**
	 * @param pTopCollegeItems the topCollegeItems to set
	 */
	public void setTopCollegeItems(String pTopCollegeItems) {
		this.mTopCollegeItems = pTopCollegeItems;
	}

	/**
	 * @return the clearanceDeals
	 */
	public String getClearanceDeals() {
		return this.mClearanceDeals;
	}

	/**
	 * @param pClearanceDeals the clearanceDeals to set
	 */
	public void setClearanceDeals(String pClearanceDeals) {
		this.mClearanceDeals = pClearanceDeals;
	}

	/**
	 * @return the custForYou
	 */
	public String getJustForYou() {
		return this.mJustForYou;
	}

	/**
	 * @param pCustForYou the custForYou to set
	 */
	public void setJustForYou(String pJustForYou) {
		this.mJustForYou = pJustForYou;
	}

	/**
	 * @return the customerAlsoViewed
	 */
	public String getCustomerAlsoViewed() {
		return this.mCustomerAlsoViewed;
	}

	/**
	 * @param pCustomerAlsoViewed the customerAlsoViewed to set
	 */
	public void setCustomerAlsoViewed(String pCustomerAlsoViewed) {
		this.mCustomerAlsoViewed = pCustomerAlsoViewed;
	}

	/**
	 * @return the frequentlyBoughtWith
	 */
	public String getFrequentlyBoughtWith() {
		return this.mFrequentlyBoughtWith;
	}

	/**
	 * @param pFrequentlyBoughtWith the frequentlyBoughtWith to set
	 */
	public void setFrequentlyBoughtWith(String pFrequentlyBoughtWith) {
		this.mFrequentlyBoughtWith = pFrequentlyBoughtWith;
	}

	/**
	 * @return the topUniversityItems
	 */
	public String getTopUniversityItems() {
		return this.mTopUniversityItems;
	}

	/**
	 * @param pTopUniversityItems the topUniversityItems to set
	 */
	public void setTopUniversityItems(String pTopUniversityItems) {
		this.mTopUniversityItems = pTopUniversityItems;
	}

	/**
	 * @return the clearancedeals
	 */
	public static String getStaticClearanceDeals() {
		return CLEARANCEDEALS;
	}

	/**
	 * @return the mBBBCatalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.mCatalogTools;
	}

	/**	
	 * @param mBBBCatalogTools the mBBBCatalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.mCatalogTools = catalogTools;
	}

	@Override
	public void service(final DynamoHttpServletRequest request, final DynamoHttpServletResponse response)
	throws javax.servlet.ServletException, java.io.IOException {
		
		final String className = "PageTabsOrderingDroplet";
		
			logDebug("Entering  " + className);
		
		BBBPerformanceMonitor.start(className); 
		
		

		try{
							
			
			String pageName =  request.getParameter("pageName");
			if(BBBUtility.isNotEmpty(pageName)){
				List<String> tabs = getCatalogTools().getTabNameList(pageName);
				if(null != tabs){
					request.setParameter("pageTab",tabs);
					request.serviceParameter(BBBCoreConstants.OUTPUT,request, response);
				}
				else{
					request.serviceParameter(BBBCoreConstants.EMPTY,request, response);
				}
			}
		}catch(BBBBusinessException be){
			
			logError(LogMessageFormatter.formatMessage(request, "PageTabsOrderingDroplet|service()|BBBBusinessException","catalog_1042"),be);
			
			request.serviceParameter(BBBCoreConstants.ERROR_OPARAM,request, response);
		} catch(BBBSystemException bs){
			
			logError(LogMessageFormatter.formatMessage(request, "PageTabsOrderingDroplet|service()|BBBSystemException","catalog_1043"),bs);
			
			request.serviceParameter(BBBCoreConstants.ERROR_OPARAM,request, response);
		}
		
		logDebug("Exiting  " + className);
		
		BBBPerformanceMonitor.end(className);
	}
}