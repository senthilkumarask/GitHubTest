package com.bbb.commerce.giftregistry.formhandler;

import java.util.List;

import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;

import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.giftregistry.vo.RegistrySearchVO;
import com.bbb.commerce.giftregistry.vo.RegistrySkinnyVO;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.email.EmailHolder;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

/**
 * This class provides test case for handler methods for
 * UC_Manage_Registry_Items
 * 
 * This class has handles following test cases 
 *  <ul>
 *  	<li>UpdateRegItem - for transient user</li>
 *  	<li>UpdateRegItem - for logged in user</li>
 *  	<li>RemoveRegItem - for transient user</li>
 *   	<li>RemoveRegItem - for logged in user</li>
 *   	<li>EmailRegistry</li>    
 *  </ul>
 * This functionality is invoked via the update
 * methods of the form handler.
 * 
 * @author ikhan2
 * 
 */
public class TestGiftRegistryManageItems extends BaseTestCase {

	/**
	  * Test Case 1 : Transient User invoking Update Registry Item
	  * testing handleCreateRegistry method
	  * @throws Exception
	  */
	 public void testHandleUpdateRegItem1() throws Exception {
		 
		 GiftRegistryFormHandler giftRegistryFormHandler = (GiftRegistryFormHandler) getObject("giftRegistryFormHandler");

		 /**
		  *  Case 1 : Profile is transient
		  */
		 Profile profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
		 profile.setDataSource(null);
		 //ensure profile is transient
		 assertTrue(profile.isTransient());
		 giftRegistryFormHandler.setUpdateRegItemsServiceName("updateRegistryItems");
		 giftRegistryFormHandler.handleUpdateItemToGiftRegistry(getRequest(), getResponse());
		 boolean formError1 = giftRegistryFormHandler.getFormError();
		 assertTrue(formError1);
		 
	 }
	 
	/**
	  * Test Case 2 : Logged in user invoking Update Registry Item
	  * testing handleCreateRegistry method
	  * @throws Exception
	  */	 
	 public void testHandleUpdateRegItem2() throws Exception {
		 
		 /**
		  *  Case 2 : Profile is not transient
		  */
		 Profile profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
		 ProfileTools profileTool = (ProfileTools) getRequest().resolveName("/atg/userprofiling/ProfileTools");
		 final BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) this.getObject("catalogTools");
		 String userToken = catalogTools.getAllValuesForKey(
					BBBWebServiceConstants.TXT_WSDLKEY,
					BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(
					BBBCoreConstants.ZERO);
		 
		 String username0 = (String) getObject("username0");
		 String siteId = (String) getObject("siteId");
		 //getRequest().setParameter("siteId", siteId);
		 profile.setDataSource(profileTool.getItemFromEmail(username0));
		 //ensure profile is not transient			
		 assertFalse(profile.isTransient());
		 
		 GiftRegistryFormHandler giftRegistryFormHandler = (GiftRegistryFormHandler) getObject("giftRegistryFormHandler");
		 giftRegistryFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(((String)getObject("siteId"))));
		 giftRegistryFormHandler.resetFormExceptions();
		 giftRegistryFormHandler.getValue().put("purchasedQuantity", "4");
		 giftRegistryFormHandler.getValue().put("quantity", "10");
		 giftRegistryFormHandler.getValue().put("regItemOldQty","5");
		 giftRegistryFormHandler.getValue().put("SKU", "10670381");
		 giftRegistryFormHandler.getValue().put("registryId", "153588274");
		 giftRegistryFormHandler.getValue().put("userToken", userToken);
		 giftRegistryFormHandler.getValue().put("rowId", "AAAJn4AACAAA3MrAA0");
		 giftRegistryFormHandler.setUpdateRegItemsServiceName("updateRegistryItems");
		 giftRegistryFormHandler.handleUpdateItemToGiftRegistry(getRequest(), getResponse());
		 
		 boolean formError = giftRegistryFormHandler.getFormError();
		 assertFalse(formError);
		 
		 
		 
		 
		 //make user transient
		 profile.setDataSource(null);
	 }
	 
	/**
	  * Test Case 3 : Transient User invoking Remove Registry Item
	  * testing handleCreateRegistry method
	  * @throws Exception
	  */
	 public void testHandleRemoveRegItem1() throws Exception {
		 
		 GiftRegistryFormHandler giftRegistryFormHandler = (GiftRegistryFormHandler) getObject("giftRegistryFormHandler");
		 giftRegistryFormHandler.resetFormExceptions();

		 Profile profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
		 profile.setDataSource(null);
		 //ensure profile is transient
		 assertTrue(profile.isTransient());
		 giftRegistryFormHandler.setUpdateRegItemsServiceName("updateRegistryItems");
		 giftRegistryFormHandler.handleRemoveItemFromGiftRegistry(getRequest(), getResponse());
		 boolean formError = giftRegistryFormHandler.getFormError();
		 assertTrue(formError);

		 if(formError && giftRegistryFormHandler.getRegistryItemOperation()==null){
			giftRegistryFormHandler.setRegistryItemOperation(BBBGiftRegistryConstants.GR_ITEM_REMOVE);
		 }
		 
	 }
	 
	/**
	  * Test Case 4 : Logged in User invoking Remove Registry Item
	  * testing handleCreateRegistry method
	  * 
	  * But regisrty does not exist
	  * @throws Exception
	  */	 
	 public void testHandleRemoveRegItem2() throws Exception {
		 

		 Profile profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
		 ProfileTools profileTool = (ProfileTools) getRequest().resolveName("/atg/userprofiling/ProfileTools");
		 final BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) this.getObject("catalogTools");
		 String userToken = catalogTools.getAllValuesForKey(
					BBBWebServiceConstants.TXT_WSDLKEY,
					BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(
					BBBCoreConstants.ZERO);
		 
		 String username0 = (String) getObject("username0");
		 String siteId = (String) getObject("siteId");
		 profile.setDataSource(profileTool.getItemFromEmail(username0));
		 //ensure profile is not transient			
		 assertFalse(profile.isTransient());
		 
		 GiftRegistryFormHandler giftRegistryFormHandler = (GiftRegistryFormHandler) getObject("giftRegistryFormHandler");
		 
		 giftRegistryFormHandler.resetFormExceptions();
		 
		 
		 giftRegistryFormHandler.getValue().put("quantity", "5");
		 giftRegistryFormHandler.getValue().put("SKU", "10670381");
		 giftRegistryFormHandler.getValue().put("registryId", "15350110");
		 giftRegistryFormHandler.getValue().put("userToken", userToken);
		 giftRegistryFormHandler.getValue().put("rowId", "AAAJn4AACAAA3MrAA0");
		 
		 //getRequest().setParameter("siteId", siteId);
		 giftRegistryFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(((String)getObject("siteId"))));
		 giftRegistryFormHandler.setUpdateRegItemsServiceName("updateRegistryItems");
		 giftRegistryFormHandler.handleRemoveItemFromGiftRegistry(getRequest(), getResponse());
		 
		 boolean formError = giftRegistryFormHandler.getFormError();
		 
		 
		 assertTrue(formError);
		 
		 
		 //log out
		 profile.setDataSource(null);
	 }
	 
	/**
	  * Test Case 5 : User sending email with Registry Link
	  * testing handleCreateRegistry method
	  * @throws Exception
	  */	 
	 public void testHandleEmailRegistry() throws Exception {
		 

		 Profile profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
		 ProfileTools profileTool = (ProfileTools) getRequest().resolveName("/atg/userprofiling/ProfileTools");
		 
		 String username0 = (String) getObject("username0");
		 String siteId = (String) getObject("siteId");
		 profile.setDataSource(profileTool.getItemFromEmail(username0));
		 //ensure profile is not transient			
		 assertFalse(profile.isTransient());
		 
		 GiftRegistryFormHandler giftRegistryFormHandler = (GiftRegistryFormHandler) getObject("giftRegistryFormHandler");
		 
		 giftRegistryFormHandler.resetFormExceptions();
		 giftRegistryFormHandler.setValidatedCaptcha(false);
		 //Dummy answer
		 if(giftRegistryFormHandler.getCaptchaAnswer() ==null){
			 giftRegistryFormHandler.setCaptchaAnswer("AD123D");
		 }
		 
		 giftRegistryFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(((String)getObject("siteId"))));
		 
		 EmailHolder emailHolder = (EmailHolder)getRequest().resolveName("/com/bbb/email/EmailHolder");
		 
		 /*giftRegistryFormHandler.getValue().put("registryURL", "www.buybuybaby/store/giftregistry/view_registry?reg=1010");
		 giftRegistryFormHandler.getValue().put("recipientEmail", "test@buybuybaby.com");
		 giftRegistryFormHandler.getValue().put("message", "Dear, This is a dummy message");
		 giftRegistryFormHandler.getValue().put("senderName", "Testing");
		 giftRegistryFormHandler.getValue().put("eventType", "baby");
		 giftRegistryFormHandler.getValue().put("rowId", "RID12");
		 */
		 
		 emailHolder.getValues().put("registryURL", "www.buybuybaby/store/giftregistry/view_registry?reg=1010");
		 emailHolder.getValues().put("recipientEmail", "test@buybuybaby.com");
		 emailHolder.getValues().put("message", "Dear, This is a dummy message");
		 emailHolder.getValues().put("senderName", "Testing");
		 emailHolder.getValues().put("eventType", "baby");
		 		 
		 //invoke handleEmailRegistry
		 giftRegistryFormHandler.handleEmailRegistry(getRequest(), getResponse());
		 
		 boolean formError = giftRegistryFormHandler.getFormError();
		 assertFalse(formError);
		 //log out
		 profile.setDataSource(null);
	 }

	 
}
