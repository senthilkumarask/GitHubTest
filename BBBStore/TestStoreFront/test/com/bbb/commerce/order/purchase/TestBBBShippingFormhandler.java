package com.bbb.commerce.order.purchase;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupRelationship;
import atg.commerce.order.purchase.CommerceItemShippingInfo;
import atg.commerce.order.purchase.ShippingGroupDroplet;
import atg.droplet.DropletException;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.account.api.BBBAddressAPI;
import com.bbb.account.api.BBBAddressVO;
import com.bbb.commerce.common.BBBAddress;
import com.bbb.commerce.common.BBBAddressContainer;
import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship;
import com.bbb.commerce.order.droplet.BBBOrderOmnitureDroplet;
import com.bbb.commerce.order.formhandler.BBBShippingGroupFormhandler;
import com.bbb.commerce.order.vo.OrderOmnitureVO;
import com.bbb.commerce.service.pricing.BBBPricingWSMapper;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.bedbathandbeyond.atg.PricingRequestDocument;
import com.sapient.common.tests.BaseTestCase;

public class TestBBBShippingFormhandler extends BaseTestCase {
	
	
	@SuppressWarnings("unchecked")
    public void testMultiShppingFormhandler() throws Exception {
	    DynamoHttpServletRequest pRequest =  getRequest();
        DynamoHttpServletResponse pResponse = getResponse();
        ServletUtil.setCurrentRequest(getRequest());
        BBBOrderOmnitureDroplet orderOmnitureDroplet = (BBBOrderOmnitureDroplet)getObject("bbbOrderOmnitureDroplet");       
        
        PricingRequestDocument wsRequest;
        BBBOrderImpl order = null;
        
        String siteId = (String)getObject("siteId");
        wsRequest = com.bedbathandbeyond.atg.PricingRequestDocument.Factory.parse((String) getObject("xmlAsString"));
        
        getRequest().setParameter("siteId", siteId);
        SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
        try {
        	siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(siteId));
        } catch (SiteContextException siteContextException) {
        	throw new BBBSystemException("Exception" + siteContextException);
        }
        
        Profile profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
        order = (BBBOrderImpl) ((BBBPricingWSMapper) getObject("bbbPriceObject")).transformRequestToOrder(wsRequest.getPricingRequest(), profile, new HashMap<String, Object>());
        order.setSiteId(siteId);
        
        OrderHolder shoppingCart = (OrderHolder) getRequest().resolveName("/atg/commerce/ShoppingCart");
        Order oldOrder = shoppingCart.getCurrent();
        
        shoppingCart.setCurrent(order);
        
        OrderOmnitureVO omnitureVO = null;
        pRequest.setParameter("order", order);
        try{
            orderOmnitureDroplet.service(pRequest, pResponse);
            omnitureVO = (OrderOmnitureVO)pRequest.getObjectParameter(BBBCoreConstants.OMNITURE_VO);
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        if(omnitureVO != null){
            assertNotNull(omnitureVO.getProducts());
            //assertEquals("Order Id should be the Purchase ID ", order.getId(), omnitureVO.getPurchaseID());
        }
        
        
		BBBShippingGroupFormhandler shpGrpForm = (BBBShippingGroupFormhandler)getObject("bbbShippingGroupFormHandler");		
		
        
        
		
		assertEquals("Order should contain 4 commerce items ", 4, order.getCommerceItemCount());
		order.setSiteId(siteId);
		shpGrpForm.setOrder(order);
		
		
		
		BBBShippingGroupContainerService bbbSGCS = (BBBShippingGroupContainerService) getRequest().resolveName((String) getObject("bbbSGCS"));		
		
		System.out.println("AllCommerceItemShippingInfo size  before : "+bbbSGCS.getAllCommerceItemShippingInfos().size());
		System.out.println("AllCommerceItem size  before : "+order.getCommerceItemCount());
		initCISIContainer(order, profile, bbbSGCS);
		System.out.println("AllCommerceItem size  after : "+order.getCommerceItemCount());
		System.out.println("AllCommerceItemShippingInfo size  after : "+bbbSGCS.getAllCommerceItemShippingInfos().size());
		
		assertEquals("AllCommerceItemShippingInfo size should be 4: ", 4, bbbSGCS.getAllCommerceItemShippingInfos().size());
		
		HardgoodShippingGroup hgSG = (HardgoodShippingGroup)shpGrpForm.getOrder().getShippingGroups().get(0);
		CommerceItem bbbItem = (CommerceItem)shpGrpForm.getOrder().getCommerceItems().get(0);

		//System.out.println("catalogRefID: " + bbbItem.getCatalogRefId());
		
		//GSARepository repository = (GSARepository)Nucleus.getGlobalNucleus().resolveName("/com/bbb/configurekeys/ConfigureKeys");
		//System.out.println("***** DATASOURCE: "+repository.getDataSource().getConnection());
		//System.out.println("***** DataSourceConnectionInfo: "+repository.getDataSourceConnectionInfo());
		
		
		
		//Test Invalidf Store ID
		shpGrpForm.setOldShippingId(hgSG.getId());
		shpGrpForm.setNewQuantity("3");
		shpGrpForm.setStoreId("");
		shpGrpForm.setCommerceItemId(bbbItem.getId());
		
		shpGrpForm.handleChangeToStorePickup(pRequest, pResponse);
		assertTrue("Error because of Invalid Store ID", shpGrpForm.getFormError());
		shpGrpForm.resetFormExceptions();
		
		//Test for new quantity greater than 3
		String storeId = (String)getObject("storeId1");//25
		shpGrpForm.setOldShippingId(hgSG.getId());
		shpGrpForm.setNewQuantity("4");
		shpGrpForm.setStoreId(storeId);
		shpGrpForm.setCommerceItemId(bbbItem.getId());
		
		
		shpGrpForm.handleChangeToStorePickup(pRequest, pResponse);	
		assertTrue("Error due to Invalid Quantity", shpGrpForm.getFormError());		
		shpGrpForm.resetFormExceptions();
		
		//Test for newQuantity == item.quantity
		shpGrpForm.setOldShippingId(hgSG.getId());		
		shpGrpForm.setNewQuantity("3");
		shpGrpForm.setStoreId(storeId);
		shpGrpForm.setCommerceItemId(bbbItem.getId());
		
		//shpGrpForm.handleChangeToStorePickup(pRequest, pResponse);
		Vector<DropletException> vec  = shpGrpForm.getFormExceptions();
		int ii=0;
		for (DropletException dropEx : vec) {
			System.out.println("Error msg"+ii+++" :" + dropEx.getMessage());
		}
		//assertTrue("No Form exceptions",shpGrpForm.getFormExceptions().isEmpty());
		
		ShippingGroupRelationship shpRel = (ShippingGroupRelationship)bbbItem.getShippingGroupRelationships().get(0);
		ShippingGroup storeSG = shpRel.getShippingGroup();
		
		//assertNotSame("Shipping group ids do not match: New  store pickup shipping group created for bbbItem",hgSG.getId(),storeSG.getId());		
		
		if(storeSG instanceof BBBStoreShippingGroup){
			assertEquals("shipping group is of type storepickup and relation count is 1 ", 1, storeSG.getCommerceItemRelationshipCount());
			assertEquals("commerce item id matches",bbbItem.getId(),((CommerceItemRelationship)storeSG.getCommerceItemRelationships().get(0)).getCommerceItem().getId());	
			
			//Test for ship to Online
			
			shpGrpForm.setOldShippingId(storeSG.getId());
			shpGrpForm.setNewQuantity("4");		
			shpGrpForm.setCommerceItemId(bbbItem.getId());
				
			shpGrpForm.handleChangeToShipOnline(pRequest, pResponse);
			assertTrue("Error due to Invalid Quantity", shpGrpForm.getFormError());		
			shpGrpForm.resetFormExceptions();
			
			//Test for newQuantity == item.quantity
			shpGrpForm.setOldShippingId(storeSG.getId());		
			shpGrpForm.setNewQuantity("3");		
			shpGrpForm.setCommerceItemId(bbbItem.getId());
			
			shpGrpForm.handleChangeToShipOnline(pRequest, pResponse);
			assertFalse("No Form exceptions",shpGrpForm.getFormError());		
			
		}
		
		for(int i=0; i < shpGrpForm.getOrder().getShippingGroupCount(); i++){
			ShippingGroup sg = (ShippingGroup)shpGrpForm.getOrder().getShippingGroups().get(i);
			if(sg instanceof BBBHardGoodShippingGroup){				
				List<BBBShippingGroupCommerceItemRelationship> BBBShippingGroupCommerceItemRelationshipList = sg.getCommerceItemRelationships();
				for (BBBShippingGroupCommerceItemRelationship bbbShippingGroupCommerceItemRelationship : BBBShippingGroupCommerceItemRelationshipList) {
					if(bbbShippingGroupCommerceItemRelationship.getCommerceItem().getId() != null && 
							bbbShippingGroupCommerceItemRelationship.getCommerceItem().getId().equalsIgnoreCase(bbbItem.getId())){
						assertEquals("All quantity of item should belong to HardGoodShippingGroup now :",bbbItem.getQuantity(), bbbShippingGroupCommerceItemRelationship.getQuantity());
					}
				}				
			}
		}
		
		//Test Add New Address
		BBBAddressAPI bbbAddrAPI = (BBBAddressAPI)getObject("bbbAddrAPI");
		List<BBBAddressVO> shipAddrList = bbbAddrAPI.getShippingAddress(profile, siteId);
		int oldCount = shipAddrList.size();
		BBBAddressVO tempAddr = createNewAddress("fName", "lname", "addr1", "Plano", "TX", "08108");		
		shpGrpForm.setAddress(tempAddr);
		shpGrpForm.setSaveShippingAddress(true);		
		shpGrpForm.setCisiIndex("0");
		shpGrpForm.handleAddNewAddress(pRequest, pResponse);	
				
		
		shipAddrList = bbbAddrAPI.getShippingAddress(profile, siteId);
		assertEquals("Profile Ship Address Size should be " + (oldCount + 1), oldCount + 1, shipAddrList.size());
		assertEquals("ShippingGroupMap Size should be 1", 1, bbbSGCS.getShippingGroupMap().size());
		
		//Test Add New LTL Address for Non LTL item
		BBBAddressVO tempLTLAddr = createNewLTLAddress("fName", "lname", "addr11206", "Plano", "TX", "70512", "9090909090", "9999999999", "ugoel@sapient.com");
		shpGrpForm.setAddress(tempLTLAddr);
		shpGrpForm.setSaveShippingAddress(true);		
		shpGrpForm.setCisiIndex("1");
		shpGrpForm.handleAddNewAddress(pRequest, pResponse);
		boolean formError=shpGrpForm.getFormError();
		System.out.println("Form Error Should be Present to Add LTL Address for Non LTL Item"); 
		assertFalse(formError);	
		//assertEquals("Order SG count should be 2", 2, shpGrpForm.getOrder().getShippingGroupCount());
		
		//Test Ship to Multiple People
		
		assertEquals("AllCommerceItemShippingInfo size Before Split should be 4: ", 4, bbbSGCS.getAllCommerceItemShippingInfos().size());
		
		shpGrpForm.setCisiIndex("0");
		shpGrpForm.handleShipToMultiplePeople(pRequest, pResponse);
		assertEquals("AllCommerceItemShippingInfo size After Split should be 6: ", 6, bbbSGCS.getAllCommerceItemShippingInfos().size());
		
		//Test Multi Shipping
		
		BBBAddressContainer addrContainer = (BBBAddressContainer)getObject("bbbAddContainer");
		oldCount = addrContainer.getAddressMap().size();
		BBBAddressVO APOAddr = createNewAddress("fName", "lname", "addr1", "plano", "TX", "12345");			
		BBBAddressVO nonAPOAddr = createNewAddress("fName", "lname", "addr1", "plano", "TX", "12345");
		
		hgSG =  shpGrpForm.getManager().createHardGoodShippingGroup( shpGrpForm.getOrder(), APOAddr, null);
		String sgName1 = bbbSGCS.getNewShippingGroupName(hgSG, null);
		bbbSGCS.addShippingGroup(sgName1, hgSG);
		addrContainer.getAddressMap().put( sgName1, (BBBAddress)APOAddr);
		
		hgSG =  shpGrpForm.getManager().createHardGoodShippingGroup( shpGrpForm.getOrder(), nonAPOAddr, null);
		String sgName2 = bbbSGCS.getNewShippingGroupName(hgSG, null);
		bbbSGCS.addShippingGroup(sgName2, hgSG);
		addrContainer.getAddressMap().put( sgName2, (BBBAddress)nonAPOAddr);
		
		hgSG =  shpGrpForm.getManager().createHardGoodShippingGroup( shpGrpForm.getOrder(), nonAPOAddr, null);
		String sgName3 = bbbSGCS.getNewShippingGroupName(hgSG, null);
		bbbSGCS.addShippingGroup(sgName3, hgSG);
		addrContainer.getAddressMap().put( sgName3, (BBBAddress)nonAPOAddr);	
		
		assertEquals("ShippingGroupMap Size is 4 ", 4, bbbSGCS.getShippingGroupMap().size());
		assertEquals("AddressMap size  is " + oldCount + 3, oldCount + 3, addrContainer.getAddressMap().size());
		
		//Check for Gift Card Restriction
		//Only GC can not be shipped by Express to a Shipping Address
		
		String giftCatalogRefId = (String)getObject("giftCatalogRefId");
		
		for (int index=0; index< shpGrpForm.getCisiItems().size(); index++) {
			String skuId = ((CommerceItemShippingInfo)shpGrpForm.getCisiItems().get(index)).getCommerceItem().getCatalogRefId();
			
			//Check Form Validation for GiftCard
			if(giftCatalogRefId.equalsIgnoreCase(skuId)){			
	    		
	    		((CommerceItemShippingInfo)shpGrpForm.getCisiItems().get(index)).setShippingMethod("1a");	
	    		//Non APO Address
	    		((CommerceItemShippingInfo)shpGrpForm.getCisiItems().get(index)).setShippingGroupName(sgName2);
			} else {
				//Non APO address
				((CommerceItemShippingInfo)shpGrpForm.getCisiItems().get(index)).setShippingGroupName(sgName3);
			}			
		}
		
		shpGrpForm.handleMultipleShipping(pRequest, pResponse);
		
		assertTrue("There should be Form Error for Giftcard restriction : ", shpGrpForm.getFormError());
		shpGrpForm.resetFormExceptions();
		
		//Check for APO state restriction
		//Express shipping is not allowed for APO states
		
		for (int index=0; index< shpGrpForm.getCisiItems().size(); index++) {
			String skuId = ((CommerceItemShippingInfo)shpGrpForm.getCisiItems().get(index)).getCommerceItem().getCatalogRefId();			
			
			
			if(giftCatalogRefId.equalsIgnoreCase(skuId)){	    		
	    		// To avoid form error
				((CommerceItemShippingInfo)shpGrpForm.getCisiItems().get(index)).setShippingMethod("Standard");
			}else{
				//Check Form Validation Shipping Method Restriction for State
				((CommerceItemShippingInfo)shpGrpForm.getCisiItems().get(index)).setShippingGroupName(sgName1);
				((CommerceItemShippingInfo)shpGrpForm.getCisiItems().get(index)).setShippingMethod("Express");
			}
			
		}
				
		shpGrpForm.handleMultipleShipping(pRequest, pResponse);
		
		//assertTrue("There should be Form Error for shipping method restriction to state APO : ", shpGrpForm.getFormError());
		shpGrpForm.resetFormExceptions();
		
		//Test Multi Shipping
		
		for (int index=0; index< shpGrpForm.getCisiItems().size(); index++) {					
			
				((CommerceItemShippingInfo)shpGrpForm.getCisiItems().get(index)).setShippingGroupName(sgName2);	
	    		((CommerceItemShippingInfo)shpGrpForm.getCisiItems().get(index)).setShippingMethod("Standard");		    		
						
		}
		
		
		
		ServletUtil.setCurrentRequest(this.getRequest());
		//Set for checking GIFT order
		shpGrpForm.setOrderIncludesGifts(true);
		
		shpGrpForm.handleMultipleShipping(pRequest, pResponse);
		
		assertFalse("There should no Form Error : ", shpGrpForm.getFormError());
		
		//Removed all unused SG
		assertFalse("Order SG size Before  : ", shpGrpForm.getOrder().getShippingGroupCount() > 1);
		
		//Check for Gift Order
		CheckoutProgressStates chkProgStates = (CheckoutProgressStates)pRequest.resolveName("/com/bbb/commerce/order/purchase/CheckoutProgressStates");
		assertEquals("Current Level should be GIFT: ", CheckoutProgressStates.DEFAULT_STATES.GIFT.toString(), chkProgStates.getCurrentLevel());
		
		shoppingCart.setCurrent(oldOrder);
		//shpGrpForm.getOrderManager().removeOrder(order.getId());
	}
	
	
	/*public void testHandleChangeToStorePickup() throws Exception{
		DynamoHttpServletRequest pRequest =  getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		BBBShippingGroupFormhandler shpGrpForm = (BBBShippingGroupFormhandler)getObject("bbbShippingGroupFormHandler");		
				
		PricingRequestDocument wsRequest;
        BBBOrderImpl order = null;
		
		String siteId = (String)getObject("siteId");
		wsRequest = com.bedbathandbeyond.atg.PricingRequestDocument.Factory.parse((String) getObject("xmlAsString"));
        
        
		
		Profile profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
		order = (BBBOrderImpl) ((BBBPricingWSMapper) getObject("bbbPriceObject")).transformRequestToOrder(wsRequest.getPricingRequest(), profile, new HashMap<String, Object>());
		order.setSiteId(siteId);
		shpGrpForm.setOrder(order);
		Order oldOrder = shpGrpForm.getShoppingCart().getCurrent();
		shpGrpForm.getShoppingCart().setCurrent(order);
		
		initCISIContainer();				
		
		HardgoodShippingGroup hgSG = (HardgoodShippingGroup)shpGrpForm.getOrder().getShippingGroups().get(0);
		CommerceItem bbbItem = (CommerceItem)shpGrpForm.getOrder().getCommerceItems().get(0);
		// Tests for BBBShippingGroupFormhandler starts here
		
		//Test for invalid store id input
		String newQuantity = (String)getObject("newQuantity");//6
		shpGrpForm.setOldShippingId(hgSG.getId());
		shpGrpForm.setNewQuantity(newQuantity);
		shpGrpForm.setStoreId("");
		shpGrpForm.setCommerceItemId(bbbItem.getId());
		
		boolean result1 = shpGrpForm.handleChangeToStorePickup(pRequest, pResponse);	
		assertTrue("Not ERR_CART_INVALID_STORE_ID", shpGrpForm.getFormError());
		//System.out.println(((DropletException)shpGrpForm.getFormExceptions().get(0)).getMessage());
		
		
		
		//Test for new quantity greater than item.getQuantity
		String storeId = (String)getObject("storeId1");//25
		shpGrpForm.setOldShippingId(hgSG.getId());
		shpGrpForm.setNewQuantity(newQuantity);
		shpGrpForm.setStoreId(storeId);
		shpGrpForm.setCommerceItemId(bbbItem.getId());
		
		shpGrpForm.resetFormExceptions();
		boolean result2 = shpGrpForm.handleChangeToStorePickup(pRequest, pResponse);	
		assertTrue("Not ERR_CART_QUANTITY_INCORRECT", shpGrpForm.getFormError());
		
		shpGrpForm.resetFormExceptions();
		
		//Test for newQuantity == item.quantity
		shpGrpForm.setOldShippingId(hgSG.getId());
		String newQuantity2 = (String)getObject("newQuantity2");//5
		shpGrpForm.setNewQuantity(newQuantity2);
		shpGrpForm.setStoreId(storeId);
		shpGrpForm.setCommerceItemId(bbbItem.getId());
		
		boolean result3 = shpGrpForm.handleChangeToStorePickup(pRequest, pResponse);
		assertTrue("No Form exceptions",shpGrpForm.getFormExceptions().isEmpty());
		
		//BBBCommerceItem bbbItem1 = (BBBCommerceItem)formHandler.getOrder().getCommerceItems().get(0);
		//System.out.println("bbbItem1 id"+bbbItem1.getId());
		assertEquals("storeID of the bbbItem is equal", storeId,((BBBCommerceItem)bbbItem).getStoreId());
		assertEquals("Shiiping group relations should be 1", 1, bbbItem.getShippingGroupRelationshipCount());		
		

		ShippingGroupRelationship shpRel = (ShippingGroupRelationship)bbbItem.getShippingGroupRelationships().get(0);
		ShippingGroup shpGrp = shpRel.getShippingGroup();
		
		assertNotSame("Shipping group ids do not match: New  store pickup shipping group created for bbbItem",hgSG.getId(),shpGrp.getId());
		assertEquals("Quantity is same:", Long.parseLong(newQuantity2),bbbItem.getQuantity());
		
		if(shpGrp instanceof BBBStoreShippingGroup){
			assertEquals("shipping group is of type storepickup and relation count is 1 ", 1, shpGrp.getCommerceItemRelationshipCount());
			assertEquals("commerce item id matches",bbbItem.getId(),((CommerceItemRelationship)shpGrp.getCommerceItemRelationships().get(0)).getCommerceItem().getId());			
		}
		
		
		
		for(int i=0; i < shpGrpForm.getOrder().getShippingGroupCount(); i++){
			if(shpGrpForm.getOrder().getShippingGroups().get(i) instanceof BBBStoreShippingGroup){
				ShippingGroup sg = (ShippingGroup)shpGrpForm.getOrder().getShippingGroups().get(i);
				
				//Test for ship to Online
				
				shpGrpForm.setOldShippingId(sg.getId());
				shpGrpForm.setNewQuantity(newQuantity);		
				shpGrpForm.setCommerceItemId(bbbItem.getId());
					
				shpGrpForm.handleChangeToShipOnline(pRequest, pResponse);
				assertTrue("Not ERR_CART_QUANTITY_INCORRECT", shpGrpForm.getFormError());
				
				shpGrpForm.resetFormExceptions();
				
				//Test for newQuantity == item.quantity
				shpGrpForm.setOldShippingId(sg.getId());		
				shpGrpForm.setNewQuantity(newQuantity2);		
				shpGrpForm.setCommerceItemId(bbbItem.getId());
				
				shpGrpForm.handleChangeToShipOnline(pRequest, pResponse);
				assertTrue("No Form exceptions",shpGrpForm.getFormExceptions().isEmpty());				
			}
		}
		
		for(int i=0; i < shpGrpForm.getOrder().getShippingGroupCount(); i++){
			ShippingGroup sg = (ShippingGroup)shpGrpForm.getOrder().getShippingGroups().get(i);
			if(sg instanceof BBBHardGoodShippingGroup){
				long qty = Long.parseLong(newQuantity2);
				assertEquals("All items should belong to HardGoodShippingGroup now :",qty, ((BBBShippingGroupCommerceItemRelationship)sg.getCommerceItemRelationships().get(0)).getQuantity());
			}
		}
		
		shpGrpForm.getShoppingCart().setCurrent(oldOrder);
	}
	

	
	public void testHandleAddNewAddress() throws Exception{	
		
		DynamoHttpServletRequest pRequest =  getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		BBBShippingGroupFormhandler shpGrpForm = (BBBShippingGroupFormhandler)getObject("bbbShippingGroupFormHandler");		
		BBBAddressAPI bbbAddrAPI = (BBBAddressAPI)getObject("bbbAddrAPI");
		BBBShippingGroupContainerService bbbSGCS = (BBBShippingGroupContainerService)getObject("bbbSGCS");
				
		PricingRequestDocument wsRequest;
        BBBOrderImpl order = null;
		
		String siteId = (String)getObject("siteId");
		wsRequest = com.bedbathandbeyond.atg.PricingRequestDocument.Factory.parse((String) getObject("xmlAsString"));
        
        
		
		Profile profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
		order = (BBBOrderImpl) ((BBBPricingWSMapper) getObject("bbbPriceObject")).transformRequestToOrder(wsRequest.getPricingRequest(), profile, new HashMap<String, Object>());
		order.setSiteId(siteId);
		shpGrpForm.setOrder(order);
		Order oldOrder = shpGrpForm.getShoppingCart().getCurrent();
		shpGrpForm.getShoppingCart().setCurrent(order);
		
		initCISIContainer();
		
		assertEquals("Size of CommerceItemShippingInfo list should be 1 ",shpGrpForm.getCommerceItemShippingInfoContainer().getAllCommerceItemShippingInfos().size(), 1);
		
		List<BBBAddressVO> shipAddrList = bbbAddrAPI.getShippingAddress(profile, siteId);
		assertEquals("Profile Ship Address Size should be 0", 0, shipAddrList.size());
				
		BBBAddressVO tempAddr = createNewAddress("fName", "lname", "addr1", "city1", "state1", "12345");		
		shpGrpForm.setAddress(tempAddr);
		shpGrpForm.setSaveShippingAddress(true);		
		shpGrpForm.setCisiIndex("0");
		shpGrpForm.handleAddNewAddress(pRequest, pResponse);	
				
		shipAddrList = bbbAddrAPI.getShippingAddress(profile, siteId);
		assertEquals("Profile Ship Address Size should be 1", 1, shipAddrList.size());
		assertEquals("ShippingGroupMap Size should be 2", 2, bbbSGCS.getShippingGroupMap().size());
		assertEquals("Order SG count should be 2", 2, shpGrpForm.getOrder().getShippingGroupCount());	
		
		shpGrpForm.getShoppingCart().setCurrent(oldOrder);
				
	}
	
	
	public void testHandleShipToMultiplePeople() throws Exception{
		DynamoHttpServletRequest pRequest =  getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		BBBShippingGroupFormhandler shpGrpForm = (BBBShippingGroupFormhandler)getObject("bbbShippingGroupFormHandler");	
		BBBShippingGroupContainerService bbbSGCS = (BBBShippingGroupContainerService)getObject("bbbSGCS");
						
		PricingRequestDocument wsRequest;
        BBBOrderImpl order = null;
		
		String siteId = (String)getObject("siteId");
		wsRequest = com.bedbathandbeyond.atg.PricingRequestDocument.Factory.parse((String) getObject("xmlAsString"));
        
        
		
		Profile profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
		order = (BBBOrderImpl) ((BBBPricingWSMapper) getObject("bbbPriceObject")).transformRequestToOrder(wsRequest.getPricingRequest(), profile, new HashMap<String, Object>());
		order.setSiteId(siteId);
		shpGrpForm.setOrder(order);
		Order oldOrder = shpGrpForm.getShoppingCart().getCurrent();
		shpGrpForm.getShoppingCart().setCurrent(order);
		
		initCISIContainer();

		assertEquals("AllCommerceItemShippingInfo size Before Split should be 3: ", 3, bbbSGCS.getAllCommerceItemShippingInfos().size());
		
		shpGrpForm.setCisiIndex("2");
		shpGrpForm.handleShipToMultiplePeople(pRequest, pResponse);
		assertEquals("AllCommerceItemShippingInfo size After Split should be 5: ", 5, bbbSGCS.getAllCommerceItemShippingInfos().size());
		
		shpGrpForm.getShoppingCart().setCurrent(oldOrder);
	}
	
	
	
	public void testHandleMultipleShipping() throws Exception{
		DynamoHttpServletRequest pRequest =  getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		BBBShippingGroupFormhandler shpGrpForm = (BBBShippingGroupFormhandler)getObject("bbbShippingGroupFormHandler");	
		BBBShippingGroupContainerService bbbSGCS = (BBBShippingGroupContainerService)getObject("bbbSGCS");		
		BBBAddressContainer addrContainer = (BBBAddressContainer)getObject("bbbAddContainer");
				
		String giftCatalogRefId = (String)getObject("giftCatalogRefId");

		
		PricingRequestDocument wsRequest;
        BBBOrderImpl order = null;
		
		String siteId = (String)getObject("siteId");
		wsRequest = com.bedbathandbeyond.atg.PricingRequestDocument.Factory.parse((String) getObject("xmlAsString"));
        
        
		
		Profile profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
		order = (BBBOrderImpl) ((BBBPricingWSMapper) getObject("bbbPriceObject")).transformRequestToOrder(wsRequest.getPricingRequest(), profile, new HashMap<String, Object>());
		order.setSiteId(siteId);
		shpGrpForm.setOrder(order);
		Order oldOrder = shpGrpForm.getShoppingCart().getCurrent();
		shpGrpForm.getShoppingCart().setCurrent(order);
				
        
		initCISIContainer();
				
		assertEquals("AllCommerceItemShippingInfo size should be 3: ", 3, bbbSGCS.getAllCommerceItemShippingInfos().size());
				
		
		BBBAddressVO APOAddr = createNewAddress("fName", "lname", "addr1", "city1", "AA", "12345");			
		BBBAddressVO nonAPOAddr = createNewAddress("fName", "lname", "addr1", "city1", "KA", "12345");
		
		BBBHardGoodShippingGroup hgSG =  shpGrpForm.getManager().createHardGoodShippingGroup( shpGrpForm.getOrder(), APOAddr, null);
		String sgName1 = bbbSGCS.getNewShippingGroupName(hgSG, null);
		bbbSGCS.addShippingGroup(sgName1, hgSG);
		addrContainer.getAddressMap().put( sgName1, (BBBAddress)APOAddr);
		
		hgSG =  shpGrpForm.getManager().createHardGoodShippingGroup( shpGrpForm.getOrder(), nonAPOAddr, null);
		String sgName2 = bbbSGCS.getNewShippingGroupName(hgSG, null);
		bbbSGCS.addShippingGroup(sgName2, hgSG);
		addrContainer.getAddressMap().put( sgName2, (BBBAddress)nonAPOAddr);
		
		hgSG =  shpGrpForm.getManager().createHardGoodShippingGroup( shpGrpForm.getOrder(), nonAPOAddr, null);
		String sgName3 = bbbSGCS.getNewShippingGroupName(hgSG, null);
		bbbSGCS.addShippingGroup(sgName3, hgSG);
		addrContainer.getAddressMap().put( sgName3, (BBBAddress)nonAPOAddr);	
    		
		for (int index=0; index< shpGrpForm.getCisiItems().size(); index++) {
			String skuId = ((CommerceItemShippingInfo)shpGrpForm.getCisiItems().get(index)).getCommerceItem().getCatalogRefId();
			
			//Check Form Validation for GiftCard
			if(giftCatalogRefId.equalsIgnoreCase(skuId)){			
	    		
	    		((CommerceItemShippingInfo)shpGrpForm.getCisiItems().get(index)).setShippingMethod("Express");	
	    		//Non APO Address
	    		((CommerceItemShippingInfo)shpGrpForm.getCisiItems().get(index)).setShippingGroupName(sgName2);
			} else {
				//Non APO address
				((CommerceItemShippingInfo)shpGrpForm.getCisiItems().get(index)).setShippingGroupName(sgName3);
			}			
		}
		
		shpGrpForm.handleMultipleShipping(pRequest, pResponse);
		
		assertTrue("There should be Form Error for Giftcard restriction : ", shpGrpForm.getFormError());
		
		for (int index=0; index< shpGrpForm.getCisiItems().size(); index++) {
			String skuId = ((CommerceItemShippingInfo)shpGrpForm.getCisiItems().get(index)).getCommerceItem().getCatalogRefId();			
			
			
			if(giftCatalogRefId.equalsIgnoreCase(skuId)){	    		
	    		// To avoid form error
				((CommerceItemShippingInfo)shpGrpForm.getCisiItems().get(index)).setShippingMethod("Standard");
			}else{
				//Check Form Validation Shipping Method Restriction for State
				((CommerceItemShippingInfo)shpGrpForm.getCisiItems().get(index)).setShippingGroupName(sgName1);
				((CommerceItemShippingInfo)shpGrpForm.getCisiItems().get(index)).setShippingMethod("Express");
			}
			
		}
		
		shpGrpForm.resetFormExceptions();
		shpGrpForm.handleMultipleShipping(pRequest, pResponse);
		
		assertTrue("There should be Form Error for shipping method restriction to state APO : ", shpGrpForm.getFormError());
		
		for (int index=0; index< shpGrpForm.getCisiItems().size(); index++) {					
			
				((CommerceItemShippingInfo)shpGrpForm.getCisiItems().get(index)).setShippingGroupName(sgName2);	
	    		((CommerceItemShippingInfo)shpGrpForm.getCisiItems().get(index)).setShippingMethod("Standard");		    		
						
		}
		shpGrpForm.resetFormExceptions();
		shpGrpForm.handleMultipleShipping(pRequest, pResponse);
		
		assertFalse("There should no Form Error : ", shpGrpForm.getFormError());
		
		assertEquals("Order SG size should be 1", 1, shpGrpForm.getOrder().getShippingGroupCount());
		
		
		shpGrpForm.getShoppingCart().setCurrent(oldOrder);
				
	}
	
	
	public void testHandleMultipleShippingForGiftOrder() throws Exception{
		DynamoHttpServletRequest pRequest =  getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		BBBShippingGroupFormhandler shpGrpForm = (BBBShippingGroupFormhandler)getObject("bbbShippingGroupFormHandler");	
		BBBShippingGroupContainerService bbbSGCS = (BBBShippingGroupContainerService)getObject("bbbSGCS");		
		BBBAddressContainer addrContainer = (BBBAddressContainer)getObject("bbbAddContainer");
		
				
		
		String nonGiftcatalogRefId1 = (String)getObject("nonGiftcatalogRefId1");			
		
				
		PricingRequestDocument wsRequest;
        BBBOrderImpl order = null;
		
		String siteId = (String)getObject("siteId");
		wsRequest = com.bedbathandbeyond.atg.PricingRequestDocument.Factory.parse((String) getObject("xmlAsString"));
        
        
		
		Profile profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
		order = (BBBOrderImpl) ((BBBPricingWSMapper) getObject("bbbPriceObject")).transformRequestToOrder(wsRequest.getPricingRequest(), profile, new HashMap<String, Object>());
		order.setSiteId(siteId);
		shpGrpForm.setOrder(order);
		Order oldOrder = shpGrpForm.getShoppingCart().getCurrent();
		shpGrpForm.getShoppingCart().setCurrent(order);
		
		initCISIContainer();
				
		assertEquals("AllCommerceItemShippingInfo size should be 2: ", 2, bbbSGCS.getAllCommerceItemShippingInfos().size());
				
		
		BBBAddressVO nonAPOAddr = createNewAddress("fName", "lname", "addr1", "city1", "KA", "12345");
		
		
		
		BBBHardGoodShippingGroup hgSG =  shpGrpForm.getManager().createHardGoodShippingGroup( shpGrpForm.getOrder(), nonAPOAddr, null);
		String sgName1 = bbbSGCS.getNewShippingGroupName(hgSG, null);
		bbbSGCS.addShippingGroup(sgName1, hgSG);
		addrContainer.getAddressMap().put( sgName1, (BBBAddress)nonAPOAddr);
		
		hgSG =  shpGrpForm.getManager().createHardGoodShippingGroup( shpGrpForm.getOrder(), nonAPOAddr, null);
		String sgName2 = bbbSGCS.getNewShippingGroupName(hgSG, null);
		bbbSGCS.addShippingGroup(sgName2, hgSG);
		addrContainer.getAddressMap().put( sgName2, (BBBAddress)nonAPOAddr);
		
		
		for (int index=0; index< shpGrpForm.getCisiItems().size(); index++) {
			String skuId = ((CommerceItemShippingInfo)shpGrpForm.getCisiItems().get(index)).getCommerceItem().getCatalogRefId();
			
			//Check Form Validation for GiftCard
			if(nonGiftcatalogRefId1.equalsIgnoreCase(skuId)){	    		
	    		((CommerceItemShippingInfo)shpGrpForm.getCisiItems().get(index)).setShippingMethod("Express");	
	    		((CommerceItemShippingInfo)shpGrpForm.getCisiItems().get(index)).setShippingGroupName(sgName1);
			} else {
				((CommerceItemShippingInfo)shpGrpForm.getCisiItems().get(index)).setShippingMethod("Standard");
				((CommerceItemShippingInfo)shpGrpForm.getCisiItems().get(index)).setShippingGroupName(sgName1);
			}			
		}
		
		
		
		shpGrpForm.setOrderIncludesGifts(true);
		shpGrpForm.resetFormExceptions();
		shpGrpForm.handleMultipleShipping(pRequest, pResponse);
			
		
		assertFalse("There should no Form Error : ", shpGrpForm.getFormError());
				
				
		CheckoutProgressStates chkProgStates = (CheckoutProgressStates)pRequest.resolveName("/com/bbb/commerce/order/purchase/CheckoutProgressStates");
		assertEquals("Current Level should be GIFT: ", CheckoutProgressStates.DEFAULT_STATES.GIFT.toString(), chkProgStates.getCurrentLevel());
		
		shpGrpForm.getShoppingCart().setCurrent(oldOrder);
	}*/
	
	
	@SuppressWarnings("unchecked")
    public void testLTLMultiShppingFormhandler() throws Exception {
	    DynamoHttpServletRequest pRequest =  getRequest();
        DynamoHttpServletResponse pResponse = getResponse();
        ServletUtil.setCurrentRequest(getRequest());
	
        PricingRequestDocument wsRequest;
        BBBOrderImpl order = null;
        
        String siteId = (String)getObject("siteId");
        wsRequest = com.bedbathandbeyond.atg.PricingRequestDocument.Factory.parse((String) getObject("xmlAsString"));
        
        getRequest().setParameter("siteId", siteId);
        SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
        try {
        	siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(siteId));
        } catch (SiteContextException siteContextException) {
        	throw new BBBSystemException("Exception" + siteContextException);
        }

        Profile profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
        order = (BBBOrderImpl) ((BBBPricingWSMapper) getObject("bbbPriceObject")).transformRequestToOrder(wsRequest.getPricingRequest(), profile, new HashMap<String, Object>());
        order.setSiteId(siteId);
        
        OrderHolder shoppingCart = (OrderHolder) getRequest().resolveName("/atg/commerce/ShoppingCart");
        shoppingCart.setCurrent(order);
        pRequest.setParameter("order", order);
        
		BBBShippingGroupFormhandler shpGrpForm = (BBBShippingGroupFormhandler)getObject("bbbShippingGroupFormHandler");		
		
		assertEquals("Order should contain 4 commerce items ", 4, order.getCommerceItemCount());
		order.setSiteId(siteId);
		shpGrpForm.setOrder(order);
		
		BBBShippingGroupContainerService bbbSGCS = (BBBShippingGroupContainerService) getRequest().resolveName((String) getObject("bbbSGCS"));		
		
		System.out.println("AllCommerceItemShippingInfo size  before : "+bbbSGCS.getAllCommerceItemShippingInfos().size());
		System.out.println("AllCommerceItem size  before : "+order.getCommerceItemCount());
		initCISIContainer(order, profile, bbbSGCS);
		System.out.println("AllCommerceItem size  after : "+order.getCommerceItemCount());
		System.out.println("AllCommerceItemShippingInfo size  after : "+bbbSGCS.getAllCommerceItemShippingInfos().size());
		
		assertEquals("AllCommerceItemShippingInfo size should be 4: ", 4, bbbSGCS.getAllCommerceItemShippingInfos().size());
		
		CommerceItem bbbItem = (CommerceItem)shpGrpForm.getOrder().getCommerceItems().get(0);
		for(int i=0; i < shpGrpForm.getOrder().getShippingGroupCount(); i++){
			ShippingGroup sg = (ShippingGroup)shpGrpForm.getOrder().getShippingGroups().get(i);
			if(sg instanceof BBBHardGoodShippingGroup){				
				List<BBBShippingGroupCommerceItemRelationship> BBBShippingGroupCommerceItemRelationshipList = sg.getCommerceItemRelationships();
				for (BBBShippingGroupCommerceItemRelationship bbbShippingGroupCommerceItemRelationship : BBBShippingGroupCommerceItemRelationshipList) {
					if(bbbShippingGroupCommerceItemRelationship.getCommerceItem().getId() != null && 
							bbbShippingGroupCommerceItemRelationship.getCommerceItem().getId().equalsIgnoreCase(bbbItem.getId())){
						assertEquals("All quantity of item should belong to HardGoodShippingGroup now :",bbbItem.getQuantity(), bbbShippingGroupCommerceItemRelationship.getQuantity());
					}
				}				
			}
		}
		
		//Test Add New LTL Address
		BBBAddressAPI bbbAddrAPI = (BBBAddressAPI)getObject("bbbAddrAPI");
		List<BBBAddressVO> shipAddrList = bbbAddrAPI.getShippingAddress(profile, siteId);
		int oldCount = shipAddrList.size();
		BBBAddressVO tempAddr = createNewLTLAddress("FirstName", "LastName", "addr11206", "Plano", "TX", "70512", "9090909090", "9999999999", "ugoel@sapient.com");
		shpGrpForm.setAddress(tempAddr);
		shpGrpForm.setSaveShippingAddress(true);		
		shpGrpForm.setCisiIndex("0");
		shpGrpForm.handleAddNewAddress(pRequest, pResponse);	
		boolean formError=shpGrpForm.getFormError();
		System.out.println("No Form Error Should be Present to Add New Address"); 
		assertFalse(formError);	
		
		shipAddrList = bbbAddrAPI.getShippingAddress(profile, siteId);
		assertEquals("Profile Ship Address Size should be " + (oldCount + 1), oldCount + 1, shipAddrList.size());
		assertEquals("ShippingGroupMap Size should be 2 for 2 LTL items having different shipping Method", 2, bbbSGCS.getShippingGroupMap().size());
		System.out.println("Profile Address Should Not Contain LTL Shipping Address Additional Fields");
		
		for(int i=0; i <shipAddrList.size(); i++){
			assertEquals("Shipping Phone Number Should be Null" , null,shipAddrList.get(i).getPhoneNumber());
			assertEquals("Shipping Alternate Phone Number Should be Null" , null,shipAddrList.get(i).getAlternatePhoneNumber());
			assertEquals("Shipping Email Should be Null" , null,shipAddrList.get(i).getEmail());
		}
	}
	
	public void testValidAddressPreAddNewAddress() throws Exception
	{
		  DynamoHttpServletRequest pRequest =  getRequest();
	      DynamoHttpServletResponse pResponse = getResponse();
	        
			BBBShippingGroupFormhandler shpGrpForm = (BBBShippingGroupFormhandler)getObject("bbbShippingGroupFormHandler");		
            BBBAddressAPI bbbAddrAPI = (BBBAddressAPI)getObject("bbbAddrAPI");
            Profile profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
            String siteId = (String)getObject("siteId");
            try {
            List<BBBAddressVO> shipAddrList = bbbAddrAPI.getShippingAddress(profile, siteId);
            int oldCount = shipAddrList.size();
            BBBAddressVO tempAddr = createNewLTLAddress("fName", "lname", "addr11206", "Plano", "TX", "70512", "9090909090", "9999999999", "ugoel@sapient.com");
           //MORE THAN 10 DIGITS
            shpGrpForm.setAddress(tempAddr);
            shpGrpForm.setLTLCommerceItem(true);
            shpGrpForm.setSaveShippingAddress(true);        
            shpGrpForm.setCisiIndex("0");
            boolean formError=shpGrpForm.getFormError();
            assertFalse(formError);
            
				shpGrpForm.preAddNewAddress(pRequest, pResponse);
			} catch (ServletException e) {
				throw new BBBSystemException("Exception" + e);
			} catch (IOException e) {
				throw new BBBSystemException("Exception" + e);
			}   			
	        
	        
			
		
	}
	
	
	public void testInvalidAddressPreAddNewAddress() throws Exception
	{
		  DynamoHttpServletRequest pRequest =  getRequest();
	      DynamoHttpServletResponse pResponse = getResponse();
	        
			BBBShippingGroupFormhandler shpGrpForm = (BBBShippingGroupFormhandler)getObject("bbbShippingGroupFormHandler");		
            BBBAddressAPI bbbAddrAPI = (BBBAddressAPI)getObject("bbbAddrAPI");
            Profile profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
            String siteId = (String)getObject("siteId");
            try {
            List<BBBAddressVO> shipAddrList = bbbAddrAPI.getShippingAddress(profile, siteId);
            int oldCount = shipAddrList.size();
            BBBAddressVO tempAddr = createNewLTLAddress("fName", "lname", "addr1", "Plano", "texas", "08108", "emailemail.com" , "99999999999898", "8888888888980");  
           //MORE THAN 10 DIGITS
            shpGrpForm.setAddress(tempAddr);
            shpGrpForm.setLTLCommerceItem(true);
            shpGrpForm.setSaveShippingAddress(true);        
            shpGrpForm.setCisiIndex("0");            
			shpGrpForm.preAddNewAddress(pRequest, pResponse);
			boolean formError=shpGrpForm.getFormError();
	        assertTrue(formError);
	            
			} catch (ServletException e) {
				throw new BBBSystemException("Exception" + e);
			} catch (IOException e) {
				throw new BBBSystemException("Exception" + e);
			}   			
	        
	        
			
		
	}
	public BBBAddressVO createNewAddress(String pFirstName, String pLastName, String pAddress1, 
			String pCity, String pState, String pPostalCode){
		
		BBBAddressVO tempAddr = new BBBAddressVO();
		tempAddr.setFirstName(pFirstName);
		tempAddr.setLastName(pLastName);
		tempAddr.setAddress1(pAddress1);
		tempAddr.setAddress2(pAddress1);
		tempAddr.setCity(pCity);
		tempAddr.setState(pState);
		tempAddr.setPostalCode(pPostalCode);
		tempAddr.setCountry("US");
		return tempAddr;
	}
	
	public BBBAddressVO createNewLTLAddress(String pFirstName, String pLastName, String pAddress1, 
            String pCity, String pState, String pPostalCode, String pEmail, String pPhoneNumber, String mAlternatePhoneNumber){
	
     BBBAddressVO tempAddr = new BBBAddressVO();
     tempAddr.setFirstName(pFirstName);
     tempAddr.setLastName(pLastName);
     tempAddr.setAddress1(pAddress1);
     tempAddr.setAddress2(pAddress1);
     tempAddr.setCity(pCity);
     tempAddr.setState(pState);
     tempAddr.setPostalCode(pPostalCode);
     tempAddr.setCountry("US");
     tempAddr.setEmail(pEmail);
     tempAddr.setPhoneNumber(pPhoneNumber);
     tempAddr.setAlternatePhoneNumber(mAlternatePhoneNumber);
     return tempAddr;
}

	
	private void initCISIContainer(Order order, Profile profile, BBBShippingGroupContainerService bbbSGCS) throws Exception {
		DynamoHttpServletRequest pRequest =  getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		//OrderHolder shoppingCart = (OrderHolder) getRequest().resolveName("/atg/commerce/ShoppingCart");
		ShippingGroupDroplet shippingGroupDroplet = (ShippingGroupDroplet) getRequest().resolveName("/atg/commerce/order/purchase/ShippingGroupDroplet");
		
		shippingGroupDroplet.setOrder(order);
		System.out.println("CommerceItems in order : " + order.getCommerceItems().get(0));
		shippingGroupDroplet.setProfile(profile);
		shippingGroupDroplet.setCommerceItemShippingInfoContainer(bbbSGCS);
		pRequest.setParameter("clear","true");
		pRequest.setParameter("createOneInfoPerUnit","false");
		pRequest.setParameter("shippingGroupTypes","hardgoodShippingGroup,storeShippingGroup");
		pRequest.setParameter("initBasedOnOrder","true");
		// Calling droplet service method
		shippingGroupDroplet.service(pRequest, pResponse);
	}
	
	
}
