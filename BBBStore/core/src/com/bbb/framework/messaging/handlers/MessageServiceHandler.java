/*
 *
 * File  : MessageServiceHandler.java
 * Project:     BBB
 */
package com.bbb.framework.messaging.handlers;

import java.util.Map;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.NamingException;

import com.bbb.commerce.catalog.BBBConfigTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.integration.handlers.MsgServiceHandlerIF;
import com.bbb.framework.integration.util.ServiceHandlerUtil;
import com.bbb.framework.messaging.DestinationFactory;
import com.bbb.framework.messaging.MessageDestination;
import com.bbb.framework.messaging.MessageMarshaller;
import com.bbb.framework.messaging.MessageServiceProvider;
import com.bbb.framework.messaging.ValidateMsgReqVO;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBUtility;

import atg.core.util.StringUtils;

public class MessageServiceHandler extends BBBGenericService implements MsgServiceHandlerIF {
	
	private DestinationFactory destinationFactory; 
	private Map serviceToErrorCodeMap;
	
	
	public DestinationFactory getDestinationFactory() {
		return destinationFactory;
	}

	public void setDestinationFactory(DestinationFactory destinationFactory) {
		this.destinationFactory = destinationFactory;
	}
	
	public Map getServiceToErrorCodeMap() {
		return serviceToErrorCodeMap;
	}
	
	private BBBConfigTools mConfigTools;
	
	/**
	 * @return the mCatalogTools
	 */
	public BBBConfigTools getConfigTools() {
		return this.mConfigTools;
	}

	/**
	 * @param mCatalogTools
	 *            the mCatalogTools to set
	 */
	public void setConfigTools(final BBBConfigTools pConfigTools) {
		this.mConfigTools = pConfigTools;
	}
	public void setServiceToErrorCodeMap(Map serviceToErrorCodeMap) {
		this.serviceToErrorCodeMap = serviceToErrorCodeMap;
	}

	/**
	 * sends message to the destination of the service mentioned
	 * @throws BBBSystemException 
	 * @throws BBBBusinessException 
	 * 
	 * @throws JMSException 
	 * @throws ClassNotFoundException 
	 * @throws InstantiationException 
	 * @throws IllegalAccessException 
	 * 
	 */
	public void send(final String service,
			final ServiceRequestIF reqVO) throws BBBSystemException, BBBBusinessException {


		final String perfLogName = new StringBuilder()
				.append("MsgServiceHandlerSend-").append(service).toString();
		BBBPerformanceMonitor.start(perfLogName, BBBPerformanceConstants.MESSAGING_CALL + "-" + service);

		MessageDestination destination = destinationFactory.getDestination(service);
		MessageServiceProvider provider = null;
		if(null != destination){
			provider = destination.getProvider();
		}else{
			BBBPerformanceMonitor.cancel(perfLogName, BBBPerformanceConstants.MESSAGING_CALL + "-" + service);
			throw new BBBSystemException(BBBCoreErrorConstants.MSG_ERROR_1000,"Can't create message destination.");
		}
		
		Session session = null;
		Connection conn = null;

		try {
			String destType = destination.getType();
			long start = System.currentTimeMillis();
			int timeout = getConfigTools().getValueForConfigKey(BBBCoreConstants.TIBCO_KEYS, BBBCoreConstants.TIBCO_TIMEOUT_KEY, 10000);
			if(service.equalsIgnoreCase("porchOrderTibcoMessage")){
				timeout=0;
			}
			if(null != provider){
				conn = provider.getConnection(destType, false);
				session = provider.createSession(conn,destType);
			}else{
				BBBPerformanceMonitor.cancel(perfLogName, BBBPerformanceConstants.MESSAGING_CALL + "-" + service);
				throw new BBBSystemException(BBBCoreErrorConstants.MSG_ERROR_1001,"Can't create JMS provider");
			}
			
			
			Message message = createMessage(session, destination);
			
			message.setJMSType(destination.getJmsType());
						
			MessageMarshaller marshaller = destination.getMarshaller();
			
			if (marshaller != null) {
				marshaller.marshall(reqVO, message);
			} else {
				BBBPerformanceMonitor.cancel(perfLogName, BBBPerformanceConstants.MESSAGING_CALL + "-" + service);
				throw new BBBSystemException(BBBCoreErrorConstants.MSG_ERROR_1002,"Can't resolve marshaller");
			}
			
			Destination jmsDestiation = destination.getDestination();
		
				logDebug("destType :" + destType + "\njmsDestiation : "
						+ jmsDestiation.getClass() + "\n Is Topic:"
						+ (jmsDestiation instanceof javax.jms.Topic)
						+ "\nis Queue: "
						+ (jmsDestiation instanceof javax.jms.Queue));
				logDebug("Message is  : " + ((TextMessage) message).getText());
			
			if ("Topic".equals(destType)) {
				TopicPublisher publisher = ((TopicSession)session).createPublisher((Topic)jmsDestiation);
				publisher.setTimeToLive(timeout);
				publisher.publish(message);
			} else if ("Queue".equals(destType)) {
				QueueSender sender = ((QueueSession)session).createSender((Queue)jmsDestiation);
				sender.setTimeToLive(timeout);
				sender.send(message);
			} else {
				BBBPerformanceMonitor.cancel(perfLogName, BBBPerformanceConstants.MESSAGING_CALL + "-" + service);
				throw new BBBSystemException(BBBCoreErrorConstants.MSG_ERROR_1003,"Not a valid destination");
			}
			long end = System.currentTimeMillis();
			logInfo("Total time taken to send JMS message =" + (end - start));
		} catch (NamingException e) {
			String errorCode = (String)getServiceToErrorCodeMap().get(service);
			BBBPerformanceMonitor.cancel(perfLogName, BBBPerformanceConstants.MESSAGING_CALL + "-" + service);
			BBBUtility.passErrToPage(errorCode,"NamingException in calling");
			throw new BBBSystemException(BBBCoreErrorConstants.MSG_ERROR_1004,e.getMessage(), e);
		} catch (JMSException e) {
			String errorCode = (String)getServiceToErrorCodeMap().get(service);
			BBBPerformanceMonitor.cancel(perfLogName, BBBPerformanceConstants.MESSAGING_CALL + "-" + service);
			BBBUtility.passErrToPage(errorCode,"JMSException in calling");
			throw new BBBSystemException(BBBCoreErrorConstants.MSG_ERROR_1005,e.getMessage(), e);
		}catch (Exception e) {
			logError("Excetion Occured.", e);
			BBBPerformanceMonitor.cancel(perfLogName, BBBPerformanceConstants.MESSAGING_CALL + "-" + service);
		} finally {
			closeSession(session);
			if (provider != null && conn != null) {
				try {
					provider.closeConnection(conn);
					if(conn!=null){
						conn.close();
					}
				} catch (JMSException e) {
					
						logError("Error in closing the connection after sending the message", e);
					
				}
			}
		}
		BBBPerformanceMonitor.end(perfLogName, BBBPerformanceConstants.MESSAGING_CALL + "-" + service);
	}
	
	/**
	 * sends message to the destination of the service mentioned
	 * 
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * 
	 * @throws JMSException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * 
	 */
	public void sendTextMessage(final String service, final ServiceRequestIF reqVO) throws BBBSystemException, BBBBusinessException {

		final String perfLogName = new StringBuilder().append("MsgServiceHandlerSend-").append(service).toString();
		BBBPerformanceMonitor.start(perfLogName, BBBPerformanceConstants.MESSAGING_CALL + "-" + service);

		String xmlMessage = null;
		MessageDestination destination = destinationFactory.getDestination(service);
		MessageServiceProvider provider = null;
		if (null != destination) {
			provider = destination.getProvider();
		} else {
			BBBPerformanceMonitor.cancel(perfLogName, BBBPerformanceConstants.MESSAGING_CALL + "-" + service);
			throw new BBBSystemException(BBBCoreErrorConstants.MSG_ERROR_1000,"Can't create message destination.");
		}

		Session session = null;
		Connection conn = null;

		try {

			MessageMarshaller marshaller = destination.getMarshaller();

			if (marshaller != null) {				
				xmlMessage = marshaller.marshall(reqVO);
				if(!StringUtils.isBlank(xmlMessage)){
					if(null != marshaller.getName() && !StringUtils.isEmpty(marshaller.getName())){
						logDebug(marshaller.getName() + " : marshal" + xmlMessage);
					}
					String destType = destination.getType();

					if (null != provider) {
						conn = provider.getConnection(destType, false);
						session = provider.createSession(conn, destType);
					} else {
						BBBPerformanceMonitor.cancel(perfLogName, BBBPerformanceConstants.MESSAGING_CALL + "-" + service);
						throw new BBBSystemException(BBBCoreErrorConstants.MSG_ERROR_1001,"Can't create JMS provider");
					}

					TextMessage txtMessage = (TextMessage) createMessage(session, destination);
					txtMessage.setJMSType(destination.getJmsType());
					txtMessage.setText(xmlMessage);	
					logDebug("xmlMessage:"+xmlMessage);
					
					Destination jmsDestiation = destination.getDestination();
					
						logDebug("destType :" + destType + "\njmsDestiation : " + jmsDestiation.getClass() + "\n Is Topic:" + (jmsDestiation instanceof javax.jms.Topic) + "\nis Queue: "
								+ (jmsDestiation instanceof javax.jms.Queue));
					
					if ("Topic".equals(destType)) {
						TopicPublisher publisher = ((TopicSession) session).createPublisher((Topic) jmsDestiation);
						publisher.publish(txtMessage);
					} else if ("Queue".equals(destType)) {
						QueueSender sender = ((QueueSession) session).createSender((Queue) jmsDestiation);
						sender.send(txtMessage);
					} else {
						BBBPerformanceMonitor.cancel(perfLogName, BBBPerformanceConstants.MESSAGING_CALL + "-" + service);
						throw new BBBSystemException(BBBCoreErrorConstants.MSG_ERROR_1003,"Not a valid destination");
					}
				}
			} else {
				throw new BBBSystemException(BBBCoreErrorConstants.MSG_ERROR_1016,"Can't resolve marshaller");
			}
		} catch (NamingException e) {
			BBBPerformanceMonitor.cancel(perfLogName, BBBPerformanceConstants.MESSAGING_CALL + "-" + service);
			throw new BBBSystemException(BBBCoreErrorConstants.MSG_ERROR_1006,e.getMessage(), e);
		} catch (JMSException e) {
			BBBPerformanceMonitor.cancel(perfLogName, BBBPerformanceConstants.MESSAGING_CALL + "-" + service);
			throw new BBBSystemException(BBBCoreErrorConstants.MSG_ERROR_1007,e.getMessage(), e);
		} finally {
			closeSession(session);
			if (provider != null && conn != null) {
				try {
					provider.closeConnection(conn);
					if (conn != null) {
						conn.close();
					}
				} catch (JMSException e) {
					
						logError("Error in closing the connection after sending the message", e);
					
				}
			}
		}
		BBBPerformanceMonitor.end(perfLogName, BBBPerformanceConstants.MESSAGING_CALL + "-" + service);
	}	
	
	/**
	 * closes all connections
	 * 
	 * @param sess
	 * @return
	 */
	public boolean closeSession(final Session session) {
		try {
			if (session != null) {
				session.close();
			}
		} catch (JMSException e) {
			
				logError("Error in closing the session after sending the message", e);
			
		}
		return true;
	}

	/**
	 * creates message based on the type of message. Types of message -
	 * TextMessage, MapMessage, ObjectMessage, BytesMessage, StreamMessage
	 * 
	 * @param session
	 * @param destination
	 * @return message
	 * @throws BBBBusinessException
	 * @throws JMSException
	 */
	private Message createMessage(Session session, MessageDestination destination) throws BBBBusinessException, JMSException {
		Message message = null;
		if(destination.getMessageType().equalsIgnoreCase("TextMessage")){
			message = session.createTextMessage();
		} else if(destination.getMessageType().equalsIgnoreCase("MapMessage")){
			message = session.createMapMessage();
		} else if(destination.getMessageType().equalsIgnoreCase("ObjectMessage")){
			message = session.createObjectMessage();
		} else if(destination.getMessageType().equalsIgnoreCase("BytesMessage")){
			message = session.createBytesMessage();
		} else if(destination.getMessageType().equalsIgnoreCase("StreamMessage")){
			message = session.createStreamMessage();
		} else {
			message = session.createMessage();
		}
		
		if (message == null) {
			throw new BBBBusinessException(BBBCoreErrorConstants.MSG_ERROR_1008,"Can't create message object");
		}
		return message;
	}
	
	/**
	 * Used only for testing purpose.
	 */
	public void testSend(){
		ValidateMsgReqVO voReq = new ValidateMsgReqVO();
		try {
			ServiceHandlerUtil.send(voReq);
		} catch (BBBBusinessException e) {
			logError(e);
		} catch (BBBSystemException e) {
			logError(e);
		}
	}
}