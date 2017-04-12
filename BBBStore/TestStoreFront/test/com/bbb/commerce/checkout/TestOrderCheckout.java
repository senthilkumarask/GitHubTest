/**
 * 
 */
package com.bbb.commerce.checkout;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletException;

import atg.beans.PropertyNotFoundException;
import atg.commerce.CommerceException;
import atg.commerce.order.CreditCard;
import atg.commerce.order.HardgoodShippingGroup;
import atg.core.util.StringUtils;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.account.BBBProfileFormHandler;
import com.bbb.account.BBBProfileTools;
import com.bbb.account.api.BBBAddressAPI;
import com.bbb.account.api.BBBAddressVO;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.checkout.droplet.BBBBillingAddressDroplet;
import com.bbb.commerce.checkout.formhandler.BBBCommitOrderFormHandler;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.commerce.checkout.tibco.SubmitOrderMarshaller;
import com.bbb.commerce.common.BBBAddressContainer;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.commerce.order.droplet.BBBOrderInfoDroplet;
import com.bbb.commerce.order.purchase.BBBShippingAddressDroplet;
import com.bbb.commerce.pricing.BBBPricingTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.bbb.social.facebook.FBConstants;
import com.sapient.common.tests.BaseTestCase;

/**
 * @author alakra
 * 
 */
public class TestOrderCheckout extends BaseTestCase {


    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void setUp() throws Exception {
        final BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) this.getObject("bbbProfileFormHandler");
        this.getRequest().setParameter("BBBProfileFormHandler", bbbProfileFormHandler);
        final BBBProfileTools bbbProfileTools = (BBBProfileTools) this.getObject("bbbProfileTools");
        final Profile pProfile = new Profile();
        final RepositoryItem profileItem = bbbProfileTools. getItemFromEmail((String) this.getObject("email"));
        pProfile.setDataSource(profileItem);
        pProfile.setPropertyValue(FBConstants.SECURITY_STATUS, 4);
        bbbProfileFormHandler.setProfile(pProfile);
        final String pSiteId = (String) this.getObject("siteId");
        bbbProfileFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));

        bbbProfileFormHandler.getValue().put("mRemoveCard", this.getObject("creditCardNickname"));
        bbbProfileFormHandler.setRemoveCard((String) this.getObject("creditCardNickname"));
        bbbProfileFormHandler.handleRemoveCard(this.getRequest(), this.getResponse());

        // delete address
        bbbProfileFormHandler.setAddressId((String) this.getObject("shippingAddrNickname"));
        bbbProfileTools.removeProfileRepositoryAddress(bbbProfileFormHandler.getProfile(), (String) this.getObject("shippingAddrNickname"), true);

        final BBBAddressAPI addressAPI = (BBBAddressAPI)this.getObject("addressAPI");

        final BBBAddressVO address = new BBBAddressVO();
        address.setFirstName("Richie");
        address.setLastName("Rich");
        address.setAddress1("131 Dartmouth St Ste 301");
        address.setCity("Boston");
        address.setPostalCode("02116-5299");
        address.setState("MA");
        address.setCountry("US");

        addressAPI.addNewShippingAddress(pProfile, address, pSiteId, true, true);
        if (!bbbProfileFormHandler.getProfile().isTransient()) {

            // Set Default Shipping Address

            // Set Default Billing Address
            bbbProfileFormHandler.getBillAddrValue().put("firstName", "Richie");
            bbbProfileFormHandler.getBillAddrValue().put("lastName", "Rich");
            bbbProfileFormHandler.getBillAddrValue().put("address1", "131 Dartmouth St Ste 301");
            bbbProfileFormHandler.getBillAddrValue().put("city", "Boston");
            bbbProfileFormHandler.getBillAddrValue().put("state", "Massachusetts");
            bbbProfileFormHandler.getBillAddrValue().put("country", "US");
            bbbProfileFormHandler.getBillAddrValue().put("postalCode", "02116-5299");
            bbbProfileFormHandler.getBillAddrValue().put("shippingAddrNickname", (String) this.getObject("shippingAddrNickname"));
            bbbProfileFormHandler.setEditValue(new HashMap());
            // Set Default Credit Card
            bbbProfileFormHandler.getEditValue().put("creditCardNumber", "4111111111111111");
            bbbProfileFormHandler.getEditValue().put("creditCardType", "Visa");
            bbbProfileFormHandler.getEditValue().put("expirationMonth", "12");
            bbbProfileFormHandler.getEditValue().put("expirationYear", "2016");
            bbbProfileFormHandler.getEditValue().put("nameOnCard", "Richie Rich");
            bbbProfileFormHandler.getEditValue().put("creditCardNickname", (String) this.getObject("creditCardNickname"));

            // add default credit card and address
            bbbProfileFormHandler.handleCreateNewCreditCardAndAddress(this.getRequest(), this.getResponse());
            final Vector v = bbbProfileFormHandler.getFormExceptions();
            assertTrue("ERRORS in submitting default credit card and address", v.size() == 0);
        }
    }

    @SuppressWarnings({ })
    public void testHandleOrderCheckout() throws Exception {
        final BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) this.getObject("bbbProfileFormHandler");
        this.expressCheckoutAsserts(bbbProfileFormHandler);
    }

    @SuppressWarnings("rawtypes")
    private void expressCheckoutAsserts(final BBBProfileFormHandler bbbProfileFormHandler) throws CommerceException, BBBSystemException, BBBBusinessException, PropertyNotFoundException,
    RepositoryException, IntrospectionException {
        final DynamoHttpServletRequest pRequest = this.getRequest();
        final Profile profile = bbbProfileFormHandler.getProfile();
        final MutableRepositoryItem defaultShippingAddress = (MutableRepositoryItem) profile.getPropertyValue("shippingAddress");
        final String sku = (String)this.getObject("sku");
        final String address1 = "Address1";
        final String pSiteId = (String) this.getObject("siteId");
        final String currency = (String)this.getObject("currency");
        defaultShippingAddress.setPropertyValue("address1", address1);
        pRequest.setRequestURI("/store/sdfasdfsd/asdfasd");
        ServletUtil.setCurrentRequest(pRequest);

        profile.setPropertyValue("shippingAddress", defaultShippingAddress);

        final BBBPricingTools pricingTools = (BBBPricingTools)this.getObject("pricingTools");
        final BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) this.getObject("catalogTools");
        final BBBCheckoutManager managerObject = (BBBCheckoutManager) this.getObject("bbbCheckoutManger");
        final BBBOrderManager bbbOrderManager = (BBBOrderManager) this.getObject("bbbOrderManager");
        final BBBShippingAddressDroplet addressDroplet = (BBBShippingAddressDroplet) this.getObject("bbbShippingAddressDroplet");
        final BBBBillingAddressDroplet billAddressDroplet = (BBBBillingAddressDroplet) this.getObject("bbbBillingAddressDroplet");
        final BBBOrder inSessionOrder = (BBBOrder) bbbOrderManager.createOrder(bbbProfileFormHandler.getProfile().getRepositoryId());
        inSessionOrder.setSiteId(pSiteId);
        final BBBHardGoodShippingGroup bbbShippingGroup = (BBBHardGoodShippingGroup)inSessionOrder.getShippingGroups().get(0);
        final RepositoryItem skuItem = catalogTools.getCatalogRepository().getItem(sku, "sku");
        @SuppressWarnings("unchecked")
        final
        Set<RepositoryItem> productItems = (Set<RepositoryItem>) skuItem.getPropertyValue("parentProducts");
        final String commerceItemType = ((BBBOrderTools) bbbOrderManager.getOrderTools()).getDefaultCommerceItemType();
        final String productId = productItems.toArray(new RepositoryItem[0])[0].getRepositoryId();
        bbbOrderManager.addAsSeparateItemToShippingGroup(inSessionOrder, bbbShippingGroup, sku, 2, productId, commerceItemType);

        assertTrue("should qualify for expressCheckout", managerObject.displayExpressCheckout(bbbProfileFormHandler.getProfile(), inSessionOrder, pSiteId));
        managerObject.ensureShippingGroups(inSessionOrder, pSiteId, bbbProfileFormHandler.getProfile());
        managerObject.ensurePaymentGroups(inSessionOrder, pSiteId, bbbProfileFormHandler.getProfile());
        assertFalse("Express checkout, billing address is not valid", StringUtils.isEmpty(inSessionOrder.getBillingAddress().getAddress1()));
        assertFalse("Express checkout, shipping address is not valid", StringUtils.isEmpty(((HardgoodShippingGroup) inSessionOrder.getShippingGroups().get(0)).getShippingAddress().getAddress1()));
        final CreditCard creditCard = (CreditCard) inSessionOrder.getPaymentGroups().get(0);
        assertEquals("Express checkout, credit card is not valid", "4111111111111111", creditCard.getCreditCardNumber());
        final DynamoHttpServletRequest request = this.getRequest();
        request.setParameter(BBBCoreConstants.PROFILE, profile);
        request.setParameter(BBBCoreConstants.ORDER, inSessionOrder);
        request.setParameter("billingAddrContainer", new BBBAddressContainer());
        request.setParameter("addressContainer", new BBBAddressContainer());
        try {
            addressDroplet.service(request, this.getResponse());
            billAddressDroplet.service(request, this.getResponse());
            final List addressList = (List) request.getObjectParameter("groupAddresses");
            final Map addressMap = (Map) request.getObjectParameter("addresses");
            assertTrue("No address found", addressList.size() > 0);
            assertTrue("No address found", addressMap.size() > 0);
        } catch (final ServletException e) {
            e.printStackTrace();
            assertFalse("AddressDroplet error", true);
        } catch (final IOException e) {
            e.printStackTrace();
            assertFalse("AddressDroplet error", true);
        }
        final BBBCommitOrderFormHandler commitOrderFormHandler = (BBBCommitOrderFormHandler) this.getObject("bbbCommitOrderFormHandler");
        try {
            creditCard.setCurrencyCode("USD");
            bbbShippingGroup.getPriceInfo().setCurrencyCode(currency);
            inSessionOrder.getPriceInfo().setCurrencyCode(currency);
            inSessionOrder.getTaxPriceInfo().setCurrencyCode(currency);
            pricingTools.priceOrderSubtotal(inSessionOrder, pricingTools.getDefaultLocale(), profile, new HashMap());

            final SubmitOrderMarshaller marshaller = (SubmitOrderMarshaller) this.getObject("submitOrderMarshaller");
            final String xml = marshaller.getOrderAsXML(inSessionOrder);
            assertFalse("Submit Order xml is null ", StringUtils.isBlank(xml));
            commitOrderFormHandler.getShoppingCart().setCurrent(inSessionOrder);
            commitOrderFormHandler.setOrder(inSessionOrder);
            commitOrderFormHandler.setCardVerNumber("123");
            commitOrderFormHandler.setLoggingDebug(true);
            commitOrderFormHandler.handleCommitOrder(request, this.getResponse());
            assertFalse("Pipeline errors while committing order", commitOrderFormHandler.getFormError());
            request.setParameter(BBBCoreConstants.ORDER, inSessionOrder);
            final BBBOrderInfoDroplet infoDroplet = (BBBOrderInfoDroplet) this.getObject("infoDroplet");
            infoDroplet.service(request, this.getResponse());
            final String productIds = request.getParameter("itemIds");
            assertTrue("Product id not found " + productId, productIds.indexOf(productId) > -1);

        } catch (final ServletException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }finally {
            /*For code coverage*/
            bbbOrderManager.getUnsubmittedOrders(BBBCoreConstants.ORDER_SUBSTATUS_UNSUBMITTED, new Timestamp(Calendar.getInstance().getTimeInMillis()), 0, 2);
            bbbOrderManager.updateOrderSubstatus(inSessionOrder, BBBCoreConstants.ORDER_SUBSTATUS_SUBMITTED);

            bbbOrderManager.removeOrder(inSessionOrder.getId());
        }
    }
}
