package com.bbb.framework.webservices.test.vo;

import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.XmlObject;

import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bbb.integration.interfaces.validateaddress.v1.validateaddress_xsd.ValidateAddressResponseDocument;
import com.bbb.integration.interfaces.validateaddress.v1.validateaddress_xsd.ValidateAddressResponseType;
import com.bedbathandbeyond.www.ArrayOfOrder;
import com.bedbathandbeyond.www.ArrayOfShipment;
import com.bedbathandbeyond.www.GetOrderHistoryResponseDocument;
import com.bedbathandbeyond.www.GetOrderHistoryResponseDocument.GetOrderHistoryResponse;
import com.bedbathandbeyond.www.Order;
import com.bedbathandbeyond.www.OrderDetailInfoReturn;
import com.bedbathandbeyond.www.OrderHistoryInfoReturn;
import com.bedbathandbeyond.www.OrderInfo;
import com.bedbathandbeyond.www.Shipment;
import com.bedbathandbeyond.www.Status;

/**
 * 
 * @author manohar
 *
 */
public class OrderHistoryUnMarshaller extends ResponseUnMarshaller{

	/*Unmarshaller having this as response as example
	<OrderHistoryInfoReturn xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns="http://www.bedbathandbeyond.com">
	<ErrorExists>false</ErrorExists>
	<orders>
	   <order>
	      <orderNumber>BBB615837931</orderNumber>
	      <orderDate>11/15/2011</orderDate>
	      <status>Order being processed.</status>
	      <shipments/>
	   </order>
	   <order>
	      <orderNumber>BBB548729067</orderNumber>
	      <orderDate>10/15/2010</orderDate>
	      <status>Delivered</status>
	      <shipments>
	         <shipment>
	            <carrier>UPS</carrier>
	            <trackingNumber>1ZE57W901271001234</trackingNumber>
	            <shippingDate>10/24/2010</shippingDate>
	            <deliveryDate>10/26/2010</deliveryDate>
	         </shipment>
	      </shipments>
	   </order>
	</orders>
	</OrderHistoryInfoReturn>*/
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public ServiceResponseIF processResponse(final XmlObject responseDocument)
			throws BBBSystemException {

		BBBPerformanceMonitor.start("OrderHistoryUnMarshaller-processResponse");

		GetOrderHistoryResVO getOrderHistoryResVO = new GetOrderHistoryResVO();

		if (responseDocument != null) {
			try {
				GetOrderHistoryResponseDocument getOrderHistoryResponseDocument = (GetOrderHistoryResponseDocument) responseDocument;
				GetOrderHistoryResponse getOrderHistoryResponse = getOrderHistoryResponseDocument
						.getGetOrderHistoryResponse();
				OrderHistoryInfoReturn orderHistoryInfoReturn = getOrderHistoryResponse
						.getGetOrderHistoryResult();
				Status status = orderHistoryInfoReturn.getStatus();
				if (null != status && status.getErrorExists()) {
					ArrayOfOrder arrayOfOrder = orderHistoryInfoReturn
							.getOrders();
					if(null != arrayOfOrder){
						Order[] orders = arrayOfOrder.getOrderArray();
						if( null != orders && orders.length > 0){
							List<OrderVO> orderVOs = new ArrayList<OrderVO>(
									orders.length);
							for (Order order : orders) {
								OrderVO orderVO = new OrderVO();
								copyOrder(order, orderVO);
								orderVOs.add(orderVO);
							}
							getOrderHistoryResVO.setOrderList(orderVOs);
						}
					}
				}
			} catch (Exception e) {
				throw new BBBSystemException(e.getMessage(), e.getCause());
			}
		}

		BBBPerformanceMonitor.end("OrderHistoryUnMarshaller-processResponse");
		return getOrderHistoryResVO;
	}
	
	public void copyOrder(Order order, OrderVO orderVO) {

		orderVO.setOrderDate(order.getOrderDate());
		orderVO.setOrderNumber(order.getOrderNumber());
		orderVO.setStatus(order.getOrderStatus());
		ArrayOfShipment arrayOfShipment = order.getShipments();
		if(null != arrayOfShipment){
			Shipment[] shipments = arrayOfShipment.getShipmentArray();
			if(null != shipments && shipments.length > 0){
				List<ShipmentVO> shipmentVOs = new ArrayList<ShipmentVO>(
						shipments.length);
				for (Shipment shipment : shipments) {
					ShipmentVO shipmentVO = new ShipmentVO();
					copyShipment(shipment, shipmentVO);
					shipmentVOs.add(shipmentVO);
				}
				orderVO.setShipments(shipmentVOs);
			}
		}
	}
	
	public void copyShipment(Shipment shipment, ShipmentVO shipmentVO) {
		shipmentVO.setCarrier(shipment.getCarrier());
		shipmentVO.setDeliveryDate(shipment.getDeliveryDate());
		shipmentVO.setShippingDate(shipment.getShippingDate());
		shipmentVO.setTrackingNum(shipment.getTrackingNumber());
	}
	
}
