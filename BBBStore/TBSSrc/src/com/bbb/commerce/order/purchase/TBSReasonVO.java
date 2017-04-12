package com.bbb.commerce.order.purchase;

import java.io.Serializable;

public class TBSReasonVO implements Serializable{
	
	private String reason;
	private String code;
			
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
}
