package com.bbb.commerce.order.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import org.apache.xmlbeans.XmlException;

import atg.commerce.catalog.custom.CustomCatalogTools;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PricingTools;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.account.api.BBBAddressVO;
import com.bbb.commerce.order.purchase.BBBCartFormhandler;
import com.bbb.commerce.service.pricing.BBBPricingWSMapper;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bedbathandbeyond.atg.PricingRequestDocument;
import com.sapient.common.tests.BaseTestCase;

public class TestPromotionExclusionMsgDroplet extends BaseTestCase {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testPromotionExclusionMsgDroplet() {

        DynamoHttpServletRequest request = getRequest();
        DynamoHttpServletResponse response = getResponse();

        Profile profile = (Profile) request.resolveName("/atg/userprofiling/Profile");
        PricingRequestDocument wsRequest;
        BBBOrderImpl order = null;
        BBBCartFormhandler formHandler = (BBBCartFormhandler) getObject("bbbCartFormHandler");
        PricingTools pricingTools = formHandler.getPromoTools().getPricingTools();
        try {
            wsRequest = com.bedbathandbeyond.atg.PricingRequestDocument.Factory.parse((String) getObject("xmlAsString"));
        
            order = (BBBOrderImpl) getPricingWSMapper().transformRequestToOrder(wsRequest.getPricingRequest(), profile, new HashMap<String, Object>());          
			/*
			 * Here we collect all global promotion for each type of pricing
			 * engine and later we merge this promotion with request promotion
			 */
			Collection<RepositoryItem> pShippingPricingModels = pricingTools.getShippingPricingEngine()
					.getPricingModels(profile);
			Collection<RepositoryItem> pItemPricingModels = pricingTools.getItemPricingEngine().getPricingModels(
					profile);
			Collection<RepositoryItem> pOrderPricingModels = pricingTools.getOrderPricingEngine().getPricingModels(
					profile);
			/*
			 * Here we extract promotion by promotion id or coupon id according
			 * to the type passed in request.
			 */
			Map<String, Collection<RepositoryItem>> pricingModels = getPricingWSMapper().populatePromotions(order,
					wsRequest.getPricingRequest(), profile);
			/*
			 * Here we merge global, profile promotions and the promotions
			 * present in the pricing service request and pass each type of
			 * promotion list to appropriate pricing engine through
			 * priceOrderSubtotalShipping method of PricingTools class.
			 */
			pShippingPricingModels.addAll(pricingModels.get(BBBCheckoutConstants.SHIPPING_PROMOTIONS));
			pItemPricingModels.addAll(pricingModels.get(BBBCheckoutConstants.ITEM_PROMOTIONS));
			pOrderPricingModels.addAll(pricingModels.get(BBBCheckoutConstants.ORDER_PROMOTIONS));

			pricingTools.priceOrderSubtotalShipping(order, pItemPricingModels, pShippingPricingModels,
					pOrderPricingModels, pricingTools.getDefaultLocale(), profile, new HashMap<String, Object>());
        } catch (XmlException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        } catch (BBBBusinessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BBBSystemException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (PricingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        System.out.println("****** commItems after applying coupons"+order.getCommerceItems());
        assertTrue("No Excluded promotions found  ", order
                .getExcludedPromotionMap().size() > 0);
        /*assertTrue("No Excluded promotions found  ", true);*/
        try {        
            PromotionExclusionMsgDroplet promoExclMsgDroplet = (PromotionExclusionMsgDroplet) getObject("PromotionExclusionMsgDroplet");
            request.setParameter("order", order);
            promoExclMsgDroplet.service(request, response);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Set promotionList = order
                .getExcludedPromotionMap().keySet();
        Map couponMap = order.getCouponMap();
        for (Iterator iterator = promotionList.iterator(); iterator.hasNext();) {
            RepositoryItem promo = (RepositoryItem) iterator.next();
            String couponCode = (String) promo.getPropertyValue(BBBCheckoutConstants.BBBCOUPONS);
            couponMap.put(couponCode, promo);
            
        }
        order.setCouponMap(couponMap);
        Map<String, Set<RepositoryItem>> comItemExclMap = (Map<String, Set<RepositoryItem>>) request
                .getObjectParameter("promoExclusionMap");

        assertNotNull(
                "promoExclusionMap is null from PromotionExclusionMsgDroplet ",
                comItemExclMap);
        UserCouponWalletDroplet userCouponDroplet = (UserCouponWalletDroplet) getObject("userCouponDroplet");
        request.setParameter("profile", profile);
        request.setParameter("site", order.getSiteId());
        try {
            userCouponDroplet.service(request, response);
            String keys = (String) request.getObjectParameter(BBBCoreConstants.KEY);
            assertTrue("No coupons displayed", keys.length() > 0);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*assertNotNull(
        "promoExclusionMap is null from PromotionExclusionMsgDroplet ",
        "Map012#$");*/

    }



    private BBBPricingWSMapper getPricingWSMapper() {
        return (BBBPricingWSMapper) getObject("bbbPriceObject");
    }

    /**
     * 
     * This method creates an order and add a commerce item to it.
     * 
     * @param pRequest
     *            DynamoHttpServletRequest
     * @param pResponse
     *            DynamoHttpServletResponse
     * @return BBBCartFormhandler
     * 
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private BBBCartFormhandler createorderAndAdditem(
            DynamoHttpServletRequest pRequest,
            DynamoHttpServletResponse pResponse) throws Exception {

        BBBCartFormhandler formHandler = (BBBCartFormhandler) getObject("bbbCartFormHandler");
        formHandler.resetFormExceptions();
        /*
        //Add new address to Order's Shipping group
        BBBShippingGroupContainerService bbbSGCS = (BBBShippingGroupContainerService)getObject("bbbSGCS");
        BBBShippingGroupManager bbbSGMgr = (BBBShippingGroupManager)getObject("bbbSGMgr");
        bbbSGMgr.removeAllShippingGroupsFromOrder(formHandler.getOrder());
            BBBAddressVO tempAddrVO = createNewAddr("addr1", "addr2", "fName", "lName");
            Address pAddress = (Address)tempAddrVO;
            String shippingMethod = "Standard";
            BBBHardGoodShippingGroup hgSG = bbbSGMgr.createHardGoodShippingGroup(formHandler.getOrder(), pAddress, shippingMethod);
            String name = ""+new Date().getTime();
            bbbSGCS.addShippingGroup(name, hgSG);
            
        formHandler.getOrderManager().updateOrder(formHandler.getOrder());

        formHandler.resetFormExceptions();*/
        
        // Add all parameters in profile level
        MutableRepositoryItem profileItem = (MutableRepositoryItem) formHandler
                .getProfile();

        PriceListManager priceListManager = (PriceListManager) getObject("bbbPriceListManager");
        String listPriceId = (String) getObject("listPriceId");
        String salePriceId = (String) getObject("salePriceId");
        RepositoryItem listPriceListItem = priceListManager
                .getPriceList(listPriceId);
        RepositoryItem salePriceListItem = priceListManager
                .getPriceList(salePriceId);
        profileItem.setPropertyValue("priceList", listPriceListItem);
        profileItem.setPropertyValue("salePriceList", salePriceListItem);

        CustomCatalogTools catalogTools = (CustomCatalogTools) getObject("catalogTools");
        List catalogIds = new ArrayList();
        String catalogId = (String) getObject("catalogId");
        catalogIds.add(catalogId);
        if (catalogTools.getCatalogsForRepository(null, catalogIds) != null) {
            RepositoryItem catalogItem = catalogTools.getCatalogsForRepository(
                    null, catalogIds)[0];
            profileItem.setPropertyValue("catalog", catalogItem);

        }
        formHandler.setProfile(profileItem);
        formHandler.setSiteId("BedBathUS");

        // Test logic
        formHandler.setAddItemCount(2);

        // Add one online product with quantity 2

        String pCatalogRefId = (String) getObject("catalogRefId");
        String pProductId = (String) getObject("productId");

        if (formHandler.getItems() != null && formHandler.getItems()[0] != null) {
            formHandler.getItems()[0].setProductId(pProductId);
            formHandler.getItems()[0].setCatalogRefId(pCatalogRefId);
            formHandler.getItems()[0].setQuantity(1);
        }

        String excludedCatalogRefId = (String) getObject("excludedCatalogRefId");
        String excludedProductId = (String) getObject("excludedProductId");

        if (formHandler.getItems() != null && formHandler.getItems()[1] != null) {
            formHandler.getItems()[1].setProductId(excludedProductId);
            formHandler.getItems()[1].setCatalogRefId(excludedCatalogRefId);
            formHandler.getItems()[1].setQuantity(1);
        }

        
        // add item to the order
        formHandler.handleAddItemToOrder(pRequest, pResponse);

        return formHandler;
    }
    
    public BBBAddressVO createNewAddr(String addr1, String addr2, String fName, String lName) {
        BBBAddressVO addressVO = new BBBAddressVO();
        addressVO.setAddress1(addr1);
        addressVO.setAddress2(addr2);
        addressVO.setFirstName(fName);
        addressVO.setLastName(lName);
        
        return addressVO;
    }
}
