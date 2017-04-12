package com.bbb.cms.stofu;

import java.util.Locale;

import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.servlet.RequestLocale;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.bbb.store.catalog.BBBGSCatalogToolsImpl;
import com.bbb.store.catalog.vo.FilteredProductDetailVO;
import com.bbb.store.catalog.vo.ProductGSVO;
import com.sapient.common.tests.BaseTestCase;

public class TestGSProductDetails extends BaseTestCase{
	
	private static final String SITE_ID = "siteId";
	private static final String SITE_CONTEXT_MANAGER = "siteContextManager";

	public void testProductDetailsSuccess() throws Exception
	{
		BBBGSCatalogToolsImpl bbbgsCatalogToolsImpl = (BBBGSCatalogToolsImpl) getObject("productDetails");
		String productId = (String) getObject("productId");
		String categoryId = (String) getObject("categoryId");
		RequestLocale rl = new RequestLocale();
		rl.setLocale(new Locale("en_US"));
		getRequest().setRequestLocale(rl);
		final String pSiteId = (String) this.getObject(SITE_ID);
	       
        getRequest().setParameter(SITE_ID, pSiteId);
        SiteContextManager siteContextManager= (SiteContextManager) getObject(SITE_CONTEXT_MANAGER);
        try {
        	siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
        } catch (SiteContextException siteContextException) {
        	throw new BBBSystemException("Exception" + siteContextException);
        }
		FilteredProductDetailVO productGsVO= bbbgsCatalogToolsImpl.getProductDetails(productId,categoryId);
		assertNotNull(productGsVO.getProductId());
		assertNotNull("The list of good to knows for this product is empty or null ", productGsVO.getGoodToKnow());
	}
	
	public void testProductDetailsError() throws Exception
	{
		BBBGSCatalogToolsImpl bbbgsCatalogToolsImpl = (BBBGSCatalogToolsImpl) getObject("productDetails");
		String productId = (String) getObject("productId");
		String categoryId = (String) getObject("categoryId");
		RequestLocale rl = new RequestLocale();
		rl.setLocale(new Locale("en_US"));
		getRequest().setRequestLocale(rl);
		try{
			bbbgsCatalogToolsImpl.getProductDetails(productId,categoryId);
			
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
}
  