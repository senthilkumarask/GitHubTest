/**
 * ProfileService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.1  Built on : Aug 31, 2011 (12:22:40 CEST)
 */
package com.bbb.account.service.profile;

/**
 * ProfileService java skeleton interface for the axisService
 */
public interface ProfileService {

	/**
	 * Auto generated method signature
	 * 
	 * @param profileInfoRequestVO
	 */

	public com.bedbathandbeyond.atg.ProfileInfoResponseVODocument getProfileInfo(com.bedbathandbeyond.atg.ProfileInfoRequestVODocument profileInfoRequestVO);

	/**
	 * Auto generated method signature
	 * 
	 * @param profileRequestVO
	 */

	public com.bedbathandbeyond.atg.ProfileResponseVODocument createUser(com.bedbathandbeyond.atg.ProfileRequestVODocument profileRequestVO);

	/**
	 * Auto generated method signature
	 * 
	 * @param linkCoRegistrantRequestVO
	 */

	public com.bedbathandbeyond.atg.LinkCoRegistrantResponseVODocument linkCoRegistrant(com.bedbathandbeyond.atg.LinkCoRegistrantRequestVODocument linkCoRegistrantRequestVO);

	/**
	 * Auto generated method signature
	 * 
	 * @param loginRequestVO
	 */

	public com.bedbathandbeyond.atg.LoginResponseVODocument login(com.bedbathandbeyond.atg.LoginRequestVODocument loginRequestVO);

	/**
	 * Auto generated method signature
	 * 
	 * @param requestVO
	 */

	public com.bedbathandbeyond.atg.ResponseVODocument getResponseVO(com.bedbathandbeyond.atg.RequestVODocument requestVO);

	/**
	 * Auto generated method signature
	 * 
	 * @param billingAddressRequestVO
	 */

	public com.bedbathandbeyond.atg.BillingAddressResponseVODocument getBillingAddress(com.bedbathandbeyond.atg.BillingAddressRequestVODocument billingAddressRequestVO);

	/**
	 * Auto generated method signature
	 * 
	 * @param linkProfileRequestVO
	 */

	public com.bedbathandbeyond.atg.LinkProfileResponseVODocument linkProfileAndRegistry(com.bedbathandbeyond.atg.LinkProfileRequestVODocument linkProfileRequestVO);

	/**
	 * Auto generated method signature
	 * 
	 * @param forgotPasswordRequestVO
	 */

	public com.bedbathandbeyond.atg.ForgotPasswordResponseVODocument forgotPassword(com.bedbathandbeyond.atg.ForgotPasswordRequestVODocument forgotPasswordRequestVO);

}
