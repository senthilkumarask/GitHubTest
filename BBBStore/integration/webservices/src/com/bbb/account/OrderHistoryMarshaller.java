package com.bbb.account;

import java.util.IllegalFormatException;

import org.apache.xmlbeans.XmlObject;
import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;

import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.RequestMarshaller;
import com.bedbathandbeyond.www.GetOrderHistoryDocument;
import com.bedbathandbeyond.www.GetOrderHistoryDocument.GetOrderHistory;

/**
 * 
 * @author jsidhu
 * 
 *         Build request for Legacy order history
 * 
 */
public class OrderHistoryMarshaller extends RequestMarshaller {

	/*
	 * Marshaller for the following example request <?xml version="1.0"
	 * encoding="utf-8"?> <soap:Envelope
	 * xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	 * xmlns:xsd="http://www.w3.org/2001/XMLSchema"
	 * xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"> <soap:Body>
	 * <GetOrderHistory xmlns="http://www.bedbathandbeyond.com/">
	 * <userToken>string</userToken> <siteFlag>string</siteFlag>
	 * <memberID>string</memberID> </GetOrderHistory> </soap:Body>
	 * </soap:Envelope>
	 */

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public XmlObject buildRequest(ServiceRequestIF reqVO)
			throws BBBBusinessException, BBBSystemException {
			logDebug("Inside OrderHistoryMarshaller:buildRequest.....");
		BBBPerformanceMonitor.start("OrderHistoryMarshaller-buildRequest");
		GetOrderHistoryDocument getOrderHistoryDocument = null;
		try {
			getOrderHistoryDocument = GetOrderHistoryDocument.Factory
					.newInstance();
			getOrderHistoryDocument
					.setGetOrderHistory(getDozerMappedResponse(reqVO));
		} finally {
			BBBPerformanceMonitor.end("OrderHistoryMarshaller-buildRequest");
		}
		return getOrderHistoryDocument;

	}

	/**
	 * 
	 * @param reqVO
	 * @return
	 * @throws BBBSystemException
	 */
	private GetOrderHistory getDozerMappedResponse(ServiceRequestIF reqVO)
			throws BBBSystemException {
			logDebug("Inside OrderHistoryMarshaller:dozerMap.....");
		BBBPerformanceMonitor
				.start("OrderHistoryMarshaller-buildGetOrderHistory");

		GetOrderHistory getOrderHistory = GetOrderHistory.Factory.newInstance();

		DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try {
			mapper.map(reqVO, getOrderHistory);
		} catch (MappingException me) {
			logError(me);
			BBBPerformanceMonitor
			.end("OrderHistoryMarshaller-buildGetOrderHistory");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1346,me.getMessage(), me);
		} catch (IllegalFormatException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("OrderHistoryMarshaller-buildGetOrderHistory");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1346,e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			// usually this is a result of un-expected Enum value in
			// response
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("OrderHistoryMarshaller-buildGetOrderHistory");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1346,e.getMessage(), e);
		} catch (SecurityException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("OrderHistoryMarshaller-buildGetOrderHistory");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1346,e.getMessage(), e);
		} catch (ClassCastException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("OrderHistoryMarshaller-buildGetOrderHistory");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1346,e.getMessage(), e);
		} finally {
			BBBPerformanceMonitor
					.end("OrderHistoryMarshaller-buildGetOrderHistory");
		}
		return getOrderHistory;

	}
}
