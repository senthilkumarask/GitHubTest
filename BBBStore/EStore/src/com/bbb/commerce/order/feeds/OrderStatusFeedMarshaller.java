package com.bbb.commerce.order.feeds;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.messaging.MessageMarshaller;

/**
 * This class acts a marshaller for order feed message that is sent on the test queue.The class is for test purpose only.
 * It is used to test if OrderFeedMessageListener is invoked properly when an order feed message is sent on Queue
 * 
 * @author njai13
 *
 */
public class OrderStatusFeedMarshaller extends MessageMarshaller {


	private static final long serialVersionUID = 1L;

	public void marshall(final ServiceRequestIF pReqVO, final Message pMessage)
			throws BBBSystemException, BBBBusinessException {
		try{
			final OrderFeedVO orderFeedVO = (OrderFeedVO) pReqVO;		
			final String orderFeedXMl = orderFeedVO.getXml();
			final TextMessage txtMessage = (TextMessage) pMessage;
			txtMessage.setText(orderFeedXMl);
		}catch (JMSException jmsException) {
			throw new BBBSystemException(BBBCoreErrorConstants.CHECKOUT_ERROR_1068,jmsException.getMessage(),
					jmsException);
		}

	}
}
