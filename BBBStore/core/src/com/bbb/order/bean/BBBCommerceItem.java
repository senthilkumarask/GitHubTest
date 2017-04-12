package com.bbb.order.bean;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.repository.MutableRepositoryItem;



/** This class is extended for commerce item implementation.
 *
 * @author Sunil*/
public class BBBCommerceItem extends BaseCommerceItemImpl {

    private static final long serialVersionUID = 1L;
    
    private static final String VDC_IND = "vdcInd";
    private static final String IS_BTS = "bts";
    private static final String FREE_SHIPPING_METHOD = "freeShippingMethod";
    private static final String REGISTRY_ID = "registryId";
    private static final String REGISTRY_INFO = "registryInfo";
    private static final String LAST_MODIFIED_DATE = "lastModifiedDate";
    private static final String PREV_PRICE = "prevPrice";
    private static final String SKU_SURCHARGE = "skuSurcharge";
    private static final String SEQNUMBER = "seqNumber";
    private static final String MSGSHOWNFLAGOFF = "msgShownFlagOff";
    private static final String MSGSHOWNOOS = "msgShownOOS";
    private static final String LTLFLAG = "ltlFlag";
    private static final String LTL_SHIP_METHOD= "ltlShipMethod";
    private static final String ORIG_LTL_SHIP_METHOD= "origLtlShipMethod";
	private static final String LTL_WHITE_GLOVE_ASSEMBLY = "whiteGloveAssembly";
	private static final String LTL_DELIVERY_ITEM_ID= "deliveryItemId";
	private static final String LTL_ASSEMBLY_ITEM_ID= "assemblyItemId";
	
	private static final String REFERENCE_NUMBER= "referenceNumber";
	private static final String FULL_IMAGE_PATH= "fullImagePath";
	private static final String THUMBNAIL_IMAGE_PATH= "thumbnailImagePath";
	private static final String MOBILE_FULL_IMAGE_PATH= "mobileFullImagePath";
	private static final String MOBILE_THUMBNAIL_IMAGE_PATH= "mobileThumbnailImagePath";
	private static final String PERSONALIZE_PRICE= "personalizePrice";
	private static final String PERSONALIZE_COST= "personalizeCost";
	private static final String PERSONALIZATION_OPTIONS= "personalizationOptions";
	private static final String PERSONALIZATION_DETAILS= "personalizationDetails";
	private static final String META_DATA_FLAG = "metaDataFlag";
	private static final String META_DATA_URL= "metaDataUrl";
	private static final String MODERATION_FLAG= "moderationFlag";
	private static final String MODERATION_URL= "moderationUrl";
	private static final String TRACKING= "tracking";
	
	private boolean eximErrorExists;
	private boolean eximPricingReq;
 
    private String commerceItemMoved;

    private boolean ecoFeeEligible;
    private boolean storeSKU;
    private boolean itemMoved;
    private boolean buyOffAssociatedItem;
    private String buyOffPrimaryRegFirstName;
    private String buyOffCoRegFirstName;
    private String buyOffRegistryEventType;
    private String personalizationOptionsDisplay;	    
    private String highestshipMethod;
    private String regisryShipMethod;    
	public String getRegisryShipMethod() {
		return regisryShipMethod;
	}
	public void setRegisryShipMethod(String regisryShipMethod) {
		this.regisryShipMethod = regisryShipMethod;
	}
	/**
	 * 
	 * @return highestshipMethod
	 */
	public String getHighestshipMethod() {
		return highestshipMethod;
	}
	/**
	 * 
	 * @param highestshipMethod
	 */
	public void setHighestshipMethod(String highestshipMethod) {
		this.highestshipMethod = highestshipMethod;
	}

	public String getPersonalizationOptionsDisplay() {
		return personalizationOptionsDisplay;
	}

	public void setPersonalizationOptionsDisplay(
			String personalizationOptionsDisplay) {
		this.personalizationOptionsDisplay = personalizationOptionsDisplay;
	}
	/**
	 * @return the eximPricingReq
	 */
	public boolean isEximPricingReq() {
		return eximPricingReq;
	}

	/**
	 * @param eximPricingReq the eximPricingReq to set
	 */
	public void setEximPricingReq(boolean eximPricingReq) {
		this.eximPricingReq = eximPricingReq;
	}

	/**
	 * @return the eximErrorExists
	 */
	public boolean isEximErrorExists() {
		return eximErrorExists;
	}

	/**
	 * @param eximErrorExists the eximErrorExists to set
	 */
	public void setEximErrorExists(boolean eximErrorExists) {
		this.eximErrorExists = eximErrorExists;
	}

	/**
   	 * @return the metaDataFlag
   	 */
   	public String getMetaDataFlag() {
   		if (this.getPropertyValue(META_DATA_FLAG) != null) {
               return ((String) this.getPropertyValue(META_DATA_FLAG));
           }
           return null;
   	}

   	/**
   	 * @param metaDataFlag the metaDataFlag to set
   	 */
   	public void setMetaDataFlag(String metaDataFlag) {
   		 this.setPropertyValue(META_DATA_FLAG, metaDataFlag);
   	}
   	
    /**
   	 * @return the metaDataUrl
   	 */
   	public String getMetaDataUrl() {
   		if (this.getPropertyValue(META_DATA_URL) != null) {
               return ((String) this.getPropertyValue(META_DATA_URL));
           }
           return null;
   	}

   	/**
   	 * @param metaDataUrl the metaDataUrl to set
   	 */
   	public void setMetaDataUrl(String metaDataUrl) {
   		 this.setPropertyValue(META_DATA_URL, metaDataUrl);
   	}
   	
    /**
   	 * @return the moderationFlag
   	 */
   	public String getModerationFlag() {
   		if (this.getPropertyValue(MODERATION_FLAG) != null) {
               return ((String) this.getPropertyValue(MODERATION_FLAG));
           }
           return null;
   	}

   	/**
   	 * @param moderationFlag the moderationFlag to set
   	 */
   	public void setModerationFlag(String moderationFlag) {
   		 this.setPropertyValue(MODERATION_FLAG, moderationFlag);
   	}
   	
    /**
   	 * @return the moderationUrl
   	 */
   	public String getModerationUrl() {
   		if (this.getPropertyValue(MODERATION_URL) != null) {
               return ((String) this.getPropertyValue(MODERATION_URL));
           }
           return null;
   	}

   	/**
   	 * @param moderationUrl the moderationUrl to set
   	 */
   	public void setModerationUrl(String moderationUrl) {
   		 this.setPropertyValue(MODERATION_URL, moderationUrl);
   	}
   	
    /** 
     * @return the personalizeCost 
     */
    public double getPersonalizeCost() {
        if (this.getPropertyValue(PERSONALIZE_COST) != null) {
            return ((Double) this.getPropertyValue(PERSONALIZE_COST)).doubleValue();
        }
        return 0.0;
    }

    /** 
     * @param personalizePrice the personalizePrice to set
     */
    public void setPersonalizeCost(final double personalizeCost) {
        this.setPropertyValue(PERSONALIZE_COST, Double.valueOf(personalizeCost));
    }
    
    /** 
     * @return the personalizePrice 
     */
    public double getPersonalizePrice() {
        if (this.getPropertyValue(PERSONALIZE_PRICE) != null) {
            return ((Double) this.getPropertyValue(PERSONALIZE_PRICE)).doubleValue();
        }
        return 0.0;
    }

    /** 
     * @param personalizePrice the personalizePrice to set
     */
    public void setPersonalizePrice(final double personalizePrice) {
        this.setPropertyValue(PERSONALIZE_PRICE, Double.valueOf(personalizePrice));
    }
    
    /**
	 * @return the personalizationDetails
	 */
	public String getPersonalizationDetails() {
		if (this.getPropertyValue(PERSONALIZATION_DETAILS) != null) {
            return ((String) this.getPropertyValue(PERSONALIZATION_DETAILS)).replaceAll("\n", "<br>").replaceAll("\\n", "<br>").replaceAll("\\\n", "<br>").replaceAll("\\\\n", "<br>");
        }
        return null;
	}

	/**
	 * @param personalizationDetails the personalizationDetails to set
	 */
	public void setPersonalizationDetails(String personalizationDetails) {
		if(personalizationDetails!=null){
			 personalizationDetails = personalizationDetails.replaceAll("\n", "<br>").replaceAll("\\n", "<br>").replaceAll("\\\n", "<br>").replaceAll("\\\\n", "<br>");
		}
		this.setPropertyValue(PERSONALIZATION_DETAILS, personalizationDetails);
    }
	
    /**
	 * @return the personalizationOptions
	 */
	public String getPersonalizationOptions() {
		if (this.getPropertyValue(PERSONALIZATION_OPTIONS) != null) {
            return ((String) this.getPropertyValue(PERSONALIZATION_OPTIONS));
        }
        return null;
	}

	/**
	 * @param personalizationOptions the personalizationOptions to set
	 */
	public void setPersonalizationOptions(String personalizationOptions) {
		 this.setPropertyValue(PERSONALIZATION_OPTIONS, personalizationOptions);
	}
    
    /**
	 * @return the mobileThumbnailImagePath
	 */
	public String getMobileThumbnailImagePath() {
		if (this.getPropertyValue(MOBILE_THUMBNAIL_IMAGE_PATH) != null) {
            return ((String) this.getPropertyValue(MOBILE_THUMBNAIL_IMAGE_PATH));
        }
        return null;
	}

	/**
	 * @param mobileThumbnailImagePath the mobileThumbnailImagePath to set
	 */
	public void setMobileThumbnailImagePath(String mobileThumbnailImagePath) {
		 this.setPropertyValue(MOBILE_THUMBNAIL_IMAGE_PATH, mobileThumbnailImagePath);
	}
    
    /**
	 * @return the mobileFullImagePath
	 */
	public String getMobileFullImagePath() {
		if (this.getPropertyValue(MOBILE_FULL_IMAGE_PATH) != null) {
            return ((String) this.getPropertyValue(MOBILE_FULL_IMAGE_PATH));
        }
        return null;
	}

	/**
	 * @param mobileFullImagePath the mobileFullImagePath to set
	 */
	public void setMobileFullImagePath(String mobileFullImagePath) {
		 this.setPropertyValue(MOBILE_FULL_IMAGE_PATH, mobileFullImagePath);
	}
    
    /**
	 * @return the thumbnailImagePath
	 */
	public String getThumbnailImagePath() {
		if (this.getPropertyValue(THUMBNAIL_IMAGE_PATH) != null) {
            return ((String) this.getPropertyValue(THUMBNAIL_IMAGE_PATH));
        }
        return null;
	}

	/**
	 * @param thumbnailImagePath the thumbnailImagePath to set
	 */
	public void setThumbnailImagePath(String thumbnailImagePath) {
		 this.setPropertyValue(THUMBNAIL_IMAGE_PATH, thumbnailImagePath);
	}
    
    /**
	 * @return the fullImagePath
	 */
	public String getFullImagePath() {
		if (this.getPropertyValue(FULL_IMAGE_PATH) != null) {
            return ((String) this.getPropertyValue(FULL_IMAGE_PATH));
        }
        return null;
	}

	/**
	 * @param fullImagePath the fullImagePath to set
	 */
	public void setFullImagePath(String fullImagePath) {
		 this.setPropertyValue(FULL_IMAGE_PATH, fullImagePath);
	}
    
    /**
	 * @return the referenceNumber
	 */
	public String getReferenceNumber() {
		if (this.getPropertyValue(REFERENCE_NUMBER) != null) {
            return ((String) this.getPropertyValue(REFERENCE_NUMBER));
        }
        return null;
	}

	/**
	 * @param referenceNumber the referenceNumber to set
	 */
	public void setReferenceNumber(String referenceNumber) {
		 this.setPropertyValue(REFERENCE_NUMBER, referenceNumber);
	}
   
    
    /**
	 * @return the ltlItem
	 */
	public boolean isLtlItem() {
		if (this.getPropertyValue(LTLFLAG) != null) {
            return ((Boolean) this.getPropertyValue(LTLFLAG)).booleanValue();
        }
        return false;
	}
	
	/**
	 * @param ltlItem
	 */
	public void setLtlItem(boolean ltlItem) {
		this.setPropertyValue(LTLFLAG, Boolean.valueOf(ltlItem));
	}

    

	/** @return the msgShownOOS */
    public boolean isMsgShownOOS() {
        if (this.getPropertyValue(MSGSHOWNOOS) != null) {
            return ((Boolean) this.getPropertyValue(MSGSHOWNOOS)).booleanValue();
        }
        return false;
    }

    /** @param pMsgShownOOS the msgShownOOS to set */
    public void setMsgShownOOS(final boolean pMsgShownOOS) {
        this.setPropertyValue(MSGSHOWNOOS, Boolean.valueOf(pMsgShownOOS));
    }

    /** @return the msgShownFlagOff */
    public boolean isMsgShownFlagOff() {
        if (this.getPropertyValue(MSGSHOWNFLAGOFF) != null) {
            return ((Boolean) this.getPropertyValue(MSGSHOWNFLAGOFF)).booleanValue();
        }
        return false;
    }

    /** @param pMsgShownFlagOff the msgShownFlagOff to set */
    public void setMsgShownFlagOff(final boolean pMsgShownFlagOff) {
        this.setPropertyValue(MSGSHOWNFLAGOFF, Boolean.valueOf(pMsgShownFlagOff));
    }

    /** @return the seqNumber */
    public int getSeqNumber() {
        if (this.getPropertyValue(SEQNUMBER) != null) {
            return ((Integer) this.getPropertyValue(SEQNUMBER)).intValue();
        }
        return 0;
    }

    /** @param pSeqNumber the seqNumber to set */
    public void setSeqNumber(final int pSeqNumber) {
        this.setPropertyValue(SEQNUMBER, Integer.valueOf(pSeqNumber));
    }

    /** @return the commerceItemMoved */
    public String getCommerceItemMoved() {
        return this.commerceItemMoved;
    }

    /** @param pCommerceItemMoved the commerceItemMoved to set */
    public void setCommerceItemMoved(final String pCommerceItemMoved) {
        this.commerceItemMoved = pCommerceItemMoved;
    }

    // Methods
    // ----------------------------------------------------------------

    /** @return the prevPrice */
    public double getPrevPrice() {
        if (this.getPropertyValue(PREV_PRICE) != null) {
            return ((Double) this.getPropertyValue(PREV_PRICE)).doubleValue();
        }
        return 0.0;
    }

    /** @param prevPrice the prevPrice to set */
    public void setPrevPrice(final double prevPrice) {
        this.setPropertyValue(PREV_PRICE, Double.valueOf(prevPrice));
    }

    /** @return the itemMoved */
    public boolean isItemMoved() {
        return this.itemMoved;
    }

    /** @param pItemMoved the itemMoved to set */
    public void setItemMoved(final boolean pItemMoved) {
        this.itemMoved = pItemMoved;
    }

    /**
     * @return the ltlShipMethod
     */
    public String getLtlShipMethod() {
    	if (this.getPropertyValue(LTL_SHIP_METHOD) != null) {
            return ((String) this.getPropertyValue(LTL_SHIP_METHOD));
        }
        return null;
	}

	/**
	 * @param ltlShipMethod
	 */
	public void setLtlShipMethod(final String ltlShipMethod) {
		this.setPropertyValue(LTL_SHIP_METHOD, ltlShipMethod);
	}
	
	private boolean shipMethodUnsupported;
	
	/**
	 * @return the shipMethodUnsupported
	 */
	public boolean isShipMethodUnsupported() {
		return shipMethodUnsupported;
	}

	/**
	 * @param shipMethodUnsupported the shipMethodUnsupported to set
	 */
	public void setShipMethodUnsupported(boolean shipMethodUnsupported) {
		this.shipMethodUnsupported = shipMethodUnsupported;
	}
	
	private String prevLtlShipMethod;

	 
	
	/**
	 * @return the prevLtlShipMethod
	 */
    public String getPrevLtlShipMethod() {
       return prevLtlShipMethod;
    }
    
    /**
	 * @param prevLtlShipMethod the prevLtlShipMethod to set
	 */
    public void setPrevLtlShipMethod(String prevLtlShipMethod) {

            this.prevLtlShipMethod = prevLtlShipMethod;

    }

	/**
	 * @return the whiteGloveAssembly
	 */
	public String getWhiteGloveAssembly() {
		if (this.getPropertyValue(LTL_WHITE_GLOVE_ASSEMBLY) != null) {
            return ((String) this.getPropertyValue(LTL_WHITE_GLOVE_ASSEMBLY));
        }
        return null;
	}

	/**
	 * @param whiteGloveAssembly the whiteGloveAssembly to set
	 */
	public void setWhiteGloveAssembly(String whiteGloveAssembly) {
		 this.setPropertyValue(LTL_WHITE_GLOVE_ASSEMBLY, whiteGloveAssembly);
	}

	/** @return the vdcInd */
    public boolean isVdcInd() {
        if (this.getPropertyValue(VDC_IND) != null) {
            return ((Boolean) this.getPropertyValue(VDC_IND)).booleanValue();
        }
        return false;

    }

    /** @param vdcInd the vdcInd to set */
    public void setVdcInd(final boolean vdcInd) {
        this.setPropertyValue(VDC_IND, Boolean.valueOf(vdcInd));
    }

    /** @return the isBTS */
    public boolean getBts() {
        if (this.getPropertyValue(IS_BTS) != null) {
            return ((Boolean) this.getPropertyValue(IS_BTS)).booleanValue();
        }
        return false;
    }

    /** @param bts the isBTS to set */
    public void setBts(final boolean bts) {
        this.setPropertyValue(IS_BTS, Boolean.valueOf(bts));
    }

    /** Gets the value of freeShippingMethod.
     *
     * @return Free Shipping Method */
    public String getFreeShippingMethod() {
        return (String) this.getPropertyValue(FREE_SHIPPING_METHOD);
    }

    /** Sets the value to parentCommerceItem.
     *
     * @param freeShippingMethod */
    public void setFreeShippingMethod(final String freeShippingMethod) {
        this.setPropertyValue(FREE_SHIPPING_METHOD, freeShippingMethod);
    }

    /** Gets the value of registryId.
     *
     * @return Registry ID */
    public String getRegistryId() {
        return (String) this.getPropertyValue(REGISTRY_ID);
    }

    /** Sets the value to registryId.
     *
     * @param registryId */
    public void setRegistryId(final String registryId) {
        this.setPropertyValue(REGISTRY_ID, registryId);
    }

    /** Gets the value of registryInfo.
     *
     * @return Registry Information */
    public String getRegistryInfo() {
        return (String) this.getPropertyValue(REGISTRY_INFO);
    }

    /** Sets the value to registryInfo.
     *
     * @param registryInfo */
    public void setRegistryInfo(final String registryInfo) {
        this.setPropertyValue(REGISTRY_INFO, registryInfo);
    }

    /** Gets the value of lastModifiedDate.
     *
     * @return Last Modified Date */
    public Date getLastModifiedDate() {
        return (Date) this.getPropertyValue(LAST_MODIFIED_DATE);
    }

    /** Sets the value to lastModifiedDate.
     *
     * @param pLastModifiedDate */
    public void setLastModifiedDate(final Date pLastModifiedDate) {
        this.setPropertyValue(LAST_MODIFIED_DATE, pLastModifiedDate);
    }

    /** Gets the value of SkuSurcharge.
     *
     * @return SKU Surcharge */
    public double getSkuSurcharge() {
        if (this.getPropertyValue(SKU_SURCHARGE) != null) {
            return ((Double) this.getPropertyValue(SKU_SURCHARGE)).doubleValue();
        }
        return 0.0;
    }

    /** Sets the value to SkuSurcharge.
     *
     * @param skuSurcharge */
    public void setSkuSurcharge(final double skuSurcharge) {
        this.setPropertyValue(SKU_SURCHARGE, Double.valueOf(skuSurcharge));
    }

    /** Gets the value of EcoFeeEligible.
     *
     * @return Eco Free Eligible */
    public boolean getIsEcoFeeEligible() {
        return this.ecoFeeEligible;
    }

    /** Sets the value to EcoFeeEligible.
     *
     * @param pIsEcoFeeEligible */
    public void setIsEcoFeeEligible(final boolean pIsEcoFeeEligible) {
        this.ecoFeeEligible = pIsEcoFeeEligible;
    }

    /** @return Store SKU */
    public boolean isStoreSKU() {
        return this.storeSKU;
    }

    /** @param pStoreSKU */
    public void setStoreSKU(final boolean pStoreSKU) {
        this.storeSKU = pStoreSKU;
    }

    /**
	 * @return the whiteGloveAssembly
	 */
	public String getAssemblyItemId() {
		if (this.getPropertyValue(LTL_ASSEMBLY_ITEM_ID) != null) {
            return ((String) this.getPropertyValue(LTL_ASSEMBLY_ITEM_ID));
        }
        return null;
	}

	/**
	 * @param whiteGloveAssembly the whiteGloveAssembly to set
	 */
	public void setAssemblyItemId(String assemblyItemId) {
		 this.setPropertyValue(LTL_ASSEMBLY_ITEM_ID, assemblyItemId);
	}
	
	 /**
	 * @return the whiteGloveAssembly
	 */
	public String getDeliveryItemId() {
		if (this.getPropertyValue(LTL_DELIVERY_ITEM_ID) != null) {
            return ((String) this.getPropertyValue(LTL_DELIVERY_ITEM_ID));
        }
        return null;
	}

	/**
	 * @param whiteGloveAssembly the whiteGloveAssembly to set
	 */
	public void setDeliveryItemId(String deliveryItemId) {
		 this.setPropertyValue(LTL_DELIVERY_ITEM_ID, deliveryItemId);
	}
    
	/**
	 * @return the TrackingInfo Map of commerce Item
	 */
    @SuppressWarnings("unchecked")
    public final Map<String, TrackingInfo> getTrackingInfosMap() {
    	Map<String, TrackingInfo> trackingInfos = null;
    	final List<MutableRepositoryItem> itemTrackingList = (List<MutableRepositoryItem>) this.getPropertyValue(TRACKING);
    	if ((itemTrackingList != null) && !itemTrackingList.isEmpty()) {
    		trackingInfos = new HashMap<String, TrackingInfo>();
    		for (final MutableRepositoryItem itemTrackingInfo : itemTrackingList) {
    			if (itemTrackingInfo != null) {
    				final TrackingInfo ti = new TrackingInfo(itemTrackingInfo);
    				trackingInfos.put(ti.getTrackingNumber(), ti);
    			}
    		}
    	}
    	return trackingInfos;
    }
    
    /**
	 * Sets the value to Tracking Info of commerce item
	 */
    @SuppressWarnings ("unchecked")
    public final void setTrackingInfos(final Map<String, TrackingInfo> lineItemTrackerMap) {

    	final List<MutableRepositoryItem> itemTrackingList = (List<MutableRepositoryItem>) this.getPropertyValue(TRACKING);

    	if ((lineItemTrackerMap != null) && !lineItemTrackerMap.isEmpty()) {
    		itemTrackingList.clear();
    		for (final TrackingInfo st : lineItemTrackerMap.values()) {
    			itemTrackingList.add(st.getRepositoryItem());
    		}
    	}
    	this.setPropertyValue(TRACKING, itemTrackingList);
    }
	
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("BBBCommerceItem [commerceItemMoved=").append(this.commerceItemMoved)
                        .append(", ecoFeeEligible=").append(this.ecoFeeEligible).append(", storeSKU=")
                        .append(this.storeSKU).append(", itemMoved=").append(this.itemMoved)
                        .append(", isMsgShownOOS()=").append(this.isMsgShownOOS()).append(", isMsgShownFlagOff()=")
                        .append(this.isMsgShownFlagOff()).append(", getSeqNumber()=").append(this.getSeqNumber())
                        .append(", getCommerceItemMoved()=").append(this.getCommerceItemMoved())
                        .append(", getPrevPrice()=").append(this.getPrevPrice()).append(", isItemMoved()=")
                        .append(this.isItemMoved()).append(", isVdcInd()=").append(this.isVdcInd())
                        .append(", getBts()=").append(this.getBts()).append(", getFreeShippingMethod()=")
                        .append(this.getFreeShippingMethod()).append(", getRegistryId()=").append(this.getRegistryId())
                        .append(", getRegistryInfo()=").append(this.getRegistryInfo())
                        .append(", getLastModifiedDate()=").append(this.getLastModifiedDate())
                        .append(", getSkuSurcharge()=").append(this.getSkuSurcharge())
                        .append(", getIsEcoFeeEligible()=").append(this.getIsEcoFeeEligible())
                        .append(", isStoreSKU()=").append(this.isStoreSKU())
                        .append(", getPersonalizePrice()=").append(this.getPersonalizePrice())
                        .append(", getPersonalizeCost()=").append(this.getPersonalizeCost())
						.append(", getPersonalizationDetails()=").append(this.getPersonalizationDetails())
						.append(", getPersonalizationOptions()=").append(this.getPersonalizationOptions())
						.append(", getMobileThumbnailImagePath()=").append(this.getMobileThumbnailImagePath())
						.append(", getMobileFullImagePath()=").append(this.getMobileFullImagePath())
						.append(", getThumbnailImagePath()=").append(this.getThumbnailImagePath())
						.append(", getFullImagePath()=").append(this.getFullImagePath())
						.append(", getReferenceNumber()=").append(this.getReferenceNumber())
						.append(", getRegistrantShipMethod ()=").append(this.getRegistrantShipMethod())
                        .append(", getLtlShipMethod()=").append(this.getLtlShipMethod()).append("]");
        return builder.toString();
    }

	public boolean isBuyOffAssociatedItem() {
		return buyOffAssociatedItem;
	}

	public void setBuyOffAssociatedItem(boolean buyOffAssociatedItem) {
		this.buyOffAssociatedItem = buyOffAssociatedItem;
	}

	public String getBuyOffPrimaryRegFirstName() {
		return buyOffPrimaryRegFirstName;
	}

	public void setBuyOffPrimaryRegFirstName(String buyOffPrimaryRegFirstName) {
		this.buyOffPrimaryRegFirstName = buyOffPrimaryRegFirstName;
	}

	public String getBuyOffCoRegFirstName() {
		return buyOffCoRegFirstName;
	}

	public void setBuyOffCoRegFirstName(String buyOffCoRegFirstName) {
		this.buyOffCoRegFirstName = buyOffCoRegFirstName;
	}

	public String getBuyOffRegistryEventType() {
		return buyOffRegistryEventType;
	}

	public void setBuyOffRegistryEventType(String buyOffRegistryEventType) {
		this.buyOffRegistryEventType = buyOffRegistryEventType;
	}
	
	/**
	 * 
	 * @return the registrantShipMethod
	 */
	public String getRegistrantShipMethod() {
		if (this.getPropertyValue(ORIG_LTL_SHIP_METHOD) != null) {
            return ((String) this.getPropertyValue(ORIG_LTL_SHIP_METHOD));
        }
        return null;
	}
	
	/**
	 * 
	 * @param the registrantShipMethod to set
	 */
	public void setRegistrantShipMethod(String registrantShipMethod) {
		this.setPropertyValue(ORIG_LTL_SHIP_METHOD, registrantShipMethod);
	}
}
