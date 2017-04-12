package com.bbb.cms.stofu;

import java.util.Locale;

import atg.servlet.RequestLocale;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.store.catalog.BBBGSBazaarVoiceManager;
import com.bbb.store.catalog.bvreviews.Reviews;
import com.sapient.common.tests.BaseTestCase;

public class TestBBBGSProductReviews extends BaseTestCase{
	
	/**
	 * Test for getting the bazaar voice details for the product 
	 * @throws Exception
	 */
	public void testGetProductReviews() throws Exception
	{
		BBBGSBazaarVoiceManager bazaarVoiceManager = (BBBGSBazaarVoiceManager) getObject("productReviewRequest");
		Reviews reviews;
		String productId = (String) getObject("productId");
		String sortingOrder = (String) getObject("sortingOrder");
		RequestLocale rl = new RequestLocale();
		rl.setLocale(new Locale("en_US"));
		getRequest().setRequestLocale(rl);
		try {
			reviews = bazaarVoiceManager.getProductReviews(productId, sortingOrder);
			assertFalse(reviews.getHasErrors());
		} catch (BBBSystemException e) {
			assertNull(e);
		} catch (BBBBusinessException e) {
			assertNull(e);
		}
		
	}
	
	/**
	 * Test for getting the bazaar voice details for the product 
	 * Error scenario : Incorrect sorting order
	 * Bazaar Voice details are wrong
	 * @throws Exception
	 */
	
	public void testGetProductReviewsError() throws Exception
	{
		BBBGSBazaarVoiceManager bazaarVoiceManager = (BBBGSBazaarVoiceManager) getObject("productReviewRequest");
		Reviews reviews;
		String productId = (String) getObject("productId");
		String sortingOrder = (String) getObject("sortingOrder");
		RequestLocale rl = new RequestLocale();
		rl.setLocale(new Locale("en_US"));
		getRequest().setRequestLocale(rl);
		try {
			reviews = bazaarVoiceManager.getProductReviews(productId, sortingOrder);
			assertTrue(reviews.getHasErrors());
		} catch (BBBSystemException e) {
			assertNotNull(e);
		} catch (BBBBusinessException e) {
			assertNotNull(e);
		}
		
	}
}
  