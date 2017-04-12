package com.bbb.selfservice.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
	
public class NICAvailabiltyResponseVO  implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4161456095100336435L;
	private String transactionDateTime;
	private String viewName;
	private String viewConfiguration;
	private Map<String,List<NICAvailabilityResponseDetailVO>> availabilityDetails;
	private Map<String, List<NICResponseErrorMessageVO>> messages;
	
	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	public String getViewConfiguration() {
		return viewConfiguration;
	}
	public void setViewConfiguration(String viewConfiguration) {
		this.viewConfiguration = viewConfiguration;
	}
	public Map<String, List<NICAvailabilityResponseDetailVO>> getAvailabilityDetails() {
		return availabilityDetails;
	}
	public void setAvailabilityDetails(
			Map<String, List<NICAvailabilityResponseDetailVO>> availabilityDetails) {
		this.availabilityDetails = availabilityDetails;
	}
	public Map<String, List<NICResponseErrorMessageVO>> getMessages() {
		return messages;
	}
	public void setMessages(Map<String, List<NICResponseErrorMessageVO>> messages) {
		this.messages = messages;
	}
	public String getTransactionDateTime() {
		return transactionDateTime;
	}
	public void setTransactionDateTime(String transactionDateTime) {
		this.transactionDateTime = transactionDateTime;
	}
	
}
