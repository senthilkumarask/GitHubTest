package com.bbb.commerce.giftregistry.bean;

import java.io.Serializable;


// TODO: Auto-generated Javadoc
/**
 * The bean is holding properties for the add to item in gift registry.
 *
 * @author sku134
 */
public class AddItemsBean implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The product id. */
	private String productId;
	
	/** The price. */
	private String price;
	
	/** The sku. */
	private String sku;
	
	/** The quantity. */
	private String quantity;
	
	/** The registry id. */
	private String registryId;
	
	//R2.1 Change
	/** The registry id. */
	private String mParamRegistryId;
	
	private String customizationRequired;
	
	private String personalizationCode;
	
	private String refNum;
	
	private String ltlFlag;
	
	public String getLtlFlag() {
		return ltlFlag;
	}

	public void setLtlFlag(String ltlFlag) {
		this.ltlFlag = ltlFlag;
	}
	/** The ltlDeliveryServices. */
	private String ltlDeliveryServices;
	/** The ltlDeliveryPrices. */
	private String ltlDeliveryPrices;
	/**
	 * @return the ltlDeliveryServices
	 */
	public String getLtlDeliveryServices() {
		return ltlDeliveryServices;
	}

	/**
	 * @param ltlDeliveryServices the ltlDeliveryServices to set
	 */
	public void setLtlDeliveryServices(String ltlDeliveryServices) {
		this.ltlDeliveryServices = ltlDeliveryServices;
	}
	/**
	 * @return the ltlDeliveryPrices
	 */
	public String getLtlDeliveryPrices() {
		return ltlDeliveryPrices;
	}

	/**
	 * @param ltlDeliveryPrices the ltlDeliveryPrices to set
	 */
	public void setLtlDeliveryPrices(String ltlDeliveryPrices) {
		this.ltlDeliveryPrices = ltlDeliveryPrices;
	}

	public String getRefNum() {
		return refNum;
	}

	public void setRefNum(String refNum) {
		this.refNum = refNum;
	}

	public String getPersonalizationCode() {
		return personalizationCode;
	}

	public void setPersonalizationCode(String personalizationCode) {
		this.personalizationCode = personalizationCode;
	}

	public String getCustomizationRequired() {
		return customizationRequired;
	}

	public void setCustomizationRequired(String customizationRequired) {
		this.customizationRequired = customizationRequired;
	}

	/**
	 * @return the paramRegistryId
	 */
	public String getParamRegistryId() {
		return mParamRegistryId;
	}

	/**
	 * @param pParamRegistryId the paramRegistryId to set
	 */
	public void setParamRegistryId(String pParamRegistryId) {
		mParamRegistryId = pParamRegistryId;
	}

	/**
	 * Gets the product id.
	 *
	 * @return the productId
	 */
	public String getProductId() {
		return productId;
	}
	
	/**
	 * Sets the product id.
	 *
	 * @param pProductId the productId to set
	 */
	public void setProductId(String pProductId) {
		productId = pProductId;
	}
	
	/**
	 * Gets the sku.
	 *
	 * @return the sku
	 */
	public String getSku() {
		return sku;
	}
	
	/**
	 * Sets the sku.
	 *
	 * @param pSku the sku to set
	 */
	public void setSku(String pSku) {
		sku = pSku;
	}
	
	/**
	 * Gets the quantity.
	 *
	 * @return the quantity
	 */
	public String getQuantity() {
		return quantity;
	}
	
	/**
	 * Sets the quantity.
	 *
	 * @param pQuantity the quantity to set
	 */
	public void setQuantity(String pQuantity) {
		quantity = pQuantity;
	}
	
	/**
	 * Gets the registry id.
	 *
	 * @return the registryId
	 */
	public String getRegistryId() {
		return registryId;
	}
	
	/**
	 * Sets the registry id.
	 *
	 * @param pRegistryId the registryId to set
	 */
	public void setRegistryId(String pRegistryId) {
		registryId = pRegistryId;
	}
	
	/**
	 * Gets the price.
	 *
	 * @return the price
	 */
	public String getPrice() {
		return price;
	}
	
	/**
	 * Sets the price.
	 *
	 * @param price the new price
	 */
	public void setPrice(String price) {
		this.price = price;
	}
	
	
	
}
