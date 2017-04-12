/*
 *
 * File  : CreateRegistryResVO.java
 * Project:     BBB
 */
package com.bbb.commerce.giftregistry.vo;

import com.bbb.framework.integration.ServiceResponseBase;

// TODO: Auto-generated Javadoc
/**
 * The Class RegistryResVO.
 *
 * @author ssha53
 */
public class RegistryResVO extends ServiceResponseBase {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The registry id. */
	private long registryId;

	/** The service error vo. */
	private ServiceErrorVO serviceErrorVO;
	
	/** The registry token. */
	private String registryToken;
	
    /** The registry vo. */
    private RegistryVO registryVO;
    
    /** The registry summary vo. */
    private RegistrySummaryVO registrySummaryVO;
    
    /** The registry search vo. */
    private RegistrySearchVO registrySearchVO;
    
    /** The imported as reg. */
    private boolean importedAsReg;

	/**
	 * Gets the registry summary vo.
	 *
	 * @return the registrySummaryVO
	 */
	public RegistrySummaryVO getRegistrySummaryVO() {
		return registrySummaryVO;
	}

	/**
	 * Sets the registry summary vo.
	 *
	 * @param registrySummaryVO the registrySummaryVO to set
	 */
	public void setRegistrySummaryVO(RegistrySummaryVO registrySummaryVO) {
		this.registrySummaryVO = registrySummaryVO;
	}

	/**
	 * Gets the registry id.
	 *
	 * @return the registryId
	 */
	public long getRegistryId() {
		return registryId;
	}

	/**
	 * Sets the registry id.
	 *
	 * @param pRegistryId the registryId to set
	 */
	public void setRegistryId(long pRegistryId) {
		registryId = pRegistryId;
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
	 * Gets the registry token.
	 *
	 * @return the registryToken
	 */
	public String getRegistryToken() {
		return registryToken;
	}

	/**
	 * Sets the registry token.
	 *
	 * @param pRegistryToken the registryToken to set
	 */
	public void setRegistryToken(String pRegistryToken) {
		registryToken = pRegistryToken;
	}

	/**
	 * Gets the registry vo.
	 *
	 * @return the registryVO
	 */
	public RegistryVO getRegistryVO() {
		return registryVO;
	}

	/**
	 * Sets the registry vo.
	 *
	 * @param pRegistryVO the registryVO to set
	 */
	public void setRegistryVO(RegistryVO pRegistryVO) {
		registryVO = pRegistryVO;
	}

	/**
	 * Gets the registry search vo.
	 *
	 * @return the registrySearchVO
	 */
	public RegistrySearchVO getRegistrySearchVO() {
		return registrySearchVO;
	}

	/**
	 * Sets the registry search vo.
	 *
	 * @param registrySearchVO the registrySearchVO to set
	 */
	public void setRegistrySearchVO(RegistrySearchVO registrySearchVO) {
		this.registrySearchVO = registrySearchVO;
	}

	/**
	 * Gets the imported as reg.
	 *
	 * @return the imported as reg
	 */
	public boolean getImportedAsReg() {
		return importedAsReg;
	}

	/**
	 * Sets the imported as reg.
	 *
	 * @param isImportedAsReg the new imported as reg
	 */
	public void setImportedAsReg(boolean isImportedAsReg) {
		this.importedAsReg = isImportedAsReg;
	}

}