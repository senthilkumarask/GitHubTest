package com.bbb.paypal;

import com.bbb.framework.integration.ServiceResponseBase;
import com.bbb.framework.webservices.vo.ErrorStatus;

/**
 * @author ssh108
 * 
 */
public class PayPalResponseVO extends ServiceResponseBase {

	private static final long serialVersionUID = 1L;
	private String ack;
	private String correlationID;
	private String timeStamp;
	private ErrorStatus errorStatus;
	/**
	 * @return the ack
	 */
	public String getAck() {
		return ack;
	}
	/**
	 * @param ack the ack to set
	 */
	public void setAck(String ack) {
		this.ack = ack;
	}
	/**
	 * @return the correlationID
	 */
	public String getCorrelationID() {
		return correlationID;
	}
	/**
	 * @param correlationID the correlationID to set
	 */
	public void setCorrelationID(String correlationID) {
		this.correlationID = correlationID;
	}
	/**
	 * @return the timeStamp
	 */
	public String getTimeStamp() {
		return timeStamp;
	}
	/**
	 * @param timeStamp the timeStamp to set
	 */
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	/**
	 * @return the errorStatus
	 */
	public ErrorStatus getErrorStatus() {
		return errorStatus;
	}
	/**
	 * @param errorStatus the errorStatus to set
	 */
	public void setErrorStatus(ErrorStatus errorStatus) {
		this.errorStatus = errorStatus;
	}


	
}
