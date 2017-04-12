package com.bbb.cms;

import java.io.Serializable;
import java.util.List;

public class CarouselVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String carouselId; 
	private int flipTime; 
	private String title;
	private List<PromoBoxVO> images;
	private String carouselImageCount;
	
	/**
	 * @return the carouselImageCount
	 */
	public String getCarouselImageCount() {
		return carouselImageCount;
	}
	/**
	 * @param carouselImageCount the carouselImageCount to set
	 */
	public void setCarouselImageCount(String carouselImageCount) {
		this.carouselImageCount = carouselImageCount;
	}
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
	 * @return the images
	 */
	public List<PromoBoxVO> getImages() {
		return images;
	}
	/**
	 * @param images the images to set
	 */
	public void setImages(List<PromoBoxVO> images) {
		this.images = images;
	}
	
	
}
