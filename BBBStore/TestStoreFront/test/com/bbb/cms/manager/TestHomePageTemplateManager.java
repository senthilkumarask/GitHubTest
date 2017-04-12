package com.bbb.cms.manager;

 

import com.bbb.cms.HomePageTemplateVO;
import com.sapient.common.tests.BaseTestCase;

public class TestHomePageTemplateManager extends BaseTestCase {

  public void testHomePageTemplateManager() throws Exception {
    HomePageTemplateManager manager = (HomePageTemplateManager) getObject("manager");
    manager.setLoggingDebug(true);
    String siteId1 = (String) getObject("siteId1");
    String siteId2 = (String) getObject("siteId2");
    
    
    HomePageTemplateVO mHomePageTemplateVO1=null;
    HomePageTemplateVO mHomePageTemplateVO2=null;
    
    //DynamoHttpServletRequest request = getRequest();
    if (manager != null) {
    	mHomePageTemplateVO1 = manager.getHomePageData(siteId1);
    }
    mHomePageTemplateVO1.getHeroImages().size();
   
    assertEquals("SiteId BuyBuyBaby was expected ","BuyBuyBaby", mHomePageTemplateVO1.getSiteId());
    
    assertNotNull("First PromoBox for BuyBuyBaby was expected ", mHomePageTemplateVO1.getPromoBoxFirst());
    
   /* try {
		if (manager != null) {
			mHomePageTemplateVO2 = manager.getHomePageData(siteId2);
		}
	} catch (BBBBusinessException e) {
		
		assertEquals(HomePageTemplateManager.RETRIEVED_MORE_DATA_FROM_REPOSITORY,  e.getMessage());
		
		
	}*/
     
  }
}
