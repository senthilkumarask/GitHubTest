package com.bbb.rest.catalog.vo;

import java.io.Serializable;
import java.util.List;

import com.bbb.commerce.catalog.vo.RecommendedCategoryVO;

/**
 * The Class ProductCategoryRestVO.
 *
 * @author msaxe6
 */
public class ProductCategoryRestVO implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The product rest vo. */
	private ProductRestVO productRestVO;
	
	/** The recommended category list. */
	private List<RecommendedCategoryVO> recommendedCategoryList;

	/**
	 * Gets the product rest vo.
	 *
	 * @return the product rest vo
	 */
	public ProductRestVO getProductRestVO() {
		return productRestVO;
	}
	

	/**
	 * Sets the product rest vo.
	 *
	 * @param productRestVO the new product rest vo
	 */
	public void setProductRestVO(ProductRestVO productRestVO) {
		this.productRestVO = productRestVO;
	}
	

	/**
	 * Gets the recommended category list.
	 *
	 * @return the recommended category list
	 */
	public List<RecommendedCategoryVO> getRecommendedCategoryList() {
		return recommendedCategoryList;
	}
	

	/**
	 * Sets the recommended category list.
	 *
	 * @param recommendedCategoryList the new recommended category list
	 */
	public void setRecommendedCategoryList(List<RecommendedCategoryVO> recommendedCategoryList) {
		this.recommendedCategoryList = recommendedCategoryList;
	}
	

}
