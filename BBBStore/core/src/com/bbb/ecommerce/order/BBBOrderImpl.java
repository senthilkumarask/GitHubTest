package com.bbb.ecommerce.order;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.ShippingGroup;
import atg.core.util.Address;
import atg.core.util.StringUtils;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;

import com.bbb.account.vo.CouponListVo;
import com.bbb.commerce.common.BBBRepositoryContactInfo;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.commerce.order.Paypal;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.EcoFeeCommerceItem;
import com.bbb.order.bean.GiftWrapCommerceItem;
import com.bbb.order.bean.LTLAssemblyFeeCommerceItem;
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem;
import com.bbb.order.bean.TrackingInfo;
import com.bbb.utils.BBBUtility;

// TODO: Auto-generated Javadoc
/** This class is extended over OrderImpl for custom implementation.
 *
 * @author jpadhi
 * @version $Revision: #1 $ */

public class BBBOrderImpl extends OrderImpl implements BBBOrder {

    /** The Constant BILLING_ADDRESS. */
    private static final String BILLING_ADDRESS = "billingAddress";
    
    /** The Constant ORDER_XML. */
    private static final String ORDER_XML = "orderXML";
    
    /** The Constant USER_IP. */
    private static final String USER_IP = "userIP";
    
    /** The Constant BOPUS_ORDER_NUMBER. */
    private static final String BOPUS_ORDER_NUMBER = "bopusOrderNumber";
    
    /** The Constant ONLINE_ORDER_NUMBER. */
    private static final String ONLINE_ORDER_NUMBER = "onlineOrderNumber";
    
    /** The Constant SCHOOL_COUPON. */
    private static final String SCHOOL_COUPON = "schoolCoupon";
    
    /** The Constant SCHOOL_ID. */
    private static final String SCHOOL_ID = "schoolId";
    
    /** The Constant AFFILIATE. */
    private static final String AFFILIATE = "affiliate";
    
    /** The Constant TOKEN. */
    //Start: 83 - PayPal - Commit Order changes
    private static final String TOKEN = "token";
    
    /** The Constant PAYERID. */
    private static final String PAYERID = "payerId";
    
    /** The Constant DEVICEFINGERPRINT. */
    private static final String DEVICEFINGERPRINT = "deviceFingerprint";
    //Start: 83 - PayPal - Commit Order changes
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The availability map. */
    private Map<String, Integer> availabilityMap = new HashMap<String, Integer>();
    
    /** The registry map. */
    private Map<String, RegistrySummaryVO> registryMap = new HashMap<String, RegistrySummaryVO>();
    
    /** The coupon map. */
    private Map<String, RepositoryItem> couponMap = new HashMap<String, RepositoryItem>();
    
    /** The excluded promotion map. */
    private final Map<String, Set<CommerceItem>> excludedPromotionMap = new HashMap<String, Set<CommerceItem>>();

    /** The express check out. */
    private boolean expressCheckOut;
    
    /** The message shown on cart. */
    private boolean messageShownOnCart;
    
    /** The dummy order. */
    private boolean dummyOrder;
    
    /** The exim webservice failure. */
    private boolean eximWebserviceFailure;

    /** The college id. */
    private String collegeId;
    private String salesOS;
    private boolean oldOrder=false;
    
    /** The Constant INTERNATIONAL_ORDER_ID. */
    private static final String INTERNATIONAL_ORDER_ID = "internationalOrderId";
    
    /** The Constant INTERNATIONAL_STATE. */
    private static final String INTERNATIONAL_STATE = "internationalState";
    
    /** The Constant CURRENCY_CODE. */
    private static final String CURRENCY_CODE = "currencyCode";
    
    /** The Constant COUNTRY_CODE. */
    private static final String COUNTRY_CODE = "countryCode";
    
    /** The Constant INTERNATIONAL_SUBMITTED_DATE. */
    private static final String INTERNATIONAL_SUBMITTED_DATE = "internationalSubmitteddate";
    
    /** The Constant ORDER_SUB_STATUS. */
    private static final String ORDER_SUB_STATUS = "substatus";
    
    /** The Constant EMAILSIGNUP. */
    private static final String EMAILSIGNUP = "emailSignUp";
    
    /** The moved commerce item. */
    private BBBCommerceItem movedCommerceItem;
    
    /** The coupon list vo. */
    private List<CouponListVo> couponListVo;
    

    /** The Constant TBS_APPROVAL_REQUIRED. */
    private static final String TBS_APPROVAL_REQUIRED = "tbsApprovalRequired";
    
    /** The Constant TBS_ORDER. */
    private static final String TBS_ORDER = "isTBSOrder";
    
    /** The Constant TBS_ASSOCIATE_ID. */
    private static final String TBS_ASSOCIATE_ID = "tbsAssociateID";
    
    /** The Constant TBS_APPROVER_ID. */
    private static final String TBS_APPROVER_ID = "tbsApproverID";
    
    /** The Constant TBS_STORE_NO. */
    private static final String TBS_STORE_NO = "tbsStoreNo";
    
    private static final String AUTO_WAIVE_FLAG = "autoWaiveFlag";
    private static final String AUTO_WAIVE_CLASSIFICATION = "autoWaiveClassification";
    
	/**
	 * This property will hold data of dsLineItemTaxInfo item temporary for
	 * anonymous user, which will be used while committing order.
	 */
	private Map<String, MutableRepositoryItem> anonymousOrderTaxItem;
	
	/**
	 * This property will hold information about which taxableItem has tax overridden for the entire shippingGroup.
	 */
	private Map<String, Boolean> taxOverrideMap;
	private boolean isPackAndHoldFlag;
	
	/**
	 * Transient property to indicate that order is being updated from OrderStatusUpdate Listener
	 */
	private boolean isOSUpdateListener;

	public boolean isOSUpdateListener() {
		return isOSUpdateListener;
	}
	
	public void setOSUpdateListener(boolean isOSUpdateListener) {
		this.isOSUpdateListener = isOSUpdateListener;
	}
	
	/**
	 * 
	 * @return oldOrder
	 */
	public boolean isOldOrder() {
		return oldOrder;
	}
	/**
	 * 
	 * @param oldOrder
	 */
	public void setOldOrder(boolean oldOrder) {
		this.oldOrder = oldOrder;
	}
	/**
	 * @return the eximWebserviceFailure
	 */
	public boolean isEximWebserviceFailure() {
		return this.eximWebserviceFailure;
	}
	
	/**
	 * Sets the exim webservice failure.
	 *
	 * @param eximWebserviceFailure the eximWebserviceFailure to set
	 */
	public void setEximWebserviceFailure(boolean eximWebserviceFailure) {
		this.eximWebserviceFailure = eximWebserviceFailure;
	}

    /**
     * Gets the coupon list vo.
     *
     * @return the coupon list vo
     */
    public List<CouponListVo> getCouponListVo() {
		return couponListVo;
	}
	
	/**
	 * Sets the coupon list vo.
	 *
	 * @param couponListVo the new coupon list vo
	 */
	public void setCouponListVo(List<CouponListVo> couponListVo) {
		this.couponListVo = couponListVo;
	}

    /** Default Constructor. */
    public BBBOrderImpl() {
        super();
    }
    
    public String getSalesOS() {
		return salesOS;
	}
	public void setSalesOS(String salesOS) {
		this.setPropertyValue("salesOS", salesOS);
	}
	
    // Added to fix BBBSL-2662 - Start
    public final void setEmailSignUp(final boolean emailSignUp) {
        this.setPropertyValue(EMAILSIGNUP, emailSignUp);
    }
    
    /**
     * Checks if is email sign up.
     *
     * @return true, if is email sign up
     */
    public final boolean isEmailSignUp() {
    	if (null != this.getPropertyValue(EMAILSIGNUP)) {
    		return ((Boolean) this.getPropertyValue(EMAILSIGNUP)).booleanValue();
        }
    	 return false;
    }
    // Added to fix BBBSL-2662 - End
    /**
     * Gets the college id.
     *
     * @return collegeId
     */
    public final String getCollegeId() {
        return this.collegeId;
    }

    /**
     * Sets the college id.
     *
     * @param pCollegeId the new college id
     */
    public final void setCollegeId(final String pCollegeId) {
        this.collegeId = pCollegeId;
    }

    /**
     * Gets the moved commerce item.
     *
     * @return the movedCommerceItem
     */
    @Override
    public BBBCommerceItem getMovedCommerceItem() {
        return this.movedCommerceItem;
    }

    /**
     * Sets the moved commerce item.
     *
     * @param movedCommerceItem the movedCommerceItem to set
     */
    @Override
    public final void setMovedCommerceItem(final BBBCommerceItem movedCommerceItem) {
        this.movedCommerceItem = movedCommerceItem;
    }

    /**
     * Checks if is message shown on cart.
     *
     * @return the isMessageShownOnCart
     */
    @Override
    public final boolean isMessageShownOnCart() {
        return this.messageShownOnCart;
    }

    /**
     * Sets the message shown on cart.
     *
     * @param isMessageShownOnCart the isMessageShownOnCart to set
     */
    @Override
    public final void setMessageShownOnCart(final boolean isMessageShownOnCart) {
        this.messageShownOnCart = isMessageShownOnCart;
    }

    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#getAvailabilityMap()
     */
    @Override
    @SuppressWarnings ("rawtypes")
    public Map getAvailabilityMap() {
        return this.availabilityMap;
    }

    /**
     * Checks if is express check out.
     *
     * @return true, if is express check out
     */
    public boolean isExpressCheckOut() {
        return this.expressCheckOut;
    }

    /**
     * Sets the express check out.
     *
     * @param pExpressCheckOut the new express check out
     */
    public void setExpressCheckOut(final boolean pExpressCheckOut) {
        this.expressCheckOut = pExpressCheckOut;
    }

    
    /**
     * Checks if is dummy order.
     *
     * @return true, if is dummy order
     */
    public boolean isDummyOrder() {
		return dummyOrder;
	}

	/**
	 * Sets the dummy order.
	 *
	 * @param dummyOrder the new dummy order
	 */
	public void setDummyOrder(boolean dummyOrder) {
		this.dummyOrder = dummyOrder;
	}

    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#setAvailabilityMap(java.util.Map)
     */
    @Override
    @SuppressWarnings ({ "rawtypes", "unchecked" })
    public final void setAvailabilityMap(final Map availabilityMap) {
        this.availabilityMap = availabilityMap;
    }

    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#getRegistryMap()
     */
    @Override
    @SuppressWarnings ("rawtypes")
    public  Map getRegistryMap() {
        return this.registryMap;
    }

    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#setRegistryMap(java.util.Map)
     */
    @Override
    @SuppressWarnings ({ "rawtypes", "unchecked" })
    public final void setRegistryMap(final Map registryMap) {
        this.registryMap = registryMap;
    }

    /**
     *  This method Loops through order's items and find items which matches to pStoreId and pSkuId.
     *
     * @param pStoreId the store id
     * @param pRegistryId the registry id
     * @param pSkuId the sku id
     * @return the exists items for same sku
     */

    public  List<CommerceItem> getExistsItemsForSameSKU(final String pStoreId, final String pRegistryId,
                    final String pSkuId) {
        List<CommerceItem> commerceItemList = null;
        final List<CommerceItem> existingItemList = new ArrayList<CommerceItem>();
        if (pSkuId != null) {

            try {
                commerceItemList = this.getCommerceItemsByCatalogRefId(pSkuId);
            } catch (final InvalidParameterException e) {
                return null;
            } catch (final CommerceItemNotFoundException e) {
                return null;
            }
            for (final CommerceItem tempItem : commerceItemList) {
                if (tempItem instanceof BBBCommerceItem) {
                    final BBBCommerceItem bbbItem = (BBBCommerceItem) tempItem;
                    if (BBBUtility.isNotEmpty(pStoreId)) {
                        if (pStoreId.equalsIgnoreCase(bbbItem.getStoreId())) {
                            existingItemList.add(bbbItem);
                        }
                    } else if (BBBUtility.isNotEmpty(pRegistryId)) {
                        if (pRegistryId.equalsIgnoreCase(bbbItem.getRegistryId())) {
                            existingItemList.add(bbbItem);
                        }
                    } else {
                        if (BBBUtility.isEmpty(bbbItem.getRegistryId()) && BBBUtility.isEmpty(bbbItem.getStoreId())) {
                            existingItemList.add(bbbItem);
                        }
                    }
                }

            }
        }
        return existingItemList;
    }

    /**
     * Gets the possible billing addresses.
     *
     * @param pOrder the order
     * @return the possible billing addresses
     */
    public final List<Address> getPossibleBillingAddresses(final Order pOrder) {
        final List<Address> addressList = new ArrayList<Address>();
        return addressList;
    }

    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#getUserIP()
     */
    @Override
    public final String getUserIP() {
        return (String) this.getPropertyValue(USER_IP);
    }

    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#setUserIP(java.lang.String)
     */
    @Override
    public final void setUserIP(final String pUserIP) {
        this.setPropertyValue(USER_IP, pUserIP);
    }

    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#getOrderXML()
     */
    @Override
    public final String getOrderXML() {
        return (String) this.getPropertyValue(ORDER_XML);
    }

    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#setOrderXML(java.lang.String)
     */
    @Override
    public final void setOrderXML(final String pOrderXML) {
        this.setPropertyValue(ORDER_XML, pOrderXML);
    }

    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#getBillingAddress()
     */
    @Override
    public BBBRepositoryContactInfo getBillingAddress() {
        BBBRepositoryContactInfo repoContInfo = null;
        if (null != this.getPropertyValue(BILLING_ADDRESS)) {
            repoContInfo = new BBBRepositoryContactInfo((MutableRepositoryItem) this.getPropertyValue(BILLING_ADDRESS));
        }
        return repoContInfo;
    }

    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#setBillingAddress(com.bbb.commerce.common.BBBRepositoryContactInfo)
     */
    @Override
    public final void setBillingAddress(final BBBRepositoryContactInfo address) {
        if (null != address) {
            final MutableRepositoryItem repoItem = address.getRepositoryItem();
            this.setPropertyValue(BILLING_ADDRESS, repoItem);
        }
    }

    /* public RepositoryContactInfo getBillingAddress() { return new RepositoryContactInfo( (MutableRepositoryItem)
     * getPropertyValue(BBBCheckoutConstants.BILLING_ADDRESS)); } */

    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#getTrackingInfos()
     */
    @Override
    @SuppressWarnings ("unchecked")
    public final List<TrackingInfo> getTrackingInfos() {
        List<TrackingInfo> trackingInfos = null;
        if ((this.getShippingGroups() != null) && !this.getShippingGroups().isEmpty()) {
            trackingInfos = new ArrayList<TrackingInfo>();
            for (final ShippingGroup sg : (List<ShippingGroup>) this.getShippingGroups()) {
                if (sg instanceof BBBHardGoodShippingGroup) {
                    final Set<TrackingInfo> sgTrackingInfos = ((BBBHardGoodShippingGroup) sg).getTrackingInfos();
                    if (sgTrackingInfos != null) {
                        trackingInfos.addAll(sgTrackingInfos);
                    }
                }
            }
        }
        return trackingInfos;
    }

    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#getCouponMap()
     */
    @Override
    @SuppressWarnings ("rawtypes")
    public Map getCouponMap() {
        return this.couponMap;
    }

    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#setCouponMap(java.util.Map)
     */
    @Override
    @SuppressWarnings ({ "rawtypes", "unchecked" })
    public void setCouponMap(final Map mCouponMap) {
        this.couponMap = mCouponMap;
    }

    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#isBopusOrder()
     */
    @Override
    @SuppressWarnings ("unchecked")
    public boolean isBopusOrder() {
        final List<CommerceItem> items = this.getCommerceItems();
        for (final CommerceItem commerceItem : items) {
            if (commerceItem instanceof BBBCommerceItem) {
                final BBBCommerceItem bbbCommerceItem = (BBBCommerceItem) commerceItem;
                if (!StringUtils.isEmpty(bbbCommerceItem.getStoreId())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Checks if is bopus order with multiple store items.
     *
     * @return true, if is bopus order with multiple store items
     */
    public boolean isBopusOrderWithMultipleStoreItems() {
    	final List<CommerceItem> items = this.getCommerceItems();
    	Set<String> storeIdCommerceIdSet = new HashSet<String>();
        for (final CommerceItem commerceItem : items) {
            if (commerceItem instanceof BBBCommerceItem) {
                final BBBCommerceItem bbbCommerceItem = (BBBCommerceItem) commerceItem;
                if (!StringUtils.isEmpty(bbbCommerceItem.getStoreId())) {
                	storeIdCommerceIdSet.add(bbbCommerceItem.getStoreId());
                	if(storeIdCommerceIdSet.size() > 1){
                		return true;
                	}
                }
            }
        }
		return false;
	}
    
    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#getOrderType()
     */
    @Override
    @SuppressWarnings ("unchecked")
    public final OrderType getOrderType() {
        boolean foundBopus = false;
        boolean foundOnline = false;
        final List<CommerceItem> commerceItems = this.getCommerceItems();
        
        if (commerceItems != null) {
            for (final CommerceItem cItem : commerceItems) {
                if (cItem instanceof BBBCommerceItem) {
                    final String bopusItem = ((BBBCommerceItem) cItem).getStoreId();
                    if (BBBUtility.isNotEmpty(bopusItem)) {
                        foundBopus = true;
                    } else {
                        foundOnline = true;
                    }
                } else if (cItem instanceof GiftWrapCommerceItem) {
                    foundOnline = true;
                }
            }

            if (foundOnline && foundBopus) {
                return OrderType.HYBRID;
            } else if (foundOnline && !foundBopus) {
                return OrderType.ONLINE;
            } else if (!foundOnline && foundBopus) {
                return OrderType.BOPUS;
            }

        }

        return null;
    }

    /**
     *  This method checks if any BOPUS item or ONLINE items are available in Order or not.
     *
     * @return String
     */
    @Override
    @SuppressWarnings ("unchecked")
    public String getOnlineBopusItemsStatusInOrder() {

        boolean foundBopus = false;
        boolean foundOnline = false;
        final List<CommerceItem> commerceItems = this.getCommerceItems();
        String status = null;

        if (commerceItems != null) {
            for (final CommerceItem cItem : commerceItems) {
                if (cItem instanceof BBBCommerceItem) {
                    final String bopusItem = ((BBBCommerceItem) cItem).getStoreId();
                    if (BBBUtility.isNotEmpty(bopusItem)) {
                        foundBopus = true;
                    } else {
                        foundOnline = true;
                    }
                } else if (cItem instanceof GiftWrapCommerceItem) {
                    foundOnline = true;
                }
            }

            if (foundOnline && foundBopus) {
                status = BBBCheckoutConstants.HYBRID;
            } else if (foundOnline && !foundBopus) {
                status = BBBCheckoutConstants.ONLINE_ONLY;
            } else if (!foundOnline && foundBopus) {
                status = BBBCheckoutConstants.BOPUS_ONLY;
            }

        }

        return status;
    }

    /**
     *  Get BBB commerceItemCount.
     *
     * @return the BBB commerce item count
     */
    @SuppressWarnings ("unchecked")
    public final long getBBBCommerceItemCount() {

        int giftWrapItemCount = 0;
        int ecoFeeItemCount = 0;
        int deliverySurchargeItemCount = 0;
        int assemblyItemCount = 0;
        long commerceItemCount = 0;

        if (this.getCommerceItemCount() >= 0) {

            commerceItemCount = this.getTotalCommerceItemCount();
            final List<CommerceItem> commerceItems = this.getCommerceItems();

            for (final CommerceItem commerceItem : commerceItems) {
                if (commerceItem instanceof GiftWrapCommerceItem) {
                    giftWrapItemCount += commerceItem.getQuantity();
                } else if (commerceItem instanceof EcoFeeCommerceItem) {
                    ecoFeeItemCount += commerceItem.getQuantity();
                } else if(commerceItem instanceof LTLAssemblyFeeCommerceItem){
                	assemblyItemCount += commerceItem.getQuantity();
                } else if(commerceItem instanceof LTLDeliveryChargeCommerceItem){
                	deliverySurchargeItemCount += commerceItem.getQuantity();
                }
            }

            if (giftWrapItemCount > 0) {
                commerceItemCount = commerceItemCount - giftWrapItemCount;
            }

            if (ecoFeeItemCount > 0) {
                commerceItemCount = commerceItemCount - ecoFeeItemCount;
            }
            
            if  (assemblyItemCount > 0) {
            	 commerceItemCount = commerceItemCount - assemblyItemCount;
        }

            if (deliverySurchargeItemCount > 0) {
            	 commerceItemCount = commerceItemCount - deliverySurchargeItemCount;
            }            
        }

        return commerceItemCount;
    }

    
    /**
     * Gets the order skus.
     *
     * @return the order skus
     */
    public final List<String> getOrderSkus() {
    	
    	List<CommerceItem> commerceItemList = getCommerceItems();
    	List<String> skuList = new ArrayList<String>(); 
    	for (final CommerceItem tempItem : commerceItemList) {
    		if (tempItem instanceof BBBCommerceItem) {
    			skuList.add(tempItem.getCatalogRefId());
		     }
	    }
		return skuList;
    }
    
    /**
     * Gets the order online skus.
     *
     * @return the order online skus
     */
    public final List<String> getOrderOnlineSkus() {
    	
    	List<CommerceItem> commerceItemList = getCommerceItems();
    	List<String> skuList = new ArrayList<String>(); 
    	for (final CommerceItem tempItem : commerceItemList) {
    		if (tempItem instanceof BBBCommerceItem
    				&& StringUtils.isEmpty(((BBBCommerceItem) tempItem).getStoreId())) {
    			skuList.add(tempItem.getCatalogRefId());
		     }
	    }
		return skuList;
    }
    
    /**
     *  This methods returns all Map<HardGroupShippingGroupID,ShippingMethodName> and if not found then returns null.
     *
     * @return the all hg shipping methods from order
     */
    @Override
    @SuppressWarnings ("unchecked")
    public final Map<String, String> getAllHGShippingMethodsFromOrder() {

        Map<String, String> shipMethods = null;

        final List<ShippingGroup> shippingGrps = this.getShippingGroups();
        if ((shippingGrps != null) && (shippingGrps.size() > 0)) {
            shipMethods = new HashMap<String, String>();
            for (final ShippingGroup shippingGroup : shippingGrps) {
                if (shippingGroup instanceof BBBHardGoodShippingGroup) {
                    shipMethods.put(shippingGroup.getId(), shippingGroup.getShippingMethod());
                }
            }
        }

        return (shipMethods != null) ? (shipMethods.size() > 0) ? shipMethods : null : shipMethods;
    }

    /**
     *  This methods returns shippingAddress for the first BBBHardGoodShippingGroup in the order and if not found then
     * returns null.
     *
     * @return the shipping address
     */
    @SuppressWarnings ("unchecked")
    public BBBRepositoryContactInfo getShippingAddress() {
        BBBRepositoryContactInfo shippingAddress = null;
        final List<ShippingGroup> shippingGrps = this.getShippingGroups();
        if ((shippingGrps != null) && (shippingGrps.size() > 0)) {
            for (final ShippingGroup shippingGroup : shippingGrps) {
                if (shippingGroup instanceof BBBHardGoodShippingGroup) {
                    shippingAddress = (BBBRepositoryContactInfo) ((BBBHardGoodShippingGroup) shippingGroup)
                                    .getShippingAddress();
                    return shippingAddress;
                }
            }
        }
        return null;
    }

    /**
     *  If shipping method is common across all hard good shipping groups then this method returns that shipping method
     * name otherwise returns null.
     *
     * @return the string
     */
    @Override
    public final String isCommonShippingMethodAmongHGS() {

        String firstMethod = null;
        boolean different = false;
        final Map<String, String> shipMethods = this.getAllHGShippingMethodsFromOrder();
        if (shipMethods != null) {
            firstMethod = shipMethods.entrySet().iterator().next().getValue();

            for (final Map.Entry<String, String> entry : shipMethods.entrySet()) {
                final String shipMethod = entry.getValue();
                if (!firstMethod.equals(shipMethod)) {
                    different = true;
                    break;
                }
            }
        }

        return (!different) ? firstMethod : null;
    }

    /* (non-Javadoc)
     * @see atg.commerce.order.OrderImpl#invalidateOrder()
     */
    @Override
    public final synchronized void invalidateOrder() {
            super.invalidateOrder();
    }

    /**
     * Invalidate and refresh order.
     */
    public final synchronized void invalidateAndRefreshOrder() {
        super.invalidateOrder();

    }
    
    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#setToken(java.lang.String)
     */
    //Start: 83 - PayPal - Commit Order changes
    public final void setToken(final String token) {
        this.setPropertyValue(TOKEN, token);
    }
    
    /**
     * Gets the token.
     *
     * @return the token
     */
    public String getToken() {
        return (String) this.getPropertyValue(TOKEN);
    }
    
    /**
     * Sets the payer id.
     *
     * @param payerId the new payer id
     */
    public final void setPayerId(final String payerId) {
        this.setPropertyValue(PAYERID, payerId);
    }
    
    /**
     * Gets the payer id.
     *
     * @return the payer id
     */
    public final String getPayerId() {
        return (String) this.getPropertyValue(PAYERID);
    }
    
    /**
     * Sets the device fingerprint.
     *
     * @param deviceFingerprint the new device fingerprint
     */
    public final void setDeviceFingerprint(final String deviceFingerprint) {
        this.setPropertyValue(DEVICEFINGERPRINT, deviceFingerprint);
    }
    
    /**
     * Gets the device fingerprint.
     *
     * @return the device fingerprint
     */
    public final String getDeviceFingerprint() {
        return (String) this.getPropertyValue(DEVICEFINGERPRINT);
    }
    //End: 83 - PayPal - Commit Order changes.
    
    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#setAffiliate(java.lang.String)
     */
    @Override
    public final void setAffiliate(final String affiliate) {
        this.setPropertyValue(AFFILIATE, affiliate);
    }
    
    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#getAffiliate()
     */
    @Override
    public final String getAffiliate() {
        return (String) this.getPropertyValue(AFFILIATE);
    }

    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#getSchoolId()
     */
    @Override
    public final String getSchoolId() {
        return (String) this.getPropertyValue(SCHOOL_ID);
    }

    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#setSchoolId(java.lang.String)
     */
    @Override
    public final void setSchoolId(final String pSchoolId) {
        this.setPropertyValue(SCHOOL_ID, pSchoolId);
    }

    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#getSchoolCoupon()
     */
    @Override
    public  String getSchoolCoupon() {
        return (String) this.getPropertyValue(SCHOOL_COUPON);
    }

    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#setSchoolCoupon(java.lang.String)
     */
    @Override
    public final void setSchoolCoupon(final String pSchoolCoupon) {
        this.setPropertyValue(SCHOOL_COUPON, pSchoolCoupon);
    }

    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#getOnlineOrderNumber()
     */
    @Override
    public String getOnlineOrderNumber() {
        return (String) this.getPropertyValue(ONLINE_ORDER_NUMBER);
    }

    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#setOnlineOrderNumber(java.lang.String)
     */
    @Override
    public final void setOnlineOrderNumber(final String onlineOrderNumber) {
        this.setPropertyValue(ONLINE_ORDER_NUMBER, onlineOrderNumber);
    }

    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#getBopusOrderNumber()
     */
    @Override
    public final String getBopusOrderNumber() {
        return (String) this.getPropertyValue(BOPUS_ORDER_NUMBER);
    }

    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#setBopusOrderNumber(java.lang.String)
     */
    @Override
    public final void setBopusOrderNumber(final String bopusOrderNumber) {
        this.setPropertyValue(BOPUS_ORDER_NUMBER, bopusOrderNumber);
    }

    /**
     * Gets the excluded promotion map.
     *
     * @return the excluded promotion map
     */

    public  Map<String, Set<CommerceItem>> getExcludedPromotionMap() {
        return this.excludedPromotionMap;
    }

    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#isTaxCalculationFailure()
     */
    @Override
    public final boolean isTaxCalculationFailure() {
        final String taxFailure = (String) this.getSpecialInstructions().get(
                        BBBCheckoutConstants.CYBERSOURCE_TAX_FAILURE);
        return (!StringUtils.isBlank(taxFailure) && Boolean.parseBoolean(taxFailure));
    }
    
    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#setCavv(java.lang.String)
     */
    public final void setCavv(final String cavv) {
        this.setPropertyValue("cavv", cavv);
    }
    
    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#getCavv()
     */
    public final String getCavv() {
        return (String) this.getPropertyValue("cavv");
    }
    
    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#setEci(java.lang.String)
     */
    public final void setEci(final String eci) {
        this.setPropertyValue("eci", eci);
    }
    
    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#getEci()
     */
    public final String getEci() {
        return (String) this.getPropertyValue("eci");
    }
    
    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#setXid(java.lang.String)
     */
    public final void setXid(final String xid) {
        this.setPropertyValue("xid", xid);
    }
    
    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#getXid()
     */
    public final String getXid() {
        return (String) this.getPropertyValue("xid");
    }
    
    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#setErrorNo(java.lang.String)
     */
    public final void setErrorNo(final String errorNo) {
        this.setPropertyValue("errorNo", errorNo);
    }
    
    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#getErrorNo()
     */
    public final String getErrorNo() {
        return (String) this.getPropertyValue("errorNo");
    }
    
    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#setErrorDesc(java.lang.String)
     */
    public final void setErrorDesc(final String errorDesc) {
        this.setPropertyValue("errorDesc", errorDesc);
    }
    
    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#getErrorDesc()
     */
    public final String getErrorDesc() {
        return (String) this.getPropertyValue("errorDesc");
    }
    
    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#setTransactionId(java.lang.String)
     */
    public final void setTransactionId(final String transactionId) {
        this.setPropertyValue("transactionId", transactionId);
    }
    
    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#getTransactionId()
     */
    public final String getTransactionId() {
        return (String) this.getPropertyValue("transactionId");
    }
    
    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#setVbvOrderId(java.lang.String)
     */
    public final void setVbvOrderId(final String vbvOrderId) {
        this.setPropertyValue("vbvOrderId", vbvOrderId);
    }
    
    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#getVbvOrderId()
     */
    public final String getVbvOrderId() {
        return (String) this.getPropertyValue("vbvOrderId");
    }
    
    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#setEnrolled(java.lang.String)
     */
    public final void setEnrolled(final String enrolled) {
        this.setPropertyValue("enrolled", enrolled);
    }
    
    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#getEnrolled()
     */
    public final String getEnrolled() {
        return (String) this.getPropertyValue("enrolled");
    }
    
    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#setSignatureVerification(java.lang.String)
     */
    public final void setSignatureVerification(final String signatureVerification) {
        this.setPropertyValue("signatureVerification", signatureVerification);
    }
    
    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#getSignatureVerification()
     */
    public final String getSignatureVerification() {
        return (String) this.getPropertyValue("signatureVerification");
    }
    
    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#setPAResStatus(java.lang.String)
     */
    public final void setPAResStatus(final String pAResStatus) {
        this.setPropertyValue("pAResStatus", pAResStatus);
    }
    
    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#getPAResStatus()
     */
    public final String getPAResStatus() {
        return (String) this.getPropertyValue("pAResStatus");
    }
    
    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#setCommerceIndicator(java.lang.String)
     */
    public final void setCommerceIndicator(final String commerceIndicator) {
        this.setPropertyValue("commerceIndicator", commerceIndicator);
    }
    
    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#getCommerceIndicator()
     */
    public final String getCommerceIndicator() {
        return (String) this.getPropertyValue("commerceIndicator");
    }
    
    
    /* (non-Javadoc)
     * @see com.bbb.ecommerce.order.BBBOrder#getTimeStamp()
     */
    public  Date getTimeStamp() {
        return (Date) this.getPropertyValue(BBBCoreConstants.TIME_STAMP);
    }

	/* (non-Javadoc)
	 * @see com.bbb.ecommerce.order.BBBOrder#setTimeStamp(java.util.Date)
	 */
	@Override
	public void setTimeStamp(Date currentDate) {
		 this.setPropertyValue(BBBCoreConstants.TIME_STAMP, currentDate);
		
	}
	
	/* (non-Javadoc)
	 * @see com.bbb.ecommerce.order.BBBOrder#getSubStatus()
	 */
	@Override
	public final String getSubStatus() {
		return (String) this.getPropertyValue(ORDER_SUB_STATUS);
	}

	/* (non-Javadoc)
	 * @see com.bbb.ecommerce.order.BBBOrder#setSubStatus(java.lang.String)
	 */
	@Override
    public final void setSubStatus(final String substatus) {
        this.setPropertyValue(ORDER_SUB_STATUS, substatus);
    }
	
	/**
	 * Checks if is pay pal order.
	 *
	 * @return true, if is pay pal order
	 */
	public boolean isPayPalOrder(){
		//R2.2 Start | Below Code will check whether order contains PayPal Payment Group
        List<PaymentGroup> payPg = this.getPaymentGroups();
		boolean isPayPalOrder = false;
		if (payPg != null) {
			for (PaymentGroup pg : payPg) {
				if (pg instanceof Paypal) {
					isPayPalOrder = true;
					break;
				}
			}
		}
		return isPayPalOrder;
		 //R2.2 Ends
	}

	/* (non-Javadoc)
	 * @see com.bbb.ecommerce.order.BBBOrder#setInternationalOrderId(java.lang.String)
	 */
	@Override
	public void setInternationalOrderId(final String orderId) {
		this.setPropertyValue(INTERNATIONAL_ORDER_ID, orderId);
		
	}

	/* (non-Javadoc)
	 * @see com.bbb.ecommerce.order.BBBOrder#setInternationalState(java.lang.String)
	 */
	@Override
	public void setInternationalState(final String orderState) {
		this.setPropertyValue(INTERNATIONAL_STATE, orderState);
		
	}

	/* (non-Javadoc)
	 * @see com.bbb.ecommerce.order.BBBOrder#setInternationalCurrencyCode(java.lang.String)
	 */
	@Override
	public void setInternationalCurrencyCode(final String currencyCode) {
		this.setPropertyValue(CURRENCY_CODE, currencyCode);
		
	}

	/* (non-Javadoc)
	 * @see com.bbb.ecommerce.order.BBBOrder#setInternationalCountryCode(java.lang.String)
	 */
	@Override
	public void setInternationalCountryCode(final String countryCode) {
		this.setPropertyValue(COUNTRY_CODE, countryCode);
		
	}

	/* (non-Javadoc)
	 * @see com.bbb.ecommerce.order.BBBOrder#setInternationalOrderDate(java.util.Date)
	 */
	@Override
	public void setInternationalOrderDate(final Date submittedDate) {
		this.setPropertyValue(INTERNATIONAL_SUBMITTED_DATE, submittedDate);
		
	}
	
	/* (non-Javadoc)
	 * @see com.bbb.ecommerce.order.BBBOrder#getInternationalOrderId()
	 */
	public final String getInternationalOrderId() {
        return (String) this.getPropertyValue(INTERNATIONAL_ORDER_ID);
    }
	
	/* (non-Javadoc)
	 * @see com.bbb.ecommerce.order.BBBOrder#getInternationalOrderDate()
	 */
	public final Date getInternationalOrderDate() {
        return (Date) this.getPropertyValue(INTERNATIONAL_SUBMITTED_DATE);
    }
	
	/* (non-Javadoc)
	 * @see com.bbb.ecommerce.order.BBBOrder#setTBSApprovalRequired(boolean)
	 */
	@Override
	public void setTBSApprovalRequired( final boolean isRequired ) {
		this.setPropertyValue(TBS_APPROVAL_REQUIRED, Boolean.valueOf(isRequired));
	}

	/* (non-Javadoc)
	 * @see com.bbb.ecommerce.order.BBBOrder#isTBSApprovalRequired()
	 */
	@Override
	public boolean isTBSApprovalRequired() {
		return ((Boolean)this.getPropertyValue(TBS_APPROVAL_REQUIRED)).booleanValue();
	}
    
	/* (non-Javadoc)
	 * @see com.bbb.ecommerce.order.BBBOrder#setTBSOrder(boolean)
	 */
	@Override
	public void setTBSOrder( final boolean isTBS ) {
		this.setPropertyValue(TBS_ORDER, Boolean.valueOf(isTBS));
	}

	/* (non-Javadoc)
	 * @see com.bbb.ecommerce.order.BBBOrder#isTBSOrder()
	 */
	@Override
	public boolean isTBSOrder() {
		if((Boolean)this.getPropertyValue(TBS_ORDER) != null){
			return ((Boolean)this.getPropertyValue(TBS_ORDER)).booleanValue();
		} else{
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.bbb.ecommerce.order.BBBOrder#setTBSAssociateID(java.lang.String)
	 */
	@Override
	public void setTBSAssociateID( final String associateID ) {
		this.setPropertyValue(TBS_ASSOCIATE_ID, associateID);
	}

	/* (non-Javadoc)
	 * @see com.bbb.ecommerce.order.BBBOrder#getTBSAssociateID()
	 */
	@Override
	public String getTBSAssociateID() {
		return (String)this.getPropertyValue(TBS_ASSOCIATE_ID);
	}

	/* (non-Javadoc)
	 * @see com.bbb.ecommerce.order.BBBOrder#setTBSApproverID(java.lang.String)
	 */
	@Override
	public void setTBSApproverID( final String approverID ) {
		this.setPropertyValue(TBS_APPROVER_ID, approverID);
	}

	/* (non-Javadoc)
	 * @see com.bbb.ecommerce.order.BBBOrder#getTBSApproverID()
	 */
	@Override
	public String getTBSApproverID() {
		return (String)this.getPropertyValue(TBS_APPROVER_ID);
	}
	
	/* (non-Javadoc)
	 * @see com.bbb.ecommerce.order.BBBOrder#getTbsStoreNo()
	 */
	public String getTbsStoreNo() {
		return (String)this.getPropertyValue(TBS_STORE_NO);
	}
	
	/* (non-Javadoc)
	 * @see com.bbb.ecommerce.order.BBBOrder#setTbsStoreNo(java.lang.String)
	 */
	public void setTbsStoreNo(String pTbsStoreNo) {
		this.setPropertyValue(TBS_STORE_NO, pTbsStoreNo);
	}
	/*public String getTbsAssociateID() {
		return (String)this.getPropertyValue(TBS_ASSOCIATE_ID);
	}

	public void setTbsAssociateID( final String associateID ) {
		this.setPropertyValue(TBS_ASSOCIATE_ID, associateID);
	}*/

	public boolean isAutoWaiveFlag() {
		//return ((Boolean)getPropertyValue(AUTO_WAIVE_FLAG)).booleanValue();
		if (null != this.getPropertyValue(AUTO_WAIVE_FLAG)) {
    		return ((Boolean) this.getPropertyValue(AUTO_WAIVE_FLAG)).booleanValue();
        }
    	return false;
	}

	public void setAutoWaiveFlag(boolean autoWaiveFlag) {
		setPropertyValue(AUTO_WAIVE_FLAG, Boolean.valueOf(autoWaiveFlag));
	}

	public void setAutoWaiveClassification(String classification) {
		setPropertyValue(AUTO_WAIVE_CLASSIFICATION, classification);
	}

	public String getAutoWaiveClassification() {
		return (String)getPropertyValue(AUTO_WAIVE_CLASSIFICATION);
	}

	/**
	 * @return the anonymousOrderTaxItem
	 */
	public Map<String, MutableRepositoryItem> getAnonymousOrderTaxItem() {
		if (null == anonymousOrderTaxItem) {
			anonymousOrderTaxItem = new HashMap<String, MutableRepositoryItem>();
		}
		return anonymousOrderTaxItem;
	}

	/**
	 * @param anonymousOrderTaxItem
	 *            the anonymousOrderTaxItem to set
	 */
	public void setAnonymousOrderTaxItem(Map<String, MutableRepositoryItem> anonymousOrderTaxItem) {
		this.anonymousOrderTaxItem = anonymousOrderTaxItem;
	}
	
	@Override
	public Map getTaxOverrideMap() {
		if(this.taxOverrideMap==null)
			this.taxOverrideMap = new HashMap<String, Boolean>();
		return this.taxOverrideMap;
	}
	@Override
	public void setTaxOverrideMap(Map taxOverrideMap) {
		this.taxOverrideMap = taxOverrideMap;
	}
	
	public String getEmailAddress() {
		return (String)this.getPropertyValue("emailAddress");
	}
	
	public void setEmailAddress(String pEmailAddress) {
		this.setPropertyValue("emailAddress", pEmailAddress);
	}
	public boolean isPackAndHoldFlag() {
		return isPackAndHoldFlag;
	}
	public void setPackAndHoldFlag(boolean isPackAndHoldFlag) {
		this.isPackAndHoldFlag = isPackAndHoldFlag;
	}
    
//    @Override
//    public String toString() {
//        final StringBuilder builder = new StringBuilder();
//        builder.append("BBBOrderImpl [availabilityMap=").append(this.availabilityMap).append(", registryMap=")
//                        .append(this.registryMap).append(", couponMap=").append(this.couponMap)
//                        .append(", excludedPromotionMap=").append(this.excludedPromotionMap)
//                        .append(", expressCheckOut=").append(this.expressCheckOut).append(", messageShownOnCart=")
//                        .append(this.messageShownOnCart).append(", movedCommerceItem=").append(this.movedCommerceItem)
//                        .append(", isBopusOrder()=").append(this.isBopusOrder()).append(", getOrderType()=")
//                        .append(this.getOrderType()).append(", getOnlineBopusItemsStatusInOrder()=")
//                        .append(this.getOnlineBopusItemsStatusInOrder()).append(", getBBBCommerceItemCount()=")
//                        .append(this.getBBBCommerceItemCount()).append(", getShippingAddress()=")
//                        .append(this.getShippingAddress()).append(", isCommonShippingMethodAmongHGS()=")
//                        .append(this.isCommonShippingMethodAmongHGS()).append(", getAffiliate()=")
//                        .append(this.getAffiliate()).append(", getSchoolId()=").append(this.getSchoolId())
//                        .append(", getSchoolCoupon()=").append(this.getSchoolCoupon())
//                        .append(", getOnlineOrderNumber()=").append(this.getOnlineOrderNumber())
//                        .append(", getBopusOrderNumber()=").append(this.getBopusOrderNumber())
//                        .append(", getExcludedPromotionMap()=").append(this.getExcludedPromotionMap())
//                        .append(", isTaxCalculationFailure()=").append(this.isTaxCalculationFailure())
//                        .append(", getProfileId()=").append(this.getProfileId()).append(", getState()=")
//                        .append(this.getState()).append(", getPriceInfo()=").append(this.getPriceInfo())
//                        .append(", getTaxPriceInfo()=").append(this.getTaxPriceInfo())
//                        .append(", getCreatedByOrderId()=").append(this.getCreatedByOrderId())
//                        .append(", getRelatedOrders()=").append(this.getRelatedOrders())
//                        .append(", getSubmittedDate()=").append(this.getSubmittedDate())
//                        .append(", getSubmittedTime()=").append(this.getSubmittedTime())
//                        .append(", getOriginOfOrder()=").append(this.getOriginOfOrder())
//                        .append(", isExplicitlySaved()=").append(this.isExplicitlySaved())
//                        .append(", getCreationDate()=").append(this.getCreationDate()).append(", getCreationTime()=")
//                        .append(this.getCreationTime()).append(", getLastModifiedDate()=")
//                        .append(this.getLastModifiedDate()).append(", getLastModifiedTime()=")
//                        .append(this.getLastModifiedTime()).append(", getCompletedDate()=")
//                        .append(this.getCompletedDate()).append(", getCompletedTime()=")
//                        .append(this.getCompletedTime()).append(", isTransient()=").append(this.isTransient())
//                        .append(", getManualAdjustments()=").append(this.getManualAdjustments())
//                        .append(", getAgentId()=").append(this.getAgentId()).append(", getSalesChannel()=")
//                        .append(this.getSalesChannel()).append(", getCreationSiteId()=")
//                        .append(this.getCreationSiteId()).append(", getSiteId()=").append(this.getSiteId())
//                        .append(", getCommerceItems()=").append(this.getCommerceItems())
//                        .append(", getCommerceItemCount()=").append(this.getCommerceItemCount())
//                        .append(", getTotalCommerceItemCount()=").append(this.getTotalCommerceItemCount())
//                        .append(", getShippingGroups()=").append(this.getShippingGroups())
//                        .append(", getShippingGroupCount()=").append(this.getShippingGroupCount())
//                        .append(", getRelationshipCount()=").append(this.getRelationshipCount()).append(", getId()=")
//                        .append(this.getId()).append("]");
//        return builder.toString();
//    }
}
