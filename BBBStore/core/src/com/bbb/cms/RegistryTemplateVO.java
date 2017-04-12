package com.bbb.cms;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

//import com.bbb.commerce.catalog.vo.AttributeVO;
import com.bbb.commerce.catalog.vo.BrandVO;
//import com.bbb.commerce.catalog.vo.StoreBopusInfoVO;

public class RegistryTemplateVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mPageName;
	private String mBbbPageName;
	private String mPageTitle;
	private String mPageHeaderFeaturedContent;
	private String mPageHeaderCopy;
	private String mPromoImageURL;
	private String mPromoImageAltText;
	private String mPageCopy;
	private String mPageType;
	private String mPageWrapper;
	private String mPageVariation;
	private PromoBoxVO imageBox;
	private String headTagContent;
	private String bodyEndTagContent;


	private List<BrandVO> mBrands;
		private Map<String, String> omnitureData;

	public RegistryTemplateVO() {
		super();
	}

	public RegistryTemplateVO(String pPageName, String pBbbPageName, String pPageTitle, String pPageHeaderFeaturedContent, String pPageHeaderCopy, String pPromoImageURL, String pPromoImageAltText, String pPageCopy, String pPageType, String pPageWrapper, String pPageVariation, Map<String,String> omnitureData, String pHeadTagContent, String pBodyEndTagContent) 
	{
		super();
		mPageName = pPageName;
		mBbbPageName = pBbbPageName;
		mPageTitle = pPageTitle;
		mPageHeaderFeaturedContent = pPageHeaderFeaturedContent;
		mPageHeaderCopy = pPageHeaderCopy;
		mPromoImageURL = pPromoImageURL;
		mPromoImageAltText = pPromoImageAltText;
		mPageCopy = pPageCopy;
		mPageType = pPageType;
		mPageWrapper = pPageWrapper;
		mPageVariation = pPageVariation;
		setHeadTagContent(pHeadTagContent);
		setBodyEndTagContent(pBodyEndTagContent);
		
	}


	public Map<String, String> getOmnitureData() {
		return omnitureData;
	}

	public void setOmnitureData(Map<String, String> omnitureData) {
		this.omnitureData = omnitureData;
	}

	/**
	 * @return the imageBox
	 */
	 public PromoBoxVO getImageBox() {
		return imageBox;
	}

	 /**
	  * @param imageBox the imageBox to set
	  */
	 public void setImageBox(PromoBoxVO imageBox) {
		 this.imageBox = imageBox;
	 }

	 public String getPageName() {
		 return mPageName;
	 }
	 public void setPageName(String pPageName) {
		 this.mPageName = pPageName;
	 }
	 public String getBbbPageName() {
		 return mBbbPageName;
	 }
	 public void setBbbPageName(String pBbbPageName) {
		 this.mBbbPageName = pBbbPageName;
	 }
	 public String getPageTitle() {
		 return mPageTitle;
	 }
	 public void setPageTitle(String pPageTitle) {
		 this.mPageTitle = pPageTitle;
	 }
	 public String getPageHeaderFeaturedContent() {
		 return mPageHeaderFeaturedContent;
	 }
	 public void setPageHeaderFeaturedContent(String pPageHeaderFeaturedContent) {
		 this.mPageHeaderFeaturedContent = pPageHeaderFeaturedContent;
	 }
	 public String getPageHeaderCopy() {
		 return mPageHeaderCopy;
	 }
	 public void setPageHeaderCopy(String pPageHeaderCopy) {
		 this.mPageHeaderCopy = pPageHeaderCopy;
	 }
	 public String getPromoImageURL() {
		 return mPromoImageURL;
	 }
	 public void setPromoImageURL(String pPromoImageURL) {
		 this.mPromoImageURL = pPromoImageURL;
	 }
	 public String getPromoImageAltText() {
		 return mPromoImageAltText;
	 }
	 public void setPromoImageAltText(String pPromoImageAltText) {
		 this.mPromoImageAltText = pPromoImageAltText;
	 }
	 public String getPageCopy() {
		 return mPageCopy;
	 }
	 public void setPageCopy(String pPageCopy) {
		 this.mPageCopy = pPageCopy;
	 }

	 public List<BrandVO> getBrands() {
		 return mBrands;
	 }

	 public void setBrands(List<BrandVO> mBrands) {
		 this.mBrands = mBrands;
	 }

	 public String getPageType() {
		 return mPageType;
	 }

	 public void setPageType(String pPageType) {
		 this.mPageType = pPageType;
	 }

	 public String getPageWrapper() {
		 return mPageWrapper;
	 }

	 public void setPageWrapper(String pPageWrapper) {
		 this.mPageWrapper = pPageWrapper;
	 }

	 public String getPageVariation() {
		 return mPageVariation;
	 }

	 public void setPageVariation(String pPageVariation) {
		 this.mPageVariation = pPageVariation;
	 }

	/**
	 * @return the headTagContent
	 */
	public String getHeadTagContent() {
		return headTagContent;
	}

	/**
	 * @param headTagContent the headTagContent to set
	 */
	public void setHeadTagContent(String headTagContent) {
		this.headTagContent = headTagContent;
	}

	/**
	 * @return the bodyEndTagContent
	 */
	public String getBodyEndTagContent() {
		return bodyEndTagContent;
	}

	/**
	 * @param bodyEndTagContent the bodyEndTagContent to set
	 */
	public void setBodyEndTagContent(String bodyEndTagContent) {
		this.bodyEndTagContent = bodyEndTagContent;
	}




}
