package com.bbb.commerce.catalog.vo;

import java.util.List;
import java.io.Serializable;
import java.sql.Timestamp;

public class StoreVO implements Serializable{

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String storeId;
	private String storeName;
	private String address;
	private String city;
	private String state;
	private String postalCode;
	private String province;
	private String provinceName;
	private String countryCode;
	private String phone;
	private String longitude;
	private String latitude;
	private String storeType;
	private String hours;
	private String latLongSrc;
	private String rowXngDt;
	private String rowXngUser;
	private boolean hiringInd;
	private boolean displayOnline;
	private String facadeStoreType;
	private String commonNamePhonetic;
	private String addressPhonetic;
	private String cityPhonetic;
	private int monOpen;
	private int monClose;
	private int tuesOpen;
	private int tuesClose;
	private int wedOpen;
	private int wedClose;
	private int thursOpen;
	private int thursClose;
	private int friOpen;
	private int friClose;
	private int satOpen;
	private int satClose;
	private int sunOpen;
	private int sunClose;
	private String mqTransCode;
	private String specialMsg;
	private boolean contactFlag;
    private boolean babyCanadaFlag;
	private List<StoreSpecialityVO> storeSpecialityVO;
	private List<StoreBopusInfoVO> storeBopusInfoVO;
	private String specialtyShopsCd;
	private List<String> appointmentTypes;
	private List<String> regAppointmentTypes;	
	private Timestamp lastModDate;
	private boolean acceptingAppointments = false;
	private boolean appointmentAvailable;
	private boolean appointmentEligible;
	private int preSelectedServiceRef;
	
	
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
	 * @return the appointmentAvailable
	 */
	public boolean isAppointmentAvailable() {
		return appointmentAvailable;
	}
	/**
	 * @param appointmentAvailable the appointmentAvailable to set
	 */
	public void setAppointmentAvailable(boolean appointmentAvailable) {
		this.appointmentAvailable = appointmentAvailable;
	}
	/**
	 * @return the appointmentEligible
	 */
	public boolean isAppointmentEligible() {
		return appointmentEligible;
	}
	/**
	 * @param appointmentEligible the appointmentEligible to set
	 */
	public void setAppointmentEligible(boolean appointmentEligible) {
		this.appointmentEligible = appointmentEligible;
	}
	public boolean isContactFlag() {
		return contactFlag;
	}
	public void setContactFlag(boolean contactFlag) {
		this.contactFlag = contactFlag;
	}
	/**
	 * @return the specialtyShopsCd
	 */
	public String getSpecialtyShopsCd() {
		return specialtyShopsCd;
	}
	/**
	 * @param pSpecialtyShopsCd the specialtyShopsCd to set
	 */
	public void setSpecialtyShopsCd(String pSpecialtyShopsCd) {
		specialtyShopsCd = pSpecialtyShopsCd;
	}
	/**
	 * @return the provinceName
	 */
	public String getProvinceName() {
		return provinceName;
	}
	/**
	 * @param provinceName the provinceName to set
	 */
	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
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
	 * @return the storeName
	 */
	public String getStoreName() {
		return storeName;
	}
	/**
	 * @param storeName the storeName to set
	 */
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}
	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
	/**
	 * @return the postalCode
	 */
	public String getPostalCode() {
		return postalCode;
	}
	/**
	 * @param postalCode the postalCode to set
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	/**
	 * @return the province
	 */
	public String getProvince() {
		return province;
	}
	/**
	 * @param province the province to set
	 */
	public void setProvince(String province) {
		this.province = province;
	}
	/**
	 * @return the countryCode
	 */
	public String getCountryCode() {
		return countryCode;
	}
	/**
	 * @param countryCode the countryCode to set
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
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
	 * @return the longitude
	 */
	public String getLongitude() {
		return longitude;
	}
	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	/**
	 * @return the latitude
	 */
	public String getLatitude() {
		return latitude;
	}
	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	/**
	 * @return the storeType
	 */
	public String getStoreType() {
		return storeType;
	}
	/**
	 * @param storeType the storeType to set
	 */
	public void setStoreType(String storeType) {
		this.storeType = storeType;
	}
	/**
	 * @return the hours
	 */
	public String getHours() {
		return hours;
	}
	/**
	 * @param hours the hours to set
	 */
	public void setHours(String hours) {
		this.hours = hours;
	}
	/**
	 * @return the latLongSrc
	 */
	public String getLatLongSrc() {
		return latLongSrc;
	}
	/**
	 * @param latLongSrc the latLongSrc to set
	 */
	public void setLatLongSrc(String latLongSrc) {
		this.latLongSrc = latLongSrc;
	}
	/**
	 * @return the rowXngDt
	 */
	public String getRowXngDt() {
		return rowXngDt;
	}
	/**
	 * @param rowXngDt the rowXngDt to set
	 */
	public void setRowXngDt(String rowXngDt) {
		this.rowXngDt = rowXngDt;
	}
	/**
	 * @return the rowXngUser
	 */
	public String getRowXngUser() {
		return rowXngUser;
	}
	/**
	 * @param rowXngUser the rowXngUser to set
	 */
	public void setRowXngUser(String rowXngUser) {
		this.rowXngUser = rowXngUser;
	}

	public boolean isHiringInd() {
		return hiringInd;
	}
	public void setHiringInd(boolean hiringInd) {
		this.hiringInd = hiringInd;
	}
	/**
	 * @return the displayOnline
	 */
	public boolean isDisplayOnline() {
		return displayOnline;
	}
	/**
	 * @param displayOnline the displayOnline to set
	 */
	public void setDisplayOnline(boolean displayOnline) {
		this.displayOnline = displayOnline;
	}
	/**
	 * @return the facadeStoreType
	 */
	public String getFacadeStoreType() {
		return facadeStoreType;
	}
	/**
	 * @param facadeStoreType the facadeStoreType to set
	 */
	public void setFacadeStoreType(String facadeStoreType) {
		this.facadeStoreType = facadeStoreType;
	}
	/**
	 * @return the commonNamePhonetic
	 */
	public String getCommonNamePhonetic() {
		return commonNamePhonetic;
	}
	/**
	 * @param commonNamePhonetic the commonNamePhonetic to set
	 */
	public void setCommonNamePhonetic(String commonNamePhonetic) {
		this.commonNamePhonetic = commonNamePhonetic;
	}
	/**
	 * @return the addressPhonetic
	 */
	public String getAddressPhonetic() {
		return addressPhonetic;
	}
	/**
	 * @param addressPhonetic the addressPhonetic to set
	 */
	public void setAddressPhonetic(String addressPhonetic) {
		this.addressPhonetic = addressPhonetic;
	}
	/**
	 * @return the cityPhonetic
	 */
	public String getCityPhonetic() {
		return cityPhonetic;
	}
	/**
	 * @param cityPhonetic the cityPhonetic to set
	 */
	public void setCityPhonetic(String cityPhonetic) {
		this.cityPhonetic = cityPhonetic;
	}
	/**
	 * @return the monOpen
	 */
	public int getMonOpen() {
		return monOpen;
	}
	/**
	 * @param monOpen the monOpen to set
	 */
	public void setMonOpen(int monOpen) {
		this.monOpen = monOpen;
	}
	/**
	 * @return the monClose
	 */
	public int getMonClose() {
		return monClose;
	}
	/**
	 * @param monClose the monClose to set
	 */
	public void setMonClose(int monClose) {
		this.monClose = monClose;
	}
	/**
	 * @return the tuesOpen
	 */
	public int getTuesOpen() {
		return tuesOpen;
	}
	/**
	 * @param tuesOpen the tuesOpen to set
	 */
	public void setTuesOpen(int tuesOpen) {
		this.tuesOpen = tuesOpen;
	}
	/**
	 * @return the tuesClose
	 */
	public int getTuesClose() {
		return tuesClose;
	}
	/**
	 * @param tuesClose the tuesClose to set
	 */
	public void setTuesClose(int tuesClose) {
		this.tuesClose = tuesClose;
	}
	/**
	 * @return the wedOpen
	 */
	public int getWedOpen() {
		return wedOpen;
	}
	/**
	 * @param wedOpen the wedOpen to set
	 */
	public void setWedOpen(int wedOpen) {
		this.wedOpen = wedOpen;
	}
	/**
	 * @return the wedClose
	 */
	public int getWedClose() {
		return wedClose;
	}
	/**
	 * @param wedClose the wedClose to set
	 */
	public void setWedClose(int wedClose) {
		this.wedClose = wedClose;
	}
	/**
	 * @return the thursOpen
	 */
	public int getThursOpen() {
		return thursOpen;
	}
	/**
	 * @param thursOpen the thursOpen to set
	 */
	public void setThursOpen(int thursOpen) {
		this.thursOpen = thursOpen;
	}
	/**
	 * @return the thursClose
	 */
	public int getThursClose() {
		return thursClose;
	}
	/**
	 * @param thursClose the thursClose to set
	 */
	public void setThursClose(int thursClose) {
		this.thursClose = thursClose;
	}
	/**
	 * @return the friOpen
	 */
	public int getFriOpen() {
		return friOpen;
	}
	/**
	 * @param friOpen the friOpen to set
	 */
	public void setFriOpen(int friOpen) {
		this.friOpen = friOpen;
	}
	/**
	 * @return the friClose
	 */
	public int getFriClose() {
		return friClose;
	}
	/**
	 * @param friClose the friClose to set
	 */
	public void setFriClose(int friClose) {
		this.friClose = friClose;
	}
	/**
	 * @return the satOpen
	 */
	public int getSatOpen() {
		return satOpen;
	}
	/**
	 * @param satOpen the satOpen to set
	 */
	public void setSatOpen(int satOpen) {
		this.satOpen = satOpen;
	}
	/**
	 * @return the satClose
	 */
	public int getSatClose() {
		return satClose;
	}
	/**
	 * @param satClose the satClose to set
	 */
	public void setSatClose(int satClose) {
		this.satClose = satClose;
	}
	/**
	 * @return the sunOpen
	 */
	public int getSunOpen() {
		return sunOpen;
	}
	/**
	 * @param sunOpen the sunOpen to set
	 */
	public void setSunOpen(int sunOpen) {
		this.sunOpen = sunOpen;
	}
	/**
	 * @return the sunClose
	 */
	public int getSunClose() {
		return sunClose;
	}
	/**
	 * @param sunClose the sunClose to set
	 */
	public void setSunClose(int sunClose) {
		this.sunClose = sunClose;
	}
	/**
	 * @return the mqTransCode
	 */
	public String getMqTransCode() {
		return mqTransCode;
	}
	/**
	 * @param mqTransCode the mqTransCode to set
	 */
	public void setMqTransCode(String mqTransCode) {
		this.mqTransCode = mqTransCode;
	}
	/**
	 * @return the specialMsg
	 */
	public String getSpecialMsg() {
		return specialMsg;
	}
	/**
	 * @param specialMsg the specialMsg to set
	 */
	public void setSpecialMsg(String specialMsg) {
		this.specialMsg = specialMsg;
	}
	/**
	 * @return the storeSpecialityVO
	 */
	public List<StoreSpecialityVO> getStoreSpecialityVO() {
		return storeSpecialityVO;
	}
	/**
	 * @param storeSpecialityVO the storeSpecialityVO to set
	 */
	public void setStoreSpecialityVO(List<StoreSpecialityVO> storeSpecialityVO) {
		this.storeSpecialityVO = storeSpecialityVO;
	}
	/**
	 * @return the storeBopusInfoVO
	 */
	public List<StoreBopusInfoVO> getStoreBopusInfoVO() {
		return storeBopusInfoVO;
	}
	/**
	 * @param storeBopusInfoVO the storeBopusInfoVO to set
	 */
	public void setStoreBopusInfoVO(List<StoreBopusInfoVO> storeBopusInfoVO) {
		this.storeBopusInfoVO = storeBopusInfoVO;
	}
	public boolean isBabyCanadaFlag() {
		return babyCanadaFlag;
	}
	public void setBabyCanadaFlag(boolean babyCanadaFlag) {
		this.babyCanadaFlag = babyCanadaFlag;
	}
	public List<String> getAppointmentTypes() {
		return appointmentTypes;
	}
	public void setAppointmentTypes(List<String> appointmentTypes) {
		this.appointmentTypes = appointmentTypes;
	}
	public List<String> getRegAppointmentTypes() {
		return regAppointmentTypes;
	}
	public void setRegAppointmentTypes(List<String> regAppointmentTypes) {
		this.regAppointmentTypes = regAppointmentTypes;
	}
	public Timestamp getLastModDate() {
		return lastModDate;
	}
	public void setLastModDate(Timestamp lastModDate) {
		this.lastModDate = lastModDate;
	}
	public boolean isAcceptingAppointments() {
		return acceptingAppointments;
	}
	public void setAcceptingAppointments(boolean acceptingAppointments) {
		this.acceptingAppointments = acceptingAppointments;
	}


}
