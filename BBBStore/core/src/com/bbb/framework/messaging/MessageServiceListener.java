/*
 *
 * File  : MessageServiceListener.java
 * Project:     BBB
 *
 */
package com.bbb.framework.messaging;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.NamingException;

import atg.nucleus.ServiceException;

import com.bbb.commerce.catalog.BBBConfigTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

/**
 * Base listener. All JMS listeners would extend this and override onMessage()
 * method
 *
 * @author manohar
 * @version 1.0
 */
public class MessageServiceListener extends BBBGenericService implements
		javax.jms.MessageListener {

	/**
	 *
	 */
	private DestinationFactory destinationFactory;

	/**
	 *
	 */
	private String service;

	private Boolean mSubscriberDurableFlag;

	public Boolean getSubscriberDurableFlag() {
		return this.mSubscriberDurableFlag;
	}

	public void setSubscriberDurableFlag(final Boolean subscriberDurableFlag) {
		this.mSubscriberDurableFlag = subscriberDurableFlag;
	}

	private BBBConfigTools catalogTools;

	public BBBConfigTools getCatalogTools() {
		return this.catalogTools;
	}

	public void setCatalogTools(final BBBConfigTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * The variable is used to store the Connection Instance
	 */
	Connection connection;

	/**
	 *
	 */
	Session session;

	public DestinationFactory getDestinationFactory() {
		return this.destinationFactory;
	}

	public void setDestinationFactory(final DestinationFactory destinationFactory) {
		this.destinationFactory = destinationFactory;
	}

	public String getService() {
		return this.service;
	}

	public void setService(final String service) {
		this.service = service;
	}

	public Connection getConnection() {
		return this.connection;
	}
	public void setConnection(final Connection connection) {
		this.connection = connection;
	}
	public Session getSession() {
		return this.session;
	}
	public void setSession(final Session session) {
		this.session = session;
	}

	@Override
	public void doStartService() throws ServiceException {
		
		this.logDebug("starting service " + this.getService());
		
		try {
			final MessageDestination destination = this.destinationFactory.getDestination(this.getService());
			final MessageServiceProvider provider = destination.getProvider();
			this.connection = provider.getConnection(destination.getType());
			final String clientId = this.getClientId();
			if(this.getSubscriberDurableFlag() && (clientId != null)) {
				this.connection.setClientID(clientId);
			}
			if(destination.getType().equalsIgnoreCase(BBBCoreConstants.QUEUE)){
				this.session = provider.createSession(this.connection,destination.getType());
				final Queue que = (Queue) provider.getInitialContext().lookup(
						destination.getDestinationJndi());
				final QueueReceiver reciever = ((QueueSession) this.session)
						.createReceiver(que);
				reciever.setMessageListener(this);
			}else if(destination.getType().equalsIgnoreCase(BBBCoreConstants.TOPIC)){
				this.session = provider.createSession(this.connection,destination.getType());
				final Topic topic = (Topic) provider.getInitialContext().lookup(
						destination.getDestinationJndi());
				TopicSubscriber topicSubscriber = null;

				//getSubscriberDurableFlag  configured in listener's properties file
				if(this.getSubscriberDurableFlag()){
					
					this.logDebug("create durable subscriber flow");
					

					final String durableName = this.getDurableName();

					
					this.logDebug("Fetched client id from config "  + clientId + " for service - "+  this.getService());
					this.logDebug("Topic Name as Durable "  + durableName + " for service - "+  this.getService());
					

					if ((this.session != null) && (clientId != null) && (durableName != null)) {
						topicSubscriber = ((TopicSession) this.session).createDurableSubscriber(topic, durableName);
					}

				}else{
					
					this.logDebug("create non durable subscriber flow");
					
					topicSubscriber = ((TopicSession) this.session).createSubscriber(topic);
				}

				if (null != topicSubscriber) {
				topicSubscriber.setMessageListener(this);
				} else {
					this.logError ("topicSubscriber is null");
				}
			}
			this.connection.start();

		
			this.logDebug("connection start method called successfully");
			
		} catch (final JMSException e) {
			
			this.logError("JMS Exception occurred while starting the message service", e);
			
		} catch (final NamingException e) {
			
			this.logError("Naming Exception occurred while starting the message service", e);
			
		} catch (final BBBSystemException e) {
			
			this.logError("System Exception occurred while starting the message service", e);
			
		}
	}

	@Override
	public void onMessage(final Message paramMessage) {
		// This method should be overridden by all listeners extending this class.

	}

	/*
	 * This Method is overriden for Closing the JMS session Connections
	 *
	 * @see atg.nucleus.GenericService#doStopService()
	 */
	@Override
	public void doStopService() throws ServiceException {
		
		//logDebug("stoping service " + this.getClass().getName());
		this.logDebug("stopping service " + this.getService());
		
		try {
			if(null != this.getSession()){
				this.getSession().close();
				this.setSession(null);
			}
			if(null != this.getConnection()){
				this.getConnection().close();
				this.setConnection(null);
			}
		} catch (final JMSException e) {
			
			this.logError("JMS Exception occurred while stopping the message service" , e);
			
		}
	}

	public String getClientId() throws BBBSystemException {
		String clientId = null;
		try {
			clientId = this.getCatalogTools()
			.getConfigValueByconfigType(BBBCheckoutConstants.BBB_JMS_CONFIG).get(this.getService() + "ClientId");
		} catch (final BBBBusinessException e) {
			
			this.logError("BBBBusinessException occurred while fetching clientId for service" +  this.getService(), e);
			
		}
		return clientId;
	}

	public String getDurableName() throws BBBSystemException {
		String durableName = null;
		try {
			durableName = this.getCatalogTools()
			.getConfigValueByconfigType(BBBCheckoutConstants.BBB_JMS_CONFIG).get(this.getService() + "durableName");
		} catch (final BBBBusinessException e) {
			
			this.logError("BBBBusinessException occurred while fetching clientId for service" +  this.getService(), e);
			
		}
		return durableName;
	}


}
