package com.bbb.framework.cache;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageFormatException;
import javax.jms.ObjectMessage;

import com.bbb.common.BBBGenericService;
import com.bbb.redirectURLs.CategoryRedirectURLLoader;

import atg.dms.patchbay.MessageSink;

public class BBBCategoryRedirectURLCacheContainer extends BBBGenericService implements MessageSink{

	private boolean enabled;
	private String cacheType;
	private CategoryRedirectURLLoader categoryRedirectURL;
	
	@Override
	public void receiveMessage(String pPortName, Message pMessage) throws JMSException {
		
		logDebug("Recieved Message......" +pMessage.toString());
		if(isEnabled()) {
			if(!(pMessage instanceof ObjectMessage)) {
				throw new MessageFormatException("Not a Object Message!");
			}
			
			ObjectMessage objectMessage = (ObjectMessage) pMessage;
			Object obj = objectMessage.getObject();
			logDebug((new StringBuilder()).append("Recieved ").append(obj.toString()).append("on Port ").append(pPortName).toString());
			if(obj instanceof CategoryRedirectURLCacheInvalidationMessage) {
				CategoryRedirectURLCacheInvalidationMessage message = (CategoryRedirectURLCacheInvalidationMessage) obj;
				logDebug("Started Clearing Cache Type..... :- "+message.getCacheType());
				if(this.getCacheType().equalsIgnoreCase(message.getCacheType())) {
					try{
						if(this.getCategoryRedirectURL().getCategoryRedirectURLMap() != null) {
							this.getCategoryRedirectURL().getCategoryRedirectURLMap().clear();
						}
						logDebug("Cleared CategoryRedirectURLMap..... :- "+message.getCacheType());
					} catch(Exception e) {
						logDebug("Unable to Invalidate CategoryRedirectURLMap...." +e.getMessage());
					}
				}
				
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

	/**
	 * @return the categoryRedirectURL
	 */
	public CategoryRedirectURLLoader getCategoryRedirectURL() {
		return categoryRedirectURL;
	}
	

	/**
	 * @param categoryRedirectURL the categoryRedirectURL to set
	 */
	public void setCategoryRedirectURL(CategoryRedirectURLLoader categoryRedirectURL) {
		this.categoryRedirectURL = categoryRedirectURL;
	}

}
