package com.bbb.search.bean.result;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;

import atg.multisite.SiteContextManager;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.AttributeVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.utils.BBBUtility;


public class BBBProduct implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private static final String NOT_YET_RATED = "Not yet rated";
	private static final String MAX_LIMIT = " out of 5 stars";
	private String productID;
	private String productName;
	private String description;
	private String swatchFlag;
	private String imageURL;
	private String verticalImageURL;
	private String highPrice;
	private String lowPrice;
	private String wasHighPrice;
	private String wasLowPrice;
	private String highPriceMX;
	private String lowPriceMX;
	private String wasHighPriceMX;
	private String wasLowPriceMX;
	private String hyperlink;
	private String ratings;
	private String reviews;
	private String ratingsTitle;
	private Map<Integer,String> attribute;
	private String priceRange;
	private String priceRangeMX;
	private String category;
	private Map<String,SkuVO> skuSet;
	private String videoId;
	private String guideId;
	private String guideTitle;
	private String guideImage;
	private String guideAltText;
	private String guideShortDesc;
	private String othResCategory;
	private String othResTitle;
	private String othResLink;
	private String seoUrl;
	private Map<String,SkuVO> colorSet;
	private String onSale;
	private String otherPageType;
	private String sceneSevenURL;
	private List<String> categoryList;
	private String priceRangeToken;
	private boolean intlRestricted;
	private boolean shipMsgFlag;
	private String displayShipMsg;
	
	//added for BBB AJAX 2.3.1  Was-Is price change on PLP
	private String wasPriceRange;
	private String wasPriceRangeMX;
	// added for R2 - item 141
	private boolean rollupFlag;
	private String collectionFlag; 
	private boolean inCompareDrawer;
	private boolean pennyPriceMX;
	private boolean pennyPrice;
	private boolean personalized;
	private String productImageUrlForGrid3x3;
	private String productImageUrlForGrid3x3_newPlp;
	private String productImageUrlForGrid4;
	private Integer ratingForCSS;
	private BBBDynamicPriceVO dynamicPriceVO;
	private boolean inCartFlag;
	private String priceLabelCode;
	private BBBCatalogTools bbbCatalogTools;
	private List<String> hideAttributeList;
	private boolean vdcFlag;
	private boolean mswpFlag;
	
	public boolean isMswpFlag() {
		return mswpFlag;
	}

	public void setMswpFlag(boolean mswpFlag) {
		this.mswpFlag = mswpFlag;
	}

	public boolean isVdcFlag() {
		return vdcFlag;
	}

	public void setVdcFlag(boolean vdcFlag) {
		this.vdcFlag = vdcFlag;
	}

	public List<String> getHideAttributeList() {
		return hideAttributeList;
	}
	
	public void setHideAttributeList(
			List<String> hideAttributeList) {
		this.hideAttributeList = hideAttributeList;
	}
	
	public void setDynamicPriceVO(BBBDynamicPriceVO pBBBDynamicPriceVO) {
		dynamicPriceVO = pBBBDynamicPriceVO;
	}
	
	public BBBDynamicPriceVO getDynamicPriceVO() {
		if(dynamicPriceVO==null){
			dynamicPriceVO = new BBBDynamicPriceVO();
			// Check if the product is eligible and present in Dynamic Price Repository
			//Also populate the DynamicPriceVO
			BBBUtility.populateDynamicPricingVO(dynamicPriceVO,
					getProductID()); 
			
		} else{
			BBBUtility.evaluateDynamicPriceEligiblity(dynamicPriceVO);
		}
		return dynamicPriceVO;
	}
	public boolean isInCartFlag() {
		return inCartFlag;
	}
	public void setInCartFlag(boolean inCartFlag) {
		this.inCartFlag = inCartFlag;
	}
	public String getPriceLabelCode() {
		return priceLabelCode;
	}
	public void setPriceLabelCode(String priceLabelCode) {
		this.priceLabelCode = priceLabelCode;
	}
	public String getDisplayShipMsg() {
		return displayShipMsg;
	}
	public void setDisplayShipMsg(String displayShipMsg) {
		this.displayShipMsg = displayShipMsg;
	}
	public boolean isShipMsgFlag() {
		return shipMsgFlag;
	}
	public void setShipMsgFlag(boolean shipMsgFlag) {
		this.shipMsgFlag = shipMsgFlag;
	}
	public String getPriceRangeToken() {
		return priceRangeToken;
	}
	public void setPriceRangeToken(String priceRangeToken) {
		this.priceRangeToken = priceRangeToken;
	}
	/**
	 * @return the inventoryLevel
	 */

	public boolean isInCompareDrawer() {
		return inCompareDrawer;
	}
	public void setInCompareDrawer(boolean inCompareDrawer) {
		this.inCompareDrawer = inCompareDrawer;
	}
	/**
	 * @return the onSale
	 */
	public String getOnSale() {
		return onSale;
	}
	public String getCollectionFlag() {
		return collectionFlag;
	}

	/**
	 * @param onSale the onSale to set
	 */
	public void setOnSale(String onSale) {
		this.onSale = onSale;
	}

	public void setCollectionFlag(String collectionFlag) {
		this.collectionFlag = collectionFlag;
	}

	public boolean isRollupFlag() {
		return rollupFlag;
	}

	public void setRollupFlag(boolean rollupFlag) {
		this.rollupFlag = rollupFlag;
	}

	public Map<String, SkuVO> getSkuSet() {
		return skuSet;
	}

	public void setSkuSet(Map<String, SkuVO> skuSet) {
		this.skuSet = skuSet;
	}
	public String getCategory() {
		return category;
	}

	public void setCategory(final String category) {
		this.category = category;
	}

	public String getProductID() {
		return productID;
	}

	public void setProductID(final String productID) {
		this.productID = productID;
	}

	public String getProductName() {
		return productName;
	}

	public String getRatings() {
		return ratings;
	}

	public void setRatings(String ratings) {
		this.ratings = ratings;
	}
	public String getRatingsTitle() {
		float f = 0.0f;
		if(null!=getRatings()  && !getRatings().isEmpty()  && !getRatings().trim().equals("")){
			f=Float.parseFloat(getRatings());
		}
		if (f<=0) {
			ratingsTitle = NOT_YET_RATED;
		} else {
            int remainder = ((int) (f*BBBCoreConstants.TEN))%BBBCoreConstants.TEN;
            int rating;
			if (remainder == 0) {
				rating = (((int) (f * BBBCoreConstants.TEN)) / BBBCoreConstants.TEN);
				ratingsTitle = rating + MAX_LIMIT;
			} else {
				ratingsTitle = f + MAX_LIMIT;
			}
		}
		return ratingsTitle;
	}

	public void setRatingsTitle(String ratingsTitle) {
		this.ratingsTitle = ratingsTitle;
	}

	public void setProductName(final String productName) {
		this.productName = productName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getHyperlink() {
		return hyperlink;
	}

	public void setHyperlink(final String hyperlink) {
		this.hyperlink = hyperlink;
	}

	/**
	 * @return the swatch
	 */
	public String getSwatchFlag() {
		return swatchFlag;
	}

	/**
	 * @param pSwatch the swatch to set
	 */
	public void setSwatchFlag(final String pSwatchFlag) {
		swatchFlag = pSwatchFlag;
	}

	/**
	 * @return the imageURL
	 */
	public String getImageURL() {
		return imageURL;
	}

	/**
	 * @param pImageURL the imageURL to set
	 */
	public void setImageURL(final String pImageURL) {
		imageURL = pImageURL;
	}

	/**
	 * @return the highPrice
	 */
	public String getHighPrice() {
		String price = null;
			price = highPrice;
		return price;
	}

	/**
	 * @param pHighPrice the highPrice to set
	 */
	public void setHighPrice(final String pHighPrice) {
		highPrice = pHighPrice;
	}

	/**
	 * @return the lowPrice
	 */
	public String getLowPrice() {
		return lowPrice;
	}

	/**
	 * @param pLowPrice the lowPrice to set
	 */
	public void setLowPrice(final String pLowPrice) {
		lowPrice = pLowPrice;
	}

	/**
	 * @return the reviews
	 */
	public String getReviews() {
		return reviews;
	}

	/**
	 * @param pReviews the reviews to set
	 */
	public void setReviews(final String pReviews) {
		reviews = pReviews;
	}

	/**
	 * @return the priceRange
	 */
	public String getPriceRange() {
		String oldPrice=this.priceRange,pricerange=this.priceRangeToken;
		String highPrice=this.getHighPrice(),lowPrice=this.getLowPrice();
		BBBDynamicPriceVO dynamicPriceVO = this.getDynamicPriceVO();
		
		if (dynamicPriceVO.isDynamicProdEligible()) {

			String listPriceString = null;
			String salePriceString = null;
			if (dynamicPriceVO.getSiteId().equals(BBBCoreConstants.SITE_BAB_CA)|| 
					dynamicPriceVO.getSiteId().equals(BBBCoreConstants.SITEBAB_CA_TBS)) {
				listPriceString = dynamicPriceVO.getCaListPriceString();
				salePriceString = dynamicPriceVO.getCaSalePriceString();

			} else if (dynamicPriceVO.getSiteId().equals(BBBCoreConstants.SITE_BBB)
					|| dynamicPriceVO.getSiteId().equals(BBBCoreConstants.SITEBAB_BABY_TBS)) {
				listPriceString = dynamicPriceVO.getBabyListPriceString();
				salePriceString = dynamicPriceVO.getBabySalePriceString();
			}
			else {
				listPriceString = dynamicPriceVO.getBbbListPriceString();
				salePriceString = dynamicPriceVO.getBbbSalePriceString();

			}

			if (salePriceString == null) {
				oldPrice = listPriceString;
			} else {
				oldPrice = salePriceString;
			}

		}
		
		else{
		if (!BBBUtility.isEmpty(oldPrice) && !BBBUtility.isEmpty(pricerange) && !BBBUtility.isEmpty(lowPrice)) {
			Properties prop = new Properties();
			oldPrice=BBBUtility.convertToInternationalPriceByToken(pricerange,lowPrice,highPrice,prop);
		}
		}
		return oldPrice;
	}

	/**
	 * @param pPriceRange the priceRange to set
	 */
	public void setPriceRange(final String pPriceRange) {
		priceRange = pPriceRange;
	}
	public void setPennyPrice(final boolean pPennyPrice) {
		pennyPrice = pPennyPrice;
	}

	public boolean isPennyPrice() {
		boolean pennyPrice = false;
		String oldPrice = this.priceRange, pricerange = this.priceRangeToken;
		String highPrice = this.getHighPrice(), lowPrice = this.getLowPrice();
		if (!BBBUtility.isEmpty(oldPrice) && !BBBUtility.isEmpty(pricerange)
				&& !BBBUtility.isEmpty(lowPrice)) {
			Properties prop = new Properties();
			pennyPrice = BBBUtility.checkPennyPrice(pricerange, lowPrice,
					highPrice, prop);
		}
		return pennyPrice;
	}
	/**
	 * @return the videoId
	 */
	public String getVideoId() {
		return videoId;
	}

	/**
	 * @param videoId the videoId to set
	 */
	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	/**
	 * @return the guideId
	 */
	public String getGuideId() {
		return guideId;
	}

	/**
	 * @param guideId the guideId to set
	 */
	public void setGuideId(String guideId) {
		this.guideId = guideId;
	}

	/**
	 * @return the guideTitle
	 */
	public String getGuideTitle() {
		return guideTitle;
	}

	/**
	 * @param guideTitle the guideTitle to set
	 */
	public void setGuideTitle(String guideTitle) {
		this.guideTitle = guideTitle;
	}

	/**
	 * @return the guideImage
	 */
	public String getGuideImage() {
		return guideImage;
	}

	/**
	 * @param guideImage the guideImage to set
	 */
	public void setGuideImage(String guideImage) {
		this.guideImage = guideImage;
	}

	/**
	 * @return the guideAltText
	 */
	public String getGuideAltText() {
		return guideAltText;
	}

	/**
	 * @param guideAltText the guideAltText to set
	 */
	public void setGuideAltText(String guideAltText) {
		this.guideAltText = guideAltText;
	}

	/**
	 * @return the guideShortDesc
	 */
	public String getGuideShortDesc() {
		return guideShortDesc;
	}

	/**
	 * @param guideShortDesc the guideShortDesc to set
	 */
	public void setGuideShortDesc(String guideShortDesc) {
		this.guideShortDesc = guideShortDesc;
	}

	/**
	 * @return the othResCategory
	 */
	public String getOthResCategory() {
		return othResCategory;
	}

	/**
	 * @param othResCategory the othResCategory to set
	 */
	public void setOthResCategory(String othResCategory) {
		this.othResCategory = othResCategory;
	}

	/**
	 * @return the othResTitle
	 */
	public String getOthResTitle() {
		return othResTitle;
	}

	/**
	 * @param othResTitle the othResTitle to set
	 */
	public void setOthResTitle(String othResTitle) {
		this.othResTitle = othResTitle;
	}

	/**
	 * @return the othResLink
	 */
	public String getOthResLink() {
		return othResLink;
	}

	/**
	 * @param othResLink the othResLink to set
	 */
	public void setOthResLink(String othResLink) {
		this.othResLink = othResLink;
	}

	/**
	 * @return the seoUrl
	 */
	public String getSeoUrl() {
		return seoUrl;
	}

	/**
	 * @param seoUrl the seoUrl to set
	 */
	public void setSeoUrl(final String seoUrl) {
		this.seoUrl = seoUrl;
	}

	/**
	 * @return the attribute
	 */
	public Map<Integer, String> getAttribute() {
		return attribute;
	}

	/**
	 * Only Sorted Map should be set in attribute.
	 * Changed from SortedMap to Map as part of BBBI-2486
	 * @param attribute the attribute to set
	 */
	public void setAttribute(Map<Integer, String> attribute) {
		this.attribute = attribute;
	}

	public String getVerticalImageURL() {
		return verticalImageURL;
	}

	public void setVerticalImageURL(String verticalImageURL) {
		this.verticalImageURL = verticalImageURL;
	}

	/**
	 * @return the colorSet
	 */
	public Map<String, SkuVO> getColorSet() {
		return colorSet;
	}

	/**
	 * @param colorSet the colorSet to set
	 */
	public void setColorSet(Map<String, SkuVO> colorSet) {
		this.colorSet = colorSet;
	}
	/**
	 * @param othRPageType the othResLink to set
	 */
	public void setOtherPageType(final String pOtherPageType) {
		this.otherPageType = pOtherPageType;
	}

	/**
	 * @return the otherPageType
	 */
	public String getOtherPageType() {
		return this.otherPageType;
	}
	/**
	 * @return the wasPriceRange
	 */
	public String getWasPriceRange() {
		
		String oldPrice=this.wasPriceRange,pricerange=this.priceRangeToken;
		String lowPrice = this.getWasLowPrice(), highPrice =this.getWasHighPrice();
		BBBDynamicPriceVO dynamicPriceVO = this.getDynamicPriceVO();
			
		if (dynamicPriceVO.isDynamicProdEligible()) {
			String listPriceString = null;
			String salePriceString = null;

			if (dynamicPriceVO.getSiteId().equals(BBBCoreConstants.SITE_BAB_CA) || dynamicPriceVO.getSiteId().equals(BBBCoreConstants.SITEBAB_CA_TBS)) {
				listPriceString = dynamicPriceVO.getCaListPriceString();
				salePriceString = dynamicPriceVO.getCaSalePriceString();
			} else if (dynamicPriceVO.getSiteId().equals(BBBCoreConstants.SITE_BBB) || dynamicPriceVO.getSiteId().equals(BBBCoreConstants.SITEBAB_BABY_TBS)) {
				listPriceString = dynamicPriceVO.getBabyListPriceString();
				salePriceString = dynamicPriceVO.getBabySalePriceString();
			} else {
				listPriceString = dynamicPriceVO.getBbbListPriceString();
				salePriceString = dynamicPriceVO.getBbbSalePriceString();
			}
			if(null != salePriceString) {
				return listPriceString;
			} else  {
				return salePriceString;
			}
		}
		else{
		if (!BBBUtility.isEmpty(oldPrice) && !BBBUtility.isEmpty(pricerange) && !BBBUtility.isEmpty(lowPrice)) {
			Properties prop = new Properties();
			oldPrice=BBBUtility.convertToInternationalPriceByToken(pricerange,lowPrice,highPrice,prop);
		}
		}
		return oldPrice;
	
	}
	/**
	 * @param wasPriceRange the wasPriceRange to set
	 */
	public void setWasPriceRange(String wasPriceRange) {
		this.wasPriceRange = wasPriceRange;
	}
	/**
	 * @return the sceneSevenURL
	 */
	public String getSceneSevenURL() {
		return sceneSevenURL;
	}
	/**
	 * @param sceneSevenURL the sceneSevenURL to set
	 */
	public void setSceneSevenURL(String pSceneSevenURL) {
		this.sceneSevenURL = pSceneSevenURL;
	}
	/**
	 * @return the categoryList
	 */
	public final List<String> getCategoryList() {
		return this.categoryList;
	}
	/**
	 * @param categoryList the categoryList to set
	 */
	public final void setCategoryList(final List<String> categoryList) {
		this.categoryList = categoryList;
	}
	
	public String getHighPriceMX() {
		return highPriceMX;
	}
	public void setHighPriceMX(String highPriceMX) {
		this.highPriceMX = highPriceMX;
	}
	public String getLowPriceMX() {
		return lowPriceMX;
	}
	public void setLowPriceMX(String lowPriceMX) {
		this.lowPriceMX = lowPriceMX;
	}

	public String getPriceRangeMX() {
		String oldPrice = this.priceRangeMX, pricerange = this.priceRangeToken;
		String highPrice = this.getHighPriceMX(), lowPrice = this
				.getLowPriceMX();
		BBBDynamicPriceVO dynamicPriceVO = this.getDynamicPriceVO();
		
		if (dynamicPriceVO.isDynamicProdEligible()) {
			String listPriceString = null;
			String salePriceString = null;

			listPriceString = dynamicPriceVO.getMxListPriceString();
			salePriceString = dynamicPriceVO.getMxSalePriceString();

			if (salePriceString == null) {
				oldPrice = listPriceString;
			} else {
				oldPrice = salePriceString;
				}
		}
		 else {
			if (!BBBUtility.isEmpty(oldPrice)
					&& !BBBUtility.isEmpty(pricerange)
					&& !BBBUtility.isEmpty(lowPrice)) {
				Properties prop = new Properties();
				oldPrice = BBBUtility.convertToInternationalPriceByToken(
						pricerange, lowPrice, highPrice, prop);
			}
		}
		return oldPrice;
	}
	public void setPriceRangeMX(String priceRangeMX) {
		this.priceRangeMX = priceRangeMX;
	}
	
	public void setPennyPriceMX(final boolean pPennyPriceMX) {
		pennyPriceMX = pPennyPriceMX;
	}
	public boolean isPennyPriceMX() {
		boolean pennyPriceMX = false;
		String oldPrice = this.priceRangeMX, pricerange = this.priceRangeToken;
		String highPrice = this.getHighPriceMX(), lowPrice = this
				.getLowPriceMX();
		if (!BBBUtility.isEmpty(oldPrice) && !BBBUtility.isEmpty(pricerange)
				&& !BBBUtility.isEmpty(lowPrice)) {
			Properties prop = new Properties();
			pennyPriceMX = BBBUtility.checkPennyPrice(pricerange, lowPrice,
					highPrice, prop);
		}
		return pennyPriceMX;
	}

	public String getWasPriceRangeMX() {
		String oldPrice = this.wasPriceRangeMX, priceRange = this.priceRangeToken;
		String highPrice = this.getWasHighPriceMX(), lowPrice = this
				.getWasLowPriceMX();
		BBBDynamicPriceVO dynamicPriceVO = this.getDynamicPriceVO();
		
		if (dynamicPriceVO.isDynamicProdEligible()) {
			String listPrice = null;
			String salePrice = null;
			listPrice = dynamicPriceVO.getMxListPriceString();
			salePrice = dynamicPriceVO.getMxSalePriceString();
			if(null != salePrice) {
				return listPrice;
			}
			return salePrice;

		} else {

			if (!BBBUtility.isEmpty(oldPrice)
					&& !BBBUtility.isEmpty(priceRange)
					&& !BBBUtility.isEmpty(lowPrice)) {
				Properties prop = new Properties();
				oldPrice = BBBUtility.convertToInternationalPriceByToken(
						priceRange, lowPrice, highPrice, prop);
			}
		}
		return oldPrice;
	}
	
	public void setWasPriceRangeMX(String wasPriceRangeMX) {
		this.wasPriceRangeMX = wasPriceRangeMX;
	}
	private List<AttributeVO> attributeVO;

	public List<AttributeVO> getAttributeVO() {
		return attributeVO;
	}
	public void setAttributeVO(List<AttributeVO> attributeVO) {
		this.attributeVO = attributeVO;
	}
	public String getWasHighPrice() {
		return wasHighPrice;
	}
	public void setWasHighPrice(String wasHighPrice) {
		this.wasHighPrice = wasHighPrice;
	}
	public String getWasLowPrice() {
		return wasLowPrice;
	}
	public void setWasLowPrice(String wasLowPrice) {
		this.wasLowPrice = wasLowPrice;
	}
	
		public boolean isIntlRestricted() {
		return intlRestricted;
	}
	public void setIntlRestricted(boolean intlRestricted) {
		this.intlRestricted = intlRestricted;
	}
	/**
	 * @return the personalized
	 */
	public boolean isPersonalized() {
		return personalized;
	}
	/**
	 * @param personalized the personalized to set
	 */
	public void setPersonalized(boolean personalized) {
		this.personalized = personalized;
	}
	
	public String getWasHighPriceMX() {
	    return wasHighPriceMX;
	  }
	 
	public void setWasHighPriceMX(String wasHighPriceMX) {
	    this.wasHighPriceMX = wasHighPriceMX;
	  }
	  
	public String getWasLowPriceMX() {
	    return wasLowPriceMX;
	  }
	  
	public void setWasLowPriceMX(String wasLowPriceMX) {
	    this.wasLowPriceMX = wasLowPriceMX;
	 }
	/**
	 * @return the productImageUrlForGrid3x3
	 */
	public String getProductImageUrlForGrid3x3() {
		return productImageUrlForGrid3x3;
	}
	/**
	 * @param productImageUrlForGrid3x3 the productImageUrlForGrid3x3 to set
	 */
	public void setProductImageUrlForGrid3x3(String productImageUrlForGrid3x3) {
		this.productImageUrlForGrid3x3 = productImageUrlForGrid3x3;
	}
	/**
	 * @return the productImageUrlForGrid4
	 */
	public String getProductImageUrlForGrid4() {
		return productImageUrlForGrid4;
	}
	/**
	 * @param productImageUrlForGrid4 the productImageUrlForGrid4 to set
	 */
	public void setProductImageUrlForGrid4(String productImageUrlForGrid4) {
		this.productImageUrlForGrid4 = productImageUrlForGrid4;
	}
	public Integer getRatingForCSS() {
		return ratingForCSS;
	}
	public void setRatingForCSS(Integer rating) {
		this.ratingForCSS = rating;
	}
	public BBBCatalogTools getBbbCatalogTools() {
		return bbbCatalogTools;
	}
	public void setBbbCatalogTools(BBBCatalogTools bbbCatalogTools) {
		this.bbbCatalogTools = bbbCatalogTools;
	}

	public String getProductImageUrlForGrid3x3_newPlp() {
		return productImageUrlForGrid3x3_newPlp;
	}

	public void setProductImageUrlForGrid3x3_newPlp(String productImageUrlForGrid3x3_newPlp) {
		this.productImageUrlForGrid3x3_newPlp = productImageUrlForGrid3x3_newPlp;
	}
	

}
