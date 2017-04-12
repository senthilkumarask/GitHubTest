package com.bbb.paypal;


/**
 * @author ssh108
 * 
 */
public class BBBSetExpressCheckoutResVO extends PayPalResponseVO {

	

	private static final long serialVersionUID = 1L;
	private String token;
	private String status;

	/**
	 * @return the status
	 */
	public String getStatus() {
		return this.status;
	}
	
	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return this.token;
	}

	/**
	 * @param token
	 *            the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	
	/**
	 * This method is used for logging purpose
	 * @return
	 */
	public String getDetailVO() {
		StringBuilder builder = new StringBuilder();
		builder.append("BBBSetExpressCheckoutResVO [token=");
		builder.append(this.token);
		builder.append(", status=");
		builder.append(this.status);
		builder.append(", getAck()=");
		builder.append(getAck());
		builder.append(", getCorrelationID()=");
		builder.append(getCorrelationID());
		builder.append(", getTimeStamp()=");
		builder.append(getTimeStamp());
		builder.append(", getErrorStatus()=");
		builder.append(getErrorStatus());
		builder.append(", getError()=");
		builder.append(getError());
		builder.append(", hasError()=");
		builder.append(hasError());
		builder.append(", isWebServiceError()=");
		builder.append(isWebServiceError());
		builder.append("]");
		return builder.toString();
	}

}
