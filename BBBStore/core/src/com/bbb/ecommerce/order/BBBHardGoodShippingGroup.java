/*
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  BBBHardGoodShippingGroup.java
 *
 *  DESCRIPTION: This class Overrides the OOTB HardgoodshippingGroup
 *
 *  HISTORY:
 *  Dec 14, 2011  Initial version
 */

package com.bbb.ecommerce.order;

import java.beans.IntrospectionException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import atg.commerce.order.ChangedProperties;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.HardgoodShippingGroup;
import atg.core.util.Address;
import atg.core.util.StringUtils;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import atg.userprofiling.address.AddressTools;

import com.bbb.commerce.order.TBSShippingInfo;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.order.bean.GiftWrapCommerceItem;
import com.bbb.order.bean.TrackingInfo;
import com.bbb.utils.BBBUtility;

/** added methods to poulate the shippinggroup custom properties.
 *
 * @author nagarg */

public class BBBHardGoodShippingGroup extends HardgoodShippingGroup implements BBBShippingGroup {

    private static final long serialVersionUID = 1L;
    private static final String IS_COMFIRMATION_ASKED = "IS_COMFIRMATION_ASKED";
    private static final String PACK_HOLD_DATE = "PACK_HOLD_DATE";
    private static final String REGISTRY_ID = "registryId";
    private static final String REGISTRY_INFO = "registryInfo";
    private static final String GIFT_WRAP_IND = "giftWrapInd";
    private static final String SHIPMENT_TRACKING = "shipmentTracking";
    private static final String SOURCE_ID = "sourceId";
    private static final String ECO_FEE_MAP = "ecoFeeItemMap";
   // private static final String LTL_ITEM_MAP = "ltlItemMap";
    
    private static final String SDD_STORE_ID = "sddStoreId";
    
    //Start: 83-U - PayPal - Order xml Changes
    private static final String IS_FROM_PAYPAL = "isFromPaypal";
    private static final String ADDRESS_STATUS = "addressStatus";
    
    //PO box order repository changes
    private static final String IS_PO_BOX_ADDRESS = "poBoxAddress";
    private static final String IS_QAS_VALIDATED = "qasValidated";
    
    //Auto-waive flag
    private static final String AUTO_WAIVE_FLAG = "autoWaiveFlag";
    private static final String AUTO_WAIVE_CLASS = "autoWaiveClassifications";
    
    //po box getter setter

    public final boolean getqasValidated() {
        if (this.getPropertyValue(IS_QAS_VALIDATED) != null) {
            return ((Boolean) this.getPropertyValue(IS_QAS_VALIDATED)).booleanValue();
        }
        return false;
    }

  
    public final void setQASValidated(final boolean qasValidated) {
        this.setPropertyValue(IS_QAS_VALIDATED, Boolean.valueOf(qasValidated));
    }
    
    public final boolean getIsPoBoxAddress() {
        if (this.getPropertyValue(IS_PO_BOX_ADDRESS) != null) {
            return ((Boolean) this.getPropertyValue(IS_PO_BOX_ADDRESS)).booleanValue();
        }
        return false;
    }

  
    public final void setPoBoxAddress(final boolean isPoBoxAddress) {
        this.setPropertyValue(IS_PO_BOX_ADDRESS, Boolean.valueOf(isPoBoxAddress));
    }
    
    public final boolean getAutoWaiveFlag() {
        if (this.getPropertyValue(AUTO_WAIVE_FLAG) != null) {
            return ((Boolean) this.getPropertyValue(AUTO_WAIVE_FLAG)).booleanValue();
        }
        return false;
    }
    
    public final void setAutoWaiveFlag(final boolean autoWaive) {
        this.setPropertyValue(AUTO_WAIVE_FLAG, Boolean.valueOf(autoWaive));
    }
    
    public final String getAutoWaiveClasses() {
    	return (String)this.getPropertyValue(AUTO_WAIVE_CLASS);
    }
    
    public final void setAutoWaiveClasses(final String autoWaiveClasses) {
        this.setPropertyValue(AUTO_WAIVE_CLASS, autoWaiveClasses);
    }

    //End: 83-U - PayPal - Order xml Changes
    private static final String  module = BBBHardGoodShippingGroup.class.getName();
    private static final ApplicationLogging logger = ClassLoggingFactory.getFactory().getLoggerForClassName(module);
    
    /** @return the giftWrapInd */
    public  boolean getGiftWrapInd() {
        if (this.getPropertyValue(GIFT_WRAP_IND) != null) {
            return ((Boolean) this.getPropertyValue(GIFT_WRAP_IND)).booleanValue();
        }
        return false;
    }

    /** @param giftWrapInd the giftWrapInd to set */
    public  void setGiftWrapInd(final boolean giftWrapInd) {
        this.setPropertyValue(GIFT_WRAP_IND, Boolean.valueOf(giftWrapInd));
    }

    /** Gets the value of registry info in the hardgood shipping group. */

    public String getRegistryInfo() {
        return (String) this.getPropertyValue(REGISTRY_INFO);
    }

    /** Sets the registry info value to the hard good shipping group. */
    public final void setRegistryInfo(final String pRegistryInfo) {
        this.setPropertyValue(REGISTRY_INFO, pRegistryInfo);
    }

    /** Gets the value of registry id in the hardgood shipping group. */
    public String getRegistryId() {
        return (String) this.getPropertyValue(REGISTRY_ID);
    }

    /** Sets the registry id value to the hard good shipping group. */
    public final void setRegistryId(final String pRegistryId) {
        this.setPropertyValue(REGISTRY_ID, pRegistryId);
    }

    /** Gets the value of sourceId id in the hardgood shipping group.
     * @return */
    public  String getSourceId() {
        return (String) this.getPropertyValue(SOURCE_ID);
    }

    /** Sets the sourceId value to the hard good shipping group. */
    public final void setSourceId(final String pSourceId) {
        this.setPropertyValue(SOURCE_ID, pSourceId);
    }
    
    /** Gets the value of sddStoreId in the hardgood shipping group.
     * @return */
    public  String getSddStoreId() {
        return (String) this.getPropertyValue(SDD_STORE_ID);
    }

    /** Sets the sddStoreId value to the hard good shipping group. */
    public final void setSddStoreId(final String pSddStoreId) {
        this.setPropertyValue(SDD_STORE_ID, pSddStoreId);
    }

    /** Checks to see if this order is gift wrapped or not.
     *
     * @return true if gift wrapped, false - otherwise */
    @SuppressWarnings ("unchecked")
    public  boolean containsGiftWrap() {
        final List<CommerceItemRelationship> commerceItemRelationshipList = this.getCommerceItemRelationships();

        for (final CommerceItemRelationship commerceItemRelationship : commerceItemRelationshipList) {
            final CommerceItem commerceItem = commerceItemRelationship.getCommerceItem();
            if (commerceItem instanceof GiftWrapCommerceItem) {
                return true;
            }
        }
        return false;
    }

    /** Checks to see if this order is gift wrapped or not.
     *
     * @return true if gift wrapped, false - otherwise */
    @SuppressWarnings ("unchecked")
    public GiftWrapCommerceItem getGiftWrapCommerceItem() {
        final List<CommerceItemRelationship> commerceItemRelationshipList = this.getCommerceItemRelationships();

        for (final CommerceItemRelationship commerceItemRelationship : commerceItemRelationshipList) {
            final CommerceItem commerceItem = commerceItemRelationship.getCommerceItem();
            if (commerceItem instanceof GiftWrapCommerceItem) {
                return (GiftWrapCommerceItem) commerceItem;
            }
        }
        return null;
    }

    /** Checks to see if this order has a gift message special instruction.
     *
     * @return true if order contains gift message, false - otherwise */
    public final boolean getContainsGiftWrapMessage() {

        if (!BBBUtility.isMapNullOrEmpty(this.getSpecialInstructions())) {
            return this.getSpecialInstructions().containsKey(BBBCheckoutConstants.GIFT_MESSAGE_KEY);
        }
        return false;

    }

    /** Sets the gift message.
     *
     * @param giftWrapMessage
     * @param pMessageTo the message to field
     * @param pMessage the message field
     * @param pMessageFrom the message from field */
    @SuppressWarnings ({ "rawtypes", "unchecked" })
    public  void setGiftMessage(final String giftWrapMessage) {
        if (StringUtils.isEmpty(giftWrapMessage)) {
        	if(!BBBUtility.isMapNullOrEmpty(this.getSpecialInstructions())){
        		this.getSpecialInstructions().remove(BBBCheckoutConstants.GIFT_MESSAGE_KEY);
        	}
        	return;
        }
        if (!BBBUtility.isMapNullOrEmpty(this.getSpecialInstructions())) {
            this.getSpecialInstructions().put(BBBCheckoutConstants.GIFT_MESSAGE_KEY, giftWrapMessage);
        } else {
            final Map specialInstructions = new HashMap();
            specialInstructions.put(BBBCheckoutConstants.GIFT_MESSAGE_KEY, giftWrapMessage);
            this.setSpecialInstructions(specialInstructions);
        }
    }

    /** Determines if user has entered gift message.
     *
     * @return true if gift message was populated, false - otherwise */
    public  String getGiftWrapMessage() {
        final String giftMessage = (String) this.getSpecialInstructions().get(BBBCheckoutConstants.GIFT_MESSAGE_KEY);
        return giftMessage;
    }
    
    /** Determines if user has entered gift message.
    *
    * @return true if gift message was populated, false - otherwise */
   public String getGiftMessage() {
       final String giftMessage = (String) this.getSpecialInstructions().get(BBBCheckoutConstants.GIFT_MESSAGE_KEY);
       return giftMessage;
   }

    /** @return Set<TrackingInfo> */
    @SuppressWarnings ("unchecked")
    public Set<TrackingInfo> getTrackingInfos() {
        Set<TrackingInfo> trackingInfos = null;
        final Set<MutableRepositoryItem> shipmentTrackingList = (Set<MutableRepositoryItem>) this
                        .getPropertyValue(SHIPMENT_TRACKING);
        if ((shipmentTrackingList != null) && !shipmentTrackingList.isEmpty()) {
            trackingInfos = new HashSet<TrackingInfo>();
            for (final MutableRepositoryItem st : shipmentTrackingList) {
                if (st != null) {
                    final TrackingInfo ti = new TrackingInfo(st);
                    trackingInfos.add(ti);
                }
            }
        }
        return trackingInfos;
    }

    @SuppressWarnings ("unchecked")
    public final Map<String, TrackingInfo> getTrackingInfosMap() {
        Map<String, TrackingInfo> trackingInfos = null;
        final Set<MutableRepositoryItem> shipmentTrackingList = (Set<MutableRepositoryItem>) this
                        .getPropertyValue(SHIPMENT_TRACKING);
        if ((shipmentTrackingList != null) && !shipmentTrackingList.isEmpty()) {
            trackingInfos = new HashMap<String, TrackingInfo>();
            for (final MutableRepositoryItem st : shipmentTrackingList) {
                if (st != null) {
                    final TrackingInfo ti = new TrackingInfo(st);
                    trackingInfos.put(ti.getTrackingNumber(), ti);
                }
            }
        }
        return trackingInfos;
    }

    /** @return */
    public final Date getPackAndHoldDate() {
        Date date = null;
        if ((this.getSpecialInstructions() != null) && (this.getSpecialInstructions().get(PACK_HOLD_DATE) != null)
                        && (this.getSpecialInstructions().get(PACK_HOLD_DATE) instanceof Date)) {
            date = (Date) this.getSpecialInstructions().get(PACK_HOLD_DATE);
        }
        return date;
    }

    /** @return */
    public final boolean isSendShippingConfirmation() {
        boolean confirmation = false;

        if ((this.getSpecialInstructions() != null)
                        && (this.getSpecialInstructions().get(IS_COMFIRMATION_ASKED) != null)) {
            confirmation = Boolean.valueOf(this.getSpecialInstructions().get(IS_COMFIRMATION_ASKED).toString());
        }

        return confirmation;
    }

    /** @return */
    public final String getShippingConfirmationEmail() {
        String email = null;
        if ((this.getSpecialInstructions() != null)
                        && (this.getSpecialInstructions().get(BBBCheckoutConstants.CONFIRMATION_EMAIL_ID) != null)) {

            email = (String) this.getSpecialInstructions().get(BBBCheckoutConstants.CONFIRMATION_EMAIL_ID);
        }
        return email;
    }

    /** @param pPackAndHoldDate */
    @SuppressWarnings ({ "unchecked", "rawtypes" })
    public final void setPackAndHoldDate(final Date pPackAndHoldDate) {
        if (!BBBUtility.isMapNullOrEmpty(this.getSpecialInstructions())) {
            this.getSpecialInstructions().put(PACK_HOLD_DATE, pPackAndHoldDate);
        } else {
            final Map specialInstructions = new HashMap();
            specialInstructions.put(PACK_HOLD_DATE, pPackAndHoldDate);
            this.setSpecialInstructions(specialInstructions);
        }
    }

    /**
     * @param pSendShippingConfirmation
     */
    @SuppressWarnings ({ "unchecked", "rawtypes" })
    public final void setSendShippingConfirmation(final boolean pSendShippingConfirmation) {
        if (!BBBUtility.isMapNullOrEmpty(this.getSpecialInstructions())) {
            this.getSpecialInstructions().put(IS_COMFIRMATION_ASKED, String.valueOf(pSendShippingConfirmation));
        } else {
            final Map specialInstructions = new HashMap();
            specialInstructions.put(IS_COMFIRMATION_ASKED, String.valueOf(pSendShippingConfirmation));
            this.setSpecialInstructions(specialInstructions);
        }
    }

    /** @param pShippingConfirmationEmail */
    @SuppressWarnings ({ "unchecked", "rawtypes" })
    public final void setShippingConfirmationEmail(final String pShippingConfirmationEmail) {
        if (!BBBUtility.isMapNullOrEmpty(this.getSpecialInstructions())) {
            this.getSpecialInstructions().put(BBBCheckoutConstants.CONFIRMATION_EMAIL_ID, pShippingConfirmationEmail);
        } else {
            final Map specialInstructions = new HashMap();
            specialInstructions.put(BBBCheckoutConstants.CONFIRMATION_EMAIL_ID, pShippingConfirmationEmail);
            this.setSpecialInstructions(specialInstructions);
        }
    }

    @Override
    @SuppressWarnings ("unchecked")
    public Map<String, String> getEcoFeeItemMap() {
        return (Map<String, String>) this.getPropertyValue(ECO_FEE_MAP);
    }

    @Override
    public final void setEcoFeeItemMap(final Map<String, String> pEcoFeeItemMap) {
        this.setPropertyValue(ECO_FEE_MAP, pEcoFeeItemMap);
    }

    /** @param shipmentTrackerSet */
    @SuppressWarnings ("unchecked")
    public final void setTrackingInfos(final Map<String, TrackingInfo> shipmentTrackerSet) {

        final Set<MutableRepositoryItem> shipmentTrackingList = (Set<MutableRepositoryItem>) this
                        .getPropertyValue(SHIPMENT_TRACKING);

        if ((shipmentTrackerSet != null) && !shipmentTrackerSet.isEmpty()) {
            shipmentTrackingList.clear();
            for (final TrackingInfo st : shipmentTrackerSet.values()) {
                shipmentTrackingList.add(st.getRepositoryItem());
            }
        }
        this.setPropertyValue(SHIPMENT_TRACKING, shipmentTrackingList);
    }

    // method override of super class for fixing defect BSL-1394
    // Added NOPMD since AddressTools.copyAddress() call OTB method which throws IntrospectionException,
    // so can't be handled by custom exceptions 
    @Override
    public final void setShippingAddress(final Address pShippingAddress) {
        if ((pShippingAddress != null) && (this.getShippingAddress() != null)) {
            try {
                AddressTools.copyAddress(pShippingAddress, this.getShippingAddress());
            } catch (final IntrospectionException e) {
            	logger.logError("BBBHardGroupShippingGroup:setShippingAddress IntrospectionException" + e.getMessage(),e);
            	throw new RuntimeException(e.getMessage()); //NOPMD
            }
        }
        super.setShippingAddress(pShippingAddress);
    }

    /** @return */
    public boolean isContainsGiftWrap() {
        return this.containsGiftWrap();
    }
    
  //Start: 83-U - PayPal - Order xml Changes
    
    /** 
     * Gets the value of isFromPaypal in the hardgood shipping group.
     * @return 
     */
    public final boolean isFromPaypal() {
    	if (this.getPropertyValue(IS_FROM_PAYPAL) != null) {
        return ((Boolean) this.getPropertyValue(IS_FROM_PAYPAL)).booleanValue();
    	}
    	return false;
    }

    /**
     * Sets the isFromPaypal to the hard good shipping group.
     * @param isFromPaypal
     */
    public final void setIsFromPaypal(final boolean isFromPaypal) {
        this.setPropertyValue(IS_FROM_PAYPAL, isFromPaypal);
    }

    public final boolean getIsFromPaypal() {
    	if (this.getPropertyValue(IS_FROM_PAYPAL) != null) {
        return ((Boolean) this.getPropertyValue(IS_FROM_PAYPAL)).booleanValue();
    	}
    	return false;
    }

    /** 
     * Gets the value of addressStatus in the hardgood shipping group.
     * @return 
     */
    public final String getAddressStatus() {
        return (String) (this.getPropertyValue(ADDRESS_STATUS));
    }

    /**
     * Sets the addressStatus to the hard good shipping group.
     * @param addressStatus
     */
    public final void setAddressStatus(final String addressStatus) {
        this.setPropertyValue(ADDRESS_STATUS, addressStatus);
    }
    
  //End: 83-U - PayPal - Order xml Changes
        private static final String TBS_SHIP_INFO = "tbsShipInfo";
    
    private TBSShippingInfo mTBSShipInfo;

	public TBSShippingInfo getTbsShipInfo() {
		return mTBSShipInfo;
	}

	public void setTbsShipInfo(TBSShippingInfo pTBSShipInfo) {
		mTBSShipInfo = pTBSShipInfo;
		MutableRepositoryItem repItem = ((ChangedProperties) pTBSShipInfo).getRepositoryItem();
		setTBShipInfoRepositoryItem(repItem);
	}
    
	/**
	 * @return the tBSItemInfoRepositoryItem
	 */
	public MutableRepositoryItem getTBSShipInfoRepositoryItem() {
		return (MutableRepositoryItem) getPropertyValue(TBS_SHIP_INFO);
	}

	/**
	 * @param pTBSItemInfoRepositoryItem the tBSItemInfoRepositoryItem to set
	 */
	public void setTBShipInfoRepositoryItem(RepositoryItem pTBSShipInfoRepositoryItem) {
		setPropertyValue(TBS_SHIP_INFO, pTBSShipInfoRepositoryItem);
	}

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("BBBHardGoodShippingGroup [getGiftWrapInd()=").append(this.getGiftWrapInd())
                        .append(", getRegistryInfo()=").append(this.getRegistryInfo()).append(", getRegistryId()=")
                        .append(this.getRegistryId()).append(", getSourceId()=").append(this.getSourceId())
                        .append(", getContainsGiftWrapMessage()=").append(this.getContainsGiftWrapMessage())
                        .append(", getTrackingInfos()=").append(this.getTrackingInfos())
                        .append(", getEcoFeeItemMap()=").append(this.getEcoFeeItemMap())
                        .append(", isContainsGiftWrap()=").append(this.isContainsGiftWrap())
                        .append(", getShippingMethod()=").append(this.getShippingMethod())
                        .append(", getDescription()=").append(this.getDescription()).append(", getState()=")
                        .append(this.getState()).append(", getPriceInfo()=").append(this.getPriceInfo())
                        .append(", getShipOnDate()=").append(this.getShipOnDate()).append(", getActualShipDate()=")
                        .append(this.getActualShipDate()).append(", getSubmittedDate()=")
                        .append(this.getSubmittedDate()).append(", getSpecialInstructions()=")
                        .append(this.getSpecialInstructions()).append(", getCommerceItemRelationshipCount()=")
                        .append(this.getCommerceItemRelationshipCount())
                        .append(", getPaymentGroupRelationshipCount()=")
                        .append(this.getPaymentGroupRelationshipCount()).append(", isChanged()=")
                        .append(this.isChanged()).append("]");
        return builder.toString();
    }
}
