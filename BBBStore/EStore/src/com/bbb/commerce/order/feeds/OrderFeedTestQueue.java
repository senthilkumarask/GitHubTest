package com.bbb.commerce.order.feeds;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import atg.nucleus.ServiceException;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.util.ServiceHandlerUtil;

import com.bbb.common.BBBGenericService;

/**
 * This class is for testing purpose only. The class sends a test order feed xml
 * on the test queue so that we can check that OrderFeedMessageListener is
 * invoked properly once order feed message is received by the queue
 * 
 * @author njai13
 * 
 */
public class OrderFeedTestQueue extends BBBGenericService {

	private String configurable = FALSE;
	
	private String xmlPath;

	private String xml;

	public String getXmlPath() {
		return xmlPath;
	}

	public void setXmlPath(String xmlPath) {
		this.xmlPath = xmlPath;
	}

	@Override
	public void doStartService() throws ServiceException {
		super.doStartService();

		try {
			sendOrderFeedXMLString();
		} catch (BBBBusinessException e) {
			logWarning(e);
		} catch (BBBSystemException e) {
			logWarning(e);
		}

	}

	public void sendOrderFeedXMLString() throws BBBBusinessException,
			BBBSystemException {
			logDebug("OrderFeedTestQueue :sendOrderFeedXMLString ");
		final OrderFeedVO orderFeedVO = new OrderFeedVO();
		StringBuffer xml = new StringBuffer("");
		int ch = 0;
		FileInputStream fstream = null;
		try {
			fstream = new FileInputStream(getXmlPath());
			while (ch != -1) {
				xml.append((char) ch);
				ch = fstream.read();
			}
		} catch (FileNotFoundException e) {
			logWarning("File can't be read at OrderStatusFeed.unmarshal()");
		} catch (IOException io) {
			logWarning("IO Exception occurred");
		}
		finally{
			if(fstream!=null)
				try {
					fstream.close();
				} catch (IOException e) {
					logError("IO exception while closing stream", e);
				}
		}

		if(isConfigurable()){
			xml = new StringBuffer(getXml());
		}else if (!(xml.length() > 0)) {
			xml = new StringBuffer(getXml());
		}

		
		logDebug("xml in queue " + xml);
		orderFeedVO.setXml(xml.toString());
		ServiceHandlerUtil.send(orderFeedVO);

	}

	/**
	 * @return the xml
	 */
	public String getXml() {
		return xml;
	}

	/**
	 * @param xml
	 *            the xml to set
	 */
	public void setXml(String xml) {
		this.xml = xml;
	}

	/**
	 * @return the configurable
	 */
	public boolean isConfigurable() {
		return (TRUE.equalsIgnoreCase(getConfigurable())) ? true : false;
	}
	
	/**
	 * @return the configurable
	 */
	public String getConfigurable() {
		return configurable;
	}

	/**
	 * @param configurable the configurable to set
	 */
	public void setConfigurable(String configurable) {
		this.configurable = configurable;
	}
	
	private static final String FALSE = "false";
	private static final String TRUE = "true";
}
