package com.bbb.wishlist;

import java.io.Serializable;
import java.io.Serializable;
import java.io.Serializable;
import java.util.Map;

import atg.repository.RepositoryItem;

import com.bbb.commerce.cart.PricingMessageVO;
import com.bbb.commerce.catalog.vo.AttributeVO;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.catalog.vo.SKUVO;
import com.bbb.commerce.giftregistry.vo.RegistryVO;

public class GiftListVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mProdID;
	private String mSkuID;
	private long mQuantity;
	private String price;
	private String mCommerceItemId;
	private String mQtyPurchased;
	private String mQtyRequested;
	private double prevPrice;
	private String wishListItemId;
	private String giftListId;
	private RepositoryItem giftRepositoryItem;
	private String mStoreID;
	private String mRegistryID;
	private ProductVO productVO;
	private SKUVO skuVO;
	private boolean msgShownFlagOff;
	private boolean msgShownOOS;
	private PricingMessageVO mPriceMessageVO;
	private double totalPrice;
	private Map<String,AttributeVO> mAttribute;
	private RegistryVO registryVO;
	private boolean isBts;
	private String ltlShipMethod;
	private String ltlShipMethodDesc;
	private double assemblyFees;
	private double deliverySurcharge;
	private boolean shipMethodUnsupported;
	
	// Adding parameters for katori SFL cookie for transient/non transient users
	private String referenceNumber;
	private String fullImagePath;
	private String thumbnailImagePath;
	private String mobileFullImagePath;
	private String mobileThumbnailImagePath;
	private double personalizePrice;
	private String personalizationOptions;
	private String personalizationDetails;
	private String personalizationStatus;
	private String personalizationOptionsDisplay;		
	private boolean eximErrorExists;
	//BBBH-3982 - For displaying message for incart eligible skus on SFL email
	private boolean skuIncartFlag;
	private String siteId;
	
	
	public boolean isSkuIncartFlag() {
		return skuIncartFlag;
	}

	public void setSkuIncartFlag(boolean skuIncartFlag) {
		this.skuIncartFlag = skuIncartFlag;
	}

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public String getPersonalizationOptionsDisplay() {
		return personalizationOptionsDisplay;
	}

	public void setPersonalizationOptionsDisplay(
			String personalizationOptionsDisplay) {
		this.personalizationOptionsDisplay = personalizationOptionsDisplay;
	}

	public boolean isEximErrorExists() {
		return eximErrorExists;
	}

	public void setEximErrorExists(boolean eximErrorExists) {
		this.eximErrorExists = eximErrorExists;
	}

	public String getPersonalizationStatus() {
		return personalizationStatus;
	}

	public void setPersonalizationStatus(String personalizationStatus) {
		this.personalizationStatus = personalizationStatus;
	}

	/**
	 * @return the personalizePrice
	 */
	public double getPersonalizePrice() {
		return personalizePrice;
	}

	/**
	 * @param personalizePrice the personalizePrice to set
	 */
	public void setPersonalizePrice(double personalizePrice) {
		this.personalizePrice = personalizePrice;
	}

	/**
	 * @return the referenceNumber
	 */
	public String getReferenceNumber() {
		return referenceNumber;
	}

	/**
	 * @param referenceNumber the referenceNumber to set
	 */
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	/**
	 * @return the fullImagePath
	 */
	public String getFullImagePath() {
		return fullImagePath;
	}

	/**
	 * @param fullImagePath the fullImagePath to set
	 */
	public void setFullImagePath(String fullImagePath) {
		this.fullImagePath = fullImagePath;
	}

	/**
	 * @return the thumbnailImagePath
	 */
	public String getThumbnailImagePath() {
		return thumbnailImagePath;
	}

	/**
	 * @param thumbnailImagePath the thumbnailImagePath to set
	 */
	public void setThumbnailImagePath(String thumbnailImagePath) {
		this.thumbnailImagePath = thumbnailImagePath;
	}

	/**
	 * @return the mobileFullImagePath
	 */
	public String getMobileFullImagePath() {
		return mobileFullImagePath;
	}

	/**
	 * @param mobileFullImagePath the mobileFullImagePath to set
	 */
	public void setMobileFullImagePath(String mobileFullImagePath) {
		this.mobileFullImagePath = mobileFullImagePath;
	}

	/**
	 * @return the mobileThumbnailImagePath
	 */
	public String getMobileThumbnailImagePath() {
		return mobileThumbnailImagePath;
	}

	/**
	 * @param mobileThumbnailImagePath the mobileThumbnailImagePath to set
	 */
	public void setMobileThumbnailImagePath(String mobileThumbnailImagePath) {
		this.mobileThumbnailImagePath = mobileThumbnailImagePath;
	}

	/**
	 * @return the personalizationOptions
	 */
	public String getPersonalizationOptions() {
		return personalizationOptions;
	}

	/**
	 * @param personalizationOptions the personalizationOptions to set
	 */
	public void setPersonalizationOptions(String personalizationOptions) {
		this.personalizationOptions = personalizationOptions;
	}

	/**
	 * @return the personalizationDetails
	 */
	public String getPersonalizationDetails() {
		return personalizationDetails;
	}

	/**
	 * @param personalizationDetails the personalizationDetails to set
	 */
	public void setPersonalizationDetails(String personalizationDetails) {
		this.personalizationDetails = personalizationDetails;
	}

	/**
	 * @return the shipMethodUnsupported
	 */
	public boolean isShipMethodUnsupported() {
		return shipMethodUnsupported;
	}

	/**
	 * @param shipMethodUnsupported
	 *            the shipMethodUnsupported to set
	 */
	public void setShipMethodUnsupported(boolean shipMethodUnsupported) {
		this.shipMethodUnsupported = shipMethodUnsupported;
	}

	/**
	 * @return the isBts
	 */
	public boolean isBts() {
		return isBts;
	}

	/**
	 * @param isBts
	 *            the isBts to set
	 */
	public void setBts(boolean isBts) {
		this.isBts = isBts;
	}

	/**
	 * @return the attribute
	 */
	public Map<String, AttributeVO> getAttribute() {
		return mAttribute;
	}

	/**
	 * @param pAttribute
	 *            the attribute to set
	 */
	public void setAttribute(Map<String, AttributeVO> pAttribute) {
		mAttribute = pAttribute;
	}

	/**
	 * @return the skuVO
	 */
	public SKUVO getSkuVO() {
		return skuVO;
	}

	/**
	 * @param skuVO
	 *            the skuVO to set
	 */
	public void setSkuVO(SKUVO skuVO) {
		this.skuVO = skuVO;
	}

	/**
	 * @return the priceMessageVO
	 */
	public PricingMessageVO getPriceMessageVO() {
		return mPriceMessageVO;
	}

	/**
	 * @param pPriceMessageVO
	 *            the priceMessageVO to set
	 */
	public void setPriceMessageVO(PricingMessageVO pPriceMessageVO) {
		mPriceMessageVO = pPriceMessageVO;
	}

	/**
	 * @return the giftListId
	 */
	public String getGiftListId() {
		return giftListId;
	}

	/**
	 * @return the msgShownFlagOff
	 */
	public boolean isMsgShownFlagOff() {
		return msgShownFlagOff;
	}

	/**
	 * @param pMsgShownFlagOff
	 *            the msgShownFlagOff to set
	 */
	public void setMsgShownFlagOff(boolean pMsgShownFlagOff) {
		msgShownFlagOff = pMsgShownFlagOff;
	}

	/**
	 * @return the msgShownOOS
	 */
	public boolean isMsgShownOOS() {
		return msgShownOOS;
	}

	/**
	 * @param pMsgShownOOS
	 *            the msgShownOOS to set
	 */
	public void setMsgShownOOS(boolean pMsgShownOOS) {
		msgShownOOS = pMsgShownOOS;
	}

	/**
	 * @param giftListId
	 *            the giftListId to set
	 */
	public void setGiftListId(String giftListId) {
		this.giftListId = giftListId;
	}

	/**
	 * @return the productVO
	 */
	public ProductVO getProductVO() {
		return productVO;
	}

	/**
	 * @param productVO
	 *            the productVO to set
	 */
	public void setProductVO(ProductVO productVO) {
		this.productVO = productVO;
	}

	public GiftListVO()	{
		//default constructor
	}
	
	public GiftListVO(String pProdID, String pSkuID, long qty, String pPrice,
			double prevPrice, String pWishListItemId, String storeId,
			String registryId, String siteId) {
		this.mProdID=pProdID;
		this.mSkuID=pSkuID;
		this.mQuantity=qty;
		this.price=pPrice;
		this.prevPrice=prevPrice;
		this.wishListItemId=pWishListItemId;
		this.mStoreID=storeId;
		this.mRegistryID=registryId;
		this.siteId=siteId;
		
	}
	
	public String getRegistryID() {
		return mRegistryID;
	}

	public void setRegistryID(String registryID) {
		mRegistryID = registryID;
	}

	public String getStoreID() {
		return mStoreID;
	}

	public void setStoreID(String storeID) {
		mStoreID = storeID;
	}

	public GiftListVO(RepositoryItem giftRepositoryItem) {
		this.giftRepositoryItem=giftRepositoryItem;
	}

	/**
	 * @return the price
	 */
	public String getPrice() {
		return price;
	}

	/**
	 * @param pPrice
	 *            the price to set
	 */
	public void setPrice(String pPrice) {
		price = pPrice;
	}

	/**
	 * @return the prodID
	 */
	public String getProdID() {
		return mProdID;
	}

	/**
	 * @param pProdID
	 *            the prodID to set
	 */
	public void setProdID(String pProdID) {
		mProdID = pProdID;
	}

	/**
	 * @return the skuID
	 */
	public String getSkuID() {
		return mSkuID;
	}

	/**
	 * @param pSkuID
	 *            the skuID to set
	 */
	public void setSkuID(String pSkuID) {
		mSkuID = pSkuID;
	}

	/**
	 * @return the quantity
	 */
	public long getQuantity() {
		return mQuantity;
	}

	/**
	 * @param pQuantity
	 *            the quantity to set
	 */
	public void setQuantity(long pQuantity) {
		mQuantity = pQuantity;
	}

	public String getCommerceItemId() {
		return mCommerceItemId;
	}

	public void setCommerceItemId(String commerceItemId) {
		mCommerceItemId = commerceItemId;
	}

	public String getQtyPurchased() {
		return mQtyPurchased;
	}

	public void setQtyPurchased(String qtyPurchased) {
		mQtyPurchased = qtyPurchased;
	}

	public String getQtyRequested() {
		return mQtyRequested;
	}

	public void setQtyRequested(String qtyRequested) {
		mQtyRequested = qtyRequested;
	}

	public double getPrevPrice() {
		return prevPrice;
	}

	public void setPrevPrice(double prevPrice) {
		this.prevPrice = prevPrice;
	}

	public String getWishListItemId() {
		// if(this.giftRepositoryItem != null &&
		// giftRepositoryItem.getPropertyValue(BBBCoreConstants.WISHLISTID)!=null)
//		{
		// return
		// (String)giftRepositoryItem.getPropertyValue(BBBCoreConstants.WISHLISTID);
//		}
//		else
		return wishListItemId;
	}

	public void setWishListItemId(String wishListItemId) {
		this.wishListItemId = wishListItemId;
	}

	public RepositoryItem getGiftRepositoryItem() {
		return giftRepositoryItem;
	}

	public void setGiftRepositoryItem(RepositoryItem giftRepositoryItem) {
		this.giftRepositoryItem = giftRepositoryItem;
	}

	/**
	 * @return the totalPrice
	 */
	public double getTotalPrice() {
		return totalPrice;
	}

	/**
	 * @param pTotalPrice
	 *            the totalPrice to set
	 */
	public void setTotalPrice(double pTotalPrice) {
		totalPrice = pTotalPrice;
	}
	
	public RegistryVO getRegistryVO() {
		return registryVO;
	}

	public void setRegistryVO(RegistryVO registryVO) {
		this.registryVO = registryVO;
	}	
	
	/**
	 * @return the ltlShipMethod
	 */
	public String getLtlShipMethod() {
		return this.ltlShipMethod;
	}

	/**
	 * @param ltlShipMethod
	 *            the ltlShipMethod to set
	 */
	public void setLtlShipMethod(String ltlShipMethod) {
		this.ltlShipMethod = ltlShipMethod;
	}
	
	public String getLtlShipMethodDesc() {
		return ltlShipMethodDesc;
	}

	public void setLtlShipMethodDesc(String ltlShipMethodDesc) {
		this.ltlShipMethodDesc = ltlShipMethodDesc;
	}
	
	public double getAssemblyFees() {
		return assemblyFees;
}

	public void setAssemblyFees(double assemblyFees) {
		this.assemblyFees = assemblyFees;
	}

	public double getDeliverySurcharge() {
		return deliverySurcharge;
	}

	public void setDeliverySurcharge(double deliverySurcharge) {
		this.deliverySurcharge = deliverySurcharge;
	}
	

}
