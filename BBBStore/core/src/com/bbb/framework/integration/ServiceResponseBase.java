/*
 *
 * File  : ServiceResponseBase.java
 * Project:     BBB
 * 
 */
package com.bbb.framework.integration;

import com.bbb.framework.integration.vo.ResponseErrorVO;

/**
 * The Class ServiceResponseBase.
 * 
 * @version 1.0
 */
public class ServiceResponseBase implements ServiceResponseIF {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The error. */
	private ResponseErrorVO error = null;

	public final ResponseErrorVO getError() {
		return this.error;
	}

	public void setError(final ResponseErrorVO error) {
		this.error = error;
	}

	public final boolean hasError() {
		if (this.error != null) {
			return true;
		}
		return false;
	}
	
	public boolean webServiceError = false;

	public boolean isWebServiceError() {
		return this.webServiceError;
	}

	public void setWebServiceError(boolean webServiceError) {
		this.webServiceError = webServiceError;
	}

}
