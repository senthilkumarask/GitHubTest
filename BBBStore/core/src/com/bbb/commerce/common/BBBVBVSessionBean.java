/**
 * 
 */
package com.bbb.commerce.common;

import com.bbb.commerce.checkout.vbv.vo.BBBVerifiedByVisaVO;
import com.bbb.common.BBBGenericService;


/**
 * @author ngup50
 * This component is session scope.
 * Used to store BBBVerifiedByVisaVO in session for Verified By Visa functionality.
 */
public class BBBVBVSessionBean extends BBBGenericService{
	
	private BBBVerifiedByVisaVO bBBVerifiedByVisaVO;

	/**
	 * @return the bBBVerifiedByVisaVO
	 */
	public BBBVerifiedByVisaVO getbBBVerifiedByVisaVO() {
		return bBBVerifiedByVisaVO;
	}

	/**
	 * @param bBBVerifiedByVisaVO the bBBVerifiedByVisaVO to set
	 */
	public void setbBBVerifiedByVisaVO(BBBVerifiedByVisaVO bBBVerifiedByVisaVO) {
		this.bBBVerifiedByVisaVO = bBBVerifiedByVisaVO;
	}

}
