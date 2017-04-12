/**
 * 
 */
package com.bbb.commerce.checklist.vo;

import java.io.Serializable;
import java.util.List;

public class ChecklistSKUMapping  implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String skuId;
	private int quantityRequested;
	private List<String> listCatId;
	private int packageCount;
	
	public String getSkuId() {
		return skuId;
	}
	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}
	public int getQuantityRequested() {
		return quantityRequested;
	}
	public void setQuantityRequested(int quantityRequested) {
		this.quantityRequested = quantityRequested;
	}
	public List<String> getListCatId() {
		return listCatId;
	}
	public void setListCatId(List<String> listCatId) {
		this.listCatId = listCatId;
	}
	public int getPackageCount() {
		return packageCount;
	}
	public void setPackageCount(int packageCount) {
		this.packageCount = packageCount;
	}
	
	
}
