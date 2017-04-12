package com.bbb.account;

import java.util.IllegalFormatException;

import org.apache.xmlbeans.XmlObject;
import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;

import com.bbb.account.vo.order.GetOrderHistoryResVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bedbathandbeyond.www.GetOrderHistoryResponseDocument;
import com.bedbathandbeyond.www.GetOrderHistoryResponseDocument.GetOrderHistoryResponse;
import com.bedbathandbeyond.www.OrderHistoryInfoReturn;

/**
 * 
 * @author jsidhu
 * 
 * Parse response of legacy order webservice
 */
public class OrderHistoryUnMarshaller extends ResponseUnMarshaller {

	/*
	 * Unmarshaller having this as response as example <OrderHistoryInfoReturn
	 * xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	 * xmlns:xsd="http://www.w3.org/2001/XMLSchema"
	 * xmlns="http://www.bedbathandbeyond.com"> <ErrorExists>false</ErrorExists>
	 * <orders> <order> <orderNumber>BBB615837931</orderNumber>
	 * <orderDate>11/15/2011</orderDate> <status>Order being processed.</status>
	 * <shipments/> </order> <order> <orderNumber>BBB548729067</orderNumber>
	 * <orderDate>10/15/2010</orderDate> <status>Delivered</status> <shipments>
	 * <shipment> <carrier>UPS</carrier>
	 * <trackingNumber>1ZE57W901271001234</trackingNumber>
	 * <shippingDate>10/24/2010</shippingDate>
	 * <deliveryDate>10/26/2010</deliveryDate> </shipment> </shipments> </order>
	 * </orders> </OrderHistoryInfoReturn>
	 */
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServiceResponseIF processResponse(final XmlObject responseDocument)
			throws BBBSystemException {
			logDebug("Inside OrderHistoryUnMarshaller:processResponse. Response Document is "
					+ responseDocument.toString());
		BBBPerformanceMonitor
				.start("OrderHistoryUnMarshaller-processResponse");

		GetOrderHistoryResVO orderHistoryResVO = new GetOrderHistoryResVO();
		
			final GetOrderHistoryResponseDocument orderHistoryResDoc = (GetOrderHistoryResponseDocument) responseDocument;
				final GetOrderHistoryResponse orderHistoryRes = orderHistoryResDoc
						.getGetOrderHistoryResponse();

				if (orderHistoryRes != null) {
					final OrderHistoryInfoReturn orderHistoryInfo = orderHistoryRes
							.getGetOrderHistoryResult();

					//if (!orderHistoryInfoReturn.getStatus().getErrorExists()) {
						orderHistoryResVO = (GetOrderHistoryResVO) getDozerMappedResponse(orderHistoryInfo);
					//}
				}
				else{
					orderHistoryResVO.setWebServiceError(true);
				}

		BBBPerformanceMonitor.end("OrderHistoryUnMarshaller-processResponse");
		return orderHistoryResVO;
	}

	private ServiceResponseIF getDozerMappedResponse(
			final OrderHistoryInfoReturn pOrderHistoryInfo)
			throws BBBSystemException {
			logDebug("Inside OrderHistoryUnMarshaller:dozerMap.....");
		BBBPerformanceMonitor.start("OrderHistoryUnMarshaller-dozerMap");

		final GetOrderHistoryResVO orderHistoryResVO = new GetOrderHistoryResVO();		

		final DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try {
			mapper.map(pOrderHistoryInfo, orderHistoryResVO);
		} catch (MappingException me) {
			logError(me);
			BBBPerformanceMonitor.end("OrderHistoryUnMarshaller-dozerMap");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1347,me.getMessage(), me);
		}
		catch (IllegalFormatException e )
		{
			logError(e.getMessage(), e);
			BBBPerformanceMonitor.end("OrderHistoryUnMarshaller-dozerMap");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1347,e.getMessage(), e);
		}
		catch (IllegalArgumentException e) {
			// usually this is a result of un-expected Enum value in response
			logError(e.getMessage(), e);
			BBBPerformanceMonitor.end("OrderHistoryUnMarshaller-dozerMap");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1347,e.getMessage(), e);
		} catch (SecurityException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor.end("OrderHistoryUnMarshaller-dozerMap");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1347,e.getMessage(), e);
		}
		catch(ClassCastException e)
		{
			logError(e.getMessage(), e);
			BBBPerformanceMonitor.end("OrderHistoryUnMarshaller-dozerMap");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1347,e.getMessage(), e);
		}

		BBBPerformanceMonitor.end("OrderHistoryUnMarshaller-dozerMap");

		return orderHistoryResVO;
	}

}
