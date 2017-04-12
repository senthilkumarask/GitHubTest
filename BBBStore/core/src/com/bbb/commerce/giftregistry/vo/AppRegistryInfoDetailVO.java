package com.bbb.commerce.giftregistry.vo;

import com.bbb.framework.integration.ServiceResponseBase;


public class AppRegistryInfoDetailVO extends ServiceResponseBase {
	
	private static final long serialVersionUID = 1L;
	private RegistrySummaryVO registrySummaryVO;
	private RegistryItemsListVO items;
	/**
	 * @return the registrySummaryVO
	 */
	public RegistrySummaryVO getRegistrySummaryVO() {
		return registrySummaryVO;
	}
	/**
	 * @param registrySummaryVO the registrySummaryVO to set
	 */
	public void setRegistrySummaryVO(RegistrySummaryVO registrySummaryVO) {
		this.registrySummaryVO = registrySummaryVO;
	}
	/**
	 * @return the items
	 */
	public RegistryItemsListVO getItems() {
		return items;
	}
	/**
	 * @param items the items to set
	 */
	public void setItems(RegistryItemsListVO items) {
		this.items = items;
	}
}