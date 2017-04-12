package com.bbb.commerce.cart.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bbb.commerce.cart.PricingMessageVO;
import com.bbb.commerce.catalog.vo.AttributeVO;
import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.commerce.catalog.vo.VendorInfoVO;
import com.bbb.commerce.common.PriceInfoDisplayVO;
import com.bbb.constants.BBBCoreConstants;

public class CommerceItemDisplayVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean giftWrapEligible;
	private int stockAvailability;
	private PriceInfoDisplayVO commerceItemPriceInfo;
	private String freeShipMethods;
	private List<ShipMethodVO> eligibleShipMethods=new ArrayList<ShipMethodVO>();
	private String commaSepNonShipableStates;
	private String skuId;
	private double shippingSurcharge;
	private boolean isEcoFeeApplicable;
	private boolean vdcSku;
	private String vdcSKUShipMesage;
	//BPSI-3297  | Set the VDC dynamic ship message for Learn more
	private String vdcSKULearnMoreMesage;
	private String skuDisplayName;
	private boolean webOfferedFlag;
	private String skuDescription;
	//private String skuLongDescription;
	private String skuColor;
	private boolean skuGiftCard;
	private boolean bopusAllowed;
	private boolean isEcoFeeEligible;
	private boolean skuActiveFlag;
	private boolean skuDisableFlag;
	private boolean skuHasRebate;
	private String commerceItemId;
	private String productId;
	private String storeId;
	private String skuThumbnailImage;
	private String skuSmallImage;
	private Date lastModifiedDate;
	private String basicImage;
	private String registryId;
	private String productSeoUrl;
	private boolean skuBelowLine;
	private PricingMessageVO mPriceMessageVO;
	private String size;
	private String upc;
	private boolean isPackNHold;
	private List<AttributeVO> restrictedAttributes;
	private boolean ltlItem; //LTL Change
	private boolean shippingMethodAvl; // LTL Change
	private boolean intlRestricted;
	//BPSI-551
	private String ltlShipMethod;
	private String ltlShipMethodDesc;
	private Boolean whiteGloveAssembly;
	private boolean shipMethodUnsupported;
	private boolean buyOffAssociatedItem;
	private String buyOffPrimaryRegFirstName;
    private String buyOffCoRegFirstName;
    private String buyOffRegistryEventType;
    
    //BPSI-2440  | Set the VDC offset message
	private String vdcOffsetMessage;
	private boolean shippingRestricted;
	
	// Adding parameters for katori/Exim
	private String referenceNumber;
	private String personalizationType;
	private String mobileFullImagePath;
	private String mobileThumbnailImagePath;
	private double personalizePrice;
	private double personalizeCost;
	private String personalizationOptions;
	private String personalizationOptionsDisplay;
	private String personalizationDetails;
	private boolean eximErrorExists;
	private String highestshipMethod;
	private String regisryShipMethod;
	private String sddAvailabilityStatus;
	private boolean isPorchService;
	private String priceEstimation;
	private String porchProjectId;	
	private List<String> porchServiceFamilyCodes;
	private String porchServiceFamilyType;
	private boolean incartPriceItem ;
	
	 
	private VendorInfoVO vendorInfoVO;
	
	public VendorInfoVO getVendorInfoVO() {
		return vendorInfoVO;
	}
	public void setVendorInfoVO(VendorInfoVO vendorInfoVO) {
		this.vendorInfoVO = vendorInfoVO;
	}
	public String getRegisryShipMethod() {
		return regisryShipMethod;
	}
	public void setRegisryShipMethod(String regisryShipMethod) {
		this.regisryShipMethod = regisryShipMethod;
	}
		 /**
     * Flag to check whether cart registry item is in guest or host view
     */
	private boolean guestView;
    
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
	/**
	 * @return the shippingRestricted
	 */
	public boolean isShippingRestricted() {
		return shippingRestricted;
	}

	/**
	 * @param shippingRestricted the shippingRestricted to set
	 */
	public void setShippingRestricted(boolean shippingRestricted) {
		this.shippingRestricted = shippingRestricted;
	}

	private String fullImagePath;
	
	public String getFullImagePath() {
		return fullImagePath;
	}

	public void setFullImagePath(String fullImagePath) {
		this.fullImagePath = fullImagePath;
	}

	/**
	 * @return the String vdcOffsetMessage
	 */
	public String getVdcOffsetMessage() {
		return this.vdcOffsetMessage;
	}
	
	/**
	 * @param vdcOffsetMessage
	 */
	public void setVdcOffsetMessage(String vdcOffsetMessage) {
		this.vdcOffsetMessage = vdcOffsetMessage;
	}

	
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

	public List<AttributeVO> getRestrictedAttributes() {
		return restrictedAttributes;
	}

	public void setRestrictedAttributes(List<AttributeVO> restrictedAttributes) {
		this.restrictedAttributes = restrictedAttributes;
	}
	
	public boolean isPackNHold() {
		return isPackNHold;
	}

	public void setPackNHold(boolean isPackNHold) {
		this.isPackNHold = isPackNHold;
	}

	/**
	 * @return the size
	 */
	public String getSize() {
		return size;
	}

	/**
	 * @param pSize the size to set
	 */
	public void setSize(String pSize) {
		size = pSize;
	}

	/**
	 * @return the upc
	 */
	public String getUpc() {
		return upc;
	}

	/**
	 * @param pUpc the upc to set
	 */
	public void setUpc(String pUpc) {
		upc = pUpc;
	}

	/**
	 * @return the priceMessageVO
	 */
	public PricingMessageVO getPriceMessageVO() {
		return mPriceMessageVO;
	}

	/**
	 * @param pPriceMessageVO the priceMessageVO to set
	 */
	public void setPriceMessageVO(PricingMessageVO pPriceMessageVO) {
		mPriceMessageVO = pPriceMessageVO;
	}

	/**
	 * @return the productSeoUrl
	 */
	public String getProductSeoUrl() {
		return productSeoUrl;
	}

	/**
	 * @param productSeoUrl the productSeoUrl to set
	 */
	public void setProductSeoUrl(String productSeoUrl) {
		this.productSeoUrl = productSeoUrl;
	}
	/**
	 * @return the registryId
	 */
	public String getRegistryId() {
		return registryId;
	}

	/**
	 * @param registryId the registryId to set
	 */
	public void setRegistryId(String registryId) {
		this.registryId = registryId;
	}

	/**
	 * @return the basicImage
	 */
	public String getBasicImage() {
		return basicImage;
	}

	/**
	 * @param pBasicImage the basicImage to set
	 */
	public void setBasicImage(String pBasicImage) {
		if(pBasicImage != null && pBasicImage.contains(BBBCoreConstants.QUESTION_MARK)){
			String[] split = pBasicImage.split(BBBCoreConstants.QUESTION_MARK_2);
			basicImage = split[0];
		} else {
			basicImage =pBasicImage; 
		}
	}

	public boolean isSkuActiveFlag() {
		return skuActiveFlag;
	}

	public void setSkuActiveFlag(boolean skuActiveFlag) {
		this.skuActiveFlag = skuActiveFlag;
	}

	public boolean isSkuDisableFlag() {
		return skuDisableFlag;
	}

	public void setSkuDisableFlag(boolean skuDisableFlag) {
		this.skuDisableFlag = skuDisableFlag;
	}

	public boolean isSkuHasRebate() {
		return skuHasRebate;
	}

	public void setSkuHasRebate(boolean skuHasRebate) {
		this.skuHasRebate = skuHasRebate;
	}

	public boolean isSkuOnSale() {
		return skuOnSale;
	}

	public void setSkuOnSale(boolean skuOnSale) {
		this.skuOnSale = skuOnSale;
	}

	private boolean skuOnSale;
	private String expectedDeliveryDateForLTLItem;
	private String itemLevelExpectedDeliveryDate;

	
	/**
	 * @return the itemLevelExpectedDeliveryDate
	 */
	public String getItemLevelExpectedDeliveryDate() {
		return itemLevelExpectedDeliveryDate;
	}

	/**
	 * @param itemLevelExpectedDeliveryDate the itemLevelExpectedDeliveryDate to set
	 */
	public void setItemLevelExpectedDeliveryDate(
			String itemLevelExpectedDeliveryDate) {
		this.itemLevelExpectedDeliveryDate = itemLevelExpectedDeliveryDate;
	}

	/**
	 * @return the commaSepNonShipableStates
	 */
	public String getCommaSepNonShipableStates() {
		return commaSepNonShipableStates;
	}

	/**
	 * @param commaSepNonShipableStates the commaSepNonShipableStates to set
	 */
	public void setCommaSepNonShipableStates(String commaSepNonShipableStates) {
		this.commaSepNonShipableStates = commaSepNonShipableStates;
	}

	
	public boolean isGiftWrapEligible() {
		return giftWrapEligible;
	}

	public void setGiftWrapEligible(boolean giftWrapEligible) {
		this.giftWrapEligible = giftWrapEligible;
	}

	public String getSkuId() {
		return skuId;
	}

	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}

	public double getShippingSurcharge() {
		return shippingSurcharge;
	}

	public void setShippingSurcharge(double shippingSurcharge) {
		this.shippingSurcharge = shippingSurcharge;
	}

	public boolean isEcoFeeApplicable() {
		return isEcoFeeApplicable;
	}

	public void setEcoFeeApplicable(boolean isEcoFeeApplicable) {
		this.isEcoFeeApplicable = isEcoFeeApplicable;
	}

	public boolean isVdcSku() {
		return vdcSku;
	}

	public void setVdcSku(boolean vdcSku) {
		this.vdcSku = vdcSku;
	}

	public String getVdcSKUShipMesage() {
		return vdcSKUShipMesage;
	}

	public void setVdcSKUShipMesage(String vdcSKUShipMesage) {
		this.vdcSKUShipMesage = vdcSKUShipMesage;
	}



	public boolean isWebOfferedFlag() {
		return webOfferedFlag;
	}

	public void setWebOfferedFlag(boolean webOfferedFlag) {
		this.webOfferedFlag = webOfferedFlag;
	}


	public boolean isSkuGiftCard() {
		return skuGiftCard;
	}

	public void setSkuGiftCard(boolean skuGiftCard) {
		this.skuGiftCard = skuGiftCard;
	}

	public boolean isBopusAllowed() {
		return bopusAllowed;
	}

	public void setBopusAllowed(boolean bopusAllowed) {
		this.bopusAllowed = bopusAllowed;
	}


	public void setCommerceItemPriceInfo(PriceInfoDisplayVO commerceItemPriceInfo) {
		this.commerceItemPriceInfo = commerceItemPriceInfo;
	}

	public PriceInfoDisplayVO getCommerceItemPriceInfo() {
		return commerceItemPriceInfo;
	}

	public void setEligibleShipMethods(List<ShipMethodVO> eligibleShipMethods) {
		this.eligibleShipMethods = eligibleShipMethods;
	}

	public List<ShipMethodVO> getEligibleShipMethods() {
		return eligibleShipMethods;
	}

	
	public void setCommerceItemId(String commerceItemId) {
		this.commerceItemId = commerceItemId;
	}

	public String getCommerceItemId() {
		return commerceItemId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductId() {
		return productId;
	}

	public void setSkuThumbnailImage(String skuThumbnailImage) {
		this.skuThumbnailImage = skuThumbnailImage;
		setBasicImage(skuThumbnailImage);
	}

	public String getSkuThumbnailImage() {
		return skuThumbnailImage;
	}

	public void setSkuDisplayName(String skuDisplayName) {
		this.skuDisplayName = skuDisplayName;
	}

	public String getSkuDisplayName() {
		return skuDisplayName;
	}

	public void setSkuDescription(String skuDescription) {
		this.skuDescription = skuDescription;
	}

	public String getSkuDescription() {
		return skuDescription;
	}

	public void setSkuColor(String skuColor) {
		this.skuColor = skuColor;
	}

	public String getSkuColor() {
		return skuColor;
	}

	public void setSkuSmallImage(String skuSmallImage) {
		this.skuSmallImage = skuSmallImage;
		setBasicImage(skuSmallImage);
	}

	public String getSkuSmallImage() {
		return skuSmallImage;
	}

	public void setStockAvailability(int stockAvailability) {
		this.stockAvailability = stockAvailability;
	}

	public int getStockAvailability() {
		return stockAvailability;
	}

	public void setEcoFeeEligible(boolean isEcoFeeEligible) {
		this.isEcoFeeEligible = isEcoFeeEligible;
	}

	public boolean isEcoFeeEligible() {
		return isEcoFeeEligible;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	/**
	 * @return the freeShipMethods
	 */
	public String getFreeShipMethods() {
		return freeShipMethods;
	}

	/**
	 * @param freeShipMethods the freeShipMethods to set
	 */
	public void setFreeShipMethods(String freeShipMethods) {
		this.freeShipMethods = freeShipMethods;
	}
	/**
	 * @return the ltlItem
	 */
	public boolean isLtlItem() {
		return ltlItem;
	}

	/**
	 * @param ltlItem the ltlItem to set
	 */
	public void setLtlItem(boolean ltlItem) {
		this.ltlItem = ltlItem;
}
	
	/**
	 * To check the shipping method .
	 * @return true if shipping method available
	 */
	public boolean isShippingMethodAvl() {
		return shippingMethodAvl;
	}

	/**
	 * Set shipping method available flag
	 * @param shippingMethodAvl
	 */
	public void setShippingMethodAvl(boolean shippingMethodAvl) {
		this.shippingMethodAvl = shippingMethodAvl;
	}
	//LTL-1235 expectedDeliveryDateForLTLItem at Item level
	/**
	 * expectedDeliveryDateForLTLItem at Item level
	 * @return
	 */
	public String getExpectedDeliveryDateForLTLItem() {
		return expectedDeliveryDateForLTLItem;
	}
	/**
	 * expectedDeliveryDateForLTLItem at Item level
	 * @param expectedDeliveryDateForLTLItem
	 */
	public void setExpectedDeliveryDateForLTLItem(
			String expectedDeliveryDateForLTLItem) {
		this.expectedDeliveryDateForLTLItem = expectedDeliveryDateForLTLItem;
	}
	//LTL-1235 expectedDeliveryDateForLTLItem at Item level
	public boolean isSkuBelowLine() {
		return skuBelowLine;
	}

	public void setSkuBelowLine(boolean skuBelowLine) {
		this.skuBelowLine = skuBelowLine;
	}

	public boolean isIntlRestricted() {
		return intlRestricted;
	}

	public void setIntlRestricted(boolean intlRestricted) {
		this.intlRestricted = intlRestricted;
	}

	/** BPSI-551  getter for ltl ship method 
	 * @return
	 */
	public String getLtlShipMethod() {
		return ltlShipMethod;
	}

	/**
	 *  BPSI-551  Setter for ltl ship method 
	 * @param ltlShipMethod
	 */
	public void setLtlShipMethod(String ltlShipMethod) {
		this.ltlShipMethod = ltlShipMethod;
	}
	
	
	/** BPSI-551  getter for whiteGloveAssembly 
	 * @return
	 */
	public Boolean getWhiteGloveAssembly() {
		return whiteGloveAssembly;
	}
	/**
	 *  BPSI-551  Setter for whiteGloveAssembly 
	 * @param whiteGloveAssembly
	 */
	public void setWhiteGloveAssembly(Boolean whiteGloveAssembly) {
		this.whiteGloveAssembly = whiteGloveAssembly;
	}

	public String getLtlShipMethodDesc() {
		return ltlShipMethodDesc;
	}

	public void setLtlShipMethodDesc(String ltlShipMethodDesc) {
		this.ltlShipMethodDesc = ltlShipMethodDesc;
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
	 * Getter for mPriceMessageVO
	 * @return the mPriceMessageVO
	 */
	public PricingMessageVO getmPriceMessageVO() {
		return mPriceMessageVO;
	}

	/**
	 * Setter for mPriceMessageVO
	 * @param mPriceMessageVO the mPriceMessageVO to set
	 */
	public void setmPriceMessageVO(PricingMessageVO mPriceMessageVO) {
		this.mPriceMessageVO = mPriceMessageVO;
	}

	/**
	 * Getter for referenceNumber
	 * @return the referenceNumber
	 */
	public String getReferenceNumber() {
		return referenceNumber;
	}

	/**
	 * Setter for referenceNumber
	 * @param referenceNumber the referenceNumber to set
	 */
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	/**
	 * Getter for personalizationType
	 * @return the personalizationType
	 */
	public String getPersonalizationType() {
		return personalizationType;
	}

	/**
	 * Setter for personalizationType
	 * @param personalizationType the personalizationType to set
	 */
	public void setPersonalizationType(String personalizationType) {
		this.personalizationType = personalizationType;
	}

	/**
	 * Getter for mobileFullImagePath
	 * @return the mobileFullImagePath
	 */
	public String getMobileFullImagePath() {
		return mobileFullImagePath;
	}

	/**
	 * Setter for mobileFullImagePath
	 * @param mobileFullImagePath the mobileFullImagePath to set
	 */
	public void setMobileFullImagePath(String mobileFullImagePath) {
		this.mobileFullImagePath = mobileFullImagePath;
	}

	/**
	 * Getter for mobileThumbnailImagePath
	 * @return the mobileThumbnailImagePath
	 */
	public String getMobileThumbnailImagePath() {
		return mobileThumbnailImagePath;
	}

	/**
	 * Setter for mobileThumbnailImagePath
	 * @param mobileThumbnailImagePath the mobileThumbnailImagePath to set
	 */
	public void setMobileThumbnailImagePath(String mobileThumbnailImagePath) {
		this.mobileThumbnailImagePath = mobileThumbnailImagePath;
	}

	/**
	 * Getter for personalizePrice
	 * @return the personalizePrice
	 */
	public double getPersonalizePrice() {
		return personalizePrice;
	}

	/**
	 * Setter for personalizePrice
	 * @param personalizePrice the personalizePrice to set
	 */
	public void setPersonalizePrice(double personalizePrice) {
		this.personalizePrice = personalizePrice;
	}

	/**
	 * Getter for personalizeCost
	 * @return the personalizeCost
	 */
	public double getPersonalizeCost() {
		return personalizeCost;
	}

	/**
	 * Setter for personalizeCost
	 * @param personalizeCost the personalizeCost to set
	 */
	public void setPersonalizeCost(double personalizeCost) {
		this.personalizeCost = personalizeCost;
	}

	/**
	 * Getter for personalizationOptions
	 * @return the personalizationOptions
	 */
	public String getPersonalizationOptions() {
		return personalizationOptions;
	}

	/**
	 * Setter for personalizationOptions
	 * @param personalizationOptions the personalizationOptions to set
	 */
	public void setPersonalizationOptions(String personalizationOptions) {
		this.personalizationOptions = personalizationOptions;
	}

	/**
	 * Getter for personalizationDetails
	 * @return the personalizationDetails
	 */
	public String getPersonalizationDetails() {
		return personalizationDetails;
	}

	/**
	 * Setter for personalizationDetails
	 * @param personalizationDetails the personalizationDetails to set
	 */
	public void setPersonalizationDetails(String personalizationDetails) {
		this.personalizationDetails = personalizationDetails;
	}

	/**Getter for vdcSKULearnMoreMesage
	 * @return the vdcSKULearnMoreMesage
	 */
	public String getVdcSKULearnMoreMesage() {
		return vdcSKULearnMoreMesage;
	}

	/**Setter for vdcSKULearnMoreMesage
	 * @param vdcSKULearnMoreMesage the vdcSKULearnMoreMesage to set
	 */
	public void setVdcSKULearnMoreMesage(String vdcSKULearnMoreMesage) {
		this.vdcSKULearnMoreMesage = vdcSKULearnMoreMesage;
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

	public String getPersonalizationOptionsDisplay() {
		return personalizationOptionsDisplay;
	}

	public void setPersonalizationOptionsDisplay(
			String personalizationOptionsDisplay) {
		this.personalizationOptionsDisplay = personalizationOptionsDisplay;
	}

	public boolean isGuestView() {
		return guestView;
	}

	public void setGuestView(boolean guestView) {
		this.guestView = guestView;
	}
	public String getSddAvailabilityStatus() {
		return sddAvailabilityStatus;
	}

	public void setSddAvailabilityStatus(String sddAvailabilityStatus) {
		this.sddAvailabilityStatus = sddAvailabilityStatus;
	}
	/**
	 * @return the isPorchService
	 */
	public boolean isPorchService() {
		return isPorchService;
	}
	/**
	 * @param isPorchService the isPorchService to set
	 */
	public void setPorchService(boolean isPorchService) {
		this.isPorchService = isPorchService;
	}
	/**
	 * @return the priceEstimation
	 */
	public String getPriceEstimation() {
		return priceEstimation;
	}
	/**
	 * @param priceEstimation the priceEstimation to set
	 */
	public void setPriceEstimation(String priceEstimation) {
		this.priceEstimation = priceEstimation;
	}
	
	public String getPorchProjectId() {
		return porchProjectId;
	}
	public void setPorchProjectId(String porchProjectId) {
		this.porchProjectId = porchProjectId;
	}
	
	/**
	 * @return the porchServiceFamilyCodes
	 */
	public List<String> getPorchServiceFamilyCodes() {
		return porchServiceFamilyCodes;
	}
	/**
	 * @param porchServiceFamilyCodes the porchServiceFamilyCodes to set
	 */
	public void setPorchServiceFamilyCodes(List<String> porchServiceFamilyCodes) {
		this.porchServiceFamilyCodes = porchServiceFamilyCodes;
	}
	/**
	 * @return the porchServiceFamilyType
	 */
	public String getPorchServiceFamilyType() {
		return porchServiceFamilyType;
	}
	/**
	 * @param porchServiceFamilyType the porchServiceFamilyType to set
	 */
	public void setPorchServiceFamilyType(String porchServiceFamilyType) {
		this.porchServiceFamilyType = porchServiceFamilyType;
	}
	public boolean isIncartPriceItem() {
		return incartPriceItem;
	}
	
	public void setIncartPriceItem(boolean incartPriceItem) {
		this.incartPriceItem = incartPriceItem;
	}
	
}
