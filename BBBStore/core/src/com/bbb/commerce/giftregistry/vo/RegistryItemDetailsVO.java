/**
 * 
 */
package com.bbb.commerce.giftregistry.vo;

import java.io.Serializable;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class RegistryItemDetailsVO.
 *
 * @author skalr2
 */
public class RegistryItemDetailsVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The registry items list. */
	private List<RegistryItemsListVO> registryItemsList;
	
	/** The service error vo. */
	private ServiceErrorVO serviceErrorVO;
	
	/**
	 * Gets the registry items list.
	 *
	 * @return the registryItemsList
	 */
	public List<RegistryItemsListVO> getRegistryItemsList() {
		return registryItemsList;
	}
	
	/**
	 * Sets the registry items list.
	 *
	 * @param registryItemsList the registryItemsList to set
	 */
	public void setRegistryItemsList(List<RegistryItemsListVO> registryItemsList) {
		this.registryItemsList = registryItemsList;
	}
	
	/**
	 * Gets the service error vo.
	 *
	 * @return the serviceErrorVO
	 */
	public ServiceErrorVO getServiceErrorVO() {
		return serviceErrorVO;
	}
	
	/**
	 * Sets the service error vo.
	 *
	 * @param serviceErrorVO the serviceErrorVO to set
	 */
	public void setServiceErrorVO(ServiceErrorVO serviceErrorVO) {
		this.serviceErrorVO = serviceErrorVO;
	}
}
