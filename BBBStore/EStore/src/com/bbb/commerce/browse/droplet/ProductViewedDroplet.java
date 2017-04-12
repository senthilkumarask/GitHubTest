package com.bbb.commerce.browse.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

/**
 * Droplet to add last viewed product Ids in sessionBean
 * The droplet should be called on all product pages
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 * Reproduction or use of this file without explicit written consent is prohibited.
 * Created by: njai13
 * Created on: November-2011
 * @author njai13
 *
 */
public class ProductViewedDroplet extends BBBDynamoServlet {
	private int limitNoOfLV;



	public int getLimitNoOfLV() {
		return limitNoOfLV;
	}



	public void setLimitNoOfLV(int limitNoOfLV) {
		this.limitNoOfLV = limitNoOfLV;
	}



	/**
	 * The method adds any new product viewed by user in the sessionBean
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void service(DynamoHttpServletRequest request,
			DynamoHttpServletResponse response)
					throws ServletException, IOException
					{
		String productId=request.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER);
		String methodName = "ProductViewedDroplet_service";
        BBBPerformanceMonitor.start(methodName );
		BBBSessionBean sessionBean =
				(BBBSessionBean)request.resolveName("/com/bbb/profile/session/SessionBean");

		HashMap  sessionMap=sessionBean.getValues();
		List <String>lastViewedItemsListInSession=new ArrayList<String>();
		if(!BBBUtility.isListEmpty((List<String>)sessionMap.get(BBBSearchBrowseConstants.LAST_VIEWED_PRODUCT_ID_LIST))){
			lastViewedItemsListInSession.addAll((List<String>)sessionMap.get(BBBSearchBrowseConstants.LAST_VIEWED_PRODUCT_ID_LIST));
		}
		if(productId!=null){
			//if list of last viewed product ids already exists add new product id in that 
			if(!BBBUtility.isListEmpty(lastViewedItemsListInSession)){
				if(lastViewedItemsListInSession.contains(productId)){
					//if product id already exists remove and again add to update its position in the list
					lastViewedItemsListInSession.remove(productId);
					logDebug("productId "+productId+" already exists in last viewed list");
				}
				lastViewedItemsListInSession.add(productId);
				logDebug("adding productId "+productId+" in already existing list");
				if(lastViewedItemsListInSession.size()>=getLimitNoOfLV()){
					int listSize=lastViewedItemsListInSession.size();
					int indexOfLVPrdtInLimit=listSize-getLimitNoOfLV();
				
					StringBuffer debug=new StringBuffer(50);
					debug.append("no of last viewed has exceeded limit of  ").append(getLimitNoOfLV()).append(" adding sublist of last viewed items from starting index ")
					.append(indexOfLVPrdtInLimit).append(" to last index ").append(listSize-1).append(" value in last viewed items is ").append(lastViewedItemsListInSession.subList(indexOfLVPrdtInLimit, listSize));
					logDebug(debug.toString());
				
					sessionBean.getValues().put(BBBSearchBrowseConstants.LAST_VIEWED_PRODUCT_ID_LIST, lastViewedItemsListInSession.subList(indexOfLVPrdtInLimit, listSize));
				}
				else{
					logDebug("no of last viewed is within limit adding all items");
					sessionBean.getValues().put(BBBSearchBrowseConstants.LAST_VIEWED_PRODUCT_ID_LIST, lastViewedItemsListInSession);
				}
			}
			//if session is new and no last viewed product list exists create new list and add  product id in it
			else{
				final List <String>lastViewedItemsList=new ArrayList<String>();
				lastViewedItemsList.add(productId);
				logDebug("new session so adding productId "+productId+" in new list");
				sessionBean.getValues().put(BBBSearchBrowseConstants.LAST_VIEWED_PRODUCT_ID_LIST, lastViewedItemsList);
			}
		}
		BBBPerformanceMonitor.end(methodName);
	}
	
}
