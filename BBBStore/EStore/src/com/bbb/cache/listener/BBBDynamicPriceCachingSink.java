package com.bbb.cache.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageFormatException;
import javax.jms.ObjectMessage;

import atg.dms.patchbay.MessageSink;

import com.bbb.cache.BBBLocalDynamicPriceSKUCache;
import com.bbb.common.BBBGenericService;
import com.bbb.framework.cache.CacheInvalidationMessage;

/**
 * This is sink for CacheInvalidationMessage for DynamicPrice cache
 * @author ikhan2
 *
 */
public class BBBDynamicPriceCachingSink extends BBBGenericService implements MessageSink {

	/**
	 * Reference to BBBLocalDynamicPriceSKUCache
	 */
	private BBBLocalDynamicPriceSKUCache dynamicPriceSKUCache;
	
	@Override
	public void receiveMessage(String pPortName, Message pMessage)
			throws JMSException {

			logDebug("BBBDynamicPriceCachingListener invoked for caching Dynamic Repository after Store Proceducre Execution");
			if (!(pMessage instanceof ObjectMessage))
				throw new MessageFormatException("ERROR: Not an ObjectMessage");

			ObjectMessage objMessage = (ObjectMessage) pMessage;
			Object obj = objMessage.getObject();

			logDebug((new StringBuilder()).append("Received ").append(obj.toString()).append(" on port ").append(pPortName).toString());
			
			if (obj instanceof CacheInvalidationMessage) {
				getDynamicPriceSKUCache().doRebuildLocalDynamicPriceSKUCache();
			} else {
				logDebug("DynamicPriceCachingListener.receiveMessage() | Not a InvalidationMessage on port "
						+ pPortName
						+ " is an instance of "
						+ pMessage.getClass());
			}
			logDebug("BBBDynamicPriceCachingListener ends for caching Dynamic Repository after Store Proceducre Execution");		
	}

	/**
	 * 
	 * @return dynamicPriceSKUCache
	 */
	public BBBLocalDynamicPriceSKUCache getDynamicPriceSKUCache() {
		return dynamicPriceSKUCache;
	}

	/**
	 * 
	 * @param dynamicPriceSKUCache
	 */
	public void setDynamicPriceSKUCache(
			BBBLocalDynamicPriceSKUCache dynamicPriceSKUCache) {
		this.dynamicPriceSKUCache = dynamicPriceSKUCache;
	}

}
