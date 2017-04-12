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
 * The Class RegSearchResVO.
 *
 * @author ssha53
 */
public class RegSearchResVO extends ServiceResponseBase {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The list registry summary vo. */
	private List<RegistrySummaryVO> listRegistrySummaryVO;

	/** The service error vo. */
	private ServiceErrorVO serviceErrorVO;
	
	/** The tot entries. */
	private int totEntries;
	
	/**
	 * Gets the list registry summary vo.
	 *
	 * @return the list registry summary vo
	 */
	public List<RegistrySummaryVO> getListRegistrySummaryVO() {
		return listRegistrySummaryVO;
	}

	/**
	 * Sets the list registry summary vo.
	 *
	 * @param pListRegistrySummaryVO the new list registry summary vo
	 */
	public final void setListRegistrySummaryVO(
			List<RegistrySummaryVO> pListRegistrySummaryVO) {
		listRegistrySummaryVO = pListRegistrySummaryVO;
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