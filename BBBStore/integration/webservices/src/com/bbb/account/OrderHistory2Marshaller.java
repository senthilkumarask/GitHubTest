package com.bbb.account;

import java.util.IllegalFormatException;

import org.apache.xmlbeans.XmlObject;
import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;

import com.bbb.account.vo.order.OrderHistory2ReqVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.RequestMarshaller;

import com.bedbathandbeyond.www.GetOrderHistory2Document;
import com.bedbathandbeyond.www.GetOrderHistory2Document.GetOrderHistory2;
/**
 * 
 * @author dwaghmare
 * 
 *         Build request for Legacy filtered order history
 * 
 */
public class OrderHistory2Marshaller extends RequestMarshaller {

	/*
    Marshaller for the following example request <?xml version="1.0" encoding="utf-8"?>
		<soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
		  <soap:Body>
		    <GetOrderHistory2 xmlns="http://www.bedbathandbeyond.com/">
		      <userToken>string</userToken>
		      <siteFlag>string</siteFlag>
		      <mbrNum>string</mbrNum>
		      <atgProfileId>string</atgProfileId>
		      <filterOptions>string</filterOptions>
		    </GetOrderHistory2>
		  </soap:Body>
		</soap:Envelope>

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public XmlObject buildRequest(ServiceRequestIF reqVO)
			throws BBBBusinessException, BBBSystemException {
			logDebug("Inside OrderHistory2Marshaller:buildRequest.....");
		BBBPerformanceMonitor.start("OrderHistory2Marshaller-buildRequest");
		GetOrderHistory2Document getOrderHistory2Document = null;
		try {
			getOrderHistory2Document = GetOrderHistory2Document.Factory
					.newInstance();
			getOrderHistory2Document
					.setGetOrderHistory2(getDozerMappedResponse(reqVO));
		} finally {
			BBBPerformanceMonitor.end("OrderHistory2Marshaller-buildRequest");
		}
		return getOrderHistory2Document;

	}

	/**
	 * 
	 * @param reqVO
	 * @return
	 * @throws BBBSystemException
	 */
	private GetOrderHistory2 getDozerMappedResponse(ServiceRequestIF reqVO)
			throws BBBSystemException {
			logDebug("Inside OrderHistory2Marshaller:dozerMap.....");
		BBBPerformanceMonitor
				.start("OrderHistory2Marshaller-buildGetOrderHistory");

		GetOrderHistory2 getOrderHistory2 = GetOrderHistory2.Factory.newInstance();

		DozerBeanMapper mapper = getDozerBean().getDozerMapper();
			getOrderHistory2.setAtgProfileId(((OrderHistory2ReqVO)reqVO).getProfileId());
			getOrderHistory2.setFilterOptions(((OrderHistory2ReqVO)reqVO).getOrderType());
			getOrderHistory2.setMbrNum(((OrderHistory2ReqVO)reqVO).getMbrNum());
			getOrderHistory2.setSiteFlag(((OrderHistory2ReqVO)reqVO).getSiteFlag());
			getOrderHistory2.setUserToken(((OrderHistory2ReqVO)reqVO).getUserToken());
			//mapper.map(reqVO, getOrderHistory2);
			BBBPerformanceMonitor
			.end("OrderHistory2Marshaller-buildGetOrderHistory");
		return getOrderHistory2;

	}
}
