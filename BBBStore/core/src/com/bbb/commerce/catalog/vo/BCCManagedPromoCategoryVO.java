/**
 * 
 */
package com.bbb.commerce.catalog.vo;

import java.util.List;


public class BCCManagedPromoCategoryVO {
	
	private boolean showQuickViewLink;
	private boolean showProductCompare;
	private boolean showColorSwatches;
	private boolean showTitle;
	private boolean showReviewRatings;
	private boolean showPrice;
	private boolean showGlobalAttributes;
	private boolean showFilters;
	private List<String> productAttributes;
	
	public List<String> getProductAttributes() {
		return productAttributes;
	}
	public void setProductAttributes(List<String> productAttributes) {
		this.productAttributes = productAttributes;
	}
	/**
	 * @return the showQuickViewLink
	 */
	public boolean isShowQuickViewLink() {
		return showQuickViewLink;
	}
	/**
	 * @param showQuickViewLink the showQuickViewLink to set
	 */
	public void setShowQuickViewLink(boolean showQuickViewLink) {
		this.showQuickViewLink = showQuickViewLink;
	}
	/**
	 * @return the showProductCompare
	 */
	public boolean isShowProductCompare() {
		return showProductCompare;
	}
	/**
	 * @param showProductCompare the showProductCompare to set
	 */
	public void setShowProductCompare(boolean showProductCompare) {
		this.showProductCompare = showProductCompare;
	}
	/**
	 * @return the showColorSwatches
	 */
	public boolean isShowColorSwatches() {
		return showColorSwatches;
	}
	/**
	 * @param showColorSwatches the showColorSwatches to set
	 */
	public void setShowColorSwatches(boolean showColorSwatches) {
		this.showColorSwatches = showColorSwatches;
	}
	/**
	 * @return the showTitle
	 */
	public boolean isShowTitle() {
		return showTitle;
	}
	/**
	 * @param showTitle the showTitle to set
	 */
	public void setShowTitle(boolean showTitle) {
		this.showTitle = showTitle;
	}
	/**
	 * @return the showReviewRatings
	 */
	public boolean isShowReviewRatings() {
		return showReviewRatings;
	}
	/**
	 * @param showReviewRatings the showReviewRatings to set
	 */
	public void setShowReviewRatings(boolean showReviewRatings) {
		this.showReviewRatings = showReviewRatings;
	}
	/**
	 * @return the showPrice
	 */
	public boolean isShowPrice() {
		return showPrice;
	}
	/**
	 * @param showPrice the showPrice to set
	 */
	public void setShowPrice(boolean showPrice) {
		this.showPrice = showPrice;
	}
	/**
	 * @return the showGlobalAttributes
	 */
	public boolean isShowGlobalAttributes() {
		return showGlobalAttributes;
	}
	/**
	 * @param showGlobalAttributes the showGlobalAttributes to set
	 */
	public void setShowGlobalAttributes(boolean showGlobalAttributes) {
		this.showGlobalAttributes = showGlobalAttributes;
	}
	/**
	 * @return the showFilters
	 */
	public boolean isShowFilters() {
		return showFilters;
	}
	/**
	 * @param showFilters the showFilters to set
	 */
	public void setShowFilters(boolean showFilters) {
		this.showFilters = showFilters;
	}
}
