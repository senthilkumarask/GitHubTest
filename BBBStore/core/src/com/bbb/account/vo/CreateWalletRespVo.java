package com.bbb.account.vo;

//import com.bbb.framework.integration.ServiceRequestBase;
import com.bbb.framework.integration.ServiceResponseBase;
import com.bbb.framework.webservices.vo.ErrorStatus;

public class CreateWalletRespVo extends ServiceResponseBase{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String walletInfomation;
	private ErrorStatus mErrorStatus;
	private boolean webserviceError;
	private String walletId;
	private String walletUrl;
	
	public boolean isWebserviceError() {
		return webserviceError;
	}
	public void setWebserviceError(boolean webserviceError) {
		this.webserviceError = webserviceError;
	}
	public String getWalletInfomation() {
		return walletInfomation;
	}
	public void setWalletInfomation(String walletInfomation) {
		this.walletInfomation = walletInfomation;
	}
	public ErrorStatus getmErrorStatus() {
		return mErrorStatus;
	}
	public void setmErrorStatus(ErrorStatus mErrorStatus) {
		this.mErrorStatus = mErrorStatus;
	}
	public String getWalletId() {
		return walletId;
	}
	public void setWalletId(String walletId) {
		this.walletId = walletId;
	}
	public String getWalletUrl() {
		return walletUrl;
	}
	public void setWalletUrl(String walletUrl) {
		this.walletUrl = walletUrl;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	@Override
	public String toString() {
		return "CreateWalletRespVo [walletInfomation=" + walletInfomation
				+ ", mErrorStatus=" + mErrorStatus + ", webserviceError="
				+ webserviceError + ", walletId=" + walletId + ", walletUrl="
				+ walletUrl + "]";
	}
	
	
	

}