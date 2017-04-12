package com.bbb.commerce.giftregistry.vo;

import java.io.Serializable;
import java.util.List;
//import java.util.Map;

public class RegistrySummaryListVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean isErrorExist=false;
	private String  errorMessage;
	private String errorCode;
    private List<RegistrySummaryVO> atgResponse;
     
	public boolean isErrorExist() {
		return isErrorExist;
	}
	public void setErrorExist(boolean isErrorExist) {
		this.isErrorExist = isErrorExist;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public List<RegistrySummaryVO> getAtgResponse() {
		return atgResponse;
	}
	public void setAtgResponse(List<RegistrySummaryVO> atgResponse) {
		this.atgResponse = atgResponse;
	}
	
}
