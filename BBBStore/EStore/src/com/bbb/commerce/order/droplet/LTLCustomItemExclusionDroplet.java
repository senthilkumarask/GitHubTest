/*
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  LTLCustomItemExclusionDroplet.java
 *
 *  DESCRIPTION: This class shows that the commerceitem is not an instance of AssemblyFee or DeliverySurcharge
 *
 *  HISTORY:
 *  JUne 25, 2014  Initial version
 */

package com.bbb.commerce.order.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.order.bean.LTLAssemblyFeeCommerceItem;
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem;
import com.bbb.utils.BBBUtility;


public class LTLCustomItemExclusionDroplet extends BBBDynamoServlet {

	private static final String OUTPUT = "output";


	/* The service method checks for items in order and 
	 *  returns list of ProductIds excluding LTL Custom Items 
	 *  in the order
	 * @see atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
	 */
	@Override
	public void service(DynamoHttpServletRequest req,
			DynamoHttpServletResponse res) throws ServletException, IOException {
		Order order = (Order) req.getObjectParameter(BBBCoreConstants.ORDER);
		// Changes made for BBBSL-3200 - Start
		String productIdList = "";

		if (order != null) {
	           @SuppressWarnings ("unchecked")
	           final List<CommerceItem> commerceItems = order.getCommerceItems();
	           
	           if(commerceItems!=null){
		           for (final CommerceItem commerceItem : commerceItems) {        	   
		        	   if(!(commerceItem instanceof LTLAssemblyFeeCommerceItem) && !(commerceItem instanceof LTLDeliveryChargeCommerceItem) ){
		        		   
		        		   String productId = commerceItem.getAuxiliaryData().getProductId();
		        		   String skuid = commerceItem.getCatalogRefId();
		        		   if(!BBBUtility.isEmpty(productIdList)){
		        			   productIdList += ",";
		        		   }
		        		   
		        		   productIdList += ";" + productId + ";;;;eVar30=" + skuid;
		        	   }
		           }
	           }
	       }
		// Changes made for BBBSL-3200 - End
		 req.setParameter(BBBCoreConstants.COMMERCE_ITEM_LIST, productIdList);
		 req.serviceLocalParameter(OUTPUT, req, res);
		
	}

}
