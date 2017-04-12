/**
 * --------------------------------------------------------------------------------
 * Copyright 2015 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: Sapinent
 *
 * Created on: 12/23/2015
 * --------------------------------------------------------------------------------
 */
package com.bbb.account.wallet;

import com.bbb.framework.integration.ServiceResponseBase;
import com.bbb.framework.webservices.vo.ErrorStatus;

public class BBBGetProfileResponse extends ServiceResponseBase {

	/**
	 * Default generated serial version.
	 */
	private static final long serialVersionUID = 1L;
	private String email, firstname, lastname, zip;
	private ErrorStatus errorStatus;

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public ErrorStatus getErrorStatus() {
		return errorStatus;
	}
	public void setErrorStatus(ErrorStatus errorStatus) {
		this.errorStatus = errorStatus;
	}
	@Override
	public String toString() {
		return "GetProfileRes [email=" + email + ", firstname=" + firstname
				+ ", lastname=" + lastname + ", zip=" + zip + ", ErrorStatus="
				+ errorStatus + "]";
	}
	
}
