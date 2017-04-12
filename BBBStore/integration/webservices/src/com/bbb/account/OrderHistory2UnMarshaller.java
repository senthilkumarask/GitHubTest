package com.bbb.account;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.IllegalFormatException;
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;

import com.bbb.account.vo.order.GetOrderHistory2ResVO;
import com.bbb.account.vo.order.GetOrderHistoryResVO;
import com.bbb.account.vo.order.OrderHistory2ResVO;
import com.bbb.account.vo.order.ShipmentTrackingInfoVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bbb.framework.webservices.vo.ErrorStatus;
import com.bbb.framework.webservices.vo.ValidationError;
import com.bedbathandbeyond.www.Order2;
import com.bedbathandbeyond.www.OrderHistoryInfoReturn;
import com.bedbathandbeyond.www.GetOrderHistory2ResponseDocument;
import com.bedbathandbeyond.www.GetOrderHistory2ResponseDocument.GetOrderHistory2Response;
import com.bedbathandbeyond.www.OrderHistoryInfoReturn2;
import com.bedbathandbeyond.www.Shipment;
import com.bedbathandbeyond.www.ValError;
/**
 * 
 * @author jsidhu
 * 
 * Parse response of legacy order webservice
 */
public class OrderHistory2UnMarshaller extends ResponseUnMarshaller {

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

		GetOrderHistory2ResVO orderHistory2ResVO = new GetOrderHistory2ResVO();
		
			final GetOrderHistory2ResponseDocument orderHistory2ResDoc = (GetOrderHistory2ResponseDocument) responseDocument;
				final GetOrderHistory2Response orderHistory2Res = orderHistory2ResDoc
						.getGetOrderHistory2Response();

				if (orderHistory2Res != null) {
					final OrderHistoryInfoReturn2 orderHistory2Info =  orderHistory2Res.getGetOrderHistory2Result();
						orderHistory2ResVO = (GetOrderHistory2ResVO) getDozerMappedResponse(orderHistory2Info);
				}
				else{
					orderHistory2ResVO.setWebServiceError(true);
				}

		BBBPerformanceMonitor.end("OrderHistoryUnMarshaller-processResponse");
		return orderHistory2ResVO;
	}

	private ServiceResponseIF getDozerMappedResponse(
			final OrderHistoryInfoReturn2 pOrderHistory2Info)
			throws BBBSystemException {
			logDebug("Inside OrderHistoryUnMarshaller:dozerMap.....");
		BBBPerformanceMonitor.start("OrderHistoryUnMarshaller-dozerMap");
		List<OrderHistory2ResVO> orderList = new ArrayList<OrderHistory2ResVO>();
		
		final GetOrderHistory2ResVO getOrderHistory2ResVO = new GetOrderHistory2ResVO();		
		
		final DozerBeanMapper mapper = getDozerBean().getDozerMapper();
	//	try {
			
			//mapper.map(pOrderHistory2Info, orderHistory2ResVO);
			com.bedbathandbeyond.www.ArrayOfOrder2 orders = pOrderHistory2Info.getOrders();
			if(orders!=null){
			Order2[] orders2= orders.getOrder2Array();
			for(Order2 order:orders2){
				OrderHistory2ResVO orderHistory2VO=new OrderHistory2ResVO();
				List<ShipmentTrackingInfoVO> shipmentTrackInfo=new ArrayList<ShipmentTrackingInfoVO>();
				DateFormat formatter ; 
				Date date ;
				   formatter = new SimpleDateFormat("MM/dd/yyyy");
				try{ 
				orderHistory2VO.setOrderDate(formatter.parse(order.getOrderDate()));
					} catch (ParseException e){
					orderHistory2VO.setOrderDate(null);
				  }
				orderHistory2VO.setOnlineOrderNumber(order.getOrderNumber());
				orderHistory2VO.setOrderNumber(order.getOrderNumber());
				orderHistory2VO.setOrderStatus(order.getOrderStatus());
				orderHistory2VO.setTotalAmt(order.getTotalAmt());
				orderHistory2VO.setWsOrder(true);
				com.bedbathandbeyond.www.ArrayOfShipment shipmentArray = order.getShipments();
				Shipment[] shipments= shipmentArray.getShipmentArray();
				for(int i=0; i<shipments.length;i++){
					ShipmentTrackingInfoVO shipmentTrackingInfoVO=new ShipmentTrackingInfoVO();
					shipmentTrackingInfoVO.setDeliveryDate(shipments[i].getDeliveryDate());
					shipmentTrackingInfoVO.setTrackingNumber(shipments[i].getTrackingNumber());
					shipmentTrackingInfoVO.setShippingDate(shipments[i].getShippingDate());
					shipmentTrackingInfoVO.setCarrier(shipments[i].getCarrier());
					shipmentTrackInfo.add(shipmentTrackingInfoVO);
				}
				orderList.add(orderHistory2VO);
			}
			getOrderHistory2ResVO.setOrderList(orderList);
			}
			ErrorStatus errorStatus=new ErrorStatus();
			if(pOrderHistory2Info.getStatus()!=null){
			errorStatus.setErrorExists(pOrderHistory2Info.getStatus().getErrorExists());
			errorStatus.setErrorId(pOrderHistory2Info.getStatus().getID());
			errorStatus.setDisplayMessage(pOrderHistory2Info.getStatus().getDisplayMessage());
			errorStatus.setErrorMessage(pOrderHistory2Info.getStatus().getErrorMessage());
			getOrderHistory2ResVO.setErrorStatus(errorStatus);
			}
			

		BBBPerformanceMonitor.end("OrderHistory2UnMarshaller-dozerMap");

		return getOrderHistory2ResVO;
	}

}
