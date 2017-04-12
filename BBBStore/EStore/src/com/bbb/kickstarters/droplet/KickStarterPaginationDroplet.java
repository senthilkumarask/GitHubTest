package com.bbb.kickstarters.droplet;
/**
 * This class retrives consultants by siteId for anonymous customer. 
 * And by siteId and eventType for loggedIn customer.
 * 
 * @author dwaghmare
 * 
 */

import java.util.ArrayList;
import java.util.List;

import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;



import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.kickstarters.tools.KickStarterTools;

public class KickStarterPaginationDroplet extends BBBDynamoServlet {
	
	public static final String OPARAM_ERROR = "error";
	private KickStarterTools kickStarterTools;
	

	public KickStarterTools getKickStarterTools() {
		return kickStarterTools;
	}


	public void setKickStarterTools(KickStarterTools kickStarterTools) {
		this.kickStarterTools = kickStarterTools;
	}

	/**
	 * Droplet to provide previous and next ids
	 *
	 */
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse)
			throws javax.servlet.ServletException, java.io.IOException {

        BBBPerformanceMonitor.start( KickStarterPaginationDroplet.class.getName() + " : " + "service");
        
		String id = ((String) pRequest.getLocalParameter("kickStarterId"));
		String kickStarterType = ((String) pRequest.getLocalParameter("kickStarterType"));
		String eventType = ((String) pRequest.getLocalParameter("eventType"));
		String siteId = (String) pRequest.getLocalParameter("siteId");
		boolean isTransient = ((Boolean) pRequest.getLocalParameter("isTransient"));
		
		List<String> topConsultantIds =new ArrayList<String>();
		  List<String> shopTheLookIds =new ArrayList<String>();
		
		List<String> kickStarterIds =new ArrayList<String>();
			 List<RepositoryItem> kickStarterDataItems =(List) new ArrayList<RepositoryItem>();

			  try {
				kickStarterDataItems = this.kickStarterTools.getKickStartersByType(eventType, siteId, isTransient,kickStarterType);
					if(kickStarterDataItems!=null){
						for(RepositoryItem kickStarterDataItem:kickStarterDataItems ){
							  if(kickStarterType.equalsIgnoreCase(BBBGiftRegistryConstants.TOP_CONSULTANT)){
								  topConsultantIds.add((String)kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.ID));
							  }else{
								  shopTheLookIds.add((String)kickStarterDataItem.getPropertyValue(BBBGiftRegistryConstants.ID));
							  }
						  }
						  if(kickStarterType.equalsIgnoreCase(BBBGiftRegistryConstants.TOP_CONSULTANT)){
							  kickStarterIds =topConsultantIds;
							  }
							  else{
								  kickStarterIds =shopTheLookIds;
							  }
					}
			  
			  } catch (RepositoryException e) {
				
					logError(e.getMessage());
				
			}
		String nextId = null;
		String previousId = null;
		int index = kickStarterIds.indexOf(id);
		if (index < 0 || index + 1 == kickStarterIds.size()) {
			logDebug("Kickstarter pagination droplet - (in if condition) - no operation in this case");
		} else {
			nextId = kickStarterIds.get(index + 1);
		}
		if (index > 0) {
			previousId = kickStarterIds.get(index - 1);
		}

		pRequest.setParameter("nextId", nextId);
		pRequest.setParameter("previousId", previousId);
		pRequest.serviceParameter(BBBCmsConstants.OUTPUT, pRequest, pResponse);
		
		 BBBPerformanceMonitor.end( KickStarterPaginationDroplet.class.getName() + " : " + "service");
	}
}