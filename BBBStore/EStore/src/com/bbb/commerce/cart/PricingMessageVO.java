package com.bbb.commerce.cart;

import java.io.Serializable;

/**
 * @author akhaju
 *
 */
public class PricingMessageVO  implements Cloneable,Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mWishListId;	
	private String mSkuId;
	private String mProdId;
	private boolean bopus;
	private boolean isFlagOff;
	private double prevPrice;
	private double currentPrice;
	private boolean isInStock;
	private String mMessage;
	private String mParentCat;
	private String mStoreId;
	private String mHeaderMessage;
	private boolean mPriceChange;
	private long mQuantity;
	private String mRegistryId;
	private String mCommerceItemId;
	private String displayName;
	private String parentCatName;
	private boolean mDisableLink;
	/**
	 * @return the disableLink
	 */
	public boolean isDisableLink() {
		return mDisableLink;
	}
	/**
	 * @param pDisableLink the disableLink to set
	 */
	public void setDisableLink(boolean pDisableLink) {
		mDisableLink = pDisableLink;
	}
	/**
	 * @return the parentCatName
	 */
	public String getParentCatName() {
		return parentCatName;
	}
	/**
	 * @param parentCatName the parentCatName to set
	 */
	public void setParentCatName(String parentCatName) {
		this.parentCatName = parentCatName;
	}
	/**
	 * @return the parentSeoUrl
	 */
	public String getParentSeoUrl() {
		return parentSeoUrl;
	}
	/**
	 * @param parentSeoUrl the parentSeoUrl to set
	 */
	public void setParentSeoUrl(String parentSeoUrl) {
		this.parentSeoUrl = parentSeoUrl;
	}
	private String parentSeoUrl;
	
	
	/**
	 * @return the commerceItemId
	 */
	public String getCommerceItemId() {
		return mCommerceItemId;
	}
	/**
	 * @param pCommerceItemId the commerceItemId to set
	 */
	public void setCommerceItemId(String pCommerceItemId) {
		mCommerceItemId = pCommerceItemId;
	}
	/**
	 * @return the registryId
	 */
	public String getRegistryId() {
		return mRegistryId;
	}
	/**
	 * @param pRegistryId the registryId to set
	 */
	public void setRegistryId(String pRegistryId) {
		mRegistryId = pRegistryId;
	}
	/**
	 * @return the quantity
	 */
	public long getQuantity() {
		return mQuantity;
	}
	/**
	 * @param pQuantity the quantity to set
	 */
	public void setQuantity(long pQuantity) {
		mQuantity = pQuantity;
	}
	/**
	 * @return the bopus
	 */
	public boolean isBopus() {
		return bopus;
	}
	/**
	 * @param pBopus the bopus to set
	 */
	public void setBopus(boolean pBopus) {
		bopus = pBopus;
	}
	/**
	 * @return the priceChange
	 */
	public boolean isPriceChange() {
		return mPriceChange;
	}
	/**
	 * @param pPriceChange the priceChange to set
	 */
	public void setPriceChange(boolean pPriceChange) {
		mPriceChange = pPriceChange;
	}
	/**
	 * @return the headerMessage
	 */
	String getHeaderMessage() {
		return mHeaderMessage;
	}
	/**
	 * @param pHeaderMessage the headerMessage to set
	 */
	void setHeaderMessage(String pHeaderMessage) {
		mHeaderMessage = pHeaderMessage;
	}
	/**
	 * @return the storeId
	 */
	public String getStoreId() {
		return mStoreId;
	}
	/**
	 * @param pStoreId the storeId to set
	 */
	public void setStoreId(String pStoreId) {
		mStoreId = pStoreId;
	}
	/**
	 * @return the wishListId
	 */
	public String getWishListId() {
		return mWishListId;
	}
	/**
	 * @param pWishListId the wishListId to set
	 */
	public void setWishListId(String pWishListId) {
		mWishListId = pWishListId;
	}
	/**
	 * @return the parentCat
	 */
	public String getParentCat() {
		return mParentCat;
	}
	/**
	 * @param pParentCat the parentCat to set
	 */
	public void setParentCat(String pParentCat) {
		mParentCat = pParentCat;
	}
	/**
	 * @return the isFlagOff
	 */
	public boolean isFlagOff() {
		return isFlagOff;
	}
	/**
	 * @param pIsFlagOff the isFlagOff to set
	 */
	public void setFlagOff(boolean pIsFlagOff) {
		isFlagOff = pIsFlagOff;
	}
	/**
	 * @return the isInStock
	 */
	public boolean isInStock() {
		return isInStock;
	}
	/**
	 * @param pIsInStock the isInStock to set
	 */
	public void setInStock(boolean pIsInStock) {
		isInStock = pIsInStock;
	}
	/**
	 * @return the skuId
	 */
	public String getSkuId() {
		return mSkuId;
	}
	/**
	 * @param pSkuId the skuId to set
	 */
	public void setSkuId(String pSkuId) {
		mSkuId = pSkuId;
	}
	/**
	 * @return the prodId
	 */
	public String getProdId() {
		return mProdId;
	}
	/**
	 * @param pProdId the prodId to set
	 */
	public void setProdId(String pProdId) {
		mProdId = pProdId;
	}

	
	/**
	 * @return the prevPrice
	 */
	public double getPrevPrice() {
		return prevPrice;
	}
	/**
	 * @param pPrevPrice the prevPrice to set
	 */
	public void setPrevPrice(double pPrevPrice) {
		prevPrice = pPrevPrice;
	}
	/**
	 * @return the currentPrice
	 */
	public double getCurrentPrice() {
		return currentPrice;
	}
	/**
	 * @param pCurrentPrice the currentPrice to set
	 */
	public void setCurrentPrice(double pCurrentPrice) {
		currentPrice = pCurrentPrice;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return mMessage;
	}
	/**
	 * @param pMessage the message to set
	 */
	public void setMessage(String pMessage) {
		mMessage = pMessage;
	}
	
	// Suppressing this PMD error since this clone is implemented correctly as we have called super.clone(), 
	// only PMD rule check it in a way which gives error  
	@SuppressWarnings("PMD")
	public Object clone(){
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			return new PricingMessageVO();
		}
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
}
