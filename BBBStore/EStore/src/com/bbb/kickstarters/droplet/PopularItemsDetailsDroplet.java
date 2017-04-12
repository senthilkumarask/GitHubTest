package com.bbb.kickstarters.droplet;

import java.util.List;

import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.kickstarters.manager.KickStarterManager;
/**
 * This droplet is used to retrieve popular items
 * 
 * @author rsirangu 
 * 
 */

public class PopularItemsDetailsDroplet extends BBBDynamoServlet {

   /**
	 * to hold kick starter manager Variable
	 *           
	 */
   private KickStarterManager mKickStarterManager = null;
   
   /**
     * to get kick starter manager
     *  
	 * @return mKickStarterManager
	 *            the mKickStarterManager to set
	 */
	public KickStarterManager getKickStarterManager() {
		return mKickStarterManager;
	}

	/**
	 * to set kick starter manager
	 * 
	 * @param pStaticTemplateManager
	 *            the mStaticTemplateManager to set
	 */
	public void setKickStarterManager(KickStarterManager pKickStarterManager) {
		mKickStarterManager = pKickStarterManager;
	}

	/**
	 * This droplet is used to retrieve popular items . this method need site id or registry type in the request.
	 * Site Id is mandatory to get results if there is no registry type passed then this service method will return all
	 * popular products specific to site.
	 * @param pRequest - request object
	 * @param pResponse - response object
	 * @exception  ServletException - if SERVLET failed to load
	 * @exception  IOException - input or ouput operation exeception
	 */
	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	throws javax.servlet.ServletException, java.io.IOException {
		
		BBBPerformanceMonitor.start( PopularItemsDetailsDroplet.class.getName() + " : " + "service");
		
		
		logDebug("Enter." + PopularItemsDetailsDroplet.class.getName() +" " +"service" );
		
		
		String registryType = ((String) pRequest.getLocalParameter("registryType"));
		String siteId = ((String) pRequest.getLocalParameter("siteId"));
		try {
			List<ProductVO> listPopularItems = getKickStarterManager().getPopularItemsDetails(registryType,siteId);				
			if(listPopularItems!=null && !listPopularItems.isEmpty()){
				logDebug(listPopularItems.toString());				
				pRequest.setParameter("popularItems",listPopularItems);
				pRequest.serviceParameter(BBBCmsConstants.OUTPUT, pRequest,pResponse);	
			}
		} catch (RepositoryException e) {
			
				logError(e.getMessage());
			
		}
		
		
		logDebug("Exit." + PopularItemsDetailsDroplet.class.getName() +" " +"service" );
		
		
		BBBPerformanceMonitor.end( PopularItemsDetailsDroplet.class.getName() + " : " + "service");
	}
}