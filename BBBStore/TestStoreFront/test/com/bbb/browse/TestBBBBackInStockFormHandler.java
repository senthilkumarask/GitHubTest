package com.bbb.browse;

import java.util.Locale;


import atg.servlet.RequestLocale;

import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestBBBBackInStockFormHandler extends BaseTestCase{
	
	public void testHandleSendOOSEmail() throws Exception
	{
		try{
		BBBBackInStockFormHandler backInStockFormHandler = (BBBBackInStockFormHandler) getObject("bbbBackInStockFormHandler");
		RequestLocale rl = new RequestLocale();
		rl.setLocale(new Locale("en_US"));
		getRequest().setRequestLocale(rl);
		backInStockFormHandler.setCatalogRefId("sku1234");
		backInStockFormHandler.setEmailAddress("abc@abc.com");
		backInStockFormHandler.setCustomerName("Sandeep");
		backInStockFormHandler.setProductId("prod1234");
		backInStockFormHandler.setProductName("productName");
//		backInStockFormHandler.setName("Name");
		backInStockFormHandler.setConfirmEmailAddress("abc@abc.com");
		backInStockFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BuyBuyBaby"));

		backInStockFormHandler.getProfileRepository();
		backInStockFormHandler.getLocaleService();
		backInStockFormHandler.setSuccessURL("");
		backInStockFormHandler.setErrorURL("");
		backInStockFormHandler.setNoJavascriptSuccessURL("");
		backInStockFormHandler.setNoJavascriptErrorURL("");
		
		backInStockFormHandler.getSuccessURL();
		backInStockFormHandler.getErrorURL();
		backInStockFormHandler.getNoJavascriptSuccessURL();
		backInStockFormHandler.getNoJavascriptErrorURL();

		backInStockFormHandler.handleSendOOSEmail(getRequest(), getResponse());
		boolean result = backInStockFormHandler.getFormError();
		assertFalse(result); // result is true if there is form error
		}
		catch(Exception e){
		}
	}
	
	public void testHandleUnSubscribeEmail() throws Exception
	{
		try{
		BBBBackInStockFormHandler backInStockFormHandler = (BBBBackInStockFormHandler) getObject("bbbBackInStockFormHandler");
		RequestLocale rl = new RequestLocale();
		rl.setLocale(new Locale("en_US"));
		getRequest().setRequestLocale(rl);
		backInStockFormHandler.setCatalogRefId("sku1234");
		backInStockFormHandler.setEmailAddress("abc@abc.com");
		backInStockFormHandler.setCustomerName("Sandeep");
		backInStockFormHandler.setProductId("prod1234");
		backInStockFormHandler.setProductName("productName");
//		backInStockFormHandler.setName("Name");
		backInStockFormHandler.setConfirmEmailAddress("abc@abc.com");
		backInStockFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BuyBuyBaby"));
		backInStockFormHandler.getProfileRepository();

		backInStockFormHandler.getProfileRepository();
		backInStockFormHandler.setSuccessURL("");
		backInStockFormHandler.setErrorURL("");
		backInStockFormHandler.setNoJavascriptSuccessURL("");
		backInStockFormHandler.setNoJavascriptErrorURL("");
		
		
		backInStockFormHandler.preUnSubscribeOOSEmail(getRequest(), getResponse());
		backInStockFormHandler.handleUnSubscribeOOSEmail(getRequest(), getResponse());
		boolean result = backInStockFormHandler.getFormError();
		assertFalse(result); // result is true if there is form error
		}catch(Exception e){
		}
	}
	
}
