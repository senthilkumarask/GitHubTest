package com.bbb.personalstore.manager;

import javax.servlet.http.Cookie;

import atg.commerce.order.purchase.AddCommerceItemInfo;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.commerce.order.purchase.BBBCartFormhandler;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

/**
 * This is a Sapunit to test personal Store manager, which contains methods to
 * test the Last Bought Cookie.
 * 
 * @author rjain40
 * 
 */
public class TestPersonalStoreManager extends BaseTestCase {
	
	/**
	 * This method tests the Last Bought Cookie
	 * 
	 * @throws Exception
	 */
	public void testLastBoughtCookie() throws Exception {
		
		PersonalStoreManager personalStoreManager = (PersonalStoreManager) getObject("TestPersonalStoreManager");
		  final BBBCartFormhandler formHandler = (BBBCartFormhandler)this.getObject("bbbCartFormHandler");
	        final DynamoHttpServletRequest pRequest =  this.getRequest();
	        final DynamoHttpServletResponse pResponse = this.getResponse();
	        ServletUtil.setCurrentRequest(pRequest);
			final String siteId = "BedBathUS";
			SiteContextManager siteContextManager = (SiteContextManager) getObject("siteContextManager");
			try {
				siteContextManager.pushSiteContext(BBBSiteContext
						.getBBBSiteContext(siteId));
			} catch (SiteContextException siteContextException) {
				throw new BBBSystemException("Exception" + siteContextException);
			}
	        formHandler.resetFormExceptions();
	        
	        final MutableRepositoryItem profileItem = (MutableRepositoryItem)formHandler.getProfile();
	        
	        formHandler.getShoppingCart().getOrderManager().removeOrder(formHandler.getOrder().getId());
	        formHandler.setOrder(formHandler.getShoppingCart().getOrderManager().createOrder(profileItem.getRepositoryId()));
	        
	        formHandler.getOrder().removeAllCommerceItems();
	        //Add all parametrs in profile level
	        this.getRequest().resolveName("/atg/userprofiling/ProfileTools");

	        //profileItem = (MutableRepositoryItem) profileTool.getItemFromEmail("raj@example.com");

	        final PriceListManager priceListManager = (PriceListManager)this.getObject("bbbPriceListManager");
	        final String listPriceId = (String)this.getObject("listPriceId");
	        final String salePriceId = (String)this.getObject("salePriceId");
	        final RepositoryItem listPriceListItem = priceListManager.getPriceList(listPriceId);
	        final RepositoryItem salePriceListItem = priceListManager.getPriceList(salePriceId);
	        profileItem.setPropertyValue("priceList", listPriceListItem);
	        profileItem.setPropertyValue("salePriceList", salePriceListItem);

	        formHandler.setProfile(profileItem);
	        formHandler.setSiteId("BedBathUS");
	        //Test logic
	        final AddCommerceItemInfo[] itemInfoArray = new AddCommerceItemInfo[3];

	        // Test Scenario
	        // Add 3 items out of which one skuID is invalid and 1 storeId is invalid
	        final String pProductId = (String)this.getObject("productId");

	        final String invalidSkuId = (String)this.getObject("invalidSkuId");
	        itemInfoArray[0] = new AddCommerceItemInfo();
	        itemInfoArray[0].setCatalogRefId(invalidSkuId);
	        itemInfoArray[0].setProductId(pProductId);
	        itemInfoArray[0].setQuantity(1);

	        final String skuId = (String)this.getObject("skuId");
	        final String invalidStoreId = (String)this.getObject("invalidStoreId");

	        itemInfoArray[1] = new AddCommerceItemInfo();
	        itemInfoArray[1].setCatalogRefId(skuId);
	        itemInfoArray[1].setProductId(pProductId);
	        itemInfoArray[1].getValue().put("storeId", invalidStoreId);
	        itemInfoArray[1].setQuantity(1);

	        itemInfoArray[2] = new AddCommerceItemInfo();
	        itemInfoArray[2].setCatalogRefId(skuId);
	        itemInfoArray[2].setProductId(pProductId);
	        itemInfoArray[2].setQuantity(1);


	        formHandler.addItemsFromCookie(itemInfoArray,
	                pRequest, pResponse);
	     // Get the last bought Cookie
	     			Cookie lBCookie = personalStoreManager.getLastBoughtCookie(formHandler.getOrder(), null, null, pRequest, pResponse);
	     			assertNotNull(lBCookie);
	        
	}
	
}
