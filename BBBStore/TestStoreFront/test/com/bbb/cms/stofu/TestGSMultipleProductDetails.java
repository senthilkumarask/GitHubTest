package com.bbb.cms.stofu;

import java.util.Locale;
import java.util.Map;

import atg.servlet.RequestLocale;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.store.catalog.BBBGSCatalogToolsImpl;
import com.bbb.store.catalog.vo.CompareProductVO;
import com.sapient.common.tests.BaseTestCase;

public class TestGSMultipleProductDetails extends BaseTestCase{
	
	public void testProductDetailsSuccess() throws Exception
	{
		BBBGSCatalogToolsImpl bbbgsCatalogToolsImpl = (BBBGSCatalogToolsImpl) getObject("multipleProductDetails");
		String jsonCompareListObj = (String) getObject("jsonCompareListObj");
		RequestLocale rl = new RequestLocale();
		rl.setLocale(new Locale("en_US"));
		getRequest().setRequestLocale(rl);
		Map<String,CompareProductVO> compareProductVO= bbbgsCatalogToolsImpl.getMultipleProductDetails(jsonCompareListObj);
		assertNotNull(compareProductVO);
		
	}
	
	public void testProductDetailsProductError() throws Exception
	{
		BBBGSCatalogToolsImpl bbbgsCatalogToolsImpl = (BBBGSCatalogToolsImpl) getObject("multipleProductDetails");
		String jsonCompareListObj = (String) getObject("jsonCompareListObj");
		RequestLocale rl = new RequestLocale();
		rl.setLocale(new Locale("en_US"));
		getRequest().setRequestLocale(rl);
		try{
			bbbgsCatalogToolsImpl.getMultipleProductDetails(jsonCompareListObj);
			
		}
		catch (BBBSystemException e) {
			assertNotNull(e);
		}
		catch (BBBBusinessException e) {
			if (e.getErrorCode().contains("1004")) {
				assertTrue(true);
			} else {
				assertFalse(true);
			}
		}
		
	}
	
	public void testProductDetailsSkuError() throws Exception
	{
		BBBGSCatalogToolsImpl bbbgsCatalogToolsImpl = (BBBGSCatalogToolsImpl) getObject("multipleProductDetails");
		String jsonCompareListObj = (String) getObject("jsonCompareListObj");
		RequestLocale rl = new RequestLocale();
		rl.setLocale(new Locale("en_US"));
		getRequest().setRequestLocale(rl);
		try{
			bbbgsCatalogToolsImpl.getMultipleProductDetails(jsonCompareListObj);
			
		}
		catch (BBBSystemException e) {
			if (e.getErrorCode().contains("9100")) {
				assertTrue(true);
			} else {
				assertFalse(true);
			}
		}
		catch (BBBBusinessException e) {
			if (e.getErrorCode().contains("9100")) {
				assertTrue(true);
			} else {
				assertFalse(true);
			}
		}
		
	}
}
  