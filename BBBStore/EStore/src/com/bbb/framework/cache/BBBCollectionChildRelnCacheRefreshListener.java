package com.bbb.framework.cache;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageFormatException;
import javax.jms.ObjectMessage;

import com.bbb.cache.scheduler.BBBCollectionChildRelnCacheBuilder;
import com.bbb.common.BBBGenericService;

import atg.dms.patchbay.MessageSink;

public class BBBCollectionChildRelnCacheRefreshListener extends BBBGenericService implements MessageSink {
	
	private boolean enabled;
	private BBBCollectionChildRelnCacheBuilder cacheBuilder;
	private String cacheType;
	
	
	@Override
	public void receiveMessage(String pPortName, Message pMessage) throws JMSException {
		logDebug("Received Message : " + pMessage.toString());
		if (isEnabled()) { 
 			if(!(pMessage instanceof ObjectMessage))
	            throw new MessageFormatException("Not an ObjectMessage");
	        ObjectMessage objMessage = (ObjectMessage)pMessage;
	        Object obj = objMessage.getObject();
	        logDebug((new StringBuilder()).append("Received ").append(obj.toString()).append(" on port ").append(pPortName).toString());
	        if(obj instanceof CollectionChildRelnCacheInvalidationMessage) {
	        	CollectionChildRelnCacheInvalidationMessage msg = (CollectionChildRelnCacheInvalidationMessage) obj;
	        	logDebug("Cache Type : " + msg.getCacheType());
	        	if (this.getCacheType().equalsIgnoreCase(msg.getCacheType())) {
	        		try {
						this.getCacheBuilder().startCacheRefresh();
					} catch (Exception exception) {
						logError("Error while building Child Product to Collection Relation Cache", exception);
					}
				} else {
					logDebug("BBBCollectionChildRelnCacheRefreshListener.receiveMessage() | Invalid cacheType");
				}
	        } else {
	            logDebug("BBBCollectionChildRelnCacheRefreshListener.receiveMessage() | Not a CacheInvalidationMessage" + " on port " + pPortName + " is an instance of " + pMessage.getClass() + " Hence ignore this function");
	        }
		}
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the cacheBuilder
	 */
	public BBBCollectionChildRelnCacheBuilder getCacheBuilder() {
		return cacheBuilder;
	}

	/**
	 * @param cacheBuilder the cacheBuilder to set
	 */
	public void setCacheBuilder(BBBCollectionChildRelnCacheBuilder cacheBuilder) {
		this.cacheBuilder = cacheBuilder;
	}

	/**
	 * @return the cacheType
	 */
	public String getCacheType() {
		return cacheType;
	}

	/**
	 * @param cacheType the cacheType to set
	 */
	public void setCacheType(String cacheType) {
		this.cacheType = cacheType;
	}
}
