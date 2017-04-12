package com.bbb.commerce.catalog.vo;

import java.io.Serializable;


/**
 * @author aku239
 *
 */
public class ScheduleAppointmentVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String skedgeURL;
	private String storeId;
	private String firstname;
	private String lastname;
	private String phone;
	private String email;
	private String registryId;
	private String eventDate;
	private String coregFN;
	private String coregLN;
	private boolean directskedgeMe;
	private boolean scheduleappointment;
	private boolean errormodal;
	private String appointmentType;
	private int preSelectedServiceRef;
	private String pageName;
	
	/**
	 * @return the pageName
	 */
	public String getPageName() {
		return pageName;
	}
	/**
	 * @param pageName the pageName to set
	 */
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	/**
	 * @return the preSelectedServiceRef
	 */
	public int getPreSelectedServiceRef() {
		return preSelectedServiceRef;
	}
	/**
	 * @param preSelectedServiceRef the preSelectedServiceRef to set
	 */
	public void setPreSelectedServiceRef(int preSelectedServiceRef) {
		this.preSelectedServiceRef = preSelectedServiceRef;
	}
	/**
	 * @return the appointmentType
	 */
	public String getAppointmentType() {
		return appointmentType;
	}
	/**
	 * @param appointmentType the appointmentType to set
	 */
	public void setAppointmentType(String appointmentType) {
		this.appointmentType = appointmentType;
	}
	/**
	 * @return the directskedgeMe
	 */
	public boolean isDirectskedgeMe() {
		return directskedgeMe;
	}
	/**
	 * @param directskedgeMe the directskedgeMe to set
	 */
	public void setDirectskedgeMe(boolean directskedgeMe) {
		this.directskedgeMe = directskedgeMe;
	}
	/**
	 * @return the isScheduleappointment
	 */
	public boolean isScheduleappointment() {
		return scheduleappointment;
	}
	/**
	 * @param isScheduleappointment the isScheduleappointment to set
	 */
	public void setScheduleappointment(boolean isScheduleappointment) {
		this.scheduleappointment = isScheduleappointment;
	}
	/**
	 * @return the errormodal
	 */
	public boolean isErrormodal() {
		return errormodal;
	}
	/**
	 * @param errormodal the errormodal to set
	 */
	public void setErrormodal(boolean errormodal) {
		this.errormodal = errormodal;
	}	
	/**
	 * @return the skedgeURL
	 */
	public String getSkedgeURL() {
		return skedgeURL;
	}
	/**
	 * @param skedgeURL the skedgeURL to set
	 */
	public void setSkedgeURL(String skedgeURL) {
		this.skedgeURL = skedgeURL;
	}
	/**
	 * @return the storeId
	 */
	public String getStoreId() {
		return storeId;
	}
	/**
	 * @param storeId the storeId to set
	 */
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	/**
	 * @return the firstname
	 */
	public String getFirstname() {
		return firstname;
	}
	/**
	 * @param firstname the firstname to set
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	/**
	 * @return the lastname
	 */
	public String getLastname() {
		return lastname;
	}
	/**
	 * @param lastname the lastname to set
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the registryId
	 */
	public String getRegistryId() {
		return registryId;
	}
	/**
	 * @param registryId the registryId to set
	 */
	public void setRegistryId(String registryId) {
		this.registryId = registryId;
	}
	/**
	 * @return the eventDate
	 */
	public String getEventDate() {
		return eventDate;
	}
	/**
	 * @param eventDate the eventDate to set
	 */
	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}
	/**
	 * @return the coregFN
	 */
	public String getCoregFN() {
		return coregFN;
	}
	/**
	 * @param coregFN the coregFN to set
	 */
	public void setCoregFN(String coregFN) {
		this.coregFN = coregFN;
	}
	/**
	 * @return the coregLN
	 */
	public String getCoregLN() {
		return coregLN;
	}
	/**
	 * @param coregLN the coregLN to set
	 */
	public void setCoregLN(String coregLN) {
		this.coregLN = coregLN;
	}	
	
	public ScheduleAppointmentVO() {
		//default constructor
	}
}
