package com.bbb.commerce.catalog.comparison.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
//import java.util.Set;



import com.bbb.commerce.catalog.vo.AttributeVO;
import com.bbb.commerce.catalog.vo.BazaarVoiceProductVO;
import com.bbb.commerce.catalog.vo.VendorInfoVO;

/**
 * @author magga3
 *
 * This VO component class is used to store the sku level attributes and
 * product level attributes of the products which are in the compare 
 * drawer/ comparison list.
 * 
 */
public class CompareProductEntryVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//product level attributes
	private String productId;
	private String imagePath;
	private String mediumImagePath;
	private String productName;
	private String shortDescription;
	private String longDescription;
	private boolean collection;
	private BazaarVoiceProductVO reviews;
	private boolean productActive;
	private String priceRangeDescription;
	private String salePriceRangeDescription;
	private Map<String, AttributeVO> attributesList;
	private Map<String, AttributeVO> ltlAttributesList;
	private boolean ltlAttributeApplicable;
	private boolean intlRestricted;
	//Changes made for BBBH-2212
	private boolean shipMsgFlag;
	//Changes made for BBBH-2212
	private String displayShipMsg;
	private String priceLabelCode;
	private boolean inCartFlag;
	private boolean dynamicPricingProduct;
	/**
	 * 
	 * @return displayShipMsg
	 */
	public String getDisplayShipMsg() {
		return displayShipMsg;
	}
	/**
	 * 
	 * @param displayShipMsg
	 */
	public void setDisplayShipMsg(String displayShipMsg) {
		this.displayShipMsg = displayShipMsg;
	}
	/**
	 * 
	 * @return shipMsgFlag
	 */
	public boolean isShipMsgFlag() {
		return shipMsgFlag;
	}
	/**
	 * 
	 * @param shipMsgFlag
	 */
	public void setShipMsgFlag(boolean shipMsgFlag) {
		this.shipMsgFlag = shipMsgFlag;
	}

	
	public boolean isLtlAttributeApplicable() {
		return this.ltlAttributeApplicable;
	}
	public void setLtlAttributeApplicable(boolean ltlAttributeApplicable) {
		this.ltlAttributeApplicable = ltlAttributeApplicable;
	}
	public Map<String, AttributeVO> getLtlAttributesList() {
		return this.ltlAttributesList;
	}
	public void setLtlAttributesList(Map<String, AttributeVO> ltlAttributesList) {
		this.ltlAttributesList = ltlAttributesList;
	}
	private String thumbnailImagePath;
	private String freeStandardShipping;
	private String clearance;
	private boolean multiSku;
	private boolean ltlProduct;

	//sku level properties
	private String defaultSkuId;
	private String skuId;
	private boolean inStock;
	private String skuGiftWrapEligible;
	private List<AttributeVO> vdcSku;	
	private String vdcSkuFlag;
	private Map<String,String> color;
	private boolean bopusExcluded;
	private boolean emailAlertOn;
	
	private String customizationCode;
	private String personalizationType;
	private boolean customizableRequired;
	private boolean customizationOffered;
	private List<String> customizationCodeValues;
	private VendorInfoVO vendorInfoVO;

	public VendorInfoVO getVendorInfoVO() {
		return vendorInfoVO;
	}
	public void setVendorInfoVO(VendorInfoVO vendorInfoVO) {
		this.vendorInfoVO = vendorInfoVO;
	}
	/**
	 * @return collection
	 */
	public final boolean isCollection() {
		return this.collection;
	}
	/**
	 * @param collection
	 */
	public final void setCollection(final boolean collection) {
		this.collection = collection;
	}
	/**
	 * @return priceRangeDescription
	 */
	public final String getPriceRangeDescription() {
		return this.priceRangeDescription;
	}
	/**
	 * @param priceRangeDescription
	 */
	public final void setPriceRangeDescription(final String priceRangeDescription) {
		this.priceRangeDescription = priceRangeDescription;
	}
	/**
	 * @return salePriceRangeDescription
	 */
	public final String getSalePriceRangeDescription() {
		return this.salePriceRangeDescription;
	}
	/**
	 * @param salePriceRangeDescription
	 */
	public final void setSalePriceRangeDescription(final String salePriceRangeDescription) {
		this.salePriceRangeDescription = salePriceRangeDescription;
	}

	/**
	 * @return imagePath
	 */
	public final String getImagePath() {
		return this.imagePath;
	}
	/**
	 * @param imagePath
	 */
	public final void setImagePath(final String imagePath) {
		this.imagePath = imagePath;
	}
	
	/**
	 * @return productId
	 */
	public final String getProductId() {
		return this.productId;
	}
	/**
	 * @param productId
	 */
	public final void setProductId(final String productId) {
		this.productId = productId;
	}
	/**
	 * @return skuId
	 */
	public final String getSkuId() {
		return this.skuId;
	}
	/**
	 * @param skuId
	 */
	public final void setSkuId(final String skuId) {
		this.skuId = skuId;
	}
	/**
	 * @return mediumImagePath
	 */
	public final String getMediumImagePath() {
		return this.mediumImagePath;
	}
	/**
	 * @param mediumImagePath
	 */
	public final void setMediumImagePath(final String mediumImagePath) {
		this.mediumImagePath = mediumImagePath;
	}
	/**
	 * @return longDescription
	 */
	public final String getLongDescription() {
		return this.longDescription;
	}
	/**
	 * @param longDescription
	 */
	public final void setLongDescription(final String longDescription) {
		this.longDescription = longDescription;
	}
	/**
	 * @return shortDescription
	 */
	public final String getShortDescription() {
		return this.shortDescription;
	}
	/**
	 * @param shortDescription
	 */
	public final void setShortDescription(final String shortDescription) {
		this.shortDescription = shortDescription;
	}
	/**
	 * @return productName
	 */
	public final String getProductName() {
		return this.productName;
	}
	/**
	 * @param productName
	 */
	public final void setProductName(final String productName) {
		this.productName = productName;
	}

	/**
	 * @return reviews
	 */
	public final BazaarVoiceProductVO getReviews() {
		return this.reviews;
	}
	/**
	 * @param reviews
	 */
	public final void setReviews(final BazaarVoiceProductVO reviews) {
		this.reviews = reviews;
	}

	/**
	 * @return productActive
	 */
	public final boolean isProductActive() {
		return this.productActive;
	}
	/**
	 * @param productActive
	 */
	public final void setProductActive(final boolean productActive) {
		this.productActive = productActive;
	}

	/**
	 * @return inStock
	 */
	public final boolean isInStock() {
		return this.inStock;
	}
	/**
	 * @param inStock
	 */
	public final void setInStock(final boolean inStock) {
		this.inStock = inStock;
	}

	/**
	 * @return the skuGiftWrapEligible
	 */
	public final String getSkuGiftWrapEligible() {
		return this.skuGiftWrapEligible;
	}
	/**
	 * @param skuGiftWrapEligible the skuGiftWrapEligible to set
	 */
	public final void setSkuGiftWrapEligible(final String skuGiftWrapEligible) {
		this.skuGiftWrapEligible = skuGiftWrapEligible;
	}
	
	/**
	 * @return the clearance
	 */
	public final String getClearance() {
		return this.clearance;
	}
	/**
	 * @param clearance the clearance to set
	 */
	public final void setClearance(final String clearance) {
		this.clearance = clearance;
	}
	/**
	 * @return the freeStandardShipping
	 */
	public final String getFreeStandardShipping() {
		return this.freeStandardShipping;
	}
	/**
	 * @param freeStandardShipping the freeStandardShipping to set
	 */
	public final void setFreeStandardShipping(final String freeStandardShipping) {
		this.freeStandardShipping = freeStandardShipping;
	}

	/**
	 * @return bopusExcluded
	 */
	public final boolean isBopusExcluded() {
		return this.bopusExcluded;
	}
	/**
	 * @param bopusExcluded
	 */
	public final void setBopusExcluded(final boolean bopusExcluded) {
		this.bopusExcluded = bopusExcluded;
	}
	/**
	 * @return thumbnailImagePath
	 */
	public final String getThumbnailImagePath() {
		return this.thumbnailImagePath;
	}
	/**
	 * @param thumbnailImagePath
	 */
	public final void setThumbnailImagePath(final String thumbnailImagePath) {
		this.thumbnailImagePath = thumbnailImagePath;
	}
	
	/**
	 * @return the attributesList
	 */
	public Map<String, AttributeVO> getAttributesList() {
		return this.attributesList;
	}
	/**
	 * @param attributesList the attributesList to set
	 */
	public void setAttributesList(Map<String, AttributeVO> attributesList) {
		this.attributesList = attributesList;
	}
	/**
	 * @return multiSku
	 */
	public boolean isMultiSku() {
		return this.multiSku;
	}
	/**
	 * @param multiSku
	 */
	public void setMultiSku(boolean multiSku) {
		this.multiSku = multiSku;
	}
	
	/**
	 * @return ltlProduct
	 */
	public boolean isLtlProduct() {
		return this.ltlProduct;
	}
	/**
	 * @param ltlProduct
	 */
	public void setLtlProduct(boolean ltlProduct) {
		this.ltlProduct = ltlProduct;
	}
	
	public Map<String,String> getColor() {
		return color;
	}
	public void setColor(Map<String,String> color) {
		this.color = color;
	}
	public String getVdcSkuFlag() {
		return vdcSkuFlag;
	}
	public void setVdcSkuFlag(String vdcSkuFlag) {
		this.vdcSkuFlag = vdcSkuFlag;
	}
	public List<AttributeVO> getVdcSku() {
		return vdcSku;
	}
	public void setVdcSku(List<AttributeVO> vdcSku) {
		this.vdcSku = vdcSku;
	}
	public String getDefaultSkuId() {
		return defaultSkuId;
	}
	public void setDefaultSkuId(String defaultSkuId) {
		this.defaultSkuId = defaultSkuId;
	}
	public boolean isEmailAlertOn() {
		return emailAlertOn;
	}
	public void setEmailAlertOn(boolean emailAlertOn) {
		this.emailAlertOn = emailAlertOn;
	}
	public boolean isIntlRestricted() {
		return intlRestricted;
	}
	public void setIntlRestricted(boolean intlRestricted) {
		this.intlRestricted = intlRestricted;
	}
	public String getCustomizationCode() {
		return customizationCode;
	}
	public void setCustomizationCode(String customizationCode) {
		this.customizationCode = customizationCode;
	}
	public boolean isCustomizableRequired() {
		return customizableRequired;
	}
	public void setCustomizableRequired(boolean customizableRequired) {
		this.customizableRequired = customizableRequired;
	}
	public List<String> getCustomizationCodeValues() {
		return customizationCodeValues;
	}
	public void setCustomizationCodeValues(List<String> customizationCodeValues) {
		this.customizationCodeValues = customizationCodeValues;
	}
	/**
	 * @return the customizationOffered
	 */
	public boolean isCustomizationOffered() {
		return this.customizationOffered;
	}
	/**
	 * @param customizationOffered the customizationOffered to set
	 */
	public void setCustomizationOffered(boolean customizationOffered) {
		this.customizationOffered = customizationOffered;
	}
	/**
	 * @return the personalizationType
	 * hh
	 */
	public String getPersonalizationType() {
		return personalizationType;
	}
	/**
	 * @param personalizationType the personalizationType to set
	 */
	public void setPersonalizationType(String personalizationType) {
		this.personalizationType = personalizationType;
	}
	/**
	 * @return the priceLabelCode
	 */
	public final String getPriceLabelCode() {
		return priceLabelCode;
	}
	/**
	 * @param priceLabelCode the priceLabelCode to set
	 */
	public final void setPriceLabelCode(String priceLabelCode) {
		this.priceLabelCode = priceLabelCode;
	}
	/**
	 * @return the inCartFlag
	 */
	public final boolean isInCartFlag() {
		return inCartFlag;
	}
	/**
	 * @param inCartFlag the inCartFlag to set
	 */
	public final void setInCartFlag(boolean inCartFlag) {
		this.inCartFlag = inCartFlag;
	}
	/**
	 * @return the dynamicPricingProduct
	 */
	public final boolean isDynamicPricingProduct() {
		return dynamicPricingProduct;
	}
	/**
	 * @param dynamicPricingProduct the dynamicPricingProduct to set
	 */
	public final void setDynamicPricingProduct(boolean dynamicPricingProduct) {
		this.dynamicPricingProduct = dynamicPricingProduct;
	}
	
}