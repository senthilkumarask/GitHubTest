package com.bbb.commerce.exim.bean;

import java.util.List;

import com.bbb.utils.BBBUtility;


/**
 * @author sanam jain
 * The Class EximCustomizedAttributesVO.
 */
public class EximCustomizedAttributesVO {
	 
	/** The refnum. */
	private String refnum;
    
    /** The retailer name. */
    private String retailerName;
    
    /** The retailer sku. */
    private String retailerSku;
    
    /** The vendor name. */
    private String vendorName;
    
    /** The vendor sku. */
    private String vendorSku;
    
    /** The customization service. */
    private String customizationService;
    
    /** The customization status. */
    private String customizationStatus;
    
    /** The moderation status. */
    private String moderationStatus;
    
    /** The metadata status. */
    private String metadataStatus;
    
    /** The created at. */
    private String createdAt;
    
    /** The updated at. */
    private String updatedAt;
    
    /** The locked at. */
    private String lockedAt;
    
    /** The released at. */
    private String releasedAt;
    
    /** The namedrop. */
    private String namedrop;
    
    /** The description. */
    private String description;
    
    /** The cost price base. */
    private String costPriceBase;
    
    /** The cost price adder. */
    private String costPriceAdder;
    
    /** The retail price base. */
    private String retailPriceBase;
    
    /** The retail price adder. */
    private String retailPriceAdder;
    
    /** The moderation url. */
    private String moderationUrl;
	
	/** The metadata url. */
	private String metadataUrl;
	
	/** The images. */
	private List<EximImagesVO> images;
	
	/** The errors. */
	private List<ErrorVO> errors;
	
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
	        final StringBuilder builder = new StringBuilder();
	        builder.append("EximCustomizedAttributesVO [referenceNumber=").append(this.refnum)
	                        .append(", retailerName=").append(this.retailerName).append(", retailerSku=")
	                        .append(this.retailerSku).append(", vendorName=").append(this.vendorName)
	                        .append(", vendorSku=").append(this.vendorSku).append(", customizationService=")
	                        .append(this.customizationService).append(", customizationStatus=").append(this.customizationStatus)
	                        .append(", moderationStatus=").append(this.moderationStatus)
	                        .append(", metadataStatus=").append(this.metadataStatus).append(", createdAt=")
	                        .append(this.createdAt).append(", updatedAt=").append(this.updatedAt)
	                        .append(", lockedAt=").append(this.lockedAt).append(", releasedAt=")
	                        .append(this.releasedAt).append(", namedrop=").append(this.namedrop)
	                        .append(", description=").append(this.description)
	                        .append(", costPriceBase=").append(this.costPriceBase)
	                        .append(", costPriceAdder=").append(this.costPriceAdder)
	                        .append(", retailPriceBase=").append(this.retailPriceBase)
	                        .append(", retailPriceAdder=").append(this.retailPriceAdder)
	                        .append(", moderationUrl=").append(this.moderationUrl)
	                        .append(", metadataUrl=").append(this.metadataUrl)
							.append(", images=").append(this.getImages().toString())
							.append(", errors=").append(this.getErrors().toString()).append("]");
							
	        return builder.toString();
	    }
	
	/**
	 * Gets the refnum.
	 *
	 * @return the refnum
	 */
	public String getRefnum() {
		return refnum;
	}
	
	/**
	 * Sets the refnum.
	 *
	 * @param refnum the new refnum
	 */
	public void setRefnum(String refnum) {
		this.refnum = refnum;
	}
	
	/**
	 * Gets the retailer name.
	 *
	 * @return the retailer name
	 */
	public String getRetailerName() {
		return retailerName;
	}
	
	/**
	 * Sets the retailer name.
	 *
	 * @param retailerName the new retailer name
	 */
	public void setRetailerName(String retailerName) {
		this.retailerName = retailerName;
	}
	
	/**
	 * Gets the retailer sku.
	 *
	 * @return the retailer sku
	 */
	public String getRetailerSku() {
		return retailerSku;
	}
	
	/**
	 * Sets the retailer sku.
	 *
	 * @param retailerSku the new retailer sku
	 */
	public void setRetailerSku(String retailerSku) {
		this.retailerSku = retailerSku;
	}
	
	/**
	 * Gets the vendor name.
	 *
	 * @return the vendor name
	 */
	public String getVendorName() {
		return vendorName;
	}
	
	/**
	 * Sets the vendor name.
	 *
	 * @param vendorName the new vendor name
	 */
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	
	/**
	 * Gets the vendor sku.
	 *
	 * @return the vendor sku
	 */
	public String getVendorSku() {
		return vendorSku;
	}
	
	/**
	 * Sets the vendor sku.
	 *
	 * @param vendorSku the new vendor sku
	 */
	public void setVendorSku(String vendorSku) {
		this.vendorSku = vendorSku;
	}
	
	/**
	 * Gets the customization service.
	 *
	 * @return the customization service
	 */
	public String getCustomizationService() {
		return customizationService;
	}
	
	/**
	 * Sets the customization service.
	 *
	 * @param customizationService the new customization service
	 */
	public void setCustomizationService(String customizationService) {
		this.customizationService = customizationService;
	}
	
	/**
	 * Gets the customization status.
	 *
	 * @return the customization status
	 */
	public String getCustomizationStatus() {
		return customizationStatus;
	}
	
	/**
	 * Sets the customization status.
	 *
	 * @param customizationStatus the new customization status
	 */
	public void setCustomizationStatus(String customizationStatus) {
		this.customizationStatus = customizationStatus;
	}
	
	/**
	 * Gets the moderation status.
	 *
	 * @return the moderation status
	 */
	public String getModerationStatus() {
		return moderationStatus;
	}
	
	/**
	 * Sets the moderation status.
	 *
	 * @param moderationStatus the new moderation status
	 */
	public void setModerationStatus(String moderationStatus) {
		this.moderationStatus = moderationStatus;
	}
	
	/**
	 * Gets the metadata status.
	 *
	 * @return the metadata status
	 */
	public String getMetadataStatus() {
		return metadataStatus;
	}
	
	/**
	 * Sets the metadata status.
	 *
	 * @param metadataStatus the new metadata status
	 */
	public void setMetadataStatus(String metadataStatus) {
		this.metadataStatus = metadataStatus;
	}
	
	/**
	 * Gets the created at.
	 *
	 * @return the created at
	 */
	public String getCreatedAt() {
		return createdAt;
	}
	
	/**
	 * Sets the created at.
	 *
	 * @param createdAt the new created at
	 */
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	
	/**
	 * Gets the updated at.
	 *
	 * @return the updated at
	 */
	public String getUpdatedAt() {
		return updatedAt;
	}
	
	/**
	 * Sets the updated at.
	 *
	 * @param updatedAt the new updated at
	 */
	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	/**
	 * Gets the locked at.
	 *
	 * @return the locked at
	 */
	public String getLockedAt() {
		return lockedAt;
	}
	
	/**
	 * Sets the locked at.
	 *
	 * @param lockedAt the new locked at
	 */
	public void setLockedAt(String lockedAt) {
		this.lockedAt = lockedAt;
	}
	
	/**
	 * Gets the released at.
	 *
	 * @return the released at
	 */
	public String getReleasedAt() {
		return releasedAt;
	}
	
	/**
	 * Sets the released at.
	 *
	 * @param releasedAt the new released at
	 */
	public void setReleasedAt(String releasedAt) {
		this.releasedAt = releasedAt;
	}
	
	/**
	 * Gets the namedrop.
	 *
	 * @return the namedrop
	 */
	public String getNamedrop() {
		return namedrop;
	}
	
	/**
	 * Sets the namedrop.
	 *
	 * @param namedrop the new namedrop
	 */
	public void setNamedrop(String namedrop) {
		//BBBSL-8736
		this.namedrop = namedrop.replace("\n","<br>");
	}
	
	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Gets the cost price base.
	 *
	 * @return the cost price base
	 */
	public double getCostPriceBase() {
		if(BBBUtility.isEmpty(costPriceBase)){
			return 0.0;
		}
		return Double.parseDouble(costPriceBase);
	}
	
	/**
	 * Sets the cost price base.
	 *
	 * @param costPriceBase the new cost price base
	 */
	public void setCostPriceBase(String costPriceBase) {
		this.costPriceBase = costPriceBase;
	}
	
	/**
	 * Gets the cost price adder.
	 *
	 * @return the cost price adder
	 */
	public double getCostPriceAdder() {
		if(BBBUtility.isEmpty(costPriceAdder)){
			return 0.0;
		}
		return Double.parseDouble(costPriceAdder);
	}
	
	/**
	 * Sets the cost price adder.
	 *
	 * @param costPriceAdder the new cost price adder
	 */
	public void setCostPriceAdder(String costPriceAdder) {
		this.costPriceAdder = costPriceAdder;
	}
	
	/**
	 * Gets the retail price base.
	 *
	 * @return the retail price base
	 */
	public double getRetailPriceBase() {
		if(BBBUtility.isEmpty(retailPriceBase)){
			return 0.0;
		}
		return Double.parseDouble(retailPriceBase);
	}
	
	/**
	 * Sets the retail price base.
	 *
	 * @param retailPriceBase the new retail price base
	 */
	public void setRetailPriceBase(String retailPriceBase) {
		this.retailPriceBase = retailPriceBase;
	}
	
	/**
	 * Gets the retail price adder.
	 *
	 * @return the retail price adder
	 */
	public double getRetailPriceAdder() {
		if(BBBUtility.isEmpty(retailPriceAdder)){
			return 0.0;
		}
		return Double.parseDouble(retailPriceAdder);
	}
	
	/**
	 * Sets the retail price adder.
	 *
	 * @param retailPriceAdder the new retail price adder
	 */
	public void setRetailPriceAdder(String retailPriceAdder) {
		this.retailPriceAdder = retailPriceAdder;
	}
	
	/**
	 * Gets the moderation url.
	 *
	 * @return the moderation url
	 */
	public String getModerationUrl() {
		return moderationUrl;
	}
	
	/**
	 * Sets the moderation url.
	 *
	 * @param moderationUrl the new moderation url
	 */
	public void setModerationUrl(String moderationUrl) {
		this.moderationUrl = moderationUrl;
	}
	
	/**
	 * Gets the metadata url.
	 *
	 * @return the metadata url
	 */
	public String getMetadataUrl() {
		return metadataUrl;
	}
	
	/**
	 * Sets the metadata url.
	 *
	 * @param metadataUrl the new metadata url
	 */
	public void setMetadataUrl(String metadataUrl) {
		this.metadataUrl = metadataUrl;
	}
	
	/**
	 * Gets the errors.
	 *
	 * @return the errors
	 */
	public List<ErrorVO> getErrors() {
		return errors;
	}
	
	/**
	 * Sets the errors.
	 *
	 * @param errors the new errors
	 */
	public void setErrors(List<ErrorVO> errors) {
		this.errors = errors;
	}
	
	/**
	 * Gets the images.
	 *
	 * @return the images
	 */
	public List<EximImagesVO> getImages() {
		return images;
	}
	
	/**
	 * Sets the images.
	 *
	 * @param images the new images
	 */
	public void setImages(List<EximImagesVO> images) {
		this.images = images;
	}
	
}
