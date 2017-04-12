/*
 *
 * File  : ValidateRegistryResVO.java
 * Project:     BBB
 */
package com.bbb.commerce.giftregistry.vo;

import com.bbb.framework.integration.ServiceResponseBase;

// TODO: Auto-generated Javadoc
/**
 * This class is Response VO for Remove Items from Registry WS
 * Call. 
 * 
 * @author ikhan2
 * 
 */
public class ManageRegItemsResVO extends ServiceResponseBase {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The flag for success of setAnnouncement ws call. */	
	private boolean operationStatus;

	/** The service error vo. */
	private ServiceErrorVO serviceErrorVO;
	
	/**
	 * Checks if is operation status.
	 *
	 * @return the registryId
	 */	
	public boolean isOperationStatus() {
		return operationStatus;
	}

	/**
	 * Sets the operation status.
	 *
	 * @param operationStatus the new operation status
	 */	
	public void setOperationStatus(final boolean operationStatus) {
		this.operationStatus = operationStatus;
	}

	/**
	 * Gets the service error vo.
	 *
	 * @return the serviceErrorVO
	 */
	public ServiceErrorVO getServiceErrorVO() {
		
		if (serviceErrorVO == null) {
			serviceErrorVO = new ServiceErrorVO();
		}
		
		return serviceErrorVO;

	}

	/**
	 * Sets the service error vo.
	 *
	 * @param pServiceErrorVO the serviceErrorVO to set
	 */
	public void setServiceErrorVO(final ServiceErrorVO pServiceErrorVO) {
		serviceErrorVO = pServiceErrorVO;
	}


}