package com.bbb.sdd.vo.response;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Class StoreSddVOResponse.
 *
 * @author 
 */
public class StoreSddVOResponse implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	
	/** The id. */
	private String id = "";
	
	/** The name. */
	private String name = "";
	
	/** The phone. */
	private String phone = "";
	
	/** The suite_number. */
	private String suite_number = "";
	
	/** The address_line_1. */
	private String address_line_1 = "";
	
	/** The address_city. */
	private String address_city = "";
	
	/** The address_state. */
	private String address_state = "";
	
	/** The address_zipcode. */
	private String address_zipcode = "";
	
	/** The type. */
	private String type = "";
	
	/** The offers_delivery. */
	private String offers_delivery = "";
	
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
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the phone.
	 *
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}
	
	/**
	 * Sets the phone.
	 *
	 * @param phone the new phone
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	/**
	 * Gets the suite_number.
	 *
	 * @return the suite_number
	 */
	public String getSuite_number() {
		return suite_number;
	}
	
	/**
	 * Sets the suite_number.
	 *
	 * @param suite_number the new suite_number
	 */
	public void setSuite_number(String suite_number) {
		this.suite_number = suite_number;
	}
	
	/**
	 * Gets the address_line_1.
	 *
	 * @return the address_line_1
	 */
	public String getAddress_line_1() {
		return address_line_1;
	}
	
	/**
	 * Sets the address_line_1.
	 *
	 * @param address_line_1 the new address_line_1
	 */
	public void setAddress_line_1(String address_line_1) {
		this.address_line_1 = address_line_1;
	}
	
	/**
	 * Gets the address_city.
	 *
	 * @return the address_city
	 */
	public String getAddress_city() {
		return address_city;
	}
	
	/**
	 * Sets the address_city.
	 *
	 * @param address_city the new address_city
	 */
	public void setAddress_city(String address_city) {
		this.address_city = address_city;
	}
	
	/**
	 * Gets the address_state.
	 *
	 * @return the address_state
	 */
	public String getAddress_state() {
		return address_state;
	}
	
	/**
	 * Sets the address_state.
	 *
	 * @param address_state the new address_state
	 */
	public void setAddress_state(String address_state) {
		this.address_state = address_state;
	}
	
	/**
	 * Gets the address_zipcode.
	 *
	 * @return the address_zipcode
	 */
	public String getAddress_zipcode() {
		return address_zipcode;
	}
	
	/**
	 * Sets the address_zipcode.
	 *
	 * @param address_zipcode the new address_zipcode
	 */
	public void setAddress_zipcode(String address_zipcode) {
		this.address_zipcode = address_zipcode;
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Gets the offers_delivery.
	 *
	 * @return the offers_delivery
	 */
	public String getOffers_delivery() {
		return offers_delivery;
	}
	
	/**
	 * Sets the offers_delivery.
	 *
	 * @param offers_delivery the new offers_delivery
	 */
	public void setOffers_delivery(String offers_delivery) {
		this.offers_delivery = offers_delivery;
	}
	
	/**
	 * Gets the serialversionuid.
	 *
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	 /* (non-Javadoc)
 	 * @see java.lang.Object#toString()
 	 */
 	public String toString(){
	StringBuffer storeDetails = new StringBuffer("");
	storeDetails.append("id: "+this.id + "|");
	storeDetails.append("name: "+this.name + "|");
	storeDetails.append("phone: "+this.phone + "|");
	storeDetails.append("suite_number: "+this.suite_number + "|");
	storeDetails.append("address_line_1: "+this.address_line_1 + "|");
	storeDetails.append("address_city: "+this.address_city + "|");
	storeDetails.append("address_state: "+this.address_state + "|");
	storeDetails.append("address_zipcode: "+this.address_zipcode + "|");
	storeDetails.append("type: "+this.type + "|");
	storeDetails.append("offers_delivery: "+this.offers_delivery + "|");
	return storeDetails.toString();
	
}
	
	}