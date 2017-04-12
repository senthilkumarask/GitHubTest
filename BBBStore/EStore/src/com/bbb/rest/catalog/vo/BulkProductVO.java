package com.bbb.rest.catalog.vo;
import java.io.Serializable;
import java.util.Map;


public class BulkProductVO implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String errorMessage;
	private boolean isErrorExist;
	private String errorCode;
	private Map<String, ProductMobileVO> productMobileVO ;
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public boolean isErrorExist() {
		return isErrorExist;
	}
	public void setErrorExist(boolean isErrorExist) {
		this.isErrorExist = isErrorExist;
	}
	public Map<String, ProductMobileVO> getProductMobileVO() {
		return productMobileVO;
	}
	public void setProductMobileVO(Map<String, ProductMobileVO> productMobileVO) {
		this.productMobileVO = productMobileVO;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	
}
