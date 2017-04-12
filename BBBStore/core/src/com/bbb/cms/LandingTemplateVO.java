package com.bbb.cms;

import java.io.Serializable;
import java.util.List;

import atg.core.util.StringUtils;

import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.catalog.vo.BrandVO;

public class LandingTemplateVO  implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mPageName;
	private String mPageTitle;
	private List<PromoImageVO> mBridalTool;
	private List<PromoImageVO> mRegistryCategoryImage;
	private String mMarketingBannerUrl;
	private String mPromoSmallContent; 
	private List<PromoBoxLayoutVO> mPromoTierLayout1;
	private List<PromoBoxLayoutVO> mPromoTierLayout2;
	private List<ProductVO> mTopRegistryItem;
	private List<ProductVO> mKeepsakeShop;
	private List<BrandVO> mBrands;
	private List<PromoBoxVO> mHeroImages;
	private Boolean mJustForYouFlag;
	private Boolean mClearanceDealsFlag;
	private Boolean mTopRegistryItemsFlag;
	private Boolean mAlsoCheckOutFlag;
	private Boolean mTopCollegeItemsFlag;
	private PromoBoxVO mCollegePromoBox1;
	private PromoBoxVO mCollegePromoBox2;
	private PromoBoxVO mCategoryPromoWidget;
	private List<PromoBoxVO> mCircularListings;
	private CategoryVO mCategory;
	private int mL2CategoryCount;
	private int mL3CategoryCount;
	private List<String> mCatPopSearch;
	private PromoBoxContentVO mPromoBoxContentVO;

	
	//R2.1 implementation for SEO Static Text on Category Landing Page Start
	private String seoStaticText;
	/**
	 * @return the seoStaticText
	 */
	public String getSeoStaticText() {
		return seoStaticText;
	}

	/**
	 * @param seoStaticText the seoStaticText to set
	 */
	public void setSeoStaticText(String seoStaticText) {
		this.seoStaticText = seoStaticText;
	}
	//R2.1 implementation for SEO Static Text on Category Landing Page End

	/**
	 * @param pPageName
	 * @param pPageTitle
	 * @param pBridalTool
	 * @param pRegistryCategoryImage
	 * @param pMarketingBannerUrl
	 * @param pPromoSmallContent
	 * @param pPromoTierLayout1
	 * @param pPromoTierLayout2
	 * @param pTopRegistryItem
	 * @param pKeepsakeShop
	 * @param pCollegeDeals
	 * @param pBrands
	 * @param pHeroImage
	 */
	public LandingTemplateVO(String pPageName, String pPageTitle, List<PromoImageVO> pBridalTool,
			List<PromoImageVO> pRegistryCategoryImage, String pMarketingBannerUrl, String pPromoSmallContent,
			List<PromoBoxLayoutVO> pPromoTierLayout1, List<PromoBoxLayoutVO> pPromoTierLayout2,
			List<ProductVO> pTopRegistryItem, List<ProductVO> pKeepsakeShop,
			List<BrandVO> pBrands, List<PromoBoxVO> pHeroImages, List<PromoBoxVO> pCircularListings, List<String> pCatPopSearch) {
		super();
		mPageName = pPageName;
		mPageTitle = pPageTitle;
		mBridalTool = pBridalTool;
		mRegistryCategoryImage = pRegistryCategoryImage;
		mMarketingBannerUrl = pMarketingBannerUrl;
		mPromoSmallContent = pPromoSmallContent;
		mPromoTierLayout1 = pPromoTierLayout1;
		mPromoTierLayout2 = pPromoTierLayout2;
		mTopRegistryItem = pTopRegistryItem;
		mKeepsakeShop = pKeepsakeShop;
		mBrands = pBrands;
		mHeroImages = pHeroImages;
		mCircularListings = pCircularListings;
		mCatPopSearch = pCatPopSearch;
	}
	
	public int getL2CategoryCount() {
		return mL2CategoryCount;
	}

	public void setL2CategoryCount(int pL2CategoryCount) {
		this.mL2CategoryCount = pL2CategoryCount;
	}

	public int getL3CategoryCount() {
		return mL3CategoryCount;
	}

	public void setL3CategoryCount(int pL3CategoryCount) {
		this.mL3CategoryCount = pL3CategoryCount;
	}

	public CategoryVO getCategory() {
		return mCategory;
	}

	public void setCategory(CategoryVO pCategory) {
		this.mCategory = pCategory;
	}
	
	public List<PromoBoxVO> getCircularListings() {
		return mCircularListings;
	}

	public void setCircularListing(List<PromoBoxVO> pCircularListings) {
		this.mCircularListings = pCircularListings;
	}
	
	public PromoBoxVO getCollegePromoBox1() {
		return mCollegePromoBox1;
	}
	
	public void setCollegePromoBox1(PromoBoxVO pCollegePromoBox1) {
		this.mCollegePromoBox1 = pCollegePromoBox1;
	}
	
	public PromoBoxVO getCollegePromoBox2() {
		return mCollegePromoBox2;
	}
	
	public void setCollegePromoBox2(PromoBoxVO pCollegePromoBox2) {
		this.mCollegePromoBox2 = pCollegePromoBox2;
	}
	
	public PromoBoxVO getCategoryPromoWidget() {
		return mCategoryPromoWidget;
	}

	public void setCategoryPromoWidget(PromoBoxVO pCategoryPromoWidget) {
		this.mCategoryPromoWidget = pCategoryPromoWidget;
	}
	
	public PromoBoxContentVO getPromoBoxContentVO() {
		return mPromoBoxContentVO;
	}
	
	public void setPromoBoxContentVO(PromoBoxContentVO pPromoBoxContentVO) {
		this.mPromoBoxContentVO = pPromoBoxContentVO;
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
	 * @return the pageName
	 */
	public String getPageName() {
		return mPageName;
	}
	/**
	 * @param pPageName the pageName to set
	 */
	public void setPageName(String pPageName) {
		mPageName = pPageName;
	}
	/**
	 * @return the pageTitle
	 */
	public String getPageTitle() {
		return mPageTitle;
	}
	/**
	 * @param pPageTitle the pageTitle to set
	 */
	public void setPageTitle(String pPageTitle) {
		mPageTitle = pPageTitle;
	}
	/**
	 * @return the bridalTool
	 */
	public List<PromoImageVO> getBridalTool() {
		return mBridalTool;
	}
	/**
	 * @param pBridalTool the bridalTool to set
	 */
	public void setBridalTool(List<PromoImageVO> pBridalTool) {
		mBridalTool = pBridalTool;
	}
	/**
	 * @return the registryCategoryImage
	 */
	public List<PromoImageVO> getRegistryCategoryImage() {
		return mRegistryCategoryImage;
	}
	/**
	 * @param pRegistryCategoryImage the registryCategoryImage to set
	 */
	public void setRegistryCategoryImage(List<PromoImageVO> pRegistryCategoryImage) {
		mRegistryCategoryImage = pRegistryCategoryImage;
	}
	/**
	 * @return the marketingBannerUrl
	 */
	public String getMarketingBannerUrl() {
		return mMarketingBannerUrl;
	}
	/**
	 * @param pMarketingBannerUrl the marketingBannerUrl to set
	 */
	public void setMarketingBannerUrl(String pMarketingBannerUrl) {
		mMarketingBannerUrl = pMarketingBannerUrl;
	}
	/**
	 * @return the promoSmallContent
	 */
	public String getPromoSmallContent() {
		return mPromoSmallContent;
	}
	/**
	 * @param pPromoSmallContent the promoSmallContent to set
	 */
	public void setPromoSmallContent(String pPromoSmallContent) {
		mPromoSmallContent = pPromoSmallContent;
	}

	/**
	 * @return the topRegistryItem
	 */
	public List<ProductVO> getTopRegistryItem() {
		return mTopRegistryItem;
	}
	/**
	 * @param pTopRegistryItem the topRegistryItem to set
	 */
	public void setTopRegistryItem(List<ProductVO> pTopRegistryItem) {
		mTopRegistryItem = pTopRegistryItem;
	}
	/**
	 * @return the keepsakeShop
	 */
	public List<ProductVO> getKeepsakeShop() {
		return mKeepsakeShop;
	}
	/**
	 * @param pKeepsakeShop the keepsakeShop to set
	 */
	public void setKeepsakeShop(List<ProductVO> pKeepsakeShop) {
		mKeepsakeShop = pKeepsakeShop;
	}
	/**
	 * @return the Popular Search Terms
	 */
	public List<String> getCatPopSearch() {
		return mCatPopSearch;
	}
	/**
	 * @param pCatPopSearch the Popular Search Terms to set
	 */
	public void setCatPopSearch(List<String> pCatPopSearch) {
		mCatPopSearch = pCatPopSearch;
	}
	/**
	 * @return the HeroImages
	 */
	public List<PromoBoxVO> getHeroImages() {
		return mHeroImages;
	}
	/**
	 * @param pHeroImages the HeroImages to set
	 */
	public void setHeroImages(List<PromoBoxVO> pHeroImages) {
		mHeroImages = pHeroImages;
	}


	/**
	 * 
	 */
	public LandingTemplateVO() {
		super();
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

	public Boolean getTopRegistryItemsFlag() {
		return mTopRegistryItemsFlag;
	}
	public void setTopRegistryItemsFlag(Boolean pTopRegistryItemsFlag) {
		this.mTopRegistryItemsFlag = pTopRegistryItemsFlag;
	}

	public Boolean getAlsoCheckOutFlag() {
		return mAlsoCheckOutFlag;
	}
	public void setAlsoCheckOutFlag(Boolean pAlsoCheckOutFlag) {
		this.mAlsoCheckOutFlag = pAlsoCheckOutFlag;
	}

	public Boolean getTopCollegeItemsFlag() {
		return mTopCollegeItemsFlag;
	}
	public void setTopCollegeItemsFlag(Boolean pTopCollegeItemsFlag) {
		this.mTopCollegeItemsFlag = pTopCollegeItemsFlag;
	}
	/**
	 * @return the brands
	 */
	public List<BrandVO> getBrands() {
		return mBrands;
	}
	/**
	 * @param pBrands the brands to set
	 */
	public void setBrands(List<BrandVO> pBrands) {
		mBrands = pBrands;
	}

	public String toString(){
		StringBuffer toString=new StringBuffer(" Landing Template VO for Page name ");
		toString.append(mPageName).append(" and Page Title ").append(mPageTitle).append("\n");
		if(mBridalTool!=null && !mBridalTool.isEmpty()){
			int i=0;
			toString.append("*******************Bridal Tool Details*********************** \n");
			for(PromoImageVO bridalTool:this.getBridalTool()){
				toString.append(++i+") ").append(bridalTool.toString());
			}
		}
		else{
			toString.append("Bridal Tool Promo Images are not available for the Landing Template").append("\n");
		}

		if(mRegistryCategoryImage!=null && !mRegistryCategoryImage.isEmpty()){
			int i=0;
			toString.append("*******************Registry Category Image Details*********************** \n");
			for(PromoImageVO regCatImage:this.getRegistryCategoryImage()){
				toString.append(++i+") ").append(regCatImage.toString());
			}
		}
		else{
			toString.append("Registry Category Images are not available for the Landing Template").append("\n");
		}

		toString.append("Marketing Banner URL").append(StringUtils.isEmpty(mMarketingBannerUrl)?"NULL":mMarketingBannerUrl).append("\n")
		.append(" Promo Small Content ").append(StringUtils.isEmpty(mPromoSmallContent)?"NULL":mPromoSmallContent).append("\n");
		if(mPromoTierLayout1!=null && !mPromoTierLayout1.isEmpty()){
			toString.append(" Promo Box Layout 1 Details  for the Landing Template \n");
			int i=0;
			for(PromoBoxLayoutVO PromoBoxLayoutVO:mPromoTierLayout1){
				toString.append(++i+") ").append(PromoBoxLayoutVO.toString());
			}
		}
		else{
			toString.append(" Promo Box Layout 1 is null for the Landing Template");
		}
		if(mPromoTierLayout2!=null && !mPromoTierLayout2.isEmpty()){
			toString.append(" Promo Box Layout 2 Details  for the Landing Template \n");
			int i=0;
			for(PromoBoxLayoutVO PromoBoxLayoutVO:mPromoTierLayout2){
				toString.append(++i+") ").append(PromoBoxLayoutVO.toString());
			}
		}
		else{
			toString.append(" Promo Box Layout 2 is null for the Landing Template");
		}
		if(mHeroImages!=null && !mHeroImages.isEmpty()){
			int i=0;
			for(PromoBoxVO heroImage:mHeroImages){
				toString.append(++i+") ").append(heroImage.toString());
			}
		}
		else{
			toString.append("Hero Images are not available for the Landing Template").append("\n");
		}
		toString.append(" First promo Box Details for the Landing Template \n ");

		if(mCircularListings!=null && !mCircularListings.isEmpty()){
			int i=0;
			for(PromoBoxVO circularListings:mCircularListings){
				toString.append(++i+") ").append(circularListings.toString());
			}
		}
		else{
			toString.append("CircularListings are not available for the Landing Template").append("\n");
		}
		toString.append(" First promo Box Details for the Circular Listing Landing Template \n ");
		
		

		if(mTopRegistryItem!=null && !mTopRegistryItem.isEmpty()){
			int i=0;
			toString.append("Top Registry Item Details for the Landing Template");
			for(ProductVO productVO:mTopRegistryItem){
				toString.append(++i+") ").append(productVO.toString());
			}
		}
		else{
			toString.append("Top Registry Item is null for the Landing Template");
		}
		if(mKeepsakeShop!=null && !mKeepsakeShop.isEmpty()){
			int i=0;
			toString.append("Keep Sake Shop Details for the Landing Template");
			for(ProductVO productVO:mKeepsakeShop){
				toString.append(++i+") ").append(productVO.toString());
			}
		}
		else{
			toString.append("Keep Sake Shop is null for the Landing Template");
		}
		if(mBrands!=null && !mBrands.isEmpty()){
			int i=0;
			toString.append("*******************Brands Details*********************** \n");
			for(BrandVO brands:this.mBrands){
				toString.append(++i+") ").append(brands.toString());
			}
		}
		else{
			toString.append("Brands Details are not available for the Landing Template").append("\n");
		}
		if(mCatPopSearch!=null && !mCatPopSearch.isEmpty()){
			int i=0;
			toString.append("Popular Search Terms for the Landing Template");
			for(String term:mCatPopSearch){
				toString.append(++i+") ").append(term);
			}
		}
		else{
			toString.append("Popular Search Terms is null for the Landing Template");
		}
		toString.append(" Just For You Flag value ").append(mJustForYouFlag!=null?mJustForYouFlag:"NULL").append("\n")
		.append(" Clearance Deals Flag value ").append(mClearanceDealsFlag!=null?mClearanceDealsFlag:"NULL").append("\n")
		.append(" Top Registry Items Flag value ").append(mTopRegistryItemsFlag!=null?mTopRegistryItemsFlag:"NULL").append("\n")
		.append(" Also Check Out Flag value ").append(mAlsoCheckOutFlag!=null?mAlsoCheckOutFlag:"NULL").append("\n")
		.append(" Top College Items Flag value ").append(mTopCollegeItemsFlag!=null?mTopCollegeItemsFlag:"NULL").append("\n");
		return toString.toString();
	}



}
