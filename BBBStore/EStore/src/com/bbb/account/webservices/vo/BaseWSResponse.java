package com.bbb.account.webservices.vo;

import java.io.Serializable;
import java.util.Arrays;

public class BaseWSResponse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean error;
	private String[] errorCodes;
	private String[] messages;
	/**
	 * @return the error
	 */
	public boolean isError() {
		return error;
	}
	/**
	 * @param error the error to set
	 */
	public void setError(final boolean error) {
		this.error = error;
	}
	/**
	 * @return the errorCodes
	 */
	public String[] getErrorCodes() {
		return errorCodes==null?errorCodes:errorCodes.clone();
	}
	/**
	 * @param errorCodes the errorCodes to set
	 */
	public void setErrorCodes(final String[] pErrorCodes) {
		this.errorCodes = pErrorCodes==null?pErrorCodes:pErrorCodes.clone();
	}
	/**
	 * @return the messages
	 */
	public String[] getMessages() {
		return messages;
	}
	/**
	 * @param messages the messages to set
	 */
	public void setMessages(final String[] pMessages) {
		this.messages = pMessages==null?pMessages:pMessages.clone();
	}

	@Override
	public String toString() {
		return "BaseWSResponse [error=" + error + ", errorCodes="
				+ Arrays.toString(errorCodes) + ", messages="
				+ Arrays.toString(messages) + "]";
	}
}
