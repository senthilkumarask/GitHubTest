package com.bbb.cms.droplet;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.common.BBBDynamoServlet;

public class HeaderWhatsNewDroplet extends BBBDynamoServlet{
	
	private BBBCatalogTools mCatalogTools;
	private String whatsNewCategoryBaby;
	private String whatsNewCategoryUS;
	private String whatsNewCategoryCanada;
	
	final static String HEADER_OUTPUT = "output";
	final static String HEADER_EMPTY = "empty";
	
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}
		
	public String getWhatsNewCategoryUS() {
		return whatsNewCategoryUS;
	}
	public void setWhatsNewCategoryUS(String whatsNewCategoryUS) {
		this.whatsNewCategoryUS = whatsNewCategoryUS;
	}
	
	public String getWhatsNewCategoryCanada() {
		return whatsNewCategoryCanada;
	}
	public void setWhatsNewCategoryCanada(String whatsNewCategoryCanada) {
		this.whatsNewCategoryCanada = whatsNewCategoryCanada;
	}
	
	public String getWhatsNewCategoryBaby() {
		return whatsNewCategoryBaby;
	}
	public void setWhatsNewCategoryBaby(String whatsNewCategoryBaby) {
		this.whatsNewCategoryBaby = whatsNewCategoryBaby;
	}
	
	public void service(final DynamoHttpServletRequest request, final DynamoHttpServletResponse response)
			throws javax.servlet.ServletException, java.io.IOException {

		
		logDebug("starting method HeaderWhatsNewDroplet");
		
		
		String methodName = "HeaderWhatsNewDroplet_service";
        BBBPerformanceMonitor.start(methodName  );
		String categoryId = null;
		String catKey = null;
		String site = SiteContextManager.getCurrentSiteId();
		
		if(site==null)
		{
			site=request.getParameter("site");
		}
		
		try {
			if(site.equalsIgnoreCase("BuyBuyBaby") || site.equalsIgnoreCase("TBS_BuyBuyBaby"))
			{
				catKey = this.getWhatsNewCategoryBaby();
			}
			else if(site.equalsIgnoreCase("BedBathCanada") || site.equalsIgnoreCase("TBS_BedBathCanada"))
			{
				catKey = this.getWhatsNewCategoryCanada();
			}
			else if(site.equalsIgnoreCase("BedBathUS") || site.equalsIgnoreCase("TBS_BedBathUS"))
			{
				catKey = this.getWhatsNewCategoryUS();
			}
			
			if(catKey != null)
			{
				categoryId = getCatalogTools().getContentCatalogConfigration(catKey).get(0);	
			}

		} catch (BBBSystemException e) {					
			
			logError(LogMessageFormatter.formatMessage(request, "HeaderWhatsNewDroplet.service() | BBBSystemException ","catalog_1037"), e);
								
		} catch (BBBBusinessException e) {	
			
			logError(LogMessageFormatter.formatMessage(request, "HeaderWhatsNewDroplet.service() | BBBBusinessException ","catalog_1038"), e);
			
		}
		
		if (categoryId != null) {
			request.setParameter("categoryId", categoryId);	
			request.serviceParameter(HEADER_OUTPUT, request, response);
		}else{
			
			logDebug("Received Header CategoryId as Null: ");
			
			request.serviceParameter(HEADER_EMPTY, request, response);
		}
		

		
		logDebug("Existing method HeaderWhatsNewDroplet");
	
		BBBPerformanceMonitor.end(methodName );
	}

}
