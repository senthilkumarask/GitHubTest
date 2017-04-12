package com.bbb.framework.webservices.test.vo;

import org.apache.xmlbeans.XmlObject;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.RequestMarshaller;
import com.bedbathandbeyond.www.GetOrderHistoryDocument;
import com.bedbathandbeyond.www.GetOrderHistoryDocument.GetOrderHistory;

/**
 * 
 * @author manohar
 *
 */
public class OrderHistoryMarshaller extends RequestMarshaller{

	/*Marshaller for the following example request
	<?xml version="1.0" encoding="utf-8"?>
	<soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
	        <soap:Body>
	                <GetOrderHistory xmlns="http://www.bedbathandbeyond.com/">
	                        <userToken>string</userToken>
	                        <siteFlag>string</siteFlag>
	                        <memberID>string</memberID>
	                </GetOrderHistory>
	        </soap:Body>
	</soap:Envelope>*/
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public XmlObject buildRequest(ServiceRequestIF reqVO) throws BBBBusinessException, BBBSystemException {

		BBBPerformanceMonitor
		.start("OrderHistoryMarshaller-buildRequest");
	
		GetOrderHistoryDocument getOrderHistoryDocument = null;
		try{
			getOrderHistoryDocument = GetOrderHistoryDocument.Factory.newInstance();
			getOrderHistoryDocument.setGetOrderHistory(buildGetOrderHistory((GetOrderHistoryReqVO) reqVO));
		}catch (Exception e) {
			throw new BBBSystemException(e.getMessage(),e.getCause());
		}
		
		BBBPerformanceMonitor
		.end("OrderHistoryMarshaller-buildRequest");
	
		return getOrderHistoryDocument;
	}

	/**
	 * 
	 * @param reqVO
	 * @return
	 */
	private GetOrderHistory buildGetOrderHistory(GetOrderHistoryReqVO reqVO){
		
		BBBPerformanceMonitor
		.start("OrderHistoryMarshaller-buildGetOrderHistory");
		
		GetOrderHistory getOrderHistory = GetOrderHistory.Factory.newInstance();
		OrderHistoryVO orderHistory = reqVO.getOrderHistory();
		//getOrderHistory.setMemberID(orderHistory.getMemberID());
		getOrderHistory.setMbrNum(orderHistory.getMbrNum());
		getOrderHistory.setSiteFlag(orderHistory.getSiteFlag());
		getOrderHistory.setUserToken(orderHistory.getUserToken());
		
		BBBPerformanceMonitor
		.end("OrderHistoryMarshaller-buildGetOrderHistory");
		
		return getOrderHistory;
		
	}
}
