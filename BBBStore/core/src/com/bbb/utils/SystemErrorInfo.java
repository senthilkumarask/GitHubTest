package com.bbb.utils;

import java.util.ArrayList;
import java.util.List;

import com.bbb.common.BBBGenericService;

/**
 * @author Rajesh Saini
 * 
 */

public class SystemErrorInfo extends BBBGenericService {

	private List<ErrorInfoVO> errorList = new ArrayList<ErrorInfoVO>();

	/**
	 * @return the errorList
	 */
	public List<ErrorInfoVO> getErrorList() {
		return errorList;
	}

	/**
	 * @param errorList
	 *            the errorList to set
	 */
	public void setErrorList(List<ErrorInfoVO> errorList) {
		this.errorList = errorList;
	}

}
