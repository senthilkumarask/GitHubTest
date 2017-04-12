/*
 *
 * File  : ValidateRegistryResVO.java
 * Project:     BBB
 */
package com.bbb.commerce.giftregistry.vo;

import com.bbb.framework.integration.ServiceResponseBase;

// TODO: Auto-generated Javadoc
/**
 * The Class RegCopyResVO.
 *
 * @author 
 */
public class RegCopyResVO extends ServiceResponseBase {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The service error vo. */
	private ServiceErrorVO serviceErrorVO;
	private Boolean isSetRegCopyResult;
	private String totalNumOfItemsCopied;
	
	
	public String gettotalNumOfItemsCopied() {
		return totalNumOfItemsCopied;
	}


	public void settotalNumOfItemsCopied(String totalNumOfItemsCopied) {
		this.totalNumOfItemsCopied = totalNumOfItemsCopied;
	}


	public Boolean getIsSetRegCopyResult() {
		return isSetRegCopyResult;
	}


	public Boolean isSetRegCopyResult() {
		return isSetRegCopyResult;
	}


	public void setSetRegCopyResult(Boolean isSetRegCopyResult) {
		this.isSetRegCopyResult = isSetRegCopyResult;
	}


	/**
	 * @return the serviceErrorVO
	 */
	public ServiceErrorVO getServiceErrorVO() {
		if (serviceErrorVO != null) {
			return serviceErrorVO;
		} else {
			serviceErrorVO = new ServiceErrorVO();
			return serviceErrorVO;

		}
	}
	

	/**
	 * Sets the service error vo.
	 *
	 * @param pServiceErrorVO the serviceErrorVO to set
	 */
	public void setServiceErrorVO(ServiceErrorVO pServiceErrorVO) {
		serviceErrorVO = pServiceErrorVO;
	}

}