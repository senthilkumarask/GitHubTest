package com.bbb.commerce.order.feeds;

import java.util.List;

import atg.repository.MutableRepository;
import atg.repository.RepositoryItem;

import com.bbb.constants.BBBCoreConstants;
import com.sapient.common.tests.BaseTestCase;
import com.sun.jms.TextMessageImpl;

public class TestOrderStatusUpdateManager extends BaseTestCase{



	@SuppressWarnings("rawtypes")
    public void testUpdateOrderStatus() throws Exception
	{
		OrderStatusUpdateManager orderStatusUpdateManager = (OrderStatusUpdateManager) getObject("orderStatusUpdateManager");
		orderStatusUpdateManager.setLoggingDebug(true);
		TextMessageImpl txtMessage=new TextMessageImpl();
		txtMessage.setText("<Orders><Order_Attributes><Order_Number>BBB5000000002390006</Order_Number><Order_State>SOURCING FAILED</Order_State><State_Description>SOURCING FAILED</State_Description><Status_Change_Date>10/11/2011</Status_Change_Date><reason>Order is shipped</reason><Shipping_Group><Shipment_Group_Id>DC1sg2390006</Shipment_Group_Id><Shipment_State>SOURCING FAILED</Shipment_State><State_Description>SOURCING FAILED</State_Description><Shipment_Tracking><Tracking_Number>1Z23A48565754</Tracking_Number><Carrier_Code>UPS</Carrier_Code><URL>UPS.com</URL><actual_ship_date>10/11/2011</actual_ship_date></Shipment_Tracking></Shipping_Group></Order_Attributes></Orders>");
		orderStatusUpdateManager.updateOrderStatus(txtMessage.getText());
		MutableRepository orderRepository=orderStatusUpdateManager.getOrderRepository();
		//System.out.println("orderRepository "+orderRepository);
		RepositoryItem orderItem =orderRepository.getItem("5000000002390006",BBBCoreConstants.ORDER_ITEM_DESCRIPTOR);
		String orderState=(String) orderItem.getPropertyValue(BBBCoreConstants.STATE_ORDER_PROPERTY_NAME);
		System.out.println("orderState  "+orderState);
		assertEquals("SOURCING FAILED", orderState);
		List sGs= ((List)orderItem.getPropertyValue(BBBCoreConstants.SHIPPING_GROUPS_ORDER_PROPERTY_NAME));
		assertTrue("no shipping groups", sGs.size() == 1);
		assertEquals("INITIAL", ((RepositoryItem)sGs.get(0)).getPropertyValue(BBBCoreConstants.STATE_ORDER_PROPERTY_NAME));
		
		orderStatusUpdateManager.updateOrderStatus("<Orders><Order_Attributes><Order_Number>BBB5000000002390006</Order_Number><Order_State>DO CREATED</Order_State><State_Description>DO CREATED</State_Description><Status_Change_Date>10/11/2011</Status_Change_Date><reason>Order is shipped</reason><Shipping_Group><Shipment_Group_Id>DC1sg2390006</Shipment_Group_Id><Shipment_State>PARTIALLY RELEASED</Shipment_State><State_Description>PARTIALLY RELEASED</State_Description><Shipment_Tracking><Tracking_Number>1Z23A48565754</Tracking_Number><Carrier_Code>UPS</Carrier_Code><URL>UPS.com</URL><actual_ship_date>10/11/2011</actual_ship_date></Shipment_Tracking></Shipping_Group></Order_Attributes></Orders>");
        orderItem = orderRepository.getItem("5000000002390006",BBBCoreConstants.ORDER_ITEM_DESCRIPTOR);
        orderState=(String) orderItem.getPropertyValue(BBBCoreConstants.STATE_ORDER_PROPERTY_NAME);
        
        assertEquals("DO CREATED", orderState);
        sGs= ((List) orderItem.getPropertyValue(BBBCoreConstants.SHIPPING_GROUPS_ORDER_PROPERTY_NAME));
        assertTrue("no shipping groups", sGs.size() == 1);
        assertEquals("INITIAL", ((RepositoryItem)sGs.get(0)).getPropertyValue(BBBCoreConstants.STATE_ORDER_PROPERTY_NAME));
	}
	
	

}
