package com.bbb.commerce.checklist.droplet;

import java.io.IOException;
import javax.servlet.ServletException;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.search.endeca.EndecaSearchUtil;
import com.bbb.utils.BBBUtility;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**

 
 * @description The droplet retrieves productCount for a checklist category
 * @version 1.0
 */

public class CheckListCatProdCountDroplet extends BBBDynamoServlet {

	private EndecaSearchUtil searchUtil;
	public static final String PRODUCT_COUNT_UPDATED = "productCountUpdated";

	/**
	 * The method retrieves productCount for a checklist category
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		
		logDebug("[START} CheckListCatProdCountDroplet service:");
		long prodCount=0;
		
	try {
		 
			String facetIdCollection = pRequest.getParameter("facetIdCollection");
			String seoUrlDimensionId = pRequest.getParameter("seoUrlDimensionId");
			
			logDebug("[START} CheckListCatProdCountDroplet catalogNavigation:" + facetIdCollection	+ ",seoUrlDimensionId:" + seoUrlDimensionId);
			
				if (BBBUtility.isNotBlank(facetIdCollection) && BBBUtility.isNotBlank(seoUrlDimensionId)) {
					 
				 StringBuffer newNavigationId= new StringBuffer();
				 newNavigationId.append(seoUrlDimensionId);				 
				 newNavigationId.append(BBBCoreConstants.SPACE);
				 newNavigationId.append(facetIdCollection.trim().replaceAll(BBBCoreConstants.HYPHEN, BBBCoreConstants.SPACE));
				 prodCount = getSearchUtil().getProductCount(newNavigationId.toString().trim(),null);
				} 
		} catch ( BBBBusinessException  bbbBusinessExc) {
			logError("[START} CheckListCatProdCountDroplet BBBBusinessException:"+bbbBusinessExc.getMessage());
		
		} catch ( BBBSystemException systemException) {
			logError("[START} CheckListCatProdCountDroplet BBBSystemException:"+systemException.getMessage());
		} catch ( Exception exception) {
			logError("[START} CheckListCatProdCountDroplet Exception"+exception.getMessage());
		}
	 
	pRequest.setParameter(PRODUCT_COUNT_UPDATED, prodCount);
	pRequest.serviceLocalParameter(BBBCoreConstants.OPARAM, pRequest, pResponse);
	
	logDebug("[END} CheckListCatProdCountDroplet service:");
		
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
