//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Reproduction or use of this file without explicit 
//written consent is prohibited.
//
//Created by: Harsh Kapoor
//
//Created on: 28-December-2011
//--------------------------------------------------------------------------------

package com.bbb.commerce.checkout.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.common.BBBDynamoServlet;

import com.bbb.constants.BBBCoreConstants;

public class BBBCreditCardDisplayDroplet extends BBBDynamoServlet {

	private static final String FIFTEEN_DIGIT_CREDIT_CARD_MASK = "***** ****** ";
	private static final String SIXTEEN_DIGIT_CREDIT_CARD_MASK = "**** **** **** ";
	private static final String MASKED_CREDIT_CARD_NO = "maskedCreditCardNo";

	/**
	 * This method takes creditCardNo as input parameter type and sets
	 * maskedCreditCardNo
	 * 
	 * @param DynamoHttpServletRequest
	 *            , DynamoHttpServletResponse
	 * @return void
	 * @throws ServletException
	 *             , IOException
	 */
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
	    String creditCardNo = (String) pRequest
                .getObjectParameter("creditCardNo");
		if (null != creditCardNo) {
			
			int creditCardNolength = creditCardNo.length();
			String lastFourDigits = creditCardNo.substring(
					creditCardNolength - 4, creditCardNolength);

			if (creditCardNolength == 16) {
				pRequest.setParameter(MASKED_CREDIT_CARD_NO,
						SIXTEEN_DIGIT_CREDIT_CARD_MASK + lastFourDigits);
			} else if (creditCardNolength == 15) {
				pRequest.setParameter(MASKED_CREDIT_CARD_NO,
						FIFTEEN_DIGIT_CREDIT_CARD_MASK + lastFourDigits);
			}
		}

		pRequest.serviceParameter(BBBCoreConstants.OPARAM, pRequest, pResponse);

	}
}