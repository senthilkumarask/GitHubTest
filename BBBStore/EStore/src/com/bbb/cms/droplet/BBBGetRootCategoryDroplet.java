package com.bbb.cms.droplet;

import java.util.Map;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;

/**
 * This class is used to get the root category of passed child category.
 * @author ajosh8
 *
 */
public class BBBGetRootCategoryDroplet extends BBBDynamoServlet{
    private static final String ERROR = "error";
	private static final String EMPTY = "empty";
	private static final String OUTPUT = "output";
	private static final String ROOT_CATEGORY = "rootCategory";
	private static final String CHILD_CATEGORY = "childCategory";
	private BBBCatalogTools catalogTools;
 
	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}
	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

 
	public void service(final DynamoHttpServletRequest request, final DynamoHttpServletResponse response)
			throws javax.servlet.ServletException, java.io.IOException {

		
		logDebug("Starting method BBBGetRootCategoryDroplet");
		
		String methodName = "BBBGetRootCategoryDroplet_service";
        BBBPerformanceMonitor.start(methodName  );
		String rootCategoryId;
		String childCategory;
		String siteId = SiteContextManager.getCurrentSiteId();

		if(request.getParameter(CHILD_CATEGORY)!=null){

			childCategory = request.getParameter(CHILD_CATEGORY);	
			
				logDebug("ChildCategory :"+childCategory);
			  
			try {
				Map<String, CategoryVO> categoryMap = this.catalogTools.getParentCategory(childCategory, siteId);
				
				if (categoryMap != null) {
					for (int count = 0; count < categoryMap.size(); count++) {
						CategoryVO category = categoryMap.get(String.valueOf(count)); 
						if (category != null) {
							if (count == categoryMap.size()-1) {
								rootCategoryId = category.getCategoryId();
								request.setParameter(ROOT_CATEGORY, rootCategoryId);   
								request.serviceParameter(OUTPUT, request, response);
								
									logDebug("RootCategory :"+rootCategoryId);
								
							}
							 
						}
					}
				}else{
					request.serviceParameter(EMPTY, request, response);
				}
	    	} catch (BBBBusinessException be) {
				
					logError("Exception while fetching category ["+childCategory+"] - "+be.getMessage());

				
				request.serviceParameter(ERROR,request, response);
			}
			catch (BBBSystemException se) {
				
					logError(LogMessageFormatter.formatMessage(request, "BBBGetRootCategoryDroplet|service()|BBBSystemException","catalog_1026s"),se);

				
				request.serviceParameter(ERROR,request, response);
			}   
		}else{
			request.serviceParameter(EMPTY, request, response);
		}
		
			logDebug("Existing method BBBGetRootCategoryDroplet");
		
		BBBPerformanceMonitor.end(methodName );
	}
}
