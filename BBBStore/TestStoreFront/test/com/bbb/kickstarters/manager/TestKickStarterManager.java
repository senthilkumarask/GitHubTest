package com.bbb.kickstarters.manager;

import com.bbb.kickstarters.KickStarterItemsVO;
import com.bbb.kickstarters.KickStarterVO;
import com.sapient.common.tests.BaseTestCase;

public class TestKickStarterManager extends BaseTestCase {

  public void testTopConsultantsService() throws Exception {
    KickStarterManager manager = (KickStarterManager) getObject("manager");

   
    
    KickStarterItemsVO kickStarterItemsVO = new KickStarterItemsVO();
    if (manager != null) {
    	kickStarterItemsVO= manager.getKickStartersByType("wedding", "BedBathUS", false, "Top Consultant");
    }
    addObjectToAssert("kickStarterItemsVO", kickStarterItemsVO.getKickStarterItems());
    
  	}
  
  public void testKickStarterDetailsService() throws Exception {
	    KickStarterManager manager = (KickStarterManager) getObject("manager");

	   
	    
	    KickStarterVO kickStarterVO = new KickStarterVO();
	    if (manager != null) {
	    	kickStarterVO= manager.getKickStarterDetails("BedBathUS", "DC280001", "wedding");
	    }
	    addObjectToAssert("kickStarterVO", kickStarterVO.toString());
	    
	  	}
}