package com.bbb.cms.droplet;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.common.BBBDynamoServlet;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
/**
 * CollegeCategoryDroplet retrieves the college categories based on categoryId from CatalogAPI
 * @author kshah
 *
 */
public class CollegeCategoryDroplet extends BBBDynamoServlet {


	private BBBCatalogTools catalogTools = null;

	

	/**
	 * Return CatalogTools
	 * @return
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * Setting CatalogTools
	 * @param catalogTools
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * This method gets the value of site id from the page and fetches the states
	 * greater than the current date and site id.
	 */
	public void service(final DynamoHttpServletRequest request, final DynamoHttpServletResponse response)
	throws javax.servlet.ServletException, java.io.IOException {

		
			logDebug("starting method CollegeCategoryDroplet");
		
		
		final String COLLEGE_OUTPUT = "output";
		final String COLLEGE_CATEGORIES="collegeCategories";

		//Request College sub-categories
		String siteId = getCurrentSiteId();
		try{
			String rootCollegeId=this.getCatalogTools().getRootCollegeIdFrmConfig(siteId);
			
			logDebug("Calling getCategoryDetail for Category : "+rootCollegeId+" and site:"+siteId);
		
			CategoryVO collegeCategory = getCatalogTools().getCategoryDetail(siteId,rootCollegeId,false);
			if (collegeCategory != null) {
				collegeCategory = getCatalogTools().getSortedCollegeCategory(collegeCategory);
			}
			
				logDebug("CollegeCategory will be created : ");
			
			request.setParameter(COLLEGE_CATEGORIES, collegeCategory);
		}catch(BBBBusinessException be){
			
			logError(LogMessageFormatter.formatMessage(request, "CollegeCategorydroplet|service|BBBBusinessException","catalog_1030"),be);
			   
			request.serviceParameter(BBBCoreConstants.ERROR_OPARAM,request, response);
		} catch(BBBSystemException bs){
			
			logError(LogMessageFormatter.formatMessage(request, "CollegeCategorydroplet|service|BBBSystemException","catalog_1031"),bs);
			    
			request.serviceParameter(BBBCoreConstants.ERROR_OPARAM,request, response);
		}
		request.serviceParameter(COLLEGE_OUTPUT, request, response);
		
			logDebug("Existing method CollegeCategoryDroplet");
	
	}

	protected String getCurrentSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}
}