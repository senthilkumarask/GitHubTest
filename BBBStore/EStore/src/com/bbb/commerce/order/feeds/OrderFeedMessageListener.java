package com.bbb.commerce.order.feeds;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import atg.nucleus.ServiceException;
import com.bbb.framework.messaging.MessageServiceListener;
import com.bbb.framework.messaging.MessageServiceProvider;
import com.bbb.logging.LogMessageFormatter;
/**
 * This class is a message listener that listens for order feed message
 * on the queue.When a message is delivered on the queue the onMessage method is called
 * To test the JMS functionality call the sendOrderFeedXMLString method of OrderFeedTestQueue
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 * Reproduction or use of this file without explicit written consent is prohibited.
 * @author njai13
 *
 */
public class OrderFeedMessageListener extends MessageServiceListener 
 {


	/**
	 * This variable is used to get the JMS provider Details
	 */
    private boolean enableService;
	private MessageServiceProvider provider;
	private OrderStatusUpdateManager orderFeedManager;
	private ExceptionListener exceptionListener;
	private boolean sendStatusReport;
	
	public boolean isSendStatusReport() {
		return sendStatusReport;
	}

	public void setSendStatusReport(boolean sendStatusReport) {
		this.sendStatusReport = sendStatusReport;
	}

	public OrderStatusUpdateManager getOrderFeedManager() {
		return orderFeedManager;
	}

	public void setOrderFeedManager(OrderStatusUpdateManager orderFeedManager) {
		this.orderFeedManager = orderFeedManager;
	}

	/**
	 * @return the exceptionListener
	 */
	public ExceptionListener getExceptionListener() {
		return exceptionListener;
	}

	/**
	 * @param exceptionListener the exceptionListener to set
	 */
	public void setExceptionListener(ExceptionListener exceptionListener) {
		this.exceptionListener = exceptionListener;
	}
		
		@Override
		public void doStartService() throws ServiceException {
			if (!isEnableService()){
				logInfo("OrderFeeMessageListener is not enabled");
				return;
			}
			super.doStartService();
			try {
				this.getConnection().setExceptionListener(this.getExceptionListener());
			} catch (JMSException e) {
				this.logError("JMS Exception occurred while setting ExceptionListener in connection object", e);
			}
		}
	
	/**
	 * The Variable will hold the Queue Name for which listener should listen
	 */
	private String queueName;
	/**
	 * This Variable will used to identify what type connection to create
	 * eg.Queue or Topic
	 */
	private String connectionType;
	/**
	 * 
	 * @return provider
	 */
	public MessageServiceProvider getProvider() {
		return provider;
	}

	/**
	 * 
	 * @param provider
	 */
	public void setProvider(final MessageServiceProvider provider) {
		this.provider = provider;
	}



	/**
	 * @return the queueName
	 */
	public String getQueueName() {
		return queueName;
	}

	/**
	 * @param queueName
	 *            the queueName to set
	 */
	public void setQueueName(final String queueName) {
		this.queueName = queueName;
	}

	/**
	 * @return the connectionType
	 */
	public String getConnectionType() {
		return connectionType;
	}

	/**
	 * @param connectionType
	 *            the connectionType to set
	 */
	public void setConnectionType(final String connectionType) {
		this.connectionType = connectionType;
	}

	/*
	 * This Method is called when the new JMS message is received to the Queue
	 * 
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	@Override
	public void onMessage(final Message message) {
			logDebug("[START] onMessage");

			try {
				
				final TextMessage txtMessage = (TextMessage) message;
				String xml = txtMessage.getText();
				
				logDebug("OrderFeedMessageListener:xml  " + xml);
				if (getOrderFeedManager() != null) {
					getOrderFeedManager().updateOrderStatus(xml);
				}
				
			} catch (Exception e) {
				logError(LogMessageFormatter.formatMessage(null,"OrderFeedMessageListener.onMessage() | JMSException "),e);
				if (isSendStatusReport()){
					getOrderFeedManager().sendFailedRecordsReport(getOrderFeedManager().getExceptionMessage());
				}
			} 
			logDebug("[END] onMessage");

	}
	
	public void onMessage(){
		
		logDebug("[START] onMessage");

		try {
			
			String xml = getXML();
			
			logDebug("OrderFeedMessageListener:xml  " + xml);
			if (getOrderFeedManager() != null) {
				getOrderFeedManager().updateOrderStatus(xml);
			}
			
		} catch (Exception e) {
			logError(LogMessageFormatter.formatMessage(null,"OrderFeedMessageListener.onMessage() | JMSException "),e);
			if (isSendStatusReport()){
				getOrderFeedManager().sendFailedRecordsReport(getOrderFeedManager().getExceptionMessage());
			}
		} 
		logDebug("[END] onMessage");
		
	}

    public boolean isEnableService() {
        return enableService;
    }
    
    public boolean getEnableService() {
        return enableService;
    }

    public void setEnableService(boolean enableService) {
        this.enableService = enableService;
    }
    
    private String mXML;
	/**
	 * @return the xML
	 */
	public String getXML() {
		return mXML;
	}

	/**
	 * @param pXML the xML to set
	 */
	public void setXML(String pXML) {
		mXML = pXML;
	}
    
}
