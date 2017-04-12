/*
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 * 
 * Reproduction or use of this file without explicit written consent is prohibited.
 * 
 * Created by: Archit Goel
 * 
 * Created on: 13-October-2011
 * --------------------------------------------------------------------------------
 */

package com.bbb.search.exception;

/**
 * This is Exception class in case of any Exception thrown from SearchClient.
 * 
 * @author agoe21
 */

public class SearchClientException extends Exception {

	/* ===================================================== *
		MEMBER VARIABLES
	* ===================================================== */
	private static final long serialVersionUID = 1L;

	/* ===================================================== *
		CONSTRUCTORS
	* ===================================================== */
	public SearchClientException() {
		// TODO Auto-generated constructor stub
	}

	public SearchClientException(final String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public SearchClientException(final Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public SearchClientException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}
}
