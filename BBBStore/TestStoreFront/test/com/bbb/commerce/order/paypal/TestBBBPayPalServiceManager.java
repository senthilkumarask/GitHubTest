package com.bbb.commerce.order.paypal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import atg.commerce.pricing.DetailedItemPriceInfo;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.UnitPriceBean;
import atg.core.util.StringUtils;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import atg.userprofiling.Profile;

import com.bbb.account.BBBProfileTools;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.common.BBBRepositoryContactInfo;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.ecommerce.order.BBBOrderTools;
import com.sapient.common.tests.BaseTestCase;

public class TestBBBPayPalServiceManager extends BaseTestCase{
	
	public TestBBBPayPalServiceManager() {
		super();
		// TODO Auto-generated constructor stub
	}	
	
	
	public void testGetPayPalRedirectURL() throws Exception{
		BBBPayPalServiceManager paypalManager = (BBBPayPalServiceManager) this.getObject("paypalManager");		
		String redirectUrl = paypalManager.getPayPalRedirectURL();
		
		 assertTrue("No redirect url is there", !StringUtils.isEmpty(redirectUrl));
		
	}
	
	@SuppressWarnings("unchecked")
	public void testAddressInOrder() throws Exception{
		BBBPayPalServiceManager paypalManager = (BBBPayPalServiceManager) this.getObject("paypalManager");		
		final BBBOrderManager bbbOrderManager = (BBBOrderManager) this.getObject("bbbOrderManager");
		BBBOrderImpl order = (BBBOrderImpl) bbbOrderManager.createOrder((String) this.getObject("orderId"));
		final String sku = (String)this.getObject("sku");
		final BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) this.getObject("catalogTools");
		final RepositoryItem skuItem = catalogTools.getCatalogRepository().getItem(sku, "sku");		
		final Set<RepositoryItem> productItems = (Set<RepositoryItem>) skuItem.getPropertyValue("parentProducts");
		final BBBHardGoodShippingGroup bbbShippingGroup = (BBBHardGoodShippingGroup)order.getShippingGroups().get(0);
		bbbShippingGroup.getShippingAddress().setCity("Alaska");
		bbbShippingGroup.getShippingAddress().setAddress1("1 Alaska");
		final String commerceItemType = ((BBBOrderTools) bbbOrderManager.getOrderTools()).getDefaultCommerceItemType();
        final String productId = productItems.toArray(new RepositoryItem[0])[0].getRepositoryId();
        bbbOrderManager.addAsSeparateItemToShippingGroup(order, bbbShippingGroup, sku, 2, productId, commerceItemType);
        
		boolean addressBool = paypalManager.addressInOrder(order);
		
		assertTrue("No address is there", addressBool);
	}
	
	public void testValidateCoupons() throws Exception{
		BBBPayPalServiceManager paypalManager = (BBBPayPalServiceManager) this.getObject("paypalManager");
		MutableRepository repository = (MutableRepository)resolveName("/atg/commerce/order/OrderRepository");
		MutableRepositoryItem item = repository.createItem("bbbAddress");
		BBBRepositoryContactInfo mBillingAddress = new BBBRepositoryContactInfo(item);
		final BBBOrderManager bbbOrderManager = (BBBOrderManager) this.getObject("bbbOrderManager");
		BBBOrderImpl order = (BBBOrderImpl) bbbOrderManager.createOrder((String) this.getObject("orderId"));
		mBillingAddress.setFirstName((String)getObject("firstName"));
		mBillingAddress.setMiddleName((String)getObject("middleName"));
		mBillingAddress.setLastName((String)getObject("lastName"));
		mBillingAddress.setAddress1((String)getObject("address1"));
		mBillingAddress.setAddress2((String)getObject("address2"));
		mBillingAddress.setCity((String)getObject("city"));
		mBillingAddress.setEmail((String)getObject("email"));
		mBillingAddress.setPhoneNumber((String)getObject("phoneNumber"));
		mBillingAddress.setPostalCode((String)getObject("zipCode"));
		mBillingAddress.setState((String)getObject("state"));
		final BBBProfileTools bbbProfileTools = (BBBProfileTools) this.getObject("bbbProfileTools");
	    final Profile pProfile = new Profile();
	    final RepositoryItem profileItem = bbbProfileTools. getItemFromEmail((String) this.getObject("email"));
	    pProfile.setDataSource(profileItem);
		repository.addItem(item);
		order.setBillingAddress(mBillingAddress);
		order.setSiteId("BedBathUS");
		boolean validate = paypalManager.validateCoupons( order, pProfile);
		
		assertTrue("No coupons exist", !validate);
	}
	
	
	public void testIsTokenExpired() throws Exception{
		BBBPayPalServiceManager paypalManager = (BBBPayPalServiceManager) this.getObject("paypalManager");
		final BBBOrderManager bbbOrderManager = (BBBOrderManager) this.getObject("bbbOrderManager");
		BBBPayPalSessionBean payPalSessionBean = new BBBPayPalSessionBean();
		BBBOrderImpl order = (BBBOrderImpl) bbbOrderManager.createOrder((String) this.getObject("orderId"));		
		order.setTimeStamp(new Date());
		order.setToken("1234");
		boolean validate =  paypalManager.isTokenExpired(  payPalSessionBean,  order);
		
		assertTrue("No coupons exist", !validate);
	}
	

}
