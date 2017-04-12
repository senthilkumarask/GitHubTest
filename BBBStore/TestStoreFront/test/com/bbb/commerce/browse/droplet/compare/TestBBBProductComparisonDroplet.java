package com.bbb.commerce.browse.droplet.compare;

import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.comparison.BBBCompareProductHandler;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;


/**
 * @author magga3
 *
 */
public class TestBBBProductComparisonDroplet extends BaseTestCase {

	private static final String SITE_ID = "siteId";
	private static final String SITE_CONTEXT_MANAGER = "siteContextManager";
	private static final String COMPARE_PRODUCTS_SUCCESS = "compareProductsSuccess";
	private static final String HANDLER = "bbbCompareProductHandler";
	private static final String PRODUCT_ID = "productID";
	private static final String DROPLET = "bbbProductComparisonDroplet";
	private static final String TRUE = "true";

	/**
	 * @throws Exception
	 */
	public void testProductComparisonDroplet() throws Exception
	{
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		ServletUtil.setCurrentRequest(pRequest);
		BBBCompareProductHandler compareProductHandler = (BBBCompareProductHandler) getObject(HANDLER);	
		String productId = (String) getObject(PRODUCT_ID);
		addProduct(productId, pRequest, pResponse, compareProductHandler);
		
		BBBProductComparisonDroplet productComparisonDroplet = (BBBProductComparisonDroplet) getObject(DROPLET);
		final String pSiteId = (String) this.getObject(SITE_ID);
       
        getRequest().setParameter(SITE_ID, pSiteId);
        SiteContextManager siteContextManager= (SiteContextManager) getObject(SITE_CONTEXT_MANAGER);
        try {
        	siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
        } catch (SiteContextException siteContextException) {
        	throw new BBBSystemException("Exception" + siteContextException);
        }
		productComparisonDroplet.service(pRequest, pResponse);
		assertEquals(TRUE, pRequest.getParameter(COMPARE_PRODUCTS_SUCCESS));
		compareProductHandler.handleClearList(pRequest, pResponse);
	}
	
	/**
	 * @param productId
	 * @param pRequest
	 * @param pResponse
	 * @param compareProductHandler
	 * @return
	 * @throws Exception
	 */
	private static boolean addProduct(String productId, DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, BBBCompareProductHandler compareProductHandler)
			throws Exception
	{
		compareProductHandler.setProductID(productId);		
		return compareProductHandler.handleAddProduct(pRequest, pResponse);
	}
	
	
}