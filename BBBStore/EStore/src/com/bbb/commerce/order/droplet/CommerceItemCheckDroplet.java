//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Reproduction or use of this file without explicit 
//written consent is prohibited.
//
//Created by: Sunil Dandriyal
//
//Created on: 02-December-2011
//--------------------------------------------------------------------------------

package com.bbb.commerce.order.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceItem;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.common.BBBDynamoServlet;

import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.order.bean.BBBCommerceItem;

/**
 * This Droplet checks if the commerce item is BBBCommerceItem
 */
public class CommerceItemCheckDroplet extends BBBDynamoServlet {
	
	  public final static ParameterName TRUE  = ParameterName.getParameterName("true");
	  public final static ParameterName FALSE = ParameterName.getParameterName("false");

	/**
	 * This method performs checks for any gift wrap eligible commerce Item in
	 * the passed Shipping group.
	 * 
	 * @param DynamoHttpServletRequest
	 *            , DynamoHttpServletResponse
	 * @return void
	 * @throws ServletException
	 *             , IOException
	 */
	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		
		logDebug("START: CommerceItemCheckDroplet.service");
		
		// multi shipping groups - Gift Wrap
		final CommerceItem commerceItem = (CommerceItem) pRequest.getObjectParameter(BBBCheckoutConstants.COMMERCE_ITEM);
		if(commerceItem != null){
			if(commerceItem instanceof BBBCommerceItem){
				pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
			} else {
				pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
			}
		} else {
			pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
		}
		
		
		logDebug("END: CommerceItemCheckDroplet.service");
		
	}
}