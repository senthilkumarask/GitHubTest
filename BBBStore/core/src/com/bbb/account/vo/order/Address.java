package com.bbb.account.vo.order;

import java.io.Serializable;

/**
 * Address.java
 *
 * This file is part of OrderDetails webservice response
 * 
 */

public class Address  implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String firstNm;

    private String midNm;

    private String lastNm;

    private String addr1;

    private String addr2;

    private String company;

    private String city;

    private String state;

    private String zip;

    private String dayPhone;

    private String evePhone;

	/**
	 * @return the firstNm
	 */
	public String getFirstNm() {
		return firstNm;
	}

	/**
	 * @param firstNm the firstNm to set
	 */
	public void setFirstNm(String firstNm) {
		this.firstNm = firstNm;
	}

	/**
	 * @return the midNm
	 */
	public String getMidNm() {
		return midNm;
	}

	/**
	 * @param midNm the midNm to set
	 */
	public void setMidNm(String midNm) {
		this.midNm = midNm;
	}

	/**
	 * @return the lastNm
	 */
	public String getLastNm() {
		return lastNm;
	}

	/**
	 * @param lastNm the lastNm to set
	 */
	public void setLastNm(String lastNm) {
		this.lastNm = lastNm;
	}

	/**
	 * @return the addr1
	 */
	public String getAddr1() {
		return addr1;
	}

	/**
	 * @param addr1 the addr1 to set
	 */
	public void setAddr1(String addr1) {
		this.addr1 = addr1;
	}

	/**
	 * @return the addr2
	 */
	public String getAddr2() {
		return addr2;
	}

	/**
	 * @param addr2 the addr2 to set
	 */
	public void setAddr2(String addr2) {
		this.addr2 = addr2;
	}

	/**
	 * @return the company
	 */
	public String getCompany() {
		return company;
	}

	/**
	 * @param company the company to set
	 */
	public void setCompany(String company) {
		this.company = company;
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
	 * @return the zip
	 */
	public String getZip() {
		return zip;
	}

	/**
	 * @param zip the zip to set
	 */
	public void setZip(String zip) {
		this.zip = zip;
	}

	/**
	 * @return the dayPhone
	 */
	public String getDayPhone() {
		return dayPhone;
	}

	/**
	 * @param dayPhone the dayPhone to set
	 */
	public void setDayPhone(String dayPhone) {
		this.dayPhone = dayPhone;
	}

	/**
	 * @return the evePhone
	 */
	public String getEvePhone() {
		return evePhone;
	}

	/**
	 * @param evePhone the evePhone to set
	 */
	public void setEvePhone(String evePhone) {
		this.evePhone = evePhone;
	}
}
