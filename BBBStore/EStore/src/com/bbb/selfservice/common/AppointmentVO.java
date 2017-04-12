package com.bbb.selfservice.common;

import java.io.Serializable;
import java.util.List;

public class AppointmentVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<String> mAppointmentTypes;

	public List<String> getAppointmentTypes() {
		return mAppointmentTypes;
	}

	public void setAppointmentTypes(List<String> pAppointmentTypes) {
		mAppointmentTypes = pAppointmentTypes;
	}
}
