package com.bbb.commerce.catalog.vo;

import java.io.Serializable;

import com.bbb.commerce.catalog.BBBCatalogConstants;

import atg.repository.RepositoryItem;

public class ShipMethodVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String shipMethodId;
	private String shipMethodDescription;
	private String daysToShip;
	private RepositoryItem shippingRepositoryItem;
	private volatile int hashCode = 0;
	private double shippingCharge;
	private double sortShippingCharge;
	private String estdShipDurationInDaysLowerLimit;
	private String estdShipDurationInDaysHigherLimit;
	
	private String discountedShippingForStandard;
	private double assemblyFees;
	private double deliverySurcharge;
	private String savedInWishlist;
	

	/**
	 * @return the assemblyFees
	 */
	public double getAssemblyFees() {
		return assemblyFees;
	}

	/**
	 * @param assemblyFees
	 */
	public void setAssemblyFees(double assemblyFees) {
		this.assemblyFees = assemblyFees;
	}

	
	private boolean eligibleShipMethod;	

	/**
	 * @return the eligibleShipMethod
	 */
	public boolean isEligibleShipMethod() {
		return eligibleShipMethod;
	}

	/**
	 * @param eligibleShipMethod
	 */
	public void setEligibleShipMethod(boolean eligibleShipMethod) {
		this.eligibleShipMethod = eligibleShipMethod;
	}


	private boolean ltlShipMethod;
		


	/**
	 * @return the ltlShipMethod
	 */
	public boolean isLtlShipMethod() {
		
		if(shippingRepositoryItem!=null && this.shippingRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SHIPPING_METHOD)!=null){
			return (Boolean) this.shippingRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SHIPPING_METHOD);
			}
			else{
				return this.ltlShipMethod;
			}
		
	}

	/**
	 * @param ltlShipMethod
	 */
	public void setLtlShipMethod(boolean ltlShipMethod) {
		this.ltlShipMethod = ltlShipMethod;
	}

	
	
	/**
	 * @return the deliverySurcharge
	 */
	public double getDeliverySurcharge() {
		return deliverySurcharge;
	}

	/**
	 * @param deliverySurcharge
	 */
	public void setDeliverySurcharge(double deliverySurcharge) {
		this.deliverySurcharge = deliverySurcharge;
	}
	/**
	 * @return the discountedShippingForStandard
	 */
	public String getDiscountedShippingForStandard() {
		return discountedShippingForStandard;
	}

	/**
	 * @param discountedShippingForStandard the discountedShippingForStandard to set
	 */
	public void setDiscountedShippingForStandard(
			String discountedShippingForStandard) {
		this.discountedShippingForStandard = discountedShippingForStandard;
	}
	public ShipMethodVO() {
		// TODO Auto-generated constructor stub
	}
	public ShipMethodVO(RepositoryItem shippingRepositoryItem){

		if(shippingRepositoryItem!=null){
			this.shippingRepositoryItem=shippingRepositoryItem;
		}
	}


	/**
	 * @return the shipMethodId
	 */
	public String getShipMethodId() {
		if(shippingRepositoryItem!=null){
		return this.shippingRepositoryItem.getRepositoryId();
		}
		else{
			return this.shipMethodId;
		}
	}

	/**
	 * @param shipMethodId the shipMethodId to set
	 */
	public void setShipMethodId(String shipMethodId) {
		this.shipMethodId = shipMethodId;
	}

	/**
	 * @return the shipMethodDescription
	 */
	public String getShipMethodDescription() {
		if(shippingRepositoryItem!=null && this.shippingRepositoryItem.getPropertyValue(BBBCatalogConstants.SHIPPING_METHOD_DESCRIPTION_SHIPPING_PROPERTY_NAME)!=null){
			return (String) this.shippingRepositoryItem.getPropertyValue(BBBCatalogConstants.SHIPPING_METHOD_DESCRIPTION_SHIPPING_PROPERTY_NAME);
			}
			else{
				return this.shipMethodDescription;
			}
	}

	/**
	 * @param shipMethodDescription the shipMethodDescription to set
	 */
	public void setShipMethodDescription(String shipMethodDescription) {
		this.shipMethodDescription = shipMethodDescription;
	}
	/**
	 * @return the daysToShip
	 */
	public String getDaysToShip() {
		return this.daysToShip;
	}
	/**
	 * @param daysToShip the daysToShip to set
	 */
	public void setDaysToShip(String daysToShip) {
		this.daysToShip = daysToShip;
	}

	public double getSortShippingCharge() {
		return sortShippingCharge;
	}

	public void setSortShippingCharge(double sortShippingCharge) {
		this.sortShippingCharge = sortShippingCharge;
	}

	
	/**
	 * Getter for Shipping Charge.
	 * 
	 * @return shippingCharge
	 */
	public double getShippingCharge() {
		return shippingCharge;
	}
	
	/**
	 * Setter for Shipping Charge.
	 * 
	 * @param shippingCharge
	 * 			shippingCharge value.
	 */
	public void setShippingCharge(double shippingCharge) {
		this.shippingCharge = shippingCharge;
	}
	
	/**
	 * Getter for estdShipDurationInDaysLowerLimit.
	 * 
	 * @return lower limit of Ship Duration.
	 */
	public String getEstdShipDurationInDaysLowerLimit() {
		return estdShipDurationInDaysLowerLimit;
	}
	
	/**
	 * Setter for estdShipDurationInDaysLowerLimit.
	 * 
	 * @param estdShipDurationInDaysLowerLimit
	 * 			lower limit of Ship Duration.
	 */
	public void setEstdShipDurationInDaysLowerLimit(
			String estdShipDurationInDaysLowerLimit) {
		this.estdShipDurationInDaysLowerLimit = estdShipDurationInDaysLowerLimit;
	}
	
	/**
	 * Getter for estdShipDurationInDaysHigherLimit.
	 * 
	 * @return upper limit of Ship Duration.
	 */
	public String getEstdShipDurationInDaysHigherLimit() {
		return estdShipDurationInDaysHigherLimit;
	}
	
	/**
	 * Setter for estdShipDurationInDaysHigherLimit.
	 * 
	 * @param estdShipDurationInDaysHigherLimit
	 * 				upper limit of Ship Duration.
	 */
	public void setEstdShipDurationInDaysHigherLimit(
			String estdShipDurationInDaysHigherLimit) {
		this.estdShipDurationInDaysHigherLimit = estdShipDurationInDaysHigherLimit;
	}
	
	
	@Override
	public boolean equals(Object obj) {
	    if(this == obj) {
            return true;
       }
	    if(obj instanceof ShipMethodVO) {
	     return this.getShipMethodId().equals(((ShipMethodVO) obj).getShipMethodId());   
	    } else {
	        return false;
	    }
	}
	
	@Override
	public int hashCode() {
	    final int multiplier = 23;
        if (hashCode == 0) {
            int code = 133;            
            code = multiplier * code + getShipMethodId().hashCode();
            hashCode = code;
        }
        return hashCode;
	}

	public String getSavedInWishlist() {
		return savedInWishlist;
	}

	public void setSavedInWishlist(String savedInWishlist) {
		this.savedInWishlist = savedInWishlist;
	}

	@Override
	public String toString() {
		return "ShipMethodVO [shipMethodId=" + shipMethodId
				+ ", shipMethodDescription=" + shipMethodDescription
				+ ", daysToShip=" + daysToShip + ", shippingRepositoryItem="
				+ shippingRepositoryItem + ", hashCode=" + hashCode
				+ ", shippingCharge=" + shippingCharge
				+ ", estdShipDurationInDaysLowerLimit="
				+ estdShipDurationInDaysLowerLimit
				+ ", estdShipDurationInDaysHigherLimit="
				+ estdShipDurationInDaysHigherLimit
				+ ", discountedShippingForStandard="
				+ discountedShippingForStandard + ", assemblyFees="
				+ assemblyFees + ", deliverySurcharge=" + deliverySurcharge
				+ ", savedInWishlist=" + savedInWishlist
				+ ", eligibleShipMethod=" + eligibleShipMethod
				+ ", ltlShipMethod=" + ltlShipMethod + "]";
	}

}
