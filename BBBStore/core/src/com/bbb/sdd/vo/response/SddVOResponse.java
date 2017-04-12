package com.bbb.sdd.vo.response;

import java.io.Serializable;
import java.util.List;

import com.bbb.sdd.vo.PackageSddVOReq;

// TODO: Auto-generated Javadoc
/**
 * The Class SddVOResponse.
 *
 * @author 
 */
public class SddVOResponse implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The id. */
	private String id;
    
    /** The object. */
    private String object;
    
    /** The tracking_code. */
    private String tracking_code;
    
    /** The order_reference. */
    private String order_reference;
    
    /** The status. */
    private String status;
    
    /** The ready_by. */
    private String ready_by;
    
    /** The driver. */
    private String driver;
    
    /** The estimated_delivery_at. */
    private String estimated_delivery_at;
    
    /** The delivered_at. */
    private String delivered_at;
    
    /** The origin_comments. */
    private String origin_comments;
	
	/** The store_signature_required. */
	private String store_signature_required;
    
    /** The destination_comments. */
    private String destination_comments;
    
    /** The customer_signature_type. */
    private String customer_signature_type;
    
    /** The age_required. */
    private String age_required;
    
    /** The webhook_url. */
    private String webhook_url;
    
    /** The delivery_window. */
    private DeliveryWindowSddVOResponse delivery_window;
    
    /** The customer. */
    private CustomerSddVOResponse customer;
    
    /** The store. */
    private StoreSddVOResponse store;
    
    /** The packages. */
    private List<PackageSddVOReq> packages;
    
    /**
     * Sets the packages.
     *
     * @param packages the new packages
     */
    public void setPackages(List<PackageSddVOReq> packages) {
		this.packages = packages;
	}
	
    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId() {
		return id;
	}
	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Gets the object.
	 *
	 * @return the object
	 */
	public String getObject() {
		return object;
	}
	
	/**
	 * Sets the object.
	 *
	 * @param object the new object
	 */
	public void setObject(String object) {
		this.object = object;
	}
	
	/**
	 * Gets the tracking_code.
	 *
	 * @return the tracking_code
	 */
	public String getTracking_code() {
		return tracking_code;
	}
	
	/**
	 * Sets the tracking_code.
	 *
	 * @param tracking_code the new tracking_code
	 */
	public void setTracking_code(String tracking_code) {
		this.tracking_code = tracking_code;
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
	
	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	
	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(String status) {
		this.status = status;
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
	 * Gets the driver.
	 *
	 * @return the driver
	 */
	public String getDriver() {
		return driver;
	}
	
	/**
	 * Sets the driver.
	 *
	 * @param driver the new driver
	 */
	public void setDriver(String driver) {
		this.driver = driver;
	}
	
	/**
	 * Gets the estimated_delivery_at.
	 *
	 * @return the estimated_delivery_at
	 */
	public String getEstimated_delivery_at() {
		return estimated_delivery_at;
	}
	
	/**
	 * Sets the estimated_delivery_at.
	 *
	 * @param estimated_delivery_at the new estimated_delivery_at
	 */
	public void setEstimated_delivery_at(String estimated_delivery_at) {
		this.estimated_delivery_at = estimated_delivery_at;
	}
	
	/**
	 * Gets the delivered_at.
	 *
	 * @return the delivered_at
	 */
	public String getDelivered_at() {
		return delivered_at;
	}
	
	/**
	 * Sets the delivered_at.
	 *
	 * @param delivered_at the new delivered_at
	 */
	public void setDelivered_at(String delivered_at) {
		this.delivered_at = delivered_at;
	}
	
	/**
	 * Gets the store_signature_required.
	 *
	 * @return the store_signature_required
	 */
	public String getStore_signature_required() {
		return store_signature_required;
	}
	
	/**
	 * Sets the store_signature_required.
	 *
	 * @param store_signature_required the new store_signature_required
	 */
	public void setStore_signature_required(String store_signature_required) {
		this.store_signature_required = store_signature_required;
	}
	
	/**
	 * Gets the destination_comments.
	 *
	 * @return the destination_comments
	 */
	public String getDestination_comments() {
		return destination_comments;
	}
	
	/**
	 * Sets the destination_comments.
	 *
	 * @param destination_comments the new destination_comments
	 */
	public void setDestination_comments(String destination_comments) {
		this.destination_comments = destination_comments;
	}
	
	/**
	 * Gets the customer_signature_type.
	 *
	 * @return the customer_signature_type
	 */
	public String getCustomer_signature_type() {
		return customer_signature_type;
	}
	
	/**
	 * Sets the customer_signature_type.
	 *
	 * @param customer_signature_type the new customer_signature_type
	 */
	public void setCustomer_signature_type(String customer_signature_type) {
		this.customer_signature_type = customer_signature_type;
	}
	
	/**
	 * Gets the age_required.
	 *
	 * @return the age_required
	 */
	public String getAge_required() {
		return age_required;
	}
	
	/**
	 * Sets the age_required.
	 *
	 * @param age_required the new age_required
	 */
	public void setAge_required(String age_required) {
		this.age_required = age_required;
	}
	
	/**
	 * Gets the webhook_url.
	 *
	 * @return the webhook_url
	 */
	public String getWebhook_url() {
		return webhook_url;
	}
	
	/**
	 * Sets the webhook_url.
	 *
	 * @param webhook_url the new webhook_url
	 */
	public void setWebhook_url(String webhook_url) {
		this.webhook_url = webhook_url;
	}
	
	/**
	 * Gets the delivery_window.
	 *
	 * @return the delivery_window
	 */
	public DeliveryWindowSddVOResponse getDelivery_window() {
		return delivery_window;
	}
	
	/**
	 * Sets the delivery_window.
	 *
	 * @param delivery_window the new delivery_window
	 */
	public void setDelivery_window(DeliveryWindowSddVOResponse delivery_window) {
		this.delivery_window = delivery_window;
	}
	
	/**
	 * Gets the customer.
	 *
	 * @return the customer
	 */
	public CustomerSddVOResponse getCustomer() {
		return customer;
	}
	
	/**
	 * Sets the customer.
	 *
	 * @param customer the new customer
	 */
	public void setCustomer(CustomerSddVOResponse customer) {
		this.customer = customer;
	}
	
	/**
	 * Gets the store.
	 *
	 * @return the store
	 */
	public StoreSddVOResponse getStore() {
		return store;
	}
	
	/**
	 * Sets the store.
	 *
	 * @param store the new store
	 */
	public void setStore(StoreSddVOResponse store) {
		this.store = store;
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
	 * Gets the origin_comments.
	 *
	 * @return the origin_comments
	 */
	public String getOrigin_comments() {
		return origin_comments;
	}
	
	/**
	 * Sets the origin_comments.
	 *
	 * @param origin_comments the new origin_comments
	 */
	public void setOrigin_comments(String origin_comments) {
		this.origin_comments = origin_comments;
	}
	
/* (non-Javadoc)
 * @see java.lang.Object#toString()
 */
public String toString(){
		
		StringBuffer sddVOResponseDetails = new StringBuffer("");
		sddVOResponseDetails.append("id: "+this.id + "|");
		sddVOResponseDetails.append("object: "+this.object + "|");
		sddVOResponseDetails.append("tracking_code: "+this.tracking_code + "|");
		sddVOResponseDetails.append("order_reference: "+this.order_reference + "|");
		sddVOResponseDetails.append("status:"+this.status + "|");
		sddVOResponseDetails.append("ready_by: "+this.ready_by + "|");
		sddVOResponseDetails.append("driver:"+this.driver + "|");
		sddVOResponseDetails.append("delivered_at: "+this.delivered_at + "|");
		sddVOResponseDetails.append("delivery_window: "+this.delivery_window + "|");
		sddVOResponseDetails.append("store: "+this.store + "|");
		sddVOResponseDetails.append("origin_comments: "+this.origin_comments + "|");
		sddVOResponseDetails.append("store_signature_required: "+this.store_signature_required + "|");
		sddVOResponseDetails.append("destination_comments: "+this.destination_comments + "|");
		sddVOResponseDetails.append("customer_signature_type: "+this.customer_signature_type + "|");
		sddVOResponseDetails.append("age_required: "+this.age_required + "|");
		sddVOResponseDetails.append("webhook_url: "+this.webhook_url + "|");
		sddVOResponseDetails.append("packages: "+this.packages.toString() + "|");
		return sddVOResponseDetails.toString();
}

}

	