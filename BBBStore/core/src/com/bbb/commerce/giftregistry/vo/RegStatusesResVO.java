/*
 *
 * File  : ValidateRegistryResVO.java
 * Project:     BBB
 */
package com.bbb.commerce.giftregistry.vo;


import java.util.List;

import com.bbb.framework.integration.ServiceResponseBase;

// TODO: Auto-generated Javadoc
/**
 * The Class RegStatusesResVO.
 *
 * @author prbhoomu
 */
public class RegStatusesResVO extends ServiceResponseBase {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The list registry summary vo. */
	private List<RegistryStatusVO> listRegistryStatusVO;

	/** The service error vo. */
	private ServiceErrorVO serviceErrorVO;
	
	/** The tot entries. */
	private int totEntries;
	

	/**
	 * Gets the list of RegistrStatusesVO.
	 *
	 * @return the serviceErrorVO
	 */	
	public List<RegistryStatusVO> getListRegistryStatusVO() {
		return listRegistryStatusVO;
	}

	/**
	 * sets the list of RegistrStatusesVO .
	 *
	 * @param listRegistryStatusVO the RegistryStatusVO to set
	 */	
	public void setListRegistryStatusVO(List<RegistryStatusVO> listRegistryStatusVO) {
		this.listRegistryStatusVO = listRegistryStatusVO;
	}

	/**
	 * Gets the service error vo.
	 *
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

	/**
	 * Gets the tot entries.
	 *
	 * @return the totEntries
	 */
	public int getTotEntries() {
		return totEntries;
	}
	
	/**
	 * Sets the tot entries.
	 *
	 * @param totEntries the totEntries to set
	 */
	public void setTotEntries(int totEntries) {
		this.totEntries = totEntries;
	}
}