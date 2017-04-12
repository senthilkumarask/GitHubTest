package com.bbb.commerce.order.feeds;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.integration.ServiceRequestBase;

/**
 * The OrderFeedVO is for test purpose only.The xml variable will be sent as a 
 * message on the test queue.This xml will then be read by OrderFeedMessageListener
 * and updated in the order repository
 * @author njai13
 *
 */
public class OrderFeedVO extends ServiceRequestBase {

	private static final long serialVersionUID = 1L;
	private	String xml;

	/**
	 * @return the xml
	 */
	public String getXml() {
		return xml;
	}

	/**
	 * @param xml the xml to set
	 */
	public void setXml(final String xml) {
		this.xml = xml;
	}

	@Override
	public String getServiceName() {
		return BBBCoreConstants.ORDER_FEED_JMS_MESSAGE;
	}
}
