/*
 *
 * File  : MessageServiceProvider.java
 * Project:     BBB
 * 
 */
package com.bbb.framework.messaging;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.XAQueueConnectionFactory;
import javax.jms.XATopicConnectionFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.bbb.common.BBBGenericService;

import com.bbb.commerce.catalog.BBBConfigTools;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

/**
 * Message provider configurations
 * 
 * @author manohar
 * @version 1.0
 */
public class MessageServiceProvider extends BBBGenericService {
	
	/**
	 * 
	 */
	private String queueConnectionFactoryName;
	
	/**
	 * 
	 */
	private String connectionFactoryName;
	/**
	 * 
	 */
	private String cfUserName;
	/**
	 * 
	 */
	private String cfPassword;
	
	/**
	 * 
	 */
	private String topicConnectionFactoryName;
	
	/**
	 * queueType
	 */
	private String queueType;
	
	/**
	 * 
	 */
	private String queueXAConnectionFactoryName;
	
	/**
	 * 
	 */
	private String topicXAConnectionFactoryName;
	
	/**
	 * 
	 */
	private BBBConfigTools catalogTools;
	
	/**
	 * 
	 */
	private TopicConnectionFactory topicConnectionFactory = null;
	
	/**
	 * 
	 */
	private XAQueueConnectionFactory xaQueueConnectionFactory = null;
	
	/**
	 * 
	 */
	private XATopicConnectionFactory xaTopicConnectionFactory = null;
	
	/**
	 * 
	 */
	private Map<String, Destination> contextDestinationMap = new HashMap<String, Destination>();
	
	/**
	 * 
	 * @return
	 */
	public Map<String, Destination> getContextDestinationMap() {
		return contextDestinationMap;
	}

	/**
	 * 
	 * @param contextDestinationMap
	 */
	public void setContextDestinationMap(
			Map<String, Destination> contextDestinationMap) {
		this.contextDestinationMap = contextDestinationMap;
	}

	/**
	 * 
	 * @return
	 */
	public String getQueueConnectionFactoryName() {
		List<String> jmsList = new ArrayList<String>();
		try {
			jmsList = getCatalogTools().getAllValuesForKey(getQueueType(), "queueConnectionFactoryName");
		} catch (BBBBusinessException bbbbEx) {
			
			logError("Business Exception while fetching Queue Connection Factory Name", bbbbEx);
			
		} catch (BBBSystemException bbbsEx) {
			
			logError("System Exception while fetching Queue Connection Factory Name", bbbsEx);
			
		}
		if(!jmsList.isEmpty()){
			queueConnectionFactoryName = jmsList.get(0);
		}
		return queueConnectionFactoryName;
	}

	/**
	 * 
	 * @param queueConnectionFactoryName
	 */
	public void setQueueConnectionFactoryName(String queueConnectionFactoryName) {
		this.queueConnectionFactoryName = queueConnectionFactoryName;
	}

	/**
	 * 
	 * @return connectionFactoryName
	 */
	public String getConnectionFactoryName() {
		List<String> jmsList = new ArrayList<String>();
		try {
			jmsList = getCatalogTools().getAllValuesForKey(getQueueType(), "connectionFactoryName");
		} catch (BBBBusinessException bbbbEx) {

			logError("Business Exception while fetching Connection Factory Name", bbbbEx);

		} catch (BBBSystemException bbbsEx) {

			logError("System Exception while fetching Connection Factory Name", bbbsEx);

		}
		if(!jmsList.isEmpty()){
			connectionFactoryName = jmsList.get(0);
		}
		return connectionFactoryName;
	}

	/**
	 * 
	 * @param connectionFactoryName
	 */
	public void setConnectionFactoryName(String connectionFactoryName) {
		this.connectionFactoryName = connectionFactoryName;
	}

	/**
	 * 
	 * @return topicConnectionFactoryName
	 */
	public String getTopicConnectionFactoryName() {
		List<String> jmsList = new ArrayList<String>();
		try {
			jmsList = getCatalogTools().getAllValuesForKey(getQueueType(), "topicConnectionFactoryName");
		} catch (BBBBusinessException bbbbEx) {
			
			logError("Business Exception while fetching Topic Connection Factory Name", bbbbEx);
			
		} catch (BBBSystemException bbbsEx) {
			
			logError("System Exception while fetching Topic Connection Factory Name", bbbsEx);
			
		}
		if(!jmsList.isEmpty()){
			topicConnectionFactoryName = jmsList.get(0);
		}
		return topicConnectionFactoryName;
	}

	/**
	 * 
	 * @param topicConnectionFactoryName
	 */
	public void setTopicConnectionFactoryName(String topicConnectionFactoryName) {
		this.topicConnectionFactoryName = topicConnectionFactoryName;
	}

	/**
	 * 
	 * @return queueXAConnectionFactoryName
	 */
	public String getQueueXAConnectionFactoryName() {
		List<String> jmsList = new ArrayList<String>();
		try {
			jmsList = getCatalogTools().getAllValuesForKey(getQueueType(), "queueXAConnectionFactoryName");
		} catch (BBBBusinessException bbbbEx) {
			
			logError("Business Exception while fetching Queue XAConnection Factory Name", bbbbEx);
			
		} catch (BBBSystemException bbbsEx) {
			
			logError("System Exception while fetching Queue XAConnection Factory Name", bbbsEx);
			
		}
		if(!jmsList.isEmpty()){
			queueXAConnectionFactoryName = jmsList.get(0);
		}
		return queueXAConnectionFactoryName;
	}

	/**
	 * 
	 * @param queueXAConnectionFactoryName
	 */
	public void setQueueXAConnectionFactoryName(String queueXAConnectionFactoryName) {
		this.queueXAConnectionFactoryName = queueXAConnectionFactoryName;
	}

	/**
	 * 
	 * @return topicXAConnectionFactoryName
	 */
	public String getTopicXAConnectionFactoryName() {
		List<String> jmsList = new ArrayList<String>();
		try {
			jmsList = getCatalogTools().getAllValuesForKey(getQueueType(), "topicXAConnectionFactoryName");
		} catch (BBBBusinessException bbbbEx) {
			
			logError("Business Exception while fetching Topic XAConnection Factory Name", bbbbEx);
			
		} catch (BBBSystemException bbbsEx) {
			
			logError("System Exception while fetching Topic XAConnection Factory Name", bbbsEx);
			
		}
		if(!jmsList.isEmpty()){
			topicXAConnectionFactoryName = jmsList.get(0);
		}
		return topicXAConnectionFactoryName;
	}

	/**
	 * 
	 * @param topicXAConnectionFactoryName
	 */
	public void setTopicXAConnectionFactoryName(String topicXAConnectionFactoryName) {
		this.topicXAConnectionFactoryName = topicXAConnectionFactoryName;
	}	

	/**
	 * 
	 */
	private BBBJMSInitialContextFactory jmsInitialContextFactory;

	/**
	 * 
	 * @return jmsInitialContextFactory
	 */
	public BBBJMSInitialContextFactory getJmsInitialContextFactory() {
		return jmsInitialContextFactory;
	}

	/**
	 * 
	 * @param jmsInitialContextFactory
	 */
	public void setJmsInitialContextFactory(
			BBBJMSInitialContextFactory jmsInitialContextFactory) {
		this.jmsInitialContextFactory = jmsInitialContextFactory;
	}

	/**
	 * 
	 * @return InitialContext
	 * @throws NamingException
	 */
	public InitialContext getInitialContext() throws NamingException {
		if (getJmsInitialContextFactory() != null) {
			return getJmsInitialContextFactory().getInitialContext();
		} else {
			return new InitialContext();
		}
	}

	/**
	 * 
	 */
	private ConnectionFactory connectionFactory = null;

	/**
	 * 
	 * @return connectionFactory
	 * @throws NamingException
	 * @throws BBBSystemException
	 */
	public ConnectionFactory getConnectionFactory() throws NamingException, BBBSystemException {
		if(getConnectionFactoryName() != null) {
			if (connectionFactory == null) {
				connectionFactory = getConnectionFactory(getConnectionFactoryName());
			}
		}
		
		if (connectionFactory == null) {
			throw new BBBSystemException(BBBCoreErrorConstants.MSG_ERROR_1009,"Check your connection factory name, couldn't get connection factory from lookup.");
		}
		
		return connectionFactory;
	}

	/**
	 * 
	 */
	private QueueConnectionFactory queueConnectionFactory = null;
	
	/**
	 * 
	 * @return queueConnectionFactory
	 * @throws NamingException
	 * @throws BBBSystemException
	 */
	public ConnectionFactory getQueueConnectionFactory() throws NamingException, BBBSystemException {
		if (queueConnectionFactory == null) {
			queueConnectionFactory = (QueueConnectionFactory)getConnectionFactory(getQueueConnectionFactoryName());
		}
		
		if (queueConnectionFactory == null) {
			throw new BBBSystemException(BBBCoreErrorConstants.MSG_ERROR_1010,"Check your queue connection factory name, couldn't get connection factory from lookup.");
		}
		return queueConnectionFactory;
	}
	
	/**
	 * 
	 * @return topicConnectionFactory
	 * @throws NamingException
	 * @throws BBBSystemException
	 */
	public TopicConnectionFactory getTopicConnectionFactory() throws NamingException, BBBSystemException {
		if (topicConnectionFactory == null) {
			topicConnectionFactory = (TopicConnectionFactory)getConnectionFactory(getTopicConnectionFactoryName());
		}
		
		if (topicConnectionFactory == null) {
			throw new BBBSystemException(BBBCoreErrorConstants.MSG_ERROR_1011,"Check your topic connection factory name, couldn't get connection factory from lookup.");
		}
		
		return topicConnectionFactory;
	}
	
	/**
	 * 
	 * @return xaQueueConnectionFactory
	 * @throws NamingException
	 * @throws BBBSystemException
	 */
	public XAQueueConnectionFactory getXAQueueConnectionFactory() throws NamingException, BBBSystemException {
		if (xaQueueConnectionFactory == null) {
			xaQueueConnectionFactory = (XAQueueConnectionFactory)getConnectionFactory(getQueueXAConnectionFactoryName());
		}
		
		if (xaQueueConnectionFactory == null) {
			throw new BBBSystemException(BBBCoreErrorConstants.MSG_ERROR_1012,"Check your XA queue connection factory name, couldn't get connection factory from lookup.");
		}
		
		return xaQueueConnectionFactory;
	}
	
	/**
	 * 
	 * @return xaTopicConnectionFactory
	 * @throws NamingException
	 * @throws BBBSystemException
	 */
	public ConnectionFactory getXATopicConnectionFactory() throws NamingException, BBBSystemException {
		if (xaTopicConnectionFactory == null) {
			xaTopicConnectionFactory = (XATopicConnectionFactory)getConnectionFactory(getTopicXAConnectionFactoryName());
		}
		
		if (xaTopicConnectionFactory == null) {
			throw new BBBSystemException(BBBCoreErrorConstants.MSG_ERROR_1013,"Check your XA topic connection factory name, couldn't get connection factory from lookup.");
		}
		
		return xaTopicConnectionFactory;
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 * @throws JMSException
	 * @throws NamingException
	 * @throws BBBSystemException 
	 */
	public Connection getConnection(String type) throws JMSException, NamingException, BBBSystemException {
		return getConnection(type, false);
	}
	
	/**
	 * 
	 * @param type
	 * @param transactionSupported
	 * @return conn
	 * @throws JMSException
	 * @throws NamingException
	 * @throws BBBSystemException
	 */
	public Connection getConnection(String type, boolean transactionSupported) 
		throws JMSException, NamingException, BBBSystemException {
		
		Connection conn = null;
		if (type == null) {
			conn = getConnectionFactory().createConnection(getCfUserName(),getCfPassword());
		} else if ("Topic".equals(type)) {
			if(transactionSupported) {
				conn = getXATopicConnectionFactory().createConnection(getCfUserName(),getCfPassword());
			} else {
				conn = getTopicConnectionFactory().createConnection(getCfUserName(),getCfPassword());
			}
		} else if ("Queue".equals(type)) {
			if(transactionSupported) {
				conn = getXAQueueConnectionFactory().createConnection(getCfUserName(),getCfPassword());
			} else {
				conn = getQueueConnectionFactory().createConnection(getCfUserName(),getCfPassword());
			}
		} else {
			conn = getConnectionFactory().createConnection(getCfUserName(),getCfPassword());
		}
		
		if (conn == null) {
			throw new BBBSystemException(BBBCoreErrorConstants.MSG_ERROR_1014,"Cann't create connection, check the factory names for Type -" + type);
		}
		
		return conn;
	}

	/**
	 * 
	 * @param conn
	 * @return session
	 * @throws JMSException
	 * @throws NamingException
	 * @throws BBBSystemException
	 */
	public Session createSession(Connection conn,String destType) throws JMSException, NamingException, BBBSystemException {
		Session session = null;
		if (conn != null) {
			if ("Queue".equals(destType)) {
				session = ((QueueConnection)conn).createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			} else if ("Topic".equals(destType)) {
				session = ((TopicConnection)conn).createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			} else {
				session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			}
		}
		
		if (session == null) {
			throw new BBBSystemException(BBBCoreErrorConstants.MSG_ERROR_1015,"Cann't create session");
		}

		return session;
	}

	/**
	 * 
	 * @param jndiName
	 * @return ConnectionFactory
	 * @throws NamingException
	 */
	public ConnectionFactory getConnectionFactory(String jndiName) throws NamingException {
		return (ConnectionFactory) getInitialContext().lookup(jndiName);
	}

	/**
	 * check if it is there in local contextDestinationMap else look for that
	 * and get the initial context
	 * 
	 * @param destName
	 * @return Destination
	 * @throws NamingException
	 */
	public Destination getDestination(String destName) throws NamingException {
		Destination destination;
		if (null != this.contextDestinationMap
				&& !this.contextDestinationMap.isEmpty()
				&& null != this.contextDestinationMap.get(destName)) {
			destination = this.contextDestinationMap.get(destName);
		} else {
			destination = (javax.jms.Destination) getInitialContext().lookup(
					destName);
			this.contextDestinationMap.put(destName, destination);
		}
		return destination;
	}

	/**
	 * 
	 * @param conn
	 * @throws JMSException
	 */
	public void closeConnection(Connection conn) throws JMSException {
		if (conn != null) {
			conn.close();
		}
	}

	/**
	 * @return the catalogTools
	 */
	public BBBConfigTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBConfigTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * @return the queueType
	 */
	public String getQueueType() {
		return queueType;
	}

	/**
	 * @param queueType the queueType to set
	 */
	public void setQueueType(String queueType) {
		this.queueType = queueType;
	}

	public String getCfUserName() {
		List<String> jmsList = new ArrayList<String>();
			try{
				jmsList = getCatalogTools().getAllValuesForKey(getQueueType(), "userName");
			} catch (BBBBusinessException bbbbEx) {
				
				logError("Business Exception while fetching Cf User Name", bbbbEx);
				
			} catch (BBBSystemException bbbsEx) {
				
				logError("System Exception while fetching Cf User Name", bbbsEx);
				
			}
		if(!jmsList.isEmpty()){
			cfUserName = jmsList.get(0);
			logDebug("MessageServiceProvider:Got Non Empty Tibco Connection UserName");
		}
		return cfUserName;
	}

	public void setCfUserName(String cfUserName) {
		this.cfUserName = cfUserName;
	}

	public String getCfPassword() {
		List<String> jmsList = new ArrayList<String>();
			try {		
				jmsList = getCatalogTools().getAllValuesForKey(getQueueType(), "password");
			} catch (BBBBusinessException bbbbEx) {
				
				logError("Business Exception while fetching Cf Password", bbbbEx);
				
			} catch (BBBSystemException bbbsEx) {
				
				logError("System Exception while fetching Cf Password", bbbsEx);
				
			}
		if(!jmsList.isEmpty()){
			cfPassword = jmsList.get(0);
			logDebug("MessageServiceProvider:Got Non Empty Tibco Connection Password");
		}
		return cfPassword;
	}

	public void setCfPassword(String cfPassword) {
		this.cfPassword = cfPassword;
	}
	//Method to reset connection factories to null, which can be called from /dyn/admin
	public void resetConnection()
	{
		connectionFactory=null;
		queueConnectionFactory=null;
		topicConnectionFactory=null;
		xaQueueConnectionFactory=null;
		xaTopicConnectionFactory=null;

	}

}
