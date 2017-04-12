package com.bbb.search.bean;

import atg.nucleus.GenericService;

public class TBSOrderSearchSessionBean extends GenericService {

	private String email;
	private String orderId;
	private String firstName;
	private String lastName;
	private String registryNum;
	private String storeNum;
	private String startDate;
	private String endDate;
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @return the orderId
	 */
	public String getOrderId() {
		return orderId;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * @return the registryNum
	 */
	public String getRegistryNum() {
		return registryNum;
	}
	/**
	 * @return the storeNum
	 */
	public String getStoreNum() {
		return storeNum;
	}
	/**
	 * @return the startDate
	 */
	public String getStartDate() {
		return startDate;
	}
	/**
	 * @return the endDate
	 */
	public String getEndDate() {
		return endDate;
	}
	/**
	 * @param pEmail the email to set
	 */
	public void setEmail(String pEmail) {
		email = pEmail;
	}
	/**
	 * @param pOrderId the orderId to set
	 */
	public void setOrderId(String pOrderId) {
		orderId = pOrderId;
	}
	/**
	 * @param pFirstName the firstName to set
	 */
	public void setFirstName(String pFirstName) {
		firstName = pFirstName;
	}
	/**
	 * @param pLastName the lastName to set
	 */
	public void setLastName(String pLastName) {
		lastName = pLastName;
	}
	/**
	 * @param pRegistryNum the registryNum to set
	 */
	public void setRegistryNum(String pRegistryNum) {
		registryNum = pRegistryNum;
	}
	/**
	 * @param pStoreNum the storeNum to set
	 */
	public void setStoreNum(String pStoreNum) {
		storeNum = pStoreNum;
	}
	/**
	 * @param pStartDate the startDate to set
	 */
	public void setStartDate(String pStartDate) {
		startDate = pStartDate;
	}
	/**
	 * @param pEndDate the endDate to set
	 */
	public void setEndDate(String pEndDate) {
		endDate = pEndDate;
	}
	
	public void clear(){
		setEmail(null);
		setOrderId(null);
		setFirstName(null);
		setLastName(null);
		setRegistryNum(null);
		setStoreNum(null);
		setStartDate(null);
		setEndDate(null);
	}
}
