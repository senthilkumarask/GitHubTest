package com.bbb.importprocess.vo;

import java.sql.Date;

import atg.core.util.StringUtils;

public class ItemVO {

	private String mProductTitle;
	private String mCAProductTitle;
	private String mBABProductTitle;
	private String mShortDescription;
	private String mCAShortDescription;
	private String mBABShortDescription;
	private String mWebDescrip;
	private String mCAWebDescrip;
	private String mBABWebDescrip;
	
	//STOFU properties
	private String mGSProductTitle;
	private String mGSCAProductTitle;
	private String mGSBABProductTitle;
	private String mGSShortDescription;
	private String mGSCAShortDescription;
	private String mGSBABShortDescription;
	private String mGSWebDescrip;
	private String mGSCAWebDescrip;
	private String mGSBABWebDescrip;
	//End of STOFU properties
	private boolean mCollectionFlag;
	private boolean mProductFlag;
	private boolean mLeadProduct;
	private String mImgHorzLoc;
	private String mImgVertLoc;
	private ProductVO mProductVO;
	private SkuVO mSkuVO;
	private String mImgSmallLoc;
	private String mImgMedLoc;
	private String mImgLargeLoc;
	private String mImgSwatchLoc;
	private String mImgZoomLoc;
	private String mAnyWhereZoom;
	private String mCollectionThumbnail;
	private String mImgZoomIndex;

	private String mWebOfferedFlag;
  private String mBABWebOfferedFlag;
  private String mCAWebOfferedFlag;
  
  private String mDisableFlag;
  private String mBABDisableFlag;
  private String mCADisableFlag;
 
  private String mWebOnlyFlag;  
  private String mCAWebOnlyFlag;  
  private String mBabWebOnlyFlag; 
  
  private String mSkuCollegeId; 
  private String mEmailOutOfStockFlag; 
  private boolean mBopusExclusion;
  
  //sql date newly added
  /** Added Site Specific Enable Date for R2.1 #53 Endeca & PIM FEED*/
  private Date mEnableDate;
  private Date mBABEnableDate;
  private Date mCAEnableDate;
  //addition of cost
  private String mCost;
  private String mCACost;
  private String designatedProductId;
  
  
  // added GS(Stofu) related flags :Begin

	private boolean mGSBBBWebOfferedFlag;
	private boolean mGSBABWebOfferedFlag;
	private boolean mGSCAWebOfferedFlag;
	private boolean mGSBBBDisabledFlag;
	private boolean mGSBABDisabledFlag;
	private boolean mGSCADisabledFlag;
	
// added product kill flags for all 4 concepts
	private boolean disableForeverPDPFlag;
	private boolean babDisableForeverPDPFlag;
	private boolean caDisableForeverPDPFlag;
	private boolean gsDisableForeverPDPFlag;
	
	public boolean isDisableForeverPDPFlag() {
		return disableForeverPDPFlag;
	}

	public void setDisableForeverPDPFlag(boolean disableForeverPDPFlag) {
		this.disableForeverPDPFlag = disableForeverPDPFlag;
	}

	public boolean isBabDisableForeverPDPFlag() {
		return babDisableForeverPDPFlag;
	}

	public void setBabDisableForeverPDPFlag(boolean babDisableForeverPDPFlag) {
		this.babDisableForeverPDPFlag = babDisableForeverPDPFlag;
	}

	public boolean isCaDisableForeverPDPFlag() {
		return caDisableForeverPDPFlag;
	}

	public void setCaDisableForeverPDPFlag(boolean caDisableForeverPDPFlag) {
		this.caDisableForeverPDPFlag = caDisableForeverPDPFlag;
	}

	public boolean isGsDisableForeverPDPFlag() {
		return gsDisableForeverPDPFlag;
	}

	public void setGsDisableForeverPDPFlag(boolean gsDisableForeverPDPFlag) {
		this.gsDisableForeverPDPFlag = gsDisableForeverPDPFlag;
	}
	
	//End ever living pdp flags
	
	public boolean isGSBBBWebOfferedFlag() {
		return mGSBBBWebOfferedFlag;
	}

	public void setGSBBBWebOfferedFlag(boolean mGSBBBWebOfferedFlag) {
		this.mGSBBBWebOfferedFlag = mGSBBBWebOfferedFlag;
	}

	public boolean isGSBABWebOfferedFlag() {
		return mGSBABWebOfferedFlag;
	}

	public void setGSBABWebOfferedFlag(boolean mGSBABWebOfferedFlag) {
		this.mGSBABWebOfferedFlag = mGSBABWebOfferedFlag;
	}

	public boolean isGSCAWebOfferedFlag() {
		return mGSCAWebOfferedFlag;
	}

	public void setGSCAWebOfferedFlag(boolean mGSCAWebOfferedFlag) {
		this.mGSCAWebOfferedFlag = mGSCAWebOfferedFlag;
	}

	public boolean isGSBBBDisabledFlag() {
		return mGSBBBDisabledFlag;
	}

	public void setGSBBBDisabledFlag(boolean mGSBBBDisabledFlag) {
		this.mGSBBBDisabledFlag = mGSBBBDisabledFlag;
	}

	public boolean isGSBABDisabledFlag() {
		return mGSBABDisabledFlag;
	}

	public void setGSBABDisabledFlag(boolean mGSBABDisabledFlag) {
		this.mGSBABDisabledFlag = mGSBABDisabledFlag;
	}

	public boolean isGSCADisabledFlag() {
		return mGSCADisabledFlag;
	}

	public void setGSCADisabledFlag(boolean mGSCADisabledFlag) {
		this.mGSCADisabledFlag = mGSCADisabledFlag;
	}

	// end

	/** Getter & Setter Site Specific Enable Date for R2.1 #53 Endeca & PIM FEED*/
	/**
	 * @return
	 */
	public Date getBABEnableDate() {
		return mBABEnableDate;
	}

	/**
	 * @param mBABEnableDate
	 */
	public void setBABEnableDate(Date mBABEnableDate) {
		this.mBABEnableDate = mBABEnableDate;
	}

	/**
	 * @return
	 */
	public Date getCAEnableDate() {
		return mCAEnableDate;
	}

	/**
	 * @param mCAEnableDate
	 */
	public void setCAEnableDate(Date mCAEnableDate) {
		this.mCAEnableDate = mCAEnableDate;
	}
	//STOFU properties
	private Date mGSEnableDate;
	private Date mGSBABEnableDate;
	private Date mGSCAEnableDate;
	//end of STOFU properties
	public String getGSProductTitle() {
		if (!StringUtils.isEmpty(mGSProductTitle)) {

			return mGSProductTitle.trim();
		}
		return mGSProductTitle;
	}

	public void setGSProductTitle(String gSProductTitle) {
		mGSProductTitle = gSProductTitle;
	}

	public String getGSCAProductTitle() {
		if (!StringUtils.isEmpty(mGSCAProductTitle)) {

			return mGSCAProductTitle.trim();
		}
		return mGSCAProductTitle;
	}

	public void setGSCAProductTitle(String gSCAProductTitle) {
		mGSCAProductTitle = gSCAProductTitle;
	}

	public String getGSBABProductTitle() {
		if (!StringUtils.isEmpty(mGSBABProductTitle)) {

			return mGSBABProductTitle.trim();
		}
		return mGSBABProductTitle;
	}

	public void setGSBABProductTitle(String gSBABProductTitle) {
		mGSBABProductTitle = gSBABProductTitle;
	}

	public String getGSShortDescription() {
		if (!StringUtils.isEmpty(mGSShortDescription)) {

			return mGSShortDescription.trim();
		}
		return mGSShortDescription;
	}

	public void setGSShortDescription(String gSShortDescription) {
		mGSShortDescription = gSShortDescription;
	}

	public String getGSCAShortDescription() {
		if (!StringUtils.isEmpty(mGSCAShortDescription)) {

			return mGSCAShortDescription.trim();
		}
		return mGSCAShortDescription;
	}

	public void setGSCAShortDescription(String gSCAShortDescription) {
		mGSCAShortDescription = gSCAShortDescription;
	}

	public String getGSBABShortDescription() {
		if (!StringUtils.isEmpty(mGSBABShortDescription)) {

			return mGSBABShortDescription.trim();
		}
		return mGSBABShortDescription;
	}

	public void setGSBABShortDescription(String gSBABShortDescription) {
		mGSBABShortDescription = gSBABShortDescription;
	}

	public String getGSWebDescrip() {
		if (!StringUtils.isEmpty(mGSWebDescrip)) {

			return mGSWebDescrip.trim();
		}
		return mGSWebDescrip;
	}

	public void setGSWebDescrip(String gSWebDescrip) {
		mGSWebDescrip = gSWebDescrip;
	}
	
	

	public String getGSCAWebDescrip() {
		if (!StringUtils.isEmpty(mGSCAWebDescrip)) {

			return mGSCAWebDescrip.trim();
		}
		return mGSCAWebDescrip;
	}

	public void setGSCAWebDescrip(String gSCAWebDescrip) {
		mGSCAWebDescrip = gSCAWebDescrip;
	}

	public String getGSBABWebDescrip() {
		if (!StringUtils.isEmpty(mGSBABWebDescrip)) {

			return mGSBABWebDescrip.trim();
		}
		return mGSBABWebDescrip;
	}

	public void setGSBABWebDescrip(String gSBABWebDescrip) {
		mGSBABWebDescrip = gSBABWebDescrip;
	}

	public Date getGSEnableDate() {
		return mGSEnableDate;
	}

	public void setGSEnableDate(Date gSEnableDate) {
		mGSEnableDate = gSEnableDate;
	}

	public Date getGSBABEnableDate() {
		return mGSBABEnableDate;
	}

	public void setGSBABEnableDate(Date gSBABEnableDate) {
		mGSBABEnableDate = gSBABEnableDate;
	}

	public Date getGSCAEnableDate() {
		return mGSCAEnableDate;
	}

	public void setGSCAEnableDate(Date gSCAEnableDate) {
		mGSCAEnableDate = gSCAEnableDate;
	}

	/**
	 * @return the productTitle
	 */
	public String getProductTitle() {
		if (!StringUtils.isEmpty(mProductTitle)) {

			return mProductTitle.trim();
		}
		return mProductTitle;
	}

	/**
	 * @param pProductTitle
	 *            the productTitle to set
	 */
	public void setProductTitle(final String pProductTitle) {
		mProductTitle = pProductTitle;
	}

	/**
	 * @return thCACAoductTitle
	 */
	public String getCAProductTitle() {
		if (!StringUtils.isEmpty(mCAProductTitle)) {

			return mCAProductTitle.trim();
		}
		return mCAProductTitle;
	}

	/**
	 * @param pCAProductTitle
	 *            the CAProductTitle to set
	 */
	public void setCAProductTitle(final String pCAProductTitle) {
		mCAProductTitle = pCAProductTitle;
	}

	/**
	 * @return the bABProductTitle
	 */
	public String getBABProductTitle() {
		if (!StringUtils.isEmpty(mBABProductTitle)) {

			return mBABProductTitle.trim();
		}
		return mBABProductTitle;
	}

	/**
	 * @param pBABProductTitle
	 *            the bABProductTitle to set
	 */
	public void setBABProductTitle(final String pBABProductTitle) {
		mBABProductTitle = pBABProductTitle;
	}

	/**
	 * @return the shortDescription
	 */
	public String getShortDescription() {
		if (!StringUtils.isEmpty(mShortDescription)) {

			return mShortDescription.trim();
		}
		return mShortDescription;
	}

	/**
	 * @param pShortDescription
	 *            the shortDescription to set
	 */
	public void setShortDescription(final String pShortDescription) {
		mShortDescription = pShortDescription;
	}

	/**
	 * @return the CAShortDescription
	 */
	public String getCAShortDescription() {
		if (!StringUtils.isEmpty(mCAShortDescription)) {

			return mCAShortDescription.trim();
		}
		return mCAShortDescription;
	}

	/**
	 * @param pCAShortDescription
	 *            the CAShortDescription to set
	 */
	public void setCAShortDescription(final String pCAShortDescription) {
		mCAShortDescription = pCAShortDescription;
	}

	/**
	 * @return the bABShortDescription
	 */
	public String getBABShortDescription() {
		if (!StringUtils.isEmpty(mBABShortDescription)) {

			return mBABShortDescription.trim();
		}
		return mBABShortDescription;
	}

	/**
	 * @param pBABShortDescription
	 *            the bABShortDescription to set
	 */
	public void setBABShortDescription(final String pBABShortDescription) {
		mBABShortDescription = pBABShortDescription;
	}

	/**
	 * @return the webDescrip
	 */
	public String getWebDescrip() {
		if (!StringUtils.isEmpty(mWebDescrip)) {

			return mWebDescrip.trim();
		}
		return mWebDescrip;
	}

	/**
	 * @param pWebDescrip
	 *            the webDescrip to set
	 */
	public void setWebDescrip(final String pWebDescrip) {
		mWebDescrip = pWebDescrip;
	}

	/**
	 * @return the CAWebDescrip
	 */
	public String getCAWebDescrip() {
		if (!StringUtils.isEmpty(mCAWebDescrip)) {

			return mCAWebDescrip.trim();
		}
		return mCAWebDescrip;
	}

	/**
	 * @param pCAWebDescrip
	 *            the CAWebDescrip to set
	 */
	public void setCAWebDescrip(final String pCAWebDescrip) {
		mCAWebDescrip = pCAWebDescrip;
	}

	/**
	 * @return the bABWebDescrip
	 */
	public String getBABWebDescrip() {
		if (!StringUtils.isEmpty(mBABWebDescrip)) {

			return mBABWebDescrip.trim();
		}
		return mBABWebDescrip;
	}

	/**
	 * @param pBABWebDescrip
	 *            the bABWebDescrip to set
	 */
	public void setBABWebDescrip(final String pBABWebDescrip) {
		mBABWebDescrip = pBABWebDescrip;
	}

	/**
	 * @return the collectionFlag
	 */
	public boolean isCollectionFlag() {

		return mCollectionFlag;
	}

	/**
	 * @param pCollectionFlag
	 *            the collectionFlag to set
	 */
	public void setCollectionFlag(boolean pCollectionFlag) {
		mCollectionFlag = pCollectionFlag;
	}

	/**
	 * @return the productFlag
	 */
	public boolean isProductFlag() {
		return mProductFlag;
	}

	/**
	 * @param pProductFlag
	 *            the productFlag to set
	 */
	public void setProductFlag(final boolean pProductFlag) {
		mProductFlag = pProductFlag;
	}

	/**
	 * @return the productFlag
	 */
	public boolean isLeadProduct() {
		return mLeadProduct;
	}

	/**
	 * @param pProductFlag
	 *            the productFlag to set
	 */
	public void setLeadProduct(final boolean pLeadProduct) {
		mLeadProduct = pLeadProduct;
	}

	/**
	 * @return the O
	 */
	public ProductVO getProductVO() {

		if (mProductVO == null) {
			mProductVO = new ProductVO();
		}
		return mProductVO;
	}

	/**
	 * @param pProductVO
	 *            the productVO to set
	 */
	public void setProductVO(final ProductVO pProductVO) {
		mProductVO = pProductVO;
	}

	/**
	 * @return the skuVO
	 */
	public SkuVO getSkuVO() {
		if (mSkuVO == null) {
			mSkuVO = new SkuVO();
		}
		return mSkuVO;
	}

	/**
	 * @param pSkuVO
	 *            the skuVO to set
	 */
	public void setSkuVO(SkuVO pSkuVO) {
		mSkuVO = pSkuVO;
	}

	/**
	 * @return the imgSmallLoc
	 */
	public String getImgSmallLoc() {
		if (!StringUtils.isEmpty(mImgSmallLoc)) {

			return mImgSmallLoc.trim();
		}
		return mImgSmallLoc;
	}

	/**
	 * @param pImgSmallLoc
	 *            the imgSmallLoc to set
	 */
	public void setImgSmallLoc(final String pImgSmallLoc) {
		mImgSmallLoc = pImgSmallLoc;
	}

	/**
	 * @return the imgMedLoc
	 */
	public String getImgMedLoc() {
		if (!StringUtils.isEmpty(mImgMedLoc)) {

			return mImgMedLoc.trim();
		}
		return mImgMedLoc;
	}

	/**
	 * @param pImgMedLoc
	 *            the imgMedLoc to set
	 */
	public void setImgMedLoc(final String pImgMedLoc) {
		mImgMedLoc = pImgMedLoc;
	}

	/**
	 * @return the imgLargeLoc
	 */
	public String getImgLargeLoc() {
		if (!StringUtils.isEmpty(mImgLargeLoc)) {

			return mImgLargeLoc.trim();
		}
		return mImgLargeLoc;
	}

	/**
	 * @param pImgLargeLoc
	 *            the imgLargeLoc to set
	 */
	public void setImgLargeLoc(final String pImgLargeLoc) {
		mImgLargeLoc = pImgLargeLoc;
	}

	/**
	 * @return the imgSwatchLoc
	 */
	public String getImgSwatchLoc() {
		if (!StringUtils.isEmpty(mImgSwatchLoc)) {

			return mImgSwatchLoc.trim();
		}
		return mImgSwatchLoc;
	}

	/**
	 * @param pImgSwatchLoc
	 *            the imgSwatchLoc to set
	 */
	public void setImgSwatchLoc(final String pImgSwatchLoc) {
		mImgSwatchLoc = pImgSwatchLoc;
	}

	/**
	 * @return the imgZoomLoc
	 */
	public String getImgZoomLoc() {
		if (!StringUtils.isEmpty(mImgZoomLoc)) {

			return mImgZoomLoc.trim();
		}
		return mImgZoomLoc;
	}

	/**
	 * @param pImgZoomLoc
	 *            the imgZoomLoc to set
	 */
	public void setImgZoomLoc(final String pImgZoomLoc) {
		mImgZoomLoc = pImgZoomLoc;
	}

	/**
	 * @return the imgZoomIndex
	 */
	public String getImgZoomIndex() {
		if (!StringUtils.isEmpty(mImgZoomIndex)) {

			return mImgZoomIndex.trim();
		}
		return mImgZoomIndex;
	}

	/**
	 * @param pImgZoomIndex
	 *            the imgZoomIndex to set
	 */
	public void setImgZoomIndex(String pImgZoomIndex) {
		mImgZoomIndex = pImgZoomIndex;
	}

	/**
	 * @return the webOfferedFlag
	 */
	public String isWebOfferedFlag() {
		if (!StringUtils.isEmpty(mWebOfferedFlag)) {

			return mWebOfferedFlag.trim();
		}
		return mWebOfferedFlag;
	}

	/**
	 * @param pWebOfferedFlag
	 *            the webOfferedFlag to set
	 */
	public void setWebOfferedFlag(String pWebOfferedFlag) {
		mWebOfferedFlag = pWebOfferedFlag;
	}

	/**
	 * @return the babWebOfferedFlag
	 */
	public String isBABWebOfferedFlag() {
		if (!StringUtils.isEmpty(mBABWebOfferedFlag)) {

			return mBABWebOfferedFlag.trim();
		}
		return mBABWebOfferedFlag;
	}

	/**
	 * @param pBABWebOfferedFlag
	 *            the babWebOfferedFlag to set
	 */
	public void setBABWebOfferedFlag(String pBABWebOfferedFlag) {
		mBABWebOfferedFlag = pBABWebOfferedFlag;
	}

	/**
	 * @return the CAWebOfferedFlag
	 */
	public String isCAWebOfferedFlag() {
		if (!StringUtils.isEmpty(mCAWebOfferedFlag)) {

			return mCAWebOfferedFlag.trim();
		}
		return mCAWebOfferedFlag;
	}

	/**
	 * @param pCAWebOfferedFlag
	 *            the CAWebOfferedFlag to set
	 */
	public void setCAWebOfferedFlag(String pCAWebOfferedFlag) {
		mCAWebOfferedFlag = pCAWebOfferedFlag;
	}

	/**
	 * @return the disableFlag
	 */
	public String isDisableFlag() {
		if (!StringUtils.isEmpty(mDisableFlag)) {

			return mDisableFlag.trim();
		}
		return mDisableFlag;
	}

	/**
	 * @param pProductDisableFlag
	 *            the disableFlag to set
	 */
	public void setDisableFlag(String pProductDisableFlag) {
		mDisableFlag = pProductDisableFlag;
	}

	/**
	 * @return the bABDisableFlag
	 */
	public String isBABDisableFlag() {
		if (!StringUtils.isEmpty(mBABDisableFlag)) {

			return mBABDisableFlag.trim();
		}
		return mBABDisableFlag;
	}

	/**
	 * @param pBABDisableFlag
	 *            the bABDisableFlag to set
	 */
	public void setBABDisableFlag(String pBABDisableFlag) {
		mBABDisableFlag = pBABDisableFlag;
	}

	/**
	 * @return the CADisableFlag
	 */
	public String isCADisableFlag() {
		if (!StringUtils.isEmpty(mCADisableFlag)) {

			return mCADisableFlag.trim();
		}
		return mCADisableFlag;
	}

	/**
	 * @param pCADisableFlag
	 *            the CADisableFlag to set
	 */
	public void setCADisableFlag(String pCADisableFlag) {
		mCADisableFlag = pCADisableFlag;
	}

	/**
	 * @return the webOnlyFlag
	 */
	public String isWebOnlyFlag() {
		if (!StringUtils.isEmpty(mWebOnlyFlag)) {

			return mWebOnlyFlag.trim();
		}
		return mWebOnlyFlag;
	}

	/**
	 * @param pWebOnlyFlag
	 *            the webOnlyFlag to set
	 */
	public void setWebOnlyFlag(final String pWebOnlyFlag) {
		mWebOnlyFlag = pWebOnlyFlag;
	}

	/**
	 * @return the CAWebOnlyFlag
	 */
	public String getCAWebOnlyFlag() {
		if (!StringUtils.isEmpty(mCAWebOnlyFlag)) {

			return mCAWebOnlyFlag.trim();
		}
		return mCAWebOnlyFlag;
	}

	/**
	 * @param pCaWebOnlyFlag
	 *            the CAWebOnlyFlag to set
	 */
	public void setCAWebOnlyFlag(final String pCaWebOnlyFlag) {
		mCAWebOnlyFlag = pCaWebOnlyFlag;
	}

	/**
	 * @return the babWebOnlyFlag
	 */
	public String getBabWebOnlyFlag() {
		if (!StringUtils.isEmpty(mBabWebOnlyFlag)) {

			return mBabWebOnlyFlag.trim();
		}
		return mBabWebOnlyFlag;
	}

	/**
	 * @param pBabWebOnlyFlag
	 *            the babWebOnlyFlag to set
	 */
	public void setBabWebOnlyFlag(final String pBabWebOnlyFlag) {
		mBabWebOnlyFlag = pBabWebOnlyFlag;
	}

	/**
	 * @return the skuCollegeId
	 */
	public String isSkuCollegeId() {
		if (!StringUtils.isEmpty(mSkuCollegeId)) {

			return mSkuCollegeId.trim();
		}
		return mSkuCollegeId;
	}

	/**
	 * @param pSkuCollegeId
	 *            the skuCollegeId to set
	 */
	public void setSkuCollegeId(String pSkuCollegeId) {
		mSkuCollegeId = pSkuCollegeId;
	}

	/**
	 * @return the emailOutOfStockFlag
	 */
	public String isEmailOutOfStockFlag() {
		if (!StringUtils.isEmpty(mEmailOutOfStockFlag)) {

			return mEmailOutOfStockFlag.trim();
		}
		return mEmailOutOfStockFlag;
	}

	/**
	 * @param pEmailOutOfStockFlag
	 *            the emailOutOfStockFlag to set
	 */
	public void setEmailOutOfStockFlag(String pEmailOutOfStockFlag) {

		mEmailOutOfStockFlag = pEmailOutOfStockFlag;
	}

	/**
	 * @return the anyWhereZoom
	 */
	public String getAnyWhereZoom() {

		if (!StringUtils.isEmpty(mAnyWhereZoom)) {

			return mAnyWhereZoom.trim();
		}
		return mAnyWhereZoom;
	}

	/**
	 * @param pAnyWhereZoom
	 *            the anyWhereZoom to set
	 */
	public void setAnyWhereZoom(String pAnyWhereZoom) {
		mAnyWhereZoom = pAnyWhereZoom;
	}

	/**
	 * @return the collectionThumbnail
	 */
	public String getCollectionThumbnail() {

		if (!StringUtils.isEmpty(mCollectionThumbnail)) {

			return mCollectionThumbnail.trim();
		}
		return mCollectionThumbnail;
	}

	/**
	 * @param pCollectionThumbnail
	 *            the collectionThumbnail to set
	 */
	public void setCollectionThumbnail(String pCollectionThumbnail) {
		mCollectionThumbnail = pCollectionThumbnail;
	}

	public boolean isBopusExclusion() {
		return mBopusExclusion;
	}

	public void setBopusExclusion(boolean pBopusExclusion) {
		this.mBopusExclusion = pBopusExclusion;
	}

	/**
	 * @return
	 */
	public String getImgHorzLoc() {
		return mImgHorzLoc;
	}

	/**
	 * @param mImgHorzLoc
	 */
	public void setImgHorzLoc(String mImgHorzLoc) {
		this.mImgHorzLoc = mImgHorzLoc;
	}

	/**
	 * @return
	 */
	public String getImgVertLoc() {
		return mImgVertLoc;
	}

	/**
	 * @param mImgVertLoc
	 */
	public void setImgVertLoc(String mImgVertLoc) {
		this.mImgVertLoc = mImgVertLoc;
	}

	/**
	 * @return
	 */
	public Date getEnableDate() {
		return mEnableDate;
	}

	/**
	 * @param mEnableDate
	 */
	public void setEnableDate(Date mEnableDate) {
		this.mEnableDate = mEnableDate;
	}
	
	
	/**
	 * @return the mCACost
	 */
	public String getmCACost() {
		return mCACost;
	}
	/**
	 * @param mCACost the mCACost to set
	 */
	public void setmCACost(String mCACost) {
		this.mCACost = mCACost;
	}
	/**
	 * @return the mCost
	 */
	public String getmCost() {
		return mCost;
	}
	/**
	 * @param mCost the mCost to set
	 */
	public void setmCost(String mCost) {
		this.mCost = mCost;
	}
	
	/**
	 * @return the designatedProductId
	 */
	public String getDesignatedProductId() {
		return designatedProductId;
	}

	/**
	 * @param designatedProductId
	 *            the designatedProductId to set
	 */
	public void setDesignatedProductId(final String pDesignatedProductId) {
		designatedProductId = pDesignatedProductId;
	}
}
