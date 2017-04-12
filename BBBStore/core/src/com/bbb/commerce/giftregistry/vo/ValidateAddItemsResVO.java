/*
 *
 * File  : ValidateRegistryResVO.java
 * Project:     BBB
 */
package com.bbb.commerce.giftregistry.vo;

import com.bbb.framework.integration.ServiceResponseBase;

// TODO: Auto-generated Javadoc
/**
 * The Class ValidateAddItemsResVO.
 *
 * @author ssha53
 */
public class ValidateAddItemsResVO extends ServiceResponseBase {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The service error vo. */
	private ServiceErrorVO serviceErrorVO;
	private int count;
	
	
	/**
	 * Gets the service error vo.
	 *
	 * @return the registryId
	 */
	

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}



	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
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