/*
 *
 * File  : ServiceResponseException.java
 * Project:     BBB
 */

package com.bbb.framework.integration.exception;

import com.bbb.framework.integration.exception.ServiceExceptionConstants.ServiceResponseError;
import com.bbb.framework.integration.vo.ErrorVOIF;
import com.bbb.framework.integration.vo.ResponseErrorVO;

/**
 * The Class ServiceResponseException.
 */
public class ServiceResponseException extends ServiceException {

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
	public ServiceResponseException(final ErrorVOIF errorVO) {
		super((errorVO == null ? null : errorVO.getErrorMsg()));
		this.errorVO=errorVO;
	}

	public ServiceResponseException(final ServiceResponseError errConst,
			final Exception e) {
		super((e == null ? null : e.getMessage()));
		final ResponseErrorVO errorVO = new ResponseErrorVO();
		errorVO.setErrorCode(errConst);
		errorVO.setErrorMsg((e == null) ? null : e.toString());
		this.errorVO=errorVO;
	}

	/**
	 * Instantiates a new service response exception.
	 * 
	 * @param pException
	 *            the exception
	 */
	public ServiceResponseException(final Exception pException) {
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
