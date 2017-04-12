package com.bbb.cms;

import java.io.Serializable;
import java.util.List;

import com.bbb.commerce.catalog.vo.ProductVO;

public class ProductCarouselVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String carouselId; 
	private int flipTime; 
	private String title;
	private List<ProductVO> productList;
	private String noOfProductsInProductCarousel;
	
	/**
	 * @return the carouselId
	 */
	public String getCarouselId() {
		return carouselId;
	}
	/**
	 * @param carouselId the carouselId to set
	 */
	public void setCarouselId(String carouselId) {
		this.carouselId = carouselId;
	}
	/**
	 * @return the flipTime
	 */
	public int getFlipTime() {
		return flipTime;
	}
	/**
	 * @param flipTime the flipTime to set
	 */
	public void setFlipTime(int flipTime) {
		this.flipTime = flipTime;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the productList
	 */
	public List<ProductVO> getProductList() {
		return productList;
	}
	/**
	 * @param productList the productList to set
	 */
	public void setProductList(List<ProductVO> productList) {
		this.productList = productList;
	}
	/**
	 * @return the noOfProductsInProductCarousel
	 */
	public String getNoOfProductsInProductCarousel() {
		return noOfProductsInProductCarousel;
	}
	/**
	 * @param noOfProductsInProductCarousel the noOfProductsInProductCarousel to set
	 */
	public void setNoOfProductsInProductCarousel(
			String noOfProductsInProductCarousel) {
		this.noOfProductsInProductCarousel = noOfProductsInProductCarousel;
	}
}
