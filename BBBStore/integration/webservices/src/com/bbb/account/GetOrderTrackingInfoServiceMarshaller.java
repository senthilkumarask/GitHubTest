/*
 *
 * File  : GetOrderTrackingInfoServiceMarshaller.java 
 * 
 * Project:     BBB
 * 
 * Description: Request Marshaller class for Order Tracking Web Service
 * 
 * HISTORY:
 * Initial Version: 12/07/2011
 * 
 * 
 */

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
import com.bedbathandbeyond.www.GetOrderTrackingInfoDocument;
import com.bedbathandbeyond.www.GetOrderTrackingInfoDocument.GetOrderTrackingInfo;

/**
 * 
 * This class contain methods used for marshalling the webservice request.
 * 
 * @author Lokesh Duseja
 * 
 */
public class GetOrderTrackingInfoServiceMarshaller extends RequestMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * This method is used to build a request.
	 */
	public XmlObject buildRequest(ServiceRequestIF pRreqVO)
			throws BBBBusinessException, BBBSystemException {

		BBBPerformanceMonitor
				.start("ServiceMarshaller-buildRequest");

		GetOrderTrackingInfoDocument getOrderTrackingInfoDocument = null;
		try
		{
			getOrderTrackingInfoDocument = GetOrderTrackingInfoDocument.Factory.newInstance();
			getOrderTrackingInfoDocument.setGetOrderTrackingInfo(getDozerMappedRequest(pRreqVO));
	
		}
		finally
		{
			BBBPerformanceMonitor
			.end("GetOrderTrackingInfoServiceMarshaller-buildRequest");
		}
		return getOrderTrackingInfoDocument;
	}

	/**
	 * This methos is used to map a page specific VO to web service VO.
	 * @throws BBBSystemException 
	 * 
*/
	private GetOrderTrackingInfo getDozerMappedRequest(ServiceRequestIF pReqVO) throws BBBSystemException {

		BBBPerformanceMonitor
				.start("GetOrderTrackingInfoServiceMarshaller-buildValidateAddressType");

		GetOrderTrackingInfo getOrderTrackingInfo = GetOrderTrackingInfo.Factory.newInstance();
		DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try
		{
			mapper.map(pReqVO, getOrderTrackingInfo);
		}
		catch(MappingException me)
		{
			logError(me.getMessage(), me);
			BBBPerformanceMonitor
			.end("GetOrderTrackingInfoServiceMarshaller-buildValidateAddressType");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1342,
					me.getMessage(), me);
		}
		catch (IllegalFormatException e )
		{
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("GetOrderTrackingInfoServiceMarshaller-buildValidateAddressType");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1342,e.getMessage(), e);
		}
		catch (IllegalArgumentException e) {
			// usually this is a result of un-expected Enum value in response
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("GetOrderTrackingInfoServiceMarshaller-buildValidateAddressType");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1342,e.getMessage(), e);
		} catch (SecurityException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("GetOrderTrackingInfoServiceMarshaller-buildValidateAddressType");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1342,e.getMessage(), e);
		}
		catch(ClassCastException e)
		{
			logError(e.getMessage(), e);
			BBBPerformanceMonitor
			.end("GetOrderTrackingInfoServiceMarshaller-buildValidateAddressType");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1342,e.getMessage(), e);
		}
		finally
		{
			BBBPerformanceMonitor
				.end("GetOrderTrackingInfoServiceMarshaller-buildValidateAddressType");
		}
		return getOrderTrackingInfo;
	}


}