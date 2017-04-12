/*
 *
 * File  : ServiceRequestException.java
 * Project:     BBB
 */
package com.bbb.framework.integration.exception;

import com.bbb.framework.integration.exception.ServiceExceptionConstants.ServiceRequestError;
import com.bbb.framework.integration.vo.ErrorVOIF;
import com.bbb.framework.integration.vo.RequestErrorVO;

/**
 * The Class ServiceRequestException.
 * 
 * 
 * @version 1.0
 */
public class ServiceRequestException extends ServiceException {

	/** serialVersionUID. */
	private static final long serialVersionUID = 1L;

	// ~ Instance variables -----------------------------------------
	/** The error vo. */
	private ErrorVOIF errorVO;

	// ~ Constructors ------------------------------------------------

	/**
	 * TODO Creates a new RegServiceException object.
	 * 
	 * @param errorVO
	 *            the error vo
	 */
	public ServiceRequestException(final ErrorVOIF errorVO) {
		super(errorVO.getErrorMsg());
		this.errorVO=errorVO;
	}

	public ServiceRequestException(final ServiceRequestError errConst,
			final Exception e) {
		super(e.getMessage());
		final RequestErrorVO errorVO = new RequestErrorVO();
		errorVO.setErrorCode(errConst);
		errorVO.setErrorMsg(e.toString());
		this.errorVO=errorVO;
	}

	/**
	 * Instantiates a new service request exception.
	 * 
	 * @param pException
	 *            the exception
	 */
	public ServiceRequestException(final Exception pException) {
		super(pException);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbb.integration.api.exception.ServiceException#setErrorVO(com.bbb
	 * .integration.api.vo.ErrorVOIF)
	 */
	public void setErrorVO(final ErrorVOIF errorVO2) {
		this.errorVO = errorVO2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbb.integration.api.exception.ServiceException#getErrorVO()
	 */
	public ErrorVOIF getErrorVO() {
		return errorVO;
	}

}
