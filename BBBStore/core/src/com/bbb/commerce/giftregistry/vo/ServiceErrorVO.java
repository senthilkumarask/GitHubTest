/*
 * 
 */
package com.bbb.commerce.giftregistry.vo;

import java.io.Serializable;


// TODO: Auto-generated Javadoc
/**
 * This class provides the Registry error information properties.
 *
 * @author sku134
 */
public class ServiceErrorVO implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The error exists. */
	private boolean errorExists;
	
	/** The error id. */
	private int errorId;
	
	/** The error message. */
	private String errorMessage;
	
	/** The error display message. */
	private String errorDisplayMessage;
	
	/**
	 * constructor stub.
	 */
	public ServiceErrorVO() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Checks if is error exists.
	 *
	 * @return the errorExists
	 */
	public boolean isErrorExists() {
		return errorExists;
	}
	
	/**
	 * Sets the error exists.
	 *
	 * @param errorExists the errorExists to set
	 */
	public void setErrorExists(boolean errorExists) {
		this.errorExists = errorExists;
	}
	
	/**
	 * Gets the error id.
	 *
	 * @return the errorId
	 */
	public int getErrorId() {
		return errorId;
	}
	
	/**
	 * Sets the error id.
	 *
	 * @param errorId the errorId to set
	 */
	public void setErrorId(int errorId) {
		this.errorId = errorId;
	}
	
	/**
	 * Gets the error message.
	 *
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	
	/**
	 * Sets the error message.
	 *
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	/**
	 * Gets the error display message.
	 *
	 * @return the errorDisplayMessage
	 */
	public String getErrorDisplayMessage() {
		return errorDisplayMessage;
	}
	
	/**
	 * Sets the error display message.
	 *
	 * @param errorDisplayMessage the errorDisplayMessage to set
	 */
	public void setErrorDisplayMessage(String errorDisplayMessage) {
		this.errorDisplayMessage = errorDisplayMessage;
	}
}
