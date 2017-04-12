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
import com.bedbathandbeyond.www.GetOrderDetailDocument;
import com.bedbathandbeyond.www.GetOrderDetailDocument.GetOrderDetail;

/**
 * 
 * @author jsidhu
 * 
 *         Build request for Legacy order Detail
 * 
 */
public class OrderDetailsMarshaller extends RequestMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public XmlObject buildRequest(ServiceRequestIF reqVO)
			throws BBBBusinessException, BBBSystemException {
			logDebug("Inside OrderDetailMarshaller:buildRequest.....");
		BBBPerformanceMonitor.start("OrderDetailMarshaller-buildRequest");
		GetOrderDetailDocument getOrderDetailDocument = null;
		try {
			getOrderDetailDocument = GetOrderDetailDocument.Factory
					.newInstance();
			getOrderDetailDocument
					.setGetOrderDetail(getDozerMappedResponse(reqVO));
		} finally {
			BBBPerformanceMonitor.end("OrderDetailMarshaller-buildRequest");
		}
		return getOrderDetailDocument;

	}

	/**
	 * 
	 * @param reqVO
	 * @return
	 * @throws BBBSystemException
	 */
	private GetOrderDetail getDozerMappedResponse(ServiceRequestIF reqVO)
			throws BBBSystemException {
			logDebug("Inside OrderDetailMarshaller:dozerMap.....");
		BBBPerformanceMonitor
				.start("OrderDetailMarshaller-buildGetOrderDetail");

		GetOrderDetail getOrderDetail = GetOrderDetail.Factory.newInstance();

		DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try {
			mapper.map(reqVO, getOrderDetail);
		} catch (MappingException me) {
			logError(me);
			BBBPerformanceMonitor
			.end("OrderDetailMarshaller-buildGetOrderDetail");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1344,me.getMessage(), me);
		} catch (IllegalFormatException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("OrderDetailMarshaller-buildGetOrderDetail");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1344,e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			// usually this is a result of un-expected Enum value in
			// response
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("OrderDetailMarshaller-buildGetOrderDetail");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1344,e.getMessage(), e);
		} catch (SecurityException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("OrderDetailMarshaller-buildGetOrderDetail");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1344,e.getMessage(), e);
		} catch (ClassCastException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("OrderDetailMarshaller-buildGetOrderDetail");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1344,e.getMessage(), e);
		} finally {
			BBBPerformanceMonitor
					.end("OrderDetailMarshaller-buildGetOrderDetail");
		}
		return getOrderDetail;

	}
}
