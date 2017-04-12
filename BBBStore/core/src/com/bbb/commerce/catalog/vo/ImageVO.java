package com.bbb.commerce.catalog.vo;

import java.io.Serializable;

import com.bbb.constants.BBBCoreConstants;

import atg.core.util.StringUtils;

/**
 * 
 * @author njai13
 *
 */
public class ImageVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String thumbnailImage;
	private String swatchImage;
	private String smallImage;
	private String largeImage;
	private String regularImage;
	private int zoomImageIndex;
	private String zoomImage;
	private String mediumImage;
	private String collectionThumbnailImage;
	private boolean anywhereZoomAvailable;
	
	private String basicImage;
	
	/**
	 * @return the basicImage
	 */
	public String getBasicImage() {
		return basicImage;
	}

	/**
	 * @param pBasicImage the basicImage to set
	 */
	public void setBasicImage(String pBasicImage) {
		if(pBasicImage != null && pBasicImage.contains(BBBCoreConstants.QUESTION_MARK)){
			String[] split = pBasicImage.split(BBBCoreConstants.QUESTION_MARK_2);
			basicImage = split[0];
		} else {
			basicImage =pBasicImage; 
		}
	}
	


	public String getCollectionThumbnailImage() {
		return collectionThumbnailImage;
	}


	/**
	 * @param collectionThumbnailImage 83px preset
	 */
	public void setCollectionThumbnailImage(String collectionThumbnailImage) {
		this.collectionThumbnailImage = collectionThumbnailImage;
	}

	/**
	 * @return the swatchImage
	 */
	public String getSwatchImage() {
		return swatchImage;
	}


	/**
	 * @param swatchImage the swatchImage to set
	 */
	public void setSwatchImage(String swatchImage) {
		this.swatchImage = swatchImage;
	}


	/**
	 * @return the thumbnailImage
	 */
	public String getThumbnailImage() {
		return thumbnailImage;
	}

	/**
	 * @return the regularImage 226px
	 */
	public String getRegularImage() {
		return regularImage;
	}

	public void setRegularImage(String regularImage) {
		this.regularImage = regularImage;
	}

	/**
	 * @param thumbnailImage the thumbnailImage to set
	 */
	public void setThumbnailImage(String thumbnailImage) {
		this.thumbnailImage = thumbnailImage;
		setBasicImage(thumbnailImage);
	}


	/**
	 * @return the smallImage
	 */
	public String getSmallImage() {
		return smallImage;
	}


	/**
	 * @param smallImage the smallImage to set
	 */
	public void setSmallImage(String smallImage) {
		this.smallImage = smallImage;
		setBasicImage(smallImage);
	}


	/**
	 * @return the largeImage
	 */
	public String getLargeImage() {
		return largeImage;
	}


	/**
	 * @param largeImage the largeImage to set
	 */
	public void setLargeImage(String largeImage) {
		this.largeImage = largeImage;
	}


	/**
	 * @return the zoomImageIndex
	 */
	public int getZoomImageIndex() {
		return zoomImageIndex;
	}


	/**
	 * @param zoomImageIndex the zoomImageIndex to set
	 */
	public void setZoomImageIndex(int zoomImageIndex) {
		this.zoomImageIndex = zoomImageIndex;
	}


	/**
	 * @return the zoomImage
	 */
	public String getZoomImage() {
		return zoomImage;
	}


	/**
	 * @param zoomImage the zoomImage to set
	 */
	public void setZoomImage(String zoomImage) {
		this.zoomImage = zoomImage;
	}


	/**
	 * @return the mediumImage
	 */
	public String getMediumImage() {
		return mediumImage;
	}


	/**
	 * @param mediumImage the mediumImage to set
	 */
	public void setMediumImage(String mediumImage) {
		this.mediumImage = mediumImage;
		setBasicImage(mediumImage);
	}


	/**
	 * @return the anywhereZoomAvailable
	 */
	public boolean isAnywhereZoomAvailable() {
		return anywhereZoomAvailable;
	}


	/**
	 * @param anywhereZoomAvailable the anywhereZoomAvailable to set
	 */
	public void setAnywhereZoomAvailable(boolean anywhereZoomAvailable) {
		this.anywhereZoomAvailable = anywhereZoomAvailable;
	}

	public String toString(){
		StringBuffer toString=new StringBuffer(" Image VO Details \n ");
		toString.append(" Thumb Nail Image ").append(StringUtils.isEmpty(thumbnailImage)?"NULL":thumbnailImage).append("\n")
		.append(" Swatch  Image ").append(StringUtils.isEmpty(swatchImage)?"NULL":swatchImage).append("\n")
		.append(" Large  Image ").append(StringUtils.isEmpty(largeImage)?"NULL":largeImage).append("\n")
		.append(" Regular  Image 226px").append(StringUtils.isEmpty(regularImage)?"NULL":regularImage).append("\n")
		.append(" Zoom  Image ").append(StringUtils.isEmpty(zoomImage)?"NULL":zoomImage).append("\n")
		.append(" Medium  Image ").append(StringUtils.isEmpty(mediumImage)?"NULL":mediumImage).append("\n")
		.append(" Any where Zoom Available ").append(anywhereZoomAvailable).append("\n")		
		.append(" Zoom Image Index ").append(zoomImageIndex).append("\n")
		.append(" Basic Image ").append(basicImage).append("\n");

		return toString.toString();
	}

}