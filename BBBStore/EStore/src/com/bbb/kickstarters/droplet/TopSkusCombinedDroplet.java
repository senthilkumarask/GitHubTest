package com.bbb.kickstarters.droplet;
/**
 * This class retrives consultant details as per given consultant id.
 * 
 * @author dwaghmare
 * 
 */

import java.util.ArrayList;
import java.util.List;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.kickstarters.PicklistVO;
import com.bbb.kickstarters.TopSkuVO;

public class TopSkusCombinedDroplet extends BBBDynamoServlet {
	
	public static final String OPARAM_ERROR = "error";

	private BBBCatalogTools catalogTools = null;	
	
	
	
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * Droplet to retrieve the top consultant details as per consultantId and eventType.
	 *
	 */
	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	throws javax.servlet.ServletException, java.io.IOException {
		
		BBBPerformanceMonitor.start( TopSkusDroplet.class.getName() + " : " + "service");
		List<TopSkuVO> topSkuItems=new ArrayList<TopSkuVO>();
		List<TopSkuVO> uniqueTopSkuItems=new ArrayList<TopSkuVO>();
		List<TopSkuVO> uniqueTopSkuItems1=new ArrayList<TopSkuVO>();
		List<PicklistVO> picklistsVO  = (List<PicklistVO>) pRequest.getLocalParameter("pickLists");
		if(picklistsVO !=null && !picklistsVO.isEmpty()){
		for(PicklistVO picklistVO: picklistsVO){
			topSkuItems.addAll(picklistVO.getTopSkus());
		}
		pRequest.setParameter("topSkus",topSkuItems);
		pRequest.serviceParameter(BBBCmsConstants.OUTPUT, pRequest,
				pResponse);
		}else{
				pRequest.serviceParameter(BBBCmsConstants.EMPTY, pRequest,
						pResponse);
			}
		
		
		BBBPerformanceMonitor.end( TopSkusDroplet.class.getName() + " : " + "service");
	}
}
