package com.bbb.kickstarters.droplet;
/**
 * This class retrives consultants by siteId for anonymous customer. 
 * And by siteId and eventType for loggedIn customer.
 * 
 * @author dwaghmare
 * 
 */

import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;



import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.kickstarters.KickStarterItemsVO;
import com.bbb.kickstarters.manager.KickStarterManager;

public class TopConsultantsDroplet extends BBBDynamoServlet {
	
	public static final String OPARAM_ERROR = "error";
	private KickStarterManager mKickStarterManager = null;
	
	
	
	
	public KickStarterManager getKickStarterManager() {
		return mKickStarterManager;
	}

	/**
	 * @param pStaticTemplateManager
	 *            the mStaticTemplateManager to set
	 */
	public void setKickStarterManager(KickStarterManager pKickStarterManager) {
		mKickStarterManager = pKickStarterManager;
	}



	/**
	 * Droplet to retrieve the top consultant details
	 *
	 */
	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	throws javax.servlet.ServletException, java.io.IOException {
		
		BBBPerformanceMonitor.start( TopConsultantsDroplet.class.getName() + " : " + "service");
		
		com.bbb.kickstarters.KickStarterItemsVO KickStarterItemsVO = new KickStarterItemsVO();
		String siteId = (String) pRequest.getLocalParameter("site_id");
		String registryType = ((String) pRequest.getLocalParameter("registryType"));
		boolean isTransient = ((Boolean) pRequest.getLocalParameter("isTransient"));
		String kickStarterType =BBBGiftRegistryConstants.TOP_CONSULTANT;		
		try {
				KickStarterItemsVO = getKickStarterManager().getKickStartersByType(registryType, siteId, isTransient,kickStarterType);

			if(KickStarterItemsVO !=null){
				pRequest.setParameter("kickStarterDataItemsList",KickStarterItemsVO.getKickStarterItems());
				pRequest.serviceParameter(BBBCmsConstants.OUTPUT, pRequest,
						pResponse);
			}else{
				pRequest.serviceParameter(BBBCmsConstants.EMPTY, pRequest,
						pResponse);
			}
		
		} catch (RepositoryException e) {
			
				logError(e.getMessage());
			
		}
		
		BBBPerformanceMonitor.end( TopConsultantsDroplet.class.getName() + " : " + "service");
		
	}
}