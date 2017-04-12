/**
 * @author jpadhi
 * @version Id: //com.bbb.commerce.order.purchase/TestBBBPurchaseProcessHelper.java.TestBBBPurchaseProcessHelper $$
 * @updated $DateTime: Nov 5, 2011 9:59:29 AM
 */
package com.bbb.commerce.order.purchase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.ServletException;

import atg.beans.DynamicBeanDescriptor;

import org.apache.http.HttpHeaders;

import atg.beans.DynamicPropertyDescriptor;
import atg.commerce.CommerceException;
import atg.beans.DynamicBeanInfo;
import atg.beans.DynamicPropertyDescriptor;
import atg.commerce.catalog.custom.CustomCatalogTools;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.purchase.AddCommerceItemInfo;
import atg.commerce.pricing.DetailedItemPriceInfo;
import atg.commerce.pricing.FilteredCommerceItem;
import atg.commerce.pricing.PricingContext;
import atg.commerce.pricing.PricingContextFactory;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.pricing.priceLists.PriceListException;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.core.util.Address;
import atg.droplet.NotZero;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryPropertyDescriptor;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.GenericHttpServletRequest;
import atg.servlet.RequestLocale;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.account.BBBProfileFormHandler;
import com.bbb.account.BBBProfileTools;
import com.bbb.account.api.BBBAddressAPI;
import com.bbb.account.api.BBBAddressVO;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.commerce.checkout.droplet.DisplayShippingRestrictionsDroplet;
import com.bbb.commerce.checkout.droplet.GetApplicableShippingMethodsDroplet;
import com.bbb.commerce.checkout.formhandler.BBBBillingAddressFormHandler;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.commerce.common.BBBAddress;
import com.bbb.commerce.common.BBBAddressContainer;
import com.bbb.commerce.common.BBBRepositoryContactInfo;
import com.bbb.commerce.order.BBBShippingGroupManager;
import com.bbb.commerce.order.droplet.EcoFeeApplicabilityCheckDroplet;
import com.bbb.commerce.order.formhandler.BBBShippingGroupFormhandler;
import com.bbb.commerce.order.vo.SkuRestrictedZipVO;
import com.bbb.commerce.pricing.BBBBopusQualifyingFilter;
import com.bbb.commerce.pricing.BBBCIAttributesBasedFilter;
import com.bbb.commerce.pricing.BBBGiftCardFilter;
import com.bbb.commerce.pricing.BBBPricingTools;
import com.bbb.commerce.pricing.bean.PriceInfoVO;
import com.bbb.commerce.pricing.droplet.BBBPriceDisplayDroplet;
import com.bbb.common.droplet.ShippingMethodDroplet;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.BBBOrderPriceInfo;
import com.bbb.order.bean.BBBShippingPriceInfo;
import com.sapient.common.tests.BaseTestCase;

/**
 * @author jpadhi
 *
 */
public class TestBBBCartFormHandler extends BaseTestCase {
    private static final String DROPLET = "bbbGetApplicableShippingMethodsDroplet";
    private static final String PARAM_PRICE_OBJECT = "priceObject";
    private static final String OUTPUT_PARAM_PRICEINFOVO = "priceInfoVO";
	private static final String REQUEST_HOST_NAME = "localhost";



  /*  @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testHandleAddItemToOrder() throws Exception {
    	//ServletUtil.getDynamoRequest(this.getRequest()).setreq
        final BBBCartFormhandler formHandler = (BBBCartFormhandler)this.getObject("bbbCartFormHandler");
        final DisplaySingleShippingDroplet displaySingleShipping = (DisplaySingleShippingDroplet)this.getObject("bbbDisplaySingleShipping");

        final DynamoHttpServletRequest pRequest =  this.getRequest();
        final DynamoHttpServletResponse pResponse = this.getResponse();

        final BBBShippingGroupFormhandler testShippingGroupForm = (BBBShippingGroupFormhandler) this.getObject("bbbShippingGroupHandler");

        //Add all parametrs in profile level
        final MutableRepositoryItem profileItem = (MutableRepositoryItem)formHandler.getProfile();


        //MutableRepositoryItem profileItem = (MutableRepositoryItem) profileTool.getItemFromEmail("andrew@example.com");

        final PriceListManager priceListManager = (PriceListManager)this.getObject("bbbPriceListManager");
        final String listPriceId = (String)this.getObject("listPriceId");
        final String salePriceId = (String)this.getObject("salePriceId");
        final RepositoryItem listPriceListItem = priceListManager.getPriceList(listPriceId);
        final RepositoryItem salePriceListItem = priceListManager.getPriceList(salePriceId);
        profileItem.setPropertyValue("priceList", listPriceListItem);
        profileItem.setPropertyValue("salePriceList", salePriceListItem);


        final CustomCatalogTools catalogTools = (CustomCatalogTools)this.getObject("bbbCatalogTools");
        final List catalogIds = new ArrayList();
        final String catalogId = (String)this.getObject("catalogId");
        catalogIds.add(catalogId);
        if(catalogTools.getCatalogsForRepository(null, catalogIds) != null){
            final RepositoryItem catalogItem = catalogTools.getCatalogsForRepository(null, catalogIds)[0];
            profileItem.setPropertyValue("catalog", catalogItem);

        }
        formHandler.setProfile(profileItem);
        final String pSiteId = "BedBathUS";
        formHandler.setSiteId(pSiteId);

        //Test logic for DisplayShippingRestrictionsDroplet Droplet

        System.out.println("Shipping Restrictions");
        formHandler.setAddItemCount(1);
        HardgoodShippingGroup sg = (HardgoodShippingGroup)formHandler.getOrder().getShippingGroups().get(0);

        formHandler.setShippingGroup(sg);
        String pCatalogRefId = (String)this.getObject("catalogRefIdZip");
        if((formHandler.getItems() != null) && (formHandler.getItems()[0] != null)){
            final String pProductId = (String)this.getObject("productIdZip");
            formHandler.getItems()[0].setProductId(pProductId);
            formHandler.getItems()[0].setCatalogRefId(pCatalogRefId);
            formHandler.getItems()[0].setQuantity(2);
            formHandler.setSiteId("BedBathUS");
            formHandler.getItems()[0].getValue().put(BBBCatalogConstants.LTL_SHIP_METHOD, BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD);
            formHandler.getItems()[0].getValue().put(BBBCatalogConstants.WHITE_GLOVE_ASSEMLBY, BBBCatalogConstants.TRUE);
            
            
           
        }
       if((formHandler.getItems() != null) &&(formHandler.getItems()[0] != null))
        {	 final String pProductId = (String)this.getObject("productIdZip");
        	 formHandler.getItems()[1].setProductId(pProductId);
             formHandler.getItems()[1].setCatalogRefId(pCatalogRefId);
             formHandler.getItems()[1].setQuantity(1);
             formHandler.setSiteId("BedBathUS");
             formHandler.getItems()[1].getValue().put(BBBCatalogConstants.LTL_SHIP_METHOD, "LW");
             formHandler.getItems()[1].getValue().put(BBBCatalogConstants.WHITE_GLOVE_ASSEMLBY, "false");
       
        }
        formHandler.handleAddItemToOrder(pRequest, pResponse);

        pRequest.setParameter(BBBCoreConstants.ORDER, formHandler.getOrder());

        final DisplayShippingRestrictionsDroplet displayShippingRestriction = (DisplayShippingRestrictionsDroplet)this.getObject("bbbDisplayShippingRestrictionsDroplet");

        displayShippingRestriction.service(pRequest, pResponse);
        //Assert
        final Map<String,List<SkuRestrictedZipVO>> mapSkuRestrictedZip = (HashMap <String,List<SkuRestrictedZipVO>>)this.getRequest().getObjectParameter("mapSkuRestrictedZip");
        //TreeMap <String,ArrayList<CollegeVO>> alphabetCollegeListMap=(TreeMap <String,ArrayList<CollegeVO>>)getRequest().getObjectParameter("mapSkuRestrictedZip");
        System.out.println(" Restricted Zipcode"+mapSkuRestrictedZip);
        System.out.println(" Before Asset for ZIP code restriction");
        assertFalse(mapSkuRestrictedZip.isEmpty());
        //assertTrue("Single Shipping Should be allowed ", (Boolean) pRequest.getObjectParameter("isSingle"));

        this.updateItemInOrder(pCatalogRefId, 0);
        //Test logic
        formHandler.setAddItemCount(1);
        sg = (HardgoodShippingGroup)formHandler.getOrder().getShippingGroups().get(0);
        sg.getShippingAddress().setState((String) this.getObject("stateIdeco"));

        formHandler.setShippingGroup(sg);
        pCatalogRefId = (String)this.getObject("catalogRefIdeco");
        if((formHandler.getItems() != null) && (formHandler.getItems()[0] != null)){
            final String pProductId = (String)this.getObject("productIdeco");
            formHandler.getItems()[0].setProductId(pProductId);
            formHandler.getItems()[0].setCatalogRefId(pCatalogRefId);
            formHandler.getItems()[0].setQuantity(2);
            formHandler.setSiteId("BedBathUS");
        }
        formHandler.handleAddItemToOrder(pRequest, pResponse);
        final int commerceItemCount = formHandler.getOrder().getCommerceItemCount();
        pRequest.setParameter(BBBCoreConstants.ORDER, formHandler.getOrder());
        displaySingleShipping.service(pRequest, pResponse);
        assertTrue("Eco Fee Items added to Order item count: " + commerceItemCount, commerceItemCount >= 1 );
        assertTrue("Single Shipping Should be allowed ", (Boolean) pRequest.getObjectParameter("isSingle"));
        this.updateItemInOrder(pCatalogRefId, 0);
        this.getRequest().setParameter(BBBCheckoutConstants.SKU_ID, pCatalogRefId);
        final EcoFeeApplicabilityCheckDroplet testEcoFeeDroplet = (EcoFeeApplicabilityCheckDroplet) this.getObject("testEcoFeeDroplet");

        testEcoFeeDroplet.service(this.getRequest(), this.getResponse());
        assertTrue("Should be EcoFee sku, " + pCatalogRefId, (Boolean) this.getRequest().getObjectParameter("isEcoFee"));

        // Test Scenario 1
        // Add one online product with quantity 2

        pCatalogRefId = (String)this.getObject("catalogRefId");
        String pProductId = (String)this.getObject("productId");
        long pQuantity = 2;



        if((formHandler.getItems() != null) && (formHandler.getItems()[0] != null)){
            formHandler.getItems()[0].setProductId(pProductId);
            formHandler.getItems()[0].setCatalogRefId(pCatalogRefId);
            formHandler.getItems()[0].setQuantity(pQuantity);

        }
        formHandler.handleAddItemToOrder(pRequest, pResponse);



        BBBCommerceItem bbbItem = (BBBCommerceItem)formHandler.getOrder().getCommerceItems().get(0);

        assertFalse("Error occoured in Add item to Order",formHandler.getFormError());
        assertEquals("CommerceItemCount should be 1", 1, formHandler.getOrder().getCommerceItemCount());
        assertEquals("SkuId is not matching", pCatalogRefId, bbbItem.getCatalogRefId());
        assertEquals("ProductId is not matching", pProductId, bbbItem.getAuxiliaryData().getProductId());
        assertEquals("qty is not matching", pQuantity, bbbItem.getQuantity());

        assertEquals("There should be only one default HardGoodShippingGroup", 1, formHandler.getOrder().getShippingGroupCount());



        formHandler.setShippingGroup(sg);


        sg.setShippingMethod(BBBCoreConstants.SHIP_METHOD_STANDARD_ID);
        formHandler.getOrder().getPriceInfo().setAmount(49);
        ((CommerceItem)formHandler.getOrder().getCommerceItems().get(0)).getPriceInfo().setAmount(49);
        ((DetailedItemPriceInfo)((CommerceItem)formHandler.getOrder().getCommerceItems().get(0)).getPriceInfo().getCurrentPriceDetails().get(0)).setAmount(49);

        final String promoId = "promo90032";
        final RepositoryItem[] promotions = this.getPromotion(promoId);

        final BBBPricingTools bbbPricingTools = (BBBPricingTools)this.getObject("bbbPricingTools");
        final PricingModelHolder pricingModelHolder = (PricingModelHolder)this.getObject("bbbPricingModelHolder");


        pricingModelHolder.setShippingPricingModels(Arrays.asList(promotions));
        java.util.HashMap parameters = new java.util.HashMap();
        bbbPricingTools.priceShippingForOrderTotal(formHandler.getOrder(), pricingModelHolder, formHandler.getUserLocale(), formHandler.getProfile(), parameters);
        assertTrue("Order priceInfo is less than 100 ",formHandler.getOrder().getPriceInfo().getAmount()<100);
        assertTrue("SG PriceInfo is zero ",sg.getPriceInfo().getAmount()>0);

        //bbbPricingTools.priceShippingForOrderTotal(formHandler.getOrder(), pricingModelHolder, formHandler.getUserLocale(), formHandler.getProfile(), parameters);

        //assertTrue("After applying promotion SG amount should be still nonZero  ",((ShippingGroup)formHandler.getOrder().getShippingGroups().get(0)).getPriceInfo().getAmount()>0);
        assertNull("There are no Shipping Closeness Qualifier ", sg.getPriceInfo().getClosenessQualifiers());
        //
        //      //add GC item
        //      /*addItemToOrder("sku60239", "prod60097", 4);
        //      assertTrue("Order priceInfo is more than 100 ", formHandler.getOrder().getPriceInfo().getAmount() > 100);
        //      //Threshhold did not count Gift Cards
        //      bbbPricingTools.priceShippingForOrderTotal(formHandler.getOrder(), pricingModelHolder, formHandler.getUserLocale(), formHandler.getProfile(), parameters);
        //      assertTrue("After applying promotion SG amount should be still nonZero  ",((ShippingGroup)formHandler.getOrder().getShippingGroups().get(0)).getPriceInfo().getAmount()>0);
        //  
        //      //update qty to reach order's threshold
        formHandler.getOrder().getPriceInfo().setAmount(85);
        ((CommerceItem)formHandler.getOrder().getCommerceItems().get(0)).getPriceInfo().setAmount(85);
        ((DetailedItemPriceInfo)((CommerceItem)formHandler.getOrder().getCommerceItems().get(0)).getPriceInfo().getCurrentPriceDetails().get(0)).setAmount(85);
        parameters.clear();
        bbbPricingTools.priceShippingForOrderTotal(formHandler.getOrder(), pricingModelHolder, formHandler.getUserLocale(), formHandler.getProfile(), parameters);
        assertNotNull("There is no Shipping Closeness Qualifier ", sg.getPriceInfo().getClosenessQualifiers());


        formHandler.getOrder().getPriceInfo().setAmount(151);
        ((CommerceItem)formHandler.getOrder().getCommerceItems().get(0)).getPriceInfo().setAmount(151);
        ((DetailedItemPriceInfo)((CommerceItem)formHandler.getOrder().getCommerceItems().get(0)).getPriceInfo().getCurrentPriceDetails().get(0)).setAmount(151);
        parameters.clear();
        bbbPricingTools.getShippingPricingEngine().priceShippingGroup(formHandler.getOrder(), sg, Arrays.asList(promotions), formHandler.getUserLocale(), formHandler.getProfile(), parameters);
        //
        assertTrue("After applying promotion SG amount should be Zero  ",((ShippingGroup)formHandler.getOrder().getShippingGroups().get(0)).getPriceInfo().getAmount() == 0);


        //Test pack N hold
        bbbItem.setBts(true);

        //This will verify the BBBCheckoutManager class methods
        final BBBCheckoutManager checkoutMangerObject = (BBBCheckoutManager) this.getObject("bbbCheckoutManger");
        //assertTrue("should qualify for single shipping address", checkoutMangerObject.displaySingleShipping(formHandler.getOrder()));
        assertTrue("should qualify for pack and hold", checkoutMangerObject.hasAllPackNHoldItems(formHandler.getSiteId(), formHandler.getOrder()));

        bbbItem.setBts(false);
        assertFalse("should not qualify for pack and hold", checkoutMangerObject.hasAllPackNHoldItems(formHandler.getSiteId(), formHandler.getOrder()));
        final BBBAddressVO testAddress = new BBBAddressVO();
        testAddress.setState("AL");
        assertTrue("should qualify for state shipping address", checkoutMangerObject.canItemShipToAddress(formHandler.getSiteId(), formHandler.getOrder().getCommerceItems(), testAddress));
        assertTrue("should qualify for shipping method", checkoutMangerObject.canItemShipByMethod(formHandler.getSiteId(), formHandler.getOrder().getCommerceItems(), BBBCoreConstants.SHIP_METHOD_STANDARD_ID));

        testAddress.setFirstName("testFirstname");
        testAddress.setLastName("testLastname");
        testAddress.setCity("testCity");
        testAddress.setPostalCode("testPostalCode");
        testAddress.setAddress1("testAddress1");

        testShippingGroupForm.setSiteId(formHandler.getSiteId());
        testShippingGroupForm.setShipToAddressName("true");//this will create a new address
        testShippingGroupForm.setAddress(testAddress);
        testShippingGroupForm.setShippingOption(BBBCoreConstants.SHIP_METHOD_STANDARD_ID);

        testShippingGroupForm.handleAddShipping(pRequest, pResponse);
        final List<BBBAddress> shippinggroupAddresses = checkoutMangerObject.getShippinggroupAddresses(testShippingGroupForm.getOrder());
        assertTrue("One shipping group address not found", shippinggroupAddresses.size() == 1);
        assertTrue("ShippingGroup address does not match with input address", testAddress.equals(shippinggroupAddresses.get(0)));
        assertFalse("New address to the order failed form error", testShippingGroupForm.getFormError());

        testShippingGroupForm.setPackNHold(true);
        testShippingGroupForm.setPackNHoldDate("12/14/2100");
        testShippingGroupForm.handleAddShipping(pRequest, pResponse);
        assertTrue("Should return error not pack and hold", testShippingGroupForm.getFormError());
        testShippingGroupForm.resetFormExceptions();

        testShippingGroupForm.setPackNHold(false);
        testShippingGroupForm.setPackNHoldDate("12/14/2100");
        testShippingGroupForm.handleAddShipping(pRequest, pResponse);
        assertFalse("Should not return any error for false pack and hold", testShippingGroupForm.getFormError());

        if( formHandler.getOrder().getShippingGroups() != null){
            if( (formHandler.getOrder().getShippingGroups().get(0) != null) && (formHandler.getOrder().getShippingGroups().get(0) instanceof HardgoodShippingGroup)) {
                ;
            }
            final HardgoodShippingGroup hgSG = (HardgoodShippingGroup)formHandler.getOrder().getShippingGroups().get(0);

            assertEquals("There should be onbly one CI relationship ##  ", 1, hgSG.getCommerceItemRelationshipCount());
            if( (hgSG.getCommerceItemRelationships() != null) && (hgSG.getCommerceItemRelationships().get(0) != null)){
                final CommerceItemRelationship ciRel = (CommerceItemRelationship)hgSG.getCommerceItemRelationships().get(0);

                assertEquals("CI relationship quantity should be 2", 2, ciRel.getQuantity());
            }
        }




        // Test Scenario 2
        // Add one more online product with quantity 1
        pQuantity = 1;
        if((formHandler.getItems() != null) && (formHandler.getItems()[0] != null)){
            formHandler.getItems()[0].setProductId(pProductId);
            formHandler.getItems()[0].setCatalogRefId(pCatalogRefId);
            formHandler.getItems()[0].setQuantity(pQuantity);

        }

        formHandler.handleAddItemToOrder(pRequest, pResponse);

        assertEquals("There should be only one default HardGoodShippingGroup", 1, formHandler.getOrder().getShippingGroupCount());

        if( formHandler.getOrder().getShippingGroups() != null){
            if( (formHandler.getOrder().getShippingGroups().get(0) != null) && (formHandler.getOrder().getShippingGroups().get(0) instanceof HardgoodShippingGroup)) {
                ;
            }
            final HardgoodShippingGroup hgSG = (HardgoodShippingGroup)formHandler.getOrder().getShippingGroups().get(0);

            assertEquals("There should be onbly one CI relationship", 1, hgSG.getCommerceItemRelationshipCount());
            if( (hgSG.getCommerceItemRelationships() != null) && (hgSG.getCommerceItemRelationships().get(0) != null)){
                final CommerceItemRelationship ciRel = (CommerceItemRelationship)hgSG.getCommerceItemRelationships().get(0);

                assertEquals("CI relationship quantity should be 3", 3, ciRel.getQuantity());
            }
        }

        // Test Scenario 3
        // Add one Junk online product with quantity 1
        
		pQuantity = 1;
		if(formHandler.getItems() != null && formHandler.getItems()[0] != null){
			formHandler.getItems()[0].setProductId("junkproductid");
			formHandler.getItems()[0].setCatalogRefId("junkskuid");
			formHandler.getItems()[0].setQuantity(pQuantity);

		}
		result = formHandler.handleAddItemToOrder(pRequest, pResponse);
		assertTrue("Error should occour in Add item to Order for junk data",formHandler.getFormError());

		formHandler.resetFormExceptions();
         
        // Test Scenario 4
        // Add one store product with quantity 1

        final String pStoreId1 = (String)this.getObject("storeId1");

        pQuantity = 1;
        final int initialNonBopusItems = formHandler.getOrder().getCommerceItemCount();
        if((formHandler.getItems() != null) && (formHandler.getItems()[0] != null)){
            formHandler.getItems()[0].setProductId(pProductId);
            formHandler.getItems()[0].setCatalogRefId(pCatalogRefId);
            formHandler.getItems()[0].setQuantity(pQuantity);
            formHandler.getItems()[0].getValue().put("storeId", pStoreId1);
        }

        formHandler.handleAddItemToOrder(pRequest, pResponse);
        pRequest.setParameter(BBBCoreConstants.ORDER, formHandler.getOrder());
        displaySingleShipping.service(pRequest, pResponse);
        assertFalse("Single Shipping Should not be allowed ", (Boolean) pRequest.getObjectParameter("isSingle"));
        int hgSGCount = 0;
        int storeSGCount = 0;
        if(formHandler.getFormError()){
            assertFalse("form error occured " + formHandler.getFormExceptions(), false);
            formHandler.resetFormExceptions();
        }else{
            assertEquals("There should be new StoreShippingGroup created along with the HardGoodShippingGroup", 2, formHandler.getOrder().getShippingGroupCount());

            assertEquals("CommerceItem count should be 2 ", initialNonBopusItems + 1, formHandler.getOrder().getCommerceItemCount());
            final BBBBopusQualifyingFilter bopusFilter = (BBBBopusQualifyingFilter) this.getObject("bbbBopusFilter");
            final List list = new ArrayList();
            final Iterator itemIterator = formHandler.getOrder().getCommerceItems().iterator();
            while (itemIterator.hasNext()) {
                final CommerceItem item = (CommerceItem) itemIterator.next();
                final FilteredCommerceItem filteredItem = new FilteredCommerceItem(item);
                list.add(filteredItem);
            }

            bopusFilter.filterItems(1, null, null, null, null, list);
            assertEquals("The filtered items should not have store pickup items", initialNonBopusItems, list.size());
            for(final Object sgObj : formHandler.getOrder().getShippingGroups()){
                if(sgObj instanceof HardgoodShippingGroup){
                    hgSGCount++;
                }else if( sgObj instanceof BBBStoreShippingGroup){
                    storeSGCount ++;
                }
            }

            assertEquals("HardGoodShippingGroup count should be 1 ", 1, hgSGCount);
            assertEquals("StoreShippingGroup count should be 1 ", 1, storeSGCount);
        }



        // Test Scenario 4
        // Add one more store product with quantity 1

        final String pStoreId2 = (String)this.getObject("storeId2");
        pQuantity = 1;

        if((formHandler.getItems() != null) && (formHandler.getItems()[0] != null)){
            formHandler.getItems()[0].setProductId(pProductId);
            formHandler.getItems()[0].setCatalogRefId(pCatalogRefId);
            formHandler.getItems()[0].setQuantity(pQuantity);
            formHandler.getItems()[0].getValue().put("storeId", pStoreId2);
        }

        formHandler.handleAddItemToOrder(pRequest, pResponse);

        if(formHandler.getFormError()){
            assertFalse("form error occured " + formHandler.getFormExceptions(), false);
            formHandler.resetFormExceptions();
        }else{

            assertEquals("There should be new StoreShippingGroup created along with the HardGoodShippingGroup and old StoreShippingGroup ", 3, formHandler.getOrder().getShippingGroupCount());

            assertEquals("CommerceItem count should be 3 ", initialNonBopusItems + 2, formHandler.getOrder().getCommerceItemCount());

            hgSGCount = 0;
            storeSGCount = 0;

            for(final Object sgObj : formHandler.getOrder().getShippingGroups()){
                if(sgObj instanceof HardgoodShippingGroup){
                    hgSGCount++;
                }else if( sgObj instanceof BBBStoreShippingGroup){
                    storeSGCount ++;
                }
            }

            assertEquals("HardGoodShippingGroup count should be 1 ", 1, hgSGCount);
            assertEquals("StoreShippingGroup count should be 2 ", 2, storeSGCount);
        }

        // Test Scenario 4
        // Add one registry product with quantity 1

        final String registryId = (String)this.getObject("registryId");
        pQuantity = 1;

        // Removed unused setter method 12/03/2012
        	GiftRegistryManager registryManager = formHandler.getRegistryManager();
        registryManager.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));

        if((formHandler.getItems() != null) && (formHandler.getItems()[0] != null)){
            formHandler.getItems()[0].setProductId(pProductId);
            formHandler.getItems()[0].setCatalogRefId(pCatalogRefId);
            formHandler.getItems()[0].setQuantity(pQuantity);
            formHandler.getItems()[0].getValue().put("registryId", registryId);
            formHandler.getItems()[0].getValue().put("storeId", "");
        }

        formHandler.handleAddItemToOrder(pRequest, pResponse);
        final BBBShippingAddressDroplet addressDroplet = (BBBShippingAddressDroplet) this.getObject("bbbShippingAddressDroplet");

        if(formHandler.getFormError()){
            assertTrue("form error occured " + formHandler.getFormExceptions(), false);
            formHandler.resetFormExceptions();
        }else{
            this.getRequest().setParameter(BBBCoreConstants.PROFILE, formHandler.getProfile());
            this.getRequest().setParameter(BBBCoreConstants.ORDER, formHandler.getOrder());
            this.getRequest().setParameter("addressContainer", this.getRequest().resolveName("/com/bbb/commerce/checkout/ShippingAddContainer"));
            addressDroplet.service(this.getRequest(), this.getResponse());

            testShippingGroupForm.setShipToAddressName(BBBCheckoutConstants.REGISTRY_SOURCE+registryId);
            testShippingGroupForm.handleAddShipping(pRequest, pResponse);
            assertNotNull("Registry Shipping group not found", ((BBBShippingGroupManager) testShippingGroupForm.getShippingGroupManager()).getRegistryPickupShippingGroup(registryId, formHandler.getOrder()));
            assertEquals("There should be 1 HardGoodShippingGroup and 2 StoreShippingGroup ", 3, formHandler.getOrder().getShippingGroupCount());
            assertEquals("CommerceItem count should be 4 ", initialNonBopusItems + 3, formHandler.getOrder().getCommerceItemCount());

        }
        BBBOrder.OrderType.BOPUS.toString();
        BBBOrder.OrderType.ONLINE.toString();
        BBBOrder.OrderType.HYBRID.toString();


        // Test Scenario 5
        // Add one more store product with same storeId and quantity 1

        pQuantity = 1;

        if((formHandler.getItems() != null) && (formHandler.getItems()[0] != null)){
            formHandler.getItems()[0].setProductId(pProductId);
            formHandler.getItems()[0].setCatalogRefId(pCatalogRefId);
            formHandler.getItems()[0].setQuantity(pQuantity);
            formHandler.getItems()[0].getValue().put("storeId", pStoreId2);
            formHandler.getItems()[0].getValue().put("registryId", "");
        }

        formHandler.handleAddItemToOrder(pRequest, pResponse);

        if(formHandler.getFormError()){
            assertFalse("form error occured " + formHandler.getFormExceptions(), false);
            formHandler.resetFormExceptions();
        }else{
            assertEquals("There should be 1 HardGoodShippingGroup and 2 StoreShippingGroup ", 3, formHandler.getOrder().getShippingGroupCount());

            assertEquals("CommerceItem count should be 6 ", initialNonBopusItems + 3, formHandler.getOrder().getCommerceItemCount());

        }

        final List pOrderPricingModels = new ArrayList();
        final String orderPromoIds = (String) this.getObject("orderPromos");
        final StringTokenizer promoIds = new StringTokenizer(orderPromoIds);
        for(;promoIds.hasMoreTokens();) {
            final String id = promoIds.nextToken();
            pOrderPricingModels.add(this.getPromotion(id)[0]);
        }
        pQuantity = 3;
        parameters = new HashMap();
        BBBOrderPriceInfo results = (BBBOrderPriceInfo) bbbPricingTools.getOrderPricingEngine().priceOrder(formHandler.getOrder(), pOrderPricingModels, formHandler.getUserLocale(), formHandler.getProfile(), parameters );
        pOrderPricingModels.remove(0);
        assertTrue("Only one promotions should have applied", results.getAdjustments().size() == 2);
        parameters.clear();
        this.updateItemInOrder(pCatalogRefId, pQuantity );
        results = (BBBOrderPriceInfo) bbbPricingTools.getOrderPricingEngine().priceOrder(formHandler.getOrder(), pOrderPricingModels, formHandler.getUserLocale(), formHandler.getProfile(), parameters);
        assertTrue("Only one promotions should have applied", results.getAdjustments().size() == 2);
        pOrderPricingModels.remove(0);
        parameters.clear();
        this.updateItemInOrder(pCatalogRefId, pQuantity);
        results = (BBBOrderPriceInfo) bbbPricingTools.getOrderPricingEngine().priceOrder(formHandler.getOrder(), pOrderPricingModels, formHandler.getUserLocale(), formHandler.getProfile(), parameters);
        pOrderPricingModels.remove(0);
        parameters.clear();
        assertTrue("Three $off promotions should have applied", results.getAdjustments().size() == 4);
        this.updateItemInOrder(pCatalogRefId, pQuantity);
        results = (BBBOrderPriceInfo) bbbPricingTools.getOrderPricingEngine().priceOrder(formHandler.getOrder(), pOrderPricingModels, formHandler.getUserLocale(), formHandler.getProfile(), parameters);
        pOrderPricingModels.remove(0);
        parameters.clear();
        assertTrue("Only one promotions should have applied", results.getAdjustments().size() == 2);
        this.updateItemInOrder(pCatalogRefId, pQuantity);
        results = (BBBOrderPriceInfo) bbbPricingTools.getOrderPricingEngine().priceOrder(formHandler.getOrder(), pOrderPricingModels, formHandler.getUserLocale(), formHandler.getProfile(), parameters);
        assertTrue("Three $off promotions should have applied", results.getAdjustments().size() == 4);

        // increase the quantity so that all the promotion can be applied
        parameters.clear();
        pQuantity = 30;
        this.updateItemInOrder(pCatalogRefId, pQuantity);
        results = (BBBOrderPriceInfo) bbbPricingTools.getOrderPricingEngine().priceOrder(formHandler.getOrder(), pOrderPricingModels, formHandler.getUserLocale(), formHandler.getProfile(), parameters);
        assertTrue("All the $off promotions should have  applied", results.getAdjustments().size() == 13);

        final GetApplicableShippingMethodsDroplet shippingMethodDroplet =
                (GetApplicableShippingMethodsDroplet) this.getObject(DROPLET);
        String operationParam = BBBCoreConstants.PER_ORDER;

        // Setting request parameters
        this.getRequest().setParameter(BBBCoreConstants.ORDER,
                formHandler.getOrder());
        this.getRequest().setParameter(BBBCoreConstants.OPERATION, operationParam);

        // Calling droplet service method
        shippingMethodDroplet.service(pRequest, pResponse);

        // Getting shipping methods from request
        List<ShipMethodVO> shipMethodVOList = (List<ShipMethodVO>) pRequest
                .getObjectParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST);

        assertTrue("Number of shipping methods not matching", shipMethodVOList
                .size() == 3);
        operationParam = BBBCoreConstants.PER_SKU;
        pCatalogRefId = (String) this.getObject("catalogRefId");



         Start -  code to test BBBGiftCardFilter 
        final BBBGiftCardFilter bbbGiftCardFilter = (BBBGiftCardFilter) this.getObject("bbbGiftCardFilter");
        List<FilteredCommerceItem> filterItemList = new ArrayList<FilteredCommerceItem>();
        List<CommerceItem> commerceItemList = formHandler.getOrder().getCommerceItems();

        //add item to filter commerce list
        for (final CommerceItem commerceItem : commerceItemList) {
            final FilteredCommerceItem filteredItem = new FilteredCommerceItem(commerceItem);
            filterItemList.add(filteredItem);
        }
        assertEquals("CommerceItem count before filtering should be 1 ", 1, filterItemList.size());

        //create pricing context
        PricingContext pricingContext = this.getPricingContext();
        pricingContext.setOrder(formHandler.getOrder());

        //call filter to filter-out gift card items
        bbbGiftCardFilter.filterItems(1, pricingContext, null, null, null, filterItemList);

        assertEquals("The filtered items should not have giftcard items", 1, filterItemList.size());

          End  - code to test BBBGiftCardFilter 

        // Setting request parameters
        this.getRequest().setParameter(BBBCoreConstants.ORDER,
                formHandler.getOrder());
        this.getRequest().setParameter(BBBCoreConstants.OPERATION, operationParam);

        // Calling droplet service method
        shippingMethodDroplet.service(pRequest, pResponse);

        // Getting shipping methods from request
        HashMap<String, List<ShipMethodVO>> shipMethodVOMap = (HashMap<String, List<ShipMethodVO>>) pRequest
                .getObjectParameter(BBBCoreConstants.SKU_MEHOD_MAP);

        final List<ShipMethodVO> shipMethodVOListForSku = shipMethodVOMap
                .get(pCatalogRefId);

        assertTrue("Number of shipping methods not matching",
                shipMethodVOListForSku.size() == 3);

        // Setting request parameters
        this.getRequest().setParameter(BBBCoreConstants.ORDER, null);

        // Calling droplet service method
        shippingMethodDroplet.service(pRequest, pResponse);

        // Getting shipping methods from request
        shipMethodVOMap = (HashMap<String, List<ShipMethodVO>>) pRequest
                .getObjectParameter(BBBCoreConstants.SKU_MEHOD_MAP);

        assertNull("No shipping methods found", shipMethodVOMap);

        this.getRequest().setParameter(BBBCoreConstants.ORDER,
                formHandler.getOrder());

        // setting wrong operation param
        this.getRequest().setParameter(BBBCoreConstants.OPERATION, BBBCoreConstants.OPERATION);

        // Calling droplet service method
        shippingMethodDroplet.service(pRequest, pResponse);


        // Getting shipping methods from request
        shipMethodVOList = (List<ShipMethodVO>) pRequest
                .getObjectParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST);

        assertNull("No shipping methods found", shipMethodVOList);

         Start -  code to test BBBGiftCardFilter 
        BBBCIAttributesBasedFilter bbbCIAttributesBasedFilter = (BBBCIAttributesBasedFilter) this.getObject("bbbCIAttributesBasedFilter");
        filterItemList = new ArrayList<FilteredCommerceItem>();
        commerceItemList = formHandler.getOrder().getCommerceItems();

        //add item to filter commerce list
        for (final CommerceItem commerceItem : commerceItemList) {
            final FilteredCommerceItem filteredItem = new FilteredCommerceItem(commerceItem);
            filterItemList.add(filteredItem);
        }

        //create pricing context
        pricingContext = this.getPricingContext();
        pricingContext.setOrder(formHandler.getOrder());

        //call filter to filter-out gift card items
        bbbCIAttributesBasedFilter.filterItems(1, pricingContext, null, null, null, filterItemList);

        assertEquals("The filtered items count should be 1", 1, filterItemList.size());

        bbbCIAttributesBasedFilter = (BBBCIAttributesBasedFilter) this.getObject("bbbCIAttributesBasedFilter");
        filterItemList = new ArrayList<FilteredCommerceItem>();

        //create pricing context
        pricingContext = this.getPricingContext();

        try{
            //call filter to filter-out gift card items
            bbbCIAttributesBasedFilter.filterItems(1, pricingContext, null, null, null, filterItemList);

        }catch(final Exception e){
            assertFalse("Exception occured while filtering items for promotions", true);
        }

        formHandler.setAddItemCount(1);

        // Test Scenario 1
        // Add one online product with quantity 2

        pCatalogRefId = (String)this.getObject("catalogRefId");
        pProductId = (String)this.getObject("productId");
        pQuantity = 2;

        if((formHandler.getItems() != null) && (formHandler.getItems()[0] != null)){
            formHandler.getItems()[0].setProductId(pProductId);
            formHandler.getItems()[0].setCatalogRefId(pCatalogRefId);
            formHandler.getItems()[0].setQuantity(pQuantity);
            //formHandler.setSiteId("BuyBuyBaby");
        }
        formHandler.handleAddItemToOrder(pRequest, pResponse);

        final BBBPriceDisplayDroplet priceDisplayDroplet = (BBBPriceDisplayDroplet) this.getObject("bbbPriceDisplayDroplet");
        //BBBCommerceItemManager bbbManager = (BBBCommerceItemManager)getObject("bbbCommerceItemManager");


        bbbItem = (BBBCommerceItem)formHandler.getOrder().getCommerceItems().get(0);
        assertNotNull(bbbItem);




        pRequest.setParameter(PARAM_PRICE_OBJECT, bbbItem);
        priceDisplayDroplet.service(pRequest, pResponse);

        PriceInfoVO priceInfoVO = (PriceInfoVO)pRequest.getObjectParameter(OUTPUT_PARAM_PRICEINFOVO);

        assertEquals("Item quantity should match", priceInfoVO.getUndiscountedItemsCount(), bbbItem.getQuantity());
        assertTrue("No discounts should be applied", priceInfoVO.getTotalSavedAmount() < 0.001);

        pRequest.setParameter(PARAM_PRICE_OBJECT, formHandler.getOrder());
        priceDisplayDroplet.service(pRequest, pResponse);
        priceInfoVO = (PriceInfoVO)pRequest.getObjectParameter(OUTPUT_PARAM_PRICEINFOVO);

        assertEquals("No store total should be applied", priceInfoVO.getTotalSavedAmount(), 0.0);
        assertEquals("order online total should be match", priceInfoVO.getOnlinePurchaseTotal(), ((BBBOrderPriceInfo) formHandler.getOrder().getPriceInfo()).getOnlineSubtotal());
        System.out.println(((BBBShippingPriceInfo)((HardgoodShippingGroup)formHandler.getOrder().getShippingGroups().get(0)).getPriceInfo()).getFinalShipping());
            System.out.println(((BBBShippingPriceInfo)((HardgoodShippingGroup)formHandler.getOrder().getShippingGroups().get(0)).getPriceInfo()).getFinalSurcharge());
            System.out.println(((BBBShippingPriceInfo)((HardgoodShippingGroup)formHandler.getOrder().getShippingGroups().get(0)).getPriceInfo()).getAmount());

        final HardgoodShippingGroup hardgoodShippingGroup = (HardgoodShippingGroup)formHandler.getOrder().getShippingGroups().get(0);
        final double shippingAmount = ((BBBShippingPriceInfo)hardgoodShippingGroup.getPriceInfo()).getFinalShipping();
        pRequest.setParameter(PARAM_PRICE_OBJECT, hardgoodShippingGroup);
        priceDisplayDroplet.service(pRequest, pResponse);
        priceInfoVO = (PriceInfoVO)pRequest.getObjectParameter(OUTPUT_PARAM_PRICEINFOVO);
        assertTrue(Math.abs(priceInfoVO.getRawShippingTotal() - shippingAmount) < 0.001);
        final ShippingMethodDroplet droplet = (ShippingMethodDroplet) this.getObject("shippingMetDrop");
        this.getRequest().setParameter("requestType", "ShippingMethodDetails");
        droplet.service(pRequest, pResponse);
        Map dropResults = (Map) pRequest.getObjectParameter("ShippingMethodDetails");
        assertNotNull("ShippingPriceTableDetail not returned", dropResults);
        assertTrue("ShippingPriceTableDetail not returned", dropResults.size() > 0);
        dropResults.clear();
        this.getRequest().setParameter("requestType", "ShippingPriceTableDetail");
        this.getRequest().setParameter("siteId", "BedBathUS");
        droplet.service(pRequest, pResponse);
        dropResults = (Map) pRequest.getObjectParameter("ShippingPriceTableDetail");
        assertNotNull("ShippingPriceTableDetail not returned", dropResults);
        assertTrue("ShippingPriceTableDetail not returned", dropResults.size() > 0);
        formHandler.getOrder().removeAllCommerceItems();

    }*/
    
    
    
    public void testHandleAddItemtoOrderLtlSku() throws Exception{

        final BBBCartFormhandler formHandler = (BBBCartFormhandler)this.getObject("bbbCartFormHandler");
        final DisplaySingleShippingDroplet displaySingleShipping = (DisplaySingleShippingDroplet)this.getObject("bbbDisplaySingleShipping");

        final DynamoHttpServletRequest pRequest =  this.getRequest();
        final DynamoHttpServletResponse pResponse = this.getResponse();

        final BBBShippingGroupFormhandler testShippingGroupForm = (BBBShippingGroupFormhandler) this.getObject("bbbShippingGroupHandler");

        //Add all parametrs in profile level
        final MutableRepositoryItem profileItem = (MutableRepositoryItem)formHandler.getProfile();

        final String siteId = "BedBathUS";
        ServletUtil.setCurrentRequest(pRequest);
		SiteContextManager siteContextManager = (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext
					.getBBBSiteContext(siteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
        RequestLocale rl = new RequestLocale();
		rl.setLocale(new Locale("en_US"));
		getRequest().setRequestLocale(rl);
		
        //MutableRepositoryItem profileItem = (MutableRepositoryItem) profileTool.getItemFromEmail("andrew@example.com");

        final PriceListManager priceListManager = (PriceListManager)this.getObject("bbbPriceListManager");
        final String listPriceId = (String)this.getObject("listPriceId");
        final String salePriceId = (String)this.getObject("salePriceId");
        final RepositoryItem listPriceListItem = priceListManager.getPriceList(listPriceId);
        final RepositoryItem salePriceListItem = priceListManager.getPriceList(salePriceId);
        profileItem.setPropertyValue("priceList", listPriceListItem);
        profileItem.setPropertyValue("salePriceList", salePriceListItem);


        final CustomCatalogTools catalogTools = (CustomCatalogTools)this.getObject("bbbCatalogTools");
        final List catalogIds = new ArrayList();
        final String catalogId = (String)this.getObject("catalogId");
        catalogIds.add(catalogId);
        if(catalogTools.getCatalogsForRepository(null, catalogIds) != null){
            final RepositoryItem catalogItem = catalogTools.getCatalogsForRepository(null, catalogIds)[0];
            profileItem.setPropertyValue("catalog", catalogItem);

        }
        formHandler.setProfile(profileItem);
        final String pSiteId = "BedBathUS";
        formHandler.setSiteId(pSiteId);

        //Test logic for DisplayShippingRestrictionsDroplet Droplet

        System.out.println("Shipping Restrictions");
        formHandler.setAddItemCount(1);
        HardgoodShippingGroup sg = (HardgoodShippingGroup)formHandler.getOrder().getShippingGroups().get(0);

        formHandler.setShippingGroup(sg);
        String pCatalogRefId = (String)this.getObject("catalogRefIdZip");
        if((formHandler.getItems() != null) && (formHandler.getItems()[0] != null)){
            final String pProductId = (String)this.getObject("productIdZip");
            formHandler.getItems()[0].setProductId(pProductId);
            formHandler.getItems()[0].setCatalogRefId(pCatalogRefId);
            formHandler.getItems()[0].setQuantity(2);
            formHandler.setSiteId("BedBathUS");
            formHandler.getItems()[0].getValue().put(BBBCatalogConstants.LTL_SHIP_METHOD, BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD);
            formHandler.getItems()[0].getValue().put(BBBCatalogConstants.WHITE_GLOVE_ASSEMLBY, BBBCatalogConstants.TRUE);
            
            
           
        }
     /*   if((formHandler.getItems() != null) &&(formHandler.getItems()[0] != null))
        {	 final String pProductId = (String)this.getObject("productIdZip");
        	 formHandler.getItems()[1].setProductId(pProductId);
             formHandler.getItems()[1].setCatalogRefId(pCatalogRefId);
             formHandler.getItems()[1].setQuantity(1);
             formHandler.setSiteId("BedBathUS");
             formHandler.getItems()[1].getValue().put(BBBCatalogConstants.LTL_SHIP_METHOD, "lw");
             formHandler.getItems()[1].getValue().put(BBBCatalogConstants.WHITE_GLOVE_ASSEMLBY, "false");
             
        }*/
        formHandler.handleAddItemToOrder(pRequest, pResponse);

        pRequest.setParameter(BBBCoreConstants.ORDER, formHandler.getOrder());

        //Test logic
        formHandler.setAddItemCount(1);
        sg = (HardgoodShippingGroup)formHandler.getOrder().getShippingGroups().get(0);
        sg.getShippingAddress().setState((String) this.getObject("stateIdeco"));

        formHandler.setShippingGroup(sg);
        pCatalogRefId = (String)this.getObject("catalogRefIdeco");
        if((formHandler.getItems() != null) && (formHandler.getItems()[0] != null)){
            final String pProductId = (String)this.getObject("productIdeco");
            formHandler.getItems()[0].setProductId(pProductId);
            formHandler.getItems()[0].setCatalogRefId(pCatalogRefId);
            formHandler.getItems()[0].setQuantity(2);
            formHandler.setSiteId("BedBathUS");
        }
        formHandler.handleAddItemToOrder(pRequest, pResponse);
        final int commerceItemCount = formHandler.getOrder().getCommerceItemCount();
        pRequest.setParameter(BBBCoreConstants.ORDER, formHandler.getOrder());
        displaySingleShipping.service(pRequest, pResponse);
        assertTrue("Eco Fee Items added to Order item count: " + commerceItemCount, commerceItemCount >= 1 );
        assertTrue("Single Shipping Should be allowed ", (Boolean) pRequest.getObjectParameter("isSingle"));
        this.updateItemInOrder(pCatalogRefId, 0);
        this.getRequest().setParameter(BBBCheckoutConstants.SKU_ID, pCatalogRefId);
        final EcoFeeApplicabilityCheckDroplet testEcoFeeDroplet = (EcoFeeApplicabilityCheckDroplet) this.getObject("testEcoFeeDroplet");

        testEcoFeeDroplet.service(this.getRequest(), this.getResponse());
        assertTrue("Should be EcoFee sku, " + pCatalogRefId, (Boolean) this.getRequest().getObjectParameter("isEcoFee"));

        // Test Scenario 1
        // Add one online product with quantity 2

        pCatalogRefId = (String)this.getObject("catalogRefId");
        String pProductId = (String)this.getObject("productId");
        long pQuantity = 2;



        if((formHandler.getItems() != null) && (formHandler.getItems()[0] != null)){
            formHandler.getItems()[0].setProductId(pProductId);
            formHandler.getItems()[0].setCatalogRefId(pCatalogRefId);
            formHandler.getItems()[0].setQuantity(pQuantity);

        }
        formHandler.handleAddItemToOrder(pRequest, pResponse);
        testShippingGroupForm.getShippingGroupManager().removeEmptyShippingGroups(formHandler.getOrder());

        BBBCommerceItem bbbItem = (BBBCommerceItem)formHandler.getOrder().getCommerceItems().get(1);

        assertFalse("Error occoured in Add item to Order",formHandler.getFormError());
        assertEquals("CommerceItemCount should be 2", 2, formHandler.getOrder().getCommerceItemCount());
        assertEquals("SkuId is not matching", pCatalogRefId, bbbItem.getCatalogRefId());
        assertEquals("ProductId is not matching", pProductId, bbbItem.getAuxiliaryData().getProductId());
        assertEquals("qty is not matching", pQuantity, bbbItem.getQuantity());

        assertEquals("There should be only one default HardGoodShippingGroup", 1, formHandler.getOrder().getShippingGroupCount());

        formHandler.setShippingGroup(sg);

        sg.setShippingMethod(BBBCoreConstants.SHIP_METHOD_STANDARD_ID);
        formHandler.getOrder().getPriceInfo().setAmount(49);
        ((CommerceItem)formHandler.getOrder().getCommerceItems().get(0)).getPriceInfo().setAmount(49);
        ((DetailedItemPriceInfo)((CommerceItem)formHandler.getOrder().getCommerceItems().get(0)).getPriceInfo().getCurrentPriceDetails().get(0)).setAmount(49);

        final String promoId = "promo90032";
        final RepositoryItem[] promotions = this.getPromotion(promoId);

        final BBBPricingTools bbbPricingTools = (BBBPricingTools)this.getObject("bbbPricingTools");
        final PricingModelHolder pricingModelHolder = (PricingModelHolder)this.getObject("bbbPricingModelHolder");


        pricingModelHolder.setShippingPricingModels(Arrays.asList(promotions));
        java.util.HashMap parameters = new java.util.HashMap();
        bbbPricingTools.priceShippingForOrderTotal(formHandler.getOrder(), pricingModelHolder, formHandler.getUserLocale(), formHandler.getProfile(), parameters);
        assertTrue("Order priceInfo is less than 100 ",formHandler.getOrder().getPriceInfo().getAmount()<100);

        //bbbPricingTools.priceShippingForOrderTotal(formHandler.getOrder(), pricingModelHolder, formHandler.getUserLocale(), formHandler.getProfile(), parameters);

        //assertTrue("After applying promotion SG amount should be still nonZero  ",((ShippingGroup)formHandler.getOrder().getShippingGroups().get(0)).getPriceInfo().getAmount()>0);
        assertNull("There are no Shipping Closeness Qualifier ", sg.getPriceInfo().getClosenessQualifiers());
        //
        //      //add GC item
        //      /*addItemToOrder("sku60239", "prod60097", 4);
        //      assertTrue("Order priceInfo is more than 100 ", formHandler.getOrder().getPriceInfo().getAmount() > 100);
        //      //Threshhold did not count Gift Cards
        //      bbbPricingTools.priceShippingForOrderTotal(formHandler.getOrder(), pricingModelHolder, formHandler.getUserLocale(), formHandler.getProfile(), parameters);
        //      assertTrue("After applying promotion SG amount should be still nonZero  ",((ShippingGroup)formHandler.getOrder().getShippingGroups().get(0)).getPriceInfo().getAmount()>0);
        //  */
        //      //update qty to reach order's threshold
        formHandler.getOrder().getPriceInfo().setAmount(85);
        ((CommerceItem)formHandler.getOrder().getCommerceItems().get(0)).getPriceInfo().setAmount(85);
        ((DetailedItemPriceInfo)((CommerceItem)formHandler.getOrder().getCommerceItems().get(0)).getPriceInfo().getCurrentPriceDetails().get(0)).setAmount(85);
        parameters.clear();
        bbbPricingTools.priceShippingForOrderTotal(formHandler.getOrder(), pricingModelHolder, formHandler.getUserLocale(), formHandler.getProfile(), parameters);
        assertNull("There is no Shipping Closeness Qualifier ", sg.getPriceInfo().getClosenessQualifiers());


        formHandler.getOrder().getPriceInfo().setAmount(151);
        ((CommerceItem)formHandler.getOrder().getCommerceItems().get(0)).getPriceInfo().setAmount(151);
        ((DetailedItemPriceInfo)((CommerceItem)formHandler.getOrder().getCommerceItems().get(0)).getPriceInfo().getCurrentPriceDetails().get(0)).setAmount(151);
        parameters.clear();
        bbbPricingTools.getShippingPricingEngine().priceShippingGroup(formHandler.getOrder(), sg, Arrays.asList(promotions), formHandler.getUserLocale(), formHandler.getProfile(), parameters);

        
        // Test Scenario 2
        // Add one more online product with quantity 1
        pQuantity = 1;
        if((formHandler.getItems() != null) && (formHandler.getItems()[0] != null)){
            formHandler.getItems()[0].setProductId(pProductId);
            formHandler.getItems()[0].setCatalogRefId(pCatalogRefId);
            formHandler.getItems()[0].setQuantity(pQuantity);

        }

        formHandler.handleAddItemToOrder(pRequest, pResponse);

        assertEquals("There should be only one default HardGoodShippingGroup", 1, formHandler.getOrder().getShippingGroupCount());

        if( formHandler.getOrder().getShippingGroups() != null){
            if( (formHandler.getOrder().getShippingGroups().get(0) != null) && (formHandler.getOrder().getShippingGroups().get(0) instanceof HardgoodShippingGroup)) {
                ;
            }
            final HardgoodShippingGroup hgSG = (HardgoodShippingGroup)formHandler.getOrder().getShippingGroups().get(0);

            assertEquals("There should be onbly one CI relationship", 2, hgSG.getCommerceItemRelationshipCount());
        }
    }

    private boolean updateItemInOrder (final String pCatalogRefId, final long pQuantity) throws Exception{
        final BBBCartFormhandler formHandler = (BBBCartFormhandler)this.getObject("bbbCartFormHandler");

        for (final Object object: formHandler.getOrder().getCommerceItems()) {
            if(object instanceof BBBCommerceItem){
                final BBBCommerceItem commerceItem = (BBBCommerceItem)object;
                if(pCatalogRefId.equalsIgnoreCase(commerceItem.getCatalogRefId()) ){
                    formHandler.setCommerceItemId(commerceItem.getId());
                    if(pQuantity == 0) {
                        formHandler.setRemovalCatalogRefIds(new String[1]);
                        formHandler.getRemovalCatalogRefIds()[0] = commerceItem.getId();
                    }
                    this.getRequest().setParameter(commerceItem.getId(), pQuantity);
                    formHandler.handleSetOrderByCommerceId(this.getRequest(), this.getResponse());
                    formHandler.getOrderManager().updateOrder(formHandler.getOrder());
                    return true;
                }
            }
        }

        return false;

    }

    private RepositoryItem[] getPromotion(final String promotionId)
            throws RepositoryException{

        final MutableRepository promoRep = (MutableRepository)this.getObject("bbbPromotionRepository");
        final String promotionQuery = "id=?0";

        final Object[] params = new Object[1];
        params[0]=promotionId;




        final RqlStatement statement = RqlStatement.parseRqlStatement(promotionQuery);
        final RepositoryView view = promoRep.getView(BBBCatalogConstants.PROMOTION_ITEM_DESCRIPTOR);
        return statement.executeQuery(view, params);
    }

    /*
     * Test the quantity update method
     */
    public void testSetOrder() throws Exception {

        final BBBCartFormhandler formHandler = (BBBCartFormhandler)this.getObject("bbbCartFormHandler");
        final DynamoHttpServletRequest pRequest =  this.getRequest();
        final DynamoHttpServletResponse pResponse = this.getResponse();
        ServletUtil.setCurrentRequest(pRequest);
		final String siteId = "BedBathUS";
		SiteContextManager siteContextManager = (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext
					.getBBBSiteContext(siteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
        formHandler.resetFormExceptions();
        formHandler.getOrder().removeAllCommerceItems();
        //Add all parametrs in profile level
        final MutableRepositoryItem profileItem = (MutableRepositoryItem)formHandler.getProfile();
        this.getRequest().resolveName("/atg/userprofiling/ProfileTools");

        //profileItem = (MutableRepositoryItem) profileTool.getItemFromEmail("raj@example.com");

        final PriceListManager priceListManager = (PriceListManager)this.getObject("bbbPriceListManager");
        final String listPriceId = (String)this.getObject("listPriceId");
        final String salePriceId = (String)this.getObject("salePriceId");
        final RepositoryItem listPriceListItem = priceListManager.getPriceList(listPriceId);
        final RepositoryItem salePriceListItem = priceListManager.getPriceList(salePriceId);
        profileItem.setPropertyValue("priceList", listPriceListItem);
        profileItem.setPropertyValue("salePriceList", salePriceListItem);

        formHandler.setProfile(profileItem);
        formHandler.setSiteId("BedBathUS");
        //Test logic
        formHandler.setAddItemCount(1);

        // Test Scenario 1
        // Add one online product with quantity 2

        final String pCatalogRefId = (String)this.getObject("catalogRefId");
        final String pProductId = (String)this.getObject("productId");
        final long pQuantity = 2;



        if((formHandler.getItems() != null) && (formHandler.getItems()[0] != null)){
            formHandler.getItems()[0].setProductId(pProductId);
            formHandler.getItems()[0].setCatalogRefId(pCatalogRefId);
            formHandler.getItems()[0].setQuantity(pQuantity);

        }
        formHandler.handleAddItemToOrder(pRequest, pResponse);
        System.out.println("Quantity Before: " + ((BBBCommerceItem)formHandler.getOrder().getCommerceItems().get(0)).getQuantity());

        assertEquals("There should be 2 Quantity ", 2, ((BBBCommerceItem)formHandler.getOrder().getCommerceItems().get(0)).getQuantity());

        final String commerceItemId = ((BBBCommerceItem)formHandler.getOrder().getCommerceItems().get(0)).getId();
        formHandler.setCommerceItemId(commerceItemId);
        pRequest.setParameter(commerceItemId, 10);
        formHandler.handleSetOrderByCommerceId(pRequest, pResponse);
        System.out.println("Quantity After: " + ((BBBCommerceItem)formHandler.getOrder().getCommerceItems().get(0)).getQuantity());

        assertEquals("There should be 10 Quantity ", 10, ((BBBCommerceItem)formHandler.getOrder().getCommerceItems().get(0)).getQuantity());

        //Commented the scenario since there is no such requirement of max limit of items in BRD
        /*
		pRequest.setParameter(commerceItemId, 100);
		formHandler.handleSetOrderByCommerceId(pRequest, pResponse);
		//System.out.println("formHandler exception "+ formHandler.getFormExceptions().get(0).toString());

		//creating error message "atg.droplet.DropletException: sku40041err_cartdetail_invalidQuantity"
		StringBuilder sb = new StringBuilder("atg.droplet.DropletException: ");
		sb.append(pCatalogRefId);
		sb.append("err_cartdetail_invalidQuantity");

		assertTrue("Exception not Raised For Invalid Quantity ", formHandler.getFormError());

         */

    }


    /*
     * Test the addItemsFromCookie method
     */
    public void testAddItemsFromCookie() throws Exception {

        final BBBCartFormhandler formHandler = (BBBCartFormhandler)this.getObject("bbbCartFormHandler");
        final DynamoHttpServletRequest pRequest =  this.getRequest();
        final DynamoHttpServletResponse pResponse = this.getResponse();
        ServletUtil.setCurrentRequest(pRequest);
		final String siteId = "BedBathUS";
		SiteContextManager siteContextManager = (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext
					.getBBBSiteContext(siteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
        formHandler.resetFormExceptions();
        
        final MutableRepositoryItem profileItem = (MutableRepositoryItem)formHandler.getProfile();
        
        formHandler.getShoppingCart().getOrderManager().removeOrder(formHandler.getOrder().getId());
        formHandler.setOrder(formHandler.getShoppingCart().getOrderManager().createOrder(profileItem.getRepositoryId()));
        
        formHandler.getOrder().removeAllCommerceItems();
        //Add all parametrs in profile level
        this.getRequest().resolveName("/atg/userprofiling/ProfileTools");

        //profileItem = (MutableRepositoryItem) profileTool.getItemFromEmail("raj@example.com");

        final PriceListManager priceListManager = (PriceListManager)this.getObject("bbbPriceListManager");
        final String listPriceId = (String)this.getObject("listPriceId");
        final String salePriceId = (String)this.getObject("salePriceId");
        final RepositoryItem listPriceListItem = priceListManager.getPriceList(listPriceId);
        final RepositoryItem salePriceListItem = priceListManager.getPriceList(salePriceId);
        profileItem.setPropertyValue("priceList", listPriceListItem);
        profileItem.setPropertyValue("salePriceList", salePriceListItem);

        formHandler.setProfile(profileItem);
        formHandler.setSiteId("BedBathUS");
        //Test logic
        final AddCommerceItemInfo[] itemInfoArray = new AddCommerceItemInfo[3];

        // Test Scenario
        // Add 3 items out of which one skuID is invalid and 1 storeId is invalid
        final String pProductId = (String)this.getObject("productId");

        final String invalidSkuId = (String)this.getObject("invalidSkuId");
        itemInfoArray[0] = new AddCommerceItemInfo();
        itemInfoArray[0].setCatalogRefId(invalidSkuId);
        itemInfoArray[0].setProductId(pProductId);
        itemInfoArray[0].setQuantity(1);

        final String skuId = (String)this.getObject("skuId");
        final String invalidStoreId = (String)this.getObject("invalidStoreId");

        itemInfoArray[1] = new AddCommerceItemInfo();
        itemInfoArray[1].setCatalogRefId(skuId);
        itemInfoArray[1].setProductId(pProductId);
        itemInfoArray[1].getValue().put("storeId", invalidStoreId);
        itemInfoArray[1].setQuantity(1);

        itemInfoArray[2] = new AddCommerceItemInfo();
        itemInfoArray[2].setCatalogRefId(skuId);
        itemInfoArray[2].setProductId(pProductId);
        itemInfoArray[2].setQuantity(1);


        formHandler.addItemsFromCookie(itemInfoArray,
                pRequest, pResponse);

        assertEquals("There should 1 commerce Item in art ", 1, formHandler.getOrder().getCommerceItems().size());

    }
    
    /**
     * author asi162
     * 
     * Test the handleCheckoutWithPaypal from Cart Page
     * 
     * @throws IOException 
     * @throws ServletException 
     * @throws RepositoryException 
     * @throws BBBSystemException 
     * @throws CommerceException 
     * 
     */
    
	public void testhandleCheckoutWithPaypalfromCart() throws ServletException,
			IOException, RepositoryException, BBBSystemException, CommerceException {

		/**
		 * This 3 line snippet adds header to the request.
		 */
		GenericHttpServletRequest genericHttpServletRequest = new GenericHttpServletRequest();
		genericHttpServletRequest.setHeader(BBBCoreConstants.HOST,
				"localhost:7003");
		ServletUtil.getDynamoRequest(this.getRequest()).setRequest(
				genericHttpServletRequest);
		final DynamoHttpServletRequest pRequest = this.getRequest();
		final DynamoHttpServletResponse pResponse = this.getResponse();
		ServletUtil.setCurrentRequest(pRequest);
		final String siteId = "BedBathUS";
		SiteContextManager siteContextManager = (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext
					.getBBBSiteContext(siteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		
		final BBBCartFormhandler bbbCartFormhandler = (BBBCartFormhandler) this
				.getObject("bbbCartFormHandler");
		final BBBProfileFormHandler bbbProfileFormhandler = (BBBProfileFormHandler) this.getObject("bbbProfileFormHandler");
		bbbCartFormhandler.resetFormExceptions();
		
		final MutableRepositoryItem profileItem = (MutableRepositoryItem) bbbCartFormhandler
				.getProfile();
		
		bbbCartFormhandler.getShoppingCart().getOrderManager().removeOrder(bbbCartFormhandler.getOrder().getId());
		bbbCartFormhandler.setOrder(bbbCartFormhandler.getShoppingCart().getOrderManager().createOrder(profileItem.getRepositoryId()));
		bbbProfileFormhandler.setOrder(bbbCartFormhandler.getOrder());
		OrderHolder holder = (OrderHolder) this.getObject("shoppingCart");
		holder.setCurrent(bbbCartFormhandler.getOrder());
		
		final PriceListManager priceListManager = (PriceListManager) this
				.getObject("bbbPriceListManager");
		final String listPriceId = (String) this.getObject("listPriceId");
		final String salePriceId = (String) this.getObject("salePriceId");
		final RepositoryItem listPriceListItem = priceListManager
				.getPriceList(listPriceId);
		final RepositoryItem salePriceListItem = priceListManager
				.getPriceList(salePriceId);

		

		pRequest.setContextPath("/store");
		pRequest.setRequestURI("/store/cart/cart.jsp");
		pRequest.setServletPath("/cart/cart.jsp");

		profileItem.setPropertyValue("priceList", listPriceListItem);
		profileItem.setPropertyValue("salePriceList", salePriceListItem);

		bbbCartFormhandler.setAddItemCount(1);
		HardgoodShippingGroup sg = (HardgoodShippingGroup) bbbCartFormhandler
				.getOrder().getShippingGroups().get(0);

		bbbCartFormhandler.setShippingGroup(sg);
		String pCatalogRefId = (String) this.getObject("catalogRefIdZip");
		if ((bbbCartFormhandler.getItems() != null)
				&& (bbbCartFormhandler.getItems()[0] != null)) {
			final String pProductId = (String) this.getObject("productIdZip");
			bbbCartFormhandler.getItems()[0].setProductId(pProductId);
			bbbCartFormhandler.getItems()[0].setCatalogRefId(pCatalogRefId);
			bbbCartFormhandler.getItems()[0].setQuantity(2);
			bbbCartFormhandler.setSiteId("BedBathUS");
		}
		if ( null != bbbCartFormhandler.getOrder().getTaxPriceInfo()){
			bbbCartFormhandler.getOrder().getTaxPriceInfo().setCurrencyCode("USD");
		}
		bbbCartFormhandler.setProfile(profileItem);
		/**
		 * adding item to the order
		 */
		bbbCartFormhandler.handleAddItemToOrder(pRequest, pResponse);

		bbbCartFormhandler.setFromCart(true);
		bbbCartFormhandler.setSiteId("BedBathUS");
		
		/**
		 * calling checkout with paypal
		 */
		bbbCartFormhandler.handleCheckoutWithPaypal(pRequest, pResponse);
		if(bbbCartFormhandler.getErrorMap() == null || bbbCartFormhandler.getErrorMap().size() == 0){
			assertTrue(bbbCartFormhandler.getPaypalToken() != null);
		}
		else{
			assertNotNull(bbbCartFormhandler.getErrorMap());
		}

	}
    
	/**
	 * Test the handleCheckoutWithPaypal from Billing Page
	 * 
	 * @throws ServletException
	 * @throws IOException
	 * @throws RepositoryException
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws CommerceException 
	 */
	public void testhandleCheckoutWithPaypalfromBilling()
			throws ServletException, IOException, RepositoryException, BBBSystemException, BBBBusinessException, CommerceException {

		/**
		 * This 3 line snippet sets header in the request
		 */
		GenericHttpServletRequest genericHttpServletRequest = new GenericHttpServletRequest();
		genericHttpServletRequest.setHeader(BBBCoreConstants.HOST,
				"localhost:7003");
		ServletUtil.getDynamoRequest(this.getRequest()).setRequest(
				genericHttpServletRequest);
		final DynamoHttpServletRequest pRequest = this.getRequest();
		final DynamoHttpServletResponse pResponse = this.getResponse();
		ServletUtil.setCurrentRequest(pRequest);
		final String siteId = "BedBathUS";
		SiteContextManager siteContextManager = (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext
					.getBBBSiteContext(siteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		final BBBCartFormhandler bbbCartFormhandler = (BBBCartFormhandler) this
				.getObject("bbbCartFormHandler");
		final BBBProfileFormHandler bbbProfileFormhandler = (BBBProfileFormHandler) this.getObject("bbbProfileFormHandler");
		bbbCartFormhandler.resetFormExceptions();
		
		final MutableRepositoryItem profileItem = (MutableRepositoryItem) bbbCartFormhandler
				.getProfile();
		
		bbbCartFormhandler.getShoppingCart().getOrderManager().removeOrder(bbbCartFormhandler.getOrder().getId());
		bbbCartFormhandler.setOrder(bbbCartFormhandler.getShoppingCart().getOrderManager().createOrder(profileItem.getRepositoryId()));
		bbbProfileFormhandler.setOrder(bbbCartFormhandler.getOrder());
		OrderHolder holder = (OrderHolder) this.getObject("shoppingCart");
		holder.setCurrent(bbbCartFormhandler.getOrder());
		
		final PriceListManager priceListManager = (PriceListManager) this
				.getObject("bbbPriceListManager");
		final String listPriceId = (String) this.getObject("listPriceId");
		final String salePriceId = (String) this.getObject("salePriceId");
		final RepositoryItem listPriceListItem = priceListManager
				.getPriceList(listPriceId);
		final RepositoryItem salePriceListItem = priceListManager
				.getPriceList(salePriceId);
		pRequest.setRequestURI("/store/cart/cart.jsp");
		pRequest.setServletPath("/cart/cart.jsp");

		/**
		 * adding item to order
		 */

		profileItem.setPropertyValue("priceList", listPriceListItem);
		profileItem.setPropertyValue("salePriceList", salePriceListItem);

		System.out.println("Shipping Restrictions");
		bbbCartFormhandler.setAddItemCount(1);
		HardgoodShippingGroup sg = (HardgoodShippingGroup) bbbCartFormhandler
				.getOrder().getShippingGroups().get(0);

		bbbCartFormhandler.setShippingGroup(sg);
		String pCatalogRefId = (String) this.getObject("catalogRefIdZip");
		if ((bbbCartFormhandler.getItems() != null)
				&& (bbbCartFormhandler.getItems()[0] != null)) {
			final String pProductId = (String) this.getObject("productIdZip");
			bbbCartFormhandler.getItems()[0].setProductId(pProductId);
			bbbCartFormhandler.getItems()[0].setCatalogRefId(pCatalogRefId);
			bbbCartFormhandler.getItems()[0].setQuantity(2);
			bbbCartFormhandler.setSiteId("BedBathUS");
		}
		if ( null != bbbCartFormhandler.getOrder().getTaxPriceInfo()){
			bbbCartFormhandler.getOrder().getTaxPriceInfo().setCurrencyCode("USD");
		}
		bbbCartFormhandler.setProfile(profileItem);

		bbbCartFormhandler.handleAddItemToOrder(pRequest, pResponse);
		// ADD to cart ENDS

		/**
		 * Adding Shipping Address to Order
		 */

		final BBBHardGoodShippingGroup hardgoodShippingGroup = (BBBHardGoodShippingGroup) (sg);
		final BBBRepositoryContactInfo atgShippingAddress = (BBBRepositoryContactInfo) hardgoodShippingGroup
				.getShippingAddress();
		atgShippingAddress.setFirstName("Test");
		atgShippingAddress.setLastName("user");
		atgShippingAddress.setAddress1("30 West Monroe");
		atgShippingAddress.setAddress2("12th Floor");
		atgShippingAddress.setCity("Chicago");
		atgShippingAddress.setState("IL");
		atgShippingAddress.setPostalCode("60603");
		hardgoodShippingGroup.setShippingAddress(atgShippingAddress);
		
		/**
		 * calling checkout with paypal
		 */

		bbbCartFormhandler.handleCheckoutWithPaypal(pRequest, pResponse);
		if(bbbCartFormhandler.getErrorMap() == null || bbbCartFormhandler.getErrorMap().size() == 0){
			assertTrue(bbbCartFormhandler.getPaypalToken() != null);
		}
		else{
			assertNotNull(bbbCartFormhandler.getErrorMap());
		}

	}  
    

    /**
     * Create Pricing Context.
     * 
     * @return pricingContext
     */
    private PricingContext getPricingContext(){

        final PricingContextFactory pcFactory = new PricingContextFactory();
        final PricingContext pricingContext = pcFactory.createPricingContext();

        pricingContext.setPricingModel(new RepositoryItem() {

            @Override
            public String getItemDisplayName() {
                return null;
            }

            @Override
            public boolean isTransient() {
                return false;
            }

            @Override
            public String getRepositoryId() {
                return null;
            }

            @Override
            public Repository getRepository() {
                return null;
            }

            @Override
            public Object getPropertyValue(final String s) {
                return "bbbCoupons";
            }

            @Override
            public RepositoryItemDescriptor getItemDescriptor()
                    throws RepositoryException {
                final RepositoryItemDescriptor repoItem = new RepositoryItemDescriptor() {

                    @Override
                    public boolean isInstance(final Object obj) {
                        // TODO Auto-generated method stub
                        return false;
                    }

                    @Override
                    public boolean hasProperty(final String s) {
                        // TODO Auto-generated method stub
                        return false;
                    }

                    @Override
                    public String[] getPropertyNames() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public DynamicPropertyDescriptor[] getPropertyDescriptors() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public DynamicPropertyDescriptor getPropertyDescriptor(final String s) {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public DynamicBeanDescriptor getBeanDescriptor() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public boolean areInstances(final DynamicBeanInfo dynamicbeaninfo) {
                        // TODO Auto-generated method stub
                        return false;
                    }

                    @Override
                    public void updatePropertyDescriptor(final RepositoryPropertyDescriptor repositorypropertydescriptor) throws RepositoryException {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void removePropertyDescriptor(final String s) throws RepositoryException {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public boolean isContextMembershipEnabled() {
                        // TODO Auto-generated method stub
                        return false;
                    }

                    @Override
                    public boolean hasCompositeKey() {
                        // TODO Auto-generated method stub
                        return false;
                    }

                    @Override
                    public RepositoryView getRepositoryView() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public Repository getRepository() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public String getItemDescriptorName() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public RepositoryPropertyDescriptor getDisplayNameProperty() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public RepositoryPropertyDescriptor getContextMembershipProperty() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public String encodeCompositeKey(final String[] as) throws RepositoryException {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public void addPropertyDescriptor(final RepositoryPropertyDescriptor repositorypropertydescriptor) throws RepositoryException {
                        // TODO Auto-generated method stub

                    }
                };
                return repoItem;
            }

            @Override
            public Collection<String> getContextMemberships()
                    throws RepositoryException {
                return null;
            }
        });


        return pricingContext;
    }
    public void testNotZero() throws Exception {
        final NotZero notZero = (NotZero) this.getObject("notZeroDroplet");
        final DynamoHttpServletRequest request = this.getRequest();
        final DynamoHttpServletResponse response = this.getResponse();
        request.setParameter("Price", this.getObject("salePrice"));
        notZero.service(request, response);
    }
    public void testZero() throws Exception {
        final NotZero notZero = (NotZero) this.getObject("notZeroDroplet");
        final DynamoHttpServletRequest request = this.getRequest();
        final DynamoHttpServletResponse response = this.getResponse();
        request.setParameter("Price", this.getObject("salePrice"));
        notZero.service(request, response);
    }
}
