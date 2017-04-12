package com.bbb.rest.catalog.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.bbb.commerce.catalog.vo.ProductVO;
/**
 * 
 * @author njai13
 *
 */
public class ProductFilterVO implements Serializable { 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ProductVO productVORest;
	private Map<String,String> skuRollupMapRest;
	private CatalogItemAttributesVO productAttributeVO ;
	private List<ProductRollupVO> productRollupVO ;
	private List<PrdRelationRollupVO> prdRelationRollup ;
	
	
	public List<ProductRollupVO> getProductRollupVO() {
		return productRollupVO;
	}
	public void setProductRollupVO(List<ProductRollupVO> productRollupVO) {
		this.productRollupVO = productRollupVO;
	}
	public List<PrdRelationRollupVO> getPrdRelationRollup() {
		return prdRelationRollup;
	}
	public void setPrdRelationRollup(List<PrdRelationRollupVO> prdRelationRollup) {
		this.prdRelationRollup = prdRelationRollup;
	}
	public CatalogItemAttributesVO getProductAttributeVO() {
		return productAttributeVO;
	}
	public void setProductAttributeVO(CatalogItemAttributesVO productAttributeVO) {
		this.productAttributeVO = productAttributeVO;
	}
	public ProductVO getProductVORest() {
		return productVORest;
	}
	public void setProductVORest(ProductVO productVORest) {
		this.productVORest = productVORest;
	}
	public Map<String, String> getSkuRollupMapRest() {
		return skuRollupMapRest;
	}
	public void setSkuRollupMapRest(Map<String, String> skuRollupMapRest) {
		this.skuRollupMapRest = skuRollupMapRest;
	}




}
