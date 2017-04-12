/**
 * 
 */
package com.bbb.commerce.giftregistry.vo;

import java.util.List;
import java.util.Map;

import com.bbb.framework.integration.ServiceResponseBase;

// TODO: Auto-generated Javadoc
/**
 * The Class RegistryItemsListVO.
 *
 * @author skalr2
 */
public class RegistryItemsListVO extends ServiceResponseBase  {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The tot entries. */
	private int totEntries;
	
	/** The sort reponse. */
	private String sortReponse;
	
	/** The service error vo. */
	private ServiceErrorVO serviceErrorVO;
	
	/** The registry item list. */
	private List<RegistryItemVO> registryItemList;
	
	/** The Sku reg item vo map. */
	private Map<String,RegistryItemVO> mSkuRegItemVOMap;
	
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
	 * @param serviceErrorVO the serviceErrorVO to set
	 */
	public void setServiceErrorVO(final ServiceErrorVO serviceErrorVO) {
		this.serviceErrorVO = serviceErrorVO;
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
	public void setTotEntries(final int totEntries) {
		this.totEntries = totEntries;
	}
	
	/**
	 * Gets the sort reponse.
	 *
	 * @return the sortReponse
	 */
	public String getSortReponse() {
		return sortReponse;
	}
	
	/**
	 * Sets the sort reponse.
	 *
	 * @param sortReponse the sortReponse to set
	 */
	public void setSortReponse(final String sortReponse) {
		this.sortReponse = sortReponse;
	}
	
	/**
	 * Gets the registry item list.
	 *
	 * @return the registryItemList
	 */
	public List<RegistryItemVO> getRegistryItemList() {
		return registryItemList;
	}
	
	/**
	 * Sets the registry item list.
	 *
	 * @param registryItemList the registryItemList to set
	 */
	public void setRegistryItemList(final List<RegistryItemVO> registryItemList) {
		this.registryItemList = registryItemList;
	}

	/**
	 * Gets the sku reg item vo map.
	 *
	 * @return the sku reg item vo map
	 */
	public Map<String, RegistryItemVO> getSkuRegItemVOMap() {
		return mSkuRegItemVOMap;
	}

	/**
	 * Sets the sku reg item vo map.
	 *
	 * @param pSkuRegItemVOMap the sku reg item vo map
	 */
	public void setSkuRegItemVOMap(Map<String, RegistryItemVO> pSkuRegItemVOMap) {
		this.mSkuRegItemVOMap = pSkuRegItemVOMap;
	}
}
