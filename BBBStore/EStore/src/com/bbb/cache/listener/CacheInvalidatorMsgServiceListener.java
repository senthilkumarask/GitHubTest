package com.bbb.cache.listener;

import java.io.StringReader;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import com.bbb.cache.tibco.vo.CacheInvalidatorVO;
import com.bbb.framework.cache.BBBObjectCache;
import com.bbb.framework.cache.BBBWebCacheIF;
import com.bbb.framework.jaxb.cache.Main;
import com.bbb.framework.messaging.MessageServiceListener;

public class CacheInvalidatorMsgServiceListener extends MessageServiceListener{
	
	/**
	 * JAXB context instance
	 */
	private JAXBContext context;
	private BBBObjectCache mObjectCache;
	private BBBWebCacheIF cacheContainer;
	
	/**
	 * @return the mObjectCache
	 */
	public BBBObjectCache getObjectCache() {
		return mObjectCache;
	}

	/**
	 * @param mObjectCache the mObjectCache to set
	 */
	public void setObjectCache(BBBObjectCache pObjectCache) {
		this.mObjectCache = pObjectCache;
	}

	/**
	 * @return the mObjectCache
	 */
	public BBBObjectCache getmObjectCache() {
		return mObjectCache;
	}

	/**
	 * @param mObjectCache the mObjectCache to set
	 */
	public void setmObjectCache(BBBObjectCache mObjectCache) {
		this.mObjectCache = mObjectCache;
	}

	public BBBWebCacheIF getCacheContainer() {
        return cacheContainer;
    }

    public void setCacheContainer(BBBWebCacheIF cacheContainer) {
        this.cacheContainer = cacheContainer;
    }

    /**
	 * @return the context
	 */
	public JAXBContext getContext() {
		return context;
	}

	/**
	 * @param context
	 *            the context to set
	 */
	public void setContext(JAXBContext context) {
		this.context = context;
	}

	/*
	 * This Method is called when the new JMS message is received to the Queue
	 * 
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	
	@Override
	public void onMessage(Message message) {

		Unmarshaller unMarshaller = null;

		try {

			if (message.getJMSType() != null && getDestinationFactory().getDestination(getService()).getJmsType() != null
					&& message.getJMSType().equals(getDestinationFactory().getDestination(getService()).getJmsType())) {

				TextMessage txtMessage = (TextMessage) message;
				if (isLoggingDebug()) {
					logDebug("Message Type" + message.getJMSType());
				}
				if (getContext() == null) {
					setContext(JAXBContext.newInstance(Main.class));
				}

				unMarshaller = getContext().createUnmarshaller();
				Source source = new StreamSource(new StringReader(txtMessage.getText()));
				Main request = (Main) unMarshaller.unmarshal(source);
				if (isLoggingDebug()) {
					logDebug("*********Message Recieved***********");
					logDebug(txtMessage.getText());
				}
				CacheInvalidatorVO invalidatorVo = new CacheInvalidatorVO();
				invalidatorVo.setClearDropletCache(request.getCACHEINVALIDATERECORD().isCLEARDROPLETCACHE());
				invalidatorVo.setClearObjectCache(request.getCACHEINVALIDATERECORD().isCLEAROBJECTCACHE());
				invalidatorVo.setObjectCacheTypes(request.getCACHEINVALIDATERECORD().getOBJECTCACHETYPE());
				clearCache(invalidatorVo);
			}
		} catch (JMSException JMSe) {
				logError("Messaging Exception :::" + JMSe);
		} catch (JAXBException jaxbe) {
				logError("Parssing Exception :::" + jaxbe);
		}catch (Exception e) {
				logError("Exception :::" + e);
		}

	}
	
	private void clearCache(CacheInvalidatorVO vo){
		if(vo.isClearDropletCache()){
			if(vo.getObjectCacheTypes()!=null && !vo.getObjectCacheTypes().isEmpty()){
				for(String cache:vo.getObjectCacheTypes()){
					getCacheContainer().clearCache(cache);
				}
			}else{
			    getCacheContainer().clearCache(null);
			}
		}
		if(vo.isClearObjectCache()){
			if(vo.getObjectCacheTypes()!=null && !vo.getObjectCacheTypes().isEmpty()){
				for(String cache:vo.getObjectCacheTypes()){
					getObjectCache().clearCache(cache);
				}
			}else{
				getObjectCache().clearCache(null);
			}
		}
	}
	
	
}
