package com.bbb.rest.catalog.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bbb.commerce.catalog.vo.ImageVO;
import com.bbb.commerce.catalog.vo.ProductVO;

public class ProductRestVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ProductVO productVO;
	private Map<String,String> skuRollupMap;
    private List<ProductRollupVO> productRollupVO ;
    private List<PrdRelationRollupVO> prdRelationRollup ;
    private CatalogItemAttributesVO productAllAttributesVO;
    private boolean errorExist;
    private String errorCode;
    private String errorMessage;
    private String escapedPrdtName;
    private Map<String,Map<String,SkuRestVO>> skuWithColorParam;
    private Map<String,Map<String,String>> skuSwatchesWithColorParam;    
    private Set<String> vendorJs;
    private String parentProductId;
    private boolean poc;
    private String parentProductName;
    private ImageVO parentImage;
    
    
    public Set<String> getVendorJs() {
		return vendorJs;
	}

	public void setVendorJs(Set<String> vendorJs) {
		this.vendorJs = vendorJs;
	}

    public String getEscapedPrdtName() {
		return escapedPrdtName;
	}

	public void setEscapedPrdtName(String escapedPrdtName) {
		this.escapedPrdtName = escapedPrdtName;
	}

	public CatalogItemAttributesVO getProductAllAttributesVO() {
		return productAllAttributesVO;
	}

	public void setProductAllAttributesVO(CatalogItemAttributesVO allAttributeVOs) {
		this.productAllAttributesVO = allAttributeVOs;
	}
    
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

	public Map<String, String> getSkuRollupMap() {
		return skuRollupMap;
	}

	public void setSkuRollupMap(Map<String, String> skuRollupMap) {
		this.skuRollupMap = skuRollupMap;
	}

	public ProductVO getProductVO() {
		return productVO;
	}

	public void setProductVO(ProductVO productVO) {
		this.productVO = productVO;
	}

	/**
	 * @return the errorExist
	 */
	public boolean isErrorExist() {
		return errorExist;
	}

	/**
	 * @param errorExist the errorExist to set
	 */
	public void setErrorExist(boolean errorExist) {
		this.errorExist = errorExist;
	}

	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Map<String,Map<String,SkuRestVO>> getSkuWithColorParam() {
		return skuWithColorParam;
	}

	public void setSkuWithColorParam(Map<String,Map<String,SkuRestVO>> skuWithColorParam) {
		this.skuWithColorParam = skuWithColorParam;
	}

	public Map<String,Map<String,String>> getSkuSwatchesWithColorParam() {
		return skuSwatchesWithColorParam;
	}

	public void setSkuSwatchesWithColorParam(
			Map<String,Map<String,String>> skuSwatchesWithColorParam) {
		this.skuSwatchesWithColorParam = skuSwatchesWithColorParam;
	}

	public String getParentProductId() {
		return parentProductId;
	}

	public void setParentProductId(String parentProductId) {
		this.parentProductId = parentProductId;
	}

	public boolean isPoc() {
		return poc;
	}

	public void setPoc(boolean poc) {
		this.poc = poc;
	}

	public String getParentProductName() {
		return parentProductName;
	}

	public void setParentProductName(String parentProductName) {
		this.parentProductName = parentProductName;
	}

	public ImageVO getParentImage() {
		return parentImage;
	}

	public void setParentImage(ImageVO parentImage) {
		this.parentImage = parentImage;
	}

	
}
