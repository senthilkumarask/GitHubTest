package com.bbb.commerce.catalog.vo;

import java.io.Serializable;


public class AppointmentVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String appointmentCode;
	private String appointmentName;
	public String getAppointmentCode() {
		return appointmentCode;
	}
	public void setAppointmentCode(String pAppointmentCode) {
		appointmentCode = pAppointmentCode;
	}
	public String getAppointmentName() {
		return appointmentName;
	}
	public void setAppointmentName(String pAppointmentName) {
		appointmentName = pAppointmentName;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuffer appointmentDetails = new StringBuffer("");
		appointmentDetails.append(this.appointmentCode + "|");
		appointmentDetails.append(this.appointmentName + "|");
		return appointmentDetails.toString();
		
	}
}
