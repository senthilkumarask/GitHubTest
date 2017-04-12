package com.bbb.commerce.order.feeds;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

import atg.nucleus.ServiceException;

import com.bbb.common.BBBGenericService;

public class JMSExceptionListenerImpl extends BBBGenericService implements ExceptionListener {

	
	private BBBGenericService bbbGenericService;
	
	/**
	 * @return the orderFeedMessageListener
	 */
	public BBBGenericService getBbbGenericService() {
		return bbbGenericService;
	}

	/**
	 * @param orderFeedMessageListener the orderFeedMessageListener to set
	 */
	public void setBbbGenericService(
			BBBGenericService bbbGenericService) {
		this.bbbGenericService = bbbGenericService;
	}

	@Override
	public void onException(JMSException ex) {
		
	
		this.logDebug("connection lost...Restarting Service "+bbbGenericService.getClass(),ex);
		try {
			this.bbbGenericService.doStopService();
			this.bbbGenericService.doStartService();
			this.logDebug("Re-connected to the JMS Server");
		} catch (ServiceException e) {
			this.logDebug("ServiceException occured while Restarting Service "+bbbGenericService.getClass(),e);
		}
		
		
	}
	
	

}
