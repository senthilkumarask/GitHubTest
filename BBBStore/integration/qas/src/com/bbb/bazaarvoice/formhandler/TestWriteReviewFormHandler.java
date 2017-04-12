package com.bbb.bazaarvoice.formhandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import atg.droplet.DropletException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.common.BBBGenericFormHandler;
import com.bbb.utils.BBBUtility;

public class TestWriteReviewFormHandler extends BBBGenericFormHandler {

	private String campaignType		= null;
	private String transactionID 	= null;
	private String evnetType 		= null;
	private String customerType 	= null;
	private String sku	= null;
	private String token 			= null;
	private String sucessUrl		= null;
	private String errorUrl			= null;
	
	public String getCampaignType() {
		return campaignType;
	}
	public void setCampaignType(String campaignType) {
		this.campaignType = campaignType;
	}
	public String getTransactionID() {
		return transactionID;
	}
	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}
	
	public String getCustomerType() {
		return customerType;
	}
	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	public String getSucessUrl() {
		return sucessUrl;
	}
	public void setSucessUrl(String sucessUrl) {
		this.sucessUrl = sucessUrl;
	}
	public String getErrorUrl() {
		return errorUrl;
	}
	public void setErrorUrl(String errorUrl) {
		this.errorUrl = errorUrl;
	}
	public String getEvnetType() {
		return evnetType;
	}
	public void setEvnetType(String evnetType) {
		this.evnetType = evnetType;
	}
	private void validateFormData(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, Map<String, String> errorPlaceHolderMap) {

		logDebug("TestWriteReviewFormHandler.validateFormData() method started");
		if (BBBUtility.isEmpty(getCampaignType())) {
			errorPlaceHolderMap.put("fieldName", "Compaign Type");
			addFormException(new DropletException("Compaign Type is Mandatory"));
		}
		if (BBBUtility.isEmpty(getTransactionID())) {
			errorPlaceHolderMap.put("fieldName", "Transaction ID");
			addFormException(new DropletException("Transaction ID is Mandatory"));
		}
		if (BBBUtility.isEmpty(getCustomerType())) {
			errorPlaceHolderMap.put("fieldName", "Customer Type");
			addFormException(new DropletException("Customer Type is Mandatory"));
		}
		if (getCustomerType().equals("S")
				&& BBBUtility.isEmpty(getToken())) {
			errorPlaceHolderMap.put("fieldName", "Token");
			addFormException(new DropletException("Token is Mandatory"));
		}
		if (BBBUtility.isEmpty(getSku())) {
			errorPlaceHolderMap.put("fieldName", "Purchased Item");
			addFormException(new DropletException("Purchased Item is Mandatory"));
		}
		logDebug("TestWriteReviewFormHandler.validate() method started");
	}
	
	public boolean handleClear(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		setCampaignType("");
		setCustomerType("");
		setSku("");
		setToken("");
		setTransactionID("");
		return true;
	}
	
	public boolean handleWriteReview(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		logDebug("TestEmailFormHandler.handleSendEmail() method started");
		Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();
		validateFormData(pRequest, pResponse, errorPlaceHolderMap);
		if (getFormError()) {
			return false;
		}
		if (getCustomerType().equals("R")) {
			pResponse.sendRedirect(getSucessUrl()+"?campaignType="+campaignType+"&customerType="+ customerType +"&transactionID="+ transactionID +"&eventType="+ evnetType +"&sku="+ sku);
		}
		else {
			pResponse.sendRedirect(getSucessUrl()+"?campaignType="+campaignType+"&customerType="+ customerType +"&transactionID="+ transactionID +"&token="+ token +"&sku="+ sku);
		}
		logDebug("TestEmailFormHandler.handleSendEmail() method ends");
		return true;
	}
	
}
