/*
 * 
 */
package com.bbb.commerce.giftregistry.vo;

import java.io.Serializable;
/*import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import atg.core.util.StringUtils;*/



// TODO: Auto-generated Javadoc
/**
 * This class provides the Registry summary information properties.
 *
 * @author sku134
 */
public class RegNamesVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int registryNum;
	int nameAddrNum;
	String nameAddrType;
	String lastName;
	String firstName;
	String address1;
	String address2;
	String city;
	String state;
	String zipCode;
	String dayPhone;
	String dayPhoneExt;
	String evePhone;
	String evePhoneExt;
	String company;
	int asOfDate;
	String emailId;
	String actionCD;
	String MI;
	String suffix;
	String maiden;
	String atgProfileId;
	String lastName2;
	String emailFlag;
	String prefContMeth;
	String prefContTime;
	String affiliateOptIn;
	String asOfDateFtrShipping;
	
	public int getRegistryNum() {
		return registryNum;
	}
	public void setRegistryNum(int registryNum) {
		this.registryNum = registryNum;
	}
	public int getNameAddrNum() {
		return nameAddrNum;
	}
	public void setNameAddrNum(int nameAddrNum) {
		this.nameAddrNum = nameAddrNum;
	}
	public String getNameAddrType() {
		return nameAddrType;
	}
	public void setNameAddrType(String nameAddrType) {
		this.nameAddrType = nameAddrType;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public String getDayPhone() {
		return dayPhone;
	}
	public void setDayPhone(String dayPhone) {
		this.dayPhone = dayPhone;
	}
	public String getDayPhoneExt() {
		return dayPhoneExt;
	}
	public void setDayPhoneExt(String dayPhoneExt) {
		this.dayPhoneExt = dayPhoneExt;
	}
	public String getEvePhone() {
		return evePhone;
	}
	public void setEvePhone(String evePhone) {
		this.evePhone = evePhone;
	}
	public String getEvePhoneExt() {
		return evePhoneExt;
	}
	public void setEvePhoneExt(String evePhoneExt) {
		this.evePhoneExt = evePhoneExt;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public int getAsOfDate() {
		return asOfDate;
	}
	public void setAsOfDate(int asOfDate) {
		this.asOfDate = asOfDate;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getActionCD() {
		return actionCD;
	}
	public void setActionCD(String actionCD) {
		this.actionCD = actionCD;
	}
	public String getMI() {
		return MI;
	}
	public void setMI(String mI) {
		MI = mI;
	}
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	public String getMaiden() {
		return maiden;
	}
	public void setMaiden(String maiden) {
		this.maiden = maiden;
	}
	public String getAtgProfileId() {
		return atgProfileId;
	}
	public void setAtgProfileId(String atgProfileId) {
		this.atgProfileId = atgProfileId;
	}
	public String getLastName2() {
		return lastName2;
	}
	public void setLastName2(String lastName2) {
		this.lastName2 = lastName2;
	}
	public String getEmailFlag() {
		return emailFlag;
	}
	public void setEmailFlag(String emailFlag) {
		this.emailFlag = emailFlag;
	}
	public String getPrefContMeth() {
		return prefContMeth;
	}
	public void setPrefContMeth(String prefContMeth) {
		this.prefContMeth = prefContMeth;
	}
	public String getPrefContTime() {
		return prefContTime;
	}
	public void setPrefContTime(String prefContTime) {
		this.prefContTime = prefContTime;
	}
	public String getAffiliateOptIn() {
		return affiliateOptIn;
	}
	public void setAffiliateOptIn(String affiliateOptIn) {
		this.affiliateOptIn = affiliateOptIn;
	}
	public String getAsOfDateFtrShipping() {
		return asOfDateFtrShipping;
	}
	public void setAsOfDateFtrShipping(String asOfDateFtrShipping) {
		this.asOfDateFtrShipping = asOfDateFtrShipping;
	}
	
	

}