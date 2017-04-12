package com.bbb.commerce.browse.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;

public class ParentCategoryForProductDroplet extends BBBDynamoServlet {

	private static final String OPARAM_OUTPUT = "output";

	private static final String OPARAM_CATEGORY_ID = "categoryId";

	private static final String PARAM_SITE_ID = "siteId";

	private static final String PARAM_PRODUCT_ID = "productId";

	private BBBCatalogTools bbbCatalogTools;

	public BBBCatalogTools getBbbCatalogTools() {
		return bbbCatalogTools;
	}

	public void setBbbCatalogTools(BBBCatalogTools bbbCatalogTools) {
		this.bbbCatalogTools = bbbCatalogTools;
	}

	public void service(final DynamoHttpServletRequest request,
			final DynamoHttpServletResponse response) throws ServletException,
			IOException {
		BBBPerformanceMonitor.start("ParentCategoryForProductDroplet",
				"service");
		String productId = request.getParameter(PARAM_PRODUCT_ID);
		String siteId = request.getParameter(PARAM_SITE_ID);

		try {
			String categoryId = this.getBbbCatalogTools()
					.getParentCategoryIdForProduct(productId, siteId);
			request.setParameter(OPARAM_CATEGORY_ID, categoryId);
			request.serviceParameter(OPARAM_OUTPUT, request, response);
		} catch (BBBBusinessException e) {
			logError(
					"Business Exception occured while fetching parent category Id from service of ParentCategoryForProductDroplet for product ID : "
							+ productId, e);
		} catch (BBBSystemException e) {
			logError(
					"System Exception occured while fetching parent category Id from service of ParentCategoryForProductDroplet for product ID : "
							+ productId, e);
		}
		BBBPerformanceMonitor.end("ParentCategoryForProductDroplet", "service");
	}

}
