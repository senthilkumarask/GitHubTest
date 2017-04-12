package com.bbb.sdd.vo.response;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Class CustomerSddVOResponse.
 *
 * @author 
 */
public class CustomerSddVOResponse implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The first_name. */
	private String first_name = "";
	
	/** The last_name. */
	private String last_name = "";
	
	/** The email. */
	private String email = "";
	
	/** The phone. */
	private String phone = "";
	
	/** The business_name. */
	private String business_name="";
	
	/** The address_zipcode. */
	private String address_zipcode;
    
    /** The address_line_1. */
    private String address_line_1;
	
	/** The address_city. */
	private String address_city;
	
	/** The address_state. */
	private String address_state;
	

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
	 * Gets the first_name.
	 *
	 * @return the first_name
	 */
	public String getFirst_name() {
		return first_name;
	}


	/**
	 * Sets the first_name.
	 *
	 * @param first_name the new first_name
	 */
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}


	/**
	 * Gets the last_name.
	 *
	 * @return the last_name
	 */
	public String getLast_name() {
		return last_name;
	}


	/**
	 * Sets the last_name.
	 *
	 * @param last_name the new last_name
	 */
	public void setLast_name(String last_name) {
		this.last_name = last_name;
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
	 * Gets the business_name.
	 *
	 * @return the business_name
	 */
	public String getBusiness_name() {
		return business_name;
	}


	/**
	 * Sets the business_name.
	 *
	 * @param business_name the new business_name
	 */
	public void setBusiness_name(String business_name) {
		this.business_name = business_name;
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
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email.
	 *
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	
	
/* (non-Javadoc)
 * @see java.lang.Object#toString()
 */
public String toString(){
		
		StringBuffer customerSddVOResponse = new StringBuffer("");
		customerSddVOResponse.append("first_name: "+this.first_name + "|");
		customerSddVOResponse.append("last_name: "+this.last_name + "|");
		customerSddVOResponse.append("business_name: "+this.business_name + "|");
		customerSddVOResponse.append("phone: "+this.phone + "|");
		customerSddVOResponse.append("email: "+this.email + "|");
		customerSddVOResponse.append("address_line_1: "+this.address_line_1 + "|");
		customerSddVOResponse.append("address_city: "+this.address_city + "|");
		customerSddVOResponse.append("address_state: "+this.address_state + "|");
		customerSddVOResponse.append("address_zipcode: "+this.address_zipcode + "|");
		return customerSddVOResponse.toString();
		
	}
	
}