/*
 *
 * File  : Destination.java
 * Project:     BBB
 * 
 */
package com.bbb.framework.messaging;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Destination;
import javax.naming.NamingException;

import com.bbb.commerce.catalog.BBBConfigTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

import atg.core.util.StringUtils;
import com.bbb.common.BBBGenericService;

/**
 * Holds all the details of Destination to send the message
 * 
 * @author manohar
 * @version 1.0
 */
public class MessageDestination extends BBBGenericService {
	
	
	/**
	 * 
	 */
	private String jmsType;

	/**
	 * 
	 */
	private MessageServiceProvider provider;

	/**
	 * 
	 */
	private String destinationJndi;

	/**
	 * 
	 */
	private String name;

	/**
	 * queueType
	 */
	private String configType;

	/**
	 * queueKey
	 */
	private String configKey;

	/**
	 * This could be either Topic OR Queue
	 */
	private String type;

	/**
	 * TextMessage, MapMessage, ByteMessage OR StreamMessage
	 */
	private String messageType;

	/**
	 * 
	 */
	private MessageMarshaller marshaller;

	/**
	 * 
	 */
	private BBBConfigTools catalogTools;

	/**
	 * 
	 * @return destinationJndi
	 */
	public String getDestinationJndi() {
		List<String> jmsList = new ArrayList<String>();
		try {
			jmsList = getCatalogTools().getAllValuesForKey(getConfigType(),
					getConfigKey());
		} catch (BBBBusinessException bbbbEx) {
			logError("Business Exception occurred while fetching the Destination Jndi", bbbbEx);
			
		} catch (BBBSystemException bbbsEx) {
			
			logError("System Exception occurred while fetching the Destination Jndi", bbbsEx);
			
		}
		if (!jmsList.isEmpty()) {
			destinationJndi = jmsList.get(0);
		}
		return destinationJndi;
	}

	/**
	 * 
	 * @param destination
	 */
	public void setDestinationJndi(String destination) {
		this.destinationJndi = destination;
	}

	/**
	 * name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return messageType
	 */
	public String getMessageType() {
		if (StringUtils.isEmpty(messageType)) {
			messageType = "TextMesssage";
		}
		return messageType;
	}

	/**
	 * 
	 * @param messageType
	 */
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	/**
	 * 
	 * @return marshaller
	 */
	public MessageMarshaller getMarshaller() {
		return marshaller;
	}

	/**
	 * 
	 * @param marshaller
	 */
	public void setMarshaller(MessageMarshaller marshaller) {
		this.marshaller = marshaller;
	}

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
	public void setProvider(MessageServiceProvider provider) {
		this.provider = provider;
	}

	/**
	 * 
	 * @return type. That is either it is a Queue or Topic
	 */
	public String getType() {
		if (StringUtils.isEmpty(type)) {
			type = "Queue";
		}
		return type;
	}

	/**
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 
	 * @return Destination
	 * @throws NamingException
	 */
	public Destination getDestination() throws NamingException {
		List<String> jmsList = new ArrayList<String>();

		try {
			jmsList = getCatalogTools().getAllValuesForKey(getConfigType(),
					getConfigKey());
		} catch (BBBBusinessException bbbbEx) {
			logError("Business Exception occurred while fetching destinationJndi", bbbbEx);
			
		} catch (BBBSystemException bbbsEx) {
			logError("System Exception occurred while fetching destinationJndi", bbbsEx);
			
		}
		if (!jmsList.isEmpty()) {
			destinationJndi = jmsList.get(0);
		}
		return provider.getDestination(destinationJndi);
	}

	/**
	 * @return the catalogTools
	 */
	public BBBConfigTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBConfigTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * @return the configType
	 */
	public String getConfigType() {
		return configType;
	}

	/**
	 * @param configType
	 *            the configType to set
	 */
	public void setConfigType(String configType) {
		this.configType = configType;
	}

	/**
	 * @return the configKey
	 */
	public String getConfigKey() {
		return configKey;
	}

	/**
	 * @param configKey
	 *            the configKey to set
	 */
	public void setConfigKey(String configKey) {
		this.configKey = configKey;
	}
	/**
	 * @return
	 */
	public String getJmsType() {
		return jmsType;
	}

	/**
	 * @param jmsType
	 */
	public void setJmsType(String jmsType) {
		this.jmsType = jmsType;
	}

}
