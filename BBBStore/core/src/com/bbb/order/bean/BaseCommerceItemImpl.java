package com.bbb.order.bean;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemImpl;
import atg.repository.RepositoryItem;

public class BaseCommerceItemImpl extends CommerceItemImpl implements Comparable<CommerceItem>{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private boolean isPorchService;
    /**
     * Constant for storeId.
     */
    private static final String STORE_ID = "storeId";
    private boolean incartPriceItem;
    @Override
    public int compareTo(CommerceItem other) {
		if (this == other) {
			return 0;
		}
		if (other == null || this.getId() == null || other.getId() == null
				|| this.getPriceInfo() == null || other.getPriceInfo() == null
				|| this.getQuantity() == 0 || other.getQuantity() == 0) {
			return 1;
		}
		if (this.getId().equals(other.getId())) {
			return 0;
		}
		double thisAmount = this.getPriceInfo().getListPrice();
		if (this.getPriceInfo().getSalePrice() > 0) {
			thisAmount = this.getPriceInfo().getSalePrice();
		}
		double otherAmount = other.getPriceInfo().getListPrice();
		if (other.getPriceInfo().getSalePrice() > 0) {
			otherAmount = other.getPriceInfo().getSalePrice();
		}
		if (thisAmount >= otherAmount) {
			return 1;
		} else {
			return -1;
		}

    }
    
    
    /**
     * Gets the value of freeShippingMethod.
     */
    public String getStoreId() {
        return (String) getPropertyValue(STORE_ID);
    }

    /**
     * Sets the value to freeShippingMethod.
     */
    public void setStoreId(String storeId) {
        setPropertyValue(STORE_ID, storeId);
    }

    /**
     * Gets the value of incartPriceItem.
     */
	public boolean isIncartPriceItem() {
		return incartPriceItem;
	}

	 /**
     * Sets the value to incartPriceItem.
     */
	public void setIncartPriceItem(boolean incartPriceItem) {
		this.incartPriceItem = incartPriceItem;
	}


	public RepositoryItem getPorchServiceRef(){
		RepositoryItem porchItem =  (RepositoryItem) this.getPropertyValue("serviceReferal");
		 return porchItem;
		
	}
	
 
	/**
	 * @return the isPorchService
	 */
	public boolean isPorchService() {
		RepositoryItem porchItems = getPorchServiceRef();
		if(null!=porchItems){
			return isPorchService=true;
		}
		else {
		
		return isPorchService=false;
		}
		 
	}
	
	
	
	public String getPriceEstimation(){
		RepositoryItem serviceRefItem = getPorchServiceRef();
		 if(null!=serviceRefItem){
			String priceEstimation =(String) serviceRefItem.getPropertyValue("jobPriceEstimation");
			return priceEstimation;	 
		}
		return null;
	}
	
	public String getPorchServiceTypeCode(){
		RepositoryItem serviceRefItem = getPorchServiceRef();
		 if(null!=serviceRefItem){
			String porchServiceFamilyTypeCode =(String) serviceRefItem.getPropertyValue("porchServiceFamilyTypeCode");
			return porchServiceFamilyTypeCode;	 
		}
		return null;
		
	}
	
	public String getPorchServiceType(){
		RepositoryItem serviceRefItem = getPorchServiceRef();
		 if(null!=serviceRefItem){
			String porchServiceFamilyType =(String) serviceRefItem.getPropertyValue("porchServiceFamilyType");
			return porchServiceFamilyType;	 
		}
		return null;
		
	}
	
	
	 
	/**
	 * @param isPorchService the isPorchService to set
	 */
	public void setPorchService(boolean isPorchService) {
		this.isPorchService = isPorchService;
	}
	
	
	public void setPorchServiceRef(RepositoryItem serviceReferal) {
		 this.setPropertyValue("serviceReferal", serviceReferal);
	}
	 
}
