package com.bbb.commerce.catalog.vo;

import java.io.Serializable;

import atg.repository.RepositoryItem;

import com.bbb.commerce.catalog.BBBCatalogConstants;
/**
 * 
 * @author njai13
 *
 */
public class SKUVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String skuId;
	private String displayName;
	private String description;
	private String longDescription;
	private boolean giftWrapEligible;
	private boolean vdcSku;
	private String color;
	private String size;
	private String taxStatus;
	private ImageVO skuImages;
	private boolean skuInStock;
	private String vdcSKUShipMesage;
	private double shippingSurcharge;
	private boolean skuBelowLine;
	private boolean skuSoldOnline;
	private boolean skuGiftCard;
	private RepositoryItem skuRepositoryItem;
	private boolean bopusAllowed;
	private String ecomFulfillment;
	private boolean mIsEcoFeeEligible;
	private String upc;
	private boolean emailStockAlertsEnabled=true;
	private boolean activeFlag;
	private boolean webOfferedFlag;
	private boolean disableFlag;
	private boolean storeSKU;

	// added for bopus and flagoff item case
	private boolean bopusExcludedForMinimalSku;

	private String orderToShipSla;
	private String assemblyTime;

	public boolean isBopusExcludedForMinimalSku() {
		return bopusExcludedForMinimalSku;
	}
	public void setBopusExcludedForMinimalSku(boolean bopusExcludedForMinimalSku) {
		this.bopusExcludedForMinimalSku = bopusExcludedForMinimalSku;
	}
	
	/**
	 * @return the emailStockAlertsEnabled
	 */
	public boolean getEmailStockAlertsEnabled() {
		
		if(this.skuRepositoryItem!=null && skuRepositoryItem.getPropertyValue(BBBCatalogConstants.EMAIL_OUT_OF_STOCK_PRODUCT_PROPERTY_NAME)!=null){
			return (Boolean) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.EMAIL_OUT_OF_STOCK_PRODUCT_PROPERTY_NAME);
		}else{
			return this.emailStockAlertsEnabled;
		}
	}
	/**
	 * @param emailStockAlertsEnabled the emailStockAlertsEnabled to set
	 */
	public void setEmailStockAlertsEnabled(boolean emailStockAlertsEnabled) {
		this.emailStockAlertsEnabled = emailStockAlertsEnabled;
	}
	/**
	 * @return the vdcSku
	 */
	public boolean isVdcSku() {
		if(this.skuRepositoryItem!=null && skuRepositoryItem.getPropertyValue(BBBCatalogConstants.VDCSKU_TYPE_SKU_PROPERTY_NAME) != null ){
			this.vdcSku = true;
		}
		else{
			this.vdcSku = false;
		}
		return vdcSku;
	}
	
	/**
	 * @return the activeFlag
	 */
	public boolean isActiveFlag() {
		return activeFlag;
	}
	/**
	 * @param activeFlag the activeFlag to set
	 */
	public void setActiveFlag(boolean activeFlag) {
		this.activeFlag = activeFlag;
	}
	/**
	 * @return the webOfferedFlag
	 */
	public boolean isWebOfferedFlag() {
		return webOfferedFlag;
	}
	/**
	 * @param webOfferedFlag the webOfferedFlag to set
	 */
	public void setWebOfferedFlag(boolean webOfferedFlag) {
		this.webOfferedFlag = webOfferedFlag;
	}
	/**
	 * @return the disableFlag
	 */
	public boolean isDisableFlag() {
		return disableFlag;
	}
	/**
	 * @param disableFlag the disableFlag to set
	 */
	public void setDisableFlag(boolean disableFlag) {
		this.disableFlag = disableFlag;
	}
	/**
	 * @param vdcSku the vdcSku to set
	 */
	public void setVdcSku(boolean vdcSku) {
		this.vdcSku = vdcSku;
	}
	/**
	 * @return the skuInStock
	 */
	public boolean isSkuInStock() {
		return skuInStock;
	}
	/**
	 * @param skuInStock the skuInStock to set
	 */
	public void setSkuInStock(boolean skuInStock) {
		this.skuInStock = skuInStock;
	}
	/**
	 * @return the skuSoldOnline
	 */
	public boolean isSkuSoldOnline() {
		return skuSoldOnline;
	}
	/**
	 * @param skuSoldOnline the skuSoldOnline to set
	 */
	public void setSkuSoldOnline(boolean skuSoldOnline) {
		this.skuSoldOnline = skuSoldOnline;
	}
	/**
	 * @return the skuGiftCard
	 */
	public boolean isSkuGiftCard() {
		if(this.skuRepositoryItem!=null && skuRepositoryItem.getPropertyValue("giftCert")!=null){
			return (Boolean) skuRepositoryItem.getPropertyValue("giftCert");
		}
		else{
			return this.skuGiftCard;
		}
	}
	/**
	 * @param skuGiftCard the skuGiftCard to set
	 */
	public void setSkuGiftCard(boolean skuGiftCard) {
		this.skuGiftCard = skuGiftCard;
	}
	public SKUVO() {
		// TODO Auto-generated constructor stub
	}
	public SKUVO(RepositoryItem skuRepositoryItem) {
		this.skuRepositoryItem=skuRepositoryItem;
	}


	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		if(this.skuRepositoryItem!=null && skuRepositoryItem.getPropertyValue("displayName")!=null){
			return (String) skuRepositoryItem.getPropertyValue("displayName");
		}
		else{
			return this.displayName;
		}
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the skuId
	 */
	public String getSkuId() {
		if(this.skuRepositoryItem!=null){
			return (String) skuRepositoryItem.getRepositoryId();
		}
		else{
			return this.skuId;
		}

	}
	/**
	 * @param skuId the skuId to set
	 */
	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		if(this.skuRepositoryItem!=null && skuRepositoryItem.getPropertyValue("description")!=null){
			return (String) skuRepositoryItem.getPropertyValue("description");
		}
		else{
			return this.description;
		}
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the longDescription
	 */
	public String getLongDescription() {
		if(this.skuRepositoryItem!=null && skuRepositoryItem.getPropertyValue(BBBCatalogConstants.LONG_DESCRIPTION_SKU_PROPERTY_NAME)!=null){
			return (String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.LONG_DESCRIPTION_SKU_PROPERTY_NAME);
		}
		else{
			return this.longDescription;
		}
	}

	/**
	 * @return the upc
	 */
	public String getUpc() {
		if(this.skuRepositoryItem!=null && skuRepositoryItem.getPropertyValue(BBBCatalogConstants.UPC_SKU_PROPERTY_NAME)!=null){
			return (String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.UPC_SKU_PROPERTY_NAME);
		}
		else{
			return upc;
		}
	}
	/**
	 * @param upc the upc to set
	 */
	public void setUpc(String upc) {
		this.upc = upc;
	}
	/**
	 * @param longDescription the longDescription to set
	 */
	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	/**
	 * @return the giftWrapEligible
	 */
	public boolean getGiftWrapEligible() {
		if(this.skuRepositoryItem!=null && skuRepositoryItem.getPropertyValue(BBBCatalogConstants.GIFT_WRAP_ELIGIBLE_SKU_PROPERTY_NAME)!=null){
			return (Boolean) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.GIFT_WRAP_ELIGIBLE_SKU_PROPERTY_NAME);
		}
		else{
			return this.giftWrapEligible;
		}
	}

	/**
	 * @param giftWrapEligible the giftWrapEligible to set
	 */
	public void setGiftWrapEligible(boolean giftWrapEligible) {
		this.giftWrapEligible = giftWrapEligible;
	}



	/**
	 * @return the color
	 */
	public String getColor() {
		if(this.skuRepositoryItem!=null && skuRepositoryItem.getPropertyValue(BBBCatalogConstants.COLOR_SURCHARGE_SKU_PROPERTY_NAME)!=null){
			return (String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.COLOR_SURCHARGE_SKU_PROPERTY_NAME);
		}
		else{
			return this.color;
		}
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * @return the size
	 */
	public String getSize() {
		if(this.skuRepositoryItem!=null && skuRepositoryItem.getPropertyValue(BBBCatalogConstants.SIZE_SKU_PROPERTY_NAME)!=null ){
			return (String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.SIZE_SKU_PROPERTY_NAME);
		}
		else{
			return this.size;
		}
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(String size) {
		this.size = size;
	}

	/**
	 * @return the taxStatus
	 */
	public String getTaxStatus() {
		if(this.skuRepositoryItem!=null){
			return (String) skuRepositoryItem.getPropertyValue("taxStatus");
		}
		else{
			return this.taxStatus;
		}
	}

	/**
	 * @param taxStatus the taxStatus to set
	 */
	public void setTaxStatus(String taxStatus) {
		this.taxStatus = taxStatus;
	}

	/**
	 * @return the skuImages
	 */
	public ImageVO getSkuImages() {
		if(skuRepositoryItem!=null)
		{
			ImageVO skuImages=new ImageVO();
			if(skuRepositoryItem.getPropertyValue(BBBCatalogConstants.LARGE_IMAGE_IMAGE_PROPERTY_NAME)!=null){
				final String largeImagePath= (String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.LARGE_IMAGE_IMAGE_PROPERTY_NAME);
				skuImages.setLargeImage(largeImagePath);
			}
			if(skuRepositoryItem.getPropertyValue(BBBCatalogConstants.SMALL_IMAGE_IMAGE_PROPERTY_NAME)!=null){
				final String smallImagePath= (String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.SMALL_IMAGE_IMAGE_PROPERTY_NAME);
				skuImages.setSmallImage(smallImagePath);
			}
			if(skuRepositoryItem.getPropertyValue(BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME)!=null){
				final String swatchImagePath= (String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME);
				skuImages.setSwatchImage(swatchImagePath);
			}
			if(skuRepositoryItem.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME)!=null){
				final String mediumImagePath= (String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME);
				skuImages.setMediumImage(mediumImagePath);
			}
			if(skuRepositoryItem.getPropertyValue(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME)!=null){
				final String thumbnailImagePath= (String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME);
				skuImages.setThumbnailImage(thumbnailImagePath);
			}
			if(skuRepositoryItem.getPropertyValue(BBBCatalogConstants.ZOOM_INDEX_PATH_PRODUCT_PROPERTY_NAME)!=null)
			{

				skuImages.setZoomImageIndex((Integer) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.ZOOM_INDEX_PATH_PRODUCT_PROPERTY_NAME));
			}
			if(skuRepositoryItem.getPropertyValue(BBBCatalogConstants.ZOOM_IMAGE_IMAGE_PROPERTY_NAME)!=null)
			{
				final String zoomImagePath= (String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.ZOOM_IMAGE_IMAGE_PROPERTY_NAME);
				skuImages.setZoomImage(zoomImagePath);
			}
			if(skuRepositoryItem.getPropertyValue(BBBCatalogConstants.ANYWHERE_ZOOM_PRODUCT_PROPERTY_NAME)!=null)
			{
				skuImages.setAnywhereZoomAvailable((Boolean) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.ANYWHERE_ZOOM_PRODUCT_PROPERTY_NAME));
			}
			return skuImages;
		}

		else{
			return this.skuImages;
		}
	}

	/**
	 * @param skuImages the skuImages to set
	 */
	public void setSkuImages(ImageVO skuImages) {
		this.skuImages = skuImages;
	}



	/**
	 * @return the vdcSKUShipMesage
	 */
	public String getVdcSKUShipMesage() {
		if(this.skuRepositoryItem!=null && skuRepositoryItem.getPropertyValue(BBBCatalogConstants.VDC_SKU_MESSAGE_SKU_PROPERTY_NAME)!=null){
			return (String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.VDC_SKU_MESSAGE_SKU_PROPERTY_NAME);
		}
		else{
			return this.vdcSKUShipMesage;
		}
	}

	/**
	 * @param vdcSKUShipMesage the vdcSKUShipMesage to set
	 */
	public void setVdcSKUShipMesage(String vdcSKUShipMesage) {
		this.vdcSKUShipMesage = vdcSKUShipMesage;
	}

	/**
	 * @return the shippingSurcharge
	 */
	public double getShippingSurcharge() {
		if(this.skuRepositoryItem!=null && skuRepositoryItem.getPropertyValue(BBBCatalogConstants.SHIPPING_SURCHARGE_SKU_PROPERTY_NAME) != null){
			return (Double) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.SHIPPING_SURCHARGE_SKU_PROPERTY_NAME);
		}
		else{
			return this.shippingSurcharge;
		}
	}

	/**
	 * @param shippingSurcharge the shippingSurcharge to set
	 */
	public void setShippingSurcharge(double shippingSurcharge) {
		this.shippingSurcharge = shippingSurcharge;
	}


	/**
	 * @return the skuBelowLine
	 */
	public boolean isSkuBelowLine() {
		return skuBelowLine;
	}
	/**
	 * @param skuBelowLine the skuBelowLine to set
	 */
	public void setSkuBelowLine(boolean skuBelowLine) {
		this.skuBelowLine = skuBelowLine;
	}
	/**
	 * @return the skuRepositoryItem
	 */
	public RepositoryItem getSkuRepositoryItem() {
		return skuRepositoryItem;
	}
	/**
	 * @param skuRepositoryItem the skuRepositoryItem to set
	 */
	public void setSkuRepositoryItem(RepositoryItem skuRepositoryItem) {
		this.skuRepositoryItem = skuRepositoryItem;
	}
	/**
	 * @return the isBopusAllowed
	 */
	public boolean isBopusAllowed() {
		if(this.skuRepositoryItem!=null && skuRepositoryItem.getPropertyValue(BBBCatalogConstants.BOPUS_EXCLUSION_SKU_PROPERTY_NAME)!=null){
			this.bopusAllowed = (Boolean) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.BOPUS_EXCLUSION_SKU_PROPERTY_NAME);
		}
		else{
			this.bopusAllowed = false;
		}
		return bopusAllowed;
	}
	/**
	 * @param isBopusAllowes the isBopusAllowes to set
	 */
	public void setBopusAllowed(boolean isBopusAllowed) {
		this.bopusAllowed = isBopusAllowed;
	}
	/**
	 * @return the ecomFulfillment
	 */
	public String getEcomFulfillment() {
		if(this.skuRepositoryItem!=null && skuRepositoryItem.getPropertyValue(BBBCatalogConstants.ECOM_FULFILLMENT_SKU_PROPERTY_NAME)!=null){
			return (String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.ECOM_FULFILLMENT_SKU_PROPERTY_NAME);
		}
		else{
			return ecomFulfillment;
		}

	}
	/**
	 * @param ecomFulfillment the ecomFulfillment to set
	 */
	public void setEcomFulfillment(String ecomFulfillment) {
		this.ecomFulfillment = ecomFulfillment;
	}


	public boolean getIsEcoFeeEligible() {
		return mIsEcoFeeEligible;
	}
	public void setIsEcoFeeEligible(boolean isEcoFeeEligible) {
		this.mIsEcoFeeEligible = isEcoFeeEligible;
	}

	public String toString(){

		if(this.skuRepositoryItem!=null){
			StringBuffer toString=new StringBuffer("Details for sku Id "+this.skuId+" \n");
			toString.append("*************Basic details *********** \n").append("Sku Name:: ").append(this.getDisplayName()).append("\n Sku Short Description :: ").append(this.description)
			.append("\n Sku Long Description ").append(this.longDescription)
			.append("\n Is sku eligible for gift wrap ").append(this.getGiftWrapEligible()).append("\n Is sku of type VDC ").append(this.isVdcSku())
			.append("\n Sku color " ).append(this.getColor()!=null ?this.getColor():" Null")
			.append("\n Sku Size " ).append(this.getSize()!=null ?this.getSize():" Null").append("\n Sku Tax status ").append(this.getTaxStatus())
			.append("\n Sku vdc SKU Ship Mesage " ).append(this.getVdcSKUShipMesage()!=null ?this.getVdcSKUShipMesage():" Null")
			.append("\n Sku shipping Surcharge " ).append(this.getShippingSurcharge())
			.append("\n is Sku Below Line " ).append(this.isSkuBelowLine())
			.append("\n Is Sku Excluded for Buy Online Pick up in store " ).append(this.isBopusAllowed())
			.append("\n  Is Sku Eco fee eligible " ).append(this.getIsEcoFeeEligible())
			.append("\n is email Stock Alerts Enabled for sku " ).append(this.getEmailStockAlertsEnabled())
			.append("\n Sku ecomFulfillment " ).append(this.getEcomFulfillment()!=null ?this.getEcomFulfillment():" Null")
			.append("\n Sku upc " ).append(this.upc!=null ?this.getUpc():" Null")
			.append("\n Is Sku sold online " ).append(this.skuSoldOnline).append("\n Is Sku a gift Card " ).append(this.isSkuGiftCard());

			
			return toString.toString();
		}
		else{
			return "";
		}
	}
	public boolean isStoreSKU() {
		return storeSKU;
	}
	public void setStoreSKU(boolean storeSKU) {
		this.storeSKU = storeSKU;
	}
	
	//Added in LTL
	/**
	 * Get the order to ship SLA
	 * @return String
	 */
	public String getOrderToShipSla() {
		if(this.skuRepositoryItem!=null && skuRepositoryItem.getPropertyValue(BBBCatalogConstants.ORDER_TO_SHIP_SLA)!=null){
			return (String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.ORDER_TO_SHIP_SLA);
		}
		else{
			return this.orderToShipSla;
		}
	
	}
	
	/**
	 * Setting the order to ship sla
	 * @param orderToShipSla
	 */
	public void setOrderToShipSla(String orderToShipSla) {
		this.orderToShipSla = orderToShipSla;
}
	
	/**
	 * Get assembly time
	 * @return string
	 */
	public String getAssemblyTime() {
		if(this.skuRepositoryItem!=null && skuRepositoryItem.getPropertyValue(BBBCatalogConstants.LTL_ITEM_ASSEMBLY_TIME)!=null){
			return (String) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.LTL_ITEM_ASSEMBLY_TIME);
		}
		else{
			return this.assemblyTime;
		}
	}
	
	/**
	 * Set Assembly time
	 * @param assemblyTime
	 */
	public void setAssemblyTime(String assemblyTime) {
		this.assemblyTime = assemblyTime;
	}
	

	
	
}
