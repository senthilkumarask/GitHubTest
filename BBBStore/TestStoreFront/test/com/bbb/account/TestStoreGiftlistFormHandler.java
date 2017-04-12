package com.bbb.account;



import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import atg.commerce.catalog.custom.CustomCatalogTools;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.RequestLocale;
import atg.servlet.ServletUtil;
import atg.userprofiling.ProfileTools;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.order.BBBCommerceItemManager;
import com.bbb.commerce.order.formhandler.BBBShippingGroupFormhandler;
import com.bbb.commerce.order.purchase.BBBCartFormhandler;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.wishlist.BBBWishlistItemCountDroplet;
import com.bbb.wishlist.BBBWishlistSiteFilterDroplet;
import com.bbb.wishlist.StoreGiftlistFormHandler;
import com.sapient.common.tests.BaseTestCase;

public class TestStoreGiftlistFormHandler extends BaseTestCase
{
    public void testStoreGiftlistFormHandler() throws Exception{

        final BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) this.getObject("bbbProfileFormHandler");
        bbbProfileFormHandler.setExpireSessionOnLogout(false);
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);
        
        if(!bbbProfileFormHandler.getProfile().isTransient()){
            bbbProfileFormHandler.handleLogout(this.getRequest(), this.getResponse());
        }
        bbbProfileFormHandler.setLoggingDebug(true);
        bbbProfileFormHandler.setLoggingInfo(true);
        bbbProfileFormHandler.setLoggingError(true);
        bbbProfileFormHandler.getShoppingCart().getCurrent().setProfileId("cc");
        this.getRequest().setParameter("userCheckingOut","asdf");
        final DynamoHttpServletRequest req = this.getRequest();
        final DynamoHttpServletResponse res = this.getResponse();
        System.out.println("TestBBBProfileFormHandler.testHandleLogin.getFormExceptions()" + bbbProfileFormHandler.getFormExceptions());
        assertTrue(bbbProfileFormHandler.getProfile().isTransient());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size() == 0);

        //getRequest().setParameter("BBBProfileFormHandler", bbbProfileFormHandler);

        //BBBProfileTools bbbProfileTools = (BBBProfileTools) getObject("bbbProfileTools");
        //getRequest().setParameter("BBBProfileFormHandler", bbbProfileFormHandler);
        final BBBProfileManager manager = (BBBProfileManager)this.getObject("bbbProfileManager");
        final String pSiteId = (String) this.getObject("siteId");
        bbbProfileFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
        atg.servlet.ServletUtil.setCurrentRequest(this.getRequest());
        final String email = (String) this.getObject("email");
        final String password = (String) this.getObject("password");
        manager.updatePassword(email, "Sapient!2");
        String siteContextSiteId = (String) this.getObject("siteId");
		SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(siteContextSiteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);
        bbbProfileFormHandler.getValue().put("login", email);
        bbbProfileFormHandler.getValue().put("password", password);
        bbbProfileFormHandler.handleLogin(this.getRequest(), this.getResponse());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size()==0);
        assertFalse(bbbProfileFormHandler.getProfile().isTransient());
        bbbProfileFormHandler.getProfileTools().setLoggingDebug(true);
        bbbProfileFormHandler.getProfileManager().setLoggingDebug(true);

        final StoreGiftlistFormHandler storeGiftlistFormHandler = (StoreGiftlistFormHandler) this.getObject("bbbStoreGiftlistFormHandler");
        storeGiftlistFormHandler.setJsonResultString("{\"addItemResults\":[{\"skuId\":\"sku40069\",\"prodId\":\"prod10013\",\"qty\":\"1\",\"price\":\"\"}],\"parentProdId\":\"1013690456\"}");
        storeGiftlistFormHandler.setProfile(bbbProfileFormHandler.getProfile());
        storeGiftlistFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
        storeGiftlistFormHandler.handleAddItemListToGiftlist(this.getRequest(), this.getResponse());
        assertTrue(storeGiftlistFormHandler.getFormExceptions().size()==0);
        final String giftId = storeGiftlistFormHandler.getGiftlistId();
        final List list = storeGiftlistFormHandler.getGiftlistManager().getGiftlistItems(giftId);
        final RepositoryItem item = (RepositoryItem)list.get(0);
        storeGiftlistFormHandler.setCurrentItemId(item.getRepositoryId());
        storeGiftlistFormHandler.setQuantity(5);
        storeGiftlistFormHandler.handleUpdateGiftlistItems(this.getRequest(), this.getResponse());
        assertTrue(storeGiftlistFormHandler.getFormExceptions().size()==0);
        final BBBCommerceItemManager bbbManager = (BBBCommerceItemManager)this.getObject("bbbCommerceItemManager");
        final String giftListId = storeGiftlistFormHandler.getGiftlistId();
        final String catalogRefId  = (String)this.getObject("catalogRefId");
        final String productId = (String)this.getObject("productId");
        final String qty = (String)this.getObject("qty");
        final int intQty = Integer.parseInt(qty);
        final BBBCommerceItem pExistingItem = (BBBCommerceItem)bbbManager.createCommerceItem(catalogRefId, productId, intQty);
        storeGiftlistFormHandler.setItemIds(new String[]{pExistingItem.getId()});
        final BBBWishlistItemCountDroplet bbbWishlistItemCountDroplet = (BBBWishlistItemCountDroplet) this.getObject("bbbWishlistItemCountDroplet");
        req.setParameter("wishlistId", giftListId);
        req.setParameter("siteId","BedBathUS");
        bbbWishlistItemCountDroplet.service(req,res);
        assertNull(bbbWishlistItemCountDroplet.getParameter("wishlistItemCount"));
        final BBBWishlistSiteFilterDroplet bbbWishlistSiteFilterDroplet = (BBBWishlistSiteFilterDroplet) this.getObject("bbbWishlistSiteFilterDroplet");
        bbbWishlistSiteFilterDroplet.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
        req.setParameter("giftItemId","123");
        req.setParameter("siteId","123");
        bbbWishlistSiteFilterDroplet.service(req,res);
        req.setParameter("giftItemId",null);
        bbbWishlistSiteFilterDroplet.service(req,res);
        assertNull(bbbWishlistSiteFilterDroplet.getParameter("output"));

        storeGiftlistFormHandler.handleMoveItemsFromCart(this.getRequest(), this.getResponse());
        assertTrue(storeGiftlistFormHandler.getFormExceptions().size()!=0);
        storeGiftlistFormHandler.getFormExceptions().clear();
        storeGiftlistFormHandler.setRemoveGiftitemIds( new String[]{storeGiftlistFormHandler.getGiftlistManager().getGiftlistItemId(storeGiftlistFormHandler.getGiftlistId(), "sku40069")});
        storeGiftlistFormHandler.handleRemoveItemsFromGiftlist(this.getRequest(), this.getResponse());
        assertTrue(storeGiftlistFormHandler.getFormExceptions().size()==0);
        storeGiftlistFormHandler.handleCancel(this.getRequest(), this.getResponse());
        assertTrue(storeGiftlistFormHandler.getFormExceptions().size()==0);
        storeGiftlistFormHandler.handleClearForm(this.getRequest(), this.getResponse());
        assertTrue(storeGiftlistFormHandler.getFormExceptions().size()==0);
        bbbProfileFormHandler.handleLogout(this.getRequest(), this.getResponse());
        assertTrue(storeGiftlistFormHandler.getFormExceptions().size()==0);

        storeGiftlistFormHandler.postMoveItemsFromCart(req,res);
        assertTrue(storeGiftlistFormHandler.getFormExceptions().size()==0);
        storeGiftlistFormHandler.getOrder().addCommerceItem(pExistingItem);
        storeGiftlistFormHandler.setItemIds(new String[]{pExistingItem.getId()});
        storeGiftlistFormHandler.setCatalogRefIds(new String[]{pExistingItem.getCatalogRefId()});
        storeGiftlistFormHandler.setProductId(productId);
        storeGiftlistFormHandler.handleMoveItemsFromCart(req,res);
        assertTrue(storeGiftlistFormHandler.getFormExceptions().size()==0);
        storeGiftlistFormHandler.postMoveItemsFromCart(req,res);
        assertTrue(storeGiftlistFormHandler.getFormExceptions().size()==0);
        storeGiftlistFormHandler.getGiftListItemFromSession(req);
        assertTrue(storeGiftlistFormHandler.getFormExceptions().size()==0);
        storeGiftlistFormHandler.setItemIds(new String[]{pExistingItem.getId()});
        storeGiftlistFormHandler.handleAddItemListToGiftlist(req,res);
        assertTrue(storeGiftlistFormHandler.getFormExceptions().size()==0);
        storeGiftlistFormHandler.moveItemsFromCart(req,res);
        assertTrue(storeGiftlistFormHandler.getFormExceptions().size()>=0);
        storeGiftlistFormHandler.getFormExceptions().clear();
        storeGiftlistFormHandler.setUseRequestLocale(false);
        storeGiftlistFormHandler.setLblTxtTemplateManager(null);
        storeGiftlistFormHandler.setAddItemToGiftlistLoginURL(null);

        storeGiftlistFormHandler.getAddItemToGiftlistLoginURL();
        //storeGiftlistFormHandler.getGiftlistIdToValue();
        storeGiftlistFormHandler.getProfilePropertyManager();
        storeGiftlistFormHandler.setRemoveItemsFromGiftlistSuccessURL(null);
        storeGiftlistFormHandler.setRemoveItemsFromGiftlistErrorURL(null);
        storeGiftlistFormHandler.setUpdateGiftlistAndItemsSuccessURL(null);
        storeGiftlistFormHandler.getUpdateGiftlistAndItemsSuccessURL();
        storeGiftlistFormHandler.setUpdateGiftlistAndItemsErrorURL(null);
        storeGiftlistFormHandler.getUpdateGiftlistAndItemsErrorURL();
        storeGiftlistFormHandler.setMoveToNewGiftListAddressSuccessURL(null);
        storeGiftlistFormHandler.getMoveToNewGiftListAddressSuccessURL();

        storeGiftlistFormHandler.setMoveToNewGiftListAddressErrorURL(null);
        storeGiftlistFormHandler.getMoveToNewGiftListAddressErrorURL();
        storeGiftlistFormHandler.setUseWishlist(true);
        storeGiftlistFormHandler.isUseWishlist();
        storeGiftlistFormHandler.setWoodFinishPicker(true);
        storeGiftlistFormHandler.isWoodFinishPicker();
        storeGiftlistFormHandler.isAddWishlistSuccessFlag();
        storeGiftlistFormHandler.getProdList();
        storeGiftlistFormHandler.setProdList(null);
        storeGiftlistFormHandler.getOrder().removeAllCommerceItems();
        storeGiftlistFormHandler.getOmnitureStatus();
        storeGiftlistFormHandler.setOmnitureStatus(null);
        storeGiftlistFormHandler.getOmniProdList();

        storeGiftlistFormHandler.setOmniProdList(null);
        storeGiftlistFormHandler.getLblTxtTemplateManager();


    }

    public void testStoreGiftlistFormHandlerLTL() throws Exception{

    	DynamoHttpServletRequest pRequest =  getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		
		RequestLocale rl = new RequestLocale();
		rl.setLocale(new Locale("en_US"));
		getRequest().setRequestLocale(rl);
        ServletUtil.setCurrentRequest(getRequest());
        
        SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
		atg.servlet.ServletUtil.setCurrentRequest(this.getRequest());
		
        try {
        	siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext("BuyBuyBaby"));
        } catch (SiteContextException siteContextException) {
        	throw new BBBSystemException("Exception" + siteContextException);
}
        
        BBBCartFormhandler formHandler = (BBBCartFormhandler)this.getObject("bbbCartFormHandler");
		
		//Add all parametrs in profile level
		MutableRepositoryItem profileItem = (MutableRepositoryItem)formHandler.getProfile();		
		ProfileTools profileTool = (ProfileTools) getRequest().resolveName("/atg/userprofiling/ProfileTools");
        
		PriceListManager priceListManager = (PriceListManager)getObject("bbbPriceListManager");
		String listPriceId = (String)getObject("listPriceId");
		String salePriceId = (String)getObject("salePriceId");
		RepositoryItem listPriceListItem = priceListManager.getPriceList(listPriceId);
		RepositoryItem salePriceListItem = priceListManager.getPriceList(salePriceId);
		profileItem.setPropertyValue("priceList", listPriceListItem);
		profileItem.setPropertyValue("salePriceList", salePriceListItem);
		
		CustomCatalogTools catalogTools = (CustomCatalogTools)getObject("bbbCatalogTools");
		List catalogIds = new ArrayList();
		String catalogId = (String)getObject("catalogId");
		catalogIds.add(catalogId);
		if(catalogTools.getCatalogsForRepository(null, catalogIds) != null){
			RepositoryItem catalogItem = catalogTools.getCatalogsForRepository(null, catalogIds)[0];
			profileItem.setPropertyValue("catalog", catalogItem);
		}
		
		formHandler.setProfile(profileItem);
		
		formHandler.setOrder(formHandler.getOrderManager().createOrder(profileItem.getRepositoryId(), formHandler.getOrder().getOrderClassType()));
		//Test logic
		formHandler.setAddItemCount(1);		
		
		// Test Scenario 1
		// Add one online product with quantity 20
		
		String pCatalogRefId = (String)getObject("catalogRefId");	
		String pProductId = (String)getObject("productId");
		long pQuantity = 5;
		
		if(formHandler.getItems() != null && formHandler.getItems()[0] != null){
			formHandler.getItems()[0].setProductId(pProductId);
			formHandler.getItems()[0].setCatalogRefId(pCatalogRefId);
			formHandler.getItems()[0].setQuantity(pQuantity);
			formHandler.getItems()[0].getValue().put(BBBCatalogConstants.LTL_SHIP_METHOD,"LT");
			formHandler.setSiteId("BuyBuyBaby");
		}
		formHandler.handleAddItemToOrder(pRequest, pResponse);
		formHandler.getOrder().getCommerceItems().get(0);
        final StoreGiftlistFormHandler storeGiftlistFormHandler = (StoreGiftlistFormHandler) this.getObject("bbbStoreGiftlistFormHandler");
        final BBBCommerceItemManager bbbManager = (BBBCommerceItemManager)this.getObject("bbbCommerceItemManager");
        final String giftListId = storeGiftlistFormHandler.getGiftlistId();
        
        BBBCommerceItem pExistingItem = (BBBCommerceItem)((BBBCartFormhandler)formHandler).getOrder().getCommerceItemsByCatalogRefId(pCatalogRefId).get(0);
        storeGiftlistFormHandler.setCurrentItemId(pExistingItem.getId());
        storeGiftlistFormHandler.setQuantity(1);
        storeGiftlistFormHandler.setCountNo(1);
        storeGiftlistFormHandler.setMoveItemsFromCartLoginURL("/store/cart/ajax_handler_cart.jsp");
        storeGiftlistFormHandler.setMoveItemsFromCartErrorURL("/store/cart/ajax_handler_cart.jsp");
        storeGiftlistFormHandler.setMoveItemsFromCartSuccessURL("/store/cart/ajax_handler_cart.jsp");	
        pRequest.setParameter("btnSaveForLater", "true");
        storeGiftlistFormHandler.getShoppingCart().setCurrent(formHandler.getOrder());
        storeGiftlistFormHandler.handleMoveItemsFromCart(pRequest, pResponse);
        //storeGiftlistFormHandler.get
        String skuid =  storeGiftlistFormHandler.getSavedItemsSessionBean().getGiftListVO().get(0).getSkuID();
        assertNotNull(skuid);
    }


}
