package com.bbb.rest.generic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.transaction.TransactionManager;

import com.bbb.account.BBBAddressTools;
import com.bbb.account.BBBProfileTools;
import com.bbb.account.order.manager.OrderDetailsManager;
import com.bbb.account.webservices.BBBProfileServices;
import com.bbb.browse.webservice.manager.BBBEximManager;
import com.bbb.commerce.cart.bean.CommerceItemVO;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.checkout.droplet.GetCreditCardsForPayment;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.commerce.common.BBBAddress;
import com.bbb.commerce.common.BBBAddressContainer;
import com.bbb.commerce.common.BBBOrderVO;
import com.bbb.commerce.common.BBBRepositoryContactInfo;
import com.bbb.commerce.common.BBBStoreInventoryContainer;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.order.BBBPaymentGroupManager;
import com.bbb.commerce.order.formhandler.BBBShippingGroupFormhandler;
import com.bbb.commerce.order.purchase.BBBPurchaseProcessHelper;
import com.bbb.commerce.order.purchase.BBBShippingAddressDroplet;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.ecommerce.order.BBBShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.rest.checkout.vo.CreditCardInfoVO;
import com.bbb.utils.BBBUtility;

import atg.beans.PropertyNotFoundException;
import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.ShippingGroupImpl;
import atg.commerce.order.ShippingGroupRelationship;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import atg.repository.servlet.RQLQueryRange;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

/** @author sjatas */
public class BBBSessionManagerFacade extends BBBGenericService {

    private static final String ADDRESS_ERROR_1023 = "addressError:1023";
    private static final String PROFILE_ADDRESSES = "profileAddresses";
    private static final String ATG_COMMERCE_SHOPPING_CART = "/atg/commerce/ShoppingCart";
    private static final String ERROR_CHECKING_THE_ELIGIBILITY_EXPRESS_CHECKOUT = "Some error occurred while checking the eligibility of user for express checkout";
    private static final String GROUP_ADDRESSES = "groupAddresses";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String KEYWORDS = "keywords";
    private static final String SEOTAGS = "SEOTags";
    private static final String SEOREPOSITORY = "/atg/seo/SEORepository";
    private static final String QUERYRQL = "key = :key";
    private static final String DISPLAYNAME = "displayName";
    private static final String REGISTRIES_ADDRESSES = "registriesAddresses";
    private static final String DEFAULT_PROFILE_ADDRESSES = "defaultProfileAddress";

    private BBBCheckoutManager checkoutManager;
    private GetCreditCardsForPayment creditCardsForPayment;
    private OrderDetailsManager orderDetailsManager;
    private RQLQueryRange rqlQueryRange;
    private BBBOrder mOrder;
    private Profile mProfile;
    private BBBProfileServices profileService;
    private BBBAddressContainer addresscontainer;
    private BBBShippingAddressDroplet addressDisplayDroplet;
    private BBBPurchaseProcessHelper purchaseProcessHelper;
    private BBBStoreInventoryContainer storeInventoryContainer;
    private BBBPaymentGroupManager paymentGroupManager;
    private BBBCatalogTools catalogTools;
    private BBBShippingGroupFormhandler shipGroupFormHandler;
    private BBBGenericSessionComponent bbbGenericSessionComponent;

    private BBBEximManager eximManager;
    
    private BBBProfileTools profileTools;
	
    public BBBProfileTools getProfileTools() {
		return profileTools;
	}

	public void setProfileTools(BBBProfileTools profileTools) {
		this.profileTools = profileTools;
	}

	public BBBGenericSessionComponent getBbbGenericSessionComponent() {
		return bbbGenericSessionComponent;
	}

	public void setBbbGenericSessionComponent(
			BBBGenericSessionComponent bbbGenericSessionComponent) {
		this.bbbGenericSessionComponent = bbbGenericSessionComponent;
	}

	public BBBShippingGroupFormhandler getShipGroupFormHandler() {
	return shipGroupFormHandler;
}

public void setShipGroupFormHandler(
		BBBShippingGroupFormhandler shipGroupFormHandler) {
	this.shipGroupFormHandler = shipGroupFormHandler;
}
    /** @return the rqlQueryRange */
    public final RQLQueryRange getRqlQueryRange() {
        return this.rqlQueryRange;
    }

    /** @param rqlQueryRange the rqlQueryRange to set */
    public final void setRqlQueryRange(final RQLQueryRange rqlQueryRange) {
        this.rqlQueryRange = rqlQueryRange;
    }

    /** @return the mPaymentGroupManager */
    public final BBBPaymentGroupManager getPaymentGroupManager() {
        return this.paymentGroupManager;
    }

    /** @param mPaymentGroupManager the mPaymentGroupManager to set */
    public final void setPaymentGroupManager(final BBBPaymentGroupManager mPaymentGroupManager) {
        this.paymentGroupManager = mPaymentGroupManager;
    }

    /** @return */
    public final BBBStoreInventoryContainer getStoreInventoryContainer() {
        return this.storeInventoryContainer;
    }

    /** @param mStoreInventoryContainer */
    public final void setStoreInventoryContainer(final BBBStoreInventoryContainer mStoreInventoryContainer) {
        this.storeInventoryContainer = mStoreInventoryContainer;
    }

    /** @return */
    public final BBBPurchaseProcessHelper getPurchaseProcessHelper() {
        return this.purchaseProcessHelper;
    }

    /** @param purchaseProcessHelper */
    public final void setPurchaseProcessHelper(final BBBPurchaseProcessHelper purchaseProcessHelper) {
        this.purchaseProcessHelper = purchaseProcessHelper;
    }

    /** @return the address container */
    public final BBBAddressContainer getAddresscontainer() {
        return this.addresscontainer;
    }

    /** @param addresscontainer the address container to set */
    public final void setAddresscontainer(final BBBAddressContainer addresscontainer) {
        this.addresscontainer = addresscontainer;
    }

    /** @return the profileServices */
    public final BBBProfileServices getProfileService() {
        return this.profileService;
    }

    /** @param profileService the profileServices to set */
    public final void setProfileService(final BBBProfileServices profileService) {
        this.profileService = profileService;
    }

    /** @return the orderDetailsManager */
    public final OrderDetailsManager getOrderDetailsManager() {
        return this.orderDetailsManager;
    }

    /** @param orderDetailsManager the orderDetailsManager to set */
    public final void setOrderDetailsManager(final OrderDetailsManager orderDetailsManager) {
        this.orderDetailsManager = orderDetailsManager;
    }

    /** @return the checkoutManager */
    public final BBBCheckoutManager getCheckoutManager() {
        return this.checkoutManager;
    }

    /** @param checkoutManager the checkoutManager to set */
    public final void setCheckoutManager(final BBBCheckoutManager checkoutManager) {
        this.checkoutManager = checkoutManager;
    }

    /** @return the mOrder */
    public final BBBOrder getOrder() {
        return (BBBOrderImpl) ((OrderHolder) ServletUtil.getCurrentRequest().resolveName(ATG_COMMERCE_SHOPPING_CART)).getCurrent();
    }

    /** @param mOrder the mOrder to set */
    public final void setOrder(final BBBOrder pOrder) {
        this.mOrder = pOrder;
    }

    /** @return the mProfile */
    public final Profile getProfile() {
    	return (Profile) ServletUtil.getCurrentRequest().resolveName("/atg/userprofiling/Profile");
    }

    /** @param mProfile the mProfile to set */
    public final void setProfile(final Profile pProfile) {
        this.mProfile = pProfile;
    }

    /** method to check if express checkout is allowed.
     *
     * @return Success / Failure
     * @throws BBBBusinessException
     * @throws BBBSystemException */
    public final boolean checkExpressCheckoutStatusForOrder() throws BBBBusinessException, BBBSystemException {

        boolean isExpressCheckoutAllowed = false;

        try {
            isExpressCheckoutAllowed = this.getCheckoutManager().displayExpressCheckout(this.getProfile(),
                            this.getOrder(), SiteContextManager.getCurrentSiteId());

        } catch (final BBBSystemException e) {
            this.logError(ERROR_CHECKING_THE_ELIGIBILITY_EXPRESS_CHECKOUT, e);
            throw new BBBSystemException(BBBCoreErrorConstants.EXPRESS_CHECKOUT_CHECK_ERR,
                            ERROR_CHECKING_THE_ELIGIBILITY_EXPRESS_CHECKOUT);

        } catch (final BBBBusinessException e) {
            this.logError("Business error occurred while checking the eligibility of user for express checkout", e);
            return false;

        } catch (final PropertyNotFoundException e) {
            this.logError(ERROR_CHECKING_THE_ELIGIBILITY_EXPRESS_CHECKOUT, e);
            throw new BBBSystemException(BBBCoreErrorConstants.EXPRESS_CHECKOUT_CHECK_ERR,
                            ERROR_CHECKING_THE_ELIGIBILITY_EXPRESS_CHECKOUT);

        }
        return isExpressCheckoutAllowed;
    }

    /** method to checks the type of shipping user is eligible for.
     *
     * @return type of shipping logged-in user is eligible for : single | Multi | Cart
     * @throws BBBSystemException */
    public final String checkShippingEligibilityForOrder() throws BBBSystemException {
        String eligibleShippingType = null;

        if (this.getOrder() == null) {
            this.logError("Order is null in BBBSessionManagerFacade : eligibleShippingType");
            throw new BBBSystemException(BBBCoreErrorConstants.ELIGIBLE_SHIPPING_TYPE_ERR,
                            "some error occurred in eligibleShippingType");
        }

        if (this.getOrder().getCommerceItemCount() <= 0) {
            eligibleShippingType = "EMPTY_CART";
         // second argument is false - as no need to check for single page functionality for mobile
        } else if (this.getCheckoutManager().displaySingleShipping(this.getOrder(), false)) {
            eligibleShippingType = "SINGLE_SHIP_ELIGIBLE";
        } else {
            eligibleShippingType = "SINGLE_SHIP_NOT_ELIGIBLE";
        }
        return eligibleShippingType;
    }

    /** Get Current Order Details.
     *
     * @param isItemLevelMsgReq Whether Item Level Message is required or not
     * @return BBB Order ValueObject
     * @throws BBBSystemException Exception
     * @throws BBBBusinessException Exception */
    public final BBBOrderVO getCurrentOrderDetails(final boolean isItemLevelMsgReq)
                    throws BBBSystemException, BBBBusinessException {
        BBBOrderVO bbbOrderVO = null;
        try {
            final DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
            ServletUtil.getCurrentResponse();
            if (isItemLevelMsgReq) {
                pRequest.setParameter(BBBCoreConstants.FROM_CART, BBBCoreConstants.TRUE);
            }
            final OrderHolder cart = (OrderHolder) pRequest.resolveName(ATG_COMMERCE_SHOPPING_CART);
            BBBOrderImpl order = (BBBOrderImpl) cart.getCurrent();
            List<String> bopusList=new ArrayList<String>();
            bopusList=checkAndUpdateInternationalOrder(order,bopusList);
            order=(BBBOrderImpl) cart.getCurrent();
           if("true".equalsIgnoreCase(eximManager.getKatoriAvailability())) {
        	   // calling exim WS in case of eximWebserviceFailure is true
               if(order.isEximWebserviceFailure()){
               	boolean status = eximManager.setEximDetailsbyMultiRefNumAPI(order.getCommerceItems(), order);
               }
           }
           
            bbbOrderVO = this.getOrderDetailsManager().getOrderDetailsVO(order, false);
            if(bopusList.size()>0){
            	bbbOrderVO.setBopusListForIS(bopusList);
            }


        } catch (final CommerceException e) {
            this.logError("Commerce Exception while getting current order details", e);
        }
        return bbbOrderVO;
    }
    
 
	/**
	/**
	 * Get Current Order Details.
	 *
	 * @param isItemLevelMsgReq
	 *            Whether Item Level Message is required or not
	 * @param pageName
	 *            request originated from page
	 * @return BBBOrder ValueObject
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
    public final BBBOrderVO getCurrentOrderDetails(final boolean isItemLevelMsgReq, final String actionReq)
			throws BBBSystemException, BBBBusinessException {
		this.logDebug("getCurrentOrderDetails called from context: " + actionReq);
		BBBOrderVO bbbOrderVO = null;
		final TransactionDemarcation td = new TransactionDemarcation();
        boolean rollback = false;
		try {
			final DynamoHttpServletRequest pRequest = ServletUtil
					.getCurrentRequest();
			ServletUtil.getCurrentResponse();
			if (isItemLevelMsgReq) {
				pRequest.setParameter(BBBCoreConstants.FROM_CART,
						BBBCoreConstants.TRUE);
			}
			if (!StringUtils.isBlank(actionReq) && actionReq.equals(BBBCoreConstants.ITEM_LEVEL_EXP_DELIVERY_REQ)) {
				pRequest.setParameter(BBBCoreConstants.ITEM_LEVEL_EXP_DELIVERY, actionReq);
			}
			// BBBH-1486 story changes
			else if (!StringUtils.isBlank(actionReq)) {
				pRequest.setParameter(BBBCoreConstants.ACTION_REQ, actionReq);
			}
			
			final OrderHolder cart = (OrderHolder) pRequest
					.resolveName(ATG_COMMERCE_SHOPPING_CART);
			BBBOrderImpl order = (BBBOrderImpl) cart.getCurrent();
			
			Profile profile = (Profile) ServletUtil.getCurrentRequest().resolveName(BBBCoreConstants.ATG_PROFILE);
	    	
			/*
			 * SAP-2856 NPE in Mobile Checkout Flow
			 * If priceInfo is null, call repriceOrder to set priceInfo object.
			 */
			final TransactionManager tm = this.getPaymentGroupManager().getTransactionManager();
            if (tm != null) {
                td.begin(tm, TransactionDemarcation.REQUIRED);
            }
	    	if(order.getPriceInfo() == null) {
	    		this.getOrderDetailsManager().repriceCartMoveItemsFromCart(order, profile);
	    	}
			/*
			 * BBBH-7045 M|User unable to login from Cart page, - Starts. The
			 * following check is added to skip below method calls from cart
			 * page as we are getting null value for priceInfo object.
			 */
			if (!BBBCoreConstants.SAME_DAY_DELIVERY_REQ.equals(actionReq)) {
				getPaymentGroupManager().processPaymentGroupStatusOnLoad(order);
			}
			/*
			 * BBBH-7045 M|User unable to login from Cart page - Ends
			 */
			List<String> bopusList = new ArrayList<String>();
			bopusList = checkAndUpdateInternationalOrder(order, bopusList);
			order = (BBBOrderImpl) cart.getCurrent();
			bbbOrderVO = this.getOrderDetailsManager().getOrderDetailsVO(order,
					false);
			if (bopusList.size() > 0) {
				bbbOrderVO.setBopusListForIS(bopusList);
			}
		} catch (final CommerceException e) {
			this.logError(
					"Commerce Exception while getting current order details", e);
			rollback = true;
		} catch (TransactionDemarcationException tranDemException) {
			rollback = true;
			this.logError("TransactionDemarcation Excpetion during handleMaxGiftCardReachedAPI method",
                    tranDemException);
			throw new BBBSystemException(BBBCatalogErrorCodes.TRANSACTION_DEMARCATION_EXCEPTION,
                    "Transaction Demarcation Exception");
		}finally {
            endTransaction(td, rollback);
        }
		return bbbOrderVO;
	}

	/**
	 * @param td
	 * @param rollback
	 * @throws BBBSystemException
	 */
	protected void endTransaction(final TransactionDemarcation td,
			boolean rollback) throws BBBSystemException {
		try {
		    td.end(rollback);
		} catch (final TransactionDemarcationException tranDemException) {
		    this.logError("TransactionDemarcation Excpetion during handleMaxGiftCardReachedAPI method",
		                    tranDemException);
		    throw new BBBSystemException(BBBCatalogErrorCodes.TRANSACTION_DEMARCATION_EXCEPTION,
		                    "Transaction Demarcation Exception");
		}
	}

    
    /** check for International Order.
     *
      * @param order Order details
     * @param bopusSkuList list of bopus commerce items in order
     */
    private  List<String>  checkAndUpdateInternationalOrder(BBBOrderImpl order,List<String> bopusSkuList) {
    	//get all commerce items from cart
    	try {
			List<CommerceItemVO> commerceItemVOs = this.getCheckoutManager().getCartItemVOList(order);
			DynamoHttpServletRequest pRequest=ServletUtil.getCurrentRequest();
			DynamoHttpServletResponse pResponse=ServletUtil.getCurrentResponse();
			if(pRequest!=null){
			BBBSessionBean sessionBean = (BBBSessionBean) ServletUtil.getCurrentRequest().resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
			shipGroupFormHandler = (BBBShippingGroupFormhandler) ServletUtil.getCurrentRequest().resolveName("/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler");
			if(sessionBean.isInternationalShippingContext())
			{
				for(CommerceItemVO commerceItemVO:commerceItemVOs){
					if(BBBUtility.isNotEmpty(commerceItemVO.getBBBCommerceItem().getStoreId())){
						bopusSkuList.add(commerceItemVO.getBBBCommerceItem().getId());
						final List<ShippingGroupRelationship> shipGrpRelnList=commerceItemVO.getBBBCommerceItem().getShippingGroupRelationships();
						for(final ShippingGroupRelationship shipGrpReln:shipGrpRelnList){
							final ShippingGroupImpl shipGrp=	(ShippingGroupImpl) shipGrpReln.getShippingGroup();
							if(!(shipGrp instanceof HardgoodShippingGroup)){

								shipGroupFormHandler.setOldShippingId(shipGrp.getId());
								shipGroupFormHandler.setCommerceItemId(commerceItemVO.getBBBCommerceItem().getId());
								shipGroupFormHandler.setNewQuantity(Long.toString(commerceItemVO.getBBBCommerceItem().getQuantity()));
								try {
									shipGroupFormHandler.handleChangeToShipOnline(pRequest, pResponse);
								} catch (ServletException e) {
									logError(" servlet exception for commerce id"+commerceItemVO.getBBBCommerceItem().getId(),e);
								} catch (IOException e) {
									logError(" IOException exception for commerce id"+commerceItemVO.getBBBCommerceItem().getId(),e);
								}
							}
						}
					}
				}
			}
		}
		} catch (BBBSystemException | BBBBusinessException e) {
			this.logError("Commerce Exception while getting current order details", e);
		} 
    	return  bopusSkuList;
	}

    /** Get Last Order Details.
     *
     * @return BBB Order ValueObject
     * @throws BBBSystemException Exception
     * @throws BBBBusinessException Exception */
    public final BBBOrderVO getLastPlacedOrderDetails() throws BBBSystemException, BBBBusinessException {
        BBBOrderVO bbbOrderVO = null;
        try {
            this.logDebug("BBBSessionManagerFacade.getLastOrderDetails() method started");
            final DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
            final OrderHolder cart = (OrderHolder) pRequest.resolveName(ATG_COMMERCE_SHOPPING_CART);
            final BBBOrderImpl order = (BBBOrderImpl) cart.getLast();
			pRequest.setParameter(BBBCoreConstants.ITEM_LEVEL_EXP_DELIVERY, BBBCoreConstants.ITEM_LEVEL_EXP_DELIVERY_REQ);
            if (order != null) {
                bbbOrderVO = this.getOrderDetailsManager().getOrderDetailsVO(order, false , false);
                final String siteId = SiteContextManager.getCurrentSiteId();
                if(null!=bbbOrderVO){
	                bbbOrderVO.setOrderSubStatus(order.getSubStatus());
	                if ((siteId != null) && (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA) || siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA))
	                                && !StringUtils.isEmpty(bbbOrderVO.getSubmittedDate())) {
	                    bbbOrderVO.setSubmittedDate(BBBUtility.convertUSDateIntoWSFormatCanada(bbbOrderVO
	                                    .getSubmittedDate()));
	                }
                }
            } else {
                throw new BBBSystemException(BBBCoreErrorConstants.LAST_PLACED_ORDER_NULL,
                                BBBCoreErrorConstants.LAST_PLACED_ORDER_NULL);
            }

        } catch (final CommerceException e) {
            this.logDebug("Commerce Exception while getting last placed order details ", e);
        }
        return bbbOrderVO;
    }

    /** Get Current Order Details.
     *
     * @return Success / Failure
     * @throws BBBSystemException Exception
     * @throws BBBBusinessException Exception */
    public final Boolean logoutUser() throws BBBSystemException, BBBBusinessException {

        try {
            this.getProfileService().logoutUser();
        } catch (final ServletException e) {
            throw new BBBSystemException(BBBCoreErrorConstants.ERROR_LOGOUT_USER_INVALID_PROFILE,
                            "Error occured while logging out user",e);
        }

        return true;

    }

    /** This method checks the pack&hold eligibility of the order.
     *
     * @return boolean -depending on the eligibility of the order */
    public final boolean checkOrderPackAndHoldEligibility(String isFromSPC) {
    	boolean isfromSPC = false;
    	if(!BBBUtility.isEmpty(isFromSPC) && BBBCoreConstants.TRUE.equalsIgnoreCase(isFromSPC)){
    		isfromSPC = true;
    	}
        final DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
        final String siteId = SiteContextManager.getCurrentSiteId();
        final OrderHolder cart = (OrderHolder) pRequest.resolveName(ATG_COMMERCE_SHOPPING_CART);
        final BBBOrderImpl order = (BBBOrderImpl) cart.getCurrent();
        if(isfromSPC && order.isBopusOrder()){
        	return false;
        }

        return this.getCheckoutManager().hasAllPackNHoldItems(siteId, order, isfromSPC);
    }

    /** This method is used to get Profile information for a loggedin user.
     *
     * @return Profile Information */
    public final Profile getProfileInfo() {

        return this.getProfile();
    }

    /** Get address details from current order and profile.
     *
     * @return Addresses Details
     * @throws BBBSystemException Exception
     * @throws IOException Exception
     * @throws ServletException Exception */
    public final Map<String, Map<String, BBBAddress>> getAllAddresses()
                    throws BBBSystemException, ServletException, IOException {
        this.logDebug("START METHOD BBBShippingAddressDroplet");
        final Map<String, Map<String, BBBAddress>> addressMap = new HashMap<String, Map<String, BBBAddress>>();
        try {

            final DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
            final DynamoHttpServletResponse response = ServletUtil.getCurrentResponse();
            final Profile profile = (Profile) request.resolveName(BBBCoreConstants.ATG_PROFILE);
            final OrderHolder cart = (OrderHolder) request.resolveName(ATG_COMMERCE_SHOPPING_CART);

            final BBBOrderImpl inSessionOrder = (BBBOrderImpl) cart.getCurrent();

            request.setParameter(BBBCoreConstants.PROFILE, profile);
            request.setParameter(BBBCoreConstants.ORDER, inSessionOrder);
            request.setParameter("billingAddrContainer", new BBBAddressContainer());
            request.setParameter("addressContainer", this.getAddresscontainer());

            this.getAddressDisplayDroplet().service(request, response);
            final List<BBBAddress> registryAdd = (List<BBBAddress>) request.getLocalParameter(REGISTRIES_ADDRESSES);
            final List<BBBAddress> profileAdd = (List<BBBAddress>) request.getLocalParameter(PROFILE_ADDRESSES);
            final List<BBBAddress> groupAdd = (List<BBBAddress>) request.getLocalParameter(GROUP_ADDRESSES);
            final BBBAddress defaultShippingAddress = (BBBAddress) request.getLocalParameter("defaultShippingAddress");
            final BBBAddress defaultBillingAddress = (BBBAddress) request.getLocalParameter("defaultBillingAddress");

            final Map<String, BBBAddress> defaultAddressMap = new HashMap<String, BBBAddress>();
            defaultAddressMap.put("defaultShippingAddress", defaultShippingAddress);
            defaultAddressMap.put("defaultBillingAddress", defaultBillingAddress);

            this.logDebug("registry Add" + registryAdd);
            this.logDebug("Group Add" + groupAdd);
            this.logDebug("profile Add" + profileAdd);
            this.logDebug("Default Address Map:" + defaultAddressMap);

            final String errorMsg = request.getParameter("errMsg");
            if (!StringUtils.isEmpty(errorMsg)) {
                throw new BBBSystemException(ADDRESS_ERROR_1023);
            }

            addressMap.put(GROUP_ADDRESSES, this.listToAddressMap(groupAdd));
            addressMap.put(PROFILE_ADDRESSES, this.listToAddressMap(profileAdd));
            addressMap.put(REGISTRIES_ADDRESSES, this.listToAddressMap(registryAdd));
            addressMap.put(DEFAULT_PROFILE_ADDRESSES, defaultAddressMap);
        } catch (final BBBSystemException e) {
            throw new BBBSystemException(ADDRESS_ERROR_1023, e);
        }
        this.logDebug("END METHOD BBBShippingAddressDroplet");
        return addressMap;
    }

	public void changeToShipOnline(){

		final DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		final DynamoHttpServletResponse pResponse = ServletUtil.getCurrentResponse();
		List<String> bopusSku=new ArrayList<String>();
		List<BBBShippingGroup> shipGrpList = getOrder().getShippingGroups();
		List<CommerceItem> commItemList=getOrder().getCommerceItems();
		shipGroupFormHandler = (BBBShippingGroupFormhandler) ServletUtil.getCurrentRequest().resolveName("/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler");
		for(CommerceItem commerceItem:commItemList){
			List<ShippingGroupRelationship> reln=commerceItem.getShippingGroupRelationships();
			for(ShippingGroupRelationship comm:reln){

				ShippingGroupImpl shipGrp=	(ShippingGroupImpl) comm.getShippingGroup();
				if(!(shipGrp instanceof HardgoodShippingGroup)){
					shipGroupFormHandler.setOldShippingId(shipGrp.getId());
					shipGroupFormHandler.setCommerceItemId(commerceItem.getId());
					shipGroupFormHandler.setNewQuantity(Long.toString(commerceItem.getQuantity()));
					try {
						shipGroupFormHandler.handleChangeToShipOnline(pRequest, pResponse);
					} catch (ServletException e) {
						logError(" servlet exception for commerce id",e);
					} catch (IOException e) {
						logError(" IOException exception for commerce id",e);
					}
				}
			}
		}


	}
    /** @return */
    public final BBBShippingAddressDroplet getAddressDisplayDroplet() {
        return this.addressDisplayDroplet;
    }

    /** @param addressDisplayDroplet */
    public final void setAddressDisplayDroplet(final BBBShippingAddressDroplet addressDisplayDroplet) {
        this.addressDisplayDroplet = addressDisplayDroplet;
    }

    /** Converting list to Map.
     *
     * @param addressList
     * @return Address Map */
    private final Map<String, BBBAddress> listToAddressMap(final List<BBBAddress> addressList) {
        final Map<String, BBBAddress> addressMap = new HashMap<String, BBBAddress>();

        if ((addressList != null) && !addressList.isEmpty()) {
            int count = 0;

            final Iterator iterator = addressList.iterator();
            while (iterator.hasNext()) {
                count++;
                final BBBAddress bbbAddress = (BBBAddress) (iterator.next());
                bbbAddress.setIsNonPOBoxAddress(BBBUtility.isNonPOBoxAddress(bbbAddress.getAddress1(),
                                bbbAddress.getAddress2()));
                addressMap.put("" + count, bbbAddress);
            }

        }

        return addressMap;

    }

    public final GetCreditCardsForPayment getCardsForPayment() {
        return this.creditCardsForPayment;
    }

    public final void setCardsForPayment(final GetCreditCardsForPayment cardsForPayment) {
        this.creditCardsForPayment = cardsForPayment;
    }

    public final CreditCardInfoVO getAllCreditCard() throws ServletException, IOException, BBBSystemException {
        return this.getCardsForPayment().getAllCreditCard(this.getOrder(), this.getProfile());
    }
	public List<String> getIntShipRestrictSKuList(){
		final BBBSessionBean sessionBean =
				(BBBSessionBean)ServletUtil.getCurrentRequest().resolveName("/com/bbb/profile/session/SessionBean");
		final HashMap  sessionMap=sessionBean.getValues();
		List<String> restrictedSKuIdList=(List<String>)sessionMap.get("internationalShipRestrictedSku");
		if(restrictedSKuIdList!=null && !restrictedSKuIdList.isEmpty()){
			return restrictedSKuIdList;

		}
		return null;
	}


	public List<String> getIntShipBopusSKuList(){
		final BBBSessionBean sessionBean =
				(BBBSessionBean)ServletUtil.getCurrentRequest().resolveName("/com/bbb/profile/session/SessionBean");
		final HashMap  sessionMap=sessionBean.getValues();
		List<String> bopusSKuIdList=(List<String>)sessionMap.get("bopusSkuNoIntShip");
		if(bopusSKuIdList!=null && !bopusSKuIdList.isEmpty()){
			return bopusSKuIdList;

		}
		return null;
	}
    public final int getInventoryStatus(final String pSkuId, final String pStoreId, final long pItemQty,
                    final String operation, final String registryId)
                    throws CommerceException, BBBBusinessException, BBBSystemException {
        if (BBBUtility.isEmpty(pSkuId) || BBBUtility.isEmpty(operation) || (pItemQty == 0)) {
            throw new BBBBusinessException(BBBCoreErrorConstants.NOT_VALID_PARAMETER,
                            "required parameters cannot be null");
        }
        if (operation.equalsIgnoreCase(BBBInventoryManager.STORE_ONLINE)
                        || operation.equalsIgnoreCase(BBBInventoryManager.STORE_STORE)
                        || operation.equalsIgnoreCase(BBBInventoryManager.ONLINE_STORE)) {
            if (BBBUtility.isEmpty(pStoreId)) {
                throw new BBBBusinessException(BBBCoreErrorConstants.STORE_ID_NULL,
                                "store id cannot be null in case of " + operation + " operation");
            }
        }
        long itemQty = pItemQty;
        itemQty = this.getPurchaseProcessHelper().getRollupQtyForUpdate(pStoreId, registryId, pSkuId,
                        (BBBOrderImpl) this.getOrder(), itemQty);
        int inventoryStatus = 0;
        try {
            inventoryStatus = this.getPurchaseProcessHelper().checkInventory(SiteContextManager.getCurrentSiteId(),
                            pSkuId, pStoreId, this.getOrder(), itemQty, operation, this.getStoreInventoryContainer(),
                            BBBInventoryManager.AVAILABLE);
        } catch (final CommerceException e) {
            throw new BBBSystemException(BBBCoreErrorConstants.ERROR_INVENTORY_CHECK,
                            "some exception occurred while checking the inventory");
        }
        return inventoryStatus;
    }

    /** Method for Rest to determine if amount covered by Gift card
     *
     * @param pRequest
     * @param pResponse
     * @return
     * @throws ServletException
     * @throws IOException
     * @throws BBBSystemException */
    public final boolean isOrderAmountCoveredByGCAPI() throws BBBSystemException {
        boolean success = false;
        try {
            success = this.getPaymentGroupManager().isOrderAmountCoveredByGC(this.getOrder());
        } catch (final Exception exception) {

            this.logError("Error in isOrderAmountCoveredByGCAPI", exception);

            throw new BBBSystemException(BBBCatalogErrorCodes.ERR_ORDER_AMOUNT_COVERED_BY_GC,
                            "Error while determining if order amount is covered by GC.");
        }
        return success;
    }

    /** Method for Rest to determine if max gift card limit reached
     *
     * @param pRequest
     * @param pResponse
     * @return
     * @throws ServletException
     * @throws IOException
     * @throws BBBBusinessException
     * @throws BBBSystemException */
    public final boolean isMaxGiftCardReachedAPI() throws BBBBusinessException, BBBSystemException {

        boolean isCreditCardLimiReached = false;
        this.logDebug("Starting method BBBGiftCardFormhandler.handleMaxGiftCardReachedAPI");
        final Order currentOrder = (this.getOrder());
        final TransactionDemarcation td = new TransactionDemarcation();
        boolean rollback = true;

        if (currentOrder instanceof Order) {
            try {
                final TransactionManager tm = this.getPaymentGroupManager().getTransactionManager();
                if (tm != null) {
                    td.begin(tm, TransactionDemarcation.REQUIRED);
                }
                synchronized (currentOrder) {
                    isCreditCardLimiReached = this.getPaymentGroupManager().isGiftCardMaxReachedAPI(this.getOrder());
                    rollback = false;
                    if (isCreditCardLimiReached) {
                        throw new BBBBusinessException(BBBCatalogErrorCodes.MAX_GIFT_CARD_LIMIT_REACHED,
                                        "Maximum Gift card limit reached");
                    }

                }

                this.logDebug("End method BBBGiftCardFormhandler.handleMaxGiftCardReachedAPI");
            } catch (final CommerceException comException) {
                this.logError("Commerce Excpetion during handleMaxGiftCardReachedAPI method", comException);
                throw new BBBSystemException(BBBCatalogErrorCodes.COMMERCE_EXCEPTION, "Commerce Exception");

            } catch (final TransactionDemarcationException tranDemException) {
                this.logError("TransactionDemarcation Excpetion during handleMaxGiftCardReachedAPI method",
                                tranDemException);
                throw new BBBSystemException(BBBCatalogErrorCodes.TRANSACTION_DEMARCATION_EXCEPTION,
                                "Transaction Demarcation Exception");
            } finally {
                endTransaction(td, rollback);
            }
        }
        return isCreditCardLimiReached;
    }

    /** @param key
     * @return
     * @throws BBBSystemException
     * @throws BBBBusinessException */
    @SuppressWarnings("unchecked")
	public final Map getSEOTagDetails(final String key,final boolean isProduct) throws BBBSystemException, BBBBusinessException {
    	
    	if (isLoggingDebug()) {
			logDebug("Entering BBBSessionManagerFacade.getSEOTagDetails for key = " + key + " and Value of isProduct = " + isProduct);
		}
        final Map<String, String> seoTags = new HashMap<String, String>();
        final DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
        final DynamoHttpServletResponse response = ServletUtil.getCurrentResponse();
        if (BBBUtility.isNotEmpty(key)) {
        	try {
            	String description = null;
               	String keywords = null;
               	String title = null;
               	String displayName = null;
            	boolean isMetaDetailsOutputAvailable = false;

            	if(isProduct){
            		//call getItems on ProductCatalog Repository to get product meta details
            		ProductVO productVO = getCatalogTools().getProductVOMetaDetails(SiteContextManager.getCurrentSiteId(), key);
            		if(productVO !=null) {
	            		title = productVO.getName();
	             		displayName = productVO.getName();
	            		description = productVO.getShortDescription();
	            		keywords = productVO.getPrdKeywords();
	                    isMetaDetailsOutputAvailable = true;
            		}
            	} else{
            		//call SEO Tag repository to get Meta SEO details
                    request.setParameter("key", key);
                    request.setParameter("howMany", "1");
                	request.setParameter("repository", SEOREPOSITORY);
                    request.setParameter("itemDescriptor", SEOTAGS);
                    request.setParameter("queryRQL", QUERYRQL);

            		this.getRqlQueryRange().service(request, response);
            		final RepositoryItem output = (RepositoryItem) request.getObjectParameter("element");
                    if (output != null) {
                     	description = (String) output.getPropertyValue(DESCRIPTION);
 	                	keywords = (String) output.getPropertyValue(KEYWORDS);
 	                    title = (String) output.getPropertyValue(TITLE);
 	                    displayName = (String) output.getPropertyValue(DISPLAYNAME);
 	                    isMetaDetailsOutputAvailable = true;
                    }
            	}

                if (isMetaDetailsOutputAvailable) {
   					seoTags.put(TITLE, title);
                    seoTags.put(KEYWORDS, keywords);
                    seoTags.put(DESCRIPTION, description);
                    seoTags.put(DISPLAYNAME, displayName);
                }

            } catch (final ServletException e) {
                this.logDebug("Method: BBBSessionManagerFacade.getSEOTags, Servlet Exception while fetching seoTags data "
                                + e);
                throw new BBBSystemException(BBBCoreErrorConstants.ERROR_SEO_TAG,
                                "Some error occurred while fetching seoTags data");
            } catch (final IOException e) {
                this.logDebug("Method: BBBSessionManagerFacade.getSEOTags, IO Exception while fetching seoTags data "
                                + e);
                throw new BBBSystemException(BBBCoreErrorConstants.ERROR_SEO_TAG,
                                "Some error occurred while fetching seoTags data");
            }
        } else {
            this.logDebug("Method: BBBSessionManagerFacade.getSEOTags, input parameter is null");
            throw new BBBBusinessException(BBBCoreErrorConstants.ERROR_SEO_TAG_INPUT_NULL, "input parameter is null");
        }
        if (isLoggingDebug()) {
			logDebug("Exit BBBSessionManagerFacade.getSEOTagDetails :: " + seoTags);
		}
        return seoTags;
    }

    public final BBBCatalogTools getCatalogTools() {
        return this.catalogTools;
    }

    public final void setCatalogTools(final BBBCatalogTools catalogTools) {
        this.catalogTools = catalogTools;
    }

    /**
     * Rest API
     * This Method is used for mobile to decide whether
     * default profile billing address needs to display on Billing page or not.
     * In case of PayPal Order
     *
     * @return
     */
    public boolean displayProfileBillingAddress(){
    	boolean displayAddress = true;
    	final OrderHolder cart = (OrderHolder) ServletUtil.getCurrentRequest().resolveName(ATG_COMMERCE_SHOPPING_CART);
        final BBBOrderImpl order = (BBBOrderImpl) cart.getCurrent();
    	if((order).isPayPalOrder() && !this.getProfile().isTransient()){
    		RepositoryItem billingAddress = (RepositoryItem)this.getProfileInfo().getPropertyValue(BBBCheckoutConstants.BILLING_ADDRESS);
    		BBBRepositoryContactInfo orderBillingAddress = order.getBillingAddress();
    		if(billingAddress != null && orderBillingAddress != null){
	    		BBBRepositoryContactInfo profileBillingAddress = new BBBRepositoryContactInfo((MutableRepositoryItem) billingAddress);
	    		displayAddress = !BBBAddressTools.compare(profileBillingAddress, orderBillingAddress);
    		}
    	}
    	return displayAddress;
    }

    /** set Device Location. i.e longitude and Latitude in session
    *
    * @param Longitude and Latitude calculated by front end
    * @return boolean true if longitude and latitude are successfully set in session, otherwise false
    **/
	public final boolean setDeviceLocation(final String latitude, final String longitude){

		if(!StringUtils.isEmpty(latitude) && !StringUtils.isEmpty(longitude)){

			getBbbGenericSessionComponent().setDeviceLocationLatitude(latitude);
			getBbbGenericSessionComponent().setDeviceLocationLongitude(longitude);

			return true;
		}

		return false;

	}

	/**
	 * @return the eximManager
	 */
	public BBBEximManager getEximManager() {
		return eximManager;
	}

	/**
	 * @param eximManager the eximManager to set
	 */
	public void setEximManager(BBBEximManager eximManager) {
		this.eximManager = eximManager;
	}
	
	/**
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws CommerceException
	 */
	public final Boolean removeItemsFromOrderAndLogOut () throws BBBSystemException, BBBBusinessException, CommerceException {

        try {
        	Cookie cartCookie = getOrderDetailsManager().createCartCookie(getOrder(), ServletUtil.getCurrentResponse());
            	this.getProfileService().logoutUser();
            BBBUtility.addCookie(ServletUtil.getCurrentResponse(), cartCookie, false);
        } catch (final ServletException e) {
            throw new BBBSystemException(BBBCoreErrorConstants.ERROR_LOGOUT_USER_INVALID_PROFILE,
                            "Error occured while logging out user",e);
        }

        return true;

    }
	
}
