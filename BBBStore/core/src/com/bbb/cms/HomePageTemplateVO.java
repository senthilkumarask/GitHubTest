/**
 * 
 */
package com.bbb.cms;

import java.io.Serializable;
import java.util.List;

import com.bbb.commerce.catalog.vo.ProductVO;

/**
 * @author iteggi
 *
 */
public class HomePageTemplateVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mSiteId;
	private PromoImageVO mLogoImage;
	private List<PromoBoxVO> mHeroImages;
	private PromoBoxVO mPromoBoxFirst;
	private PromoBoxVO mPromoBoxSecond;
	private String mRegistryStatus;
	private ProductVO mFeaturedProduct;
	private List<ProductVO> mSecondaryProducts;
	private List<PromoBoxLayoutVO> mPromoTierLayout1;
	private List<PromoBoxLayoutVO> mPromoTierLayout2;
	private List<PromoBoxLayoutVO> mPromoTierLayout3;
	private Boolean mJustForYouFlag;
	private Boolean mClearanceDealsFlag;
	private List<CategoryContainerVO> mCategoryContainer;
	private List<String> mHomePopSearch;
	
	private List<CategoryContainerVO> mFeaturedCategoryContainer;
	private PromoBoxVO mAnnouncement;
	/**
	 * @return the siteId
	 */
	public String getSiteId() {
		return mSiteId;
	}
	/**
	 * @param pSiteId the siteId to set
	 */
	public void setSiteId(String pSiteId) {
		mSiteId = pSiteId;
	}
	
	/**
	 * @return the logoImage
	 */
	public PromoImageVO getLogoImage() {
		return mLogoImage;
	}
	/**
	 * @param pLogoImage the logoImage to set
	 */
	public void setLogoImage(PromoImageVO pLogoImage) {
		this.mLogoImage = pLogoImage;
	}
	/**
	 * @return the heroImages
	 */
	public List<PromoBoxVO> getHeroImages() {
		return mHeroImages;
	}
	/**
	 * @param pHeroImages the heroImages to set
	 */
	public void setHeroImages(List<PromoBoxVO> pHeroImages) {
		mHeroImages = pHeroImages;
	}
	/**
	 * @return the promoBoxFirst
	 */
	public PromoBoxVO getPromoBoxFirst() {
		return mPromoBoxFirst;
	}
	/**
	 * @param pPromoBoxFirst the promoBoxFirst to set
	 */
	public void setPromoBoxFirst(PromoBoxVO pPromoBoxFirst) {
		mPromoBoxFirst = pPromoBoxFirst;
	}
	/**
	 * @return the promoBoxSecond
	 */
	public PromoBoxVO getPromoBoxSecond() {
		return mPromoBoxSecond;
	}
	/**
	 * @param pPromoBoxSecond the promoBoxSecond to set
	 */
	public void setPromoBoxSecond(PromoBoxVO pPromoBoxSecond) {
		mPromoBoxSecond = pPromoBoxSecond;
	}
	/**
	 * @return the registryStatus
	 */
	public String getRegistryStatus() {
		return mRegistryStatus;
	}
	/**
	 * @param pRegistryStatus the registryStatus to set
	 */
	public void setRegistryStatus(String pRegistryStatus) {
		mRegistryStatus = pRegistryStatus;
	}
	/**
	 * @return the featuredProduct
	 */
	public ProductVO getFeaturedProduct() {
		return mFeaturedProduct;
	}
	/**
	 * @param pFeaturedProduct the featuredProduct to set
	 */
	public void setFeaturedProduct(ProductVO pFeaturedProduct) {
		mFeaturedProduct = pFeaturedProduct;
	}
	/**
	 * @return the secondaryProducts
	 */
	public List<ProductVO> getSecondaryProducts() {
		return mSecondaryProducts;
	}
	/**
	 * @param pSecondaryProducts the secondaryProducts to set
	 */
	public void setSecondaryProducts(List<ProductVO> pSecondaryProducts) {
		mSecondaryProducts = pSecondaryProducts;
	}
	/**
	 * @return the promoTierLayout1
	 */
	public List<PromoBoxLayoutVO> getPromoTierLayout1() {
		return mPromoTierLayout1;
	}
	/**
	 * @param pPromoTierLayout1 the promoTierLayout1 to set
	 */
	public void setPromoTierLayout1(List<PromoBoxLayoutVO> pPromoTierLayout1) {
		mPromoTierLayout1 = pPromoTierLayout1;
	}
	/**
	 * @return the promoTierLayout2
	 */
	public List<PromoBoxLayoutVO> getPromoTierLayout2() {
		return mPromoTierLayout2;
	}
	/**
	 * @param pPromoTierLayout2 the promoTierLayout2 to set
	 */
	public void setPromoTierLayout2(List<PromoBoxLayoutVO> pPromoTierLayout2) {
		mPromoTierLayout2 = pPromoTierLayout2;
	}
	/**
	 * @return the promoTierLayout3
	 */
	public List<PromoBoxLayoutVO> getPromoTierLayout3() {
		return mPromoTierLayout3;
	}
	/**
	 * @param pPromoTierLayout3 the promoTierLayout3 to set
	 */
	public void setPromoTierLayout3(List<PromoBoxLayoutVO> pPromoTierLayout3) {
		mPromoTierLayout3 = pPromoTierLayout3;
	}
	
	
	public Boolean getJustForYouFlag() {
		return mJustForYouFlag;
	}
	public void setJustForYouFlag(Boolean pJustForYouFlag) {
		this.mJustForYouFlag = pJustForYouFlag;
	}
	
	public Boolean getClearanceDealsFlag() {
		return mClearanceDealsFlag;
	}
	public void setClearanceDealsFlag(Boolean pClearanceDealsFlag) {
		this.mClearanceDealsFlag = pClearanceDealsFlag;
	}
	/**
	 * @return the categoryContainer
	 */
	public List<CategoryContainerVO> getCategoryContainer() {
		return mCategoryContainer;
	}
	/**
	 * @param pCategoryContainer the categoryContainer to set
	 */
	public void setCategoryContainer(List<CategoryContainerVO> pCategoryContainer) {
		mCategoryContainer = pCategoryContainer;
	}
	/**
	 * @return the Popular Search Terms
	 */
	public List<String> getHomePopSearch() {
		return mHomePopSearch;
	}
	/**
	 * @param pCatPopSearch the Popular Search Terms to set
	 */
	public void setHomePopSearch(List<String> pHomePopSearch) {
		mHomePopSearch = pHomePopSearch;
	}
	

	
	/**
	 * @return the featuredCategoryContainer
	 */
	public List<CategoryContainerVO> getFeaturedCategoryContainer() {
		return mFeaturedCategoryContainer;
	}
	/**
	 * @param pFeaturedCategoryContainer the featuredCategoryContainer to set
	 */
	public void setFeaturedCategoryContainer(
			List<CategoryContainerVO> pFeaturedCategoryContainer) {
		this.mFeaturedCategoryContainer = pFeaturedCategoryContainer;
	}
	/**
	 * @return the announcement
	 */
	public PromoBoxVO getAnnouncement() {
		return mAnnouncement;
	}
	/**
	 * @param pAnnouncement the announcement to set
	 */
	public void setAnnouncement(PromoBoxVO pAnnouncement) {
		this.mAnnouncement = pAnnouncement;
	}
	public String toString(){
		StringBuffer toString=new StringBuffer(" Home Page VO for Site ");
		toString.append(mSiteId).append("\n");
		if(mHeroImages!=null && !mHeroImages.isEmpty()){
			int i=0;
			for(PromoBoxVO heroImage:mHeroImages){
				toString.append(++i+") ").append(heroImage.toString());
			}
		}
		else{
			toString.append("Hero Images are not available for the home page").append("\n");
		}
		toString.append(" First promo Box Details for the Home Page \n ");
		if(mPromoBoxFirst!=null){
			toString.append(mPromoBoxFirst.toString());
		}
		else{
			toString.append("First Promo Box is null for the Home Page \n");
		}
		if(mPromoBoxSecond!=null){
			toString.append(mPromoBoxSecond.toString());
		}
		else{
			toString.append("Second Promo Box is null for the Home Page \n");
		}
		toString.append(this.getRegistryStatus()!=null?this.getRegistryStatus():"Null ").append("\n");
		if(mFeaturedProduct!=null){
			toString.append(mFeaturedProduct.toString());
		}
		else{
			toString.append("Featured Product is null for the Home Page \n ");
		}
		if(mSecondaryProducts!=null && !mSecondaryProducts.isEmpty()){
			int i=0;
			for(ProductVO productVO:mSecondaryProducts){
				toString.append(++i+") ").append(productVO.toString());
			}
		}
		else{
			toString.append("Second Promo Box is null for the Home Page");
		}
		if(mPromoTierLayout1!=null && !mPromoTierLayout1.isEmpty()){
			toString.append(" \n Promo Box Layout 1 Details  for the Home Page \n");
			for(PromoBoxLayoutVO PromoBoxLayoutVO:mPromoTierLayout1){
				toString.append(PromoBoxLayoutVO.toString());
			}
		}
		else{
			toString.append(" Promo Box Layout 1 is null for the Home Page");
		}
		if(mPromoTierLayout2!=null && !mPromoTierLayout2.isEmpty()){
			toString.append(" Promo Box Layout 2 Details  for the Home Page \n");
			for(PromoBoxLayoutVO PromoBoxLayoutVO:mPromoTierLayout2){
				toString.append(PromoBoxLayoutVO.toString());
			}
		}
		else{
			toString.append(" Promo Box Layout 2 is null for the Home Page");
		}
		if(mPromoTierLayout3!=null && !mPromoTierLayout3.isEmpty()){
			toString.append(" Promo Box Layout 3 Details  for the Home Page \n");
			for(PromoBoxLayoutVO PromoBoxLayoutVO:mPromoTierLayout3){
				toString.append(PromoBoxLayoutVO.toString());
			}
		}
		else{
			toString.append(" Promo Box Layout 3 is null for the Home Page \n");
		}
		if(mHomePopSearch!=null && !mHomePopSearch.isEmpty()){
			int i=0;
			toString.append("Popular Search Terms for the Home Page");
			for(String term:mHomePopSearch){
				toString.append(++i+") ").append(term);
			}
		}
		else{
			toString.append("Popular Search Terms is null for the Home Page");
		}
		toString.append(" Just For You Flag value ").append(mJustForYouFlag!=null?mJustForYouFlag:"NULL").append("\n")
		.append(" Clearance Deals Flag value ").append(mClearanceDealsFlag!=null?mClearanceDealsFlag:"NULL").append("\n");
		if(mCategoryContainer!=null && !mCategoryContainer.isEmpty()){
			toString.append(" Category Content VO Details  for the Home Page \n");
			int i=0;
			for(CategoryContainerVO categoryContainerVO:mCategoryContainer){
				toString.append(++i+") ").append(categoryContainerVO.toString()).append("\n");
			}
		}
		else{
			toString.append(" Category Content VO  is null for the Home Page \n");
		}
		return toString.toString();
	}

}
