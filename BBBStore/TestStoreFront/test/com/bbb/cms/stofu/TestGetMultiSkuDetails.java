package com.bbb.cms.stofu;

import java.util.Locale;
import java.util.Map;

import atg.servlet.RequestLocale;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.store.catalog.BBBGSCatalogToolsImpl;
import com.bbb.store.catalog.vo.MultiSkuDataVO;
import com.bbb.store.catalog.vo.ProductGSVO;
import com.sapient.common.tests.BaseTestCase;

public class TestGetMultiSkuDetails extends BaseTestCase{
	
	public void testGetMultiSkuDetailsSuccess() throws Exception
	{
		BBBGSCatalogToolsImpl bbbgsCatalogToolsImpl = (BBBGSCatalogToolsImpl) getObject("getMultiSkuDetails");
		String skiIdsList = (String) getObject("arg1");
		RequestLocale rl = new RequestLocale();
		rl.setLocale(new Locale("en_US"));
		getRequest().setRequestLocale(rl);
		Map<String,MultiSkuDataVO> multiSkuMap= bbbgsCatalogToolsImpl.getMultiSkuDetails(skiIdsList);
		assertNotNull(multiSkuMap);
		
	}
	
	public void testGetMultiSkuDetailsError() throws Exception
	{
		BBBGSCatalogToolsImpl bbbgsCatalogToolsImpl = (BBBGSCatalogToolsImpl) getObject("getMultiSkuDetails");
		String skiIdsList = (String) getObject("arg1");
		RequestLocale rl = new RequestLocale();
		rl.setLocale(new Locale("en_US"));
		getRequest().setRequestLocale(rl);
		
		try{
			bbbgsCatalogToolsImpl.getMultiSkuDetails(skiIdsList);
			}
		catch (BBBSystemException e) {
			assertNotNull(e);
		}
		catch (BBBBusinessException e) {
			if (e.getMessage().contains("9100")) {
				assertTrue(true);
			} else {
				assertFalse(true);
			}
		}
		
	}
}
  