package com.bbb.commerce.browse.droplet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.browse.manager.ProductManager;
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;

/** This droplet is used to fetch the order of categories i.e.
 * their hierarchies for the Mobile Deep Linking meta tag content.
 * It will return a map of categories order.
 * Created by: magga3
 * Created on: September-2013
 * 
 * @author magga3
 */
public class MobileCategoryOrderDroplet extends BBBDynamoServlet{

	private static final String PARAM_CATAGORY_ID = "categoryId";
	private static final String PARAM_SITE_ID = "siteId";
	private static final String OPARAM_CAT_ORDER = "categoryOrder";
	private static final String OPARAM_OUTPUT = "output";

	private ProductManager productManager;

	/**
	 * This method get the site id and Category id from the jsp and pass these
	 * value to manager class and get the Map<String, CategoryVO> from manager class
	 * 
	 * @param DynamoHttpServletRequest
	 * @param DynamoHttpServletResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	@Override
	public void service(final DynamoHttpServletRequest request,
			final DynamoHttpServletResponse response) throws ServletException, IOException {

		BBBPerformanceMonitor.start("MobileCategoryOrderDroplet", "service");

		final String categoryId = request.getParameter(PARAM_CATAGORY_ID);
		final String siteId = request.getParameter(PARAM_SITE_ID);
		Map<String, CategoryVO> categoryHierarchy = null;

		logDebug("Category Id = " + categoryId);
		logDebug("Site Id = " + siteId);

		if (StringUtils.isEmpty(categoryId)) {
			logDebug("Empty Category id for Mobile meta tag content");
		} else {
			try {
				categoryHierarchy = this.productManager.getParentCategory(categoryId, siteId);
			} catch (BBBSystemException e) {
				logError("BBBSystem Exception in finding category order for category Id" + categoryId, e);
			} catch (BBBBusinessException e) {
				logError("BBBBusiness Exception in finding category order for category Id" + categoryId, e);
			}
		}

		logDebug("Category Order = " + categoryHierarchy);
		request.setParameter(OPARAM_CAT_ORDER, categoryHierarchy);
		request.serviceParameter(OPARAM_OUTPUT, request,
				response);
		BBBPerformanceMonitor.end("MobileCategoryOrderDroplet", "service");
	}
	
	/**
	 * @return the productManager
	 */
	public ProductManager getProductManager() {
		return this.productManager;
	}

	/**
	 * @param productManager the productManager to set
	 */
	public void setProductManager(final ProductManager productManager) {
		this.productManager = productManager;
	}

}
