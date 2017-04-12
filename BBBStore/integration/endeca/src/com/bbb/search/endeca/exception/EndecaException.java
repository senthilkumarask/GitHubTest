/**
 * 
 */
package com.bbb.search.endeca.exception;

import com.bbb.search.exception.SearchClientException;

/**
 * @author agoe21
 *
 */
public class EndecaException extends SearchClientException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public EndecaException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public EndecaException(final String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public EndecaException(final Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public EndecaException(final String arg0, final Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

}
