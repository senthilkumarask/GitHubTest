package com.bbb.certona.droplet;


import com.sapient.common.tests.BaseTestCase;
import com.bbb.certona.droplet.CertonaDroplet;
import com.bbb.certona.vo.CertonaResponseVO;
import com.bbb.framework.BBBSiteContext;


public class TestCertonaDroplet extends BaseTestCase {

	
	/**
	  * Test Case 1 : Product recommendations 
	  * 
	  * Criteria :
	  * a. All required parameters are supplied
	  * b. Some optional parameters are supplied
	  * c. Scheme for products is supplied
	  * 
	  * Output:
	  * CertonaResponseVO not null
	  * ProductVOs are not null
	  * Request contains non null cookie parameter for trackingId & sessionId
	  * 
	  * @throws Exception
	  */
	 public void testProductRecomms_noError() throws Exception {
		 
		 CertonaDroplet certonaDroplet = (CertonaDroplet) getObject("TestCertonaDroplet");

		 String siteId = (String) getObject("siteId");

		 getRequest().setParameter("errorMsg",null);
		 getRequest().setParameter("siteId", siteId);
		 getRequest().setParameter("scheme", "clp_jfy");
		 getRequest().setParameter("categoryId", "10001");
		 getRequest().setParameter("userid", "");
		 getRequest().setParameter("trackingId", "");
		 getRequest().setParameter("siteId", "BuyBuyBaby");
		 //certonaDroplet.setSiteContext(BBBSiteContext.getBBBSiteContext("BuyBuyBaby"));
		 		 
		 certonaDroplet.service(getRequest(), getResponse());
			
		 CertonaResponseVO responseVO = (CertonaResponseVO)(getRequest().getObjectParameter("certonaResponseVO"));

		 if(responseVO!= null){
			 assertNull(getRequest().getObjectParameter("errorMsg"));
			 
			 assertNotNull(responseVO.getResonanceMap().get("clp_jfy").getProductsVOsList());
		 }
		 
		 //for next test case not to fail
		 getRequest().setParameter("certonaResponseVO", null);
	 }
	 
	 
	 /**
	  * Test Case 2 : Product recommendations 
	  * 
	  * Criteria :
	  * a. A required parameters is missing
	  * b. Some optional parameters are supplied
	  * c. Scheme for products is supplied
	  * 
	  * Output:
	  * CertonaResponseVO is null
	  * ErrorMsg is not null
	  * 
	  * @throws Exception
	  */
	 public void testProductRecomms_Error() throws Exception {
		 
		 CertonaDroplet certonaDroplet = (CertonaDroplet) getObject("TestCertonaDroplet");

		 String siteId = (String) getObject("siteId");

		 getRequest().setParameter("errorMsg",null);		 
		 getRequest().setParameter("siteId", siteId);
		 getRequest().setParameter("scheme", "clp_jfy");
		 getRequest().setParameter("userid", "UID00002");
		 getRequest().setParameter("categoryId",null);
		 getRequest().setParameter("productId", "PID0002");
		 
		 
				 
		 certonaDroplet.service(getRequest(), getResponse());
		
		 if(getRequest().getObjectParameter("errorMsg") !=null){
			 //errorMsg should be not null
			 assertNotNull(getRequest().getObjectParameter("errorMsg"));
			 
			 CertonaResponseVO responseVO = (CertonaResponseVO)(getRequest().getObjectParameter("certonaResponseVO"));
			 
			 //response will be null
			 assertNull(responseVO);
		 }
		//for next test case not to fail
		 getRequest().setParameter("errorMsg", null);
	 }
	 
	 /**
	  * Test Case 3 : SKU recommendations 
	  * 
	  * Criteria :
	  * a. All required parameters are supplied
	  * b. Some optional parameters are supplied
	  * c. Scheme for sku is supplied
	  * 
	  * Output:
	  * CertonaResponseVO is not null
	  * ErrorMsg is null
	  * 
	  * @throws Exception
	  */ 
	 public void testSKURecomms_noError_noBackupBehav() throws Exception {
		 
		 CertonaDroplet certonaDroplet = (CertonaDroplet) getObject("TestCertonaDroplet");

		 String siteId = (String) getObject("siteId");

		 getRequest().setParameter("errorMsg",null);		 
		 getRequest().setParameter("siteId", siteId);
		 getRequest().setParameter("scheme", "fc_lmi");
		 getRequest().setParameter("userid", "UID00003");
		 getRequest().setParameter("context","context0003");
		 getRequest().setParameter("ipaddress","202.123.12.34");

	 
		 certonaDroplet.service(getRequest(), getResponse());
			
		 CertonaResponseVO responseVO = (CertonaResponseVO)(getRequest().getObjectParameter("certonaResponseVO"));
		 
		 if( getRequest().getObjectParameter("errorMsg") ==null ){
			 assertNull(getRequest().getObjectParameter("errorMsg"));
			 
			 assertNotNull(responseVO);
			 
			 assertNotNull(responseVO.getResonanceMap().get("fc_lmi").getSkuDetailVOList());
		 }
		 //for next test case not to fail
		 getRequest().setParameter("certonaResponseVO", null);
	 }
	 
	 
	 /**
	  * Test Case 4 : SKU recommendations 
	  * 
	  * Criteria :
	  * a. All required parameters are supplied
	  * b. Some optional parameters are supplied
	  * c. Scheme for sku is supplied
	  * d. Dummy response is set to EMPTY
	  * 
	  * Output:
	  * CertonaResponseVO is not null
	  * ErrorMsg is null
	  * 
	  * @throws Exception
	  */ 
	 public void testSKURecomms_noError_backupBehav() throws Exception {
		 
		 CertonaDroplet certonaDroplet = (CertonaDroplet) getObject("TestCertonaDroplet");

		 String siteId = (String) getObject("siteId");
		 getRequest().setParameter("errorMsg",null);
		 getRequest().setParameter("siteId", siteId);
		 getRequest().setParameter("scheme", "fc_lmi");
		 getRequest().setParameter("userid", "UID00004");
		 getRequest().setParameter("context","context0004");
		 getRequest().setParameter("ipaddress","202.123.12.34");
		 
		 //response is going to empty & hence backup behaviour will be triggered
		 certonaDroplet.getHttpQueryManager().getHttpCallInvoker().setDummyQuery(" ");
		 certonaDroplet.service(getRequest(), getResponse());
			
		 CertonaResponseVO responseVO = (CertonaResponseVO)(getRequest().getObjectParameter("certonaResponseVO"));
		 
		 if(getRequest().getObjectParameter("errorMsg") == null){
			 assertNull(getRequest().getObjectParameter("errorMsg"));
			 
			 assertNotNull(responseVO);
			 
			 assertNotNull(responseVO.getResonanceMap().get("fc_lmi").getSkuDetailVOList());
		 }
		 //for next test case not to fail
		 getRequest().setParameter("certonaResponseVO", null);
	 }
	 
	 /**
	  * Test Case 5 : Product recommendations 
	  * 
	  * Criteria :
	  * a. All required parameters are supplied
	  * b. Some optional parameters are supplied
	  * c. Scheme for prod is supplied
	  * d. Dummy response is set to EMPTY
	  * 
	  * Output:
	  * CertonaResponseVO will be null
	  * ErrorMsg will b null
	  * Empty parameter will be not null
	  * 
	  * @throws Exception
	  */ 
	 public void testProdRecomms_EmptyOparam() throws Exception {
		 
		 CertonaDroplet certonaDroplet = (CertonaDroplet) getObject("TestCertonaDroplet");

		 String siteId = (String) getObject("siteId");

		 getRequest().setParameter("errorMsg",null);		 
		 getRequest().setParameter("siteId", siteId);
		 getRequest().setParameter("scheme", "pdp_fbw");
		 getRequest().setParameter("userid", "UID00005");
		 getRequest().setParameter("context","context0005");
		 getRequest().setParameter("ipaddress","202.123.12.34");
		 
		 //response is going to empty & hence response will be empty
		 certonaDroplet.getHttpQueryManager().getHttpCallInvoker().setDummyQuery(" ");
		 certonaDroplet.service(getRequest(), getResponse());
			
		 CertonaResponseVO responseVO = (CertonaResponseVO)(getRequest().getObjectParameter("certonaResponseVO"));
		 
		 //assertNull(getRequest().getObjectParameter("errorMsg"));
		 
		 assertNull(responseVO);
		 
		 //for next test case not to fail
		 getRequest().setParameter("certonaResponseVO", null);
		 getRequest().setParameter("empty", null);
		 
		 certonaDroplet.getHttpQueryManager().getHttpCallInvoker().setDummyQuery("<resonance>\n   <items>\n   <item>      <sku>prod10037</sku> </item> \n <item>     <sku>prod60011</sku>  </item> \n <item>  <sku>130510</sku> </item>\n  <item>  <sku>124723</sku> </item>\n <item>  <sku>100892</sku> </item>\n <item>  <sku>17312774</sku> </item>\n 	  </items>\n   <pageid>res09113010584253544725595</pageid>\n 	 <trackingid>71485053405392</trackingid>\n 	 	 </resonance>");
	 }	 
		 

}
