/**
 * 
 */
package com.bbb.commerce.inventory;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import java.math.BigInteger;
import java.util.Date;

import com.bbb.commerce.inventory.vo.InventoryVO;
import com.bbb.framework.integration.ServiceRequestBase;

/**
 * @author vchan5
 *
 */
public class InventoryDecrementVO extends ServiceRequestBase {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2628433347669126856L;
	
	private String mServiceName = "inventoryJMSMessage";
	
	@Override
	public String getServiceName() {
		return mServiceName;
	}
	
	String messageId;
	String conversationId;
	String producer;
	String consumer;
	String payLoad;
	String messageFormat;
	String coRelationId;
	BigInteger messagePriority;
	XMLGregorianCalendar messageCreateTS;
	
	String dataCenter;
	
	String site;
		
	Date orderSubmissionDate;
	public void setServiceName(String pServiceName) {
		this.mServiceName = pServiceName;
	}
	
	List<InventoryVO> listOfInventoryVos;
	/**
	 * @return the listOfInventoryVos
	 */
	public List<InventoryVO> getListOfInventoryVos() {
		return listOfInventoryVos;
	}
	/**
	 * @param listOfInventoryVos the listOfInventoryVos to set
	 */
	public void setListOfInventoryVos(List<InventoryVO> listOfInventoryVos) {
		this.listOfInventoryVos = listOfInventoryVos;
	}
	public String getDataCenter() {
		return dataCenter;
	}
	public void setDataCenter(String dataCenter) {
		this.dataCenter = dataCenter;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public Date getOrderSubmissionDate() {
		return orderSubmissionDate;
	}
	public void setOrderSubmissionDate(Date orderSubmissionDate) {
		this.orderSubmissionDate = orderSubmissionDate;
	}
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getConversationId() {
		return conversationId;
	}
	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}
	public String getProducer() {
		return producer;
	}
	public void setProducer(String producer) {
		this.producer = producer;
	}
	public String getConsumer() {
		return consumer;
	}
	public void setConsumer(String consumer) {
		this.consumer = consumer;
	}
	public String getPayLoad() {
		return payLoad;
	}
	public void setPayLoad(String payLoad) {
		this.payLoad = payLoad;
	}
	public String getMessageFormat() {
		return messageFormat;
	}
	public void setMessageFormat(String messageFormat) {
		this.messageFormat = messageFormat;
	}
	public BigInteger getMessagePriority() {
		return messagePriority;
	}
	public void setMessagePriority(BigInteger messagePriority) {
		this.messagePriority = messagePriority;
	}
	public XMLGregorianCalendar getMessageCreateTS() {
		return messageCreateTS;
	}
	public void setMessageCreateTS(XMLGregorianCalendar messageCreateTS) {
		this.messageCreateTS = messageCreateTS;
	}
	public String getCoRelationId() {
		return coRelationId;
	}
	public void setCoRelationId(String coRelationId) {
		this.coRelationId = coRelationId;
	}	
	
	
}
