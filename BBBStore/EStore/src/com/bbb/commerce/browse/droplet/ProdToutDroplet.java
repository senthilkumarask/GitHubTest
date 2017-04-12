package com.bbb.commerce.browse.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.multisite.SiteContextManager;
import atg.nucleus.ServiceMap;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.browse.category.IProdToutHelper;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

/**
 * Droplet to access all data clearance and last viewed tabs.
 * 
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 * Reproduction or use of this file without explicit written consent is prohibited.
 * Created by: njai13
 * Created on: November-2011
 * @author njai13
 * upbdated on: Apr 18, 2014
 * @author Rashid M
 *
 */
public class ProdToutDroplet extends BBBDynamoServlet {
	public final static String  OPARAM_OUTPUT="output";
	public final static String  OPARAM_EMPTY="empty";
	public final static String  PARAMETER_PRODUCT_LIST_MAP="productsListMap";
	public final static String  PARAMETER_LAST_VIEWED_PRODUCT_LIST="lastViewedproductsList";

	public static final ParameterName TABLIST = ParameterName.getParameterName("tabList");
	public final static String  PARAMETER_SITE_ID="siteId";

	public final static String  PARAMETER_CATEGORY_ID="id";
	public final static String LINK_STRING = "linkString";

	
	private ServiceMap tabsToutHelperServiceMap;
	
	/**
	 * @return the tabsToutHelperServiceMap
	 */
	public ServiceMap getTabsToutHelperServiceMap() {
		return tabsToutHelperServiceMap;
	}
	/**
	 * @param tabsToutHelperServiceMap the tabsToutHelperServiceMap to set
	 */
	public void setTabsToutHelperServiceMap(ServiceMap tabsToutHelperServiceMap) {
		this.tabsToutHelperServiceMap = tabsToutHelperServiceMap;
	}
	
	/**
	 * The service method takes String of list of tabs to be displayed on the page
	 * The map of tab and the corresponding class to be instantiated  is provided 
	 * in toutHelperMap
	 * Once the list of productVO is obtained it is set as parameter with param name as
	 * key+"ProductsList".
	 * 
	 * 
	 */

	public void service(DynamoHttpServletRequest request,
			DynamoHttpServletResponse response)
					throws ServletException, IOException
					{
		final String methodName="ProdToutDroplet:service";
		BBBPerformanceMonitor.start(methodName);
		String linkString = "";
		logDebug("Method entry ["+methodName+"]");
		
		final String tabList= request.getParameter(TABLIST);
		String[] tabs = null;
		if(tabList!=null){
			tabs = tabList.split(",");
		}

		String categoryId=request.getParameter(PARAMETER_CATEGORY_ID );
		String siteId = request.getParameter(BBBCmsConstants.SITE_ID);
		
		logDebug("siteId from request ["+siteId+"]");
		
		if (siteId == null) {
			siteId = extractCurrentSiteId();
			logDebug("siteId from request is null getting siteId from SiteContextManager ["+siteId+"]");
		}

		if(!BBBUtility.isEmpty(tabs)){
			for(int i=0;i<tabs.length;i++){
				final String tab=(String) tabs[i];
				
				logDebug("Getting toutHelper for tab::"+tab);
				
				final IProdToutHelper prodHelper=(IProdToutHelper) this.getTabsToutHelperServiceMap().get(tab);
				if(prodHelper != null){
				
					logDebug("Getting products for tab::"+tab);
					
					List<ProductVO> productVOList;
					try {
						productVOList = prodHelper.getProducts(siteId, categoryId);
					
					final String paramName=tab+"ProductsList";
					
					logDebug("paramName ["+paramName+"] ");
					
					request.setParameter (paramName, productVOList);
					linkString = linkString + createLinkString(productVOList);
					request.setParameter(LINK_STRING, linkString);
					} catch (BBBSystemException e) {
						logError(LogMessageFormatter.formatMessage(request, "System Exception from service of ProdToutDroplet ",BBBCoreErrorConstants.BROWSE_ERROR_1030),e);
					} catch (BBBBusinessException e) {
						logError(LogMessageFormatter.formatMessage(request, "Business Exception from service of ProdToutDroplet ",BBBCoreErrorConstants.BROWSE_ERROR_1031),e);
					}
				}
			}
		}

		request.serviceParameter (OPARAM_OUTPUT, request, response);
		BBBPerformanceMonitor.end(methodName);
	}
	protected String extractCurrentSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}
	
	private String createLinkString(final List<ProductVO> productIDsList) {
		final StringBuilder finalString = new StringBuilder("");
		
		if(productIDsList != null){
			for (ProductVO productId : productIDsList) {
				finalString.append(productId.getProductId()).append(';');
			}
		}
		return finalString.toString();
	}
}
