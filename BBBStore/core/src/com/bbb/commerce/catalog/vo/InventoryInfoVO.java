/**
 * 
 */
package com.bbb.commerce.catalog.vo;

import java.io.Serializable;

/**
 * @author iteggi
 *
 */
public class InventoryInfoVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long shopInventory;
	private  long registryInventory;

	public InventoryInfoVO() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the shopInventory
	 */
	public long getShopInventory() {
		return shopInventory;
	}

	/**
	 * @param pShopInventory the shopInventory to set
	 */
	public void setShopInventory(long pShopInventory) {
		shopInventory = pShopInventory;
	}

	/**
	 * @return the registryInventory
	 */
	public long getRegistryInventory() {
		return registryInventory;
	}

	/**
	 * @param pRegistryInventory the registryInventory to set
	 */
	public void setRegistryInventory(long pRegistryInventory) {
		registryInventory = pRegistryInventory;
	}

}
