package com.bbb.kickstarters.droplet;


import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.kickstarters.KickStarterVO;
import com.bbb.kickstarters.manager.KickStarterManager;
/**
 * This class retrives consultant details as per given consultant id.
 * 
 * @author dwaghmare
 * 
 */
public class KickStarterDetailsDroplet extends BBBDynamoServlet {
	
	/**
	 * to hold error variable
	 */
	public static final String OPARAM_ERROR = "error";
	
	/**
	 * to hold Kick Starter Manager
	 */
	private KickStarterManager mKickStarterManager = null;
	
	/**
	 * to hold manager class
	 * 
	 * @return mKickStarterManager - to hold manager class
	 */
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
	 * Droplet to retrieve the top consultant details as per consultantId, eventType and siteId.
	 * 
	 * @param  pRequest - DynamoHttpServletRequest
	 * @param  pResponse - DynamoHttpServletResponse
	 */
	/* (non-Javadoc)
	 * @see atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
	 */
	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	throws javax.servlet.ServletException, java.io.IOException {
		BBBPerformanceMonitor.start( KickStarterDetailsDroplet.class.getName() + " : " + "service");
		String siteId = (String) pRequest.getLocalParameter("siteId");
		String consultantId = ((String) pRequest.getLocalParameter("consultantId"));
		String eventType = ((String) pRequest.getLocalParameter("eventType"));
		KickStarterVO kickStarterVO = new KickStarterVO();
		try {
			kickStarterVO= getKickStarterManager().getKickStarterDetails(siteId,consultantId,eventType);
			if(kickStarterVO !=null){
				pRequest.setParameter("kickStarterVO",kickStarterVO);
				pRequest.serviceParameter(BBBCmsConstants.OUTPUT, pRequest,
						pResponse);
			}else{
				pRequest.serviceParameter(BBBCmsConstants.EMPTY, pRequest,
						pResponse);
			}
		} catch (RepositoryException e) {
			
			logError(e.getSourceException());
			
		}
		BBBPerformanceMonitor.end(KickStarterDetailsDroplet.class.getName() + " : " + "service");
	}
}