/**
 *
 * File  : InventoryDecrementManager.java
 * Project:     BBB
 * 
 * 
 * This class is  Manager which is used for sending the
 * InventoryDecrementVO Object which contains list of INventory VO objects
 * 
 * @author vchan5
 * 
 */
package com.bbb.commerce.inventory;

import java.util.ArrayList;
import java.util.List;

import com.bbb.common.BBBGenericService;

import com.bbb.commerce.inventory.vo.InventoryVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.util.ServiceHandlerUtil;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;

public class InventoryDecrementManager extends BBBGenericService {

	OnlineInventoryManager inventoryManager;

	/**
	 * @return the inventoryManager
	 */
	public OnlineInventoryManager getInventoryManager() {
		return inventoryManager;
	}

	/**
	 * @param inventoryManager
	 *            the inventoryManager to set
	 */
	public void setInventoryManager(OnlineInventoryManager inventoryManager) {
		this.inventoryManager = inventoryManager;
	}

	/**
	 * this method is will call the ServiceHandlerUtil for sending the Inventory
	 * decrement VO object for JMS Topic
	 * 
	 * @throws BBBBusinessException
	 */
	public void sendInventoryDecrement() throws BBBBusinessException {
		String methodName = BBBCoreConstants.INVENTORY_TIBCO_CALL + "sendInventoryDecrement";
		BBBPerformanceMonitor.start(BBBPerformanceConstants.MESSAGING_CALL, methodName);
		List<InventoryVO> inVOLst = new ArrayList<InventoryVO>();
		InventoryDecrementVO idVO = new InventoryDecrementVO();
		try {

			if (getSkuIdUS() != null) {
				InventoryVO inventoryVOUS = new InventoryVO();
				inventoryVOUS.setSiteID(getSiteIdUS());
				inventoryVOUS.setSkuID(getSkuIdUS());
				inventoryVOUS.setOrderedQuantity(getOrderedQuantityUS());
				inVOLst.add(inventoryVOUS);
			}
			if (getSkuIdCA() != null) {
				InventoryVO inventoryVOCA = new InventoryVO();
				inventoryVOCA.setSiteID(getSiteIdCA());
				inventoryVOCA.setSkuID(getSkuIdCA());
				inventoryVOCA.setOrderedQuantity(getOrderedQuantityCA());
				inVOLst.add(inventoryVOCA);
			}
			if (getSkuIdBaby() != null) {
				InventoryVO inventoryVOBaby = new InventoryVO();
				inventoryVOBaby.setSiteID(getSiteIdBaby());
				inventoryVOBaby.setSkuID(getSkuIdBaby());
				inventoryVOBaby.setOrderedQuantity(getOrderedQuantityBaby());
				inVOLst.add(inventoryVOBaby);
			}
			if (getSkuId() != null) {
				InventoryVO inventoryVO = new InventoryVO();
				inventoryVO.setSiteID(getSiteId());
				inventoryVO.setSkuID(getSkuId());
				inventoryVO.setOrderedQuantity(getOrderedQuantity());
				inVOLst.add(inventoryVO);
			}
			if (inVOLst.size() > 0) {
				idVO.setListOfInventoryVos(inVOLst);
				ServiceHandlerUtil.send(idVO);
			}
		} catch (BBBSystemException ex) {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.MESSAGING_CALL, methodName);
			throw new BBBBusinessException(ex.getErrorCode(),ex.getMessage(), ex);
		} finally {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.MESSAGING_CALL, methodName);
		}

	}

	public String getSiteId() {
		return siteId;
	}

	public String getSkuIdUS() {
		return skuIdUS;
	}

	public void setSkuIdUS(String skuIdUS) {
		this.skuIdUS = skuIdUS;
	}

	public String getSkuIdCA() {
		return skuIdCA;
	}

	public void setSkuIdCA(String skuIdCA) {
		this.skuIdCA = skuIdCA;
	}

	public String getSkuIdBaby() {
		return skuIdBaby;
	}

	public void setSkuIdBaby(String skuIdBaby) {
		this.skuIdBaby = skuIdBaby;
	}

	public long getOrderedQuantityUS() {
		return orderedQuantityUS;
	}

	public void setOrderedQuantityUS(long orderedQuantityUS) {
		this.orderedQuantityUS = orderedQuantityUS;
	}

	public long getOrderedQuantityCA() {
		return orderedQuantityCA;
	}

	public void setOrderedQuantityCA(long orderedQuantityCA) {
		this.orderedQuantityCA = orderedQuantityCA;
	}

	public long getOrderedQuantityBaby() {
		return orderedQuantityBaby;
	}

	public void setOrderedQuantityBaby(long orderedQuantityBaby) {
		this.orderedQuantityBaby = orderedQuantityBaby;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public String getSkuId() {
		return skuId;
	}

	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}

	public long getOrderedQuantity() {
		return orderedQuantity;
	}

	public String getSiteIdCA() {
		return siteIdCA;
	}

	public void setSiteIdCA(String siteIdCA) {
		this.siteIdCA = siteIdCA;
	}

	public String getSiteIdUS() {
		return siteIdUS;
	}

	public void setSiteIdUS(String siteIdUS) {
		this.siteIdUS = siteIdUS;
	}

	public String getSiteIdBaby() {
		return siteIdBaby;
	}

	public void setSiteIdBaby(String siteIdBaby) {
		this.siteIdBaby = siteIdBaby;
	}

	public void setOrderedQuantity(long orderedQuantity) {
		this.orderedQuantity = orderedQuantity;
	}

	private String siteId;
	private String skuId;
	private long orderedQuantity;
	private String siteIdCA;
	private String siteIdUS;
	private String siteIdBaby;
	private String skuIdUS;
	private String skuIdCA;
	private String skuIdBaby;
	private long orderedQuantityUS;
	private long orderedQuantityCA;
	private long orderedQuantityBaby;
}
