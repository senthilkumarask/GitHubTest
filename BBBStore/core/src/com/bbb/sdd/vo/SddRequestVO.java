package com.bbb.sdd.vo;

import java.io.Serializable;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class SddRequestVO.
 *
 * @author 
 */
public class SddRequestVO implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The store_id. */
	private String store_id;
	private String store_id_alias;
	public String getStore_id_alias() {
		return store_id_alias;
	}

	public void setStore_id_alias(String store_id_alias) {
		this.store_id_alias = store_id_alias;
	}


	/**
 * Gets the store_id.
 *
 * @return the store_id
 */
public String getStore_id() {
		return store_id;
	}


	/**
	 * Sets the store_id.
	 *
	 * @param store_id the new store_id
	 */
	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}

	/** The delivery_window_id. */
	private String delivery_window_id;
    
    /** The order_reference. */
    private String order_reference;
	
	/** The packages. */
	private List<PackageSddVOReq> packages;
	
	/** The customer. */
	private CustomerSddVOReq customer;
    
    /** The ready_by. */
    private String ready_by;
	
	/**
	 * Gets the delivery_window_id.
	 *
	 * @return the delivery_window_id
	 */
	public String getDelivery_window_id() {
		return delivery_window_id;
	}


	/**
	 * Sets the delivery_window_id.
	 *
	 * @param delivery_window_id the new delivery_window_id
	 */
	public void setDelivery_window_id(String delivery_window_id) {
		this.delivery_window_id = delivery_window_id;
	}
	

	/**
	 * Gets the customer.
	 *
	 * @return the customer
	 */
	public CustomerSddVOReq getCustomer() {
		return customer;
	}


	/**
	 * Sets the customer.
	 *
	 * @param customer the new customer
	 */
	public void setCustomer(CustomerSddVOReq customer) {
		this.customer = customer;
	}


	/**
	 * Gets the packages.
	 *
	 * @return the packages
	 */
	public List<PackageSddVOReq> getPackages() {
		return packages;
	}


	/**
	 * Sets the packages.
	 *
	 * @param packages the new packages
	 */
	public void setPackages(List<PackageSddVOReq> packages) {
		this.packages = packages;
	}


	/**
	 * Gets the serialversionuid.
	 *
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	/**
	 * Gets the ready_by.
	 *
	 * @return the ready_by
	 */
	public String getReady_by() {
		return ready_by;
	}


	/**
	 * Sets the ready_by.
	 *
	 * @param ready_by the new ready_by
	 */
	public void setReady_by(String ready_by) {
		this.ready_by = ready_by;
	}


	/**
	 * Gets the order_reference.
	 *
	 * @return the order_reference
	 */
	public String getOrder_reference() {
		return order_reference;
	}


	/**
	 * Sets the order_reference.
	 *
	 * @param order_reference the new order_reference
	 */
	public void setOrder_reference(String order_reference) {
		this.order_reference = order_reference;
	}

/* (non-Javadoc)
 * @see java.lang.Object#toString()
 */
public String toString(){
		
		StringBuffer sddRequestDetails = new StringBuffer("");
		sddRequestDetails.append("store_id: "+this.store_id + "|");
		sddRequestDetails.append("store_id_alias: "+this.store_id_alias + "|");
		sddRequestDetails.append("customer: "+this.customer.toString() + "|");
		sddRequestDetails.append("ready_by: "+this.ready_by + "|");
		sddRequestDetails.append("packages: "+this.packages.toString() + "|");
		sddRequestDetails.append("order_reference: "+this.order_reference + "|");
		
		return sddRequestDetails.toString();
		
	}
	
}