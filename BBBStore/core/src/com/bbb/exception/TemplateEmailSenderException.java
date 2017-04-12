package com.bbb.exception;

public class TemplateEmailSenderException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public TemplateEmailSenderException(String errorMsg) {
		super(errorMsg);
	}
	public TemplateEmailSenderException(String errorMsg, Throwable cause) {
		super(errorMsg,cause);
	}
}
