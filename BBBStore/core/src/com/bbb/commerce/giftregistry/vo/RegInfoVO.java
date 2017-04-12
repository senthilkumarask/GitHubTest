package com.bbb.commerce.giftregistry.vo;

import java.io.Serializable;


//import com.bbb.certona.vo.CertonaGiftRegistryVO;

//import atg.core.util.StringUtils;



// TODO: Auto-generated Javadoc
/**
 * This class provides the Registry summary information properties.
 *
 * @author sku134
 */
public class RegInfoVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int registryNum;
	String eventType;
	int eventDate;
	String actionCD;
	String processFlag;
	String onlineRegFlag;
	String giftWrap;
	
	
	public String getOnlineRegFlag() {
		return onlineRegFlag;
	}
	public void setOnlineRegFlag(String onlineRegFlag) {
		this.onlineRegFlag = onlineRegFlag;
	}
	public String getGiftWrap() {
		return giftWrap;
	}
	public void setGiftWrap(String giftWrap) {
		this.giftWrap = giftWrap;
	}
	
	
	public String getActionCD() {
		return actionCD;
	}
	public void setActionCD(String actionCD) {
		this.actionCD = actionCD;
	}
	

	public int getRegistryNum() {
		return registryNum;
	}
	public void setRegistryNum(int registryNum) {
		this.registryNum = registryNum;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public int getEventDate() {
		return eventDate;
	}
	public void setEventDate(int eventDate) {
		this.eventDate = eventDate;
	}
	
	public String getProcessFlag() {
		return processFlag;
	}
	public void setProcessFlag(String processFlag) {
		this.processFlag = processFlag;
	}

	
	
	
	
	
	
	
}