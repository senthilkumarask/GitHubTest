package com.bbb.rest.catalog.vo;

import java.io.Serializable;

import com.bbb.commerce.catalog.vo.ImageVO;

public class ProductMobileVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String productId;
	private String pdpSeoUrl;
	private String childSku;
	private SkuRestVO skuRestVO;
	private String imageURL;
	private String productTitle;
	private double currentPrice;
	private boolean webOfferedFlag;
	private String errorMessage;
	private boolean isCustomizationOffered;
	private boolean customizableRequired;
	private boolean disabled;
	private boolean inStock;
	private boolean isErrorExist;
	private String errorCode;
	private boolean intlRestricted;
    private String parentProductId;
    private boolean poc;
    private String parentProductName;
    private ImageVO parentImage;
	
	public boolean isIntlRestricted() {
		return intlRestricted;
	}


	public void setIntlRestricted(boolean intlRestricted) {
		this.intlRestricted = intlRestricted;
	}


	public boolean isDisabled() {
		return disabled;
	}


	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}


	public boolean isInStock() {
		return inStock;
	}


	public void setInStock(boolean inStock) {
		this.inStock = inStock;
	}


	/**
	 * @return errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}


	/**
	 * @param errorMessage
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}


	/**
	 * @return currentPrice
	 */
	public double getCurrentPrice() {
		return currentPrice;
	}


	/**
	 * @param currentPrice
	 */
	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
	}


	/**
	 * @return webOfferedFlag
	 */
	public boolean isWebOfferedFlag() {
		return webOfferedFlag;
	}

	
	/**
	 * @param webOfferedFlag
	 */
	public void setWebOfferedFlag(boolean webOfferedFlag) {
		this.webOfferedFlag = webOfferedFlag;
	}

	/**
	 * @return productTitle
	 */ 
	public String getProductTitle() {
		return productTitle;
	}

	/**
	 * @param productTitle
	 */
	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}

	public SkuRestVO getSkuRestVO() {
		return skuRestVO;
	}

	/**
	 * @return imageURL
	 */
	public String getImageURL() {
		return imageURL;
	}

	/**
	 * @param imageURL
	 */
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	/**
	 * @param skuRestVO
	 */
	public void setSkuRestVO(SkuRestVO skuRestVO) {
		this.skuRestVO = skuRestVO;
	}

	/**
	 * @return childSku
	 */
	public String getChildSku() {
		return childSku;
	}

	/**
	 * @param childSku
	 */
	public void setChildSku(String childSku) {
		this.childSku = childSku;
	}

	/**
	 * @return productId
	 */
	public String getProductId() {
		return productId;
	}

	/**
	 * @param productId
	 */
	public void setProductId(String productId) {
		this.productId = productId;
	}

	/**
	 * @return pdpSeoUrl
	 */
	public String getPdpSeoUrl() {
		return pdpSeoUrl;
	}

	/**
	 * @param pdpSeoUrl
	 */
	public void setPdpSeoUrl(String pdpSeoUrl) {
		this.pdpSeoUrl = pdpSeoUrl;
	}

	/**
	 * @return the isCustomizationOffered
	 */
	public boolean isCustomizationOffered() {
		return isCustomizationOffered;
}

	@Override
	public String toString() {
		return "ProductMobileVO [productId=" + productId + ", pdpSeoUrl="
				+ pdpSeoUrl + ", childSku=" + childSku + ", skuRestVO="
				+ skuRestVO + ", imageURL=" + imageURL + ", productTitle="
				+ productTitle + ", currentPrice=" + currentPrice
				+ ", webOfferedFlag=" + webOfferedFlag + ", errorMessage="
				+ errorMessage + ", isCustomizationOffered="
				+ isCustomizationOffered + ", customizableRequired="
				+ customizableRequired + ", disabled=" + disabled
				+ ", inStock=" + inStock + ", intlRestricted="
				+ intlRestricted + "]";
	}


	/**
	 * @param isCustomizationOffered the isCustomizationOffered to set
	 */
	public void setCustomizationOffered(boolean isCustomizationOffered) {
		this.isCustomizationOffered = isCustomizationOffered;
	}


	public boolean isCustomizableRequired() {
		return customizableRequired;
	}


	public void setCustomizableRequired(boolean customizableRequired) {
		this.customizableRequired = customizableRequired;
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


	public boolean isErrorExist() {
		return isErrorExist;
	}


	public void setErrorExist(boolean isErrorExist) {
		this.isErrorExist = isErrorExist;
	}


	public String getErrorCode() {
		return errorCode;
	}


	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}


	
}
