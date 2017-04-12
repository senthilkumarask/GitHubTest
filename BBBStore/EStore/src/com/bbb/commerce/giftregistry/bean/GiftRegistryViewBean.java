package com.bbb.commerce.giftregistry.bean;

import java.util.List;

import com.bbb.framework.integration.ServiceRequestBase;

// TODO: Auto-generated Javadoc
/**
 * The bean is holding properties for the add to item in gift registry.
 *
 * @author sku134
 */
public class GiftRegistryViewBean extends ServiceRequestBase{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The registry size. */
	private int registrySize;
	private String totQtySrcReg;
	private boolean copyRegErr;



	/** The user token. */
	private String userToken;

	/** The product id. */
	private String productId;

	/** The reg token. */
	private String regToken;

	/** The success url. */
	private String successURL;
	
	/** The success url for collection child product used for N&D modal. */
	private String successURLforChildProduct;

	/** The quantity. */
	private String quantity;
	/** The quantity. */
	private int totQuantity;

	/** The registry id. */
	private String registryId;

	/** The registry name. */
	private String registryName;

	/** The omni product list. */
	private String omniProductList;

	/** The service name. */
	private String serviceName;

	/** The site flag. */
	private String siteFlag;

	/** The row id. */
	private String rowId;

	/** The parent product id. */
	private String parentProductId;

	/** The additem. */
	private List<AddItemsBean> additem;

	private String purchasedQuantity;

	private String regItemOldQty;

	private String sourceRegistry;
	private String targetRegistry;

	private String repositoryId;

	private String isDeclined = "true";

	private String isFromPendingTab = "false";

	// BPS-1381 Integrate Declined Tab
	private String isFromDeclinedTab = "false";
	private String userCurrency;
	private String userCountry;
	private Boolean internationalContext;
	
	private String refNum;
	private String personlizationCodes;
	private String personalizationPrices;
	private String personalizationDescrips;
	private String personalizedImageUrls;
	private String personalizedImageUrlThumbs;
	private String personalizedMobImageUrls;
	private String personalizedMobImageUrlThumbs;
	private String assemblySelections;
	private String itemTypes; 
	private String assemblyPrices;
	private String ltlDeliveryServices;
	private String ltlDeliveryPrices;
	private String customizationPrices;
	
	
	public String getCustomizationPrices() {
		return customizationPrices;
	}

	public void setCustomizationPrices(String customizationPrices) {
		this.customizationPrices = customizationPrices;
	}
	/**
	 * 
	 * @return assemblySelections
	 */
	public String getAssemblySelections() {
		return assemblySelections;
	}
	/**
	 * 
	 * @param assemblySelections
	 */
	public void setAssemblySelections(String assemblySelections) {
		this.assemblySelections = assemblySelections;
	}
	/**
	 * 
	 * @return itemTypes
	 */
	public String getItemTypes() {
		return itemTypes;
	}
	/**
	 * 
	 * @param itemTypes
	 */
	public void setItemTypes(String itemTypes) {
		this.itemTypes = itemTypes;
	}
	/**
	 * 
	 * @return assemblyPrices
	 */
	public String getAssemblyPrices() {
		return assemblyPrices;
	}
	/**
	 * 
	 * @param assemblyPrices
	 */
	public void setAssemblyPrices(String assemblyPrices) {
		this.assemblyPrices = assemblyPrices;
	}
	/**
	 * 
	 * @return ltlDeliveryServices
	 */
	public String getLtlDeliveryServices() {
		return ltlDeliveryServices;
	}
	/**
	 * 
	 * @param ltlDeliveryServices
	 */
	public void setLtlDeliveryServices(String ltlDeliveryServices) {
		this.ltlDeliveryServices = ltlDeliveryServices;
	}
	/**
	 * 
	 * @return ltlDeliveryPrices
	 */
	public String getLtlDeliveryPrices() {
		return ltlDeliveryPrices;
	}
	/**
	 * 
	 * @param ltlDeliveryPrices
	 */
	public void setLtlDeliveryPrices(String ltlDeliveryPrices) {
		this.ltlDeliveryPrices = ltlDeliveryPrices;
	}

	public String getRefNum() {
		return refNum;
	}

	public void setRefNum(String refNum) {
		this.refNum = refNum;
	}

	public String getPersonlizationCodes() {
		return personlizationCodes;
	}

	public void setPersonlizationCodes(String personlizationCodes) {
		this.personlizationCodes = personlizationCodes;
	}

	public String getPersonalizationPrices() {
		return personalizationPrices;
	}

	public void setPersonalizationPrices(String personalizationPrices) {
		this.personalizationPrices = personalizationPrices;
	}

	public String getPersonalizationDescrips() {
		return personalizationDescrips;
	}

	public void setPersonalizationDescrips(String personalizationDescrips) {
		this.personalizationDescrips = personalizationDescrips;
	}

	public String getPersonalizedImageUrls() {
		return personalizedImageUrls;
	}

	public void setPersonalizedImageUrls(String personalizedImageUrls) {
		this.personalizedImageUrls = personalizedImageUrls;
	}

	public String getPersonalizedImageUrlThumbs() {
		return personalizedImageUrlThumbs;
	}

	public void setPersonalizedImageUrlThumbs(String personalizedImageUrlThumbs) {
		this.personalizedImageUrlThumbs = personalizedImageUrlThumbs;
	}

	public String getPersonalizedMobImageUrls() {
		return personalizedMobImageUrls;
	}

	public void setPersonalizedMobImageUrls(String personalizedMobImageUrls) {
		this.personalizedMobImageUrls = personalizedMobImageUrls;
	}

	public String getPersonalizedMobImageUrlThumbs() {
		return personalizedMobImageUrlThumbs;
	}

	public void setPersonalizedMobImageUrlThumbs(
			String personalizedMobImageUrlThumbs) {
		this.personalizedMobImageUrlThumbs = personalizedMobImageUrlThumbs;
	}

	public String getIsFromPendingTab() {
		return isFromPendingTab;
	}

	public void setIsFromPendingTab(String isFromPendingTab) {
		this.isFromPendingTab = isFromPendingTab;
	}

	public String getIsDeclined() {
		return isDeclined;
	}

	public void setIsDeclined(String isDeclined) {
		this.isDeclined = isDeclined;
	}

	public String getIsFromDeclinedTab() {
		return isFromDeclinedTab;
	}

	public void setIsFromDeclinedTab(String isFromDeclinedTab) {
		this.isFromDeclinedTab = isFromDeclinedTab;
	}

	public String getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(String repositoryId) {
		this.repositoryId = repositoryId;
	}

	public boolean isCopyRegErr() {
		return copyRegErr;
	}

	public void setCopyRegErr(boolean copyRegErr) {
		this.copyRegErr = copyRegErr;
	}

	public String getTotQtySrcReg() {
		return totQtySrcReg;
	}

	public void setTotQtySrcReg(String totQtySrcReg) {
		this.totQtySrcReg = totQtySrcReg;
	}
	public String getSourceRegistry() {
		return sourceRegistry;
	}

	public void setSourceRegistry(String sourceRegistry) {
		this.sourceRegistry = sourceRegistry;
	}

	public String getTargetRegistry() {
		return targetRegistry;
	}

	public void setTargetRegistry(String targetRegistry) {
		this.targetRegistry = targetRegistry;
	}
	private String consultantName;

	public String getConsultantName() {
		return consultantName;
	}

	public void setConsultantName(String consultantName) {
		this.consultantName = consultantName;
	}

	/**
	 * Gets the site flag.
	 *
	 * @return the serviceName
	 */
	public String getSiteFlag() {
		return siteFlag;
	}

	/**
	 * Sets the site flag.
	 *
	 * @param siteFlag the new site flag
	 * @return the serviceName
	 */
	public void setSiteFlag(String siteFlag) {
		this.siteFlag = siteFlag;
	}

	/**
	 * Gets the service name.
	 *
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * Sets the service name.
	 *
	 * @param pServiceName the serviceName to set
	 */
	public void setServiceName(String pServiceName) {
		serviceName = pServiceName;
	}

	/**
	 * Gets the registry size.
	 *
	 * @return the registrySize
	 */
	public int getRegistrySize() {
		return registrySize;
	}

	/**
	 * Sets the registry size.
	 *
	 * @param pRegistrySize the registrySize to set
	 */
	public void setRegistrySize(int pRegistrySize) {
		registrySize = pRegistrySize;
	}

	/**
	 * Gets the success url.
	 *
	 * @return the successURL
	 */
	public String getSuccessURL() {
		return successURL;
	}

	/**
	 * Sets the success url.
	 *
	 * @param pSuccessURL the successURL to set
	 */
	public void setSuccessURL(String pSuccessURL) {
		successURL = pSuccessURL;
	}


	/**
	 * Gets the sku.
	 *
	 * @return the sku
	 */
	public String getSku() {
		return sku;
	}

	/**
	 * Sets the sku.
	 *
	 * @param pSku the sku to set
	 */
	public void setSku(String pSku) {
		sku = pSku;
	}

	/**
	 * Gets the quantity.
	 *
	 * @return the quantity
	 */
	public String getQuantity() {
		return quantity;
	}

	/**
	 * Sets the quantity.
	 *
	 * @param pQuantity the quantity to set
	 */
	public void setQuantity(String pQuantity) {
		quantity = pQuantity;
	}

	/**
	 * Gets the registry id.
	 *
	 * @return the registryId
	 */
	public String getRegistryId() {
		return registryId;
	}

	/**
	 * Sets the registry id.
	 *
	 * @param pRegistryId the registryId to set
	 */
	public void setRegistryId(String pRegistryId) {
		registryId = pRegistryId;
	}

	/**
	 * Gets the user token.
	 *
	 * @return the userToken
	 */
	public String getUserToken() {
		return userToken;
	}

	/**
	 * Sets the user token.
	 *
	 * @param pUserToken the userToken to set
	 */
	public void setUserToken(String pUserToken) {
		userToken = pUserToken;
	}

	/**
	 * Gets the reg token.
	 *
	 * @return the regToken
	 */
	public String getRegToken() {
		return regToken;
	}

	/**
	 * Sets the reg token.
	 *
	 * @param pRegToken the regToken to set
	 */
	public void setRegToken(String pRegToken) {
		regToken = pRegToken;
	}

	/**
	 * Gets the registry name.
	 *
	 * @return the registryName
	 */
	public String getRegistryName() {
		return registryName;
	}

	/**
	 * Sets the registry name.
	 *
	 * @param pRegistryName the registryName to set
	 */
	public void setRegistryName(String pRegistryName) {
		registryName = pRegistryName;
	}

	/**
	 * Gets the product id.
	 *
	 * @return the productId
	 */
	public String getProductId() {
		return productId;
	}

	/**
	 * Sets the product id.
	 *
	 * @param pProductId the productId to set
	 */
	public void setProductId(String pProductId) {
		productId = pProductId;
	}

	/**
	 * Gets the row id.
	 *
	 * @return the rowId
	 */
	public String getRowId() {
		return rowId;
	}

	/**
	 * Sets the row id.
	 *
	 * @param rowId the productId to set
	 */
	public void setRowId(String rowId) {
		this.rowId = rowId;
	}

	/**
	 * Gets the additem.
	 *
	 * @return the additem
	 */
	public List<AddItemsBean> getAdditem() {
		return additem;
	}

	/**
	 * Sets the additem.
	 *
	 * @param pAdditem the additem to set
	 */
	public void setAdditem(List<AddItemsBean> pAdditem) {
		additem = pAdditem;
	}

	/**
	 * Gets the parent product id.
	 *
	 * @return the parent product id
	 */
	public String getParentProductId() {
		return parentProductId;
	}

	/**
	 * Sets the parent product id.
	 *
	 * @param parentProductId the new parent product id
	 */
	public void setParentProductId(String parentProductId) {
		this.parentProductId = parentProductId;
	}

	/**
	 * Gets the omni product list.
	 *
	 * @return the omni product list
	 */
	public String getOmniProductList() {
		return omniProductList;
	}

	/**
	 * Sets the omni product list.
	 *
	 * @param omniProductList the new omni product list
	 */
	public void setOmniProductList(String omniProductList) {
		this.omniProductList = omniProductList;
	}

	public int getTotQuantity() {
		return totQuantity;
	}

	public void setTotQuantity(int totQuantity) {
		this.totQuantity = totQuantity;
	}

	public String getPurchasedQuantity() {
		return purchasedQuantity;
	}

	public void setPurchasedQuantity(String purchasedQty) {
		this.purchasedQuantity = purchasedQty;
	}

	public String getRegItemOldQty() {
		return regItemOldQty;
	}

	public void setRegItemOldQty(String regItemOldQty) {
		this.regItemOldQty = regItemOldQty;
	}

public String getUserCurrency() {
		return userCurrency;
	}

	public void setUserCurrency(String userCurrency) {
		this.userCurrency = userCurrency;
	}

	public String getUserCountry() {
		return userCountry;
	}

	public void setUserCountry(String userCountry) {
		this.userCountry = userCountry;
	}

	public Boolean getInternationalContext() {
		return internationalContext;
	}

	public void setInternationalContext(Boolean internationalContext) {
		this.internationalContext = internationalContext;
	}
	
	/** The sku. */
	private String sku;

	
	/**
	 * @return the successURLforChildProduct
	 */
	public String getSuccessURLforChildProduct() {
		return successURLforChildProduct;
	}

	/**
	 * @param successURLforChildProduct the successURLforChildProduct to set
	 */
	public void setSuccessURLforChildProduct(String successURLforChildProduct) {
		this.successURLforChildProduct = successURLforChildProduct;
	}
	private String successURLforNotifyProduct;

	public String getSuccessURLforNotifyProduct() {
		return successURLforNotifyProduct;
	}

	public void setSuccessURLforNotifyProduct(String successURLforNotifyProduct) {
		this.successURLforNotifyProduct = successURLforNotifyProduct;
	}
	
	

}
