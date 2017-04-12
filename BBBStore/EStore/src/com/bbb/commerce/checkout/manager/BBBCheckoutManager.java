package com.bbb.commerce.checkout.manager;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import org.apache.commons.collections.CollectionUtils;

import atg.beans.PropertyNotFoundException;
import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CreditCard;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupImpl;
import atg.commerce.order.SimpleOrderManager;
import atg.commerce.order.purchase.CommerceItemShippingInfo;
import atg.commerce.profile.CommercePropertyManager;
import atg.core.util.Address;
import atg.core.util.ContactInfo;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.multisite.SiteContextManager;
import atg.payment.creditcard.CreditCardTools;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import atg.userprofiling.address.AddressTools;

import com.bbb.account.BBBAddressTools;
import com.bbb.account.BBBProfileManager;
import com.bbb.account.BBBProfileTools;
import com.bbb.account.api.BBBAddressAPI;
import com.bbb.account.api.BBBAddressVO;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.cart.bean.CommerceItemVO;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.catalog.droplet.BBBPromotionTools;
import com.bbb.commerce.catalog.vo.RegionVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.commerce.checkout.BBBVerifiedByVisaConstants;
import com.bbb.commerce.checkout.tools.BBBCheckoutTools;
import com.bbb.commerce.checkout.vbv.vo.BBBVerifiedByVisaVO;
import com.bbb.commerce.common.BBBAddress;
import com.bbb.commerce.common.BBBAddressImpl;
import com.bbb.commerce.common.BBBRepositoryContactInfo;
import com.bbb.commerce.common.BBBVBVSessionBean;
import com.bbb.commerce.common.BasicBBBCreditCardInfo;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.tool.GiftRegistryTools;
import com.bbb.commerce.giftregistry.vo.AddressVO;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.order.BBBCreditCard;
import com.bbb.commerce.order.BBBPaymentGroupManager;
import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship;
import com.bbb.commerce.order.BBBShippingGroupManager;
import com.bbb.commerce.order.paypal.BBBPayPalServiceManager;
import com.bbb.commerce.order.purchase.BBBPurchaseProcessHelper;
import com.bbb.commerce.order.shipping.BBBShippingInfoBean;
import com.bbb.commerce.vo.PayPalInputVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.constants.BBBPayPalConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrder.OrderType;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.ecommerce.order.BBBOrderTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.GiftWrapCommerceItem;
import com.bbb.paypal.BBBGetExpressCheckoutDetailsResVO;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.rest.checkout.vo.AppliedCouponsVO;
import com.bbb.selfservice.manager.SearchStoreManager;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;
import com.bbb.wishlist.GiftListVO;
import com.cardinalcommerce.client.CentinelRequest;


public class BBBCheckoutManager extends BBBGenericService {

    

	private static final String EXPRESS_CHECKOUT_NOT_POSSIBLE_AS_PROFILE_IS_TRANSIENT = "displayExpressCheckout - Not possible as profile is transient";
    private static final String EXPRESS_CHECKOUT_NOT_POSSIBLE_AS_INTERNATIONAL_SHIPPING = "displayExpressCheckout - Not possible in International Shipping";
    private static final String EXPRESS_CHECKOUT_NOT_POSSIBLE_DUE_TO_MULTIPLE_HARDGOOD_SHIPPING_GROUPS = "displayExpressCheckout - Not possible due to Multiple Hardgood Shipping Groups";
    private static final String EXPRESS_CHECKOUT_NOT_POSSIBLE_AS_ITS_A_BOPUS_ORDER = "displayExpressCheckout - Not possible as its a Bopus order";
    private static final String EXPRESS_CHECKOUT_NOT_POSSIBLE_AS_DEFAULT_SHIPPING_METHOD_IS_SHIPPING_METHOD_LIST = "displayExpressCheckout - Not possible as default shipping method is shipping method list";
    private static final String EXPRESS_CHECKOUT_NOT_POSSIBLE_AS_PHONE_NUMBER_AND_MOBILE_ARE_MISSING = "displayExpressCheckout - Not possible as phone number and mobile are missing";
    private static final String EXPRESS_CHECKOUT_NOT_POSSIBLE_AS_DEFAULT_BILLING_ADDRESS_INFO_IS_MISSING = "displayExpressCheckout - Not possible as default billing address info is missing";
    private static final String EXPRESS_CHECKOUT_NOT_POSSIBLE_AS_SHIPPING_ADDRESS_IS_NULL = "displayExpressCheckout - Not possible as shipping address is null";
    private static final String EXPRESS_CHECKOUT_NOT_POSSIBLE_AS_SHIPPING_ADDRESS_IS_POBOX = "displayExpressCheckout - Not possible as shipping address is PO Box";
    private static final String BUSINESS_EXCEPTION_IN_FETCHING_SKU_DETAILS = "Business exception occured in BBBCheckoutManager| displayBannerOnCart while fetching sku details of sku ID ";
    private static final String SYSTEM_EXCEPTION_IN_FETCHING_SKU_DETAILS = "System exception occured in BBBCheckoutManager| displayBannerOnCart while fetching sku details of sku ID ";
    private static final String POBOX = "PO";
    private GiftRegistryManager giftregistryManager;
    private GiftRegistryTools giftRegistryTools;

    private BBBOrderTools orderTools;
    private BBBShippingGroupManager shippingGroupManager;
    private BBBAddressTools addressTools;
    private BBBCatalogToolsImpl catalogTools;
    private BBBAddressAPI addressApi;
    private BBBProfileTools profileTools;
    private BBBPurchaseProcessHelper purchaseProcessHelper;
    private BBBCheckoutTools checkoutTools;
    private MutableRepository catalogRepository;
    private BBBPayPalServiceManager paypalServiceManager;
    private boolean isDummyOrderOn =false;
    private SearchStoreManager searchStoreManager;
    private SearchStoreManager searchStoreManagerResolve;

    /**
	 * @return the paypalServiceManager
	 */
	public BBBPayPalServiceManager getPaypalServiceManager() {
		return this.paypalServiceManager;
	}

	/**
	 * @param paypalServiceManager the paypalServiceManager to set
	 */
	public void setPaypalServiceManager(BBBPayPalServiceManager paypalServiceManager) {
		this.paypalServiceManager = paypalServiceManager;
	}

	public BBBCheckoutTools getCheckoutTools() {
		return this.checkoutTools;
	}

	public void setCheckoutTools(BBBCheckoutTools checkoutTools) {
		this.checkoutTools = checkoutTools;
	}

	/** @return the Shipping Helper component. */
    public final BBBPurchaseProcessHelper getShippingHelper() {
        return this.purchaseProcessHelper;
    }

    /** @return */
    public final GiftRegistryManager getRegistryManager() {
        return this.giftregistryManager;
    }

    /** @param registryManager */
    public final void setRegistryManager(final GiftRegistryManager registryManager) {
        this.giftregistryManager = registryManager;
    }

    /** @param pShippingHelper the shipping helper component to set. */
    public final void setShippingHelper(final BBBPurchaseProcessHelper pShippingHelper) {
        this.purchaseProcessHelper = pShippingHelper;
    }

    /** @return */
    public  BBBCatalogToolsImpl getCatalogTools() {
        return this.catalogTools;
    }

    /** @param mCatalogTools */
    public final void setCatalogTools(final BBBCatalogToolsImpl mCatalogTools) {
        this.catalogTools = mCatalogTools;
    }

    /** @return */
    public BBBAddressTools getAddressTools() {
        return this.addressTools;
    }

    /** @param mAddressTools */
    public final void setAddressTools(final BBBAddressTools mAddressTools) {
        this.addressTools = mAddressTools;
    }

    /** @return */
    public final BBBOrderTools getOrderTools() {
        return this.orderTools;
    }

    /** @param pOrderTools */
    public final void setOrderTools(final BBBOrderTools pOrderTools) {
        this.orderTools = pOrderTools;
    }

    /** @return */
    public  BBBShippingGroupManager getShippingGroupManager() {
        return this.shippingGroupManager;
    }

    /** @param pShippingGroupManager */
    public final void setShippingGroupManager(final BBBShippingGroupManager pShippingGroupManager) {
        this.shippingGroupManager = pShippingGroupManager;
    }

    /** @return */
    public BBBAddressAPI getProfileAddressTool() {
        return this.addressApi;
    }

    /** @param mAddressTool */
    public final void setProfileAddressTool(final BBBAddressAPI mAddressTool) {
        this.addressApi = mAddressTool;
    }

    /** @return */
    public BBBProfileTools getProfileTools() {
        return this.profileTools;
    }

    /** @param profileTools */
    public final void setProfileTools(final BBBProfileTools profileTools) {
        this.profileTools = profileTools;
    }

    /** The order should not contain multiple hardgood, multi registry or any store pickup for single address shipping.
     *
     * @param order
     * @param singleCheckoutKey 
     * @return true if the order qualifies for single shipping. */
    public  boolean displaySingleShipping(final Order order, boolean singlePageCheckout) {
        @SuppressWarnings ("unchecked")
        final List<CommerceItem> items = order.getCommerceItems();
        
        //removing empty shipping group.
        //Fix BBBP-9664 - Bopus Item||No of Attempts increased for wrong credit card details|
        //Redirect To Cart creating empty hardgoodshipping group
        // Adding Transaction as it was giving version mismatch exception while adding LTL item to Cart. 
        boolean shouldRollback = true;
		TransactionDemarcation td = new TransactionDemarcation();
	        try {
	        	td.begin(this.getOrderTools().getTransactionManager(), TransactionDemarcation.REQUIRED);
	        	synchronized (order) {
	        		int shippingGroupCountBefore = 0;
                    int shippingGroupCountAfter = 0;
                    if (order.getShippingGroups() != null) {
                           shippingGroupCountBefore = order.getShippingGroups().size();
                    }

	        		this.getShippingGroupManager().removeEmptyShippingGroups(order);
	        		if (order.getShippingGroups() != null) {
                        shippingGroupCountAfter = order.getShippingGroups().size();
	        		}
	        		if (shippingGroupCountBefore != shippingGroupCountAfter) {
	        			this.getOrderTools().getOrderManager().updateOrder(order);
					}
	        		shouldRollback = false;
	        	}
	        } catch (CommerceException e) {
	        	logError("Error occurred while removing empty ship group", e);
	        } catch (TransactionDemarcationException e) {
	        	logError("TransactionDemarcationException exception occurred", e);
			} finally {
	        	try {
					td.end(shouldRollback);
				} catch (TransactionDemarcationException e) {
					this.logError("TransactionDemarcationException exception occurred", e);
				}
	        }
        
        if (this.getShippingGroupManager().isMultipleHardgoodShippingGroupsWithRelationships(order)) {

            this.logDebug("there are multiple hardgoodshippinggroups in order with id " + order.getId());
            return false;
        }
        if(singlePageCheckout){
        	int shippingGrpCnt = order.getShippingGroupCount();
        	if (shippingGrpCnt > 1 || ((BBBOrderImpl) order).isBopusOrderWithMultipleStoreItems() || ((OrderType.HYBRID).equals(((BBBOrderImpl) order).getOrderType()))) {
                this.logDebug("there are multi store bopus items in order with id " + order.getId());
                return false;
            }
        } else{
        	if (((BBBOrderImpl) order).isBopusOrder() || ((OrderType.HYBRID).equals(((BBBOrderImpl) order).getOrderType()))) {
                this.logDebug("there are store pickup in order with id " + order.getId());
                return false;
            }
        }
        
        if (!this.itemsFromSingleRegistry(items)) {
            this.logDebug("All items are not from single registry for the order " + order.getId());
            return false;
        }

        if (this.getCommonShippingMethodCount(order) == 0) {
            return false;
        }

        return true;
    }

   
    /** returns the count of registry referenced from the items
     *
     * @param siteId
     * @param siteId
     * @param items
     * @return */
    public int getItemsRegistryCount(final List<CommerceItem> items) {
        final List<String> registryList = new ArrayList<String>();
        for (final CommerceItem commerceItem : items) {
            if (commerceItem instanceof BBBCommerceItem) {
                final BBBCommerceItem bbbComItem = (BBBCommerceItem) commerceItem;
                if (!(StringUtils.isEmpty(bbbComItem.getRegistryId()) || registryList.contains(bbbComItem
                                .getRegistryId()))) {
                    registryList.add(((BBBCommerceItem) commerceItem).getRegistryId());
                }
            }
        }
        this.logDebug("The order contains registries with count " + registryList.size());
        return registryList.size();
    }

    /** Checks if all items are from Single Registry
     *
     * @param siteId
     * @param siteId
     * @param items
     * @return */
    public final boolean itemsFromSingleRegistry(final List<CommerceItem> items) {
        final List<String> registryList = new ArrayList<String>();
        for (final CommerceItem commerceItem : items) {
            if (commerceItem instanceof BBBCommerceItem) {
                final BBBCommerceItem bbbComItem = (BBBCommerceItem) commerceItem;
                //if (StringUtils.isEmpty(bbbComItem.getRegistryId())) {
                //} else {
                    if (!StringUtils.isEmpty(bbbComItem.getRegistryId()) && !registryList.contains(bbbComItem.getRegistryId())) {
                        registryList.add(((BBBCommerceItem) commerceItem).getRegistryId());
                    }
                //}
            }
        }
        this.logDebug("The order contains registries with count " + registryList.size());
        return !(registryList.size() > 1);
    }

    /** gets the common shipping method count for all items from order
     *
     * @param siteId
     * @param order
     * @return */
    public final int getCommonShippingMethodCount(final Order order) {
        final List<ShipMethodVO> shippingMethodList = this.getShippingGroupManager().getShippingMethodsForOrder(order);
        this.logDebug("The order has common the shippingmethods with count " + shippingMethodList.size());
        return shippingMethodList.size();
    }

    /** this method checks for restricted states for any sku and return false if it restricted
     *
     * @param siteId
     * @param items
     * @param address
     * @return */
    public boolean
                    canItemShipToAddress(final String siteId, final List<CommerceItem> items, final Address address) {

        if ((address == null) || StringUtils.isEmpty(address.getState())) {
            this.logDebug("No state founds so this order is restricted");
            return false;
        }
        final String state = address.getState();
        for (final CommerceItem commerceItem : items) {
            try {
                final SKUDetailVO skuDetails = this.getCatalogTools().getSKUDetails(siteId,
                                commerceItem.getCatalogRefId(), false, true, true);
                if (skuDetails == null) {
                    this.logDebug("No sku details founds so this order is restricted");
                    return false;
                }
                final List<StateVO> nonShippableStates = skuDetails.getNonShippableStates();
                ((BBBAddress)address).setIsNonPOBoxAddress(false);
                for (final StateVO stateVO : nonShippableStates) {
                    if ((stateVO.getStateCode() != null) && state.equalsIgnoreCase(stateVO.getStateCode())) {
                        this.logDebug(skuDetails.getDisplayName() + " Sku is resticted for this state " + state);
                        return false;
                    }
                    if (stateVO.getStateCode() != null && stateVO.getStateCode().equalsIgnoreCase(POBOX)
                    		&& (((BBBAddress) address).isPoBoxAddress() || isPostOfficeBoxAddress(address))) {
                        this.logDebug(skuDetails.getDisplayName() + " Sku is restricted for PO BOX");
                        ((BBBAddress)address).setIsNonPOBoxAddress(true);
                        return false;
                    }
                }

            } catch (final Exception e) {
                this.logDebug("There are errors while state restrictions validation", e);
                return false;
            }
        }
        this.logDebug("There are no state restrictions for any item in the order");
        return true;
    }
    
    /** this method checks for restricted states for any sku and return false if it restricted
    *
    * @param siteId
    * @param items
    * @param address
    * @return */
   public boolean
                   canItemShipToCiSiIndexAddress(final String siteId, final CommerceItemShippingInfo item, final Address address) {
	   try {
		   final String state = address.getState();
           final SKUDetailVO skuDetails = this.getCatalogTools().getSKUDetails(siteId,
        		   item.getCommerceItem().getCatalogRefId(), false, true, true);
           if (skuDetails == null) {
               this.logDebug("No sku details founds so this order is restricted");
               return false;
           }
           final List<StateVO> nonShippableStates = skuDetails.getNonShippableStates();
           ((BBBAddress)address).setIsNonPOBoxAddress(false);
           for (final StateVO stateVO : nonShippableStates) {

               if ((stateVO.getStateCode() != null) && state.equalsIgnoreCase(stateVO.getStateCode())) {
                   this.logDebug(skuDetails.getDisplayName() + " Sku is resticted for this state " + state);
                   return false;
               }
               if (stateVO.getStateCode() != null && stateVO.getStateCode().equalsIgnoreCase(POBOX)
               			&& (((BBBAddress) address).isPoBoxAddress() || isPostOfficeBoxAddress(address))) {
               		this.logDebug(skuDetails.getDisplayName() + " Sku is restricted for PO BOX");
                   ((BBBAddress)address).setIsNonPOBoxAddress(true);
                   return false;
               }
           }

       } catch (final Exception e) {
           this.logDebug("There are errors while state restrictions validation", e);
           return false;
       }
    return true;
   }


    /** this method checks for restricted states for given Sku
     *
     * @param siteId
     * @param skuId
     * @param address
     * @return */
    public boolean checkItemShipToAddress(final String siteId, final String skuId, final Address address) {
        this.logDebug("checkItemShipToAddress() starts : ");
        this.logDebug("Input paramters : siteId " + siteId + " ,skuId " + skuId + " ,address " + address);
        if ((address == null) || StringUtils.isEmpty(address.getState())) {
            this.logDebug("No address input found so this item is restricted");
            return true;
        }
        final String state = address.getState();

        try {
            final SKUDetailVO skuDetails = this.getCatalogTools().getSKUDetails(siteId, skuId, false, true, true);
            if (skuDetails == null) {
                this.logDebug("No sku details found for this so this item is restricted");
                return true;
            }
            final List<StateVO> nonShippableStates = skuDetails.getNonShippableStates();
            ((BBBAddress)address).setIsNonPOBoxAddress(false);
            for (final StateVO stateVO : nonShippableStates) {
                if ((stateVO.getStateCode() != null) && state.equalsIgnoreCase(stateVO.getStateCode())) {
                    this.logDebug(skuDetails.getDisplayName() + " Sku is restricted for this state " + state);
                    return true;
                }
                if (stateVO.getStateCode() != null && stateVO.getStateCode().equalsIgnoreCase(POBOX)
                		&& (((BBBAddress) address).isPoBoxAddress() || isPostOfficeBoxAddress(address))) {
                    this.logDebug(skuDetails.getDisplayName() + " Sku is restricted for PO BOX");
                    ((BBBAddress)address).setIsNonPOBoxAddress(true);
                    return true;
                }
            }

        } catch (final Exception e) {
            this.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1012
                            + ": There are errors while state restrictions validation", e);
            return true;
        }

        this.logDebug("checkItemShipToAddress() ends : There are no state restrictions for any item in the order");
        return false;
    }

    /** @param siteId
     * @param items
     * @param shippingMethod
     * @return */
    public boolean canItemShipByMethod(final String siteId, final List<CommerceItem> items,
                    final String shippingMethod) {

        if (shippingMethod == null) {
            this.logDebug("No shippingMethod founds so this order is restricted");
            return false;
        }
        boolean sameDayDeliveryFlag = false;
 		String sddEligibleOn = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG);
 		if(null != sddEligibleOn){
 			sameDayDeliveryFlag = Boolean.valueOf(sddEligibleOn);
 		}
        for (final CommerceItem commerceItem : items) {
            try {
                final List<ShipMethodVO> skuMethodVO = this.getCatalogTools().getShippingMethodsForSku(siteId,
                                commerceItem.getCatalogRefId(), sameDayDeliveryFlag);
                if (skuMethodVO == null) {
                    this.logDebug("No skumethodvo not founds so this order is restricted");
                    return false;
                }
                final StringBuffer shipMethodsList = new StringBuffer("");
                for (final ShipMethodVO shipMethodVO : skuMethodVO) {
                    shipMethodsList.append(shipMethodVO.getShipMethodId());
                }
                if (shipMethodsList.toString().indexOf(shippingMethod) < 0) {
                    this.logDebug("Shipping method not found in the valid available methods");
                    return false;
                }

            } catch (final Exception e) {
                this.logDebug("Some error occured during Shipping method validation, invalidating shipping method", e);
                return false;
            }
        }
        return true;
    }
    
    public boolean canAddTheAddressToProfile(BBBAddress bbbAddress) {
    	
   		return !getNotSavableStates().contains(bbbAddress.getState());
    }
    
    public List<String> getNotSavableStates() {
    	
    	List<String> territories = new ArrayList<String>();
    	Object obj = SiteContextManager.getCurrentSite().getPropertyValue(BBBCatalogConstants.STATES_PROPERTY_NAME);
    	if(obj != null) {
    		Set<RepositoryItem> stateSet = (Set<RepositoryItem>) obj;
    		for(RepositoryItem state: stateSet) {
    			if(state.getPropertyValue(BBBCatalogConstants.SHOW_ON_BILLING_PAGES) != null
    					&& !(Boolean)state.getPropertyValue(BBBCatalogConstants.SHOW_ON_BILLING_PAGES)) {
    				territories.add(state.getRepositoryId());
    			}
    		}
    	}
    	return territories;
    }
    /** @param pOrder
     * @param pProfile
     * @return */
    public  List<BBBAddress> getShippinggroupAddresses(final Order pOrder) {
        return this.getShippingGroupManager().getAllShippingGroupAddress(pOrder);
    }

    /** @param pOrder
     * @param pAddress
     * @throws RepositoryException
     * @throws IntrospectionException */
    public void saveBillingAddress(final BBBOrder pOrder, final BBBAddress pAddress)
                    throws RepositoryException, IntrospectionException {
        this.logDebug("Entry BBBCheckoutManager.saveBillingAddress");
        final BBBOrderImpl order = (BBBOrderImpl) pOrder;
        final ContactInfo bbbNewAddress = (ContactInfo) pAddress;
        if (null == order.getBillingAddress()) {
            final BBBRepositoryContactInfo bbbRepoContInfo = this.getOrderTools().createBillingAddress();
            AddressTools.copyAddress(bbbNewAddress, bbbRepoContInfo);
            bbbNewAddress.setPhoneNumber(null);
            bbbNewAddress.setEmail(null);
            ((MutableRepository) this.getOrderTools().getOrderRepository())
                            .addItem(bbbRepoContInfo.getRepositoryItem());
            order.setBillingAddress(bbbRepoContInfo);
        } else {
            if (null == bbbNewAddress.getEmail()) {
                bbbNewAddress.setEmail(order.getBillingAddress().getEmail());
            }
            if (null == bbbNewAddress.getPhoneNumber()) {
                bbbNewAddress.setPhoneNumber(order.getBillingAddress().getPhoneNumber());
            }
            AddressTools.copyAddress(bbbNewAddress, order.getBillingAddress());
            ((MutableRepository) this.getOrderTools().getOrderRepository()).updateItem(order.getBillingAddress()
                            .getRepositoryItem());
        }
        if(BBBUtility.isEmpty(order.getBillingAddress().getMobileNumber())){
        	order.getBillingAddress().setMobileNumber(order.getBillingAddress().getPhoneNumber());
        }
        order.getBillingAddress().setFromPaypal(false);
    }

    /** gives list of CommerceItemVOs
     *
     * @param pOrder
     * @return List<CommerceItemVO>
     * @throws BBBBusinessException
     * @throws BBBSystemException */
    public List<CommerceItemVO> getCartItemVOList(final Order pOrder)//remove final modifier
                    throws BBBSystemException, BBBBusinessException {
        logDebug("Start method : getCartItemVOList()");
    	final List<CommerceItemVO> commerceItemVOs = new ArrayList<CommerceItemVO>();
    	String siteId = null;

        if (pOrder != null) {

            @SuppressWarnings ("unchecked")
            final List<CommerceItem> commerceItems = pOrder.getCommerceItems();
            String ltlShipMethodDesc =null;
            for (final CommerceItem commerceItem : commerceItems) {
                final CommerceItemVO commerceItemVO = new CommerceItemVO();

                BBBCommerceItem bbbCommerceItem = null;
                final Object itemObj = commerceItem;
                if (itemObj instanceof BBBCommerceItem) {
                    bbbCommerceItem = (BBBCommerceItem) itemObj;
                    commerceItemVO.setBBBCommerceItem(bbbCommerceItem);
					if(!BBBUtility.isEmpty(bbbCommerceItem.getLtlShipMethod())) {
						RepositoryItem shippingMethod = getCatalogTools().getShippingMethod(bbbCommerceItem.getLtlShipMethod());
						ltlShipMethodDesc = (String) shippingMethod.getPropertyValue("shipMethodDescription");
					}
					commerceItemVO.setLtlShipMethodDesc(ltlShipMethodDesc);
                    
                    //------------------------------------------------------------
                    if(null == bbbCommerceItem.getAuxiliaryData().getCatalogRef()){
                    	
                    	logDebug("bbbCommerceItem.getAuxiliaryData().getCatalogRef() is null");
                    	logDebug("order ::" + pOrder.getId() + "::" + pOrder.toString());
                    	logDebug("commerceItem ::" + commerceItem.getId() + commerceItem.toString());
                    	logDebug("catalogTools :: " + this.catalogTools );
                    	logDebug("auxiliryData :: " + bbbCommerceItem.getAuxiliaryData());
                    	logDebug("siteId :: " + bbbCommerceItem.getAuxiliaryData().getSiteId());
                    	logDebug("catalogRef :: " + bbbCommerceItem.getAuxiliaryData().getCatalogRef());                    	
                    	 siteId = bbbCommerceItem.getAuxiliaryData().getSiteId();
                    	
                    	final SKUDetailVO skuDetailVO = this.catalogTools.getSKUDetails(bbbCommerceItem.getAuxiliaryData()
                                .getSiteId(), bbbCommerceItem.getCatalogRefId(), false, false, true);
                        final boolean isCommItemBelowLine = this.catalogTools.isSKUBelowLine(SiteContextManager
                                .getCurrentSiteId(), bbbCommerceItem.getCatalogRefId());
                        skuDetailVO.setSkuBelowLine(isCommItemBelowLine);
                        commerceItemVO.setSkuDetailVO(skuDetailVO);
                    }
                    else{ siteId = bbbCommerceItem.getAuxiliaryData().getSiteId();
                    	final SKUDetailVO skuDetailVO = this.catalogTools.getSKUDetails(bbbCommerceItem.getAuxiliaryData()
                                    .getSiteId(), ((RepositoryItem) bbbCommerceItem.getAuxiliaryData().getCatalogRef())
                                    .getRepositoryId(), false, false, true);
                        final boolean isCommItemBelowLine = this.catalogTools.isSKUBelowLine(SiteContextManager
                                    .getCurrentSiteId(), ((RepositoryItem) bbbCommerceItem.getAuxiliaryData()
                                    .getCatalogRef()).getRepositoryId());
                        skuDetailVO.setSkuBelowLine(isCommItemBelowLine);
                        commerceItemVO.setSkuDetailVO(skuDetailVO);
                    }
                    
                    if(!BBBUtility.isEmpty(commerceItemVO.getBBBCommerceItem().getReferenceNumber())){
                    	commerceItemVO.setVendorInfoVO((getCatalogTools().getProductVOMetaDetails(SiteContextManager.getCurrentSiteId(), 
                        		bbbCommerceItem.getAuxiliaryData().getProductId())).getVendorInfoVO());                    
                    }
                    
                    
                    @SuppressWarnings ("unchecked")
                    final Map<String, Integer> availabilityMap = ((BBBOrderImpl) pOrder).getAvailabilityMap();
                    if ((availabilityMap != null) && (availabilityMap.get(bbbCommerceItem.getId()) != null)) {
                        commerceItemVO.setStockAvailability(availabilityMap.get(bbbCommerceItem.getId()));
                    }
                    
                                    

                    commerceItemVOs.add(commerceItemVO);
                }
            }
        }
        logDebug("Exiting method getCartItemVOList() with Commerce Items Count :" + commerceItemVOs.size());
        
        
       return commerceItemVOs;
    }
    
    /**
     * This method will start the processing for sdd items on cart page.
     * @throws BBBSystemException 
     * @throws BBBBusinessException 
     */
	public void processSddItemsOnCart(List<CommerceItemVO> commerceItemVOs, String siteId) throws BBBBusinessException, BBBSystemException {

		logDebug("processSddItemsOnCart STARTS  commerceItemVOs :: " + commerceItemVOs + "siteId :: " + siteId);

		RegionVO regionVO = null;
		boolean sddFlagOnRegion = false;
		boolean sameDayDeliveryFlag = false;
		boolean sameDayDeliveryOnCartFlag = false;
		String sddEligibleOn = null;
		String sddOnCartOn = null;
		String regionId = null;
		String promotAttId = null;
		sddEligibleOn = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS,
				BBBCoreConstants.SAME_DAY_DELIVERY_FLAG);
		sddOnCartOn = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS,
				BBBCoreConstants.SDD_ON_CART_FLAG);

		logDebug("processSddItemsOnCart sddEligibleOn :: " + sddEligibleOn + " sddOnCartOn  :: " + sddOnCartOn
				+ " sddFlagOnRegion :: " + sddFlagOnRegion);

		if (null != sddEligibleOn) {
			sameDayDeliveryFlag = Boolean.valueOf(sddEligibleOn);
		}
		if (null != sddOnCartOn) {
			sameDayDeliveryOnCartFlag = Boolean.valueOf(sddOnCartOn);
		}

		if (!(sameDayDeliveryFlag && sameDayDeliveryOnCartFlag)){
			return;
		}
		// get the request & session
		final DynamoHttpServletRequest req = ServletUtil.getCurrentRequest();
		
		BBBSessionBean sessionBean = BBBProfileManager.resolveSessionBean(req);
		if(null != sessionBean && sessionBean.isInternationalShippingContext()){ 
			return;
		}
		
		if (null != sessionBean && null != sessionBean.getCurrentZipcodeVO()) {
			sddFlagOnRegion = sessionBean.getCurrentZipcodeVO().isSddEligibility();
			promotAttId = sessionBean.getCurrentZipcodeVO().getPromoAttId();
			logDebug("sddFlagOnRegion :: " + sddFlagOnRegion);
		}
		
		if(sddFlagOnRegion) {
			
			//Filter eligible commerceItems
			
			boolean sddFlag = false;
			
			List<CommerceItemVO> eligibleCommerceItems = new ArrayList<CommerceItemVO>();
			for(CommerceItemVO commerceItemVo : commerceItemVOs){
				// get the sdd flag
				if (null != commerceItemVo.getSkuDetailVO()) {
					
					String skuId = commerceItemVo.getSkuDetailVO().getSkuId();
					RepositoryItem skuRepositoryItem;
					try {
						skuRepositoryItem = getCatalogTools().getCatalogRepository().getItem(skuId,
								BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
					} catch (RepositoryException e) {
		                this.logError("BBBCheckoutManager Method Name [processSddItemsOnCart]: RepositoryException ");
		                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
		                                BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
		            }
					sddFlag = getCatalogTools().hasSDDAttribute(SiteContextManager.getCurrentSiteId(), skuRepositoryItem, promotAttId, true);
					
					logDebug("sddFlag :: " + sddFlag);
					
					if(sddFlag){
						
						eligibleCommerceItems.add(commerceItemVo);
						
						
					}else{
						
						commerceItemVo.setSddAvailabilityStatus(BBBCoreConstants.SDD_ITEM_INELIGIBLE);
					}

				}
				
			}

			
			logDebug("sending SDD Eligible commerce Items ::  " + eligibleCommerceItems);
			
			if(!CollectionUtils.isEmpty(eligibleCommerceItems)){
				getSddInventoryStatus(eligibleCommerceItems, siteId, sessionBean);	
			}
			
		}

		logDebug("processSddItemsOnCart ENDS  commerceItemVOs :: " + commerceItemVOs);

	}
    
    
    /**
     * This method will execute the main logic for sdd items in the store.
     * @param commerceItemVOs
     */
	public void getSddInventoryStatus(List<CommerceItemVO> commerceItemVOs, String siteId, BBBSessionBean sessionBean ) {

		logDebug("processSddItemsOnCart STARTS  commerceItemVOs :: " + commerceItemVOs + " siteId :: " + siteId + "sessionBean :: " + sessionBean);
		
		if(!BBBUtility.isListEmpty(commerceItemVOs) && !StringUtils.isBlank(siteId)){
			
			String sddStoreId = sessionBean.getSddStoreId();
			logDebug("sddStoreId :: " + sddStoreId);

			boolean skuExist = true;

			// If sddStoreId is not blank, then check if all the items are there in
			// the store in session
			if (!StringUtils.isBlank(sddStoreId)) {

				skuExist = checkCommerceItemsInCurrentStore(sessionBean, commerceItemVOs, siteId);
				logDebug("skuExist :: " + skuExist);

				// all the skus are not existing in a single store.
				if (!skuExist) {

					processCommerceItemInStoreInventory(sessionBean, commerceItemVOs, siteId, null);
				}

			} else {
				// storeId is not in session
				// Now get all the storeIds and iterate over them

				processCommerceItemInStoreInventory(sessionBean, commerceItemVOs, siteId, BBBCoreConstants.SDD_ITEM_STORE_NOT_IN_SESSION);

			}
		}else{
			
			logDebug("siteId or commerceItemVOs List is empty " +"siteId  : " + siteId + " commerceItemVO :: " + commerceItemVOs);
			
		}
		
		
		logDebug("processSddItemsOnCart ENDS  commerceItemVOs :: " + commerceItemVOs + " siteId :: " + siteId + "sessionBean :: " + sessionBean);

	}

    /**
     * 1. This method will fetch the region for the regionId in session and, get the stores mapped to the region and iterate them.
     * 2. If there is no store in session then during iteration, if a single item is found belonging to a store, then that store will be put in session.
     * 3. It will attempt to find all items in a single store, and set the status of items accordingly.
     * 4. If no such store is found then it will set the status according to the store in session.
     * @param sessionBean
     * @param commerceItemVOs
     * @param siteId
     */
	public void processCommerceItemInStoreInventory(BBBSessionBean sessionBean, List<CommerceItemVO> commerceItemVOs,
			String siteId, String flowType) {

		logDebug("processCommerceItemInStoreInventory STARTS sessionBean :: " + sessionBean + " commerceItemVOs ::   " + commerceItemVOs + "siteId :: " + siteId + "flowType :: " + flowType );
		
		// Since one or more skus weren't not found in the store in session,
		// check the skus in all the stores

		boolean skuExistingInAnyStore = false;
		
		String regionId = null;

		if (null != sessionBean && null != sessionBean.getCurrentZipcodeVO()) {
			regionId = sessionBean.getCurrentZipcodeVO().getRegionId();
			logDebug("regionId :: " + regionId);
		}

		Set<String> storesForRegion = null;
		try {

			//Fetch stores for a given region Id
			storesForRegion = fetchStoresFromRegion(regionId);

			// once you have the store from region, get the stores with valid
			// inventory from amongst these stores.

			if (null != storesForRegion && !storesForRegion.isEmpty()) {

				// convert the set of stores to list of stores
				List<String> regionStoresList = new ArrayList<String>(storesForRegion);
				boolean unavailableItemFound  = false;

				for (String store : regionStoresList) {

					//clear commerceItemVos list
					unavailableItemFound  = false;
					
					for (CommerceItemVO commerceItemVo : commerceItemVOs) {

						String skuId = commerceItemVo.getSkuDetailVO().getSkuId();
						Long qty = commerceItemVo.getBBBCommerceItem().getQuantity();

						logDebug("skuId ::  " + skuId + "qty :: " + qty);

						try {

							if (null != getStoreManager()) {

								skuExistingInAnyStore = getStoreManager().checkStoreHasInventoryForSkuId(store, skuId,
										qty, siteId);
							}

							if (skuExistingInAnyStore && StringUtils.isBlank(sessionBean.getSddStoreId())) {
								sessionBean.setSddStoreId(store);
							}

							if (!skuExistingInAnyStore && null != flowType
									&& flowType.equals(BBBCoreConstants.SDD_ITEM_STORE_NOT_IN_SESSION)) {

								unavailableItemFound = true;

							}

							if (!skuExistingInAnyStore && !StringUtils.isBlank(sessionBean.getSddStoreId())) {

								break;
							}

						} catch (BBBBusinessException | BBBSystemException e) {
							logError("Exception in BBBCheckoutManager| processCommerceItemInStoreInventory :: " + e);
						}

					}

					// check if boolean true
					if (skuExistingInAnyStore && !unavailableItemFound) {
						// set the store in session
						sessionBean.setSddStoreId(store);
						for (CommerceItemVO commerceItemVo : commerceItemVOs) {

							commerceItemVo.setSddAvailabilityStatus(BBBCoreConstants.SDD_ITEM_AVAILABLE);
						}
						break;

					}
					// if the immediate above codes doesn't get executed then we
					// already have the statuses according to the store in
					// session.

				}

				if (unavailableItemFound) {

					// persist the item status for the current store in session.
					checkCommerceItemsInCurrentStore(sessionBean, commerceItemVOs, siteId);
				}

			}

		} catch (BBBBusinessException e) {
			logError(
					"BBBCheckoutManager| processCommerceItemInStoreInventory | BBBBusinessException occurred while processing commerceItems in store inventory. ",
					e);
		} catch (BBBSystemException e) {
			logError(
					"BBBCheckoutManager| processCommerceItemInStoreInventory | BBBSystemException occurred while processing commerceItems in store inventory. ",
					e);
		} catch (RepositoryException e) {
			logError(
					"BBBCheckoutManager| processCommerceItemInStoreInventory | RepositoryException occurred while processing commerceItems in store inventory. ",
					e);
		} catch (ServletException e) {
			logError(
					"BBBCheckoutManager| processCommerceItemInStoreInventory | ServletException occurred while processing commerceItems in store inventory. ",
					e);
		} catch (IOException e) {
			logError(
					"BBBCheckoutManager| processCommerceItemInStoreInventory | IOException occurred while processing commerceItems in store inventory. ",
					e);
		}
		
		
		logDebug("processCommerceItemInStoreInventory STARTS sessionBean :: " + sessionBean + " commerceItemVOs ::   " + commerceItemVOs );

	}
	
	
	
	/**
	 * This metod will fetch the stores from region
	 * @param regionId
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public Set<String> fetchStoresFromRegion(String regionId) throws BBBBusinessException, BBBSystemException {

		logDebug("fetchStoresFromRegion STARTS regionId :: " + regionId);

		RegionVO regionVO = null;
		Set<String> storesForRegion = null;

		regionVO = getCatalogTools().getStoreIdsFromRegion(regionId);
		if (null != regionVO) {

			storesForRegion = regionVO.getStoreIds();
		}

		logDebug("fetchStoresFromRegion ENDS regionId :: " + storesForRegion);

		return storesForRegion;

	}
	
	/**
	 * This method will process commerce Items present in current store id.
	 * 1. According to the storeId in session, it will set the availability status as available, unavailable or ineligible.
	 * 2. If storeId is not present in session, then we are coming from no store in session flow.
	 * 3. If storeId is not there in session, then none of the item is present in any store, so items will be unavailable or ineligible.
	 * @param sddStoreId
	 * @param commerceItemVOs
	 * @param siteId
	 */
	public boolean checkCommerceItemsInCurrentStore(BBBSessionBean sessionBean, List<CommerceItemVO> commerceItemVOs, String siteId) {

		logDebug("checkCommerceItemsInCurrentStore STARTS sessionBean :: " + sessionBean + " commerceItemVOs ::  "  + commerceItemVOs +  " siteId :: " + siteId  );
		
		boolean skuExistingInSessionStore = false;
		boolean finalResult = true;
		String sddStoreId = sessionBean.getSddStoreId();
		logDebug("sddStoreId ::  "  + sddStoreId );
		
		
		if (!StringUtils.isBlank(sddStoreId)) {

			for (CommerceItemVO commerceItemVo : commerceItemVOs) {

				String skuId = commerceItemVo.getSkuDetailVO().getSkuId();
				Long qty = commerceItemVo.getBBBCommerceItem().getQuantity();
				logDebug("skuId ::  " + skuId + "qty :: " + qty);

				try {

					// check at the inventory level, the stock for the sku

					if (null != getStoreManager()) {

						skuExistingInSessionStore = getStoreManager().checkStoreHasInventoryForSkuId(sddStoreId, skuId,
								qty, siteId);
					}

					logDebug("skuExistingInSessionStore :: " + skuExistingInSessionStore);

					// any time it is false we will break the loop
					if (!skuExistingInSessionStore) {
						commerceItemVo.setSddAvailabilityStatus(BBBCoreConstants.SDD_ITEM_UNAVAILABLE);
						finalResult = false;

					} else {
						commerceItemVo.setSddAvailabilityStatus(BBBCoreConstants.SDD_ITEM_AVAILABLE);
					}

				} catch (BBBBusinessException | RepositoryException | BBBSystemException | ServletException
						| IOException e) {
					logError("Exception in BBBCheckoutManager | checkCommerceItemsInCurrentStore " + e);
				}

			}

		} else {
			// we are coming from no store in session flow
			// boolean sddFlag = false;
			for (CommerceItemVO commerceItemVo : commerceItemVOs) {

				commerceItemVo.setSddAvailabilityStatus(BBBCoreConstants.SDD_ITEM_UNAVAILABLE);

			}

		}
		
		logDebug("checkCommerceItemsInCurrentStore ENDS finalResult " + finalResult  );
		return finalResult;
	}
	
	
	
	
    public  BBBAddress getBillingAddress(final BBBOrder pOrder) {
        return (null != pOrder) ? pOrder.getBillingAddress() : null;
    }

    /** If item is deleted from order but the order contains the RegSummVO in registrySummaryMap then compare with
     * current items in Order
     *
     * @param order
     * @param registrySummaryVO
     * @return */
    private boolean checkForRegistryItemInOrder(final Order order, final RegistrySummaryVO registrySummaryVO) {
        if ((registrySummaryVO != null) && (registrySummaryVO.getRegistryId() != null)) {
            for (final CommerceItem item : (List<CommerceItem>) order.getCommerceItems()) {
                if ((item instanceof BBBCommerceItem)
                                && registrySummaryVO.getRegistryId().equalsIgnoreCase(
                                                ((BBBCommerceItem) item).getRegistryId())) {
                    return true;
                }
            }
        } else {
            return false;
        }

        return false;
    }

    /** Returns the address related to the registry in the order
     *
     * @param siteId
     * @param order
     * @return */
    @SuppressWarnings ("unchecked")
    public List<BBBAddress> getRegistryShippingAddress(final String siteId, final Order order) {
        final List<BBBAddress> addressList = new ArrayList<BBBAddress>();

        // call to extract registry summaries from web service and update registry map in order. BBBSL-1987
        this.updateOrderRegistryMapInfo(order);
        final Collection<RegistrySummaryVO> registryVOList = ((BBBOrderImpl) order).getRegistryMap().values();
        final StringBuffer registryInfo = new StringBuffer();
        if (this.getItemsRegistryCount(order.getCommerceItems()) > 0) {
            for (final RegistrySummaryVO registrySummaryVO : registryVOList) {
            	registryInfo.setLength(0);
                // Check for removed registry items in order
                if (this.checkForRegistryItemInOrder(order, registrySummaryVO)) {
                    AddressVO shipping = registrySummaryVO.getShippingAddress();
                    final AddressVO futureShipping = registrySummaryVO.getFutureShippingAddress();
                    final String futureShippingDate = registrySummaryVO.getFutureShippingDate();
                    if (BBBUtility.isNotEmpty(futureShippingDate) && (null != futureShipping)) {
                        long diffDays = 0L;
                        try {
                            diffDays = BBBUtility.getDateDiff(futureShippingDate, siteId);
                        } catch (final ParseException e) {
                            this.logError("ParseException while date conversion in getRegistryShippingAddress", e);
                        }

                        if (diffDays <= 0 && !BBBUtility.isEmpty(futureShipping.getAddressLine1())) {
                       		shipping = futureShipping;
                        	shipping.setCountry((String) SiteContextManager.getCurrentSite().getPropertyValue(
                                            BBBCoreConstants.DEFAULT_COUNTRY));
                        }
                    }
                    List<String> shiptoPOBoxOn;
            		boolean shiptoPOFlag =false;
                    try {
            			shiptoPOBoxOn = catalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON);
            			shiptoPOFlag = Boolean.parseBoolean(shiptoPOBoxOn.get(0));
                    if ((shipping != null)
                                    && !StringUtils.isEmpty(shipping.getAddressLine1())) {
                    	if(shiptoPOFlag || (!shiptoPOFlag &&BBBUtility.isNonPOBoxAddress(shipping.getAddressLine1(),
                                shipping.getAddressLine2()))){
                        final BBBAddressImpl bbbAddress = this.getBBBAddress(shipping);
                        bbbAddress.setSource(BBBCheckoutConstants.REGISTRY_SOURCE);
                        bbbAddress.setSourceIdentifier(registrySummaryVO.getRegistryId());
                        bbbAddress.setId(registrySummaryVO.getRegistryId());
                        bbbAddress.setRegistryId(registrySummaryVO.getRegistryId());
                        
                        // bbbAddress.setIdentifier(registrySummaryVO.getRegistryId()
                        // );
                        bbbAddress.setIdentifier(bbbAddress.getSource() + bbbAddress.getId());
                        bbbAddress.setEmail(registrySummaryVO.getRegistrantEmail());
                        bbbAddress.setPoBoxAddress(registrySummaryVO.getShippingAddress().isPoBoxAddress());
                        
                        // PSI7 Changed in GiftRegistryBeanMapping.xml to fetch phone number and mobile num of registry
                        // to pass address details further in registry address for LTL Registry Items
                        bbbAddress.setPhoneNumber(registrySummaryVO.getPrimaryRegistrantPrimaryPhoneNum());
                        bbbAddress.setMobileNumber(registrySummaryVO.getPrimaryRegistrantMobileNum());
                        registryInfo.append(registrySummaryVO.getPrimaryRegistrantFirstName());
                        registryInfo.append(BBBCoreConstants.SPACE);
                        registryInfo.append(registrySummaryVO.getPrimaryRegistrantLastName());
                        registryInfo.append(BBBCoreConstants.SPACE);
                        if (registrySummaryVO.getCoRegistrantFirstName() != null) {
                            registryInfo.append(BBBCoreConstants.AMPERSAND);
                            registryInfo.append(BBBCoreConstants.SPACE);
                            registryInfo.append(registrySummaryVO.getCoRegistrantFirstName());
                            registryInfo.append(BBBCoreConstants.SPACE);
                            registryInfo.append(registrySummaryVO.getCoRegistrantLastName());
                            registryInfo.append(BBBCoreConstants.SPACE);
                        }

                        bbbAddress.setRegistryInfo(registryInfo.toString());
                        addressList.add(bbbAddress);
                    	}
                    }
                    }catch (BBBSystemException e) {
            			// TODO Auto-generated catch block
            			//e.printStackTrace();
                    	logError(e.getMessage(),e);
            		} catch (BBBBusinessException e) {
            			// TODO Auto-generated catch block
            			//e.printStackTrace();
            			logError(e.getMessage(),e);
            		}
                }

            }
        }

        return addressList;
    }

    /** This method is used to extract registry information from web services and update the registry map in order to get
     * the latest shipping address of the registry present in order. BBBSL-1987
     *
     * @param order */
    @SuppressWarnings("unchecked")
	public final void updateOrderRegistryMapInfo(final Order order) {

        this.logDebug("BBBCheckoutManger.updateOrderRegistryMapInfo method started");
        final DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
        Map<String, RegistrySummaryVO> registryMap;

        final BBBOrderImpl bbbOrder = (BBBOrderImpl) order;
        registryMap = bbbOrder.getRegistryMap();

        @SuppressWarnings ("rawtypes")
        RegistrySummaryVO registrySummaryVO = null;
        if ((registryMap != null) && !registryMap.isEmpty()) {
            for (final Object element : registryMap.keySet()) {
                final String registryId = (String) element;
                if (registryId != null) {
                    try {
                        this.logDebug("Updating the registry VO from web service for registry id " + registryId);
                        boolean regItemsWSCall = false;
                		List<String> regItemsWSCallFlag = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall");
                		if (regItemsWSCallFlag != null && !regItemsWSCallFlag.isEmpty())
                		{
                			regItemsWSCall = Boolean.parseBoolean(regItemsWSCallFlag.get(0));
                		}
                		if (regItemsWSCall)
                		{
                			// WS Call | calling the GiftRegistryTool's getRegistryInfo method
                			registrySummaryVO = this.getRegistryManager().getRegistryInfoFromWebService(registryId,
                                    bbbOrder.getSiteId());
                		}
                		else
                		{
                			// DB Call | calling the GiftRegistryTool's
                			// getRegistryInfoFromEcomAdmin method
                			registrySummaryVO = this.getRegistryManager().getRegistryInfoFromEcomAdmin(registryId,bbbOrder.getSiteId()).getRegistrySummaryVO();
                			
                		}
                        
						if (registrySummaryVO != null) {
							if (null != registrySummaryVO.getRegistryType()
									&& null == registrySummaryVO.getRegistryType().getRegistryTypeDesc()) {
								logDebug("BBBCheckoutManager.updateOrderRegistryMapInfo : RegistryTypeDesc is null, updating it from siterepository");
								Map<String, String> registryTypeMap = new HashMap<String, String>();
								final RepositoryItem siteConfiguration = getCatalogTools().getSiteRepository().getItem(
										bbbOrder.getSiteId(), BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
								if (null != siteConfiguration) {
									final Set<RepositoryItem> registryRepositoryItem = (Set<RepositoryItem>) siteConfiguration
											.getPropertyValue(BBBCatalogConstants.REGISTRY_TYPES_SITE_PROPERTY_NAME);
									logDebug("BBBCheckoutManager.updateOrderRegistryMapInfo : RegistryRepositoryItem for site id :"
											+ bbbOrder.getSiteId() + " is : " + registryRepositoryItem);
									if (null != registryRepositoryItem && !registryRepositoryItem.isEmpty()) {
										for (final RepositoryItem registryRepoItem : registryRepositoryItem) {
											String registryTypeCode = new String();
											String registryTypeDesc = new String();
											if (null != registryRepoItem
													.getPropertyValue(BBBCatalogConstants.REGISTRY_TYPE_NAME_REGISTRY_PROPERTY_CODE)) {
												registryTypeCode = registryRepoItem.getPropertyValue(
														BBBCatalogConstants.REGISTRY_TYPE_NAME_REGISTRY_PROPERTY_CODE)
														.toString();
											}
											if (null != registryRepoItem
													.getPropertyValue(BBBCatalogConstants.REGISTRY_TYPE_DESC_REGISTRY_PROPERTY_NAME)) {
												registryTypeDesc = registryRepoItem.getPropertyValue(
														BBBCatalogConstants.REGISTRY_TYPE_DESC_REGISTRY_PROPERTY_NAME)
														.toString();
											}
											registryTypeMap.put(registryTypeCode, registryTypeDesc);
											logDebug("BBBCheckoutManager.updateOrderRegistryMapInfo : Populating RegistryType code to RegistryTypeDesc Map, registryTypeMap : "
													+ registryTypeMap);
										}
									}
									registrySummaryVO.getRegistryType().setRegistryTypeDesc(
											registryTypeMap.get(registrySummaryVO.getRegistryType()
													.getRegistryTypeName()));
									logDebug("BBBCheckoutManager.updateOrderRegistryMapInfo : RegistryTypeDesc is updated from siterepository value : "
											+ registrySummaryVO.getRegistryType().getRegistryTypeName());
								}
							}
							registrySummaryVO.getShippingAddress().setCountry(
									this.getCatalogTools().getDefaultCountryForSite(
											SiteContextManager.getCurrentSiteId()));
							registryMap.put(registryId, registrySummaryVO);
						}

                    } catch (RepositoryException e) {
                    	this.logError(LogMessageFormatter.formatMessage(pRequest, "BBBCheckoutManager.updateOrderRegistryMapInfo : Registry Type Missing"), e);
					} catch (final BBBSystemException exception) {
                        this.logError(LogMessageFormatter.formatMessage(pRequest, "Default Country Missing"), exception);
                    } catch (final BBBBusinessException exception) {
                        this.logError(LogMessageFormatter.formatMessage(pRequest, "Default Country Missing"), exception);
                    } 
                }
            }
        }
        bbbOrder.setRegistryMap(registryMap);
        this.logDebug("BBBCheckoutManger.updateOrderRegistryMapInfo method ended");
    }

    /** generates the BBBAddress type from AddressVo returned from registry address call
     *
     * @param shipping
     * @return */
    private BBBAddressImpl getBBBAddress(final AddressVO shipping) {
        final BBBAddressImpl address = new BBBAddressImpl();

        address.setFirstName(shipping.getFirstName());
        // address.setMiddleName(shipping.getM);
        address.setLastName(shipping.getLastName());
        address.setAddress1(shipping.getAddressLine1());
        address.setAddress2(shipping.getAddressLine2());
        // address.setAddress3(shipping.getA);
        address.setCity(shipping.getCity());
        address.setCompanyName(shipping.getCompany());
        // address.setCounty(shipping.getC);
        address.setCountry(shipping.getCountry());
        // address.setEmail(shipping.getFirstName());
        // address.setFaxNumber(shipping.getFirstName());
        address.setPostalCode(shipping.getZip());
        address.setState(shipping.getState());
        address.setPoBoxAddress(shipping.isPoBoxAddress());
        address.setQasValidated(shipping.isQasValidated());
        return address;
    }

    public final GiftRegistryTools getGiftRegistryTools() {
        return this.giftRegistryTools;
    }

    public final void setGiftRegistryTools(final GiftRegistryTools pGiftRegistryTools) {
        this.giftRegistryTools = pGiftRegistryTools;
    }

    public final boolean isAnyHardgoodItems(final BBBOrder pOrder) {
        return ((null != this.getShippingGroupManager().getHardgoodShippingGroups(pOrder)) && !this
                        .getShippingGroupManager().getHardgoodShippingGroups(pOrder).isEmpty()) ? true : false;
    }

    /** The methods takes list of RepositoryContactInfo and returns the corresponding list on BBBAddress
     *
     * @param allShippingAddresses
     * @return */
    public final List<BBBAddress> changeToBBBAdressList(@SuppressWarnings ("rawtypes") final List allShippingAddresses) {
        final List<BBBAddress> addressList = new ArrayList<BBBAddress>();
        for (final Object object : allShippingAddresses) {
            addressList.add((BBBAddress) object);
        }
        return addressList;
    }

    /** checks order and verifies that all commerceitem are eligible for pack and hold
     *
     * @param order
     * @return */
    public final boolean hasAllPackNHoldItems(final Order order) {
        if (this.getShippingGroupManager().isMultipleHardgoodShippingGroupsWithRelationships(order)) {
            return false;
        }
        @SuppressWarnings ("rawtypes")
        final List commerceItems = order.getCommerceItems();
        if (commerceItems.isEmpty()) {
            return false;
        }
        for (final Object item : commerceItems) {
            if (item instanceof BBBCommerceItem) {
                if (!((BBBCommerceItem) item).getBts() || ((BBBCommerceItem) item).isVdcInd()) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }
    /** checks order and verifies that all commerceitem are eligible for pack and hold fro mobile spc
    *
    * @param order
    * @return */
    public final boolean hasAllPackNHoldItemsForSPC(Order order) {
        if (this.getShippingGroupManager().isMultipleHardgoodShippingGroupsWithRelationships(order)) {
            return false;
        }
        List commerceItems = order.getCommerceItems();
        if (commerceItems.isEmpty()) {
            return false;
        }
        boolean btsExists = false;
        for (Object item : commerceItems) {
            if (item instanceof BBBCommerceItem) {
                if (((BBBCommerceItem)item).isVdcInd() || ((BBBCommerceItem)item).isLtlItem() || !BBBUtility.isEmpty(((BBBCommerceItem)item).getReferenceNumber())) {
                    return false;
                }else if (((BBBCommerceItem)item).getBts()){
                	btsExists = true;
                }
            }else if(item instanceof GiftWrapCommerceItem){
            	 continue;
            }else{
            	return false;
            }
        }
        return btsExists;
    }
    /** 
    * checks order and verifies whether order has even a single college item
    * @param order
    * @return boolean
    * */
   public final boolean hasEvenSingleCollegeItem(final Order order) {
       if (this.getShippingGroupManager().isMultipleHardgoodShippingGroupsWithRelationships(order)) {
           return false;
       }
       @SuppressWarnings ("rawtypes")
       final List commerceItems = order.getCommerceItems();
       if (commerceItems.isEmpty()) {
           return false;
       }
       for (final Object item : commerceItems) {
           if (item instanceof BBBCommerceItem) {
               if (((BBBCommerceItem) item).getBts() && !((BBBCommerceItem)item).isVdcInd()) {
                   return true;
               }
           }
       }
       return false;
   }
   
   /** 
    * checks order and verifies whether order has even a single VDC item
    * @param order
    * @return boolean
    * */
   public final boolean hasEvenSingleVDCItem(final Order order) {
       @SuppressWarnings ("rawtypes")
       final List commerceItems = order.getCommerceItems();
       if (commerceItems.isEmpty()) {
           return false;
       }
       for (final Object item : commerceItems) {
           if (item instanceof BBBCommerceItem) {
               if (((BBBCommerceItem) item).isVdcInd()) {
                   return true;
               }
           }
       }
       return false;
   }

    /** Return true if postal code is restricted for a default shipping address else return false.
     *
     * @param profile
     * @param order
     * @param siteId
     * @return
     * @throws BBBSystemException
     * @throws BBBBusinessException */
    public final boolean checkRestrictedZip(final Profile profile, final BBBOrder order, final String siteId)
                    throws BBBSystemException, BBBBusinessException {
        boolean isRestricted = false;
        @SuppressWarnings ("rawtypes")
        final List commerceItems = order.getCommerceItems();
        final BBBAddressVO defaultAddress = this.getProfileAddressTool().getDefaultShippingAddress(profile, siteId);
        if ((defaultAddress != null) && BBBUtility.isEmpty(defaultAddress.getPostalCode())) {
            return false;
        }
        for (@SuppressWarnings ("rawtypes")
        final Iterator iterator = commerceItems.iterator(); iterator.hasNext();) {
            final Object currentItem = iterator.next();
            if (currentItem instanceof BBBCommerceItem) {
                final String skuId = ((BBBCommerceItem) currentItem).getCatalogRefId();
                if(defaultAddress != null){
                	isRestricted = this.getCatalogTools().isShippingZipCodeRestrictedForSku(skuId, order.getSiteId(),
                		defaultAddress.getPostalCode());
                }
                if (isRestricted) {
                    return true;
                }
            }
        }
        return isRestricted;

    }

    /** Generates the list of states to be used for user selection on address pages or any other pages
     *
     * @param siteId
     * @return */
    /** Generates the list of states to be used for user selection on address pages or any other pages
    *
    * @param siteId
    * @return */
   public  List<StateVO> getStates(final String siteId, final boolean showMilitaryStates,final String noShowPage) {
       try {
           return this.getCatalogTools().getStates(siteId, showMilitaryStates, noShowPage);
       } catch (final Exception e) {
           this.logError(LogMessageFormatter.formatMessage(null, "No states returned for shipping address page",
                           BBBCoreErrorConstants.CHECKOUT_ERROR_1013), e);
       }
       return null;
   }
   
   public final List<StateVO> getStatesBasedOnCountry(final String countryCode, String siteId, final boolean showMilitaryStates,final String noShowPage) {
	   logDebug("BBBCheckoutManager.getStatesBasedOnCountry Method Starts.");
	   if(BBBPayPalConstants.US_COUNTRY_CODE.equalsIgnoreCase(countryCode)){
			if(!BBBCoreConstants.SITE_BBB.equalsIgnoreCase(siteId)){
				siteId=BBBCoreConstants.SITE_BAB_US;
			}
		} else if(BBBPayPalConstants.CA_COUNTRY_CODE.equalsIgnoreCase(countryCode)){
			siteId=BBBCoreConstants.SITE_BAB_CA;
		}
	   siteId = siteId==null ? SiteContextManager.getCurrentSiteId() : siteId;
	   List<StateVO> states = null;
       try {
    	   states =  this.getCatalogTools().getStates(siteId, showMilitaryStates, noShowPage);
       } catch (final Exception e) {
           this.logError(LogMessageFormatter.formatMessage(null, "No states returned while fetching states for country-" + countryCode,
                           BBBCoreErrorConstants.CHECKOUT_ERROR_1013), e);
       }
       logDebug("BBBCheckoutManager.getStatesBasedOnCountry Method ends with states:-" + states);
       return states;
   }

    /**
     * This method checks whether order eligible for shop now ship later
     * @param siteId
     * @param order
     * @param isFromSPC
     * @return
     */
    public final boolean hasAllPackNHoldItems(final String siteId, final Order order, boolean isFromSPC) {
        boolean results = false;
        try {
            results = this.getCatalogTools().isPackNHoldWindow(siteId, new Date());
        } catch (final Exception e) {
            this.logError(LogMessageFormatter.formatMessage(null,
                            "Error getting packnhold flag for the site " + siteId,
                            BBBCoreErrorConstants.CHECKOUT_ERROR_1014), e);
            return results;
        }
        if (results) {
            results = isFromSPC ? this.hasAllPackNHoldItemsForSPC(order) : this.hasAllPackNHoldItems(order);
        }
        return results;
    }
    
    /**
     * This method checks whether order contains even a single college item
     * @param siteId
     * @param order
     * @return boolean
     */
    public boolean hasEvenSingleCollegeItem (final String siteId, final Order order) {
        boolean result = false;
        try {
            result = this.getCatalogTools().isPackNHoldWindow(siteId, new Date());
        } catch (final Exception e) {
            this.logError(LogMessageFormatter.formatMessage(null,
                            "BBBCheckoutManager.hasEvenSingleCollegeItem :: Error getting packnhold flag for the site " + siteId,
                            BBBCoreErrorConstants.CHECKOUT_ERROR_1014), e);
            return result;
        }
        if (result) {
            result = this.hasEvenSingleCollegeItem(order);
        }
        return result;
    }

    /**
     * @param profile Profile
     * @param order Order
     * @param siteId Site ID
     * @return Success / Failure
     * @throws BBBSystemException Exception
     * @throws BBBBusinessException Exception
     * @throws PropertyNotFoundException Exception*/
    public final boolean displayExpressCheckout(final Profile profile, final BBBOrder order, final String siteId)
                    throws BBBSystemException, BBBBusinessException, PropertyNotFoundException {
        if (profile.isTransient()) {
            // user not logged in
            this.logDebug(EXPRESS_CHECKOUT_NOT_POSSIBLE_AS_PROFILE_IS_TRANSIENT);
            return false;
        }
        
        if (profile.getPropertyValue(BBBInternationalShippingConstants.INTERNATIONAL_CONTEXT) != null && (Boolean) profile.getPropertyValue(BBBInternationalShippingConstants.INTERNATIONAL_CONTEXT)) {
            // international shipping
            this.logDebug(EXPRESS_CHECKOUT_NOT_POSSIBLE_AS_INTERNATIONAL_SHIPPING);
            return false;
        }

        if (this.getShippingGroupManager().isMultipleHardgoodShippingGroupsWithRelationships(order)) {
            // multiple hardgood
            this.logDebug(EXPRESS_CHECKOUT_NOT_POSSIBLE_DUE_TO_MULTIPLE_HARDGOOD_SHIPPING_GROUPS);
            return false;
        }
        if (((BBBOrderImpl) order).isBopusOrder()) {
            // store pickup items
            this.logDebug(EXPRESS_CHECKOUT_NOT_POSSIBLE_AS_ITS_A_BOPUS_ORDER);
            return false;
        }
        BBBAddressVO defaultAddress = this.getProfileAddressTool().getDefaultShippingAddress(profile, siteId);
        if (defaultAddress == null || StringUtils.isEmpty(defaultAddress.getAddress1())) {
            // no default shipping address found
            this.logDebug(EXPRESS_CHECKOUT_NOT_POSSIBLE_AS_SHIPPING_ADDRESS_IS_NULL);
            return false;
        }
        List<String> shiptoPOBoxOn = catalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON);
        boolean shiptoPOFlag = Boolean.parseBoolean(shiptoPOBoxOn.get(0));
		if(!shiptoPOFlag && !BBBUtility.isNonPOBoxAddress(defaultAddress.getAddress1(),
        			defaultAddress.getAddress2())) {
            this.logDebug(EXPRESS_CHECKOUT_NOT_POSSIBLE_AS_SHIPPING_ADDRESS_IS_POBOX);
            return false;
        }
        defaultAddress = this.getProfileAddressTool().getDefaultBillingAddress(profile, siteId);
        if ((defaultAddress == null) || StringUtils.isEmpty(defaultAddress.getAddress1())) {
            // no default billing address found
            this.logDebug(EXPRESS_CHECKOUT_NOT_POSSIBLE_AS_DEFAULT_BILLING_ADDRESS_INFO_IS_MISSING);
            return false;
        }

        if (BBBUtility.isEmpty((String) profile.getPropertyValue(BBBCoreConstants.PHONENUMBER))
                        && BBBUtility.isEmpty((String) profile.getPropertyValue(BBBCoreConstants.MOBILENUMBER))) {
            // if no phone/mobile number in profile then no express checkout.
            this.logDebug(EXPRESS_CHECKOUT_NOT_POSSIBLE_AS_PHONE_NUMBER_AND_MOBILE_ARE_MISSING);
            return false;
        }

        final List<ShipMethodVO> shippingMethodList = this.getShippingGroupManager().getShippingMethodsForOrder(order);

        if (!shippingMethodList.contains(this.getCatalogTools().getDefaultShippingMethod(siteId))) {
            this.logDebug(EXPRESS_CHECKOUT_NOT_POSSIBLE_AS_DEFAULT_SHIPPING_METHOD_IS_SHIPPING_METHOD_LIST);
            return false;
        }
        final RepositoryItem defaultCreditCard = this.getProfileTools().getDefaultCreditCard(profile);
        final BasicBBBCreditCardInfo creditCard = new BasicBBBCreditCardInfo();

        if (defaultCreditCard != null) {
            this.getProfileTools().copyShallowCreditCardProperties(defaultCreditCard, creditCard);
        } else {
            this.logDebug("displayExpressCheckout - defaultCreditCard is null !!!!!");
            return false;
        }

        if (CreditCardTools.verifyCreditCard(creditCard) != CreditCardTools.SUCCESS) {
            this.logDebug("displayExpressCheckout - Not possible due to invalid credit card");
            return false;
        }
        return true;
    }

    /**Returns the order inventory status.
     *
     * @param order Order
     * @return Status*/

    public static final int getOrderInventoryStatus(final Order order) {

        @SuppressWarnings ("unchecked")
        final Collection<Integer> inventoryValues = ((BBBOrderImpl) order).getAvailabilityMap().values();
        for (final Integer object : inventoryValues) {
            if (object.intValue() == BBBInventoryManager.NOT_AVAILABLE) {
                return BBBInventoryManager.NOT_AVAILABLE;
            }
        }
        return BBBInventoryManager.AVAILABLE;
    }

    /**Resets hardgood shipping group and adds address and shipping method to it.
     *
     * @param order Order
     * @param siteId Site ID
     * @param profile Profile
     * @throws CommerceException Exception
     * @throws BBBSystemException Exception
     * @throws BBBBusinessException Exception
     * @throws RepositoryException Exception
     * @throws IntrospectionException Exception */
    public void ensureShippingGroups(final Order order, final String siteId, final Profile profile)
                    throws BBBSystemException, BBBBusinessException, RepositoryException, IntrospectionException,
                    CommerceException {

        HardgoodShippingGroup shippingGroup = this.getShippingGroupManager()
                        .getFirstNonGiftHardgoodShippingGroupWithRels(order);
        if (shippingGroup == null) {
            shippingGroup = (HardgoodShippingGroup) this.getShippingGroupManager().createShippingGroup(
                            this.getOrderTools().getDefaultShippingGroupType());
            order.addShippingGroup(shippingGroup);
        }
        final BBBAddressVO shipAddress = this.getProfileAddressTool().getDefaultShippingAddress(profile, siteId);
        final ShipMethodVO defaultShippingMethod = this.getCatalogTools().getDefaultShippingMethod(siteId);
        final BBBShippingInfoBean shippingInfo = new BBBShippingInfoBean();
        shippingInfo.setAddress(shipAddress);
        shippingInfo.setShippingMethod(defaultShippingMethod.getShipMethodId());
        this.getShippingGroupManager().shipToAddress(shippingInfo, (BBBHardGoodShippingGroup) shippingGroup);

    }

    /** Fills the paymentgroup with profile credit card and billing address.
     *
     * @param order Order
     * @param siteId Site ID
     * @param profile Profile
     * @throws CommerceException Exception
     * @throws BBBSystemException Exception
     * @throws BBBBusinessException Exception
     * @throws RepositoryException Exception
     * @throws IntrospectionException Exception*/
    public void ensurePaymentGroups(final Order order, final String siteId, final Profile profile)
                    throws CommerceException, BBBSystemException, BBBBusinessException, RepositoryException,
                    IntrospectionException {

        CreditCard userCreditCard = null;
        if ((order.getPaymentGroups() != null) && (order.getPaymentGroups().size() > 0)) {
        	
        	for (Iterator lIterator = order.getPaymentGroups().iterator(); lIterator.hasNext();) {
				PaymentGroup pg = (PaymentGroup) lIterator.next();
				if (pg instanceof CreditCard) {
					userCreditCard = (CreditCard)pg;
				}
			}
        }

        if (userCreditCard == null) {
            userCreditCard = (CreditCard) this.getShippingGroupManager().getPaymentGroupManager()
                            .createPaymentGroup("creditCard");
            this.getShippingGroupManager().getPaymentGroupManager().addPaymentGroupToOrder(order, userCreditCard);
        }

        final RepositoryItem defaultCard = this.getProfileTools().getDefaultCreditCard(profile);
        final BBBAddressVO defaultBillingAddress = this.getProfileAddressTool().getDefaultBillingAddress(profile,
                        order.getSiteId());

        if (defaultBillingAddress != null) {
           //this.getProfileTools().copyCreditCard(defaultCard, userCreditCard);
        	//RepositoryItem toAddress;
        	//startAddress
            CommercePropertyManager cpmgr = (CommercePropertyManager) profileTools
			.getPropertyManager();
            try {
            	this.getProfileTools().copyShallowCreditCardProperties(defaultCard, userCreditCard);
              RepositoryItem fromAddress = (RepositoryItem)defaultCard.getPropertyValue(cpmgr.getCreditCardBillingAddressPropertyName());
              this.getProfileAddressTool().copyToBillAddress(fromAddress, userCreditCard.getBillingAddress());
             // OrderTools.copyAddress(fromAddress, userCreditCard.getBillingAddress());
            } catch (PropertyNotFoundException pnfe) {
              if (isLoggingError())
                logError(pnfe);
            }
            
            //setting dummy order parameter if eligible
            this.dummyOrder(userCreditCard.getCreditCardNumber(), order);
            
            defaultBillingAddress.setEmail((String) profile.getPropertyValue(BBBCoreConstants.EMAIL));

            String phoneNumber = null;
            if (!BBBUtility.isEmpty((String) profile.getPropertyValue(BBBCoreConstants.PHONENUMBER))) {
                phoneNumber = (String) profile.getPropertyValue(BBBCoreConstants.PHONENUMBER);

            } else if (!BBBUtility.isEmpty((String) profile.getPropertyValue(BBBCoreConstants.MOBILENUMBER))) {
                phoneNumber = (String) profile.getPropertyValue(BBBCoreConstants.MOBILENUMBER);
            }
            defaultBillingAddress.setPhoneNumber(phoneNumber);

            final Address ccBillingAddress = userCreditCard.getBillingAddress();
            this.getProfileAddressTool().copyBBBAddressVO(defaultBillingAddress, ccBillingAddress);
            userCreditCard.setBillingAddress(ccBillingAddress);
            this.saveBillingAddress((BBBOrder) order, defaultBillingAddress);
        }
    }
    
    
    public final void dummyOrder(String creditCardNumber, Order order)
    throws CommerceException {

    	List<String> dummyOrderOn;
			try {
				dummyOrderOn = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.DUMMY_ORDERS_FLAG);
				if(dummyOrderOn != null && !dummyOrderOn.isEmpty()){
					isDummyOrderOn = Boolean.parseBoolean(dummyOrderOn.get(0));
				}else{
					isDummyOrderOn=false;
				}
				   String dummyDiscover = this.getCatalogTools().getContentCatalogConfigration("DummyCC_Discover").get(0);
	               String dummyMaster = this.getCatalogTools().getContentCatalogConfigration("DummyCC_Master").get(0);
	               String dummyVisa = this.getCatalogTools().getContentCatalogConfigration("DummyCC_Visa").get(0);
				if(isDummyOrderOn &&(((dummyDiscover!=null && !dummyDiscover.isEmpty())&&dummyDiscover.equalsIgnoreCase(creditCardNumber))||((dummyMaster!=null &&!dummyMaster.isEmpty())&&dummyMaster.equalsIgnoreCase(creditCardNumber))||((dummyVisa!=null && !dummyVisa.isEmpty())&&dummyVisa.equalsIgnoreCase(creditCardNumber)) )){
					((BBBOrderImpl) order).setDummyOrder(true); 
					
				}else{
					((BBBOrderImpl) order).setDummyOrder(false);
				}
			} catch (BBBSystemException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				logError(e.getMessage(),e);
			} catch (BBBBusinessException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				logError(e.getMessage(),e);
			}
			
    }

    /**
     * @param pProfile Profile
     * @param pAddress Address
     * @param pSiteId Site ID
     * @return Success / Failure
     * @throws BBBSystemException Exception
     * @throws BBBBusinessException Exception*/
    public BBBAddressImpl saveAddressToProfile(final Profile pProfile, final Address pAddress,
                    final String pSiteId) throws BBBSystemException, BBBBusinessException {
        final BBBAddressVO addressVO = (BBBAddressVO) pAddress;
        final List addresses = this.getProfileTools().getAllShippingAddresses(pProfile);
        if ((addresses == null) || addresses.isEmpty()) {
            return (BBBAddressImpl) this.getProfileAddressTool().addNewShippingAddress(pProfile, addressVO, pSiteId,
                            true, true);
        }
        return (BBBAddressImpl) this.getProfileAddressTool().addNewShippingAddress(pProfile, addressVO, pSiteId,
                        false, false);
    }

    /**
     * @param pSiteId Site ID
     * @param pSkuId SKU ID
     * @return Gift Card Item
     * @throws BBBBusinessException Exception
     * @throws BBBSystemException Exception*/
    public boolean isGiftCardItem(final String pSiteId, final String pSkuId)
                    throws BBBBusinessException, BBBSystemException {
        return this.getCatalogTools().isGiftCardItem(pSiteId, pSkuId);
    }

    /** This method returns the list of non registry Shipping Address.
     *
     * @param pOrder Order
     * @return Non Registry Shipping Address*/
    public final List<BBBAddress> getNonRegistryShippingAddress(final BBBOrder pOrder) {

        return this.shippingGroupManager.getNonRegistryShippingAddress(pOrder);

    }

    /** This method returns the list of credit card info .
     *
     * @param pOrder Order
     * @return Credit Card Information List */
    public static final List<BasicBBBCreditCardInfo> getCreditCardFromOrder(final BBBOrder pOrder) {
        return BBBPurchaseProcessHelper.getCreditCardFromOrder(pOrder);

    }

    /**
     * @param pProfile Profile ID
     * @param pSiteId Site ID
     * @return Success / Failure
     * @throws BBBSystemException Exception
     * @throws BBBBusinessException Exception*/
    public  List<BBBAddressVO> getProfileShippingAddress(final Profile pProfile, final String pSiteId)
                    throws BBBSystemException, BBBBusinessException {
        final List<BBBAddressVO> shippingAddresses = new ArrayList<BBBAddressVO>();
        final List<BBBAddressVO> profileAddresses = this.getProfileAddressTool().getShippingAddress(pProfile, pSiteId);
        for (final BBBAddressVO address : profileAddresses) {
		List<String> shiptoPOBoxOn;
		boolean shiptoPOFlag =false;
		shiptoPOBoxOn = getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON);
		shiptoPOFlag = Boolean.parseBoolean(shiptoPOBoxOn.get(0));
		if (shiptoPOFlag || (BBBUtility.isNonPOBoxAddress(address.getAddress1(), address.getAddress2()))) {
                shippingAddresses.add(address);
            }
        }
        return shippingAddresses;
    }

    /** This methods iterates through all the list of sku-Ids and checks the shipping eligibility of each SKU in the
     * particular state(parameter).
     *
     * @param skus : List of SKUs(IDs) for which validation will be performed
     * @param state : state code against which shipping eligibility of the Sku will be checked
     * @return : map of SKU IDs as keys and their shipping status as value
     * @throws BBBBusinessException */
    public final Map<String, Boolean> checkSkusEligibilityForShipping(final List<String> skus, final String state)
                    throws BBBBusinessException {
        if ((skus == null) || (state == null) || skus.isEmpty() || state.isEmpty()) {
            throw new BBBBusinessException(BBBCoreErrorConstants.REQUIRED_PARA_NULL,
                            "Required parameter can not be empty");
        }
        final Map<String, Boolean> skuEligibilityStatusMap = new HashMap<String, Boolean>();
        final Address address = new Address();
        address.setState(state);
        final String siteId = SiteContextManager.getCurrentSiteId();
        for (final String sku : skus) {
            skuEligibilityStatusMap.put(sku, Boolean.valueOf(this.checkItemShipToAddress(siteId, sku, address)));
        }
        return skuEligibilityStatusMap;
    }
    
	/**
	 * This method is used to add name / value pairs to request object to construct XML for lookup request.
	 * @param pRequest the servlet's request
	 * @param bbbOrder object of BBBOrder class
	 * @param centinelRequest object of CentinelRequest class
	 * @return boolean lookUpFlag to check whether to go for VBV or commit order as unauthenticated 
	 */
    @SuppressWarnings("unchecked")
	public boolean vbvCentinelAddLookupRequest(DynamoHttpServletRequest pRequest, BBBOrder bbbOrder,  CentinelRequest centinelRequest, BBBVerifiedByVisaVO bBBVerifiedByVisaVO, BBBVBVSessionBean vbvSessionBean){
    	logDebug("Start vbvCentinelAddLookupRequest method of BBBCheckout manager for Order Id: " + bbbOrder.getId());
		boolean lookUpFlag=false;
		final List<PaymentGroup> paymentGroups = bbbOrder.getPaymentGroups();		
		for (final PaymentGroup paymentGroup : paymentGroups) {
			//check if payment group is credit card 
			if (paymentGroup instanceof BBBCreditCard) {
				final BBBCreditCard creditCard = (BBBCreditCard) paymentGroup;
				//check if credit card is of type visa or mastercard and set lookUpFlag as true.
				if(creditCard.getCreditCardType().equalsIgnoreCase(BBBVerifiedByVisaConstants.VISA) || creditCard.getCreditCardType().equalsIgnoreCase(BBBVerifiedByVisaConstants.MASTERCARD)){
					centinelRequest.add(BBBVerifiedByVisaConstants.CardNumber, creditCard.getCreditCardNumber());
					//credit card returns single digit but we need to send double digit for VBV. 
					String expMonth = creditCard.getExpirationMonth();
					if(creditCard.getExpirationMonth().length()==1){
						expMonth="0"+creditCard.getExpirationMonth();
					}
			    	centinelRequest.add(BBBVerifiedByVisaConstants.CardExpMonth, expMonth);
			    	centinelRequest.add(BBBVerifiedByVisaConstants.CardExpYear, creditCard.getExpirationYear());
			    	bBBVerifiedByVisaVO.setCentinel_PIType(creditCard.getCreditCardType().toUpperCase());
			    	vbvSessionBean.setbBBVerifiedByVisaVO(bBBVerifiedByVisaVO);
					try {
						centinelRequest.add(BBBVerifiedByVisaConstants.Version, getCatalogTools().getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVVersion).get(0));
						centinelRequest.add(BBBVerifiedByVisaConstants.MsgType, getCatalogTools().getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVLookupMsgType).get(0));
				    	centinelRequest.add(BBBVerifiedByVisaConstants.ProcessorId, getCatalogTools().getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVProcessorId).get(0));
				    	centinelRequest.add(BBBVerifiedByVisaConstants.MerchantId, getCatalogTools().getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVMerchantId).get(0));
				    	centinelRequest.add(BBBVerifiedByVisaConstants.TransactionPwd, getCatalogTools().getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVTransactionPwd).get(0));
				    	centinelRequest.add(BBBVerifiedByVisaConstants.TransactionType, getCatalogTools().getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVTransactionType).get(0));
				    	centinelRequest.add(BBBVerifiedByVisaConstants.OrderChannel, getCatalogTools().getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVOrderChannel).get(0));
				    	centinelRequest.add(BBBVerifiedByVisaConstants.ProductCode, getCatalogTools().getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVProductCode).get(0));
				    	centinelRequest.add(BBBVerifiedByVisaConstants.TransactionMode, getCatalogTools().getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVTransactionMode).get(0));
				    	centinelRequest.add(BBBVerifiedByVisaConstants.CurrencyCode, getCatalogTools().getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVCurrencyCode).get(0));
					}//if error exists in fetching ThirdPartyURLs then return false as lookUpFlag value.
					catch (BBBSystemException e) {
						this.logError("Error occured while fetching key from ThirdPartyURLs", e);
						return lookUpFlag;
					}
					catch (BBBBusinessException e) {
						this.logError("Error occured while fetching key from ThirdPartyURLs", e);
						return lookUpFlag;
					}
			        centinelRequest.add(BBBVerifiedByVisaConstants.UserAgent, pRequest.getHeader(BBBVerifiedByVisaConstants.User_Agent));
			    	centinelRequest.add(BBBVerifiedByVisaConstants.BrowserHeader, pRequest.getHeader(BBBVerifiedByVisaConstants.Accept));
			    	centinelRequest.add(BBBVerifiedByVisaConstants.IPAddress, pRequest.getRemoteAddr());
			    	
			    	if (bbbOrder.getOnlineBopusItemsStatusInOrder().equalsIgnoreCase(BBBCheckoutConstants.BOPUS_ONLY)) {
					   	centinelRequest.add(BBBVerifiedByVisaConstants.OrderNumber, bbbOrder.getBopusOrderNumber());
					}else {
						centinelRequest.add(BBBVerifiedByVisaConstants.OrderNumber, bbbOrder.getOnlineOrderNumber());
					}
			 
			    	centinelRequest.add(BBBVerifiedByVisaConstants.OrderDescription, bbbOrder.getDescription());
			    	//get amount charged by credit card
			    	double ccAmount = ((BBBPaymentGroupManager) this.getShippingGroupManager().getPaymentGroupManager()).getAmountCoveredByCreditCard(paymentGroups, bbbOrder);
			    	String creditCardAmount=vbvAmount(ccAmount);
			    	String shippingAmount = vbvAmount(bbbOrder.getPriceInfo().getShipping());
			    	String taxAmount = vbvAmount(bbbOrder.getPriceInfo().getTax());
			    	centinelRequest.add(BBBVerifiedByVisaConstants.Amount, creditCardAmount);
			    	centinelRequest.add(BBBVerifiedByVisaConstants.ShippingAmount, shippingAmount);
			    	centinelRequest.add(BBBVerifiedByVisaConstants.TaxAmount, taxAmount);
			    	//add billing and shipping details.
			    	centinelRequest.add(BBBVerifiedByVisaConstants.BillingFirstName, bbbOrder.getBillingAddress().getFirstName());
			    	centinelRequest.add(BBBVerifiedByVisaConstants.BillingMiddleName, bbbOrder.getBillingAddress().getMiddleName());
			    	centinelRequest.add(BBBVerifiedByVisaConstants.BillingLastName, bbbOrder.getBillingAddress().getLastName());
			    	centinelRequest.add(BBBVerifiedByVisaConstants.BillingAddress1, bbbOrder.getBillingAddress().getAddress1());
			    	centinelRequest.add(BBBVerifiedByVisaConstants.BillingAddress2, bbbOrder.getBillingAddress().getAddress2());
			    	centinelRequest.add(BBBVerifiedByVisaConstants.BillingCity, bbbOrder.getBillingAddress().getCity());
			    	centinelRequest.add(BBBVerifiedByVisaConstants.BillingState, bbbOrder.getBillingAddress().getState());
			    	centinelRequest.add(BBBVerifiedByVisaConstants.BillingPostalCode, bbbOrder.getBillingAddress().getPostalCode());
			    	centinelRequest.add(BBBVerifiedByVisaConstants.BillingCountryCode, bbbOrder.getBillingAddress().getCountry());
			    	centinelRequest.add(BBBVerifiedByVisaConstants.BillingPhone, bbbOrder.getBillingAddress().getMobileNumber());
			    	if(bbbOrder.getShippingAddress()!=null){
				    	centinelRequest.add(BBBVerifiedByVisaConstants.ShippingFirstName, bbbOrder.getShippingAddress().getFirstName());
				    	centinelRequest.add(BBBVerifiedByVisaConstants.ShippingMiddleName, bbbOrder.getShippingAddress().getMiddleName());
				    	centinelRequest.add(BBBVerifiedByVisaConstants.ShippingLastName, bbbOrder.getShippingAddress().getLastName());
				    	centinelRequest.add(BBBVerifiedByVisaConstants.ShippingAddress1, bbbOrder.getShippingAddress().getAddress1());
				    	centinelRequest.add(BBBVerifiedByVisaConstants.ShippingAddress2, bbbOrder.getShippingAddress().getAddress2());
				    	centinelRequest.add(BBBVerifiedByVisaConstants.ShippingCity, bbbOrder.getShippingAddress().getCity());
				    	centinelRequest.add(BBBVerifiedByVisaConstants.ShippingState, bbbOrder.getShippingAddress().getState());
				    	centinelRequest.add(BBBVerifiedByVisaConstants.ShippingPostalCode, bbbOrder.getShippingAddress().getPostalCode());
				    	centinelRequest.add(BBBVerifiedByVisaConstants.ShippingCountryCode, bbbOrder.getShippingAddress().getCountry());
				    	centinelRequest.add(BBBVerifiedByVisaConstants.ShippingPhone, bbbOrder.getShippingAddress().getMobileNumber());
				    	centinelRequest.add(BBBVerifiedByVisaConstants.EMail, bbbOrder.getShippingAddress().getEmail());
			    	}else{
			    		logDebug("Shipping address is NULL foro Order ID ["+bbbOrder.getId()+"]");
			    	}
			    	//add deatil of each commerce item.
					List<CommerceItem> itemList = bbbOrder.getCommerceItems();
					BBBCommerceItem bbbItem = null;
			    	for (int itemCount=0; itemCount<itemList.size(); itemCount++) {
						if (itemList.get(itemCount) instanceof BBBCommerceItem) {
							bbbItem = (BBBCommerceItem) itemList.get(itemCount);
					    	centinelRequest.add(BBBVerifiedByVisaConstants.Item_Name + itemCount, bbbItem.getRepositoryItem().getItemDisplayName());
					    	centinelRequest.add(BBBVerifiedByVisaConstants.Item_Desc + itemCount, bbbItem.getRepositoryItem().getItemDisplayName());
					    	centinelRequest.add(BBBVerifiedByVisaConstants.Item_SKU + itemCount, bbbItem.getCatalogRefId());
					    	centinelRequest.add(BBBVerifiedByVisaConstants.Item_Price + itemCount, vbvAmount(bbbItem.getPriceInfo().getAmount()));
					    	centinelRequest.add(BBBVerifiedByVisaConstants.Item_Quantity + itemCount, Long.toString(bbbItem.getQuantity()));
					    	//this method adds jda depatment, jda sub department and jda class in centinel request object.
					    	populateRepositoryItemDetails(centinelRequest, bbbItem, itemCount);
						}
			    	}
			    	lookUpFlag=true;
				}
			}
		}
		//if payment group is credit card and card type is visa or mastercard then return lookUpFlag as true else return false.
		logDebug("End vbvCentinelAddLookupRequest method of BBBCheckoutManager for Order Id: " + bbbOrder.getId());
		return lookUpFlag;
	}

	/**
	 * @param centinelRequest
	 * @param bbbItem
	 * @param itemCount
	 * @return
	 */
	private void populateRepositoryItemDetails(
			CentinelRequest centinelRequest, BBBCommerceItem bbbItem,
			int itemCount) {
		RepositoryItem skuRepositoryItem = null;
		try {
			skuRepositoryItem = this.getCatalogRepository().getItem(bbbItem.getCatalogRefId(),
			        BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
			if(skuRepositoryItem!=null){
		    	String jdaDept = "";
		        String jdaSubDept = "";
		        String jdaClass = "";
		    	if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME) != null) {
		            jdaDept = ((RepositoryItem) skuRepositoryItem
		                            .getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME))
		                            .getRepositoryId();
		    	}
		    	if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME) != null) {
		            jdaSubDept = ((RepositoryItem) skuRepositoryItem
		                            .getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME))
		                            .getRepositoryId();
		    	}
		    	if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME) != null) {
		            jdaClass = (String) skuRepositoryItem
		                            .getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME);
		    	}
		    	centinelRequest.add(BBBVerifiedByVisaConstants.Item_jdaDept + itemCount, jdaDept);
		    	centinelRequest.add(BBBVerifiedByVisaConstants.Item_jdaSubDept + itemCount, jdaSubDept);
		    	centinelRequest.add(BBBVerifiedByVisaConstants.Item_jdaClass + itemCount, jdaClass);
			}
		} catch (RepositoryException e) {
		    this.logError("commitOrderFormHandler Method Name [addCentinelLookupRequest]: RepositoryException "
		            + BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION);
		}
	}

	/**
	 * This method is used to remove decimals. For VBV amount to be sent without decimals egs. 14899 in place of $148.99
	 * @param double amount
	 * @return String vbvamount
	 */
	private String vbvAmount (double amount){
		
		String vbvamount = Double.toString(amount*100).substring(0, Double.toString(amount*100).indexOf("."));
		return vbvamount;
	}
	
	
	/**
	 * This method calls sendHTTP method to make thin client call for Lookup and update order accordingly with response attributes.
	 * @param centinelRequest
	 * @return void
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
    
	public boolean vbvCentinelSendLookupRequest(CentinelRequest centinelRequest,	String cardVerNumber, BBBOrder bbbOrder,
			boolean errorExists, SimpleOrderManager orderManager, BBBVBVSessionBean vbvSessionBean) throws CommerceException, BBBSystemException, BBBBusinessException{
		
		logDebug("Start vbvCentinelSendLookupRequest method of BBBCheckoutManager for Order Id: " + bbbOrder.getId());
		//this.getCheckoutTools().vbvCentinelSendLookupRequest calls sendHTTP method to make thin client call. Response of this call is set in BBBVerifiedByVisaVO and this vo is set in session bean.
		BBBVerifiedByVisaVO bBBVerifiedByVisaVO = this.getCheckoutTools().vbvCentinelSendLookupRequest(centinelRequest, cardVerNumber, bbbOrder, vbvSessionBean);
		//this.getCheckoutTools().vbvUpdateLookupOrderAttributes method updates order accordingly with response attributes.
		errorExists = this.getCheckoutTools().vbvUpdateLookupOrderAttributes(bbbOrder, bBBVerifiedByVisaVO, errorExists, orderManager);
		logDebug("End vbvCentinelSendLookupRequest method of BBBCheckoutManager for Order Id: " + bbbOrder.getId());
		return errorExists;
	}
	
	
	/**
	 * First this method is used to add name / value pairs to request object to construct XML for authenticate request.
	 * Second this method calls sendHTTP method to make thin client call for Authenticate and update order accordingly with response attributes.
	 * @param bbbOrder is object of BBBOrder
	 * @param bBBVerifiedByVisaVO
	 * @param authenticateResponse is the response recieved from acs url
	 * @param centinelRequest
	 * @param messageHandler is used to get error messages from error repository.
	 * @param orderManager is used to call update order
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws CommerceException is thrown when update order call fails
	 */
	public void vbvCentinelAuthenticateRequest(BBBOrder bbbOrder, BBBVerifiedByVisaVO bBBVerifiedByVisaVO, String authenticateResponse, CentinelRequest centinelRequest,
			LblTxtTemplateManager messageHandler, SimpleOrderManager orderManager, BBBVBVSessionBean vbvSessionBean) throws BBBSystemException, BBBBusinessException, CommerceException{
		
		logDebug("Start vbvCentinelAuthenticateRequest method of BBBCheckoutManager for Order Id: " + bbbOrder.getId());
		vbvAddAuthenticateRequest(centinelRequest, authenticateResponse, vbvSessionBean);
		bBBVerifiedByVisaVO = this.getCheckoutTools().vbvSendAuthenticateRequest(centinelRequest, bbbOrder, vbvSessionBean);
		this.getCheckoutTools().vbvUpdateAuthOrderAttributes(bbbOrder, bBBVerifiedByVisaVO, messageHandler, orderManager);
		logDebug("End vbvCentinelAuthenticateRequest method of BBBCheckoutManager for Order Id: " + bbbOrder.getId());
	}
	
	/**
	 * This method calls add method of CentinelRequest class to add name / value pairs to request object to construct XML for Authenticate call.
	 * @param centinelRequest
	 * @param authenticateResponse recieved fromacs url
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public final void vbvAddAuthenticateRequest(CentinelRequest centinelRequest, String authenticateResponse, BBBVBVSessionBean vbvSessionBean) throws BBBSystemException, BBBBusinessException{
		
		logDebug("Start vbvAddAuthenticateRequest method of BBBCheckoutManager");
		centinelRequest.add(BBBVerifiedByVisaConstants.Version, getCatalogTools().getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVVersion).get(0));
		centinelRequest.add(BBBVerifiedByVisaConstants.MsgType, getCatalogTools().getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVAuthenticateMsgType ).get(0));
		centinelRequest.add(BBBVerifiedByVisaConstants.ProcessorId, getCatalogTools().getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVProcessorId).get(0));
		centinelRequest.add(BBBVerifiedByVisaConstants.MerchantId, getCatalogTools().getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVMerchantId).get(0));
		centinelRequest.add(BBBVerifiedByVisaConstants.TransactionPwd, getCatalogTools().getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVTransactionPwd).get(0));
		centinelRequest.add(BBBVerifiedByVisaConstants.TransactionType, getCatalogTools().getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVTransactionType).get(0));
		centinelRequest.add(BBBVerifiedByVisaConstants.OrderId, vbvSessionBean.getbBBVerifiedByVisaVO().getOrderId());
		centinelRequest.add(BBBVerifiedByVisaConstants.PAResPayload, authenticateResponse);
		logDebug("End vbvAddAuthenticateRequest method of BBBCheckoutManager");
	}
	
	/**
	 * @return the catalogRepository
	 */
	public MutableRepository getCatalogRepository() {
		return catalogRepository;
	}

	/**
	 * @param catalogRepository the catalogRepository to set
	 */
	public void setCatalogRepository(MutableRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}

    /**
     * This method validates if token exist in order or not.
     * @param token
     * @param order
     * @return
     */
    public boolean validateToken(String token, BBBOrderImpl order, Profile profile){
    	
    	return this.getCheckoutTools().validatePayPalToken(token, order, profile);
    }
    
    /**
     * This method process paypal after returning from PayPal on basis of parameters recieved from request
     * 
     * @param validateAddress
     * @param voResp
     * @param order
     * @param validateShippingMethod
     * @throws ServletException
     * @throws IOException
     */
    public void processPayPal(BBBGetExpressCheckoutDetailsResVO voResp, BBBOrderImpl order, String validateShippingMethod, PayPalInputVO paypalInput) throws ServletException, IOException{
    	
    	DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
    	DynamoHttpServletResponse response = ServletUtil.getCurrentResponse();
    
    	//set Response in Session so that we do not have to call getExpCheckout webservice again. 
		//Once user lands on shipping page, we will reset address and error list to null
    	paypalInput.getPaypalSessionBean().setGetExpCheckoutResponse(voResp);
    	boolean addressExistInOrder = getPaypalServiceManager().addressInOrder(order);
		/*********************************************************************
		 * This block will validate shipping method against the items present in cart
		 * ********************************************************************/
		if(addressExistInOrder && !StringUtils.isEmpty(validateShippingMethod)){
			logDebug("Shipping Address exists in Order, Validating Shipping method : Starts");
			String ShippingMethodType = "";
			try {
				ShippingMethodType = getPaypalServiceManager().validateShippingMethod(order, paypalInput.getProfile());
			} catch (BBBSystemException e) {
				logError("PayPalDroplet.service() :: System Exception while the calling validating Shipping Group" + e);
			}
			if(!StringUtils.isEmpty(ShippingMethodType) && ShippingMethodType.equals(BBBCoreConstants.SHIP_METHOD_EXPEDIATED_ID)){
				logInfo("Shipping method changed - " + ShippingMethodType);
				logInfo("Shipping method restriction - " + BBBCoreErrorConstants.PAYPAL_CAN_NOT_SHIP_TO_EXPEDIT);
				request.setParameter(BBBPayPalConstants.ISSHIPPINGMETHODCHANGE, BBBCoreErrorConstants.PAYPAL_CAN_NOT_SHIP_TO_EXPEDIT);
			}
			else if(!StringUtils.isEmpty(ShippingMethodType) && ShippingMethodType.equals(BBBCoreConstants.SHIP_METHOD_EXPRESS_ID)){
				logInfo("Shipping method changed - " + ShippingMethodType);
				logInfo("Shipping method restriction - " + BBBCoreErrorConstants.PAYPAL_CAN_NOT_SHIP_TO_EXPRESS);
				request.setParameter(BBBPayPalConstants.ISSHIPPINGMETHODCHANGE, BBBCoreErrorConstants.PAYPAL_CAN_NOT_SHIP_TO_EXPRESS);
			}
			//request.serviceParameter(BBBCoreConstants.OUTPUT, request, response);
		}
		
		/*This block will execute first time on paypal redirect jsp when we don't need to validate any address. Just
		 sending address fields for QAS validation on front end. If address already exist in order then 
		 qas is already validated, no need to send address for qas validation*/
		else{
			boolean validateOrderAddress = paypalInput.getPaypalSessionBean().isValidateOrderAddress();
			if(addressExistInOrder || validateOrderAddress){
				request.setParameter(BBBPayPalConstants.ADDRESS_IN_ORDER, Boolean.TRUE);
			}
			else{
				request.setParameter(BBBPayPalConstants.ADDRESS_IN_ORDER, Boolean.FALSE);
			}
		} 
    }
    
    
    /**
     * @param order
     * @return boolean
     * This method returns true if order contains LTL Item else returns false
     */
   public  boolean orderContainsLTLItem(final Order pOrder)
                   throws BBBSystemException, BBBBusinessException {
       
       if (pOrder != null) {

           @SuppressWarnings ("unchecked")
           final List<ShippingGroupImpl> shippingGroups = ((OrderImpl)pOrder).getShippingGroups();
           if(shippingGroups != null && !shippingGroups.isEmpty()){
        	   for(ShippingGroupImpl shippingGroup: shippingGroups){
        		   List<BBBShippingGroupCommerceItemRelationship> shippingGroupCIRList = shippingGroup.getCommerceItemRelationships();
        			for(final BBBShippingGroupCommerceItemRelationship shippingGroupCIR : shippingGroupCIRList){
        				if(shippingGroupCIR.getCommerceItem() instanceof BBBCommerceItem && ((BBBCommerceItem)shippingGroupCIR.getCommerceItem()).isLtlItem()){
        					return true;
        				}
        			}
               }
           }
       }
       return false;
   }
   
   /**
    * @param order
    * @return boolean
    * This method returns true if order contains LTL Item else returns false
    */
  public final boolean orderContainsVDCItem(final Order pOrder)
                  throws BBBSystemException, BBBBusinessException {
      
      if (pOrder != null) {

          @SuppressWarnings ("unchecked")
          final List<ShippingGroupImpl> shippingGroups = ((OrderImpl)pOrder).getShippingGroups();
          if(shippingGroups != null && !shippingGroups.isEmpty()){
       	   for(ShippingGroupImpl shippingGroup: shippingGroups){
       		   List<BBBShippingGroupCommerceItemRelationship> shippingGroupCIRList = shippingGroup.getCommerceItemRelationships();
       			for(final BBBShippingGroupCommerceItemRelationship shippingGroupCIR : shippingGroupCIRList){
       				if(shippingGroupCIR.getCommerceItem() instanceof BBBCommerceItem && ((BBBCommerceItem)shippingGroupCIR.getCommerceItem()).isVdcInd()){
       					return true;
       				}
       			}
              }
          }
      }
      return false;
  }
   
   
   /**
    * @return boolean
    * This method returns true if order contains international restricted Commerce Item else returns false
    */
  public  String orderContainsIntlRestrictedItem(final Order pOrder)
                  throws BBBSystemException, BBBBusinessException {
	  List<String> intlRestricedItems=new ArrayList<String>();
	  String intlRestrictedCommerceItems=null;
      
      if (pOrder != null) {
    	  List<CommerceItemVO> commerceItemVOs = null;
    	  commerceItemVOs = getCartItemVOList(pOrder);
    	  
    	  if(commerceItemVOs != null) {
    	  
    	  for(CommerceItemVO commerceItemVO:commerceItemVOs){
    		  if(commerceItemVO.getSkuDetailVO().isIntlRestricted()) {
    			  intlRestricedItems.add(commerceItemVO.getBBBCommerceItem().getId()+BBBCoreConstants.EQUAL+BBBCoreConstants.ZERO);
    			  
				}
    	  }
    	  }
	}
      if (!intlRestricedItems.isEmpty() && intlRestricedItems.size() > 0) {
			Iterator<String> itemIterator = intlRestricedItems.iterator();
			intlRestrictedCommerceItems = itemIterator.next();
			while (itemIterator.hasNext()) {
				intlRestrictedCommerceItems += BBBCoreConstants.SEMICOLON + itemIterator.next();
			}
		
		}
      
      return intlRestrictedCommerceItems;
  }
  public String getKatoriAvailability() {
		String katoriFlag = null;
		try {
			katoriFlag = getCatalogTools().getConfigValueByconfigType(BBBCoreConstants.EXIM_KEYS).get(BBBCoreConstants.ENABLE_KATORI);
		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(null, "BBBSystemException occurs in BBBEximPricingManager.getKatoriAvailability "
					+ ":: katoriFlag = " + katoriFlag), e);
		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(null, "BBBSystemException occurs in BBBEximPricingManager.getKatoriAvailability "
					+ ":: katoriFlag = " + katoriFlag), e);
		}
		return katoriFlag;
	}
  /**
   * @return boolean
   * This method returns true if order contains international restricted Commerce Item else returns false
   */
  public  boolean orderContainsErrorPersonalizedItem(final Order pOrder)
                 throws BBBSystemException, BBBBusinessException {
     
     if (pOrder != null) {
    	final List<CommerceItem> commerceItems = pOrder.getCommerceItems();
   	  	for(CommerceItem commItem : commerceItems){
   	  		if(commItem instanceof BBBCommerceItem && BBBUtility.isNotEmpty(((BBBCommerceItem)commItem).getReferenceNumber())
   	  				&&("false".equalsIgnoreCase(getKatoriAvailability()) || ((BBBCommerceItem)commItem).isEximErrorExists())) {
					return true;
				}
   	  		}
          }
     return false;
 }/**
   * @return boolean
   * This method returns true if GifListVO contains error while calling exim Web service else returns false
   */
  public  boolean savedItemsContainErrorPersonalizedItem(List<GiftListVO> giftList)
                 throws BBBSystemException, BBBBusinessException {
     
     if (giftList != null) {
   	  	
   	  	for(GiftListVO giftListVO : giftList){
   	  		if(BBBUtility.isNotEmpty(giftListVO.getReferenceNumber())
   	  				&& (giftListVO.isEximErrorExists())){
   	  			return true;
   	  		}
   	  	}
     }
     return false;
 }
   
   /**
    * Return true if any shipping group is empty or null
    * @param order
    * @return
    */
   public final boolean orderContainsEmptySG(final Order order) {
	   List <ShippingGroup> shippingGroups = order.getShippingGroups();
	   for(ShippingGroup shippingGroup : shippingGroups)
	   {
		   if(shippingGroup instanceof BBBHardGoodShippingGroup){
			   if(BBBUtility.isEmpty(shippingGroup.getShippingMethod())){
				   return true;
			   }
		   }
	   }
	return false;
   }
   /**
    * @return boolean
    * This method returns true if order contains personalized Commerce Item else returns false
    */
  public  boolean ordercContainsPersonalizedItem(final Order pOrder)
                  throws BBBSystemException, BBBBusinessException {
      
      if (pOrder != null) {
    	  List<CommerceItemVO> commerceItemVOs = null;
    	  commerceItemVOs = getCartItemVOList(pOrder);
    	  
    	  if(commerceItemVOs != null) {
    	  
    	  for(CommerceItemVO commerceItemVO:commerceItemVOs){
    		 
    		  if((commerceItemVO.getBBBCommerceItem().getReferenceNumber() != null) && (!commerceItemVO.getBBBCommerceItem().getReferenceNumber().equalsIgnoreCase(""))) {
					return true;
				}
    	  }
    	  }
	}
      return false;
  }
  
   public boolean isPostOfficeBoxAddress(Address address) {
    boolean isPOAddress = false;
  	if (!BBBUtility.isNonPOBoxAddress(((BBBAddress) address).getAddress1(),
  			((BBBAddress) address).getAddress2())) {
  		isPOAddress = true;
  	}
  	return isPOAddress;
  }

  
  /**
   * Mobile Rest Service - Fetches coupon list by input email, phoneNumber.
   *
   * @param email the email
   * @param phoneNumber the phone number
   * @return the applied coupons vo
   * @throws RepositoryException the repository exception
   * @throws CommerceException the commerce exception
   * @throws BBBSystemException the BBB system exception
   * @throws BBBBusinessException the BBB business exception
   */
  public AppliedCouponsVO fetchCouponList(String email, String phoneNumber) throws BBBSystemException {
	  logDebug("BBBCheckoutManager.fetchCouponList : Starts");
	  String couponOn = null;
	  final DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
	  final AppliedCouponsVO appliedCouponsVO = new AppliedCouponsVO();
	  Map<String, RepositoryItem> promotions = new HashMap<String, RepositoryItem>();
	  OrderHolder cart = (OrderHolder) pRequest.resolveName(BBBCoreConstants.SHOPPING_CART_PATH);
	  BBBOrderImpl order = (BBBOrderImpl) cart.getCurrent();
	  final String orderSiteId = order.getSiteId();
	  try{
		  final Map<String, String> configMap = this.getCatalogTools().getConfigValueByconfigType(
				  BBBCoreConstants.CONTENT_CATALOG_KEYS);
		  RepositoryItem profile = this.getProfileTools().getProfileForOrder(order.getId());
		  Profile profileObj = (Profile) ServletUtil.getCurrentRequest().resolveName(BBBCoreConstants.ATG_PROFILE);
		  if (orderSiteId.equalsIgnoreCase(configMap.get(BBBCoreConstants.BED_BATH_US_SITE_CODE))) {
			  couponOn = this.getCatalogTools().getContentCatalogConfigration(BBBCoreConstants.COUPON_TAG_US).get(0);
		  } else if (orderSiteId.equalsIgnoreCase(configMap.get(BBBCoreConstants.BUY_BUY_BABY_SITE_CODE))) {
			  couponOn = this.getCatalogTools().getContentCatalogConfigration(BBBCoreConstants.COUPON_TAG_BABY).get(0);
		  } else if (orderSiteId.equalsIgnoreCase(configMap.get(BBBCoreConstants.BED_BATH_CANADA_SITE_CODE))) {
			  couponOn = this.getCatalogTools().getContentCatalogConfigration(BBBCoreConstants.COUPON_TAG_CA).get(0);
		  }
		  if (couponOn == null) {
			  couponOn = "TRUE";
		  }
		  @SuppressWarnings ("unchecked")
		  final List<RepositoryItem> availablePromotions = (List<RepositoryItem>) this.getProfileTools().getProfileForOrder(order.getId())
		  .getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST);

		  if (couponOn.equalsIgnoreCase("TRUE")) {
			  // remove already granted promotion
			  for (final Object element : availablePromotions) {
				  final RepositoryItem promotion = (RepositoryItem) element;
				  this.getProfileTools().getPromotionTools().removePromotion((MutableRepositoryItem) profile, promotion,
						  false);
			  }
			  availablePromotions.clear();
			  ((MutableRepositoryItem) profile).setPropertyValue(
					  BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST, availablePromotions);
			  ((BBBPromotionTools) this.getProfileTools().getPromotionTools()).initializePricingModels();
			  ((BBBPromotionTools) this.getProfileTools().getPromotionTools()).getPromoManager().populateCouponsFromService(order, orderSiteId, false, promotions,
					  email, null, phoneNumber);
			  promotions = ((BBBPromotionTools) this.getProfileTools().getPromotionTools()).getCouponUtil().applySchoolPromotion(promotions,
					  (Profile) pRequest.resolveName(this.getProfileTools().getProfilePath()), order);
			  ((BBBOrderImpl) order).setCouponMap(promotions);
			  /*Resetting REFRESH_COUPON attribute after refreshing coupons with updated email*/
			  pRequest.getSession().setAttribute(BBBCoreConstants.REFRESH_COUPON, null);
			  ((BBBPromotionTools) this.getProfileTools().getPromotionTools()).populateAppliedCouponVO(order, profileObj, 
					  SiteContextManager.getCurrentSiteId(), appliedCouponsVO, promotions, order.getCouponListVo());
		  }
		  logDebug("BBBCheckoutManager.fetchCouponList : END");
	  } catch (RepositoryException re){
		  throw new BBBSystemException("RepositoryException occured", re);
	  } catch (CommerceException ce){
		  throw new BBBSystemException("CommerceException occured", ce);
	  } catch (BBBBusinessException be) {
		  throw new BBBSystemException("BBBBusinessException occured", be);
	  } catch (ServletException e) {
		  throw new BBBSystemException("ServletException occured", e);
	  } catch (IOException e) {
		  throw new BBBSystemException("IOException occured", e);
	  }
	  return appliedCouponsVO;
  }

 

  
public SearchStoreManager getStoreManager() {
	return this.searchStoreManager;
}

public void setStoreManager(SearchStoreManager searchStoreManager) {
	this.searchStoreManager = searchStoreManager;
}

  
}
