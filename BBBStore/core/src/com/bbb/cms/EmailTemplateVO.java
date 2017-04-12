package com.bbb.cms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.userprofiling.email.TemplateEmailInfo;

import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.catalog.vo.StoreVO;

public class EmailTemplateVO{

	private String mSiteId;
	private String mEmailType;
	private String mEmailFrom;
	private String mEmailSubject;
	private String mEmailBody;
	private String mEmailHeader;
	private String mEmailFooter;
	private String mEmailFlag;
	private String mEmailPersistId;
	private String mShowViewInBrowser;
	private String currencyCode;
	private String countryCode;
	private String internationalFlag;
	private String emailFromcart;
	private String messageFromcart;
	private String firstName;
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getEmailFromcart() {
		return emailFromcart;
	}
	public void setEmailFromcart(String emailFromcart) {
		this.emailFromcart = emailFromcart;
	}
	public String getMessageFromcart() {
		return messageFromcart;
	}
	public void setMessageFromcart(String messageFromcart) {
		this.messageFromcart = messageFromcart;
	}
	public String getShowViewInBrowser() {
	    return mShowViewInBrowser;
	}
	public void setShowViewInBrowser(String pShowViewInBrowser) {
	    this.mShowViewInBrowser = pShowViewInBrowser;
	}
	public String getEmailFlag() {
	    return mEmailFlag;
	}
	public void setEmailFlag(String mEmailFlag) {
	    this.mEmailFlag = mEmailFlag;
	}
	//added for email stofu
	private String productId;
	private Boolean productAvailabilityFlag;
	private String skuId;
	private String recipientList;
	private String reviewRating;
	private String storeId;
	private String siteId;
	private String channelId;
	TemplateEmailInfo emailInfo;
	StoreVO storeVO;
	ProductVO productVo;
	private List<ProductVO> siblingProducts;
	private Map<String,Boolean> goodToKnow;
	private String priceRangeDescription;
	private Map<String,List<GSEmailVO>> tableRegistryCartMap;
	private List<GSEmailVO> compareArray;
	private String tableName;
	private Map<String,Map<String, List<GSEmailVO>>> tableCheckListMapOuter;
	HashMap<String, String> primaryCategoryCountMap;
	private String hostUrl;
	//added for email stofu

	public String getSiteId() {
		return mSiteId;
	}
	public void setSiteId(String pSiteId) {
		this.mSiteId = pSiteId;
	}
	public String getEmailType() {
		return mEmailType;
	}
	public void setEmailType(String pEmailType) {
		this.mEmailType = pEmailType;
	}
	public String getEmailFrom() {
		return mEmailFrom;
	}
	public void setEmailFrom(String pEmailFrom) {
		this.mEmailFrom = pEmailFrom;
	}
	public String getEmailSubject() {
		return mEmailSubject;
	}
	public void setEmailSubject(String pEmailSubject) {
		this.mEmailSubject = pEmailSubject;
	}
	public String getEmailBody() {
		return mEmailBody;
	}
	public void setEmailBody(String pEmailBody) {
		this.mEmailBody = pEmailBody;
	}
	public String getEmailHeader() {
		return mEmailHeader;
	}
	public void setEmailHeader(String pEmailHeader) {
		this.mEmailHeader = pEmailHeader;
	}
	public String getEmailFooter() {
		return mEmailFooter;
	}
	public void setEmailFooter(String pEmailFooter) {
		this.mEmailFooter = pEmailFooter;
	}
	public String getEmailPersistId() {
	    return mEmailPersistId;
	}
	public void setEmailPersistId(String mEmailPersistId) {
	    this.mEmailPersistId = mEmailPersistId;
	}

	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}

	public Boolean getProductAvailabilityFlag() {
		return productAvailabilityFlag;
	}
	public void setProductAvailabilityFlag(Boolean productAvailabilityFlag) {
		this.productAvailabilityFlag = productAvailabilityFlag;
	}
	public String getSkuId() {
		return skuId;
	}
	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}
	public String getRecipientList() {
		return recipientList;
	}
	public void setRecipientList(String recipientList) {
		this.recipientList = recipientList;
	}
	public String getReviewRating() {
		return reviewRating;
	}
	public void setReviewRating(String reviewRating) {
		this.reviewRating = reviewRating;
	}
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public TemplateEmailInfo getEmailInfo() {
		return emailInfo;
	}
	public void setEmailInfo(TemplateEmailInfo emailInfo) {
		this.emailInfo = emailInfo;
	}
	public StoreVO getStoreVO() {
		return storeVO;
	}
	public void setStoreVO(StoreVO storeVO) {
		this.storeVO = storeVO;
	}
	public ProductVO getProductVo() {
		return productVo;
	}
	public void setProductVo(ProductVO productVo) {
		this.productVo = productVo;
	}
	public List<ProductVO> getSiblingProducts() {
		return siblingProducts;
	}
	public void setSiblingProducts(List<ProductVO> siblingProducts) {
		this.siblingProducts = siblingProducts;
	}
	public Map<String, Boolean> getGoodToKnow() {
		return goodToKnow;
	}
	public void setGoodToKnow(Map<String, Boolean> goodToKnow) {
		this.goodToKnow = goodToKnow;
	}
	public String getPriceRangeDescription() {
		return priceRangeDescription;
	}
	public void setPriceRangeDescription(String priceRangeDescription) {
		this.priceRangeDescription = priceRangeDescription;
	}
	/*public List<GSEmailVO> getTableRegistryCartArray() {
		return tableRegistryCartArray;
	}
	public void setTableRegistryCartArray(
			List<GSEmailVO> tableRegistryCartArray) {
		this.tableRegistryCartArray = tableRegistryCartArray;
	}*/
	/*public List<String> getUniqueTableNames() {
		return uniqueTableNames;
	}
	public void setUniqueTableNames(List<String> uniqueTableNames) {
		this.uniqueTableNames = uniqueTableNames;
	}*/
	public Map<String, List<GSEmailVO>> getTableRegistryCartMap() {
		return tableRegistryCartMap;
	}
	public void setTableRegistryCartMap(
			Map<String, List<GSEmailVO>> tableRegistryCartMap) {
		this.tableRegistryCartMap = tableRegistryCartMap;
	}
	public String getmSiteId() {
		return mSiteId;
	}
	public void setmSiteId(String mSiteId) {
		this.mSiteId = mSiteId;
	}
	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}
	/**
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	/**
	 * @return the primaryCategoryCountMap
	 */
	public HashMap<String, String> getPrimaryCategoryCountMap() {
		return primaryCategoryCountMap;
	}
	/**
	 * @param primaryCategoryCountMap the primaryCategoryCountMap to set
	 */
	public void setPrimaryCategoryCountMap(
			HashMap<String, String> primaryCategoryCountMap) {
		this.primaryCategoryCountMap = primaryCategoryCountMap;
	}
	/**
	 * @return the hostUrl
	 */
	public String getHostUrl() {
		return hostUrl;
	}
	/**
	 * @param hostUrl the hostUrl to set
	 */
	public void setHostUrl(String hostUrl) {
		this.hostUrl = hostUrl;
	}
	public List<GSEmailVO> getCompareArray() {
		return compareArray;
	}
	public void setCompareArray(List<GSEmailVO> compareArray) {
		this.compareArray = compareArray;
	}
	public Map<String, Map<String, List<GSEmailVO>>> getTableCheckListMapOuter() {
		return tableCheckListMapOuter;
	}
	public void setTableCheckListMapOuter(
			Map<String, Map<String, List<GSEmailVO>>> tableCheckListMapOuter) {
		this.tableCheckListMapOuter = tableCheckListMapOuter;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getInternationalFlag() {
		return internationalFlag;
	}
	public void setInternationalFlag(String internationalFlag) {
		this.internationalFlag = internationalFlag;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	@Override
	public String toString() {
		return "EmailTemplateVO [mSiteId=" + mSiteId + ", mEmailType="
				+ mEmailType + ", mEmailFrom=" + mEmailFrom
				+ ", mEmailSubject=" + mEmailSubject + ", mEmailBody="
				+ mEmailBody + ", mEmailHeader=" + mEmailHeader
				+ ", mEmailFooter=" + mEmailFooter + ", mEmailFlag="
				+ mEmailFlag + ", mEmailPersistId=" + mEmailPersistId
				+ ", mShowViewInBrowser=" + mShowViewInBrowser
				+ ", currencyCode=" + currencyCode + ", countryCode="
				+ countryCode + ", internationalFlag=" + internationalFlag
				+ ", productId=" + productId + ", productAvailabilityFlag="
				+ productAvailabilityFlag + ", skuId=" + skuId
				+ ", recipientList=" + recipientList + ", reviewRating="
				+ reviewRating + ", storeId=" + storeId + ", siteId=" + siteId
				+ ", channelId=" + channelId + ", emailInfo=" + emailInfo
				+ ", storeVO=" + storeVO + ", productVo=" + productVo
				+ ", siblingProducts=" + siblingProducts + ", goodToKnow="
				+ goodToKnow + ", priceRangeDescription="
				+ priceRangeDescription + ", tableRegistryCartMap="
				+ tableRegistryCartMap + ", compareArray=" + compareArray
				+ ", tableName=" + tableName + ", tableCheckListMapOuter="
				+ tableCheckListMapOuter + ", primaryCategoryCountMap="
				+ primaryCategoryCountMap + ", hostUrl=" + hostUrl + "]";
	}


}
