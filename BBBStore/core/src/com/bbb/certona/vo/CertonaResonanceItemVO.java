/**
 * 
 */
package com.bbb.certona.vo;

import java.util.List;
import java.util.Map;

import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.framework.httpquery.vo.HTTPServiceResponseIF;

/**
 * @author ikhan2
 *
 */
public class CertonaResonanceItemVO implements HTTPServiceResponseIF{

	private String error;
	private List<String> productIDsList;
	private List<ProductVO> productsVOsList;
	private List<SKUDetailVO> skuDetailVOList;
	private Map<String, String> skuParentProductMap;
	private String pageId;
	private String trackingId;
	private List<String> activeProductIDsList;
	
	/**
	 * @return the activeProductIDsList
	 */
	public List<String> getActiveProductIDsList() {
		return activeProductIDsList;
	}

	/**
	 * @param activeProductIDsList the activeProductIDsList to set
	 */
	public void setActiveProductIDsList(List<String> activeProductIDsList) {
		this.activeProductIDsList = activeProductIDsList;
	}

	@Override
	public void setError(final String errorCode) {
		this.error = errorCode;
	}
	
	public String getError() {
		return this.error;
	}

	public List<String> getProductIDsList() {
		return this.productIDsList;
	}

	public void setProductIDsList(final List<String> productIDsList) {
		this.productIDsList = productIDsList;
	}

	public List<ProductVO> getProductsVOsList() {
		return this.productsVOsList;
	}

	public void setProductsVOsList(final List<ProductVO> productsVOsList) {
		this.productsVOsList = productsVOsList;
	}
	
	@Override
	public String toString() {
		return "CertonaResonanceItemVO [error=" + this.error + ", productIDsList="
				+ this.productIDsList + ", productsVOsList=" + this.productsVOsList
				+ ", skuDetailVOList=" + this.skuDetailVOList
				+ ", skuParentProductMap=" + this.skuParentProductMap + ", pageId="
				+ this.pageId + ", trackingId=" + this.trackingId + ", activeProductIDsList=" + this.activeProductIDsList + "]";
	}

	public Map<String, String> getSkuParentProductMap() {
		return this.skuParentProductMap;
	}

	public void setSkuParentProductMap(final Map<String, String> skuParentProductMap) {
		this.skuParentProductMap = skuParentProductMap;
	}

	public List<SKUDetailVO> getSkuDetailVOList() {
		return this.skuDetailVOList;
	}

	public void setSkuDetailVOList(final List<SKUDetailVO> skuDetailVOList) {
		this.skuDetailVOList = skuDetailVOList;
	}

	public String getPageId() {
		return this.pageId;
	}

	public void setPageId(final String pageId) {
		this.pageId = pageId;
	}

	public String getTrackingId() {
		return this.trackingId;
	}

	public void setTrackingId(final String trackingId) {
		this.trackingId = trackingId;
	}

}
