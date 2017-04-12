/*
 *
 * File  : ResponseErrorVO.java
 * Project:     BBB
 *
 */
package com.bbb.framework.integration.vo;

import java.io.Serializable;

import com.bbb.framework.integration.exception.ServiceError;
import com.bbb.framework.integration.exception.ServiceExceptionConstants.ServiceErrorType;
import com.bbb.framework.integration.exception.ServiceExceptionConstants.ServiceResponseError;
import com.bbb.framework.performance.BBBPerformanceMonitor;

/**
 * The Class ResponseErrorVO.
 * 
 * @version 1.0
 */
public class ResponseErrorVO implements ErrorVOIF, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1666722594185987252L;

	/** The error type. */
	private ServiceErrorType errorType;
	
	/** The error code. */
	private ServiceResponseError errorCode;
	
	/** The error msg. */
	private String errorMsg;
	
	/** The actor. */
	private String actor;
	
	/** The trans id. */
	private String transId;
	
	/** The error fields. */
	private String[] errorFields;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String toString() {
		BBBPerformanceMonitor
		.start("ResponseErrorVO-toString");
		final StringBuilder sb = new StringBuilder();
		sb.append("Error Code : ");
		sb.append(errorCode);
		sb.append("| Error Type : ");
		sb.append(errorType);
		sb.append("| Error Msg : ");
		sb.append(errorMsg);
		sb.append("| Trans-Id : ");
		sb.append(transId);
		sb.append("| Actor : ");
		sb.append(actor);
		sb.append("| Error in fields : ");
		if (errorFields != null) {
			for (String fld : errorFields) {
				sb.append(fld).append(", ");
			}			
		}
		BBBPerformanceMonitor
		.end("ResponseErrorVO-toString");
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see com.sprint.integration.api.vo.ErrorVOIF#getErrorCode()
	 */
	public final ServiceError getErrorCode() {
		return errorCode;
	}
	
	/**
	 * Sets the error code.
	 * 
	 * @param errorCode the new error code
	 */
	public void setErrorCode(final ServiceError errorCode) {
		if(errorCode instanceof ServiceResponseError){
			this.errorCode = (ServiceResponseError) errorCode;
			this.errorType = errorCode.getErrorType();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.sprint.integration.api.vo.ErrorVOIF#getTransId()
	 */
	public final String getTransId() {
		return transId;
	}
	
	/**
	 * Sets the trans id.
	 * 
	 * @param transId the new trans id
	 */
	public void setTransId(final String transId) {
		this.transId = transId;
	}
	
	/* (non-Javadoc)
	 * @see com.sprint.integration.api.vo.ErrorVOIF#getErrorFields()
	 */
	public final String[] getErrorFields() {
		return errorFields;
	}
	
	/**
	 * Sets the error fields.
	 * 
	 * @param errorFields the new error fields
	 */
	public void setErrorFields(final String[] errorFields) {
		this.errorFields = errorFields;
	}
	
	/* (non-Javadoc)
	 * @see com.sprint.integration.api.vo.ErrorVOIF#getErrorType()
	 */
	public final ServiceErrorType getErrorType() {
		return errorType;
	}
	
	/**
	 * Sets the error type.
	 * 
	 * @param errorType the new error type
	 */
	public void setErrorType(final ServiceErrorType errorType) {
		this.errorType = errorType;
	}
	
	/* (non-Javadoc)
	 * @see com.sprint.integration.api.vo.ErrorVOIF#getErrorMsg()
	 */
	public final String getErrorMsg() {
		return errorMsg;
	}
	
	/**
	 * Sets the error msg.
	 * 
	 * @param errorMsg the new error msg
	 */
	public void setErrorMsg(final String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	/* (non-Javadoc)
	 * @see com.sprint.integration.api.vo.ErrorVOIF#getActor()
	 */
	public final String getActor() {
		return actor;
	}
	
	/**
	 * Sets the actor.
	 * 
	 * @param actor the new actor
	 */
	public void setActor(final String actor) {
		this.actor = actor;
	}
}
