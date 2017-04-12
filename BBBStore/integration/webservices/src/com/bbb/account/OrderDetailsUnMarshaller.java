package com.bbb.account;

import java.util.IllegalFormatException;

import org.apache.xmlbeans.XmlObject;
import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;

import com.bbb.account.vo.order.OrderDetailInfoReturn;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bedbathandbeyond.www.GetOrderDetailResponseDocument;
import com.bedbathandbeyond.www.GetOrderDetailResponseDocument.GetOrderDetailResponse;

/**
 * 
 * @author jsidhu
 * 
 * Parse response of legacy order details webservice
 */
public class OrderDetailsUnMarshaller extends ResponseUnMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServiceResponseIF processResponse(final XmlObject responseDocument)
			throws BBBSystemException {
			logDebug("Inside OrderDetailUnMarshaller:processResponse. Response Document is "
					+ responseDocument.toString());
		BBBPerformanceMonitor
				.start("OrderDetailUnMarshaller-processResponse");

		OrderDetailInfoReturn orderDetailsResVO = new OrderDetailInfoReturn();

			final GetOrderDetailResponseDocument orderDetailsResDoc = (GetOrderDetailResponseDocument) responseDocument;
			final GetOrderDetailResponse orderDetailsRes = orderDetailsResDoc
						.getGetOrderDetailResponse();

			if (orderDetailsRes != null) {
					final com.bedbathandbeyond.www.OrderDetailInfoReturn orderDetailInfoReturn = orderDetailsRes
							.getGetOrderDetailResult();
					//if (!orderDetailInfoReturn.getStatus().getErrorExists()) {
					orderDetailsResVO = (OrderDetailInfoReturn) getDozerMappedResponse( orderDetailInfoReturn);
					//}
			}
			else{
				orderDetailsResVO.setWebServiceError(true);
			}
				

		BBBPerformanceMonitor.end("OrderDetailUnMarshaller-processResponse");
		return orderDetailsResVO;
	}

	private ServiceResponseIF getDozerMappedResponse(
			final com.bedbathandbeyond.www.OrderDetailInfoReturn porderDetailInfoReturn)
			throws BBBSystemException {
			logDebug("Inside OrderDetailUnMarshaller:dozerMap.....");
		BBBPerformanceMonitor.start("OrderDetailUnMarshaller-dozerMap");
		final OrderDetailInfoReturn orderDetailResVO = new OrderDetailInfoReturn();				

		final DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try {
			mapper.map(porderDetailInfoReturn, orderDetailResVO);
		} catch (MappingException me) {
			logError(me);
			BBBPerformanceMonitor.end("OrderDetailUnMarshaller-dozerMap");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1345,me.getMessage(), me);
		}
		catch (IllegalFormatException e )
		{
			logError(e.getMessage(), e);
			BBBPerformanceMonitor.end("OrderDetailUnMarshaller-dozerMap");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1345,e.getMessage(), e);
		}
		catch (IllegalArgumentException e) {
			// usually this is a result of un-expected Enum value in response
			logError(e.getMessage(), e);
			BBBPerformanceMonitor.end("OrderDetailUnMarshaller-dozerMap");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1345,e.getMessage(), e);
		} catch (SecurityException e) {
			logError(e.getMessage(), e);
			BBBPerformanceMonitor.end("OrderDetailUnMarshaller-dozerMap");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1345,e.getMessage(), e);
		}
		catch(ClassCastException e)
		{
			logError(e.getMessage(), e);
			BBBPerformanceMonitor.end("OrderDetailUnMarshaller-dozerMap");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1345,e.getMessage(), e);
		}

		BBBPerformanceMonitor.end("OrderDetailUnMarshaller-dozerMap");

		return orderDetailResVO;
	}

}
