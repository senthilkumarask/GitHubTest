package com.bbb.selfservice.vo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import atg.multisite.SiteContextManager;

import com.bbb.constants.BBBCoreConstants;

/**
 * @author rsain4
 * 
 */
public class BeddingShipAddrVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String schoolId;
	private String addrLine1;
	private String addrLine2;
	private String city;
	private String state;
	private String zip;
	private String shippingStartDate;
	private String shippingEndDate;
	private String companyName;
	private String collegeName;
	private String currentDate;
	/**
		 * 
		 */
	public BeddingShipAddrVO() {
		super();
	}

	/**
	 * @return the collegeId
	 */
	public final String getSchoolId() {
		return this.schoolId;
	}

	/**
	 * @param pSchoolId
	 *            the companyName to set
	 */
	public final void setSchoolId(final String pSchoolId) {
		this.schoolId = pSchoolId;
	}

	/**
	 * @return the companyName
	 */
	public final String getCompanyName() {
		return this.companyName;
	}

	/**
	 * @param pCompanyName
	 *            the companyName to set
	 */
	public final void setCompanyName(final String pCompanyName) {
		this.companyName = pCompanyName;
	}

	/**
	 * @return the shippingStartDate
	 */
	public final String getShippingStartDate() {
		return this.shippingStartDate;
	}

	/**
	 * @param pShippingStartDate
	 *            the shippingStartDate to set
	 */
	public final void setShippingStartDate(final String pShippingStartDate) {
		this.shippingStartDate = pShippingStartDate;
	}

	/**
	 * @return the shippingEndDate
	 */
	public final String getShippingEndDate() {
		return this.shippingEndDate;
	}

	/**
	 * @param pShippingEndDate
	 *            the shippingEndDate to set
	 */
	public final void setShippingEndDate(final String pShippingEndDate) {
		this.shippingEndDate = pShippingEndDate;
	}

	/**
	 * @return the addrLine1
	 */
	public final String getAddrLine1() {
		return this.addrLine1;
	}

	/**
	 * @param pAddrLine1
	 *            the addrLine1 to set
	 */
	public final void setAddrLine1(final String pAddrLine1) {
		this.addrLine1 = pAddrLine1;
	}

	/**
	 * @return the addrLine2
	 */
	public final String getAddrLine2() {
		return this.addrLine2;
	}

	/**
	 * @param pAddrLine2
	 *            the addrLine2 to set
	 */
	public final void setAddrLine2(final String pAddrLine2) {
		this.addrLine2 = pAddrLine2;
	}

	/**
	 * @return the city
	 */
	public final String getCity() {
		return this.city;
	}

	/**
	 * @param pCity
	 *            the city to set
	 */
	public final void setCity(final String pCity) {
		this.city = pCity;
	}

	/**
	 * @return the zip
	 */
	public final String getZip() {
		return this.zip;
	}

	/**
	 * @param pZip
	 *            the zip to set
	 */
	public final void setZip(final String pZip) {
		this.zip = pZip;
	}

	/**
	 * @return the state
	 */
	public final String getState() {
		return this.state;
	}

	/**
	 * @param pState
	 *            the state to set
	 */
	public final void setState(final String pState) {
		this.state = pState;
	}

	/**
	 * @return the collegeName
	 */
	public final String getCollegeName() {
		return this.collegeName;
	}

	/**
	 * @param pCollegeName
	 *            the collegeName to set
	 */
	public final void setCollegeName(final String pCollegeName) {
		this.collegeName = pCollegeName;
	}

	/**
	 * @return the currentDate
	 */
	public final String getCurrentDate() {
		final Date date = new Date();
		final String siteId = SiteContextManager.getCurrentSiteId();
		String format;
		if (null !=siteId && siteId.equals(BBBCoreConstants.SITE_BAB_CA)) {
			format = BBBCoreConstants.CA_DATE_FORMAT;
		} else {
			format = BBBCoreConstants.US_DATE_FORMAT;
		}
		final SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		final String currentDate = dateFormat.format(date);
		return currentDate;
	}

	/**
	 * @param currentDate the currentDate to set
	 */
	public final void setCurrentDate(final String currentDate) {
		this.currentDate = currentDate;
	}

}
