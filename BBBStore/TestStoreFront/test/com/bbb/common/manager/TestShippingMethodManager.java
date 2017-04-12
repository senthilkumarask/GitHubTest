package com.bbb.common.manager;

 

import java.util.List;
import java.util.Map;

import com.bbb.common.vo.ShippingInfoKey;
import com.bbb.common.vo.StatesShippingMethodPriceVO;
import com.sapient.common.tests.BaseTestCase;

public class TestShippingMethodManager extends BaseTestCase {

  public void testShippingMethodsService() throws Exception {
    ShippingMethodManager manager = (ShippingMethodManager) getObject("manager");

   
    
    Map<String,List<String>> shippingDetail=null;
    if (manager != null) {
      shippingDetail = manager.getShippingMethodDetails("BuyBuyBaby");
    }
    
    addObjectToAssert("shippingMethodResults", shippingDetail.isEmpty());
    

  }
  
  public void testShippingPriceTableService() throws Exception {
	    ShippingMethodManager manager = (ShippingMethodManager) getObject("manager");

	   
	    String siteId = (String) getObject("siteId");
	   
	    Map<String, Map<ShippingInfoKey, StatesShippingMethodPriceVO>> shippingPriceTable = null;
	    if (manager != null) {
	    	shippingPriceTable = manager.getShippingPriceTableDetail(siteId);
	      }
	      
	    addObjectToAssert("shippingPriceTableNull", shippingPriceTable);
	    
	  }
}
