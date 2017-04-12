package com.bbb.commerce.catalog.comparison;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.sapient.common.tests.BaseTestCase;


public class TestBBBCompareProductHandler extends BaseTestCase {

	private static final String HANDLER = "bbbCompareProductHandler";
	private static final String PRODUCT_ID = "productID";

	/**
	 * @throws Exception
	 */
	public void testCompareProductHandlerAddProduct() throws Exception
	{
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		
		BBBCompareProductHandler compareProductHandler = (BBBCompareProductHandler) getObject(HANDLER);	
		String productId = (String) getObject(PRODUCT_ID);
		addProduct(productId, pRequest, pResponse, compareProductHandler);
		int listSize = compareProductHandler.getProductComparisonList().getItems().size();
		assertTrue(listSize == 1);
		//Call to Clear all products from Comparison List
		compareProductHandler.handleClearList(pRequest, pResponse);
	}
	
	/**
	 * @throws Exception
	 */
	public void testCompareProductHandlerRemoveProduct() throws Exception
	{
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		
		BBBCompareProductHandler compareProductHandler = (BBBCompareProductHandler) getObject(HANDLER);	
		//Add Products to Comparison List
		String productId = (String) getObject("productIDAdd1");
		addProduct(productId,pRequest, pResponse, compareProductHandler);
		
		productId = (String) getObject("productIDAdd2");
		addProduct(productId,pRequest, pResponse, compareProductHandler);
		
		productId = (String) getObject("productIDAdd3");
		addProduct(productId,pRequest, pResponse, compareProductHandler);
		
		//Calculate size of Comparison List before Removing an item
		int beforeListSize = compareProductHandler.getProductComparisonList().getItems().size();
		
		productId = (String) getObject("productIDRemove");
		compareProductHandler.setProductID(productId);		
		//Call to Remove specific product from comparison List
		compareProductHandler.handleRemoveProduct(pRequest, pResponse);
		//Calculate size of Comparison List after Removing an item
		int afterListSize = compareProductHandler.getProductComparisonList().getItems().size();
		assertTrue(afterListSize == beforeListSize - 1); 
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