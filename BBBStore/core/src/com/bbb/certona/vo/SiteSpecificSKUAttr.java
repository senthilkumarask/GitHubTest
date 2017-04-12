package com.bbb.certona.vo;

import java.io.Serializable;
import java.util.List;

public class SiteSpecificSKUAttr implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String displayName;
	private boolean isActiveSku=true;
	private String description;
	private String longDescription;
	private double listPrice;
	private boolean webOffered;
	private boolean disabled;
	private double salePrice;
	private double shippingSurcharge;
	private List<SkuAttributesType> skuAttributes;
	private List<RebatesDetails> skuRebates;
	private List<StatesDetails> nonShippableStates;
	private List<ShippingDetails> eligibleShipMethods;
	private List<ShippingDetails> freeShipMethods;

	
	
	/**
	 * @return the webOffered
	 */
	public boolean isWebOffered() {
		return webOffered;
	}
	/**
	 * @param webOffered the webOffered to set
	 */
	public void setWebOffered(boolean webOffered) {
		this.webOffered = webOffered;
	}
	/**
	 * @return the disabled
	 */
	public boolean isDisabled() {
		return disabled;
	}
	/**
	 * @param disabled the disabled to set
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	public String toString(){

		StringBuffer toString=new StringBuffer();
		toString.append("\n").append("Display Name ").append(displayName).append("\n")
		.append(" description ").append(description).append("\n")
		.append(" is Sku Active ").append(isActiveSku).append("::")
		.append(" List price ").append(listPrice).append("::")
		.append(" Sale Price ").append(salePrice).append("::")
		.append("shippingSurcharge").append(shippingSurcharge).append("\n");
		if(freeShipMethods!=null && !freeShipMethods.isEmpty()){
			toString.append(" \n Free Ship Methods \n");
			int i=0;
			for(ShippingDetails shipVo:freeShipMethods){
				toString.append(++i+")").append(shipVo.getShipMethodDescription()).append("::");
			}

		}
		else{
			toString.append("  No Free Ship Methods available ");
		}
		if(eligibleShipMethods!=null && !eligibleShipMethods.isEmpty()){
			toString.append( " \n Eligible Ship Methods ");
			int i=0;
			for(ShippingDetails shipVo:eligibleShipMethods){
				toString.append(++i+")").append(shipVo.getShipMethodDescription()).append("\n");
			}

		}
		else{
			toString.append(" \n No Eligible Ship Methods available  ");
		}


		if(this.skuAttributes!=null && !skuAttributes.isEmpty()){
			int i=0;
			toString.append(" \n Sku Attributes Details ").append(" :: ");
			for(SkuAttributesType skuAttributesType:skuAttributes){
		
				

				toString.append(++i+") "+ skuAttributesType.toString()).append("\n");	
			}
		}
		else{
			toString.append("No Attributes defined for product \n ");
		}

		return  toString.toString();
	}
	public List<SkuAttributesType> getSkuAttributes() {
		return skuAttributes;
	}
	public void setSkuAttributes(List<SkuAttributesType> skuAttributes) {
		this.skuAttributes = skuAttributes;
	}
	public List<RebatesDetails> getSkuRebates() {
		return skuRebates;
	}
	public void setSkuRebates(List<RebatesDetails> skuRebates) {
		this.skuRebates = skuRebates;
	}
	public List<StatesDetails> getNonShippableStates() {
		return nonShippableStates;
	}
	public void setNonShippableStates(List<StatesDetails> nonShippableStates) {
		this.nonShippableStates = nonShippableStates;
	}
	public List<ShippingDetails> getEligibleShipMethods() {
		return eligibleShipMethods;
	}
	public void setEligibleShipMethods(List<ShippingDetails> eligibleShipMethods) {
		this.eligibleShipMethods = eligibleShipMethods;
	}
	public List<ShippingDetails> getFreeShipMethods() {
		return freeShipMethods;
	}
	public void setFreeShipMethods(List<ShippingDetails> freeShipMethods) {
		this.freeShipMethods = freeShipMethods;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public boolean isActiveSku() {
		return isActiveSku;
	}
	public void setActiveSku(boolean isActiveSku) {
		this.isActiveSku = isActiveSku;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLongDescription() {
		return longDescription;
	}
	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}
	public double getListPrice() {
		return listPrice;
	}
	public void setListPrice(double listPrice) {
		this.listPrice = listPrice;
	}
	public double getSalePrice() {
		return salePrice;
	}
	public void setSalePrice(double salePrice) {
		this.salePrice = salePrice;
	}
	public double getShippingSurcharge() {
		return shippingSurcharge;
	}
	public void setShippingSurcharge(double shippingSurcharge) {
		this.shippingSurcharge = shippingSurcharge;
	}


}
