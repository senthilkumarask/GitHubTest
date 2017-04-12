package com.bbb.commerce.order.purchase;

public class TBSPriceOverrideErrorMessageVO {
	
	private String mFieldId;
	private String mMessage;
	
	public TBSPriceOverrideErrorMessageVO(String pFieldId,String pMessage){
		this.mFieldId = pFieldId;
		this.mMessage = pMessage;
	}

	public String getFieldId() {
		return mFieldId;
	}

	public void setmFieldId(String pFieldId) {
		this.mFieldId = pFieldId;
	}

	public String getMessage() {
		return mMessage;
	}

	public void setMessage(String pMessage) {
		this.mMessage = pMessage;
	}

}
