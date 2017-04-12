package com.bbb.cms.stofu;

import java.util.Locale;

import atg.servlet.RequestLocale;

import com.bbb.commerce.giftregistry.manager.GSGiftRegistryManager;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.sapient.common.tests.BaseTestCase;

public class TestAddStoreItemsToRegistry extends BaseTestCase{
	
	/**
	 * Test for Add to registry for all valid conditions 
	 * @throws Exception
	 */
	public void testAddStoreItemsToRegistry() throws Exception
	{
		GSGiftRegistryManager GSGiftRegistryManager = (GSGiftRegistryManager) getObject("addRegistryRequest");
		String jsonCollectionObj = (String) getObject("jsonCollectionObj");
		RequestLocale rl = new RequestLocale();
		rl.setLocale(new Locale("en_US"));
		getRequest().setRequestLocale(rl);
		try {
			boolean addStoreItems= GSGiftRegistryManager.addStoreItemsToGiftRegistry(jsonCollectionObj);
			assertTrue(addStoreItems);
		} catch (BBBSystemException e) {
			assertNull(e);
		} catch (BBBBusinessException e) {
			assertNull(e);
		}
		
	}
	
	/**
	 * Test for Add to registry with invalid sku
	 * @throws Exception
	 */
	public void testAddStoreItemsToRegistrySkuError() throws Exception
	{
		GSGiftRegistryManager GSGiftRegistryManager = (GSGiftRegistryManager) getObject("addRegistryRequest");
		String jsonCollectionObj = (String) getObject("jsonCollectionObj");
		RequestLocale rl = new RequestLocale();
		rl.setLocale(new Locale("en_US"));
		getRequest().setRequestLocale(rl);
		
		try{
			GSGiftRegistryManager.addStoreItemsToGiftRegistry(jsonCollectionObj);
			
		}
		catch (BBBBusinessException e) {
			assertNotNull(e);
		}
		catch (Exception e){
			assertNull(e);
		}
		
	}
	
	/**
	 * Test for Add to registry with invalid registry id
	 * @throws Exception
	 */
	public void testAddStoreItemsToRegistryError() throws Exception
	{
		GSGiftRegistryManager GSGiftRegistryManager = (GSGiftRegistryManager) getObject("addRegistryRequest");
		String jsonCollectionObj = (String) getObject("jsonCollectionObj");
		RequestLocale rl = new RequestLocale();
		rl.setLocale(new Locale("en_US"));
		getRequest().setRequestLocale(rl);
		
		try{
			GSGiftRegistryManager.addStoreItemsToGiftRegistry(jsonCollectionObj);
			
		}
		catch (BBBBusinessException e) {
			assertNotNull(e);
		}
		catch (Exception e){
			assertNull(e);
		}
	}
	
	/**
	 * Test for Add to registry with invalid quantity
	 * @throws Exception
	 */
	public void testAddStoreItemsToRegistryQtyError() throws Exception
	{
		GSGiftRegistryManager GSGiftRegistryManager = (GSGiftRegistryManager) getObject("addRegistryRequest");
		String jsonCollectionObj = (String) getObject("jsonCollectionObj");
		RequestLocale rl = new RequestLocale();
		rl.setLocale(new Locale("en_US"));
		getRequest().setRequestLocale(rl);
		
		try{
			GSGiftRegistryManager.addStoreItemsToGiftRegistry(jsonCollectionObj);
		}
		catch (NumberFormatException e) {
			assertNotNull(e);
		}
		catch (BBBBusinessException e) {
			assertNotNull(e);
		}
		catch (Exception e){
			assertNull(e);
		}
	}
}
  