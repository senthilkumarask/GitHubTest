package com.bbb.cms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StaticTemplateContentVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String templateId;
	private String pageName;
	private String pageTitle;
	private String pageSubHeaderCopy;
	private CarouselVO carouselVO;
	private String seoUrl;
	private String siteId;
	private List<ContentBoxVO> pageContent;
	private String noOfContentBoxes;
	private String pageHeaderCopy;
	private PromoBoxVO imageBox;
	private BannerVO banner;
	private List<ProductCarouselVO> productList = new ArrayList<ProductCarouselVO>();
	private String noOfProductsInProductCarousel;
	private String pageMessage;
	private String pageCopy;
	private String bbbPageName;
	private Map<String, String> omnitureData;
	private boolean ErrorExist;
	private String errorCode;
	private String errorMsg;
	private String vdcShipMessage;
	private String headTagContent;
	private String bodyEndTagContent;
	private boolean registryRibbonFlag;

	/**
	 * @return the registryRibbonFlag
	 */
	public boolean isRegistryRibbonFlag() {
		return registryRibbonFlag;
	}

	/**
	 * @param registryRibbonFlag the registryRibbonFlag to set
	 */
	public void setRegistryRibbonFlag(boolean registryRibbonFlag) {
		this.registryRibbonFlag = registryRibbonFlag;
	}

	/**
	 * @return the vdcShipMessage
	 */
	public String getVdcShipMessage() {
		return vdcShipMessage;
	}
	
	/**
	 * @param vdcShipMessage the String to set
	 */
	public void setVdcShipMessage(String vdcShipMessage) {
		this.vdcShipMessage = vdcShipMessage;
	}
	
	public Map<String, String> getOmnitureData() {
		return omnitureData;
	}
	public void setOmnitureData(Map<String, String> omnitureData) {
		this.omnitureData = omnitureData;
	}
	/**
	 * @return the bbbPageName
	 */
	public String getBbbPageName() {
		return bbbPageName;
	}
	/**
	 * @param bbbPageName the bbbPageName to set
	 */
	public void setBbbPageName(String bbbPageName) {
		this.bbbPageName = bbbPageName;
	}
	/**
	 * @return the pageCopy
	 */
	public String getPageCopy() {
		return pageCopy;
	}
	/**
	 * @param pageCopy the pageCopy to set
	 */
	public void setPageCopy(String pageCopy) {
		this.pageCopy = pageCopy;
	}
	/**
	 * @return the templateId
	 */
	public String getTemplateId() {
		return templateId;
	}
	/**
	 * @param templateId the templateId to set
	 */
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	/**
	 * @return the pageName
	 */
	public String getPageName() {
		return pageName;
	}
	/**
	 * @param pageName the pageName to set
	 */
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	/**
	 * @return the pageTitle
	 */
	public String getPageTitle() {
		return pageTitle;
	}
	/**
	 * @param pageTitle the pageTitle to set
	 */
	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}
	/**
	 * @return the pageSubHeaderCopy
	 */
	public String getPageSubHeaderCopy() {
		return pageSubHeaderCopy;
	}
	/**
	 * @param pageSubHeaderCopy the pageSubHeaderCopy to set
	 */
	public void setPageSubHeaderCopy(String pageSubHeaderCopy) {
		this.pageSubHeaderCopy = pageSubHeaderCopy;
	}
	/**
	 * @return the carouselVO
	 */
	public CarouselVO getCarouselVO() {
		return carouselVO;
	}
	/**
	 * @param carouselVO the carouselVO to set
	 */
	public void setCarouselVO(CarouselVO carouselVO) {
		this.carouselVO = carouselVO;
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
	public void setSeoUrl(String seoUrl) {
		this.seoUrl = seoUrl;
	}
	/**
	 * @return the siteId
	 */
	public String getSiteId() {
		return siteId;
	}
	/**
	 * @param siteId the siteId to set
	 */
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
	/**
	 * @return the pageContent
	 */
	public List<ContentBoxVO> getPageContent() {
		return pageContent;
	}
	/**
	 * @param pageContent the pageContent to set
	 */
	public void setPageContent(List<ContentBoxVO> pageContent) {
		this.pageContent = pageContent;
	}
	/**
	 * @return the noOfContentBoxes
	 */
	public String getNoOfContentBoxes() {
		return noOfContentBoxes;
	}
	/**
	 * @param noOfContentBoxes the noOfContentBoxes to set
	 */
	public void setNoOfContentBoxes(String noOfContentBoxes) {
		this.noOfContentBoxes = noOfContentBoxes;
	}
	/**
	 * @return the pageHeaderCopy
	 */
	public String getPageHeaderCopy() {
		return pageHeaderCopy;
	}
	/**
	 * @param pageHeaderCopy the pageHeaderCopy to set
	 */
	public void setPageHeaderCopy(String pageHeaderCopy) {
		this.pageHeaderCopy = pageHeaderCopy;
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
	/**
	 * @return the banner
	 */
	public BannerVO getBanner() {
		return banner;
	}
	/**
	 * @param banner the banner to set
	 */
	public void setBanner(BannerVO banner) {
		this.banner = banner;
	}
	/**
	 * @return the productList
	 */
	public List<ProductCarouselVO> getProductList() {
		return productList;
	}
	
	/**
	 * @return the noOfProductsInProductCarousel
	 */
	public String getNoOfProductsInProductCarousel() {
		return noOfProductsInProductCarousel;
	}
	/**
	 * @param noOfProductsInProductCarousel the noOfProductsInProductCarousel to set
	 */
	public void setNoOfProductsInProductCarousel(
			String noOfProductsInProductCarousel) {
		this.noOfProductsInProductCarousel = noOfProductsInProductCarousel;
	}
	/**
	 * @return the pageMessage
	 */
	public String getPageMessage() {
		return pageMessage;
	}
	/**
	 * @param pageMessage the pageMessage to set
	 */
	public void setPageMessage(String pageMessage) {
		this.pageMessage = pageMessage;
	}
	public boolean isErrorExist() {
		return ErrorExist;
	}
	public void setErrorExist(boolean errorExist) {
		ErrorExist = errorExist;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
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
