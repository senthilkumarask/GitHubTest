package com.bbb.commerce.inventory.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author pku104
 *
 */
public class InventoryResponseVO implements Serializable {

	private static final long serialVersionUID = 7844135047289804859L;
	private String availability;
	private String transactionDateTime;
	private String viewName;
	private String viewConfiguration;
	private Map<String, List<SupplyBalanceAvlDetailsVO>> availabilityDetails;
	private Map<String, List<SupplyBalanceErrorMessageVO>> messages;
	/**
	 * @return the availability
	 */
	public String getAvailability() {
		return availability;
	}
	/**
	 * @param availability the availability to set
	 */
	public void setAvailability(String availability) {
		this.availability = availability;
	}
	/**
	 * @return the transactionDateTime
	 */
	public String getTransactionDateTime() {
		return transactionDateTime;
	}
	/**
	 * @param transactionDateTime the transactionDateTime to set
	 */
	public void setTransactionDateTime(String transactionDateTime) {
		this.transactionDateTime = transactionDateTime;
	}
	/**
	 * @return the viewName
	 */
	public String getViewName() {
		return viewName;
	}
	/**
	 * @param viewName the viewName to set
	 */
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	/**
	 * @return the viewConfiguration
	 */
	public String getViewConfiguration() {
		return viewConfiguration;
	}
	/**
	 * @param viewConfiguration the viewConfiguration to set
	 */
	public void setViewConfiguration(String viewConfiguration) {
		this.viewConfiguration = viewConfiguration;
	}
	/**
	 * @return the availabilityDetails
	 */
	public Map<String, List<SupplyBalanceAvlDetailsVO>> getAvailabilityDetails() {
		return availabilityDetails;
	}
	/**
	 * @param availabilityDetails the availabilityDetails to set
	 */
	public void setAvailabilityDetails(
			Map<String, List<SupplyBalanceAvlDetailsVO>> availabilityDetails) {
		this.availabilityDetails = availabilityDetails;
	}
	/**
	 * @return
	 */
	public Map<String, List<SupplyBalanceErrorMessageVO>> getMessages() {
		return messages;
	}
	/**
	 * @param messages
	 */
	public void setMessages(Map<String, List<SupplyBalanceErrorMessageVO>> messages) {
		this.messages = messages;
	}

}
